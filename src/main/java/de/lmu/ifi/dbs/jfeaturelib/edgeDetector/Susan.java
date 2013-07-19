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
package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.utils.RGBtoGray;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Implementation of the SUSAN (Smallest Univalue Segment Assimilating Nucleus) edge detector.
 *
 * <p>
 * See also the Wikipedia page: http://en.wikipedia.org/wiki/Corner_detection for more information.</p>
 *
 * @author Benedikt
 */
public class Susan extends AbstractDescriptor {

    private int IS_EDGE = -1;
    private int NO_EDGE = -16777216;

    private int radius;
    private int threshold;
    private ByteProcessor edgemask;
    private int[][] picture;

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
     * @return
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(Supports.DOES_RGB, Supports.DOES_8G, Supports.DOES_8C);
        return set;
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();

        this.picture = ip.getIntArray();
        if (ip.getClass().isAssignableFrom(ColorProcessor.class)) {
            colorToGray();
        }
        this.edgemask = new ByteProcessor(ip.getWidth(), ip.getHeight());

        process();

        // write the result back into the image processor
        for (int i = 0; i < edgemask.getPixelCount(); i++) {
            ip.set(i, edgemask.get(i) != 0 ? IS_EDGE : NO_EDGE);
        }

        endProgress();
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
        int WIDTH = edgemask.getWidth();
        int HEIGHT = edgemask.getHeight();

        int[][] mask = new int[radius * 2 + 1][radius * 2 + 1];
        //ignore borders
        for (int x = radius; x < WIDTH - radius; x++) {
            for (int y = radius; y < HEIGHT - radius; y++) {
                for (int maskX = 0; maskX <= radius * 2; maskX++) {
                    for (int maskY = 0; maskY <= radius * 2; maskY++) {
                        mask[maskX][maskY] = picture[x - radius + maskX][y - radius + maskY];
                    }
                }
                {//horizontal edge
                    boolean edge = true;
                    for (int maskX = 0; maskX <= radius * 2; maskX++) {
                        for (int maskY = 0; edge && maskY <= radius; maskY++) {
                            edge &= Math.abs(mask[maskX][maskY] - mask[radius][radius]) < threshold;
                        }
                        edgemask.set(x, y, edge ? 255 : 0);
                    }
                }
                {//vertical edge
                    boolean edge = true;
                    for (int maskX = 0; maskX <= radius; maskX++) {
                        for (int maskY = 0; edge && maskY <= radius * 2; maskY++) {
                            edge &= Math.abs(mask[maskX][maskY] - mask[radius][radius]) < threshold;
                        }
                        edgemask.set(x, y, edge ? 255 : 0);
                    }
                }
            }
            int progress = (int) 100d * x / WIDTH;

            pcs.firePropertyChange(Progress.getName(), null, new Progress(progress));
        }

    }

    /**
     * @return the edge mask with edges being marked with 255
     */
    public ByteProcessor getEdgemask() {
        return edgemask;
    }

    private void colorToGray() {
        // to Gray
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[0].length; j++) {
                picture[i][j] = RGBtoGray.ARGB_NTSC(picture[i][j]);
            }
        }
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
