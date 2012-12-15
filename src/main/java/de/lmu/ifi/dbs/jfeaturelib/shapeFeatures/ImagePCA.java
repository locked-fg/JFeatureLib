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
package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import ij.process.ImageProcessor;
import java.awt.Point;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Utility class for 2d image PCA.
 *
 * As long as it is not fully clear that this class is used somewhere else as
 * well, we'll keep it backage private.
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
class ImagePCA {

    private static final Logger log = Logger.getLogger(ImagePCA.class.getName());
    private ImageProcessor ip2;
    private double angle = 0;

    public ImagePCA(ImageProcessor ip, int backgroundColor) {
        ip2 = ip;
        ip = ip.convertToByte(true);
        ArrayList<Point> points = new ArrayList<>();
        double xx = 0, xy = 0, yx = 0, yy = 0;
        double counter = 0;
        double meanX = 0, meanY = 0;

        //1) retrieve relevant points
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (ip.getPixel(i, j) != backgroundColor) {
                    points.add(new Point(i, j));
                    counter++;
                    meanX = ((counter - 1.0) / counter) * meanX + 1.0 / counter * i;
                    meanY = ((counter - 1.0) / counter) * meanY + 1.0 / counter * j;
                }
            }
        }

        //2) compute covariances
        double c = 0;
        for (Point p : points) {
            c++;
            xx = ((c - 1) / c) * xx + (1 / c) * (p.getX() - meanX) * (p.getX() - meanX);
            yy = ((c - 1) / c) * yy + (1 / c) * (p.getY() - meanY) * (p.getY() - meanY);
            xy = ((c - 1) / c) * xy + (1 / c) * (p.getY() - meanY) * (p.getX() - meanX);
        }
        yx = xy;

        //3) compute eigenvalues
        double lambda1 = 0.5 * yy + 0.5 * xx + 0.5 * Math.sqrt(yy * yy - 2 * yy * xx + xx * xx + 4 * xy * xy);
        double lambda2 = 0.5 * yy + 0.5 * xx - 0.5 * Math.sqrt(yy * yy - 2 * yy * xx + xx * xx + 4 * xy * xy);

        double[] eigenVector1 = new double[]{(-xy / (lambda2 - yy)), 1};
        double[] eigenVector2 = new double[]{(-xy / (lambda1 - yy)), 1};
        double[] ev;

        if (lambda2 > lambda1) {
            ev = eigenVector2;
        } else {
            ev = eigenVector1;
        }
        double evlen = Math.sqrt(ev[0] * ev[0] + ev[1] * ev[1]);
        double cosa = ev[0] * 1.0 / evlen;
        angle = Math.acos(cosa);

        log.debug("PCA2D-Angle: " + (-(angle / Math.PI) * 180));
        //uncomment me if you want to see the rotated image...
        ip2 = ip2.duplicate();
        ip2.invert();
        ip2.rotate(-(angle / Math.PI) * 180);
        ip2.invert();
    }

    /*rotate the image counter clockwise by the returned angle and the resulting images principal component is parallel to the x-axis...*/
    public double getAngle() {
        return this.angle;
    }

    public ImageProcessor getResultImage() {
        return this.ip2;
    }
}
