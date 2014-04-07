package de.mpa.analysis;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.main.Parameters;

public class KeggMapsTest extends TestCase{

	private Map<String, Character> keggPathwayMap;
	private Map<String, Character> keggTaxonomyMap;

	@Override
	public void setUp() throws Exception {
		// Pathways
		keggPathwayMap = Parameters.getInstance().getKeggPathwayMap();
		
		// Taxonomy
		keggTaxonomyMap = Parameters.getInstance().getKeggTaxonomyMap();
	}
	
	
	@Test
	public void testKeggPathwayMap() {
		assertEquals('A', keggTaxonomyMap.get("Eukaryotes (155)").charValue());
		assertEquals('B', keggTaxonomyMap.get("Animals (52)").charValue());
		assertEquals('C', keggTaxonomyMap.get("Vertebrates (20)").charValue());
		assertEquals('D', keggTaxonomyMap.get("Reptiles (1)").charValue());
		assertEquals('E', keggTaxonomyMap.get("rno  Rattus norvegicus (rat)").charValue());
	}
	
	@Test 
	public void testKeggTaxonmyMap() {
		assertEquals('A', keggPathwayMap.get("Metabolism").charValue());
		assertEquals('B', keggPathwayMap.get("Carbohydrate Metabolism").charValue());
		assertEquals('C', keggPathwayMap.get("04060  Cytokine-cytokine receptor interaction").charValue());
	}
}
