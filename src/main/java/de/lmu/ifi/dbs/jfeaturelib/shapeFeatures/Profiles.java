/*
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
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Computes the horizontal, vertical and diagonal (starting top left for TLProfile analog bottom left for BLProfile)
 * Profiles according to "Bryan S. Morse (2000): Lecture 9: Shape Description (Regions), Brigham Young University,
 * Available from: http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * The descriptor itself returns a list of features. The features are the generated horizontal, vertical, TL and BL
 * arrays that can also be obtained by the approtiate getter methods.
 *
 * Since version 1.1 this descriptor returnes a list of features insread of a concatenated array that concatenated all
 * the profiles together into one array.
 *
 * @author Johannes Stadler, Johannes Niedermaier, Franz Graf
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

    /**
     * The descriptor generates a list of features.
     *
     * The features are the generated horizontal, vertical, TL and BL arrays that can also be obtained by the approtiate
     * getter methods.
     */
    @Override
    public void run(ImageProcessor ip) {
        startProgress();
        ip = transformAndConvert(ip);

        int[] currentHorizontalProfile = new int[ip.getWidth()];
        int[] currentVerticalProfile = new int[ip.getHeight()];
        int[] currentTLProfile = new int[ip.getWidth() + ip.getHeight()];
        int[] currentBLProfile = new int[ip.getHeight() + ip.getWidth()];

        int height = ip.getHeight();
        int width = ip.getWidth();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // if (ip.getPixel(x, y) != backgroundColor) {
                if (ip.get(x, y) != backgroundColor) {
                    currentHorizontalProfile[x]++;
                    currentVerticalProfile[y]++;
                    if (x > y) {
                        currentBLProfile[height + (x - y)]++;
                    } else {
                        currentBLProfile[height - (y - x)]++;
                    }
                    if (y < width) {
                        currentTLProfile[x + y]++;
                    } else {
                        currentTLProfile[width + (y - width)]++;
                    }
                }
            }
        }

        this.horizontalProfile = reinsert(currentHorizontalProfile);
        this.verticalProfile = reinsert(currentVerticalProfile);
        this.TLProfile = reinsert(currentTLProfile);
        this.BLProfile = reinsert(currentBLProfile);

        createFeatures();
        endProgress();
    }

    /**
     * Identifies the indices enclosing the real content (=first index after leading zeros and last index before
     * trailing zeros).
     *
     * @param profile
     * @return reduced int array without leading/traingin zeros
     */
    ProfileTuple shortenProfile(int[] profile) {
        int start = 0;
        while (start < profile.length && profile[start] == 0) {
            start++;
        }

        int end = profile.length - 1;
        while (end > start && profile[end] == 0) {
            end--;
        }
        return new ProfileTuple(start, profile.length - end);
    }

    /**
     * Extracts the content part of the given array.
     *
     * @param oldProfile
     * @return array without leading/trailing zeros
     */
    int[] reinsert(int[] oldProfile) {
        ProfileTuple t1 = shortenProfile(oldProfile);
        int length = oldProfile.length - t1.getStart() - t1.getEnd() + 1;
        int[] newProfileX = new int[length];
        System.arraycopy(oldProfile, t1.start, newProfileX, 0, newProfileX.length);
        return newProfileX;
    }

    void createFeatures() {
        addData(horizontalProfile);
        addData(verticalProfile);
        addData(TLProfile);
        addData(BLProfile);
    }

    @Override
    public String getDescription() {
        return "horizontal, vertical and diagonal Profiles";
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
