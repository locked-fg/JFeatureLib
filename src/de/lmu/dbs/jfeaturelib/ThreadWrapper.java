package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.process.ColorProcessor;
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
    private Object[] args;
    private long time;
        
            
    public ThreadWrapper(FeatureDescriptor descriptor, ImagePlus imp, Object[] args){
        this.descriptor = descriptor;
        this.imp = imp;
        this.args = args;
    }
      
    @Override
    protected double[] doInBackground(){
        //For evaluation
        try {
            Thread.sleep(5000);
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
}
