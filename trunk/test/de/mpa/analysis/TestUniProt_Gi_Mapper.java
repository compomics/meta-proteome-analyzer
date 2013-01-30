package de.mpa.analysis;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import junit.framework.TestListener;

import org.junit.Before;
import org.junit.Test;

import de.mpa.analysis.UniProtGiMapper;

/**
 * Class to test the mapping from GI number to UniProt via web service
 * @author R. Heyer
 */
public class TestUniProt_Gi_Mapper extends TestCase {

	ArrayList<String> testList;
	
	@Before
	public void setUp(){
		testList = new ArrayList<String>();
		testList.add("500551516"); // Not in NCBI
		testList.add(null);
		testList.add("81941549");
		testList.add("47060116");
		testList.add("49237298");
		testList.add("221726920");
		testList.add("254801230");
		testList.add("222082241");
		
	}
	
	@Test
	public void testMapping(){
		Map<String, String> mapping = UniProtGiMapper.getMapping(testList);

		assertEquals(mapping.get("81941549"),"Q6GZX4");
		assertEquals(mapping.get("47060116"),"Q6GZX4");
		assertEquals(mapping.get("49237298"),"Q6GZX4");
		// Last entries in TestFile
		assertEquals(mapping.get("221726920"),"B9JJB7");
		assertEquals(mapping.get("254801230"),"B9JJB7");
		assertEquals(mapping.get("222082241"),"B9JJB7");
		
		assertEquals(mapping.get("500551516"), null);
	}
}
