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
package de.lmu.ifi.dbs.jfeaturelib.features.surf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    public Date startTime;
    public String imageTitle;
    public int detectedIPs;
    /**
     * Sorted array containing strength of all detected IPs.
     */
    public float[] strengthOfIPs;
    public long timeIntegralImage;
    public long timeSURFDetector;
    public long timeSURFDescriptor;
    public long timeMatcher;
    public List<DetectorStat> detectorStatList = new ArrayList<DetectorStat>();

    public static class DetectorStat {

        public int octave;
        public int layer;
        public int countIPCandidates;
        public int countThresholded;
        public int countSuppressed;
        public int countInterpolationNotSucceed;
        public int countBadInterpolationResult;
        public int countIP;
    }

    public void add(int octave, int layer, int countIPCandidates, int countThresholded, int countSuppressed, int countInterpolationNotSucceed,
            int countBadInterpolationResult, int countIP) {

        DetectorStat ds = new DetectorStat();
        ds.octave = octave;
        ds.layer = layer;
        ds.countIPCandidates = countIPCandidates;
        ds.countThresholded = countThresholded;
        ds.countSuppressed = countSuppressed;
        ds.countInterpolationNotSucceed = countInterpolationNotSucceed;
        ds.countBadInterpolationResult = countBadInterpolationResult;
        ds.countIP = countIP;

        detectorStatList.add(ds);
    }

    // TODO move the folowing methods to IJFacade
    public static String getHeadersForIJ() {
        return "Octave" + "\t" + "Layer" + "\t" + "IPCandidates" + "\t" + "Thresholded" + "\t" + "Suppressed" + "\t" + "InterpolationNotSucceed" + "\t" + "BadInterpolationResult" + "\t" + "Final IPs";
    }

    public static String getEmptyHeadersForIJ() {
        return " " + "\t" + " " + "\t" + " " + "\t" + " " + "\t" + " " + "\t" + " " + "\t" + " " + "\t" + " ";
    }

    public String[] getRowsForIJ() {
        String[] result = new String[detectorStatList.size()];
        for (int i = 0; i < detectorStatList.size(); i++) {
            DetectorStat ds = detectorStatList.get(i);
            result[i] = ds.octave + "\t" + ds.layer + "\t" + ds.countIPCandidates + "\t" + ds.countThresholded + "\t" + ds.countSuppressed + "\t" + ds.countInterpolationNotSucceed + "\t" + ds.countBadInterpolationResult + "\t" + ds.countIP;
        }
        return result;
    }
}
