package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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
public class Kernel implements FeatureDescriptor {

    /**
     * Standard 3x3 SOBEL mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * @see http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SOBEL = new float[]{
        1, 0, -1,
        2, 0, -2,
        1, 0, -1
    };
    /**
     * Standard 3x3 SCHARR mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * @see http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SCHARR = new float[]{
        3, 0, -3,
        10, 0, -10,
        3, 0, -3
    };
    /**
     * 3x3 PREWITT mask (-1, 0, 1, ...)
     *
     * @see http://en.wikipedia.org/wiki/Prewitt_operator
     */
    public static final float[] PREWITT = new float[]{
        -1, 0, 1,
        -1, 0, 1,
        -1, 0, 1
    };
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ByteProcessor image;
    private int width;
    private int height;
    private float[] kernel;
    private int kernelWidth;
    private int treshold;
    private int[] result;

    /**
     * Constructs a new detector with standart Sobel kernel
     */
    public Kernel() {
        this(SOBEL);
    }

    /**
     * Constructs a new detector with given double[] kernel. The double array is
     * converted immediately to a float array.
     *
     * @param kernel double[] with kernel
     */
    public Kernel(double[] kernel) {
        this.kernelWidth = Math.round((float) Math.sqrt(kernel.length + 1.0f));
        this.kernel = Arrays2.convertToFloat(kernel);
        this.treshold = 0;
    }

    /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     */
    public Kernel(float[] kernel) {
        this.kernelWidth = Math.round((float) Math.sqrt(kernel.length + 1.0f));
        this.kernel = kernel;
        this.treshold = 0;
    }

    /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     * @param width width of kernel
     */
    public Kernel(float[] kernel, int width) {
        this.kernelWidth = width;
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
     * Sets the convolution kernel
     *
     * @param kernel float[] with kernel
     */
    public void setKernel(float[] kernel) {
        this.kernel = kernel;
    }

    /**
     * Returns width of the kernel
     *
     * @return int with width of kernel
     */
    public int getKernelWidth() {
        return kernelWidth;
    }

    /**
     * Setter for width of the kernel
     *
     * @param kernelWidth int with width
     */
    public void setKernelWidth(int kernelWidth) {
        this.kernelWidth = kernelWidth;
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
     * @param treshold int
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
        pcs.firePropertyChange(Progress.getName(), null, new Progress(0, "initialized"));

        width = image.getWidth();
        height = image.getHeight();

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
        result = (int[]) image.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, width, height, null);

        pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));
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
     * Returns the image edges as INT_ARGB array. This can be used to create a
     * buffered image, if the dimensions are known.
     */
    @Override
    public List<double[]> getFeatures() {
        if (result != null) {
            ArrayList<double[]> thisResult = new ArrayList<>(1);
            thisResult.add(Arrays2.convertToDouble(result));
            return thisResult;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Returns information about the getFeauture returns in a String array.
     */
    @Override
    public String getDescription() {
        String info = "Each pixel value";
        return info;
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
        //set.addAll(DOES_ALL);
        return set;
    }

    /**
     * Starts the canny edge detection.
     *
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

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}