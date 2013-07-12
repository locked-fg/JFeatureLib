package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author Franz
 */
public class HistogramTest {

    private volatile Throwable threadException = null;

    public HistogramTest() {
    }

    @Before
    public void setup() {
        threadException = null;
    }

    @Test
    public void testRGB2() {
        try {
            ColorProcessor processor = new ColorProcessor(ImageIO.read(new File(MyRGBThreadTest.class
                    .getResource("/test.jpg").toURI())));
            Histogram histogram = new Histogram();
            histogram.type = Histogram.TYPE.RGB;
            histogram.bins = 128;
            histogram.run(processor);
            List<double[]> features = histogram.getFeatures();
        } catch (IOException | URISyntaxException ex) {
            fail(ex.getMessage());
            Logger.getLogger(HistogramTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testRGBThreads() throws InterruptedException {
        int numberOfThreads = 25;
        List<Thread> l = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 100; j++) {
                            ColorProcessor processor =
                                    new ColorProcessor(ImageIO.read(
                                    new File(MyRGBThreadTest.class.getResource("/test.jpg").toURI())));
                            Histogram histogram = new Histogram();
                            histogram.type = Histogram.TYPE.RGB;
                            histogram.bins = 128;
                            histogram.run(processor);
                            histogram.getFeatures();
                        }
                    } catch (Throwable ex) {
                        threadException = ex;
                    }
                }
            });
            t.start();
            l.add(t);
        }

        String message = "";
        for (Thread t : l) {
            t.join();
            if (threadException != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                threadException.printStackTrace(new PrintStream(baos));
                message = baos.toString();
            }
            assertNull(message, threadException);
        }
    }

    @Test
    public void testRGB() {
        ColorProcessor cp = new ColorProcessor(3, 1);
        cp.setColor(Color.red);
        cp.fill(new Roi(0, 0, 1, 1));
        cp.setColor(Color.green);
        cp.fill(new Roi(1, 0, 2, 1));
        cp.setColor(Color.blue);
        cp.fill(new Roi(2, 0, 3, 1));

        Histogram histogram = new Histogram();
        histogram.type = Histogram.TYPE.RGB;
        histogram.bins = 3;
        histogram.run(cp);
        List<double[]> features = histogram.getFeatures();
        assertEquals(features.get(0).length, 3);
        assertEquals(3, features.get(0)[0], 0.001);
        assertEquals(3, features.get(0)[1], 0.001);
        assertEquals(3, features.get(0)[2], 0.001);
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
     * tests array scaling with a scaling factor that is not an integer. I.e: scale 3 to 2
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
     * tests array scalig where the factor between old length and new length is an integer I.e: scale 2 to 1, 4 to 2 or
     * 4 to 1
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
