package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 * @author graf
 */
public class HaralickTest {

    public HaralickTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfig1() {
        Haralick haralick = new Haralick();
        haralick.setHaralickDist(0);
    }

    @Test
    public void testConfig2() {
        Haralick haralick = new Haralick();
        haralick.setHaralickDist(10);
        assertEquals(10, haralick.getHaralickDist());
    }

    @Test
    public void testConfig3() throws IOException {
        LibProperties prop = LibProperties.get();
        prop.setProperty(LibProperties.HARALICK_DISTANCE, 2);

        Haralick haralick = new Haralick();
        haralick.setProperties(prop);
        assertEquals(2, haralick.getHaralickDist());
    }

    @Test
    public void testMeanGray1() {
        Haralick.Coocurrence m;
        ByteProcessor ip = new ByteProcessor(10, 10);

        ip.setColor(Color.WHITE);
        ip.fill();
        m = new Haralick.Coocurrence(ip, 255, 2);
        m.calculate();
        assertEquals(255, m.getMeanGrayValue(), 0.0001);

        ip.setColor(Color.BLACK);
        ip.fill();
        m = new Haralick.Coocurrence(ip, 255, 2);
        m.calculate();
        assertEquals(0, m.getMeanGrayValue(), 0.0001);

        ip.setColor(128); // mixed
        ip.fill();
        m = new Haralick.Coocurrence(ip, 255, 2);
        m.calculate();
        assertEquals(128, m.getMeanGrayValue(), 0.0001);

        ip.setColor(200);
        ip.fill();
        ip.setColor(0);
        ip.fill(new Roi(0, 0, 5, 10));
        m = new Haralick.Coocurrence(ip, 255, 2);
        m.calculate();
        assertEquals(100, m.getMeanGrayValue(), 0.0001);
    }

    @Test
    public void testMatrix() {
        ByteProcessor ip = new ByteProcessor(4, 4, new byte[]{
            0, 0, 1, 1, //
            0, 0, 1, 1, //
            0, 2, 2, 2, //
            2, 2, 3, 3});
        byte[][] exp = new byte[][]{
            new byte[]{16, 4, 6, 0},
            new byte[]{4, 12, 5, 0},
            new byte[]{6, 5, 12, 6},
            new byte[]{0, 0, 6, 2}
        };

        Haralick.Coocurrence cm = new Haralick.Coocurrence(ip, 4, 1);
        cm.GRAY_RANGES = exp.length;
        cm.calculate();
        double[][] m = cm.getCooccurrenceMatrix();
        assertEquals(exp.length, m.length);
        for (int i = 0; i < m.length; i++) {
            System.out.println(Arrays2.join(m[i], " ", "%02.0f"));
            for (int j = 0; j < m.length; j++) {
                assertEquals(i + ":" + j, exp[i][j], m[i][j], 0.00001);
            }
        }
    }

    // travisCI just does not like this test
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
