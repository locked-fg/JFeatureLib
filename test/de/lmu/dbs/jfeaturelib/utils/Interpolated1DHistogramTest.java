package de.lmu.dbs.jfeaturelib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class Interpolated1DHistogramTest {

    private Interpolated1DHistogram ih;

    @Before
    public void setUp() {
        ih = new Interpolated1DHistogram(0, 100, 10);
    }

    @Test
    public void testAddWrapLeft() {
        ih.add(1, 10);
        assertEquals(6, ih.getData()[0], 0.001);
    }

    @Test
    public void testAddWrapLeft2() {
        ih.add(0, 10);
        assertEquals(5, ih.getData()[0], 0.001);
    }

    @Test
    public void testAddLeft() {
        ih.add(3, 10);
        assertEquals(8, ih.getData()[0], 0.001);
    }

    @Test
    public void testAddMiddle() {
        ih.add(5, 10);
        assertEquals(10, ih.getData()[0], 0.001);
        assertEquals(0, ih.getData()[9], 0.001);
        assertEquals(0, ih.getData()[1], 0.001);
    }

    @Test
    public void testAddRight() {
        ih.add(6, 10);
        assertEquals(9, ih.getData()[0], 0.001);
        assertEquals(1, ih.getData()[1], 0.001);
        assertEquals(0, ih.getData()[9], 0.001);
    }

    @Test
    public void testAddWrapRight() {
        ih.add(98, 10);
        assertEquals(7, ih.getData()[9], 0.001);
        assertEquals(0, ih.getData()[8], 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddBounds() {
        ih.add(100, 10);
    }

    @Test
    public void testAddAggregate() {
        ih.add(1, 10);
        assertEquals(6, ih.getData()[0], 0.001);

        ih.add(7, 10);
        assertEquals(14, ih.getData()[0], 0.001);
        assertEquals(2, ih.getData()[1], 0.001);
    }

    @Test
    public void testData1() {
        ih = new Interpolated1DHistogram(0, 360, 8);
        ih.add(54, 1);
        assertEquals(0.7, ih.getData()[1], 0.001);
        assertEquals(0.3, ih.getData()[0], 0.001);
    }

    @Test
    public void getBinForTest() {
        assertEquals(0, ih.getBinFor(0));
        assertEquals(0, ih.getBinFor(9));
        assertEquals(1, ih.getBinFor(10));
        assertEquals(1, ih.getBinFor(19));
        assertEquals(1, ih.getBinFor(11));
        assertEquals(9, ih.getBinFor(100));
    }

    @Test
    public void testOtherMin() {
        ih = new Interpolated1DHistogram(-50, 50, 10);
        ih.add(-45, 10);
        assertEquals(10, ih.getData()[0], 0.01);

        ih.add(2, 10);
        assertEquals(5, ih.getBinFor(0));
        assertEquals(7, ih.getData()[5], 0.01); // value1
        assertEquals(3, ih.getData()[4], 0.01); // value2
    }

    @Test
    public void testDoubles() {
        ih = new Interpolated1DHistogram(0, 1, 10);
        ih.add(0.5, 10);
        assertEquals(5, ih.getData()[4], 0.001);
        assertEquals(5, ih.getData()[5], 0.001);

        ih.add(0.75, 10);
        assertEquals(10, ih.getData()[7], 0.001);
    }
}
