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

import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import de.lmu.ifi.dbs.jfeaturelib.utils.Distance;
import de.lmu.ifi.dbs.jfeaturelib.utils.DistanceL2;
import ij.process.ImageProcessor;
import java.util.ArrayList;

/**
 * Computes extremal points according to "Bryan S. Morse (2000): Lecture 9:
 * Shape Description (Regions), Brigham Young University, Available from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class ExtremalPoints extends AbstractFeatureDescriptor {

    private static final Distance dist = new DistanceL2();
    private ArrayList<Integer> feature = new ArrayList();
    private int backgroundColor = 255;

    ArrayList getFeature() {
        return feature;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return dist.distance(x1, y1, x2, y2);
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();

        int i = 0;
        //top left & top right
        while (i < ip.getHeight()) {
            for (int j = 0; j < ip.getWidth(); j++) {
                if (ip.getPixel(j, i) != backgroundColor) {
                    feature.add(j);
                    feature.add(i);
                    for (int k = ip.getWidth() - 1; k > 0; k--) {
                        if (ip.getPixel(k, i) != backgroundColor) {
                            feature.add(k);
                            feature.add(i);
                            break;
                        }
                    }
                    i = ip.getHeight();
                    break;
                }
            }
            i++;
        }

        //right top & right bottom
        i = ip.getWidth() - 1;
        while (i > 0) {
            for (int i2 = 0; i2 < ip.getHeight(); i2++) {
                if (ip.getPixel(i, i2) != backgroundColor) {
                    feature.add(i);
                    feature.add(i2);
                    for (int k = ip.getHeight() - 1; k > 0; k--) {
                        if (ip.getPixel(i, k) != backgroundColor) {
                            feature.add(i);
                            feature.add(k);
                            break;
                        }
                    }
                    i = 0;
                    break;
                }
            }
            i--;
        }

        //bottom right & bottom left
        i = ip.getHeight() - 1;
        while (i > 0) {
            for (int k = ip.getWidth() - 1; k > 0; k--) {
                if (ip.getPixel(k, i) != backgroundColor) {
                    feature.add(k);
                    feature.add(i);
                    for (int j = 0; j < ip.getWidth(); j++) {
                        if (ip.getPixel(j, i) != backgroundColor) {
                            feature.add(j);
                            feature.add(i);
                            break;
                        }

                    }
                    i = 0;
                    break;
                }
            }
            i--;
        }
        //left bottom & left top
        i = 0;
        while (i < ip.getWidth()) {
            for (int k = ip.getHeight() - 1; k > 0; k--) {
                if (ip.getPixel(i, k) != backgroundColor) {
                    feature.add(i);
                    feature.add(k);
                    for (int i2 = 0; i2 < ip.getHeight(); i2++) {
                        if (ip.getPixel(i, i2) != backgroundColor) {
                            feature.add(i);
                            feature.add(i2);
                            break;
                        }
                    }
                    i = ip.getWidth();
                    break;
                }
            }
            i++;
        }


        //Extremal points eccentricity
        double[] array = new double[4];
        array[0] = distance((int) feature.get(0), (int) feature.get(1), (int) feature.get(6),
                (int) feature.get(7));
        array[1] = distance((int) feature.get(2), (int) feature.get(3), (int) feature.get(4),
                (int) feature.get(5));
        array[2] = distance((int) feature.get(8), (int) feature.get(9), (int) feature.get(14),
                (int) feature.get(15));
        array[3] = distance((int) feature.get(10), (int) feature.get(11), (int) feature.get(12),
                (int) feature.get(13));
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i2 = 0; i2 < 4; i2++) {
            max = Math.max(max, array[i2]);
            min = Math.min(min, array[i2]);
        }
        feature.add((int) (max / min));

        createFeature();
        endProgress();
    }

    private void createFeature() {
        double[] arr = new double[feature.size()];
        for (int i = 0; i < feature.size(); i++) {
            arr[i] = feature.get(i);
        }
        addData(arr);
    }

    @Override
    public String getDescription() {
        return "extremal points of the shape";
    }
}
