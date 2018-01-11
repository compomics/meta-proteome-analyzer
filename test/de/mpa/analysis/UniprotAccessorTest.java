package de.mpa.analysis;

import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

/**
 * Method to test the UniprotAPi
 * @author Robert Heyer, Thilo Muth
 *
 */
public class UniprotAccessorTest extends TestCase {

	/**
	 * UniProt Query Service. 
	 */
	static UniProtService uniProtQueryService;
	private UniRefService uniRefQueryService;
	private String uniprotKO;
	private String uniProtID;
	private String type;
	private List<String> ecNumbers;
	private List<NcbiTaxon> taxonomy;
	private String taxon;
	private List<Keyword> keywords;
	private List<Go> goTerms;
	private String uniprotKegg;
	
	@Before
	public void setUp() throws ServiceException {
		
		// factory
		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
		// uniprot-service
		UniprotAccessorTest.uniProtQueryService = serviceFactoryInstance.getUniProtQueryService();
		UniprotAccessorTest.uniProtQueryService.start();
		// unirefqueryservice
		this.uniRefQueryService = serviceFactoryInstance.getUniRefQueryService();
		this.uniRefQueryService.start();
		
		HashSet<String> accessions = new HashSet<String>();
		accessions.add("P11558");
		Query query = UniProtQueryBuilder.accessions(accessions);	
		QueryResult<UniProtEntry> entryIterator = UniprotAccessorTest.uniProtQueryService.getEntries(query);
		UniProtEntry entry = entryIterator.next();

		if (entry != null) {
			// UniprotID
			uniProtID = entry.getUniProtId().getValue();
			
			// Type
			type = entry.getType().name();
			
			// UniprotKO
			List<DatabaseCrossReference> dcrKO = entry.getDatabaseCrossReferences(DatabaseType.KO);
			uniprotKO = dcrKO.get(0).getPrimaryId().getValue() + ";";
			
			// UniprotKEGG
			List<DatabaseCrossReference> dcrKegg = entry.getDatabaseCrossReferences(DatabaseType.KEGG);
			uniprotKegg = dcrKegg.get(0).getPrimaryId().getValue();
			
			// UniprotEC
			ecNumbers = entry.getProteinDescription().getEcNumbers();

			// UniprotTaxonomie
			taxonomy = entry.getTaxonomy();
			taxon = taxonomy.get(2).toString();

			// Uniprot keywords
			keywords = entry.getKeywords();

			// Uniprot GO annotation
			goTerms = entry.getGoTerms();
			
			uniProtQueryService.stop();
			uniRefQueryService.stop();

		}
	}
	
	@Test // Test Type
	public void testType() {
		assertEquals(type, "SWISSPROT");
	}
	@Test // Test UniProtID
	public void testUniProtID() {
		assertEquals(uniProtID, "MCRA_METTM");
	}		
	
	@Test // Test KO
	public void testUniProtKO() {
		assertEquals(uniprotKO, "K00399");
	}
	
	@Test // Test Kegg
	public void testUniProtKegg() {
		assertEquals(uniprotKegg, "mmg:MTBMA_c15480");
	}
	
	@Test // Test EC
	public void testUniProtEC() {
		assertEquals(ecNumbers.get(0).toString(), "2.8.4.1");
	}

	@Test // Test taxon
	public void testUniProtTaxon() {
		assertEquals(taxon, "Methanobacteria");
	}

	@Test // Test keywords
	public void testUniProtKeywords() {
		assertEquals(keywords.get(3).toString(), "Metal-binding");
	}

	@Test // Test keywords
	public void testGoTerms() {
		assertEquals(goTerms.get(2).getGoTerm().getValue(), "methanogenesis");
		assertEquals(goTerms.get(2).getOntologyType().getValue(), "P:");
	}
}
