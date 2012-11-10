package de.lmu.ifi.dbs.jfeaturelib.edgeDetector;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;

/**
 * TODO comments
 * This class make the edge extraction using DroG operator.
 * @author Carmelo Pulvirenti pulvirenti.carmelo@libero.it
 * @author rob
 */
public class DroG implements Descriptor {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**
     * This is the type of filter that user has choice.
     */
    private float edgeFilter[];
    private int edge;
    private short gKernel;
    private float gSigma;
    /**
     * This is the threshold value. [0-255]
     */
    private int threshold;

    public DroG() {
        edge = 0;
        gKernel = 3;
        gSigma = 0.5f;
        threshold = 20;

        if (edge == 0) {
            edgeFilter = Service.DroGX(gKernel, gSigma);
        } else {
            edgeFilter = Service.DroGY(gKernel, gSigma);
        }
    }

    /**
     * 
     * @param edgeXY Choose 0 if you want to detect vertical edges or 1 if you want to detect horizontal edges, default = 0 (X-Edge)
     * @param gaussianKernelSize the size of the gaussian kernel, has to be an odd number, default 3
     * @param gaussianSigma Gaussian Sigma, has to be greater than 0 and positive, default 0.5
     * @param threshold Threshold number from 0-255, default 20
     */
    public DroG(int edgeXY, short gaussianKernelSize, float gaussianSigma, int threshold) {
        this.edge = edgeXY;
        this.gKernel = gaussianKernelSize;
        this.gSigma = gaussianSigma;
        this.threshold = threshold;

        if (!(edge == 0 || edge == 1)) {
            throw new IllegalArgumentException("Nonexistent Edge selected!");
        }
        if (gKernel <= 0) {
            throw new IllegalArgumentException("Gaussian Kernel Size is negative or zero!");
        }
        if (gKernel % 2 == 0) {
            throw new IllegalArgumentException("Kernel is not odd!");
        }
        if (gSigma <= 0) {
            throw new IllegalArgumentException("Gaussian Sigma is negative or zero!");
        }
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException("Threshold is not between 0 and 255!");
        }
        if (edge == 0) {
            edgeFilter = Service.DroGX(gKernel, gSigma);
        } else {
            edgeFilter = Service.DroGY(gKernel, gSigma);
        }
    }

    @Override
    public EnumSet<Supports> supports() {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ByteProcessor bp = Service.getByteProcessor(ip);
        ImageProcessor floatForConv = bp.convertToFloat();
        Convolver conv = new Convolver();
        conv.setNormalize(false);
        conv.convolve(floatForConv, this.edgeFilter, (int) Math.sqrt(this.edgeFilter.length), (int) Math.sqrt(this.edgeFilter.length));
        bp = Service.getByteProcessor(floatForConv);
        ImageProcessor out = bp.duplicate();
        //ImagePlus newImg = new ImagePlus("DroG Result", out);
        //newImg.show();

        // Lut processor.
        int lut[] = new int[256];
        int i = 0;
        for (; i < threshold; i++) {
            lut[i] = 0;
        }
        for (int j = i; j < lut.length; j++) {
            lut[j] = 255;
        }
        bp.applyTable(lut);
        //ImagePlus newImgLut = new ImagePlus("Lut result after DroG", bp);
        //newImgLut.show();

        ip.insert(bp, 0, 0);

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}

/**
 * This is a service class.
 */
class Service {

    /*************************************************************************
     * public constants
     *************************************************************************/
    /**
     * SobelX kernel.
     */
    public final static float[] sobelX = new float[]{
        -1, -2, -1,
        0, 0, 0,
        1, 2, 1
    };
    /**
     * SobelY kernel.
     */
    public final static float[] sobelY = new float[]{
        -1, 0, 1,
        -2, 0, 2,
        -1, 0, 1
    };
    /**
     * PrewittX kernel.
     */
    public final static float[] prewittX = new float[]{
        -1, -1, -1,
        0, 0, 0,
        1, 1, 1
    };
    /**
     * PrewittY kernel.
     */
    public final static float[] prewittY = new float[]{
        -1, 0, 1,
        -1, 0, 1,
        -1, 0, 1
    };

    /***********************************************************************************
     * Methods of Support
     * *********************************************************************************/
    /**
     * This method copies an ImageProcessor in a ByteProcessor.
     * @param ip the input ImageProcessor.
     * @return ByteProcessor.
     */
    public static ByteProcessor getByteProcessor(ImageProcessor ip) {
        ByteProcessor bp = new ByteProcessor(ip.getWidth(), ip.getHeight());
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                bp.set(x, y, ip.getPixel(x, y));
            }
        }
        return bp;
    }

    /**
     * This method makes a LoG kernel in an array[].
     * @param window number of row and columns of LoG matrix. It must be an odd.
     * @param sigma value of gaussian.
     * @return the LoG array.
     * @throws IllegalArgumentException if the window is negative zero or it is not odd.
     *                                  If sigma is zero or negative.
     */
    public static float[] initLaplacianOfGaussian(short window, float sigma) throws IllegalArgumentException {
        controlInput(window, sigma);

        short aperture = (short) (window / 2);
        double[][] LacianOfGaussian = new double[2 * aperture + 1][2 * aperture + 1];
        float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
        double gauss[][] = new double[2 * aperture + 1][2 * aperture + 1];
        int k = 0;
        double sum = 0;
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                gauss[dx + aperture][dy + aperture] = Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                sum += gauss[dx + aperture][dy + aperture];
            }
        }
        // Normalizzation of gaussian
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                gauss[dx + aperture][dy + aperture] = gauss[dx + aperture][dy + aperture] / sum;
            }
        }



        sum = 0;
        //Laplacian
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                LacianOfGaussian[dx + aperture][dy + aperture] = gauss[dx + aperture][dy + aperture] * (dx * dx + dy * dy - 2 * Math.pow(sigma, 2)) / Math.pow(sigma, 4);
                sum += LacianOfGaussian[dx + aperture][dy + aperture];
            }
        }
        sum = sum / (window * window);
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                out[k++] = (float) (LacianOfGaussian[dx + aperture][dy + aperture] - sum);
            }
        }

        return out;
    }

    /**
     * This method makes a symmetrical gaussian in an array[].
     * @param window number of row and columns of gaussian matrix. It must be an odd.
     * @param sigma value of gaussian.
     * @return the gaussian array.
     * @throws IllegalArgumentException if the window is negative zero or it is not odd.
     *                                  If sigma is zero or negative.
     */
    public static float[] initGaussianKernel(short window, float sigma) throws IllegalArgumentException {
        controlInput(window, sigma);
        short aperture = (short) (window / 2);
        float[][] gaussianKernel = new float[2 * aperture + 1][2 * aperture + 1];
        float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
        int k = 0;
        float sum = 0;
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                gaussianKernel[dx + aperture][dy + aperture] = (float) Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                sum += gaussianKernel[dx + aperture][dy + aperture];
            }
        }
        for (int dy = -aperture; dy <= aperture; dy++) {
            for (int dx = -aperture; dx <= aperture; dx++) {
                out[k++] = gaussianKernel[dx + aperture][dy + aperture] / sum;
            }
        }
        return out;
    }

    /**
     * This function makes a discrete DroGX kernel in a float array.
     * DroGX(x,y,sigma) = (-y/sigma^2)* EXP(-(x^2+y^2)/(2*sigma^2))
     * @param window number of row and columns of gaussian matrix. It must be an odd.
     * @param sigma value of gaussian.
     * @return the array with DroGX kernel.
     * @throws IllegalArgumentException if the window is negative zero or it is not odd.
     *                                  If sigma is zero or negative.
     */
    public static float[] DroGX(short window, float sigma) throws IllegalArgumentException {
        controlInput(window, sigma);
        short aperture = (short) (window / 2);
        float[][] Drog = new float[2 * aperture + 1][2 * aperture + 1];
        float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
        int k = 0;
        float sum = 0;
        for (float dy = -aperture; dy <= aperture; dy = dy + 1) {
            for (float dx = -aperture; dx <= aperture; dx = dx + 1) {
                float r = ((dx * dx) + (dy * dy));
                float one = (float) -(dy / Math.pow(sigma, 2));
                float two = (float) (-r / (2.0 * Math.pow(sigma, 2)));
                float three = (float) Math.exp(two);
                Drog[(int) (dx + aperture)][(int) (dy + aperture)] = (float) (one * three);
                sum += Drog[(int) (dx + aperture)][(int) (dy + aperture)];
            }
        }
        sum = sum / (window * window);
        for (float dy = -aperture; dy <= aperture; dy = dy + 1) {
            for (float dx = -aperture; dx <= aperture; dx = dx + 1) {
                out[k++] = Drog[(int) (dx + aperture)][(int) (dy + aperture)] - sum;
            }
        }
        return out;
    }

    /**
     * This function makes a discrete DroGY kernel in a float array.
     * DroGY(x,y,sigma) = (-x/sigma^2)* EXP(-(x^2+y^2)/(2*sigma^2))
     * @param window number of row and columns of the gaussian matrix. It must be an odd.
     * @param sigma value of gaussian.
     * @return the array with DroGY kernel.
     * @throws IllegalArgumentException if the window is negative zero or it is not odd.
     *                                  If sigma is zero or negative.
     */
    public static float[] DroGY(short window, float sigma) throws IllegalArgumentException {
        controlInput(window, sigma);
        short aperture = (short) (window / 2);
        float[][] Drog = new float[2 * aperture + 1][2 * aperture + 1];
        float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
        int k = 0;
        float sum = 0;
        for (float dy = -aperture; dy <= aperture; dy = dy + 1) {
            for (float dx = -aperture; dx <= aperture; dx = dx + 1) {
                float r = ((dx * dx) + (dy * dy));
                float one = (float) -(dx / Math.pow(sigma, 2));
                float two = (float) (-r / (2.0 * Math.pow(sigma, 2)));
                float three = (float) Math.exp(two);
                Drog[(int) (dx + aperture)][(int) (dy + aperture)] = (float) (one * three);
                sum += Drog[(int) (dx + aperture)][(int) (dy + aperture)];
            }
        }
        sum = sum / (window * window);
        for (float dy = -aperture; dy <= aperture; dy = dy + 1) {
            for (float dx = -aperture; dx <= aperture; dx = dx + 1) {
                out[k++] = Drog[(int) (dx + aperture)][(int) (dy + aperture)] - sum;
            }
        }
        return out;
    }

    /**
     * This method checks the gaussian value.
     * @param window the window of the gaussian.
     * @param sigma the gaussian sigma.
     * @throws IllegalArgumentException if the window is negative zero or it is not odd.
     *                                  If sigma is zero or negative.
     */
    private static void controlInput(short window, float sigma) throws IllegalArgumentException {
        if (window % 2 == 0) {
            throw new IllegalArgumentException("Window isn't an odd.");
        }
        if (window <= 0) {
            throw new IllegalArgumentException("Window is negative or zero");
        }
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma of the gaussian is zero or negative.");
        }
    }
}
