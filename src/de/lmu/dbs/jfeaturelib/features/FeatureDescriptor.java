package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;
import java.beans.PropertyChangeListener;
import java.util.Collections;
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
    List<double[]> getFeatures();

    /**
     * Returns a short plain Text description about the Descriptor and the 
     * semantics of the double array obtained from {@link #getFeatures()}.
     * 
     * May return null if no description is implemented - even though this 
     * should be avoided by any means.
     * 
     * @return semantic description of getFeatures or null.
     */
    String getDescription();

    /**
     * Each descriptor must be able to take an empty constructor and pull args from a double array
     * 
     * @param args double array with arguments
     * @deprecated usage of reflection and BeanInfo Spec or Builder pattern is more appropriate in this case. 10/27/2011
     */
    void setArgs(double[] args);
    
    /**
     * Adds a change listener to this descriptor. 
     * The descriptor might fire status change events which can be used to track 
     * the progress. Keep in mind that no implementation is forced to fire any 
     * events at all.
     * 
     * @param listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

}