package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.Progress;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {

    /**
     * Returns the values of the descriptor as a double array.
     * The semantics of the according values should be explained in the JavaDocs 
     * of the implementing class.
     * 
     * If features are not (yet) computed, an ({@link Collections#EMPTY_LIST})
     * should be returned.
     * 
     * @return list of feature vectors
     */
    @SuppressWarnings("deprecation")
    @Override
    List<double[]> getFeatures();

    /**
     * @FIXME change to String return only
     * 
     * @return 
     */
    String getDescription();

    /**
     * Returns the time for opening and processing an image.
     * 
     * @return Time for execution in milliseconds
     * @deprecated time measurements should be done by a delegate or wrapper class if needed. 10/27/2011
     */
    long getTime();

    /**
     * Checks wether this instance was calculated
     * 
     * @return Boolean if instance was calculated
     * @deprecated feature descriptors should be used ONCE only. If such checks are needed, use a wrapper class. 10/27/2011
     */
    boolean isCalculated();

    /**
     * Returns the progress of the calculation as int from 0 to 100
     * 
     * @return progress of the calculation int from 0 to 100
     * @deprecated usee observers instead and fire the state from inside. 10/27/2011
     */
    int getProgress();

    /**
     * Each descriptor must be able to take an empty constructor and pull args from a double array
     * 
     * @param args double array with arguments
     * @deprecated usage of reflection and BeanInfo Spec or Builder pattern is more appropriate in this case. 10/27/2011
     */
    void setArgs(double[] args);

    /**
     * @deprecated use {@link FeatureDescriptor#addChangeListener(PropertyChangeListener)} instead. 10/27/2011
     */
    public void addChangeListener(DescriptorChangeListener listener);
    
    /**
     * Adds a change listener to this descriptor. 
     * The descriptor might fire status change events which can be used to track 
     * the progress. Keep in mind that no implementation is forced to fire any 
     * events at all.
     * 
     * @param listener 
     */
    public void addChangeListener(PropertyChangeListener listener);

    @Deprecated
    /*
     * This should be handeled by one of the
     * PropertyChangeSupport.firePropertyChange(...)
     * methods.
     */
    public void fireStateChanged();

    /**
     * @deprecated use {@link PropertyChangeListener} instead. 10/27/2011
     */
    public interface DescriptorChangeListener extends EventListener {

        public void valueChanged(DescriptorChangeEvent e);
    }

    /**
     * @deprecated use {@link Progress} instead. 10/27/2011
     */
    public class DescriptorChangeEvent extends EventObject {

        public DescriptorChangeEvent(FeatureDescriptor source) {
            super(source);
        }

        public int getProgress() {
            return ((FeatureDescriptor) source).getProgress();
        }

        @Override
        public FeatureDescriptor getSource() {
            return (FeatureDescriptor) source;
        }
    }
}