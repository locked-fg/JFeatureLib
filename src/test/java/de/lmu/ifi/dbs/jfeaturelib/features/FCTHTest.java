package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class FCTHTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        FCTH cedd = new FCTH();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(192, features.get(0).length);
    }
}
