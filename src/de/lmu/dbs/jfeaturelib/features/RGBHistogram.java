package de.lmu.dbs.jfeaturelib.features;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as double[]
 * @author Benedikt
 */
public class RGBHistogram implements FeatureDescriptor{

    int TONAL_VALUES = 256;
    int CHANNELS = 3;
    double[] features = new double[CHANNELS*TONAL_VALUES];
    private ColorProcessor image;
    
    public RGBHistogram(){
        //assuming 8bit image
    }
    
    //TODO: implement constructor for 16bit images
    
    @Override
    public double[] getFeatures() {
        
        return features;
    }

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

    public void run(ColorProcessor ip) {

        this.image = ip;
        process();
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
    
    @Override
    public String[] getInfo() {
        String[] info =  new String[CHANNELS*TONAL_VALUES];
        for (int i = 0; i < info.length; i++){
            if(i<TONAL_VALUES) info[i] = "Red Pixels with tonal value " + i;
            else if(i<TONAL_VALUES*2) info[i] = "Green Pixels with tonal value " + i%TONAL_VALUES;
            else info[i] = "Blue Pixels with tonal value " + i%TONAL_VALUES;
        }
        return(info);
    }
    
}
