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
package de.lmu.ifi.dbs.jfeaturelib.features.lbp;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.ifi.dbs.jfeaturelib.utils.Histogram;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Constructs a histogram of local binary patterns (LBP) for each pixel.
 * <p>
 * For each pixel a histogram of LBP is constructed from all pixels in the region
 * specified by {@link #setNeighborhoodSize(int)}. In this region the LBP is
 * calculated for every pixel. The LBP is calculated by comparing the pixel
 * intensities of the central pixel to <tt>N</tt> neighboring pixels ({@link #setNumPoints(int)})
 * on a circle of radius <tt>r</tt> ({@link #setRadius(double)}). Interpolation
 * is used to retrieve the intensity of neighboring pixels.
 * </p>
 * <p>
 * In comparison to {@link MeanIntensityLocalBinaryPatterns}, this class computes
 * one histogram of each pixel and computes local binary patterns from neighbors
 * lying on a circle around the central pixel.
 * </p>
 * <p>References:
 * <pre>
 * @article{Heikkla2006,
 *   author = {Heikklä, Marko and Pietikäinen, Matti},
 *   title = {A texture-based method for modeling the background and detecting moving objects.},
 *   journal = {IEEE transactions on pattern analysis and machine intelligence},
 *   number = {4},
 *   volume = {28},
 *   year = {2006},
 *   pages = {657--62}
 * }
 * </pre>
 * </p>
 *
 * @author sebp
 * @see MeanIntensityLocalBinaryPatterns
 */
public class LocalBinaryPatterns extends AbstractFeatureDescriptor {

    private double m_radius;
    private int m_numPoints;
    private int m_neighborhoodSize;
    private double m_constant;
    private int m_histogramSize;

    /* The angle between adjacent neighbors */
    protected double m_angle;
    /* Offset in x and y direction of all neighbors */
    protected double[] m_offsets;
    protected ImageProcessor m_ip;

    public LocalBinaryPatterns() {
    }

    @Override
    public String getDescription() {
        return "Local Binary Patterns";
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
    public void setProperties(LibProperties properties) throws IOException {
        setRadius(properties.getDouble(LibProperties.LBP_RADIUS));
        setNumPoints(properties.getInteger(LibProperties.LBP_NUM_POINTS));
        setNeighborhoodSize(properties.getInteger(LibProperties.LBP_NEIGHBORHOOD_SIZE));
        setConstant(properties.getDouble(LibProperties.LBP_CONSTANT));
        setNumberOfHistogramBins(properties.getInteger(LibProperties.LBP_HISTOGRAM_SIZE));
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        final int width = ip.getWidth();
        final int height = ip.getHeight();

        setImageProcessor(ip);

        byte[] mask = m_ip.getMaskArray();
        int  k = 0;
        for (int y = 0; y < height ; y++) {
            for (int x = 0; x < width; x++) {
                if (mask == null || mask[k++] != 0)
                    addData(processPixel(x, y));
            }
            int p = (int) (y / (double) height * 100);
            firePropertyChange(new Progress(p));
        }

        // free memory
        m_ip = null;
        m_offsets = null;

        firePropertyChange(Progress.END);
    }

    protected int getMaxBinaryPattern() {
        return (int) Math.pow(2, m_numPoints);
    }

    protected void setImageProcessor(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ImageProcessor mask = ip.getMask();
            ip = ip.convertToByte(true);
            ip.setMask(mask);
        }
        m_ip = ip;

        calculateOffsets();
    }

    /**
     * Calculates relative offsets in x and y direction of neighors with
     * respect to central pixel.
     */
    protected void calculateOffsets() {
        m_offsets = new double[m_numPoints * 2];
        for (int i = 0; i < m_numPoints; i++) {
            double a = i * m_angle;

            m_offsets[i * 2] = m_radius * Math.cos(a);
            m_offsets[i * 2 + 1] = m_radius * Math.sin(a);
        }
    }

    protected double[] processPixel(final int x, final int y) {
        int xStart = Math.max(x - m_neighborhoodSize, 0);
        int xEnd = Math.min(x + m_neighborhoodSize + 1, m_ip.getWidth());
        int yStart = Math.max(y - m_neighborhoodSize, 0);
        int yEnd = Math.min(y + m_neighborhoodSize + 1, m_ip.getHeight());

        Histogram hist = new Histogram(m_histogramSize, getMaxBinaryPattern());

        // iterate over neighborhood
        for (int yi = yStart; yi < yEnd; yi++) {
            for (int xi = xStart; xi < xEnd; xi++) {
                hist.add(getBinaryPattern(xi, yi));
            }
        }

        return hist.getHistogramm();
    }

    protected int getBinaryPattern(final int x, final int y) {
        final float centerPixel = m_ip.getf(x, y);
        short pattern = 0;
        for (int i = 0; i < m_numPoints; i++) {
            double xi = x + m_offsets[i * 2];
            double yi = y + m_offsets[i * 2 + 1];
            if (xi < 0 || xi >= m_ip.getWidth() || yi < 0 || yi >= m_ip.getHeight())
                return 0;

            double val = m_ip.getInterpolatedPixel(xi, yi);
            if (val > centerPixel + m_constant) {
                pattern |= 1 << i;
            }
        }

        return pattern;
    }

    /**
     * Number of neighbors to consider.
     * <p>
     * All neighbors lie equally spaced on a circle determined by {@link #setRadius(double)}.
     * </p>
     * @param numPoints
     */
    public void setNumPoints(int numPoints) {
        if (numPoints >= 32)
            throw new IllegalArgumentException(
                    "numPoints must be smaller 32, but is " + numPoints);
        m_numPoints = numPoints;

        m_angle = 2.0 * Math.PI / numPoints;
    }

    /**
     * Set the radius of the neighborhood to consider.
     *
     * @param radius in pixels
     * @throws  IllegalArgumentException if <code>radius <= 0</code>
     */
    public void setRadius(double radius) {
        if (radius <= 0)
            throw new IllegalArgumentException(
                    "radius must be bigger than zero, but is " + radius);
        m_radius = radius;
    }

    /**
     * Set the size of the neighborhood that is considered to construct a histogram
     * of binary patterns for each pixel.
     *
     * For instance, a neighborhood size of 1 considers the 8-neighborhood of each
     * pixel, and a neighborhood size of 2 the 25-neighborhood. The neighborhood
     * is always quadractic.
     *
     * @param neighborhoodSize positive number
     */
    public void setNeighborhoodSize(int neighborhoodSize) {
        if (neighborhoodSize <= 0)
            throw new IllegalArgumentException(
                    "neighborhoodSize must be bigger than zero, but is " + neighborhoodSize);
        m_neighborhoodSize = neighborhoodSize;
    }

    /**
     * Constant added to the intensity of the central pixel when comparing it
     * to its neighbors.
     */
    public void setConstant(double offset) {
        m_constant = offset;
    }

    /**
     * Set the number of bins of the LBP histogram of each pixel.
     *
     * @param numBins a positive number
     */
    public void setNumberOfHistogramBins(int numBins) {
         if (numBins <= 0)
            throw new IllegalArgumentException(
                    "numBins must be bigger than zero, but is " + numBins);
        m_histogramSize = numBins;
    }

    public double getRadius() {
        return m_radius;
    }

    public int getMumPoints() {
        return m_numPoints;
    }

    public double getConstant() {
        return m_constant;
    }

    public int getNumberOfHistogramBins() {
        return m_histogramSize;
    }

}
