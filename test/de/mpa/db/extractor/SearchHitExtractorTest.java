package de.mpa.db.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.mpa.db.DBManager;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.extractor.SearchHitExtractor;

public class SearchHitExtractorTest {
	
	/**
	 * DB Connection.
	 */
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}

	@Test
	public void exportExperiments() throws Exception {
		double fdr = 0.01;
		long exp_id = 340L;
		
		ExperimentAccessor experiment = ExperimentAccessor.findExperimentByID(exp_id, conn);
		List<Searchspectrum> searchspectra = Searchspectrum.findFromExperimentID(experiment.getExperimentid(), conn);
		
		List<SearchHit> hits = SearchHitExtractor.findSearchHitsFromExperimentID(experiment.getExperimentid(), conn);
		System.out.println("Found a total of " + hits.size() + " PSMs for this experiment.");
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + "//script dump//" + experiment.getTitle() + ".csv")));
		String SEP = "\t"; 
		Set<String> spectrumTitles = new HashSet<String>();
		Set<String> peptideSequences = new HashSet<String>();
		Set<String> proteinAccessions = new HashSet<String>();
		for (SearchHit searchHit : hits) {
			if (searchHit.getQvalue().doubleValue() < fdr ) {
				Searchspectrum searchspectrum = Searchspectrum.findFromSearchSpectrumID(searchHit.getFk_searchspectrumid(), conn);
				Spectrum spectrum = Spectrum.findFromSpectrumID(searchspectrum.getFk_spectrumid(), conn);
				spectrumTitles.add(spectrum.getTitle());
				
				// peptide sequence
				PeptideAccessor peptideAccessor = PeptideAccessor.findFromID(searchHit.getFk_peptideid(), conn);
				String sequence = peptideAccessor.getSequence();
				peptideSequences.add(sequence);
				
				// protein accession
				ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(searchHit.getFk_proteinid(), conn);
				String accession = proteinAccessor.getAccession();
				proteinAccessions.add(accession);
				
				bWriter.append(spectrum.getTitle() + SEP + sequence + SEP + accession + SEP + searchHit.getType() + SEP + searchHit.getScore() + SEP + searchHit.getQvalue());
				bWriter.newLine();
			}
		}
		String comment = "Identified/Searched Spectra: " + spectrumTitles.size() + "/" + searchspectra.size() 
				+ " Identified Peptides: " + peptideSequences.size() 
				+ " Identified Proteins: " + proteinAccessions.size()
				+ " at FDR " + fdr;
		bWriter.append("# " + comment);
		System.out.println(comment);
		bWriter.flush();
		bWriter.close();
		
//		for (String string : proteinAccessions) {
//			System.out.println(string);
//		}
	}

}
