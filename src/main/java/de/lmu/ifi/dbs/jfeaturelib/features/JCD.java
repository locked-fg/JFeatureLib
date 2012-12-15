package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;

/**
 * A joint descriptor joining CEDD and FCTH in one histogram by Savvas A. Chatzichristofis.
 * 
 * This is a wrapper class for the corresponding lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.JCD
 */
public class JCD extends AbstractFeatureDescriptor {

    private CEDD cedd = new CEDD();
    private FCTH fcth = new FCTH();

    public JCD() {
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        cedd.setProperties(properties);
        fcth.setProperties(properties);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        fcth.run(ip);
        firePropertyChange(new Progress(33));

        cedd.run(ip);
        firePropertyChange(new Progress(66));

        net.semanticmetadata.lire.imageanalysis.JCD jcd = new net.semanticmetadata.lire.imageanalysis.JCD(cedd.getCedd(), fcth.getFcth());
        jcd.extract(ip.getBufferedImage());

        addData(jcd.getDoubleHistogram());
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
