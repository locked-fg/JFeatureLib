package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.LibProperties;
import de.lmu.dbs.jfeaturelib.Progress;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * @author Franz
 */
public abstract class ConfigurableDescriptor implements Descriptor {

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    protected void startProgress() {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
    }

    protected void endProgress() {
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    public void setProperties(LibProperties properties) throws IOException {
    }
}
