/*
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

import de.lmu.ifi.dbs.utilities.properties.PropertyContainer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * The property wrapper class for
 * <code>jfeaturelib.properties</code>. This class is used to access the properties of jfeaturelib.properties in an
 * easier way without having to request keys by magic strings.
 *
 * For example in order to obtain the value of
 * <code>sift.binary</code>, you can request
 * <code>LibProperties.get().getFile(LibProperties.SIFT_BINARY)</code>.
 *
 * @author graf
 * @see PropertyContainer
 */
public class LibProperties extends PropertyContainer {

    private static final Logger log = Logger.getLogger(LibProperties.class);
    private static LibProperties singleton;
    /**
     * The file name of the properties file.
     */
    public static final File BASE_FILE = new File("jfeaturelib.properties");
    public static final String IMAGE_FORMATS = "imageFormats";
    // ###########################################
    // Features
    /**
     * Path to the sift binary which can be obtained from the SIFT homepage.
     *
     * See http://www.cs.ubc.ca/~lowe/keypoints/ for more information.
     */
    public static final String SIFT_BINARY = "features.sift.binary";
    public static final String HISTOGRAMS_TYPE = "features.histogram.type";
    public static final String HISTOGRAMS_BINS = "features.histogram.bins";
    // reference color similarity
    public static final String REFERENCE_COLOR_SIMILARITY_INIT = "features.referenceColorSimilarity.init";
    public static final String REFERENCE_COLOR_SIMILARITY_H = "features.referenceColorSimilarity.h";
    public static final String REFERENCE_COLOR_SIMILARITY_S = "features.referenceColorSimilarity.s";
    public static final String REFERENCE_COLOR_SIMILARITY_B = "features.referenceColorSimilarity.b";
    public static final String REFERENCE_COLOR_SIMILARITY_G = "features.referenceColorSimilarity.g";
    public static final String REFERENCE_COLOR_SIMILARITY_COLORS = "features.referenceColorSimilarity.colors";
    // color histograms
    public static final String COLOR_HISTOGRAMS_TYPE = "features.colorhistograms.type";
    public static final String COLOR_HISTOGRAMS_BINS_X = "features.colorhistograms.bins.x";
    public static final String COLOR_HISTOGRAMS_BINS_Y = "features.colorhistograms.bins.y";
    public static final String COLOR_HISTOGRAMS_BINS_Z = "features.colorhistograms.bins.z";
    // Phog
    public static final String PHOG_CANNY = "features.phog.canny";
    public static final String PHOG_BINS = "features.phog.bins";
    public static final String PHOG_RECURSIONS = "features.phog.recursions";
    // Haralick
    public static final String HARALICK_DISTANCE = "features.haralick.distance";
    // CEDD
    public static final String CEDD_T0 = "features.cedd.t0";
    public static final String CEDD_T1 = "features.cedd.t1";
    public static final String CEDD_T2 = "features.cedd.t2";
    public static final String CEDD_T3 = "features.cedd.t3";
    public static final String CEDD_COMPACT = "features.cedd.compact";
    // Auto Color Correlogram
    public static final String AUTOCOLORCORRELOGRAM_DISTANCE = "features.autoColorCorrelogram.distance";
    // Thumbnail 
    public static final String THUMBNAIL_RESIZE = "features.thumbnail.resize";
    public static final String THUMBNAIL_WIDTH = "features.thumbnail.width";
    public static final String THUMBNAIL_HEIGHT = "features.thumbnail.height";
    // ###########################################
    // Shapes
    public static final String POLYGON_EVOLUTION = "shapefeatures.polygonevolution.iterations";
    public static final String SMSM_BG_COLOR = "shapefeatures.SquareModelShapeMatrix.background";
    public static final String SMSM_DIMENSIONS = "shapefeatures.SquareModelShapeMatrix.dimensions";
    // ###########################################
    // Edges
    // canny
    public static final String CANNY_LOW_THRESHOLD = "edge.canny.lowThreshold";
    public static final String CANNY_HIGH_THRESHOLD = "edge.canny.highThreshold";
    public static final String CANNY_KERNEL_RADIUS = "edge.canny.gaussianKernelRadius";
    public static final String CANNY_KERNEL_WIDTH = "edge.canny.gaussianKernelWidth";
    public static final String CANNY_NORMALIZE_CONTRAST = "edge.canny.contrastNormalized";

    /**
     * Initializes an empty properties set. You should know what you are doing as the needed Properties will not be
     * present. So all required properties have to be set before it is passed to an extractor/descriptor.
     */
    public LibProperties() {
        super();
    }

    /**
     * Constructor that initializes the properties container with the given file.
     *
     * @param file
     * @throws IOException
     */
    public LibProperties(File file) throws IOException {
        super(file);
    }

    /**
     * Constructor that initializes the properties container with the given input stream.
     *
     * @param file
     * @throws IOException
     */
    public LibProperties(InputStream is) throws IOException {
        super(is);
    }

    /**
     * Factory method for the properties container.
     *
     * @return an instance of the container
     * @throws IOException
     */
    public static LibProperties get() throws IOException {
        if (singleton == null) {
            if (BASE_FILE.exists()) { // read from file
                log.debug("reading properties from file: " + BASE_FILE.getAbsolutePath());
                singleton = new LibProperties(BASE_FILE);

            } else {
                log.debug("reading properties from jar file as no " + BASE_FILE.getName() + " was found");
                try (InputStream is = LibProperties.class.getResourceAsStream("/" + BASE_FILE.getName())) {
                    singleton = new LibProperties(is);
                }
            }

            assert singleton != null : "properties should not be null";
        }
        return singleton;
    }
}