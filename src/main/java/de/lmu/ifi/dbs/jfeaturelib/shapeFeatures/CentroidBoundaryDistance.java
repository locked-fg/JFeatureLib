/**
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.edgeDetector.Susan;
import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Computes a normalized distance shape feature as Array List containing the max
 * distances measured for each angle or null if no boundary was found at that
 * angle.
 *
 * This algorithm is described in "Yang Mingqiang, Kpalma Kidiyo and Ronsin
 * Joseph (2008). A Survey of Shape Feature Extraction Techniques, Pattern
 * Recognition Techniques, Technology and Applications, Peng-Yeng Yin (Ed.),
 * ISBN: 978-953-7619-24-4, InTech, Available from:
 * http://www.intechopen.com/articles/show/title/a_survey_of_shape_feature_extraction_techniques
 * " as "centroid-to-boundary distance approach".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class CentroidBoundaryDistance extends AbstractFeatureDescriptor {

    private double angles;
    private ArrayList<Double> distances = new ArrayList(1);
    private int backgroundColor = 0;

    /**
     * Constructs a CentroidBoundaryDistance object with distances measured all
     * 22,5°
     */
    public CentroidBoundaryDistance() {
        angles = 16;
    }

    /**
     * Constructs a CentroidBoundaryDistance object
     *
     * @param angles Number of angles, i.e. 36 for measurements all 10°
     */
    public CentroidBoundaryDistance(int angles) {
        this.angles = angles;
    }

    public ArrayList getDistances() {
        return distances;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        double dx = (double) x1 - (double) x2;
        double dy = (double) y1 - (double) y2;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    //Adds a neighbourpixel to each pixel to eliminate missed hits
    public ImageProcessor thicken(ImageProcessor ip) {
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (ip.get(i, j) == backgroundColor) {
                    if (i - 1 > 0) {
                        ip.set(i - 1, j, backgroundColor);
                    }
                }
            }
        }
        return ip;
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        ImagePCA pca = new ImagePCA(ip, backgroundColor);
        ip = pca.getResultImage();

        double[] centroid;
        {
            CentroidFeature cf = new CentroidFeature();
            cf.run(ip);
            centroid = cf.getFeatures().get(0);

        }
        //turns the image by 180° if the centroid is above the main axis
        for (int j = 0; j < ip.getHeight(); j++) {
            for (int i = 0; i < ip.getHeight(); i++) {
                if (ip.getPixel(j, i) != backgroundColor) {
                    if (i < centroid[1]) {
                        ip.rotate(180);
                        break;
                    }
                }
            }
        }

        distances = new ArrayList();
        // xC and yC are the centroids coordinates
        int xC;
        int yC;
        {
            CentroidFeature cf = new CentroidFeature();
            cf.run(ip);
            double[] xCyC = cf.getFeatures().get(0);
            xC = (int) xCyC[0];
            yC = (int) xCyC[1];
        }
        //ip = ip.convertToRGB();
        new Susan().run(ip);
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        ByteProcessor image = (ByteProcessor) ip;
        ip = thicken(ip);
        //Fix outer border error from SUSAN
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (i <= 1 || i >= ip.getWidth() - 3) {
                    ip.putPixel(i, j, 255);
                } else if (j <= 1 || j >= ip.getHeight() - 2) {
                    ip.putPixel(i, j, 255);
                }
            }
        }
        //Angles from the centroid to the corners of the image for boundary checking
        double angleTopRight = Math.toDegrees(Math.atan((double) yC / (image.getWidth() - (double) xC)));
        double angleTopLeft = 90.0 + Math.toDegrees(Math.atan(((double) xC / (double) yC)));
        double angleBottomLeft = 180.0 + Math.toDegrees(Math.atan(((image.getHeight() - (double) yC) / (double) xC)));
        double angleBottomRight = 270.0 + Math.toDegrees(Math.atan((image.getWidth() - (double) xC) / (image.getHeight() - (double) yC)));
        //Distances for each angle
        for (int i = 0; i < angles; i++) {
            int border = 0;
            ArrayList<Double> angleDist = new ArrayList();
            double angle = i * (360.0 / angles);
            if (angle == 0) {
                for (int j = 0; j < image.getWidth() - xC; j++) {
                    if (image.getPixel(xC + j, yC) == backgroundColor) {
                        angleDist.add((double) j);
                    }
                }
            } else if (angle <= 45) {
                if (angle < angleTopRight) {
                    border = image.getWidth() - xC;
                } else {
                    border = (int) (yC / Math.tan(Math.toRadians(angle)));
                }
                for (int j = 0; j < border; j++) {
                    int y = (int) (Math.tan(Math.toRadians(angle)) * j);
                    if (image.getPixel(xC + j, yC - y) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC + j, yC - y));
                    }
                }
            } else if (angle < 90) {
                if (angle > angleTopRight) {
                    border = yC;
                } else {
                    border = (int) (Math.tan(Math.toRadians(angle)) * (image.getWidth() - xC));
                }
                for (int j = 0; j < border; j++) {
                    int x = (int) (j / Math.tan(Math.toRadians(angle)));
                    if (image.getPixel(xC + x, yC - j) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC + x, yC - j));
                    }
                }
            } else if (angle == 90) {
                for (int j = 0; j < yC; j++) {
                    if (image.getPixel(xC, yC - j) == backgroundColor) {
                        angleDist.add((double) j);
                    }
                }
            } else if (angle <= 135) {
                if (angle < angleTopLeft) {
                    border = yC;
                } else {
                    border = (int) (Math.tan(Math.toRadians(180.0 - angle)) * xC);
                }
                for (int j = 0; j < border; j++) {
                    int x = (int) (j / Math.tan(Math.toRadians(angle)));
                    if (image.getPixel(xC + x, yC - j) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC + x, yC - j));
                    }
                }
            } else if (angle < 180) {
                if (angle > angleTopLeft) {
                    border = xC;
                } else {
                    border = (int) (yC / Math.tan(Math.toRadians(180.0 - angle)));
                }
                for (int j = 0; j < border; j++) {
                    int y = (int) (Math.tan(Math.toRadians(angle)) * j);
                    if (image.getPixel(xC - j, yC + y) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC - j, yC + y));
                    }
                }
            } else if (angle == 180) {
                for (int j = 0; j < xC; j++) {
                    if (image.getPixel(xC - j, yC) == backgroundColor) {
                        angleDist.add((double) j);
                    }
                }
            } else if (angle <= 225) {
                if (angle < angleBottomLeft) {
                    border = xC;
                } else {
                    border = (int) ((image.getHeight() - yC) / Math.tan(Math.toRadians(angle - 180.0)));
                }
                for (int j = 0; j < border; j++) {
                    int y = (int) (Math.tan(Math.toRadians(angle)) * j);
                    if (image.getPixel(xC - j, yC + y) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC - j, yC + y));
                    }
                }
            } else if (angle < 270) {
                if (angle > angleBottomLeft) {
                    border = image.getHeight() - yC;
                } else {
                    border = (int) (Math.tan(Math.toRadians(angle - 180.0)) * xC);
                }
                for (int j = 0; j < border; j++) {
                    int x = (int) (j / Math.tan(Math.toRadians(angle)));
                    if (image.getPixel(xC - x, yC + j) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC - x, yC + j));
                    }
                }
            } else if (angle == 270) {
                for (int j = 0; j < (image.getHeight() - yC); j++) {
                    if (image.getPixel(xC, yC + j) == backgroundColor) {
                        angleDist.add((double) j);
                    }
                }
            } else if (angle <= 315) {
                if (angle < angleBottomRight) {
                    border = image.getHeight() - yC;
                } else {
                    border = (int) ((image.getWidth() - xC) / (Math.tan(Math.toRadians(angle - 270.0))));
                }
                for (int j = 0; j < border; j++) {
                    int x = (int) (j / Math.tan(Math.toRadians(angle)));
                    if (image.getPixel(xC - x, yC + j) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC - x, yC + j));
                    }
                }
            } else {
                if (angle > angleBottomRight) {
                    border = image.getWidth() - xC;
                } else {
                    border = (int) ((image.getHeight() - yC) * (Math.tan(Math.toRadians(angle - 270.0))));
                }
                for (int j = 0; j < border; j++) {
                    int y = (int) (Math.tan(Math.toRadians(angle)) * j);
                    if (image.getPixel(xC + j, yC - y) == backgroundColor) {
                        angleDist.add(distance(xC, yC, xC + j, yC - y));
                    }
                }
            }
            if (angleDist.isEmpty()) {
                distances.add(null);
            } else {
                distances.add((double) angleDist.get(angleDist.size() - 1));
            }
        }
        //Normalisation to guarantee rotation and scaling invariance.
        //Transition invariance is ensured by the use of a centroid.
        if (!distances.isEmpty()) {
            for (int i = 0; i < distances.size(); i++) {
                if (distances.get(i) != null && (double) distances.get(i) == 0.0) {
                    distances.set(i, null);
                }
            }
            double minEle = Double.MAX_VALUE;
            double maxEle = Double.MIN_VALUE;
            for (int i = 0; i < distances.size(); i++) {
                if (distances.get(i) != null && (double) distances.get(i) < minEle) {
                    minEle = (double) distances.get(i);
                }
                if (distances.get(i) != null) {
                    maxEle = Math.max(maxEle, distances.get(i));
                }
            }
            maxEle /= minEle;
            //Scaling invariance
            for (int i = 0; i < distances.size(); i++) {
                if (distances.get(i) != null) {
                    distances.set(i, ((double) distances.get(i) / minEle));
                } else {
                    distances.set(i, 0.0);
                }
            }
            //Rotation invariance
            /*for (int i = 0; i < distances.size(); i++) {
             if (distances.get(0) > 0.0) {
             if ((double) distances.get(0) > 1.0) {
             distances.add(distances.get(0));
             distances.remove(distances.get(0));
             } else {
             break;
             }
             } else {
             distances.add(distances.get(0));
             distances.remove(distances.get(0));
             }
             }*/

        }

        // set data
        generateFeatures();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    public void generateFeatures() {
        double[] x = new double[distances.size()];
        for (int i = 0; i < (distances.size()); i++) {
            x[i] = distances.get(i);
        }
        addData(x);
    }

    @Override
    public EnumSet<Descriptor.Supports> supports() {
        return EnumSet.of(Descriptor.Supports.NoChanges, Descriptor.Supports.DOES_16);
    }

    @Override
    public String getDescription() {
        return "Shape feature descriptor that returns the distances between the centroid and the boundary as feature";
    }
}
