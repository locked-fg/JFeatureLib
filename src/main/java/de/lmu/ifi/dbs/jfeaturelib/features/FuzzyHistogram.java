package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Fuzzy Color Histogram.
 */
public class FuzzyHistogram extends AbstractFeatureDescriptor {

    public FuzzyHistogram() {
    }

    @Override
    public void run(ImageProcessor ip) {
        de.lmu.ifi.dbs.jfeaturelib.features.lire.FuzzyColorHistogram fuzzy = new de.lmu.ifi.dbs.jfeaturelib.features.lire.FuzzyColorHistogram();

        firePropertyChange(Progress.START);
        fuzzy.extract(ip.getBufferedImage());
        addData(fuzzy.getData());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return new de.lmu.ifi.dbs.jfeaturelib.features.lire.FuzzyColorHistogram().getStringRepresentation();
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_RGB);
        return set;
    }
}
