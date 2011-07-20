/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lmu.dbs.jfeaturelib.features;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Implements Canny Edge Detection
 * @author Benedikt
 * Pseudocode from http://www.cis.rit.edu/class/simg782/lectures/lecture_15/lec782_05_15.pdf
 * FUNCTION canny,$
image,$ ;Input image array (required)
sigma,$ ;Smoothing filter spread (required)
tlow,$ ;Low threshold (required)
thigh,$ ;High threshold (required)
SMOOTHED_IMAGE=smoothedim,$ ;Smoothed image
GRAD_X=delta_x,$ ;X gradient
GRAD_Y=delta_y,$ ;Y gradient
GRAD_M=magnitude,$ ;Gradient magnitudes
GRAD_A=dir_radians,$ ;Gradient angles
NMS=nms ;Internal possible edge map
imsize=Size(image)
IF imsize[0] NE 2 THEN Message,’Image must be 2D,/Error’
rows=imsize[2] & cols=imsize[1]
;Perform Gaussian smoothing
gaussian_smooth,image, rows, cols, sigma, smoothedim
;Compute X and Y gradients
derivative_x_y,smoothedim, rows, cols, delta_x, delta_y
;Compute magnitude of gradient
magnitude=Sqrt(Float(delta_x)^2 + Float(delta_y)^2)
;Compute direction of gradient (for output)
radian_direction,delta_x, delta_y, dir_radians, -1, -1
;Perform non-maximal suppression
Non_max_supp,magnitude, delta_x, delta_y, rows, cols, nms
;Apply hysteresis to mark edge pixels
Apply_Hysteresis,magnitude, nms, rows, cols, tlow, thigh, edge
Return,BytScl(edge)
END
 */
public class Canny implements FeatureDescriptor{

    double[] features;
    private ByteProcessor image;
    
    public Canny(){
        
    }
    
    @Override
    public double[] getFeatures() {
        
        return features;
    }

    @Override
    public EnumSet<Supports> supports() {
        throw new UnsupportedOperationException("Not supported yet.");
        /*
        EnumSet set = EnumSet.of(
            Supports.NoChanges,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB
        );
        return set;*/
    }

    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        process();
    }
    
    private void process() {
       // TODO
    }

    @Override
    public String[] getInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
