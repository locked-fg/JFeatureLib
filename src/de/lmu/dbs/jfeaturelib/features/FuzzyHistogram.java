package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Fuzzy Color Histogram.
 */
public class FuzzyHistogram extends AbstractFeatureDescriptor {

    private final de.lmu.dbs.jfeaturelib.features.lire.FuzzyColorHistogram fuzzy;

    public FuzzyHistogram() {
        fuzzy = new de.lmu.dbs.jfeaturelib.features.lire.FuzzyColorHistogram();
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
        fuzzy.extract(ip.getBufferedImage());
        addData(fuzzy.getData());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return fuzzy.getStringRepresentation();
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_RGB);
        //set.addAll(DOES_ALL);
        return set;
    }
}
