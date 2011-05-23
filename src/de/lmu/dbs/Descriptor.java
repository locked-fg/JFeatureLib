package de.lmu.dbs;

import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * @author graf
 */
public interface Descriptor {

    enum Supports {

        Masking, NoChanges
    }

    EnumSet<Supports> supports();

    void run(ImageProcessor ip);

}
