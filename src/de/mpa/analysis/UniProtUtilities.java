package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.mpa.client.Constants;
import de.mpa.main.Starter;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefDatabaseType;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.kraken.interfaces.uniref.member.UniRefRepresentativeMember;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

/**
 * Class to access the EBI UniProt WebService.
 * @author T.Muth, A. Behne, R. Heyer
 * @date 19-12-2013
 */
public class UniProtUtilities {
	
	// Max clause count == BATCH SIZE is set to 1024
	private static final int BATCH_SIZE = 100;
	
	/**
	 * Enumeration holding ontology keywords.
	 */
	public enum KeywordCategory {
		BIOLOGICAL_PROCESS(new Keyword("Biological Process", null, null)),
		CELLULAR_COMPONENT(new Keyword("Cellular Component", null, null)),
		CODING_SEQUNCE_DIVERSITY(new Keyword("Coding sequence diversity", null, null)),
		DEVELOPMENTAL_STAGE(new Keyword("Developmental stage", null, null)),
		DISEASE(new Keyword("Disease", null, null)),
		DOMAIN(new Keyword("Domain", null, null)),
		LIGAND(new Keyword("Ligand", null, null)),
		MOLECULAR_FUNCTION(new Keyword("Molecular Function", null, null)),
		PTM(new Keyword("PTM", null, null)),
		TECHNICAL_TERM(new Keyword("Technical term", null, null));
		
		/**
		 * The ontology keyword backing this category.
		 */
		private Keyword keyword;
		
		/**
		 * Creates a keyword category from the specified keyword.
		 * @param keyword the keyword wrapping the category data
		 */
		private KeywordCategory(Keyword keyword) {
			this.keyword = keyword;
		}
		
		/**
		 * Returns the keyword backing the category entry.
		 * @return the keyword
		 */
		public Keyword getKeyword() {
			return keyword;
		}
		
		@Override
		public String toString() {
			return keyword.getName();
		}

		/**
		 * Returns the category entry pertaining to the specified keyword.
		 * @param keyword the keyword
		 * @return the category wrapping the keyword or <code>null</code> if no such category exists
		 */
		public static KeywordCategory valueOf(Keyword keyword) {
			for (KeywordCategory category : KeywordCategory.values()) {
				if (category.keyword.equals(keyword)) {
					return category;
				}
			}
			return null;
		}
	}
	
	/**
	 * Helper class for wrapping UniProt keyword-based ontology entries.
	 * @author A. Behne
	 */
	public static class Keyword {
		
		/**
		 * The name.
		 */
		private String name;
		
		/**
		 * The description.
		 */
		private String description;
		
		/**
		 * The parent keyword category.
		 */
		private Keyword category;

		/**
		 * Creates a keyword entry from the specified name, description and
		 * parent category.
		 * @param name the name
		 * @param description the description
		 * @param category the parent category
		 */
		private Keyword(String name, String description,
				Keyword category) {
			this.name = name;
			this.description = description;
			this.category = category;
		}

		/**
		 * Returns the name.
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the description.
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * Sets the description.
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * Returns the category.
		 * @return the category
		 */
		public Keyword getCategory() {
			return category;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Keyword) {
				Keyword that = (Keyword) obj;
				return this.getName().equals(that.getName());
			}
			return false;
		}
		
	}

	/**
	 * Enumeration holding taxonomic ranks.
	 */
	public enum TaxonomyRank {
		ROOT("root"),
		SUPERKINGDOM("Superkingdom"),
		KINGDOM("Kingdom"),
		PHYLUM("Phylum"),
		CLASS("Class"),
		ORDER("Order"),
		FAMILY("Family"),
		GENUS("Genus"), 
		SPECIES("Species"), 
		SUBSPECIES("Subspecies"),
		NO_RANK("No rank"); 
		
		private String val;
		private TaxonomyRank(String value) {
			this.val = value;
		}
		
		@Override
		public String toString() {
			return val;
		}
		
	}

	/**
	 * Retrieves batch-wise a mapping of UniProt identifiers to UniProt entries.
	 * @param identifierList {@link List} of UniProt identifiers.
	 * @return {@link Map} of ReducedProteinData objects.
	 */
	public static Map<String, ReducedProteinData> retrieveProteinData(List<String> identifierList, boolean doUniRefRetrieval) throws Exception {
		Map<String, ReducedProteinData> proteinData = new TreeMap<String, ReducedProteinData>();
		
		int maxClauseCount = BATCH_SIZE;
		int maxBatchCount = identifierList.size() / maxClauseCount;		
		for (int i = 0; i < maxBatchCount; i++) {
			int startIndex = i * maxClauseCount;
			int endIndex = (i + 1) * maxClauseCount - 1;
			List<String> shortList = new ArrayList<String>(identifierList.subList(startIndex, endIndex));
			startIndex = endIndex + 1;
			queryUniProtEntriesByIdentifiers(shortList, proteinData, doUniRefRetrieval);
			shortList.clear();
		}
		queryUniProtEntriesByIdentifiers(new ArrayList<String>(identifierList.subList(maxBatchCount * maxClauseCount, identifierList.size())), proteinData, doUniRefRetrieval);
		
		return proteinData;
	}
	
	/**
	 * Queries the UniProt entries by identifiers.
	 * @param accessions {@link List} of UniProt identifiers.
	 * @param uniprotEntries {@link Map} of UniProt entries.
	 */
	private static void queryUniProtEntriesByIdentifiers(List<String> accessions, Map<String, ReducedProteinData> uniprotEntries, boolean doUniRefRetrieval) throws Exception {
		
	    ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();

	    // UniProtService
	    UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
	    uniprotService.start();
	    
	    // UniRefService
	    UniRefService unirefService = serviceFactoryInstance.getUniRefQueryService();
	    unirefService.start();
		
		Set<String> set = new HashSet<>(accessions); 
		Query query = UniProtQueryBuilder.accessions(set);
		QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
	    
		while (entries.hasNext()) {
			
			UniProtEntry entry = entries.next();
			ReducedProteinData proteinData;			
			String accession = entry.getPrimaryUniProtAccession().getValue();
			if (doUniRefRetrieval) {
				// Get the UniRefEntry
//				String uniRef100Identifier = "UniRef100_" + accession;
				UniRefEntry uniRefEntry = unirefService.getEntryByUniProtAccession(accession, UniRefDatabaseType.UniRef100);
				
				if (uniRefEntry != null) {
					UniRefRepresentativeMember member = uniRefEntry.getRepresentativeMember();
					proteinData = new ReducedProteinData(entry, uniRefEntry.getUniRefEntryId().getValue(), member.getUniRef90EntryId().getValue(), member.getUniRef50EntryId().getValue());
				} else {
					//TODO: 
					proteinData = new ReducedProteinData(entry, "", "", "");
				}
			} else {
				proteinData = new ReducedProteinData(entry);
			}
			uniprotEntries.put(accession, proteinData);
		}	
	}

	/**
	 * The UniProt keyword taxonomy map.
	 */
	public static final Map<String, TaxonomyRank> TAXONOMY_RANKS_MAP;
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
		map.put("subspecies", TaxonomyRank.SUBSPECIES);
		TAXONOMY_RANKS_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * The UniProt keyword ontology map.
	 */
	public static final Map<String, Keyword> ONTOLOGY_MAP = createOntologyMap();

	/**
	 * Parses a text file containing keyword data and stores it into a map.<br>
	 * @see <a href="http://www.uniprot.org/keywords/?format=obo">http://www.uniprot.org/keywords/?format=obo</a>
	 * @return the ontology map
	 */
	public static Map<String, Keyword> createOntologyMap() {
		// Initialize category keywords, link them to KeyWordOntology enums
		Map<String, KeywordCategory> categoryMap = new HashMap<String, KeywordCategory>();
		categoryMap.put("KW-9990", KeywordCategory.TECHNICAL_TERM);
		categoryMap.put("KW-9991", KeywordCategory.PTM);
		categoryMap.put("KW-9992", KeywordCategory.MOLECULAR_FUNCTION);
		categoryMap.put("KW-9993", KeywordCategory.LIGAND);
		categoryMap.put("KW-9994", KeywordCategory.DOMAIN);
		categoryMap.put("KW-9995", KeywordCategory.DISEASE);
		categoryMap.put("KW-9996", KeywordCategory.DEVELOPMENTAL_STAGE);
		categoryMap.put("KW-9997", KeywordCategory.CODING_SEQUNCE_DIVERSITY);
		categoryMap.put("KW-9998", KeywordCategory.CELLULAR_COMPONENT);
		categoryMap.put("KW-9999", KeywordCategory.BIOLOGICAL_PROCESS);
		
		// Initialize ontology map
		HashMap<String, Keyword> ontologyMap = new HashMap<String, Keyword>();
		
		try {
			// Initialize reader
			BufferedReader br = null;
			if (Starter.isJarExport()) {
				br = new BufferedReader(new FileReader(new File(Constants.CONFIGURATION_PATH_JAR + File.separator + "keywords-all.obo")));
			} else {
				InputStream is = ClassLoader.getSystemResourceAsStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "keywords-all.obo");
				br = new BufferedReader(new InputStreamReader(is));
			}
			
			String line;
			String id = null;
			String name = null;
			String description = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("id: ")) {
					id = line.substring(4);
					continue;
				}
				if (line.startsWith("name: ")) {
					name = line.substring(6);
					continue;
				}
				if (line.startsWith("def: ")) {
					description = line.substring(6, line.lastIndexOf('\"'));
					
					// if we reach a category's entry use it to fill out the
					// uninitialized description string now
					KeywordCategory category = categoryMap.get(id);
					if (category != null) {
						category.getKeyword().setDescription(description);
					}
					continue;
				}
				if (line.startsWith("relationship: ")) {
					String category = line.substring(23);
					KeywordCategory kwCategory = categoryMap.get(category);
					ontologyMap.put(name, new Keyword(name, description, kwCategory.getKeyword()));
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
