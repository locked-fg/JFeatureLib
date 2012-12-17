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
package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;

/**
 * Euclidean distance (L2)
 * 
 * <code>d = Math.sqrt(x^2 + y^2)</code>
 * 
 * @author graf
 */
public class DistanceL2 implements Distance {

    @Override
    public double distance(ImagePoint a, ImagePoint b) {
        double x1 = a.getX() - b.getX();
        double y1 = a.getY() - b.getY();
        return Math.sqrt(x1*x1 + y1*y1);
    }

    @Override
    public double distance(int x1, int y1, int x2, int y2) {
        x1-=x2;
        y1-=y2;
        return Math.sqrt(x1*x1 + y1*y1);
    }
}
