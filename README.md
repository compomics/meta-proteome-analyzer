# MetaProteomeAnalyzer (MPA)

  * [Introduction](#introduction)
  * [Features](#features)
  * [Remote Server](#remote-server)
  * [Downloads](#downloads)
  * [Installation](#installation)
  * [System Requirements](#system-requirements)

---

## MetaProteomeAnalyzer Publications:

  * [Muth _et al._: Anal Chem. 2018 90(1):685-689](https://www.ncbi.nlm.nih.gov/pubmed/29215871).
  * [Muth _et al._: J Proteome Res. 2015 14(3):1557-65](http://www.ncbi.nlm.nih.gov/pubmed/25660940).
  * If you use MetaProteomeAnalyzer as part of a publication, please include the reference(s). Thank you!


---

## Introduction

**MetaProteomeAnalyzer** (MPA) is a scientific software for analyzing and visualizing metaproteomics (and also proteomics) data. The tool presents a MS/MS spectrum data processing application for protein identification in combination with an user-friendly interactive graphical interface. The metaproteomics data analysis software is developed in the Java programming language and provides various features for an user-defined querying of the results. In addition, MPA can also be executed on the command line (see below).

---

## Features

The MPA metaproteomics software comes with the following key features:
  * Intuitive graphical user interface
  * Project management for MS/MS experiments
  * Shotgun proteomics data analysis tool
  * Integrates the search algorithms X!Tandem and OMSSA
  * Supports result file from the Mascot search engine  
  * Detailed analysis of taxa, ontologies, pathways and enzymes
  * Grouping of redundant protein hits to so-called "meta-proteins" (protein groups)
  * Label-free quantification methods
  * Interactive overview for each result set

---

## Remote Server

We highly recommend users to use the MPA on our remote server provided by the German Network for Bioinformatics Infrastructure (de.NBI). Through a remote desktop connection, users can gain access to the server and execute their database searches and data analysis tasks using hardware usually not available to the user (up to 48 processor cores and up to 500 GB of RAM). Furthermore, full software and bioinformatics support is easily available. Access remote server at www.mpa.ovgu.de. To get an account write an email at mpa@ovgu.de. 

---

## Downloads

[[Download full installation package]](http://www.mpa.ovgu.de/wp-content/uploads/MPAv2-Installation-package.zip)

[[Download initialized database]](http://www.mpa.ovgu.de/wp-content/uploads/MPA_Init_Database.sql)

---

## Installation

The easiest way for installation is to use the provided installation package and the initalized SQL database (See downloads). The package contains everything necessary to run the MPA (except Java 1.8). It also includes a test data set to confirm the software is running properly (see: http://www.mpa.ovgu.de/index.php/test-dataset/). The entire installation process should take approximately than 15 minutes.

For Windows installations you can follow our installation guide here: http://www.mpa.ovgu.de/index.php/tutorials/how-to-install-mpa-on-windows/

For other operating systems, you have to install Java 1.8 and MySQL beforehand. Furthermore, you need to initialize the SQL database using the MPAinit.sql (See Downloads) using the command line: "mysql -uUSERNAME -p MPAinit.sql > metaprot". The folder "metaprot" can be put anywhere in the file system. The configuration file "config_LINUX.properties" must be modified to specifiy the correct SQL user, SQL password and SQL database. The MPA can be started using the command line: "java -jar mpa.jar" or by creating a script that executes this command. 

---

## System Requirements

  * **Operating system**: (Tested on Windows XP, Windows Vista, Windows 7/8/10 and various Linux systems)
  * **Memory**: The more memory, the better! (preferably 4 GB at least, but highly recommended are 8 or 16 GB of RAM)

To run the MPA, please install Java 1.8 and MySQL. The installation package contains the necessary files for a MySQL installation on Windows.  

If you haven't installed Java 1.8, please go directly to
[Java.com](http://www.java.com/download/) to download the latest Java version.


