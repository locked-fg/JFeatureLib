package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * Performs convolution with a given Kernel directly on this image.
 *
 * Basiacally it is just a wrapper for ImageJ's ImageProcessor.convolve().
 *
 * @see ImageProcessor#convolve(float[], int, int)
 * @author Benedikt
 * @author Franz
 */
public class Kernel implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private float[] kernel;
    private int kernelWidth;

    /**
     * Constructs a new detector with standart Sobel kernel
     */
    public Kernel() {
        this.kernelWidth = 3;
        this.kernel = new float[]{1, 0, -1, 2, 0, -2, 1, 0, -1};
    }

    /**
     * Constructs a new detector with given double[] kernel
     *
     * @param kernel double[] with kernel
     * @param width width of kernel
     */
    public Kernel(double[] kernel, int width) {
        this(Arrays2.convertToFloat(kernel), width);
    }

    /**
     * Constructs a new detector with given float[] kernel
     *
     * @param kernel float[] with kernel
     * @param width width of kernel
     */
    public Kernel(float[] kernel, int width) {
        this.kernelWidth = width;
        this.kernel = kernel;
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
     * Starts the convolution.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ip.convolve(kernel, kernelWidth, kernel.length / kernelWidth);

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
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
    //</editor-fold>
}