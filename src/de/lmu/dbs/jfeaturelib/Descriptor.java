package de.lmu.dbs.jfeaturelib;

import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * @author graf
 */
public interface Descriptor {

    /**
     * Defines the capability of the algorithm.
     * It is possible that the list will be extended to the complete list of ImageJ's PlugInFilter Flags
     * 
     * @see PlugInFilter
     * @see #supports() 
     */
    public enum Supports {

        /** If a mask is set, the features will only be extracted from this area */
        Masking,
        /** Set this, if the ImageProcessor will not be changed. */
        NoChanges,
        /** Supports 8bit Grayscale */
        DOES_8G,
        /** Supports 8bit indexed color */
        DOES_8C,
        /** supports 16-bit images */
        DOES_16,
        /** supports float images */
        DOES_32,
        /** supports RGB images */
        DOES_RGB
    }
    /** Convenience field for "support all kinds of images". */
    public final EnumSet<Supports> DOES_ALL = EnumSet.of(
            Supports.DOES_16,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB);

    /**
     * Determine the capabilities of this algorithm.
     * 
     * @return 
     */
    public EnumSet<Supports> supports();

    /**
     * Start processing of this algorithm on the given image processor.
     * Keep in mind that if the imageProcessor is changed, Supports.NoChanges 
     * must not be set.
     * 
     * This is also the case even if only ImageProcessor.snapshot() is used.
     * 
     * @param ip 
     */
    public void run(ImageProcessor ip);
    
    /**
     * @FIXME add javadoc
     * 
     * @return 
     */
    String[] getDescription();
}
