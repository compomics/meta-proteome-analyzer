package de.mpa.analysis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

public class UniProtUtilitiesTest extends TestCase {
	
	private UniProtService uniprotService;

	@Before
	public void setUp() {
		  /*
	     * Client Class has a couple of static methods to create a ServiceFactory instance.
	     * From ServiceFactory, you can fetch the JAPI Services.
	     */
	    ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();

	    // UniProtService
	    uniprotService = serviceFactoryInstance.getUniProtQueryService();
	    uniprotService.start();
	}
	
	
	
	@Test 
	public void testFetchSingleProteinEntry() throws ServiceException {
		System.out.println("Test");
		UniProtEntry entry = uniprotService.getEntry("P10144");
		assertEquals("GRAB_HUMAN", entry.getUniProtId().getValue());
		assertEquals("Swiss-Prot", entry.getType().getValue());
	}
	
	public void testFetchMultipleProteinEntries() throws ServiceException {
		Set<String> set = new HashSet<>(); 
		set.add("P10144");
		set.add("P02769");
		
		Query query = UniProtQueryBuilder.accessions(set);
		
		QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
        int count = 0;
        while (entries.hasNext()) {
            count++;
            UniProtEntry entry = entries.next();
            System.out.println(entry.getUniProtId().getValue());
        }
        assertEquals(2,  count);
        
	}
}
