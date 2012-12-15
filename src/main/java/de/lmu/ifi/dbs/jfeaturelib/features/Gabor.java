package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Implementation of a Gabor texture features
 *
 * @author Marko Keuschnig & Christian Penz
 */
public class Gabor extends AbstractFeatureDescriptor {

    public Gabor() {
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
        net.semanticmetadata.lire.imageanalysis.Gabor gabor = new net.semanticmetadata.lire.imageanalysis.Gabor();
        gabor.extract(ip.getBufferedImage());
        addData(gabor.getDoubleHistogram());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "Gabor";
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        return set;
    }
}
