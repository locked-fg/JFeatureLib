package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * Threadwrapper extends Swingworker and thus is used to instantiate a feature
 * descriptor and calculate its results for a given image. There is no limit on
 * how many Threadwrappers may work at a time, and using it will prevent the GUI
 * from freezing. A PropertyChangeListener can be used to track the status of
 * the computation, the values range from 0 to 100.
 *
 * @author Benedikt
 */
public class ThreadWrapper extends SwingWorker<List<double[]>, Object>
        implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ThreadWrapper.class.getName());
    private FeatureDescriptor descriptor;
    private Class descriptorClass;
    private File file;
    private int number;
    private long time;

    /**
     * Constructs a new ThreadWrapper
     *
     * @param descriptorClass Class of the descriptor to be applied
     * @param imp Image on which the descriptor should work
     */
    public ThreadWrapper(Class descriptorClass, File file) {
        this(descriptorClass, file, -1);
    }

    /**
     * Constructs a new ThreadWrapper with explicit ID
     *
     * @param descriptorClass Class of the descriptor to be applied
     * @param imp Image on which the descriptor should work
     * @param number Explicit ID for identifying parallel ThreadWrappers
     */
    public ThreadWrapper(Class descriptorClass, File file, int number) {
        this.descriptorClass = descriptorClass;
        this.file = file;
        this.number = number;
        logger.setLevel(Level.FINEST);

        instantiate();
    }

    private void instantiate() {
        try {
            descriptor = (FeatureDescriptor) descriptorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            String msg = "Error during instantiation of " + descriptorClass.getSimpleName();
            logger.severe(msg);
            throw new IllegalStateException(msg);
        }

    }

    @Override
    protected List<double[]> doInBackground() {
        long start = System.currentTimeMillis();
        Opener opener = new Opener();
        opener.getFileType(file.getAbsolutePath());
        ImagePlus imp = opener.openImage(file.getAbsolutePath());
        descriptor.addPropertyChangeListener(this);
        descriptor.run(new ColorProcessor(imp.getImage()));
        time = (System.currentTimeMillis() - start);
        imp = null;
        return descriptor.getFeatures();
    }

    /**
     * Returns the time for opening and processing an image.     *
     * @return Time for execution in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the name of the descriptor which is used
     * @return Name of descriptor contained in this ThreadWrapper
     */
    public String getDescriptorName() {
        return descriptorClass.getSimpleName();
    }

    /**
     * Returns the class of the descriptor
     * @return Class of descriptor contained in this ThreadWrapper
     */
    public Class getDescriptorClass() {
        return descriptorClass;
    }

    /**
     * Returns the number of this worker
     * @return Number of this ThreadWrapper, used for parallel computation
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns image file which is processed
     * @return Image file which is processed
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the instance of the descriptor
     * @return The actual descriptor contained in this ThreadWrapper
     */
    public FeatureDescriptor getInstance() {
        return descriptor;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue().getClass().getSimpleName().equals("Progress")) {
            setProgress(((Progress) evt.getNewValue()).getProgress());

        }
    }
}
