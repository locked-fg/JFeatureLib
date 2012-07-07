package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * A joint descriptor joining CEDD and FCTH in one histogram by Savvas A. Chatzichristofis.
 */
public class JCD extends AbstractFeatureDescriptor {

    public JCD() {
    }

    @Override
    public void run(ImageProcessor ip) {
        de.lmu.dbs.jfeaturelib.features.lire.JCD jcd = new de.lmu.dbs.jfeaturelib.features.lire.JCD();

        firePropertyChange(Progress.START);

        jcd.extract(ip.getBufferedImage());
        addData(jcd.getData());

        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "JCD";
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
}
