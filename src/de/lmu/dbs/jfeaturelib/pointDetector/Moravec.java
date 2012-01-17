package de.lmu.dbs.jfeaturelib.pointDetector;

import de.lmu.dbs.jfeaturelib.ImagePoint;
import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;
import java.util.List;

/**
 * FIXME Documentation
 * @author Robert Zelhofer
 * http://www.ri.cmu.edu/pub_files/pub4/moravec_hans_1980_1/moravec_hans_1980_1.pdf
 */
public class Moravec implements PointDetector {
    
    private List<ImagePoint> corners;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);;

    @Override
    public List<ImagePoint> getPoints() {
        return corners;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        
        //FIXME implementation
        
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
}
