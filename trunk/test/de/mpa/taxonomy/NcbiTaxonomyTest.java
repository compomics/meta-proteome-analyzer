package de.mpa.taxonomy;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test the dumb of the ncbi taxonomy tree.
 * @author A. Behne and R. Heyer
 */
public class NcbiTaxonomyTest extends TestCase {
	
//	@Test uncomment just onces necessary for new taxonomy version
//	public void testNcbiTaxonomyDumping() throws IOException {
//		// dump to file
//		NcbiTaxonomy.dumpTaxonomies("Z:\\bpt\\bptprot\\MetaProteomeAnalyzer\\databases\\NCBI Taxonomy\\taxdmp\\" );
//		
//		// read dumped data by instancing NcbiTaxonomy object
//		NcbiTaxonomy ncbiTaxonomy = NcbiTaxonomy.getInstance();
//		
//		assertEquals(1, ncbiTaxonomy.getRootNode().getTaxId());
//	}
	
	@Test
	public void testCommonAncestorRetrieval() {
		TaxonNode commonAncestor = NcbiTaxonomy.getInstance().getCommonAncestor(269797, 79929);
		
		assertEquals(28890, commonAncestor.getTaxId());
	}
}