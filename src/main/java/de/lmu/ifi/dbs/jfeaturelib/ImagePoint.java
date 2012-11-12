package de.lmu.ifi.dbs.jfeaturelib;

/*
 * A pixel representing a location in (x, y) coordinate plane, 
 * specified in double precision.
 * 
 * While images are usually only represented as ints, several algorithms use 
 * sub pixel locations, so that doubles are more useful.
 */
public class ImagePoint {

    /**
     * The x-coordinate of the according point.
     */
    public double x;
    /**
     * The x-coordinate of the according point.
     */
    public double y;

    public ImagePoint() {
    }

    /**
     * constructs and initializes a point at the specified location in the
     * coordinate plane
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public ImagePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
