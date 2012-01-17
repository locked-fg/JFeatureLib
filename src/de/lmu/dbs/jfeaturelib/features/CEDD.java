package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * The CEDD feature was created, implemented and provided by Savvas A.
 * Chatzichristofis<br/> More information can be found in: Savvas A.
 * Chatzichristofis and Yiannis S. Boutalis,
 *
 * <i>CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for
 * Image Indexing and Retrieval</i>, A. Gasteratos, M. Vincze, and J.K. Tsotsos
 * (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 */
public class CEDD implements FeatureDescriptor {

    private double T0;
    private double T1;
    private double T2;
    private double T3;
    private boolean Compact = false;
    /**
     * 
     */
    protected double[] data = null;
    private de.lmu.dbs.jfeaturelib.features.lire.CEDD cedd;
    private BufferedImage bi;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructor with default parameters
     */

    public CEDD() {
        this.T0 = 14;
        this.T1 = 0.68;
        this.T2 = 0.98;
        this.T3 = 0.98;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }    
    
    /**
     * Constructs new CEDD descriptor
     * 
     * @param Th0 Treshold for edge detection
     * @param Th1 Treshold for non-directional edges
     * @param Th2 Treshold for horizontal and vertical edges
     * @param Th3 Treshold for diagonal edges
     * @param CompactDescriptor Compact output of results
     */
    public CEDD(double Th0, double Th1, double Th2, double Th3, boolean CompactDescriptor) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
        this.Compact = CompactDescriptor;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }

    /**
     * Treshold for edge detection
     * @return Treshold for edge detection
     */
    public double getT0(){
        return this.T0;
    }
    /**
     * Treshold for edge detection
     * @param t0 Treshold for edge detection
     */
    public void setT0(double t0){
        this.T0 = t0;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }
    /**
     * Treshold for non-directional edges
     * @return Treshold for non-directional edges
     */
    public double getT1(){
        return this.T1;
    }
    /**
     * Treshold for non-directional edges
     * @param t1 Treshold for non-directional edges
     */
    public void setT1(double t1){
        this.T1 = t1;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }
    /**
     * Treshold for horizontal and vertical edges
     * @return Treshold for horizontal and vertical edges
     */
    public double getT2(){
        return this.T2;
    }
    /**
     * Treshold for horizontal and vertical edges
     * @param t2 Treshold for horizontal and vertical edges
     */
    public void setT2(double t2){
        this.T2 = t2;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }
    /**
     * Treshold for diagonal edges
     * @return Treshold for diagonal edges
     */
    public double getT3(){
        return this.T3;
    }
    /**
     * Treshold for diagonal edges
     * @param t3 Treshold for diagonal edges
     */
    public void setT3(double t3){
        this.T3 = t3;
        cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, Compact);
    }
    /**
     * Compact output of results
     * @return Compact output of results
     */
    public boolean getCompact(){
        return Compact;
    }
    /**
     * Compact output of results
     * @param compact Compact output of results
     */
    public void setCompact(boolean compact){
        this.Compact = compact;
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
                Supports.DOES_RGB);
        //set.addAll(DOES_ALL);
        return set;
    }
}