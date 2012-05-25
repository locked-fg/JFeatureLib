package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the histogram from the Image Processor and returns it as double[].
 *
 * The features represents 3 concatenated 256bin histograms (red, green, blue)
 *
 * @author Benedikt
 */
public class RGBHistogram extends AbstractFeatureDescriptor {

    private ColorProcessor image;

    /**
     * Constructs a RGB histogram with default parameters (8bit per channel).
     */
    public RGBHistogram() {
    }

    /**
     * Returns information about the getFeauture returns in a String array.
     */
    @Override
    public String getDescription() {
        return "RGB Histogram tonal values ";
    }

    /**
     * Starts the RGB histogram detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        this.image = (ColorProcessor) ip;
        firePropertyChange(Progress.START);
        process();
        firePropertyChange(Progress.END);
    }

    private void process() {
        int[] features = new int[3 * 256];

        ColorProcessor.setWeightingFactors(1, 0, 0);
        System.arraycopy(image.getHistogram(), 0, features, 0, 256);
        firePropertyChange(new Progress(30));

        ColorProcessor.setWeightingFactors(0, 1, 0);
        System.arraycopy(image.getHistogram(), 0, features, 256, 256);
        firePropertyChange(new Progress(60));

        ColorProcessor.setWeightingFactors(0, 0, 1);
        System.arraycopy(image.getHistogram(), 0, features, 512, 256);
        firePropertyChange(new Progress(90));

        addData(features);
    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_RGB,
                Supports.Masking);
        return set;
    }
}
