package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
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
public class SusanCorner implements FeatureDescriptor{

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    double[] features;

    private ColorProcessor image;
    
    /**
     * 
     */    
    public SusanCorner(){
    }
    
    
    @Override
    public List<double[]> getFeatures() {
        if(features != null){
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
        this.image = (ColorProcessor)ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }
    
    private void process() {
        //TODO implement ;)
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
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
