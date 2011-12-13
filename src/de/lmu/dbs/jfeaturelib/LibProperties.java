package de.lmu.dbs.jfeaturelib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * The property wrapper class for <code>jfeaturelib.properties</code>.
 * This class is used to access the properties of jfeaturelib.properties in an 
 * easier way without having to request keys by magic strings.
 * 
 * For example in order to obtain the value of <code>sift.binary</code>, you 
 * can request <code>LibProperties.SIFT_BINARY.getValue()</code>.
 * By doing so, you implicitly load the properties file and perform a call to
 * {@link Properties#getProperty(java.lang.String)} with the magic string being 
 * replaced by the enum.
 * 
 * @author graf
 * @see #getValue() 
 * @see Properties
 */
public enum LibProperties {

    /**
     * Key identifier for the  sift binary which can be obtained from the SIFT 
     * homepage.
     */
    SIFT_BINARY("sift.binary");
    // fields 
    /**
     * The file name of the properties file
     */
    private static final String BASE_FILE = "jfeaturelib.properties";
    /**
     * the key identifier for each property
     */
    private final String key;

    LibProperties(String key) {
        this.key = key;
    }

    /**
     * @return the object from the property identified by this enum
     * @throws IOException 
     */
    public Object getValue() throws IOException {
        return getProperties().getProperty(this.key);
    }

    // Should this be replaces by a singleton? 
    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader(BASE_FILE)));
        return properties;
    }

    public String toString() {
        return "LibProperties{" + "key=" + key + '}';
    }
};
