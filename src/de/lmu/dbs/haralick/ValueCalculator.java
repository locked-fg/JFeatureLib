package de.lmu.dbs.haralick;

import java.awt.image.BufferedImage;

public class ValueCalculator extends Calculator {

    public static final String COLORMOMENT_HSL_MEAN = "colormoment_hsl_mean";
    public static final String COLORMOMENT_YUV_MEAN = "colormoment_yuv_mean";
    public static final String COLORMOMENT_HSV_MEAN = "colormoment_hsv_mean";
    public static final String COLORHISTOGRAM_YUV = "colorhistogram_yuv";
    public static final String COLORHISTOGRAM_HSL = "colorhistogram_hsl";
    public static final String COLORHISTOGRAM_HSV = "colorhistogram_hsv";
    /** The default weight for red samples in the conversion, 0.3f. */
//    private static final double DEFAULT_RED_WEIGHT = 0.3f;
    private static final double DEFAULT_RED_WEIGHT = 1d / 3d;
    /** The default weight for green samples in the conversion, 0.59f. */
//    private static final double DEFAULT_GREEN_WEIGHT = 0.59f;
    private static final double DEFAULT_GREEN_WEIGHT = 1d / 3d;
    /** The default weight for blue samples in the conversion, 0.11f. */
//    private static final double DEFAULT_BLUE_WEIGHT = 0.11f;
    private static final double DEFAULT_BLUE_WEIGHT = 1d / 3d;
    /** The constant for shifting the rgb value for getting the red value. */
    private static final int RED_SHIFT = 16;
    /** The constant for shifting the rgb value for getting the green value. */
    private static final int GREEN_SHIFT = 8;
    /** The constant for shifting the rgb value for getting the blue value. */
    private static final int BLUE_SHIFT = 0;
    /** Contains the color histogram of the image. */
    private double[] colorhist_HSV_SecShell_HS;
    private double[] colorhist_HSL_SecShell_HS;
    private double[] colorhist_YUV_SecShell_YU;
    /** Contains the hsv values of the image. */
    private float[][] hsvValues;
    /** Contains the hsl values of the image. */
    private float[][] hslValues;
    /** Contains the yuv values of the image. */
    private float[][] yuvValues;
    /** Contains the gray histogram of the image. */
    private double[] grayHistogram;
    /** Contains the quantized gray values of each pixel of the image. */
    private byte[] grayValue;
    /** Contains the gray values of each pixel of the image (in the range [0..1]). */
    private float[] grayPixel;
    /** Contains the mean value of the gray values of the underlying image. */
    private double meanGrayValue;
    /** The cooccurrence matrices for each neighboring distance value and for the
     * different orientations and one summarized orientation. */
    private double[][][] cooccurrenceMatrices = new double[DISTANCES.length][][];
    /** Contains the sum of the entries of each cooccurrence matrix. */
    private double[] cooccurrenceSums = new double[DISTANCES.length];
    /** The value for one increment in the gray/color histograms. */
    private double histogramIncrement;
    private boolean notEmpty;
    private double[] meanHSL;
    private double[] meanHSV;
    private double[] meanYUV;
    private boolean has_calculated;

    public ValueCalculator(BufferedImage image) {
        super(image);

        // set the value for one increment in the gray/color histograms
        this.histogramIncrement = 1.0f / this.imageSize;

        // init cooccurrence matrices
        for (int d = 0; d < DISTANCES.length; d++) {
            cooccurrenceMatrices[d] = new double[NUM_GRAY_VALUES][NUM_GRAY_VALUES];
        }

        this.has_calculated = false;
    }

    public float[][] getHsvValues() {
        return this.hsvValues;
    }

    public float[][] getHslValues() {
        return this.hslValues;
    }

    public float[][] getYuvValues() {
        return this.yuvValues;
    }

    public float[] getGrayPixels() {
        return this.grayPixel;
    }

    public double[] getGrayHistogram() {
        return this.grayHistogram;
    }

    public double getMeanGrayValue() {
        return this.meanGrayValue;
    }

    public double[][][] getCooccurrenceMatrices() {
        return this.cooccurrenceMatrices;
    }

    public double[] getCooccurenceSums() {
        return this.cooccurrenceSums;
    }

    private void initValues() {
        this.meanHSL = new double[3];
        this.meanHSV = new double[3];
        this.meanYUV = new double[3];

        this.colorhist_HSV_SecShell_HS = new double[HS_H_RANGES * HS_S_RANGES];
        this.colorhist_HSL_SecShell_HS = new double[HS_H_RANGES * HS_S_RANGES];
        this.colorhist_YUV_SecShell_YU = new double[UV_U_RANGES * UV_V_RANGES];

        // init the arrays for the hsv, hsl and yuv values
        this.hsvValues = new float[imageSize][];
        this.hslValues = new float[imageSize][];
        this.yuvValues = new float[imageSize][];

        // init the arrays for the gray values, gray pixels
        this.grayValue = new byte[imageSize];
        this.grayPixel = new float[imageSize];

        this.grayHistogram = new double[GRAY_RANGES];
        this.meanGrayValue = 0;
    }

    /**
     * Returns true if the image is empty, false otherwise.
     *
     * @return true if the image is empty, false otherwise
     */
    public boolean isEmpty() {
        return !notEmpty;
    }

    @Override
    public void calculate() {
        if (this.has_calculated) {
            return;
        }

        initValues();

        // image is not empty per default
        notEmpty = false;
        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
                int pos = this.imageWidth * y + x;

                // for each pixel
                int rgb = this.image.getRGB(x, y);
                int r = (rgb >> RED_SHIFT) & 0xff;
                int g = (rgb >> GREEN_SHIFT) & 0xff;
                int b = (rgb >> BLUE_SHIFT) & 0xff;

                // check if image is empty
                if (!notEmpty && (r > 0 || g > 0 || b > 0)) {
                    notEmpty = true;
                }

                // HSV color space
                calculateHsvValue(pos, r, g, b);

                // HSL color space
                calculateHslValue(pos, r, g, b);

                // YUV color space
                calculateYuvValue(pos, r, g, b);

                // determine gray value [0..255]
                calculateGreyValues(pos, r, g, b);

                for (int k = 0; k < DISTANCES.length; k++) {
                    int d = DISTANCES[k];

                    // horizontal neighbor: 0 degrees
                    int i = x - d;
                    int j = y;
                    if (!(i < 0)) {
                        increment(grayValue[pos], grayValue[pos - d], k, cooccurrenceMatrices, cooccurrenceSums);
                    }

                    // vertical neighbor: 90 degree
                    i = x;
                    j = y - d;
                    if (!(j < 0)) {
                        increment(grayValue[pos], grayValue[pos - d * this.imageWidth], k, cooccurrenceMatrices, cooccurrenceSums);
                    }

                    // 45 degree diagonal neigbor
                    i = x + d;
                    j = y - d;
                    if (i < this.imageWidth && !(j < 0)) {
                        increment(grayValue[pos], grayValue[pos + d - d * this.imageWidth], k, cooccurrenceMatrices, cooccurrenceSums);
                    }

                    // 135 vertical neighbor
                    i = x - d;
                    j = y - d;
                    if (!(i < 0) && !(j < 0)) {
                        increment(grayValue[pos], grayValue[pos - d - d * this.imageWidth], k, cooccurrenceMatrices, cooccurrenceSums);
                    }
                }
            }
        }

        double normsize = 1.0 / (double) (imageSize);

        for (int i = 0; i < 3; i++) {
            meanHSV[i] *= normsize;
            meanHSL[i] *= normsize;
            meanYUV[i] *= normsize;
        }
        this.addDescriptorInfo(COLORMOMENT_HSV_MEAN, meanHSV);
        this.addDescriptorInfo(COLORMOMENT_HSL_MEAN, meanHSL);
        this.addDescriptorInfo(COLORMOMENT_YUV_MEAN, meanYUV);

        this.addDescriptorInfo(COLORHISTOGRAM_HSV, colorhist_HSV_SecShell_HS);
        this.addDescriptorInfo(COLORHISTOGRAM_HSL, colorhist_HSL_SecShell_HS);
        this.addDescriptorInfo(COLORHISTOGRAM_YUV, colorhist_YUV_SecShell_YU);

        this.has_calculated = true;
    }

    private void calculateGreyValues(int pos, int r, int g, int b) {
        double gray = (r * DEFAULT_RED_WEIGHT + g * DEFAULT_GREEN_WEIGHT + b * DEFAULT_BLUE_WEIGHT) / (DEFAULT_RED_WEIGHT + DEFAULT_GREEN_WEIGHT + DEFAULT_BLUE_WEIGHT);
//        double gray = (r + g + b) / 3.0;  // unweighted conversion
        grayValue[pos] = (byte) (gray / GRAY_SCALE);  // quantized for texture analysis
        grayPixel[pos] = (float) (gray / 255.0);  // full resolution for gradient analysis

        // check gray value
        if (grayValue[pos] >= NUM_GRAY_VALUES) {
            throw new RuntimeException("Should never happen!");
        }

        // gray histogram entry
        int gindex = (int) gray;
        if (gindex >= GRAY_RANGES) {
            gindex = GRAY_RANGES - 1; // should never happen
        }
        grayHistogram[gindex] += histogramIncrement;

        // update mean
        meanGrayValue += grayValue[pos];
    }

    private void calculateYuvValue(int pos, int r, int g, int b) {
        yuvValues[pos] = RGBtoYUV(r, g, b);

        int yindex = (int) (UV_U_RANGES * yuvValues[pos][1]); // chroma u
        if (yindex >= UV_U_RANGES) {
            yindex = UV_U_RANGES - 1;
        }
        int uindex = (int) (UV_V_RANGES * yuvValues[pos][2]);  // chroma v
        if (uindex >= UV_U_RANGES) {
            uindex = UV_U_RANGES - 1;
        }
        colorhist_YUV_SecShell_YU[yindex + uindex * UV_U_RANGES] += histogramIncrement;  // square model for the colors

        for (int i = 0; i < 3; i++) {
            meanYUV[i] += yuvValues[pos][i];
        }
    }

    private void calculateHslValue(int pos, int r, int g, int b) {
        hslValues[pos] = RGBtoHSL(r, g, b);

        int hindex = (int) (HS_H_RANGES * hslValues[pos][0]); // hue
        if (hindex >= HS_H_RANGES) {
            hindex = HS_H_RANGES - 1;
        }
        int sindex = (int) (HS_S_RANGES * hslValues[pos][1]);  // saturation
        if (sindex >= HS_S_RANGES) {
            sindex = HS_S_RANGES - 1;
        }
        colorhist_HSL_SecShell_HS[hindex + sindex * HS_H_RANGES] += histogramIncrement;  // sector/shell model for the colors

        for (int i = 0; i < 3; i++) {
            meanHSL[i] += hslValues[pos][i];
        }
    }

    private void calculateHsvValue(int pos, int r, int g, int b) {
        hsvValues[pos] = RGBtoHSV(r, g, b);

        int hindex = (int) (HS_H_RANGES * hsvValues[pos][0]); // hue
        if (hindex >= HS_H_RANGES) {
            hindex = HS_H_RANGES - 1;
        }
        int sindex = (int) (HS_S_RANGES * hsvValues[pos][1]);  // saturation
        if (sindex >= HS_S_RANGES) {
            sindex = HS_S_RANGES - 1;
        }
        colorhist_HSV_SecShell_HS[hindex + sindex * HS_H_RANGES] += histogramIncrement;  // sector/shell model for the colors

//        correloHistogram[hindex + sindex*H_RANGES].add(new ImagePoint(x,y));

        for (int i = 0; i < 3; i++) {
            meanHSV[i] += hsvValues[pos][i];
        }
    }

    /**
     * Incremets the specified coocurrence matrix and the summarized coocurrence matrix
     * of the specified distance value d at the specified positions (g1,g2) and (g2,g1).
     *
     * @param g1 the gray value of the first pixel
     * @param g2 the gray value of the second pixel
     * @param d  the index of the distance value specifiying the coocurrence matrix
     * @param cooc  the coocurrence matrix array
     * @param sums  the coocurrence sum of entries matrix array
     */
    private static void increment(int g1, int g2, int d, double[][][] cooc, double[] sums) {
        cooc[d][g1][g2] += 1;
        cooc[d][g2][g1] += 1;
        sums[d] += 2;
    }

    /**
     * Transforms the specified rgb value to the corresponding hsv (Hue Saturation Value) value.
     *
     * @param r the r value
     * @param g the g value
     * @param b the b value
     * @param hsl the values for the specified rgb value (normalized to the range [0..1])
     */
    private static float[] RGBtoHSL(int r, int g, int b) {
        float min = min(r, g, b);
        float max = max(r, g, b);
        float[] hsl = new float[3];

        if (max == min) {
            // h is undefined
            hsl[0] = 0;
        } else if (r == max) {
            hsl[0] = ((g - b) / (max - min)) * 60;  // between yellow & magenta
        } else if (g == max) {
            hsl[0] = (2 + (b - r) / (max - min)) * 60;  // between cyan & yellow
        } else if (b == max) {
            hsl[0] = (4 + (r - g) / (max - min)) * 60;  // between magenta & cyan
        }
        if (hsl[0] < 0) {
            hsl[0] += 360;
        }
        hsl[0] /= 360;  // normalize

        if (max == min) {
            // s is undefined
            hsl[1] = 0;
        } else {
            if ((max + min) <= 255) {
                hsl[1] = (max - min) / (max + min); // s
            } else {
                hsl[1] = (max - min) / (2 * 255 - (max + min)); // s
            }
        }

        hsl[2] = (max + min) / (2 * 255); // l

        return hsl;
    }

    /**
     * Transforms the specified rgb value to the corresponding hsv (Hue Saturation Value) value.
     *
     * @param r the r value
     * @param g the g value
     * @param b the b value
     * @param hsv the values for the specified rgb value (normalized to the range [0..1])
     */
    private static float[] RGBtoHSV(int r, int g, int b) {
        float min = min(r, g, b);
        float max = max(r, g, b);
        float[] hsv = new float[3];

        // h value
        if (max == min) {
            hsv[0] = 0;
        } else if (r == max) {
            hsv[0] = ((g - b) / (max - min)) * 60;  // between yellow & magenta
        } else if (g == max) {
            hsv[0] = (2 + (b - r) / (max - min)) * 60;  // between cyan & yellow
        } else if (b == max) {
            hsv[0] = (4 + (r - g) / (max - min)) * 60;  // between magenta & cyan
        }
        if (hsv[0] < 0) {
            hsv[0] += 360;
        }
        hsv[0] /= 360;  // normalize

        // s value
        if (max == 0) {
            hsv[1] = 0;
        } else {
            hsv[1] = (max - min) / max;
        }

        // v value
        hsv[2] = max / 255; // normalize

        return hsv;
    }

    /**
     * Transforms the specified rgb value to the corresponding yuv (Chrominance/Luminance) value.
     *
     * @param r the r value
     * @param g the g value
     * @param b the b value
     * @param yuv the values for the specified rgb value (normalized to the range [0..1])
     */
    private static float[] RGBtoYUV(int r, int g, int b) {
//    yuv[0] = (( 0.299f*r + 0.587f*g + 0.114f*b) / 255);
//    yuv[1] = ((-0.147f*r - 0.289f*g + 0.436f*b) / 255)*(0.5f/0.436f) + 0.5f;  // normalize
//    yuv[2] = (( 0.615f*r - 0.515f*g - 0.100f*b) / 255)*(0.5f/0.615f) + 0.5f;  // normalize
        float[] yuv = new float[3];

        // integer version of the lines above
        yuv[0] = (float) (((1254097 * r + 2462056 * g + 478151 * b) / 255)) / (float) 4194304;
        yuv[1] = (float) (((-707067 * r - 1390085 * g + 2097152 * b) / 255) + 2097152) / (float) 4194304; // normalize
        yuv[2] = (float) (((2097152 * r - 1756152 * g - 341000 * b) / 255) + 2097152) / (float) 4194304; // normalize

        return yuv;
    }

    /**
     * Returns the maximum of the three specified double values.
     *
     * @param r first value
     * @param g secon value
     * @param b third value
     * @return the maximum of the three specified double values
     */
    private static int max(int r, int g, int b) {
        return Math.max(r, Math.max(g, b));
    }

    /**
     * Returns the minimum of the three specified double values.
     *
     * @param r first value
     * @param g secon value
     * @param b third value
     * @return the minimum of the three specified double values
     */
    private static int min(int r, int g, int b) {
        return Math.min(r, Math.min(g, b));
    }
}
