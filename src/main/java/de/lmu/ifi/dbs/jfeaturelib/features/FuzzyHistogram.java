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
        firePropertyChange(Progress.START);
        
        net.semanticmetadata.lire.imageanalysis.FuzzyColorHistogram fuzzy = new net.semanticmetadata.lire.imageanalysis.FuzzyColorHistogram();
        fuzzy.extract(ip.getBufferedImage());
        addData(fuzzy.getDoubleHistogram());
        
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "Fuzzy Histogram";
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
