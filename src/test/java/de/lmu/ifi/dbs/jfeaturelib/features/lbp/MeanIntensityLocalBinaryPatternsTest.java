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
package de.lmu.ifi.dbs.jfeaturelib.features.lbp;

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
public class MeanIntensityLocalBinaryPatternsTest {

    private MeanIntensityLocalBinaryPatterns m_lbp;

    @Before
    public void setUp() {
        m_lbp = new MeanIntensityLocalBinaryPatterns();
    }

    @Test
    public void testGetBinaryPattern1() {
        m_lbp.createPatchDescriptor(
            new ByteProcessor(MeanPatchIntensityHistogramTest.WIDTH_2,
                MeanPatchIntensityHistogramTest.HEIGHT_2,
                MeanPatchIntensityHistogramTest.IMAGE_2));

        byte expected = 0b00110110;
        assertEquals(expected, m_lbp.getBinaryPattern(2, 2) & 0xFF);
    }

    @Test
    public void testFeatures1() {
        m_lbp.run(
            new ByteProcessor(MeanPatchIntensityHistogramTest.WIDTH_2,
                MeanPatchIntensityHistogramTest.HEIGHT_2,
                MeanPatchIntensityHistogramTest.IMAGE_2));
        List<double[]> features = m_lbp.getFeatures();
        double[] hist = features.get(0);

        assertEquals(256, hist.length);
        assertEquals(1.0, hist[54], 0);
        assertEquals(1.0, Arrays2.sum(hist), 0);
    }

    @Test
    public void testGetBinaryPattern2() {
        m_lbp.createPatchDescriptor(
            new ByteProcessor(MeanPatchIntensityHistogramTest.WIDTH_3,
                MeanPatchIntensityHistogramTest.HEIGHT_3,
                MeanPatchIntensityHistogramTest.IMAGE_3));

        int[] expected = new int[] {
            0b00110110, 0b11111011, 0b01100001,
            0b00000000, 0b10000000, 0b10000000
        };

        int k = 0;
        assertEquals(expected[k++], m_lbp.getBinaryPattern(2, 2) & 0xFF);
        assertEquals(expected[k++], m_lbp.getBinaryPattern(3, 2) & 0xFF);
        assertEquals(expected[k++], m_lbp.getBinaryPattern(4, 2) & 0xFF);
        assertEquals(expected[k++], m_lbp.getBinaryPattern(2, 3) & 0xFF);
        assertEquals(expected[k++], m_lbp.getBinaryPattern(3, 3) & 0xFF);
        assertEquals(expected[k++], m_lbp.getBinaryPattern(4, 3) & 0xFF);
    }

    @Test
    public void testFeatures2() {
        m_lbp.run(
            new ByteProcessor(MeanPatchIntensityHistogramTest.WIDTH_3,
                MeanPatchIntensityHistogramTest.HEIGHT_3,
                MeanPatchIntensityHistogramTest.IMAGE_3));
        List<double[]> features = m_lbp.getFeatures();
        double[] hist = features.get(0);

        assertEquals(256, hist.length);
        assertEquals(1.0, hist[54], 0);
        assertEquals(1.0, hist[251], 0);
        assertEquals(1.0, hist[97], 0);
        assertEquals(1.0, hist[0], 0);
        assertEquals(2.0, hist[128], 0);
        assertEquals(6.0, Arrays2.sum(hist), 0);
    }

}