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

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.io.IOException;
import net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram.Mode;
import org.apache.log4j.Logger;

/**
 * VisualDescriptor for the AutoCorrelogram based on color as described in Huang, J.; Kumar, S. R.; Mitra, M.; Zhu, W. &
 * Zabih, R. (2007) "Image Indexing Using Color Correlograms", IEEE Computer Society see also DOI
 * 10.1109/CVPR.1997.609412
 *
 * This is a wrapper class for the appropriate lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram
 *
 * @author Franz
 * @since 1.2.0
 */
public class AutoColorCorrelogram extends AbstractFeatureDescriptor {

    private static final Logger log = Logger.getLogger(AutoColorCorrelogram.class);
    /**
     * distance d > 0
     */
    int distance = 3;

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        distance = properties.getInteger(LibProperties.AUTOCOLORCORRELOGRAM_DISTANCE, 3);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram acc = 
                new net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram(distance, Mode.SuperFast);
        acc.extract(ip.getBufferedImage());
        addData(acc.getDoubleHistogram());

        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "AutoColorCorrelogram";
    }
}
