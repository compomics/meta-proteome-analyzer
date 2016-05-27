UNIPROT JAVA CLIENT API, version 1.0.6
------------------
http://www.ebi.ac.uk/uniprot/japi


1. INTRODUCTION

You can access UniProt data from a Java application with the UniProtJAPI.
Using provided data retrieval and search services, you can find and retrieve UniProt,
UniParc or UniRef entries matching a specified query.


2. DISTRIBUTION

./src - Source files for examples on using the UniProtJAPI
./classes - The pre-compiled sources
./lib - Libraries required to run the UniProtJAPI including the UniProtJAPI
./doc - Documentation and JavaDoc of the UniProt Object model.


3. RUN THE EXAMPLES

Use the runExample script provided: runExample.cmd for windows; or runExample.sh for most other environments.
The script requires a single parameter, which is the name of the test to be run. As a reference for any
classes you may develop in the future, the tests can be found in the /src directory.

E.g. On windows:

runExample.cmd AttributeExample

On unix:

sh runExample.sh AttributeExample

This command will run then the AttributeExample class located in the /classes directory.