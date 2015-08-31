/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
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
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.utils.Histogram;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.utils.IntegralImage;
import ij.measure.Measurements;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.EnumSet;

/**
 * This descriptor calculates a histogram of mean intensities of specified neighborhood size.
 * <p>
 * For each pixel in the image, a patch of size
 * <code>1 + 2 * size</code> is extracted, where
 * <code>size</code> is determined by {@link #setSize(int)}. Mean intensities of all patches are summarized in a
 * histogram.
 * </p><p>
 * Mean intensities are computed efficiently by using {@link IntegralImage}.
 * </p>
 *
 * @author sebp
 */
public class MeanPatchIntensityHistogram extends AbstractFeatureDescriptor {

    protected int m_size;
    protected int m_bins;
    protected double m_histMin;
    protected double m_histMax;

    protected int m_patchSize;
    protected int m_patchArea;
    protected IntegralImage m_integralImage;

    public MeanPatchIntensityHistogram() {
    }

    @Override
    public String getDescription() {
        return "Mean patch intensities";
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        setSize(properties.getInteger(LibProperties.MEAN_PATCH_INTENSITIES_PATCH_SIZE));
        setNumberOfBins(properties.getInteger(LibProperties.MEAN_PATCH_INTENSITIES_BINS));
        setHistogramRange(
                properties.getDouble(LibProperties.MEAN_PATCH_INTENSITIES_HIST_MIN),
                properties.getDouble(LibProperties.MEAN_PATCH_INTENSITIES_HIST_MAX));
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        createIntegralImage((ByteProcessor) ip);

        int yStart, xStart, yEnd, xEnd;
        yStart = m_size;
        xStart = m_size;
        yEnd = ip.getHeight() - m_size;
        xEnd = ip.getWidth() - m_size;

        if (m_histMin == 0 && m_histMax == 0) {
            retrieveMinAndMaxFromImage(ip);
            // Histogram class excludes the maximum value,
            // therefore increase it by 1
            m_histMax++;
        }

        Histogram hist = new Histogram(m_bins, m_histMin, m_histMax);

        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                hist.add(getMeanIntensity(x, y));
            }
            int p = (int) (y / (double) yEnd * 100);
            firePropertyChange(new Progress(p));
        }

        addData(hist.getHistogramm());

        m_integralImage = null;
        firePropertyChange(Progress.END);
    }

    protected void createIntegralImage(ByteProcessor ip) {
        m_integralImage = new IntegralImage();
        m_integralImage.compute(ip);
    }

    protected void retrieveMinAndMaxFromImage(ImageProcessor ip) {
        ImageStatistics stats = ImageStatistics.getStatistics(ip, Measurements.MIN_MAX, null);
        m_histMin = stats.min;
        m_histMax = stats.max;
    }

    protected float getMeanIntensity(final int x, final int y) {
        Rectangle rect = new Rectangle(x - m_size, y - m_size, m_patchSize, m_patchSize);
        return m_integralImage.get(rect) / (float) m_patchArea;
    }

    public int getSize() {
        return m_size;
    }

    /**
     * Set size of neighborhood which determines the patch size.
     * <p>
     * The outer most <tt>size</tt> pixels will not be considered because they have incomplete neighborhoods.
     * </p>
     *
     * @param size
     * @throws IllegalArgumentException if <code>size <= 0</code>
     */
    public void setSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater zero, but got " + size);
        }
        m_size = size;
        m_patchSize = 1 + 2 * size;
        m_patchArea = m_patchSize * m_patchSize;
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
        if (bins <= 0) {
            throw new IllegalArgumentException("number of bins must be greater zero, but got " + bins);
        }
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
     * <p>
     * If both min and max are set to zero (default), limits are retrieved from data.
     * </p>
     *
     * @param min
     * @param max
     * @throws IllegalArgumentException if <code>min > max</code>
     */
    public void setHistogramRange(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be smaller than or equal to max");
        }
        m_histMin = min;
        m_histMax = max;
    }

}
