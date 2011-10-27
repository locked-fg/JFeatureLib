package de.lmu.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


public class KernelEdgeDetection implements FeatureDescriptor{

        private DescriptorChangeListener changeListener;
        private long time;
        private boolean calculated;
        private int progress; 
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
	
        public KernelEdgeDetection(){
            float[] kernel = {0, 0, 0, 0, 0, 0, 0, 0, 0};
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = kernel;
            calculated = false;
            progress = 0;
            
        }
        
        public KernelEdgeDetection(double[] kernel){
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = Arrays2.convertToFloat(kernel);
            calculated = false;
            progress = 0;
        }
        
	public KernelEdgeDetection(float[] kernel) {
            this.kernelWidth = Math.round((float)Math.sqrt(kernel.length+1.0f));
            this.kernelX = kernel;
            this.kernelY = new float[kernelWidth*kernelWidth];
            calculated = false;
            progress = 0;
	}
        
        public void setKernel(float[] kernel){
            this.kernelX = kernel;
            this.kernelY = new float[kernelWidth*kernelWidth];
        }
                
        public void setKernelDimension(int kernelWidth){
             this.kernelWidth = kernelWidth;
        }
        
       /*
        * Proceses the image applying the kernel in x- and y-direction
        */
        public void process() {
            result = new int[imageWidth*imageHeight];
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            
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
            float[][] kernelY2D = de.lmu.dbs.jfeaturelib.utils.RotateArrays.rotateFloatCW(kernelX2D);
            i = 0;
            for(int x = 0; x<kernelWidth; x++){
                for(int y = 0; y<kernelWidth; y++){
                    kernelY[i] = kernelY2D[x][y];
                    progress = (int)Math.round(i*(100.0/(double)(kernelWidth*kernelWidth)));
                    fireStateChanged();
                    i++;
                }                
            }

            image.convolve(kernelX, kernelWidth, kernelWidth);
            image.convolve(kernelY, kernelWidth, kernelWidth);
            
            result = (int[]) image.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, imageWidth, imageHeight, null);
            progress = 100;
            fireStateChanged();

	}

    /**
    * Returns the image edges as INT_ARGB array.
    * This can be used to create a buffered image, if the dimensions are known.
    */
    @Override
    public List<double[]> getFeatures() {
        if (calculated) {
            ArrayList<double[]> thisResult = new ArrayList<double[]>(1);
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
        long start = System.currentTimeMillis();
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        fireStateChanged();
        this.process();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }

    @Override
    public long getTime(){
         return time;
    }
    
    @Override
    public boolean isCalculated(){
        return calculated;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setArgs(double[] args) {
        if(args == null){
            
        }
        else if(args.length % 2 == 0 || args.length % 3 == 0 || args.length % 4 == 0){
            this.kernelWidth = Math.round((float)Math.sqrt(args.length+1.0f));
            this.kernelX = Arrays2.convertToFloat(args);
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Arguments array is not formatted correctly");
        }
    }
    
    @Override
    public void addChangeListener(DescriptorChangeListener l) {
        changeListener = l;
        l.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void fireStateChanged() {
        changeListener.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
    }
}