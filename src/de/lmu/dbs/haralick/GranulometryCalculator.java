package de.lmu.dbs.haralick;

import java.awt.image.BufferedImage;


public class GranulometryCalculator extends ValueCalculatorDependency {
	
	public static final String GRANULOMETRY = "granulometry";
	
	/** The granulometry statistics of the picture. */
    private double[] gran_density;    

    private enum RankType {
        MEDIAN, MEAN, MIN, MAX, VARIANCE
    };

	public GranulometryCalculator(BufferedImage image) {
		super(image);
	}

	public GranulometryCalculator(ValueCalculator val_calc) {
		super(val_calc);
	}
    
    @Override
    public void calculate() {
    	this.value_calc.calculate();
    	
    	this.gran_density = new double[12];
    	
    	float[] grayPixel = this.value_calc.getGrayPixels();
    	calculateGranulometry(grayPixel, this.imageWidth, this.imageHeight, 0, 12, 1);
    	
    	this.addDescriptorInfo(GRANULOMETRY, this.gran_density);
    }
    
    /* Compute the granulometry of a grayscale image using circular structure elements.
    *
    * Granulometries were introduced by Matheron [G. Matheron, Randoms sets and integral equation,
    * Wiley, New York, 1978.] as tools to extract size distribution from binary images. By performing
    * series of morphological openings with a family of structuring elements (SE-s) of increasing size,
    * we can obtain the granulometry function which maps each SE to the number of image pixels removed
    * during the opening operation with the corresponding structuring element.
    *
    * The pixel size distribution function Ps can be defined as
    * Ps(k) = Sum of all the pixels in the image, opened with k-th SE. One can also define the Normalized
    * size distribution function simply as N(k) = 1-Ps(k)/Ps(0), where Ps(0) is the Ps of the original image.
    *
    * The granulometric density function can be defined as
    * G(k) = N(k+1) - N(k)
    *
    * This function can be viewed as discrete first derivative function of image structure sizes.
    * The above definition is often referred to as pattern spectrum of the image. A local maximum
    * in the pattern spectrum at a given scale indicates the presence of many structures at that scale.
    */
   private void calculateGranulometry(float[] pixels, int ww, int hh, double minRadius, double maxRadius, double step) {
       ImageInfo oldInfo = RoughnessCalculator.analyze(pixels, ww, hh);
       int ratio = (int) ((maxRadius - minRadius) / step);
       float[] pixcount = new float[ratio + 1];
       pixcount[0] = getPixelSum(pixels, ww, hh);
       float cal = pixcount[0];
       int counter = 0;
       double radius = minRadius;
       while (radius <= maxRadius) {
           float[] pixels2;
           // perform graylevel opening (ie. erosion followed dilation) with the given radius (particle size = 2*radius+1)
           pixels2 = rankFilter(pixels, ww, hh, oldInfo, radius, RankType.MIN);	// erosion
           pixels2 = rankFilter(pixels2, ww, hh, oldInfo, radius, RankType.MAX);	// dilation

           pixcount[counter] = getPixelSum(pixels2, ww, hh);
           float density;
           if (counter == 0) {
               density = 0;
           } else {
               density = (pixcount[counter - 1] - pixcount[counter]) / cal;
               gran_density[counter - 1] = density;
           }
//     double content = Math.PI*radius*radius;
//     System.out.println("radius: "+radius+", content: "+content+", pixels: "+pixcount[counter]+", density: "+density);

           if (pixcount[counter] == 0) {
               break;
           }
           radius += step;
           counter++;
       }
   }
   
   private static float getPixelSum(float[] pixels, int ww, int hh) {
       float sum = 0;
       int counter = ww * hh;
       for (int j = 0; j < counter; j++) {
           sum += pixels[j];
       }
       return sum;
   }
   
   // Morphological operations like Median, Mean, Minimum, Maximum and Variance
   private static float[] rankFilter(float[] pixels, int ww, int hh, ImageInfo oldInfo, double radius, RankType type) {
       int x1 = 0;
       int y1 = 0;
       int x2 = ww - 1;
       int y2 = hh - 1;
       int kw = ((int) (radius + 0.5)) * 2 + 1;
       int kh = kw;
       int uc = kw / 2;
       int vc = kh / 2;

       int[] mask = createCircularMask(kw, radius);
       int maskSize = 0;
       for (int i = 0; i < kw * kw; i++) {
           if (mask[i] != 0) {
               maskSize++;
           }
       }
       float values[] = new float[maskSize];

       float[] pixels2 = new float[ww * hh];
       int xedge = ww - uc;
       int yedge = hh - vc;
       for (int y = y1; y <= y2; y++) {
           for (int x = x1; x <= x2; x++) {
               int i = 0;
               int count = 0;
               boolean edgePixel = y < vc || y >= yedge || x < uc || x >= xedge;
               if (edgePixel) {
                   for (int v = -vc; v <= vc; v++) {
                       int offset = x + (y + v) * ww;
                       for (int u = -uc; u <= uc; u++) {
                           if (mask[i++] != 0) {
                               if (testPixel(ww, hh, x + u, y + v)) {
                                   values[count++] = pixels[offset + u];
                               }
                           }
                       }
                   }
               } else {
                   for (int v = -vc; v <= vc; v++) {
                       int offset = x + (y + v) * ww;
                       for (int u = -uc; u <= uc; u++) {
                           if (mask[i++] != 0) {
                               values[count++] = pixels[offset + u];
                           }
                       }
                   }
               }
               switch (type) {
                   case MEDIAN:
                       pixels2[x + y * ww] = findMedian(values, count);
                       break;
                   case MEAN:
                       pixels2[x + y * ww] = findMean(values, count);
                       break;
                   case MIN:
                       pixels2[x + y * ww] = findMin(values, count);
                       break;
                   case MAX:
                       pixels2[x + y * ww] = findMax(values, count);
                       break;
                   case VARIANCE:
                       pixels2[x + y * ww] = findVariance(values, count);
                       break;
                   default:
                       break;
               }
           }
       }
       if (oldInfo != null) {
           ImageInfo newInfo = RoughnessCalculator.analyze(pixels2, ww, hh);
           RoughnessCalculator.normalize(pixels2, ww, hh, oldInfo, newInfo);
       }
       return pixels2;
   }
   
   private static int[] createCircularMask(int width, double radius) {
       if ((radius >= 1.5) && (radius < 1.75)) {
           radius = 1.75;
       } else if ((radius >= 2.5) && (radius < 2.85)) {
           radius = 2.85;
       }
       int[] mask = new int[width * width];
       int r = width / 2;
       int r2 = (int) (radius * radius) + 1;
       for (int x = -r; x <= r; x++) {
           for (int y = -r; y <= r; y++) {
               if ((x * x + y * y) <= r2) {
                   mask[r + x + (r + y) * width] = 1;
               }
           }
       }
       return mask;
   }

   // Modified algorithm according to http://www.geocities.com/zabrodskyvlada/3alg.html
   // Contributed by Heinz Klar.
   private static float findMedian(float[] a, int n) {
       final int nv1b2 = (n - 1) / 2;
       int l = 0;
       int m = n - 1;
       float med = a[nv1b2];
       while (l < m) {
           int i = l;
           int j = m;
           do {
               while (a[i] < med) {
                   i++;
               }
               while (med < a[j]) {
                   j--;
               }
               float tmp = a[j];
               a[j] = a[i];
               a[i] = tmp;
               i++;
               j--;
           } while ((j >= nv1b2) && (i <= nv1b2));
           if (j < nv1b2) {
               l = i;
           }
           if (nv1b2 < i) {
               m = j;
           }
           med = a[nv1b2];
       }
       return med;
   }

   private static float findMin(float[] values, int n) {
       float min = values[0];
       for (int i = 1; i < n; i++) {
           if (values[i] < min) {
               min = values[i];
           }
       }
       return min;
   }

   private static float findMax(float[] values, int n) {
       float max = values[0];
       for (int i = 1; i < n; i++) {
           if (values[i] > max) {
               max = values[i];
           }
       }
       return max;
   }

   private static float findMean(float[] values, int n) {
       float sum = values[0];
       for (int i = 1; i < n; i++) {
           sum += values[i];
       }
       return sum / n;
   }

   private static float findVariance(float[] values, int n) {
       double sum = 0.0, sum2 = 0.0;
       float min = findMin(values, n);
       for (int i = 0; i < n; i++) {
           double v = values[i] - min;	// why "- min"???
           sum += v;
           sum2 += v * v;
       }
       return (float) ((n * sum2 - sum * sum) / n);	// ?? "/(n*n)" should be correct
   }

   private static boolean testPixel(int ww, int hh, int x, int y) {
       if ((x < 0) || (x >= ww)) {
           return false;
       }
       if ((y < 0) || (y >= hh)) {
           return false;
       }
       return true;
   }

}
