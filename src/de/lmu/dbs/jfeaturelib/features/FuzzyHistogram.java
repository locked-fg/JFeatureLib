package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Fuzzy Color Histogram.
 */

public class FuzzyHistogram implements FeatureDescriptor {
    protected double[] data = null;
    private de.lmu.dbs.jfeaturelib.features.lire.FuzzyColorHistogram fuzzy;
    private BufferedImage bi;    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public FuzzyHistogram(){
        fuzzy = new de.lmu.dbs.jfeaturelib.features.lire.FuzzyColorHistogram();
        
    }
    
    @Override
    public void run(ImageProcessor ip) {
        bi = ip.getBufferedImage();
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        fuzzy.extract(bi);
        data = fuzzy.getData();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }
    
    @Override
    public List<double[]> getFeatures() {
        if (data != null) {
            ArrayList<double[]> result = new ArrayList<>(1);
            result.add(data);
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getDescription() {
        return fuzzy.getStringRepresentation();
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public EnumSet<Supports> supports() {        
        EnumSet set = EnumSet.of(
            Supports.NoChanges,
            Supports.DOES_8C,
            Supports.DOES_RGB
        );
        //set.addAll(DOES_ALL);
        return set;
    }
}
