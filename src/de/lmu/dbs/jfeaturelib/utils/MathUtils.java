package de.lmu.dbs.jfeaturelib.utils;

/**
 *
 * @author Benedikt
 */
public class MathUtils {
    
    /**
     * 
     * @param array Source array
     * @return Returns the maximal value from an double array
     */
    public static double findDoubleMax(double[] array){
        
        double max = Double.MIN_VALUE;
        
        for(int i = 0; i<array.length; i++){
            if(max < array[i]){
                max = array[i];
            }
        }
        
        return max;
    }
    
    /**
     * 
     * @param array Source array
     * @return Returns the minimal value from an double array
     */
    public static double findDoubleMin(double[] array){
        
        double min = Double.MAX_VALUE;
        
        for(int i = 0; i<array.length; i++){
            if(min > array[i]){
                 min = array[i];
            }
        }
        
        return  min;
    }
    
    /**
     * 
     * @param array Source array
     * @return Returns the maximal value from an int array
     */
    public static double findIntMax(int[] array){
        
        int max = Integer.MIN_VALUE;
        
        for(int i = 0; i<array.length; i++){
            if(max < array[i]){
                max = array[i];
            }
        }
        
        return max;
    }
    
    /**
     * 
     * @param array Source array
     * @return Returns the minimal value from an int array
     */
    public static double findIntMin(int[] array){
        
        int min = Integer.MAX_VALUE;
        
        for(int i = 0; i<array.length; i++){
            if(min > array[i]){
                 min = array[i];
            }
        }
        
        return  min;
    }
    
    /**
     * 
     * @param array Source array
     * @param max Maximal value in array
     * @param multiplier Value to multiply each entry with, use 1.0 for default
     * @return Returns a normalized double array
     */
    public static double[] normalizeDoubleArray(double[] array, double max, double multiplier){
        double[] normalizedArray = new double[array.length];
        for(int i=0; i<array.length; i++){
                    normalizedArray[i] = (array[i]/max)*multiplier;
        }
        return normalizedArray;
    }
    
    /**
     * 
     * @param array Source array
     * @param max Maximal value in array
     * @param multiplier Value to multiply each entry with, use 1.0 for default
     * @return Returns a normalized double array
     */
    public static double[] normalizeIntArray(int[] array, double max, double multiplier){
        double[] normalizedArray = new double[array.length];
                for(int i=0; i<array.length; i++){
                    normalizedArray[i] = (array[i]/max)*multiplier;
        }
        return normalizedArray;
    }
}
