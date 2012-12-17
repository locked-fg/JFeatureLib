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
package de.lmu.ifi.dbs.jfeaturelib.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;

/**
 * The PackageScanner class is used to find classes deriving a certain class
 * within a package.
 *
 * THis can be useful for example if you need all Classes extending the
 * FeatureDescriptor interface within a certain package.
 *
 * @author Franz
 */
public class PackageScanner<T> {

    private static final Logger log = Logger.getLogger(PackageScanner.class.getName());
    private boolean includeInnerClasses = false;
    private boolean includeInterfaces = false;
    private boolean includeAbstractClasses = false;

    List<String> getNames(Package inPackage) throws UnsupportedEncodingException, URISyntaxException,
            ZipException, IOException {
        List<String> binaryNames = new ArrayList<>();

        String packagePath = inPackage.getName();
        packagePath = packagePath.replace('.', '/');

        // During tests, this points to the classes/ directory, later, this points to the jar
        CodeSource src = PackageScanner.class.getProtectionDomain().getCodeSource();
        URL location = src.getLocation();
        // test case
        File dirOrJar = new File(location.toURI());
        if (dirOrJar.isDirectory()) {
            // +1 to include the slash after the directory name
            int basePathLength = dirOrJar.toString().length() + 1;
            File packageDir = new File(dirOrJar, packagePath);

            // list all .class files in this package directory
            for (File file : packageDir.listFiles((FilenameFilter) new SuffixFileFilter(".class"))) {
                // strip the leading directory
                String binaryName = file.getPath().substring(basePathLength);
                binaryName = pathToBinary(binaryName);
                binaryNames.add(binaryName);
            }

        } else {
            ZipFile jar = new ZipFile(dirOrJar);
            for (Enumeration entries = jar.entries(); entries.hasMoreElements();) {
                String binaryName = ((ZipEntry) entries.nextElement()).getName();
                if (!binaryName.endsWith(".class")) { // we only need classes
                    continue;
                }
                if (binaryName.startsWith(packagePath)) {
                    binaryName = pathToBinary(binaryName);
                    binaryNames.add(binaryName);
                }
            }
        }

        return binaryNames;
    }

    /**
     * Convert a path to a binary name by replacing (back) slashes to periods
     * and removing '.class'
     *
     * @param path
     * @return binary name
     */
    private String pathToBinary(String path) {
        // directory to package
        path = path.replace("\\", ".");
        path = path.replace("/", ".");
        path = path.replace(".class", "");
        return path;
    }

    public List<Class<T>> scanForClass(Package thePackage, Class<T> needle) throws IOException,
            UnsupportedEncodingException, URISyntaxException {
        List<String> binaryNames = getNames(thePackage);
        List<Class<T>> list = new ArrayList<>();

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        for (String binaryName : binaryNames) {
            if (binaryName.contains("$") && !includeInnerClasses) {
                log.debug("Skipped inner class: " + binaryName);
                continue;
            }

            try {
                Class<?> clazz = loader.loadClass(binaryName);
                int modifiers = clazz.getModifiers();
                if ((modifiers & Modifier.INTERFACE) != 0 && !includeInterfaces) {
                    log.debug("Skipped interface: " + binaryName);
                    continue;
                }
                if ((modifiers & Modifier.ABSTRACT) != 0 && !includeAbstractClasses) {
                    log.debug("Skipped abstract class: " + binaryName);
                    continue;
                }
                if (needle.isAssignableFrom(clazz)) {
                    log.debug("added class/interface/..: " + binaryName);
                    list.add((Class<T>) clazz);
                }
            } catch (ClassNotFoundException e) {
                // This should never happen
                log.warn("couldn't find class (?!): " + binaryName, e);
            }
        }
        return list;
    }

    //<editor-fold defaultstate="collapsed" desc="getter & setter">
    public boolean isIncludeInnerClasses() {
        return includeInnerClasses;
    }

    public void setIncludeInnerClasses(boolean includeInnerClasses) {
        this.includeInnerClasses = includeInnerClasses;
    }

    public boolean isIncludeInterfaces() {
        return includeInterfaces;
    }

    public void setIncludeInterfaces(boolean includeInterfaces) {
        this.includeInterfaces = includeInterfaces;
    }

    public boolean isIncludeAbstractClasses() {
        return includeAbstractClasses;
    }

    public void setIncludeAbstractClasses(boolean includeAbstractClasses) {
        this.includeAbstractClasses = includeAbstractClasses;
    }
    //</editor-fold>
}
