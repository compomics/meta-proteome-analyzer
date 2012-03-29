package de.mpa.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.ExperimentResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.PeptideSpectrumMatch;
import de.mpa.client.model.ProteinHit;
import de.mpa.io.ResultExporter;


public class ProteinExportTest extends TestCase{

	private String	filePath;
	private ExperimentResult expResult;
	
	@Before
	public void setUp() {
		String path = getClass().getClassLoader().getResource("Export").getPath();
		filePath = path + "/" + "testProtExp.csv";

		// Create protein
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch();
		PeptideSpectrumMatch psm2 = new PeptideSpectrumMatch();
		psm.setSpectrumId(1);
		psm2.setSpectrumId(2);
		PeptideHit peptideHit = new PeptideHit("ALGDLR", psm);
		PeptideHit peptideHit2 = new PeptideHit("KLLRDR", psm2);
		ProteinHit proteinHit = new ProteinHit("A0G921",
				"Thilo Do this yourself", peptideHit);
		proteinHit.addPeptideHit(peptideHit2);
		expResult = new ExperimentResult("ProjectX", "Experiment Blah", "uniprot.fasta");
		expResult.addProtein(proteinHit);
		List<String> searchEngines = new ArrayList<String>();
		searchEngines.add("Mascot");
		expResult.setSearchEngines(searchEngines);
		expResult.setSearchDate(new Date());
	}
	
	@Test
	public void testProteinExport() {
		try {
			ResultExporter.exportProteins(filePath, expResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
