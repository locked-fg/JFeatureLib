package de.lmu.ifi.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franz
 */
public class ThumbnailTest {

    @Test
    public void testResize() {
        ImageProcessor ip = new ColorProcessor(10, 10);
        Thumbnail desc = new Thumbnail();
        desc.resize = true;
        desc.width = 9;
        desc.height = 3;
        desc.run(ip);

        int length = desc.getFeatures().get(0).length;
        assertEquals(9 * 3, length);
        
        double[] features = desc.getFeatures().get(0);
        assertArrayEquals(new double[9*3], features, 0.001);
    }
    
    @Test
    public void testRun() {
        ImageProcessor ip = new ColorProcessor(2, 2);
        ip.setColor(5);
        ip.fill();
        Thumbnail desc = new Thumbnail();
        desc.run(ip);

        double[] features = desc.getFeatures().get(0);
        assertEquals(4, features.length);

        double[] exp = new double[4];
        Arrays.fill(exp, 0, 4, 5);
        assertArrayEquals(exp, features, 0.001);
    }
}
