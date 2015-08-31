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
package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * Performs convolution with a given Kernel directly on this image.
 *
 * Basiacally it is just a wrapper for ImageJ's ImageProcessor.convolve().
 *
 * Predefined masks are SOBEL, SCHARR, PREWITT
 *
 * @see ImageProcessor#convolve(float[], int, int)
 * @author Benedikt
 * @author Franz
 */
public class Kernel extends AbstractDescriptor {

    /**
     * Standard 3x3 SOBEL mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * See http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SOBEL = new float[]{
        1, 0, -1,
        2, 0, -2,
        1, 0, -1
    };
    /**
     * Standard 3x3 SCHARR mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * See http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SCHARR = new float[]{
        3, 0, -3,
        10, 0, -10,
        3, 0, -3
    };
    /**
     * 3x3 PREWITT mask (-1, 0, 1, ...)
     *
     * See http://en.wikipedia.org/wiki/Prewitt_operator
     */
    public static final float[] PREWITT = new float[]{
        -1, 0, 1,
        -1, 0, 1,
        -1, 0, 1
    };
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ByteProcessor image;
    private int treshold = 0;
    float[] kernel;

    /**
     * Constructs a new detector with standart Sobel kernel
     */
    public Kernel() {
        this(SOBEL);
    }

    /**
     * Constructs a new detector with given double[] kernel.
     *
     * The double array is converted immediately to a float array.
     *
     * @param kernel double[] with kernel
     */
    public Kernel(double[] kernel) {
        this(Arrays2.convertToFloat(kernel));
    }

    /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     */
    public Kernel(float[] kernel) {
        setKernel(kernel);
    }

    /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     * @param width width of kernel
     */
    public Kernel(float[] kernel, int width) {
        this.kernel = kernel;
    }

    //<editor-fold defaultstate="collapsed" desc="accessors">
    /**
     * Return the convolution kernel before transformation
     *
     * @return float[] with kernel
     */
    public float[] getKernel() {
        return kernel;
    }

    /**
     * Sets the convolution kernel (must be odd sized).
     *
     * @param kernel float[] with kernel
     * @throws IllegalArgumentException if the size is even
     */
    public void setKernel(float[] kernel) {
        if (kernel.length % 2 == 0) {
            throw new IllegalArgumentException("kernel size must be odd but was " + kernel.length);
        }
        this.kernel = kernel;
    }

    /**
     * Returns width of the kernel
     *
     * @return int with width of kernel
     */
    public int getKernelWidth() {
        return Math.round((float) Math.sqrt(kernel.length + 1.0f));
    }

    /**
     * Setter for width of the kernel
     *
     * @param kernelWidth int with width
     * @deprecated since 1.5.0
     */
    @Deprecated
    public void setKernelWidth(int kernelWidth) {
        throw new UnsupportedOperationException("width cannot be set");
    }

    /**
     * Returns treshold
     *
     * @return treshold
     */
    public int getTreshold() {
        return treshold;
    }

    /**
     * Setter for treshold, default zero
     *
     * @param treshold 
     */
    public void setTreshold(int treshold) {
        if (treshold < 255) {
            this.treshold = treshold;
        } else {
            this.treshold = 254;
        }
    }

    /**
     * Proceses the image applying the kernel in x- and y-direction
     */
    public void process() {
        startProgress();

        final int width = image.getWidth();
        final int height = image.getHeight();
        final int kernelWidth = Math.round((float) Math.sqrt(kernel.length + 1.0f));

        ByteProcessor imgX = new ByteProcessor(image.createImage());
        ByteProcessor imgY = new ByteProcessor(image.createImage());

        float[] kernelX = kernel;
        float[] kernelY = new float[kernelWidth * kernelWidth];
        float[][] kernelX2D = new float[kernelWidth][kernelWidth];
        int i = 0;
        for (int x = 0; x < kernelWidth; x++) {
            for (int y = 0; y < kernelWidth; y++) {
                kernelX2D[x][y] = kernelX[i++];
            }
        }

        float[][] kernelY2D = rotateFloatCW(kernelX2D);
        i = 0;
        for (int x = 0; x < kernelWidth; x++) {
            for (int y = 0; y < kernelWidth; y++) {
                kernelY[i++] = kernelY2D[x][y];
            }
            int progress = (int) Math.round(i * (100.0 / (double) (kernelWidth * kernelWidth)));
            pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + kernelWidth * kernelWidth));
        }

        imgX.convolve(kernelX, kernelWidth, kernelWidth);
        imgY.convolve(kernelY, kernelWidth, kernelWidth);
        int[][] imgXa = imgX.getIntArray();
        int[][] imgYa = imgY.getIntArray();
        int[][] imgA = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                imgA[x][y] = (int) Math.round(Math.sqrt(imgXa[x][y] * imgXa[x][y] + imgYa[x][y] * imgYa[x][y]));
                if (imgA[x][y] > 255) {
                    imgA[x][y] = 255;
                } else if (imgA[x][y] < treshold) {
                    imgA[x][y] = 0;
                }
            }
        }

        image.setIntArray(imgA);

        endProgress();
    }

    private float[][] rotateFloatCW(float[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        float[][] ret = new float[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M - 1 - r] = mat[r][c];
            }
        }
        return ret;
    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB,
                Supports.DOES_16);
        return set;
    }

    /**
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }
}
