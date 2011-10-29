package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.SwingWorker;

/**
 * FIXME add Documentation
 * @author Benedikt
 */
public class ThreadWrapper extends SwingWorker<double[], Object> implements PropertyChangeListener{
    private FeatureDescriptor descriptor;
    private String descriptorName;
    private ImagePlus imp;
    private double[] args;
    private int number;
    private long time;
    private final String featurePackage = "de.lmu.dbs.jfeaturelib.features.";
        
    public ThreadWrapper(String descriptorName, ImagePlus imp, double[] args){
        this(descriptorName, imp, args, -1);
    }
    
        public ThreadWrapper(String descriptorName, ImagePlus imp, double[] args, int number){
        this.descriptorName = descriptorName;
        this.imp = imp;
        this.args = args;
        this.number = number;
        
        try{
            descriptor = (FeatureDescriptor) Class.forName(featurePackage+descriptorName).newInstance();
            //descriptor.setArgs(args);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected double[] doInBackground(){

        long start = System.currentTimeMillis();
        descriptor.addPropertyChangeListener(this);
        descriptor.run(new ColorProcessor(imp.getImage()));
        //FIXME there can be more than just asingle feature vector!
        time = (System.currentTimeMillis() - start);
        return descriptor.getFeatures().get(0);
    }
    

    /**
     * Returns the time for opening and processing an image.
     * 
     * @return Time for execution in milliseconds
     */ 
     public long getTime(){
         return time;
     }

     public String getDescriptorName(){
         return descriptorName;
     }
     
     public int getNumber(){
         return number;
     }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getNewValue().getClass().getSimpleName().equals("Progress")){
            System.out.println(((Progress)evt.getNewValue()).getProgress());
            setProgress(((Progress)evt.getNewValue()).getProgress());
                    
        }
    }
}
