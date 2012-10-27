package de.lmu.dbs.jfeaturelib.features.surf;

import ij.IJ;
import ij.process.ImageProcessor;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.List;

/**
 * SURF library for ImageJ. Based on SURF paper (2008) and OpenSURF C++
 * implementation. See SURF_Test.java for example of usage.
 *
 * @author Eugen Labun
 */
public class IJFacade {

    private IJFacade() {
    }
//    /**
//     * Cached last result.
//     */
//    private static List<InterestPoint> lastResult = null;
//
//    synchronized public static void setLastResult(List<InterestPoint> ipts) {
//        lastResult = ipts;
//    }
//
//    synchronized public static List<InterestPoint> getLastResult() {
//        return lastResult;
//    }

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


//        setLastResult(ipts);
        return ipts;
    }

    /**
     * Draws interest points onto suplied
     * <code>ImageProcessor</code>.
     */
    public static void drawInterestPoints(ImageProcessor img, List<InterestPoint> ipts, Params params) {
        // TODO: 3 loops: 1) rectangles only, 2) orientation vectors only, 3) interest points only
        // ^^ to be shure all interest points are visible!

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
//		if (ori != 0) {
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
        IJ.write("Program Version:\t" + Params.programVersion);
        IJ.write("");

    }

    public static void displayStatistics(Params p) {
        Statistics stat = p.getStatistics();
        IJ.write("Start Time:\t" + stat.startTime);
        IJ.write("Image:\t" + stat.imageTitle);
        IJ.write("");
        IJ.write("Params");
        IJ.write("Octaves:\t" + p.getOctaves());
        IJ.write("Layers:\t" + p.getLayers());
        IJ.write("Threshold:\t" + IJ.d2s(p.getThreshold(), 5));
        IJ.write("InitSamplStep:\t" + p.getInitStep());
        IJ.write("");

        IJ.write("Detector Statistics");
        IJ.write(Statistics.getHeadersForIJ());
        String[] rows = stat.getRowsForIJ();
        for (String s : rows) {
            IJ.write(s);
        }
        IJ.write("");

        int detectedIPs = p.getStatistics().detectedIPs;
        IJ.write("Interest Points:\t" + detectedIPs);
        IJ.write("");

        IJ.write("Strength of Interest Points");
        float[] strengthOfIPs = p.getStatistics().strengthOfIPs;
        IJ.write("Min:\t" + IJ.d2s(strengthOfIPs[0], 10));
        IJ.write("Max:\t" + IJ.d2s(strengthOfIPs[detectedIPs - 1], 10));
        IJ.write("Median:\t" + IJ.d2s(strengthOfIPs[detectedIPs / 2], 10));
        float sum = 0;
        for (float v : strengthOfIPs) {
            sum += v;
        }
        IJ.write("Average:\t" + IJ.d2s(sum / detectedIPs, 10));
        IJ.write("");

        IJ.write("Time (ms)");
        IJ.write("IntegralImage:\t" + stat.timeIntegralImage);
        IJ.write("Detector:\t" + stat.timeSURFDetector);
        IJ.write("Descriptor:\t" + stat.timeSURFDescriptor);
        IJ.write("");
        IJ.write("");

    }
}
