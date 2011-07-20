package de.lmu.dbs.jfeaturelib.utils;

import ij.process.ImageProcessor;
import ij.process.FloatProcessor;
import javax.vecmath.Vector2f;

/**
 * Class that calculates gradients from a given image.
 * 
 * The image is convolved with sobel-masks in x and y in order
 * to create the derivates. Afterwards, the gradient orientation
 * and gradient length is computed for each pixel.
 * 
 * @author graf
 */
public class GradientImage {

    private final FloatProcessor length, theta;
    private final static int[] sobelX = {1, 0, -1, 2, 0, -2, 1, 0, -1};
    private final static int[] sobelY = {1, 2, 1, 0, 0, 0, -1, -2, -1};

    public GradientImage(ImageProcessor ip) {
        ip = ip.convertToFloat();
        ip.resetMinAndMax();
        length = new FloatProcessor(ip.getWidth(), ip.getHeight());
        theta = new FloatProcessor(ip.getWidth(), ip.getHeight());

        FloatProcessor ipX = (FloatProcessor) ip.duplicate();
        FloatProcessor ipY = (FloatProcessor) ip.duplicate();
        ipX.convolve3x3(sobelX);
        ipY.convolve3x3(sobelY);

        // create length and angle processors
        int max = ip.getWidth() * ip.getHeight();
        float dx, dy;
        for (int i = 0; i < max; i++) {
            // set length
            dx = ipX.getf(i);
            dy = ipY.getf(i);
            length.setf(i, (float) Math.sqrt(dx * dx + dy * dy));

            // compute angle (theta)
            if (dx != 0 && dy != 0) {
                double thetaValue = Math.atan(dy / dx);
                // make theta > 0
                thetaValue = Double.isNaN(thetaValue) ? 0 : thetaValue;
                thetaValue += thetaValue < 0 ? Math.PI : 0;
                theta.setf(i, (float) thetaValue);
            }
        }
        // is resetMinandMax really needed here?
        length.resetMinAndMax();
        theta.resetMinAndMax();
    }

    /**
     * Returns the angle of the gradient in the area -pi through pi
     * (see {@link Math#atan2(dy, dx)}). If you want the value between 0 and pi
     * use: gradient += gradient &lt; 0 ? Math.PI : 0;
     *
     * @param x x-cooridnate in pixel space
     * @param y y-cooridnate in pixel space
     * @return angle of gradient [0-2pi]
     */
    public float getTheta(int x, int y) {
        return theta.getf(x, y);
    }

    /**
     * return the length of the gradient vector at the specified position
     * 
     * @param x x-cooridnate in pixel space
     * @param y y-cooridnate in pixel space
     * @return length of gradient
     */
    public float getLength(int x, int y) {
        return length.getf(x, y);
    }

    /**
     * creates and returns the gradient vector at this position
     * 
     * @TODO do we need a Gradient class?
     * @param x
     * @param y
     * @return double[length, theta]
     */
    public double[] getGradient(int x, int y) {
        return new double[]{getLength(x, y), getTheta(x, y)};
    }

    /**
     * Returns the instance of the length-image.
     * Each pixel value represents the length of the gradient vector.
     * 
     * Keep in mind, that a reference of the imageProcessor is returned here!
     * You might want to call duplicate() if you want to alter the pixel data.
     * 
     * @return imageprocessor
     */
    public FloatProcessor getLength() {
        return length;
    }

    /**
     * Returns the instance of the theta-image.
     * Each pixel value represents the angle (Theta) of the gradient vector.
     * 
     * Keep in mind, that a reference of the imageProcessor is returned here!
     * You might want to call duplicate() if you want to alter the pixel data.
     * 
     * @return imageprocessor
     */
    public FloatProcessor getTheta() {
        return theta;
    }
}
