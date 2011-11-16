package de.lmu.dbs.jfeaturelib.features;

import de.lmu.dbs.jfeaturelib.Progress;
import de.lmu.dbs.jfeaturelib.features.surf.IJFacade;
import de.lmu.dbs.jfeaturelib.features.surf.IntegralImage;
import de.lmu.dbs.jfeaturelib.features.surf.InterestPoint;
import de.lmu.dbs.jfeaturelib.features.surf.Params;
import ij.process.ImageProcessor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This is not much more than a Wrapper for the ImageJ SURF plugin (http://www.labun.com/imagej-surf/)
 * @author Benedikt
 */
public class SURF implements FeatureDescriptor{
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Params params;
    private ImageProcessor image;
    private IntegralImage intImg;
    private List<InterestPoint> ipts;
    private List<double[]> results;
    //Default parameters
    private int octaves = 4;
    private int layers = 4;
    private float threshold = 0.00100f;
    private int initStep = 2;
    private boolean upright = false;
    private boolean displayOrientationVectors = true;
    private  boolean displayDescriptorWindows = false;
    private int lineWidth = 1;
    private boolean displayStatistics = false;

    /**
     * Empty default constructor
     */
    public SURF(){
        //Accumulate parameters
        accParams();
    }
    
    /**
     * Constructs a SURF instance with all params
     * @param octaves
     * @param layers
     * @param threshold
     * @param initStep
     * @param upright
     * @param displayOrientationVectors
     * @param displayDescriptorWindows
     * @param lineWidth
     * @param displayStatistics
     */
    public SURF(int octaves, int layers, float threshold, int initStep, boolean upright, boolean displayOrientationVectors, boolean displayDescriptorWindows, int lineWidth, boolean displayStatistics){
        this.octaves = octaves;
        this.layers = layers;
        this.threshold = threshold;
        this.initStep = initStep;
        this.upright = upright;
        this.displayOrientationVectors = displayOrientationVectors;
        this.displayDescriptorWindows = displayDescriptorWindows;
        this.lineWidth = lineWidth;
        this.displayStatistics = displayStatistics;        
        accParams();
    }
    
    /**
     * Set octaves param
     * @param octaves
     */
    public void setOctaves(int octaves){
        this.octaves = octaves;        
        accParams();
    }
    /**
     * Set layers param
     * @param layers
     */
    public void setLayers(int layers){
        this.layers = layers;        
        accParams();
    }
    /**
     * Set treshold param
     * @param threshold
     */
    public void setThreshold(float threshold){
        this.threshold = threshold;        
        accParams();
    }
    /**
     * Set initial step param
     * @param initStep
     */
    public void setInitStep(int initStep){
        this.initStep = initStep;        
        accParams();
    }
    /**
     * Set if rotation invariant should be excluded
     * @param upright
     */
    public void setUpright(boolean upright){
        this.upright = upright;        
        accParams();
    }
    /**
     * Set if line width should display IP strength
     * @param displayOrientationVectors
     */
    public void setDisplayOrientationVectors(boolean displayOrientationVectors){
        this.displayOrientationVectors = displayOrientationVectors;        
        accParams();
    }
    /**
     * Set if windows for each IP should be displayed
     * @param displayDescriptorWindows
     */
    public void setDisplayDescriptorWindows(boolean displayDescriptorWindows){
        this.displayDescriptorWindows = displayDescriptorWindows;        
        accParams();
    }
    /**
     * Set overall line width
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth){
        this.lineWidth = lineWidth;        
        accParams();
    }
    /**
     * Sets if detailled statistics are displayed
     * @param displayStatistics
     */
    public void setDisplayStatistics(boolean displayStatistics){
        this.displayStatistics = displayStatistics;          
        accParams();      
    }
    
    /**
     * 
     * @return Number of octaves
     */
    public int getOctaves(){
        return octaves;
    }
    /**
     * 
     * @return number of layers
     */
    public int getLayers(){
         return layers;
    }
    /**
     * 
     * @return treshold
     */
    public float getThreshold(){
         return threshold;
    }
    /**
     * 
     * @return initial step
     */
    public int getInitStep(){
         return initStep;
    }
    /**
     * 
     * @return no rotation invariant
     */
    public boolean getUpright(){
         return upright;
    }
    /**
     * 
     * @return vector lines for each IP
     */
    public boolean getDisplayOrientationVectors(){
         return displayOrientationVectors;
    }
    /**
     * 
     * @return windows for each IP
     */
    public boolean getDisplayDescriptorWindows(){
         return displayDescriptorWindows;
    }
    /**
     * 
     * @return overall line width
     */
    public int getLineWidth(){
         return lineWidth;
    }
    /**
     * 
     * @return detailled statistics
     */
    public boolean getDisplayStatistics(){
         return displayStatistics;
    }
    /**
     * 
     * @return Params object holding parameters
     */
    public Params getParams(){
        return params;
    }
    
    /**
     * Accumulate all single parameters into one Params object
     */
    
    private void accParams(){
        params = new Params(octaves, layers, threshold, initStep, upright, displayOrientationVectors, displayDescriptorWindows, lineWidth, displayStatistics);    
    }


    @Override
    public String getDescription() {
        String info =  "SURF Interest Points\n";
        info +=  "The first 64 numbers of an InterestPoint describe its descriptor";
        return(info);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

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
    
    @Override
    public List<double[]> getFeatures() {
        
        results = new ArrayList<>();
        for(int i=0; i < ipts.size(); i++){
            results.add(interestPoinToDoubleArray(ipts.get(i)));
        }
        return results;
    }
    
    @Override
    public void run(ImageProcessor ip) {
        this.image = ip;
        
        intImg = new IntegralImage(image, true);
        
        pcs.firePropertyChange(Progress.getName(), null, Progress.START);
        process();
        pcs.firePropertyChange(Progress.getName(), null, Progress.END);
    }

    private void process() {
        ipts = IJFacade.detectAndDescribeInterestPoints(intImg, params);
    }
    
    /**
     * Converts a JFeatureLib-compatible double[] into an InterestPoint
     * @param darr double[] holding IP values
     * @return IP
     */
    public static InterestPoint doubleArrayToInterestPoint(double[] darr){
        if(darr.length == 71){
            InterestPoint ip = new InterestPoint((float)darr[64], (float)darr[65], (float)darr[66], (float)darr[67], (float)darr[68]);
            float[] floatDesc = new float[64];
            for(int i=0; i<64; i++){
                    floatDesc[i] = (float)darr[i];
            }
            ip.descriptor = floatDesc;
            ip.orientation = (float)darr[70];
            return ip;
        }
        else return null;
    }
    
    /**
     * Converts an InterestPoint into a JFeatureLib-compatible double[]
     * @param ipt IP
     * @return double[] holding IP values
     */
    public double[] interestPoinToDoubleArray(InterestPoint ipt){
        InterestPoint ip = ipt;
        
        //An InterestPoint has 64 floats for the descriptor and 7 other items
        double[] interestpoint = new double[71];
        for(int i=0; i<64; i++){
            interestpoint[i] = ip.descriptor[i];
        }
        interestpoint[64] = ip.x;
        interestpoint[65] = ip.y;
        interestpoint[66] = ip.strength;
        interestpoint[67] = ip.trace;
        interestpoint[68] = ip.scale;
        interestpoint[69] = (interestpoint[67] >= 0) ? 1:0;
        interestpoint[70] = ip.orientation;
        
        return interestpoint;
    }
}
