PepNovo+ v3.1 (beta) de novo sequencing for MS/MS. All rights
reserved to the Regents of the University of California, 2009.

Programmed by Ari Frank, please send any questions, comments or bug
reports to arf@cs.ucsd.edu.

PepNovo for MS-Blast
--------------------

PepNovo+ can be used in conjunction with MS-Blast to identify
homologous proteins when no database is available for the studied
organism. To this end, PepNovo creates additional output files that
contain de novo sequencing results that are amenable to processing
by MS-Blast.

To identify homologous proteins you first need to run PepNovo on you
input file (see readme.txt for instructions). You can use all input
flags used by regular PepNovo (model, PTMs, etc.). In addition you
can use the following options:

-msb_query_name NAME - where the parameter NAME is the file name
prefix that will be given to the output files (as explained below,
PepNovo will create the files NAME_dnv.txt, NAME_full.txt,
NAME_query.txt).

-msb_num_soloution X - The default is X=7; the maximal number of
sequences X that will be generated for each spectrum.

-msb_query_size X - The default is X=150000; the maximal size of the
MS-Blast query file.

-msb_min_score X - The default X=3.0; the minimal MS-Blast score
required in order to be included in the MS-Blast query. The MS-Blast
score is approximately the expected number of correct amino acids in
the de novo results for the spectrum. Typically a score of 7 or more
represents results that are likely to be (mostly) correct.

The output from MS-Blast consists of 3 files:

- NAME_dnv.txt - the regular PepNovo+ denovo results for the input
file(s).

- NAME_full.txt - the MSBlast query sequences generated for each
spectrum in the input file. The format of each line is: file index,
scan number, precursor m/z, number of sequences, MS-Blast score and
then a strings of sequences separated by "-".

- NAME_query.txt - the actual query that should be submitted to
MS-Blast. The lines are the same format as the "_full.txt" file,
however they are sorted according to descending MS-Blast score. In
addition, each line may contain (up to 3 times) more solutions that
each line in the "-full.txt" file. This happens because when
creating the query file, PepNovo tries to merge de novo results that
appear to belong to similar spectra from the same peptide. This
leads to less redundancy, and more unique peptide ids.

After generating the query file, it can be loaded to an MS-Blast
server for analysis (e.g., http://genetics.bwh.harvard.edu/msblast).
You can simply "cut and paste" the text from the query in the
appropriate window, set the parameters (e.g., database, Applying
LC-MS presets, etc.), and submit the job.

Citations:
----------

1. Original MS-Blast paper:

Shevchenko A, Sunyaev S, Loboda A, Shevchenko A, Bork P, Ens W,
Standing KG. Charting the proteomes of organisms with unsequenced
genomes by MALDI-quadrupole time-of-flight mass spectrometry and
BLAST homology searching. Anal Chem. 2001 73:1917-26.

2. Original PepNovo paper:

Frank, A. and Pevzner, P. "PepNovo: De Novo Peptide Sequencing via
Probabilistic Network Modeling", Analytical Chemistry 77:964-973,
2005.

3. Identifying homologous proteins using EagleEye, PepNovo and
MS-Blast:

Waridel P, Frank A, Thomas H, Surendranath V, Sunyaev S, Pevzner P,
Shevchenko A. Sequence similarity-driven proteomics in organisms
with unknown genomes by LC-MS/MS and automated de novo sequencing.
Proteomics. 2007 7:2318-29.


4. PepNovo+'s new scoring models:

Frank, A.M. A Ranking-Based Scoring Function for Peptide-Spectrum
Matches. J.Proteome Research 2009, 8, 2241-2252.

