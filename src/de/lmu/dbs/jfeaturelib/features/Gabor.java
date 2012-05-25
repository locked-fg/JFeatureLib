package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Implementation of a Gabor texture features done by Marko Keuschnig &
 * Christian Penz
 */
public class Gabor extends AbstractFeatureDescriptor {

    private final de.lmu.dbs.jfeaturelib.features.lire.Gabor gabor;

    public Gabor() {
        gabor = new de.lmu.dbs.jfeaturelib.features.lire.Gabor();
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
        gabor.extract(ip.getBufferedImage());
        addData(gabor.getData());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return gabor.getStringRepresentation();
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        //set.addAll(DOES_ALL);
        return set;
    }
}
