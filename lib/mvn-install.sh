echo "Installing all dependencies into local maven repository"
DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Caliph Emir
mvn install:install-file -Dfile=$DIR/caliph-emir-cbir.jar -DgroupId=at.lux -DartifactId=imageanalysis -Dversion=1.0.0 -Dpackaging=jar

# ImageJ
mvn install:install-file -Dfile=$DIR/ij.jar -DgroupId=at.lux -DartifactId=imageanalysis -Dversion=1.45.0 -Dpackaging=jar 

# Lire
mvn install:install-file -Dfile=$DIR/lire.jar -DgroupId=net.semanticmetadata -DartifactId=lire -Dversion=1.0.0 -Dpackaging=jar