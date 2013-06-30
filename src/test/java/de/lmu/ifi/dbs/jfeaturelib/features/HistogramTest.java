package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import java.awt.Color;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Franz
 */
public class HistogramTest {

    public HistogramTest() {
    }

    @Test
    public void testWithoutMasking() {
        ColorProcessor blackRed = new ColorProcessor(10, 10);
        blackRed.setColor(Color.black);
        blackRed.fill();
        blackRed.setColor(Color.red);
        blackRed.fill(new Roi(0, 5, 10, 5));

        Histogram histogram = new Histogram();
        histogram.type = Histogram.TYPE.Red;
        histogram.bins = 2;
        histogram.run(blackRed);
        List<double[]> features = histogram.getFeatures();
        assertEquals(features.get(0).length, 2);
        assertEquals(50, features.get(0)[0], 0.001);
        assertEquals(50, features.get(0)[1], 0.001);
    }

    @Test
    public void testWithMasking() {
        ColorProcessor blackRed = new ColorProcessor(10, 10);
        blackRed.setColor(Color.black);
        blackRed.fill();
        blackRed.setColor(Color.red);
        blackRed.fill(new Roi(0, 6, 10, 5));

        ByteProcessor mask = new ByteProcessor(10, 10);
        mask.setColor(Color.black);
        mask.fill();
        mask.setColor(Color.white);
        mask.fill(new Roi(0, 9, 10, 1));
        
        blackRed.setMask(mask);

        Histogram histogram1 = new Histogram();
        histogram1.type = Histogram.TYPE.Red;
        histogram1.bins = 2;
        histogram1.run(blackRed);
        List<double[]> features = histogram1.getFeatures();
        assertEquals(features.get(0).length, 2);
        assertEquals(0, features.get(0)[0], 0.001);
        assertEquals(10, features.get(0)[1], 0.001);
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
