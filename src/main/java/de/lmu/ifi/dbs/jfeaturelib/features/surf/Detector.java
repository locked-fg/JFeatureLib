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
package de.lmu.ifi.dbs.jfeaturelib.features.surf;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;

public class Detector {

    public static List<InterestPoint> fastHessian(IntegralImage img, Params p) {

        /**
         * Determinant of hessian responses
         */
        float[][][] det = new float[p.getLayers()][img.getWidth()][img.getHeight()];

        /**
         * Trace of the determinant of hessian responses. The sign of trace (the
         * laplacian sign) is used to indicate the type of blobs: negative means
         * light blobs on dark background, positive -- vice versa. (Signs will
         * be computed in constructor of interest point)
         */
        float[][][] trace = new float[p.getLayers()][img.getWidth()][img.getHeight()];

        List<InterestPoint> res = new ArrayList<InterestPoint>(2000);

        for (int octave = 0, step = p.getInitStep(); octave < p.getOctaves(); octave++, step *= p.getStepIncFactor()) {

            // Determine the border width (margin) for the largest filter in the octave
            // (the largest filter in the octave must fit into image)
            int margin = p.getMaxFilterSize(octave) / 2;
            int xBound = img.getWidth() - margin;  // non-inclusive
            int yBound = img.getHeight() - margin; // non-inclusive

            for (int layer = 0; layer < p.getLayers(); layer++) {
                int w = p.getFilterSize(octave, layer); // filter width == filter size
                int L = w / 3; // lobe size, e.g. 3 in 9x9 filter
                int L2 = 2 * L - 1; // "another lobe" size, e.g. 5 in 9x9 filter (in Dxx and Dyy filters only) 
                int wHalf = w / 2;  // filter's half-width
                int LHalf = L / 2;
                int Lminus1 = L - 1;
                float filterArea = w * w;
                float Dxx, Dyy, Dxy;

                for (int y = margin; y < yBound; y += step) { // row
                    for (int x = margin; x < xBound; x += step) { // column

                        Dxx = img.area(x - wHalf, y - Lminus1, w, L2)
                                - img.area(x - LHalf, y - Lminus1, L, L2) * 3;
                        Dyy = img.area(x - Lminus1, y - wHalf, L2, w)
                                - img.area(x - Lminus1, y - LHalf, L2, L) * 3;
                        Dxy = img.area(x - L, y - L, L, L)
                                - img.area(x + 1, y - L, L, L)
                                + img.area(x + 1, y + 1, L, L)
                                - img.area(x - L, y + 1, L, L);

                        // Normalise the filter responses with respect to their size
                        Dxx /= filterArea;
                        Dyy /= filterArea;
                        Dxy /= filterArea;

                        det[layer][x][y] = Dxx * Dyy - 0.81f * Dxy * Dxy;
                        trace[layer][x][y] = Dxx + Dyy;

                    }
                }

            }


            // 3x3x3 Non-maximum suppression

            // Adjust margins to get true 3x3 neighbors (with respect to the actual 'step')
            margin += step;
            xBound -= step;
            yBound -= step;

            // Iterate over all layers except the first and the last
            for (int layer = 1; layer < p.getLayers() - 1; layer++) {
                int filterSize = p.getFilterSize(octave, layer);
                int filterSizeIncrement = filterSize - p.getFilterSize(octave, layer - 1);
                float v, xInterp, yInterp, scale;

                // Statistics
                int countIPCandidates = 0;
                int countThresholded = 0;
                int countSuppressed = 0;
                int countInterpolationNotSucceed = 0;
                int countBadInterpolationResult = 0;
                int countIP = 0;

                for (int y = margin; y < yBound; y += step) { // row
                    for (int x = margin; x < xBound; x += step) { // column
                        countIPCandidates++;

                        v = det[layer][x][y];
                        if (v < p.getThreshold()) {
                            countThresholded++;
                            continue;
                        }


                        if (!isLocalMaximum(v, det, layer, x, y, step)) {
                            countSuppressed++;
                            continue;
                        }

                        // Interpolate maxima location within the 3x3x3 neighborhood
                        float[] X = interpolatePoint(det, layer, x, y, step);
                        if (X == null) {
                            countInterpolationNotSucceed++;
                            continue;
                        }

                        xInterp = x + X[0] * step;
                        yInterp = y + X[1] * step;
                        scale = (filterSize + X[2] * filterSizeIncrement) * (1.2f / 9.0f);

                        // "Sometimes the interpolation step gives a negative size etc."
                        if (scale >= 1 && xInterp >= 0 && xInterp < img.getWidth() && yInterp >= 0 && yInterp < img.getHeight()) { // <-- from OpenCV-2.0.0
                            // ^^ should be OK instead of "if (abs(xi) < 0.5f && abs(xr) < 0.5f && abs(xc) < 0.5f)"	(OpenSURF).
                            // The OpenCV-2.0.0 version leaves ~ 25% more IPs
                            countIP++;
                            res.add(new InterestPoint(xInterp, yInterp, det[layer][x][y], trace[layer][x][y], scale));
                        } else {
                            countBadInterpolationResult++;
                        }

                    } // end for (x)
                } // end for (y)

                p.getStatistics().add(octave, layer, countIPCandidates, countThresholded, countSuppressed, countInterpolationNotSucceed, countBadInterpolationResult, countIP);


            } // end for (layer)

            // End Non-maximum suppression for current layer

        } // end for (octave)

        return res;

    }

    /**
     * Returns
     * <code>true</code> if the value
     * <code>v</code> is a local maximum in the 3x3x3 matrix around the center (
     * <code>s</code>,
     * <code>x</code>,
     * <code>y</code>) (exclusive).
     */
    private static boolean isLocalMaximum(float v, float[][][] det, int s, int x, int y, int step) {
        float[][] l = det[s - 1], m = det[s], u = det[s + 1]; // lower, middle and upper layers
        int px = x - step, nx = x + step, py = y - step, ny = y + step; // px: "previos x", nx: "next x", ...

        return (v >= l[px][py] && v >= l[px][y] && v >= l[px][ny]
                && v >= l[x][py] && v >= l[x][y] && v >= l[x][ny]
                && v >= l[nx][py] && v >= l[nx][y] && v >= l[nx][ny]
                && v >= m[px][py] && v >= m[px][y] && v >= m[px][ny]
                && v >= m[x][py] && /*
                 * v is here
                 */ v >= m[x][ny] && // <-- v is at m[x][x]
                v >= m[nx][py] && v >= m[nx][y] && v >= m[nx][ny]
                && v >= u[px][py] && v >= u[px][y] && v >= u[px][ny]
                && v >= u[x][py] && v >= u[x][y] && v >= u[x][ny]
                && v >= u[nx][py] && v >= u[nx][y] && v >= u[nx][ny]);
    }

    /**
     * Interpolating function. Adapted from Lowe's SIFT implementation by
     * Christopher Evans (OpenSURF).
     */
    static float[] interpolatePoint(float[][][] det, int i, int x, int y, int step) {

        float[][] l = det[i - 1], m = det[i], u = det[i + 1]; // lower, middle and upper layers
        int px = x - step, nx = x + step, py = y - step, ny = y + step; // "previos" x, "next" x, "previos" y, "next" y

        // Compute the partial derivatives in x, y and scale of a pixel
        float dx = -(m[nx][y] - m[px][y]) / 2;
        float dy = -(m[x][ny] - m[x][py]) / 2;
        float ds = -(u[x][y] - l[x][y]) / 2;
        float[] b = {dx, dy, ds};


        // Compute the 3D Hessian matrix for a pixel
        float v = m[x][y];

        float dxx = (m[px][y] - 2 * v + m[nx][y]);
        float dxy = (m[nx][ny] - m[px][ny] - m[nx][py] + m[px][py]) / 4;
        float dxs = (u[nx][y] - u[px][y] - l[nx][y] + l[px][y]) / 4;

        float dyx = dxy;
        float dyy = (m[x][py] - 2 * v + m[x][ny]);
        float dys = (u[x][ny] - u[x][py] - l[x][ny] + l[x][py]) / 4;

        float dsx = dxs;
        float dsy = dys;
        float dss = (l[x][y] - 2 * v + u[x][y]);

        float[][] A = {{dxx, dxy, dxs}, {dyx, dyy, dys}, {dsx, dsy, dss}};

        // Try to solve the linear system A*x = b
        float[] X = new float[3];
        if (solve(A, b, X)) {
            return X;
        } else {
            return null;
        }

    }

    /**
     * Solves the linear system A*x = b, where A is 3x3 matrix. Returns true if
     * succeeds (vector x will be filled with values) or false if the linear
     * system has no solution.
     */
    public static boolean solve(float[][] A, float[] B, float[] X) {

        float a = A[0][0], b = A[0][1], c = A[0][2],
                d = A[1][0], e = A[1][1], f = A[1][2],
                g = A[2][0], h = A[2][1], i = A[2][2];

        float r = B[0],
                s = B[1],
                t = B[2];

        float detA = det(a, b, c,
                d, e, f,
                g, h, i);

        if (equal(detA, 0)) {
            return false;
        }

        float detX1 = det(r, b, c,
                s, e, f,
                t, h, i);

        float detX2 = det(a, r, c,
                d, s, f,
                g, t, i);

        float detX3 = det(a, b, r,
                d, e, s,
                g, h, t);

        X[0] = detX1 / detA;
        X[1] = detX2 / detA;
        X[2] = detX3 / detA;

        return true;

    }

    static float det(float a, float b, float c, float d, float e, float f, float g, float h, float i) {
        return a * (e * i - f * h) + b * (f * g - d * i) + c * (d * h - e * g);
    }
    /**
     * Small number (1.4e-43) for comparison of float values.<br> A float value
     * <code>val</code> is considered to be 0 if <br>
     * <code>abs(val) < EPSILON</code>.
     */
    static final float EPSILON = Float.MIN_VALUE * 100; // 1.4e-45*100 = 1.4e-43

    /**
     * Two float values are considered to be equal if
     * <code>abs(a-b) < EPSILON</code>.
     */
    public static boolean equal(float f1, float f2) {
        return abs(f1 - f2) < EPSILON;
    }
}
