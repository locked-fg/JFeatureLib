package de.lmu.dbs.jfeaturelib.pointDetector;

import de.lmu.dbs.jfeaturelib.Descriptor;
import de.lmu.dbs.jfeaturelib.ImagePoint;
import java.util.List;

/**
 * Interface for all intereting point detectors.
 * 
 * Classes implementing this interface will often provide additional methods for 
 * additional information. Such information can be a radius or shape defining an 
 * interesting region surrounding an interesting point.
 * 
 * @author graf
 */
interface PointDetector extends Descriptor {

    List<ImagePoint> getPoints();
}
