<table>
<blockquote><tr>
<blockquote><td width='70%'>
</blockquote></blockquote><ul><li><a href='#General.md'>General</a>
</li><li><a href='#Overview.md'>Overview</a>
</li><li><a href='#Basic_View.md'>Basic View</a>
</li><li><a href='#Meta-Protein_View.md'>Meta-Protein View</a>
</li><li><a href='#Taxonomy_View.md'>Taxonomy View</a>
</li><li><a href='#Enzyme_View.md'>Enzyme View</a>
</li><li><a href='#Pathway_View.md'>Pathway View</a>
<blockquote></td>
</blockquote><blockquote></tr>
</table></blockquote></li></ul>


---

## General ##

The following will introduce and explain the various possibilities MPA offers to view processed data and to further evaluate derived results.

**NOTE:** Currently MPA is mainly designed for evaluating UniProt data as it uses meta-information provided by its web service. Nevertheless MPA is capable to handle results from other databases as well, but data interpretation will partly be hampered by lack of taxonomic information, for example.

Results derived by either performing a DB search or by parsing MASCOT DAT files both will safe spectra, peptide to spectrum matches (PSM) and protein hits, besides some other information, to the MPA database. To inspect these results the `Fetch Results from DB` button has to be pressed. Using the small green cog wheel will open an option panel where parameters for the later assembly of meta-proteins can be set . `Fetch Results from File` button allows to open exported MPA result files.

[Go to top of page](#General.md)


---

## Overview ##

The Overview panel is divided into four sections. Summary panel offers a view on very general statistics of the complete experimental dataset. Chart View in contrast enables first data interpretation by showing a pie chart. There are two entities to configure that influence the assembly of this pie chart: quantification and classification criterion. For the first there is the choice between numbers of spectra, peptides, proteins and so-called meta-proteins. The classification criterion can be changed using the Select Chart Type-button at top right. Right-click on the pie chart will open a dialog to export the data used to set up `Chart View` according to quantification and classification selected. It is possible to pick single pieces of the pie chart by left-click. Doing this, the `Chart Details` panel below will fill automatically. Finally the `Heat Map` allows a more abstract inspection of the results. By this, a very lucid view on the data is provided allowing to find patterns and distributions of certain hits. All three dimensions of this feature can be customized by left-clicking their axis titles and choosing from the popup menu. To apply changes to the `Heat Map` it has to be refreshed.

[Go to top of page](#General.md)


---

## Basic View ##

A more detailed table view is depicted following `Database Search Results`. In the upper table all identified proteins are shown, several columns provide further information. Protein table columns can be customized. Deselecting of proteins using will automatically put that entry at the bottom of the table. For UniProt data protein accessions will appear in blue underlined letters. If computer is connected to internet, following this link will open a web browser that directly shows selected protein on UniProt website. Alternatively, other external web resources can be  addressed by clicking the `World` icon at right hand of each protein hits row. A list will pop up that opens the respective web page for the chosen protein hit.

The following web services are available:
  * _UniProt_
  * _NCBI_
  * _KEGG_
  * _BLAST_
  * _PFAM_
  * _InterPro_
  * _PDB_
  * _Pride_
  * _Reactome_
  * _QuickGO_
  * _eggNOG_

Search function using ctrl + f on keyboard is implemented onto this table, too. In addition, each column contains a function to filter entries. Peptides that are matched to a selected protein hit are listed in a peptide table containing additional information like peptide sequence, spectral count and the amount of proteins this peptide also has been assigned to. Multiple protein assignments of single peptides are shown by light brown colored row. For those peptides that are only assigned to one protein, peptide sequence is shown bold. Additionally, using the button will switch peptide table view to protein sequence coverage view. Finally selecting a single peptide in the peptide table will fill the peptide to spectrum match table. It contains the peptide ions charge and information about respective search engines. Selecting an entry from PSM table will open its spectrum in spectrum viewer panel. MPA offers a spectrum annotation tool, delta masses are shown when mouse over dashed blue lines. Basic view described in this section can be switched into five different modes that allow another perspective on processed data. Therefore on top right a drop down menu is integrated to switch to these tables. For some applications an additional taxonomic filter might be useful which can be applied via the top right `Filter` button.

[Go to top of page](#General.md)


---

## Meta-Protein View ##

Basic View can be switched to a table of meta-proteins. For those all respective protein information and label free quantifications are calculated and displayed. Additionally, this view allows expanding single meta-proteins to reveal all proteins that have been used for assembly. Proteins are pooled to meta-proteins if they share at least one peptide.

[Go to top of page](#General.md)


---

## Taxonomy View ##

All proteins identified in the experiment are mapped into a phylogenetic tree. This view is more reasonable to investigate taxonomic assignments.

[Go to top of page](#General.md)


---

## Enzyme View ##

This view will map all proteins of a respective dataset according to Enzyme Commission Number (E.C.). This classification is based on the chemical reaction an enzyme catalyzes regardless of its taxonomic source. Tables `Accession` column includes a link on each E.C. number that directly opens ExPASy Bioinformatics Source Portal in a browser window showing a list of that respective E.C. family.

[Go to top of page](#General.md)


---

## Pathway View ##

Identified proteins are sorted according to the respective functional in-situ pathway. This classification, as well as E.C. classification, is independent of its taxonomic source. Tables `Accession` column shows the name of each super and sub pathways, these are linked to KEGG Pathway Maps. Following the link will open the pathway map in a new browser window, directly highlighting all proteins identified and assigned to it by red color.

[Go to top of page](#General.md)