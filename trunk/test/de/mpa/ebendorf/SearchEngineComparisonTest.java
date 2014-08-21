package de.mpa.ebendorf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpa.db.DBManager;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;

public class SearchEngineComparisonTest {
	
	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testExtractSearchEngineResults() throws IOException, ClassNotFoundException, SQLException  {
		Map<Long, String> map = new HashMap<Long, String>();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("/scratch/results/Ebendorf1/Ebendorf_Crux_Proteins_FDR_5.txt"));
		map.put(574L, "Ebendorf_1_All");
		
		Set<Entry<Long, String>> entrySet = map.entrySet();
		for (Entry<Long, String> entry : entrySet) {
			List<SearchHit> hits = new ArrayList<SearchHit>();
//			hits.addAll(Omssahit.getHitsFromExperimentID(entry.getKey(), conn));
//			hits.addAll(XTandemhit.getHitsFromExperimentID(entry.getKey(), conn));
//			hits.addAll(Inspecthit.getHitsFromExperimentID(entry.getKey(), conn));
			hits.addAll(Cruxhit.getHitsFromExperimentID(entry.getKey(), conn));
			Set<Long> spectra = new HashSet<Long>();
	
			Set<String> peptides = new HashSet<String>();
			Set<String> proteins = new HashSet<String>();
			for (SearchHit searchHit : hits) {
				if (searchHit.getQvalue().doubleValue() < 0.05) {
					Searchspectrum searchspectrum = Searchspectrum.findFromSearchSpectrumID(searchHit.getFk_searchspectrumid(), conn);
//					String title = Spectrum.findFromSpectrumID(searchspectrum.getFk_spectrumid(), conn).getTitle();
					spectra.add(searchspectrum.getFk_spectrumid());
					PeptideAccessor peptide = PeptideAccessor.findFromID(searchHit.getFk_peptideid(), conn);
					peptides.add(peptide.getSequence());
					ProteinAccessor protein = ProteinAccessor.findFromID(searchHit.getFk_proteinid(), conn);
					proteins.add(protein.getAccession());
				}
			}
			for (String protein : proteins) {
				writer.write(protein);
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		}
	}

}
