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
package de.lmu.ifi.dbs.jfeaturelib.utils;

/**
 * Simple util to convert RGB values to gray values
 *
 * @author Benedikt
 */
public class RGBtoGray {

    /**
     * Converts NTSC RGB to gray
     *
     * See also http://en.wikipedia.org/wiki/Luma_%28video%29
     * 
     * @param p rgb color pixel
     * @return gray value
     */
    public static int ARGB_NTSC(int p) {
        int r = (p & 0xff0000) >> 16;
        int g = (p & 0xff00) >> 8;
        int b = p & 0xff;
        return NTSCRGBtoGray(r, g, b);
    }

    /**
     * Converts a color pixel to gray qith a factor of 1/3 each.
     *
     * @param p rgb color pixel
     * @return gray value
     */
    public static int ARGB_Mean(int p) {
        int r = (p & 0xff0000) >> 16;
        int g = (p & 0xff00) >> 8;
        int b = p & 0xff;
        return MeanRGBtoGray(r, g, b);
    }

    /**
     * Converts NTSC RGB to gray
     *
     * See also http://en.wikipedia.org/wiki/Luma_%28video%29
     * 
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return gray value
     */
    public static int NTSCRGBtoGray(int r, int g, int b) {
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    /**
     * Converts the rgb values to gray with equal weight
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return gray value
     */
    public static int MeanRGBtoGray(int r, int g, int b) {
        return (r + b + g) / 3;
    }
}
