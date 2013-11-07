package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.db.DBManager;
import de.mpa.util.Formatter;

public class LinkUniProtToProteinTest {
	
	/**
	 * DB Connection.
	 */
	private Connection conn;
	private Map<String, UniProtEntry> uniProtEntries;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testRetrieveProteinsWithoutUniProtEntries() throws SQLException {
		Map<String, Long> proteinHits = new HashMap<String, Long>();
		Map<String, Long> proteins = ProteinAccessor.findAllProteins(conn);
		Set<Entry<String, Long>> entrySet2 = proteins.entrySet();
		for (Entry<String, Long> entry : entrySet2) {
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(entry.getValue(), conn);
			if (uniprotentry == null) proteinHits.put(entry.getKey(), entry.getValue());
		}
		
		Set<String> keySet = proteinHits.keySet();
		List<String> accessions = new ArrayList<String>();
		for (String string : keySet) {			
			// UniProt accession
			if (string.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				accessions.add(string);		
			}
		}
		
		if (!accessions.isEmpty()) {
			uniProtEntries = UniprotAccessor.retrieveUniProtEntries(accessions);
			Set<Entry<String, UniProtEntry>> entrySet = uniProtEntries.entrySet();
			int counter = 0;
			for (Entry<String, UniProtEntry> e : entrySet) {
				UniProtEntry uniprotEntry = e.getValue();
				if (uniprotEntry != null) {
					// Get the corresponding protein accessor.
					long proteinid = 0L;

					// Check for secondary protein accessions.
					if (proteinHits.get(e.getKey()) == null) {
						List<SecondaryUniProtAccession> secondaryUniProtAccessions = e.getValue().getSecondaryUniProtAccessions();
						for (SecondaryUniProtAccession acc : secondaryUniProtAccessions) {
							if (proteinHits.get(acc.getValue()) != null) {
								proteinid = proteinHits.get(acc.getValue());
							}
						}
					} else {
						proteinid = proteinHits.get(e.getKey());
					}
					// Get taxonomy id
					Long taxID = Long.valueOf(uniprotEntry.getNcbiTaxonomyIds().get(0).getValue());

					// Get EC Numbers.
					String ecNumbers = "";
					List<String> ecNumberList = uniprotEntry
							.getProteinDescription().getEcNumbers();
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
					List<DatabaseCrossReference> xRefs = uniprotEntry
							.getDatabaseCrossReferences(DatabaseType.KO);
					if (xRefs.size() > 0) {
						for (DatabaseCrossReference xRef : xRefs) {
							koNumbers += (((KO) xRef).getKOIdentifier().getValue()) + ";";
						}
						koNumbers = Formatter.removeLastChar(koNumbers);
					}
					Uniprotentry.addUniProtEntryWithProteinID((Long) proteinid, taxID, ecNumbers, koNumbers, keywords, conn);
				}
				counter++;

				if (counter % 500 == 0) {
					System.out.println(counter + "/" + proteinHits.size() + " UniProt entries updated.");
					conn.commit();
				}
			}
			// Final commit and clearing of map.
			conn.commit();
		}
	}
}
