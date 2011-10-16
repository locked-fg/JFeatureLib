package de.lmu.dbs.jfeaturelib.features;

import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.util.EnumSet;

/**
 * 
 * @author Benedikt
 */
public class SusanEdge implements FeatureDescriptor{

    private long time;
    private boolean calculated;
    private int progress;
    int WIDTH;
    int HEIGHT;
    int radius;
    int treshold;
    int[] features;
    int[][] picture;
    private ColorProcessor image;
    
    /**
     * 
     */    
    public SusanEdge(){
        this.radius = 2;
        this.treshold = 15;
        calculated = false;
    }
    
     public SusanEdge(int radius, int treshold){
        this.radius = radius;
        this.treshold = treshold;
        calculated = false;
        progress = 0;
    
     }
     
    /**
     * 
     */      
    @Override
    public double[] getFeatures() {
        if(calculated){
            return Arrays2.convertToDouble(features);
        }
        else{
            //TODO throw exception
            return null;
        }
    }
      
    /**
     * Defines the capability of the algorithm.
     * 
     * @see PlugInFilter
     * @see #supports() 
     */ 
    @Override
    public EnumSet<Supports> supports() {        
        EnumSet set = EnumSet.of(
            Supports.NoChanges,
            Supports.DOES_8C,
            Supports.DOES_8G,
            Supports.DOES_RGB
        );
        //set.addAll(DOES_ALL);
        return set;
    }
    
    /**
     * Returns information about the getFeauture returns in a String array.
     */ 
    @Override
    public String[] getDescription() {
        String[] info =  new String[2];

        return(info);
    }
    
    /**
     * 
     * @param ip ImageProcessor of the source image
     */    
    @Override
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis();
        this.image = (ColorProcessor)ip;
        process();
        calculated = true;
        time = (System.currentTimeMillis() - start);
    }
    
   /* 
    * http://users.fmrib.ox.ac.uk/~steve/susan/susan/node6.html
    * Place a circular mask around the pixel in question (the nucleus).
    * Using Equation 4 calculate the number of pixels within the circular mask which have similar brightness to the nucleus. (These pixels define the USAN.)
    * Using Equation 3 subtract the USAN size from the geometric threshold to produce an edge strength image.
    * Use moment calculations applied to the USAN to find the edge direction.
    * Apply non-maximum suppression, thinning and sub-pixel estimation, if required. 
    */    
    private void process() {
        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();
        picture = image.getIntArray();
        features = new int[WIDTH*HEIGHT];
        
        BufferedImage result = new BufferedImage(WIDTH, HEIGHT, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        
        int[][] mask = new int[radius*2+1][radius*2+1];
        
        int i = 0;
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
                //ignore borders
                if(x >= radius && x < WIDTH-radius && y >= radius && y < HEIGHT-radius){
                    
                    for(int maskX = 0; maskX <= radius*2; maskX++){
                        for(int maskY = 0; maskY <= radius*2; maskY++){
                            mask[maskX][maskY] = de.lmu.dbs.jfeaturelib.utils.RGBtoGray.ARGB_NTSC(picture[x-radius+maskX][y-radius+maskY]);
                            
                            //System.out.print(mask[maskX][maskY] + " ");
                        }//System.out.print("\n");
                    } //System.out.print("\n\n");
                    //horizontal edge
                    boolean edge = true;
                    for(int maskX = 0; maskX <= radius*2; maskX++){
                        for(int maskY = 0; maskY <= radius; maskY++){
                            if(edge && Math.abs(mask[maskX][maskY]-mask[radius][radius]) < treshold){
                                edge = true;
                            }
                            else{
                                edge=false;
                            }
                            
                        }
                        if(edge){
                            result.setRGB(x, y, -1);
                            //features[i] = -1;
                        }
                        else{
                            result.setRGB(x, y, -16777216);
                            //features[i] = -16777216;
                        }
                    }
                    //vertical edge
                    edge = true;
                    for(int maskX = 0; maskX <= radius; maskX++){
                        for(int maskY = 0; maskY <= radius*2; maskY++){
                            if(edge && Math.abs(mask[maskX][maskY]-mask[radius][radius]) < treshold){
                                edge = true;
                            }
                            else{
                                edge=false;
                            }
                            
                        }
                        if(edge){
                            result.setRGB(x, y, -1);
                            //features[i] = -1;
                        }
                        else if(/*features[i] != -1*/result.getRGB(x, y) != -1){
                            result.setRGB(x, y, -16777216);
                            //features[i] = -16777216;
                        }
                    }
                }
                //border
                else{}
                i++;
                //System.out.println("Int: " + picture[x][y] + ", converted: " + de.lmu.dbs.jfeaturelib.utils.RGBtoGray.ARGB_NTSC(picture[x][y]));
            }
        progress = (int)Math.round(i*(100.0/WIDTH*HEIGHT));
        }
        ColorProcessor cp = new ColorProcessor(result);
        features = (int[]) cp.convertToRGB().getBufferedImage().getData().getDataElements(0, 0, WIDTH, HEIGHT, null);
        //TODO this is not very nice
    }
    
    @Override
     public long getTime(){
         return time;
     }

    @Override
    public boolean isCalculated(){
        return calculated;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setArgs(double[] args) {
        if(args == null){
            
        }
        else if(args.length == 2){
            this.radius = Integer.valueOf((int)args[0]);
            this.treshold = Integer.valueOf((int)args[1]);
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Arguments array is not formatted correctly");
        }
    }
}
