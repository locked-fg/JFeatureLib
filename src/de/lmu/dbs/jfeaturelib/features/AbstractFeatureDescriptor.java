package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class provides some convenient base functionalities for feature
 * descriptors like the getter for the data (including null check) and the
 * propery change support.
 *
 * @author Franz
 */
public abstract class AbstractFeatureDescriptor implements FeatureDescriptor {

    /**
     * holds the most recent progress event that was fired using the
     * {@link #firePropertyChange(de.lmu.dbs.jfeaturelib.Progress)} method.
     */
    private Progress previous = null;
    /**
     * Property change support that can be used by the implementing class to
     * inform listeners about updates.
     */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**
     * The data arrays which hold the calculated features. Most of the
     * descriptors will only return a single array. Yet there are some
     * descriptors like Sift which return a bag of features.
     *
     * @see Sift
     */
    private List<double[]> data = new ArrayList<>(1);

    /**
     * Returns a reference to the data calculated by the according descriptor.
     *
     * The list will most likely just containa single double array holding the
     * computed values. In cases where a descriptor computes mutliple features
     * (for example SIFT, where a vector is calculated for each point of
     * interest), the list will contain several double arrays.
     *
     * @return list of feature vectors.
     */
    @Override
    public List<double[]> getFeatures() {
        return data;
    }

    /**
     * Adds a Property change listener for this feature vector.
     *
     * During the computation of the descriptor, at least 2 progress events
     * should be fired (Start/End).
     *
     * @see Progress#START
     * @see Progress#END
     * @see Progress
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Adds the double array to the list. Keep in mind that the array is NOT
     * copied but directly put into the list.
     *
     * @see #data
     */
    protected void addData(double[] data) {
        this.data.add(data);
    }

    /**
     * Converts the given int array to double[] and adds this array to the list.
     *
     * @see #data
     */
    protected void addData(int[] data) {
        this.data.add(Arrays2.convertToDouble(data));
    }

    /**
     * Adds the double array list to the list of data arrays. Keep in mind that
     * the array is NOT copied but directly put into the list.
     *
     * @see #data
     */
    protected void addData(List<double[]> data) {
        this.data.addAll(data);
    }

    /**
     * Propagates the given progress event using property change support.
     *
     * The old value of the firePropertyChange is the most recent progress event
     * that was propagated by this method (null in case of the first event).
     *
     * @param event
     */
    protected void firePropertyChange(Progress event) {
        pcs.firePropertyChange(Progress.getName(), previous, event);
        previous = event;
    }
}
