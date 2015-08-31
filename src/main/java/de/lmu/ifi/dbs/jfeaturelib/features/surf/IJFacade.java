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
package de.lmu.ifi.dbs.jfeaturelib.features.surf;

import ij.IJ;
import ij.process.ImageProcessor;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * SURF library for ImageJ. Based on SURF paper (2008) and OpenSURF C++ implementation. See SURF_Test.java for example
 * of usage.
 *
 * @author Eugen Labun
 */
public class IJFacade {

    private static final Logger log = Logger.getLogger(IJFacade.class);

    private IJFacade() {
    }

    /**
     * Finds interest points using the default parameter.
     */
    public static List<InterestPoint> detectAndDescribeInterestPoints(IntegralImage intImg) {
        return detectAndDescribeInterestPoints(intImg, new Params());
    }

    /**
     * Finds interest points using the provided parameter.
     */
    public static List<InterestPoint> detectAndDescribeInterestPoints(IntegralImage intImg, Params p) {
        long begin, end;

        // Detect interest points with Fast-Hessian
        begin = System.currentTimeMillis();
        List<InterestPoint> ipts = Detector.fastHessian(intImg, p);
        end = System.currentTimeMillis();
        p.getStatistics().timeSURFDetector = end - begin;

        p.getStatistics().detectedIPs = ipts.size();
        float[] strengthOfIPs = new float[ipts.size()];
        for (int i = 0; i < ipts.size(); i++) {
            strengthOfIPs[i] = ipts.get(i).strength;
        }
        Arrays.sort(strengthOfIPs);
        p.getStatistics().strengthOfIPs = strengthOfIPs;


        // Describe interest points with SURF-descriptor
        begin = System.currentTimeMillis();
        if (!p.isUpright()) {
            for (InterestPoint ipt : ipts) {
                Descriptor.computeAndSetOrientation(ipt, intImg);
            }
        }
        for (InterestPoint ipt : ipts) {
            Descriptor.computeAndSetDescriptor(ipt, intImg, p);
        }
        end = System.currentTimeMillis();
        p.getStatistics().timeSURFDescriptor = end - begin;

        return ipts;
    }

    /**
     * Draws interest points onto suplied
     * <code>ImageProcessor</code>.
     */
    public static void drawInterestPoints(ImageProcessor img, List<InterestPoint> ipts, Params params) {
        for (InterestPoint ipt : ipts) {
            drawSingleInterestPoint(img, params, ipt);
        }
    }

    public static void drawSingleInterestPoint(ImageProcessor img, Params p, InterestPoint ipt) {
        int x = round(ipt.x);
        int y = round(ipt.y);
        float w = ipt.scale * 10; // for descriptor window
        float ori = ipt.orientation;
        float co = (float) cos(ori);
        float si = (float) sin(ori);
        float s = ipt.strength * 10000; // for orientation vector

        // The order of drawing is important: 
        // 1) descriptor windows
        // 2) orientation vectors
        // 3) points
        // Otherwise some points could be overdrawed by descriptor- or vector-lines. 

        // Draw descriptor window around the interest point
        if (p.isDisplayDescriptorWindows()) {
            img.setLineWidth(p.getLineWidth());
            img.setColor(p.getDescriptorWindowColor());

            float x0 = w * (si + co) + ipt.x;
            float y0 = w * (-co + si) + ipt.y;
            float x1 = w * (si - co) + ipt.x;
            float y1 = w * (-co - si) + ipt.y;
            float x2 = w * (-si - co) + ipt.x;
            float y2 = w * (co - si) + ipt.y;
            float x3 = w * (-si + co) + ipt.x;
            float y3 = w * (co + si) + ipt.y;

            // normal window
//			img.moveTo(round(x0), round(y0));
//			img.lineTo(round(x1), round(y1));
//			img.lineTo(round(x2), round(y2));
//			img.lineTo(round(x3), round(y3));
//			img.lineTo(round(x0), round(y0));

            // 'envelope'-window
            img.moveTo(x, y);
            img.lineTo(round(x0), round(y0));
            img.lineTo(round(x1), round(y1));
            img.lineTo(round(x2), round(y2));
            img.lineTo(round(x3), round(y3));
            img.lineTo(x, y);
        }


        // Draw orientation vector
        if (p.isDisplayOrientationVectors()) {
            img.setLineWidth(p.getLineWidth());
            img.setColor(p.getOrientationVectorColor());
            img.drawLine(x, y, round(s * co + x), round(s * si + y));
        }


        // Draw interest point
        img.setLineWidth(p.getLineWidth() * 4);
        if (ipt.sign) {
            img.setColor(p.getDarkPointColor());
        } else {
            img.setColor(p.getLightPointColor());
        }
        img.drawDot(x, y);

        // TODO: Draw motion (x,y) -> (x+dx, y+dy) ?   Or (x-dx, y-dy) -> (x,y) ?
        // (OpenSURF uses 'tailSize' parameter to indicate wether to draw the motion.)
    }

    public static void initializeStatisticsWindow() {
        IJ.setColumnHeadings(Statistics.getEmptyHeadersForIJ());
        log.info("Program Version:\t" + Params.programVersion);

    }

    public static void displayStatistics(Params p) {
        String out = "";
        Statistics stat = p.getStatistics();
        out += "Start Time:\t" + stat.startTime + "\n";
        out += "Image:\t" + stat.imageTitle + "\n";
        out += "\n";
        out += "Params\n";
        out += "Octaves:\t" + p.getOctaves() + "\n";
        out += "Layers:\t" + p.getLayers() + "\n";
        out += "Threshold:\t" + IJ.d2s(p.getThreshold(), 5) + "\n";
        out += "InitSamplStep:\t" + p.getInitStep() + "\n";
        out += "\n";

        out += "Detector Statistics\n";
        out += Statistics.getHeadersForIJ() + "\n";
        String[] rows = stat.getRowsForIJ();
        for (String s : rows) {
            out += s;
        }
        out += "\n";

        int detectedIPs = p.getStatistics().detectedIPs;
        out += "Interest Points:\t" + detectedIPs + "\n";
        out += "\n";

        out += "Strength of Interest Points\n";
        float[] strengthOfIPs = p.getStatistics().strengthOfIPs;
        out += "Min:\t" + IJ.d2s(strengthOfIPs[0], 10) + "\n";
        out += "Max:\t" + IJ.d2s(strengthOfIPs[detectedIPs - 1], 10) + "\n";
        out += "Median:\t" + IJ.d2s(strengthOfIPs[detectedIPs / 2], 10) + "\n";
        float sum = 0;
        for (float v : strengthOfIPs) {
            sum += v;
        }
        out += "Average:\t" + IJ.d2s(sum / detectedIPs, 10) + "\n";
        out += "\n";

        out += "Time (ms)\n";
        out += "IntegralImage:\t" + stat.timeIntegralImage + "\n";
        out += "Detector:\t" + stat.timeSURFDetector + "\n";
        out += "Descriptor:\t" + stat.timeSURFDescriptor + "\n";

        log.info(out);
    }
}
