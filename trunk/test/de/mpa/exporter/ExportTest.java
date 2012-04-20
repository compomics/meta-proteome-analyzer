package de.mpa.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.io.ResultExporter;


public class ExportTest extends TestCase{

	private String	filePath;
	private DbSearchResult expResult;
	private String path;
	
	//TODO: not running yet for windoof
	@Before
	public void setUp() {
		path = getClass().getClassLoader().getResource("Export").getPath();
		path = path.replaceAll("%20", " ");
		
		// Create protein
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch();
		PeptideSpectrumMatch psm2 = new PeptideSpectrumMatch();
		psm.setSpectrumId(1);
		psm2.setSpectrumId(2);
		PeptideHit peptideHit = new PeptideHit("ALGDLR", psm);
		PeptideHit peptideHit2 = new PeptideHit("KLLRDR", psm2);
		ProteinHit proteinHit = new ProteinHit("A0G921",
				"Thilo Do this yourself", "", peptideHit);
		proteinHit.addPeptideHit(peptideHit2);
		expResult = new DbSearchResult("ProjectX", "Experiment Blah", "uniprot.fasta");
		expResult.addProtein(proteinHit);
		List<String> searchEngines = new ArrayList<String>();
		searchEngines.add("Mascot");
		expResult.setSearchEngines(searchEngines);
		expResult.setSearchDate(new Date());
	}
	
	@Test
	public void testExport() {
		try {
			filePath = path + File.separator + "testProtExp.csv";
			ResultExporter.exportProteins(filePath, expResult);
			filePath = path + File.separator  + "testPeptideExp.csv";
			ResultExporter.exportPeptides(filePath, expResult);
			filePath = path + File.separator  + "testPSMExp.csv";
			ResultExporter.exportPSMs(filePath, expResult);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
