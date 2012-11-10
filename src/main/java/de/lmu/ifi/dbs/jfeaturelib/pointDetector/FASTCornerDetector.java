/*
 *Copyright (c) 2006, 2008, 2009, 2010 Edward Rosten
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 	*Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *      *Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 * 
 * 	*Neither the name of the University of Cambridge nor the names of 
 *       its contributors may be used to endorse or promote products derived 
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Edward Rosten
 * @author Robert Zelhofer
 * @see http://www.edwardrosten.com/work/fast.html
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
