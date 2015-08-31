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
 * 
 * ##########################################################
 * FAST Corner Detector
 * 
 * FAST was first published By Edward Rosten in 2005, 2006:<br>
 * - Fusing points and lines for high performance tracking (2005) and<br>
 * - Machine learning for high-speed corner detection (2006).
 * 
 * Please see http://www.edwardrosten.com/work/fast.html for links to the according papers, more information 
 * and other reference implementations. At this page you can also find BSD-licensed code.
 * 
 * In case of using this code, the above copyright notive must retain the attribution to the author.
 */
package de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST;

/**
 * @author Robert ZELHOFER
 */
class FASTUtils {

    static void make_offsets(int[] pixel, int row_stride, int x, int y) {
        pixel[0] = 0 + row_stride * 3 + x + y * row_stride;
        pixel[1] = 1 + row_stride * 3 + x + y * row_stride;
        pixel[2] = 2 + row_stride * 2 + x + y * row_stride;
        pixel[3] = 3 + row_stride * 1 + x + y * row_stride;
        pixel[4] = 3 + row_stride * 0 + x + y * row_stride;
        pixel[5] = 3 + row_stride * -1 + x + y * row_stride;
        pixel[6] = 2 + row_stride * -2 + x + y * row_stride;
        pixel[7] = 1 + row_stride * -3 + x + y * row_stride;
        pixel[8] = 0 + row_stride * -3 + x + y * row_stride;
        pixel[9] = -1 + row_stride * -3 + x + y * row_stride;
        pixel[10] = -2 + row_stride * -2 + x + y * row_stride;
        pixel[11] = -3 + row_stride * -1 + x + y * row_stride;
        pixel[12] = -3 + row_stride * 0 + x + y * row_stride;
        pixel[13] = -3 + row_stride * 1 + x + y * row_stride;
        pixel[14] = -2 + row_stride * 2 + x + y * row_stride;
        pixel[15] = -1 + row_stride * 3 + x + y * row_stride;
    }
}
