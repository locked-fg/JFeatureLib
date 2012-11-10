package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.utils.RGBtoGray;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * Implementation of the SUSAN (Smallest Univalue Segment Assimilating Nucleus)
 * edge detector.
 *
 * @author Benedikt
 * @see http://en.wikipedia.org/wiki/Corner_detection#The_SUSAN_corner_detector
 */
public class Susan implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int radius;
    private int threshold;
    private ColorProcessor image;

    /**
     * Standart constructor with radius 2 and threshold 15
     */
    public Susan() {
        this.radius = 2;
        this.threshold = 15;
    }

    /**
     * @param radius Radius in which the image is looked at
     * @param threshold Threshold for difference in luminosity
     */
    public Susan(int radius, int threshold) {
        this.radius = radius;
        this.threshold = threshold;

    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(Supports.DOES_RGB);
        return set;
    }

    @Override
    public void run(ImageProcessor ip) {
        if (!ip.getClass().isAssignableFrom(ColorProcessor.class)) {
            throw new IllegalArgumentException("incompatible processor");
        }
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        this.image = (ColorProcessor) ip;
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    /*
     * http://users.fmrib.ox.ac.uk/~steve/susan/susan/node6.html Place a
     * circular mask around the pixel in question (the nucleus). Using Equation
     * 4 calculate the number of pixels within the circular mask which have
     * similar brightness to the nucleus. (These pixels define the USAN.) Using
     * Equation 3 subtract the USAN size from the geometric threshold to produce
     * an edge strength image. Use moment calculations applied to the USAN to
     * find the edge direction. Apply non-maximum suppression, thinning and
     * sub-pixel estimation, if required.
     */
    private void process() {
        int WIDTH = image.getWidth();
        int HEIGHT = image.getHeight();
        int[][] picture = image.getIntArray();

        ColorProcessor result = new ColorProcessor(WIDTH, HEIGHT);
        int[][] mask = new int[radius * 2 + 1][radius * 2 + 1];
        int i = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                //ignore borders
                if (x >= radius && x < WIDTH - radius && y >= radius && y < HEIGHT - radius) {
                    for (int maskX = 0; maskX <= radius * 2; maskX++) {
                        for (int maskY = 0; maskY <= radius * 2; maskY++) {
                            mask[maskX][maskY] = RGBtoGray.ARGB_NTSC(picture[x - radius + maskX][y - radius + maskY]);
                        }
                    }
                    //horizontal edge
                    boolean edge = true;
                    for (int maskX = 0; maskX <= radius * 2; maskX++) {
                        for (int maskY = 0; maskY <= radius; maskY++) {
                            if (edge && Math.abs(mask[maskX][maskY] - mask[radius][radius]) < threshold) {
                                edge = true;
                            } else {
                                edge = false;
                            }

                        }
                        if (edge) {
                            result.set(x, y, -1);
                        } else {
                            result.set(x, y, -16777216);
                        }
                    }
                    //vertical edge
                    edge = true;
                    for (int maskX = 0; maskX <= radius; maskX++) {
                        for (int maskY = 0; maskY <= radius * 2; maskY++) {
                            if (edge && Math.abs(mask[maskX][maskY] - mask[radius][radius]) < threshold) {
                                edge = true;
                            } else {
                                edge = false;
                            }

                        }
                        if (edge) {
                            result.set(x, y, -1);
                        } else if (result.get(x, y) != -1) {
                            result.set(x, y, -16777216);
                        }
                    }
                } //border
                else {
                }
                i++;
            }
            int progress = (int) Math.round(i * (100.0 / (double) (WIDTH * HEIGHT)));
            pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + WIDTH * HEIGHT));
        }

        // write the result back into the image processor
        image.insert(result, 0, 0);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    //<editor-fold defaultstate="collapsed" desc="accessors">
    /**
     * @return Radius in which the image is investigated
     */
    public int getRadius() {
        return radius;
    }

    /**
     *
     * @param radius Radius in which the image is investigated
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     *
     * @return Threshold for difference in luminosity
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     *
     * @param threshold Threshold for difference in luminosity
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    //</editor-fold>
}
