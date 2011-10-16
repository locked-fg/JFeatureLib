package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;
import java.beans.PropertyChangeListener;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {


    /**
     * Returns the values of the descriptor in an int array.
     * The semantics of the according values should be explained in the JavaDocs 
     * of the implementing class.
     * 
     * @return int array of feature values
     */
    @Override
    double[] getFeatures();

        /**
     * @FIXME add javadoc
     * 
     * @return 
     */
    String[] getDescription();
    
   /**
     * Returns the time for opening and processing an image.
     * 
     * @return Time for execution in milliseconds
     */
    long getTime();
    

    
    /**
     * Checks wether this instance was calculated
     * 
     * @return Boolean if instance was calculated
     */
    boolean isCalculated();
    
    /**
     * Returns the progress of the calculation as int from 0 to 100
     * 
     * @return progress of the calculation int from 0 to 100
     */
    int getProgress();
    
    /**
     * Each descriptor must be able to take an empty constructor and pull args from a double array
     * 
     * @param args double array with arguments
     */
    void setArgs(double[] args);
}