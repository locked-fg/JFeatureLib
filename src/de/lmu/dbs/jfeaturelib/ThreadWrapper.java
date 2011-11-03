package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 * FIXME add Documentation
 * @author Benedikt
 */
public class ThreadWrapper extends SwingWorker<double[], Object> implements PropertyChangeListener{
    private FeatureDescriptor descriptor;
    private Class descriptorClass;
    private ImagePlus imp;
    private double[] args;
    private int number;
    private long time;
        
    public ThreadWrapper(Class descriptorClass, ImagePlus imp, double[] args){
        this(descriptorClass, imp, args, -1);
    }
    
        public ThreadWrapper(Class descriptorClass, ImagePlus imp, double[] args, int number){
        this.descriptorClass = descriptorClass;
        this.imp = imp;
        this.args = args;
        this.number = number;
        
        try{
            descriptor = (FeatureDescriptor) descriptorClass.newInstance();
            //descriptor.setArgs(args);
        }
        catch(InstantiationException | IllegalAccessException e){
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
         return descriptorClass.getName();
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
