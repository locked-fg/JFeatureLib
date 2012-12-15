package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * @author Franz
 */
public abstract class AbstractDescriptor implements Descriptor {

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
