package de.lmu.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/** 
 * FIXME doc!
 */
public class Marr_Hildreth implements FeatureDescriptor{

        private DescriptorChangeListener changeListener;
        private long time;
        private boolean calculated;
        private int progress; 
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
            progress = 0;
	
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
                    progress = (int)Math.round(i*(100.0/(double)times));
                    fireStateChanged();
                }
	}


    
    /**
    * Returns the image edges as INT_ARGB array.
    * This can be used to create a buffered image, if the dimensions are known.
    */
    @Override
    public List<double[]> getFeatures() {
        if(calculated){
            int[] data = (int[])image.getBufferedImage().getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
            ArrayList<double[]> list = new ArrayList<double[]>(1);
            list.add(Arrays2.convertToDouble(data));
            return list;
        }
        else{
            return Collections.EMPTY_LIST;
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
        fireStateChanged();
        this.process();
        progress = 100;
        fireStateChanged();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }

    @Override
    public long getTime(){
         return time;
    }

    @Override
    public boolean isCalculated(){
        return calculated;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setArgs(double[] args) {
        if(args == null){
            
        }
        else if(args.length == 3){
            this.deviation = args[0];
            this.kernelSize = Integer.valueOf((int)args[1]);
            this.times = Integer.valueOf((int)args[2]);
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Arguments array is not formatted correctly");
        }
    }
    
    @Override
    public void addChangeListener(DescriptorChangeListener l) {
        changeListener = l;
        l.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void fireStateChanged() {
        changeListener.valueChanged(new DescriptorChangeEvent(this));
    }

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
    }
}
