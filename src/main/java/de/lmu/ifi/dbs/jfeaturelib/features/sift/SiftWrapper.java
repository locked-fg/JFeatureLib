/*
 * This file is part of the JFeatureLib project: http://jfeaturelib.googlecode.com
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://code.google.com/p/jfeaturelib/wiki/Citation
 */
package de.lmu.ifi.dbs.jfeaturelib.features.sift;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Code that Wraps the Sift Executable.
 *
 * <code>
 * SiftWrapper t = new SiftWrapper(new File("c:/temp/siftWin32.exe"));
 * List<double[]> features = t.getFeatures(new File("c:\\temp\\ant-sample.pgm"));
 * </code>
 *
 * An image passed to this class is first saved as a .pgm in the systems temp
 * directory. This file is passed to the sift binary and deleted afterwards.
 *
 * @author Franz Graf
 */
public class SiftWrapper {

    private static final int NUMBER_OF_GRADIENTS = 128;
    private static final String PREFIX = "JFeatureLib-SiftWrapper";
    private static final String SUFFIX = ".pgm";
    private static final Logger log = Logger.getLogger(SiftWrapper.class.getName());
    /**
     * process starting the executable
     */
    private final ProcessBuilder processBuilder;

    /**
     * creates the sift wrapper
     *
     * @param siftBinary the SIFT executable
     * @throws IOException if exec is not an executable
     */
    public SiftWrapper(File siftBinary) throws IOException {
        if (!siftBinary.canExecute()) {
            throw new IOException("Sift Binary not executable: " + siftBinary);
        }
        processBuilder = new ProcessBuilder(new String[]{siftBinary.getAbsolutePath()});
        // Todo redirect error stream to logger?
        processBuilder.redirectErrorStream(false);
    }

    public List<double[]> getFeatures(ImageProcessor ip) throws IOException,
            InterruptedException {
        File tmpFile = File.createTempFile(PREFIX, SUFFIX);
        List<double[]> features = Collections.EMPTY_LIST;
        try {
            tmpFile.deleteOnExit();
            ImagePlus iPlus = new ImagePlus(tmpFile.getAbsolutePath());
            iPlus.setProcessor("", ip);
            new FileSaver(iPlus).saveAsPgm();
            features = getFeatures(tmpFile);
        } finally {
            tmpFile.delete();
        }
        return features;
    }

    /**
     * Creates and returnes Sift-Features from the given file. The file MUST be
     * a PGM image.
     *
     * @param f PGM image
     * @return features
     * @throws IOException
     * @throws InterruptedException
     */
    public List<double[]> getFeatures(File f) throws IOException,
            InterruptedException {
        // primitive PGM check
        if (!f.getName().toLowerCase().endsWith(SUFFIX)) {
            log.warn("File does not have a .pgm extension. Sure it is a PGM file? " + f.getAbsolutePath());
        }
        return convertToArray(getFeatures(new FileInputStream(f)));
    }

    /**
     * Converts a list of Sift Feature Vectors into a list of plain arrays
     */
    private List<double[]> convertToArray(List<SiftFeatureVector> sifts) {
        List<double[]> arrays = new ArrayList<>(sifts.size());
        for (SiftFeatureVector vector : sifts) {
            arrays.add(vector.asArray());
        }
        return arrays;
    }

    /**
     * Extracts the features from the text input stream which is created by the
     * sift binary.
     *
     * @param textInput
     * @return list of feature vectors
     * @throws InterruptedException
     * @throws IOException
     */
    private List<SiftFeatureVector> getFeatures(InputStream textInput)
            throws InterruptedException, IOException {
        Process process = processBuilder.start();
        dataToProcess(process.getOutputStream(), textInput);

        List<SiftFeatureVector> v = extractFeatures(new InputStreamReader(
                process.getInputStream()));

        // let the process finish and close all streams
        process.getErrorStream().close();
        process.getInputStream().close();
        process.getOutputStream().close();
        process.waitFor();

        return v;
    }

    /**
     * Extract features from the streamreader. Siftfeatures all have their
     * primary key = 0 and class = -1
     *
     * @param isr streamreader from which the input is read
     * @return array of {@link SiftFeatureVector}s (may be empty)
     * @throws IOException
     */
    List<SiftFeatureVector> extractFeatures(InputStreamReader isr)
            throws IOException {
        List<SiftFeatureVector> vectors = new ArrayList<>(100);

        BufferedReader inR = new BufferedReader(isr);
        String inLine = null;
        SiftFeatureVector currentVector = null;

        // parse stream
        int linecount = 0;
        ArrayList<Double> dataList = new ArrayList<>(NUMBER_OF_GRADIENTS);

        while (null != (inLine = inR.readLine())) {
            linecount++;

            if (log.isTraceEnabled()) {
                log.trace(inLine);
            }
            // line 1 is just a comment line
            if (linecount == 1) {
                continue;
            }

            // Each NEW vector begins with a non-space-char.
            // Vector data then follows with a space at the beginning of
            // each line
            if (inLine.charAt(0) != ' ') {
                // create new vector and add header info
                String[] split = inLine.split(" ");
                double y = new Double(split[0]);
                double x = new Double(split[1]);
                double scale = new Double(split[2]);
                double rotation = new Double(split[3]);
                currentVector = new SiftFeatureVector(x, y, scale, rotation);
            } else {
                String[] split = inLine.trim().split(" ");
                // check size of resulting vector
                if (dataList.size() + split.length > NUMBER_OF_GRADIENTS) {
                    throw new IllegalArgumentException("adding too much elements to the vector");
                }

                for (String part : split) {
                    dataList.add(Double.parseDouble(part));
                }
                // 128 data elements found. The vector is completed.
                // convert array to list and add it to the vector
                if (dataList.size() == NUMBER_OF_GRADIENTS) {
                    double[] dataArr = new double[NUMBER_OF_GRADIENTS];
                    for (int i = 0; i < dataList.size(); i++) {
                        dataArr[i] = dataList.get(i);
                    }
                    currentVector.setGradients(dataArr);
                    vectors.add(currentVector);

                    // reset variables
                    currentVector = null;
                    dataList.clear();
                }
            }
        }// while

        return vectors;
    }

    /**
     * writes the data to the process in order to analyze this data
     *
     * @param os destination
     * @param in source
     * @throws IOException
     */
    private void dataToProcess(OutputStream os, InputStream in)
            throws IOException {
        BufferedInputStream inS;
        try (BufferedOutputStream outS = new BufferedOutputStream(os)) {
            inS = new BufferedInputStream(in);
            int data;
            while ((data = inS.read()) != -1) {
                outS.write(data);
            }
        }
        inS.close();
    }
}
