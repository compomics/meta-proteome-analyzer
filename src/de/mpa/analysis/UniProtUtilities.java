package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;

import uk.ac.ebi.kraken.interfaces.ProteinData;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefDatabaseType;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.blast.BlastHit;
import de.mpa.client.blast.BlastResult;
import de.mpa.client.blast.DbEntry;
import de.mpa.client.blast.RunMultiBlast;
import de.mpa.client.blast.DbEntry.DB_Type;
import de.mpa.client.blast.RunBlast;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.accessor.UniprotentryTableAccessor;
import de.mpa.main.Starter;
import de.mpa.util.Formatter;

/**
 * Class to access the EBI UniProt WebService.
 * @author T.Muth, A. Behne, R. Heyer
 * @date 19-12-2013
 */
public class UniProtUtilities {

	// Max clause count == BATCH SIZE is set to 1024
	private static final int BATCH_SIZE = 1024;

	/**
	 * The shared UniProt query service instance. 
	 */
	private static UniProtQueryService uniProtQueryService;

	/**
	 * UniProt (single) entry retrieval service instance.
	 */
	private static EntryRetrievalService entryRetrievalService;

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
	public static Map<String, ReducedProteinData> retrieveProteinData(List<String> identifierList, boolean doUniRefRetrieval) {
		Map<String, ReducedProteinData> proteinData = new TreeMap<String, ReducedProteinData>();

		// Check whether UniProt query service has been established yet.
		if (uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		}

		// Check whether UniProt query service has been established yet.
		if (entryRetrievalService == null) {
			entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();
		}

		int maxClauseCount = BATCH_SIZE;
		int maxBatchCount = identifierList.size() / maxClauseCount;		
		for (int i = 0; i < maxBatchCount; i++) {
			//			System.out.println("Batch Number: "+ i+1);
			int startIndex = i * maxClauseCount;
			int endIndex = (i + 1) * maxClauseCount - 1;
			List<String> shortList = new ArrayList<String>(identifierList.subList(startIndex, endIndex));
			startIndex = endIndex + 1;
			if (shortList != null &&shortList.size()>0 ) {
				queryUniProtEntriesByIdentifiers(shortList, proteinData, doUniRefRetrieval);
				shortList.clear();
			}

		}
		ArrayList<String> newIDList = new ArrayList<String>(identifierList.subList(maxBatchCount * maxClauseCount, identifierList.size()));
		if (newIDList != null ||newIDList.size()>0) {
			queryUniProtEntriesByIdentifiers(newIDList, proteinData, doUniRefRetrieval);
		}


		return proteinData;
	}

	/**
	 * Queries the UniProt entries by identifiers.
	 * @param identifierList {@link List} of UniProt identifiers.
	 * @param uniprotEntries {@link Map} of UniProt entries.
	 */
	private static void queryUniProtEntriesByIdentifiers(List<String> identifierList, Map<String, ReducedProteinData> uniprotEntries, boolean doUniRefRetrieval) {
		if (identifierList !=null && identifierList.size()>0) {
			// Logging
			if (identifierList != null) {
				if (Client.getInstance() != null) {
					Client.getInstance().firePropertyChange("new message", null, "QUERYING UNIPROT FOR " + identifierList.size() + " ENTRIES");
					Client.getInstance().firePropertyChange("resetall", -1L, (long) identifierList.size());
					Client.getInstance().firePropertyChange("resetcur", -1L, (long) identifierList.size());
				}

				// Query UniProt
				Query query = UniProtQueryBuilder.buildIDListQuery(identifierList);
				EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
				
				// Iterate the entries and add them to the list. 
				for (UniProtEntry entry : entryIterator) {
					ReducedProteinData proteinData;			
					String accession = entry.getPrimaryUniProtAccession().getValue();
					if (doUniRefRetrieval) {
						// Get the protein data + uniRef entry 
						proteinData = getUniRefByUniProtAcc(accession);
					} else {
						proteinData = new ReducedProteinData(entry);
					}
					uniprotEntries.put(accession, proteinData);
					if (Client.getInstance() != null) {
						Client.getInstance().firePropertyChange("progressmade", false, true);
					}
//					System.out.println(entry.getOrganism());
				}	
				if (Client.getInstance() != null) {
					Client.getInstance().firePropertyChange("new message", null, "FINISHED UNIPROT QUERY");
				}
			}
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

	/**
	 * Method to repair missing UniRef references
	 * @throws SQLException 
	 */
	public static void repairMissingUniRefs() throws SQLException{

		// Protein map
		Map<String, ReducedProteinData> proteinDataMap;

		// Path of the taxonomy dump folder
		Connection conn = DBManager.getInstance().getConnection();

		// Get all uniprotEntries.
		long begin = 0L;
		long increment = 100L;

		int counter = 0;

		List<UniprotentryTableAccessor> entries = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn);
		long upperLimit = entries.size();
		Client.getInstance().firePropertyChange("new message", null, "FOUND " + entries.size() + " UNIPROT ENTRIES MISSING ANNOTATION");
		Client.getInstance().firePropertyChange("resetall", -1L, (long) entries.size());
		Client.getInstance().firePropertyChange("resetcur", -1L, (long) entries.size());

		//			System.out.println(upperLimit + " unreferenced UniProt entries found");

		upperLimit = 26000L;
		while (begin < upperLimit) {
			Map<String, UniprotentryTableAccessor> uniprotEntries = new HashMap<String, UniprotentryTableAccessor>();
			//					List<UniprotentryTableAccessor> allEntries = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn, 0, increment);
			List<UniprotentryTableAccessor> allEntries = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn,0, increment);
			List<String> accessions = new ArrayList<String>();

			for (int i = 0; i < allEntries.size(); i++) {
				UniprotentryTableAccessor uniProtEntry = allEntries.get(i);
				ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(uniProtEntry.getFk_proteinid(), conn);
				accessions.add(proteinAccessor.getAccession());
				uniprotEntries.put(proteinAccessor.getAccession(), uniProtEntry);
			}
			//				System.out.println("Found " + allEntries.size() + " unreferenced UniProt entries.");

			if (!accessions.isEmpty()) {
				proteinDataMap = UniProtUtilities.retrieveProteinData(accessions, true);
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
						List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword> keywordsList = (List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword>) uniprotEntry.getKeywords();

						if (keywordsList.size() > 0) {
							for (uk.ac.ebi.kraken.interfaces.uniprot.Keyword kw : keywordsList) {
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

						// Get UniRef Ids
						ReducedProteinData uniRefs = getUniRefByUniProtAcc(proteinData.getUniProtEntry().getPrimaryUniProtAccession().toString());

						if (oldUniProtEntry != null) {
							Uniprotentry.updateUniProtEntryWithProteinID(
									oldUniProtEntry.getUniprotentryid(), oldUniProtEntry.getFk_proteinid(),
									taxID, ecNumbers, koNumbers, keywords, uniRefs.getUniRef100EntryId(), uniRefs.getUniRef90EntryId(), uniRefs.getUniRef50EntryId(), conn);
						}

						Client.getInstance().firePropertyChange("progressmade", false, true);
					}
					counter++;
					if (counter % increment == 0) {
						//							System.out.println(counter + "/" + allEntries.size() + " UniProt entries have been updated.");
						//						Client.getInstance().firePropertyChange("new message", null, counter + "/" + allEntries.size() + " ENTRIES HAVE BEEN UPDATED");
						conn.commit();
					}
				}
				// Final commit and clearing of map.
				conn.commit();
			}
			begin += increment;
		}
		//			System.out.println("All UniProt entries have been updated.");
		Client.getInstance().firePropertyChange("new message", null, "FINISHED UPDATING UNIPROT ENTRIES");
	}

	/**
	 * Method to repair empty UniProtEntries and if they are unknown to BLAST them
	 * @param blastFile. The file of the blast algorithm.
	 * @param database. The database for BLAST search.
	 * @param eValue. The evalue cutoff for the BLAST search.
	 * @param blast. Flag for include BLAST or not
	 */

	public static void updateUniProtEntries(Set<Long> proteins, String blastFile, String blastDatabase, double eValue, boolean blast) throws SQLException{
		Connection conn = DBManager.getInstance().getConnection();

		// Find all proteins without a UniProt entry
		//			System.out.println(proteins.size() + " proteins in this experiment ...");
		Set<Long> unlinkedProteins = new HashSet<Long>();
		for (Long ID : proteins) {
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(ID, conn);
			// TODO Needs a check for corrupted uniprot entries (for instance because of missing taxonomy information)
			if (uniprotentry == null) {
				unlinkedProteins.add(ID);
			}
		}
		Client.getInstance().firePropertyChange("new message", null, "FOUND " + unlinkedProteins.size() + " UNLINKED PROTEINS");
		//			System.out.println("... " + unlinkedProteins.size() + " of which have no UniProt entry.");

		// Mapping for the entries without UniProt Entry --> <ORIGINAL_ACCESSION, UNIPROT_ACCESSION>
		Map<String, String> accessionsMap = new HashMap<String, String>();

		// Sort out which proteins need to be BLASTed
		List<ProteinAccessor> blastProteins = new ArrayList<ProteinAccessor>();
		for (Long ID : unlinkedProteins) {
			// get the current protein accession entry
			ProteinAccessor accProt = ProteinAccessor.findFromID(ID, conn);
			String accession = accProt.getAccession();
			// there is an accession that looks like a UniProt accession but no UniProt entry
			if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				// keep the accession and look for UniProt update later
				accessionsMap.put(accession, accession);
				// there is no proper accession available for this protein
			} else {
				// remember the ProteinAccessor and BLAST later
				blastProteins.add(accProt);
			}
		}

		if (blast) {

			String status;
			Map<String, BlastResult> blastBatch;
			//			System.out.print(" done.\n");
			if (!blastProteins.isEmpty()) {
				// Make DbEntries from the proteins in question
				List<DbEntry> blastEntries = new ArrayList<DbEntry>();
				for (ProteinAccessor prot : blastProteins) {
					DbEntry dbEntry = new DbEntry(prot.getAccession(),
							prot.getAccession(), DB_Type.UNIPROTSPROT, null);
					dbEntry.setSequence(prot.getSequence());
					blastEntries.add(dbEntry);
				}
				//			System.out.println("... " + blastEntries.size() + " of those have no valid accession and will be BLASTed.");
				Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST ON " + blastEntries.size() + " PROTEINS");
				Client.getInstance().firePropertyChange("indeterminate", false,	true);
				// Start the BLAST
				//			System.out.print("Running BLAST ...");
				RunMultiBlast blaster = new RunMultiBlast(blastFile, blastDatabase, eValue, blastEntries);
				status = "FINISHED";
				try {
					blaster.blast();
				} catch (IOException e) {
					e.printStackTrace();
					status = "FAILED";
				}
				blastBatch = blaster.getBlastResultMap();

				//			System.out.println("... " + blastBatch.size() + " proteins were found during BLAST.");
				// Update BLASTED proteins
				for (ProteinAccessor prot : blastProteins) {
					String acc = prot.getAccession();
					// Get best BLAST hit
					if (blastBatch.get(acc) != null) {
						BlastHit bestBlastHit = blastBatch.get(acc)
								.getBestBitScoreBlastHit();
						// and update database entry
						accessionsMap.put(acc, bestBlastHit.getAccession());
						String newDescription = "MG: " + bestBlastHit.getName()
								+ " [" + bestBlastHit.getAccession()
								+ "] Score: " + bestBlastHit.getScore();
						//					System.out.println("BLAST query " + acc + " was identified as: " + bestBlastHit.getAccession());
						ProteinAccessor.upDateProteinEntry(prot.getProteinid(),
								prot.getAccession(), newDescription,
								prot.getSequence(), prot.getCreationdate(),
								conn);
					}
				}
				Client.getInstance().firePropertyChange("indeterminate", true, false);
				Client.getInstance().firePropertyChange("new message", null, "BLAST FOUND " + blastBatch.size() + " PROTEINS");
				Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST " + status);
			}
		}

		// Get UniProtEntries for the found proteins.
		fetchEmptyUniProtEntries(accessionsMap);

	};

	/**
	 * Method to repair empty UniProtEntries
	 * @param accessionsMap mapping with original accessions and newly found UniProt accessions.
	 * @throws SQLException
	 */
	public static void fetchEmptyUniProtEntries(Map<String, String> accessionsMap) throws SQLException {
		
		if (!accessionsMap.isEmpty()) {
			// Protein map
			Map<String, ReducedProteinData> proteinDataMap;
			// Database connection
			Connection conn = DBManager.getInstance().getConnection();
			// Extract all UniProt accessions for query
			List<String> accessionsList = new ArrayList<>();
			for (Entry<String, String> queryAccessions : accessionsMap.entrySet()) {
				accessionsList.add(queryAccessions.getValue());
			}
			// Query uniprot for entries
			Client.getInstance().firePropertyChange("new message", null, "PREPARING " + accessionsList.size() + " UNIPROT QUERIES.");
			proteinDataMap = UniProtUtilities.retrieveProteinData(accessionsList, true);
			// Backmapping and adding of the UniProt entries
			int counter = 0;
			if (proteinDataMap != null) {
				Client.getInstance().firePropertyChange("new message", null, "UPDATING UNIPROT ENTRIES");
				Client.getInstance().firePropertyChange("resetall", -1L, (long) accessionsMap.size());
				Client.getInstance().firePropertyChange("resetcur", -1L, (long) accessionsMap.size());
				for (String oriAccession : accessionsMap.keySet()) {
					// Get UniProt entry if possible
					ReducedProteinData proteinData = proteinDataMap.get(accessionsMap.get(oriAccession));
					if (proteinData != null) {
						UniProtEntry uniProtEntry = proteinData.getUniProtEntry();
						// Get corresponding protein accessor
						long proteinid = ProteinAccessor.findFromAttributes(oriAccession, conn).getProteinid();
						// Get UniProt entry informations
						// Get taxonomy id
						Long taxID = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());

						// Get EC Numbers.
						String ecNumbers = "";
						List<String> ecNumberList = uniProtEntry
								.getProteinDescription().getEcNumbers();
						if (ecNumberList.size() > 0) {
							for (String ecNumber : ecNumberList) {
								ecNumbers += ecNumber + ";";
							}
							ecNumbers = Formatter.removeLastChar(ecNumbers);
						}

						// Get ontology keywords.
						String keywords = "";
						List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword> keywordsList = uniProtEntry.getKeywords();

						if (keywordsList.size() > 0) {
							for (uk.ac.ebi.kraken.interfaces.uniprot.Keyword kw : keywordsList) {
								keywords += kw.getValue() + ";";
							}
							keywords = Formatter.removeLastChar(keywords);
						}

						// Get KO numbers.
						String koNumbers = "";
						List<DatabaseCrossReference> xRefs = uniProtEntry
								.getDatabaseCrossReferences(DatabaseType.KO);
						if (xRefs.size() > 0) {
							for (DatabaseCrossReference xRef : xRefs) {
								koNumbers += xRef.getPrimaryId().getValue() + ";";
							}
							koNumbers = Formatter.removeLastChar(koNumbers);
						}

						//						String uniref100 = null, uniref90 = null, uniref50 = null;
						//						if (proteinData.getUniRef100EntryId() != null) {
						//							uniref100 = proteinData.getUniRef100EntryId();
						//						}
						//						if (proteinData.getUniRef90EntryId() != null) {
						//							uniref90 = proteinData.getUniRef90EntryId();
						//						}
						//						if (proteinData.getUniRef50EntryId() != null) {
						//							uniref50 = proteinData.getUniRef50EntryId();
						//						}

						ReducedProteinData uniRefs = getUniRefByUniProtAcc(accessionsMap.get(oriAccession));
						Uniprotentry.addUniProtEntryWithProteinID((Long) proteinid, taxID, ecNumbers, koNumbers, keywords, uniRefs.getUniRef100EntryId(), uniRefs.getUniRef90EntryId(), uniRefs.getUniRef50EntryId(), conn);

						counter++;

						if (counter % 500 == 0) {
							//							System.out.println(counter + "/" + accessionsList.size() + " UniProt entries have been updated.");
							conn.commit();
						}

					}else{
						//						System.out.println("No UniProt data available for " + accessionsMap.get(oriAccession));
					}
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
				// Final commit and clearing of map.
				//					System.out.println("UniProt entries have been updated.");
				Client.getInstance().firePropertyChange("new message", null, "UPDATING UNIPROT ENTRIES FINISHED");
				conn.commit();
			}
		}else{
			System.out.println("Supplied no data for querying.");
		}
	}

	/**
	 * Method to repair empty UniProt references
	 * @throws SQLException 
	 */
	@Deprecated
	public static void repairEmptyUniProtEntries() throws SQLException{
		repairEmptyUniProtEntriesAndBLAST(null, null, 0.0, false);
	}

	/**
	 * Method to repair empty UniProtEntries and if they are unknown to BLAST them
	 * @param blastFile. The file of the blast algorithm.
	 * @param database. The database for BLAST search.
	 * @param eValue. The evalue cutoff for the BLAST search.
	 * @param blast. Flag for include BLAST or not
	 */
	@Deprecated
	public static void repairEmptyUniProtEntriesAndBLAST(String blastFile, String database, double eValue, boolean blast) throws SQLException{

		// Path of the taxonomy dump folder
		Connection conn = DBManager.getInstance().getConnection();
		// Map for proteins without UniProt entry <ACCESSION, PROTID>
		Map<String, Long> noUniProtProteinHits = new HashMap<String, Long>();
		Map<String, Long> proteins = ProteinAccessor.findAllProteins(conn);
		System.out.println("Number Proteins: " + proteins.size());
		Set<Entry<String, Long>> entrySet2 = proteins.entrySet();
		// Fetch all proteins, and put them into the noUniProtProteinHits map, if they process no UniProt entry
		for (Entry<String, Long> entry : entrySet2) {
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(entry.getValue(), conn);
			if (uniprotentry == null) noUniProtProteinHits.put(entry.getKey(), entry.getValue());
		}

		System.out.println("Unlinked proteins: " + noUniProtProteinHits.size());

		Set<String> keySet = noUniProtProteinHits.keySet();
		// Mapping for the entries without UniProt Entry --> <ORIGINAL_ACCESSION, UNIPROT_ACCESSION>
		Map<String, String> accessionsMap = new HashMap<String, String>();
		for (String acc : keySet) {			
			// UniProt accession --> gets only UniProt entries as long as they have an UniProt accession
			if (acc.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				accessionsMap.put(acc, acc);		
			}else{
				if (blast) {
					ProteinAccessor prot = ProteinAccessor.findFromID(noUniProtProteinHits.get(acc), conn);

					DbEntry dbEntry = new DbEntry(prot.getAccession(), prot.getAccession(), DB_Type.UNIPROTSPROT, null);
					dbEntry.setSequence(prot.getSequence());
					// BLAST application
					BlastResult blastRes = RunBlast.blast(blastFile, database, eValue, dbEntry);
					Map<String, BlastHit> blastHitsMap = blastRes.getBlastHitsMap();

					BlastHit bestBlastHit = blastRes.getBestBitScoreBlastHit();
					if (bestBlastHit != null ) {
						// Get best BLAST hit
						accessionsMap.put(acc, blastRes.getBestBitScoreBlastHit().getAccession()); 
						String newDescription = "MG: " + blastRes.getBestBitScoreBlastHit().getAccession() + " " +blastRes.getBestBitScoreBlastHit().getName() + " Bitscore: " + blastRes.getBestBitScoreBlastHit().getScore();
						System.out.println("BLAST" + acc + " blasted to: " + blastRes.getBestBitScoreBlastHit().getAccession());
						ProteinAccessor.upDateProteinEntry(prot.getProteinid(), prot.getAccession(), newDescription, prot.getSequence(), prot.getCreationdate(),  conn);
					}
				}
			}
		}
		System.out.println("Found " + accessionsMap.size() + " with unlinked proteins to UniProt entries.");

		fetchEmptyUniProtEntries(accessionsMap);
	}

	/**
	 * This method queries the UniRef entry for a certain uniProtaccession.
	 * If no uniRef information is available it returns null for all UniRefs.
	 * However if at least one UniRef informations is available it fills the remaining UniRefs with 
	 * the UniProt Accession + "UniRefXXX". This is due to uncomplete information from uniProt
	 * 
	 * @param acc. The protein accession
	 */
	public static ReducedProteinData getUniRefByUniProtAcc(String accession){

		// Creates a default UniRef 
		ReducedProteinData redProtEntry = new ReducedProteinData(null, null, null, null);

		// Create entry retrival service
		EntryRetrievalService entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();

		// Query UniRef information
		ProteinData proteinData = entryRetrievalService.getProteinData(accession);

		// Check whether any data were returned
		if (proteinData != null) {
			// Get the UniRefs
			String Ref100 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef100).getUniRefEntryId().getValue();
			String Ref90 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef90).getUniRefEntryId().getValue();
			String Ref50 = proteinData.getUniRefEntry(UniRefDatabaseType.UniRef50).getUniRefEntryId().getValue();

			// Check if any UniRef information is available. This looks not really straight forward, but for some proteins 
			// the UniProt query service returns only the lowest UniRef50 cluster, UNIProt API returns "" if UniProt entry is similar
			//TODO Keep an eye on the results of the UniProt query service
			if (!(Ref100.equals("") && Ref90.equals("")  && Ref50.equals(""))) {
				// Creates the UniRef entry with the protein accession
				redProtEntry = new ReducedProteinData(proteinData.getUniProtEntry(), "UniRef100_" + accession, "UniRef90_" + accession, "UniRef50_" + accession);
				// Update the UniRefs to the queried UniRefs
				if (!Ref100.equals("")) {
					redProtEntry.setUniRef100EntryId(Ref100);
					redProtEntry.setUniRef90EntryId(Ref100.replaceAll("UniRef100_", "UniRef90_"));
					redProtEntry.setUniRef50EntryId(Ref100.replaceAll("UniRef100_", "UniRef50_"));
				}
				// Update the UniRefs to the queried UniRefs
				if (!Ref90.equals("")) {
					redProtEntry.setUniRef90EntryId(Ref90);
					redProtEntry.setUniRef50EntryId(Ref90.replaceAll("UniRef90_", "UniRef50_"));
				}
				// Update the UniRefs to the queried UniRefs
				if (!Ref50.equals("")) {
					redProtEntry.setUniRef50EntryId(Ref50);
				}
			}
		}
		return redProtEntry;
	}
}
