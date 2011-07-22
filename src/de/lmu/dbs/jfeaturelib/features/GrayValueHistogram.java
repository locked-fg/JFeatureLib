package de.lmu.dbs.jfeaturelib.features;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as int[].      
 * @author Benedikt
 */
public class GrayValueHistogram implements FeatureDescriptor{

    int TONAL_VALUES = 256;
    int[] features = new int[TONAL_VALUES];
    private ByteProcessor image;

    /**
     * Constructs a histogram with default parameters.
     */     
    public GrayValueHistogram(){
        //assuming 8bit image
    }
    
    //TODO: implement constructor for 16bit images
    
    /**
    * Returns the histogram as int array.
    */    
    @Override
    public int[] getFeaturesInt() {
        return features;
    }

    /**
     * Returns the histogram as double array.
     * This is NOT recommended!
     */  
    @Override
    public double[] getFeaturesDouble() {
        double[] featuresDouble = new double[features.length];
        for (int i = 0; i <  featuresDouble.length; i++){
            featuresDouble[i] = (double)features[i];
        }
        return  featuresDouble;
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
            Supports.DOES_RGB
        );
        //set.addAll(DOES_ALL);
        return set;
    }
    
    /**
     * Starts the histogram detection.
     * @param ip ImageProcessor of the source image
     */ 
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        process();
    }
    
    /**
     * Returns information about the getFeauture returns in a String array.
     */     
    @Override
    public String[] getDescription() {
        String[] info =  new String[TONAL_VALUES];
        for (int i = 0; i < info.length; i++){
            info[i] = "Pixels with tonal value " + i;
        }
        return(info);
    }
    
    private void process() {
        features = image.getHistogram();
    }
}
