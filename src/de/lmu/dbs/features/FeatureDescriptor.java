package de.lmu.dbs.features;

import de.lmu.dbs.Descriptor;

/**
 * Interface for a common descriptor that returns a <b>single</b> feature vector.
 * 
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {

    double[] getFeatures();
}