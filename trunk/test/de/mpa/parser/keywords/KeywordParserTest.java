package de.mpa.parser.keywords;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.Keyword;
import de.mpa.analysis.UniProtUtilities.KeywordCategory;

/**
 * Test class for UniProt keyword parsing.
 * @author R. Heyer	 and A. Behne
 */
public class KeywordParserTest extends TestCase {

	@Test
	public void testKeywordParsing() throws Exception {
		
		// Gets the ontolgy map.
		Map<String, Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		
		// Test
		assertEquals(KeywordCategory.LIGAND.getKeyword(), ontologyMap.get("2Fe-2S"));
		assertEquals(KeywordCategory.PTM.getKeyword(), ontologyMap.get("Zymogen"));
		assertEquals(KeywordCategory.BIOLOGICAL_PROCESS.getKeyword(), ontologyMap.get("Xylose metabolism"));
		
		assertEquals(1135, ontologyMap.size()); // 1145 - 10 main categories
	}
}
