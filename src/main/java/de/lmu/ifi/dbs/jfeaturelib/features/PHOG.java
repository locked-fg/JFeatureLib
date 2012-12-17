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
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.edgeDetector.Canny;
import de.lmu.ifi.dbs.jfeaturelib.utils.GradientImage;
import de.lmu.ifi.dbs.jfeaturelib.utils.GradientSource;
import de.lmu.ifi.dbs.jfeaturelib.utils.Interpolated1DHistogram;
import de.lmu.ifi.dbs.utilities.Math2;
import de.lmu.ifi.dbs.utilities.Vectors;
import de.lmu.ifi.dbs.utilities.primitiveArrays.DoubleArray;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.log4j.Logger;

/**
 * Generates Pyramid Histograms of Oriented Gradients (PHoG).
 *
 * Phogs were first introduced in "Representing shape with a spatial pyramid
 * kernel" (2007). By Anna Bosch, Andrew Zisserman, Xavier Munoz
 *
 * See also http://www.robots.ox.ac.uk/~vgg/publications/2007/Bosch07/ and
 * http://dl.acm.org/citation.cfm?id=1282340 for further information.
 *
 * In the original paper, canny edge extraction is applied prior to extracting
 * the gradients. As edge detection can be performed in several ways, it is not
 * hard wired. It can be enabled in the properties by setting
 * features.phog.canny to true. The parameters for the canny operator are also
 * taken from the properties file.
 *
 * If another edge detection operator should be used, simple set the canny
 * parameter to false and call {@link #run(ij.process.ImageProcessor)} with an
 * pre processed image.
 *
 * @author graf
 * @since 11/4/2011
 */
public class PHOG extends AbstractFeatureDescriptor {

    private static final Logger log = Logger.getLogger(PHOG.class.getName());
    /*
     * Amount of bins for each histogram
     */
    private int bins = 8;
    /*
     * Amount of recursions for this descriptor. 0 means only the root level.
     */
    private int recursions = 1;
    /**
     * dynamic array holding the feature
     */
    private DoubleArray feature;
    /**
     * the wrapper class to extract the gradient information from
     */
    private GradientSource gradientSource = new GradientImage();
    private Interpolated1DHistogram histogram;
    boolean useCanny;
    private LibProperties properties;

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        this.properties = properties;

        bins = properties.getInteger(LibProperties.PHOG_BINS, 8);
        recursions = properties.getInteger(LibProperties.PHOG_RECURSIONS, 1);
        useCanny = properties.getBoolean(LibProperties.PHOG_CANNY, true);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
        setMask(ip);
        if (useCanny) {
            ip = applyCanny(ip);
        }
        if (!(ip instanceof ByteProcessor)) {
            ip = ip.convertToByte(true);
        }

        gradientSource.setIp(ip);
        initFeature();

        histogram = new Interpolated1DHistogram(0, Math.PI, bins);
        buildHistogramRecursively(ip.getRoi(), 0);

        // release memory
        gradientSource = null;
        Vectors.normalize(feature.getData());

        addData(feature.getData());
        firePropertyChange(Progress.END);
    }

    ImageProcessor applyCanny(ImageProcessor ip) throws IllegalStateException {
        try {
            Canny canny = new Canny();
            canny.setProperties(properties);
            canny.run(ip);
        } catch (IOException ex) {
            log.error("error in canny config", ex);
            throw new IllegalStateException(ex);
        }
        return ip;
    }

    private void buildHistogramRecursively(Rectangle r, int recursion) {
        histogram.clear();

        final int borderRight = r.x + r.width;
        final int borderBottom = r.y + r.height;
        for (int x = r.x; x < borderRight; x++) {
            for (int y = r.y; y < borderBottom; y++) {
                if (inMask(x, y) && gradientSource.getLength(x, y) != 0) {
                    histogram.add(gradientSource.getTheta(x, y),
                            gradientSource.getLength(x, y));
                }
            }
        }
        feature.addAll(histogram.getData());

        // descend into next recursion
        if (recursion++ < recursions) {
            final int w2 = r.width / 2;
            final int h2 = r.height / 2;

            Rectangle tl = new Rectangle(r.x, r.y, w2, h2);
            buildHistogramRecursively(tl, recursion);

            Rectangle tr = new Rectangle(w2, r.y, w2, h2);
            buildHistogramRecursively(tr, recursion);

            Rectangle bl = new Rectangle(r.x, h2, w2, h2);
            buildHistogramRecursively(bl, recursion);

            Rectangle br = new Rectangle(w2, h2, w2, h2);
            buildHistogramRecursively(br, recursion);
        }
    }

    void initFeature() {
        int length = 0;
        for (int i = 0; i <= recursions; i++) {
            length += bins * Math2.pow(4, i);
        }
        feature = new DoubleArray(length);
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G, Supports.DOES_8C, Supports.DOES_16,
                Supports.DOES_32, Supports.Masking);
    }

    @Override
    public String getDescription() {
        return "Pyramid Histograms of Oriented Gradients";
    }

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    public GradientSource getGradientSource() {
        return gradientSource;
    }

    public void setGradientSource(GradientSource gradientSource) {
        if (gradientSource == null) {
            throw new NullPointerException("gradientSource must not be null");
        }
        this.gradientSource = gradientSource;
    }

    public int getBins() {
        return bins;
    }

    public void setBins(int bins) {
        if (bins <= 0) {
            throw new IllegalArgumentException("bins must be > 0 but was " + bins);
        }
        this.bins = bins;
    }

    public int getRecursions() {
        return recursions;
    }

    public void setRecursions(int recursions) {
        if (recursions < 0) {
            throw new IllegalArgumentException("recursions must be >= 0 but was " + recursions);
        }
        this.recursions = recursions;
    }
    //</editor-fold>
}
