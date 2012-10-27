package de.lmu.dbs.jfeaturelib.shapeFeatures;

import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.dbs.jfeaturelib.features.SampleDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Computes a normalized distance shape feature as Array List containing the
 * quadtree values from level 0 to the maximal level.
 *
 * This algorithm is described in "Yang Mingqiang, Kpalma Kidiyo and Ronsin
 * Joseph (2008). A Survey of Shape Feature Extraction Techniques, Pattern
 * Recognition Techniques, Technology and Applications, Peng-Yeng Yin (Ed.),
 * ISBN: 978-953-7619-24-4, InTech, Available from:
 * http://www.intechopen.com/articles/show/title/a_survey_of_shape_feature_extraction_techniques
 * as "adaptive grid resolution".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class AdaptiveGridResolution extends AbstractFeatureDescriptor {

    private static final Logger log = Logger.getLogger(SampleDescriptor.class.getName());
    private QuadTreeNode quadtree;
    private int backgroundColor = 0;
    private int resizeSize = 64;

    /*
     * Computes an ArrayList from the quadtree.
     */
    private List<Double> createFeature() {
        ArrayList<Double> feature = new ArrayList<>();
        while (quadtree.getNextFeatureNode() != null) {
            if (quadtree.getNextFeatureNode().getValue() == 1) {
                double size = (double) quadtree.getNextFeatureNode().getSize();
                feature.add(quadtree.getNextFeatureNode().getX() + (size / 2.0));
                feature.add(quadtree.getNextFeatureNode().getY() + (size / 2.0));
                feature.add(size);
            }
            quadtree.getNextFeatureNode().setFeature();
        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < feature.size() - 5; i += 3) {
                if (feature.get(i + 2) < feature.get(i + 5)) {
                    double x = feature.get(i);
                    double y = feature.get(i + 1);
                    double size = feature.get(i + 2);
                    feature.set(i, feature.get(i + 3));
                    feature.set(i + 1, feature.get(i + 4));
                    feature.set(i + 2, feature.get(i + 5));
                    feature.set(i + 3, x);
                    feature.set(i + 4, y);
                    feature.set(i + 5, size);
                    flag = true;
                }
            }
        }
        return feature;
    }

    /**
     * Constructs a AdaptiveGridResolution object
     *
     * @param resizeSize parameter for the image.resize(resizeSize) method, a
     * bigger number results in a better resolution but a longer calculation
     * time.
     */
    public AdaptiveGridResolution(int resizeSize) {
        this.resizeSize = resizeSize;
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        ImageProcessor image;
        {
            ImagePCA pca2d = new ImagePCA(ip, 0);
            image = pca2d.getResultImage();
            if (!ByteProcessor.class.isAssignableFrom(image.getClass())) {
                image = (ByteProcessor) image.convertToByte(true);
            }
        }
        double[] centroid;
        {
            CentroidFeature cf = new CentroidFeature();
            cf.run(image);
            centroid = cf.getFeatures().get(0);
        }
        //turns the image by 180° if the centroid is above the main axis
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getHeight(); i++) {
                if (image.getPixel(j, i) != backgroundColor) {
                    if (i < centroid[1]) {
                        image.rotate(180);
                        break;
                    }
                }
            }
        }
        if (Double.isNaN(centroid[0]) || Double.isNaN(centroid[1])) {
            quadtree = new QuadTreeNode(0, 0, 0, resizeSize);
            return;
        }
        //Sets the region of interest to the tangenting rectancle of the shape 
        int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getPixel(x, y) != backgroundColor) {
                    x1 = Math.min(x1, x);
                    y1 = Math.min(y1, y);
                    x2 = Math.max(x2, x);
                    y2 = Math.max(y2, y);
                }
            }
        }
        ImageProcessor ipNorm;
        int resizeSize2;
        if (y2 - y1 > x2 - x1) {
            resizeSize2 = (int) (resizeSize * ((double) (x2 - x1) / (double) (y2 - y1)));
        } else {
            resizeSize2 = resizeSize;
        }
        image.setRoi(x1, y1, (x2 - x1), (y2 - y1));
        ipNorm = image.resize(resizeSize2);
        QuadTreeNode quadtree2 = new QuadTreeNode(-1, 0, 0, resizeSize);
        //Computes the quadtree
        while (quadtree2.getNextToDoNode() != null) {
            QuadTreeNode todo = quadtree2.getNextToDoNode();
            int startwert;
            if (todo.getX() >= ipNorm.getWidth() || todo.getY() >= ipNorm.getHeight()) {
                startwert = backgroundColor;
            } else {
                startwert = ipNorm.getPixel(todo.getX(), todo.getY());
            }
            boolean flag = true;
            for (int i = 0; i < todo.getSize(); i++) {
                for (int j = 0; j < todo.getSize(); j++) {
                    if (todo.getX() + i >= ipNorm.getWidth() || todo.getY() >= ipNorm.getHeight()) {
                        if (startwert != backgroundColor) {
                            flag = false;
                            break;
                        }
                    } else {
                        if (startwert != (int) ipNorm.getPixel(todo.getX() + i, todo.getY() + j)) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (!flag) {
                    break;
                }
            }
            if (flag) {
                if (startwert == backgroundColor) {
                    todo.setValue(0);
                } else {
                    todo.setValue(1);
                }
            } else {
                QuadTreeNode[] children = {new QuadTreeNode(todo, 0), new QuadTreeNode(todo, 1), new QuadTreeNode(todo, 2), new QuadTreeNode(todo, 3)};
                todo.setChildren(children);
            }
        }
        quadtree = quadtree2;

        fillFeatures();

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.NoChanges, Supports.DOES_16);
    }

    @Override
    public String getDescription() {
        return "Shape feature descriptor that returns a quadtree as feature";
    }

    private void fillFeatures() {
        List<Double> feature = this.createFeature();
        double[] array = new double[feature.size()];
        for (int i = 0; i < feature.size(); i++) {
            array[i] = feature.get(i);
        }
        addData(array);
    }

    private class QuadTreeNode {

//        private boolean root = false;
//        private QuadTreeNode parent;
        private QuadTreeNode[] children = null;
        private int value;
        private int x;
        private int y;
        private int size;
        private boolean feature = false;

        private QuadTreeNode(QuadTreeNode parent, int flag) {
            value = -1;
            if (flag == 0) {
                this.size = parent.size / 2;
                this.x = parent.x;
                this.y = parent.y;
            } else if (flag == 1) {
                this.size = parent.size / 2;
                this.x = parent.x + size;
                this.y = parent.y;
            } else if (flag == 2) {
                this.size = parent.size / 2;
                this.x = parent.x + size;
                this.y = parent.y + size;
            } else if (flag == 3) {
                this.size = parent.size / 2;
                this.x = parent.x;
                this.y = parent.y + size;
            }
        }

        private QuadTreeNode(int value, int x, int y, int size) {
//            root = true;
            this.value = value;
            this.x = x;
            this.y = y;
            this.size = size;
            feature = false;
        }

        private QuadTreeNode getNextToDoNode() {
            if (children == null && value == -1) {
                return this;
            } else if (value == -1) {
                if (children[0].getNextToDoNode() != null) {
                    return children[0].getNextToDoNode();
                } else if (children[1].getNextToDoNode() != null) {
                    return children[1].getNextToDoNode();
                } else if (children[2].getNextToDoNode() != null) {
                    return children[2].getNextToDoNode();
                } else if (children[3].getNextToDoNode() != null) {
                    return children[3].getNextToDoNode();
                }
            }
            return null;
        }

        private QuadTreeNode getNextFeatureNode() {
            if (feature == false) {
                return this;
            } else if (children != null) {
                if (children[0].getNextFeatureNode() != null) {
                    return children[0].getNextFeatureNode();
                } else if (children[1].getNextFeatureNode() != null) {
                    return children[1].getNextFeatureNode();
                } else if (children[2].getNextFeatureNode() != null) {
                    return children[2].getNextFeatureNode();
                } else if (children[3].getNextFeatureNode() != null) {
                    return children[3].getNextFeatureNode();
                }
            }
            return null;
        }

        private void setValue(int value) {
            this.value = value;
        }

        private void setChildren(QuadTreeNode[] children) {
            this.children = children;
        }

        private void setFeature() {
            feature = true;
        }

        private int getSize() {
            return size;
        }

        private int getX() {
            return x;
        }

        private int getY() {
            return y;
        }

        private int getValue() {
            return value;
        }
    }
}
