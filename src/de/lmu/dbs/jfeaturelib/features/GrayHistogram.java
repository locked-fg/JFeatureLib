package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Reads the 256 bin gray histogram from the Image Processor and returns it as
 * double[].
 *
 * @see ByteProcessor#getHistogram()
 * @author Benedikt
 */
public class GrayHistogram extends AbstractFeatureDescriptor {

    /**
     * Constructs a histogram with default parameters.
     */
    public GrayHistogram() {
    }

    /**
     * Starts the histogram detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }

        firePropertyChange(Progress.START);
        int[] features = ((ByteProcessor) ip).getHistogram();
        addData(Arrays2.convertToDouble(features));
        firePropertyChange(Progress.END);
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
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB,
                Supports.Masking);
        return set;
    }

    @Override
    public String getDescription() {
        return "Pixels with tonal value ";
    }
}
