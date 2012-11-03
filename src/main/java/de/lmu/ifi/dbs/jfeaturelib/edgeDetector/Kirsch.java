package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Simple implementation of the Kirsch operator
 *
 * @see http://en.wikipedia.org/wiki/Kirsch-Operator
 */
public class Kirsch implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final float[] g1 = new float[]{
        +5, +5, +5,
        -3, -0, -3,
        -3, -3, -3
    };
    private final float[] g2 = new float[]{
        +5, +5, -3,
        +5, -0, -3,
        -3, -3, -3
    };
    private final float[] g3 = new float[]{
        +5, -3, -3,
        +5, -0, -3,
        +5, -3, -3
    };
    private final float[] g4 = new float[]{
        -3, -3, -3,
        +5, -0, -3,
        +5, +5, -3
    };
    private final float[] g5 = new float[]{
        -3, -3, -3,
        -3, -0, -3,
        +5, +5, +5
    };
    private final float[] g6 = new float[]{
        -3, -3, -3,
        -3, -0, +5,
        -3, +5, +5
    };
    private final float[] g7 = new float[]{
        -3, -3, +5,
        -3, -0, +5,
        -3, -3, +5
    };
    private final float[] g8 = new float[]{
        -3, +5, +5,
        -3, -0, +5,
        -3, -3, -3
    };

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        if (!ip.getClass().isAssignableFrom(ByteProcessor.class)) {
            throw new IllegalArgumentException("incompatible processor");
        }
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        double step = 0;

        // initialize output array with absolute minimum
        ByteProcessor result = new ByteProcessor(ip.getWidth(), ip.getHeight());
        Arrays.fill((byte[]) result.getPixels(), Byte.MIN_VALUE);

        // convolve masks and find max
        float[][] kernels = new float[][]{g1, g2, g3, g4, g5, g6, g7, g8};
        for (float[] k : kernels) {
            convolveAndCompare(k, ip, result);
            step += 12.5;
            pcs.firePropertyChange(Progress.getName(), null, new Progress((int) step));
        }

        // set the data back into the incoming object
        ip.setPixels(result.getPixels());
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    /**
     * Duplicates the incoming ip, convolves it with the given kernel and
     * compares the outcome with the data in the result processor. Afterwards
     * the maximum-operation is performed so that the largest values of both
     * processor will retain in the result.
     *
     * @param k
     * @param ip
     * @param result
     */
    private void convolveAndCompare(float[] k, ImageProcessor ip, ByteProcessor result) {
        ByteProcessor compare = (ByteProcessor) ip.duplicate();
        new Kernel(k, 3).run(compare);

        byte[] convolved = (byte[]) compare.getPixels();
        byte[] max = (byte[]) result.getPixels();

        for (int i = 0; i < max.length; i++) {
            if (convolved[i] > max[i]) {
                max[i] = convolved[i];
            }
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
