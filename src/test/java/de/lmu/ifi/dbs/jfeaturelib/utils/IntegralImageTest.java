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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sebp
 */
public class IntegralImageTest {

    private static final int WIDTH = 4;
    private static final int HEIGHT = 3;
    private static final byte[] m_image = new byte[]{
        4, 8, -66, 19, // -66=190
        12, 13, 7, 101,
        -102, -17, 3, 4 // -102=154, -17=239
    };
    private IntegralImage m_img;

    @Before
    public void setUp() {
        ByteProcessor ip = new ByteProcessor(WIDTH, HEIGHT, m_image);
        m_img = new IntegralImage();
        m_img.compute(ip);
    }

    @Test
    public void testGetWidth() {
        assertEquals(WIDTH, m_img.getWidth());
    }

    @Test
    public void testGetHeight() {
        assertEquals(HEIGHT, m_img.getHeight());
    }

    @Test
    public void testGetFirstRow() {
        for (int i = 0; i < WIDTH; i++) {
            assertEquals(0L, m_img.get(i, 0));
        }
    }

    @Test
    public void testGetFirstColumn() {
        for (int i = 0; i < HEIGHT; i++) {
            assertEquals(0L, m_img.get(0, i));
        }
    }

    @Test
    public void testGetx1y1() {
        assertEquals(4L, m_img.get(1, 1));
    }

    @Test
    public void testGetx1y2() {
        assertEquals(12L, m_img.get(2, 1));
    }

    @Test
    public void testGetx1yl3() {
        assertEquals(202L, m_img.get(3, 1));
    }

    @Test
    public void testGetx1y4() {
        assertEquals(221L, m_img.get(4, 1));
    }

    @Test
    public void testGetx2y1() {
        assertEquals(16L, m_img.get(1, 2));
    }

    @Test
    public void testGetx3y1() {
        assertEquals(170L, m_img.get(1, 3));
    }

    @Test
    public void testGetx2y2() {
        assertEquals(37L, m_img.get(2, 2));
    }

    @Test
    public void testGetx3y2() {
        assertEquals(234L, m_img.get(3, 2));
    }

    @Test
    public void testGetx4y2() {
        assertEquals(354L, m_img.get(4, 2));
    }

    @Test
    public void testGetx2y3() {
        assertEquals(430L, m_img.get(2, 3));
    }

    @Test
    public void testGetx3y3() {
        assertEquals(630L, m_img.get(3, 3));
    }

    @Test
    public void testGetx4y3() {
        assertEquals(754L, m_img.get(4, 3));
    }

    @Test
    public void testGetRectangle1() {
        Rectangle rect = new Rectangle(0, 0, WIDTH, HEIGHT);
        assertEquals(754L, m_img.get(rect));
    }

    @Test
    public void testGetRectangle2() {
        Rectangle rect = new Rectangle(0, 0, 1, 1);
        assertEquals(4L, m_img.get(rect));
    }

    @Test
    public void testGetRectangle3() {
        Rectangle rect = new Rectangle(3, 2, 1, 1);
        assertEquals(4L, m_img.get(rect));
    }

    @Test
    public void testGetRectangle4() {
        Rectangle rect = new Rectangle(2, 1, 2, 2);
        assertEquals(115L, m_img.get(rect));
    }

    @Test
    public void testGetRectangle5() {
        Rectangle rect = new Rectangle(0, 1, 2, 2);
        assertEquals(418L, m_img.get(rect));
    }

    @Test
    public void testGetRectangle6() {
        Rectangle rect = new Rectangle(1, 0, 2, 3);
        assertEquals(460L, m_img.get(rect));
    }

    @Test
    public void testGetRectangleOutside1() {
        Rectangle rect = new Rectangle(-10, -10, 10, 10);
        assertEquals(0L, m_img.get(rect));
    }

    @Test
    public void testGetRectangleOutside2() {
        Rectangle rect = new Rectangle(-1, -1, 2, 2);
        assertEquals(4L, m_img.get(rect));
    }

    @Test
    public void testGetRectangleOutside3() {
        Rectangle rect = new Rectangle(4, 3, 20, 20);
        assertEquals(0L, m_img.get(rect));
    }

    @Test
    public void testGetRectangleOutside4() {
        Rectangle rect = new Rectangle(3, 2, 20, 20);
        assertEquals(4L, m_img.get(rect));
    }
}