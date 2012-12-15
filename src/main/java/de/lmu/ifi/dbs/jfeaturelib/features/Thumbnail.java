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
