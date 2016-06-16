package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.net.bsd.RExecClient;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntryType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import com.compomics.util.protein.Protein;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.blast.BlastHit;
import de.mpa.client.blast.BlastResult;
import de.mpa.client.blast.DbEntry;
import de.mpa.client.blast.DbEntry.DB_Type;
import de.mpa.client.blast.RunMultiBlast;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.MascothitTableAccessor;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.OmssahitTableAccessor;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.Pep2protTableAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.ProteinTableAccessor;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.accessor.UniprotentryTableAccessor;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.accessor.XtandemhitTableAccessor;
import de.mpa.main.Starter;
import de.mpa.util.Formatter;


/**
 * Class to access the EBI UniProt WebService.
 * @author T.Muth, A. Behne, R. Heyer
 * @date 19-12-2013
 */
public class UniProtUtilities {

	// Max clause count == BATCH SIZE is set to 1024
	/**
	 *  Max batch size constant, default is 1024 
	 */	
	private static final int BATCH_SIZE = 1024;

	/**
	 * UniProt service instance. 
	 */	
	private UniProtService uniProtQueryService = null;
	
	/**
	 * UniRef service instance.
	 */
	private UniRefService uniRefQueryService = null;
	
	
	/**
	 *  Constructor for this class
	 */	
	public UniProtUtilities() {
		// constructor is empty for now
	}

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
	
	// ACTUAL METHODS
	/**
	 * Class that starts the UniProtService
	 * @author K.Schallert
	 */	
	public void startUniProtService() {
		ServiceFactory serviceFactoryInstance = uk.ac.ebi.uniprot.dataservice.client.Client.getServiceFactoryInstance();			
		this.uniProtQueryService = serviceFactoryInstance.getUniProtQueryService();
		uniProtQueryService.start();
	}
	
	/**
	 * Class that starts the UniProtService
	 * @author K.Schallert
	 */	
	public void stopUniProtService() {
		this.uniProtQueryService.stop();
	}	
	
	/**
	 * Class that starts the UniRefService
	 * @author K.Schallert
	 */	
	public void startUniRefService() {
		ServiceFactory serviceFactoryInstance = uk.ac.ebi.uniprot.dataservice.client.Client.getServiceFactoryInstance();			
		this.uniRefQueryService = serviceFactoryInstance.getUniRefQueryService();
		this.uniRefQueryService.start();
	}
	
	/**
	 * Class that stops the UniRefService
	 * @author K.Schallert
	 */	
	public void stopUniRefService() {
		this.uniRefQueryService.stop();
	}

	/**
	 * create uniprot-entries from accessionlist
	 * given an accessionlist from a search, we can create uniprot-entries
	 * 
	 * @author K.Schallert
	 */	
	public Map<String, ReducedProteinData> getUniProtData(List<String> accessionList) {
		// initialize return value
		Map<String, ReducedProteinData> proteinData = new TreeMap<String, ReducedProteinData>();
		// make batches and call methods for querying uniprot and for storing to DB
		// make batches based on accessionlist size
		int maxClauseCount = BATCH_SIZE;
		int maxBatchCount = accessionList.size() / maxClauseCount;
		if (maxBatchCount == 0) {
			maxBatchCount = 1;	
		}	
		// feedback for user
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "QUERYING UNIPROT FOR " + accessionList.size() + " ENTRIES");
		}
		// loop through i batches
		for (int i = 0; i < maxBatchCount; i++) {
			// indexes to access accessionlist
			int startIndex = i * maxClauseCount;
			int endIndex = (i + 1) * maxClauseCount - 1;
			// Sets are required for uniprot queries, this set contains the current batch of accessions
			Set<String> shortList;
			// case for last batch
			if (i == (maxBatchCount - 1)) {
				if (startIndex != (accessionList.size() -1)) {
					shortList = new HashSet<String>(accessionList.subList(startIndex, accessionList.size()));
				} else {
					shortList = new HashSet<String>(accessionList);
				}
				
			} else {
				shortList = new HashSet<String>(accessionList.subList(startIndex, endIndex));
			}
			// catch empty/null errors
			if (shortList != null && shortList.size()>0 ) {
				// get uniprotdata for this batch, proteindata
				this.processBatch(shortList, proteinData);
				shortList.clear();
			}
			// feedback on batches
			if (Client.getInstance() != null) {
				Client.getInstance().firePropertyChange("new message", null, "BATCH " + (i + 1) + " OF " + maxBatchCount + " FINISHED");
			}
		}
		// what does this do?
//		Set<String> newIDList = new HashSet<String>(identifierList.subList(maxBatchCount * maxClauseCount, identifierList.size()));
//		if (newIDList != null ||newIDList.size()>0) {
//			queryUniProtEntriesByIdentifiers(newIDList, proteinData, doUniRefRetrieval);
//		}
		// return data
		return proteinData;
	}
	
	/**
	 * process a batch of accessions and retrieve uniprot information 
	 * 
	 * @author K.Schallert
	 */	
	public Map<String, ReducedProteinData> processBatch(Set<String> accessionList, Map<String, ReducedProteinData> proteinData) {
		// check for null
		if (accessionList !=null && accessionList.size()>0) {
			// some feedback for user
			if (Client.getInstance() != null) {				
				Client.getInstance().firePropertyChange("resetall", -1L, (long) accessionList.size());
				Client.getInstance().firePropertyChange("resetcur", -1L, (long) accessionList.size());
			}
			// start uniprotservice
			this.startUniProtService();
			
			// Query UniProt
			QueryResult<UniProtEntry> entryIterator = null;
			try {			
				Query query = UniProtQueryBuilder.accessions(accessionList);									
				entryIterator = uniProtQueryService.getEntries(query);				
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			// Iterate the entries and add them to the list. 
			while (entryIterator.hasNext()) {
				// make new uniprot entries
				UniProtEntry entry = entryIterator.next();
				ReducedProteinData thisprotein = new ReducedProteinData(entry);			
				String accession = entry.getPrimaryUniProtAccession().getValue();
				// Get the protein data + uniRef entry 
				thisprotein = this.getUniRefs(accession, thisprotein);
				// put into returnmap
				proteinData.put(accession, thisprotein);
				// progress on this batch
				
				if (Client.getInstance() != null) {
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
			}	
			// stop uniprotservice
			this.stopUniProtService();
		}
		return proteinData;
	}

	/**
	 * update uniprot entries for proteins that do not hava a uniprot entry 
	 * 
	 * @author K.Schallert
	 */	
	public ReducedProteinData getUniRefs(String accession, ReducedProteinData redProtEntry) {
		// start the uniref service
		this.startUniRefService();
		// Get the UniRefs
		Query query = UniRefQueryBuilder.memberAccession(accession);
		QueryResult<UniRefEntry> entries = null;
		try {		
			entries = this.uniRefQueryService.getEntries(query);
		} catch (ServiceException e) {
			e.printStackTrace();
		}		
		if (entries != null) {
			while (entries.hasNext()) {
				try {
					UniRefEntry thisentry = entries.next();	
					if (thisentry.getUniRefEntryId().getValue().contains("UniRef100")) {
						redProtEntry.setUniRef100EntryId(thisentry.getUniRefEntryId().getValue());
					}
					if (thisentry.getUniRefEntryId().getValue().contains("UniRef90")) {
						redProtEntry.setUniRef90EntryId(thisentry.getUniRefEntryId().getValue());
					}
					if (thisentry.getUniRefEntryId().getValue().contains("UniRef50")) {
						redProtEntry.setUniRef50EntryId(thisentry.getUniRefEntryId().getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Accession: "+accession+ " broken uniref entries: " + redProtEntry);
				}
			}
		} else {
			// TODO: add something to deal with this problem
			System.out.println("query failed!");
		}
		// stop the unirefservice and return
		this.stopUniRefService();
		return redProtEntry;
	}
	
	/**
	 * repair unirefs
	 * 
	 * @author K.Schallert
	 */		
	public void repairUniRefs() throws SQLException {
		// connect to database
		Connection conn = DBManager.getInstance().getConnection();
		// initialize variables for commit
		// Get all uniprotEntries.
		List<UniprotentryTableAccessor> uniprotList = Uniprotentry.retrieveAllEntriesWithEmptyUniRefAnnotation(conn);
		// number of uniref entries to be processed
		Client.getInstance().firePropertyChange("new message", null, "FOUND " + uniprotList.size() + " UNIPROT ENTRIES MISSING ANNOTATION");
		Client.getInstance().firePropertyChange("resetall", -1L, (long) uniprotList.size());
		Client.getInstance().firePropertyChange("resetcur", -1L, (long) uniprotList.size());
		// maps accessions to uniprotentries in SQL
		Map<String, UniprotentryTableAccessor> uniprotAccessionMap = new HashMap<String, UniprotentryTableAccessor>();
		// lists all accessions
		List<String> accessions = new ArrayList<String>();
		// this loop gets the accession-uniprotentry-map
		for (int i = 0; i < uniprotList.size(); i++) {
			// current uniprotentry
			UniprotentryTableAccessor uniProtEntry = uniprotList.get(i);
			// get its proteinaccession 
			ProteinAccessor proteinAccessor = null;
			proteinAccessor = ProteinAccessor.findFromID(uniProtEntry.getFk_proteinid(), conn);
			// add this accession to the map (this seems redundant)
			accessions.add(proteinAccessor.getAccession());
			// finally put it all into our map, maps accession to uniprotentry
			uniprotAccessionMap.put(proteinAccessor.getAccession(), uniProtEntry);
		}
		// loop through accessions, retrieve only unirefs, update uniprotentry in db
		for (String accession : uniprotAccessionMap.keySet()) {			
			ReducedProteinData uniRefs = new ReducedProteinData(null);
			// call to retrieve unirefs
			this.getUniRefs(accession, uniRefs);
			uniprotAccessionMap.get(accession).setUniref100(uniRefs.getUniRef100EntryId());
			uniprotAccessionMap.get(accession).setUniref90(uniRefs.getUniRef90EntryId());
			uniprotAccessionMap.get(accession).setUniref50(uniRefs.getUniRef50EntryId());
			// update database and commit
			uniprotAccessionMap.get(accession).update(conn);
			conn.commit();
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		// Final commit
		conn.commit();
		// finished
		Client.getInstance().firePropertyChange("new message", null, "FINISHED UPDATING UNIPROT ENTRIES");
	}
	
	/**
	 * perform blast or just get uniprot-entries
	 * 
	 * @author K.Schallert
	 */		
	public void blast(Set<Long> proteins, String blastFile, String blastDatabase, double eValue, boolean blast) throws SQLException {
		// make connection
		Connection conn = DBManager.getInstance().getConnection();
		// Find all proteins without a UniProt entry
		Set<Long> unlinkedProteins = new HashSet<Long>();
		for (Long ID : proteins) {
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(ID, conn);
			// TODO Needs a check for corrupted uniprot entries (for instance because of missing taxonomy information)
			// if there is no uniprotentry we add the ID
			if (uniprotentry == null) {
				unlinkedProteins.add(ID);
			}
		}
		// message how many unlinked proteins we have
		Client.getInstance().firePropertyChange("new message", null, "FOUND " + unlinkedProteins.size() + " UNLINKED PROTEINS");
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
			} else if (accession.contains("_BLAST_")) {
				// dealing with newly created blast accessions that miss a uniprot entry
				String newacc = accession.split("_BLAST_")[1];
				accessionsMap.put(accession, newacc);
			} else {				
				blastProteins.add(accProt);
			}
		}
		// do uniprotretrievel on proteins not marked for blast
		this.fetchEmptyUniProtEntries(accessionsMap);
				
		// perform blast
		if (blast) {			
			// status can be finished or failed
			String status;
			// make batches
			Map<String, BlastResult> blastBatch;
			if (!blastProteins.isEmpty()) {				
				// Make DbEntries from the proteins in question
				List<DbEntry> blastEntries = new ArrayList<DbEntry>();
				for (ProteinAccessor prot : blastProteins) {
					DbEntry dbEntry = new DbEntry(prot.getAccession(),
							prot.getAccession(), DB_Type.UNIPROTSPROT, null);					
					if (prot.getSequence() != null) {
						dbEntry.setSequence(prot.getSequence());
						blastEntries.add(dbEntry);
					} else {
						System.out.println("Broken Protein Entry: " + dbEntry.getIdentifier());
					}
				}
				// how many proteins are blasted
				Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST ON " + blastEntries.size() + " PROTEINS");
				Client.getInstance().firePropertyChange("indeterminate", false,	true);
				// Actually Start the BLAST
				RunMultiBlast blaster = new RunMultiBlast(blastFile, blastDatabase, eValue, blastEntries);
				status = "FINISHED";				
				try {
					System.out.println("Actual blast");
					blaster.blast();
					System.out.println("Actual blast done");
				} catch (IOException e) {
					e.printStackTrace();
					status = "FAILED";
				}
				
				// get results and submit to db
				blastBatch = blaster.getBlastResultMap();				
				// update database with new proteins				
				for (ProteinAccessor prot : blastProteins) {
					// store the old accession
					String old_accession = prot.getAccession();	
					// get proteinid for the old protein entry
					Long old_proteinid = prot.getProteinid();
					// get protein_sequence from the old protein entry
					String old_sequence = prot.getSequence();				
					// Get best BLAST hit
					if (blastBatch.get(old_accession) != null) {
						// best blast hit, used for ??
						//BlastHit bestBlastHit = blastBatch.get(old_accession).getBestBitScoreBlastHit();
						// list of all blasthits that satisfy the evalue threshold  (evalue check is redundant)
						List<BlastHit> allblasthits = blastBatch.get(old_accession).getBestBlastHits(eValue);		
						// get all the protein information that needs to be copied (only in case of multiple blast hits for this accession)
						// inits
						List<Pep2prot> peptoprot_list = null;
						List<MascothitTableAccessor> mascothit_list = null;
						List<OmssahitTableAccessor> omssahit_list = null;
						List<XtandemhitTableAccessor> xtandemhit_list = null;
						// but only if batch contains more than one entry (saves time)						
						if (allblasthits.size() > 1) {
//							System.out.println("blastbatch "+blastBatch.size());
//							System.out.println("looking up old stuff: " + old_proteinid);
							// make sql-queries 
							// get pep2prots for this protein							
							peptoprot_list = Pep2prot.get_pep2prots_for_proteinid(old_proteinid, conn);
//							System.out.println("pep2prot: " + peptoprot_list.size());
							// get mascothits for this protein
							mascothit_list = Mascothit.getHitsFromProteinID(old_proteinid, conn);
//							System.out.println("mascothit: " + mascothit_list.size());
							// get omssahits for this protein
							omssahit_list = Omssahit.getHitsFromProteinid(old_proteinid, conn);
//							System.out.println("omssahit: " + omssahit_list.size());
							// get xtandemhits for this protein
							xtandemhit_list = XTandemhit.getHitsFromProteinID(old_proteinid, conn);
//							System.out.println("xtandemhit: " + xtandemhit_list.size());
						}
						
						// TODO: here we can add a check to determine if a blast-protein contains any or all of the peptides found in the search
						
						// get protein sequence (from where?)
						// get all peptide sequences that we actually found (from sql-db)
						
						// needs protein-sequence and the protein-sequences for all pep2prot-entries, then the "allblasthits"-list can be reduced 
				
						// mark first hit, because one protein entry has to be updated
						// TODO: this code is very slow, because of all the sql-updates for copied entries 
						boolean first_hit = true;
						for (BlastHit hit : allblasthits) {
							// new accession is constructed from the old accession and the accession from BLAST result
							String new_accession = old_accession + "_BLAST_" + hit.getAccession();														
							// new description TODO: new accessions might cause problems somewhere else (check this) 
							String newDescription = "MG: " + hit.getName() + " [" + hit.getAccession() + "] Score: " + hit.getScore();							
							// feedback
							//System.out.println("BLAST query " + hit.getAccession() + " e-value: " + hit.geteValue() + " score: " + hit.getScore() + " as: " + newDescription);	
							// mark this accession for uniprot update (the hit-accession is used for unprot lookup)  
							accessionsMap.put(new_accession, hit.getAccession());	
							// if we have 
							if (first_hit) {
								//System.out.println("First hit");
								// update the old entry
								ProteinAccessor.upDateProteinEntry(prot.getProteinid(),
										new_accession, newDescription,
										prot.getSequence(), prot.getCreationdate(),
										conn);
								// unmark first hit
								first_hit = false;
							} else {
								//System.out.println("other hits");
								// make new entries 								
								// create new protein entry
    							// this adds a new protein
    							ProteinAccessor protAccessor = ProteinAccessor.addProteinToDatabase(new_accession, newDescription, old_sequence, conn);								
								// create pep2prot references
								for (Pep2prot old_pep2prot_entry : peptoprot_list) {
									Pep2prot.linkPeptideToProtein(old_pep2prot_entry.getFk_peptideid(), protAccessor.getProteinid(), conn);
								}
								// create duplicate mascothits
								for (MascothitTableAccessor old_mascothit : mascothit_list) {								
									Mascothit.copymascothit(protAccessor.getProteinid(), old_mascothit, conn);
								}
								// create duplicate xtandemhits
								for (XtandemhitTableAccessor old_xtandemhit : xtandemhit_list) {								
									XTandemhit.copyxtandemhit(protAccessor.getProteinid(), old_xtandemhit, conn);
								}								
								// create duplicate omssahits
								for (OmssahitTableAccessor old_omssahit : omssahit_list) {								
									Omssahit.copyomssahit(protAccessor.getProteinid(), old_omssahit, conn);
								}
								// anything missing?--> commit!?							
							}
							conn.commit();
						}
						conn.commit();
					}
				}
				Client.getInstance().firePropertyChange("indeterminate", true, false);
				Client.getInstance().firePropertyChange("new message", null, "BLAST FOUND " + blastBatch.size() + " PROTEINS");
				Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST " + status);
			}
		}

		// do uniprotretrievel on blasthits
		this.fetchEmptyUniProtEntries(accessionsMap);
	}
	
	/**
	 * Method to repair empty UniProtEntries, more or less
	 * @param accessionsMap mapping with original accessions and newly found UniProt accessions.
	 * @throws SQLException
	 */
	public void fetchEmptyUniProtEntries(Map<String, String> accessionsMap) throws SQLException {	
		if (!(accessionsMap.isEmpty())) {
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
			proteinDataMap = this.getUniProtData(accessionsList);
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
						ReducedProteinData uniRefs = new ReducedProteinData(uniProtEntry);
						uniRefs = this.getUniRefs(accessionsMap.get(oriAccession), uniRefs);
						Uniprotentry.addUniProtEntryWithProteinID((Long) proteinid, taxID, ecNumbers, koNumbers, keywords, uniRefs.getUniRef100EntryId(), uniRefs.getUniRef90EntryId(), uniRefs.getUniRef50EntryId(), conn);
						counter++;
						if (counter % 500 == 0) {
							conn.commit();
						}

					} else {
						System.out.println("No UniProt data available for " + accessionsMap.get(oriAccession));
					}
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
				// Final commit and clearing of map.
				Client.getInstance().firePropertyChange("new message", null, "UPDATING UNIPROT ENTRIES FINISHED");
				conn.commit();
			}
		}else{
			System.out.println("Supplied no data for querying.");
		}
	}	

	/**
	 * deletes blast hits
	 * 
	 * @author K.Schallert
	 * @throws SQLException 
	 */	
	public static void deleteblasthits() throws SQLException {
		// get client
		Client.getInstance();
		Client.getInstance().firePropertyChange("new message", null, "DELETING BLAST RESULTS");

		// connect to db
		Connection conn = DBManager.getInstance().getConnection();
		// find all blast proteins -> contain "_BLAST_"
		List<ProteinTableAccessor> proteinlist = ProteinAccessor.findBlastHits(conn);
		// we now need to make 2 new lists: one list for entries that remain, one for entries to delete
		List<ProteinTableAccessor> deletelist = new ArrayList<ProteinTableAccessor>();
		Map<String, ProteinTableAccessor> revertmap = new TreeMap<String, ProteinTableAccessor>();
		for (ProteinTableAccessor prot : proteinlist) {
			String original_prot_accession = prot.getAccession().split("_BLAST_")[0];
			// String uniprot_prot_accession = prot.getAccession().split("_BLAST_")[1];
			if (revertmap.containsKey(original_prot_accession)) {
				deletelist.add(prot);
			} else {
				revertmap.put(original_prot_accession, prot);
			}
		}
		Client.getInstance().firePropertyChange("resetall", -1L, (long) revertmap.size());
		Client.getInstance().firePropertyChange("resetcur", -1L, (long) revertmap.size());
//		System.out.println("revertlist: "+revertmap.size());
//		System.out.println("deletelist: "+deletelist.size());
		// cycle through proteins for reversion
		for (String prot_acc : revertmap.keySet()) {
//			System.out.println("Reverting: "+prot_acc);
			ProteinTableAccessor thisprot = revertmap.get(prot_acc);
			thisprot.setAccession(prot_acc);			
			thisprot.setDescription("Metagenome Unknown");			
			// find the uniprotentry to this protein 
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(thisprot.getProteinid(), conn);
			// and delete it
			if (uniprotentry != null) {
				uniprotentry.delete(conn);
			}
			thisprot.update(conn);
			conn.commit();
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		conn.commit();
		
		Client.getInstance().firePropertyChange("resetall", -1L, (long) deletelist.size());
		Client.getInstance().firePropertyChange("resetcur", -1L, (long) deletelist.size());		
		// cylce through proteins for deletion 
		for (ProteinTableAccessor protein : deletelist) {
//			System.out.println("Deleting: "+protein.getAccession());
			// find  omssahits to this protein 
			List<OmssahitTableAccessor> omssahits = Omssahit.getHitsFromProteinid(protein.getProteinid(), conn);
			for (OmssahitTableAccessor omssahit : omssahits) {
				// and delete them
				omssahit.delete(conn);
			}
			// find  mascothits to this protein 
			List<MascothitTableAccessor> mascothits = Mascothit.getHitsFromProteinID(protein.getProteinid(), conn);
			for (MascothitTableAccessor mascothit : mascothits) {
				// and delete them
				mascothit.delete(conn);
			}
			// find  mascothits to this protein 
			List<XtandemhitTableAccessor> xtandemhits = XTandemhit.getHitsFromProteinID(protein.getProteinid(), conn);
			for (XtandemhitTableAccessor xtandemhit : xtandemhits) {
				// and delete them
				xtandemhit.delete(conn);
			}
			// find  pep2prot to this protein 
			List<Pep2prot> pep2prots = Pep2prot.get_pep2prots_for_proteinid(protein.getProteinid(), conn);
			for (Pep2prot pep2prot : pep2prots) {
				// and delete them
				pep2prot.delete(conn);
			}
			// find the uniprotentry to this protein 
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(protein.getProteinid(), conn);
			// and delete them
			if (uniprotentry != null) {
				uniprotentry.delete(conn);	
			}			
			// delete proteinhit, next item
			protein.delete(conn);
			conn.commit();
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		conn.commit();
		Client.getInstance().firePropertyChange("new message", null, "DELETING BLAST RESULTS FINISHED");
	}	

	// METHODS FROM FASTALOADER
	
	/**
	 * Returns a protein object queried by the UniProt webservice (if no indexed FASTA is available.
	 * @param id Protein accession.
	 * @return Protein object containing header + sequence.
	 */
	public Protein getProteinFromWebService(String id) {
		// start service
		this.startUniProtService();		
		// Retrieve UniProt entry by its accession number
		UniProtEntry entry = null;
		try {
			entry = (UniProtEntry) this.uniProtQueryService.getEntry(id);
		} catch (ServiceException e) {
			e.printStackTrace();
		}		
		String header = ">";
		if(entry.getType() == UniProtEntryType.TREMBL) {
			header += "tr|";
		} else if(entry.getType() == UniProtEntryType.SWISSPROT) {
			header += "sw|";
		}
		header += id + "|";
		header += this.getProteinName(entry.getProteinDescription());
		String sequence = entry.getSequence().getValue();
		this.stopUniProtService();
		return new Protein(header, sequence);
	}
	
	/**
	 * Returns the protein name(s) as formatted string
	 * @param desc ProteinDescription object.
	 * @return Protein name(s) as formatted string.
	 */
	public String getProteinName(ProteinDescription desc) {
		Name name = null;
		
		if (desc.hasRecommendedName()) {
			name = desc.getRecommendedName();
		} else if (desc.hasAlternativeNames()) {
			name = desc.getAlternativeNames().get(0);
		} else if (desc.hasSubNames()) {
			name = desc.getSubNames().get(0);
		}
		return (name == null) ? "unknown" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
	}

	// OTHER CLASSES / STATIC METHODS
	
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
//				InputStream is = ClassLoader.getSystemResourceAsStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "keywords-all.obo");
				br = new BufferedReader(new FileReader(new File(Constants.CONFIGURATION_DIR_PATH+  "keywords-all.obo")));
				
				
//				br = new BufferedReader(new InputStreamReader(is));
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
