package de.lmu.dbs.jfeaturelib.pointDetector;

import de.lmu.dbs.jfeaturelib.ImagePoint;
import de.lmu.dbs.jfeaturelib.Progress;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * 
 * Moravec Corner Detector
 * 
 * Returns a List of ImagePoint Corners
 * 
 * @author Robert Zelhofer
 * @see http://www.ri.cmu.edu/pub_files/pub4/moravec_hans_1980_1/moravec_hans_1980_1.pdf
 */
public class Moravec implements PointDetector {
    //Returning List of Corners
    private List<ImagePoint> corners;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    //Threshold value
    private int threshold;
    //windowsize value
    private int size;
    
    //static window sizes
    //TODO create flexible windowSize
    private final int[] xBlueDelta = new int[] { 
        -1, 0,  1, 
        -1,      1,
        -1,  0,  1 
    };
    private final int[] yBlueDelta = new int[] { 
        -1, -1, -1, 
        0,      0,
        1,  1,  1 
    };
    
    private final int[] xRedDelta = new int[] { 
        -1, 0,  1, 
        -1, 0,  1,
        -1,  0,  1 
    };
    private final int[] yRedDelta = new int[] { 
        -1, -1, -1, 
        0,  0,  0,
        1,  1,  1 
    };
    

    @Override
    public List<ImagePoint> getPoints() {
        return corners;
    }

    @Override
    public EnumSet<Supports> supports() {
        return EnumSet.of(
                Supports.NoChanges,
                Supports.DOES_8G);
    }

    @Override
    public void run(ImageProcessor ip) {
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        
        
        int width = ip.getWidth();
        int height = ip.getHeight();
        //radius is used to prevent the Algorithm to search out of bounds
        int radius = size / 2;
        
        //Check each Pixel
        for (int y = radius, maxY = height - radius; y < maxY; y++) {
            for (int x = radius, maxX = width - radius; x < maxX; x++) {
                int minSum = Integer.MAX_VALUE;
                
                //check the shifting directions
                for (int k = 0; k < 8; k++) {
                    // sy and sx is the Center Point of the Shifted Window
                    int sy = y + yBlueDelta[k];
                    int sx = x + xBlueDelta[k];
                    
                    // Shifted Window out of Bounds Check
                    if  (sy < radius || sx < radius || sy >= maxY || sx >= maxX) { continue;}
                    
                    int sum = 0;
                    
                    // Sum up the difference of the window around the actual pixel and the shifted window
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            int redX = x + xRedDelta[i + j];
                            int redY = y + yRedDelta[i + j];
                            int redValue = ip.getPixel(redX, redY);
                            int blueX = sx + xRedDelta[i + j];
                            int blueY = sy + yRedDelta[i + j];
                            int blueValue = ip.getPixel(blueX, blueY);
                            int dif = redValue - blueValue;
                            sum += dif * dif;                            
                        }
                    }
                    
                    if (sum < minSum) {
                        minSum = sum;
                    }
                }
                
                //System.out.print("\t" + minSum+ "\t|");
                
                //Threshold check
                if (minSum < threshold) {
                    minSum = 0;
                } else {
                    corners.add(new ImagePoint(x, y));
                }
                
            }
            //System.out.println();
            
        }
        
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * Creates Moravec Detector with Default values: Threshold = 500
     */
    public Moravec () {
        this(500, 3);
    }
    
    /**
     * Creates Moravec Detector
     * @param threshold Threshold Value, which is used for filtering uninteresting Points
     */
    public Moravec (int threshold) {
        this(threshold, 3);
    }
    
    /**
     * Creates Moravec Detector
     * @param threshold Threshold Value, which is used for filtering uninteresting Points
     * @param size Window Size. Must be Odd. Used for Initial Search for Corners and Search for Local Maximums.
     */
    private Moravec (int threshold, int size) {
        this.threshold = threshold;
        this.size = size;
        corners = new ArrayList<>();
    }
}
