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
