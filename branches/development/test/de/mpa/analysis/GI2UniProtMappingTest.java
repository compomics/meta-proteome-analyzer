package de.mpa.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Class to test the mapping from GI number to UniProt via web service
 * @author R. Heyer
 */
public class GI2UniProtMappingTest extends TestCase {

	List<String> giList;
	
	@Before
	public void setUp(){
		giList = new ArrayList<String>();
		giList.add("500551516"); // Not in NCBI
		giList.add("81941549");
		giList.add("47060116");
		giList.add("49237298");
		giList.add("221726920");
		giList.add("254801230");
		giList.add("222082241");
	}
	
	@Test
	public void testMapping() throws IOException {
		Map<String, String> gi2up = UniProtGiMapper.retrieveGiToUniProtMapping(giList);
		
		for (Entry<String, String> entry : gi2up.entrySet()) {
			System.out.println("gi: " + entry.getKey() + " up: " + entry.getValue());
		}

		assertEquals("Q6GZX4", gi2up.get("81941549"));
		assertEquals("Q6GZX4", gi2up.get("47060116"));
		assertEquals("Q6GZX4", gi2up.get("49237298"));
		// Last entries in TestFile
		assertEquals("B9JJB7", gi2up.get("221726920"));
		assertEquals("B9JJB7", gi2up.get("254801230"));
		assertEquals("B9JJB7", gi2up.get("222082241"));
		
	}
}
