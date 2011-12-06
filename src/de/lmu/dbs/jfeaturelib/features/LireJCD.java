package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.lire.JCD;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * A joint descriptor joining CEDD and FCTH in one histogram by Savvas A. Chatzichristofis.
 */

public class LireJCD implements FeatureDescriptor {
    protected double[] data = null;
    private JCD jcd;
    private BufferedImage bi;    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public LireJCD(){
        jcd = new JCD();
        
    }
    
    @Override
    public void run(ImageProcessor ip) {
        bi = ip.getBufferedImage();
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        jcd.extract(bi);
        data = jcd.getData();
        pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));
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
        return jcd.getStringRepresentation();
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
            Supports.DOES_8G,
            Supports.DOES_RGB
        );
        //set.addAll(DOES_ALL);
        return set;
    }
}
