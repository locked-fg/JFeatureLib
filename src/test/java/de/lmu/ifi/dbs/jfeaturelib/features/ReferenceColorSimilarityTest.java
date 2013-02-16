package de.lmu.ifi.dbs.jfeaturelib.features;

import com.google.common.base.Preconditions;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReferenceColorSimilarityTest {

    @Test
    public void testSetPropertiesInit() throws IOException {
        ReferenceColorSimilarity desc = new ReferenceColorSimilarity();
        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "x");
            desc.setProperties(p);
            fail("init value");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testSetPropertiesColor() throws IOException {
        ReferenceColorSimilarity desc = new ReferenceColorSimilarity();

        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "color");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_COLORS, "");
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }
        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "color");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_COLORS, "-1,1");
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }

        LibProperties p = LibProperties.get();
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "color");
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_COLORS, "1,1");
        desc.setProperties(p);
        // this should work!
    }

    @Test
    public void testSetPropertiesBins() throws IOException {
        ReferenceColorSimilarity desc = new ReferenceColorSimilarity();

        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_B, 0);
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }

        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_G, 0);
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }

        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_H, 0);
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }
        try {
            LibProperties p = LibProperties.get();
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
            p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_S, 0);
            desc.setProperties(p);
            fail("color similarity");
        } catch (IllegalArgumentException e) {
        }
    }


    /**
     * VERY basic check if the descriptor can be executed
     * @throws IOException 
     */
    @Test
    public void testRun() throws IOException {
        LibProperties p = LibProperties.get();
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_H, 2);
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_S, 2);
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_B, 2);
        p.setProperty(LibProperties.REFERENCE_COLOR_SIMILARITY_G, 2);

        ImageProcessor ip = new ColorProcessor(3, 3);
        ReferenceColorSimilarity instance = new ReferenceColorSimilarity();
        instance.setProperties(p);
        instance.run(ip);
        List<double[]> features = instance.getFeatures();
        assertFalse(features.isEmpty());
        assertEquals("NaN found in Descriptor", -1, Arrays2.findNaN(features.get(0)));
    }
}