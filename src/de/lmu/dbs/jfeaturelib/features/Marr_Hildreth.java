package de.lmu.dbs.jfeaturelib.features;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/** 
 *
 */

public class Marr_Hildreth implements FeatureDescriptorInt{

        private long time;
        private boolean calculated; 
        private ColorProcessor image;
        private float[] kernel = null;
	
        /** Gaussian deviation */
        private double deviation;
        /** Kernel size */
        private int kernelSize;
        /** How many times to apply laplacian gaussian operation */
        private int times;
            
        public Marr_Hildreth() {
            this.deviation = 0.6;
            this.kernelSize = 7;
            this.times = 1;
            calculated = false;
        }
        
	public Marr_Hildreth(double deviation, int kernelSize, int times) {
            this.deviation = deviation;
            this.kernelSize = kernelSize;
            this.times = times;
            calculated = false;
	
	}
        
           /*
            * @author Giovane.Kuhn - brain@netuno.com.br
            * http://www.java2s.com/Open-Source/Java-Document/Graphic-Library/apollo/org/apollo/effect/LaplacianGaussianEffect.java.htm
            *
            * Create new laplacian gaussian operation
            * @return New instance
            */
            private float[] createLoGOp() {
                float[] data = new float[kernelSize * kernelSize];
                double first = -1.0 / (Math.PI * Math.pow(deviation, 4.0));
                double second = 2.0 * Math.pow(deviation, 2.0);
                double third;
                int r = kernelSize / 2;
                int x, y;
                for (int i = -r; i <= r; i++) {
                    x = i + r;
                    for (int j = -r; j <= r; j++) {
                        y = j + r;
                        third = (Math.pow(i, 2.0) + Math.pow(j, 2.0)) / second;
                        data[x + y * kernelSize] = (float) (first * (1 - third) * Math
                                .exp(-third));
                    }
                }
                return data;
            }
        
	public void process() {
            // laplacian gaussian operation
                if (kernel == null) {
                    kernel = createLoGOp();
                }
                for (int i = 0; i < times; i++) {
                    image.convolve(kernel, kernelSize, kernelSize);
                }
	}


    
    /**
    * Returns the image edges as INT_ARGB array.
    * This can be used to create a buffered image, if the dimensions are known.
    */
    @Override
    public int[] getFeatures() {
        if(calculated){
            int[] data = (int[])image.getBufferedImage().getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
            return data;
        }
        else{
            //TODO throw exception
            return new int[]{0};
        }
    }  

    /**
     * Returns information about the getFeauture returns in a String array.
     */      
    @Override
    public String[] getDescription() {
        String[] info = new String[1];
        info[0] = "Each pixel value";
        info[1] = "Each pixel value";
        info[2] = "Each pixel value";
        info[3] = "Each pixel value";
        return info;
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
            Supports.DOES_RGB,
            Supports.DOES_16
        );
        //set.addAll(DOES_ALL);
        return set;
    }

    /**
     * Starts the canny edge detection.
     * @param ip ImageProcessor of the source image
     */ 
    @Override
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis();
        image = (ColorProcessor) ip;
        this.process();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }

    public long getTime(){
         return time;
    }

    public boolean isCalculated(){
        return calculated;
    }
}
