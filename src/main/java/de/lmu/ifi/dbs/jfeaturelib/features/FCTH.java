package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * The FCTH feature was created, implemented and provided by Savvas A. Chatzichristofis
 *
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis, <i>FCTH: Fuzzy Color and
 * Texture Histogram - A Low Level Feature for Accurate Image Retrieval</i>, in Proceedings of the Ninth International
 * Workshop on Image Analysis for Multimedia Interactive Services, IEEE, Klagenfurt, May, 2008.
 */
public class FCTH extends AbstractFeatureDescriptor {

    private net.semanticmetadata.lire.imageanalysis.FCTH fcth = new net.semanticmetadata.lire.imageanalysis.FCTH();

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        fcth.extract(ip.getBufferedImage());
        addData(fcth.getDoubleHistogram());

        firePropertyChange(Progress.END);
    }

    /**
     * @return the fcth instance from lire
     */
    public net.semanticmetadata.lire.imageanalysis.FCTH getFcth() {
        return fcth;
    }

    @Override
    public String getDescription() {
        return "FCTH";
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