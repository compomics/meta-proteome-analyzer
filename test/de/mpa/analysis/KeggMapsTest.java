package de.mpa.analysis;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.io.parser.kegg.KEGGMap;
import de.mpa.io.parser.kegg.KEGGNode;
import de.mpa.io.parser.kegg.KEGGOrthologyNode;
import de.mpa.io.parser.kegg.KEGGReader;

/**
 * Test class for validating parsed KEGG pathway mappings.
 * 
 * @author A. Behne
 */
public class KeggMapsTest extends TestCase {

	@Test 
	public void testKeggPathwayMap() {
		KEGGMap koMap = new KEGGMap(
				KEGGReader.readKEGGTree(new KEGGOrthologyNode("root"), "src/conf/ko00001.keg"));
		
		List<KEGGNode> nodes = koMap.get("K00844");
		
		assertEquals(12, nodes.size());
		assertEquals("Glycolysis / Gluconeogenesis",
				((KEGGOrthologyNode) nodes.get(1).getParent()).getDescription());
	}
	
}
