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
    @Deprecated
    public final String getDescription() {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final long getTime() {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final boolean isCalculated() {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final int getProgress() {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final void setArgs(double[] args) {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final void addChangeListener(DescriptorChangeListener listener) {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    @Deprecated
    public final void fireStateChanged() {
        throw new UnsupportedOperationException("Use of deprecated API");
    }

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
    }
}
