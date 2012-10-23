package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.LibProperties;
import de.lmu.dbs.jfeaturelib.features.sift.SiftWrapper;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This descriptor is a wrapper class for the famous SIFT algorithm proposed by
 * David Lowe: http://www.cs.ubc.ca/~lowe/keypoints/
 *
 * Related papers:<br> The most complete and up-to-date reference for the SIFT
 * feature detector is given in the following journal paper:<br> David G. Lowe,
 * "Distinctive image features from scale-invariant keypoints," International
 * Journal of Computer Vision, 60, 2 (2004), pp. 91-110.<br> The SIFT approach
 * to invariant keypoint detection was first described in the following ICCV
 * 1999 conference paper, which also gives some more information on the
 * applications to object recognition:<br> David G. Lowe, "Object recognition
 * from local scale-invariant features," International Conference on Computer
 * Vision, Corfu, Greece (September 1999), pp. 1150-1157.<br> The following
 * paper gives methods for performing 3D object recognition by interpolating
 * between 2D views. It also provides a probabilistic model for verification of
 * recognition.<br> David G. Lowe, "Local feature view clustering for 3D object
 * recognition," IEEE Conference on Computer Vision and Pattern Recognition,
 * Kauai, Hawaii (December 2001), pp. 682-688. [PDF];
 *
 * Patents:<br> Method and apparatus for identifying scale invariant features in
 * an image and use of same for locating an object in an image<br> David G.
 * Lowe, US Patent 6,711,293 (March 23, 2004). Provisional application filed
 * March 8, 1999. Asignee: The University of British Columbia.
 *
 * Keep in mind that the SIFT algorithm is patented. The SIFT binary is NOT
 * included in this lib in order to avoid violating the patent.
 *
 * @author graf
 */
public class Sift extends AbstractFeatureDescriptor {

    static final Logger log = Logger.getLogger(Sift.class.getName());
    private File siftBinary = null;

    /**
     * Initialize the Sift wrapper without a binary being set!
     */
    public Sift() {
    }

    /**
     * Initialize the Sift wrapper with the sift binary file. This file is
     * called by a java process that wraps the call and parses the output.
     *
     * @param siftBinary
     */
    public Sift(File siftBinary) throws IOException {
        if (!siftBinary.canExecute()) {
            throw new IOException("Cannot execute sift binary at: " + siftBinary.getAbsolutePath());
        }
        this.siftBinary = siftBinary;
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        siftBinary = LibProperties.get().getFile(LibProperties.SIFT_BINARY);
    }

    /**
     * The output feature vectors are a parsed version of the SIFT-binary
     * output. The features of each vector are assigned as follows: <ol> <li>
     * y-coordinate </li> <li> x-coordinate </li> <li> scale factor </li> <li>
     * rotation angle in radians</li> <li> 128 gradient values</li> </ol>
     *
     * @return list of sift features
     */
    @Override
    public List<double[]> getFeatures() {
        return super.getFeatures();
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        try {
            SiftWrapper siftWrapper = new SiftWrapper(siftBinary);
            addData(siftWrapper.getFeatures(ip));
        } catch (InterruptedException | IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    @Override
    public String getDescription() {
        return "Features extracted by the SIFT binary";
    }
}
