package de.mpa.analysis;

import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.main.Parameters;

public class KeggMapsTest extends TestCase{

	private LinkedHashMap<String, String> keggPathwayList;
	private LinkedHashMap<String, String> keggTaxonomie;

	@Override
	public void setUp() throws Exception {
		// Pathways
		String pathwayPath = "/" + Parameters.getInstance().getConfPath() + "/keggPathways.txt";
		keggPathwayList = KeggMaps.getKeggPathwayList(pathwayPath);
		
		// Taxonomy
		String taxonomyPath = "/" + Parameters.getInstance().getConfPath() + "/keggTaxonomyMap.txt";
		keggTaxonomie = KeggMaps.getKeggTaxonomie(taxonomyPath);
	}
	
	
	@Test 
	public void testKeggPathwayMap(){
		assertEquals("A", keggTaxonomie.get("<html><i><b>Eukaryotes</b> (155)</i></html>").toString());	
		assertEquals("B", keggTaxonomie.get("<html><b>Animals</b> (52)</html>").toString());	
		assertEquals("C", keggTaxonomie.get("Vertebrates (20)").toString());	
		assertEquals("D", keggTaxonomie.get("Reptiles (1)").toString());	
		assertEquals("E", keggTaxonomie.get("rno  Rattus norvegicus (rat)").toString());	
	}
	
	@Test 
	public void testKeggTaxonmyMap(){
		assertEquals("A", keggPathwayList.get("<html><i><b>Metabolism</b></i></html>").toString());	
		assertEquals("B", keggPathwayList.get("Carbohydrate Metabolism").toString());	
		assertEquals("C", keggPathwayList.get("04060  Cytokine-cytokine receptor interaction").toString());		
	}
}
