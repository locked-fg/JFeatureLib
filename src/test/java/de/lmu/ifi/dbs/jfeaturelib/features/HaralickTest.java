package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import ij.process.ColorProcessor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author graf
 */
public class HaralickTest {

    public HaralickTest() {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConfig1(){
        Haralick haralick = new Haralick();
        haralick.setHaralickDist(0);
    }
    
    @Test
    public void testConfig2(){
        Haralick haralick = new Haralick();
        haralick.setHaralickDist(10);
        assertEquals(10, haralick.getHaralickDist());
    }

    @Test
    public void testConfig3() throws IOException{
        LibProperties prop = LibProperties.get();
        prop.setProperty(LibProperties.HARALICK_DISTANCE, 2);
        
        Haralick haralick = new Haralick();
        haralick.setProperties(prop);
        assertEquals(2, haralick.getHaralickDist());
    }
    
    // travisci just does not like this test
//    @Test
//    public void haralick() throws IOException, URISyntaxException {
//        File url = new File(HaralickTest.class.getResource("/test.jpg").toURI());
//        Haralick h = new Haralick();
//        h.run(new ColorProcessor(ImageIO.read(url)));
//        List<double[]> features = h.getFeatures();
//        double[] exp = {0.01476540897835774, 6.820295531639701, 256115.1832968409, 1.2940467372098054E12, 0.4861984083590497, 27.657563999316697, 183.01272795104856, 3.6224534790086653, 5.082412506506258, 3.8490831959173835, 1.7674297577322204, -0.31227166770030385, 0.9151818951854432, 1.1326601440921478};
//        assertArrayEquals(exp, features.get(0), 0.1);
//    }
    
}
