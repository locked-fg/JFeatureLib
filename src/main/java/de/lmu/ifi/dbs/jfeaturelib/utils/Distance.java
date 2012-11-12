package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;

/**
 * An abstract definition of a distance measure between two coordinates or image
 * points.
 *
 * @author graf
 */
public interface Distance {

    public double distance(ImagePoint a, ImagePoint b);

    public double distance(int x1, int y1, int x2, int y2);
}
