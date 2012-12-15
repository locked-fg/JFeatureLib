/**
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib;

import de.lmu.ifi.dbs.jfeaturelib.features.FeatureDescriptor;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * Threadwrapper extends Swingworker and thus is used to instantiate a feature descriptor and calculate its results for
 * a given image. There is no limit on how many Threadwrappers may work at a time, and using it will prevent the GUI
 * from freezing. A PropertyChangeListener can be used to track the status of the computation, the values range from 0
 * to 100.
 *
 * @author Benedikt
 * @deprecated since after v1.0.0
 */
@Deprecated
public class ThreadWrapper extends SwingWorker<List<double[]>, Object>
        implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ThreadWrapper.class.getName());
    private final Class descriptorClass;
    private final File file;
    private final int number;
    private FeatureDescriptor descriptor;
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

        instantiate();
    }

    private void instantiate() {
        try {
            descriptor = (FeatureDescriptor) descriptorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            String msg = "Error during instantiation of " + descriptorClass.getSimpleName();
            logger.warn(msg);
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

        return descriptor.getFeatures();
    }

    /**
     * Returns the time for opening and processing an image.
     *
     *
     * @return Time for execution in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the name of the descriptor which is used
     *
     * @return Name of descriptor contained in this ThreadWrapper
     */
    public String getDescriptorName() {
        return descriptorClass.getSimpleName();
    }

    /**
     * Returns the class of the descriptor
     *
     * @return Class of descriptor contained in this ThreadWrapper
     */
    public Class getDescriptorClass() {
        return descriptorClass;
    }

    /**
     * Returns the number of this worker
     *
     * @return Number of this ThreadWrapper, used for parallel computation
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns image file which is processed
     *
     * @return Image file which is processed
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the instance of the descriptor
     *
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
