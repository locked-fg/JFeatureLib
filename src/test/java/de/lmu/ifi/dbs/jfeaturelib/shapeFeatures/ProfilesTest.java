package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import ij.process.ImageProcessor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franz
 */
public class ProfilesTest {

    public ProfilesTest() {
    }

    @Test
    public void testCreateFeature() {
        Profiles p = new Profiles();
        p.horizontalProfile = new int[]{3, 3};
        p.verticalProfile = new int[]{4, 4};
        p.TLProfile = new int[]{2, 2};
        p.BLProfile = new int[]{1, 1};
        
        p.createFeature();
        double[] arr = p.getFeatures().get(0);
        assertArrayEquals(new double[]{3,3,4,4,2,2,1,1}, arr, 0.001);
    }
}
