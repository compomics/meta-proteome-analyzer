package de.mpa.db.extractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.SpecSearchHit;
import de.mpa.io.MascotGenericFile;

public class SpectrumSpectrumMatchTest extends TestCase {
	
	private SpectrumExtractor extractor;
	private Connection conn;
	private SpecSimResult specSimResult;

	@Before
	public void setUp() {
		try {
			DBManager manager = DBManager.getInstance();
			conn = manager.getConnection();
			extractor = new SpectrumExtractor(conn);
			specSimResult = SpecSearchHit.getAnnotations(3L, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSpectrumSpectrumMatchDisplay() {
		List<ProteinHit> hits = new ArrayList<ProteinHit>(specSimResult.getProteinHits().values());
		SpectrumSpectrumMatch match = (SpectrumSpectrumMatch) hits.get(0).getSinglePeptideHit().getSingleSpectrumMatch();
		
		try {
//			MascotGenericFile searchspectrum = extractor.getSpectrumFromSearchSpectrumID(match.getSpectrumId());
//			MascotGenericFile libspectrum = extractor.getSpectrumFromLibSpectrumID(match.getLibspectrumID());
			MascotGenericFile searchspectrum = extractor.getSpectrumFromSearchSpectrumID(1187L);
			MascotGenericFile libspectrum = extractor.getSpectrumFromLibSpectrumID(178L);
			
//			JFrame frame = new JFrame("SSM Viewer");
//			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//			MultiPlotPanel plot = new MultiPlotPanel();
//			plot.setFirstSpectrum(searchspectrum);
//			plot.setSecondSpectrum(libspectrum);
//			plot.setPreferredSize(new Dimension(640, 480));
//			frame.getContentPane().add(plot);
//			frame.pack();
//			frame.setVisible(true);
			while (true);
			
//			assertEquals(1.0, match.getSimilarity(), 0.01);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
