package de.lmu.dbs.jfeaturelib.shapeFeatures;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Computes the Centroid according to "Bryan S. Morse (2000): Lecture 9: Shape
 * Description (Regions), Brigham Young University, Available from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 */
public class CentroidFeature extends AbstractFeatureDescriptor {

    private double x = 0, y = 0;
    private int k;
    private int backgroundColor = 0;

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        ByteProcessor image;
        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            image = (ByteProcessor) ip.convertToByte(true);
        } else {
            image = (ByteProcessor) ip;
        }
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (image.getPixel(j, i) != backgroundColor) {
                    k++;
                    x += j;
                    y += i;
                }

            }
        }
        x = x / k;
        y = y / k;

        addData(new double[]{x, y});
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String getDescription() {
        return "returns the shape's centroid pixel (x,y)";
    }
}