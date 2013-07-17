package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class JCDTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        JCD jcd = new JCD();
        jcd.run(ip);
        List<double[]> features = jcd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(168, features.get(0).length);
    }
}
