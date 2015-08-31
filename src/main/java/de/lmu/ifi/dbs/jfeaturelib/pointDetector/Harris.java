/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
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
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.pointDetector;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Harris Corner Detection
 *
 * @author Mariagrazia Messina - mariagraziamess@libero.it
 * http://svg.dmi.unict.it/iplab/imagej/Plugins/Feature%20Point%20Detectors/Harris/harris.htm
 */
public class Harris implements PointDetector {

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private List<ImagePoint> resultingCorners;
    // list which will contain the corners at every iteration
    private List<int[]> corners;
    // half-window size
    private int halfwindow = 1;
    // variance
    private float gaussiansigma = 0;
    // threshold parameters
    private int minDistance = 0;
    private int minMeasure = 0;
    private int piramidi = 0;
    // the vector we'll use to compute the gradient
    private GradientVector gradient = new GradientVector();
    // corners
    int matriceCorner[][];

    /**
     * Creates Harris Corner detection with default parameters
     */
    public Harris() {
        this.gaussiansigma = 1.4f;
        this.minMeasure = 10;
        this.minDistance = 80;
        this.piramidi = 1;
    }

    /**
     * Creates Harris Corner Detection
     *
     * @param gaussianSigma Gaussian Variance (Default: 1.4f)
     * @param minDistance Distance Threshold (Default: 10)
     * @param minMeasure Value Threshold (Default: 80)
     * @param iteractions Number of Iteractions (Default: 1)
     */
    public Harris(float gaussianSigma, int minDistance, int minMeasure, int iteractions) {
        this.gaussiansigma = gaussianSigma;
        this.minMeasure = minMeasure;
        this.minDistance = minDistance;
        this.piramidi = iteractions;
    }

    /**
     * Returns the Corners as an ImagePoint List
     *
     * @return ImagePoint List
     */
    @Override
    public List<ImagePoint> getPoints() {
        return resultingCorners;
    }

    /**
     * Defines the capability of the algorithm.
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
        return set;
    }

    /**
     * Starts the Harris Corner Detection
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ByteProcessor bp = (ByteProcessor) ip.convertToByte(true);
        int width = bp.getWidth();
        int height = bp.getHeight();
        int potenza = (int) Math.pow(2, piramidi - 1);
        if ((width / potenza < 8) || (height / potenza < 8)) {
            piramidi = 1;
        }

        for (int i = 0; i < this.piramidi; i++) {
            corners = new ArrayList<>();
            resultingCorners = new ArrayList<>();
            filter(bp, this.minMeasure, this.minDistance, i);
            bp = Supporto.smussaEsottocampiona(bp, 3, this.gaussiansigma);
        }

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    /**
     * Harris Corner Detection
     *
     * @param c immagine
     * @param minMeasure threshold of the minimum corner value
     * @param minDistance threshold of the minimum distance between two corners
     */
    private void filter(ByteProcessor c, int minMeasure, int minDistance, int factor) {

        int width = c.getWidth();
        int height = c.getHeight();

        // darkens the image
        ByteProcessor c2 = new ByteProcessor(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                c2.set(x, y, (int) (c.get(x, y) * 0.80));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // harris response(-1 if the pixel is not a local )
                int h = (int) spatialMaximaofHarrisMeasure(c, x, y);

                // adds the corner to the list if it's above the threshold
                if (h >= minMeasure) {
                    if (factor != 0) {
                        int XY[] = mappatura(x, y, factor);
                        x = XY[0];
                        y = XY[1];
                    }

                    corners.add(new int[]{x, y, h});
                    resultingCorners.add(new ImagePoint(x, y));

                }
            }
        }

        // we only keep the highest response (?) values
        Iterator<int[]> iter = corners.iterator();
        while (iter.hasNext()) {
            int[] p = iter.next();
            for (int[] n : corners) {
                if (n == p) {
                    continue;
                }
                int dist = (int) Math.sqrt((p[0] - n[0]) * (p[0] - n[0]) + (p[1] - n[1]) * (p[1] - n[1]));
                if (dist > minDistance) {
                    continue;
                }
                if (n[2] < p[2]) {
                    continue;
                }
                iter.remove();
                break;
            }
        }


    }

    /**
     * returns the harris measure of the pixel (x,y) if it's a maximum otherwise -1
     *
     * @param c image
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the harris response if the pixel is a local maximum, -1 otherwise
     */
    private double spatialMaximaofHarrisMeasure(ByteProcessor c, int x, int y) {
        int n = 8;
        int[] dx = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
        int[] dy = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
        // calculating the harris response on x,y
        double w = harrisMeasure(c, x, y);
        // we calculate the harris response for every point in a
        // neighbourhood of x,y
        for (int i = 0; i < n; i++) {
            double wk = harrisMeasure(c, x + dx[i], y + dy[i]);
            // if at least a value of the neighbourhood is greater than that of (x,y)
            // then it's not a local maximum ...
            if (wk >= w) {
                return -1;
            }
        }
        // ...otherwise it is
        return w;
    }

    /**
     * computa harris corner response
     *
     * @param c Image map
     * @param x x-coordinate
     * @param y y-coordinate
     * @return harris corner response
     */
    private double harrisMeasure(ByteProcessor c, int x, int y) {
        double m00 = 0, m01 = 0, m10 = 0, m11 = 0;

        // k = det(A) - lambda * trace(A)^2
        // A is the second moment matrix
        // lambda is generally between 0.04 and 0.06. we chose 0.06
        for (int dy = -halfwindow; dy <= halfwindow; dy++) {
            for (int dx = -halfwindow; dx <= halfwindow; dx++) {
                int xk = x + dx;
                int yk = y + dy;
                if (xk < 0 || xk >= c.getWidth()) {
                    continue;
                }
                if (yk < 0 || yk >= c.getHeight()) {
                    continue;
                }

                // gradient of c in the point (xk,yk)
                double[] g = gradient.getVector(c, xk, yk);
                double gx = g[0];
                double gy = g[1];

                // we calculate the weight of the gaussian window on dx,dy
                double gw = gaussian(dx, dy, gaussiansigma);

                // matrix elements
                m00 += gx * gx * gw;
                m01 += gx * gy * gw;
                m10 = m01;
                m11 += gy * gy * gw;
            }
        }

        // harris = det(A) - 0.06*traccia(A)^2;
        //det(A)=m00*m11 - m01*m10
        double det = m00 * m11 - m01 * m10;
        //tr(A)=(m00+m11)*(m00+m11);
        double traccia = (m00 + m11);
        // harris response= det-k tr^2;
        double harris = det - 0.06 * (traccia * traccia);
        return harris / (256 * 256);
    }

    /**
     * Function to calculate the Gaussian window
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param sigma2 variance
     * @return value of the function
     */
    private double gaussian(double x, double y, float sigma2) {
        double t = (x * x + y * y) / (2 * sigma2);
        double u = 1.0 / (2 * Math.PI * sigma2);
        double e = u * Math.exp(-t);
        return e;
    }

    /**
     * Function to map the pixels of the subsampled image within the original one
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param fact parametro di scala
     * @return coordinate x e y nell'immagine originale
     */
    public int[] mappatura(int x, int y, int fact) {
        int nuoviXY[] = new int[2];
        nuoviXY[0] = x * (2 * fact);
        nuoviXY[1] = y * (2 * fact);
        return nuoviXY;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Gradient vector classe to calculate the smoothed gradient, using both the x and y derivatives of a gaussian
     * function
     *
     * @author Messina Mariagrazia
     *
     */
    static class GradientVector {

        int halfwindow = 1;
        double sigma2 = 1.2;
        double[][] kernelGx = new double[2 * halfwindow + 1][2 * halfwindow + 1];
        double[][] kernelGy = new double[2 * halfwindow + 1][2 * halfwindow + 1];

        public GradientVector() {
            for (int y = -halfwindow; y <= halfwindow; y++) {
                for (int x = -halfwindow; x <= halfwindow; x++) {
                    kernelGx[halfwindow + y][halfwindow + x] = Gx(x, y);
                    kernelGy[halfwindow + y][halfwindow + x] = Gy(x, y);
                }
            }
        }

        /**
         * Function to smooth an image through a gaussian to then compute its x-derivative (Drog operator)
         *
         * @param x x-coordinate
         * @param y y-coordinate
         * @return value of the gaussian in x,y
         */
        private double Gx(int x, int y) {
            double t = (x * x + y * y) / (2 * sigma2);
            double d2t = -x / sigma2;
            double e = d2t * Math.exp(-t);
            return e;
        }

        /**
         * Function to smooth an image through a gaussian to then compute its y-derivative (Drog operator)
         *
         * @param x x-coordinate
         * @param y y-coordinate
         * @return value of the gaussian in x,y
         */
        private double Gy(int x, int y) {
            double t = (x * x + y * y) / (2 * sigma2);
            double d2t = -y / sigma2;
            double e = d2t * Math.exp(-t);
            return e;
        }

        /**
         * Function that puts in a vector the value of the gradient of all the points within a window (returns the
         * Gradient value in the pixel (x,y))
         *
         * @param x x-coordinate
         * @param y y-coordinate
         * @param c image
         * @return value of the x and y gradient in all the points of the window
         */
        public double[] getVector(ByteProcessor c, int x, int y) {
            double gx = 0, gy = 0;
            for (int dy = -halfwindow; dy <= halfwindow; dy++) {
                for (int dx = -halfwindow; dx <= halfwindow; dx++) {
                    int xk = x + dx;
                    int yk = y + dy;
                    double vk = c.getPixel(xk, yk); // <-- value of the pixel
                    gx += kernelGx[halfwindow - dy][halfwindow - dx] * vk;
                    gy += kernelGy[halfwindow - dy][halfwindow - dx] * vk;
                }
            }

            double[] gradientVector = new double[]{gx, gy};
            return gradientVector;
        }
    }

    static class Supporto {

        static ByteProcessor smussaEsottocampiona(ByteProcessor input, int window, float sigma)
                throws IllegalArgumentException {
            ByteProcessor prepocessing = (ByteProcessor) input.duplicate();
            float gauss[] = initGaussianKernel(window, sigma);
            Convolver convolver = new Convolver();
            ImageProcessor temp = prepocessing.convertToFloat();
            convolver.convolve(temp, gauss, (int) Math.sqrt(gauss.length), (int) Math.sqrt(gauss.length));
            prepocessing = (ByteProcessor) input.duplicate();
            int prepocessingWidth = prepocessing.getWidth();
            int prepocessingHeight = prepocessing.getHeight();
            ByteProcessor out = new ByteProcessor(prepocessingWidth / 2, prepocessingHeight / 2);
            if (prepocessingWidth % 2 != 0) {
                prepocessingWidth--;
            }
            if (prepocessingHeight % 2 != 0) {
                prepocessingHeight--;
            }
            for (int i = 0, x = 0; i < prepocessingWidth; i = i + 2) {
                for (int j = 0, y = 0; j < prepocessingHeight; j = j + 2) {
                    out.set(x, y, prepocessing.get(i, j));
                    y++;
                }
                x++;
            }
            return out;
        }

        /**
         * Creates the gaussian and inserts the values in an array
         *
         * @param window rows and columns of the gaussian matrix. It has to be odd
         * @param sigma
         * @return gaussian array
         * @throws IllegalArgumentException if the window is negative, zero or even or if sigma is zero or even.
         */
        static float[] initGaussianKernel(int window, float sigma) throws IllegalArgumentException {
            controlInput(window, sigma);
            short aperture = (short) (window / 2);
            float[][] gaussianKernel = new float[2 * aperture + 1][2 * aperture + 1];
            float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
            int k = 0;
            float sum = 0;
            for (int dy = -aperture; dy <= aperture; dy++) {
                for (int dx = -aperture; dx <= aperture; dx++) {
                    gaussianKernel[dx + aperture][dy + aperture] = (float) Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                    sum += gaussianKernel[dx + aperture][dy + aperture];
                }
            }
            for (int dy = -aperture; dy <= aperture; dy++) {
                for (int dx = -aperture; dx <= aperture; dx++) {
                    out[k++] = gaussianKernel[dx + aperture][dy + aperture] / sum;
                }
            }
            return out;
        }

        /**
         * checks the gaussian's values
         *
         * @param window the gaussian's window.
         * @param sigma the gaussian's sigma value
         * @throws IllegalArgumentException if the window is negative, zero or even or if sigma is zero or even.
         */
        private static void controlInput(int window, float sigma) throws
                IllegalArgumentException {
            if (window % 2 == 0) {
                throw new IllegalArgumentException("Window isn't an odd.");
            }
            if (window <= 0) {
                throw new IllegalArgumentException("Window is negative or zero");
            }
            if (sigma <= 0) {
                throw new IllegalArgumentException("Sigma of the gaussian is zero or negative.");
            }
        }
    }
}