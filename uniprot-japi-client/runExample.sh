#!/bin/sh

echo "==============================================="
echo "---- Demonstrating the UniProt client JAPI ----"
echo "==============================================="

## A simple example demonstrating how to use the UniProt Java Client.
echo "Please input the name of the Java class in the 'src/' (please remove the '.java'), that you would like to be compiled and executed:"
read mainClassName

# check specified class exists
if [ ! -f "src/$mainClassName.java" ]; then
    echo "[ERROR] the file src/$mainClassName.java does not exist."
    exit 1;
fi

# compile the classes
mainClassPackage="uk.ac.ebi.uniprot.dataservice.client.examples"
mainClass="$mainClassPackage.$mainClassName"
classes_dir=classes
mkdir -p $classes_dir

javac -d classes -classpath "lib/*" "src/$mainClassName.java"

# run the test
java -classpath "$classes_dir:lib/*" $mainClass