/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
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
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

/**
 * Wrapper for the according LIRE class.
 * 
 *  64 bin Opponent Histogram, based on the Opponent color space as described in van de Sande, Gevers & Snoek (2010)
 * "Evaluating Color Descriptors for Object and Scene Recognition", IEEE PAMI:
 *
 * <pre>
 * &#64;ARTICLE{Sande2010,
 *   author={van de Sande, K.E.A. and Gevers, T. and Snoek, C.G.M.},
 *   journal={Pattern Analysis and Machine Intelligence, IEEE Transactions on},
 *   title={Evaluating Color Descriptors for Object and Scene Recognition},
 *   year={2010},
 *   month={sept. },
 *   volume={32},
 *   number={9},
 *   pages={1582 -1596},
 *   doi={10.1109/TPAMI.2009.154},
 *   ISSN={0162-8828},
 *}
 * </pre>
 * 
 * @author Franz
 * @see <a href="http://staff.science.uva.nl/~ksande/pub/vandesande-cvpr2008.pdf">original paper</a>
 * @since 1.4.0
 */
public class OpponentHistogram extends LireWrapper{

    public OpponentHistogram() {
        super(new net.semanticmetadata.lire.imageanalysis.OpponentHistogram());
    }
    
}
