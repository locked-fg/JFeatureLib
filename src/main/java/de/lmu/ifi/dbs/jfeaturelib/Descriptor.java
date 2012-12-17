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
package de.lmu.ifi.dbs.jfeaturelib;

import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;

/**
 * @author graf
 */
public interface Descriptor {

    /**
     * Defines the capability of the algorithm. It is possible that the list
     * will be extended to the complete list of ImageJ's PlugInFilter Flags
     *
     * @see PlugInFilter
     * @see #supports()
     */
    public enum Supports {

        /**
         * If a mask is set, the features will only be extracted from this area
         */
        Masking,
        /**
         * Set this, if the ImageProcessor will not be changed.
         */
        NoChanges,
        /**
         * Supports 8bit Grayscale
         */
        DOES_8G,
        /**
         * Supports 8bit indexed color
         */
        DOES_8C,
        /**
         * supports 16-bit images
         */
        DOES_16,
        /**
         * supports float images
         */
        DOES_32,
        /**
         * supports RGB images
         */
        DOES_RGB
    }
    /**
     * Convenience field for "support all kinds of images".
     *
     * USE WITH EXTREME CAUTION: This may cause problems if a current algorithm
     * uses DOES_ALL and new types for SUPPORT are added.
     */
    public final EnumSet<Supports> DOES_ALL = EnumSet.of(
            Supports.DOES_32,
            Supports.DOES_16,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB);

    /**
     * Determine the capabilities of this algorithm.
     *
     * @return
     */
    public EnumSet<Supports> supports();

    /**
     * Start processing of this algorithm on the given image processor. Keep in
     * mind that if the imageProcessor is changed, Supports.NoChanges must not
     * be set.
     *
     * This is also the case even if only ImageProcessor.snapshot() is used.
     *
     * @param ip
     */
    public void run(ImageProcessor ip);

    /**
     * Adds a change listener to this descriptor. The descriptor might fire
     * status change events which can be used to track the progress. Keep in
     * mind that no implementation is forced to fire any events at all.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
}
