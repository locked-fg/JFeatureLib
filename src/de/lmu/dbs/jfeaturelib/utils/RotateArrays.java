package de.lmu.dbs.jfeaturelib.utils;

/**
 * Rotates 2D arrays like matrices
 * @author Benedikt
 * @deprecated since 1.3.2012
 */
public class RotateArrays {
    
    public static int[][] rotateIntCW(int[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        int[][] ret = new int[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }
    
    public static double[][] rotateDoubleCW(double[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        double[][] ret = new double[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }
        
    public static float[][] rotateFloatCW(float[][] mat) {
        final int M = mat.length;
        final int N = mat[0].length;
        float[][] ret = new float[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M-1-r] = mat[r][c];
            }
        }
        return ret;
    }

}
