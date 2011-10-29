package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Reads the histogram from the Image Processor and returns it as double[]
 * @author Benedikt
 */
public class RGBHistogram implements FeatureDescriptor{

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int tonalValues;
    private final int CHANNELS;
    private int[] features;
    private ColorProcessor image;
    
    /**
     * Constructs a RGB histogram with default parameters (8bit per channel).
     */    
    public RGBHistogram(){
        //assuming 8bit RGB image
        tonalValues = 256;
        CHANNELS = 3;
        features = new int[CHANNELS*tonalValues];
    }
    
    /**
     * Constructs a RGB histogram.
     * @param values Number of tonal values, i.e. 256 for 8bit jpeg
     */ 
    public RGBHistogram(int values){
        //assuming 8bit RGB image
        tonalValues = values;
        CHANNELS = 3;
        features = new int[CHANNELS*tonalValues];
    }
    
    /**
     * Returns the RGB histogram as int array.
     */      
    @Override
    public List<double[]> getFeatures() {
        if (features != null) {
            List<double[]> result = new ArrayList<double[]>(1);
            result.add(Arrays2.convertToDouble(features));
            return result;
        } else {
            return Collections.EMPTY_LIST;
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
    public String getDescription() {
        String info =  "RGB Histogram tonal values ";
        return(info);
    }
    
    /**
     * Starts the RGB histogram detection.
     * @param ip ImageProcessor of the source image
     */    
    @Override
    public void run(ImageProcessor ip) {
        this.image = (ColorProcessor)ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
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
            if(i<tonalValues)features[i] = r[i];
            else if(i<tonalValues*2)features[i] = g[i%tonalValues];
            else features[i] = b[i%tonalValues];
            int progress = (int)Math.round(i*(100.0/features.length));
            pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + features.length));
        }
    }

    @Override
    public void setArgs(double[] args) {
        if(args == null){
            this.tonalValues = 256;
        }
        else if(args.length == 1){
            this.tonalValues = Integer.valueOf((int)args[0]);
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Arguments array is not formatted correctly");
        }
        
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
