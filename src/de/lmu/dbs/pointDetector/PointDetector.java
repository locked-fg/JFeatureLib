package de.lmu.dbs.pointDetector;

import de.lmu.dbs.Descriptor;
import de.lmu.dbs.ImagePoint;
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
