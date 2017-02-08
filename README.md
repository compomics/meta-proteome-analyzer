# MetaProteomeAnalyzer (MPA) #

  * [News](#news)
  * [Download](#download)
  * [Introduction](#introduction)
  * [MPA Portable](#mpa-portable)
  * [Command Line Usage](#command-line-usage)
  * [Features](#features)
  * [System Requirements](#system-requirements)
  * [MPA Startup](#mpa-startup)

---

## MetaProteomeAnalyzer Publication:
  * [Muth _et al._: J Proteome Res. 2015 Mar 6;14(3):1557-65.](http://www.ncbi.nlm.nih.gov/pubmed/25660940).
  * If you use MetaProteomeAnalyzer as part of a publication, please include this reference.

---

## Download ##

[[download]](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-portable-1.3.0.zip)  *MPA Portable version 1.3.0 - Windows and Linux 32-bit/64-bit - released on December 17, 2016*

---

## News ##

*MPA Portable version 1.3.0* has been released with the following new features:
 * Automated integration of the database search algorithms Comet and MS-GF+ (OMSSA was removed)
 * Support of two-step database searching (beta version - to be tested)
 * Cross-platform compatiblity with full linux support
 * The overall speed of the application was improved due to various code modifications
 * Support of the latest UniProtJAPI version 1.0.9.

---

## Introduction ##

**MetaProteomeAnalyzer** (MPA) is a scientific software for analyzing and visualizing metaproteomics (and also proteomics) data. The tool presents a MS/MS spectrum data processing application for protein identification in combination with an user-friendly interactive graphical interface. The metaproteomics data analysis software is developed in the Java programming language and provides various features for an user-defined querying of the results. In addition, MPA can also be executed on the command line (see below).

---

## MPA Portable ##

**MPA Portable** is a light-weight and stand-alone software for the identification of proteins and in-depth analysis of metaproteomics (and also proteomics) data. The MPA software currently supports the database search engines [Comet](http://comet-ms.sourceforge.net/), [MS-GF+](https://bix-lab.ucsd.edu/pages/viewpage.action?pageId=13533355) and [X!Tandem](http://www.thegpm.org/tandem/) taking MGF spectrum files as input data. User-provided FASTA databases (preferably downloaded from UniProtKB) are formatted automatically.
*Please note:* MPA Portable can be run directly on your desktop PC or laptop and no separate installation is needed: just double-click the provided JAR file or use the provided batch file (see below on this page).

---

## Command Line Usage ##

MPA can also be used via the command line, see [MetaProteomeAnalyzerCLI](https://github.com/compomics/meta-proteome-analyzer/wiki/MetaProteomeAnalyzerCLI) for details.

--

## Features ##

The MPA metaproteomics software comes with the following key features:
  * Intuitive graphical user interface
  * Project management for MS/MS experiments
  * Shotgun protomics data analysis tool
  * Integrating MS-GF+, X!Tandem and OMSSA search algorithms
  * Detailed analysis of taxa, ontologies, pathways and enzymes
  * Grouping of redundant protein hits to so-called "meta-proteins" (protein groups)
  * Label-free quantification methods
  * Graph database-driven customized querying of results
  * Interactive overview for each result set
  * Portable application 

---

## System Requirements ##
  * **Operating system**: (Tested on Windows XP, Windows Vista, Windows 7/8/10 and various Linux systems)
  * **Memory**: The more memory, the better! (preferably 4 GB at least, but highly recommended are 8 or 16 GB of RAM)

Please [download](http://svn.mpi-magdeburg.mpg.de/MetaProteomeAnalyzer/Download/mpa-portable-1.3.0.zip) the latest version of `mpa-portable-X.Y.Z.zip` (where X, Y and Z represent the current version of the software).

Before starting the MPA Portable version, please make sure that you have Java 1.8 installed. To check the currently installed java version, open a console/bash window and type:
```
java –version
```

If you haven't installed Java 1.8, please go directly to
[Java.com](http://www.java.com/download/) to download the latest Java version.

---

## MPA Startup ##
After downloading the zip file, simply unzip the file and use the provided script from the built folder, i.e. mpa-portable.bat for Windows  or mpa-portable.sh for Linux (granting the access rights accordingly!).

You can also double-click on the JAR file, however this will give you no options to change the memory settings (see below).

Another possiblity is to use the commandline directly:
```
java -jar mpa-portable-X.Y.Z.jar -XmxXXXXm 
```
**Note:** -Xmx*XXXX*m stands for maximum assigned memory (in megabytes of RAM)

