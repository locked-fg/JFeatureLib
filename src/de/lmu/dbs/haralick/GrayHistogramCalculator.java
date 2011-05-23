package de.lmu.dbs.haralick;


import de.lmu.dbs.ImagePoint;
import de.lmu.dbs.utils.Distance;
import de.lmu.dbs.utils.DistanceL2;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GrayHistogramCalculator extends ValueCalculatorDependency {

	public static final String GRAYHISTOGRAM = "grayhistogram";
	private double[] grayHistogram;
	
	public GrayHistogramCalculator(BufferedImage image) {
		super(image);
	}
	
	public GrayHistogramCalculator(ValueCalculator val_calc) {
		super(val_calc);
	}
    
    /**
     * Calculates some staticsical values
     */
    @Override
    public void calculate() {
    	this.value_calc.calculate();
    	grayHistogram = this.value_calc.getGrayHistogram();    	
        createGrayHistogram();
        
//	    getEntropie();
//	    getGlobalContrast();
//	    getLocalContrast();
//	    createCorrelogram();
    }

    private void createGrayHistogram() {
        double[] grayscaleHistogram = new double[NUM_GRAY_VALUES];
        for (int i = 0; i < grayHistogram.length; i++) {
            grayscaleHistogram[(i * NUM_GRAY_VALUES) / GRAY_RANGES] += grayHistogram[i];
        }
        
        this.addDescriptorInfo(GRAYHISTOGRAM, grayscaleHistogram);
    }
    
    private double getEntropie() {
        double entropie = 0.0;
        for (int i = 0; i < grayHistogram.length; i++) {
            double grayEntry = grayHistogram[i];
//    -sum Hp(i) log2Hp(i) (0<=i<K)
//    System.out.println("grayEntry: ["+i+"]: "+grayEntry);
            if (grayEntry != 0.0) {
                entropie += (grayEntry * (Math.log10(grayEntry) / Math.log10(2)));
            }
        }
//    System.out.println("Entropie: " + (-entropie));
        return -entropie;
    }

    private double getGlobalContrast() {
        int mingrau = 0;
        for (int i = 0; i < grayHistogram.length; i++) {
            if (grayHistogram[i] != 0.0) {
                mingrau = i;
                break;
            }
        }
        int maxgrau = grayHistogram.length - 1;
        for (int i = grayHistogram.length - 1; i > 0; i--) {
            if (grayHistogram[i] != 0.0) {
                maxgrau = i;
                break;
            }
        }
//    System.out.println("mingrau: " + mingrau + ", maxgrau: " + maxgrau);
        return (double) (maxgrau - mingrau) / (double) (grayHistogram.length - 1);
    }

    private double getLocalContrast() {
        float[] grayPixel = this.value_calc.getGrayPixels();
        double contrast = 0.0;
        for (int y = 0; y < this.imageHeight; y++) {
            for (int x = 0; x < this.imageWidth; x++) {
                int pos = this.imageWidth * y + x;

                double viererNachbarschaft = 0.0;
				if (x > 0) {
                    viererNachbarschaft += grayPixel[pos - 1];
                }
                if (x < this.imageWidth  - 1) {
                    viererNachbarschaft += grayPixel[pos + 1];
                }
                if (y > 0) {
                    viererNachbarschaft += grayPixel[pos - this.imageWidth];
                }
                if (y < this.imageHeight - 1) {
                    viererNachbarschaft += grayPixel[pos + this.imageWidth];
                }

                contrast += Math.abs(grayPixel[pos] - (viererNachbarschaft / 4.0)); // abs()???
            }
        }
//    System.out.println("contrast: "+contrast);
        return contrast / (double) (this.imageSize);
    }

    private List<ImagePoint>[] createCorrelogram() {
    	/** Contains the correlation histogram of the image. */
        List<ImagePoint>[] correloHistogram = new ArrayList[HS_H_RANGES * HS_S_RANGES];
    	// init correlogram
        for (int i = 0; i < (HS_H_RANGES * HS_S_RANGES); i++) {
            correloHistogram[i] = new ArrayList<ImagePoint>();
        }
    	
        Correlogram correlogram = new Correlogram();
        Distance dist = new DistanceL2();
        int distance = (int) dist.distance(0, 0, this.imageWidth, this.imageHeight);
        correlogram.setCorrelogram(new int[HS_H_RANGES * HS_S_RANGES][distance]);
        int maxcount = 0;
        for (int k = 0; k < correloHistogram.length; k++) {
            for (int i = 0; i < correloHistogram[k].size(); i++) {
                ImagePoint temp = correloHistogram[k].get(i);
                for (int j = i + 1; j < correloHistogram[k].size(); j++) {
                    ImagePoint dummytemp = correloHistogram[k].get(j);
                    distance = (int) dist.distance(temp, dummytemp);

                    if (correlogram.getCorrelogram()[i] == null) {
                        correlogram.getCorrelogram()[i][distance] = 1;
                    } else {
                        correlogram.getCorrelogram()[i][distance]++;
                    }

                    if (correlogram.getCorrelogram()[i][distance] > maxcount) {
                        maxcount = correlogram.getCorrelogram()[i][distance];
                    }
                }
            }
        }
        correlogram.setMaxvalue(maxcount);
        
        return correloHistogram;
    }

}
