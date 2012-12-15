/**
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

/**
 * A histogram class for counting occurences of values.
 *
 * Each bin of the histogram stores a counter for the domain [min of bin, max of
 * bin[.
 *
 * So if you create a Histogram with <tt>new Histogram(2,2)</tt> from 0 to 2
 * with two bins, you will get an error if you want to call <tt>add(2)</tt> as
 * the domains are [0;1[, [1;2[.
 *
 * @author graf
 */
public class Histogram {

    /**
     * the actual histogram
     */
    final double[] histogramm;
    final double binWidth, minValue, maxValue;

    /**
     * create a histogram with the specified amount of bins with the min value
     * set to zero
     *
     * @param bins amount of bins (>0)
     * @param maxValue max value (>minValue)
     */
    public Histogram(int bins, double maxValue) {
        this(bins, 0, maxValue);
    }

    /**
     * Create a histogram with the specified amount of bins with the min value
     * set to zero
     *
     * @param bins amount of bins (>0)
     * @param maxValue max value (>minValue)
     * @throws IllegalArgumentException if bins &lt;= 0 or max &lt;=min
     */
    public Histogram(int bins, double minValue, double maxValue) {
        if (bins <= 0) {
            throw new IllegalArgumentException("bins must be > 0 but was: " + bins);
        }
        if (maxValue <= minValue) {
            throw new IllegalArgumentException("maxValue must be > minValue: " + minValue + "|" + maxValue);
        }

        this.histogramm = new double[bins];
        this.binWidth = (maxValue - minValue) / bins;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Raise the counter for the specified value by 1.
     *
     * @param value
     */
    public void add(double value) {
        int bin = (int) ((value - minValue) / binWidth);
        if (bin > histogramm.length) { // ???
            bin--;
        }
        histogramm[bin]++;
    }

    /**
     * Raise the counter vor the value by the amount denoted by increase.
     *
     * @param value
     * @param increase
     */
    public void add(double value, double increase) {
        int bin = (int) ((value - minValue) / binWidth);
        if (bin > histogramm.length) {
            bin--;
        }
        histogramm[bin] += increase;
    }

    @Override
    public String toString() {
        String[] s = new String[histogramm.length];
        for (int i = 0; i < histogramm.length; i++) {
            s[i] = String.format("%.2f", histogramm[i]);
        }
        String description = "Histogramm (min: " + minValue + " - max: " + maxValue + " - bins: " + histogramm.length + " - width: " + binWidth + ")";
        return description + "\n" + "[" + Arrays2.join(s, ";") + "]";
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    /**
     * returns a reference(!) to the histogram array
     *
     * @return histogram array
     */
    public double[] getHistogramm() {
        return histogramm;
    }

    public double getBinWidth() {
        return binWidth;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }
    //</editor-fold>
}
