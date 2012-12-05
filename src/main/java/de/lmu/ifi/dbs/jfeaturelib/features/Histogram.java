package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;
import java.util.EnumSet;

/**
 * Class that generates several types of histograms.
 *
 * This class replaces RGBHistogram and GrayHistogram in previous versions.
 *
 * @author graf
 */
public class Histogram extends AbstractFeatureDescriptor {

    public static enum TYPE {

        RGB, Red, Green, Blue, HSB, Hue, Saturation, Brightness, Gray
    };
    TYPE type;
    int bins;

    public Histogram() {
    }

    @Override
    public void setProperties(LibProperties properties) {
        type = TYPE.valueOf(properties.getString(LibProperties.HISTOGRAMS_TYPE));
        bins = properties.getInteger(LibProperties.HISTOGRAMS_BINS);
    }

    /**
     * Starts the histogram detection.
     *
     * @param ip ImageProcessor of the source image
     */
    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        InnerHistogram histogram = null;
        if (type == TYPE.Gray) {
            histogram = new Gray();

        } else if (type == TYPE.RGB) {
            histogram = new RGB();
        } else if (type == TYPE.Red) {
            histogram = new RgbMix(1, 0, 0);
        } else if (type == TYPE.Green) {
            histogram = new RgbMix(0, 1, 0);
        } else if (type == TYPE.Blue) {
            histogram = new RgbMix(0, 0, 1);

        } else if (type == TYPE.HSB) {
            histogram = new HSB();
        } else if (type == TYPE.Hue || type == TYPE.Saturation || type == TYPE.Brightness) {
            histogram = new HsbMix(type);
        }

        int[] hist = histogram.run(ip);
        double[] features = Arrays2.convertToDouble(hist);
        features = scale(features, bins);

        addData(features);
        firePropertyChange(Progress.END);
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB,
                Supports.Masking);
        return set;
    }

    /**
     * Reduces the dimensions of src to a length of newLength.
     *
     * For example if the input array is 265 and newLength is 64, then the 256d
     * are shifted/compressed down to the new 64. In example, dimension 1-4 will
     * be added up in bin 1 of the target array
     *
     * @param src
     * @param newLength
     * @return new double array with newLength size
     */
    double[] scale(double[] src, int newLength) {
        if (src.length != newLength) {
            double[] target = new double[newLength];
            double factor = 1d * newLength / src.length;
            // newLength: 64
            // doubleArr: 256
            // factor: 1/4
            for (int srcI = 0; srcI < src.length; srcI++) {
                int j = (int) Math.floor(srcI * factor);
                target[j] += src[srcI];
            }

            src = target;
        }
        return src;
    }

    private interface InnerHistogram {

        public int[] run(ImageProcessor ip);
    }

    private class Gray implements InnerHistogram {

        @Override
        public int[] run(ImageProcessor ip) {
            if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
                ip = ip.convertToByte(true);
            }
            return ((ByteProcessor) ip).getHistogram();
        }
    }

    private class RGB implements InnerHistogram {

        @Override
        public int[] run(ImageProcessor image) {
            int[] features = new int[256 * 3];

            ColorProcessor.setWeightingFactors(1, 0, 0);
            System.arraycopy(image.getHistogram(), 0, features, 0, 256);
            firePropertyChange(new Progress(33));

            ColorProcessor.setWeightingFactors(0, 1, 0);
            System.arraycopy(image.getHistogram(), 0, features, 256, 256);
            firePropertyChange(new Progress(66));

            ColorProcessor.setWeightingFactors(0, 0, 1);
            System.arraycopy(image.getHistogram(), 0, features, 512, 256);
            firePropertyChange(new Progress(99));

            return features;
        }
    }

    private class RgbMix implements InnerHistogram {

        private final double r;
        private final double g;
        private final double b;

        public RgbMix(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public int[] run(ImageProcessor image) {
            ColorProcessor.setWeightingFactors(r, g, b);
            return image.getHistogram();
        }
    }

    private class HSB implements InnerHistogram {

        @Override
        public int[] run(ImageProcessor ip) {
            ColorProcessor cp;
            if (ip instanceof ColorProcessor) {
                cp = (ColorProcessor) ip;
            } else {
                cp = (ColorProcessor) ip.convertToRGB();
            }

            int width = ip.getWidth();
            int height = ip.getHeight();
            byte[] H = new byte[width * height];
            byte[] S = new byte[width * height];
            byte[] B = new byte[width * height];
            cp.getHSB(H, S, B);

            int[] features = new int[256 * 3];
            ByteProcessor channel = new ByteProcessor(width, height, H, cp.getDefaultColorModel());
            channel.setMask(ip.getMask());

            System.arraycopy(channel.getHistogram(), 0, features, 0, 256);
            firePropertyChange(new Progress(33));

            channel.setPixels(S);
            System.arraycopy(channel.getHistogram(), 0, features, 256, 256);
            firePropertyChange(new Progress(66));

            channel.setPixels(B);
            System.arraycopy(channel.getHistogram(), 0, features, 512, 256);
            firePropertyChange(new Progress(99));

            return features;
        }
    }

    private class HsbMix implements InnerHistogram {

        private final TYPE type;

        private HsbMix(TYPE type) {
            this.type = type;
        }

        @Override
        public int[] run(ImageProcessor ip) {
            ColorProcessor cp;
            if (ip instanceof ColorProcessor) {
                cp = (ColorProcessor) ip;
            } else {
                cp = (ColorProcessor) ip.convertToRGB();
            }

            int width = ip.getWidth();
            int height = ip.getHeight();
            byte[] H = new byte[width * height];
            byte[] S = new byte[width * height];
            byte[] B = new byte[width * height];
            cp.getHSB(H, S, B);

            ByteProcessor channel;
            ColorModel colorModel = cp.getDefaultColorModel();
            if (type == TYPE.Hue) {
                channel = new ByteProcessor(width, height, H, colorModel);
            } else if (type == TYPE.Saturation) {
                channel = new ByteProcessor(width, height, S, colorModel);
            } else if (type == TYPE.Brightness) {
                channel = new ByteProcessor(width, height, B, colorModel);
            } else {
                throw new IllegalArgumentException("type must be H,S or B");
            }

            return channel.getHistogram(ip.getMask());
        }
    }

    @Override
    public String getDescription() {
        return "Histograms";
    }
}
