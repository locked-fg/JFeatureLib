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
    private ImagePlus imp;
    private int[] result;
    private double[] args;
    private long time;
        
            
    public ThreadWrapper(FeatureDescriptor descriptor, ImagePlus imp, int result[], double[] args){
        this.descriptor = descriptor;
        this.imp = imp;
        this.result = result;
        this.args = args;
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
    
    @Override
    protected void done(){
        try {
            result = Arrays2.convertToInt(get());
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ThreadWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
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
}
