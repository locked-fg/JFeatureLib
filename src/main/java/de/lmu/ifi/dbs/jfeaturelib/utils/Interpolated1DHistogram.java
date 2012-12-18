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
package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.utilities.Arrays2;
import java.util.Arrays;

/**
 * The
 * <code>Interpolated1DHistogram</code> class is used to create interpolates
 * histograms for a certain range of values ([min,max[) with a defined range of
 * bins. Values that are added to the histogram are interpolated to ne nearest
 * neighboring bin according to the distance of the value to the center of the
 * bin. If the according neighbor is out of the range of the histogram, then the
 * according interpolated value is skipped silently and the value of one bin is
 * raised only:
 *
 * <pre>
 * Interpolated1DHistogram ih = new Interpolated1DHistogram(0,3,3);
 * ih.add(0);    // histogram: 0.5, 0, 0
 * ih.add(1.5);  // histogram: 0.5, 1, 0
 * </pre>
 *
 * @author graf
 * @since 11/4/2011
 */
public class Interpolated1DHistogram {

    /**
     * inclusive minimum value of the histogram range
     */
    final double min;
    /**
     * exclusive maximum value of the histogram range
     */
    final double max;
    /**
     * The actual histogram data values.
     */
    final double[] bins;
    /**
     * the lower bounds (= left borders) of each bin
     */
    final double[] lowerKey;
    /**
     * width of a histogram bin
     */
    final double binWidth;

    /**
     * Initializes the histogram with user defined min, max and count values
     *
     * @param min inclusive lower bound
     * @param max exclusive upper bound
     * @param binCount amount of bins between min and max
     */
    public Interpolated1DHistogram(double min, double max, int binCount) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be > max. But was " + min + " / " + max);
        }
        this.min = min;
        this.max = max;
        this.binWidth = (this.max - this.min) / binCount;

        this.lowerKey = new double[binCount];
        this.bins = new double[binCount];
        for (int i = 0; i < binCount; i++) {
            lowerKey[i] = min + i * binWidth;
        }
    }

    /**
     * Raises the histogram at the specivied key position by 1 (might decrease
     * due to interpolation).
     *
     * @param pos in the range of [min, max[
     */
    public void add(double pos) {
        add(pos, 1);
    }

    /**
     * Raises the histogram at the specivied key position by
     * <code>value</code> (might decrease due to interpolation).
     *
     * @param pos in the range of [min, max[
     * @param value
     */
    public void add(double pos, double value) {
        if (pos < min || pos >= max || Double.isNaN(pos)) {
            throw new IllegalArgumentException("value must be in [" + min + ", " + max + "[ but was: " + pos);
        }

        final int index = getBinFor(pos);
        final double mid = lowerKey[index] + (0.5 * binWidth);

        // direct hit (unlikely with doubles)
        if (pos - mid == 0) {
            bins[index] += value;

        } // right of mid, interpolate to following bin
        else if (pos > mid) {
            double weight = (pos - mid) / binWidth;
            bins[index] += (1 - weight) * value;

            // interpolate to right bin
            if (index + 1 < bins.length) {
                bins[index + 1] += weight * value;
            }
        } // value left of mid, interpolate to previous bin
        else if (pos < mid) {
            double weight = (mid - pos) / binWidth;
            bins[index] += (1 - weight) * value;

            // interpolate to left bin
            if (index - 1 >= 0) {
                bins[index - 1] += weight * value;
            }
        }
    }

    /**
     * Returns a reference(!) to the data array.
     *
     * @return the data array
     */
    public double[] getData() {
        return bins;
    }

    /**
     * Resets the data array by filling it with zero.
     */
    public void clear() {
        Arrays.fill(bins, 0);
    }

    /**
     * Returns the array index for the given value.
     *
     * As this is not a public method, the input and outputs are NOT checked for
     * ArrayIndexOutOfBounds!
     *
     * <pre>
     * i = new Interpolated1DHistogram(0,10,10);
     * i.getBinFor(0.1); // returns 0
     * </pre>
     *
     * @param pos
     * @return array index
     */
    int getBinFor(double pos) {
        assert (pos <= max) : "pos > max: " + pos + " > " + max;

        int index = Arrays.binarySearch(lowerKey, pos);
        if (index >= 0) {
            return index;
        } else {
            return -index - 2;
        }
    }

    /**
     * Returns a string representation of the histogram.
     *
     * The string is a comma separated concatenation of the values
     *
     * @return string representation of the ata array
     */
    @Override
    public String toString() {
        return "[" + Arrays2.join(bins, ", ", "%7.2f") + "]";
    }
}
