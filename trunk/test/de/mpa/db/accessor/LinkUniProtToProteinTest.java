package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;
import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.db.DBManager;
import de.mpa.util.Formatter;

public class LinkUniProtToProteinTest {
	
	/**
	 * DB Connection.
	 */
	private Connection conn;
	private Map<String, ReducedProteinData> proteinDataMap;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test 
	public void testUpdateUniProtEntries() throws SQLException {

		// Get all uniprotEntries.
		long range = 0;
		
		int counter = 0;
		List<UniprotentryTableAccessor> entries = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn, null);
		long upperLimit = entries.size();
		System.out.println(upperLimit + " unreferenced UniProt entries found");
		while (range < upperLimit) {
			Map<String, UniprotentryTableAccessor> uniprotEntries = new HashMap<String, UniprotentryTableAccessor>();
			List<UniprotentryTableAccessor> allEntries = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn, range);
			List<String> accessions = new ArrayList<String>();
			
			for (int i = 0; i < allEntries.size(); i++) {
				UniprotentryTableAccessor uniProtEntry = allEntries.get(i);
				ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(uniProtEntry.getFk_proteinid(), conn);
				accessions.add(proteinAccessor.getAccession());
				uniprotEntries.put(proteinAccessor.getAccession(), uniProtEntry);
			}
			System.out.println("Found " + allEntries.size() + " unreferenced UniProt entries.");
			
			if (!accessions.isEmpty()) {
				proteinDataMap = UniProtUtilities.retrieveProteinData(accessions);
				Set<Entry<String, ReducedProteinData>> entrySet = proteinDataMap.entrySet();
				
				for (Entry<String, ReducedProteinData> e : entrySet) {
					ReducedProteinData proteinData = e.getValue();
					UniProtEntry uniprotEntry = proteinData.getUniProtEntry();
					if (proteinData != null && uniprotEntry != null) {
						// Get the corresponding protein accessor.
						UniprotentryTableAccessor oldUniProtEntry = uniprotEntries.get(e.getKey());
						
						// Get taxonomy id
						Long taxID = Long.valueOf(uniprotEntry.getNcbiTaxonomyIds().get(0).getValue());

						// Get EC Numbers.
						String ecNumbers = "";
						List<String> ecNumberList = uniprotEntry.getProteinDescription().getEcNumbers();
						if (ecNumberList.size() > 0) {
							for (String ecNumber : ecNumberList) {
								ecNumbers += ecNumber + ";";
							}
							ecNumbers = Formatter.removeLastChar(ecNumbers);
						}

						// Get ontology keywords.
						String keywords = "";
						List<Keyword> keywordsList = uniprotEntry.getKeywords();

						if (keywordsList.size() > 0) {
							for (Keyword kw : keywordsList) {
								keywords += kw.getValue() + ";";
							}
							keywords = Formatter.removeLastChar(keywords);
						}

						// Get KO numbers.
						String koNumbers = "";
						List<DatabaseCrossReference> xRefs = uniprotEntry.getDatabaseCrossReferences(DatabaseType.KO);
						if (xRefs.size() > 0) {
							for (DatabaseCrossReference xRef : xRefs) {
								koNumbers += (((KO) xRef).getKOIdentifier().getValue()) + ";";
							}
							koNumbers = Formatter.removeLastChar(koNumbers);
						}
						
						String uniref100 = "", uniref90 ="", uniref50 = "";
						if (proteinData.getUniRef100EntryId() != null) {
							uniref100 = proteinData.getUniRef100EntryId();
						}
						if (proteinData.getUniRef90EntryId() != null) {
							uniref90 = proteinData.getUniRef90EntryId();
						}
						if (proteinData.getUniRef50EntryId() != null) {
							uniref50 = proteinData.getUniRef50EntryId();
						}
						if (oldUniProtEntry != null) {
							Uniprotentry.updateUniProtEntryWithProteinID(oldUniProtEntry.getUniprotentryid(), oldUniProtEntry.getFk_proteinid(), taxID, ecNumbers, koNumbers, keywords, uniref100, uniref90, uniref50, conn);
						}
					}
					counter++;
					if (counter % 100 == 0) {
						System.out.println(counter + "/" + upperLimit + " UniProt entries have been updated.");
						conn.commit();
					}
				}
				// Final commit and clearing of map.
				System.out.println("All UniProt entries have been updated.");
				conn.commit();
			}
			range += 100;
		}
	}
	
//	@Test
//	public void testRetrieveProteinsWithoutUniProtEntries() throws SQLException {
//		Map<String, Long> proteinHits = new HashMap<String, Long>();
//		Map<String, Long> proteins = ProteinAccessor.findAllProteins(conn);
//		Set<Entry<String, Long>> entrySet2 = proteins.entrySet();
//		for (Entry<String, Long> entry : entrySet2) {
//			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(entry.getValue(), conn);
//			if (uniprotentry == null) proteinHits.put(entry.getKey(), entry.getValue());
//		}
//		
//		Set<String> keySet = proteinHits.keySet();
//		List<String> accessions = new ArrayList<String>();
//		for (String string : keySet) {			
//			// UniProt accession
//			if (string.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
//				accessions.add(string);		
//			}
//		}
//		System.out.println("Found " + accessions.size() + " proteins not linked to UniProt entries.");
//		
//		if (!accessions.isEmpty()) {
//			proteinDataMap = UniprotAccessor.retrieveProteinDataEntries(accessions);
//			Set<Entry<String, ProteinData>> entrySet = proteinDataMap.entrySet();
//			int counter = 0;
//			for (Entry<String, ProteinData> e : entrySet) {
//				ProteinData proteinData = e.getValue();
//				UniProtEntry uniprotEntry = e.getValue().getUniProtEntry();
//				if (proteinData != null && uniprotEntry != null) {
//					// Get the corresponding protein accessor.
//					long proteinid = 0L;
//
//					// Check for secondary protein accessions.
//					if (proteinHits.get(e.getKey()) == null) {
//						List<SecondaryUniProtAccession> secondaryUniProtAccessions = uniprotEntry.getSecondaryUniProtAccessions();
//						for (SecondaryUniProtAccession acc : secondaryUniProtAccessions) {
//							if (proteinHits.get(acc.getValue()) != null) {
//								proteinid = proteinHits.get(acc.getValue());
//							}
//						}
//					} else {
//						proteinid = proteinHits.get(e.getKey());
//					}
//					// Get taxonomy id
//					Long taxID = Long.valueOf(uniprotEntry.getNcbiTaxonomyIds().get(0).getValue());
//
//					// Get EC Numbers.
//					String ecNumbers = "";
//					List<String> ecNumberList = uniprotEntry
//							.getProteinDescription().getEcNumbers();
//					if (ecNumberList.size() > 0) {
//						for (String ecNumber : ecNumberList) {
//							ecNumbers += ecNumber + ";";
//						}
//						ecNumbers = Formatter.removeLastChar(ecNumbers);
//					}
//
//					// Get ontology keywords.
//					String keywords = "";
//					List<Keyword> keywordsList = uniprotEntry.getKeywords();
//
//					if (keywordsList.size() > 0) {
//						for (Keyword kw : keywordsList) {
//							keywords += kw.getValue() + ";";
//						}
//						keywords = Formatter.removeLastChar(keywords);
//					}
//
//					// Get KO numbers.
//					String koNumbers = "";
//					List<DatabaseCrossReference> xRefs = uniprotEntry
//							.getDatabaseCrossReferences(DatabaseType.KO);
//					if (xRefs.size() > 0) {
//						for (DatabaseCrossReference xRef : xRefs) {
//							koNumbers += (((KO) xRef).getKOIdentifier().getValue()) + ";";
//						}
//						koNumbers = Formatter.removeLastChar(koNumbers);
//					}
//					
//					String uniref100 = "", uniref90 ="", uniref50 = "";
//					if (proteinData.getUniRefEntry(UniRefDatabaseType.UniRef100) != null) {
//						uniref100 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef100).getUniRefEntryId().getValue();
//					}
//					if (proteinData.getUniRefEntry(UniRefDatabaseType.UniRef90) != null) {
//						uniref90 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef90).getUniRefEntryId().getValue();
//					}
//					if (proteinData.getUniRefEntry(UniRefDatabaseType.UniRef50) != null) {
//						uniref50 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef50).getUniRefEntryId().getValue();
//					}
//					Uniprotentry.addUniProtEntryWithProteinID((Long) proteinid, taxID, ecNumbers, koNumbers, keywords, uniref100, uniref90, uniref50, conn);
//				}
//				counter++;
//
//				if (counter % 500 == 0) {
//					System.out.println(counter + "/" + accessions.size() + " UniProt entries have been updated.");
//					conn.commit();
//				}
//			}
//			// Final commit and clearing of map.
//			System.out.println("All UniProt entries have been updated.");
//			conn.commit();
//		}
//	}
}
