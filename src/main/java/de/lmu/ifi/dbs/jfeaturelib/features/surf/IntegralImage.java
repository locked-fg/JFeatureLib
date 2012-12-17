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

import ij.IJ;
import ij.process.*;
// TODO detach from IJ, move IJ-related code into IJFacade
public class IntegralImage {

    /**
     * Should be set via setData() method.
     */
    private float[][] data;

    /**
     * Sets internal array to the argument
     * <code>a</code> and updates attributes width, height, maxX, maxY.
     */
    private void setData(float[][] a) {
        data = a;
        width = data.length;
        height = data[0].length;
        maxX = width - 1;
        maxY = height - 1;
    }
    private int width;
    private int height;
    /**
     * Max valid X coordinate.
     */
    private int maxX;
    /**
     * Max valid Y coordinate.
     */
    private int maxY;

    public float get(int x, int y) {
        return data[x][y];
    }

    // set() is not needed
    // public void set(int x, int y, float val) {
    // data[x][y] = val;
    // }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    // TODO: constructors for byte[] and float[] input images?
    /**
     * Creates the integral image of the source image. The order of coordinates
     * in the integral image is [x][y]. <p> The
     * <code>integral image</code> is an image where pixel values are sums of
     * pixel values above and to the left of the actual pixel (inclusive) from
     * the source image.
     */
    public IntegralImage(ImageProcessor src) { // TODO: remove this constructor in favor of IntegralImage(ImageProcessor, boolean)!!!
        // Get the source image as a new float[][] grayscale array
        // using the defautl ImageJ algorithm.
        // NB: in OpenSURF C# version following is used to convert from RGB to
        // grayscale:
        // luminance = (0.3 R + 0.59 G + 0.11 B) / 255.

        // ImageJ:
        // Weighting factors used by getPixelValue(), getHistogram() and
        // convertToByte().
        // Enable "Weighted RGB Conversion" in <i>Edit/Options/Conversions</i>
        // to use 0.299, 0.587 and 0.114.
        // private static double rWeight=1d/3d, gWeight=1d/3d, bWeight=1d/3d;
        // TODO: Call ColorProcessor.setWeightingFactors() ?

        // TODO: implement as int[][] or long[][]? Or as int[] or long[]?

        // NB: OpenSURF does   cvConvertScale( gray8, gray32, 1.0 / 255.0, 0 );
        // at converting to grayscale!


        // Initialize the instance variables
        setData(src.convertToByte(false).getFloatArray()); // [width][height]
        // [x][y]

        convertInternalBufferToIntegralImage();
    }

    /**
     * Compute the integral image.
     */
    private void convertInternalBufferToIntegralImage() {
        float rowSum = 0;

        // first row:
        for (int x = 0; x < width; x++) {
            rowSum += data[x][0];
            data[x][0] = rowSum;
        }

        // the rest:
        for (int y = 1; y < height; y++) {
            rowSum = 0;
            for (int x = 0; x < width; x++) {
                rowSum += data[x][y];
                data[x][y] = rowSum + data[x][y - 1];
            }
        }
    }

    public IntegralImage(ImageProcessor src, boolean weightedAndNormalizedConversion) {
        // TODO: make weightedAndNormalizedConversion the default and remove other constructors! 
        int width = src.getWidth();
        int height = src.getHeight();
        float[][] a = new float[width][height];
        float val, min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        int i, x, y;
        float[] col;

        // Convert to float and compute min and max values

        if (src instanceof ByteProcessor || src instanceof ShortProcessor || src instanceof FloatProcessor) {
            for (i = 0, y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {
                    val = src.getf(i++);
                    a[x][y] = val;
                    if (val < min) {
                        min = val;
                    } else if (val > max) {
                        max = val;
                    }
                }
            }

        } else if (src instanceof ColorProcessor) { // weighted conversion
            int intVal, r, g, b;
            float rw = 0.299f, gw = 0.587f, bw = 0.114f;

            for (i = 0, y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {

                    intVal = src.get(i++);
                    r = (intVal & 0xff0000) >> 16;
                    g = (intVal & 0xff00) >> 8;
                    b = intVal & 0xff;
                    val = r * rw + g * gw + b * bw;

                    a[x][y] = val;
                    if (val < min) {
                        min = val;
                    } else if (val > max) {
                        max = val;
                    }
                }
            }

        } else {			// Should never happen.
            IJ.error("SURF: IntegralImage", "Unknown image type.\nCannot proceed.");
            return;
        }


        // Normalize values (i.e. scale max-min range to 0..1 range)

        float scale = 1f / (max - min);
        for (i = 0, x = 0; x < width; x++) {
            col = a[x];
            for (y = 0; y < height; y++) {
                val = col[y] - min;
                if (val < 0) {
                    val = 0;
                }
                val *= scale;
                if (val > 1) {
                    val = 1;
                }
                col[y] = val;
            }
        }

        setData(a);

        convertInternalBufferToIntegralImage();

    }

    /**
     * Computes the sum of pixels in an integral image
     * <code>img</code> within the rectangle specified by the top-left start
     * coordinate (inclusive) and size.<br>
     */
    float area(int x1, int y1, int rectWidth, int rectHeight) {
        x1--;
        y1--;                  //  A +--------+ B
        int x2 = x1 + rectWidth;     //    |        |        A(x1,y1)
        int y2 = y1 + rectHeight;    //  C +--------+ D

        // bounds check
        if (x1 > maxX) {
            x1 = maxX;
        }
        if (y1 > maxY) {
            y1 = maxY;
        }
        if (x2 > maxX) {
            x2 = maxX;
        }
        if (y2 > maxY) {
            y2 = maxY;
        }

        float A = (x1 < 0 || y1 < 0) ? 0 : data[x1][y1];
        float B = (x2 < 0 || y1 < 0) ? 0 : data[x2][y1];
        float C = (x1 < 0 || y2 < 0) ? 0 : data[x1][y2];
        float D = (x2 < 0 || y2 < 0) ? 0 : data[x2][y2];

        return D - B - C + A;

    }

    /**
     * A speed optimized version of {@link #area(FloatProcessor, int, int, int, int)}
     * without bounds check (for 0 < x1 < width and 0 < y1 < height).
     */
    float area2(int x1, int y1, int rectWidth, int rectHeight) {
        x1--;
        y1--;                  //  A +--------+ B
        int x2 = x1 + rectWidth;     //    |        |        A(x1,y1)
        int y2 = y1 + rectHeight;    //  C +--------+ D
        return data[x2][y2] - data[x2][y1] - data[x1][y2] + data[x1][y1]; // D - B - C + A 
    }
}
