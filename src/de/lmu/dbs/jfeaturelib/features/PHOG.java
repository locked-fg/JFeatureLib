package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.LibProperties;
import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.edgeDetector.Canny;
import de.lmu.dbs.jfeaturelib.utils.GradientImage;
import de.lmu.dbs.jfeaturelib.utils.GradientSource;
import de.lmu.dbs.jfeaturelib.utils.Interpolated1DHistogram;
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
 * Generates Pyramid Histograms of Oriented Gradients, first introduced in
 * "Representing shape with a spatial pyramid kernel" (2007). By Anna Bosch,
 * Andrew Zisserman, Xavier Munoz
 *
 * See also http://www.robots.ox.ac.uk/~vgg/publications/2007/Bosch07/ and
 * http://dl.acm.org/citation.cfm?id=1282340 for further information.
 *
 * @author graf
 * @since 11/4/2011
 *
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

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        bins = properties.getInteger(LibProperties.PHOG_BINS);
        recursions = properties.getInteger(LibProperties.PHOG_RECURSIONS);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
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

    private void buildHistogramRecursively(Rectangle r, int recursion) {
        histogram.clear();

        final int borderRight = r.x + r.width;
        final int borderBottom = r.y + r.height;
        for (int x = r.x; x < borderRight; x++) {
            for (int y = r.y; y < borderBottom; y++) {
                if (gradientSource.getLength(x, y) != 0) {
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

    private static class CannyWrapper implements GradientSource {

        private final Canny canny;
        private boolean isProcessed = false;

        private CannyWrapper(Canny canny) {
            this.canny = canny;
        }

        @Override
        public void setIp(ImageProcessor ip) {
            if (isProcessed) {
                throw new IllegalStateException();
            }
            canny.run(ip);
        }

        @Override
        public double getLength(int x, int y) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getTheta(int x, int y) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
