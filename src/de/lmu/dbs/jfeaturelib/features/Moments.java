package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Calculates the basic statistical moments of an image like 
 * mean, std_dev, skewness and kurtosis.
 * 
 * @author graf
 * @since 11/4/2011
 */
public class Moments extends FeatureDescriptorAdapter {

    private double[] feature;

    @Override
    public List<double[]> getFeatures() {
        ArrayList<double[]> result = new ArrayList<>(1);
        result.add(feature);
        return result;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.allOf(Supports.class);
    }

    @Override
    public void run(ImageProcessor ip) {
        int STATS = ImageStatistics.MEAN
                + ImageStatistics.STD_DEV
                + ImageStatistics.SKEWNESS
                + ImageStatistics.KURTOSIS;
        ImageStatistics stat = ImageStatistics.getStatistics(ip, STATS, null);
        feature = new double[]{stat.mean, stat.stdDev, stat.skewness, stat.kurtosis};
    }
}
