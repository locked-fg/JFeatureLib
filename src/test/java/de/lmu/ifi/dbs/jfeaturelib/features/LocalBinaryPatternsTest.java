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

import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;
import ij.process.ByteProcessor;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sebp
 */
public class LocalBinaryPatternsTest {

    private LocalBinaryPatterns m_lbp;

    @Before
    public void setUp() {
        m_lbp = new LocalBinaryPatterns();
    }

    @Test
    public void testGetBinaryPattern() {
        m_lbp.setNumPoints(8);
        m_lbp.setRadius(1);

        ByteProcessor ip = new ByteProcessor(
                MeanPatchIntensityHistogramTest.WIDTH_3,
                MeanPatchIntensityHistogramTest.HEIGHT_3,
                MeanPatchIntensityHistogramTest.IMAGE_3);
        m_lbp.setImageProcessor(ip);

        assertEquals(0, m_lbp.getBinaryPattern(1, 1));
        assertEquals(255, m_lbp.getBinaryPattern(2, 1));
        assertEquals(0b00000100, m_lbp.getBinaryPattern(3, 1));
    }

}