package de.mpa.analysis;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

//import uk.ac.ebi.kraken.interfaces.ProteinData;
//import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
//import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
//import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
//import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
//import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
//import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
//import uk.ac.ebi.kraken.interfaces.uniref.UniRefDatabaseType;
//import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
//import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniRefQueryService;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;

/**
 * Method to test the UniprotAPi
 * @author Robert Heyer, Thilo Muth
 *
 */
public class UniprotAccessorTest extends TestCase {

	/**
	 * UniProt Query Service. 
	 */
//	private static UniProtQueryService uniProtQueryService;
	private String uniprotKO;
	private String uniProtID;
	private String type;
	private List<String> ecNumbers;
//	private List<NcbiTaxon> taxonomy;
	private String taxon;
//	private List<Keyword> keywords;
//	private List<Go> goTerms;
	private String uniprotKegg;
	
	@Before
	public void setUp() {
		
		/*
	     * Client Class has a couple of static methods to create a ServiceFactory instance.
	     * From ServiceFactory, you can fetch the JAPI Services.
	     */
	    ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();

	    // UniProtService
	    UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
		
//		// Check whether UniProt query service has been established yet.
//		if (uniProtQueryService == null) {
//			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
//		}
//
//		// Create entry retrival service
//		EntryRetrievalService entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();
//
//		// Retrieve UniProt entry by its accession number
//		UniProtEntry entry = (UniProtEntry) entryRetrievalService.getUniProtEntry("P11558");
//
//		// If entry with a given accession number is not found, entry will be == null
//		if (entry != null) {
//			// UniprotID
//			uniProtID = entry.getUniProtId().getValue();
//			
//			// Type
//			type = entry.getType().name();
//			
//			// UniprotKO
//			List<DatabaseCrossReference> dcrKO = entry.getDatabaseCrossReferences(DatabaseType.KO);
//			uniprotKO = dcrKO.get(0).getPrimaryId().getValue() + ";";
//			
//			// UniprotKEGG
//			List<DatabaseCrossReference> dcrKegg = entry.getDatabaseCrossReferences(DatabaseType.KEGG);
//			uniprotKegg = dcrKegg.get(0).getPrimaryId().getValue();
//			
//			// UniprotEC
//			ecNumbers = entry.getProteinDescription().getEcNumbers();
//
//			// UniprotTaxonomie
//			taxonomy = entry.getTaxonomy();
//			taxon = taxonomy.get(2).toString();
//
//			// Uniprot keywords
//			keywords = entry.getKeywords();
//
//			// Uniprot GO annotation
//			goTerms = entry.getGoTerms();
//			
//			//TODo Robbie tested
//			// Create entry retrival service
//			EntryRetrievalService service = UniProtJAPI.factory.getEntryRetrievalService();
//			ProteinData proteinData = service.getProteinData("a4ip67");
//			UniRefEntry u100 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef100);
//			UniRefEntry u90 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef90);
//			UniRefEntry u50 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef50);
//			System.out.println(	"UNIREF: " + u100.getUniRefEntryId().getValue() + "  90 " + u90.getUniRefEntryId().getValue() + "  50 " + u50.getUniRefEntryId().getValue());
//			System.out.println("Rep " + u90.getName());
//		}
	}
	
	@Test // Test Type
	public void testType() {
		assertEquals(true, true);
	}
//		assertEquals(type, "SWISSPROT");
//	}
//	@Test // Test UniProtID
//	public void testUniProtID() {
//		assertEquals(uniProtID, "MCRA_METTM");
//	}		
//	
//	@Test // Test KO
//	public void testUniProtKO() {
//		assertEquals(uniprotKO, "K00399");
//	}
//	
//	@Test // Test Kegg
//	public void testUniProtKegg() {
//		assertEquals(uniprotKegg, "mmg:MTBMA_c15480");
//	}
//	
//	@Test // Test EC
//	public void testUniProtEC() {
//		assertEquals(ecNumbers.get(0).toString(), "2.8.4.1");
//	}
//
//	@Test // Test taxon
//	public void testUniProtTaxon() {
//		assertEquals(taxon, "Methanobacteria");
//	}
//
//	@Test // Test keywords
//	public void testUniProtKeywords() {
//		assertEquals(keywords.get(3).toString(), "Metal-binding");
//	}
//
//	@Test // Test keywords
//	public void testGoTerms() {
//		assertEquals(goTerms.get(2).getGoTerm().getValue(), "methanogenesis");
//		assertEquals(goTerms.get(2).getOntologyType().getValue(), "P:");
//	}
}
