package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * Threadwrapper extends Swingworker and thus is used to instantiate a feature descriptor and calculate its results for a given image.
 * There is no limit on how many Threadwrappers may work at a time, and using it will prevent the GUI from freezing.
 * A PropertyChangeListener can be used to track the status of the computation, the values range from 0 to 100.
 * 
 * @author Benedikt
 */
public class ThreadWrapper extends SwingWorker<List<double[]>, Object> implements PropertyChangeListener{
    private FeatureDescriptor descriptor;
    private Class descriptorClass;
    private File file;
    private ImagePlus imp;
    private int number;
    private long time;
        
    /**
     * Constructs a new ThreadWrapper
     * @param descriptorClass Class of the descriptor to be applied
     * @param imp Image on which the descriptor should work
     */
    public ThreadWrapper(Class descriptorClass, File file){
        this(descriptorClass, file, -1);
        instantiate();
    }
    
    /**
     * Constructs a new ThreadWrapper with explicit ID
     * @param descriptorClass Class of the descriptor to be applied
     * @param imp Image on which the descriptor should work
     * @param number Explicit ID for identifying parallel ThreadWrappers
     */
    public ThreadWrapper(Class descriptorClass, File file, int number){
        this.descriptorClass = descriptorClass;
        this.file = file;
        this.number = number;
        instantiate();
    }
    
    private void instantiate(){
        try{
            Opener opener = new Opener();
            opener.getFileType(file.getAbsolutePath());
            imp = opener.openImage(file.getAbsolutePath());
            descriptor = (FeatureDescriptor) descriptorClass.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e){
            //FIXME Fix this or little kittens will die!
            System.out.println("Error during instantiation");
            e.printStackTrace();
        }
        
    }

    @Override
    protected List<double[]> doInBackground(){
        long start = System.currentTimeMillis();
        descriptor.addPropertyChangeListener(this);
        descriptor.run(new ColorProcessor(imp.getImage()));
        //FIXME there can be more than just asingle feature vector!
        time = (System.currentTimeMillis() - start);
        imp = null;
        return descriptor.getFeatures();
    }
    

    /**
     * Returns the time for opening and processing an image.
     * 
     * @return Time for execution in milliseconds
     */ 
     public long getTime(){
         return time;
     }

     /**
      * 
      * @return Name of descriptor contained in this ThreadWrapper
      */
     public String getDescriptorName(){
         return descriptorClass.getSimpleName();
     }
     
     /**
      * 
      * @return Class of descriptor contained in this ThreadWrapper
      */
     public Class getDescriptorClass(){
         return descriptorClass;
     }
     
     /**
      * 
      * @return Number of this ThreadWrapper, used for parallel computation
      */
     public int getNumber(){
         return number;
     }
     
     /**
      * 
      * @return Image file which is processed
      */     
     
     public File getFile(){
         return file;
     }     
     
     /**
      * 
      * @return The actual Descriptor contained in this ThreadWrapper
      */
     public FeatureDescriptor getInstance(){
         return descriptor;
     }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getNewValue().getClass().getSimpleName().equals("Progress")){
            //System.out.println(((Progress)evt.getNewValue()).getProgress());
            setProgress(((Progress)evt.getNewValue()).getProgress());
                    
        }
    }
}
