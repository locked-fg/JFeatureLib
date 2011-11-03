package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Reads the histogram from the Image Processor and returns it as double[].      
 * @author Benedikt
 */
public class GrayHistogram implements FeatureDescriptor{
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int tonalValues;
    private int[] features;
    private ByteProcessor image;

    /**
     * Constructs a histogram with default parameters.
     */     
    public GrayHistogram(){
        //assuming 8bit image
        tonalValues = 256;
        pcs.firePropertyChange(Progress.getName(), null, new Progress(0, "getting started"));
    }
    
    /**
     * Constructs a histogram.
     * @param values Number of tonal values, i.e. 256 for 8bit jpeg
     */  
    
    public GrayHistogram(int values){
        tonalValues = values;
    }
    
    /**
     * Setter for tonal Values (defaults to 256)
     * @param tonalValues Number of tonal values, i.e. 256 for 8bit jpeg
     */
    public void setTonalValues(int tonalValues){
        this.tonalValues = tonalValues;
    }
    
   /**
     * Getter for tonal Values
     * @return tonalValues Number of tonal values, i.e. 256 for 8bit jpeg
     */    
    public int getTonalValues(){
        return tonalValues;
    }
    
    /**
    * Returns the histogram as int array.
    */    
    @Override
    public List<double[]> getFeatures() {
        if (features != null) {
            ArrayList<double[]> result = new ArrayList<>(1);
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
        features = new int[tonalValues];
        this.image = (ByteProcessor) ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
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
        pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
