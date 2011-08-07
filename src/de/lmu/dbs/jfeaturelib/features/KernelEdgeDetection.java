package de.lmu.dbs.jfeaturelib.features;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;


public class KernelEdgeDetection implements FeatureDescriptorInt{

        private long time;
        private ByteProcessor image;
        private int imageWidth;
        private int imageHeight;
        private float[] kernelX;
        private float[] kernelY;
        private int kernelWidth;
        private double direction;
        private int[] result;
	
        // constructors
	
	/**
	 * Constructs a new detector.
         * @param kernel The kernel for x-convolution
         * @param kernelWidth Dimension of the kernel matrix
	 */
	
	public KernelEdgeDetection(float[] kernel, int kernelWidth) {
                this.kernelX = kernel;
                this.kernelWidth = kernelWidth;
                kernelY = new float[kernelWidth*kernelWidth];
	}
        
       /*
        * Proceses the image applying the kernel in x- and y-direction
        */
        public void process() {
            result = new int[imageWidth*imageHeight];
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            
            float[][] kernelX2D = new float[kernelWidth][kernelWidth];
            int i = 0;
            for(int x = 0; x<kernelWidth; x++){
                for(int y = 0; y<kernelWidth; y++){
                    kernelX2D[x][y] = kernelX[i];
                    //System.out.println("X kernel (" + x + "," + y + "):" + kernelX[i]);
                    i++;
                }
            }
            float[][] kernelY2D = de.lmu.dbs.jfeaturelib.utils.RotateArrays.rotateFloatCW(kernelX2D);
            i = 0;
            for(int x = 0; x<kernelWidth; x++){
                for(int y = 0; y<kernelWidth; y++){
                    kernelY[i] = kernelY2D[x][y];
                    //System.out.println("Y kernel (" + x + "," + y + "):" + kernelY[i]);
                    i++;
                }                
            }

            image.convolve(kernelX, kernelWidth, kernelWidth);
            image.convolve(kernelY, kernelWidth, kernelWidth);
            
            result = (int[]) image.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, imageWidth, imageHeight, null);
	}

    /**
    * Returns the image edges as INT_ARGB array.
    * This can be used to create a buffered image, if the dimensions are known.
    */
    @Override
    public int[] getFeatures() {
        return result;
    }  

    /**
     * Returns information about the getFeauture returns in a String array.
     */      
    @Override
    public String[] getDescription() {
        String[] info = new String[1];
        info[0] = "Each pixel value";
        info[1] = "Each pixel value";
        info[2] = "Each pixel value";
        info[3] = "Each pixel value";
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
        long start = System.currentTimeMillis();
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        this.process();
        time = (System.currentTimeMillis() - start);
    }

    public long getTime(){
         return time;
    }
}