package de.lmu.dbs.jfeaturelib;

/**
 * The Progress class can be used in situations where an algorithm should fire 
 * updates about its progress. Valid progress values ar in the range of [0,100]. 
 * Subsequent events should have increasing progress values.
 * 
 * @author graf
 */
public enum Progress {

    START(0), END(100);
    /**
     * Describes a progress between 0 and 100. 
     * 0 Indicates the start of a progress, 100 the end.
     */
    private final int progress;

    Progress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress value must be in [0,100] but was " + progress);
        }
        this.progress = progress;
    }

    public int getProgress() {
        return this.progress;
    }
}
