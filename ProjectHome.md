# Metaproteomics Data Analysis Software #
![http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA_splash01.png](http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA_splash01.png)

## Introduction ##
The **MetaProteomeAnalyzer** (MPA) is a software pipeline to analyze and visualize **metaproteomics (and also proteomics) data**. It consists of a processing server application and a graph-database driven interactive client.
The **metaproteomics data analysis software** is developed in the Java programming language and uses a [neo4j graph database](http://www.neo4j.org) backend for flexible and user-defined data querying of the results.


---


## Software Versions ##
**Please note:** The MPA software versions currently only support **Java 7**.

### MPA Portable ###
The **MPA Portable** represents a light-weight and **stand-alone software** for the identification of proteins and in-depth analysis of metaproteomics (and also proteomics) data. The MPA software uses X!Tandem and OMSSA as search engines and takes MGF spectrum files as input. The server application is included within the application and no SQL database needs to be set up. The user-provided FASTA databases are formatted automatically.

**Please note:** MPA Portable can be run directly on your desktop PC or laptop and no separate installation is needed: just double-click the provided JAR file or use one of the provided batch files.

_Current MPA Portable version: 1.0.4_ [Download MPA Portable Here (Java 7 - Windows 64bit)](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-portable-1.0.4.zip)

### MPA Viewer ###
The **MPA Viewer** provides a convenient way to visualize processed MPA result files. The viewer application is listed under the downloads section. Feel free to have a closer look into our software by using the provided metaproteomics sample data set ([Dataset\_Ebendorf.zip](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/Dataset_Ebendorf.zip)
).

**Please note:** A [tutorial](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/MPA_Tutorial.pdf) is available to guide you through a first evaluation of the example data set via the MPA Viewer application.


_Current MPA Viewer version: 1.0.4_ (under Downloads)

### MPA Client-Server Pipeline ###
The **MPA Client** and **MPA Server** versions are available for running the MPA and the whole processing pipeline with own metaproteomics data for a powerful in-house client-server workflow. In this setting, up to five identification search engines are supported and multiple client can connect to one server.
Both required software programs can be found under the download section.
Please have a look on the MPA wiki pages for setting up the client-server infrastructure and the SQL database in your facility.

**Please note:** If you intend to use the MPA software on one single computer, we recommend to use the **MPA Portable** version instead.


_Current MPA Client-Server versions: 1.0.4_ (under Downloads)

---


## Publication ##
[Muth, Behne et al. J Proteome Res. 2015 Mar 6;14(3):1557-65](http://www.ncbi.nlm.nih.gov/pubmed/25660940).

If you use **MetaProteomeAnalyzer** as part of a paper, please include the reference above. Thank you.

---


## News ##
**_May 06, 2015:_ MPA Portable v1.0.4 is now available:**
  * Updated UniProt JAPI to latest version (05/2015).
  * Organized some code and removed redundancies.

**_April 23, 2015:_ MPA Portable v1.0.3 is now available:**
  * X!Tandem and OMSSA output files + QVality files are removed after processing of the search task has finished.
  * Expection is thrown if FASTA accession is not found in the index file.
  * Low memory settings caused delayed loading of taxonomy map. Buffered Inputstream is used to load the taxonomy map by a factor of 8.


**_April 14, 2015:_ MPA Portable v1.0.2 is now available:**
  * FASTA file with white spaces are renamed by replacing spaces with underscore "_" characters.
  * An error shows up if the user uses the software in a folder structure with white spaces.
  * Updated UniProt JAPI to latest version (04/2015)._


**_April 08, 2015:_ MPA Portable v1.0.1 is now available:**
  * No client-server architecture or database is needed for MPA Portable.
  * Automated formatting of the FASTA databases.
  * X!Tandem and OMSSA are currently supported (MASCOT will be supported soon).
  * Taxonomy has beed updated to the latest NCBI version.


**_January 23, 2015:_ MPA Client, Server and Viewer v1.0.4 now available:**
  * Expanded tree views are now exported by default.
  * Fixed bug in the export views (taxonomic levels).
  * File icon (www) is not exported as file path anymore.
  * The protein header now need to be formatting in the following format: DB|ACCESSION|SHORT\_DESCRIPTION FULL\_DESCRIPTION.
  * Indexfasta.jar has been updated accordingly.
  * Changed memory default settings to 2GB for the whole MPA project.
  * Fixed MascotParameters dialog issue.
  * Updated UniProt JAPI to latest version (01/2015).
  * Added MPA server, client and viewer version 1.0.4 to the website.


**_Dezember 10, 2014:_ MPA Client and Server v1.0.3 is now available:**
  * Updated UniProt JAPI to latest version (11/2014).
  * Fixed bug in Mascot parameters dialog (integer casting with double value did not work).
  * Removed hard-coded port for the server webservice and added it to the configuration file "server-settings.txt": app.port=8080 (Default port)
  * Client-settings.txt contains server port (not hard-coded default port anymore)
  * Some improvements on the client and the handling advanced settings / parameters.
  * Added MPA server and client version 1.0.3 to the website.


**_October 17, 2014:_ MPA Client and Server v1.0.2 now available:**
  * Added MPA server version 1.0.2 to the website.
  * Added MPA client version 1.0.2 to the webiste.
  * Updated MPA viewer to version 1.0.2.
  * Fixed error when scores from ambiguous spectra were shown twice for each search engine.
  * Fixed meta-protein grouping parameter: changes now result in reprocessing of the results.
  * Added exporting of the meta-protein taxonomy and identified spectra.
  * Updated UniProt JAPI to the latest version (09/2014)
  * Updated various classes and removed obsolete code.


**_July 17, 2014:_ MPA Client and Server v1.0.1 now available:**
  * Added Subspecies to taxonomy export.
  * Added Krona-Plot support to taxonomy export.
  * Fixed lacking support for checkbox selection on KEGG pathways string building.
  * Fixed problems when exporting PSMs, Proteins and Taxonomies from the export menu.
  * Updated GraphDB queries to account for taxonomy ranks.
  * Added a few new GraphDB queries to the saved queries.

**_May 28, 2014:_ MetaProteomeAnalyzer v1.0 is now available:**
  * MPA Viewer release candidate is now provided under the downloads.


---


## Downloads ##

  * MPA Portable Application: [MPA Portable v1.0.4 (Java 7 - Windows 64bit)](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-portable-1.0.4.zip)


---


  * MPA Viewer Application: [MPA Viewer v1.0.4 (Java 7 - All Operating Systems)](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-viewer-1.0.4.zip)
  * Dataset (Biogas Sample): [Dataset\_Ebendorf.zip](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/Dataset_Ebendorf.zip)
  * Guided Tutorial PDF: [MPA\_Tutorial.pdf](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/MPA_Tutorial.pdf)


---


  * MPA Client Application: [MPA Client v1.0.4 (Java 7 - All Operating Systems) ](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-client-1.0.4.zip)

---


  * MPA Server Application: [MPA Server v1.0.4 (Java 7 - Ubuntu Linux)](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-server-1.0.4.zip)


---


## MPA software suite ##
The MPA metaproteomics software comes with the following key features:

  * Intuitive graphical user interface
  * Project management
  * Top-down metaproteomics data analysis
  * In-depth analysis of taxonomies, ontologies, pathways and enzymes
  * Grouping of redundant protein hits to so-called "Meta-Proteins" (protein groups)
  * Label-free quantification methods
  * Graph database-driven
  * Interactive overview (Charts and Heatmap)
  * Portable application (see Downloads)
  * Viewer application (see Downloads)
  * Client-server pipeline (see Downloads)


---


## Screenshots ##

![![](http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA1_small.png)](http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA1.png)
![![](http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA2_small.png)](http://meta-proteome-analyzer.googlecode.com/svn/wiki/images/MPA2.png)


---


## Protein Identification ##
The metaproteomics data analysis pipeline uses a combination of multiple database search algorithms to enhance proteomics data analysis.

The following identification algorithms are implemented:
| **Algorithm** | **MPA Versions** | **Parser** |
|:--------------|:-----------------|:-----------|
| [X!Tandem](http://www.thegpm.org/tandem/) | Server, Portable  | [XTandem Parser](https://code.google.com/p/xtandem-parser/) |
| [OMSSA](http://pubchem.ncbi.nlm.nih.gov/omssa/) | Server, Portable | [OMSSA Parser](https://code.google.com/p/omssa-parser/) |
| [Crux](http://noble.gs.washington.edu/proj/crux/) | Server           | ---        |
| [InsPecT](http://proteomics.ucsd.edu/Software/Inspect.html) | Server           | ---        |
| [MASCOT](http://www.matrixscience.com/) | Client, Portable | [MascotDatFile](http://code.google.com/p/mascotdatfile/) |


---


## Client-server communication ##
The communication between the MPA server and client is handled via **Web Services** and the JAX-WS (Java API for XML Web Services) module.