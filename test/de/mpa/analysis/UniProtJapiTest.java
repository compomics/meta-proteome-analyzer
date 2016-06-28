package de.mpa.analysis;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
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
	private TreeMap<String, UniRefEntry> uniRefs = new TreeMap<String, UniRefEntry>();
	private QueryResult<UniProtEntry> entryIterator = null;
	private QueryResult<UniRefEntry> UniRefIterator = null;

	@Before
	public void setUp() throws ServiceException {

		// broken accession
//		String accession = "Q1XIQ3";		
		
		// test case, single entry
		String accession = "P11558";
		
		// test case multiple entries
		Set<String> accessionList = new TreeSet<String>();
		accessionList.add("P11558");
		accessionList.add("A7IAU7");
		accessionList.add("P13368");
		accessionList.add("P20806");
		accessionList.add("Q9UM73");
		accessionList.add("P97793");
		accessionList.add("Q17192");
		
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
		entry = null;
		entry = uniprotService.getEntry(accession);
		
		

		// Query for UniProt / multiple entries
		
		try {			
			Query query = UniProtQueryBuilder.accessions(accessionList);									
			entryIterator = uniprotService.getEntries(query);				
		} catch (ServiceException e) {
			e.printStackTrace();
		}	

		if (entry != null) {
			// UniprotID
			uniProtID = entry.getUniProtId().getValue();
			// Type
			type = entry.getType().name();
			// UniprotKO
			List<DatabaseCrossReference> dcrKO = entry.getDatabaseCrossReferences(DatabaseType.KO);
			if (dcrKO.size() != 0) {
				uniprotKO = dcrKO.get(0).getPrimaryId().getValue() + ";";
			} else {
				System.out.println("has no uniprot KO");
			}
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
		} else {
			System.out.println("no entry!");
		}
		uniprotService.stop();

		// UniRef Service
		UniRefService uniRefQueryService = serviceFactoryInstance.getUniRefQueryService();
		uniRefQueryService.start();
		// Fetch UniRef entries from a query
		try {
			Query query = UniRefQueryBuilder.memberAccession(accession);
			QueryResult<UniRefEntry> entries = uniRefQueryService.getEntries(query);
			while (entries.hasNext()) {
				UniRefEntry thisentry = entries.next();
				String[] uniref_split = thisentry.getUniRefEntryId().toString().split("_");
				uniRefs.put(uniref_split[0], thisentry);
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

//	@Ignore
	@Test // Test UniProtID
	public void testUniProtID() {
		assertEquals(uniProtID, "MCRA_METTM");
	}        

//	@Ignore
	@Test // Test KO
	public void testUniProtKO() {
		assertEquals(uniprotKO, "K00399;");
	}

//	@Ignore
	@Test // Test Kegg
	public void testUniProtKegg() {
		assertEquals(uniprotKegg, "mmg:MTBMA_c15480");
	}

//	@Ignore
	@Test // Test EC
	public void testUniProtEC() {
		assertEquals(ecNumbers.get(0).toString(), "2.8.4.1");
	}

//	@Ignore
	@Test // Test taxon
	public void testUniProtTaxon() {
		assertEquals(taxon, "Methanobacteria");
	}

//	@Ignore
	@Test // Test keywords
	public void testUniProtKeywords() {
		assertEquals(keywords.get(3).toString(), "Metal-binding");
	}

//	@Ignore
	@Test // Test keywords
	public void testGoTerms() {
		assertEquals(goTerms.get(2).getGoTerm().getValue(), "methanogenesis");
		assertEquals(goTerms.get(2).getOntologyType().getValue(), "P:");
	}
	
//	@Ignore
	@Test // Test UniRefservice
	public void testUniref() {
		assertEquals(uniRefs.get("UniRef100").getUniRefEntryId().toString(), "UniRef100_P11558");
		assertEquals(uniRefs.get("UniRef90").getUniRefEntryId().toString(), "UniRef90_P11558");
		assertEquals(uniRefs.get("UniRef50").getUniRefEntryId().toString(), "UniRef50_P11558");
	}

	@Test // Test Print everything
	public void testAccess() {
		System.out.println("UniProtID: "+ uniProtID);
		System.out.println("Type: "+ type);
		System.out.println("UniprotKO: "+ uniprotKO);
		System.out.println("UniprotKegg: "+ uniprotKegg);
		int count = 0;
		for (String ecnumber : ecNumbers) {
			count++;
			System.out.println("ECNumber " + count + ": "+ ecnumber);			
		}
		count = 0;
		for (NcbiTaxon tax : taxonomy) {
			count++;
			System.out.println("Taxonomy " + count + ": "+ tax.getValue());			
		}
		count = 0;
		for (Keyword kw : keywords) {
			count++;
			System.out.println("Keyword " + count + ": "+ kw.getValue());			
		}
		count = 0;
		for (Go gt : goTerms) {
			count++;
			System.out.println("GoTerm " + count + ": "+ gt.toString());			
		}
		for (String uniref : uniRefs.keySet()) {
			System.out.println("Uniref: " + uniref + " : " + uniRefs.get(uniref).getRepresentativeMember().getMemberId() + " : " + uniRefs.get(uniref).getUniRefEntryId());			
		}
	}
	
	@Test // Test UniProtBatch
	public void testUniProtBatch() {
		int count = 0;
		while (entryIterator.hasNext()) {
			count++;
			// make new uniprot entries			
			UniProtEntry thisentry = entryIterator.next();
			System.out.println("BatchTest, " + count + " : "+ thisentry.getUniProtId());
		}
	}
	
}