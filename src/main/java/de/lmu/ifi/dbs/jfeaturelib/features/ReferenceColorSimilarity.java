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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.EnumSet;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;

/**
 * Color similarity features. This is not a histogram, because the reference colors are processed independently; any
 * subset of dimensions gives the same result as computing just these colors, making this feature space very favorable
 * for feature bagging and other projections. However, with an increasing number of colors, you can expect the
 * dimensions to be strongly correlated.
 *
 * If you want to cite these features, the first known use is the publication:
 * <p>
 * H.-P. Kriegel, E. Schubert, A. Zimek<br />
 * Evaluation of Multiple Clustering Solutions<br />
 * In 2nd MultiClust Workshop: Discovering, Summarizing and Using Multiple Clusterings Held in Conjunction with ECML
 * PKDD 2011, Athens, Greece: 55–66, 2011.
 * </p>
 * <blockquote> [..] we compute some simple color analysis on the pictures. We defined a set of 77 colors spaced evenly
 * in HSV color space (18 hues with 100% and 50% each in saturation and brightness plus 5 grey values for saturation
 * 0%), then computed the average pixel color similarity to these colors for each image to obtain object reference
 * scorings.</blockquote>
 *
 * This code is contributed to jFeatureLib from the ELKI data mining project at http://elki.dbs.ifi.lmu.de/
 *
 * Recommended distance functions for this feature:
 * <ul>
 * <li>{@code minkowski.ManhattanDistanceFunction}, since the histograms are normalized</li>
 * </ul>
 *
 * @since 1.3.0
 * @author Erich Schubert
 */
public class ReferenceColorSimilarity extends AbstractFeatureDescriptor {

    /**
     * Constant value of two pi.
     */
    private static double TWOPI = Math.PI * 2;
    /**
     * Reference colors: hue, saturation, brightness, cos(hue), sin(hue)
     */
    float[][] refcols;

    /**
     * Constructor for 18*2*2 + 5 = 77 colors.
     */
    public ReferenceColorSimilarity() {
        this(18, 2, 2, 5);
    }

    /**
     * Constructor, using 2*numbbins+1 shades of gray.
     *
     * @param numhbins Bins in Hue
     * @param numsbins Bins in Saturation
     * @param numbbins Bins in Brightness
     */
    public ReferenceColorSimilarity(int numhbins, int numsbins, int numbbins) {
        this(numhbins, numsbins, numbbins, numbbins * 2 + 1);
    }

    /**
     * Constructor for (numhbins * numsbins * numbbins + numbbinsG) colors.
     *
     * @param numhbins Bins in Hue
     * @param numsbins Bins in Saturation
     * @param numbbins Bins in Brightness
     * @param numbbinsG Number of different grey shades
     */
    public ReferenceColorSimilarity(int numhbins, int numsbins, int numbbins, int numbbinsG) {
        super();
        initByBins(numhbins, numsbins, numbbins, numbbinsG);
    }

    /**
     * Constructor with reference colors.
     *
     * @param colors Existing color array
     */
    public ReferenceColorSimilarity(int[] colors) {
        initByColors(colors);
    }

    /**
     *
     * @param numhbins Bins in Hue
     * @param numsbins Bins in Saturation
     * @param numbbins Bins in Brightness
     * @param numbbinsG Number of different grey shades
     */
    private void initByBins(int numhbins, int numsbins, int numbbins, int numbbinsG) {
        float hflt = numhbins, sflt = numsbins, bflt = numbbins, bfltG = numbbinsG - 1;
        int total = numhbins * numsbins * numbbins + numbbinsG;
        refcols = new float[total][5];
        // Colors:
        for (int b = 0; b < numbbins; b++) {
            for (int s = 0; s < numsbins; s++) {
                for (int h = 0; h < numhbins; h++) {
                    int i = h + numhbins * s + (numhbins * numsbins) * b;
                    refcols[i][0] = h / hflt;
                    refcols[i][1] = 1.0f - (s / sflt);
                    refcols[i][2] = 1.0f - (b / bflt);
                    refcols[i][3] = (float) Math.cos(refcols[i][0] * TWOPI);
                    refcols[i][4] = (float) Math.sin(refcols[i][0] * TWOPI);
                }
            }
        }
        // Grey values
        for (int b = 0; b < numbbinsG; b++) {
            int i = numhbins * numsbins * numbbins + b;
            refcols[i][0] = 0.0f;
            refcols[i][1] = 0.0f;
            refcols[i][2] = b / bfltG;
            refcols[i][3] = 1.0f;
            refcols[i][4] = 0.0f;
        }
    }

    private void initByColors(int[] colors) {
        refcols = new float[colors.length][5];
        for (int i = 0; i < colors.length; i++) {
            int r = (colors[i] & 0xFF0000) >> 16, g = (colors[i] & 0x00FF00) >> 8, b = (colors[i] & 0x0000FF);
            Color.RGBtoHSB(r, g, b, refcols[i]);
            refcols[i][3] = (float) Math.cos(refcols[i][0] * TWOPI);
            refcols[i][4] = (float) Math.sin(refcols[i][0] * TWOPI);
        }
    }

    @Override
    public void setProperties(LibProperties properties) {
        String init = properties.getString(LibProperties.REFERENCE_COLOR_SIMILARITY_INIT, "bins");
        switch (init) {
            case "bins":
                int hbins = properties.getInteger(LibProperties.REFERENCE_COLOR_SIMILARITY_H, 1);
                int sbins = properties.getInteger(LibProperties.REFERENCE_COLOR_SIMILARITY_S, 1);
                int bbins = properties.getInteger(LibProperties.REFERENCE_COLOR_SIMILARITY_B, 1);
                int gbins = properties.getInteger(LibProperties.REFERENCE_COLOR_SIMILARITY_G, 1);
                checkArgument(hbins > 0, "hbins must be > 0 but was " + hbins);
                checkArgument(sbins > 0, "sbins must be > 0 but was " + sbins);
                checkArgument(bbins > 0, "bbins must be > 0 but was " + bbins);
                checkArgument(gbins > 0, "gbins must be > 0 but was " + gbins);
                initByBins(hbins, sbins, bbins, gbins);
                break;

            case "color":
                String colString = properties.getString(LibProperties.REFERENCE_COLOR_SIMILARITY_COLORS, "");
                checkNotNull(colString, "colors must not be null");

                colString.trim();
                checkArgument(!colString.isEmpty(), "colors must not be empty");

                String[] colStrings = colString.split(",");
                checkArgument(colStrings.length > 0, "colors array must not be empty");

                int[] cols = new int[colStrings.length];
                for (int i = 0; i < cols.length; i++) {
                    String s = colStrings[i].trim();
                    cols[i] = Integer.parseInt(s);
                    checkArgument(cols[i] >= 0, "colors array components must be >=0");
                }
                checkArgument(Arrays2.sum(cols) > 0, "colors array must not be zero");
                initByColors(cols);
                break;

            default:
                throw new IllegalArgumentException("invalid init mode given: " + init + ". Allowed: bins/color");
        }
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet<Supports> set = EnumSet.of(Supports.Masking,
                Supports.NoChanges, Supports.DOES_8C, Supports.DOES_16,
                Supports.DOES_32, Supports.DOES_RGB);
        return set;
    }

    @Override
    public void run(ImageProcessor ip) {
        if (!ColorProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToRGB();
        }
        firePropertyChange(Progress.START);
        process((ColorProcessor) ip);
        firePropertyChange(Progress.END);
    }

    private void process(ColorProcessor ip) {
        double[] features = new double[refcols.length];
        int realpixels = 0;

        ImageProcessor mask = ip.getMask();

        int numpixels = ip.getPixelCount();
        float[] hsbvals = new float[5]; // Buffer
        for (int i = 0; i < numpixels; i++) {
            if (mask == null || mask.get(i) == 0) {
                int rgb = ip.get(i);
                int r = (rgb & 0xFF0000) >> 16, g = (rgb & 0x00FF00) >> 8, b = (rgb & 0x0000FF);
                Color.RGBtoHSB(r, g, b, hsbvals);
                hsbvals[3] = (float) Math.cos(hsbvals[0] * TWOPI);
                hsbvals[4] = (float) Math.sin(hsbvals[0] * TWOPI);
                // Compare to reference colors:
                for (int j = 0; j < refcols.length; j++) {
                    double cos = refcols[j][3] * refcols[j][1]
                            - hsbvals[3] * hsbvals[1];
                    double sin = refcols[j][4] * refcols[j][1]
                            - hsbvals[4] * hsbvals[1];
                    double db = refcols[j][2] - hsbvals[2];
                    double val = 1. - Math
                            .sqrt((db * db + sin * sin + cos * cos) / 5);
                    features[j] += val;
                }
                realpixels++;
            }
        }

        // Normalize by number of pixels processed.
        for (int i = 0; i < features.length; i++) {
            features[i] /= (double) realpixels;
        }
        addData(features);
    }

    @Override
    public String getDescription() {
        return "ReferenceColorSimilarity";
    }
}