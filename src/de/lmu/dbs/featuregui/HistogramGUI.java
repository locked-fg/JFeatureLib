/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lmu.dbs.featuregui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.lang.Math;

/**
 *
 * @author Benedikt
 */
public class HistogramGUI extends javax.swing.JPanel {
    
    int[] histogram;
    int channels;
    int divide;
    
    public HistogramGUI(int[] hist, int max, int numchan)
    {
        histogram = hist;
        channels = numchan;
        divide = max/256;
        setPreferredSize(new Dimension(256, 256));
        setBackground(Color.white);
        //initComponents();
    }
    
    @Override
     public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if(channels == 1){draw1Chan(g2);}
        else if(channels == 3){draw3Chan(g2);}
        else if(channels == 4){draw4Chan(g2);}
        else{}
        
     }
     
    private void draw1Chan(Graphics2D g2){
        g2.setColor(Color.black);
        for(int i = 0; i < histogram.length; i++){
            Line2D lin = new Line2D.Float(i, histogram.length, i, histogram.length-histogram[i]/divide);
            g2.draw(lin);
            
        }
    }
    
    private void draw3Chan(Graphics2D g2){
        
    }
     
    private void draw4Chan(Graphics2D g2){
        draw1Chan(g2);
        draw3Chan(g2);
    }
    

}
