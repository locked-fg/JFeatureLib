package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author Benedikt
 */
public class ThreadWrapper extends SwingWorker<double[], Object> {
    private FeatureDescriptor descriptor;
    private String descriptorName;
    private ImagePlus imp;
    private double[] args;
    private long time;
    private final String featurePackage = "de.lmu.dbs.jfeaturelib.features.";
        
    public ThreadWrapper(String descriptorName, ImagePlus imp, double[] args){
        this.descriptorName = descriptorName;this.imp = imp;
        this.args = args;
        
        try{
            descriptor = (FeatureDescriptor) Class.forName(featurePackage+descriptorName).newInstance();
            descriptor.setArgs(args);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected double[] doInBackground(){
        //For evaluation
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } 
        descriptor.run(new ColorProcessor(imp.getImage()));
        time = descriptor.getTime();
        return descriptor.getFeatures();
    }
    
    /**
     * Returns the time for opening and processing an image.
     * 
     * @return 
     */ 
     public long getTime(){
         return time;
     }

     public int getCurrent(){
         System.out.println(descriptor.getProgress());
         return descriptor.getProgress();
     }
     
     public String getDescriptorName(){
         return descriptorName;
     }
}
