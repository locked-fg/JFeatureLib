package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 *
 * @author Tommaso Testa, Salvatore Adriano Zappala', Salvo Scalia
 * @author recoded by Robert Zelhofer
 * @see http://svg.dmi.unict.it/iplab/imagej/Plugins/Forensics/Filters/PAGINE%20HTML/Laplaciano.html
 */
public class LaplaceFilter implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int laplaceKernelDimensions;
    float[] kernel3x3 = {
        0, 1, 0,
        1, -4, 1,
        0, 1, 0};
    private final float[] kernel3x3_2 = {
        1, 1, 1,
        1, -8, 1,
        1, 1, 1};
    private final float[] kernel5x5 = {
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, -24, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1};
    private final float[] kernel7x7 = {
        -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 48, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1};

    /**
     * Constructs default Laplace Edge Filter
     */
    public LaplaceFilter() {
        laplaceKernelDimensions = 1;
    }

    /**
     * Contructs Laplace Egde Filter
     * @param supportDiagonalEdges Choose the dimension of laplacian kernel: 1, 3, 5 or 7
     */
    public LaplaceFilter(int kernelDimensions) {
        laplaceKernelDimensions = kernelDimensions;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.DOES_8G,
                Supports.DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        float[] kernel = getKernel(laplaceKernelDimensions);

        ip.convolve(kernel, laplaceKernelDimensions, laplaceKernelDimensions);

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    private float[] getKernel(int dimensions) {
        switch (dimensions) {
            case 1:
                return kernel3x3;
            case 3:
                return kernel3x3_2;
            case 5:
                return kernel5x5;
            case 7:
                return kernel7x7;
        }
        return null;
    }
}
