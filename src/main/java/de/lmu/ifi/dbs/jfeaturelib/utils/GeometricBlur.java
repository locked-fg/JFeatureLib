/**
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
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
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Gaussian Blur where sigma is modified according to spacial position to the
 * "query point".
 *
 * Can be used as a component for geometric blur. Geom.blur first separates the
 * input image into 4 channels of half-wave rectified responses from oriented
 * edge filters.
 *
 * The idea was proposed by Alexander Berg: http://acberg.com/gb.html
 *
 * Relevant papers: Shape Matching and Object Recognition using Low Distortion
 * Correspondence Alexander C. Berg, Tamara L. Berg, Jitendra Malik IEEE
 * Computer Vision and Pattern Recognition (CVPR) 2005 (Presents a descriptor
 * based on geometric blur and applies this to object recognition.)
 *
 * Geometric Blur for Template Matching Alexander C. Berg, Jitendra Malik
 * Computer Vision and Pattern Recognition (CVPR) 2001, Hawaii, pp I.607-614
 * (The original geometric blur paper concentrating on template matching and
 * localization.)
 *
 * Shape Matching and Object Recognition Alexander C. Berg Ph.D. Thesis,
 * Computer Science Division, U.C. Berkeley, December 2005 (Presents more detail
 * on the motivation for geometric blur, study of corresponding image patches,
 * etc.)
 *
 * @author Graf
 */
public class GeometricBlur {

    private final Distance distance = new DistanceL2();
    private ImageProcessor ip = null;
    private double sigma = 5;
    private int blursteps = 20;
    private List<ImageProcessor> myStack = new ArrayList<>();

    public GeometricBlur() {
    }

    /**
     * @param ip image processor of source image
     * @param sig maximum sigma of gauss blur
     * @param steps how many slices of images (with differing sigma) should be
     * created.
     */
    public GeometricBlur(ImageProcessor ip, double sig, int steps) {
        this.sigma = sig;
        this.blursteps = steps;
        this.ip = ip;
        update();
    }

    /**
     * @param out write blurred image into this image processor
     * @param p point in the source image being the center of the query
     */
    public void paintInto(final ImageProcessor out, final ImagePoint center) {
        if (out == null) {
            throw new NullPointerException("ImageProcessor must not be null");
        }

        int ow = out.getWidth();
        int oh = out.getHeight();

        ImagePoint p = new ImagePoint();
        Rectangle bound = new Rectangle(ip.getWidth(), ip.getHeight());
        int offsetX = (int) (center.x - ow / 2d);
        int offsetY = (int) (center.y - oh / 2d);
        for (int x = 0; x < ow; x++) {
            for (int y = 0; y < oh; y++) {
                // pixel coords of window in bigger source image
                p.x = offsetX + x;
                p.y = offsetY + y;
                if (bound.contains(p.x, p.y)) {
                    out.putPixel(x, y, getPixel(p, center));
                }
            }
        }
    }

    /**
     * Returns the spacially blurred pixel value at the point q, assuming that
     * center is the source of the blur.
     *
     * @param q pixel coordinate that should be retrieved
     * @param center the source of the blur
     * @return pixel value from {@link ImageProcessor#getPixel(int, int)}
     */
    public int getPixel(ImagePoint q, ImagePoint center) {
        return myStack.get(getSlice(q, center)).getPixel((int) q.x, (int) q.y);
    }

    /**
     * Returns the spacially blurred pixel value at the point q, assuming that
     * center is the source of the blur.
     *
     * @param q pixel coordante which should be retrieved
     * @param center the source of the blur
     * @return pixel value from {@link ImageProcessor#getPixelValue(int, int)}
     */
    public float getPixelValue(ImagePoint q, ImagePoint center) {
        return myStack.get(getSlice(q, center)).getPixelValue((int) q.getX(), (int) q.getY());
    }

    private int getSlice(ImagePoint q, ImagePoint center) {
        double dst = distance.distance(q, center);
        int slice = (int) dst;
        if (slice < 0) {
            slice = 0;
        } else if (slice > myStack.size() - 1) {
            slice = myStack.size() - 1;
        }
        return slice;
    }

    /**
     * create the geometric blur stack
     */
    private void update() {
        myStack.clear();
        if (ip == null) {
            return;
        }
        // 0 = unmodified
        myStack.add(ip.duplicate());
        for (int i = 1; i <= blursteps; i++) {
            ImageProcessor blurred = ip.duplicate();
            double tmpSigma = i * sigma / (1d * blursteps);
            GaussianBlur gb = new GaussianBlur();
            gb.blurGaussian(blurred, tmpSigma, tmpSigma, 0.01);
            myStack.add(blurred);
        }
    }

    public ImageProcessor getIp() {
        return ip;
    }

    public void setIp(ImageProcessor ip) {
        this.ip = ip;
        update();
    }

    public int getBlursteps() {
        return blursteps;
    }

    public void setBlursteps(int blursteps) {
        this.blursteps = blursteps;
        update();
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
        update();
    }
}
