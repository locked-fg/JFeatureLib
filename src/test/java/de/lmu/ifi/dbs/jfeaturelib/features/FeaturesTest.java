package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.utils.PackageScanner;
import ij.process.ColorProcessor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author graf
 */
public class FeaturesTest {

    @Test
    public void testInit() {
        try {
            Package fdPackage = FeatureDescriptor.class.getPackage();
            // search for the descriptors
            PackageScanner<FeatureDescriptor> scanner = new PackageScanner<>();
            List<Class<FeatureDescriptor>> classes = scanner.scanForClass(fdPackage, FeatureDescriptor.class);

            // now we know the longest descriptor name - build the output string
            for (Class<FeatureDescriptor> fd : classes) {
                fd.newInstance();
            }
        } catch (Exception ex) {
            Logger.getLogger(FeaturesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
    }
}
