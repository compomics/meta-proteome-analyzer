<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General_Information.md'>General Information</a>
</li><li><a href='#System_Requirements.md'>System Requirements</a>
</li><li><a href='#Connection_Settings.md'>Connection Settings</a>
</li><li><a href='#Client_Settings.md'>Client Settings</a>
</li><li><a href='#Client_Startup.md'>Client Startup</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>

## General Information ##
Before installing MetaProteomeAnalyzer, please make sure that you have Java 1.7 installed. To check the currently installed java version, open a console/bash window and type:
```
java –version
```

If you haven't installed Java 1.7, go directly to
[Java.com](http://www.java.com/download/) to download a proper version of Java.

[Go to top of page](#General_Information.md)

---

## System Requirements ##
  * **Operating system**: Platform-independent (tested on Windows XP, Windows Vista, Windows 7, various Linux/Ubuntu distributions)
  * **Memory**: The bigger the better (preferably 1 GB at least)

Please download the latest version of `mpa-client-X.Y.Z.zip` (where X, Y and Z represent the current version of the software).

[Go to top of page](#General_Information.md)

---

## Connection Settings ##
The file `connection-settings.txt` in the folder **conf** contains the following parameters:
```
*dbAddress* => The URL of the SQL database, e.g. localhost or an IP address
*dbName* => The name of the database, the chosen default is *metaprot*
*dbUsername* => The username for the SQL database access
*dbPass* => The password for the SQL database access
*dbPass* => The password for the SQL database access
*srvAddress* => The IP address of the server (running mpa-server.jar)
*srvPort* => The port of the server (default: 8080)
```

**Important:** Please make sure that this file is located in the folder **conf** relative to the mpa-client-X.Y.Z.jar.

[Go to top of page](#General_Information.md)

---

## Client Settings ##
The client settings are for used only for the selection of the chosen FASTA database(s). For example, when using a the SwissProt database, the following should be inserted here:
```
files.fasta=uniprot_sprot
```
**Important:** For using multiple FASTA databases, the names of the FASTA files should be provided as comma separated list, e.g. uniprot\_sprot, uniprot\_trembl.

The names of the FASTA databases will be shown in the client application for selection. Please make sure that the server does contain exactly these FASTA databases and their formatted version (target-decoy and indexed versions).

[Go to top of page](#General_Information.md)

---

## Client Startup ##
After downloading the zip file, simply unzip the file and use the provided script, i.e. mpa-client.bat (Windows) or mpa-client.sh (Linux)

Another possiblity is to use the commandline directly:
```
java -jar mpa-client-X.Y.Z.jar -XmxXXXXm 
```
**Note:** -Xmx\*XXXX\*m stands for maximum assigned memory (in megabytes of RAM)
