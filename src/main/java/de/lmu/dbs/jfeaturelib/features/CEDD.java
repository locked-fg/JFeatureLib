package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.util.EnumSet;

/**
 * The CEDD feature was created, implemented and provided by Savvas A. Chatzichristofis
 *
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis,
 *
 * <i>CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for Image Indexing and Retrieval</i>, A.
 * Gasteratos, M. Vincze, and J.K. Tsotsos (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 */
public class CEDD extends AbstractFeatureDescriptor {

    private double T0 = 14;
    private double T1 = 0.68;
    private double T2 = 0.98;
    private double T3 = 0.98;
    private boolean compact = false;

    /**
     * Constructor with default parameters
     */
    public CEDD() {
    }

    /**
     * Constructs new CEDD descriptor
     *
     * @param Th0 Treshold for edge detection
     * @param Th1 Treshold for non-directional edges
     * @param Th2 Treshold for horizontal and vertical edges
     * @param Th3 Treshold for diagonal edges
     * @param compactDescriptor Compact output of results
     */
    public CEDD(double Th0, double Th1, double Th2, double Th3, boolean compactDescriptor) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
        this.compact = compactDescriptor;
    }

    @Override
    public void run(ImageProcessor ip) {
        de.lmu.dbs.jfeaturelib.features.lire.CEDD cedd = new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, compact);

        firePropertyChange(Progress.START);
        cedd.extract(ip.getBufferedImage());
        addData(cedd.getData());
        firePropertyChange(Progress.END);
    }

    @Override
    public String getDescription() {
        return new de.lmu.dbs.jfeaturelib.features.lire.CEDD(T0, T1, T2, T3, compact).getStringRepresentation();
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

    //<editor-fold defaultstate="collapsed" desc="getters & setters">
    /**
     * Treshold for edge detection
     *
     * @return Treshold for edge detection
     */
    public double getT0() {
        return this.T0;
    }

    /**
     * Treshold for edge detection
     *
     * @param t0 Treshold for edge detection
     */
    public void setT0(double t0) {
        this.T0 = t0;
    }

    /**
     * Treshold for non-directional edges
     *
     * @return Treshold for non-directional edges
     */
    public double getT1() {
        return this.T1;
    }

    /**
     * Treshold for non-directional edges
     *
     * @param t1 Treshold for non-directional edges
     */
    public void setT1(double t1) {
        this.T1 = t1;
    }

    /**
     * Treshold for horizontal and vertical edges
     *
     * @return Treshold for horizontal and vertical edges
     */
    public double getT2() {
        return this.T2;
    }

    /**
     * Treshold for horizontal and vertical edges
     *
     * @param t2 Treshold for horizontal and vertical edges
     */
    public void setT2(double t2) {
        this.T2 = t2;
    }

    /**
     * Treshold for diagonal edges
     *
     * @return Treshold for diagonal edges
     */
    public double getT3() {
        return this.T3;
    }

    /**
     * Treshold for diagonal edges
     *
     * @param t3 Treshold for diagonal edges
     */
    public void setT3(double t3) {
        this.T3 = t3;
    }

    /**
     * Compact output of results
     *
     * @return Compact output of results
     */
    public boolean getCompact() {
        return compact;
    }

    /**
     * Compact output of results
     *
     * @param compact Compact output of results
     */
    public void setCompact(boolean compact) {
        this.compact = compact;
    }
    //</editor-fold>
}