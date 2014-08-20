#!/usr/bin/env perl
use strict;
use Test::More tests => 1;
use File::Basename;
my $dir=dirname $0;

chdir $dir;

use InSilicoSpectro::Databanks::DBEntryUniprot;

my $dbu=new InSilicoSpectro::Databanks::DBEntryUniprot;
ok($dbu, "InSilicoSpectro::Databanks::DBEntryUniprot object instanciated");

print STDERR "DIR=$dir\n";
use Cwd qw(getcwd);

$ENV{INSILICOSPECTRO_DEFFILE}=getcwd."/insilicodef-test.xml";


{
  local $/="//\n";;
  $_=<DATA>;
  $dbu->readDat($_);
  $dbu->printFasta();
  my @tmp=$dbu->generateDerivedForms();
  foreach (@tmp){
    $_->printFasta;
  }
}




__DATA__
ID   AACT_HUMAN     STANDARD;      PRT;   423 AA.
AC   P01011; Q13703; Q59GP9; Q6LBY8; Q6LDT7; Q6NSC9; Q8N177à; Q96DW8;
AC   Q9UNU9;
DT   21-JUL-1986, integrated into UniProtKB/Swiss-Prot.
DT   01-AUG-1991, sequence version 2.
DT   27-JUN-2006, entry version 88.
DE   Alpha-1-antichymotrypsin precursor (ACT) [Contains: Alpha-1-
DE   antichymotrypsin His-Pro-less].
GN   Name=SERPINA3; Synonyms=AACT;
OS   Homo sapiens (Human).
OC   Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi;
OC   Mammalia; Eutheria; Euarchontoglires; Primates; Haplorrhini;
OC   Catarrhini; Hominidae; Homo.
OX   NCBI_TaxID=9606;
RN   [1]
RP   NUCLEOTIDE SEQUENCE [MRNA] (ISOFORM 1).
RX   MEDLINE=84080367; PubMed=6606438;
RA   Chandra T., Stackhouse R., Kidd V.J., Robson K.J.H., Woo S.L.C.;
RT   "Sequence homology between human alpha 1-antichymotrypsin, alpha 1-
RT   antitrypsin, and antithrombin III.";
RL   Biochemistry 22:5055-5061(1983).
RN   [2]
RP   NUCLEOTIDE SEQUENCE [GENOMIC DNA] (ISOFORM 1), AND VARIANTS BOCHUM-1
RP   PRO-78 AND BONN-1 ALA-252.
RX   MEDLINE=94063919; PubMed=8244391;
RA   Poller W., Faber J.-P., Weidinger S., Tief K., Scholz S., Fischer M.,
RA   Olek K., Kirchgesser M., Heidtmann H.-H.;
RT   "A leucine-to-proline substitution causes a defective alpha 1-
RT   antichymotrypsin allele associated with familial obstructive lung
RT   disease.";
RL   Genomics 17:740-743(1993).
RN   [3]
RP   NUCLEOTIDE SEQUENCE [LARGE SCALE MRNA] (ISOFORM 2), AND VARIANT THR-9.
RC   TISSUE=Brain;
RA   Totoki Y., Toyoda A., Takeda T., Sakaki Y., Tanaka A., Yokoyama S.,
RA   Ohara O., Nagase T., Kikuno F.R.;
RL   Submitted (MAR-2005) to the EMBL/GenBank/DDBJ databases.
RN   [4]
RP   NUCLEOTIDE SEQUENCE [LARGE SCALE MRNA] (ISOFORMS 1 AND 3), AND VARIANT
RP   THR-9.
RC   TISSUE=Brain, Liver, and Skin;
RX   MEDLINE=22388257; PubMed=12477932; DOI=10.1073/pnas.242603899;
RA   Strausberg R.L., Feingold E.A., Grouse L.H., Derge J.G.,
RA   Klausner R.D., Collins F.S., Wagner L., Shenmen C.M., Schuler G.D.,
RA   Altschul S.F., Zeeberg B., Buetow K.H., Schaefer C.F., Bhat N.K.,
RA   Hopkins R.F., Jordan H., Moore T., Max S.I., Wang J., Hsieh F.,
RA   Diatchenko L., Marusina K., Farmer A.A., Rubin G.M., Hong L.,
RA   Stapleton M., Soares M.B., Bonaldo M.F., Casavant T.L., Scheetz T.E.,
RA   Brownstein M.J., Usdin T.B., Toshiyuki S., Carninci P., Prange C.,
RA   Raha S.S., Loquellano N.A., Peters G.J., Abramson R.D., Mullahy S.J.,
RA   Bosak S.A., McEwan P.J., McKernan K.J., Malek J.A., Gunaratne P.H.,
RA   Richards S., Worley K.C., Hale S., Garcia A.M., Gay L.J., Hulyk S.W.,
RA   Villalon D.K., Muzny D.M., Sodergren E.J., Lu X., Gibbs R.A.,
RA   Fahey J., Helton E., Ketteman M., Madan A., Rodrigues S., Sanchez A.,
RA   Whiting M., Madan A., Young A.C., Shevchenko Y., Bouffard G.G.,
RA   Blakesley R.W., Touchman J.W., Green E.D., Dickson M.C.,
RA   Rodriguez A.C., Grimwood J., Schmutz J., Myers R.M.,
RA   Butterfield Y.S.N., Krzywinski M.I., Skalska U., Smailus D.E.,
RA   Schnerch A., Schein J.E., Jones S.J.M., Marra M.A.;
RT   "Generation and initial analysis of more than 15,000 full-length human
RT   and mouse cDNA sequences.";
RL   Proc. Natl. Acad. Sci. U.S.A. 99:16899-16903(2002).
RN   [5]
RP   NUCLEOTIDE SEQUENCE [MRNA] OF 1-46 (ISOFORMS 1/2/3).
RX   PubMed=3257719;
RA   Abraham C.R., Selkoe D.J., Potter H.;
RT   "Immunochemical identification of the serine protease inhibitor alpha
RT   1-antichymotrypsin in the brain amyloid deposits of Alzheimer's
RT   disease.";
RL   Cell 52:487-501(1988).
RN   [6]
RP   NUCLEOTIDE SEQUENCE [MRNA] OF 17-423 (ISOFORM 1).
RC   TISSUE=Hippocampus;
RX   MEDLINE=99098931; PubMed=9880565; DOI=10.1074/jbc.274.3.1821;
RA   Hwang S.-R., Steineckert B., Kohn A., Palkovits M., Hook V.Y.H.;
RT   "Molecular studies define the primary structure of alpha1-
RT   antichymotrypsin (ACT) protease inhibitor in Alzheimer's disease
RT   brains. Comparison of act in hippocampus and liver.";
RL   J. Biol. Chem. 274:1821-1827(1999).
RN   [7]
RP   NUCLEOTIDE SEQUENCE [MRNA] OF 22-86 AND 130-423 (ISOFORM 1).
RA   Rubin H.;
RL   Submitted (OCT-1989) to the EMBL/GenBank/DDBJ databases.
RN   [8]
RP   PROTEIN SEQUENCE OF N-TERMINUS.
RX   MEDLINE=89323223; PubMed=2787670; DOI=10.1016/0167-4838(89)90139-8;
RA   Lindmark B., Hilja H., Alan R., Eriksson S.;
RT   "The microheterogeneity of desialylated alpha 1-antichymotrypsin: the
RT   occurrence of two amino-terminal isoforms, one lacking a His-Pro
RT   dipeptide.";
RL   Biochim. Biophys. Acta 997:90-95(1989).
RN   [9]
RP   NUCLEOTIDE SEQUENCE [MRNA] OF 36-45 (ISOFORMS 1/2/3).
RX   MEDLINE=94354957; PubMed=7521171;
RA   Korzus E., Luisetti M., Travis J.;
RT   "Interactions of alpha-1-antichymotrypsin, alpha-1-proteinase
RT   inhibitor, and alpha-2-macroglobulin with the fungal enzyme,
RT   seaprose.";
RL   Biol. Chem. Hoppe-Seyler 375:335-341(1994).
RN   [10]
RP   PROTEIN SEQUENCE OF 41-60 (ISOFORMS 1/2/3).
RX   MEDLINE=83178256; PubMed=6687683;
RA   Morii M., Travis J.;
RT   "Structural alterations in alpha 1-antichymotrypsin from normal and
RT   acute phase human plasma.";
RL   Biochem. Biophys. Res. Commun. 111:438-443(1983).
RN   [11]
RP   NUCLEOTIDE SEQUENCE [MRNA] OF 87-129 (ISOFORMS 1/2).
RX   MEDLINE=90110106; PubMed=2404007;
RA   Rubin H., Wang Z., Nickbarg E.B., McLarney S., Naidoo N.,
RA   Schoenberger O.L., Johnson J.L., Cooperman B.S.;
RT   "Cloning, expression, purification, and biological activity of
RT   recombinant native and variant human alpha 1-antichymotrypsins.";
RL   J. Biol. Chem. 265:1199-1207(1990).
RN   [12]
RP   NUCLEOTIDE SEQUENCE [GENOMIC DNA] OF 205-423 (ISOFORM 1).
RX   MEDLINE=84295637; PubMed=6547997; DOI=10.1038/311175a0;
RA   Hill R.E., Shaw P.H., Boyd P.A., Baumann H., Hastie N.D.;
RT   "Plasma protease inhibitors in mouse and man: divergence within the
RT   reactive centre regions.";
RL   Nature 311:175-177(1984).
RN   [13]
RP   ACTIVE SITE.
RX   MEDLINE=84032476; PubMed=6556193;
RA   Morii M., Travis J.;
RT   "Amino acid sequence at the reactive site of human alpha 1-
RT   antichymotrypsin.";
RL   J. Biol. Chem. 258:12749-12752(1983).
RN   [14]
RP   GLYCOSYLATION AT ASN-93 AND ASN-106.
RX   MEDLINE=22660472; PubMed=12754519; DOI=10.1038/nbt827;
RA   Zhang H., Li X.-J., Martin D.B., Aebersold R.;
RT   "Identification and quantification of N-linked glycoproteins using
RT   hydrazide chemistry, stable isotope labeling and mass spectrometry.";
RL   Nat. Biotechnol. 21:660-666(2003).
RN   [15]
RP   REGION RCL.
RX   PubMed=15638460; DOI=10.1007/s00239-004-2640-9;
RA   Horvath A.J., Forsyth S.L., Coughlin P.B.;
RT   "Expression patterns of murine antichymotrypsin-like genes reflect
RT   evolutionary divergence at the Serpina3 locus.";
RL   J. Mol. Evol. 59:488-497(2004).
RN   [16]
RP   GLYCOSYLATION AT ASN-93.
RX   PubMed=14760718; DOI=10.1002/pmic.200300556;
RA   Bunkenborg J., Pilch B.J., Podtelejnikov A.V., Wisniewski J.R.;
RT   "Screening for N-glycosylated proteins by liquid chromatography mass
RT   spectrometry.";
RL   Proteomics 4:454-465(2004).
RN   [17]
RP   GLYCOSYLATION AT ASN-33; ASN-93; ASN-106; ASN-127; ASN-186 AND
RP   ASN-271.
RX   PubMed=16335952; DOI=10.1021/pr0502065;
RA   Liu T., Qian W.-J., Gritsenko M.A., Camp D.G. II, Monroe M.E.,
RA   Moore R.J., Smith R.D.;
RT   "Human plasma N-glycoproteome analysis by immunoaffinity subtraction,
RT   hydrazide chemistry, and mass spectrometry.";
RL   J. Proteome Res. 4:2070-2080(2005).
RN   [18]
RP   X-RAY CRYSTALLOGRAPHY (2.7 ANGSTROMS) OF 24-423.
RX   MEDLINE=91202538; PubMed=2016749;
RA   Baumann U., Huber R., Bode W., Grosse D., Lesjak M., Laurell C.-B.;
RT   "Crystal structure of cleaved human alpha 1-antichymotrypsin at 2.7-A
RT   resolution and its comparison with other serpins.";
RL   J. Mol. Biol. 218:595-606(1991).
RN   [19]
RP   X-RAY CRYSTALLOGRAPHY (2.95 ANGSTROMS) OF 43-423 OF MUTANTS ARG-370
RP   AND ARG-372.
RX   MEDLINE=96433079; PubMed=8836107;
RA   Lukacs C.M., Zhong J.Q., Plotnick M.I., Rubin H., Cooperman B.S.,
RA   Christianson D.W.;
RT   "Arginine substitutions in the hinge region of antichymotrypsin affect
RT   serpin beta-sheet rearrangement.";
RL   Nat. Struct. Biol. 3:888-893(1996).
RN   [20]
RP   X-RAY CRYSTALLOGRAPHY (2.1 ANGSTROMS) OF 43-423 OF MUTANTS ARG-370;
RP   ARG-372 AND ARG-374.
RX   MEDLINE=98198038; PubMed=9521649; DOI=10.1021/bi972359e;
RA   Lukacs C.M., Rubin H., Christianson D.W.;
RT   "Engineering an anion-binding cavity in antichymotrypsin modulates the
RT   'spring-loaded' serpin-protease interaction.";
RL   Biochemistry 37:3297-3304(1998).
RN   [21]
RP   X-RAY CRYSTALLOGRAPHY (2.27 ANGSTROMS) OF 26-423.
RX   MEDLINE=20087203; PubMed=10618372; DOI=10.1073/pnas.97.1.67;
RA   Gooptu B., Hazes B., Chang W.-S.W., Dafforn T.R., Carrell R.W.,
RA   Read R.J., Lomas D.A.;
RT   "Inactive conformation of the serpin alpha(1)-antichymotrypsin
RT   indicates two-stage insertion of the reactive loop: implications for
RT   inhibitory function and conformational disease.";
RL   Proc. Natl. Acad. Sci. U.S.A. 97:67-72(2000).
RN   [22]
RP   VARIANT ISEHARA-1 VAL-401.
RX   MEDLINE=92316200; PubMed=1618300; DOI=10.1016/0014-5793(92)80590-D;
RA   Tsuda M., Sei Y., Yamamura M., Yamamoto M., Shinohara Y.;
RT   "Detection of a new mutant alpha-1-antichymotrypsin in patients with
RT   occlusive-cerebrovascular disease.";
RL   FEBS Lett. 304:66-68(1992).
RN   [23]
RP   VARIANT BONN-1 ALA-252.
RX   MEDLINE=92292844; PubMed=1351206; DOI=10.1016/0140-6736(92)91301-N;
RA   Poller W., Faber J.-P., Scholz S., Weindinger S., Bartholome K.,
RA   Olek K., Eriksson S.;
RT   "Mis-sense mutation of alpha 1-antichymotrypsin gene associated with
RT   chronic lung disease.";
RL   Lancet 339:1538-1538(1992).
CC   -!- FUNCTION: Although its physiological function is unclear, it can
CC       inhibit neutrophil cathepsin G and mast cell chymase, both of
CC       which can convert angiotensin-1 to the active angiotensin-2.
CC   -!- SUBCELLULAR LOCATION: Secreted protein.
CC   -!- ALTERNATIVE PRODUCTS:
CC       Event=Alternative splicing; Named isoforms=3;
CC       Name=1;
CC         IsoId=P01011-1; Sequence=Displayed;
CC       Name=2;
CC         IsoId=P01011-2; Sequence=VSP_014227, VSP_014228;
CC         Note=No experimental confirmation available;
CC       Name=3;
CC         IsoId=P01011-3; Sequence=VSP_014225, VSP_014226;
CC         Note=No experimental confirmation available;
CC   -!- TISSUE SPECIFICITY: Plasma. Synthesized in the liver. Like the
CC       related alpha-1-antitrypsin, its concentration increases in the
CC       acute phase of inflammation or infection.
CC   -!- DOMAIN: The reactive center loop (RCL) extends out from the body
CC       of the protein and directs binding to the target protease. The
CC       protease cleaves the serpin at the reactive site within the RCL,
CC       establishing a covalent linkage between the carboxyl group of the
CC       serpin reactive site and the serine hydroxyl of the protease. The
CC       resulting inactive serpin-protease complex is highly stable.
CC   -!- DISEASE: Defects in SERPINA3 may be a cause of chronic obstructive
CC       pulmonary disease (COPD) [MIM:107280].
CC   -!- MISCELLANEOUS: Alpha-1-antichymotrypsin can bind DNA.
CC   -!- SIMILARITY: Belongs to the serpin family.
CC   -!- CAUTION: It is uncertain whether Met-1 or Met-4 is the initiator.
CC   -!- CAUTION: Ref.1 sequence differs from that shown due to frameshifts
CC       in positions 101, 106, 111, 117, 123, 129 and 421.
CC   -----------------------------------------------------------------------
CC   Copyrighted by the UniProt Consortium, see http://www.uniprot.org/terms
CC   Distributed under the Creative Commons Attribution-NoDerivs License
CC   -----------------------------------------------------------------------
DR   EMBL; K01500; AAA51543.1; ALT_FRAME; mRNA.
DR   EMBL; X68733; CAA48671.1; ALT_INIT; Genomic_DNA.
DR   EMBL; X68734; CAA48671.1; JOINED; Genomic_DNA.
DR   EMBL; X68735; CAA48671.1; JOINED; Genomic_DNA.
DR   EMBL; X68736; CAA48671.1; JOINED; Genomic_DNA.
DR   EMBL; X68737; CAA48671.1; JOINED; Genomic_DNA.
DR   EMBL; AB209060; BAD92297.1; ALT_INIT; mRNA.
DR   EMBL; BC003559; AAH03559.1; -; mRNA.
DR   EMBL; BC010530; AAH10530.1; -; mRNA.
DR   EMBL; BC013189; AAH13189.1; -; mRNA.
DR   EMBL; BC034554; AAH34554.1; -; mRNA.
DR   EMBL; BC070265; AAH70265.1; -; mRNA.
DR   EMBL; M18906; AAA51559.1; -; mRNA.
DR   EMBL; AF089747; AAD08810.1; -; mRNA.
DR   EMBL; J05176; AAA51560.1; -; mRNA.
DR   EMBL; X00947; CAA25459.1; -; Genomic_DNA.
DR   PIR; A90475; ITHUC.
DR   PIR; S62374; S62374.
DR   UniGene; Hs.534293; -.
DR   UniGene; Hs.612083; -.
DR   UniGene; Hs.620629; -.
DR   PDB; 1AS4; X-ray; A=43-383, B=387-423.
DR   PDB; 1QMN; X-ray; A=26-423.
DR   PDB; 2ACH; X-ray; A=24-383, B=384-423.
DR   PDB; 3CAA; X-ray; A=43-383, B=387-423.
DR   PDB; 4CAA; X-ray; A=43-383, B=387-423.
DR   MEROPS; I04.002; -.
DR   GlycoSuiteDB; P01011; -.
DR   SWISS-2DPAGE; P01011; HUMAN.
DR   Siena-2DPAGE; P01011; -.
DR   Ensembl; ENSG00000196136; Homo sapiens.
DR   H-InvDB; HIX0011931; -.
DR   HGNC; HGNC:16; SERPINA3.
DR   MIM; 107280; gene.
DR   LinkHub; P01011; -.
DR   RZPD-ProtExp; IOH25751; -.
DR   RZPD-ProtExp; IOH4992; -.
DR   GO; GO:0005576; C:extracellular region; NAS.
DR   GO; GO:0005622; C:intracellular; NAS.
DR   GO; GO:0030569; F:chymotrypsin inhibitor activity; NAS.
DR   GO; GO:0003677; F:DNA binding; IC.
DR   GO; GO:0005515; F:protein binding; IPI.
DR   GO; GO:0006954; P:inflammatory response; NAS.
DR   GO; GO:0019216; P:regulation of lipid metabolism; NAS.
DR   InterPro; IPR000215; Prot_inh_serpin.
DR   Pfam; PF00079; Serpin; 1.
DR   SMART; SM00093; SERPIN; 1.
DR   PROSITE; PS00284; SERPIN; 1.
KW   3D-structure; Acute phase; Alternative splicing;
KW   Direct protein sequencing; Disease mutation; Glycoprotein;
KW   Polymorphism; Protease inhibitor; Serine protease inhibitor; Signal.
FT   SIGNAL        1     23
FT   CHAIN        24    423       Alpha-1-antichymotrypsin.
FT                                /FTId=PRO_0000032411.
FT   CHAIN        26    423       Alpha-1-antichymotrypsin His-Pro-less.
FT                                /FTId=PRO_0000032412.
FT   DNA_BIND    235    237
FT   REGION      369    394       RCL.
FT   SITE        383    384       Reactive bond.
FT   CARBOHYD     33     33       N-linked (GlcNAc...).
FT   CARBOHYD     93     93       N-linked (GlcNAc...).
FT   CARBOHYD    106    106       N-linked (GlcNAc...).
FT   CARBOHYD    127    127       N-linked (GlcNAc...).
FT   CARBOHYD    186    186       N-linked (GlcNAc...).
FT   CARBOHYD    271    271       N-linked (GlcNAc...).
FT   VAR_SEQ      64     95       LVLKAPDKNVIFSPLSISTALAFLSLGAHNTT -> SPRWS
FT                                IRLCLMYLRRAQKHLLPQQSKSPSFLH (in isoform
FT                                3).
FT                                /FTId=VSP_014225.
FT   VAR_SEQ      96    423       Missing (in isoform 3).
FT                                /FTId=VSP_014226.
FT   VAR_SEQ     215    216       AK -> ER (in isoform 2).
FT                                /FTId=VSP_014227.
FT   VAR_SEQ     217    423       Missing (in isoform 2).
FT                                /FTId=VSP_014228.
FT   VARIANT       9      9       A -> T (in dbSNP:4934).
FT                                /FTId=VAR_006973.
FT   VARIANT      78     78       L -> P (in COPD; Bochum-1;
FT                                dbSNP:1800463).
FT                                /FTId=VAR_006974.
FT   VARIANT     167    167       A -> G.
FT                                /FTId=VAR_006975.
FT   VARIANT     252    252       P -> A (in COPD; Bonn-1; dbSNP:17473).
FT                                /FTId=VAR_006976.
FT   VARIANT     401    401       M -> V (associated with occlusive-
FT                                cerebrovascular disease; Isehara-1).
FT                                /FTId=VAR_006977.
FT   VARIANT     407    407       D -> G (in dbSNP:10956).
FT                                /FTId=VAR_011742.
FT   CONFLICT     55     55       D -> S (in Ref. 10).
FT   CONFLICT     69     69       P -> L (in Ref. 1).
FT   CONFLICT    101    101       K -> R (in Ref. 3).
FT   CONFLICT    199    199       L -> P (in Ref. 1).
FT   CONFLICT    267    267       K -> R (in Ref. 4; AAH34554).
FT   CONFLICT    361    363       AVL -> VVS (in Ref. 1).
FT   HELIX        49     67
FT   TURN         69     70
FT   STRAND       71     71
FT   STRAND       73     75
FT   HELIX        77     88
FT   TURN         89     90
FT   HELIX        93    102
FT   TURN        103    104
FT   TURN        107    109
FT   STRAND      110    110
FT   HELIX       112    126
FT   STRAND      128    129
FT   STRAND      131    132
FT   STRAND      134    144
FT   TURN        145    146
FT   HELIX       151    161
FT   STRAND      164    168
FT   TURN        170    171
FT   STRAND      172    172
FT   HELIX       173    187
FT   TURN        188    190
FT   STRAND      191    191
FT   STRAND      195    195
FT   TURN        201    202
FT   STRAND      203    219
FT   HELIX       223    225
FT   STRAND      227    234
FT   TURN        235    236
FT   STRAND      237    256
FT   TURN        257    260
FT   STRAND      261    279
FT   TURN        281    282
FT   HELIX       284    289
FT   TURN        290    290
FT   HELIX       293    302
FT   STRAND      304    314
FT   STRAND      316    323
FT   HELIX       325    330
FT   TURN        331    332
FT   HELIX       335    337
FT   STRAND      338    338
FT   TURN        339    340
FT   HELIX       344    347
FT   STRAND      348    350
FT   STRAND      352    365
FT   STRAND      367    382
FT   STRAND      391    394
FT   STRAND      397    397
FT   STRAND      399    405
FT   TURN        406    407
FT   STRAND      408    409
FT   STRAND      412    418
FT   TURN        420    421
SQ   SEQUENCE   423 AA;  47651 MW;  B002F946C86A8951 CRC64;
     MERMLPLLAL GLLAAGFCPA VLCHPNSPLD EENLTQENQD RGTHVDLGLA SANVDFAFSL
     YKQLVLKAPD KNVIFSPLSI STALAFLSLG AHNTTLTEIL KGLKFNLTET SEAEIHQSFQ
     HLLRTLNQSS DELQLSMGNA MFVKEQLSLL DRFTEDAKRL YGSEAFATDF QDSAAAKKLI
     NDYVKNGTRG KITDLIKDLD SQTMMVLVNY IFFKAKWEMP FDPQDTHQSR FYLSKKKWVM
     VPMMSLHHLT IPYFRDEELS CTVVELKYTG NASALFILPD QDKMEEVEAM LLPETLKRWR
     DSLEFREIGE LYLPKFSISR DYNLNDILLQ LGIEEAFTSK ADLSGITGAR NLAVSQVVHK
     AVLDVFEEGT EASAATAVKI TLLSALVETR TIVRFNRPFL MIIVPTDTQN IFFMSKVTNP
     KQA
//
