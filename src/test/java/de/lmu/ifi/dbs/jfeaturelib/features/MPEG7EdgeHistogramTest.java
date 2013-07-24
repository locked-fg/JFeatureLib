package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MPEG7EdgeHistogramTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        MPEG7EdgeHistogram cedd = new MPEG7EdgeHistogram();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(80, features.get(0).length);
    }
}
