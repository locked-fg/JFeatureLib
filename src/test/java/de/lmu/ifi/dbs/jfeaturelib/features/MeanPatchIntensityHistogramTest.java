/*
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
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
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sebp
 */
public class MeanPatchIntensityHistogramTest {

    static final int WIDTH = 5;
    static final int HEIGHT = 4;
    static final byte[] IMAGE = new byte[]{
        16, 12, -4, 13, 124,  // -4=252
        14, -1, 0, -57, 77,   // -1=255, -57=199
        33, 15, -26, -7, 10,  // -26=230, -7=249
        17, 56, 19, 113, -125 // -125=131
    };

    static final int WIDTH_2 = 5;
    static final int HEIGHT_2 = 5;
    static final byte[] IMAGE_2 = new byte[]{
        16, 12, -4, 13, 124,  // -4=252
        14, -1, 0, -57, 77,   // -1=255, -57=199
        33, 15, -26, -7, 10,  // -26=230, -7=249
        17, 56, 19, 113, -125, // -125=131
        15, -23, 111, -35, 99  // -23=233, -35=221
    };

    static final int WIDTH_3 = 7;
    static final int HEIGHT_3 = 6;
    static final byte[] IMAGE_3 = new byte[]{
        16,  12,  -4,  13, 124,  88, 121, // -4=252,
        14,  -1,   0, -57,  77,  13, 124, // -1=255, -57=199
        33,  15, -26,  -7,  10, 100,  83, // -26=230, -7=249
        17,  56,  19, 113,-125,-100,  24, // -125=131, -100=156
        15, -23, 111, -35,  99,  77,  99, // -23=233, -35=221
       110, -19, 117,   1,  67, -99, -33  // -19=237, -99=157, -33=223
    };

    private MeanPatchIntensityHistogram m_descriptor;

    @Before
    public void setUp() {
        m_descriptor = new MeanPatchIntensityHistogram();
        m_descriptor.setBins(256);
        m_descriptor.setHistogramRange(0, 256);
    }

    @Test
    public void testGetMeanIntensity1() {
        m_descriptor.setSize(1);
        m_descriptor.createIntegralImage(new ByteProcessor(WIDTH, HEIGHT, IMAGE));

        double[] expected = new double[]{
            33, 61, 81.222, 73.889, 45.889,
            38.333, 91.889, 136.111, 128.222, 74.778,
            43.333, 71, 126.222, 114.222, 86.556,
            13.444, 41.111, 75.778, 83.556, 55.889
        };

        int ei = 0;
        for (int y=0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                assertEquals(expected[ei++], m_descriptor.getMeanIntensity(x, y), 0.3);
            }
        }
    }

    @Test
    public void testGetMeanIntensity2() {
        m_descriptor.setSize(2);
        m_descriptor.createIntegralImage(new ByteProcessor(WIDTH, HEIGHT, IMAGE));

        double[] expected = new double[]{
            33.08, 51.52, 59.96, 57.44, 46.16,
            36.76, 59.72, 73.4, 70.2, 56.68,
            36.76, 59.72, 73.4, 70.2, 56.68,
            25.56, 48, 56.72, 54.16, 41.12
        };

        int ei = 0;
        for (int y=0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                assertEquals(expected[ei++], m_descriptor.getMeanIntensity(x, y), 0.3);
            }
        }
    }

    @Test
    public void testGetMeanIntensity3() {
        m_descriptor.setSize(1);
        m_descriptor.createIntegralImage(new ByteProcessor(WIDTH_2, HEIGHT_2, IMAGE_2));
        double[] expected = new double[]{
            91.889, 136.111, 128.222,
            71, 126.222, 114.222,
            81, 138.556, 131.444
        };

        int ei = 0;
        for (int y=1; y < HEIGHT_2 - 1; y++) {
            for (int x = 1; x < WIDTH_2 - 1; x++) {
                assertEquals(expected[ei++], m_descriptor.getMeanIntensity(x, y), 0.3);
            }
        }
    }

    @Test
    public void testGetMeanIntensity4() {
        m_descriptor.setSize(1);
        m_descriptor.createIntegralImage(new ByteProcessor(WIDTH_3, HEIGHT_3, IMAGE_3));
        double[] expected = new double[]{
            91.889, 136.111, 128.222, 97, 82.222,
            71, 126.222, 114.222, 116.444, 79.778,
            81, 138.556, 131.444, 128.444, 86.556,
            101.667, 123.111, 97.667, 113.556, 114.778
        };

        int ei = 0;
        for (int y=1; y < HEIGHT_3 - 1; y++) {
            for (int x = 1; x < WIDTH_3 - 1; x++) {
                assertEquals(expected[ei++], m_descriptor.getMeanIntensity(x, y), 0.3);
            }
        }
    }

    @Test
    public void testFeatures1() {
        m_descriptor.setSize(1);
        m_descriptor.run(new ByteProcessor(WIDTH, HEIGHT, IMAGE));
        List<double[]> features = m_descriptor.getFeatures();
        double[] hist = features.get(0);

        assertEquals(256, hist.length);

        assertEquals(1.0, hist[91], 0);
        assertEquals(1.0, hist[136], 0);
        assertEquals(1.0, hist[128], 0);
        assertEquals(1.0, hist[71], 0);
        assertEquals(1.0, hist[126], 0);
        assertEquals(1.0, hist[114], 0);

        assertEquals(6.0, Arrays2.sum(hist), 0);
    }

    @Test
    public void testFeatures2() {
        m_descriptor.setSize(1);
        m_descriptor.run(new ByteProcessor(WIDTH_2, HEIGHT_2, IMAGE_2));
        List<double[]> features = m_descriptor.getFeatures();
        double[] hist = features.get(0);

        assertEquals(256, hist.length);

        assertEquals(1.0, hist[91], 0);
        assertEquals(1.0, hist[136], 0);
        assertEquals(1.0, hist[128], 0);
        assertEquals(1.0, hist[71], 0);
        assertEquals(1.0, hist[126], 0);
        assertEquals(1.0, hist[114], 0);
        assertEquals(1.0, hist[81], 0);
        assertEquals(1.0, hist[138], 0);
        assertEquals(1.0, hist[131], 0);

        assertEquals(9.0, Arrays2.sum(hist), 0);
    }

    @Test
    public void testFeaturesPatchTooLarge() {
        m_descriptor.setSize(2);
        m_descriptor.run(new ByteProcessor(WIDTH, HEIGHT, IMAGE));
        List<double[]> features = m_descriptor.getFeatures();

        assertEquals(0.0, Arrays2.sum(features.get(0)), 0);
    }

    @Test
    public void testFeatures3() {
        m_descriptor.setSize(2);
        m_descriptor.run(new ByteProcessor(WIDTH_2, HEIGHT_2, IMAGE_2));
        List<double[]> features = m_descriptor.getFeatures();
        double[] hist = features.get(0);

        assertEquals(256, hist.length);
        assertEquals(1.0, hist[100], 0);
        assertEquals(1.0, Arrays2.sum(hist), 0);
    }
}