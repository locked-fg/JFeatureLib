package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class GaborTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        Gabor cedd = new Gabor();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(60, features.get(0).length);
    }
}
