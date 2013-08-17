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
package de.lmu.ifi.dbs.jfeaturelib.features.surf;

import ij.gui.GenericDialog;
import java.awt.Color;

//TODO move IJ-related code into IJFacade
// TODO JavaDoc for getter/setter
/**
 * Parameter for SURF Detector, SURF Descriptor and for displaying the results.
 */
public class Params {

    public final static String programVersion = "ImageJ SURF v2009-12-01";

    public Params() { 	// TODO: move initializing here ?
    }

    public Params(int octaves, int layers, float threshold, int initStep, boolean upright, boolean displayOrientationVectors, boolean displayDescriptorWindows, int lineWidth, boolean displayStatistics) {
        this.octaves = validate(octaves, 3, 4);
        this.layers = validate(layers, 3, 4);
        this.threshold = validate(threshold, 0, 1);
        this.initStep = validate(initStep, 1, 6);
        this.upright = upright;
        this.displayOrientationVectors = displayOrientationVectors;
        this.displayDescriptorWindows = displayDescriptorWindows;
        this.lineWidth = validate(lineWidth, 1, 5);
        this.displayStatistics = displayStatistics;
    }

    public Params(Params p) {
        this.octaves = p.octaves;
        this.layers = p.layers;
        this.threshold = p.threshold;
        this.initStep = p.initStep;
        this.upright = p.upright;
        this.displayOrientationVectors = p.displayOrientationVectors;
        this.displayDescriptorWindows = p.displayDescriptorWindows;
        this.lineWidth = p.lineWidth;
        this.displayStatistics = p.displayStatistics;
    }

    private float validate(float val, float lowerBound, float upperBound) {
        if (val < lowerBound) {
            return lowerBound;
        }
        if (val > upperBound) {
            return upperBound;
        }
        return val;
    }

    /**
     * Validates parameter values for the constructor.
     *
     * @param lowerBound The lowest valid value.
     * @param upperBound The highest valid value.
     * @param val The value to validate.
     */
    int validate(int val, int lowerBound, int upperBound) {
        //		return (val < lowerBound) ? lowerBound : (val > upperBound ? upperBound : val); // this is the same as below
        if (val < lowerBound) {
            return lowerBound;
        }
        if (val > upperBound) {
            return upperBound;
        }
        return val;
    }
    /////////////////////////////////////////////////////////////////////////7
    // Detector params
    /**
     * Defines octaves and layers (filter sizes). The actual implementation
     * assumes the equal number of layers in each octave. Default is 4 octaves
     * with 4 layers per octave and values as proposed in the SURF paper
     * (2008):<br>
     * <code>{{9,15,21,27},{15,27,39,51},{27,51,75,99},{51,99,147,195}}</code>.
     */
    private int[][] filterSizes = {{9, 15, 21, 27}, {15, 27, 39, 51}, {27, 51, 75, 99}, {51, 99, 147, 195}};
    private int[] maxFilterSizes = {27, 51, 99, 195};
    //TODO: add alternate filter size (to apply after image scaling)
    /**
     * Number of analysed octaves. Default is 4.
     */
    private int octaves = 4; //  3 .. 4 (filterSizes.length)      OpenSURF (C++): 3     orig.SURF: 4
    private int layers = 4; // 3 .. 4 (filterSizes[0].length)

    public int getOctaves() {
        return octaves;
    }

    public int getLayers() {
        return layers;
    }

    public int getFilterSize(int octave, int layer) {
        return filterSizes[octave][layer];
    }

    /**
     * Returns the biggest filter size in the octave.
     */
    public int getMaxFilterSize(int octave) {
        return maxFilterSizes[octave];
    }
    // Set this flag "true" to double the image size
//    boolean doubleImageSize = false;
    // TODO: rename to "upscale image first"? + implementation (siehe ImageJ_SIFT e.g. as parameter to intImg constr.) + update constructor/getter/setter
    /**
     * The responses are thresholded such that all values below the
     * <code>threshold</code> are removed. Increasing the threshold lowers the
     * number of detected interest points, and vice versa. Must be >= 0 and <=
     * 1.
     */
    //	private float threshold = 600; 
    // NB: in C++ version 0.0004f (sometimes 0.0006f); in C# version 0.002f
    // orig.SURF: double thres = 4.0; "Blob response treshold";  cvsurf: "hessianThreshold"
    // private float threshold = 0.0002f; 
    private float threshold = 0.001f;

    public float getThreshold() {
        return threshold;
    }
    /**
     * The initial sampling step (1..6). Default is 2. <br> Will be doubled for
     * each next octave (see stepIncFactor).
     */
    private int initStep = 2; // orig.SURF: 2 (2 gives less IPs than 1 but much faster)

    public int getInitStep() {
        return initStep;
    }
    private int stepIncFactor = 2;

    public int getStepIncFactor() {
        return stepIncFactor;
    }
    //	private int interp_steps = 5; // from C# version of OpenSURF (not exists in C++ version)  TODO: purpose?
    /////////////////////////////////////////////////////////////////////////7
    // Descriptor params
    /**
     * Extract upright (i.e. not rotation invariant) descriptors. Default is
     * <code>false</code>.
     */
    private boolean upright;

    public boolean isUpright() {
        return upright;
    }
    private int descSize = 64;

    public int getDescSize() {
        return descSize;
    }
    /////////////////////////////////////////////////////////////////////////7
    // Display params
    private boolean displayOrientationVectors = true;

    public boolean isDisplayOrientationVectors() {
        return displayOrientationVectors;
    }
    private boolean displayDescriptorWindows = false;

    public boolean isDisplayDescriptorWindows() {
        return displayDescriptorWindows;
    }
    private int lineWidth = 1; // 1..5

    public int getLineWidth() {
        return lineWidth;
    }
    boolean displayStatistics = false;

    public boolean isDisplayStatistics() {
        return displayStatistics;
    }
    private Statistics stat = new Statistics();

    public Statistics getStatistics() {
        return stat;
    }

    /**
     * Adds SURF parameter to the ImageJ GenericDialog
     * 
     * The order and types of fields do affect
     * the method Params#getSurfParamsFromDialog(GenericDialog). Bounds
     * description depends on bounds check in constructor {@link Params#Params(int, int, float, int, boolean, boolean, int)}.
     */
    public static void addSurfParamsToDialog(GenericDialog gd) {
        Params p = new Params();
        gd.addMessage("=================== DETECTOR ===================");
        gd.addNumericField("Octaves (3..4) :", p.getOctaves(), 0);
        gd.addNumericField("Layers per Octave (3..4) :", p.getLayers(), 0);
        gd.addNumericField("Hessian Threshold (0..1) :", p.getThreshold(), 5);
        gd.addNumericField("Initial Sampling Step (1..6) :", p.getInitStep(), 0);
        gd.addMessage("================== DESCRIPTOR ==================");
        gd.addCheckbox("Upright SURF (slightly faster, but not rotation invariant)", p.isUpright());
        gd.addMessage("=============== DISPLAYING RESULTS ==============");
        //TODO: add Matcher params: best/secondBest value, boolean reverseCheck, float tolerance for comparison with homography
        // threshold for distance-between-matching-points (outlier/Ausrei�er)
        gd.addCheckbox("Orientation Vectors (Length shows the strength of I.P.) ", p.isDisplayOrientationVectors());
        gd.addCheckbox("Descriptor Windows", p.isDisplayDescriptorWindows());
        gd.addNumericField("Line Width (1..5) :", p.getLineWidth(), 0);
        gd.addCheckbox("Statistics", p.isDisplayStatistics());
    }

    /**
     * Reads SURF parameter from the ImageJ
     * <code>GenericDialog</code> and returns a
     * <code>SurfParams</code> object. Depends on the order and types of fields
     * in the method {@link Params#addSurfParamsToDialog(GenericDialog)}.
     */
    public static Params getSurfParamsFromDialog(GenericDialog gd) {
        int octaves = (int) gd.getNextNumber();
        int layers = (int) gd.getNextNumber();
        float threshold = (float) gd.getNextNumber();
        int initStep = (int) gd.getNextNumber();
        boolean upright = gd.getNextBoolean();
        boolean displayOrientationVectors = gd.getNextBoolean();
        boolean displayDescriptorWindows = gd.getNextBoolean();
        int lineWidth = (int) gd.getNextNumber();
        boolean displayStatistics = gd.getNextBoolean();
        return new Params(octaves, layers, threshold, initStep, upright, displayOrientationVectors, displayDescriptorWindows, lineWidth, displayStatistics);
    }

    public Color getDescriptorWindowColor() {
        return Color.PINK;
    }

    public Color getOrientationVectorColor() {
        return Color.YELLOW;
    }

    /**
     * Drawing color for dark blobs on light background
     */
    public Color getDarkPointColor() {
        return Color.BLUE;
    }

    /**
     * Drawing color for light blobs on dark background
     */
    public Color getLightPointColor() {
        return Color.RED;
    }
}
