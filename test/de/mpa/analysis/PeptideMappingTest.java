package de.mpa.analysis;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Test;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.protein_inference.PeptideProteinMapping;
import com.compomics.util.experiment.identification.protein_inference.fm_index.FMIndex;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import com.compomics.util.preferences.PeptideVariantsPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;

public class PeptideMappingTest {
	
	@Test
	public void test() throws ClassNotFoundException, IOException, InterruptedException, SQLException {
		
		// Initialize variables
	    File fastaFile = new File("test/de/mpa/resources/test.fasta");
        PtmSettings ptmSettings = null;
        
        // PeptideVariantsPreferences
        PeptideVariantsPreferences peptideVariantsPreferences = null;
        peptideVariantsPreferences = PeptideVariantsPreferences.getNoVariantPreferences();

        // SequenceMatchingPreferences
        SequenceMatchingPreferences sequenceMatchingPreferences = null;
        sequenceMatchingPreferences = new SequenceMatchingPreferences();
        sequenceMatchingPreferences.setSequenceMatchingType(SequenceMatchingPreferences.MatchingType.indistiguishableAminoAcids);
        sequenceMatchingPreferences.setLimitX(0.25);
		
		// Parse the FASTA file
	    SequenceFactory sequenceFactory = com.compomics.util.experiment.identification.protein_sequences.SequenceFactory.getInstance();
		sequenceFactory.loadFastaFile(fastaFile);
		
		// Index the protein sequences
		FMIndex fmIndex = new FMIndex(null, false, ptmSettings, peptideVariantsPreferences);

	    // Map a peptide sequence to the protein sequences
		ArrayList<PeptideProteinMapping> proteinMapping = fmIndex.getProteinMapping("VVMLNIK", sequenceMatchingPreferences);
		
		System.out.println(proteinMapping.size());
	}
}
