package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class LuminanceLayoutTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ByteProcessor(10, 10);
        
        LuminanceLayout f = new LuminanceLayout();
        f.run(ip);
        List<double[]> features = f.getFeatures();
        assertEquals(1, features.size());
        assertEquals(64, features.get(0).length);
    }
}
