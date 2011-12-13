package de.lmu.dbs.jfeaturelib.features;

import java.io.File;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SiftTest {
    
    public SiftTest() {
    }

//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }

    @Test(expected=IOException.class)
    public void testInit() throws IOException {
            Sift instance = new Sift(new File("foo.bar"));
            fail();
    }
}
