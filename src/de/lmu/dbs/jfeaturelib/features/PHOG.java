package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.utils.GradientImage;
import de.lmu.dbs.jfeaturelib.utils.GradientSource;
import de.lmu.dbs.jfeaturelib.utils.Interpolated1DHistogram;
import de.lmu.ifi.dbs.utilities.Math2;
import de.lmu.ifi.dbs.utilities.Vectors;
import de.lmu.ifi.dbs.utilities.primitiveArrays.DoubleArray;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Generates Pyramid Histograms of Oriented Gradients,
 * first introduced in "Representing shape with a spatial pyramid kernel" (2007).
 * By Anna Bosch, Andrew Zisserman, Xavier Munoz
 * 
 * @author graf
 * @since 11/4/2011
 **/
public class PHOG extends FeatureDescriptorAdapter {

    /* 
     * Amount of bins for each histogram 
     */
    private int bins = 8;
    /*
     * Amount of recursions for this descriptor.
     * 0 means only the root level.
     */
    private int recursions = 1;
    private DoubleArray feature;
    private GradientSource gradientSource = new GradientImage();
    private Interpolated1DHistogram hist;

    @Override
    public List<double[]> getFeatures() {
        ArrayList<double[]> list = new ArrayList<>(1);
        list.add(feature.getData());
        return list;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(Supports.DOES_8G, Supports.Masking);
    }

    @Override
    public void run(ImageProcessor ip) {
        hist = new Interpolated1DHistogram(0, Math.PI, bins);
        gradientSource.setIp(ip);
        initFeature();

        buildHistogramRecursively(ip.getRoi(), 0);

        // release memory
        gradientSource = null;
        Vectors.normalize(feature.getData());
    }

    private void buildHistogramRecursively(Rectangle r, int recursion) {
        hist.clear();

        final int borderRight = r.x + r.width;
        final int borderBottom = r.y + r.height;
        for (int x = r.x; x < borderRight; x++) {
            for (int y = r.y; y < borderBottom; y++) {
                if (gradientSource.getLength(x, y) != 0) {
                    hist.add(gradientSource.getTheta(x, y),
                            gradientSource.getLength(x, y));
                }
            }
        }
        feature.addAll(hist.getData());

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
