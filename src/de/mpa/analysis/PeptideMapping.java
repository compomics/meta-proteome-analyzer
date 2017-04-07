package de.mpa.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.protein_inference.PeptideProteinMapping;
import com.compomics.util.experiment.identification.protein_inference.fm_index.FMIndex;
import com.compomics.util.preferences.PeptideVariantsPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;

public class PeptideMapping {
	
	/**
	 * Returns the proteins for a distinct peptide sequence using FM Index structure.
	 * @param peptide Peptide sequence
	 * @return List of protein accessions.
	 */
	public static Set<String> retrieveProteinsForPeptide(String peptide) {
		Set<String> accessions = new HashSet<String>();
		
        PtmSettings ptmSettings = null;
        
        // PeptideVariantsPreferences
        PeptideVariantsPreferences peptideVariantsPreferences = null;
        peptideVariantsPreferences = PeptideVariantsPreferences.getNoVariantPreferences();

        // SequenceMatchingPreferences
        SequenceMatchingPreferences sequenceMatchingPreferences = null;
        sequenceMatchingPreferences = new SequenceMatchingPreferences();
        sequenceMatchingPreferences.setSequenceMatchingType(SequenceMatchingPreferences.MatchingType.indistiguishableAminoAcids);
        sequenceMatchingPreferences.setLimitX(0.25);
		
		// Index the protein sequences
		FMIndex fmIndex = new FMIndex(null, false, ptmSettings, peptideVariantsPreferences);

	    // Map a peptide sequence to the protein sequences
		ArrayList<PeptideProteinMapping> proteinMapping = fmIndex.getProteinMapping(peptide, sequenceMatchingPreferences);
		
		for (PeptideProteinMapping mapping : proteinMapping) {
			accessions.add(mapping.getProteinAccession());
		}
		return accessions; 
	}

}



