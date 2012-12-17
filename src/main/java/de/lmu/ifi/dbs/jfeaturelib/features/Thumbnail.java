/*
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.log4j.Logger;

/**
 * Simple thumbnail representation that serializes the complete image to a pixel array.
 *
 * @author Franz
 */
public class Thumbnail extends AbstractFeatureDescriptor {

    static final Logger log = Logger.getLogger(Thumbnail.class.getName());
    boolean resize = false;
    int width, height;

    @Override
    public String getDescription() {
        return "thumbnail representation";
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_RGB);
        return set;
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        this.resize = properties.getBoolean(LibProperties.THUMBNAIL_RESIZE, false);
        this.width = properties.getInteger(LibProperties.THUMBNAIL_WIDTH);
        this.height = properties.getInteger(LibProperties.THUMBNAIL_HEIGHT);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        if (resize) {
            ip = ip.resize(width, height);
        }

        Object pixels = ip.getPixels(); // byte short flot int
        Class type = pixels.getClass().getComponentType();

        double[] data = null;
        if (type.equals(byte.class)) {
            data = Arrays2.convertToDouble((byte[]) ip.getPixels());

        } else if (type.equals(short.class)) {
            data = Arrays2.convertToDouble((short[]) ip.getPixels());

        } else if (type.equals(float.class)) {
            data = Arrays2.convertToDouble((float[]) ip.getPixels());

        } else if (type.equals(int.class)) {
            data = Arrays2.convertToDouble((int[]) ip.getPixels());

        } else {
            String message = "an array of type " + type.getCanonicalName() + " was not expected here.";
            log.warn(message);
            throw new IllegalStateException(message);
        }

        addData(data);
        firePropertyChange(Progress.END);
    }
}
