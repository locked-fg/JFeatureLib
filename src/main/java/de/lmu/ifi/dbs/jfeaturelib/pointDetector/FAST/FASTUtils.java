package de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST;

/**
 *
 * @author Robert ZELHOFER
 */
public class FASTUtils {

    public static void make_offsets(int[] pixel, int row_stride, int x, int y) {
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
