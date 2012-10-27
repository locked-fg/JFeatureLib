package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * Simple implementation of the Roberts(Cross)-Operator.
 *
 * The Roberts-Cross-Operator uses just 2x2 matrices instead of the alsmost
 * standard odd-sized kenrels.
 *
 * For sake of simplicity (and in order to conveniently use imageJ's convolve
 * method), the following 3x3 matrix is used which should produce the same
 * result as the 2x2 matrix (just a bit more inefficient):
 * <pre>
 * 0  0  0
 * 0  1  1
 * 0 -1 -1
 * </pre>
 *
 * @see http://en.wikipedia.org/wiki/Roberts_Cross
 */
public class Roberts implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final int[] kernel = new int[]{
        0, +0, +0,
        0, +1, +1,
        0, -1, -1
    };

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.DOES_16,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ip.convolve3x3(kernel);

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
