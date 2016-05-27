package de.mpa.analysis;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

public class UniProtJapiTest {

	private UniProtEntry entry;
	private String uniProtID;
	private String type;
	private String uniprotKO;
	private String uniprotKegg;
	private List<String> ecNumbers;
	private List<NcbiTaxon> taxonomy;
	private String taxon;
	private List<Keyword> keywords;
	private List<Go> goTerms;

	@Before
	public void setUp() {

		//String accession = "P11558";
		String accession = "Q9SLK2";
		/*
		 * Client Class has a couple of static methods to create a ServiceFactory instance.
		 * From ServiceFactory, you can fetch the JAPI Services.
		 */
		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();

		// UniProtService
		UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();

		/*
		 * After you obtain a service, you will need to call start() to connect to the server
		 * before you can use the service to retrieve data
		 */
		uniprotService.start();

		/*
		 * If you want to fetch all of the data contained within a specific entry. Then you can bypass the query construction
		 * phase, by directly calling the UniProtService.getEntry(String accession) method.
		 */
		System.out.println("get entry");
		entry = null;
		try {
			entry = uniprotService.getEntry(accession);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("getting entry done");


		if (entry != null) {
			System.out.println("entry not null");
			// UniprotID
			System.out.println("get uniProtID");
			uniProtID = entry.getUniProtId().getValue();
			// Type
			System.out.println("get type");
			type = entry.getType().name();
			// UniprotKO
			System.out.println("get uniprotKO");
			List<DatabaseCrossReference> dcrKO = entry.getDatabaseCrossReferences(DatabaseType.KO);
			if (dcrKO.size() != 0) {
				uniprotKO = dcrKO.get(0).getPrimaryId().getValue() + ";";
			} else {
				System.out.println("has no uniprotKO");
			}
			// UniprotKEGG
			System.out.println("get uniprotKegg");
			List<DatabaseCrossReference> dcrKegg = entry.getDatabaseCrossReferences(DatabaseType.KEGG);
			uniprotKegg = dcrKegg.get(0).getPrimaryId().getValue();
			// UniprotEC
			System.out.println("get ecNumbers");
			ecNumbers = entry.getProteinDescription().getEcNumbers();
			// UniprotTaxonomie
			System.out.println("get taxonomy");
			taxonomy = entry.getTaxonomy();
			taxon = taxonomy.get(2).toString();
			// Uniprot keywords
			System.out.println("get keywords");
			keywords = entry.getKeywords();
			// Uniprot GO annotation
			System.out.println("get goTerms");
			goTerms = entry.getGoTerms();
			System.out.println("TEST");
		} else {
			System.out.println("no entry!");
		}


		// UniRef Service
		UniRefService uniRefQueryService = serviceFactoryInstance.getUniRefQueryService();
		uniRefQueryService.start();
		// Fetch UniRef entries from a query
		try {
			System.out.println("TEST2");
//			uniRef100 = uniRefQueryService.getEntryByUniProtAccession(accession, UniRefDatabaseType.UniRef100);
//			if (uniRef100 != null) {
//				System.out.println("Uniref100: " + uniRef100.getUniRefEntryId());
//			} else {
//				System.out.println("Uniref100 is null!");
//			}
//			uniRef90 = uniRefQueryService.getEntryByUniProtAccession(accession, UniRefDatabaseType.UniRef90);
//			if (uniRef90 != null) {
//				System.out.println("Uniref90: "  + uniRef90.getUniRefEntryId());
//			} else {
//				System.out.println("Uniref90 is null!");
//			}
//			if (uniRef50 != null) {
//				System.out.println("Uniref50: "  + uniRef50.getUniRefEntryId());
//			} else {
//				System.out.println("Uniref50 is null!");
//			}
			
			//Query query = UniRefQueryBuilder.representativeAccession(accession);
			Query query = UniRefQueryBuilder.memberAccession(accession);
			QueryResult<UniRefEntry> entries = uniRefQueryService.getEntries(query);
			while (entries.hasNext()) {
				UniRefEntry thisentry = entries.next();
				System.out.println("now: "+thisentry.getUniRefEntryId());
			}
			



		} catch (ServiceException e) {
			e.printStackTrace();
		}
		uniRefQueryService.stop();
	}

	@Test // Test Type
	public void testType() {
		assertEquals(type, "SWISSPROT");
	}

	@Ignore
	@Test // Test UniProtID
	public void testUniProtID() {
		assertEquals(uniProtID, "MCRA_METTM");
	}        

	@Ignore
	@Test // Test KO
	public void testUniProtKO() {
		assertEquals(uniprotKO, "K00399;");
	}

	@Ignore
	@Test // Test Kegg
	public void testUniProtKegg() {
		assertEquals(uniprotKegg, "mmg:MTBMA_c15480");
	}

	@Ignore
	@Test // Test EC
	public void testUniProtEC() {
		assertEquals(ecNumbers.get(0).toString(), "2.8.4.1");
	}

	@Ignore
	@Test // Test taxon
	public void testUniProtTaxon() {
		assertEquals(taxon, "Methanobacteria");
	}

	@Ignore
	@Test // Test keywords
	public void testUniProtKeywords() {
		assertEquals(keywords.get(3).toString(), "Metal-binding");
	}

	@Ignore
	@Test // Test keywords
	public void testGoTerms() {
		assertEquals(goTerms.get(2).getGoTerm().getValue(), "methanogenesis");
		assertEquals(goTerms.get(2).getOntologyType().getValue(), "P:");
	}
}