package de.lmu.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Reads the histogram from the Image Processor and returns it as double[].      
 * @author Benedikt
 */
public class GrayHistogram implements FeatureDescriptor{

    private DescriptorChangeListener changeListener;
    private boolean calculated;
    private int progress; 
    private int tonalValues;
    private int[] features;
    private ByteProcessor image;

    /**
     * Constructs a histogram with default parameters.
     */     
    public GrayHistogram(){
        //assuming 8bit image
        tonalValues = 256;
        features = new int[tonalValues];
        calculated = false;
        progress = 0;
    }
    
    /**
     * Constructs a histogram.
     * @param values Number of tonal values, i.e. 256 for 8bit jpeg
     */  
    
    public GrayHistogram(int values){
        tonalValues = values;
        features = new int[tonalValues];
    }
        
    /**
    * Returns the histogram as int array.
    */    
    @Override
    public List<double[]> getFeatures() {
        if (calculated) {
            ArrayList<double[]> result = new ArrayList<double[]>(1);
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
     * Starts the histogram detection.
     * @param ip ImageProcessor of the source image
     */ 
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        fireStateChanged();
        process();
        calculated = true;
    }
    
    /**
     * Returns information about the getFeauture returns in a String array.
     */     
    @Override
    public String getDescription() {
        String info =  "Pixels with tonal value ";
        return(info);
    }
    
    private void process() {
        features = image.getHistogram();
        progress = 100;
        fireStateChanged();
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
    public void addChangeListener(DescriptorChangeListener l) {
        changeListener = l;
        l.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void fireStateChanged() {
        changeListener.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }
}
