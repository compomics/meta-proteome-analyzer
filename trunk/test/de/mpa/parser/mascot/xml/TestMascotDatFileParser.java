package de.mpa.parser.mascot.xml;

import java.util.List;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Test for the parsing of the mascot dat file based on http://code.google.com/p/mascotdatfile/
 * Based on mascot.dat-File 20407.dat
 * @author F. Kohrs and R. Heyer
 */
public class TestMascotDatFileParser {

	static DbSearchResult dbSearchResult;
	 static List<ProteinHit> proteinHitList;

	@BeforeClass
	public static void setUpClass() {
		// Start parser
		Client.getInstance().retrieveDbSearchResult("TestMascotParser", "Dat_file_reader_Test", 254); // Score 50 Few proteins mgf/ F20407.dat
		 dbSearchResult = Client.getInstance().getDbSearchResult();
		 proteinHitList = dbSearchResult.getProteinHitList();
	}
	
	@Test
	public void testProteinLevel(){
		TestCase.assertEquals(8, proteinHitList.size());	
		TestCase.assertEquals("P69910", dbSearchResult.getProteinHit("P69910").getAccession());
		TestCase.assertEquals("DCEB_ECOLI Glutamate decarboxylase beta OS=Escherichia coli (strain K12) GN=gadB PE=1 SV=1", dbSearchResult.getProteinHit("P69910").getDescription());
		TestCase.assertEquals(null, dbSearchResult.getProteinHit("P0A6N2"));
		TestCase.assertEquals("P0ABQ0", dbSearchResult.getProteinHit("P0ABQ0").getAccession());
		TestCase.assertEquals("COABC_ECOLI Coenzyme A biosynthesis bifunctional protein CoaBC OS=Escherichia coli (strain K12) GN=coaBC PE=1 SV=2", dbSearchResult.getProteinHit("P0ABQ0").getDescription());
	}
	
	@Test
	public void testPeptideLevel(){
		TestCase.assertEquals("LQGIAQQNSFK", dbSearchResult.getProteinHit("P69910").getPeptideHitList().get(0).getSequence());
		TestCase.assertEquals(1, dbSearchResult.getProteinHit("P69910").getPeptideHitList().size());
		TestCase.assertEquals("AAATQHNLEVLASR", dbSearchResult.getProteinHit("P0ABQ0").getPeptideHitList().get(0).getSequence());
		TestCase.assertEquals(1, dbSearchResult.getProteinHit("P0ABQ0").getPeptideHitList().size());
	}
	
	@Test
	public void testPsmLevel(){
		PeptideHit peptideHit = dbSearchResult.getProteinHit("P69910").getPeptideHitList().get(0);
		PeptideSpectrumMatch psm = (PeptideSpectrumMatch)peptideHit.getSpectrumMatches().get(0);
		TestCase.assertEquals(2, psm.getCharge());
	}
	
}
