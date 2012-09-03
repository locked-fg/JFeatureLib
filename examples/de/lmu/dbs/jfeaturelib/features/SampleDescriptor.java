package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.logging.Logger;

/**
 * This is a sample descriptor
 *
 * @author Franz
 */
public class SampleDescriptor extends AbstractFeatureDescriptor {

    static final Logger log = Logger.getLogger(SampleDescriptor.class.getName());

    @Override
    public String getDescription() {
        return "A sample descript that just returns some arbitrary numbers";
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        // if you want to extract multiple features for one image just add as much 
        // feature vectors as you want
        double[] featureVector = {22d, 03d, 1980d};
        addData(featureVector);

        firePropertyChange(Progress.END);
    }
}
