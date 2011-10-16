package de.lmu.ifi.dbs.paros.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @TODO support ZIP as well
 * @author graf
 */
public class PluginLoader<T> {

    private final File pluginDir;
    private final URLClassLoader classLoader;
    private final List<File> jars;
    private final Class<T> pluginClass;
    private final List<Entry<Class<T>, File>> map = new ArrayList<Entry<Class<T>, File>>();
    private boolean includeInnerClasses = false;
    private boolean includeInterfaces = false;
    private boolean includeAbstractClasses = false;

    public PluginLoader(File pluginDir, Class<T> pluginClass) throws IOException {
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            throw new IOException("given file does not exist or is not a dir: " + pluginDir.getAbsolutePath());
        }
        this.pluginDir = pluginDir;
        this.pluginClass = pluginClass;

        // scan dir for JARs
        jars = scanForJars();
        classLoader = new URLClassLoader(files2Urls(jars));

        // scan each URL for plugins and put them in the map
        for (File jar : jars) {
            for (Class<T> clazz : scanForPlugins(jar)) {
                map.add(new SimpleEntry(clazz, jar));
            }
        }
    }

    private List<Class<T>> scanForPlugins(File f) throws
            IOException {
        List<Class<T>> list = new ArrayList<Class<T>>();
        ZipFile jar = new ZipFile(f);
        for (Enumeration entries = jar.entries(); entries.hasMoreElements();) {
            String binaryName = ((ZipEntry) entries.nextElement()).getName();
            if (!binaryName.endsWith(".class")) { // we only need classes
                continue;
            }
            // make binary name: http://download.oracle.com/javase/6/docs/api/java/lang/ClassLoader.html#name
            binaryName = binaryName.replace(".class", "");
            binaryName = binaryName.replace("/", "."); // directory to package
            if (binaryName.contains("$") && !includeInnerClasses) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.FINE, "Skipped inner class: " + binaryName);
                continue;
            }

            try {
                Class<?> clazz = classLoader.loadClass(binaryName);
                int modifiers = clazz.getModifiers();
                if ((modifiers & Modifier.INTERFACE) != 0 && !includeInterfaces) {
                    continue;
                }
                if ((modifiers & Modifier.ABSTRACT) != 0 && !includeAbstractClasses) {
                    continue;
                }

                if (pluginClass.isAssignableFrom(clazz)) {
                    list.add((Class<T>) clazz);
                }
            } catch (ClassNotFoundException e) {
                // This should never happen
                Logger.getLogger(PluginLoader.class.getName()).log(Level.WARNING, "couldn't find class: " + binaryName, e);
            }
        }
        return list;
    }

    /**
     * scan and add all jars in the pluginDir directory
     *
     * @return
     * @throws MalformedURLException
     */
    private List<File> scanForJars() throws MalformedURLException {
        File[] fileArr = pluginDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".jar");
            }
        });
        return Arrays.asList(fileArr);
    }

    private URL[] files2Urls(List<File> jarFiles) throws MalformedURLException {
        URL[] urls = new URL[jarFiles.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = jarFiles.get(i).toURI().toURL();
        }
        return urls;
    }

    /**
     * 
     * @return List of all found algorithmic jar/zips
     */
    public List<Entry<Class<T>, File>> getMap() {
        return map;
    }
//    public static void main(String[] args) throws Exception {
//        File pluginDir = new File("C://Dokumente und Einstellungen//graf//workspace//Plugins//dist//");
//        PluginLoader<Plugin> loader = new PluginLoader<Plugin>(pluginDir, Plugin.class);
//        for (Class<Plugin> key : loader.map.keySet()) {
//            System.out.println(key.getClass().getName() + " > " + loader.map.get(key) + ": ");
//            key.newInstance().foo();
//        }
//    }
}
