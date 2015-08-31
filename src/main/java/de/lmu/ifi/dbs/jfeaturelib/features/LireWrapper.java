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

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.semanticmetadata.lire.imageanalysis.LireFeature;

/**
 * Lire provides a great set of features. For those features that do not require
 * any parameterization, this wrapper can be used.
 *
 * @author Franz
 */
abstract class LireWrapper extends AbstractFeatureDescriptor {

    protected final net.semanticmetadata.lire.imageanalysis.LireFeature feature;
    protected final String name;

    protected LireWrapper(LireFeature feature, String name) {
        this.feature = feature;
        this.name = name;
    }

    protected LireWrapper(LireFeature feature) {
        this.feature = feature;
        String[] split = feature.getClass().getName().split("\\.");
        this.name = split[split.length - 2];
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        feature.extract(ip.getBufferedImage());
        addData(feature.getDoubleHistogram());

        firePropertyChange(Progress.END);
    }

    /**
     * @return the fcth instance from lire
     */
    public net.semanticmetadata.lire.imageanalysis.LireFeature getLireFeature() {
        return feature;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        return set;
    }
}
