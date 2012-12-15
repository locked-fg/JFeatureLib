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
package de.lmu.ifi.dbs.jfeaturelib.features.sift;

/**
 * This class represents a single SIFT feature vector as it is obtained from the 
 * sift binary.
 * 
 * @author graf
 */
class SiftFeatureVector {

    private final double x, y, scale, rotation;
    private double[] gradients;

    public SiftFeatureVector(double x, double y, double scale, double rotation) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
    }

    void setGradients(double[] gradients) {
        this.gradients = gradients;
    }

    double[] asArray() {
        double[] out = new double[gradients.length + 4];
        out[0] = y;
        out[1] = x;
        out[2] = scale;
        out[3] = rotation;
        System.arraycopy(gradients, 0, out, 4, gradients.length);
        return out;
    }
}