/*
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

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.Polygon;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.log4j.Logger;

/**
 * The Descriptor represents a binarized shape model by the SquareModelShapreMatrix, also known as GridDescriptor.
 *
 * A brief explanation can be found in
 * http://cdn.intechopen.com/pdfs/5781/InTech-A_survey_of_shape_feature_extraction_techniques.pdf
 *
 * The original paper is written by<br> J. Flusser, “Invariant shape description and measure of object similarity,” in
 * Proc. 4th, International Conference on Image Processing and its Applications, 1992, pp. 139-142.
 *
 * @author Johannes Stadler
 * @since v1.1.0
 */
public class SquareModelShapeMatrix extends AbstractFeatureDescriptor {

    private final static Logger log = Logger.getLogger(SquareModelShapeMatrix.class);
    private int[][] feature;
    private int backgroundColor = 0;
    int matrixDimension = 30;

    /**
     * Initializes the SquareModelShapeMatrix with default background color (0/black) and a default matrix size.
     */
    public SquareModelShapeMatrix() {
    }

    public SquareModelShapeMatrix(int matrixDimension) {
        setMatrixDimension(matrixDimension);
    }

    public SquareModelShapeMatrix(int matrixDimension, int backgroundColor) {
        setMatrixDimension(matrixDimension);
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        setMatrixDimension(properties.getInteger(LibProperties.SMSM_DIMENSIONS, 30));
        backgroundColor = properties.getInteger(LibProperties.SMSM_BG_COLOR, 0);
    }

    /**
     * @return the actual binarized SquareModelShapeMatrix
     */
    public int[][] getMatrix() {
        return feature;
    }

    /**
     * calculates the euclidean distance
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return euclidean distance
     */
    private double distance(int x1, int y1, int x2, int y2) {
        double dx = (double) x1 - (double) x2;
        double dy = (double) y1 - (double) y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();

        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }

        int xC, yC, xMax = 0, yMax = 0;
        double bigSquareSize = Double.MIN_VALUE; //angle

        {
            CentroidFeature cf = new CentroidFeature();
            cf.run(ip);
            double[] xCyC = cf.getFeatures().get(0);
            xC = (int) xCyC[0];
            yC = (int) xCyC[1];
        }

        for (int i = 0; i < ip.getWidth(); i++) {
            double dist;
            for (int j = 0; j < ip.getHeight(); j++) {
                dist = distance(xC, yC, i, j);
                if (ip.get(i, j) != backgroundColor && dist > bigSquareSize) {
                    bigSquareSize = dist;
                    xMax = i;
                    yMax = j;
                }
            }
        }

        int deltax = Math.abs(xC - xMax);
        int deltay = Math.abs(yC - yMax);
        int xA = xC - deltax;
        int yA = yC - deltay;
        int xB = xC + deltax;
        int yB = yC + deltay;
        int xP1 = xA - deltay;
        int yP1 = yA + deltax;
        int xP2 = xA + deltay;
        int yP2 = yA - deltax;
        // int xP3 = xB + deltay;
        // int yP3 = yB - deltax;
        int xP4 = xB - deltay;
        int yP4 = yB + deltax;
        double xVektorP12 = xP2 - xP1;
        double yVektorP12 = yP2 - yP1;
        double xVektorP14 = xP4 - xP1;
        double yVektorP14 = yP4 - yP1;
        log.debug("Delta: " + deltax + " " + deltay);
        feature = new int[matrixDimension][matrixDimension];
        for (int i = 0; i < matrixDimension; i++) {
            for (int j = 0; j < matrixDimension; j++) {
                int x1 = (int) Math.round(xP1 + (xVektorP12 * ((double) i / matrixDimension)) + (xVektorP14 * ((double) j / matrixDimension)));
                int y1 = (int) Math.round(yP1 + (yVektorP12 * ((double) i / matrixDimension)) + (yVektorP14 * ((double) j / matrixDimension)));
                int x2 = (int) Math.round(xP1 + (xVektorP12 * (((double) i + 1) / matrixDimension)) + (xVektorP14 * ((double) j / matrixDimension)));
                int y2 = (int) Math.round(yP1 + (yVektorP12 * (((double) i + 1) / matrixDimension)) + (yVektorP14 * ((double) j / matrixDimension)));
                int x3 = (int) Math.round(xP1 + (xVektorP12 * ((double) i / matrixDimension)) + (xVektorP14 * (((double) j + 1) / matrixDimension)));
                int y3 = (int) Math.round(yP1 + (yVektorP12 * ((double) i / matrixDimension)) + (yVektorP14 * (((double) j + 1) / matrixDimension)));
                int x4 = (int) Math.round(xP1 + (xVektorP12 * (((double) i + 1) / matrixDimension)) + (xVektorP14 * (((double) j + 1) / matrixDimension)));
                int y4 = (int) Math.round(yP1 + (yVektorP12 * (((double) i + 1) / matrixDimension)) + (yVektorP14 * (((double) j + 1) / matrixDimension)));
                int MinX = Math.min(x1, Math.min(x2, Math.min(x3, x4)));
                int MaxX = Math.max(x1, Math.max(x2, Math.max(x3, x4)));
                int MinY = Math.min(y1, Math.min(y2, Math.min(y3, y4)));
                int MaxY = Math.max(y1, Math.max(y2, Math.max(y3, y4)));
                int x[] = {x1, x2, x3, x4};
                int y[] = {y1, y2, y3, y4};
                Polygon poly = new Polygon(x, y, 4);
                int counter = 0;
                for (int k = MinX; k <= MaxX; k++) {
                    for (int l = MinY; l <= MaxY; l++) {
                        if (poly.contains(k, l)) {
                            if (k < ip.getWidth() && l < ip.getHeight() && ip.getPixel(k, l) != backgroundColor) {
                                counter++;
                            } else {
                                counter--;
                            }
                        }
                    }
                }
                if (counter <= 0) {
                    feature[i][j] = 0;
                } else {
                    feature[i][j] = 1;
                }
            }
        }

        createFeature();
        endProgress();
    }

    /**
     * concatenates the matrix into a single long array
     */
    private void createFeature() {
        double[] x = new double[feature.length * feature.length];
        for (int i = 0; i < feature.length; i++) {
            for (int j = 0; j < feature.length; j++) {
                x[(i * feature.length) + j] = feature[i][j];
            }
        }
        addData(x);
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.NoChanges, Supports.DOES_32, Supports.DOES_16,
                Supports.DOES_8C, Supports.DOES_8G, Supports.DOES_RGB);
    }

    @Override
    public String getDescription() {
        return "Shape feature descriptor that returns the shape matrix as feature";
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setMatrixDimension(int matrixDimension) {
        if (matrixDimension <= 0) {
            throw new IllegalArgumentException("dimension must be > 0 but was " + matrixDimension);
        }
        this.matrixDimension = matrixDimension;
    }
}
