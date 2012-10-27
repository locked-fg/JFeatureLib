package de.lmu.dbs.jfeaturelib.utils;

import de.lmu.dbs.jfeaturelib.ImagePoint;

/**
 *
 * @author graf
 */
public interface Distance {

    public double distance(ImagePoint a, ImagePoint b);

    public double distance(int x1, int y1, int x2, int y2);
}
