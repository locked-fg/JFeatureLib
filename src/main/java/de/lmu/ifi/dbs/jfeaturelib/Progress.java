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
package de.lmu.ifi.dbs.jfeaturelib;

/**
 * The Progress class can be used in situations where an algorithm should fire 
 * updates about its progress. Valid progress values are in the range of [0,100]. 
 * Subsequent events should have increasing progress values.
 * 
 * @author graf
 */
public class Progress {

    public static final Progress START = new Progress(0, "started");
    public static final Progress END = new Progress(100, "finished");
    /**
     * Describes a progress between 0 and 100. 
     * 0 Indicates the start of a progress, 100 the end.
     */
    private final int progress;
    private final String message;

    /**
     * Initialize the progress with an empty default message.
     * 
     * @param progress in [0,100]
     * @throws IllegalArgumentException if progress is out of bounds
     */
    public Progress(int progress) {
        this(progress, "");
    }

    /**
     * Initialize the progress including a progress message.
     * 
     * @param progress in [0,100]
     * @param message the progress message.
     * @throws IllegalArgumentException if progress is out of bounds
     */
    public Progress(int progress, String message) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress value must be in [0,100] but was " + progress);
        }
        if (message == null) {
            throw new NullPointerException("message must not be null");
        }
        this.progress = progress;
        this.message = message;
    }

    /**
     * @return progress value in [0, 100]
     */
    public int getProgress() {
        return this.progress;
    }

    /**
     * @return progress message (never null)
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return classname of the progress class for use in PropertyChangeEvents
     */
    public static String getName() {
        return Progress.class.getName();
    }
}
