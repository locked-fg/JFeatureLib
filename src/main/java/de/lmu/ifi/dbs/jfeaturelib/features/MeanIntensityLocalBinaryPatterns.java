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

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Patch-based local binary patterns.
 * <p>
 * Constructs histogram of binary patterns based on comparing the mean intensity
 * of the 8-neighborhood of each pixel N<sub>i</sub> in the patch to the mean
 * intensity of the 8-neighborhood around the pixel in the center N<sub>C</sub>.
 * Each of the 8 mean intensities N<sub>i</sub> is compared to the mean intensity
 * N<sub>C</sub> in the center, if N<sub>i</sub> is smaller than N<sub>C</sub>,
 * N<sub>i</sub> is assigned weight 0, else 1. The weights form a binary pattern
 * by contactinating them in counter clockwise order, starting with the weight
 * to the left of the pixel in the center.
 * </p>
 * <p><strong>Example:</strong><br/>
 * Mean intensity patch:
 * <pre>
 *    |  2 | 25 | 32 |
 *    | 24 | 23 |  9 |
 *    | 19 | 99 | 13 |
 * </pre>
 * Weights:
 * <pre>
 *    | 0 | 1 | 1 |
 *    | 1 |   | 0 |
 *    | 0 | 1 | 0 |
 * </pre>
 * Binary pattern: <code>0b10100110</code>
 * </p>
 * <p>
 * All pixels part of the 2-pixel wide margin are not considered due to incomplete
 * 8-neighborhoods of these pixels.
 * </p>
 * <p>
 * References:
 * <pre>
 * @inproceedings{
 *   author = {Ojala, Timo and Pietikäinen, Matti and Harwood, David},
 *   title = {Performance Evaluation of Texture Measures with Classification
 *       Based on Kullback Disrimination of Distributions},
 *   booktitle = {Proceedings of the 12th IAPR International Conference on
 *       Pattern Recognition (ICPR 1994)},
 *   year = {1994},
 *   pages = {582--585}
 * }
 * </pre>
 * </p>
 *
 * @author sebp
 * @see MeanPatchIntensityDescriptor
 */
public class MeanIntensityLocalBinaryPatterns extends AbstractFeatureDescriptor {

    protected int m_bins;
    protected double m_histMin;
    protected double m_histMax;

    protected MeanPatchIntensityHistogram m_meanDescriptor;

    public MeanIntensityLocalBinaryPatterns() {
    }

    @Override
    public String getDescription() {
        return "Mean Intensity Local Binary Patterns";
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.Masking,
                Supports.NoChanges,
                Supports.DOES_8G,
                Supports.DOES_8C,
                Supports.DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);
        createPatchDescriptor(ip);

        final int yEnd = ip.getHeight() - 2;
        final int xEnd = ip.getWidth() - 2;
        final int width = xEnd - 2;
        final int height = yEnd - 2;

        final byte[] mask = ip.getMaskArray();
        byte[] data = new byte[width * height];
        byte[] histMask = new byte[width * height];
        // do not process pixels at border, which don't have a complete 8-neighborhood
        int k = 0;
        for (int y=2; y < yEnd; y++) {
            for (int x=2; x < xEnd; x++) {
                if (mask == null || mask[k] != 0) {
                    data[k] = getBinaryPattern(x, y);
                    histMask[k] = -1; // -1 = 0xFF
                }
                k++;
            }
        }

        ByteProcessor lbpImage = new ByteProcessor(width, height, data);
        lbpImage.setHistogramSize(256);
        lbpImage.setHistogramRange(0, 256);
        int[] hist;
        if (mask == null) {
            hist = lbpImage.getHistogram();
        } else {
            ByteProcessor maskIp = new ByteProcessor(width, height, histMask);
            hist = lbpImage.getHistogram(maskIp);
        }
        addData(hist);

        m_meanDescriptor = null;
        firePropertyChange(Progress.END);
    }

    protected void createPatchDescriptor(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ImageProcessor mask = ip.getMask();
            ip = ip.convertToByte(true);
            ip.setMask(mask);
        }

        m_meanDescriptor = new MeanPatchIntensityHistogram();
        m_meanDescriptor.setSize(1);
        m_meanDescriptor.createIntegralImage((ByteProcessor) ip);
    }

    protected byte getBinaryPattern(final int x, final int y) {
        final double meanCenter = m_meanDescriptor.getMeanIntensity(x, y);

        // define 8-neighborhood clockwise starting from (x-1, y-1)
        final int[] yp = new int[] {y-1, y-1, y-1, y, y+1, y+1, y+1, y};
        final int[] xp = new int[] {x-1, x, x+1, x+1, x+1, x, x-1, x-1};

        byte bit = 0;
        byte result = 0;
        // iterate over 8-neighborhood of pixel at (x, y)
        for (int i=0; i<xp.length; i++) {
            double meanPixel = m_meanDescriptor.getMeanIntensity(xp[i], yp[i]);
            if (meanPixel >= meanCenter) {
                result |= (1 << bit);
            }
            bit++;
        }
        return result;
    }

    public int getNumberOfBins() {
        return m_bins;
    }

    /**
     * Set the number of bins of the histogram.
     *
     * @param bins
     * @throws IllegalArgumentException if <code>bins <= 0</code>
     */
    public void setNumberOfBins(int bins) {
        if (bins <= 0)
            throw new IllegalArgumentException("number of bins must be greater zero, but got " + bins);
        m_bins = bins;
    }

    public double getHistogramMin() {
        return m_histMin;
    }

    public double getHistogramMax() {
        return m_histMax;
    }

    /**
     * Set the minimum and maximum value that the histogram should consider.
     *
     * If both min and max are set to zero (default), limits are retrieved from data.
     *
     * @throws IllegalArgumentException if <code>min > max</code>
     */
    public void setHistogramRange(double min, double max) {
        if (min > max)
            throw new IllegalArgumentException("min must be smaller than or equal to max");
        m_histMin = min;
        m_histMax = max;
    }
}