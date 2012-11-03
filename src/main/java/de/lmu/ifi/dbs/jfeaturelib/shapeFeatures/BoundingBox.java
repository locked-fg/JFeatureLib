package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.edgeDetector.Susan;
import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.ifi.dbs.jfeaturelib.utils.Distance;
import de.lmu.ifi.dbs.jfeaturelib.utils.DistanceL2;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Computes a bounding box (not necessarily the minimal bounding box) with the
 * width of the largest distance between two points on the boundary of a shape
 * and the height of the vertical expansion. "Bryan S. Morse (2000): Lecture 9:
 * Shape Description (Regions), Brigham Young University, Available from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
class BoundingBox extends AbstractFeatureDescriptor {

    private final Distance dist = new DistanceL2();
    private int backgroundColor = 255;
    private int shapeColor = 0;
    private double longestDist;
    private double longestVerticalDist;
    private double angle;
    private int x;
    private int y;
    private ImageProcessor returnimage;

    public ImageProcessor getImage() {
        return returnimage;
    }

    public double[] getBoundingBox() {
        double[] ret = new double[5];
        ret[0] = x;
        ret[1] = y;
        ret[2] = longestDist;
        ret[3] = longestVerticalDist;
        ret[4] = angle;
        return ret;
    }

    private void fromBWToWB(ImageProcessor ip) {
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (ip.getPixel(i, j) == backgroundColor) {
                    ip.putPixel(i, j, shapeColor);
                } else {
                    ip.putPixel(i, j, backgroundColor);
                }
            }
        }
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ByteProcessor image = null;
        ImageProcessor image2 = (ImageProcessor) ip.clone();
        if (!ByteProcessor.class.isAssignableFrom(image2.getClass())) {
            image = (ByteProcessor) image2.convertToByte(true);
        }
        fromBWToWB(image);
        new Susan().run(ip);
        ByteProcessor ip2 = (ByteProcessor) ip.convertToByte(true);
        //Fix outer border error from SUSAN
        for (int i = 0; i < ip2.getWidth(); i++) {
            for (int j = 0; j < ip2.getHeight(); j++) {
                if (i <= 1 || i >= ip2.getWidth() - 3) {
                    ip2.putPixel(i, j, backgroundColor);
                } else if (j <= 1 || j >= ip2.getHeight() - 2) {
                    ip2.putPixel(i, j, backgroundColor);
                }
            }
        }

        double maxDistance = Double.MIN_VALUE;
        int maxX1 = 0, maxY1 = 0, maxX2 = 0, maxY2 = 0;
        double distTmp;
        for (int x1 = 0; x1 < ip2.getWidth(); x1++) {
            for (int y1 = 0; y1 < ip2.getHeight(); y1++) {
                if (ip2.getPixel(x1, y1) != backgroundColor) {
                    for (int x2 = 0; x2 < ip2.getWidth(); x2++) {
                        for (int y2 = 0; y2 < ip2.getHeight(); y2++) {
                            if (ip2.getPixel(x2, y2) != backgroundColor) {
                                distTmp = dist.distance(x1, y1, x2, y2);
                                if (maxDistance < distTmp) {
                                    maxDistance = distTmp;
                                    maxX1 = x1;
                                    maxY1 = y1;
                                    maxX2 = x2;
                                    maxY2 = y2;
                                }
                            }
                        }
                    }
                    ip2.putPixel(x1, y1, backgroundColor);
                }
            }
        }
        longestDist = maxDistance;
        double deltaX = maxX1 - maxX2;
        double deltaY = maxY1 - maxY2;
        angle = Math.toDegrees(Math.atan(deltaY / deltaX));
        if (angle > 0) {
            angle = 180 - Math.toDegrees(Math.atan(deltaY / deltaX));
        } else {
            angle = Math.abs(Math.toDegrees(Math.atan(deltaY / deltaX)));
        }
        image.rotate(angle);
        ArrayList listV = new ArrayList();
        ArrayList listH = new ArrayList();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getPixel(i, j) != backgroundColor) {
                    listV.add(j);
                    listH.add(i);
                }
            }
        }
        Collections.sort(listH);
        x = (int) listH.get(0);
        Collections.sort(listV);
        y = (int) listV.get(0);
        longestVerticalDist = (int) listV.get(listV.size() - 1) - (int) listV.get(0);
        returnimage = image;

        addData(getBoundingBox());
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public String getDescription() {
        return "returns a bounding box around the shape";
    }
}
