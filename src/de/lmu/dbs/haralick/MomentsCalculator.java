package de.lmu.dbs.haralick;

import java.awt.image.BufferedImage;

public class MomentsCalculator extends ValueCalculatorDependency {
	
	public static final String COLORMOMENT_YUV_KURT = "colormoment_yuv_kurt";
	public static final String COLORMOMENT_YUV_SKEW = "colormoment_yuv_skew";
	public static final String COLORMOMENT_YUV_STDD = "colormoment_yuv_stdd";
	
	public static final String COLORMOMENT_HSL_KURT = "colormoment_hsl_kurt";
	public static final String COLORMOMENT_HSL_SKEW = "colormoment_hsl_skew";
	public static final String COLORMOMENT_HSL_STDD = "colormoment_hsl_stdd";
	
	public static final String COLORMOMENT_HSV_KURT = "colormoment_hsv_kurt";
	public static final String COLORMOMENT_HSV_SKEW = "colormoment_hsv_skew";
	public static final String COLORMOMENT_HSV_STDD = "colormoment_hsv_stdd";
    
    public MomentsCalculator(ValueCalculator value_calc) {
    	super(value_calc);
    }
    
    public MomentsCalculator(BufferedImage image) {
    	super(image);
    }

    /**
     * Calculates the second and third moment of the hsv values
     * (standard deviation and skewness).
     */
    @Override
    public void calculate() {
    	this.value_calc.calculate();
    	
    	calculateHsvMoment();
        calculateHslMoment();
        calculateYuvMoment();
    }

	private void calculateYuvMoment() {
		double[] meanYUV = this.value_calc.getDescriptorInfo(ValueCalculator.COLORMOMENT_YUV_MEAN).data;
			
		double[] stddevYUV = new double[3];
		double[] skewnessYUV = new double[3];
		double[] kurtosisYUV = new double[3];
	
		double[] yuv_sum_2 = new double[3];
        double[] yuv_sum_3 = new double[3];
        double[] yuv_sum_4 = new double[3];
        
        float[][] yuvValues = this.value_calc.getYuvValues();

        // sum^2 and sum^3
        calculateValueSums(yuvValues, meanYUV, yuv_sum_2, yuv_sum_3, yuv_sum_4);

        // standard deviation, skewness and kurtosis
        for (int i = 0; i < stddevYUV.length; i++) {
            stddevYUV[i] = Math.sqrt(yuv_sum_2[i] / yuvValues.length);
            if (Double.isNaN(stddevYUV[i])) {
                stddevYUV[i] = 0;
            }

            double s_3 = stddevYUV[i] * stddevYUV[i] * stddevYUV[i];
            skewnessYUV[i] = yuv_sum_3[i] / (yuvValues.length * s_3);
            if (Double.isNaN(skewnessYUV[i])) {
                skewnessYUV[i] = 0;
            }

            double s_4 = s_3 * stddevYUV[i];
            kurtosisYUV[i] = (yuv_sum_4[i] / (yuvValues.length * s_4)) - 3;
            if (Double.isNaN(kurtosisYUV[i])) {
                kurtosisYUV[i] = 0;
            }
        }

       	this.addDescriptorInfo(COLORMOMENT_YUV_STDD, stddevYUV);
       	this.addDescriptorInfo(COLORMOMENT_YUV_SKEW, skewnessYUV);
       	this.addDescriptorInfo(COLORMOMENT_YUV_KURT, kurtosisYUV);
	}

	private void calculateHslMoment() {
		double[] meanHSL = this.value_calc.getDescriptorInfo(ValueCalculator.COLORMOMENT_HSL_MEAN).data;
			
		double[] stddevHSL = new double[3];
		double[] skewnessHSL = new double[3];
		double[] kurtosisHSL = new double[3];
	
		double[] hsl_sum_2 = new double[3];
        double[] hsl_sum_3 = new double[3];
        double[] hsl_sum_4 = new double[3];
        
        float[][] hslValues = this.value_calc.getHslValues();

        // sum^2 and sum^3
        calculateValueSums(hslValues, meanHSL, hsl_sum_2, hsl_sum_3, hsl_sum_4);

        // standard deviation, skewness and kurtosis
        for (int i = 0; i < stddevHSL.length; i++) {
            stddevHSL[i] = Math.sqrt(hsl_sum_2[i] / hslValues.length);
            if (Double.isNaN(stddevHSL[i])) {
                stddevHSL[i] = 0;
            }

            double s_3 = stddevHSL[i] * stddevHSL[i] * stddevHSL[i];
            skewnessHSL[i] = hsl_sum_3[i] / (hslValues.length * s_3);
            if (Double.isNaN(skewnessHSL[i])) {
                skewnessHSL[i] = 0;
            }

            double s_4 = s_3 * stddevHSL[i];
            kurtosisHSL[i] = (hsl_sum_4[i] / (hslValues.length * s_4)) - 3;
            if (Double.isNaN(kurtosisHSL[i])) {
                kurtosisHSL[i] = 0;
            }
        }
        
       	this.addDescriptorInfo(COLORMOMENT_HSL_STDD, stddevHSL);
       	this.addDescriptorInfo(COLORMOMENT_HSL_SKEW, skewnessHSL);
       	this.addDescriptorInfo(COLORMOMENT_HSL_KURT, kurtosisHSL);
	}

	private void calculateHsvMoment() {
		double[] meanHSV = this.value_calc.getDescriptorInfo(ValueCalculator.COLORMOMENT_HSV_MEAN).data;
		
		double[] stddevHSV = new double[3];
    	double[] skewnessHSV = new double[3];
    	double[] kurtosisHSV = new double[3];
    	
        double[] hsv_sum_2 = new double[3];
        double[] hsv_sum_3 = new double[3];
        double[] hsv_sum_4 = new double[3];
        
        float[][] hsvValues = this.value_calc.getHsvValues();

        // sum^2 and sum^3
        calculateValueSums(hsvValues, meanHSV, hsv_sum_2, hsv_sum_3, hsv_sum_4);

        // standard deviation, skewness and kurtosis
        for (int i = 0; i < stddevHSV.length; i++) {
            stddevHSV[i] = Math.sqrt(hsv_sum_2[i] / hsvValues.length);
            if (Double.isNaN(stddevHSV[i])) {
                stddevHSV[i] = 0;
            }

            double s_3 = stddevHSV[i] * stddevHSV[i] * stddevHSV[i];
            skewnessHSV[i] = hsv_sum_3[i] / (hsvValues.length * s_3);
            if (Double.isNaN(skewnessHSV[i])) {
                skewnessHSV[i] = 0;
            }

            double s_4 = s_3 * stddevHSV[i];
            kurtosisHSV[i] = (hsv_sum_4[i] / (hsvValues.length * s_4)) - 3;
            if (Double.isNaN(kurtosisHSV[i])) {
                kurtosisHSV[i] = 0;
            }
        }
       	
       	this.addDescriptorInfo(COLORMOMENT_HSV_STDD, stddevHSV);
       	this.addDescriptorInfo(COLORMOMENT_HSV_SKEW, skewnessHSV);
       	this.addDescriptorInfo(COLORMOMENT_HSV_KURT, kurtosisHSV);
	}

	private static void calculateValueSums (float[][] values, double[] meanValues,
			double[] sum_2_out, double[] sum_3_out, double[] sum_4_out) {
		for (float[] value : values) {
			for (int j = 0; j < 3; j++) {
                double delta = value[j] - meanValues[j];
                double square_delta = delta * delta;
                sum_2_out[j] += square_delta;
                sum_3_out[j] += square_delta * delta;
                sum_4_out[j] += square_delta * square_delta;
            }
		}
	}
}
