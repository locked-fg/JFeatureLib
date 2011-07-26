package de.lmu.dbs.jfeaturelib.utils;

/**
 * Simple util to return a gray value for given RGB value
 * @author Benedikt
 */
public class RGBtoGray {
    
    public static int NTSCRGBtoGray(int r, int g, int b){
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }
    
    public static int MeanRGBtoGray(int r, int g, int b){
        return (int)(r+b+g)/3;
    }
}
