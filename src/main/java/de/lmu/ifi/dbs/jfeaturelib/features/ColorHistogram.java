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
 * A RGB/HSB histogram.
 *
 * This color space can be split sensibly in a non-uniform way, to give more detail e.g. to the hues, while not
 * splitting fine on the saturation.
 *
 * In case of using the HSB color space, good results have been reported with 7x2x2 and 7x3x3 splits, for example.
 *
 * This code is contributed to jFeatureLib from the ELKI data mining project at http://elki.dbs.ifi.lmu.de/
 *
 * Recommended distance functions for this feature:
 * <ul>
 * <li>{@code minkowski.ManhattanDistanceFunction}, since the histograms are normalized</li>
 * <li>{@code colorhistogram.HistogramIntersectionDistanceFunction} (equivalent to Manhattan on normalized
 * histograms)</li>
 * <li>{@code colorhistogram.HSBHistogramQuadraticDistanceFunction}</li>
 * </ul>
 *
 * @author Erich Schubert
 * @since 1.3.0
 */
public class ColorHistogram extends AbstractFeatureDescriptor {

    /**
     * Type defining which type of colorspace should be used
     */
    public static enum TYPE {

        RGB, HSB
    }
    TYPE type = TYPE.RGB;
    /**
     * Bins per plane.
     */
    int binX, binY, binZ;

    /**
     * Constructor for 7x3x3 = 63 bins.
     */
    public ColorHistogram() {
        this(7, 3, 3);
    }

    /**
     * @param quanth Bins in Hue/Red
     * @param quants Bins in Saturation/Green
     * @param quantb Bins in Brightness/Blue
     */
    public ColorHistogram(int quanth, int quants, int quantb) {
        super();
        this.binX = quanth;
        this.binY = quants;
        this.binZ = quantb;
    }

    /**
     * set the type of extraction to either RGB or HSB
     *
     * @param type
     */
    public ColorHistogram setType(TYPE type) {
        this.type = type;
        return this;
    }

    @Override
    public void setProperties(LibProperties properties) {
        type = TYPE.valueOf(properties.getString(LibProperties.COLOR_HISTOGRAMS_TYPE));
        binX = properties.getInteger(LibProperties.COLOR_HISTOGRAMS_BINS_X);
        binY = properties.getInteger(LibProperties.COLOR_HISTOGRAMS_BINS_Y);
        binZ = properties.getInteger(LibProperties.COLOR_HISTOGRAMS_BINS_Z);
        
        
        checkNotNull(type, "type must not be null");
        checkArgument(binX>0, "bin x must be >0 but was "+binX);
        checkArgument(binY>0, "bin y must be >0 but was "+binY);
        checkArgument(binZ>0, "bin z must be >0 but was "+binZ);
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
        double[] feature = new double[binX * binY * binZ];
        int processedPixels = 0;

        ImageProcessor mask = ip.getMask();

        int numpixels = ip.getPixelCount();
        float[] hsbvals = new float[3]; // Conversion buffer
        for (int i = 0; i < numpixels; i++) {
            if (mask == null || mask.get(i) != 0) {
                if (type == TYPE.HSB) {
                    feature[getBinForHSB(ip.get(i), hsbvals)]++;
                } else if (type == TYPE.RGB) {
                    feature[getBinForRGB(ip.get(i))]++;
                }
                processedPixels++;
            }
        }

        Arrays2.div(feature, processedPixels);
        addData(feature);
    }

    private int getBinForRGB(int rgb) {
        int r = (rgb & 0xFF0000) >> 16, g = (rgb & 0x00FF00) >> 8, b = (rgb & 0x0000FF);
        r = (int) Math.floor(binX * r / 256.);
        g = (int) Math.floor(binY * g / 256.);
        b = (int) Math.floor(binZ * b / 256.);
        return r * binX * binY + g * binZ + b;
    }

    private int getBinForHSB(int rgb, float[] hsbvals) {
        final int r = (rgb & 0xFF0000) >> 16, g = (rgb & 0x00FF00) >> 8, b = (rgb & 0x0000FF);
        // TODO: AWT Color.RGBtoHSB can be sped up numerically
        Color.RGBtoHSB(r, g, b, hsbvals);
        // The values returned by RGBtoHSB are all in [0:1]
        int h = Math.min((int) Math.floor(binX * hsbvals[0]), binX - 1);
        int s = Math.min((int) Math.floor(binY * hsbvals[1]), binY - 1);
        int v = Math.min((int) Math.floor(binZ * hsbvals[2]), binZ - 1);
        return h * binY * binZ + s * binZ + v;
    }

    @Override
    public String getDescription() {
        return "HSB/RGBHistogram";
    }
}