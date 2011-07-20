package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {

    /**
     * Returns the values of the descriptor in a double array.
     * The semantics of the according values should be explained in the JavaDocs 
     * of the implementing class.
     * 
     * @return array of feature values
     */
    double[] getFeatures();
    
    /**
     * @FIXME add javadoc
     * 
     * @return 
     */
    String[] getInfo();
}