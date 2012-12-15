/**
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
package de.lmu.ifi.dbs.jfeaturelib.utils;

import ij.process.ImageProcessor;

/**
 * Defines a class from which image gradients can be obtained.
 * 
 * Implementations could be simply sobel-filtered images or canny edge processed
 * images
 * 
 * @author graf
 */
public interface GradientSource {
    
    /**
     * Initialize the GradientSource with this Processor
     * @param ip 
     */
    void setIp(ImageProcessor ip);
    
    /**
     * Returns the length of the gradient at pixel location x,y.
     * 
     * @param x
     * @param y
     * @return Math.sqrt(dx * dx + dy * dy)
     */
    double getLength(int x, int y);
    
    /**
     * @param x
     * @param y
     * @return Math.atan(dy / dx) or 0
     */
    double getTheta(int x, int y);
}
