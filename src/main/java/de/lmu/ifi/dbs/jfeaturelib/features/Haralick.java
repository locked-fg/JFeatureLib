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
package de.lmu.ifi.dbs.jfeaturelib.features;

import Jama.Matrix;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Haralick texture features
 *
 * http://makseq.com/materials/lib/Articles-Books/Filters/Texture/Co-occurence/haralick73.pdf
 * <pre>
 *
 * @article{haralick1973textural, title={Textural features for image
 * classification}, author={Haralick, R.M. and Shanmugam, K. and Dinstein, I.},
 * journal={Systems, Man and Cybernetics, IEEE Transactions on}, volume={3},
 * number={6}, pages={610--621}, year={1973}, publisher={IEEE} }
 * </pre>
 *
 * The feature descriptor is composed of the following features: <ol> <li>Angular 2nd moment</li> <li>Contrast</li>
 * <li>Correlation</li> <li>variance</li> <li>Inverse Difference Moment</li> <li>Sum Average</li> <li>Sum Variance</li>
 * <li>Sum Entropy</li> <li>Entropy</li> <li>Difference Variance</li> <li>Difference Entropy</li> <li>Information
 * Measures of Correlation</li> <li>Information Measures of Correlation</li> <li>Maximum Correlation COefficient</li>
 * </ol>
 *
 * @author graf
 */
public class Haralick extends AbstractFeatureDescriptor {

    /**
     * The number of gray values for the textures
     */
    private final int NUM_GRAY_VALUES = 32;
    /**
     * p_(x+y) statistics
     */
    private double[] p_x_plus_y = new double[2 * NUM_GRAY_VALUES - 1];
    /**
     * p_(x-y) statistics
     */
    private double[] p_x_minus_y = new double[NUM_GRAY_VALUES];
    /**
     * row mean value
     */
    private double mu_x = 0;
    /**
     * column mean value
     */
    private double mu_y = 0;
    /**
     * row variance
     */
    private double var_x = 0;
    /**
     * column variance
     */
    private double var_y = 0;
    /**
     * HXY1 statistics
     */
    private double hx = 0;
    /**
     * HXY2 statistics
     */
    private double hy = 0;
    /**
     * HXY1 statistics
     */
    private double hxy1 = 0;
    /**
     * HXY2 statistics
     */
    private double hxy2 = 0;
    /**
     * p_x statistics
     */
    private double[] p_x = new double[NUM_GRAY_VALUES];
    /**
     * p_y statistics
     */
    private double[] p_y = new double[NUM_GRAY_VALUES];
    // -
    private int haralickDist;
    double[] features = null;

    /**
     * Constructs a haralick detector with default parameters.
     */
    public Haralick() {
        this.haralickDist = 1;
    }

    /**
     * Constructs a haralick detector.
     *
     * @param haralickDist Integer for haralick distribution (>= 1)
     */
    public Haralick(int haralickDist) {
        setHaralickDist(haralickDist);
    }

    /**
     * Defines the capability of the algorithm.
     *
     * @see PlugInFilter
     * @see #supports()
     */
    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        return set;
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        setHaralickDist(properties.getInteger(LibProperties.HARALICK_DISTANCE, 1));
    }

    /**
     * Starts the haralick detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        firePropertyChange(Progress.START);
        process((ByteProcessor) ip);
        addData(features);
        firePropertyChange(Progress.END);
    }

    /**
     * Returns information about the getFeature
     */
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Haralick features: ");
        sb.append("Angular 2nd moment, ");
        sb.append("Contrast, ");
        sb.append("Correlation, ");
        sb.append("variance, ");
        sb.append("Inverse Difference Moment, ");
        sb.append("Sum Average, ");
        sb.append("Sum Variance, ");
        sb.append("Sum Entropy, ");
        sb.append("Entropy, ");
        sb.append("Difference Variance, ");
        sb.append("Difference Entropy, ");
        sb.append("Information Measures of Correlation, ");
        sb.append("Information Measures of Correlation, ");
        sb.append("Maximum Correlation COefficient");
        return sb.toString();
    }

    private void process(ByteProcessor image) {
        features = new double[14];

        firePropertyChange(new Progress(1, "creating coocurrence matrix"));
        Coocurrence coocurrence = new Coocurrence(image, NUM_GRAY_VALUES, this.haralickDist);
        double[][] cooccurrenceMatrix = coocurrence.getCooccurrenceMatrix();
        double meanGrayValue = coocurrence.getMeanGrayValue();

        firePropertyChange(new Progress(25, "normalizing"));
        normalize(cooccurrenceMatrix, coocurrence.getCooccurenceSums());

        firePropertyChange(new Progress(50, "computing statistics"));
        calculateStatistics(cooccurrenceMatrix);

        firePropertyChange(new Progress(75, "computing features"));

        double[][] p = cooccurrenceMatrix;
        double[][] Q = new double[NUM_GRAY_VALUES][NUM_GRAY_VALUES];
        for (int i = 0; i < NUM_GRAY_VALUES; i++) {
            double sum_j_p_x_minus_y = 0;
            for (int j = 0; j < NUM_GRAY_VALUES; j++) {
                double p_ij = p[i][j];

                sum_j_p_x_minus_y += j * p_x_minus_y[j];

                features[0] += p_ij * p_ij;
                features[2] += i * j * p_ij - mu_x * mu_y;
                features[3] += (i - meanGrayValue) * (i - meanGrayValue) * p_ij;
                features[4] += p_ij / (1 + (i - j) * (i - j));
                features[8] += p_ij * log(p_ij);

                // feature 13
                if (p_ij != 0 && p_x[i] != 0) { // would result in 0
                    for (int k = 0; k < NUM_GRAY_VALUES; k++) {
                        if (p_y[k] != 0 && p[j][k] != 0) { // would result in NaN
                            Q[i][j] += (p_ij * p[j][k]) / (p_x[i] * p_y[k]);
                        }
                    }
                }
            }

            features[1] += i * i * p_x_minus_y[i];
            features[9] += (i - sum_j_p_x_minus_y) * (i - sum_j_p_x_minus_y) * p_x_minus_y[i];
            features[10] += p_x_minus_y[i] * log(p_x_minus_y[i]);
        }


        // feature 13: Max Correlation Coefficient
        double[] realEigenvaluesOfQ = new Matrix(Q).eig().getRealEigenvalues();
        Arrays2.abs(realEigenvaluesOfQ);
        Arrays.sort(realEigenvaluesOfQ);
        features[13] = Math.sqrt(realEigenvaluesOfQ[realEigenvaluesOfQ.length - 2]);

        features[2] /= Math.sqrt(var_x * var_y);
        features[8] *= -1;
        features[10] *= -1;
        double maxhxhy = Math.max(hx, hy);
        if (Math.signum(maxhxhy) == 0) {
            features[11] = 0;
        } else {
            features[11] = (features[8] - hxy1) / maxhxhy;
        }
        features[12] = Math.sqrt(1 - Math.exp(-2 * (hxy2 - features[8])));

        for (int i = 0; i < 2 * NUM_GRAY_VALUES - 1; i++) {
            features[5] += i * p_x_plus_y[i];
            features[7] += p_x_plus_y[i] * log(p_x_plus_y[i]);

            double sum_j_p_x_plus_y = 0;
            for (int j = 0; j < 2 * NUM_GRAY_VALUES - 1; j++) {
                sum_j_p_x_plus_y += j * p_x_plus_y[j];
            }
            features[6] += (i - sum_j_p_x_plus_y) * (i - sum_j_p_x_plus_y) * p_x_plus_y[i];
        }

        features[7] *= -1;

    }

    /**
     * Calculates the statistical properties.
     */
    private void calculateStatistics(double[][] cooccurrenceMatrix) {
        // p_x, p_y, p_x+y, p_x-y
        for (int i = 0; i < NUM_GRAY_VALUES; i++) {
            for (int j = 0; j < NUM_GRAY_VALUES; j++) {
                double p_ij = cooccurrenceMatrix[i][j];

                p_x[i] += p_ij;
                p_y[j] += p_ij;

                p_x_plus_y[i + j] += p_ij;
                p_x_minus_y[Math.abs(i - j)] += p_ij;
            }
        }

        // mean and variance values
        double[] meanVar;
        meanVar = meanVar(p_x);
        mu_x = meanVar[0];
        var_x = meanVar[1];
        meanVar = meanVar(p_y);
        mu_y = meanVar[0];
        var_y = meanVar[1];

        for (int i = 0; i < NUM_GRAY_VALUES; i++) {
            // hx and hy
            hx += p_x[i] * log(p_x[i]);
            hy += p_y[i] * log(p_y[i]);

            // hxy1 and hxy2
            for (int j = 0; j < NUM_GRAY_VALUES; j++) {
                double p_ij = cooccurrenceMatrix[i][j];
                hxy1 += p_ij * log(p_x[i] * p_y[j]);
                hxy2 += p_x[i] * p_y[j] * log(p_x[i] * p_y[j]);
            }
        }
        hx *= -1;
        hy *= -1;
        hxy1 *= -1;
        hxy2 *= -1;
    }

    /**
     * Compute mean and variance of the given array
     *
     * @param a inut values
     * @return array{mean, variance}
     */
    private double[] meanVar(double[] a) {
        // VAR(X) = E(X^2) - E(X)^2
        double ex = 0, ex2 = 0; // E(X), E(X^2)
        for (int i = 0; i < NUM_GRAY_VALUES; i++) {
            ex += a[i];
            ex2 += a[i] * a[i];
        }
        ex /= a.length;
        ex2 /= a.length;
        double var = ex2 - ex * ex;

        return new double[]{ex, var};
    }

    /**
     * Returns the bound logarithm of the specified value.
     *
     * If Math.log would be Double.NEGATIVE_INFINITY, 0 is returned
     *
     * @param value the value for which the logarithm should be returned
     * @return the logarithm of the specified value
     */
    private double log(double value) {
        double log = Math.log(value);
        if (log == Double.NEGATIVE_INFINITY) {
            log = 0;
        }
        return log;
    }

    /**
     * Normalizes the array by the given sum. by dividing each 2nd dimension array componentwise by the sum.
     *
     * @param A
     * @param sum
     */
    private void normalize(double[][] A, double sum) {
        for (int i = 0; i < A.length; i++) {
            Arrays2.div(A[i], sum);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getter/Setter">
    /**
     * Getter for haralick distributions
     *
     * @return haralick distributions
     */
    public int getHaralickDist() {
        return haralickDist;
    }

    /**
     * Setter for haralick distributions
     *
     * @param haralickDist int for haralick distributions (must be >= 1)
     */
    public void setHaralickDist(int haralickDist) {
        if (haralickDist <= 0) {
            throw new IllegalArgumentException("the distance for haralick must be >= 1 but was " + haralickDist);
        }
        this.haralickDist = haralickDist;
    }
    //</editor-fold>
}

//<editor-fold defaultstate="collapsed" desc="Coocurrence Matrix">
/**
 * http://makseq.com/materials/lib/Articles-Books/Filters/Texture/Co-occurence/haralick73.pdf
 */
class Coocurrence {

    /**
     * The number of gray values for the textures
     */
    private final int NUM_GRAY_VALUES;
    /**
     * The number of gray levels in an image
     */
    private final int GRAY_RANGES = 256;
    /**
     * The scale for the gray values for conversion rgb to gray values.
     */
    private final double GRAY_SCALE;
    /**
     * gray histogram of the image.
     */
    private final double[] grayHistogram;
    /**
     * quantized gray values of each pixel of the image.
     */
    private final byte[] grayValue;
    /**
     * mean gray value
     */
    private double meanGrayValue = 0;
    /**
     * The cooccurrence matrix
     */
    private final double[][] cooccurrenceMatrices;
    /**
     * The value for one increment in the gray/color histograms.
     */
    private final int HARALICK_DIST;
    private final ByteProcessor image;

    public Coocurrence(ByteProcessor b, int numGrayValues, int haralickDist) {
        this.NUM_GRAY_VALUES = numGrayValues;
        this.image = b;
        this.GRAY_SCALE = (double) GRAY_RANGES / (double) NUM_GRAY_VALUES;
        this.cooccurrenceMatrices = new double[NUM_GRAY_VALUES][NUM_GRAY_VALUES];
        this.grayValue = new byte[image.getPixelCount()];
        this.grayHistogram = new double[GRAY_RANGES];
        this.HARALICK_DIST = haralickDist;
        calculate();
    }

    public double getMeanGrayValue() {
        return this.meanGrayValue;
    }

    public double[][] getCooccurrenceMatrix() {
        return this.cooccurrenceMatrices;
    }

    public double getCooccurenceSums() {
        return image.getPixelCount() * 8;
    }

    private void calculate() {
        calculateGreyValues();

        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final int d = HARALICK_DIST;
        int i, j, pos;

        // image is not empty per default
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                pos = imageWidth * y + x;

                // horizontal neighbor: 0 degrees
                i = x - d;
//                j = y;
                if (!(i < 0)) {
                    increment(grayValue[pos], grayValue[pos - d]);
                }

                // vertical neighbor: 90 degree
//                i = x;
                j = y - d;
                if (!(j < 0)) {
                    increment(grayValue[pos], grayValue[pos - d * imageWidth]);
                }

                // 45 degree diagonal neigbor
                i = x + d;
                j = y - d;
                if (i < imageWidth && !(j < 0)) {
                    increment(grayValue[pos], grayValue[pos + d - d * imageWidth]);
                }

                // 135 vertical neighbor
                i = x - d;
                j = y - d;
                if (!(i < 0) && !(j < 0)) {
                    increment(grayValue[pos], grayValue[pos - d - d * imageWidth]);
                }
            }
        }
        meanGrayValue = Arrays2.sum(grayValue);
    }

    private void calculateGreyValues() {
        int size = image.getPixelCount();
        int gray;
        for (int pos = 0; pos < size; pos++) {
            gray = image.get(pos);
            grayValue[pos] = (byte) (gray / GRAY_SCALE);  // quantized for texture analysis
            grayHistogram[gray]++;
        }
        Arrays2.div(grayHistogram, size);
    }

    /**
     * Incremets the coocurrence matrix at the specified positions (g1,g2) and (g2,g1).
     *
     * @param g1 the gray value of the first pixel
     * @param g2 the gray value of the second pixel
     */
    private void increment(int g1, int g2) {
        cooccurrenceMatrices[g1][g2]++;
        cooccurrenceMatrices[g2][g1]++;
    }
}
//</editor-fold>
