package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {

    double[] getFeatures();
}