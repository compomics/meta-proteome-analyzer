<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General_Information.md'>General Information</a>
</li><li><a href='#System_Requirements.md'>System Requirements</a>
</li><li><a href='#Portable_Startup.md'>Portable Startup</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>

## General Information ##
The MPA Portable represents a light-weight and stand-alone software for the identification of proteins for metaproteomics (and also proteomics) data. The MPA software uses X!Tandem and OMSSA as search engines and takes MGF spectrum files as input. The server application is included within the application and no SQL database needs to be set up. FASTA databases are formatted automatically within the tool.

**Please note:** MPA Portable can be run directly on your desktop PC or laptop and no separate installation for the search engine algorithms is needed. X!Tandem and OMSSA are included already in the MPA portable package.

[Go to top of page](#General_Information.md)

---

## System Requirements ##
  * **Operating system**: (Tested on Windows XP, Windows Vista, Windows 7)
  * **Memory**: The bigger the better (preferably 1 GB at least)

Please download the latest version of `mpa-portable-X.Y.Z.zip` (where X, Y and Z represent the current version of the software).

Before starting the MPA Portable version, please make sure that you have Java 1.7 installed. To check the currently installed java version, open a console/bash window and type:
```
java –version
```

If you haven't installed Java 1.7, go directly to
[Java.com](http://www.java.com/download/) to download this Java version.

[Go to top of page](#General_Information.md)

---

## Portable Startup ##
After downloading the zip file, simply unzip the file and use the provided script, i.e. mpa-portable.bat (Windows) or mpa-portable.sh
(Linux).

You can also double-click on the JAR file, however this will give you no options to change the memory settings (see below).

Another possiblity is to use the commandline directly:
```
java -jar mpa-portable-X.Y.Z.jar -XmxXXXXm 
```
**Note:** -Xmx\*XXXX\*m stands for maximum assigned memory (in megabytes of RAM)
