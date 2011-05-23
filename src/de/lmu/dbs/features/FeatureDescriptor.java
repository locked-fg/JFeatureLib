package de.lmu.dbs.features;

import de.lmu.dbs.Descriptor;

/**
 *
 * @author graf
 */
interface FeatureDescriptor extends Descriptor {

    double[] getFeatures();
}