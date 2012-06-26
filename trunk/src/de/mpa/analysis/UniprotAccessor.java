package de.mpa.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Class to access the UniProt EBI WebService.
 * @author T.Muth
 * @date 25-06-2012
 *
 */
public class UniprotAccessor {
	
	/**
	 * UniProt Query Service. 
	 */
	private static UniProtQueryService uniProtQueryService;
	
	/**
	 * Retrieve a list of protein entries which hold protein hits and UniProt entries.
	 * @param dbSearchResult The database search result.
	 * @return List of ProteinEntry objects.
	 */
	public static List<UniProtEntry> retrieveUniprotEntries(DbSearchResult dbSearchResult) throws RemoteDataAccessException {
		
		// Check whether UniProt query service has been established yet.
		if(uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		}
		
		// Get the protein hits from the search result.
		Map<String, ProteinHit> proteinHits = dbSearchResult.getProteinHits();
		List<String> accList = new ArrayList<String>(proteinHits.keySet());
		
		Query query = UniProtQueryBuilder.buildIDListQuery(accList);
		
		List<UniProtEntry> entries = new ArrayList<UniProtEntry>();
		
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		
		// Iterate the entries and add them to the list. 
		for (UniProtEntry e : entryIterator) {
			String accession = e.getPrimaryUniProtAccession().getValue();
			proteinHits.get(accession).setUniprotEntry(e);
			entries.add(e);
		}
		return entries;
	}
	
}
