package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;


public class AutoColorCorrelogramTest {

    @Test
    public void testSetProperties() throws IOException{
        LibProperties prop = new LibProperties();
        prop.setProperty(LibProperties.AUTOCOLORCORRELOGRAM_DISTANCE, 10);
        AutoColorCorrelogram f = new AutoColorCorrelogram();
        f.setProperties(prop);
        assertEquals(10, f.distance);
    }
    
    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(100, 100);
        AutoColorCorrelogram f = new AutoColorCorrelogram();
        f.run(ip);
        List<double[]> features = f.getFeatures();
        
        assertEquals(1, features.size());
    }
}