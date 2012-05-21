package de.lmu.dbs.jfeaturelib.utils;

/**
 * Simple util to return a gray value for given RGB value
 *
 * @author Benedikt
 */
public class RGBtoGray {

    /**
     * Converts NTSC RGB to gray
     *
     * @param p rgb color pixel
     * @return gray value
     * @see http://en.wikipedia.org/wiki/Luma_%28video%29
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
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return gray value
     * @see http://en.wikipedia.org/wiki/Luma_%28video%29
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
