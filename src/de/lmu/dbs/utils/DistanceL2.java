package de.lmu.dbs.utils;

import de.lmu.dbs.ImagePoint;

/**
 *
 * @author graf
 */
public class DistanceL2 implements Distance {

    @Override
    public double distance(ImagePoint a, ImagePoint b) {
        double x1 = a.getX() - b.getX();
        double y1 = a.getY() - b.getY();
        return Math.sqrt(x1*x1 + y1*y1);
    }

    @Override
    public double distance(int x1, int y1, int x2, int y2) {
        x1-=x2;
        y1-=y2;
        return Math.sqrt(x1*x1 + y1*y1);
    }
}
