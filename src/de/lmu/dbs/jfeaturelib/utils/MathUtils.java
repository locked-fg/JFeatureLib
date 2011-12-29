package de.lmu.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.utilities.Arrays2;

/**
 *
 * @author Benedikt
 */
public class MathUtils {

    /**
     * Deprecated since 2011/12/29, use {@link Arrays2#maxValue(double[])}
     * 
     * @param array Source array
     * @return Returns the maximal value from an double array
     * @deprecated since 2011/12/29, use {@link Arrays2#maxValue(double[])}
     * @see Arrays2#maxValue(double[]) 
     */
    public static double findDoubleMax(double[] array) {
        return Arrays2.maxValue(array);
    }

    /**
     * Deprecated since 2011/12/29, use {@link Arrays2#minValue(double[])}
     * 
     * @param array Source array
     * @return Returns the minimal value from an double array
     * @deprecated since 2011/12/29, use {@link Arrays2#minValue(double[])}
     * @see Arrays2#minValue(double[]) 
     */
    public static double findDoubleMin(double[] array) {
        return Arrays2.minValue(array);
    }

    /**
     * 
     * @param array Source array
     * @return Returns the maximal value from an int array
     * @deprecated since 2011/12/29, use {@link Arrays2#maxValue(int[])}
     * @see Arrays2#maxValue(int[]) 
     */
    public static double findIntMax(int[] array) {
        return Arrays2.minValue(array);
    }

    /**
     * 
     * @param array Source array
     * @return Returns the minimal value from an int array
     * @deprecated since 2011/12/29, use {@link Arrays2#minValue(int[])}
     * @see Arrays2#minValue(int[]) 
     */
    public static double findIntMin(int[] array) {
        return Arrays2.minValue(array);
    }

    /**
     * 
     * @param array Source array
     * @param max Maximal value in array
     * @param multiplier Value to multiply each entry with, use 1.0 for default
     * @return Returns a normalized double array
     */
    public static double[] normalizeDoubleArray(double[] array, double max, double multiplier) {
        double[] normalizedArray = array.clone();
        Arrays2.mul(normalizedArray, multiplier / max);
        return normalizedArray;
//        double[] normalizedArray = new double[array.length];
//        for (int i = 0; i < array.length; i++) {
//            normalizedArray[i] = (array[i] / max) * multiplier;
//        }
//        return normalizedArray;
    }

    /**
     * 
     * @param array Source array
     * @param max Maximal value in array
     * @param multiplier Value to multiply each entry with, use 1.0 for default
     * @return Returns a normalized double array
     */
    public static double[] normalizeIntArray(int[] array, double max, double multiplier) {
        return normalizeDoubleArray(Arrays2.convertToDouble(array), max, multiplier);
//        double[] normalizedArray = new double[array.length];
//        for (int i = 0; i < array.length; i++) {
//            normalizedArray[i] = (array[i] / max) * multiplier;
//        }
//        return normalizedArray;
    }
}
