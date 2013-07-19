package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import ij.plugin.ImageCalculator;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class SusanTest {

    @Test
    public void testRun1() throws IOException {
        ImageProcessor expected = new ColorProcessor(ImageIO.read(new File("src/test/resources/susan-out.gif")));
        expected = expected.convertToByte(false);
        expected.invert();

        ImageProcessor result = new ColorProcessor(ImageIO.read(new File("src/test/resources/susan-in.gif")));
        Susan s = new Susan();
        s.setThreshold(10);
        s.run(result);
        result = result.convertToByte(true);
        result.dilate();
        result.dilate();

        int w = result.getWidth();
        int h = result.getHeight();
        int correct = 0;
        int edge = 0;
        for (int i = 0; i < w * h; i++) {
            edge += expected.get(i) != 0 ? 0 : 1;
            if (expected.get(i) == 0 && result.get(i) == 0) {
                correct++;
            }
        }
        assertEquals(edge, correct);
    }

    @Test
    public void testRun2() throws IOException {
        ImageProcessor cp1 = new ColorProcessor(ImageIO.read(new File("src/test/resources/susan-in.gif")));
        ImageProcessor cp2 = new ColorProcessor(ImageIO.read(new File("src/test/resources/susan-in.gif")));
        cp2 = cp2.convertToByte(true);

        Susan s1 = new Susan();
        Susan s2 = new Susan();
        s1.run(cp1);
        s2.run(cp2);

        cp1 = s1.getEdgemask();
        cp2 = s2.getEdgemask();
        int delta = 0;
        for (int i = 0; i < cp1.getPixelCount(); i++) {
            if (cp1.get(i) != cp2.get(i)) {
                delta++;
            }
        }
        // be a bit tolerant, due to dithering there can be some crap
        assertTrue(200 > delta);
    }

    @Test
    @Ignore
    public void testRun() throws IOException {
        ColorProcessor cp = new ColorProcessor(ImageIO.read(new File("src/test/resources/checkerboard.png")));
        new Susan().run(cp);
//        File out = new File("src/test/resources/out_susan.png");
//        out.deleteOnExit();
        // ImageIO.write(cp.getBufferedImage(), "PNG", out);
    }

}
