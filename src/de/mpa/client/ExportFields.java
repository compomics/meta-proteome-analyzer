package de.mpa.client;

/**
 * Class for the fields of the export Dialog
 * @author R. Heyer
 *
 */
public class ExportFields {

	/**
	 * The singleton instance of the exportFields
	 */
	private static ExportFields exportFields;

	/**
	 * Protein export.
	 */
	public boolean proteinNumber = true;
	public boolean proteinAccession = true;
	public boolean proteinDescription = true;
	public boolean proteinTaxonomy = true;
	public boolean proteinSeqCoverage = true;
	public boolean proteinMolWeight = true;
	public boolean proteinPi = true;
	public boolean proteinPepCount = true;
	public boolean proteinSpecCount = true;
	public boolean proteinEmPAI;
	public boolean proteinNSAF = true;
	public boolean proteinSequence;
	public boolean proteinPeptides = true;
	
	/**
	 * Peptide export.
	 */
	public boolean peptideTaxGroup;
	public boolean peptideProteinAccessions;
	public boolean peptideSequence;
	public boolean peptideProtCount;
	public boolean peptideSpecCount;
	public boolean peptideNumber;
	public boolean sharedPeptidesOnly;
	public boolean uniquePeptidesOnly;
	public boolean peptideTaxId;
	public boolean peptideTaxRank;
	
	/**
	 * PSM export
	 */
	public boolean psmNumber;
	public boolean psmProteinAccession;
	public boolean psmPeptideSequence;
	public boolean psmSpectrumTitle;
	public boolean psmCharge;
	public boolean psmSearchEngine;
	public boolean psmQValue;
	public boolean psmScore;
	
	/**
	 * Identified spectra export
	 */
	public boolean spectrumNumber = true;
	public boolean spectrumID = true;
	public boolean spectrumTitle = true;
	public boolean spectrumPeptides = true;
	public boolean spectrumAccessions = true;
	
	/**
	 * MetaProteinExport.
	 */
	public boolean metaproteinNumber;
	public boolean metaproteinAccessions;
	public boolean metaproteinDescription;
	public boolean metaproteinTaxonomy;
	public boolean metaprotUniRef100;
	public boolean metaprotUniRef90;
	public boolean metaprotUniRef50;
	public boolean metaprotKO;
	public boolean metaprotEC;
	public boolean metaproteinSeqCoverage;
	public boolean metaproteinAId;
	public boolean metaproteinNSAF;
	public boolean metaproteinEmPAI;
	public boolean metaproteinSpecCount;
	public boolean metaproteinPepCount;
	public boolean metaproteinProteins;
	public boolean metaproteinPeptides;
	
	/**
	 * Protein Taxonomy Export.
	 */
	public boolean taxonomyUnclassified;
	public boolean taxonomySuperKingdom;
	public boolean taxonomyKingdom;
	public boolean taxonomyPhylum;
	public boolean taxonomyOrder;
	public boolean taxonomyClass;
	public boolean taxonomyFamily;
	public boolean taxonomyGenus;
	public boolean taxonomySpecies;
	public boolean taxonomySubspecies;
	public boolean taxonomySpecificPeptides;
	public boolean taxonomySpecificSpecCount;
	public boolean taxonomyKronaSpecCount;
	
	/**
	 * Meta-protein taxonomy export.
	 */
	public boolean metaproteinTaxonomySpecificPeptides = true;
	public boolean metaproteinTaxonomyKronaSpecCount = true;
	
	/**
	 * Constructor to create default export fields.
	 */
	private ExportFields(){
        this.peptideNumber = true;
        this.peptideProteinAccessions = true;
        this.peptideSequence = true;
        this.peptideTaxGroup = true;
        this.uniquePeptidesOnly = true;
        this.sharedPeptidesOnly = true;
        this.peptideProtCount = false;
        this.peptideSpecCount = true;
        this.peptideTaxRank = true;
        this.peptideTaxId = true;

        this.psmNumber = true;
        this.psmProteinAccession = true;
        this.psmPeptideSequence = true;
        this.psmSpectrumTitle = true;
        this.psmCharge = true;
        this.psmSearchEngine = true;
        this.psmQValue = true;
        this.psmScore = true;

        this.metaproteinNumber = true;
        this.metaproteinAccessions = true;
        this.metaproteinDescription = true;
        this.metaproteinTaxonomy = true;
        this.metaprotUniRef100 =true;
        this.metaprotUniRef90 =true;
        this.metaprotUniRef50 =true;
        this.metaprotKO =true;
        this.metaprotEC =true;
        this.metaproteinSpecCount = true;
        this.metaproteinPepCount = true;
        this.metaproteinProteins = true;
        this.metaproteinPeptides = true;

        this.taxonomyUnclassified = true;
        this.taxonomySuperKingdom = true;
        this.taxonomyKingdom = true;
        this.taxonomyPhylum = true;
        this.taxonomyOrder = true;
        this.taxonomyClass = true;
        this.taxonomyFamily = true;
        this.taxonomyGenus = true;
        this.taxonomySpecies = true;
        this.taxonomySubspecies = true;
        this.taxonomySpecificPeptides = true;
        this.taxonomySpecificSpecCount = true;
        this.taxonomyKronaSpecCount = true;
	}


	/**
	 * If is not existing, create a new instance.
	 * @return The exportFields.
	 */
	public static ExportFields getInstance() {
		if (ExportFields.exportFields == null) {
            ExportFields.exportFields = new ExportFields();
		}
			return ExportFields.exportFields;
	}
}
