package de.lmu.dbs.jfeaturelib;

import de.lmu.ifi.dbs.utilities.PropertyContainer;
import java.io.File;
import java.io.IOException;

/**
 * The property wrapper class for
 * <code>jfeaturelib.properties</code>. This class is used to access the
 * properties of jfeaturelib.properties in an easier way without having to
 * request keys by magic strings.
 *
 * For example in order to obtain the value of
 * <code>sift.binary</code>, you can request
 * <code>LibProperties.get().getFile(LibProperties.SIFT_BINARY)</code>.
 *
 * @author graf
 * @see PropertyContainer
 */
public class LibProperties extends PropertyContainer {

    private static LibProperties singleton;
    /**
     * The file name of the properties file.
     */
    private static final File BASE_FILE = new File("jfeaturelib.properties");
    /**
     * Path to the sift binary which can be obtained from the SIFT homepage.
     *
     * {@link http://www.cs.ubc.ca/~lowe/keypoints/}
     */
    public static final String SIFT_BINARY = "features.sift.binary";
    public static final String IMAGE_FORMATS = "imageFormats";
    public static final String HISTOGRAMS_TYPE = "features.histogram.type";
    public static final String HISTOGRAMS_BINS = "features.histogram.bins";
    // Phog
    public static final String PHOG_CANNY = "features.phog.canny";
    public static final String PHOG_BINS = "features.phog.bins";
    public static final String PHOG_RECURSIONS = "features.phog.recursions";

    // shapes
    public static final String POLYGON_EVOLUTION = "shapefeatures.polygonevolution.iterations";
    // Edges
    // canny
    public static final String CANNY_LOW_THRESHOLD = "edge.canny.lowThreshold";
    public static final String CANNY_HIGH_THRESHOLD = "edge.canny.highThreshold";
    public static final String CANNY_KERNEL_RADIUS = "edge.canny.gaussianKernelRadius";
    public static final String CANNY_KERNEL_WIDTH = "edge.canny.gaussianKernelWidth";
    public static final String CANNY_NORMALIZE_CONTRAST = "edge.canny.contrastNormalized";

    /**
     * Constructor that initializes the properties container with the file
     * defined in BASE_FILE.
     *
     * @see #BASE_FILE
     * @throws IOException
     */
    LibProperties() throws IOException {
        super(BASE_FILE);
    }

    /**
     * Factory method for the properties container.
     *
     * @return an instance of the container
     * @throws IOException
     */
    public static LibProperties get() throws IOException {
        if (singleton == null) {
            singleton = new LibProperties();
        }
        return singleton;
    }
}