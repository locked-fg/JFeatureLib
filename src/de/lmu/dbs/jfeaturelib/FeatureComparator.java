package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.features.surf.InterestPoint;
import de.lmu.dbs.jfeaturelib.features.surf.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Benedikt
 */
public class FeatureComparator {
    
    double[] normalizedCompResult;
    double[] normalizedResult;
    double tonalValues = 256;
    double similarity = 0;
    
    int HIST_SAME = 1;
    int HIST_MARGIN = 2;
    int HIST_H6 = 3;
    int HIST_H7 = 4;
    
    /**
     * 
     */
    public FeatureComparator(){
        
    }
    
    /**
     * 
     * @param compResult Array with data for comparision
     * @param results Array with data
     * @param eval Evaluation method: 1 for exact match, 2 for margin, 3 for H6 and 4 for H7
     * @return
     */
    public double compareHistogram(double[] compResult, double[] results, int eval){
        
        normalizedCompResult = compResult;
        normalizedResult = results;   
        similarity = 0;
        
        if (eval == HIST_SAME){
        // 100% fit
        boolean same = true;
        for(int i=0; i<compResult.length; i++){
            if(normalizedCompResult[i] != normalizedResult[i]){
                same = false;
            }
            else{}
        }
        if(same){
            similarity = 100;
        }
        else{
            similarity = 0;
        }
        }
        
        else if (eval==HIST_MARGIN){
        // margin for each value
        double margin = 10;

        similarity = 0;
        for(int i=0; i<normalizedCompResult.length; i++){
            //System.out.println("Value: " + normalizedResult[i] + " Compare: " + normalizedCompResult[i] + " Difference: " + Math.abs(normalizedCompResult[i]-normalizedResult[i]) + " Lower Threshold: " + (normalizedResult[i]-(margin*(tonalValues/100))) + " Upper Threshold: " + (normalizedResult[i]+(margin*(tonalValues/100))));
            if(normalizedCompResult[i] == normalizedResult[i]){
                similarity += 100.0/compResult.length;
                //System.out.println(" + Identical");
            }
            else if(normalizedCompResult[i] >= normalizedResult[i]-(margin*(tonalValues/100)) && normalizedCompResult[i] <= normalizedResult[i]+(margin*(tonalValues/100))){
                double maxMargin = tonalValues*(margin/100.0);
                similarity += (100.0/normalizedCompResult.length - (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin) * 100.0 / normalizedCompResult.length);
                //System.out.println(" - Max Increment: " + 100.0/normalizedCompResult.length + " Abs/maxMargin: " + (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin) + " Abs/maxMargin*Increment: " + (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin)*100.0/normalizedCompResult.length + " Incremented by: " + (100.0/normalizedCompResult.length - (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin)*100.0/normalizedCompResult.length));
            }
            else{
                 similarity += 0;
            }
        }
        }
        
        else if (eval == HIST_H6){
        //minmax
        // H6 from Using Similarity Measures for Histogram Comparison
        similarity = 0;
        for(int i=0; i<normalizedCompResult.length; i++){
            double a,b,ab;
            if(normalizedResult[i] == normalizedCompResult[i]){
                similarity += 100.0 / normalizedCompResult.length;
            }
            else{ if(normalizedResult[i] > normalizedCompResult[i]){
                a = normalizedCompResult[i];
                b = normalizedResult[i];
            }
            else {
                a = normalizedResult[i];
                b = normalizedCompResult[i];
            }
            if(b == 0.0){b = 1;}
            ab = a/b;
            similarity += 100.0 / normalizedCompResult.length * ab;}
        }
        }
        
        else if (eval == HIST_H7){
        // H7 from Using Similarity Measures for Histogram Comparison
        similarity = 0;
        for(int i=0; i<normalizedCompResult.length; i++){
            double nr,ncr,a,b,ab,cd,abcd;
            if(normalizedResult[i] == normalizedCompResult[i]){
                similarity += 100.0 / normalizedCompResult.length;
            }
            else{ 
                if(normalizedResult[i] == 0)nr = 1;
                else nr = normalizedResult[i];
                if(normalizedCompResult[i] == 0)ncr = 1;
                else ncr = normalizedCompResult[i];

                a = nr/ncr;
                b = ncr/nr;

                if(a > b){
                ab = Math.abs(b/a);
                cd = Math.abs(a/b);
            }
            else {
                ab = Math.abs(a/b);
                cd = Math.abs(b/a);
            }
            if(cd == 0.0){cd = 1;}
            abcd = ab/cd;
            similarity += 100.0 / normalizedCompResult.length * abcd;}
        }
        }
        
        else{
        //TODO margin to left and right
        //Throw some exception
        }        
        
        return similarity;
    }
    
    public double compareSurf(List<double[]> compIpts, List<double[]> resultIpts){
        similarity = 0;
        
        List<InterestPoint> ipts1 = new ArrayList<>();
        List<InterestPoint> ipts2 = new ArrayList<>();
        for(int i = 0; i<compIpts.size(); i++){
            ipts1.add(de.lmu.dbs.jfeaturelib.features.SURF.doubleArrayToInterestPoint(compIpts.get(i)));
        }
        for(int i = 0; i<resultIpts.size(); i++){
            ipts2.add(de.lmu.dbs.jfeaturelib.features.SURF.doubleArrayToInterestPoint(resultIpts.get(i)));
        }
             
        Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMathes(ipts1, ipts2);
        
        if(matchedPoints.isEmpty()){
            return 0.0;
        }
        else{
            //only comparing how many points from the orignal image were found
            similarity = 100.0*(double)matchedPoints.size()/(double)ipts1.size();
        }
                
        return similarity;
    }

}
