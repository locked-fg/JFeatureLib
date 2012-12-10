package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.shapeFeatures.Profiles.ProfileTuple;
import ij.process.ImageProcessor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franz
 */
public class ProfilesTest {

    @Test
    public void testRun() {
        
    }

    @Test
    public void testShortenProfile() {
        int[] arr = {0, 0, 1, 2, 0, 3, 4, 0, 0};
        Profiles p = new Profiles();
        ProfileTuple tuple = p.shortenProfile(arr);
        assertEquals(2, tuple.start);
        assertEquals(3, tuple.end);
    }

    @Test
    public void reinsert() {
        int[] arr1 = {0, 0, 1, 2, 0, 3, 4, 0, 0};
        int[] exp = {1, 2, 0, 3, 4};

        Profiles p = new Profiles();
        int[] arr2 = p.reinsert(arr1);
        assertArrayEquals(exp, arr2);
    }

    @Test
    public void testCreateFeature() {
        Profiles p = new Profiles();
        p.horizontalProfile = new int[]{3, 3};
        p.verticalProfile = new int[]{4, 4};
        p.TLProfile = new int[]{2, 2};
        p.BLProfile = new int[]{1, 1};

        p.createFeatures();
        assertEquals(4, p.getFeatures().size());
        assertArrayEquals(new double[]{3, 3}, p.getFeatures().get(0), 0.001);
        assertArrayEquals(new double[]{4, 4}, p.getFeatures().get(1), 0.001);
        assertArrayEquals(new double[]{2, 2}, p.getFeatures().get(2), 0.001);
        assertArrayEquals(new double[]{1, 1}, p.getFeatures().get(3), 0.001);
    }
}
