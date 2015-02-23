package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;

import de.mpa.db.DBManager;
import de.mpa.io.MascotGenericFile;

public class ExtractUnidentifiedSpectraTest extends TestCase {
	
	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	public void testIdentifiedSpectraFromExperiment() throws SQLException {
		List<XTandemhit> xtandemHits = XTandemhit.getHitsFromExperimentID(579, conn);
		List<Omssahit> omssaHits = Omssahit.getHitsFromExperimentID(579, conn);
		
		List<SearchHit> hits = new ArrayList<SearchHit>();
		hits.addAll(xtandemHits);
		hits.addAll(omssaHits);
		
		Set<Long> identifiedSpectra = new HashSet<Long>();
		for (SearchHit hit : hits) {
			identifiedSpectra.add(hit.getFk_searchspectrumid());
		}
		
		Set<Long> allSearchspectrumIds = new HashSet<Long>();
		for (Searchspectrum searchspectrum : Searchspectrum.findFromExperimentID(579, conn)) {
			allSearchspectrumIds.add(searchspectrum.getSearchspectrumid());
			
		}
		allSearchspectrumIds.removeAll(identifiedSpectra);
		
		for (Long searchSpectrumId : allSearchspectrumIds) {
			long spectrumId = Searchspectrum.findFromSearchSpectrumID(searchSpectrumId, conn).getFk_spectrumid();
			MascotGenericFile mgf = Spectrum.getSpectrumFileFromIdAndTitle(spectrumId, "FILE", conn);
			if (mgf != null) {
				System.out.println(mgf.getTitle());
			}
			
		}
		
	}

}
