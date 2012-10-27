package de.lmu.dbs.jfeaturelib.features.sift;

/**
 * This class represents a single SIFT feature vector as it is obtained from the 
 * sift binary.
 * 
 * @author graf
 */
class SiftFeatureVector {

    private final double x, y, scale, rotation;
    private double[] gradients;

    public SiftFeatureVector(double x, double y, double scale, double rotation) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
    }

    void setGradients(double[] gradients) {
        this.gradients = gradients;
    }

    double[] asArray() {
        double[] out = new double[gradients.length + 4];
        out[0] = y;
        out[1] = x;
        out[2] = scale;
        out[3] = rotation;
        System.arraycopy(gradients, 0, out, 4, gradients.length);
        return out;
    }
}