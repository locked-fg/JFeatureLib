package de.lmu.dbs.jfeaturelib;

import de.lmu.dbs.jfeaturelib.compare.CompareResult;
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
    double distance = 0.0;
    double similarity = 0.0;
    
    
    int HIST_SAME = 1;
    int HIST_MARGIN = 2;
    int HIST_H6 = 3;
    int HIST_H7 = 4;
    int HIST_DIST = 5;
    
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
    
    //linear similarity/distance is used, maybe include exponential options?
    
    public CompareResult compareHistogram(double[] compResult, double[] results, int eval){
        
        normalizedCompResult = compResult;
        normalizedResult = results;   
        distance = 0.0;
        similarity = 0.0;
        
//      FIXME all commented comparisons do not include distance measurement at the moment          
        
//        if (eval == HIST_SAME){
//        // 100% fit
//        boolean same = true;
//        for(int i=0; i<compResult.length; i++){
//            if(normalizedCompResult[i] != normalizedResult[i]){
//                same = false;
//            }
//            else{}
//        }
//        if(same){
//            similarity = 100;
//        }
//        else{
//            similarity = 0;
//        }
//        }
//        
//        else if (eval==HIST_MARGIN){
//        // margin for each value
//        double margin = 10;
//
//        similarity = 0;
//        for(int i=0; i<normalizedCompResult.length; i++){
//            //System.out.println("Value: " + normalizedResult[i] + " Compare: " + normalizedCompResult[i] + " Difference: " + Math.abs(normalizedCompResult[i]-normalizedResult[i]) + " Lower Threshold: " + (normalizedResult[i]-(margin*(tonalValues/100))) + " Upper Threshold: " + (normalizedResult[i]+(margin*(tonalValues/100))));
//            if(normalizedCompResult[i] == normalizedResult[i]){
//                similarity += 100.0/compResult.length;
//                //System.out.println(" + Identical");
//            }
//            else if(normalizedCompResult[i] >= normalizedResult[i]-(margin*(tonalValues/100)) && normalizedCompResult[i] <= normalizedResult[i]+(margin*(tonalValues/100))){
//                double maxMargin = tonalValues*(margin/100.0);
//                similarity += (100.0/normalizedCompResult.length - (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin) * 100.0 / normalizedCompResult.length);
//                //System.out.println(" - Max Increment: " + 100.0/normalizedCompResult.length + " Abs/maxMargin: " + (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin) + " Abs/maxMargin*Increment: " + (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin)*100.0/normalizedCompResult.length + " Incremented by: " + (100.0/normalizedCompResult.length - (Math.abs(normalizedCompResult[i]-normalizedResult[i])/maxMargin)*100.0/normalizedCompResult.length));
//            }
//            else{
//                 similarity += 0;
//            }
//        }
//        }
//        
//        else if (eval == HIST_H6){
//        //minmax
//        // H6 from Using Similarity Measures for Histogram Comparison
//        similarity = 0;
//        for(int i=0; i<normalizedCompResult.length; i++){
//            double a,b,ab;
//            if(normalizedResult[i] == normalizedCompResult[i]){
//                similarity += 100.0 / normalizedCompResult.length;
//            }
//            else{ if(normalizedResult[i] > normalizedCompResult[i]){
//                a = normalizedCompResult[i];
//                b = normalizedResult[i];
//            }
//            else {
//                a = normalizedResult[i];
//                b = normalizedCompResult[i];
//            }
//            if(b == 0.0){b = 1;}
//            ab = a/b;
//            similarity += 100.0 / normalizedCompResult.length * ab;}
//        }
//        }
//        
//        else if (eval == HIST_H7){
//        // H7 from Using Similarity Measures for Histogram Comparison
//        similarity = 0;
//        for(int i=0; i<normalizedCompResult.length; i++){
//            double nr,ncr,a,b,ab,cd,abcd;
//            if(normalizedResult[i] == normalizedCompResult[i]){
//                similarity += 100.0 / normalizedCompResult.length;
//            }
//            else{ 
//                if(normalizedResult[i] == 0)nr = 1;
//                else nr = normalizedResult[i];
//                if(normalizedCompResult[i] == 0)ncr = 1;
//                else ncr = normalizedCompResult[i];
//
//                a = nr/ncr;
//                b = ncr/nr;
//
//                if(a > b){
//                ab = Math.abs(b/a);
//                cd = Math.abs(a/b);
//            }
//            else {
//                ab = Math.abs(a/b);
//                cd = Math.abs(b/a);
//            }
//            if(cd == 0.0){cd = 1;}
//            abcd = ab/cd;
//            similarity += 100.0 / normalizedCompResult.length * abcd;}
//            }
//        }
//        else if (eval == HIST_DIST){   
//        distance = 0.0;
//        similarity = 0.0;
                
            for(int i = 0; i<normalizedResult.length; i++){
                distance += ((normalizedResult[i] - normalizedCompResult[i]) * (normalizedResult[i] - normalizedCompResult[i]));
            }
            distance =  Math.sqrt(distance / normalizedResult.length);
            similarity = 100*(1.0-(distance/100.0));
            return new CompareResult(distance, similarity);
//        }
//        else{
//        //TODO margin to left and right
//        //Throw some exception
//        }        
//        
//        return similarity;
    }
    
    public CompareResult compareSurf(List<double[]> compIpts, List<double[]> resultIpts){
        distance = 0.0;
        similarity = 0.0;
        
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
            return new CompareResult(100.0, 0.0);
        }
        else{
            //only comparing how many points from the orignal image were found
            similarity = 100.0*(double)matchedPoints.size()/(double)ipts1.size();
            distance = 100.0 -  similarity;
        }
        
        distance = ipts1.size()-matchedPoints.size();
        return new CompareResult(distance, similarity);
    }
    
    
    
    //@FIXME Lire comparation not working as intended, max. distance is just guessed
    public CompareResult compareLireCeddFCTH(double[] compResult, double[] results){        
        distance = 0.0;
        similarity = 0.0;
        
        double[] data2 = compResult;
        double[] data1 = results;
        
        // Taken from net.semanticmetadata.lire.imageanalysis.CEDD
        double Temp1 = 0;
        double Temp2 = 0;

        double TempCount1 = 0, TempCount2 = 0, TempCount3 = 0;

        for (int i = 0; i < data2.length; i++) {
            Temp1 += data2[i];
            Temp2 += data1[i];
        }

        if (Temp1 == 0 || Temp2 == 0) similarity = 100;
        if (Temp1 == 0 && Temp2 == 0) similarity = 0;

        if (Temp1 > 0 && Temp2 > 0) {
            for (int i = 0; i < data2.length; i++) {
                TempCount1 += (data2[i] / Temp1) * (data1[i] / Temp2);
                TempCount2 += (data1[i] / Temp2) * (data1[i] / Temp2);
                TempCount3 += (data2[i] / Temp1) * (data2[i] / Temp1);

            }

            distance = (100 - 100 * (TempCount1 / (TempCount2 + TempCount3 - TempCount1))); 
        }
        
        similarity = 100*(1.0-(distance/100.0));
        return new CompareResult(distance, similarity);
    }
    
    public CompareResult compareLireFuzzy(double[] compResult, double[] results){
        distance = 0.0;
        similarity = 0.0;
        
        double[] queryFeatureVector = results;
        double[] targetFeatureVector = compResult;     
        int size3 = 5*5*5;
        
        for (int i = 0; i < size3; i++){
            distance += ((queryFeatureVector[i] - targetFeatureVector[i]) * (queryFeatureVector[i] - targetFeatureVector[i]));
        }
        distance =  Math.sqrt(distance / size3);
        
        similarity = 100*(1.0-(distance/100.0));
        return new CompareResult(distance, similarity);
    } 
    
    public CompareResult compareLireGabor(double[] compResult, double[] results, int M, int N){        
        distance = 0.0;
        similarity = 0.0;
        
        double[] queryFeatureVector = compResult;
        double[] targetFeatureVector = results;
        for (int m = 0; m < M; m++) {
            for (int n = 0; n < N; n++) {
                distance += Math.sqrt(Math.pow(queryFeatureVector[m * N + n * 2] - targetFeatureVector[m * N + n * 2], 2) + Math.pow(queryFeatureVector[m * N + n * 2 + 1] - targetFeatureVector[m * N + n * 2 + 1], 2));
            }
        }
        
        similarity = 100*(1.0-(distance/500.0));
        return new CompareResult(distance, similarity);
    }    
            
    public CompareResult compareLireTamura(double[] compResult, double[] results){  
        distance = 0.0;
        similarity = 0.0;
        
        double[] queryFeature = compResult;
        double[] targetFeature = results;
        for (int i = 2; i < targetFeature.length; i ++) {
            distance += Math.pow(targetFeature[i] - queryFeature[i], 2);
        }
        
        similarity = 100*(1.0-(distance/2000000.0));
        return new CompareResult(distance, similarity);
    }
}
