package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MomentsTest {

    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        
        Moments cedd = new Moments();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(4, features.get(0).length);
        assertEquals(0, features.get(0)[0], 0.0001);
        assertEquals(0, features.get(0)[1], 0.0001);
        assertTrue(Double.isNaN(features.get(0)[2]));
        assertTrue(Double.isNaN(features.get(0)[3]));
    }
    
    @Test
    public void testRun1() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        ip.setColor(Color.BLACK);
        ip.fill();
        ip.setColor(Color.WHITE);
        ip.fillOval(0, 0, 0, 5);
        
        Moments cedd = new Moments();
        cedd.run(ip);
        List<double[]> features = cedd.getFeatures();
        assertEquals(1, features.size());
        assertEquals(4, features.get(0).length);
        assertEquals(12.75, features.get(0)[0], 0.0001);
        assertEquals(55.85, features.get(0)[1], 0.01);
        assertEquals(4.12, features.get(0)[2], 0.01);
        assertEquals(15.05, features.get(0)[3], 0.01);
    }
}
