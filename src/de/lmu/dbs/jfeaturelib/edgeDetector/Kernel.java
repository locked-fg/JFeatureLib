package de.lmu.dbs.jfeaturelib.edgeDetector;

import de.lmu.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


/**
 * Performs convolution with a given Kernel directly on this image.
 *
 * Basiacally it is just a wrapper for ImageJ's ImageProcessor.convolve().
 *
 * Predefined masks are SOBEL, SCHARR, PREWITT
 *
 * @see ImageProcessor#convolve(float[], int, int)
 * @author Benedikt
 * @author Franz
 */

public class Kernel implements FeatureDescriptor{

    /**
     * Standard 3x3 SOBEL mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * @see http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SOBEL = new float[]{
        1, 0, -1,
        2, 0, -2,
        1, 0, -1
    };
    /**
     * Standard 3x3 SCHARR mask (1 0 -1, 2, 0, -2, 1, 0, -1 )
     *
     * @see http://en.wikipedia.org/wiki/Sobel_operator
     */
    public static final float[] SCHARR = new float[]{
        3, 0, -3,
        10, 0, -10,
        3, 0, -3
    };
    /**
     * 3x3 PREWITT mask (-1, 0, 1, ...)
     *
     * @see http://en.wikipedia.org/wiki/Prewitt_operator
     */
    public static final float[] PREWITT = new float[]{
        -1, 0, 1,
        -1, 0, 1,
        -1, 0, 1
    };
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ByteProcessor image;
    private int width;
    private int height;
    private float[] kernel;
    private int kernelWidth;
    private int[] result;

    /**
     * Constructs a new detector with standart Sobel kernel
     */
    public Kernel() {
        this(SOBEL);
    }

    /**
     * Constructs a new detector with given double[] kernel. The double array is
     * converted immediately to a float array.
     *
     * @param kernel double[] with kernel
     */
    public Kernel(double[] kernel) {        
        this.kernelWidth = Math.round((float) Math.sqrt(kernel.length + 1.0f));
        this.kernel = Arrays2.convertToFloat(kernel);
    }

    /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     */
    
    public Kernel(float[] kernel) {
        this.kernelWidth = Math.round((float) Math.sqrt(kernel.length + 1.0f));
        this.kernel = kernel;
    }
    
        /**
     * Constructs a new detector with given float[] kernel and according width.
     *
     * @param kernel float[] with kernel
     * @param width width of kernel
     */
    
    public Kernel(float[] kernel, int width) {
        this.kernelWidth = width;
        this.kernel = kernel;
    }

    //<editor-fold defaultstate="collapsed" desc="accessors">
    /**
     * Return the convolution kernel before transformation
     *
     * @return float[] with kernel
     */
    public float[] getKernel() {
        return kernel;
    }

    /**
     * Sets the convolution kernel
     *
     * @param kernel float[] with kernel
     */
    public void setKernel(float[] kernel) {
        this.kernel = kernel;
    }

    /**
     * Returns width of the kernel
     *
     * @return int with width of kernel
     */
    public int getKernelWidth() {
        return kernelWidth;
    }

    /**
     * Setter for width of the kernel
     *
     * @param kernelWidth int with width
     */
    public void setKernelWidth(int kernelWidth) {
        this.kernelWidth = kernelWidth;
    }

    /**
     * Proceses the image applying the kernel in x- and y-direction
     */
    public void process(){

       pcs.firePropertyChange(Progress.getName(), null, new Progress(0, "initialized"));
       
       ByteProcessor imgX = new ByteProcessor(image.createImage());
       ByteProcessor imgY = new ByteProcessor(image.createImage());
       float[] kernelX = kernel;
       float[] kernelY = new float[kernelWidth*kernelWidth];
       float[][] kernelX2D = new float[kernelWidth][kernelWidth];
       int i = 0;
       for(int x = 0; x<kernelWidth; x++){
           for(int y = 0; y<kernelWidth; y++){
               kernelX2D[x][y] = kernelX[i];
               i++;
           }
       }
       float[][] kernelY2D = de.lmu.dbs.jfeaturelib.utils.RotateArrays.rotateFloatCW(kernelX2D);
       i = 0;
       for(int x = 0; x<kernelWidth; x++){
           for(int y = 0; y<kernelWidth; y++){
               kernelY[i] = kernelY2D[x][y];
               int progress = (int)Math.round(i*(100.0/(double)(kernelWidth*kernelWidth)));
               pcs.firePropertyChange(Progress.getName(), null, new Progress(progress, "Step " + i + " of " + kernelWidth*kernelWidth));
               i++;
           }                
       }       
       imgX.convolve(kernelX, kernelWidth, kernelWidth);
       imgY.convolve(kernelY, kernelWidth, kernelWidth);
       int[][] imgXa = imgX.getIntArray();
       int[][] imgYa = imgY.getIntArray();
       int[][] imgA = new int[image.getWidth()][image.getHeight()];
       for(int x = 0; x < image.getWidth();x++){
           for(int y = 0; y < image.getHeight(); y++){
               if((int)Math.round(Math.sqrt(imgXa[x][y]*imgXa[x][y]+imgYa[x][y]*imgYa[x][y])) > 255){
                   imgA[x][y] = 255;
               }
               else{
               imgA[x][y] = (int)Math.round(Math.sqrt(imgXa[x][y]*imgXa[x][y]+imgYa[x][y]*imgYa[x][y]));                   
               }
           }           
       }
       image.setIntArray(imgA);
       result = (int[]) image.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
       
       
       /*
        //inspired by http://users.ecs.soton.ac.uk/msn/book/new_demo/sobel/
        width = image.getWidth();
        height = image.getHeight();
        int[] input = new int[width*height];
        int[] output = new int[width*height];
        double[] direction = new double[width*height];

        float[] GY = new float[width*height];
        float[] GX = new float[width*height];
        int[] total = new int[width*height];
        int sum=0;
        int max=0;        
        
        PixelGrabber grabber = new PixelGrabber(image.getBufferedImage(), 0, 0, width, height, input, 0, width);
        try {
            grabber.grabPixels();
        } catch (InterruptedException ex) {
            System.out.println("Fatal error trying to convert image to int array");
        }


        for(int x=(kernelWidth-1)/2; x<width-(kernelWidth+1)/2;x++) {
                for(int y=(kernelWidth-1)/2; y<height-(kernelWidth+1)/2;y++) {
                        sum=0;

                        for(int x1=0;x1<kernelWidth;x1++) {
                                for(int y1=0;y1<kernelWidth;y1++) {
                                        int x2 = (x-(kernelWidth-1)/2+x1);
                                        int y2 = (y-(kernelWidth-1)/2+y1);
                                        float value = (input[y2*width+x2] & 0xff) * (kernel[y1*kernelWidth+x1]);
                                        sum += value;
                                }
                        }
                        GY[y*width+x] = sum;
                        for(int x1=0;x1<kernelWidth;x1++) {
                                for(int y1=0;y1<kernelWidth;y1++) {
                                        int x2 = (x-(kernelWidth-1)/2+x1);
                                        int y2 = (y-(kernelWidth-1)/2+y1);
                                        float value = (input[y2*width+x2] & 0xff) * (kernel[x1*kernelWidth+y1]);
                                        sum += value;
                                }
                        }
                        GX[y*width+x] = sum;

                }
        }
        for(int x=0; x<width;x++) {
                for(int y=0; y<height;y++) {
                        total[y*width+x]=(int)Math.sqrt(GX[y*width+x]*GX[y*width+x]+GY[y*width+x]*GY[y*width+x]);
                        direction[y*width+x] = Math.atan2(GX[y*width+x],GY[y*width+x]);
                        if(max<total[y*width+x])
                                max=total[y*width+x];
                }
        }
        float ratio=(float)max/255;
        for(int x=0; x<width;x++) {
                for(int y=0; y<height;y++) {
                        sum=(int)(total[y*width+x]/ratio);
                        output[y*width+x] = 0xff000000 | ((int)sum << 16 | (int)sum << 8 | (int)sum);
                }
        }

       result = output;
        */
       pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));
    }
    
    /**
     * Returns the image edges as INT_ARGB array. This can be used to create a
     * buffered image, if the dimensions are known.
     */
    @Override
    public List<double[]> getFeatures() {
        if (result != null) {
            ArrayList<double[]> thisResult = new ArrayList<>(1);
            thisResult.add(Arrays2.convertToDouble(result));
            return thisResult;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Returns information about the getFeauture returns in a String array.
     */
    @Override
    public String getDescription() {
        String info = "Each pixel value";
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
                Supports.DOES_16);
        //set.addAll(DOES_ALL);
        return set;
    }

    /**
     * Starts the canny edge detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        this.image = (ByteProcessor) ip;
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}