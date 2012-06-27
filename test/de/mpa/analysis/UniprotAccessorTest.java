package de.mpa.analysis;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

public class UniprotAccessorTest extends TestCase {
	
	private EntryRetrievalService entryRetrievalService;

	@Override
	public void setUp() throws Exception {
		 //Create entry retrieval service
        entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();
	}
	
	@Test
	public void testEntryRetrieval(){
		
        //Retrieve UniProt entry by its accession number
        UniProtEntry entry = (UniProtEntry) entryRetrievalService.getUniProtEntry("H8I8C4");

        System.out.println("entry = " + entry);

        //If entry with a given accession number is not found, entry will be equal null
        if (entry != null) {
            System.out.println("entry = " + entry.getUniProtId().getValue());
            System.out.println("entry recommended names = " + entry.getProteinDescription().getRecommendedName().getFields());
            System.out.println("entry ec numbers = " + entry.getProteinDescription().getEcNumbers());
            List<NcbiTaxon> taxonomy = entry.getTaxonomy();
            for (NcbiTaxon taxon : taxonomy) {
				System.out.println(taxon.getValue());
			}

        }
	}
}
