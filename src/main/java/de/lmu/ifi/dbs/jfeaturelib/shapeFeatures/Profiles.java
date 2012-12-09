package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Computes the horizontal, vertical and diagonal (starting top left for TLProfile analog bottom left for BLProfile)
 * Profiles according to "Bryan S. Morse (2000): Lecture 9: Shape Description (Regions), Brigham Young University,
 * Available from: http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler, Johannes Niedermaier
 * @since 09/29/2012
 */
public class Profiles extends AbstractFeatureDescriptor {

    int[] horizontalProfile;
    int[] verticalProfile;
    int[] TLProfile;
    int[] BLProfile;
    private int backgroundColor = 0;

    public int[] getHorizontalProfile() {
        return horizontalProfile;
    }

    public int[] getVerticalProfile() {
        return verticalProfile;
    }

    public int[] getTLProfile() {
        return TLProfile;
    }

    public int[] getBLProfile() {
        return BLProfile;
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();
        ip = transformAndConvert(ip);

        int[] currentHorizontalProfile = new int[ip.getWidth()];
        int[] currentVerticalProfile = new int[ip.getHeight()];
        int[] currentTLProfile = new int[ip.getWidth() + ip.getHeight()];
        int[] currentBLProfile = new int[ip.getHeight() + ip.getWidth()];

        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (ip.getPixel(i, j) != backgroundColor) {
                    currentHorizontalProfile[i]++;
                    currentVerticalProfile[j]++;
                    if (i > j) {
                        currentBLProfile[ip.getHeight() + (i - j)]++;
                    } else {
                        currentBLProfile[ip.getHeight() - (j - i)]++;
                    }
                    if (j < ip.getWidth()) {
                        currentTLProfile[i + j]++;
                    } else {
                        currentTLProfile[ip.getWidth() + (j - ip.getWidth())]++;
                    }
                }
            }
        }

        ProfileTuple t1 = shortenProfile(currentHorizontalProfile);
        this.horizontalProfile = new int[currentHorizontalProfile.length-t1.getStart()-t1.getEnd()];
        reinsert(this.horizontalProfile, currentHorizontalProfile, t1);
        
        t1 = shortenProfile(currentVerticalProfile);
        this.verticalProfile = new int[currentVerticalProfile.length-t1.getStart()-t1.getEnd()];
        reinsert(this.verticalProfile, currentVerticalProfile, t1);
        
        t1 = shortenProfile(currentTLProfile);
        this.TLProfile = new int[currentTLProfile.length-t1.getStart()-t1.getEnd()];
        reinsert(this.TLProfile, currentTLProfile, t1);
        
        t1 = shortenProfile(currentBLProfile);
        this.BLProfile = new int[currentBLProfile.length-t1.getStart()-t1.getEnd()];
        reinsert(this.BLProfile, currentBLProfile, t1);
        
        createFeature();
        endProgress();
    }

    ProfileTuple shortenProfile(int[] profile) {
        int start = 0;
        boolean stop = false;
        while (start < profile.length && !stop) {
            if (profile[start] != 0) {
                stop = true;
            } else {
                start++;
            }
        }

        int end = profile.length - 1;
        stop = false;
        while (end >= 0 && !stop) {
            if (profile[end] != 0) {
                stop = true;
            } else {
                end--;
            }
        }
        return new ProfileTuple(start, profile.length - end);
    }

    void reinsert(int[] newProfile, int[] oldProfile, ProfileTuple t1) {
        for (int i = t1.getStart(); i < oldProfile.length - t1.getEnd(); i++) {
            newProfile[i - t1.getStart()] = oldProfile[i];
        }
    }

    void createFeature() {
        double[] data = new double[horizontalProfile.length
                + verticalProfile.length + TLProfile.length + BLProfile.length];

        int i = 0;
        for (int j = 0; j < horizontalProfile.length; j++) {
            data[i++] = horizontalProfile[j];
        }
        for (int j = 0; j < verticalProfile.length; j++) {
            data[i++] = verticalProfile[j];
        }
        for (int j = 0; j < TLProfile.length; j++) {
            data[i++] = TLProfile[j];
        }
        for (int j = 0; j < BLProfile.length; j++) {
            data[i++] = BLProfile[j];
        }

        addData(data);
    }

    @Override
    public String getDescription() {
        return "horizontal, vertical and diagonal Profiles";
    }

    private void shortenProfiles() {
        ProfileTuple t1;
        t1 = shortenProfile(horizontalProfile);
        this.horizontalProfile = new int[horizontalProfile.length - t1.getStart() - t1.getEnd()];
        reinsert(this.horizontalProfile, horizontalProfile, t1);

        t1 = shortenProfile(verticalProfile);
        this.verticalProfile = new int[verticalProfile.length - t1.getStart() - t1.getEnd()];
        reinsert(this.verticalProfile, verticalProfile, t1);

        t1 = shortenProfile(TLProfile);
        this.TLProfile = new int[TLProfile.length - t1.getStart() - t1.getEnd()];
        reinsert(this.TLProfile, TLProfile, t1);

        t1 = shortenProfile(BLProfile);
        this.BLProfile = new int[BLProfile.length - t1.getStart() - t1.getEnd()];
        reinsert(this.BLProfile, BLProfile, t1);
    }

    private ImageProcessor transformAndConvert(ImageProcessor ip) throws IllegalArgumentException {
        ImagePCA pca2d = new ImagePCA(ip, 0);
        if (Double.isNaN(pca2d.getAngle())) {
            throw new IllegalArgumentException("The mask in this image is empty.");
        }
        ip = pca2d.getResultImage();
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = (ByteProcessor) ip.convertToByte(true);
        }
        return ip;
    }

    class ProfileTuple {

        int start;
        int end;

        ProfileTuple(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int getStart() {
            return start;
        }

        int getEnd() {
            return end;
        }
    }
}
