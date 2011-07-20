package de.lmu.dbs.jfeaturelib.features;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as double[]      
 * @author Benedikt
 */
public class GrayValueHistogram implements FeatureDescriptor{

    int TONAL_VALUES = 256;
    double[] features = new double[TONAL_VALUES];
    private ByteProcessor image;
    
    public GrayValueHistogram(){
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

    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        process();
    }
    
    private void process() {
        int[] result = image.getHistogram();
        for (int i = 0; i < result.length; i++){
            features[i] = (double)result[i];
        }
    }

    @Override
    public String[] getInfo() {
        String[] info =  new String[TONAL_VALUES];
        for (int i = 0; i < info.length; i++){
            info[i] = "Pixels with tonal value " + i;
        }
        return(info);
    }
    
}
