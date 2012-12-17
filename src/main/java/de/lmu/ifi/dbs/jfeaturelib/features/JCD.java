/*
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
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
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;

/**
 * A joint descriptor joining CEDD and FCTH in one histogram by Savvas A. Chatzichristofis.
 * 
 * This is a wrapper class for the corresponding lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.JCD
 */
public class JCD extends AbstractFeatureDescriptor {

    private CEDD cedd = new CEDD();
    private FCTH fcth = new FCTH();

    public JCD() {
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        cedd.setProperties(properties);
        fcth.setProperties(properties);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        fcth.run(ip);
        firePropertyChange(new Progress(33));

        cedd.run(ip);
        firePropertyChange(new Progress(66));

        net.semanticmetadata.lire.imageanalysis.JCD jcd = new net.semanticmetadata.lire.imageanalysis.JCD(cedd.getCedd(), fcth.getFcth());
        jcd.extract(ip.getBufferedImage());

        addData(jcd.getDoubleHistogram());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "JCD";
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
