package de.lmu.dbs.jfeaturelib.edgeDetector;

import ij.process.ColorProcessor;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class SusanTest {
    
    @Test
    public void testRun() throws IOException {
        ColorProcessor cp = new ColorProcessor(ImageIO.read(new File("test/checkerboard.png")));
        new Susan().run(cp);
        File out = new File("test/out_susan.png");
        ImageIO.write(cp.getBufferedImage(), "PNG", out);
    }

}
