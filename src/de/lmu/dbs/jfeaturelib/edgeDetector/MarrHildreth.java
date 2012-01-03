package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.dbs.jfeaturelib.Progress;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * The Marr Hildreth edge detector uses the Laplacian of the Gaussian function
 * to detect edges. Faster runtimes could by achieved by using Difference of
 * Gaussians instead.
 */
public class MarrHildreth implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ColorProcessor image;
    private float[] kernel = null;
    /**
     * Gaussian deviation
     */
    private double deviation;
    /**
     * Kernel size
     */
    private int kernelSize;
    /**
     * How many times to apply laplacian gaussian operation
     */
    private int times;

    /**
     * Creates Marr Hildreth edge detection with default parameters
     */
    public MarrHildreth() {
        this.deviation = 0.6;
        this.kernelSize = 7;
        this.times = 1;
    }

    /**
     * Creates Marr Hildreth edge detection
     *
     * @param deviation
     * @param kernelSize
     * @param times
     */
    public MarrHildreth(double deviation, int kernelSize, int times) {
        this.deviation = deviation;
        this.kernelSize = kernelSize;
        this.times = times;

    }

    /*
     * @author Giovane.Kuhn - brain@netuno.com.br
     * http://www.java2s.com/Open-Source/Java-Document/Graphic-Library/apollo/org/apollo/effect/LaplacianGaussianEffect.java.htm
     *
     * Create new laplacian gaussian operation @return New instance
     */
    private float[] createLoGOp() {
        float[] data = new float[kernelSize * kernelSize];
        double first = -1.0 / (Math.PI * Math.pow(deviation, 4.0));
        double second = 2.0 * Math.pow(deviation, 2.0);
        double third;
        int r = kernelSize / 2;
        int x, y;
        for (int i = -r; i <= r; i++) {
            x = i + r;
            for (int j = -r; j <= r; j++) {
                y = j + r;
                third = (Math.pow(i, 2.0) + Math.pow(j, 2.0)) / second;
                data[x + y * kernelSize] = (float) (first * (1 - third) * Math.exp(-third));
            }
        }
        return data;
    }

    public void process() {
        // laplacian gaussian operation
        kernel = createLoGOp();
        for (int i = 0; i < times; i++) {
            image.convolve(kernel, kernelSize, kernelSize);
            int progress = (int) Math.round(i * (100.0 / (double) times));
            pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + times));
        }
    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(Supports.DOES_RGB);
        return set;
    }

    /**
     * Starts the canny edge detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        if (!ip.getClass().isAssignableFrom(ColorProcessor.class)) {
            throw new IllegalArgumentException("incompatible processor");
        }
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        image = (ColorProcessor) ip;
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    //<editor-fold defaultstate="collapsed" desc="accessors">
    /**
     * Return deviation
     *
     * @return
     */
    public double getDeviation() {
        return deviation;
    }

    /**
     * Sets deviation
     *
     * @param deviation
     */
    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    /**
     * Returns size of kernel
     *
     * @return
     */
    public int getKernelSize() {
        return kernelSize;
    }

    /**
     * Sets size of kernel
     *
     * @param kernelSize
     */
    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    /**
     * Returns number of iterations
     *
     * @return
     */
    public int getIterations() {
        return times;
    }

    /**
     * Sets number of iterations
     *
     * @param times
     */
    public void setIterations(int times) {
        this.times = times;
    }
    //</editor-fold>
}
