package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.features.sift.SiftWrapper;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This descriptor is a wrapper class for the famous SIFT algorithm proposed by 
 * David Lowe: http://www.cs.ubc.ca/~lowe/keypoints/
 * 
 * Related papers:<br>
 * The most complete and up-to-date reference for the SIFT feature detector is given in the following journal paper:<br>
 *   David G. Lowe, "Distinctive image features from scale-invariant keypoints," International Journal of Computer Vision, 60, 2 (2004), pp. 91-110.<br>
 * The SIFT approach to invariant keypoint detection was first described in the following ICCV 1999 conference paper, which also gives some more information on the applications to object recognition:<br>
 *  David G. Lowe, "Object recognition from local scale-invariant features," International Conference on Computer Vision, Corfu, Greece (September 1999), pp. 1150-1157.<br> 
 * The following paper gives methods for performing 3D object recognition by interpolating between 2D views. It also provides a probabilistic model for verification of recognition.<br>
 *  David G. Lowe, "Local feature view clustering for 3D object recognition," IEEE Conference on Computer Vision and Pattern Recognition, Kauai, Hawaii (December 2001), pp. 682-688. [PDF]; 
 *
 * Patents:<br>
 * Method and apparatus for identifying scale invariant features in an image and use of same for locating an object in an image<br>
 *  David G. Lowe, US Patent 6,711,293 (March 23, 2004). Provisional application filed March 8, 1999. Asignee: The University of British Columbia. 
 * 
 * Keep in mind that the SIFT algorithm is patented. The SIFT binary is NOT 
 * included in this lib in order to avoid violating the patent.
 * 
 * @author graf
 */
public class Sift extends FeatureDescriptorAdapter {

    static final Logger log = Logger.getLogger(Sift.class.getName());
    private final File siftBinary;
    private List<double[]> features = Collections.EMPTY_LIST;

    /**
     * Initialize the Sift wrapper with the sift Binary file.
     * This file is called by a java process that wraps the call and parses 
     * the output.
     * 
     * @param siftBinary 
     */
    public Sift(File siftBinary) {
        this.siftBinary = siftBinary;
    }

    /**
     * The output feature vectors are a parsed version of the SIFT-binary output.
     * The features of each vector are assigned as follows:
     * <ol>
     *  <li> y-coordinate </li>
     *  <li> x-coordinate </li>
     *  <li> scale factor </li>
     *  <li> rotation angle in radians</li>
     *  <li> 128 gradient values</li>
     * </ol>
     * 
     * @return list of sift features
     */
    @Override
    public List<double[]> getFeatures() {
        return features;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        try {
            SiftWrapper siftWrapper = new SiftWrapper(siftBinary);
            features = siftWrapper.getFeatures(ip);
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
