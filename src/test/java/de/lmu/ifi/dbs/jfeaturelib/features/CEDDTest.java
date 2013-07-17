package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CEDDTest {

    @Test
    public void testSetProperties() throws Exception {
        LibProperties properties = LibProperties.get();
        properties.setProperty(LibProperties.CEDD_T0, 1d);
        properties.setProperty(LibProperties.CEDD_T1, 2d);
        properties.setProperty(LibProperties.CEDD_T2, 3d);
        properties.setProperty(LibProperties.CEDD_T3, 4d);
        properties.setProperty(LibProperties.CEDD_COMPACT, false);
        
        CEDD cedd = new CEDD();
        cedd.setProperties(properties);

        assertEquals(1d, cedd.T0, 0.0001);
        assertEquals(2d, cedd.T1, 0.0001);
        assertEquals(3d, cedd.T2, 0.0001);
        assertEquals(4d, cedd.T3, 0.0001);
        assertEquals(false, cedd.compact);
        
        properties.setProperty(LibProperties.CEDD_COMPACT, true);
        cedd.setProperties(properties);
        assertEquals(true, cedd.compact);
    }

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        CEDD cedd = new CEDD();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(144, features.get(0).length);
    }
}
