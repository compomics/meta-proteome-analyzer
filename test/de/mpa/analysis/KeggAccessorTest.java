package de.mpa.analysis;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.junit.Test;

public class KeggAccessorTest extends TestCase {

	private KEGGPortType serv;

	@Override
	public void setUp() throws Exception {
		KEGGLocator  locator = new KEGGLocator();
        serv = locator.getKEGGPort();
	}
	
	@Test
	public void testPathwayRetrievalByKO() throws RemoteException {
		String[] koList = {
				"ko:K00647"
		};
		String[] pathways = serv.get_pathways_by_kos(koList, "eco");
		assertEquals("path:eco00061", pathways[0]);
	}
	
	@Test
	public void testPathwayRetrievalByGene() throws RemoteException {
		String[] geneList = {
				"eco:b2323"
		};
		String[] pathways = serv.get_pathways_by_genes(geneList);
		assertEquals("path:eco00061", pathways[0]);
	}
	
}
