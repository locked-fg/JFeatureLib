package de.lmu.dbs.jfeaturelib.utils;

import de.lmu.dbs.jfeaturelib.LibProperties;
import de.lmu.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Class used as a commandline tool to extract features from directories of
 * images. The features are then written to a outfile.
 *
 * @author Franz
 */
public class Extractor {

    private static final Logger log = Logger.getLogger(Extractor.class.getName());
    private static final int TERMINATION_TIMEOUT = 100;
    @Option(name = "--threads", usage = "amount of threads (defaults to amount of available processors))")
    private int threads = Runtime.getRuntime().availableProcessors();
    // 
    @Option(name = "-d", aliases = {"--src-dir"}, usage = "directory containing images (default: execution directory)")
    File imageDirectory;
    //
    @Option(name = "-r", usage = "recursively descend into directories (default: no)")
    private boolean recursive = false;
    //
    @Option(name = "-o", aliases = {"--output-dir"}, usage = "output to this file (default: features.csv)")
    private File outFile;
    //
    @Option(name = "-m", aliases = {"--masks-dir"}, usage = "directory containing masks")
    File maskDirectory = null;
    //
    @Option(name = "--append", usage = "append to output file (default: false = overwrite)")
    private boolean append;
    //
    @Option(name = "-nh", usage = "omit headerline")
    private boolean omitHeader = false;
    //
    @Option(name = "-D", aliases = {"--descriptor"}, usage = "use this feature descriptor (e.G: Sift)", required = true)
    private String descriptor = null;
    //
    @Option(name = "-c", usage = "image class that should be written to the output file")
    private String imageClass = null;
    //
    @Option(name = "--help", usage = "show this screen")
    private boolean showHelp = false;
    // other command line parameters than options
    // @Argument
    // private List<String> arguments = new ArrayList<>();
    // 
    // FIXME move to properties file
    private static final int WRITE_BUFFER = 1024 * 1024; // bytes
    private static final String NL = "\n";
    //
    private final LibProperties properties;
    private final String[] imageFormats;
    private String separator = ", ";
    private int lineCounter = 0;
    // file exists and has a length > 0
    private boolean fileExists;
    // the descriptor to use
    private Class descriptorClazz;
    private Writer writer;
    private ExecutorService pool;

    public static void main(String[] args) throws IOException {
        InputStream is = Extractor.class.getResourceAsStream("/META-INF/logging.properties");
        PropertyConfigurator.configure(is);

        Extractor extractor = new Extractor();
        CmdLineParser parser = new CmdLineParser(extractor);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar JFeatureLib.jar de.lmu.dbs.jfeaturelib.utils.Extractor [arguments]");
            parser.printUsage(System.err);
            return;
        }

        if (extractor.showHelp) {
            System.err.println("java -jar JFeatureLib.jar de.lmu.dbs.jfeaturelib.utils.Extractor [arguments]");
            parser.printUsage(System.out);
            System.exit(0);
        }

        extractor.run();
    }

    Extractor() throws IOException {
        properties = LibProperties.get();
        imageFormats = properties.getString(LibProperties.IMAGE_FORMATS).split(" *, *");
        for (int i = 0; i < imageFormats.length; i++) {
            imageFormats[i] = imageFormats[i].trim();
        }
    }

    private void run() {
        validateInput();
        openWriter();

        openPool();
        Collection<File> maskList = createFileList(maskDirectory);
        Collection<File> imageList = createFileList(imageDirectory);
        HashMap<File, File> tuples = findTuples(imageList, maskList);

        processImages(imageList);
        closePool();

        closeWriter();
    }

    private void validateInput() throws IllegalArgumentException {
        try {
            String base = FeatureDescriptor.class.getPackage().getName();
            descriptorClazz = Class.forName(base + "." + descriptor);
            if (!FeatureDescriptor.class.isAssignableFrom(descriptorClazz)) {
                throw new IllegalArgumentException("The class must derive from FeatureDescriptor");
            }
        } catch (ClassNotFoundException ex) {
            log.warn(ex.getMessage(), ex);
            throw new IllegalArgumentException("the descriptor class does not exist or cannot be created");
        }
        if (imageDirectory == null || !imageDirectory.isDirectory() || !imageDirectory.canRead()) {
            throw new IllegalArgumentException("the source directory cannot be read or does not exist");
        }
        if (maskDirectory != null && (!maskDirectory.isDirectory() || !maskDirectory.canRead())) {
            throw new IllegalArgumentException("the mask directory cannot be read or does not exist");
        }
        if (outFile == null || (outFile.exists() && !outFile.canWrite())) {
            throw new IllegalArgumentException("the output file is not valid or not writable");
        }
        try {
            outFile.createNewFile();
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
            throw new IllegalArgumentException("the output file could not be created");
        }
        if (imageClass != null && !imageClass.matches("^\\w$")) {
            throw new IllegalArgumentException("the image class must only contain word characters");
        }

        fileExists = (outFile.exists() && outFile.length() > 0);
    }

    /**
     * creates a list of image files in the specified directory and all
     * subdirectories (if recursive is enabled)
     *
     * @param dir directory to start from
     * @return a list of image files in this directory (possibly empty)
     */
    Collection<File> createFileList(File dir) {
        if (dir == null) {
            log.info("directory is null, returning empty list");
            return Collections.EMPTY_LIST;
        } else {
            return FileUtils.listFiles(dir, imageFormats, recursive);
        }
    }

    /**
     * opens the BufferedWriter which is used to write the output
     */
    private void openWriter() {
        try {
            writer = new BufferedWriter(new FileWriter(outFile, append), WRITE_BUFFER);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
            throw new IllegalStateException("could not open output file for writing");
        }
    }

    /**
     * closes the output writer
     */
    private void closeWriter() {
        try {
            writer.close();
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
            throw new IllegalStateException("could not close output file");
        }
    }

    private void processImages(Collection<File> images) {
        for (File file : images) {
            pool.submit(new Task(file, properties));
        }
    }

    /**
     * creates a new Threadpool for the image extraction tasks
     */
    private void openPool() {
        pool = Executors.newFixedThreadPool(threads);
    }

    /**
     * closes the threadpool and awaits termination
     */
    private void closePool() {
        try {
            pool.shutdown();
            pool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage(), ex);
            throw new IllegalStateException("error while shutting down pool");
        }
    }

    /**
     * Synchronized method to write all features that were extracted from the
     * given file to the output writer.
     *
     * @param file
     * @param features
     * @throws IOException
     */
    private synchronized void writeOutput(File file, List<double[]> features) throws IOException {
        // we are appending to an existing file. so start with a new line
        if (lineCounter == 0 && fileExists) {
            writer.append(NL);
        }

        // write head?
        if (!omitHeader) {
            omitHeader = true;
            if (imageClass != null) {
                writer.append("class" + separator);
            }
            writer.append("filename");
            for (int i = 0; i < features.get(0).length; i++) {
                writer.append(separator + i);
            }
            writer.append(NL);
        }

        // write one line for each feature
        for (double[] feature : features) {
            // a second line is being written. Thus prepend a new line
            if (lineCounter++ > 0) {
                writer.append(NL);
            }
            // prepend image class (if given)
            if (imageClass != null) {
                writer.append(imageClass).append(separator);
            }

            // write file name
            writer.append(file.getName()).append(separator);

            // serialize the feature values
            writer.append(Arrays2.join(feature, separator));
        }
    }

    /**
     * Try to find and map image files and mask files together.
     *
     * Thereby the relative path (starting from imageDirectory and
     * maskDirectory) must be equal. A different file suffix is allowed. Thus,
     * an image [imageDirectory]/classA/car.jpg can have a mask file
     * [maskDirectory]/classA/car.png
     *
     * @param imageList
     * @param maskList
     * @return
     */
    HashMap<File, File> findTuples(Collection<File> imageList, Collection<File> maskList) {
        assert imageDirectory != null : "image Directory must not be null";

        HashMap<File, File> map = new HashMap<>(imageList.size());
        String maskBasePath = maskDirectory == null ? null : maskDirectory.getAbsolutePath();

        String imageBasePath = imageDirectory.getAbsolutePath();
        String imageSuffixes = Arrays2.join(imageFormats, "|");
        String imageSuffixReplacement = "\\.(" + imageSuffixes + ")$";

        for (File imageFile : imageList) {
            File correspondingMask = null;

            if (maskDirectory != null) {
                // get base image path starting from the imageDirectory
                // -> foo/bar/image.jpeg
                String imgPart = imageFile.getAbsolutePath().replace(imageBasePath, "");
                // remove image suffix
                // -> foo/bar/image
                imgPart = imgPart.replaceAll(imageSuffixReplacement, "");

                // search mask file
                for (File maskFile : maskList) {
                    String maskPath = maskFile.getAbsolutePath();
                    // FIXME is this correct?
                    if (maskPath.startsWith(maskBasePath + imgPart)) {
                        correspondingMask = maskFile;
                        break;
                    }
                }

                // associate image with mask
                if (correspondingMask == null) {
                    log.warn("no mask file found for " + imageFile.getAbsolutePath());
                }
            }

            map.put(imageFile, correspondingMask);
        }

        return map;
    }

    private class Task implements Runnable {

        private final File file;
        private final LibProperties properties;

        public Task(File file, LibProperties properties) {
            this.file = file;
            this.properties = properties;
        }

        @Override
        public void run() {
            try {
                log.debug("processing file " + file.getName());
                ImagePlus iplus = new Opener().openImage(file.getAbsolutePath());
                ImageProcessor processor = iplus.getProcessor();
//                ImageProcessor mask = getMask();

                FeatureDescriptor fd = (FeatureDescriptor) descriptorClazz.newInstance();
                fd.setProperties(properties);
                fd.run(processor);
                List<double[]> features = fd.getFeatures();

                writeOutput(file, features);
            } catch (IOException | InstantiationException | IllegalAccessException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
    }
}
