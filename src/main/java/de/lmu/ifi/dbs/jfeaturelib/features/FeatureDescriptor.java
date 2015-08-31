/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 */
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