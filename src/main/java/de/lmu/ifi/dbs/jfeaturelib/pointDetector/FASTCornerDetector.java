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
 * 
 * ##########################################################
 * FAST Corner Detector
 * 
 * FAST was first published By Edward Rosten in 2005, 2006:<br>
 * - Fusing points and lines for high performance tracking (2005) and<br>
 * - Machine learning for high-speed corner detection (2006).
 * 
 * Please see http://www.edwardrosten.com/work/fast.html for links to the according papers, more information 
 * and other reference implementations. At this page you can also find BSD-licensed code.
 * 
 * In case of using this code, the above copyright notive must retain the attribution to the author.
 */
package de.lmu.ifi.dbs.jfeaturelib.pointDetector;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST.FAST10;
import de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST.FAST11;
import de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST.FAST12;
import de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST.FAST9;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * FAST Corner Detector
 * 
 * FAST was first published By Edward Rosten in 2005, 2006:<br>
 * - Fusing points and lines for high performance tracking (2005) and<br>
 * - Machine learning for high-speed corner detection (2006).
 * 
 * Please see http://www.edwardrosten.com/work/fast.html for links to the according papers, more information 
 * and other reference implementations. At this page you can also find BSD-licensed code.
 * 
 * In case of using this code, the above copyright notive must retain the attribution to the author.
 * 
 * @author Edward Rosten
 * @author Robert Zelhofer
 */
public class FASTCornerDetector implements PointDetector {
    //Return List of ImagePoints
    List<ImagePoint> corners;
    //Threshold Value
    private int threshold;
    //Contiguous Pixels Number
    private int fastNNumber;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @Override
    public List<ImagePoint> getPoints() {
        return corners;
    }

    @Override
    public EnumSet<Supports> supports() {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ImageProcessor ipConverted = ip.convertToByte(true).convertToRGB().duplicate();
        int[] ipArray = (int[]) ipConverted.getPixels();

        int height = ipConverted.getHeight();
        int width = ipConverted.getWidth();
        
        List<ImagePoint> retList = new ArrayList<>();
        
        switch (fastNNumber) {
            case 9:
                FAST9 f9 = new FAST9();
                corners = f9.fast9_detect_nonmax(ipArray, width, height, width, threshold, retList);
                break;
            case 10:
                FAST10 f10 = new FAST10();
                corners = f10.fast10_detect_nonmax(ipArray, width, height, width, threshold, retList);
                break;
            case 11:
                FAST11 f11 = new FAST11();
                corners = f11.fast11_detect_nonmax(ipArray, width, height, width, threshold, retList);
                break;
            case 12:
                FAST12 f12 = new FAST12();
                corners = f12.fast12_detect_nonmax(ipArray, width, height, width, threshold, retList);
                break;
            default:
                break;
        }
        
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    /**
     * Creates default FAST Corner Detector with threshold = 20 and test for 9 contiguous Pixels
     */
    public FASTCornerDetector() {
        this(20, 9);
    }
    
    /**
     * Creates Fast Corner Detector for 9 contiguous Pixels
     * @param threshold Threshold Value
     */
    public FASTCornerDetector(int threshold) {
        this(threshold, 9);
    }
    /**
     * Creates FAST Corner Detector
     * @param threshold Integer for Threshold value.
     * @param fastNNumber Integer n. Fast algorithm Tests whether there are n contiguous Pixels int the circle which are all darker, or all brighter than Pixel p. This Test does not generalize well for n < 12. There for n can only range from 9 to 12.
     */
    public FASTCornerDetector(int threshold, int fastNNumber) {
        this.corners = new ArrayList<>();
        this.threshold = threshold;
        if (fastNNumber < 9 || fastNNumber > 12) {
            throw new IllegalArgumentException("Fast N Number is not 9 - 12");
        }
        this.fastNNumber = fastNNumber;
    }
}
