package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurfTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(100, 100);
        ip.setColor(Color.black);
        ip.fill();
        ip.setColor(Color.white);
        ip.fillOval(30, 30, 40, 40);
        
        
        SURF f = new SURF();
        f.run(ip);
        List<double[]> features = f.getFeatures();
        assertEquals(9, features.size());
        assertEquals(70, features.get(0).length);
        assertEquals(70, features.get(1).length);
    }
}
