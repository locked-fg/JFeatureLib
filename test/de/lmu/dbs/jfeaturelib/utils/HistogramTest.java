package de.lmu.dbs.jfeaturelib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * @author graf
 */
public class HistogramTest {

    @Test
    public void testAdd_double() {
        Histogram histogram = new Histogram(2, 2);
        assertEquals(0, histogram.getMinValue(), 0.0001);
        assertEquals(2, histogram.getMaxValue(), 0.0001);
        assertEquals(1, histogram.getBinWidth(), 0.0001);

        histogram.add(0);
        assertEquals(1, histogram.getHistogramm()[0], 0000.1);
        histogram.add(0.5);
        assertEquals(2, histogram.getHistogramm()[0], 0000.1);
        histogram.add(1);
        assertEquals(1, histogram.getHistogramm()[1], 0000.1);

        try {
            histogram.add(2);
            fail("this should have thrown an exception");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testAdd_double_double() {
        Histogram instance = new Histogram(2, -1, 1);
        instance.add(-1);
        assertEquals(1, instance.getHistogramm()[0], 0.0001);
        instance.add(0);
        assertEquals(1, instance.getHistogramm()[1], 0.0001);
    }
}
