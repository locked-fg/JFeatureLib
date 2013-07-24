package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MPEG7ColorLayoutTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        MPEG7ColorLayout cedd = new MPEG7ColorLayout();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(33, features.get(0).length);
    }
}
