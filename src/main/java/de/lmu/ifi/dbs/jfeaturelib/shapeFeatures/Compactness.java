package de.lmu.ifi.dbs.jfeaturelib.shapeFeatures;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.jfeaturelib.features.AbstractFeatureDescriptor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Computes the compactness according to "Bryan S. Morse (2000): Lecture 9:
 * Shape Description (Regions), Brigham Young University, Available from:
 * http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/MORSE/region-props-and-moments.pdf".
 *
 * @author Johannes Stadler
 * @since 09/29/2012
 */
public class Compactness extends AbstractFeatureDescriptor {

    private int area;
    private int perimeter;
    private int backgroundColor = 0;

    public double getCompactness() {
        return (double) area / (double) perimeter;
    }

    public int getArea() {
        return area;
    }

    public int getPerimeter() {
        return perimeter;
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);

        if (!ByteProcessor.class.isAssignableFrom(ip.getClass())) {
            ip = ip.convertToByte(true);
        }
        ByteProcessor image = (ByteProcessor) ip;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getPixel(i, j) != backgroundColor) {
                    if (image.getPixel(i - 1, j - 1) == backgroundColor
                            || image.getPixel(i, j - 1) == backgroundColor
                            || image.getPixel(i + 1, j - 1) == backgroundColor
                            || image.getPixel(i - 1, j) == backgroundColor
                            || image.getPixel(i + 1, j) == backgroundColor
                            || image.getPixel(i - 1, j + 1) == backgroundColor
                            || image.getPixel(i, j + 1) == backgroundColor
                            || image.getPixel(i + 1, j + 1) == backgroundColor) {
                        perimeter++;
                    }
                    area++;
                }
            }
        }

        addData(new double[]{getArea(), getPerimeter(), getCompactness()});

        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public String getDescription() {
        return "compactnes features: area, perimeter, compactness (=area/perimeter)";
    }
}
