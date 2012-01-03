package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.utils.RotateArrays;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


/**
 * Performs convolution with givne Kernel in X- and Y-direction on the image
 * @author Benedikt
 */
public class KernelEdgeDetection implements FeatureDescriptor{

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        private ByteProcessor image;
        private int imageWidth;
        private int imageHeight;
        private float[] kernelX;
        private float[] kernelY;
        private int kernelWidth;
        private int[] result;
	
	/**
	 * Constructs a new detector with standart Sobel kernel
	 */
	
        public KernelEdgeDetection(){
            float[] kernel = {1, 0, -1, 2, 0, -2, 1, 0, -1};
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = kernel;
            
        }
        
        /**
         * Constructs a new detector with given double[] kernel (not advised)
         * @param kernel double[] with kernel
         */
        public KernelEdgeDetection(double[] kernel){
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = Arrays2.convertToFloat(kernel);
        }
        
        /**
         * Constructs a new detector with given float[] kernel
         * @param kernel float[] with kernel
         */
        public KernelEdgeDetection(float[] kernel) {
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = kernel;
            this.kernelY = new float[kernelWidth*kernelWidth];
	}
        /**
         * Return the convolution kernel before transformation
         * @return float[] with kernel
         */
        public float[] getKernel(){
            return kernelX;
        }
                    
        /**
         * Sets the convolution kernel
         * @param kernel float[] with kernel
         */
        public void setKernel(float[] kernel){
            this.kernelX = kernel;
            this.kernelY = new float[kernelWidth*kernelWidth];
        }
                
        /**
         * Returns width of the kernel
         * @return int with width of kernel
         */
        public int getKernelDimension(){
            return kernelWidth;
        }
        
        /**
         * Setter for width of the kernel
         * @param kernelWidth int with width
         */
        public void setKernelDimension(int kernelWidth){
             this.kernelWidth = kernelWidth;
        }
        
       /**
        * Proceses the image applying the kernel in x- and y-direction
        */

        public void process() {
            result = new int[imageWidth*imageHeight];
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            pcs.firePropertyChange(Progress.getName(), null, new Progress(0, "initialized"));
            
            float[][] kernelX2D = new float[kernelWidth][kernelWidth];
            kernelY = new float[kernelWidth*kernelWidth];
            
            int i = 0;
            for(int x = 0; x<kernelWidth; x++){
                for(int y = 0; y<kernelWidth; y++){
                    kernelX2D[x][y] = kernelX[i];
                    //System.out.println("X kernel (" + x + "," + y + "):" + kernelX[i]);
                    i++;
                }
            }
            float[][] kernelY2D = RotateArrays.rotateFloatCW(kernelX2D);
            i = 0;
            for(int x = 0; x<kernelWidth; x++){
                for(int y = 0; y<kernelWidth; y++){
                    kernelY[i] = kernelY2D[x][y];
                    int progress = (int)Math.round(i*(100.0/(double)(kernelWidth*kernelWidth)));
                    pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + kernelWidth*kernelWidth));
                    i++;
                }                
            }

            image.convolve(kernelX, kernelWidth, kernelWidth);
            image.convolve(kernelY, kernelWidth, kernelWidth);
            
            result = (int[]) image.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, imageWidth, imageHeight, null);
        pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));

	}

   /**
    * Returns the image edges as INT_ARGB array.
    * This can be used to create a buffered image, if the dimensions are known.
    */
    @Override
    public List<double[]> getFeatures() {
        if (result != null) {
            ArrayList<double[]> thisResult = new ArrayList<>(1);
            thisResult.add(Arrays2.convertToDouble(result));
            return thisResult;
        } else {
            return Collections.EMPTY_LIST;
        }
    }  

    /**
     * Returns information about the getFeauture returns in a String array.
     */      
    @Override
    public String getDescription() {
        String info = "Each pixel value";
        return info;
    }

    /**
     * Defines the capability of the algorithm.
     * 
     * @see PlugInFilter
     * @see #supports() 
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
            Supports.NoChanges,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB,
            Supports.DOES_16
        );
        //set.addAll(DOES_ALL);
        return set;
    }

    /**
     * Starts the canny edge detection.
     * @param ip ImageProcessor of the source image
     */ 
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }
 
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}