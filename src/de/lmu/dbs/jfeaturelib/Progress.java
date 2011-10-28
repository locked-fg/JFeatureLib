package de.lmu.dbs.jfeaturelib;

/**
 * The Progress class can be used in situations where an algorithm should fire 
 * updates about its progress. Valid progress values ar in the range of [0,100]. 
 * Subsequent events should have increasing progress values.
 * 
 * Also, an optional progress message can be given.
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
