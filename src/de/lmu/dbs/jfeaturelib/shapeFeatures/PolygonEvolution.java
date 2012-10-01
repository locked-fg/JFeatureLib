package de.lmu.dbs.jfeaturelib.shapeFeatures;

import de.lmu.dbs.jfeaturelib.LibProperties;
import de.lmu.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.dbs.jfeaturelib.features.Histogram;
import de.lmu.dbs.jfeaturelib.utils.Distance;
import de.lmu.dbs.jfeaturelib.utils.DistanceL2;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Computes a distance shape feature as Array List containing the x and y
 * coordinates from the points generating the polygon. This method is described
 * in "Yang Mingqiang, Kpalma Kidiyo and Ronsin Joseph (2008). A Survey of Shape
 * Feature Extraction Techniques, Pattern Recognition Techniques, Technology and
 * Applications, Peng-Yeng Yin (Ed.), ISBN: 978-953-7619-24-4, InTech, Available
 * from:
 * http://www.intechopen.com/articles/show/title/a_survey_of_shape_feature_extraction_techniques".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class PolygonEvolution extends AbstractFeatureDescriptor {

    private static Logger log = Logger.getLogger(PolygonEvolution.class.getName());
    private static final Distance dist = new DistanceL2();
    private List<Integer> feature = new ArrayList<>();
    private int iterations = 50;
    private int backgroundColor = -1;
    int nothingFound = 0;

    /**
     * default constructor starting with 50 iterations
     */
    public PolygonEvolution() {
    }

    public PolygonEvolution(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public void setProperties(LibProperties properties) {
        iterations = properties.getInteger(LibProperties.POLYGON_EVOLUTION, 50);
    }

    private double getKValue(int x1, int y1, int x2, int y2, int x3, int y3) {
        double lengthS1 = dist.distance(x1, y1, x2, y2);
        double lengthS2 = dist.distance(x2, y2, x3, y3);
        double lengthS3 = dist.distance(x1, y1, x3, y3);
        double angle = Math.toDegrees(Math.acos((lengthS1 * lengthS1 + lengthS2 * lengthS2 - lengthS3 * lengthS3) / (-2.0 * lengthS1 * lengthS2)));
        double k = (angle * lengthS1 * lengthS2) / (lengthS1 + lengthS2);
        return k;
    }

    private List<Integer> removeDublicates(List<Integer> l) {
        for (int i = 0; i <= l.size() - 4; i += 2) {
            for (int j = i + 2; j <= l.size() - 2; j += 2) {
                if ((int) l.get(i) == (int) l.get(j) && (int) l.get(i + 1) == (int) l.get(j + 1)) {
                    l.set(j, -1);
                    l.set(j + 1, -1);
                } else {
                    break;
                }
            }
        }
        for (int k = 0; k < l.size(); k++) {
            while (k < l.size() && (int) l.get(k) < 0) {
                l.remove(k);
            }
        }
        while ((int) l.get(0) == (int) l.get(l.size() - 2) && (int) l.get(1) == (int) l.get(l.size() - 1)) {
            l.set(l.size() - 2, -1);
            l.set(l.size() - 1, -1);
        }
        for (int k = 0; k < l.size(); k++) {
            while (k < l.size() && (int) l.get(k) < 0) {
                l.remove(k);
            }
        }
        return l;
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();

        ExtremalPoints ep = new ExtremalPoints();
        ep.run(ip);
        List<Integer> points = ep.getFeature();
        points.remove(16);

        List<Double> kValue = new ArrayList();
        int counter = 0;
        int maxI;
        points = removeDublicates(points);

        while (counter < iterations) {
            kValue.clear();
            for (int x = 0; x <= points.size() - 6; x = x + 2) {
                kValue.add(getKValue((int) points.get(x), (int) points.get(x + 1), (int) points.get(x + 2), (int) points.get(x + 3), (int) points.get(x + 4), (int) points.get(x + 5)));
            }
            kValue.add(getKValue((int) points.get(points.size() - 4), (int) points.get(points.size() - 4 + 1), (int) points.get(points.size() - 4 + 2), (int) points.get(points.size() - 4 + 3), (int) points.get(0), (int) points.get(1)));
            kValue.add(getKValue((int) points.get(points.size() - 2), (int) points.get(points.size() - 1), (int) points.get(0), (int) points.get(1), (int) points.get(2), (int) points.get(3)));
            double maxK = Double.MIN_VALUE;
            maxI = Integer.MIN_VALUE;
            for (int i = 0; i < kValue.size(); i++) {
                if ((double) kValue.get(i) > maxK) {
                    maxK = (double) kValue.get(i);
                    maxI = i;
                }
            }
            int x1, x2, x3, y1, y2, y3;
            if (maxI * 2 <= points.size() - 6) {
                x1 = (int) points.get(maxI * 2);
                y1 = (int) points.get(maxI * 2 + 1);
                x2 = (int) points.get(maxI * 2 + 2);
                y2 = (int) points.get(maxI * 2 + 3);
                x3 = (int) points.get(maxI * 2 + 4);
                y3 = (int) points.get(maxI * 2 + 5);
            } else if (maxI * 2 == points.size() - 4) {
                x1 = (int) points.get(maxI * 2);
                y1 = (int) points.get(maxI * 2 + 1);
                x2 = (int) points.get(maxI * 2 + 2);
                y2 = (int) points.get(maxI * 2 + 3);
                x3 = (int) points.get(0);
                y3 = (int) points.get(1);
            } else {
                x1 = (int) points.get(maxI * 2);
                y1 = (int) points.get(maxI * 2 + 1);
                x2 = (int) points.get(0);
                y2 = (int) points.get(1);
                x3 = (int) points.get(2);
                y3 = (int) points.get(3);
            }
            byte[] pixelsCopy = (byte[]) ip.getPixelsCopy();
            byte[][] pixel = new byte[ip.getWidth()][ip.getHeight()];
            for (int i = 0; i < pixelsCopy.length; i++) {
                pixel[i % ip.getWidth()][(i - (i % ip.getWidth())) / ip.getWidth()] = pixelsCopy[i];
            }
            byte[][] pixel2 = pixel.clone();
            List p1 = getBorderDist(pixel, x1, y1, x2, y2, x3, y3);
            List p2 = getBorderDist(pixel2, x3, y3, x2, y2, x1, y1);
            if (maxI * 2 <= points.size() - 6) {
                points.addAll(maxI * 2 + 2, p1);
                points.addAll(maxI * 2 + 6, p2);
            } else if (maxI * 2 == points.size() - 4) {
                log.debug("spezialfall1");

                points.addAll(maxI * 2 + 2, p1);
                points.addAll(p2);
            } else {
                log.debug("spezialfall2");

                points.addAll(p1);
                points.addAll(2, p2);
            }
            points = removeDublicates(points);
            log.debug(maxI);
//            for (int i = 0; i < points.size(); i++) {
//                System.out.print(points.get(i) + " ");
//            }
            counter++;
        }
        feature = points;

        createFeature();
        endProgress();
    }

    private List getBorderDist(byte[][] pixel, int x1, int y1, int x2, int y2, int x3, int y3) {
        double maxDist = Double.MIN_VALUE;
        int currentX = x1;
        int currentY = y1;
        int maxX = x1;
        int maxY = y1;
        List returnValues = new ArrayList();
        int flag = 1;
        while (flag > 0) {
            if (currentX != 0 && currentY != 0 && pixel[currentX - 1][currentY - 1] != backgroundColor) {
                currentX = currentX - 1;
                currentY = currentY - 1;
                double distTmp = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (distTmp > maxDist) {
                    maxDist = distTmp;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentY != 0 && pixel[currentX][currentY - 1] != backgroundColor) {
                currentY = currentY - 1;
                double distTmp = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (distTmp > maxDist) {
                    maxDist = distTmp;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentX != pixel.length - 1 && currentY != 0 && pixel[currentX + 1][currentY - 1] != backgroundColor) {
                currentX = currentX + 1;
                currentY = currentY - 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentX != pixel.length - 1 && pixel[currentX + 1][currentY] != backgroundColor) {
                currentX = currentX + 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentX != 0 && pixel[currentX - 1][currentY] != backgroundColor) {
                currentX = currentX - 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentX != 0 && currentY != pixel[0].length - 1 && pixel[currentX - 1][currentY + 1] != backgroundColor) {
                currentX = currentX - 1;
                currentY = currentY + 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentX != pixel.length - 1 && currentY != pixel[0].length - 1 && pixel[currentX + 1][currentY + 1] != backgroundColor) {
                currentX = currentX + 1;
                currentY = currentY + 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else if (currentY != pixel[0].length - 1 && pixel[currentX][currentY + 1] != backgroundColor) {
                currentY = currentY + 1;
                double dist = Math.abs((x2 - x1) * (y1 - currentY) - (x1 - currentX) * (y2 - y1))
                        / Math.sqrt((Math.pow((x2 - x1), 2)) + (Math.pow((y2 - y1), 2)));
                if (dist > maxDist) {
                    maxDist = dist;
                    maxX = currentX;
                    maxY = currentY;
                }
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("current" + currentX + " " + currentY);

                pixel[currentX][currentY] = (byte) backgroundColor;
            } else {
                log.debug("nix gefunden " + nothingFound);
                nothingFound++;
                returnValues.add(0, x1);
                returnValues.add(1, y1);
                log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
                log.debug("gef2: " + x1 + " " + y1);

                return returnValues;
            }
            if (currentX == x2 && currentY == y2) {
                flag = 0;
                pixel[currentX][currentY] = 0;
            } else if (currentX == x3 && currentY == y3) {
                currentX = x1;
                currentY = y1;
                maxX = currentX;
                maxY = currentY;
                maxDist = Double.MIN_VALUE;
                pixel[currentX][currentY] = (byte) backgroundColor;

            }
        }
        returnValues.add(0, maxX);
        returnValues.add(1, maxY);
        log.debug("ges" + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3);
        log.debug("gef" + maxX + " " + maxY);

        return returnValues;
    }

    @Override
    public String getDescription() {
        return "PolygonEvolution";
    }

    private void createFeature() {
        double[] arr = new double[feature.size()];
        for (int i = 0; i < feature.size(); i++) {
            arr[i] = feature.get(i);
        }
        addData(arr);
    }
}
