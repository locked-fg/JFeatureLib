package de.lmu.dbs.jfeaturelib.features;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Franz
 */
public class HistogramTest {

    public HistogramTest() {
    }

    /**
     * tests array scaling with a scaling factor that is not an integer. I.e:
     * scale 3 to 2
     */
    @Test
    public void testScaleInterpolate() {
        Histogram h = new Histogram();
        double[] in, exp;

        in = new double[]{1d, 1d, 1d};
        exp = new double[]{2d, 1d};
        assertArrayEquals(exp, h.scale(in, 2), 0.0001);
    }

    /**
     * tests array scalig where the factor between old length and new length is
     * an integer I.e: scale 2 to 1, 4 to 2 or 4 to 1
     */
    @Test
    public void testScaleDirectFit() {
        Histogram h = new Histogram();
        double[] in, exp;

        in = new double[]{1d, 1d};
        exp = new double[]{2d};
        assertArrayEquals(exp, h.scale(in, 1), 0.0001);

        in = new double[]{1d, 1d, 2d, 3d};
        exp = new double[]{2d, 5d};
        assertArrayEquals(exp, h.scale(in, 2), 0.0001);

        in = new double[]{1d, 1d, 2d, 3d};
        exp = new double[]{7d};
        assertArrayEquals(exp, h.scale(in, 1), 0.0001);
    }
}
