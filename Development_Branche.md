# Commit and Update to Development branch #

## Version 11.02.2015 ##
  * Fixed bugs in peptide taxonomy determination
  * Added export for EC and KO to metaprotein export


## Version 31.01.2015 ##
  * Debugged Mascot Storager (redundancy of saved protein entries)

## Version 26.01.2015 ##
  * Added export for UniRef entries

## Version 15.01.2015 ##

  * Storing AS sequence in DB for MascotDatFile Reading from local FASTA

## Version 11.12.2014 ##
  * Enabled to search against metagenome FASTA databases
  * Enabled BLAST  for non-UniProt protein entries
    1. Works only for Xtandem and OMSSA, because Mascot dat.-File parsing does not store protein sequences
  * Added items to the ClientFrameMenuBar to update UniProt and UniRef entries


# Useful Steps #
> ## How to create a new server version ##
  * Go in "scr" and look for server\_export.xml
  * Update in this file the version number and the required libraries
  * Right-Click and "Ant Build"
  * Copy this file your MPA directory
  * Adapt startserver.sh to the new version number

> ## Update UniProtJAPi or other library ##

This is often necessary for the UniProtJAPi, because you need always the current version for the querying service (http://www.ebi.ac.uk/uniprot/remotingAPI/).
  * Download new library and copy into your library folder (MetaproteomeAnalyzer\lib)
  * Right-Click on MPA project, "Configure BuildPath", "Libraries" , Select old library and "Remove" it from the Build Path
  * Select in the MPA the new library in the "lib" folder, Right-Click and "Add to Build Path"