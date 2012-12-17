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

import static java.lang.Math.*;

public class Descriptor {

    final static float pi = 3.14159f;
    final static double[][] gauss25 = {
        {0.02350693969273, 0.01849121369071, 0.01239503121241, 0.00708015417522, 0.00344628101733, 0.00142945847484, 0.00050524879060},
        {0.02169964028389, 0.01706954162243, 0.01144205592615, 0.00653580605408, 0.00318131834134, 0.00131955648461, 0.00046640341759},
        {0.01706954162243, 0.01342737701584, 0.00900063997939, 0.00514124713667, 0.00250251364222, 0.00103799989504, 0.00036688592278},
        {0.01144205592615, 0.00900063997939, 0.00603330940534, 0.00344628101733, 0.00167748505986, 0.00069579213743, 0.00024593098864},
        {0.00653580605408, 0.00514124713667, 0.00344628101733, 0.00196854695367, 0.00095819467066, 0.00039744277546, 0.00014047800980},
        {0.00318131834134, 0.00250251364222, 0.00167748505986, 0.00095819467066, 0.00046640341759, 0.00019345616757, 0.00006837798818},
        {0.00131955648461, 0.00103799989504, 0.00069579213743, 0.00039744277546, 0.00019345616757, 0.00008024231247, 0.00002836202103}
    };
    final static double[][] gauss33 = {
        {0.014614763, 0.013958917, 0.012162744, 0.00966788, 0.00701053, 0.004637568, 0.002798657, 0.001540738, 0.000773799, 0.000354525, 0.000148179},
        {0.013958917, 0.013332502, 0.011616933, 0.009234028, 0.006695928, 0.004429455, 0.002673066, 0.001471597, 0.000739074, 0.000338616, 0.000141529},
        {0.012162744, 0.011616933, 0.010122116, 0.008045833, 0.005834325, 0.003859491, 0.002329107, 0.001282238, 0.000643973, 0.000295044, 0.000123318},
        {0.00966788, 0.009234028, 0.008045833, 0.006395444, 0.004637568, 0.003067819, 0.001851353, 0.001019221, 0.000511879, 0.000234524, 9.80224E-05},
        {0.00701053, 0.006695928, 0.005834325, 0.004637568, 0.003362869, 0.002224587, 0.001342483, 0.000739074, 0.000371182, 0.000170062, 7.10796E-05},
        {0.004637568, 0.004429455, 0.003859491, 0.003067819, 0.002224587, 0.001471597, 0.000888072, 0.000488908, 0.000245542, 0.000112498, 4.70202E-05},
        {0.002798657, 0.002673066, 0.002329107, 0.001851353, 0.001342483, 0.000888072, 0.000535929, 0.000295044, 0.000148179, 6.78899E-05, 2.83755E-05},
        {0.001540738, 0.001471597, 0.001282238, 0.001019221, 0.000739074, 0.000488908, 0.000295044, 0.00016243, 8.15765E-05, 3.73753E-05, 1.56215E-05},
        {0.000773799, 0.000739074, 0.000643973, 0.000511879, 0.000371182, 0.000245542, 0.000148179, 8.15765E-05, 4.09698E-05, 1.87708E-05, 7.84553E-06},
        {0.000354525, 0.000338616, 0.000295044, 0.000234524, 0.000170062, 0.000112498, 6.78899E-05, 3.73753E-05, 1.87708E-05, 8.60008E-06, 3.59452E-06},
        {0.000148179, 0.000141529, 0.000123318, 9.80224E-05, 7.10796E-05, 4.70202E-05, 2.83755E-05, 1.56215E-05, 7.84553E-06, 3.59452E-06, 1.50238E-06}
    };

    /**
     * Returns the descriptor of the interest point as an array of 64 float
     * values. Depends on the
     * <code>orientation</code> field of the interest point. (I.e. in case of
     * non-upright SURF the orientation must be computed and assigned before.)
     */
    public static void computeAndSetDescriptor(InterestPoint ipt, IntegralImage intImg, Params p) {
        float co, si;
        if (p.isUpright()) {
            co = 1;
            si = 0;
        } else {
            co = (float) cos(ipt.orientation);
            si = (float) sin(ipt.orientation);
        }

        float[] desc = new float[p.getDescSize()];

        int sample_x, sample_y, count = 0;
        int ix = 0, jx = 0, xs = 0, ys = 0;
        float gauss_s1 = 0.f, gauss_s2 = 0.f;
        float rx = 0.f, ry = 0.f, rrx = 0.f, rry = 0.f, len = 0.f;

        float scale = ipt.scale;
        int doubledScale = 2 * round(scale);
        int x = round(ipt.x);
        int y = round(ipt.y);

        int i = -8, j = 0; // <-- ?!
        float cx = -0.5f, cy = 0.f; // Subregion centers for the 4x4 gaussian
        // weighting
        float dx, dy, mdx, mdy;

        // Calculate descriptor for this interest point
        while (i < 12) {
            j = -8; // ?
            i -= 4;
            cx += 1.0f;
            cy = -0.5f;

            while (j < 12) {
                dx = dy = mdx = mdy = 0;
                cy += 1.0f;
                j -= 4;
                ix = i + 5;
                jx = j + 5;
                xs = round(x + (-jx * scale * si + ix * scale * co));
                ys = round(y + (jx * scale * co + ix * scale * si));

                for (int k = i; k < i + 9; ++k) {
                    for (int l = j; l < j + 9; ++l) {
                        // Get coords of sample point on the rotated axis
                        sample_x = round(x + scale * (-l * si + k * co));
                        sample_y = round(y + scale * (l * co + k * si));

                        // Get the gaussian weighted x and y responses
                        gauss_s1 = gaussian(xs - sample_x, ys - sample_y, 2.5f * scale);
                        rx = haarX(intImg, sample_x, sample_y, doubledScale);
                        ry = haarY(intImg, sample_x, sample_y, doubledScale);

                        // Get the gaussian weighted x and y responses on
                        // rotated axis
                        rrx = gauss_s1 * (-rx * si + ry * co);
                        rry = gauss_s1 * (rx * co + ry * si);

                        dx += rrx;
                        dy += rry;
                        mdx += abs(rrx);
                        mdy += abs(rry);
                    }
                }

                // Add the values to the descriptor vector
                gauss_s2 = gaussian(cx - 2, cy - 2, 1.5f);

                desc[count++] = dx * gauss_s2;
                desc[count++] = dy * gauss_s2;
                desc[count++] = mdx * gauss_s2;
                desc[count++] = mdy * gauss_s2;

                len += (dx * dx + dy * dy + mdx * mdx + mdy * mdy) * gauss_s2 * gauss_s2;

                j += 9;
            }
            i += 9;
        }

        // Convert to Unit Vector:
        len = (float) sqrt(len);
        for (int idx = 0; idx < p.getDescSize(); idx++) {
            desc[idx] /= len;
        }

        // save descriptor into Interest Point object:
        ipt.descriptor = desc;

    }

    /**
     * Returns orientation of the dominant response vector.
     */
    public static void computeAndSetOrientation(InterestPoint ipt, IntegralImage intImg) {
        float[] resX = new float[109];
        float[] resY = new float[109];
        float[] Ang = new float[109];
        int id[] = {6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6};

        // Calculate haar responses for points within radius of 6*scale
        final int x = round(ipt.x), y = round(ipt.y), s = round(ipt.scale);
        int waveletSize = 4 * s;
        int i = 0;
        float gauss = 0;
        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                if (dx * dx + dy * dy < 36) {
                    gauss = (float) gauss25[id[dx + 6]][id[dy + 6]];
                    resX[i] = gauss * haarX(intImg, x + dx * s, y + dy * s, waveletSize); // <-- Multiplication with scale value is here!
                    resY[i] = gauss * haarY(intImg, x + dx * s, y + dy * s, waveletSize);
                    Ang[i] = getAngle(resX[i], resY[i]);
                    i++;
                }
            }
        }

        // Calculate the dominant direction by looping over pi/3 sliding windows (between the angles ang1 and ang2) 
        // around the feature point
        final float windowSize = pi / 3; /*
         * The size of the sliding window (in radians) used to assign an
         * orientation
         */
        final float step = 0.15f; /*
         * Increment used for the orientation sliding window (in radians)
         */
        // ^^ OpenSURF: 0.15 radians (=>41.8 times), cvsurf: 5 degrees (=>72 times)
        float twoPi = 2 * pi;
        float sumX = 0, sumY = 0;
        float current = 0, max = 0, orientation = 0;
        for (float ang1 = 0, ang2 = ang1 + windowSize; ang1 < twoPi; ang1 += step, ang2 += step) {
            sumX = sumY = 0;
            for (int k = 0; k < Ang.length; k++) {
                // Determine wheteher the point's angel is in between the angels ang1 and ang2 (i.e. within the window):
                if ((ang1 <= Ang[k] && Ang[k] < ang2)
                        || (ang1 <= Ang[k] + twoPi && Ang[k] + twoPi < ang2)) { // <-- because we can be over the 0 again
                    sumX += resX[k];
                    sumY += resY[k];
                }
            }
            // If the vector produced from this window is longer than all 
            // previous vectors then this forms the new dominant direction
            current = sumX * sumX + sumY * sumY; // <-- squared size of the vector
            if (current > max) {
                max = current;
                orientation = getAngle(sumX, sumY);
            }
        }

        // save orientation into Interest Point object
        ipt.orientation = orientation;
    }

    /**
     * Calculate the value of the 2d gaussian at x,y
     */
    static float gaussian(float x, float y, float sig) {
        return (float) ((1 / (2 * pi * sig * sig)) * exp(-(x * x + y * y) / (2 * sig * sig)));
    }

    /**
     * Calculates Haar wavelet response in X direction at the point
     * <code>x</code>,
     * <code>y</code> for the wavelet size
     * <code>w</code>.
     */
    static float haarX(IntegralImage img, int x, int y, int w) {
        int wHalf = w / 2;
        return img.area(x, y - wHalf, wHalf, w)
                - img.area(x - wHalf, y - wHalf, wHalf, w);

    }

    /**
     * Calculates Haar wavelet response in Y direction at the point
     * <code>x</code>,
     * <code>y</code> for the wavelet size
     * <code>w</code>.
     */
    static float haarY(IntegralImage img, int x, int y, int w) {
        int wHalf = w / 2;
        return img.area(x - wHalf, y, w, wHalf)
                - img.area(x - wHalf, y - wHalf, w, wHalf);
    }

    /**
     * Get the angle from the +ve x-axis of the vector given by (X Y)
     */
    static float getAngle(float x, float y) {
        if (x >= 0 && y >= 0) {
            return (float) atan(y / x);
        }
        if (x < 0 && y >= 0) {
            return (float) (pi - atan(-y / x));
        }
        if (x < 0 && y < 0) {
            return (float) (pi + atan(y / x));
        }
        if (x >= 0 && y < 0) {
            return (float) (2 * pi - atan(-y / x));
        }
        return 0;
    }
}
