package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.features.ColorHistogram.TYPE;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class ColorHistogramTest {

    @Test
    public void testSetType() {
        ColorHistogram descriptor = new ColorHistogram();

        descriptor.setType(TYPE.HSB);
        assertEquals(TYPE.HSB, descriptor.type);

        descriptor.setType(TYPE.RGB);
        assertEquals(TYPE.RGB, descriptor.type);
    }

    @Test
    public void testSetProperties() throws IOException {
        LibProperties properties = LibProperties.get();
        ColorHistogram descriptor = new ColorHistogram();

        for (int i = 1; i < 3; i++) {
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_X, i);
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Y, i);
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Z, i);
            descriptor.setProperties(properties);

            assertEquals(i, descriptor.binX);
            assertEquals(i, descriptor.binY);
            assertEquals(i, descriptor.binZ);
        }

        try {
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_X, 0);
            descriptor.setProperties(properties);
            fail("0 not allowed!");
        } catch (IllegalArgumentException e) {
        }

        try {
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Y, 0);
            descriptor.setProperties(properties);
            fail("0 not allowed!");
        } catch (IllegalArgumentException e) {
        }

        try {
            properties.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Z, 0);
            descriptor.setProperties(properties);
            fail("0 not allowed!");
        } catch (IllegalArgumentException e) {
        }

    }

    @Test
    public void testRun() {
        LibProperties p = new LibProperties();
        p.setProperty(LibProperties.COLOR_HISTOGRAMS_TYPE, "RGB");
        p.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_X, 2);
        p.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Y, 2);
        p.setProperty(LibProperties.COLOR_HISTOGRAMS_BINS_Z, 2);

        ImageProcessor ip = new ColorProcessor(5, 5);
        ip.setColor(Color.red);
        ip.fill();
        
        ColorHistogram instance = new ColorHistogram();
        instance.setProperties(p);
        instance.run(ip);
        List<double[]> features = instance.getFeatures();
        
        assertEquals(1, features.size());
        assertEquals(1, features.get(0)[4], 0.001);
        assertEquals(1, Arrays2.sum(features.get(0)), 0.001);
    }
}