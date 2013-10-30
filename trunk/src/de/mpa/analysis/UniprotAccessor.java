package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import de.mpa.client.Constants;

/**
 * Class to access the EBI UniProt WebService.
 * @author T.Muth, A. Behne, R. Heyer
 * @date 25-06-2012
 *
 */
public class UniprotAccessor {
	
	// Max clause count == BATCH SIZE is set to 1024
	private static final int BATCH_SIZE = 1024;
	
	/**
	 * The shared UniProt query service instance. 
	 */
	private static UniProtQueryService uniProtQueryService;

	/**
	 * Enumeration holding ontology keywords.
	 */
	public enum KeywordOntology {
		BIOLOGICAL_PROCESS("Biological Process"),
		CELLULAR_COMPONENT("Cellular Component"),
		CODING_SEQUNCE_DIVERSITY("Coding sequence diversity"),
		DEVELOPMENTAL_STAGE("Developmental stage"),
		DISEASE("Disease"),
		DOMAIN("Domain"),
		LIGAND("Ligand"),
		MOLECULAR_FUNCTION("Molecular Function"),
		PTM("PTM"),
		TECHNICAL_TERM("Technical term");

		private String val;
		
		private KeywordOntology(String value) {
			this.val = value;
		}
		
		@Override
		public String toString() {
			return val;
		}
	}

	/**
	 * Enumeration holding taxonomic ranks.
	 */
	public enum TaxonomyRank {
		ROOT, SUPERKINGDOM, KINGDOM, PHYLUM, CLASS, ORDER, FAMILY, GENUS, SPECIES, NO_RANK 
	}

	/**
	 * Retrieves batch-wise a mapping of UniProt identifiers to UniProt entries.
	 * @param identifierList {@link List} of UniProt identifiers.
	 * @return {@link Map} of UniProt entries.
	 */
	public static Map<String, UniProtEntry> retrieveUniProtEntries(List<String> identifierList) {
		Map<String, UniProtEntry> uniprotEntryMap = new TreeMap<String, UniProtEntry>();
		
		// Check whether UniProt query service has been established yet.
		if (uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		}
		int maxClauseCount = BATCH_SIZE;
		int maxBatchCount = identifierList.size() / maxClauseCount;		
		for (int i = 0; i < maxBatchCount; i++) {
			int startIndex = i * maxClauseCount;
			int endIndex = (i + 1) * maxClauseCount - 1;
			List<String> shortList = new ArrayList<String>(identifierList.subList(startIndex, endIndex));
			startIndex = endIndex + 1;
			queryUniProtEntriesByIdentifiers(shortList, uniprotEntryMap);
			shortList.clear();
		}
		queryUniProtEntriesByIdentifiers(new ArrayList<String>(identifierList.subList(maxBatchCount * maxClauseCount, identifierList.size())), uniprotEntryMap);
		
		return uniprotEntryMap;
	}
	
	/**
	 * Queries the UniProt entries by identifiers.
	 * @param identifierList {@link List} of UniProt identifiers.
	 * @param uniprotEntries {@link Map} of UniProt entries.
	 */
	private static void queryUniProtEntriesByIdentifiers(List<String> identifierList, Map<String, UniProtEntry> uniprotEntries) {
		Query query = UniProtQueryBuilder.buildIDListQuery(identifierList);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		
		// Iterate the entries and add them to the list. 
		for (UniProtEntry entry : entryIterator) {
			uniprotEntries.put(entry.getPrimaryUniProtAccession().getValue(), entry);
		}	
	}

	/**
	 * The UniProt keyword taxonomy map.
	 */
	public static final Map<String, TaxonomyRank> TAXONOMY_MAP;
	static {
		Map<String, TaxonomyRank> map = new LinkedHashMap<String, TaxonomyRank>();
		map.put("root", TaxonomyRank.ROOT);
		map.put("superkingdom", TaxonomyRank.SUPERKINGDOM);
		map.put("kingdom", TaxonomyRank.KINGDOM);
		map.put("phylum", TaxonomyRank.PHYLUM);
		map.put("class", TaxonomyRank.CLASS);
		map.put("order", TaxonomyRank.ORDER);
		map.put("family", TaxonomyRank.FAMILY);
		map.put("genus", TaxonomyRank.GENUS);
		map.put("species", TaxonomyRank.SPECIES);
		TAXONOMY_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * The UniProt keyword ontology map.
	 */
	public static final Map<String, KeywordOntology> ONTOLOGY_MAP = createOntologyMap();

	/**
	 * Parses a text file containing keyword data and stores it into a map.<br>
	 * @see <a href="http://www.uniprot.org/keywords/?format=obo">http://www.uniprot.org/keywords/?format=obo</a>
	 * @return the ontology map
	 */
	public static Map<String, KeywordOntology> createOntologyMap() {
		// Initialize category keywords, link them to KeyWordOntology enums
		Map<String, KeywordOntology> categoryMap = new HashMap<String, UniprotAccessor.KeywordOntology>();
		categoryMap.put("KW-9990", KeywordOntology.TECHNICAL_TERM);
		categoryMap.put("KW-9991", KeywordOntology.PTM);
		categoryMap.put("KW-9992", KeywordOntology.MOLECULAR_FUNCTION);
		categoryMap.put("KW-9993", KeywordOntology.LIGAND);
		categoryMap.put("KW-9994", KeywordOntology.DOMAIN);
		categoryMap.put("KW-9995", KeywordOntology.DISEASE);
		categoryMap.put("KW-9996", KeywordOntology.DEVELOPMENTAL_STAGE);
		categoryMap.put("KW-9997", KeywordOntology.CODING_SEQUNCE_DIVERSITY);
		categoryMap.put("KW-9998", KeywordOntology.CELLULAR_COMPONENT);
		categoryMap.put("KW-9999", KeywordOntology.BIOLOGICAL_PROCESS);
		
		// Initialize ontology map
		HashMap<String, KeywordOntology> ontologyMap = new HashMap<String, KeywordOntology>();
		
		try {
			// Initialize reader
			BufferedReader br = new BufferedReader(new FileReader(
					new File(UniprotAccessor.class.getClass().getResource(
							Constants.CONFIGURATION_PATH + "keywords-all.obo").toURI())));
			
			String line;
			String name = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("name: ")) {
					name = line.substring(6);
					continue;
				}
				if (line.startsWith("relationship: ")) {
					String category = line.substring(23);
					ontologyMap.put(name, categoryMap.get(category));
				}
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Return unmodifiable view of map
		return Collections.unmodifiableMap(ontologyMap);
	}
}
