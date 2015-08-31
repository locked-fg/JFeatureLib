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

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Zelhofer
 */
public class FASTNonMaxSuppression {

    private boolean compare(double x, double y) {
        return x >= y;
    }

    public List<ImagePoint> nonmax_suppression(List<ImagePoint> corners, int[] scores, int num_corners) {
        int num_nonmax = 0;
        int last_row;
        int[] row_start;
        int i, j;
        List<ImagePoint> ret_nonmax = new ArrayList<>();

        
        int sz = num_corners;

        /*Point above points (roughly) to the pixel above the one of interest, if there
        is a feature there.*/
        int point_above = 0;
        int point_below = 0;


        if (num_corners < 1) {
            return ret_nonmax;
        }

        /* Find where each row begins
        (the corners are output in raster scan order). A beginning of -1 signifies
        that there are no corners on that row. */
        last_row = (int)corners.get(num_corners - 1).y;
        row_start = new int[last_row + 1];

        for (i = 0; i < last_row + 1; i++) {
            row_start[i] = -1;
        }

        {
            int prev_row = -1;
            for (i = 0; i < num_corners; i++) {
                if ((int)corners.get(i).y != prev_row) {
                    row_start[(int)corners.get(i).y] = i;
                    prev_row = (int)corners.get(i).y;
                }
            }
        }



        for (i = 0; i < sz; i++) {
            boolean isCorner = false;
            int score = scores[i];
            ImagePoint pos = corners.get(i);

            /*Check left */
            if (i > 0) {
                if (corners.get(i - 1).x == pos.x - 1 && corners.get(i - 1).y == pos.y && compare(scores[i - 1], score)) {
                    continue;
                }
            }

            /*Check right*/
            if (i < (sz - 1)) {
                if (corners.get(i + 1).x == pos.x + 1 && corners.get(i + 1).y == pos.y && compare(scores[i + 1], score)) {
                    continue;
                }
            }

            /*Check above (if there is a valid row above)*/
            if (pos.y != 0 && row_start[(int)pos.y - 1] != -1) {
                /*Make sure that current point_above is one
                row above.*/
                if (corners.get(point_above).y < pos.y - 1) {
                    point_above = row_start[(int)pos.y - 1];
                }

                /*Make point_above point to the first of the pixels above the current point,
                if it exists.*/
                for (; corners.get(point_above).y < pos.y && corners.get(point_above).x < pos.x - 1; point_above++) {
                }


                for (j = point_above; corners.get(j).y < pos.y && corners.get(j).x <= pos.x + 1; j++) {
                    double x = corners.get(j).x;
                    if ((x == pos.x - 1 || x == pos.x || x == pos.x + 1) && compare(scores[j], score)) {
                        isCorner = false;
                        continue;
                    } else {
                        isCorner = true;
                    }
                    
                    
                }

            }

            /*Check below (if there is anything below)*/
            if (pos.y != last_row && row_start[(int)pos.y + 1] != -1 && point_below < sz) /*Nothing below*/ {
                if (corners.get(point_below).y < pos.y + 1) {
                    point_below = row_start[(int)pos.y + 1];
                }

                /* Make point below point to one of the pixels belowthe current point, if it
                exists.*/
                for (; point_below < sz && corners.get(point_below).y == pos.y + 1 && corners.get(point_below).x < pos.x - 1; point_below++) {
                }

                for (j = point_below; j < sz && corners.get(j).y == pos.y + 1 && corners.get(j).x <= pos.x + 1; j++) {
                    double x = corners.get(j).x;
                    if ((x == pos.x - 1 || x == pos.x || x == pos.x + 1) && compare(scores[j], score)) {
                        isCorner = false;
                        continue;
                    } else {
                        isCorner = true;
                    }
                    
                    
                }
            }
            
            ret_nonmax.add(corners.get(i));
            
        }
        return ret_nonmax;
    }
}
