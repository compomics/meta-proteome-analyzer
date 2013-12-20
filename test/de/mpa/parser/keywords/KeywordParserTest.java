package de.mpa.parser.keywords;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.KeywordOntology;

/**
 * Test class for UniProt keyword parsing.
 * @author R. Heyer	 and A. Behne
 */
public class KeywordParserTest extends TestCase {

	@Test
	public void testKeywordParsing() throws Exception {
		
		// Gets the ontolgy map.
		Map<String, KeywordOntology> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		
		// Test
		assertEquals(KeywordOntology.LIGAND, ontologyMap.get("2Fe-2S"));
		assertEquals(KeywordOntology.PTM, ontologyMap.get("Zymogen"));
		assertEquals(KeywordOntology.BIOLOGICAL_PROCESS, ontologyMap.get("Xylose metabolism"));
		
		assertEquals(1135, ontologyMap.size()); // 1145 - 10 main categories
	}
}
