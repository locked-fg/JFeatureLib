package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * Computes the ratio of the length of the longest chord of the shape to the
 * longest chord perpendicular to it according to "Bryan S. Morse (2000):
 * Lecture 9: Shape Description (Regions), Brigham Young University, Available
 * from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class Eccentricity extends AbstractFeatureDescriptor {

    private double eccentricity;

    public double getEccentricity() {
        return eccentricity;
    }

    @Override
    public void run(ImageProcessor ip) {
        startProgress();

        BoundingBox bb = new BoundingBox();
        bb.run(ip);
        double[] boundingBox = bb.getBoundingBox();
        eccentricity = boundingBox[2] / boundingBox[3];

        addData(new double[]{eccentricity});

        endProgress();
    }

    @Override
    public EnumSet<Supports> supports() {
        return new BoundingBox().supports();
    }

    @Override
    public String getDescription() {
        return "Eccentricity of the shape";
    }
}