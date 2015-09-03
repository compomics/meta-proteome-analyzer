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
	public boolean proteinEmPAI = false;
	public boolean proteinNSAF = true;
	public boolean proteinSequence = false;
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
	public boolean psmPEP;
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
		peptideNumber = true;
		peptideProteinAccessions = true;
		peptideSequence = true;
		peptideTaxGroup = true;
		uniquePeptidesOnly = true;
		sharedPeptidesOnly = true;
		peptideProtCount = false;
		peptideSpecCount = true;
		peptideTaxRank = true;
		peptideTaxId = true;
		
		psmNumber = true;
		psmProteinAccession = true;
		psmPeptideSequence = true;
		psmSpectrumTitle = true;
		psmCharge = true;
		psmSearchEngine = true;
		psmQValue = true;
		psmPEP = true;
		psmScore = true;
		
		metaproteinNumber = true;
		metaproteinAccessions = true;
		metaproteinDescription = true;
		metaproteinTaxonomy = true;
		metaprotUniRef100 =true;
		metaprotUniRef90 =true;
		metaprotUniRef50 =true;
		metaprotKO =true;
		metaprotEC =true;
		metaproteinSpecCount = true;
		metaproteinPepCount = true;
		metaproteinProteins = true;
		metaproteinPeptides = true;

		taxonomyUnclassified = true;
		taxonomySuperKingdom = true;
		taxonomyKingdom = true;
		taxonomyPhylum = true;
		taxonomyOrder = true;
		taxonomyClass = true;
		taxonomyFamily = true;
		taxonomyGenus = true;
		taxonomySpecies = true;
		taxonomySubspecies = true;
		taxonomySpecificPeptides = true;
		taxonomySpecificSpecCount = true;
		taxonomyKronaSpecCount= true;
	}


	/**
	 * If is not existing, create a new instance.
	 * @return The exportFields.
	 */
	public static ExportFields getInstance() {
		if (exportFields == null) {
			exportFields = new ExportFields();
		}
			return exportFields;
	}
}
