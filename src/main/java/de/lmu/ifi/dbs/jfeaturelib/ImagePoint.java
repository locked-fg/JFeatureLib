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

/*
 * A pixel representing a location in (x, y) coordinate plane, 
 * specified in double precision.
 * 
 * While images are usually only represented as ints, several algorithms use 
 * sub pixel locations, so that doubles are more useful.
 */
public class ImagePoint {

    /**
     * The x-coordinate of the according point.
     */
    public double x;
    /**
     * The x-coordinate of the according point.
     */
    public double y;

    public ImagePoint() {
    }

    /**
     * constructs and initializes a point at the specified location in the
     * coordinate plane
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public ImagePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
