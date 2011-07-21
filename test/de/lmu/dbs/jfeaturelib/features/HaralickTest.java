package de.lmu.dbs.jfeaturelib.features;

import java.awt.Image;
import ij.process.ColorProcessor;
import java.net.URL;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author graf
 */
public class HaralickTest {

    public HaralickTest() {
    }

    @Test
    public void haralick() throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("test.jpg").toURI().toURL();
        Haralick h = new Haralick();
        h.run(new ColorProcessor(ImageIO.read(url)));
        double[] features = h.getFeaturesDouble();
        // values from the "old" implementation
        double[] exp = {0.01476540897835774, 6.820295531639701, -4121.275943091568, 1.2940467372098054E12, 0.4861984083590497, 27.657563999316697, 183.01272795104856, 3.6224534790086653, 5.082412506506258, 3.8490831959173835, 1.7674297577322204, -0.31227166770030385, 0.9151818951854432};
        assertArrayEquals(exp, features, 0.1);
    }
    
        
    public double[] haralick(Image img){
        Haralick h = new Haralick();
        h.run(new ColorProcessor(img));
        double[] features = h.getFeaturesDouble();
        return features;
    }
    
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
}
