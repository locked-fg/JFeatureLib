package de.lmu.dbs.haralick;


import java.awt.image.BufferedImage;
import java.util.Arrays;

public class RoughnessCalculator extends ValueCalculatorDependency {
	
	public static final String FACET_MIN = "facet_min";
	public static final String FACET_MAX = "facet_max";
	public static final String FACET_MED = "facet_med";
	public static final String FACET_MEAN = "facet_mean";
	public static final String FACET_STDD = "facet_stdd";
	public static final String FACET_SKEW = "facet_skew";
	public static final String FACET_KURT = "facet_kurt";
	public static final String FACET_AREA = "facet_area";
//	public static final String FACET_WHITE = "facet_white";
	
	public static final String ROUGHNESS_RV = "roughness_rv";
	public static final String ROUGHNESS_RP = "roughness_rp";
	public static final String ROUGHNESS_RT = "roughness_rt";
	public static final String ROUGHNESS_RM = "roughness_rm";
	public static final String ROUGHNESS_RA = "roughness_ra";
	public static final String ROUGHNESS_RQ = "roughness_rq";
	public static final String ROUGHNESS_RSK = "roughness_rsk";
	public static final String ROUGHNESS_RKU = "roughness_rku";
	
	public static final String POLAR_MIN = "polar_min";
	public static final String POLAR_MAX = "polar_max";
	public static final String POLAR_MED = "polar_med";
	public static final String POLAR_MEAN = "polar_mean";
	public static final String POLAR_STDD = "polar_stdd";
	public static final String POLAR_SKEW = "polar_skew";
	public static final String POLAR_KURT = "polar_kurt";
//	public static final String POLAR_ANGLES = "polar_angles";
	
	private double[] facet_min;
	private double[] facet_max;
	private double[] facet_med;
	private double[] facet_mean;
	private double[] facet_stdd;
	private double[] facet_skew;
	private double[] facet_kurt;
	private double[] facet_area;
//	private double[] facet_white;
	
	private double[] roughness_rv;
	private double[] roughness_rp;
	private double[] roughness_rt;
	private double[] roughness_rm;
	private double[] roughness_ra;
	private double[] roughness_rq;
	private double[] roughness_rsk;
	private double[] roughness_rku;
	
	private double[] polar_min;
	private double[] polar_max;
	private double[] polar_med;
	private double[] polar_mean;
	private double[] polar_stdd;
	private double[] polar_skew;
	private double[] polar_kurt;
//	private double[] polar_angles;
	
	public RoughnessCalculator(ValueCalculator val_calc) {
		super(val_calc);
	}
	
	public RoughnessCalculator(BufferedImage image) {
		super(image);
	}

	@Override
	public void calculate() {
		this.value_calc.calculate();
    	this.initValues();
    	
		float[] pixels = this.value_calc.getGrayPixels();
		calculateAll(pixels, false, false, 0, 5.0, 0.55, 0);
        calculateAll(pixels, false, true, 0, 5.0, 0.25, 1);
        calculateAll(pixels, false, true, 1, 5.0, 0.25, 2);
        calculateAll(pixels, true, false, 0, 5.0, 0.55, 3);
        calculateAll(pixels, true, true, 0, 5.0, 0.35, 4);
        calculateAll(pixels, true, true, 1, 5.0, 0.35, 5);
        
        this.addAllDescriptorInfos();
	}
	
	private void initValues() {
    	facet_min = new double[6];
        facet_max = new double[6];
        facet_med = new double[6];
        facet_mean = new double[6];
        facet_stdd = new double[6];
        facet_skew = new double[6];
        facet_kurt = new double[6];
        facet_area = new double[6];
//        facet_white = new double[6];
        
        roughness_rv = new double[6];
        roughness_rp = new double[6];
        roughness_rt = new double[6];
        roughness_rm = new double[6];
        roughness_ra = new double[6];
        roughness_rq = new double[6];
        roughness_rsk = new double[6];
        roughness_rku = new double[6];
        
        polar_min = new double[6];
        polar_max = new double[6];
        polar_med = new double[6];
        polar_mean = new double[6];
        polar_stdd = new double[6];
        polar_skew = new double[6];
        polar_kurt = new double[6];
//        polar_angles = new double[36];
	}
	
	private void addAllDescriptorInfos() {
		this.addDescriptorInfo(FACET_MIN, facet_min);
		this.addDescriptorInfo(FACET_MAX, facet_max);
		this.addDescriptorInfo(FACET_MED, facet_med);
		this.addDescriptorInfo(FACET_MEAN, facet_mean);
		this.addDescriptorInfo(FACET_STDD, facet_stdd);
		this.addDescriptorInfo(FACET_SKEW, facet_skew);
		this.addDescriptorInfo(FACET_KURT, facet_kurt);
		this.addDescriptorInfo(FACET_AREA, facet_area);
//		this.addDescriptorInfo(FACET_WHITE, facet_white);
		
		this.addDescriptorInfo(POLAR_MIN, polar_min);
		this.addDescriptorInfo(POLAR_MAX, polar_max);
		this.addDescriptorInfo(POLAR_MED, polar_med);
		this.addDescriptorInfo(POLAR_MEAN, polar_mean);
		this.addDescriptorInfo(POLAR_STDD, polar_stdd);
		this.addDescriptorInfo(POLAR_SKEW, polar_skew);
		this.addDescriptorInfo(POLAR_KURT, polar_kurt);
//		this.addDescriptorInfo(POLAR_ANGLES polar_angles);
		
		this.addDescriptorInfo(ROUGHNESS_RV, roughness_rv);
		this.addDescriptorInfo(ROUGHNESS_RP, roughness_rp);
		this.addDescriptorInfo(ROUGHNESS_RT, roughness_rt);
		this.addDescriptorInfo(ROUGHNESS_RM, roughness_rm);
		this.addDescriptorInfo(ROUGHNESS_RA, roughness_ra);
		this.addDescriptorInfo(ROUGHNESS_RQ, roughness_rq);
		this.addDescriptorInfo(ROUGHNESS_RSK, roughness_rsk);
		this.addDescriptorInfo(ROUGHNESS_RKU, roughness_rku);
	}
	
	/*
     * ROUGHNESS STATISTICS
     * Measure R-values on the whole image, gives roughness values according
     * to the ISO 4287/2000 standard:
     *  Rv: Lowest valley
     *  Rp: Highest peak
     *  Rt: The total height of the profile
     *  Ra: Arithmetical mean deviation
     *  Rq: Root mean square deviation
     *  Rsk: Skewness of the assessed profile
     *  Rku: Kurtosis of the assessed profile
     *
     * levelSurf (true/false):
     *  Level surface to perform tilt correction.
     *
     * filterSurf (true/false):
     *  Filter the original image with a Gaussian filter having a radius corresponding
     *  to the structure size limit (default 5). The image is then separated into a roughness
     *  and a waviness component.
     *  type 0: the roughness image is given and the R-values calculated.
     *  type 1: the waviness image is given and the R-values calculated.
     *
     * See also:
     *  http://www.gcsca.net/IJ/SurfCharJ.html
     * and
     *  Chinga, G., Gregersen, O., Dougherty, B.,
     *  "Paper surface characterisation by laser profilometry and image analysis",
     *  Journal of Microscopy and Analysis, July 2003.
     *
     */
    private void calculateAll(float[] pixels, boolean levelSurf,
    		boolean filterSurf, int filterType, double lStruct, double polarThreshold, int s) {
    	
        ImageInfo oldInfo = analyze(pixels, this.imageWidth, this.imageHeight);

        float[] pixels2 = this.getPixels(pixels, levelSurf, filterSurf, filterType,
				lStruct, oldInfo);
        
        calculateRoughnessStatistics(s, pixels2);
        
        float[] ipAzimuthal = new float[this.imageWidth * this.imageHeight];
        double sStd = calculateFacetOrientationStatistics(s, pixels2, polarThreshold, ipAzimuthal);
        
        calculatePolarAngleStatistics(s, ipAzimuthal, sStd);
    }

	private float[] getPixels(float[] pixels, boolean levelSurf,
			boolean filterSurf, int filterType, double lStruct,
			ImageInfo oldInfo) {
		float[] pixels2;
        if (levelSurf) {
            pixels2 = levelSurface(pixels, this.imageWidth, this.imageHeight, oldInfo, 4);
        } else {
            pixels2 = pixels;
        }

        if (filterSurf) {
            if (filterType == 0) {
                pixels2 = gaussianFiltering(pixels2, this.imageWidth, this.imageHeight,
                		oldInfo, (lStruct / 2), true);    // Gaussian radius is equal to half the lStruct size
            } else if (filterType == 1) {
                pixels2 = gaussianFiltering(pixels2, this.imageWidth, this.imageHeight,
                		oldInfo, (lStruct / 2), false);
            } else {
                throw new RuntimeException("Illegal argument!");
            }
        }
		return pixels2;
	}

	private void calculatePolarAngleStatistics(int s, float[] ipAzimuthal,
			double sStd) {
		/** The Polar angle statistics of the picture. */
        
        double aMin = Double.MAX_VALUE;
        double aMax = -Double.MAX_VALUE;
        double aSkew = 0, aKurt = 0;
		double aMed = calculateMedian(ipAzimuthal, this.imageWidth, this.imageHeight);
        double aMean = calculateMean(ipAzimuthal, this.imageWidth, this.imageHeight);
        double aStd = calculateStdd(ipAzimuthal, this.imageWidth, this.imageHeight, aMean);

        for (int j = 0; j < this.imageSize; j++) {
            double temp = ipAzimuthal[j];
            double aTemp = temp - aMean;
            if (sStd != 0) {
                aSkew += sqr3(aTemp / aStd);
                aKurt += sqr4(aTemp / aStd);
            }
            aMin = Math.min(aMin, temp);
            aMax = Math.max(aMax, temp);
        }

        // polar-angle statistics
        polar_min[s] = aMin;  // Lowest
        polar_max[s] = aMax;  // Highest
        polar_med[s] = aMed;
        polar_mean[s] = aMean;  // Mean
        polar_stdd[s] = aStd;  // Variation
        polar_skew[s] = aSkew / this.imageSize;
        polar_kurt[s] = (aKurt / this.imageSize) - 3;  // excess kurtosis (the kurtosis for a standard normal distribution is three)
//        polar_angles[s] = calculatePolarAngles(ipAzimuthal,
//        		this.imageWidth, this.imageHeight, 36);  // polar histogram
	}
    
    private double calculateFacetOrientationStatistics(int s, float[] pixels2, double polarThreshold, float[] ipAzimuthal) {
    	/** The Facet-Orientation statistics of the picture. */
        
        double pSize = 1;  // pixel size
        
        float[] ipPolar = new float[this.imageWidth * this.imageHeight];
        double sArea = getFacetDetails(pixels2, this.imageWidth, this.imageHeight, pSize, ipPolar, ipAzimuthal);

        double sMin = Double.MAX_VALUE;
        double sMax = -Double.MAX_VALUE;
        double sSkew = 0, sKurt = 0;
        double sMed = calculateMedian(ipPolar, this.imageWidth, this.imageHeight);
        double sMean = calculateMean(ipPolar, this.imageWidth, this.imageHeight);
        double sStd = calculateStdd(ipPolar, this.imageWidth, this.imageHeight, sMean);

        for (int j = 0; j < this.imageSize; j++) {
            double temp = ipPolar[j];
            double sTemp = temp - sMean;
            if (sStd != 0) {
                sSkew += sqr3(sTemp / sStd);
                sKurt += sqr4(sTemp / sStd);
            }
            sMin = Math.min(sMin, temp);
            sMax = Math.max(sMax, temp);
        }

        // facet-orientation statistics
        facet_min[s] = sMin;  // FO-Lowest
        facet_max[s] = sMax;  // FO-Highest
        facet_med[s] = sMed;
        facet_mean[s] = sMean;  // FO-Mean
        facet_stdd[s] = sStd;  // FO-Variation
        facet_skew[s] = sSkew / this.imageSize;
        facet_kurt[s] = (sKurt / this.imageSize) - 3;  // excess kurtosis (the kurtosis for a standard normal distribution is three)
        facet_area[s] = sArea / this.imageSize; // Surface area
//        facet_white[s] = calculateWhiteArea(ipPolar, this.imageWidth,
//        		this.imageHeight, polarThreshold);  // White area (0 <= polarThreshold <= 1.0 degrees, default 0.5)

        return sStd;
    }

	private void calculateRoughnessStatistics(int s, float[] pixels2) {
		/** The Roughness statistics of the picture. */
        
        double zMin = Double.MAX_VALUE;
        double zMax = -Double.MAX_VALUE;
        double ra = 0, sk = 0, ku = 0;
        double zMed = calculateMedian(pixels2, this.imageWidth, this.imageHeight);
        double zMean = calculateMean(pixels2, this.imageWidth, this.imageHeight);
        double zStd = calculateStdd(pixels2, this.imageWidth, this.imageHeight, zMean);

        for (int j = 0; j < this.imageSize; j++) {
            double temp = pixels2[j];
            double zTemp = temp - zMean;
            if (zStd != 0) {
                sk += sqr3(zTemp / zStd);
                ku += sqr4(zTemp / zStd);
            }
            ra += Math.abs(zTemp);
            zMin = Math.min(zMin, temp);
            zMax = Math.max(zMax, temp);
        }

        // roughness statistics
        roughness_rv[s] = zMin;  // lowest valley
        roughness_rp[s] = zMax;  // highest peak
        roughness_rt[s] = zMax + Math.abs(zMin);  // total height
        roughness_rm[s] = zMed;
        roughness_rq[s] = zStd;  // root mean square deviation
        roughness_ra[s] = ra / this.imageSize;  // arithmetical mean deviation
        roughness_rsk[s] = sk / this.imageSize;  // skewness
        roughness_rku[s] = (ku / this.imageSize) - 3;  // kurtosis
	}

    private double calculateWhiteArea(float[] pixels, int ww, int hh, double thr) {
        int wa = 0;
        int counter = ww * hh;
        for (int j = 0; j < counter; j++) {
            if (pixels[j] < thr) {
                wa++;
            }
        }
        return (double) wa / (double) counter;
    }

    private static double[] calculatePolarAngles(float[] azimuth, int ww, int hh, int size) {
        double[] aziAngle = new double[size];
        int aziCount = 0;
        for (int y = 1; y < hh - 2; y++) {
            for (int x = 1; x < ww - 2; x++) {
                int index = x + ww * y;
                int azi = (int) (azimuth[index] * size);
                if (azi < 0) {
                    azi += size;
                }
                if (azi >= size) {
                    azi -= size;
                }
                aziAngle[azi]++;
                aziCount++;
            }
        }

        // filter the values in order to remove deviating peaks (at 0, 45, 90, 135, etc.)
        float[] tempAngles = new float[3];
        for (int ii = 0; ii < size; ii++) {
            for (int iii = 0; iii < 3; iii++) {
                tempAngles[iii] = (float) aziAngle[(ii + iii) % size];
            }
            aziAngle[ii] = calculateMedian(tempAngles, 3, 1) / (double) aziCount;  // normalize
        }
        return aziAngle;
    }

    // Find the orientation of facets in a topographical image
    // (loosely based on the Facetorientation plugin by Bob Dougherty)
    private static double getFacetDetails(float[] pixels, int ww, int hh, double ps, float[] magnitude, float[] direction) {

        final float[] kernelX = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        final float[] kernelY = {1, 2, 1, 0, 0, 0, -1, -2, -1};

        // evaluate the gradients (and translate them so they are positive)
        float[] gradX = convolve3x3(pixels, ww, hh, null, kernelX, true);
        ImageInfo oldInfoX = analyze(gradX, ww, hh);
        ImageInfo newInfoX = new ImageInfo(0, oldInfoX.maxVal - oldInfoX.minVal);
        normalize(gradX, ww, hh, oldInfoX, newInfoX);
        float[] gradY = convolve3x3(pixels, ww, hh, null, kernelY, true);
        ImageInfo oldInfoY = analyze(gradY, ww, hh);
        ImageInfo newInfoY = new ImageInfo(0, oldInfoY.maxVal - oldInfoY.minVal);
        normalize(gradY, ww, hh, oldInfoY, newInfoY);

        // Extract the orientation data from the gradients of the distance image
        double sArea = 0;
        for (int y = 0; y < hh; y++) {
            for (int x = 0; x < ww; x++) {
                int index = x + ww * y;
                // the derivative requires the factors 1/(2*ps) for the centered
                // finite difference and 1/4 for the coefficients of the kernel.
                double gx = gradX[index] / (8 * ps);
                double gy = gradY[index] / (8 * ps);
                double sqr = gx * gx + gy * gy; // Math.hypot() is slow...
                magnitude[index] = (float) Math.sqrt(sqr);  // gradient magnitude
                direction[index] = (float) ((Math.atan2(gy, gx)) / (Math.PI / 2.0));  // gradient direction
                // computes the surface element by da = sqrt(1 + hypot^2)
                // (suggestion given by Bob Dougherty, Optinav.com)
                sArea += sqr2(ps) * Math.sqrt(1 + sqr);
            }
        }
        return sArea;
    }

    private static float[] gaussianFiltering(float[] pixels, int ww, int hh, ImageInfo oldInfo, double r, boolean roughness) {
        float[] pixels2 = gaussianBlur(pixels, ww, hh, oldInfo, r);
        if (roughness) {
            int counter = ww * hh;
            for (int j = 0; j < counter; j++) {
                pixels2[j] = pixels[j] - pixels2[j];
            }
            ImageInfo newInfo = analyze(pixels2, ww, hh);
            normalize(pixels2, ww, hh, oldInfo, newInfo);
        }
        return pixels2;
    }

    // performs a separate smoothing in each direction using a 1-D filter (the Gaussian filter is separable)
    private static float[] gaussianBlur(float[] pixels, int ww, int hh, ImageInfo oldInfo, double radius) {
        float[] kernel = makeKernel(radius);
        pixels = convolve(pixels, ww, hh, oldInfo, kernel, kernel.length, 1, true);  // horizontal
        pixels = convolve(pixels, ww, hh, oldInfo, kernel, 1, kernel.length, true);  // vertical
        return pixels;
    }

    // analyze a picture of width ww and height hh (for later normalization)
    protected static ImageInfo analyze(float[] pixels, int ww, int hh) {
        float minVal = Float.MAX_VALUE;
        float maxVal = -Float.MAX_VALUE;
        int counter = ww * hh;
        for (int j = 0; j < counter; j++) {
            float value = pixels[j];
            if (!Float.isInfinite(value)) {
                if (value < minVal) {
                    minVal = value;
                }
                if (value > maxVal) {
                    maxVal = value;
                }
            }
        }
        return new ImageInfo(minVal, maxVal);
    }

    /*
     * SURFACE LEVELING
     * Check Level surface to perform tilt correction. This is accomplished by scanning
     * the surface profile and calculating a regression plane according to Bhattacharyya
     * and Johnson (1997). The images are then horizontally aligned by subtracting the
     * regression plane from the surface profile according to the following equation:
     * z_i = z_i - (a + b_1 * i_1 + b_2 * i_2)
     */
    private static float[] levelSurface(float[] pixels, int ww, int hh, ImageInfo oldInfo, int iter) {

        float[] tempArray = new float[ww * hh];
        System.arraycopy(pixels, 0, tempArray, 0, ww * hh);

        int n = ww * hh;

        // iterate several times (default 4)
        for (int it = 0; it < iter; it++) {
            float Ex1, Ex1x1, Ex2x2, Ex1x2, Ey, Ex2, Ex1y, Ex2y;
            float x1Mean, x2Mean, yMean;
            float Sx1y, Sx2y, Sx1x2, Sx1x1, Sx2x2, b1, b2, b0;

            Ex1 = 0;
            Ex1x1 = 0;
            Ex2x2 = 0;
            Ex1x2 = 0;
            Ey = 0;
            Ex2 = 0;
            Ex1y = 0;
            Ex2y = 0;

            // go through the pixel values in the image
            // x1 og x2 are the pixel coordinates, y are the topography values
            for (int x2 = 0; x2 < hh; x2++) {
                for (int x1 = 0; x1 < ww; x1++) {
                    int index = x1 + ww * x2;
                    // int index2 = x2 + hh*x1;
                    float y = tempArray[index];

                    Ex1 = Ex1 + x1;      // sums x, y...
                    Ex2 = Ex2 + x2;
                    Ey = Ey + y;
                    Ex1x1 = Ex1x1 + x1 * x1;  // sums of squares and
                    Ex2x2 = Ex2x2 + x2 * x2;
                    Ex1x2 = Ex1x2 + x1 * x2;  // cross products of variables
                    Ex1y = Ex1y + x1 * y;
                    Ex2y = Ex2y + x2 * y;
                }
            }

            yMean = Ey / n;
            x1Mean = Ex1 / n;
            x2Mean = Ex2 / n;
            Sx1x2 = Ex1x2 - (n * (x1Mean) * (x2Mean));
            Sx1x1 = Ex1x1 - (n * (x1Mean) * (x1Mean));
            Sx2x2 = Ex2x2 - (n * (x2Mean) * (x2Mean));
            Sx1y = Ex1y - (n * (x1Mean) * (yMean));
            Sx2y = Ex2y - (n * (x2Mean) * (yMean));

            // calculate the values of the least square estimates
            b1 = ((Sx2x2 * Sx1y) + (Sx1x2 * Sx2y)) / ((Sx1x1 * Sx2x2) + (Sx1x2 * Sx1x2));
            b2 = (Sx2y - (b1 * Sx1x2)) / (Sx2x2);
            b0 = yMean - b1 * x1Mean - b2 * x2Mean;

            for (int x2 = 0; x2 < hh; x2++) {
                for (int x1 = 0; x1 < ww; x1++) {
                    int index = x1 + ww * x2;
                    float y = tempArray[index];

                    // regression model applied to each pixel in the image
                    tempArray[index] = y - (b0 + b1 * x1 + b2 * x2);
                }
            }
        }
        if (oldInfo != null) {
            ImageInfo newInfo = analyze(tempArray, ww, hh);
            normalize(tempArray, ww, hh, oldInfo, newInfo);
        }
        return tempArray;
    }

    // normalize a picture of width ww and height hh according to the provided information
    protected static void normalize(float[] pixels, int ww, int hh, ImageInfo oldInfo, ImageInfo newInfo) {
        if ((oldInfo.minVal != newInfo.minVal) || (oldInfo.maxVal != newInfo.maxVal)) {
            if ((oldInfo.minVal != oldInfo.maxVal) && (newInfo.minVal != newInfo.maxVal)) {
                float scale = (newInfo.maxVal - newInfo.minVal) / (oldInfo.maxVal - oldInfo.minVal);
                float trans = ((oldInfo.maxVal * newInfo.minVal) - (oldInfo.minVal * newInfo.maxVal)) / (oldInfo.maxVal - oldInfo.minVal);
                int counter = ww * hh;
                for (int j = 0; j < counter; j++) {
                    pixels[j] = pixels[j] * scale + trans;
                }
            }
        }
    }

    // 3x3 convolution by Glynne Casteel
    private static float[] convolve3x3(float[] pixels, int ww, int hh, ImageInfo oldInfo, float[] kernel, boolean normalize) {
        float k1 = kernel[0], k2 = kernel[1], k3 = kernel[2],
                k4 = kernel[3], k5 = kernel[4], k6 = kernel[5],
                k7 = kernel[6], k8 = kernel[7], k9 = kernel[8];

        // normalize convolution kernel
        float scale = 0f;
        if (normalize) {
            for (float k : kernel) {
                scale += k;
            }
            if (scale == 0) {
                scale = 1f;
            } else {
                scale = 1f / scale;
            }
        } else {
            scale = 1f;
        }

        float[] pixels2 = new float[ww * hh];
        // setup limits for 3x3 filters
        int xMin = 1;
        int xMax = ww - 2;
        int yMin = 1;
        int yMax = hh - 2;
        int rowOffset = ww;
        for (int y = yMin; y <= yMax; y++) {
            float p1, p2, p3, p4, p5, p6, p7, p8, p9;
            int offset;
            offset = xMin + y * ww;
            p1 = 0f;
            p2 = pixels[offset - rowOffset - 1];
            p3 = pixels[offset - rowOffset];
            p4 = 0f;
            p5 = pixels[offset - 1];
            p6 = pixels[offset];
            p7 = 0f;
            p8 = pixels[offset + rowOffset - 1];
            p9 = pixels[offset + rowOffset];

            for (int x = xMin; x <= xMax; x++) {
                float sum;
                p1 = p2;
                p2 = p3;
                p3 = pixels[offset - rowOffset + 1];
                p4 = p5;
                p5 = p6;
                p6 = pixels[offset + 1];
                p7 = p8;
                p8 = p9;
                p9 = pixels[offset + rowOffset + 1];
                sum = k1 * p1 + k2 * p2 + k3 * p3 + k4 * p4 + k5 * p5 + k6 * p6 + k7 * p7 + k8 * p8 + k9 * p9;
                pixels2[offset++] = sum * scale;
            }
        }
        if (oldInfo != null) {
            ImageInfo newInfo = analyze(pixels2, ww, hh);
            normalize(pixels2, ww, hh, oldInfo, newInfo);
        }
        return pixels2;
    }

    // FIXME: the computation of the Gaussian seems to be broken
    // compute a Gaussian Blur kernel
    private static float[] makeKernel(double radius) {
        radius += 1;
        int size = (int) radius * 2 + 1;
        float[] kernel = new float[size];
        for (int i = 0; i < size; i++) {
            kernel[i] = (float) Math.exp(-0.5 * (sqr2((i - radius) / (radius * 2))) / sqr2(0.2));
        }
        float[] kernel2 = new float[size - 2];
        System.arraycopy(kernel, 1, kernel2, 0, size - 2);
        if (kernel2.length == 1) {
            kernel2[0] = 1f;
        }
        return kernel2;
    }

    // convolves the image with a kernel of width kw and height kh
    private static float[] convolve(float[] pixels, int ww, int hh,
    		ImageInfo oldInfo, float[] kernel, int kw, int kh, boolean normalize) {
        int x1 = 0;
        int y1 = 0;
        int x2 = x1 + ww;
        int y2 = y1 + hh;
        int uc = kw / 2;
        int vc = kh / 2;

        // normalize convolution kernel
        float scale = 0f;
        if (normalize) {
            for (float k : kernel) {
                scale += k;
            }
            if (scale == 0) {
                scale = 1f;
            } else {
                scale = 1f / scale;
            }
        } else {
            scale = 1f;
        }

        float[] pixels2 = new float[ww * hh];
        int xedge = ww - uc;
        int yedge = hh - vc;
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                float sum = 0.0f;
                int i = 0;
                boolean edgePixel = y < vc || y >= yedge || x < uc || x >= xedge;
                if (edgePixel) {
                    for (int v = -vc; v <= vc; v++) {
                        for (int u = -uc; u <= uc; u++) {
                            sum += clipPixel(pixels, ww, hh, x + u, y + v) * kernel[i++];
                        }
                    }
                } else {
                    for (int v = -vc; v <= vc; v++) {
                        int offset = x + (y + v) * ww;
                        for (int u = -uc; u <= uc; u++) {
                            sum += pixels[offset + u] * kernel[i++];
                        }
                    }
                }
                pixels2[x + y * ww] = sum * scale;
            }
        }
        if (oldInfo != null) {
            ImageInfo newInfo = analyze(pixels2, ww, hh);
            normalize(pixels2, ww, hh, oldInfo, newInfo);
        }
        return pixels2;
    }

    private static float clipPixel(float[] pixels, int ww, int hh, int x, int y) {
        if (x < 0) {
            x = 0;
        }
        if (x >= ww) {
            x = ww - 1;
        }
        if (y < 0) {
            y = 0;
        }
        if (y >= hh) {
            y = hh - 1;
        }
        return pixels[x + y * ww];
    }

    private static double sqr2(double x) {
        return x * x;
    }

    private static double sqr3(double x) {
        return x * x * x;
    }

    private static double sqr4(double x) {
        return x * x * x * x;
    }

    private static double calculateMean(float[] pixels, int ww, int hh) {
        double mValue = 0;
        int counter = ww * hh;
        for (int j = 0; j < counter; j++) {
            mValue += pixels[j];
        }
        return mValue / (double) counter;
    }

    private static double calculateStdd(float[] pixels, int ww, int hh, double mean) {
        double sValue = 0;
        int counter = ww * hh;
        if (counter > 1) {
            for (int j = 0; j < counter; j++) {
                sValue += sqr2(mean - pixels[j]);
            }
            sValue = Math.sqrt(sValue / counter);
        }
        return sValue;
    }

    private static double calculateMedian(float[] pixels, int ww, int hh) {
        float[] pixels2 = new float[ww * hh];
        System.arraycopy(pixels, 0, pixels2, 0, ww * hh);
        int lengthArray = pixels.length;
        Arrays.sort(pixels2);
        int mid;
        double med;
        if ((lengthArray % 2) != 0) {
            mid = lengthArray / 2;
            med = pixels2[mid];
        } else {
            mid = (int) ((lengthArray / 2) + 0.5);
            med = (pixels2[mid - 1] + pixels2[mid]) / 2;
        }
        return med;
    }

}
