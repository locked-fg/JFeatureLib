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
    
    int octaves = 4;
    int layers = 4;
    float threshold = 0.00100f;
    int initStep = 2;
    boolean upright = false;
    boolean displayOrientationVectors = true;
    boolean displayDescriptorWindows = false;
    int lineWidth = 1;
    boolean displayStatistics = false;

    public SURF(){
        accParams();
    }
    
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
    
    public void setOctaves(int octaves){
        this.octaves = octaves;        
        accParams();
    }
    public void setLayers(int layers){
        this.layers = layers;        
        accParams();
    }
    public void setThreshold(float threshold){
        this.threshold = threshold;        
        accParams();
    }
    public void setInitStep(int initStep){
        this.initStep = initStep;        
        accParams();
    }
    public void setUpright(boolean upright){
        this.upright = upright;        
        accParams();
    }
    public void setDisplayOrientationVectors(boolean displayOrientationVectors){
        this.displayOrientationVectors = displayOrientationVectors;        
        accParams();
    }
    public void setDisplayDescriptorWindows(boolean displayDescriptorWindows){
        this.displayDescriptorWindows = displayDescriptorWindows;        
        accParams();
    }
    public void setLineWidth(int lineWidth){
        this.lineWidth = lineWidth;        
        accParams();
    }
    public void setDisplayStatistics(boolean displayStatistics){
        this.displayStatistics = displayStatistics;          
        accParams();      
    }
    
    public int getOctaves(){
        return octaves;
    }
    public int getLayers(){
         return layers;
    }
    public float getThreshold(){
         return threshold;
    }
    public int getInitStep(){
         return initStep;
    }
    public boolean getUpright(){
         return upright;
    }
    public boolean getDisplayOrientationVectors(){
         return displayOrientationVectors;
    }
    public boolean getDisplayDescriptorWindows(){
         return displayDescriptorWindows;
    }
    public int getLineWidth(){
         return lineWidth;
    }
    public boolean getDisplayStatistics(){
         return displayStatistics;
    }
    public Params getParams(){
        return params;
    }

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
    
    public static InterestPoint doubleArrayToInterestPoint(double[] darr){
        if(darr.length == 70){
            InterestPoint ip = new InterestPoint((float)darr[64], (float)darr[65], (float)darr[66], (float)darr[67], (float)darr[68]);
            float[] floatDesc = new float[64];
            for(int i=0; i<64; i++){
                    floatDesc[i] = (float)darr[i];
            }
            ip.descriptor = floatDesc;
            return ip;
        }
        else return null;
    }
    
    public double[] interestPoinToDoubleArray(InterestPoint ipt){
        InterestPoint ip = ipt;
        
        //An InterestPoint has 64 floats for the descriptor and 6 other items
        double[] interestpoint = new double[70];
        for(int i=0; i<64; i++){
            interestpoint[i] = ip.descriptor[i];
        }
        interestpoint[64] = ip.x;
        interestpoint[65] = ip.y;
        interestpoint[66] = ip.strength;
        interestpoint[67] = ip.trace;
        interestpoint[68] = ip.scale;
        interestpoint[69] = (interestpoint[67] >= 0) ? 1:0;
        
        return interestpoint;
    }
}
