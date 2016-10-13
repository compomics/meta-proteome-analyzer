package de.mpa.analysis;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;

/**
 * Test the UniProt JAPI
 * @author R. Heyer
 *
 */
public class UniProtJapiTest extends TestCase {

	/** 
	 * The resultmap for the uniprot queries
	 */
	private TreeMap<String, UniProtEntryMPA> multipleUniProtMap = null;
	
	/** 
	 * The resultmap for a single uniprot queries
	 */
	private TreeMap<String, UniProtEntryMPA> singleUniProtMap = null;
	
	/** 
	 * The resultmap for a single uniprot queries
	 */
	private TreeMap<String, UniProtEntryMPA> singleUniProtWrongMap;
	
	// Test single accession list
	private Set<String> accessionListSingleCorrect = new TreeSet<String>();
	
	// Test single accession list
	private Set<String> accessionListSingleWrong = new TreeSet<String>();

	
	// Test multiple accession list
	private Set<String> accessionListMultiple = new TreeSet<String>();

	
//	B5YNV2
	
	@Before
	public void setUp() throws ServiceException {
		
		// Fill test lists O30318 B5YNV2
		accessionListSingleCorrect.add("O30318");
		
		accessionListSingleWrong.add("P0C7V5");
		//String accession = "A0AJ21Z";
		//String accession = "P20806";
		accessionListMultiple.add("B2ICR0");
		accessionListMultiple.add("B2U355");
		accessionListMultiple.add("B8E3Q6");
		accessionListMultiple.add("C1AG22");
		accessionListMultiple.add("O55718");
		accessionListMultiple.add("Q196Y0");
		accessionListMultiple.add("Q2TA17");
		accessionListMultiple.add("Q5PH83");
		accessionListMultiple.add("Q6GZR4");
		accessionListMultiple.add("P0C7V5"); // Missing entry

		UniProtUtilities uniProt = new UniProtUtilities();
		// flag for the uniRef queries
		boolean uniRefs = true;
		
		try {
		multipleUniProtMap = uniProt.fetchUniProtEntriesByAccessions(new ArrayList<String>(accessionListMultiple), uniRefs);
		singleUniProtMap = uniProt.fetchUniProtEntriesByAccessions(new ArrayList<String>(accessionListSingleCorrect), uniRefs);
		singleUniProtWrongMap = uniProt.fetchUniProtEntriesByAccessions(new ArrayList<String>(accessionListSingleWrong), uniRefs);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	@Before
//	public void setUp() throws ServiceException {
//	// NOTHING SPECIAL
//	}
	
	@Test // Test the batch
	public void testbatch() {
		// 9 of 10 should posses a uniProt entry
		assertEquals(9, multipleUniProtMap.size());
	}
	
	@Test // Test a single correct entry for entry "O30318"
	public void testSingleKorrektEntry(){
		for (String key : singleUniProtMap.keySet()) {
			UniProtEntryMPA uniProtEntryMPA = singleUniProtMap.get(key);
			assertEquals("O30318", uniProtEntryMPA.getAccession());
			assertEquals(224325L, uniProtEntryMPA.getTaxid());
			assertEquals(true, uniProtEntryMPA.getEcnumbers().isEmpty());
			assertEquals(true, uniProtEntryMPA.getKonumbers().isEmpty());
			assertEquals("Cell membrane", uniProtEntryMPA.getKeywords().get(0));
			assertEquals("Complete proteome", uniProtEntryMPA.getKeywords().get(1));
			assertEquals(6, uniProtEntryMPA.getKeywords().size());

			assertEquals("UniRef100_O30318", uniProtEntryMPA.getUniRefMPA().getUniRef100());
			assertEquals("UniRef90_O30318", uniProtEntryMPA.getUniRefMPA().getUniRef90());
			assertEquals("UniRef50_O30318", uniProtEntryMPA.getUniRefMPA().getUniRef50());
		}
	}
	
	@Test // Test a single wrong entry for entry "O30318"
	public void testSingleWrongEntry(){
		assertEquals(0, singleUniProtWrongMap.size());
	}
	@Test
	public void testdefault() {
		assertEquals(true, true);
	}
	
}