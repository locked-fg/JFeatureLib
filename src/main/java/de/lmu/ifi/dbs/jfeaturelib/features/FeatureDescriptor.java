package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import java.io.IOException;
import java.util.List;

/**
 * Interface for a common descriptor that returns a either a single feature
 * vector or a set / list of feature vectors.
 *
 * @author graf
 */
public interface FeatureDescriptor extends Descriptor {

    /**
     * Returns the values of the descriptor as a double array. The semantics of
     * the according values should be explained in the JavaDocs of the
     * implementing class.
     *
     * If features are not (yet) computed, an empty listis returned.
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
     * Injects the properties class that can be used to retrieve several
     * properties.
     * 
     * @throws IOException if something went wrong 
     */
    void setProperties(LibProperties properties) throws IOException;
}