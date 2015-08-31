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
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.features.surf.IJFacade;
import de.lmu.ifi.dbs.jfeaturelib.features.surf.IntegralImage;
import de.lmu.ifi.dbs.jfeaturelib.features.surf.InterestPoint;
import de.lmu.ifi.dbs.jfeaturelib.features.surf.Params;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ImageProcessor;
import java.util.EnumSet;
import java.util.List;

/**
 * Wrapper for the ImageJ SURF plugin (http://www.labun.com/imagej-surf/)
 *
 * @author Benedikt
 */
public class SURF extends AbstractFeatureDescriptor {

    //Default parameters
    private int octaves = 4;
    private int layers = 4;
    private float threshold = 0.00100f;
    private int initStep = 2;
    private boolean upright = false;
    private boolean displayOrientationVectors = true;
    private boolean displayDescriptorWindows = false;
    private int lineWidth = 1;
    private boolean displayStatistics = false;

    /**
     * Empty default constructor
     */
    public SURF() {
    }

    /**
     * Constructs a SURF instance with all params
     *
     * @param octaves
     * @param layers
     * @param threshold
     * @param initStep
     * @param upright
     * @param displayOrientationVectors
     * @param displayDescriptorWindows
     * @param lineWidth
     * @param displayStatistics
     */
    public SURF(int octaves, int layers, float threshold, int initStep, boolean upright, boolean displayOrientationVectors, boolean displayDescriptorWindows, int lineWidth, boolean displayStatistics) {
        this.octaves = octaves;
        this.layers = layers;
        this.threshold = threshold;
        this.initStep = initStep;
        this.upright = upright;
        this.displayOrientationVectors = displayOrientationVectors;
        this.displayDescriptorWindows = displayDescriptorWindows;
        this.lineWidth = lineWidth;
        this.displayStatistics = displayStatistics;
    }

    @Override
    public String getDescription() {
        String info = "SURF Interest Points: ";
        info += "The first 64 numbers of an InterestPoint describe its descriptor";
        return info;
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        IntegralImage intImg = new IntegralImage(ip, true);
        List<InterestPoint> ipts = IJFacade.detectAndDescribeInterestPoints(intImg, getParams());

        for (InterestPoint poi : ipts) {
            double[] data = {poi.x, poi.y, poi.trace, poi.strength, poi.scale, poi.orientation};
            double[] data2 = Arrays2.append(data, Arrays2.convertToDouble(poi.descriptor));
            addData(data2);
        }
        firePropertyChange(Progress.END);
    }

    /**
     * Returns the list of SURF features.
     *
     * Each feature vector is composed as follows: poi.x, poi.y, poi.trace,
     * poi.strength, poi.scale, poi.orientation, [feature vector]
     *
     * @return list of vectors
     */
    @Override
    public List<double[]> getFeatures() {
        return super.getFeatures();
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        return set;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/Setter">
    /**
     * Set octaves param
     *
     * @param octaves
     */
    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }

    /**
     * Set layers param
     *
     * @param layers
     */
    public void setLayers(int layers) {
        this.layers = layers;
    }

    /**
     * Set treshold param
     *
     * @param threshold
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Set initial step param
     *
     * @param initStep
     */
    public void setInitStep(int initStep) {
        this.initStep = initStep;
    }

    /**
     * Set if rotation invariant should be excluded
     *
     * @param upright
     */
    public void setUpright(boolean upright) {
        this.upright = upright;
    }

    /**
     * Set if line width should display IP strength
     *
     * @param displayOrientationVectors
     */
    public void setDisplayOrientationVectors(boolean displayOrientationVectors) {
        this.displayOrientationVectors = displayOrientationVectors;
    }

    /**
     * Set if windows for each IP should be displayed
     *
     * @param displayDescriptorWindows
     */
    public void setDisplayDescriptorWindows(boolean displayDescriptorWindows) {
        this.displayDescriptorWindows = displayDescriptorWindows;
    }

    /**
     * Set overall line width
     *
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Sets if detailled statistics are displayed
     *
     * @param displayStatistics
     */
    public void setDisplayStatistics(boolean displayStatistics) {
        this.displayStatistics = displayStatistics;
    }

    /**
     *
     * @return Number of octaves
     */
    public int getOctaves() {
        return octaves;
    }

    /**
     *
     * @return number of layers
     */
    public int getLayers() {
        return layers;
    }

    /**
     *
     * @return treshold
     */
    public float getThreshold() {
        return threshold;
    }

    /**
     *
     * @return initial step
     */
    public int getInitStep() {
        return initStep;
    }

    /**
     *
     * @return no rotation invariant
     */
    public boolean getUpright() {
        return upright;
    }

    /**
     *
     * @return vector lines for each IP
     */
    public boolean getDisplayOrientationVectors() {
        return displayOrientationVectors;
    }

    /**
     *
     * @return windows for each IP
     */
    public boolean getDisplayDescriptorWindows() {
        return displayDescriptorWindows;
    }

    /**
     *
     * @return overall line width
     */
    public int getLineWidth() {
        return lineWidth;
    }

    /**
     *
     * @return detailled statistics
     */
    public boolean getDisplayStatistics() {
        return displayStatistics;
    }

    /**
     *
     * @return Params object holding parameters
     */
    public Params getParams() {
        return new Params(octaves, layers, threshold, initStep, upright,
                displayOrientationVectors, displayDescriptorWindows,
                lineWidth, displayStatistics);
    }
    //</editor-fold>
}
