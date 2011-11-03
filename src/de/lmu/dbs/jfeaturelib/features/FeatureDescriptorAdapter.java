package de.lmu.dbs.jfeaturelib.features;

import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

/**
 * Adapter class to avoid having to implement methods that you just don't want 
 * to use or to hide deprectaed methods.
 * 
 * @author graf
 */
public abstract class FeatureDescriptorAdapter implements FeatureDescriptor {

    private static final Logger log = Logger.getLogger(FeatureDescriptorAdapter.class.getName());

    @Override
    public String getDescription() {
        log.fine("getDescription() not implemented in ".getClass().getName());
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        log.fine("Property Change support not implemented in this Descriptor");
    }
}
