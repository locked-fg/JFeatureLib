/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import com.google.common.base.Preconditions;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
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
        } else {
            throw new IllegalStateException("no valid histogram type selected: " + type);
        }

        setMask(ip);
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
     * For example if the input array is 265 and newLength is 64, then the 256d are shifted/compressed down to the new
     * 64. In example, dimension 1-4 will be added up in bin 1 of the target array
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

    /**
     * Copied from imageJ as ImageJ unfortunately uses static fields for the weights which causes race conditions in
     * multi threaded environments.
     *
     * @see ColorProcessor#getHistogram(ij.process.ImageProcessor)
     * @param ip
     * @param rWeight
     * @param gWeight
     * @param bWeight
     * @return 3*256 bin histogram
     */
    private int[] getHistogram(ImageProcessor ip, double rWeight, double gWeight, double bWeight) {
        ImageProcessor mask = getMask();
        if (mask != null) {
            return getHistogram(ip, mask, rWeight, gWeight, bWeight);
        }
        Rectangle roi = ip.getRoi();
        int roiX = roi.x;
        int roiY = roi.y;
        int roiWidth = roi.width;
        int roiHeight = roi.height;
        int width = ip.getWidth();

        int c, r, g, b, v;
        int[] histogram = new int[256];
        for (int y = roiY; y < (roiY + roiHeight); y++) {
            int i = y * width + roiX;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                c = ip.get(i++);
                // c = pixels[i++];
                r = (c & 0xff0000) >> 16;
                g = (c & 0xff00) >> 8;
                b = c & 0xff;
                v = (int) (r * rWeight + g * gWeight + b * bWeight + 0.5);
                histogram[v]++;
            }
        }
        return histogram;
    }

    /**
     * Copied from imageJ as ImageJ unfortunately uses static fields for the weights which causes race conditions in
     * multi threaded environments.
     *
     * @see ColorProcessor#getHistogram(ij.process.ImageProcessor)
     * @param ip
     * @param mask
     * @return 3*256 bin histogram
     */
    private int[] getHistogram(ImageProcessor ip, ImageProcessor mask,
            double rWeight, double gWeight, double bWeight) {
        Rectangle roi = ip.getRoi();
        int roiX = roi.x;
        int roiY = roi.y;
        int roiWidth = roi.width;
        int roiHeight = roi.height;
        if (mask.getWidth() != roiWidth || mask.getHeight() != roiHeight) {
            throw new IllegalArgumentException("Mask size != ROI size");
        }
        int width = ip.getWidth();
        int c, r, g, b, v;
        int[] histogram = new int[256];
        for (int y = roiY, my = 0; y < (roiY + roiHeight); y++, my++) {
            int i = y * width + roiX;
            int mi = my * roiWidth;
            for (int x = roiX; x < (roiX + roiWidth); x++) {
                if(mask.get(mi++) != 0) {
                    c = ip.get(i);
                    // c = pixels[i];
                    r = (c & 0xff0000) >> 16;
                    g = (c & 0xff00) >> 8;
                    b = c & 0xff;
                    v = (int) (r * rWeight + g * gWeight + b * bWeight + 0.5);
                    histogram[v]++;
                }
                i++;
            }
        }
        return histogram;
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
            System.arraycopy(getHistogram(image, 1, 0, 0), 0, features, 0, 256);
            firePropertyChange(new Progress(33));

            System.arraycopy(getHistogram(image, 0, 1, 0), 0, features, 256, 256);
            firePropertyChange(new Progress(66));

            System.arraycopy(getHistogram(image, 0, 0, 1), 0, features, 512, 256);
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
            return getHistogram(image, r, g, b);
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
