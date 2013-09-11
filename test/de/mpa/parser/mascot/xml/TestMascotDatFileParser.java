package de.mpa.parser.mascot.xml;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
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
		Client.getInstance().retrieveDbSearchResult("TestMascotParser", "TestMascotParser4", 182);
		 dbSearchResult = Client.getInstance().getDbSearchResult();
		 proteinHitList = dbSearchResult.getProteinHitList();
	}
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testSpectrum(){
		TestCase.assertEquals(26, proteinHitList.size());	
		}
	
	@Test
	public void testProteinLevel(){
		TestCase.assertEquals("7331218", dbSearchResult.getProteinHit("7331218").getAccession());
		TestCase.assertEquals("keratin 1 [Homo sapiens]", dbSearchResult.getProteinHit("7331218").getDescription());
		TestCase.assertEquals(null, dbSearchResult.getProteinHit("136429"));
		TestCase.assertEquals(null, dbSearchResult.getProteinHit("28317"));
		TestCase.assertEquals("345308743", dbSearchResult.getProteinHit("345308743").getAccession());
		TestCase.assertEquals("no description", dbSearchResult.getProteinHit("345308743").getDescription());
	}
	
	@Test
	public void testPeptideLevel(){
		TestCase.assertEquals("YEELQLTAGR", dbSearchResult.getProteinHit("345308743").getPeptideHitList().get(0).getSequence());
		TestCase.assertEquals(1, dbSearchResult.getProteinHit("7331218").getPeptideHitList().size());
		TestCase.assertEquals(377, dbSearchResult.getProteinHit("7331218").getPeptideHitList().get(0).getStart());
		TestCase.assertEquals(386, dbSearchResult.getProteinHit("7331218").getPeptideHitList().get(0).getEnd());
		TestCase.assertEquals(1, dbSearchResult.getProteinHit("345308743").getPeptideHitList().size());
		//TODO wrong from parser
//		TestCase.assertEquals(294, dbSearchResult.getProteinHit("345308743").getPeptideHitList().get(0).getStart());
//		TestCase.assertEquals(303, dbSearchResult.getProteinHit("345308743").getPeptideHitList().get(0).getEnd());
	}
	
	@Test
	public void testPsmLevel(){
		PeptideHit peptideHit = dbSearchResult.getProteinHit("345308743").getPeptideHitList().get(0);
		PeptideSpectrumMatch psm = (PeptideSpectrumMatch)peptideHit.getSpectrumMatches().get(0);
		
		
//		System.out.println(psm.toString());
		TestCase.assertEquals(2, psm.getCharge());
	}
	
}
