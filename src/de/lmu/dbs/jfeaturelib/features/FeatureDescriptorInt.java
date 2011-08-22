package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptorInt extends Descriptor {


    /**
     * Returns the values of the descriptor in an int array.
     * The semantics of the according values should be explained in the JavaDocs 
     * of the implementing class.
     * 
     * @return int array of feature values
     */
    @Override
    int[] getFeatures();
    

}