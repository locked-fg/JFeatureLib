[![Build Status](https://secure.travis-ci.org/locked-fg/JFeatureLib.png?branch=v1.0.0)](https://travis-ci.org/locked-fg/JFeatureLib)

JFeatureLib
===========

A free library that provides implementations for several kinds of image features 
and also several point/region detectors used mainly in the research field of 
ComputerVision.

This Github repo is a mirror of https://jfeaturelib.googlecode.com

Current releases can also be found at maven:
https://oss.sonatype.org/content/repositories/releases/de/lmu/ifi/dbs/jfeaturelib/JFeatureLib/


Changelog
=======
* 1.6.0:
      - added LocalBinaryPatterns, MeanIntensityLocalBinaryPatterns, MeanPatchIntensityHistogram (Pull Request #21).
        Thanks Sebastian Pölsterl.
* 1.5.3:
       - Omit "save file" dialog when using SIFT. Fixes #19. Thanks Johannes Niedermayer
* 1.5.2:
       - Caught all Throwables in Extractor (Pull Request #17). Thanks Sebastian Pölsterl.
       - added quoting of Extractor Output (Pull Request #18). Thanks Sebastian Pölsterl.
* 1.5.1:
       - Fixed an Haralick-Bug (Issue #16). Thanks Sebastian Pölsterl.
       - Provide multi character image classes (Issue #15). Thanks Sebastian Pölsterl.
       - Fixed quite some issues that FindBugs marked.
* 1.5.0:
       - Extractor now also supports output to stdout (Thanks Erich Schubert)
       - Extractor now also supports case insensitive file suffixes
       - added JpegCoefficientsHistogram (from Lire)
       - added basic tests for features
       - added capabilities to SUSAN extractor
       - removed obsolete RGBtoGray
       - extended the Extractor help functionality
       - fixed an invalid API in Kernel
       - fixed LuminanceLayout (typo and wrong implementation)
       - fixed Extractor debugging
* 1.4.1: 
       - fixed Tamura instanciation bug
* 1.4.0: 
       - packaged LIRE directly into the JARs, thanks David Maiserer
       - updated to LIRE 0.9.4-beta (08.July.2013 including custom patch)
       - added LuminanceLayout (from LIRE)
       - added OpponentHistogram (from LIRE)
       - added FuzzyOpponentHistogram (from LIRE)
       - added MPEG7ColorLayout (from LIRE)
       - added MPEG7EdgeHistogram (from LIRE)
       - fixed Threading issue in Histograms, thanks Christian Surer
       - updated dependency to imageJ v1.46
* 1.3.2: 
       - fixed Dependency to Google Guava, thanks Nepomuk Seiler
* 1.3.1: 
       - fixed Issue 29 & 20
       - changed dependency of common-extension-lib (fixes NaNs)
       - Made the jar executable (again)
* 1.3.0: 
       - changed PHOG code according to new common-extension-lib
       - fixed Issue 24: NPE in AutoColorCorrelogram
       - fixed Issue 26: LibProperties
       - fixed Issue 23: added new feature ColorHistogram, ReferenceColorSimilarity
* 1.2.0: 
       - added new feature descriptor AutoColorCorrelogram, thanx LIRE
       - replaced the lire library with the most recent lire version 0.9.3
       - removed the package de.lmu.ifi.dbs.jfeaturelib.features.lire and implemented the according delegates
       - added properties support for lire features
       - added properties support for thumbnail
       - removed deprecated method calls in SURF / removed surf classes that are not needed for extraction
       - Changed ConfigurableDescriptor to AbstractDescriptor
       - added code licencing blocks
       - corrected JavaDocs to get rid of all the warnings
       - changed dependency to common-extension-lib 2.1.0
* 1.1.0: 
       - added SquareModelShapeMatrix and according properties
       - changed de.lmu.ifi.dbs.jfeaturelib.shapeFeatures.Profiles to a new implemenation provided by Johannes Niedermayer
       - made Histogram.TYPE public static
       - added config option for haralick distance
* 1.0.1: 
       - fixed error that properties were not found
       - added possibility to extract jfeaturelib.properties from jar by using the Extractor
       - added possibility to extract logging.properties from jar by using the Extractor
* 1.0.0: 
       - corrected package path from de.lmu.dbs.jfeaturelib to de.lmu.ifi.dbs.jfeaturelib (added ifi)
       - Fixed Issue 19 (possibility to list feature descriptor capabilities in CLI)
       - published to maven https://oss.sonatype.org/content/groups/public/de/lmu/ifi/dbs/jfeaturelib/JFeatureLib/
* 0.4.0: 
       - changed build system to maven, thanks Nepomuk Seiler
       - checked the support values of all feature extractors
       - Fixed Issue 18 (masking support in PHOG)
       - Fixed Issue 17 (masking support in CLI)
       - Made Extractor more convenient and robust
       - Fixed Issue 8 (Canny support)
* 0.3.1: 
       - annoying Logging.properties fix in Extractor
* 0.3.0: 
       - made Canny and PHOG configurable via properties
       - extend properties and PHOG to combine PHOG and Canny
* 0.2.0: 
       - added shape features (special thanks to Johannes Stadler and Johannes Niedermayer)
       - changed completely to log4j
       - fixed buildfile
       - Fixed Issue 14
* 0.1.2: 
       - aggregates GrayHistogram and RGB Histogram in Histogram which also supports 
         separate extraction of R,G,B, HSB, H, S, B Histograms
       - added settings to properties
       - added more convenient way of setting properties even through Extractor
* 0.1.1:
       - introduced Abstract Feature Descriptor
       - fixed bug in Haralick feature (Issue 11)
* 0.1.0:
       - start of history file
