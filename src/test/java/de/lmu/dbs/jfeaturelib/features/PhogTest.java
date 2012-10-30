package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.LibProperties;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Franz
 */
public class PhogTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetProperties() throws Exception {
        PHOG phog = new PHOG();
        LibProperties props = LibProperties.get();

        props.setProperty(LibProperties.PHOG_CANNY, true);
        phog.setProperties(props);
        assertTrue(phog.useCanny);

        props.setProperty(LibProperties.PHOG_CANNY, false);
        phog.setProperties(props);
        assertFalse(phog.useCanny);
    }

    @Test
    public void testRun() throws IOException, URISyntaxException {
        File url = new File("src/test/resources/test.jpg");

        LibProperties props = LibProperties.get();
        props.setProperty(LibProperties.PHOG_CANNY, true);

        PHOG phog = new PHOG();
        phog.setProperties(props);
        ImageProcessor src = new ColorProcessor(ImageIO.read(url));
        ImageProcessor ip = src.duplicate();
        phog.applyCanny(ip);

        // just check first line
        int diff = 0;
        for(int i = 0; i < ip.getWidth(); i++){
            diff += ip.get(i) != src.get(i) ? 1:0;
        }
        assertTrue("no more than 5 pixels different", diff > 5);
        
        assertFalse(0 == src.get(0));
        assertFalse(ip.get(0) == src.get(0));
    }
}
