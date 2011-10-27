package de.lmu.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Reads the histogram from the Image Processor and returns it as double[]
 * @author Benedikt
 */
public class SusanCorner implements FeatureDescriptor{

    private DescriptorChangeListener changeListener;
    private long time;
    private boolean calculated; 
    private int progress;
    double[] features;

    private ColorProcessor image;
    
    /**
     * 
     */    
    public SusanCorner(){
        calculated = false;
        progress = 0;
    }
    
    
    @Override
    public List<double[]> getFeatures() {
        if(calculated){
            ArrayList<double[]> result = new ArrayList<double[]>(1);
            result.add(features);
            return result;
        }
        else{
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
     * 
     * @param ip ImageProcessor of the source image
     */    
    @Override
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis();
        this.image = (ColorProcessor)ip;
        fireStateChanged();
        process();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }
    
    private void process() {
        //TODO implement ;)
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

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
