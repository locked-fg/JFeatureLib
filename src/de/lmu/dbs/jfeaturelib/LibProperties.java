package de.lmu.dbs.jfeaturelib;

import de.lmu.ifi.dbs.utilities.PropertyContainer;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * The property wrapper class for
 * <code>jfeaturelib.properties</code>. This class is used to access the
 * properties of jfeaturelib.properties in an easier way without having to
 * request keys by magic strings.
 *
 * For example in order to obtain the value of
 * <code>sift.binary</code>, you can request
 * <code>LibProperties.SIFT_BINARY.getValue()</code>. By doing so, you
 * implicitly load the properties file and perform a call to
 * {@link Properties#getProperty(java.lang.String)} with the magic string being
 * replaced by the enum.
 *
 * @author graf
 * @see #getValue()
 * @see Properties
 */
public class LibProperties extends PropertyContainer {

    private static LibProperties singleton;
    /**
     * The file name of the properties file
     */
    private static final File BASE_FILE = new File("jfeaturelib.properties");
    /**
     * path to the sift binary which can be obtained from the SIFT homepage.
     */
    public static final String SIFT_BINARY = "sift.binary";

    public LibProperties() throws IOException {
        super(BASE_FILE);
    }

    public static LibProperties get() throws IOException {
        if (singleton == null) {
            singleton = new LibProperties();
        }
        return singleton;
    }
}