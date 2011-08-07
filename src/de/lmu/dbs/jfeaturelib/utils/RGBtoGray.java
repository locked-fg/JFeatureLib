package de.lmu.dbs.jfeaturelib.utils;

/**
 * Simple util to return a gray value for given RGB value
 * @author Benedikt
 */
public class RGBtoGray {
    
    public static int ARGB_NTSC(int p){
        int r = (p & 0xff0000) >> 16;
	int g = (p & 0xff00) >> 8;
	int b = p & 0xff;
        return NTSCRGBtoGray(r, g, b);
    }
    
    public static int ARGB_Mean(int p){
        int r = (p & 0xff0000) >> 16;
	int g = (p & 0xff00) >> 8;
	int b = p & 0xff;
        return MeanRGBtoGray(r, g, b);
    }
    
    public static int NTSCRGBtoGray(int r, int g, int b){
        return (int)(0.2126*r+0.7152*g+0.0722*b);
    }
    
    public static int MeanRGBtoGray(int r, int g, int b){
        return (int)(r+b+g)/3;
    }
}
