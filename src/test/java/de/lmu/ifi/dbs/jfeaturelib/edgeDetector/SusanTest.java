package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.edgeDetector.Susan;
import ij.process.ColorProcessor;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class SusanTest {
    
    @Test
    public void testRun() throws IOException {
        System.out.println(new File(".").getAbsolutePath());
        ColorProcessor cp = new ColorProcessor(ImageIO.read(new File("src/test/resources/checkerboard.png")));
        new Susan().run(cp);
        File out = new File("src/test/resources/out_susan.png");
        ImageIO.write(cp.getBufferedImage(), "PNG", out);
    }

}
