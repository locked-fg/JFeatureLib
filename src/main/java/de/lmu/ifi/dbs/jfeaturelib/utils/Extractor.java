package de.lmu.ifi.dbs.jfeaturelib.utils;

import de.lmu.ifi.dbs.jfeaturelib.Descriptor.Supports;
import de.lmu.ifi.dbs.jfeaturelib.LibProperties;
import de.lmu.ifi.dbs.jfeaturelib.features.FeatureDescriptor;
import de.lmu.ifi.dbs.utilities.Arrays2;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

//import static com.google.common.base.Preconditions.*;
/**
 * Class used as a commandline tool to extract features from directories of
 * images.
 *
 * The features are then written to a outfile.
 *
 * @author Franz
 */
public class Extractor {

    private static final Logger log = Logger.getLogger(Extractor.class.getName());
    /**
     * Timeout used for the threadpool. Just set it to a large enough value so
     * that all threads will terminate.
     */
    private static final int TERMINATION_TIMEOUT = 100;
    //
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
    private File outFile = new File("features.csv");
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
    @Option(name = "-D", aliases = {"--descriptor"}, usage = "use this feature descriptor (e.G: Sift)")
    private String descriptor = null;
    //
    @Option(name = "-c", usage = "image class that should be written to the output file")
    private String imageClass = null;
    //
    @Option(name = "--list-capabilities", usage = "list the registered FeatureDescriptors and the output of their supports() method")
    private boolean listCapabilities = false;
    //
    @Option(name = "--help", usage = "show this screen")
    private boolean showHelp = false;
    //
    @Option(name = "-v", usage = "show JFeatureLib debug messages")
    private boolean debugJFeatureLib = false;
    //
    @Option(name = "--unpack-properties", usage = "extracts the default properties and loggiing properties into the current directory")
    private boolean unpackProperties = false;
    // other command line parameters than options
    // @Argument
    // private List<String> arguments = new ArrayList<>();
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

    public static void main(String[] args) throws Exception {
        try {
            initLoggingProperties();

            Extractor extractor = new Extractor();
            CmdLineParser parser = new CmdLineParser(extractor);

            try {
                parser.parseArgument(args);
            } catch (CmdLineException e) {
                log.warn(e);
                System.err.println("java -cp JFeatureLib.jar " + Extractor.class.getCanonicalName() + " [arguments]");
                parser.printUsage(System.err);
                System.exit(1);
            }

            // maybe adjust log level according to CL value
            if (extractor.debugJFeatureLib) {
                Logger.getLogger("de.lmu.ifi.dbs.jfeaturelib").setLevel(Level.DEBUG);
            }

            // process commands that should not start execution
            if (extractor.showHelp) {
                parser.printUsage(System.out);
                System.exit(0);

            } else if (extractor.listCapabilities) {
                extractor.listFeatureDescriptorCapabilities();
                System.exit(0);

            } else if (extractor.unpackProperties) {
                extractor.unpackProperties();
                System.exit(0);

            } else {
                // okay everything is fine, validate input
                try {
                    extractor.validateInput();
                } catch (Throwable t) {
                    log.debug("input validation failed.", t);
                    parser.printUsage(System.err);
                    System.err.println(t.getMessage());
                    System.exit(1);
                }

                // and finally, if validation is fine, start
                extractor.process();
            }
        } catch (Throwable t) {
            log.warn("Uncaught Exception: ", t);
            throw t;
        }
    }

    /**
     * Initialize the logging properties.
     *
     * If a ./logging.properties is found that this file will be used.
     * Otherwise, the configuration from /logging.properties will be read.
     */
    private static void initLoggingProperties() {
        if (new File("./log4j.properties").exists()) {
            log.debug("read logging configuration from file");
            PropertyConfigurator.configure("./log4j.properties");
        }
    }

    /**
     * reads the shipped properties file and copies it into the current
     * execution directory
     */
    private void unpackProperties() {
        try {
            try (InputStream is = LibProperties.class.getResourceAsStream("/" + LibProperties.BASE_FILE.getName());
                    FileChannel dst = new FileOutputStream(LibProperties.BASE_FILE).getChannel()) {
                dst.transferFrom(Channels.newChannel(is), 0, Integer.MAX_VALUE);
                log.info("wrote jfeaturelib.properties");
            }
            try (InputStream is = LibProperties.class.getResourceAsStream("/log4j.properties");
                    FileChannel dst = new FileOutputStream("log4j.properties").getChannel()) {
                dst.transferFrom(Channels.newChannel(is), 0, Integer.MAX_VALUE);
                log.info("wrote log4j.properties");
            }
        } catch (IOException ex) {
            log.debug("the properties could not be extracted.", ex);
            log.warn("The properties could not be extracted. Please see the log for more information.");
        }
    }

    /**
     * Prints the supports capabilities of the feature descriptors and prints
     * the string to system.out.
     */
    private void listFeatureDescriptorCapabilities() throws InstantiationException, IllegalAccessException,
            IOException, URISyntaxException {
        Package fdPackage = FeatureDescriptor.class.getPackage();
        int offset = fdPackage.getName().length() + 1;

        // search for the descriptors
        PackageScanner<FeatureDescriptor> scanner = new PackageScanner<>();
        List<Class<FeatureDescriptor>> classes = scanner.scanForClass(fdPackage, FeatureDescriptor.class);

        // find the longest name to make a nice output
        int maxNameLength = 0;
        for (Class<FeatureDescriptor> fd : classes) {
            maxNameLength = Math.max(fd.getName().length() - offset, maxNameLength);
        }

        // sort the classes by class name
        Collections.sort(classes, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // now we know the longest descriptor name - build the output string
        String outString = "";
        for (Class<FeatureDescriptor> fd : classes) {
            String name = fd.getName().substring(offset);
            outString += StringUtils.rightPad(name, maxNameLength);
            outString += " : ";
            outString += fd.newInstance().supports().toString();
            outString += "\n";
        }
        System.out.println(outString);
    }

    /**
     * The constructor that should not be called outside this class (Except
     * Testclasses)
     *
     * @throws IOException
     */
    Extractor() throws IOException {
        properties = LibProperties.get();
        imageFormats = initImageFormats(properties);
    }

    /**
     * Read the image formats from the properties.
     *
     * @param libProperties
     * @return array of image formats
     */
    private String[] initImageFormats(LibProperties libProperties) {
        String[] formats = libProperties.getString(LibProperties.IMAGE_FORMATS).split(" *, *");
        for (int i = 0; i < formats.length; i++) {
            formats[i] = formats[i].trim();
        }
        return formats;
    }

    /**
     * start the actual work
     */
    private void process() {
        validateInput();

        Collection<File> maskList = createFileList(maskDirectory);
        Collection<File> imageList = createFileList(imageDirectory);
        HashMap<File, File> tuples = findTuples(imageList, maskList);

        openWriter();

        openPool();
        processImages(tuples);
        closePool();

        closeWriter();
    }

    /**
     * Validates the input parameters like descriptor names (nullchecks) and
     * ensures that the required files and directories are existent.
     *
     * @throws IllegalArgumentException
     */
    private void validateInput() throws IllegalArgumentException {
        if (descriptor == null) {
            throw new NullPointerException("descriptor must not be null");
        }

        try { // check if the descriptor class is valid
            String base = FeatureDescriptor.class.getPackage().getName();
            descriptorClazz = Class.forName(base + "." + descriptor);
            if (!FeatureDescriptor.class.isAssignableFrom(descriptorClazz)) {
                throw new IllegalArgumentException("The class must derive from FeatureDescriptor");
            }

            // check if masking is required and supported
            FeatureDescriptor fd = (FeatureDescriptor) descriptorClazz.newInstance();
            boolean supportsMasking = fd.supports().contains(Supports.Masking);
            if (maskDirectory != null && !supportsMasking) {
                log.warn("A masking directory is set but the chosen descriptor does NOT support masking. Masking will be ignored!");
                maskDirectory = null;
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
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
            log.debug("directory is null, returning empty list");
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

    /**
     * submits an ExtractionTask for each tuple to the thread pool
     *
     * @param tuples
     */
    private void processImages(HashMap<File, File> tuples) {
        for (Map.Entry<File, File> entry : tuples.entrySet()) {
            pool.submit(new ExtractionTask(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * creates a new thread pool for the image extraction tasks
     */
    private void openPool() {
        pool = Executors.newFixedThreadPool(threads);
    }

    /**
     * closes the thread pool and awaits termination
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

    /**
     * This task is used to read image data from disk, extract features and
     * initiate writing the output
     */
    class ExtractionTask implements Runnable {

        /**
         * the image file which should be processed
         */
        private final File image;
        /**
         * possibly set mask (may be null)
         */
        private final File mask;

        ExtractionTask(File image, File mask) {
            if (image == null) {
                throw new NullPointerException("image must not be null");
            }
            this.image = image;
            this.mask = mask;
        }

        @Override
        public void run() {
            try {
                long time = System.currentTimeMillis();

                // create some logging output
                if (log.isDebugEnabled()) {
                    String msg = "processing file " + image.getName();
                    if (mask != null) {
                        msg += " using mask: " + mask.getName();
                    }
                    log.debug(msg);
                }

                // read image and mask (if set)
                ImageProcessor processor = getProcessor(image);
                ImageProcessor maskProcessor = getProcessor(mask);
                processor.setMask(maskProcessor);

                // extraction
                FeatureDescriptor fd = (FeatureDescriptor) descriptorClazz.newInstance();
                fd.setProperties(properties);
                fd.run(processor);
                List<double[]> features = fd.getFeatures();

                // log some stats
                if (log.isDebugEnabled()) {
                    time = System.currentTimeMillis() - time;
                    log.debug("processed " + image.getName() + " in " + time + "ms");
                }

                // synchronously write to file
                writeOutput(image, features);
            } catch (IOException | InstantiationException | IllegalAccessException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }

        /**
         * Null safe image reader.
         *
         * @param path to the image file
         * @return image processor or null
         */
        private ImageProcessor getProcessor(File path) {
            ImageProcessor ip = null;
            if (path != null) {
                ImagePlus iplus = new Opener().openImage(path.getAbsolutePath());
                ip = iplus.getProcessor();
            }
            return ip;
        }
    }
}
