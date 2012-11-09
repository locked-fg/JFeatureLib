package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.ifi.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.jfeaturelib.features.Haralick;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Franz
 */
public class PackageScannerTest {

    @Test
    public void testGetJarName() throws Exception {
        Package pack = AbstractFeatureDescriptor.class.getPackage();
        PackageScanner<FeatureDescriptor> ps = new PackageScanner();
        List<Class<FeatureDescriptor>> classes = ps.scanForClass(pack, FeatureDescriptor.class);
        assertTrue(classes.contains(Haralick.class));
    }
}
