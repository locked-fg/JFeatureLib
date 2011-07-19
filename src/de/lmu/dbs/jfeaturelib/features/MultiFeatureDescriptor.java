package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Interface for a common descriptor that returns a <b>a set of</b> feature vectors.
 * 
 * Identifiers like pixel location, ID, etc can be encoded in the double array or 
 * in a separate object <b>T</b> which acts as the key of the feature vector map.
 * If no special keys are required, Integers should be used as keys.
 *
 * @author graf
 */
interface MultiFeatureDescriptor<T> extends Descriptor {

    HashMap<T, double[]> getAllFeatures();
}