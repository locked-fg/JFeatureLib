package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franz
 */
public class AbstractFeatureDescriptorTest {

    // black mask with a white reagion from 10x10 to 90x90 (80 width/height)
    ImageProcessor mask = new ByteProcessor(100, 100);

    public AbstractFeatureDescriptorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mask.setColor(0);
        mask.fill();
        mask.setColor(255);
        mask.fill(new Roi(10, 10, 80, 80));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInMask() {
        ImageProcessor ip = new ColorProcessor(100, 100);
        ip.setColor(Color.red);
        ip.fill();
        ip.setMask(mask);
        
        AbstractFeatureDescriptor afd = new AbstractFeatureDescriptorImpl();
        afd.setMask(ip);
        
        assertFalse(afd.inMask(0, 0));
        assertTrue(afd.inMask(10, 10));
        assertTrue(afd.inMask(15, 15));
        assertFalse(afd.inMask(5, 15));
    }
    
    @Test
    public void testInMaskNull() {
        ImageProcessor ip = new ColorProcessor(100, 100);
        AbstractFeatureDescriptor afd = new AbstractFeatureDescriptorImpl();
        afd.setMask(ip);
        
        assertTrue(afd.inMask(0, 0));
        assertTrue(afd.inMask(10, 10));
        assertTrue(afd.inMask(15, 15));
        assertTrue(afd.inMask(5, 15));
    }
    
    @Test(expected=NullPointerException.class)
    public void testInMaskException() {
        AbstractFeatureDescriptor afd = new AbstractFeatureDescriptorImpl();
        afd.setMask(null);
    }

    public class AbstractFeatureDescriptorImpl extends AbstractFeatureDescriptor {

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void run(ImageProcessor ip) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
