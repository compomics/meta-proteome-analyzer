<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General_Information.md'>General Information</a>
</li><li><a href='#System_Requirements.md'>System Requirements</a>
</li><li><a href='#SQL_Database_Setup.md'>SQL Database Setup</a>
</li><li><a href='#Connection_Settings.md'>Connection Settings</a>
</li><li><a href='#Algorithms_Installation.md'>Algorithms Installation</a>
</li><li><a href='#FDR_Estimation.md'>FDR Estimation</a>
</li><li><a href='#Server_Settings.md'>Server Settings</a>
</li><li><a href='#FASTA_Database_Formatting.md'>FASTA Database Formatting</a>
</li><li><a href='#Server_Startup.md'>Server Startup</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>



---

## General Information ##

On this page you will find information how to setup the MetaProteomeAnalyzer server software, which is available via the `mpa-server-X.Y.Z.zip` file (see Downloads section).

**Important:** Please note that you may require some IT administration staff (and rights) in order to successfully install the MetaProteomeAnalyzer server pipeline.

[Go to top of page](#General_Information.md)

---

## System Requirements ##

  * Linux machine (Ubuntu or Debian preferably)
  * MySQL database (InnoDB preferably)
  * Minimum 2 GB RAM
  * Fast 64-bit machine (though 32-bit works as well)

**System folders:**

The default folder structure we used for our **metaprot** server was the following:
```
/scratch/metaprot/software 
```

This presents the main folder for the bash script **clearfolders.sh** and the main executable **mpa-server-X.Y.Z.jar**.
See [below](#Server_Startup.md) for executing the server.

For the data exchange (received spectra from the client and identification result files) we recommend to also use a default like this:
```
/scratch/metaprot/data
```

For each of the algorithm result files (for algorithms installation see [below](#Algorithms_Installation.md)), there should be an output folder created, as follows:
```
/scratch/metaprot/data/output/xtandem (For X!Tandem)
/scratch/metaprot/data/output/omssa (For OMSSA)
```

In more details the transfered spectra from the client are directed to the following folder:
```
/scratch/metaprot/data/transfer
```

**Important:** It is possible to adjust these default folders specifications (and also the file locations) by a provided properties file, see `server-settings.txt` in the **software/conf** folder.

[Go to top of page](#General_Information.md)

---

## SQL Database Setup ##
Make sure you have a ready MySQL server running. We recommend the tool **PhpMyAdmin** in order to interact with the MySQL via the browser.

The first step is to create the database **metaprot**.
Then please execute the provided SQL structure script called `metaprot.sql` from the folder **sql database** (in the unzipped mpa-server folder structure).

After execution 26 tables should have been created.

The next step is to insert data into the taxonomy table by using the `taxonomy.sql` in the **SQL database** folder.

**Note:** Please make sure that the table **taxonomy** in the SQL metaprot database has been successfully filled.

**Important:** You may also need to insert the taxonomy.sql directly via the commandline (instead of PhyMyAdmin) due to upload limitations.
This taxonomy insert then can be done by using the following command within mysql:
```
source PATH/taxonomy.sql
```

[Go to top of page](#General_Information.md)

---

## Connection Settings ##
The file `connection-settings.txt` in the folder **conf** contains the following parameters:
```
*dbAddress* => The URL of the SQL database, e.g. localhost or an IP address
*dbName* => The name of the database, the chosen default is *metaprot*
*dbUsername* => The username for the SQL database access
*dbPass* => The password for the SQL database access
 *srvAddress* => The URL of the server or IP address
 *srvPort* => The server port, e.g. 8080 
```

**Important:** Please make sure that this file is located in the folder **conf** relaive to the mpa-server-X.Y.Z.jar.

Note: If you have any trouble with setting up the MySQL database, please contact your local system administrator.

[Go to top of page](#General_Information.md)

---

## Algorithms Installation ##

Please note that the installation instructions may not be up-to-date. Please visit the websites of the search engine algorithms to get the latest versions and further installation details.
**Important: We recommend using only X!Tandem and OMSSA for the searches. Crux may not produce reliable results and InSpect may run for a long time. Therefore, the installation of Crux and InSpect can be omitted.**

  * X!Tandem [Link to website](http://www.thegpm.org/tandem/)
```

Installation:

wget ftp://ftp.thegpm.org/projects/tandem/source/tandem-linux-13-09-01-1.tar.gz
tar xvfz tandem-linux-13-09-01-1.tar.gz

Note: There are 32-bit and 64-bit versions of X!Tandem available. Please choose the version which is suitable for your system.
For example, there is a 64-bit folder with static and dynamic builds:
The best is to pick one of the tandem.exe files and move the file to the main bin folder (e.g. /scratch/metaprot/software/xtandem/bin).

Folder:
/scratch/metaprot/software/xtandem

Example usage:
./tandem.exe input.xml
```

  * OMSSA [Link FTP Server](ftp://ftp.ncbi.nih.gov/pub/lewisg/omssa/CURRENT/)
```
Installation:
wget ftp://ftp.ncbi.nih.gov/pub/lewisg/omssa/CURRENT/omssa-linux.tar.gz
tar xvfz omssa-linux.tar.gz

Folder:
/scratch/metaprot/software/omssa

Note: You need to install a version of BLAST for formatting of the FASTA databases.

Installation BLAST:
_32-bit:_
wget ftp://ftp.ncbi.nih.gov/blast/executables/LATEST/ncbi-blast-2.2.29+-ia32-linux.tar.gz 
tar xvfz ncbi-blast-2.2.29+-ia32-linux.tar.gz

_64-bit:_
wget ftp://ftp.ncbi.nih.gov/blast/executables/LATEST/ncbi-blast-2.2.29+-x64-linux.tar.gz 
tar xvfz ncbi-blast-2.2.29+-x64-linux.tar.gz

Folder:
/scratch/metaprot/software/blast
```

  * Crux (Installation not recommended) [Link to website](http://noble.gs.washington.edu/proj/crux/)
```
Installation:
_32-bit:_
wget http://noble.gs.washington.edu/proj/crux/download/crux-1.40.Linux.i686.zip
unzip crux-1.40.Linux.i686.zip

_64-bit:_
wget http://noble.gs.washington.edu/proj/crux/download/crux-1.40.Linux.x86_64.zip
unzip crux-1.40.Linux.x86_64.zip

Folder:
/scratch/metaprot/software/crux
```
**Important:** Please make sure you use a parameters file for Crux and put it into the Crux bin-folder here:
/scratch/metaprot/software/crux/bin

An example of a default parameters file can be found [here (default.params)](http://noble.gs.washington.edu/proj/crux/default.params).


  * Inspect (Installation not recommended) [Link to website](http://proteomics.ucsd.edu/Software/Inspect.html)
```
Installation:
wget http://proteomics.ucsd.edu/Downloads/Inspect.20120109.zip
mkdir /scratch/metaprot/software/inspect (Attention: the zipfile does not contain a separate folder)
Move to this folder and unzip the file to it:
unzip Inspect.20120109.zip

Dependencies: 
_expat_
sudo apt-get install expat libexpat1-dev (Ubuntu)
_python-numpy_
apt-get install python-numpy (Ubuntu)

Folder:
/scratch/metaprot/software/inspect
```

[Go to top of page](#General_Information.md)

---

## FDR Estimation ##

In order to the perform a reliable FDR estimation via the target-decoy approach, the software QVality (Kaell et al.) is used in the MPA pipeline. Therefore, the Percolator needs to be whole Percolator package needs to be installed.

```
Installation:
wget http://sourceforge.net/projects/percolator/files/v2-08-01/percolator-v2-08-01-linux-amd64.deb

sudo dpkg -i percolator-v2-08-01-linux-amd64.deb

Default folder:
/usr/bin/qvality (installed globally)
```

[Go to top of page](#General_Information.md)

---

## Server Settings ##
Within the file `server-settings.txt` in the folder **conf** you can specify parameters and path specifications for the algorithms. Changing this file is necessary whenever you intend to use non-default settings.
The file is structured as follows:
```
# Port of the server webservice 
app.port=8080

# Main path where the application is running
path.app=/scratch/metaprot/software/

# Transfer path for the spectra
path.transfer=/scratch/metaprot/data/transfer/

...
```

**Important:** Please make sure that the server-settings.txt file is located in the folder **conf** relaive to the mpa-server-X.Y.Z.jar.

[Go to top of page](#General_Information.md)

---

## FASTA Database Formatting ##
The FASTA databases (e.g. uniprot\_sprot.fasta) should be provided to the default FASTA database folder:
```
/scratch/metaprot/data/fasta
```

When you have your FASTA file(s) in place, copy the following files and directories from the folder **fasta** (from the mpa-server zipfile) to the FASTA database folder (see above):
  * `indexfasta.jar` => MPA Indexing of the FASTA file
  * `DBTools` (+ subdirectories) => Decoy generation of the FASTA file
  * `fastaformat.sh` => Script used to execute the whole formatting process

The actual FASTA formatting in then executing by the following command
```
./fastaformat.sh uniprot_sprot (Execution rights needed)
```
**Important:** Please note that the file ending **.fasta** must be omitted here!

**Note:** The fastaformat.sh script contains the default locations of the algorithms, please adjust them accordingly. Otherwise the FASTA database formatting will not work.

Note: Please edit the script to your demands, e.g. by omitting certain search engine algorithms, e.g. Crux and InsPect are omitted by default.

**Please Note:** If you are using protein databases in a format different from UniProt (see [UniProt FASTA header examples](http://www.uniprot.org/help/fasta-headers)), please make sure that the FASTA header for each protein entry meets the following conditions:
```

>DB|ACCESSION|SHORT_DESCRIPTION FULL_DESCRIPTION

DB: for example "sp" resembles Swiss-Prot. "mg" stands for Metagenome-based protein database.
ACCESSION: Unique identifier. For example a 8-digit augmenting number: 00000001
SHORT_DESCRIPTION: Short string description (maximum 10 chars, no spaces)
FULL_DESCRIPTION: Full string description (no limited char number, spaces allowed)

```
The MPA software supports user-defined (e.g. metagenome-based) databases in general, however, many function related to UniProt meta-information, such as ontologies, pathways and taxonomies cannot be accessed.

**Tip:** For an easy FASTA database creation and manipulation (including a protein redundancy check), we recommend the software tool dbtoolkit:

http://code.google.com/p/dbtoolkit

[Go to top of page](#General_Information.md)

---

## Server Startup ##
Make sure that the script `clearfolders.sh` from the folder (from the mpa-server zipfile) is also located in the default software folder (e.g. /scratch/metaprot/software) next to the mpa-server application.
**Important:** This script needs to have execution rights and should contain the correct locations of the output folders.

The server application can be started within the software folder using the following command:
```
nohup java -jar mpa-server-X.Y.Z.jar -XmxXXXXm where XXXX stands for maximum assigned memory (in megabytes of RAM)
```

[Go to top of page](#General_Information.md)

---
