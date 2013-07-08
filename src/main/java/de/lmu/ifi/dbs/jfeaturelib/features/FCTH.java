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

/**
 * The FCTH feature was created, implemented and provided by Savvas A. Chatzichristofis
 *
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis, <i>FCTH: Fuzzy Color and
 * Texture Histogram - A Low Level Feature for Accurate Image Retrieval</i>, in Proceedings of the Ninth International
 * Workshop on Image Analysis for Multimedia Interactive Services, IEEE, Klagenfurt, May, 2008.
 * 
 * This is a wrapper class for the corresponding lire class
 *
 * @see net.semanticmetadata.lire.imageanalysis.FCTH
 */
public class FCTH extends LireWrapper {


    public FCTH() {
        super(new net.semanticmetadata.lire.imageanalysis.FCTH());
    }

    /**
     * @return the FCTH Lire feature object
     * @deprecated since 1.4.0
     * @see #getLireFeature() 
     */
    @Deprecated
    public net.semanticmetadata.lire.imageanalysis.FCTH getFcth() {
        return (net.semanticmetadata.lire.imageanalysis.FCTH) getLireFeature();
    }
}