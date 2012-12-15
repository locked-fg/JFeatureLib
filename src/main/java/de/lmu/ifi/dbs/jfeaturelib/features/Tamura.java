package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Implementation of (three) Tamura features done by Marko Keuschnig & Christian Penz
 *
 * This is a wrapper class for the corresponding lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.Tamura
 */
public class Tamura extends AbstractFeatureDescriptor {

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        net.semanticmetadata.lire.imageanalysis.Tamura tamura = new net.semanticmetadata.lire.imageanalysis.Tamura();
        tamura.extract(ip.getBufferedImage());
        addData(tamura.getDoubleHistogram());

        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "Tamura";
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
