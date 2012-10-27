package de.lmu.dbs.jfeaturelib.features;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.fail;
import org.junit.Test;

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
