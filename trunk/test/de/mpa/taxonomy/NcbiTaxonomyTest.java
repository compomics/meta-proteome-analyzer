package de.mpa.taxonomy;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests for NCBI taxonomy-related classes and methods.
 * @author R. Heyer, A. Behne
 */
public class NcbiTaxonomyTest extends TestCase {
	
	@Test
	public void testNcbiTaxonomyDumping() {
		// dump to file
		NcbiTaxonomy.dumpTaxonomies("Z:\\bpt\\bptprot\\MetaProteomeAnalyzer\\databases\\NCBI Taxonomy\\taxdmp\\" );
	
		//		NcbiTaxonomy.dumpTaxonomies("/data/bpt/bptprot/MetaProteomeAnalyzer/databases/NCBI Taxonomy/taxdmp/");
		
		// read dumped data by instancing NcbiTaxonomy object
		NcbiTaxonomy ncbiTaxonomy = NcbiTaxonomy.getInstance();
		
		assertEquals(1, ncbiTaxonomy.getRootNode().getTaxId());
	}
	
	@Test
	public void testCommonAncestorRetrieval() {
		TaxonNode commonAncestor = NcbiTaxonomy.getInstance().getCommonAncestor(269797, 79929);
		
		assertEquals(28890, commonAncestor.getTaxId());
	}
}