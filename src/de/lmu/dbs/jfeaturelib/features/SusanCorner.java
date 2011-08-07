package de.lmu.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as int[]
 * @author Benedikt
 */
public class SusanCorner implements FeatureDescriptor{

    long time;
    double[] features;

    private ColorProcessor image;
    
    /**
     * 
     */    
    public SusanCorner(){

    }
    
    
    /**
     * 
     */      
    @Override
    public double[] getFeatures() {
        
        return features;
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
        String[] info =  new String[2];

        return(info);
    }
    
    /**
     * 
     * @param ip ImageProcessor of the source image
     */    
    @Override
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis();
        this.image = (ColorProcessor)ip;
        process();
        time = (System.currentTimeMillis() - start);
    }
    
    private void process() {
        //TODO implement ;)
    }
    
     public long getTime(){
         return time;
     }
}
