package de.lmu.dbs.jfeaturelib.features.surf;

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
