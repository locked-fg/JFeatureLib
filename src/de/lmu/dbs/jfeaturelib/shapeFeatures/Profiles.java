package de.lmu.dbs.jfeaturelib.shapeFeatures;

import de.lmu.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ImageProcessor;

/**
 * Computes the horizontal, vertical and diagonal (starting top left for
 * TLProfile analog bottom left for BLProfile) Profiles according to "Bryan S.
 * Morse (2000): Lecture 9: Shape Description (Regions), Brigham Young
 * University, Available from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class Profiles extends AbstractFeatureDescriptor {

    private int[] horizontalProfile;
    private int[] verticalProfile;
    private int[] TLProfile;
    private int[] BLProfile;
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

        horizontalProfile = new int[ip.getWidth()];
        verticalProfile = new int[ip.getHeight()];
        TLProfile = new int[ip.getWidth() + ip.getHeight()];
        BLProfile = new int[ip.getHeight() + ip.getWidth()];
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                if (ip.getPixel(i, j) != backgroundColor) {
                    horizontalProfile[i]++;
                    verticalProfile[j]++;
                    if (i > j) {
                        BLProfile[ip.getHeight() + (i - j)]++;
                    } else {
                        BLProfile[ip.getHeight() - (j - i)]++;
                    }

                    if (j < ip.getWidth()) {
                        TLProfile[i + j]++;
                    } else {
                        TLProfile[ip.getWidth() + (j - ip.getWidth())]++;
                    }
                }
            }
        }

        createFeature();
        endProgress();
    }

    @Override
    public String getDescription() {
        return "horizontal, vertical and diagonal Profiles";
    }

    private void createFeature() {
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
}
