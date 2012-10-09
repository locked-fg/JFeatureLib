package de.lmu.dbs.jfeaturelib.utils;

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
