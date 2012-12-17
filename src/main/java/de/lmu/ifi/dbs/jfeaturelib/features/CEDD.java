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
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.EnumSet;

/**
 * The CEDD feature was created, implemented and provided by Savvas A. Chatzichristofis
 *
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis,
 *
 * <i>CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for Image Indexing and Retrieval</i>, A.
 * Gasteratos, M. Vincze, and J.K. Tsotsos (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 *
 * This is a wrapper class for the corresponding lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.CEDD
 */
public class CEDD extends AbstractFeatureDescriptor {

    private double T0 = 14;
    private double T1 = 0.68;
    private double T2 = 0.98;
    private double T3 = 0.98;
    private boolean compact = false;
    private net.semanticmetadata.lire.imageanalysis.CEDD cedd;

    /**
     * Constructor with default parameters
     */
    public CEDD() {
    }

    @Override
    public void setProperties(LibProperties properties) throws IOException {
        T0 = properties.getDouble(LibProperties.CEDD_T0, 14);
        T1 = properties.getDouble(LibProperties.CEDD_T1, 0.68);
        T2 = properties.getDouble(LibProperties.CEDD_T2, 0.98);
        T3 = properties.getDouble(LibProperties.CEDD_T3, 0.98);
        compact = properties.getBoolean(LibProperties.CEDD_COMPACT, false);
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
        firePropertyChange(Progress.START);
        cedd = new net.semanticmetadata.lire.imageanalysis.CEDD(T0, T1, T2, T3, compact);
        cedd.extract(ip.getBufferedImage());
        addData(cedd.getDoubleHistogram());
        firePropertyChange(Progress.END);
    }

    /**
     * Returns the cedd instance from LIRE. The instance is available only after the run method has been called.
     *
     * @return
     */
    public net.semanticmetadata.lire.imageanalysis.CEDD getCedd() {
        return cedd;
    }

    public void setCedd(net.semanticmetadata.lire.imageanalysis.CEDD cedd) {
        this.cedd = cedd;
    }

    @Override
    public String getDescription() {
        return "CEDD";
    }

    @Override
    public EnumSet<Supports> supports() {
        EnumSet set = EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8C,
                Supports.DOES_8G,
                Supports.DOES_32,
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