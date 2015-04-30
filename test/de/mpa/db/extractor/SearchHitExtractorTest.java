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
		ExperimentAccessor experiment = ExperimentAccessor.findExperimentByID(280L, conn);
		List<Searchspectrum> searchspectra = Searchspectrum.findFromExperimentID(experiment.getExperimentid(), conn);
		
		List<SearchHit> hits = SearchHitExtractor.findSearchHitsFromExperimentID(experiment.getExperimentid(), conn);
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(experiment.getTitle() + ".csv")));
		String SEP = "\t"; 
		Set<String> spectrumTitles = new HashSet<String>();
		for (SearchHit searchHit : hits) {
			if (searchHit.getQvalue().doubleValue() < 0.01) {
				Searchspectrum searchspectrum = Searchspectrum.findFromSearchSpectrumID(searchHit.getFk_searchspectrumid(), conn);
				
				Spectrum spectrum = Spectrum.findFromSpectrumID(searchspectrum.getFk_spectrumid(), conn);
				spectrumTitles.add(spectrum.getTitle());
				PeptideAccessor peptideAccessor = PeptideAccessor.findFromID(searchHit.getFk_peptideid(), conn);
				bWriter.append(spectrum.getTitle() + SEP + peptideAccessor.getSequence() + SEP + searchHit.getType() + SEP + searchHit.getScore() + SEP + searchHit.getQvalue());
				bWriter.newLine();
			}
		}
		bWriter.flush();
		bWriter.close();
		System.out.println("Identified/Searched Spectra: " + spectrumTitles.size() + "/" + searchspectra.size());
	}

}
