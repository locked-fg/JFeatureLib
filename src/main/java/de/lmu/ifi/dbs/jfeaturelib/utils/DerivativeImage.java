package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ImageProcessor;

/**
 * Calculates the standard derivatives Dx and Dy of an image using a -1,0,1 
 * kernel and stores the results in separate ImageProcessors.
 * 
 * The kernel can be modified by the according setter methods.
 * 
 * Gradient lenghts and orientations can be obtained easily by using 
 * {@link GradientImage}.
 * 
 * @author graf
 * @see GradientImage
 */
public class DerivativeImage {

    /**
     * The processor describing the derivative in X-direction.
     */
    private ImageProcessor ipX;
    /**
     * The processor describing the derivative in X-direction.
     */
    private ImageProcessor ipY;
    /**
     * The kernel mask used for derivation in x-direction
     */
    private float[] kernelX = {-1, 0, 1};
    /**
     * The kernel mask used for derivation in y-direction
     */
    private float[] kernelY = {-1, 0, 1};

    public DerivativeImage() {
    }

    public DerivativeImage(ImageProcessor ip) {
        setIp(ip);
    }

    public void setIp(ImageProcessor ip) {
        ipX = ip.convertToFloat();
        ipY = ipX.duplicate();

        ipX.convolve(kernelX, 3, 1);
        ipY.convolve(kernelY, 1, 3);
    }

    public String toString() {
        return "DerivativeImage{" + "kernelX=" + Arrays2.join(kernelX, " ") + ", kernelY=" + Arrays2.join(kernelY," ") + '}';
    }
    
    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public float[] getKernelX() {
        return kernelX;
    }

    /**
     * Sets a new kernel for the derivation in x direction.
     * Keep in mind that just setting the kernel does NOT recompute the result.
     * 
     * @param kernelX 
     */
    public void setKernelX(float[] kernelX) {
        this.kernelX = kernelX;
    }

    /**
     * Sets a new kernel for the derivation in y direction.
     * Keep in mind that just setting the kernel does NOT recompute the result.
     * 
     * @param kernelY 
     */
    public float[] getKernelY() {
        return kernelY;
    }

    public void setKernelY(float[] kernelY) {
        this.kernelY = kernelY;
    }

    
    /**
     * @return reference to Dx
     */
    public ImageProcessor getIpX() {
        return ipX;
    }

    /**
     * @return reference to Dy
     */
    public ImageProcessor getIpY() {
        return ipY;
    }
    //</editor-fold>

}
