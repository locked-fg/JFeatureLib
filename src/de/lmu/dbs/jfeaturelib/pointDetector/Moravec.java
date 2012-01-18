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
 * TODO Documentation
 * TODO add constructors
 * @author Robert Zelhofer
 * http://www.ri.cmu.edu/pub_files/pub4/moravec_hans_1980_1/moravec_hans_1980_1.pdf
 */
public class Moravec implements PointDetector {
    
    private List<ImagePoint> corners;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private int threshold;
    private int size;
    
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
        
        //TODO implementation
        int width = ip.getWidth();
        int height = ip.getHeight();
        int radius = size / 2;
        
        for (int y = radius, maxY = height - radius; y < maxY; y++) {
            for (int x = radius, maxX = width - radius; x < maxX; x++) {
                int minSum = Integer.MAX_VALUE;
                
                for (int k = 0; k < 8; k++) {
                    int sy = y + yBlueDelta[k];
                    int sx = x + xBlueDelta[k];
                    
                    
                    if  (sy < radius || sx < radius || sy >= maxY || sx >= maxX) { continue;}
                    
                    int sum = 0;
                    
                    
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
    
    
    public Moravec () {
        threshold = 500;
        size = 3;
        corners = new ArrayList<>();
    }
}
