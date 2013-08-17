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

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * Class that calculates gradients from a given image.
 *
 * The image is convolved with masks (default = sobel) in x and y in order to
 * create the derivates. Afterwards, the gradient orientation and length is
 * computed for each pixel.
 *
 * If the task is just to create a derivation of the image
 * {@link DerivativeImage} might be more useful.
 *
 * @author graf
 * @see DerivativeImage
 */
public class GradientImage implements GradientSource {

    /**
     * The kernel mask used for derivation in x-direction
     */
    private int[] kernelX = {1, 0, -1, 2, 0, -2, 1, 0, -1};
    /**
     * The kernel mask used for derivation in y-direction
     */
    private int[] kernelY = {1, 2, 1, 0, 0, 0, -1, -2, -1};
    /**
     * The processor describing the length of the gradients.
     */
    private FloatProcessor length = null;
    /**
     * The processor describing the orientation of the gradients in [0, Pi[.
     */
    private FloatProcessor theta = null;

    @Override
    public void setIp(ImageProcessor ip) {
        ip = ip.convertToFloat();

        /*
         * ipX and ipY are temporary processors for the derivations. This of
         * course consumes quite some memory. A more optimal yet less
         * transparent solution would be to use the length and theta processors
         * for derivation AND for the final result at once.
         */
        FloatProcessor ipX = (FloatProcessor) ip.duplicate();
        FloatProcessor ipY = (FloatProcessor) ip.duplicate();
        ipX.convolve3x3(kernelX);
        ipY.convolve3x3(kernelY);

        length = new FloatProcessor(ip.getWidth(), ip.getHeight());
        theta = new FloatProcessor(ip.getWidth(), ip.getHeight());

        final int max = ip.getWidth() * ip.getHeight();
        float dx, dy;
        float thetaValue;
        for (int i = 0; i < max; i++) {
            dx = ipX.getf(i);
            dy = ipY.getf(i);

            if (dx != 0 || dy != 0) {
                length.setf(i, (float) Math.sqrt(dx * dx + dy * dy));
                thetaValue = (float) (Math.atan2(dy, dx) + Math.PI);
                thetaValue %= Math.PI;
                theta.setf(i, thetaValue);
            }
        }
    }

    /**
     * Returns the angle of the gradient in the area [0, PI[.
     *
     * @param x x-cooridnate in pixel space
     * @param y y-cooridnate in pixel space
     * @return angle of gradient [0,pi[
     */
    @Override
    public double getTheta(int x, int y) {
        return theta.getf(x, y);
    }

    /**
     * return the length of the gradient vector at the specified position
     *
     * @param x x-cooridnate in pixel space
     * @param y y-cooridnate in pixel space
     * @return length of gradient
     */
    @Override
    public double getLength(int x, int y) {
        return length.getf(x, y);
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    public int[] getKernelX() {
        return kernelX;
    }

    public void setKernelX(int[] kernelX) {
        this.kernelX = kernelX;
    }

    public int[] getKernelY() {
        return kernelY;
    }

    public void setKernelY(int[] kernelY) {
        this.kernelY = kernelY;
    }
    //</editor-fold>
}
