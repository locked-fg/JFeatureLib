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
package de.lmu.ifi.dbs.jfeaturelib.pointDetector;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * Moravec Corner Detector.
 *
 * This implementation is based on the Original Paper from Moravec, 1980. See link below. And also based on the Aricle
 * from Donovan Parks and Jean-Philippe Gravel. Also see link below. This implementation does differ from the Wikipedia
 * Article about the Moravec Detector!
 *
 * See http://www.ri.cmu.edu/pub_files/pub4/moravec_hans_1980_1/moravec_hans_1980_1.pdf and
 * http://kiwi.cs.dal.ca/~dparks/CornerDetection/moravec.htm for more information
 *
 * @author Robert Zelhofer
 */
public class Moravec implements PointDetector {
    //Returning List of Corners

    private List<ImagePoint> corners;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    //Threshold value
    private int threshold;
    //windowsize value
    private int size;
    //static window sizes
    //windows for the 8 Principle Directions around the current Pixel
    private final int[] xPricipleDelta = new int[]{
        -1, 0, 1,
        -1, 1,
        -1, 0, 1
    };
    private final int[] yPrincipleDelta = new int[]{
        -1, -1, -1,
        0, 0,
        1, 1, 1
    };
    //Shifted Window for both the orignial Pixel and the shifted Pixels
    private int[] xRedDelta;
    private int[] yRedDelta;
    /*
     * This is how a window for windowsize = 3 looks like
     private int[] xRedDelta = new int[] { 
     -1, 0,  1, 
     -1, 0,  1,
     -1,  0,  1 
     };
     private int[] yRedDelta = new int[] { 
     -1, -1, -1, 
     0,  0,  0,
     1,  1,  1 
     };
     */

    //Shifted Window for both the orignial Pixel and the shifted Pixels
    //private ArrayList<ImagePoint> redDeltaList;
    @Override
    public List<ImagePoint> getPoints() {
        return corners;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        this.generateWindow(size);

        int width = ip.getWidth();
        int height = ip.getHeight();
        //radius is used to prevent the Algorithm to search out of bounds
        int radius = size / 2;

        //Check each Pixel
        for (int y = radius, maxY = height - radius; y < maxY; y++) {
            for (int x = radius, maxX = width - radius; x < maxX; x++) {
                int minSum = Integer.MAX_VALUE;

                //check the shifting directions
                for (int k = 0; k < 8; k++) {
                    // sy and sx is the Center Point of the Shifted Window
                    int sy = y + yPrincipleDelta[k];
                    int sx = x + xPricipleDelta[k];

                    // Shifted Window out of Bounds Check
                    if (sy < radius || sx < radius || sy >= maxY || sx >= maxX) {
                        continue;
                    }

                    int sum = 0;

                    // Sum up the difference of the window around the actual pixel and the shifted window

                    for (int i = 0; i < xRedDelta.length; i++) {
                        int redX = x + xRedDelta[i];
                        int redY = y + yRedDelta[i];
                        int redValue = ip.getPixel(redX, redY);
                        int blueX = sx + xRedDelta[i];
                        int blueY = sy + yRedDelta[i];
                        int blueValue = ip.getPixel(blueX, blueY);
                        int dif = redValue - blueValue;
                        sum += dif * dif;
                    }

                    if (sum < minSum) {
                        minSum = sum;
                    }
                }

                //System.out.print("\t" + minSum+ "\t|");

                //Threshold check
                if (minSum < threshold) {
                    minSum = 0;
                } else {
                    corners.add(new ImagePoint(x, y));
                }

            }
            //System.out.println();

        }

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Creates Moravec Detector with Default values: Threshold = 500
     */
    public Moravec() {
        this(500, 3);
    }

    /**
     * Creates Moravec Detector
     *
     * @param threshold Threshold Value, which is used for filtering uninteresting Points
     */
    public Moravec(int threshold) {
        this(threshold, 3);
    }

    /**
     * Creates Moravec Detector
     *
     * @param threshold Threshold Value, which is used for filtering uninteresting Points
     * @param size Window Size. Must be Odd and >= 3. Used for Initial Search for Corners and Search for Local Maximums.
     */
    public Moravec(int threshold, int size) {
        this.threshold = threshold;
        if (size < 3) {
            throw new IllegalArgumentException("Window Size is smaller than 3!");
        }
        if (size % 2 == 0) {
            throw new IllegalArgumentException("Window Size is not odd!");
        }
        this.size = size;
        corners = new ArrayList<>();
    }

    /**
     *
     * create the shifted Windows for calculation the Itensity Differences creates for window size = 7: -3 -3 -2 -3 -1
     * -3 0 -3 1 -3 2 -3 3 -3 -3 -2 3 -2 -3 -1 3 -1 -3 0 0 0 3 0 -3 1 3 1 -3 2 3 2 -3 3 -2 3 -1 3 0 3 1 3 2 3 3 3
     *
     * @param size Window Size
     * @return List of ImagePoints
     */
    private void generateWindow(int size) {
        int redDeltaLength = (size - 1) * 4;
        xRedDelta = new int[redDeltaLength];
        yRedDelta = new int[redDeltaLength];

        //ArrayList<ImagePoint> tempList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int radius = size / 2;
                if (Math.abs(j - radius) == radius || Math.abs(i - radius) == radius) {
                    //System.out.print((j-radius) + " " + (i-radius) + "\t");
                    //tempList.add(new ImagePoint(j-radius, i-radius));
                    xRedDelta[i] = j - radius;
                    yRedDelta[i] = i - radius;
                } else {
                    //System.out.print("\t");
                }

            }
            //System.out.println();
            //System.out.println();
        }
        //tempList.add(new ImagePoint(0, 0));
        xRedDelta[redDeltaLength - 1] = 0;
        yRedDelta[redDeltaLength - 1] = 0;
    }
}
