package de.lmu.ifi.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * VisualDescriptor for the AutoCorrelogram based on color as described in Huang, J.; Kumar, S. R.; Mitra, M.; Zhu, W. &
 * Zabih, R. (2007) "Image Indexing Using Color Correlograms", IEEE Computer Society see also DOI
 * 10.1109/CVPR.1997.609412
 *
 * This is a wrapper class for the appropriate lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram
 *
 * @author Franz
 * @since 1.2.0
 */
public class AutoColorCorrelogram extends AbstractFeatureDescriptor {

    private static final Logger log = Logger.getLogger(AutoColorCorrelogram.class);
    /**
     * distance d > 0
     */
    private int distance = 3;

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        distance = properties.getInteger(LibProperties.AUTOCOLORCORRELOGRAM_DISTANCE, 3);
    }

    @Override
    public void run(ImageProcessor ip) {
        firePropertyChange(Progress.START);

        net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram acc = new net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram(distance);
        acc.extract(ip.getBufferedImage());
        addData(acc.getDoubleHistogram());

        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return "AutoColorCorrelogram";
    }
}
