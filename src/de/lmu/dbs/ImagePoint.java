package de.lmu.dbs;

/*
 * A pixel representing a location in (x, y) coordinate plane, 
 * specified in integer precision.
 */
public class ImagePoint {

    private final int x;
    private final int y;

    // constructs and initializes a point at the specified location in the coordinate plane
    public ImagePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
