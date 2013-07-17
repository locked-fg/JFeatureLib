package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class JpegCoefficientsHistogramTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        JpegCoefficientHistogram f = new JpegCoefficientHistogram();
        f.run(ip);
        List<double[]> features = f.getFeatures();
        assertEquals(1, features.size());
        assertEquals(192, features.get(0).length);
    }
}
