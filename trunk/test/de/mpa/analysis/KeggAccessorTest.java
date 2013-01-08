package de.mpa.analysis;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class KeggAccessorTest extends TestCase {

	// FIXME: possibly obsolete class, investigate!
	
	private KeggAccessor accessor;
	
	private String[] genes = {
			"bta:280717",
			"ecc:c1365", "ecc:c1922", "ecc:c2869", "ecc:c3880", "ecc:c4328", "ecc:c4463",
			"ece:Z1734", "ece:Z2187", "ece:Z2215", "ece:Z4930", "ece:Z5919",
			"ecg:E2348C_2828",
			"eco:b1493", "eco:b2323", "eco:b3517",
			"ecp:ECP_3429",
			"ecv:APECO1_3114",
			"ecw:EcE24377A_3808",
			"ecw:EcE24377A_4519",
			"sep:SE0792",
			};

	@Before
	public void setUp() throws Exception {
		accessor = KeggAccessor.getInstance();
	}
	
	/**
	 * <a href="http://www.genome.jp/dbget-bin/www_bget?ko00250">ko00250</a>
	 */
	@Test
	public void testKoRetrievalByPathway() throws IOException {
		String pathway = "path:map00250";
		Set<String> actualKos = 
			new HashSet<String>(Arrays.asList(accessor.getKeggPort().get_kos_by_pathway(pathway)));
		
		List<String> expectedKos = Arrays.asList(new String[] { "ko:K00139", "ko:K03334" });
		assertEquals(57, actualKos.size());
		assertTrue(actualKos.containsAll(expectedKos));
	}
	
	/**
	 * <a href="http://www.genome.jp/dbget-bin/www_bget?ko:K01963">K01963</a><br>
	 * <a href="http://www.genome.jp/dbget-bin/www_bget?ko:K01964">K01964</a>
	 */
	@Test
	public void testPathwayRetrievalByKo() throws IOException {
		String[] koList = { "ko:K01963", "ko:K01964" };
		String organism = "map";
		Set<String> actualPathways = 
			new HashSet<String>(Arrays.asList(accessor.getKeggPort().get_pathways_by_kos(koList, organism)));
		
		List<String> expectedPathways =
			Arrays.asList(new String[] { "path:map00061", "path:map00640", "path:map00720" });
		assertEquals(3, actualPathways.size());
		assertTrue(actualPathways.containsAll(expectedPathways));
	}
	
	@Test
	public void testPathwayRetrievalByGene() throws IOException {
		String[] geneList;
		Set<String> actualPathways;
		List<String> expectedPathways;
		
		// all eco genes
		geneList = Arrays.copyOfRange(genes, 13, 15);
		actualPathways = new HashSet<String>(Arrays.asList(accessor.getKeggPort().get_pathways_by_genes(geneList)));
		expectedPathways = Arrays.asList(new String[] { "path:eco01100" });
		
		assertEquals(1, actualPathways.size());
		assertTrue(actualPathways.containsAll(expectedPathways));
		
		// any eco genes
		actualPathways = new HashSet<String>();
		for (String gene : geneList) {
			actualPathways.addAll(Arrays.asList(accessor.getKeggPort().get_pathways_by_genes(new String[] { gene })));
		}
		expectedPathways = Arrays.asList(new String[] { "path:eco00061", "path:eco00250", "path:eco00410", 
				"path:eco00430", "path:eco00650", "path:eco01100", "path:eco01110" });
		
		assertEquals(7, actualPathways.size());
		assertTrue(actualPathways.containsAll(expectedPathways));
	}
	
//	@Test
//	public void testPathwayDumping() throws IOException {
//		// Warning: dumping takes several minutes to finish! Uncomment only if absolutely necessary :)
//		accessor.dumpRemoteKeggPathways();
//		
//		// Glycolysis/Gluconeogenesis pathway
//		short pathwayID = 10;
//		
//		short koToDump = accessor.getKOsByPathway(pathwayID).get(0);
//		
//		accessor.readDumpedKeggPathways();
//		
//		short dumpedKo = accessor.getKOsByPathway(pathwayID).get(0);
//		
//		System.out.println(accessor.getECsByPathway(pathwayID).get(0)[0]);
//		
//		assertEquals(koToDump, dumpedKo);
//	}
	
}
