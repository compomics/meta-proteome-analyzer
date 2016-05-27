package de.mpa.analysis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import de.mpa.db.DBManager;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.util.Formatter;

public class ProteinDataRetrievalTest {
	
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testUpdateSingleUniprotEntry() throws SQLException {
	    ProteinAccessor proteinAccessor = ProteinAccessor.findFromAttributes("A0AJX1", conn);
		Map<String, ReducedProteinData> proteinDataMap = null;
		
		// Retrieve the UniProt entries.
		List<String> accessions = new ArrayList<String>();
		String accession = proteinAccessor.getAccession();
		// UniProt accession
		if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
			accessions.add(accession);		
		}
		if (!accessions.isEmpty()) {
			UniProtUtilities uniprotweb = new UniProtUtilities();
			proteinDataMap = uniprotweb.getUniProtData(accessions);		
		}
		
		Set<Entry<String, ReducedProteinData>> entrySet = proteinDataMap.entrySet();
		for (Entry<String, ReducedProteinData> e : entrySet) {
			ReducedProteinData proteinData = e.getValue();
			UniProtEntry uniprotEntry = proteinData.getUniProtEntry();
			
			if (proteinData != null && uniprotEntry != null) {
				// Get the corresponding protein accessor.
				long proteinid = proteinAccessor.getProteinid();
				Uniprotentry uniProtEntryAccessor = Uniprotentry.findFromProteinID(proteinid, conn);
				
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
						koNumbers += xRef.getPrimaryId().getValue() + ";";
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
				Uniprotentry.updateUniProtEntryWithProteinID(uniProtEntryAccessor.getUniprotentryid(), (Long) proteinid, taxID, ecNumbers, koNumbers, keywords, uniref100, uniref90, uniref50, conn);
			}	
		}
		conn.commit();
	}

}
