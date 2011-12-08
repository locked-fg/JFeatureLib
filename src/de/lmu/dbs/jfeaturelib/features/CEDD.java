package de.lmu.dbs.jfeaturelib.features;


import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.List;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The CEDD feature was created, implemented and provided by Savvas A. Chatzichristofis<br/>
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis,
 * <i>CEDD: Color and Edge Directivity Descriptor. A Compact
 * Descriptor for Image Indexing and Retrieval</i>, A. Gasteratos, M. Vincze, and J.K.
 * Tsotsos (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 */
public class CEDD implements FeatureDescriptor {
    public double T0;
    public double T1;
    public double T2;
    public double T3;
    public boolean Compact = false;
    protected double[] data = null;
    private de.lmu.dbs.jfeaturelib.features.lire.CEDD cedd;
    private BufferedImage bi;    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public CEDD(double Th0, double Th1, double Th2, double Th3, boolean CompactDescriptor) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
        this.Compact = CompactDescriptor;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }

    public CEDD() {
        this.T0 = 14;
        this.T1 = 0.68;
        this.T2 = 0.98;
        this.T3 = 0.98;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }

    @Override
    public void run(ImageProcessor ip) {
        bi = ip.getBufferedImage();
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        cedd.extract(bi);
        data = cedd.getData();
        pcs.firePropertyChange(Progress.getName(), null, new Progress(100, "all done"));
    }
    
    @Override
    public List<double[]> getFeatures() {
        if (data != null) {
            ArrayList<double[]> result = new ArrayList<>(1);
            result.add(data);
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getDescription() {
        return cedd.getStringRepresentation();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public EnumSet<Supports> supports() {        
        EnumSet set = EnumSet.of(
            Supports.NoChanges,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB
        );
        //set.addAll(DOES_ALL);
        return set;
    }
}