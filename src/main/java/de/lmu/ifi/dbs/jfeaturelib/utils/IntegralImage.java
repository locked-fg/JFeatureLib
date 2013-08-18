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
package de.lmu.ifi.dbs.jfeaturelib.utils;

import ij.process.ByteProcessor;
import java.awt.Rectangle;

/**
 * Integral images are an efficient way to retrieve the sum of pixel intensities
 * in any rectangular region of the image.
 *
 * @author sebp
 */
public class IntegralImage {

    private int m_height;
    private int m_width;
    private long[] m_values;

    /**
     * Constructs the integral image.
     *
     * @param ip ImageProcessor to use
     */
    public void compute(final ByteProcessor ip) {
        m_height = ip.getHeight();
        m_width = ip.getWidth();

        m_values = new long[ip.getPixelCount()];
        // initialize north-west corner
        m_values[0] = ip.get(0);
        // initialize first row
        for (int x = 1; x < m_width; x++) {
            long p = ip.get(x) & 0xFF;
            m_values[x] = m_values[x - 1] + p;
        }
        // initialize first column
        for (int y = 1; y < m_height; y++) {
            int idx = y * m_width;
            long p = ip.get(idx) & 0xFF;
            m_values[idx] = m_values[idx - m_width] + p;
        }

        for (int y = 1; y < m_height; y++) {
            int mi = y * m_width + 1;
            for (int x = 1; x < m_width; x++) {
                long p = ip.get(mi) & 0xFF;
                m_values[mi] = p + m_values[mi - 1] + m_values[mi - m_width] - m_values[mi - m_width - 1];
                mi++;
            }
        }
    }

    /**
     * Get value of integral image at specified position.
     *
     * In mathematical terms, this method returns
     * <tt>\sum_{p &lt; x; q &lt; y} I(p, q)</tt>. Calling {@link #compute} is
     * required before calling this method. No sanity-check of indices
     * <tt>x</tt> and <tt>y</tt> is performed!
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return sum of intensities over rectangle from origin to point (x, y)
     */
    public long get(final int x, final int y) {
        if (x <= 0 || y <= 0) {
            return 0L;
        }
        int i = (y - 1) * m_width + x - 1;
        return m_values[i];
    }

    /**
     * Returns the sum of intensities in the specified rectangle.
     *
     * Parts that lie outside of the image are treated as having
     * zero intensity.
     *
     * @param region region of interest
     * @return sum of intensities in specified region
     */
    public long get(final Rectangle region) {
        int x2 = Math.min(region.x + region.width, m_width);
        int y2 = Math.min(region.y + region.height, m_height);
        return get(x2, y2) - get(region.x, y2)
                - get(x2, region.y) + get(region.x, region.y);
    }

    /**
     * Get the image's height.
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * Get the image's width.
     */
    public int getWidth() {
        return m_width;
    }
}
