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
	public boolean  proteinNumber; 
	public boolean  proteinAccession;
	public boolean  proteinDescription;
	public boolean  proteinTaxonomy;
	public boolean  proteinSeqCoverage;
	public boolean  proteinMolWeight;
	public boolean  proteinPi;
	public boolean  proteinPepCount;
	public boolean  proteinSpecCount;
	public boolean  proteinEmPAI;
	public boolean  proteinNSAF;
	public boolean  proteinSequence;
	public boolean  proteinPeptides;
	
	/**
	 * Peptide export.
	 */
	public boolean	 peptideTaxGroup;
	public boolean peptideProteinAccessions;
	public boolean peptideSequence;
	public boolean	 peptideProtCount;
	public boolean	 peptideSpecCount;
	public boolean	 peptideNumber;
	public boolean	 sharedPeptidesOnly;
	public boolean	 uniquePeptidesOnly;
	public boolean	 peptideTaxId;
	public boolean	 peptideTaxRank;
	
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
	 * MetaProteinExport.
	 */
	public boolean metaproteinNumber;
	public boolean metaproteinAccessions;
	public boolean metaproteinDescription;
	public boolean metaproteinTaxonomy;
	public boolean metaproteinSeqCoverage;
	public boolean metaproteinAId;
	public boolean metaproteinNSAF;
	public boolean metaproteinEmPAI;
	public boolean metaproteinSpecCount;
	public boolean metaproteinPepCount;
	public boolean metaproteinPeptides;
	
	/**
	 * Taxonomy Export.
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
	public boolean taxonomySpecificPeptides;
	public boolean taxonomySpecificSpecCount;
	
//	public boolean taxonomyUnspecificPeptides;
//	public boolean taxonomyUnspecificSpecCount;
	
	
	/**
	 * Constructor to create default export fields.
	 */
	private ExportFields(){
		
		/**
		 * Protein export
		 */
		proteinNumber 			=   true;
		proteinAccession 		=	true;
		proteinDescription 		= 	true;
		proteinTaxonomy 			=	true;
		proteinSeqCoverage		= 	true;
		proteinPepCount			= 	true;
		proteinNSAF				= 	true;
		proteinEmPAI 			= 	false;
		proteinPi				= 	true;
		proteinMolWeight 		= 	true;
		proteinSequence			=  	false;
		proteinPeptides			= 	true;
		
		/**
		 * Peptide export.
		 */
		peptideNumber			= true;
		peptideProteinAccessions= true;
		peptideSequence			= true;
		peptideTaxGroup			= true;
		uniquePeptidesOnly		= true;
		sharedPeptidesOnly		= true;
		peptideProtCount		= false;
		peptideSpecCount		= true;
		peptideTaxRank			= true;
		peptideTaxId			= true;
		
		/**
		 * PSM export
		 */
		psmNumber				= true;
		psmProteinAccession		= true;
		psmPeptideSequence		= true;
		psmSpectrumTitle		= true;
		psmCharge				= true;
		psmSearchEngine			= true;
		psmQValue				= true;
		psmScore				= true;
		
		/**
		 * MetaProteinExport
		 */
		metaproteinNumber		= true;
		metaproteinAccessions	= true;
		metaproteinDescription	= true;
		metaproteinTaxonomy		= true;
		metaproteinSeqCoverage	= true;
		metaproteinAId			= false;
		metaproteinNSAF			= true;
		metaproteinEmPAI		= true;
		metaproteinSpecCount	= true;
		metaproteinPepCount		= true;
		metaproteinPeptides		= true;
		

		/**
		 * Taxonomy Export.
		 */
		taxonomyUnclassified	= true;
		taxonomySuperKingdom	= true;
		taxonomyKingdom			= true;
		taxonomyPhylum			= true;
		taxonomyOrder			= true;
		taxonomyClass			= true;
		taxonomyFamily			= true;
		taxonomyGenus			= true;
		taxonomySpecies			= true;
		taxonomySpecificPeptides	= true;
		taxonomySpecificSpecCount	= true;
		
//		taxonomyUnspecificPeptides	= true; 
//		taxonomyUnspecificSpecCount	= true; 
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
