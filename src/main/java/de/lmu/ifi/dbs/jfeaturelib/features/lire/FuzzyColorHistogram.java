/*
 * FuzzyColorHistogram.java
 *
 * Ported from C#, performance is probably poor
 *
 * FuzzyColorHistogram.cs
 * Part of the Callisto framework
 * (c) 2009 Arthur Pitman. All rights reserved.
 *
 */

package de.lmu.ifi.dbs.jfeaturelib.features.lire;

import at.lux.imageanalysis.VisualDescriptor;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.StringTokenizer;
import net.semanticmetadata.lire.imageanalysis.LireFeature;

public class FuzzyColorHistogram implements LireFeature {

    protected Color[] binColors;
    protected final int SIZE = 5;
    protected final int SIZE3 = SIZE * SIZE * SIZE;

    protected double[] descriptorValues;


    public void extract(BufferedImage bimg) {
        binColors = new Color[SIZE3];

        int counter = 0;
        for (int k = 0; k < SIZE; k++) {
            for (int j = 0; j < SIZE; j++) {
                for (int i = 0; i < SIZE; i++) {
                    binColors[counter] = getColorForBin(i, j, k);
                    counter++;
                }
            }
        }

        double[] histogramA = new double[SIZE3];

        int width = bimg.getWidth();
        int height = bimg.getHeight();

        WritableRaster raster = bimg.getRaster();
        int[] pixel = new int[3];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                raster.getPixel(i, j, pixel);
                int r = pixel[0];
                int g = pixel[1];
                int b = pixel[2];


                for (int k = 0; k < SIZE3; k++) {
                    double rDiff = (double) (binColors[k].getRed()) - r;
                    double gDiff = (double) (binColors[k].getGreen()) - g;
                    double bDiff = (double) (binColors[k].getBlue()) - b;
                    double rdist = rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;
                    histogramA[k] += (10.0 / Math.sqrt(rdist + 1));
                }
            }
        }

        double maxA = 0;
        for (int k = 0; k < SIZE3; k++) {
            if (histogramA[k] > maxA)
                maxA = histogramA[k];
        }

        descriptorValues = new double[SIZE3];
        for (int k = 0; k < SIZE3; k++)
            descriptorValues[k] = histogramA[k] / maxA * 255;
    }


    public float getDistance(VisualDescriptor vd) {
        if (!(vd instanceof FuzzyColorHistogram))
            throw new UnsupportedOperationException("Wrong descriptor.");
        FuzzyColorHistogram target = (FuzzyColorHistogram) vd;
        double distance = 0;
        for (int i = 0; i < SIZE3; i++)
            distance += ((descriptorValues[i] - target.descriptorValues[i]) * (descriptorValues[i] - target.descriptorValues[i]));

        return (float) Math.sqrt(distance / SIZE3);
    }

    public String getStringRepresentation() { // added by mlux
        StringBuilder sb = new StringBuilder(descriptorValues.length * 2 + 25);
        sb.append("fuzzycolorhist");
        sb.append(' ');
        sb.append(descriptorValues.length);
        sb.append(' ');
        for (double aData : descriptorValues) {
            sb.append((int) aData);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public void setStringRepresentation(String s) { // added by mlux
        StringTokenizer st = new StringTokenizer(s);
        if (!st.nextToken().equals("fuzzycolorhist"))
            throw new UnsupportedOperationException("This is not a fuzzycolorhist descriptor.");
        descriptorValues = new double[Integer.parseInt(st.nextToken())];
        for (int i = 0; i < descriptorValues.length; i++) {
            if (!st.hasMoreTokens())
                throw new IndexOutOfBoundsException("Too few numbers in string representation.");
            descriptorValues[i] = (int) Integer.parseInt(st.nextToken());
        }

    }


    protected Color getColorForBin(int rBin, int gBin, int bBin) {
        // work out the width of each bin and place the color in the middle
        int binWidth = 256 / SIZE;
        int offset = binWidth / 2;

        return new Color(rBin * binWidth + offset, gBin * binWidth + offset, bBin * binWidth + offset);
    }
    
    public double[] getData(){
        return descriptorValues;
    }

}
