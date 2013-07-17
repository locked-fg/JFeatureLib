package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class OpponentHistogramTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        OpponentHistogram f = new OpponentHistogram();
        f.run(ip);
        List<double[]> features = f.getFeatures();
        assertEquals(1, features.size());
        assertEquals(64, features.get(0).length);
    }
}
