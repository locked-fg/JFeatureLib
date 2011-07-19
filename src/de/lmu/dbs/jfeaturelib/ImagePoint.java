package de.lmu.dbs.jfeaturelib;

/*
 * A pixel representing a location in (x, y) coordinate plane, 
 * specified in double precision.
 * 
 * While images are usually only represented as ints, several algorithms use 
 * sub pixel locations, so that doubles are more useful.
 */
public class ImagePoint {

    private final double x;
    private final double y;

    // constructs and initializes a point at the specified location in the coordinate plane
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
