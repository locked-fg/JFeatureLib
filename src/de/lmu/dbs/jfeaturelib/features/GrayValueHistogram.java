package de.lmu.dbs.jfeaturelib.features;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as double[]
 * @TODO: Class for RGB histograms
 * @author Benedikt
 */
public class GrayValueHistogram implements FeatureDescriptor{

    double[] features = new double[256];
    private ByteProcessor image;
    
    public GrayValueHistogram(){
        
    }
    
    @Override
    public double[] getFeatures() {
        
        return features;
    }

    @Override
    public EnumSet<Supports> supports() {        
        EnumSet set = EnumSet.of(Supports.NoChanges);
        set.addAll(DOES_ALL);
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
    
}
