package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.features.Histogram;
import ij.process.ColorProcessor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.imageio.ImageIO;

public class MyRGBThreadTest {

    MyRGBThreadTest() {

        int numberOfThreads = 25;

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        try {
                            ColorProcessor processor = new ColorProcessor(ImageIO
                                    .read(new File(MyRGBThreadTest.class
                                    .getResource("/test.jpg").toURI())));

                            Histogram histogram = new Histogram();
                            histogram.type = Histogram.TYPE.RGB;
                            histogram.bins = 128;
                            histogram.run(processor);
                            List<double[]> features = histogram.getFeatures();
                            // do nothing
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        new MyRGBThreadTest();
    }
}
