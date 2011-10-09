package de.lmu.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as int[]
 * @author Benedikt
 */
public class RGBHistogram implements FeatureDescriptorInt{

    private long time;
    private boolean calculated;    
    private int TONAL_VALUES;
    private int CHANNELS;
    private int[] features;
    private ColorProcessor image;
    
    /**
     * Constructs a RGB histogram with default parameters (8bit per channel).
     */    
    public RGBHistogram(){
        //assuming 8bit RGB image
        TONAL_VALUES = 256;
        CHANNELS = 3;
        features = new int[CHANNELS*TONAL_VALUES];
        calculated = false;
    }
    
    /**
     * Constructs a RGB histogram.
     * @param values Number of tonal values, i.e. 256 for 8bit jpeg
     */ 
    public RGBHistogram(int values){
        //assuming 8bit RGB image
        TONAL_VALUES = values;
        CHANNELS = 3;
        features = new int[CHANNELS*TONAL_VALUES];
        calculated = false;
    }
    
    /**
     * Returns the RGB histogram as int array.
     */      
    @Override
    public int[] getFeatures() {
        if(calculated){
            return features;
        }
        else{
            //TODO throw exception
            return new int[]{0};
        }
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
     * Returns information about the getFeauture returns in a String array.
     */ 
    @Override
    public String[] getDescription() {
        String[] info =  new String[CHANNELS*TONAL_VALUES];
        for (int i = 0; i < info.length; i++){
            if(i<TONAL_VALUES) info[i] = "Red Pixels with tonal value " + i;
            else if(i<TONAL_VALUES*2) info[i] = "Green Pixels with tonal value " + i%TONAL_VALUES;
            else info[i] = "Blue Pixels with tonal value " + i%TONAL_VALUES;
        }
        return(info);
    }
    
    /**
     * Starts the RGB histogram detection.
     * @param ip ImageProcessor of the source image
     */    
    @Override
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis();
        this.image = (ColorProcessor)ip;
        process();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }
    
    private void process() {
        
        int[] r,g,b;
        ColorProcessor.setWeightingFactors(1,0,0);
        r = image.getHistogram();
        ColorProcessor.setWeightingFactors(0,1,0);
        g = image.getHistogram();
        ColorProcessor.setWeightingFactors(0,0,1);
        b = image.getHistogram();
        
        for(int i = 0; i < features.length; i++){
            if(i<TONAL_VALUES)features[i] = r[i];
            else if(i<TONAL_VALUES*2)features[i] = g[i%TONAL_VALUES];
            else features[i] = b[i%TONAL_VALUES];
        }
    }
    
     public long getTime(){
         return time;
     }
     
    public boolean isCalculated(){
        return calculated;
    }     
}
