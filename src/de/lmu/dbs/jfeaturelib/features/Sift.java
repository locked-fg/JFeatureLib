package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.features.sift.SiftWrapper;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sift extends FeatureDescriptorAdapter {

    static final Logger log = Logger.getLogger(Sift.class.getName());
    private final File siftBinary;
    private List<double[]> features = Collections.EMPTY_LIST;

    public Sift(File siftBinary) {
        this.siftBinary = siftBinary;
    }

    @Override
    public List<double[]> getFeatures() {
        return features;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        try {
            SiftWrapper siftWrapper = new SiftWrapper(siftBinary);
            features = siftWrapper.getFeatures(ip);
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
