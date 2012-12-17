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
package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * Simple implementation of the Roberts(Cross)-Operator.
 *
 * The Roberts-Cross-Operator uses just 2x2 matrices instead of the alsmost
 * standard odd-sized kenrels.
 *
 * For sake of simplicity (and in order to conveniently use imageJ's convolve
 * method), the following 3x3 matrix is used which should produce the same
 * result as the 2x2 matrix (just a bit more inefficient):
 * <pre>
 * 0  0  0
 * 0  1  1
 * 0 -1 -1
 * </pre>
 *
 * @see http://en.wikipedia.org/wiki/Roberts_Cross
 */
public class Roberts implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final int[] kernel = new int[]{
        0, +0, +0,
        0, +1, +1,
        0, -1, -1
    };

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.DOES_16,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ip.convolve3x3(kernel);

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
