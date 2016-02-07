package de.lmu.ifi.dbs.jfeaturelib.features.sift;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class SiftWrapperTest {

    @Test
    public void testExtractFeatures() throws Exception {
        URI uri = getClass().getClassLoader().getResource("siftOutput.txt").toURI();
        String content = new String(Files.readAllBytes(Paths.get(uri)));
        
        List<SiftFeatureVector> features = new SiftWrapper().extractFeatures(content);
        assertEquals(2, features.size());
        
        double[] first = features.get(0).asArray();
        assertEquals(138.42, first[0], 0.001);
        assertEquals(44.74, first[1], 0.001);
    }

}
