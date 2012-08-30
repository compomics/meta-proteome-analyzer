package de.mpa.analysis;

import java.rmi.RemoteException;

import junit.framework.TestCase;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.junit.Test;

public class KeggAccessorTest extends TestCase {

	private KEGGPortType serv;
	
	private String[] genes = { "ecv:APECO1_3114", "ecc:c2869", "ecp:ECP_3429", "ecw:EcE24377A_4519", 
			"eco:b1493", "ecc:c4463", "ecw:EcE24377A_3808", "ece:Z1734", "bta:280717", "ecc:c3880",
			"sep:SE0792", "ece:Z4930", "ecc:c1365", "ecc:c4328", "ecg:E2348C_2828", "eco:b3517",
			"ece:Z5919", "ece:Z2215", "ece:Z2187", "eco:b2323", "ecc:c1922" };

	@Override
	public void setUp() throws Exception {
		KEGGLocator  locator = new KEGGLocator();
        serv = locator.getKEGGPort();
	}
	
	
	@Test // Get Kos by pathway
	public void testGetPathwaysbyMap() throws RemoteException {
		String[] map = serv.get_kos_by_pathway("path:map00250");
		assertEquals(map[1], "ko:K00139");}
	
	@Test //pathway by KO
	public void testPathwayRetrievalByKO() throws RemoteException {
		String[] koList = {"ko:K00647"};
		String[] pathways = serv.get_pathways_by_kos(koList, "eco");
		assertEquals("path:eco00061", pathways[0]);}
	
	@Test // Pathway by Gene
	public void testPathwayRetrievalByGene() throws RemoteException {
		String[] geneList = {"eco:b2323"};
		String[] pathways = serv.get_pathways_by_genes(geneList);
		assertEquals("path:eco00061", pathways[0]);
	}
	
	@Test // Pathway by Gene
	public void testPathwayRetrievalByGene2() throws RemoteException {
		String[] geneList = {"mmg:MTBMA_c15480"};
		String[] pathways = serv.get_pathways_by_genes(geneList);
		assertEquals("path:mmg01120", pathways[2]);
	}
	
	@Test // Pathway by Gene
	public void testPathwayRetrievalByGene3() throws RemoteException {
		String[] geneList = {"ecc:c2869"};
		String[] pathways = serv.get_pathways_by_genes(geneList);
	}
	
}
