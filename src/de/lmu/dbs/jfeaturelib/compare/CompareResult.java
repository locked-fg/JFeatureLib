package de.lmu.dbs.jfeaturelib.compare;

/**
 * A simple data structure, containing the results of a comparison
 * @author Benedikt
 */
public class CompareResult {
    
    private double distance;
    private double similarity;
    
    public CompareResult(double distance, double similarity){
        this.distance = distance;
        this.similarity = similarity;
    }
    
    public double getDistance(){
        return distance;
    }
    
    public double getSimilarity(){
        return similarity;
    }
}
