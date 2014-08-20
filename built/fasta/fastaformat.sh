#!/bin/sh

# First step: Decoy database
# Decoy database via mimic (Kaell et al.), conserving homolog peptides, see:
# https://github.com/percolator/mimic/wiki/

echo "Creating decoy database from $1.fasta..."
perl /scratch/metaprot/data/fasta/DBTools/scripts/fasta-decoy.pl --ac-prefix=DECOY_ --in=$1.fasta --method=reverse --out=$1_decoy.fasta
echo "Created successfully $1_decoy.fasta."

# Second step: Format target and decoy databases for OMSSA, 
# pre-installed BLAST necessary, see:
# http://pubchem.ncbi.nlm.nih.gov/omssa/blast.htm

echo "Formatting target database for OMSSA..."
/scratch/metaprot/software/blast/bin/makeblastdb -in $1.fasta -dbtype prot
echo "Formatted successfully target database $1.fasta for OMSSA."

echo "Formatting decoy database for OMSSA..."
/scratch/metaprot/software/blast/bin/makeblastdb -in $1_decoy.fasta -dbtype prot
echo "Formatted successfully decoy database $1_decoy.fasta for OMSSA."

# Third step: Format and index databases form Crux, see:
#http://noble.gs.washington.edu/proj/crux/crux-create-index.html

#echo "Indexing database and added decoy database for Crux..."
#/scratch/metaprot/software/crux/bin/crux create-index $1.fasta $1-index --parameter-file /scratch/metaprot/software/crux/bin/default.params
#echo "Finished indexing database for Crux."

# Fourth and fifth step: Index the target database and created the shuffled database
# http://proteomics.ucsd.edu/InspectDocs/InspectTutorial.pdf
#echo "Indexing target database for Inspect..."
#python /scratch/metaprot/software/inspect/PrepDB.py FASTA $1.fasta
#echo "Indexed target database for Inspect."

#echo "Creating decoy database for Inspect..."
#python /scratch/metaprot/software/inspect/ShuffleDB.py -r $1.trie -w $1.RS.trie
#echo "Created decoy database for Inspect."

# Last step: Index the FASTA file for internal 
echo "Index FASTA file: Creating $1.fasta.fb..."
java -jar indexfasta.jar $1.fasta
echo "Created $1.fasta.fb."
:
echo "Finished database formatting. Have a nice day!"
exit 0
