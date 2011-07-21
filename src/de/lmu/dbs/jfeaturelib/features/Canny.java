/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lmu.dbs.jfeaturelib.features;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;
import javax.swing.ImageIcon;

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
    private ColorProcessor image;
    double low = 1.0;
    double high = 2.0;
    int[] sigmas = {1,2,1,2,4,2,1,2,1};
    int[] xSobel = {1,0,-1,2,0,-2,1,0,-1};
    int[] ySobel = {1,2,1,0,0,0,-1,-2,-1};
    int PREVIEW_RESOLUTION = 256;
    
    public Canny(){
        
    }
    
    public Canny(double lowT, double highT, int[] sigma){
        low = lowT;
        high = highT;
        sigmas = sigma;
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
//        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
//            ip = ip.convertToByte(true);
//        }
//        this.image = (ByteProcessor) ip;
        this.image = (ColorProcessor)ip;
        //process();
    }
    
    private void process() {
       //Smoothen image using sigma
        // TODO
    }
    
    public ImageIcon process1(){
        image.convolve3x3(sigmas);
        
        ImagePlus im1 = new ImagePlus("Thumb", image.resize(PREVIEW_RESOLUTION, PREVIEW_RESOLUTION));
        
        return new ImageIcon(im1.getImage());
    }
    
    public ImageIcon process2(){
        ByteProcessor imagex = (ByteProcessor)image.convertToByte(true);
        ByteProcessor imagey = (ByteProcessor)image.convertToByte(true);
        imagex.convolve3x3(xSobel);
        imagey.convolve3x3(ySobel);
        
        int g, xval, yval;
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                xval = imagex.getPixel(x,y);
                yval = imagey.getPixel(x,y);
                
                g = (int)java.lang.Math.sqrt(java.lang.Math.pow(xval,2)+java.lang.Math.pow(yval,2));
                System.out.println("x: " + x + ", xval: " + xval + ", y: " + y + ", yval: " + yval + ", g: " + g + ", arctan: " + java.lang.Math.atan((double)xval/(double)yval));
                if(imagex.getPixel(x,y) != 0){
                    double arctan = java.lang.Math.atan((double)xval/(double)yval);
                    image.putPixel(x,y,(int)arctan*162);
                }
                else if(xval == 0 && yval == 0 ){
                    //0 degree
                    image.putPixel(x,y,0);
                }
                else if(xval == 0 && yval != 0 ){
                    //90 degree
                    image.putPixel(x,y,255);
                }
            }
        }
        ImagePlus im2 = new ImagePlus("Thumb", image.resize(PREVIEW_RESOLUTION, PREVIEW_RESOLUTION));
        
        return new ImageIcon(im2.getImage());
    }


    @Override
    public int[] getFeaturesInt() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getFeaturesDouble() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
