package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.query.Query;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.client.model.dbsearch.UniRefEntryMPA;
import de.mpa.db.DBManager;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.main.Starter;


/**
 * Class to access the EBI UniProt WebService.
 * @author R. Heyer
 * @date 30-09-2016
 */
public class UniProtUtilities {

	// Max clause count == BATCH SIZE is set to 1024
	/**
	 *  Max batch size constant, default is 500 by UniProt. was bigger before, 0 for just one entry
	 */	
	public static final int BATCH_SIZE = 200;

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
	 * Class that starts the UniProtService
	 * @author K.Schallert
	 */	
	public void startUniProtService() {
		silenceOutput(true);
		ServiceFactory serviceFactoryInstance = uk.ac.ebi.uniprot.dataservice.client.Client.getServiceFactoryInstance();			
		this.uniProtQueryService = serviceFactoryInstance.getUniProtQueryService();
		uniProtQueryService.start();
		silenceOutput(false);
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
	 * A backup of the outputstream
	 */
	public PrintStream original = System.out;

//	public Map<String, List<Long>> perform_blast(List<ProteinAccessor> blastProteins_fulllist, String blastFile, String blastDatabase, double eValue) throws SQLException {
//		// how many proteins are blasted
//		Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST");
//		Client.getInstance().firePropertyChange("indeterminate", false,	true);
//		// return map is a map from proteinid to uniprot-search-accession
//		Map<String, List<Long>> uniprot_map = new TreeMap<String, List<Long>>();
//		// make connection
//		Connection conn = DBManager.getInstance().getConnection();
//		// initialize batch calculations
//		List<ProteinAccessor> blastProteins = new ArrayList<ProteinAccessor>();
//		int batchsize = 1000;
//		int batchamount = blastProteins_fulllist.size()/batchsize;
//		if ((blastProteins_fulllist.size() % batchsize) != 0) {
//			batchamount = batchamount + 1;
//		}
//		int found_protein_count = 0;
////		int no_hit_count = 0;
//		// iterate and create sublists
//		for (int batchnumber = 0; batchnumber < batchamount; batchnumber++) {
//			// not last batch, contains batchsize proteins
//			if (batchnumber != batchamount-1) { 
//				blastProteins = blastProteins_fulllist.subList(batchnumber*batchsize,((batchnumber+1)*batchsize));
//			// last batch
//			} else {
//				blastProteins = blastProteins_fulllist.subList(batchnumber*batchsize, blastProteins_fulllist.size());					
//			}
//			// how many proteins are blasted
//			Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST BATCH " + batchnumber + " OF " + batchamount);
//			Client.getInstance().firePropertyChange("indeterminate", false,	true);
//			// perform blast on sublist
//			// status can be finished or failed
//			String status;
//			// make batches
//			Map<String, BlastResult> blastBatch;
//			int broken_count = 0;
//			if (!blastProteins.isEmpty()) {				
//				// Make DbEntries from the proteins in question
//				List<DbEntry> blastEntries = new ArrayList<DbEntry>();
//				for (ProteinAccessor prot : blastProteins) {
//					DbEntry dbEntry = new DbEntry(prot.getAccession(), prot.getAccession(), DB_Type.UNIPROTSPROT, null);					
//					if (prot.getSequence() != null) {
//						dbEntry.setSequence(prot.getSequence());
//						blastEntries.add(dbEntry);
//					} else {
//						broken_count++;
////						System.out.println("Broken Protein Entry: " + dbEntry.getIdentifier());
//					}
//				}
//				Client.getInstance().firePropertyChange("new message", null, "FOUND " + broken_count + " BROKEN PROTEIN ENTRIES");
//				// Actually Start the BLAST
//				RunMultiBlast blaster = new RunMultiBlast(blastFile, blastDatabase, eValue, blastEntries);
//				status = "FINISHED";				
//				try {
//					blaster.blast();
//				} catch (IOException e) {
//					e.printStackTrace();
//					status = "FAILED";
//				}
//				// get results and submit to db
//				blastBatch = blaster.getBlastResultMap();
//				Client.getInstance().firePropertyChange("new message", null, "STORING BLAST HITS BATCH " + batchnumber + " OF " + batchamount);
//				Client.getInstance().firePropertyChange("indeterminate", false,	true);
//				Client.getInstance().firePropertyChange("resetall", -1L, (long) blastBatch.size());
//				Client.getInstance().firePropertyChange("resetcur", -1L, (long) blastBatch.size());
//				// update database with new proteins				
//				for (ProteinAccessor prot : blastProteins) {
//					// store the old accession
//					String old_accession = prot.getAccession();	
//					// get proteinid for the old protein entry
//					Long old_proteinid = prot.getProteinid();
//					// get protein_sequence from the old protein entry
//					String old_sequence = prot.getSequence();				
//					// Get best BLAST hit
//					if (blastBatch.get(old_accession) != null) {
//						// best blast hit, used for ??
//						//BlastHit bestBlastHit = blastBatch.get(old_accession).getBestBitScoreBlastHit();
//						// list of all blasthits that satisfy the evalue threshold  (evalue check is redundant)
//						List<BlastHit> allblasthits = blastBatch.get(old_accession).getBestBlastHits(eValue);		
//						// get all the protein information that needs to be copied (only in case of multiple blast hits for this accession)
//						// inits
//						List<Pep2prot> peptoprot_list = null;
//						List<MascothitTableAccessor> mascothit_list = null;
//						List<OmssahitTableAccessor> omssahit_list = null;
//						List<XtandemhitTableAccessor> xtandemhit_list = null;
//						// but only if batch contains more than one entry (saves time)
//						if (allblasthits.size() > 1) {
//							// make sql-queries 
//							// get pep2prots for this protein							
//							peptoprot_list = Pep2prot.get_pep2prots_for_proteinid(old_proteinid, conn);
//							// get mascothits for this protein
//							mascothit_list = Mascothit.getHitsFromProteinID(old_proteinid, conn);
//							// get omssahits for this protein
//							omssahit_list = Omssahit.getHitsFromProteinid(old_proteinid, conn);
//							// get xtandemhits for this protein
//							xtandemhit_list = XTandemhit.getHitsFromProteinID(old_proteinid, conn);
//						}
//						
//						// TODO: here we can add a check to determine if a blast-protein contains any or all of the peptides found in the search
//						
//						// get protein sequence (from where?)
//						// get all peptide sequences that we actually found (from sql-db)
//						
//						// needs protein-sequence and the protein-sequences for all pep2prot-entries, then the "allblasthits"-list can be reduced 
//				
//						// mark first hit, because one protein entry has to be updated
//						// TODO: this code is very slow, because of all the sql-updates for copied entries						
//						boolean first_hit = true;
//						int commitcount = 0;
//						for (BlastHit hit : allblasthits) {
//							commitcount++;
//							found_protein_count++;
//							// new accession is constructed from the old accession and the accession from BLAST result
//							String new_accession = old_accession + "_BLAST_" + hit.getAccession();														
//							// new description TODO: new accessions might cause problems somewhere else (check this) 
//							String newDescription = "MG: " + hit.getName() + " [" + hit.getAccession() + "] Score: " + hit.getScore();							
//							// if we have 
//							if (first_hit) {
//								// update the old entry
//								ProteinAccessor.upDateProteinEntry(prot.getProteinid(),
//										new_accession, newDescription,
//										prot.getSequence(), prot.getCreationdate(),
//										conn);
//								// unmark first hit
//								first_hit = false;
//								// mark this accession for uniprot update (the hit-accession is used for uniprot lookup)
//								if (uniprot_map.containsKey(hit.getAccession())) {									
//									uniprot_map.get(hit.getAccession()).add(prot.getProteinid());	
//								} else {
//									List<Long> idlist = new ArrayList<Long>();
//									uniprot_map.put(hit.getAccession(), idlist);
//								}
//							} else {
//								// make new entries 								
//								// create new protein entry
//    							// this adds a new protein
//    							ProteinAccessor protAccessor = ProteinAccessor.addProteinToDatabase(new_accession, newDescription, old_sequence, conn);								
//								// create pep2prot references
//								for (Pep2prot old_pep2prot_entry : peptoprot_list) {
//									Pep2prot.linkPeptideToProtein(old_pep2prot_entry.getFk_peptideid(), protAccessor.getProteinid(), conn);
//								}
//								// create duplicate mascothits
//								for (MascothitTableAccessor old_mascothit : mascothit_list) {								
//									Mascothit.copymascothit(protAccessor.getProteinid(), old_mascothit, conn);
//								}
//								// create duplicate xtandemhits
//								for (XtandemhitTableAccessor old_xtandemhit : xtandemhit_list) {								
//									XTandemhit.copyxtandemhit(protAccessor.getProteinid(), old_xtandemhit, conn);
//								}								
//								// create duplicate omssahits
//								for (OmssahitTableAccessor old_omssahit : omssahit_list) {								
//									Omssahit.copyomssahit(protAccessor.getProteinid(), old_omssahit, conn);
//								}
//								// mark this accession for uniprot update (the hit-accession is used for uniprot lookup)
//								if (uniprot_map.containsKey(hit.getAccession())) {									
//									uniprot_map.get(hit.getAccession()).add(protAccessor.getProteinid());	
//								} else {
//									List<Long> idlist = new ArrayList<Long>();
//									uniprot_map.put(hit.getAccession(), idlist);
//								}
//								// anything missing?--> commit!?							
//							}
//							if ((commitcount % 500) == 0) {
//								conn.commit();
//							}
//						}
//						conn.commit();
//					}
//				}
//				Client.getInstance().firePropertyChange("indeterminate", true, false);
//				Client.getInstance().firePropertyChange("new message", null, "BLAST FOUND " + found_protein_count + " PROTEINS");
//				Client.getInstance().firePropertyChange("new message", null, "RUNNING BLAST " + status);
//				Client.getInstance().firePropertyChange("resetall", -1L, 100L);
//				Client.getInstance().firePropertyChange("resetcur", -1L, 100L);
//			}
//		}
//		return uniprot_map;
//	}
	
//	public Map<String, List<Long>> find_unlinked_proteins(Map<String, Long> protein_map) throws SQLException {
//		Client.getInstance().firePropertyChange("new message", null, "FIND UNLINKED PROTEINS");
//		Client.getInstance().firePropertyChange("indeterminate", true,	false);
//		Client.getInstance().firePropertyChange("resetall", -1L, (long) protein_map.size());
//		Client.getInstance().firePropertyChange("resetcur", -1L, (long) protein_map.size());
//		//
//		Map<String, List<Long>> unlinked_map = new TreeMap<String, List<Long>>();
//		// make connection
//		Connection conn = DBManager.getInstance().getConnection();
//		// Find all proteins without a UniProt entry
//		for (String accession : protein_map.keySet()) {
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//			Long ID = protein_map.get(accession);
//			// if there is no uniprotentry we add the ID
//			if (!(Uniprotentry.check_if_UniProtEntry_exists_from_proteinID(ID, conn))) {
//				String uniprot_accession = null;
//				if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
//					uniprot_accession = accession;
//				} else if (accession.contains("_BLAST_")) {
//					uniprot_accession = accession.split("_BLAST_")[1];
//				} else {
//					// ignore
//				}
//				if (uniprot_accession != null) {
//					if (unlinked_map.containsKey(uniprot_accession)) {
//						unlinked_map.get(uniprot_accession).add(ID);
//					} else {
//						List<Long> prot_id_list = new ArrayList<Long>();
//						prot_id_list.add(ID);
//						unlinked_map.put(uniprot_accession, prot_id_list);
//					}
//				}
//			}
//		}
//		Client.getInstance().firePropertyChange("new message", null, "FOUND " + unlinked_map.size() + " UNLINKED PROTEINS");
//		return unlinked_map;
//	}
//	
//	public List<ProteinAccessor> find_proteins_for_blast(List<ProteinAccessor> proteins) {
//		Client.getInstance().firePropertyChange("new message", null, "FIND PROTEINS FOR BLAST");
//		Client.getInstance().firePropertyChange("indeterminate", true,	false);
//		Client.getInstance().firePropertyChange("resetall", -1L, (long) proteins.size());
//		Client.getInstance().firePropertyChange("resetcur", -1L, (long) proteins.size());
//		// Sort out which proteins need to be BLASTed
//		int uniprot_count = 0;
//		int already_blasted_count = 0;
//		int blast_proteins_count = 0;
//		List<ProteinAccessor> blastProteins_fulllist = new ArrayList<ProteinAccessor>();
//		for (ProteinAccessor prot : proteins) {
//			// get the current protein accession entry
//			String accession = prot.getAccession();
//			// there is an accession that looks like a UniProt accession but no UniProt entry
//			if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
//				uniprot_count++;
//			} else if (accession.contains("_BLAST_")) {
//				already_blasted_count++;
//			} else {
//				blast_proteins_count++;
//				blastProteins_fulllist.add(prot);
//			}
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//		}
//		Client.getInstance().firePropertyChange("new message", null, "FOUND " + uniprot_count + " UNIPROT PROTEINS");
//		Client.getInstance().firePropertyChange("new message", null, "FOUND " + already_blasted_count + " PROTEINS ALREADY BLASTED");
//		Client.getInstance().firePropertyChange("new message", null, "FOUND " + blast_proteins_count + " PROTEINS FOR BLAST");
//		Client.getInstance().firePropertyChange("new message", null, "FOUND " + blastProteins_fulllist.size() + " PROTEINS FOR BLAST");
//		return blastProteins_fulllist;
//	}
	
//	public List<RepairEntry> find_incomplete_uniprotentries() throws SQLException {
//		// find uniprotentries
//		List<RepairEntry> incomplete_uniprots = Uniprotentry.find_incomplete_uniprot_entries(Client.getInstance().getConnection());
//		// return map
//		return incomplete_uniprots;
//	}
	
//	/**
//	 * 
//	 * @param accessionMap
//	 * @throws SQLException
//	 */
//	public void make_uniprot_entries(Map<String, List<Long>> accessionMap) throws SQLException {
//		Client.getInstance().firePropertyChange("new message", null, "RETRIEVING UNIPROT ENTRIES");
//		Client.getInstance().firePropertyChange("indeterminate", false,	true);
//		int fail_count = 0;
//		// make connection
//		Connection conn = DBManager.getInstance().getConnection();
//		// just create a list of all the strings
//		List<String> accessions = new ArrayList<String>();
//		for (String key_accession : accessionMap.keySet()) {
//			accessions.add(key_accession);
//		}
//		// make batches and call methods for querying uniprot and for storing to DB
//		int maxClauseCount = BATCH_SIZE;
//		int maxBatchCount = accessions.size() / maxClauseCount;
//		if (maxBatchCount == 0) {
//			maxBatchCount = 1;	
//		}	
//		// loop through i batches
//		for (int i = 0; i < maxBatchCount; i++) {
//			Client.getInstance().firePropertyChange("new message", null, "UNIPROT BATCH " + (i+1) + " OF " + maxBatchCount);
//			Client.getInstance().firePropertyChange("indeterminate", true,	false);
//			Client.getInstance().firePropertyChange("resetall", -1L, (long) (2*BATCH_SIZE));
//			Client.getInstance().firePropertyChange("resetcur", -1L, (long) (2*BATCH_SIZE));
//			// indexes to access accessionlist
//			int startIndex = i * maxClauseCount;
//			int endIndex = (i + 1) * maxClauseCount - 1;
//			// Sets are required for uniprot queries, this set contains the current batch of accessions
//			Set<String> shortList;
//			// case for last batch
//			if (i == (maxBatchCount - 1)) {
//				if (startIndex != (accessions.size() -1)) {
//					shortList = new TreeSet<String>(accessions.subList(startIndex, accessions.size()));
//				} else {
//					shortList = new TreeSet<String>(accessions);
//				}
//			} else {
//				shortList = new TreeSet<String>(accessions.subList(startIndex, endIndex));
//			}
//			// catch empty/null errors
//			if (shortList != null && shortList.size()>0 ) {
//				Client.getInstance().firePropertyChange("new message", null, "UNIPROT BATCH " + (i+1) + " SIZE: " + shortList.size());
//				// get uniprotdata for this batch, proteindata
//				Map<String, ReducedProteinData> proteinData = new TreeMap<String, ReducedProteinData>();
//				this.processBatch(shortList, proteinData);
//				shortList.clear();
//				int counter = 0;
//				// here we commit stuff
//				if (!(proteinData.isEmpty())) {
//					for (String accession : proteinData.keySet()) {
//						UniProtEntry uniProtEntry = proteinData.get(accession).getUniProtEntry();
//						// iterate over protein ids
//						for (Long protid : accessionMap.get(accession)) {
//							// Get UniProt entry informations
//							// Get taxonomy id
//							Long taxID = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());
//							// Get EC Numbers.
//							String ecNumbers = "";
//							List<String> ecNumberList = uniProtEntry.getProteinDescription().getEcNumbers();
//							if (ecNumberList.size() > 0) {
//								for (String ecNumber : ecNumberList) {
//									ecNumbers += ecNumber + ";";
//								}
//								ecNumbers = Formatter.removeLastChar(ecNumbers);
//							}
//							// Get ontology keywords.
//							String keywords = "";
//							List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword> keywordsList = uniProtEntry.getKeywords();
//							if (keywordsList.size() > 0) {
//								for (uk.ac.ebi.kraken.interfaces.uniprot.Keyword kw : keywordsList) {
//									keywords += kw.getValue() + ";";
//								}
//								keywords = Formatter.removeLastChar(keywords);
//							}
//							// Get KO numbers.
//							String koNumbers = "";
//							List<DatabaseCrossReference> xRefs = uniProtEntry
//									.getDatabaseCrossReferences(DatabaseType.KO);
//							if (xRefs.size() > 0) {
//								for (DatabaseCrossReference xRef : xRefs) {
//									koNumbers += xRef.getPrimaryId().getValue() + ";";
//								}
//								koNumbers = Formatter.removeLastChar(koNumbers);
//							}
//							ReducedProteinData uniRefs = proteinData.get(accession);								
////							Uniprotentry.addUniProtEntryWithProteinID(protid, taxID, ecNumbers, koNumbers, keywords, uniRefs.getUniRef100EntryId(), uniRefs.getUniRef90EntryId(), uniRefs.getUniRef50EntryId(), conn);
//							counter++;
//							if (counter % 500 == 0) {
//								conn.commit();
//							}
//						}
//						Client.getInstance().firePropertyChange("progressmade", false, true);
//					}
//					conn.commit();
//				} else {
//					fail_count++;
//				}
//			}
//		}
//		Client.getInstance().firePropertyChange("new message", null, "NO DATA FOR " + fail_count + " PROTEINS");
//		Client.getInstance().firePropertyChange("new message", null, "UNIPROT RETRIEVAL FINISHED");
//		Client.getInstance().firePropertyChange("resetall", 100L, 100L);
//		Client.getInstance().firePropertyChange("resetcur", 100L, 100L);
//	}
	
//	public void repair_uniprotentries(List<RepairEntry> uniprot_for_repair) throws SQLException {
//		Connection conn = Client.getInstance().getConnection();
//		int commitcount = 0;
//		Client.getInstance().firePropertyChange("new message", null, "UPDATING " + uniprot_for_repair.size() + " UNIPROT-ENTRIES");
//		Client.getInstance().firePropertyChange("resetall", -1L, (long) uniprot_for_repair.size());
//		Client.getInstance().firePropertyChange("resetcur", -1L, (long) uniprot_for_repair.size());
//		this.startUniProtService();
//		// just loop through entries and update if necassary		
//		for (RepairEntry repairentry : uniprot_for_repair) {
//			commitcount++;
//			// Query UniProt
//			UniProtEntry entry = null;
//			try {
//				entry = uniProtQueryService.getEntry(repairentry.get_accession());				
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
//			if (entry != null) {
//				ReducedProteinData thisprotein = new ReducedProteinData(entry);			
//				String accession = entry.getPrimaryUniProtAccession().getValue();
//				// Get the protein data + uniRef entry 
//				thisprotein = this.getUniRefs(accession, thisprotein);
//				// Get taxonomy id
//				Long taxID = Long.valueOf(thisprotein.getUniProtEntry().getNcbiTaxonomyIds().get(0).getValue());
//				// Get EC Numbers.
//				String ecNumbers = "";
//				List<String> ecNumberList = thisprotein.getUniProtEntry().getProteinDescription().getEcNumbers();
//				if (ecNumberList.size() > 0) {
//					for (String ecNumber : ecNumberList) {
//						ecNumbers += ecNumber + ";";
//					}
//					ecNumbers = Formatter.removeLastChar(ecNumbers);
//				}
//				// Get ontology keywords.
//				String keywords = "";
//				List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword> keywordsList = thisprotein.getUniProtEntry().getKeywords();
//				if (keywordsList.size() > 0) {
//					for (uk.ac.ebi.kraken.interfaces.uniprot.Keyword kw : keywordsList) {
//						keywords += kw.getValue() + ";";
//					}
//					keywords = Formatter.removeLastChar(keywords);
//				}
//				// Get KO numbers.
//				String koNumbers = "";
//				List<DatabaseCrossReference> xRefs = thisprotein.getUniProtEntry().getDatabaseCrossReferences(DatabaseType.KO);
//				if (xRefs.size() > 0) {
//					for (DatabaseCrossReference xRef : xRefs) {
//						koNumbers += xRef.getPrimaryId().getValue() + ";";
//					}
//					koNumbers = Formatter.removeLastChar(koNumbers);
//				}
//				Uniprotentry.updateUniProtEntryWithProteinID(repairentry.get_uniprotentry().getUniprotentryid(), repairentry.get_proteinid(), taxID, ecNumbers, koNumbers, keywords, thisprotein.getUniRef100EntryId(), thisprotein.getUniRef90EntryId(), thisprotein.getUniRef50EntryId(), Client.getInstance().getConnection());
//				if ((commitcount % 500) == 0) {
//					conn.commit();
//				}
//			}
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//		}
//		conn.commit();
//		this.stopUniProtService();
//		Client.getInstance().firePropertyChange("new message", null, "UPDATING UNIPROT-ENTRIES FINISHED");
//	}
//	
//	/**
//	 * process a batch of accessions and retrieve uniprot information 
//	 * 
//	 * @author K.Schallert
//	 */	
//	public Map<String, ReducedProteinData> processBatch(Set<String> accessionList, Map<String, ReducedProteinData> proteinData) {
//		// check for null
//		if (accessionList !=null && accessionList.size()>0) {
//			// some feedback for user
//			if (Client.getInstance() != null) {	
//				Client.getInstance().firePropertyChange("resetall", -1L, (long) accessionList.size());
//				Client.getInstance().firePropertyChange("resetcur", -1L, (long) accessionList.size());
//			}
//			// start uniprotservice
//			this.startUniProtService();
//			
//			// Query UniProt
//			QueryResult<UniProtEntry> entryIterator = null;
//			try {			
//				Query query = UniProtQueryBuilder.accessions(accessionList);									
//				entryIterator = uniProtQueryService.getEntries(query);				
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
//			// Iterate the entries and add them to the list. 
//			while (entryIterator.hasNext()) {
//				// make new uniprot entries
//				UniProtEntry entry = entryIterator.next();
//				ReducedProteinData thisprotein = new ReducedProteinData(entry);			
//				String accession = entry.getPrimaryUniProtAccession().getValue();
//				// Get the protein data + uniRef entry 
//				thisprotein = this.getUniRefs(accession, thisprotein);
//				// put into returnmap
//				proteinData.put(accession, thisprotein);
//				// progress on this batch
//				
//				if (Client.getInstance() != null) {
//					Client.getInstance().firePropertyChange("progressmade", false, true);
//				}
//			}	
//			// stop uniprotservice
//			this.stopUniProtService();
//		}
//		return proteinData;
//	}
	
	/**
	 * Process a batch of uniprot queries and retrieve uniprot information 
	 * @param accessionList
	 * @author K. Schallert, modified by R. Heyer
	 * @return batchResultmap. A map containing the accessions and the UniProtEntries.
	 */
	public TreeMap<String, UniProtEntryMPA> processBatch(Set<String> batchAccessions, boolean addUniRef) {
		
		// The map with the results
		TreeMap<String, UniProtEntryMPA> batchResultMap = new TreeMap<String, UniProtEntryMPA>();
		
		// check for null
		if (batchAccessions !=null && batchAccessions.size()>0) {
			// some feedback for user
			if (Client.getInstance() != null) {	
				Client.getInstance().firePropertyChange("resetall", -1L, (long) batchAccessions.size());
				Client.getInstance().firePropertyChange("resetcur", -1L, (long) batchAccessions.size());
			}
			// start uniprotservice
			this.startUniProtService();
			
			
			// Query UniProt
			QueryResult<UniProtEntry> entryIterator = null;
			try {			
				Query query = UniProtQueryBuilder.accessions(batchAccessions);	
				entryIterator = uniProtQueryService.getEntries(query);		
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			// Iterate the entries and add them to the list. 
			System.out.println("Size of results" + entryIterator.getNumberOfHits());
			while (entryIterator.hasNext()) {
				// take the next uniprot entry
				
				// Avoid output stream
				silenceOutput(true);  
				UniProtEntry entry = entryIterator.next();
				silenceOutput(false);
				
				// Add a new entry to the map
				UniProtEntryMPA uniProtEntryMPA = new UniProtEntryMPA(entry, null);
				batchResultMap.put(entry.getPrimaryUniProtAccession().getValue(), uniProtEntryMPA);
				
				// progress on this batch
				if (Client.getInstance() != null) {
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
			}	
			
			// stop uniprotservice
			this.stopUniProtService();
		}
		
		// Add uniRefs
		if (addUniRef) {
			TreeMap<String, UniRefEntryMPA> uniRefMap = this.fetchUniRefEntriesByAccessions(batchAccessions);
			for (String key : batchResultMap.keySet()) {
				UniRefEntryMPA mpaUniRef = uniRefMap.get(key);
				if (mpaUniRef != null) {
					batchResultMap.get(key).setUniRefMPA(mpaUniRef);
				}
			}
		}
		return batchResultMap;
	}

	/**
	 * Fetches all uniprot entries for a FASTA list
	 * @param addUniRefs 
	 * @param fastaEntryList. A list with all fasta entries
	 * @return. Map with the accessions and the UniRefs
	 * @throws SQLException
	 */
	public TreeMap<String, UniProtEntryMPA> fetchUniProtEntriesByFastaEntryList(ArrayList<DigFASTAEntry> fastaEntryList, boolean addUniRefs) throws SQLException {
	
		// The resultmap with the uniprotEntries
		TreeMap<String, UniProtEntryMPA>  uniRef2AccMap = new TreeMap<String, UniProtEntryMPA>(); 
		
		// Create an accesion list 
		ArrayList<String> fastaAccessionList = new ArrayList<String>();
		for (DigFASTAEntry fastaEntry : fastaEntryList) {
			// Length of uniProt accessions is 6 for swissprot and 6 or 12 for trembl
			if (fastaEntry.getIdentifier().length() == 6 || fastaEntry.getIdentifier().length() == 10 ) {
				fastaAccessionList.add(fastaEntry.getIdentifier());	
			}
		}
		
		// Forward the accession list to the fetchUniProtEntriesByAccessions
		if (fastaAccessionList.size() > 0) {
			 uniRef2AccMap = this.fetchUniProtEntriesByAccessions(fastaAccessionList, addUniRefs);
		}
		
		 
		return uniRef2AccMap;
	}
	
	/**
	 * Fetches all uniref entries FASTA list
	 * @param fastaEntryList. A list with all fasta entries
	 * @param: query also UniRefEntries
	 * @return The uniProtResultmap with the accession as key
	 * @throws SQLException
	 */
	public TreeMap<String, UniRefEntryMPA>fetchUniRefEntriesByFastaEntryList(ArrayList<DigFASTAEntry> fastaEntryList) throws SQLException {
	
		// The resultmap with the uniprotEntries
		TreeMap<String, UniRefEntryMPA> uniprotResultMap = new TreeMap<String, UniRefEntryMPA>();
		
		// Create an accesion list 
		Set<String> fastaAccessionList = new TreeSet<String>();
		for (DigFASTAEntry fastaEntry : fastaEntryList) {
			fastaAccessionList.add(fastaEntry.getIdentifier());	
		}
		
		// Forward the accession list to the fetchUniProtEntriesByAccessions
		uniprotResultMap = this.fetchUniRefEntriesByAccessions(fastaAccessionList);
		
		return uniprotResultMap;
	}
	
	/**
	 * Fetches for an accession list the UniProtEntries via the uniprot webservice
	 * @param addUniRefs 
	 * @param accessionMap
	 * @param: query also UniRefEntries
	 * @throws SQLException
	 * @return A map with Accessions and uniprot entries
	 */
	@SuppressWarnings("unused")
	public TreeMap<String, UniProtEntryMPA>fetchUniProtEntriesByAccessions(ArrayList<String> accessionList, boolean addUniRefs) throws SQLException {
		
		// The resultmap with the uniprotEntries
		TreeMap<String, UniProtEntryMPA> uniprotResultMap = new TreeMap<String, UniProtEntryMPA>();
	
		// number of wrong entries
		int fail_count = 0;
		
		// some feedback for user
		if (Client.getInstance() != null) {	
		Client.getInstance().firePropertyChange("new message", null, "RETRIEVING UNIPROT ENTRIES");
		Client.getInstance().firePropertyChange("indeterminate", false,	true);
		}
		
		// make connection
		Connection conn = DBManager.getInstance().getConnection();
		
		// Sets are required for uniprot queries, this set contains the current batch of accessions
		Set<String> shortList = new TreeSet<String>(accessionList); 
		
//		shortList = 
//		// Proove whether you want to process more entries and make sublists then
//		if (BATCH_SIZE > 1) {
//			int maxClauseCount = BATCH_SIZE;
//			int maxBatchCount = accessionList.size() / maxClauseCount;
//			if (maxBatchCount == 0) {
//				maxBatchCount = 1;	
//			}	
//			// loop through i batches
//			for (int i = 0; i < maxBatchCount; i++) {
//				// some feedback for user
//				if (Client.getInstance() != null) {	
//					Client.getInstance().firePropertyChange("new message", null, "UNIPROT BATCH " + (i+1) + " OF " + maxBatchCount);
//					Client.getInstance().firePropertyChange("indeterminate", true,	false);
//					Client.getInstance().firePropertyChange("resetall", -1L, (long) (2*BATCH_SIZE));
//					Client.getInstance().firePropertyChange("resetcur", -1L, (long) (2*BATCH_SIZE));
//				}
//
//				// indexes to access sub accessionlist
//				int startIndex = i * maxClauseCount;
//				int endIndex = (i + 1) * maxClauseCount - 1;
//
//				// Break big list in sublist and make a of remaining entries for the last batch
//				if (i == (maxBatchCount - 1)) {
//					if (startIndex != (accessionList.size() -1)) {
//						shortList = new TreeSet<String>(accessionList.subList(startIndex, accessionList.size()));
//					} else {
//						shortList = new TreeSet<String>(accessionList);
//					}
//				} else {
//					shortList = new TreeSet<String>(accessionList.subList(startIndex, endIndex));
//				}
//			}
//		}else{
//			// Shortlist contains just one entry
//			shortList.clear();
//			shortList.add(accessionList.get(0));
//		}

			// catch empty/null errors
			if (shortList != null && shortList.size()>0 ) {
				
				// Process a batch of entries
				TreeMap<String, UniProtEntryMPA> batchResultMap = this.processBatch(shortList, addUniRefs);
				
				// Proove the correctness of the entries
				for (String key : shortList) {
//					System.out.println("rob " + key + " SIZE " + shortList.size());
					if (batchResultMap.get(key) == null) {
						fail_count++;
						System.out.println("RobertERROR fetching entry: " + key);
					}
				}
				// Add the results of the batch
				uniprotResultMap.putAll(batchResultMap);
				// reset the short list
				shortList.clear();
			}
		
		// some feedback for user
		if (Client.getInstance() != null) {	
		Client.getInstance().firePropertyChange("new message", null, "NO DATA FOR " + fail_count + " PROTEINS");
		Client.getInstance().firePropertyChange("new message", null, "UNIPROT RETRIEVAL FINISHED");
		Client.getInstance().firePropertyChange("resetall", 100L, 100L);
		Client.getInstance().firePropertyChange("resetcur", 100L, 100L);
		}
		
		return uniprotResultMap;
	}
	
	
	/**
	 * Fetches for an accession list the UniProtRef entries via the uniprot webservice
	 * @param Set with accessions
	 * @throws SQLException
	 * @return A map with accessions and uniRef entries
	 */
	public TreeMap<String, UniRefEntryMPA> fetchUniRefEntriesByAccessions(Set<String> accessionLists) {
		
		// The resultmap with the uniprotEntries
		TreeMap<String, UniRefEntryMPA> uniRefMap = new TreeMap<String, UniRefEntryMPA>();

		// some feedback for user
		if (Client.getInstance() != null) {	
			Client.getInstance().firePropertyChange("new message", null, "RETRIEVING UNIREF ENTRIES");
			Client.getInstance().firePropertyChange("indeterminate", false,	true);
		}

		// start the uniref service
		this.startUniRefService();

		// Fetch all uniRef entries from the list
		for (String acc : accessionLists) {
			UniRefEntryMPA uniRefEntry = null;
			try {
				uniRefEntry = fetchUniRefEntriesByAccession(acc);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Add UniRef to result map
			if (uniRefEntry != null) {
				uniRefMap.put(acc, uniRefEntry);
			}

		}		
		// stop the unirefservice and return
		this.stopUniRefService();
		
		return uniRefMap;
	}
	
	/**
	 * Fetches for an accession  the UniProtRef entries via the uniprot webservice
	 * @param accession
	 * @throws SQLException
	 * @return The uniref entry
	 */
	private UniRefEntryMPA fetchUniRefEntriesByAccession(String accession) throws SQLException {
		
		// The resultmap with the uniprotEntries
		UniRefEntryMPA uniRefs = new UniRefEntryMPA("EMPTY", "EMPTY", "EMPTY");
		
		// Build a uniref query 
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
						uniRefs.setUniRef100(thisentry.getUniRefEntryId().getValue());
					}
					if (thisentry.getUniRefEntryId().getValue().contains("UniRef90")) {
						uniRefs.setUniRef90(thisentry.getUniRefEntryId().getValue());
					}
					if (thisentry.getUniRefEntryId().getValue().contains("UniRef50")) {
						uniRefs.setUniRef50(thisentry.getUniRefEntryId().getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		} 
		
		return uniRefs;
	}
	
	
	
//	/**
//	 * update uniprot entries for proteins that do not hava a uniprot entry 
//	 * 
//	 * @author K.Schallert
//	 */	
//	public ReducedProteinData getUniRefs(String accession, ReducedProteinData redProtEntry) {
//		// start the uniref service
//		this.startUniRefService();
//		// Get the UniRefs
//		Query query = UniRefQueryBuilder.memberAccession(accession);
//		QueryResult<UniRefEntry> entries = null;
//		try {		
//			entries = this.uniRefQueryService.getEntries(query);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}		
//		if (entries != null) {
//			while (entries.hasNext()) {
//				try {
//					UniRefEntry thisentry = entries.next();	
//					if (thisentry.getUniRefEntryId().getValue().contains("UniRef100")) {
//						redProtEntry.setUniRef100EntryId(thisentry.getUniRefEntryId().getValue());
//					}
//					if (thisentry.getUniRefEntryId().getValue().contains("UniRef90")) {
//						redProtEntry.setUniRef90EntryId(thisentry.getUniRefEntryId().getValue());
//					}
//					if (thisentry.getUniRefEntryId().getValue().contains("UniRef50")) {
//						redProtEntry.setUniRef50EntryId(thisentry.getUniRefEntryId().getValue());
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					System.out.println("Accession: "+accession+ " broken uniref entries: " + redProtEntry);
//					break;
//				}
//			}
//		} else {
//			// TODO: add something to deal with this problem
//			System.out.println("query failed!");
//		}
//		// stop the unirefservice and return
//		this.stopUniRefService();
//		return redProtEntry;
//	}
	
//	/**
//	 * deletes blast hits
//	 * 
//	 * @author K.Schallert
//	 * @throws SQLException 
//	 */	
//	public static void deleteblasthits() throws SQLException {
//		// get client
//		Client.getInstance();
//		Client.getInstance().firePropertyChange("new message", null, "DELETING BLAST RESULTS");
//
//		// connect to db
//		Connection conn = DBManager.getInstance().getConnection();
//		// find all blast proteins -> contain "_BLAST_"
//		List<ProteinTableAccessor> proteinlist = ProteinAccessor.findBlastHits(conn);
//		// we now need to make 2 new lists: one list for entries that remain, one for entries to delete
//		List<ProteinTableAccessor> deletelist = new ArrayList<ProteinTableAccessor>();
//		Map<String, ProteinTableAccessor> revertmap = new TreeMap<String, ProteinTableAccessor>();
//		for (ProteinTableAccessor prot : proteinlist) {
//			String original_prot_accession = prot.getAccession().split("_BLAST_")[0];
//			// String uniprot_prot_accession = prot.getAccession().split("_BLAST_")[1];
//			if (revertmap.containsKey(original_prot_accession)) {
//				deletelist.add(prot);
//			} else {
//				revertmap.put(original_prot_accession, prot);
//			}
//		}
//		Client.getInstance().firePropertyChange("new message", null, "REVERTING ENTRIES");
//		Client.getInstance().firePropertyChange("indeterminate", false,	true);
//		Client.getInstance().firePropertyChange("resetall", -1L, (long) revertmap.size());
//		Client.getInstance().firePropertyChange("resetcur", -1L, (long) revertmap.size());
//		// cycle through proteins for reversion		
//		for (String prot_acc : revertmap.keySet()) {			
//			ProteinTableAccessor thisprot = revertmap.get(prot_acc);
//			thisprot.setAccession(prot_acc);			
//			thisprot.setDescription("Metagenome Unknown");			
//			// find the uniprotentry to this protein 
//			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(thisprot.getProteinid(), conn);
//			// and delete it
//			if (uniprotentry != null) {
//				uniprotentry.delete(conn);
//			}
//			thisprot.update(conn);
//			conn.commit();
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//		}
//		conn.commit();
//		Client.getInstance().firePropertyChange("new message", null, "DELETING ENTRIES");
//		Client.getInstance().firePropertyChange("indeterminate", false,	true);
//		Client.getInstance().firePropertyChange("resetall", -1L, (long) deletelist.size());
//		Client.getInstance().firePropertyChange("resetcur", -1L, (long) deletelist.size());		
//		// cylce through proteins for deletion 
//		for (ProteinTableAccessor protein : deletelist) {
//			// find  omssahits to this protein 
//			List<OmssahitTableAccessor> omssahits = Omssahit.getHitsFromProteinid(protein.getProteinid(), conn);
//			for (OmssahitTableAccessor omssahit : omssahits) {
//				// and delete them
//				omssahit.delete(conn);
//			}
//			// find  mascothits to this protein 
//			List<MascothitTableAccessor> mascothits = Mascothit.getHitsFromProteinID(protein.getProteinid(), conn);
//			for (MascothitTableAccessor mascothit : mascothits) {
//				// and delete them
//				mascothit.delete(conn);
//			}
//			// find  mascothits to this protein 
//			List<XtandemhitTableAccessor> xtandemhits = XTandemhit.getHitsFromProteinID(protein.getProteinid(), conn);
//			for (XtandemhitTableAccessor xtandemhit : xtandemhits) {
//				// and delete them
//				xtandemhit.delete(conn);
//			}
//			// find  pep2prot to this protein 
//			List<Pep2prot> pep2prots = Pep2prot.get_pep2prots_for_proteinid(protein.getProteinid(), conn);
//			for (Pep2prot pep2prot : pep2prots) {
//				// and delete them
//				pep2prot.delete(conn);
//			}
//			// find the uniprotentry to this protein 
//			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(protein.getProteinid(), conn);
//			// and delete them
//			if (uniprotentry != null) {
//				uniprotentry.delete(conn);	
//			}			
//			// delete proteinhit, next item
//			protein.delete(conn);
//			conn.commit();
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//		}
//		conn.commit();
//		Client.getInstance().firePropertyChange("new message", null, "DELETING BLAST RESULTS FINISHED");
//		Client.getInstance().firePropertyChange("indeterminate", false,	true);
//		Client.getInstance().firePropertyChange("resetall", -1L, 100L);
//		Client.getInstance().firePropertyChange("resetcur", -1L, 100L);		
//	}	

//	/**
//	 * Class to create an UniProt entries in the SQL table
//	 * @param accessionList. The set of accessions
//	 * @throws SQLException
//	 */
//	public void addUniProtentry(DigFASTAEntry entry) throws SQLException{
//		
//		/**
//		 * List of accessions with uniProt 
//		 */
//		QueryResult<UniProtEntry> uniProtEntries; 
//		
//		/**
//		 * Map containing all the UniRef type and value for one protein
//		 */
//		TreeMap<String, String> uniRefs = new TreeMap<String, String>();
//		Connection conni = DBManager.getInstance().getConnection();
//		
//		if (entry.getType().equals(DigFASTAEntry.Type.UNIPROTSPROT) || entry.getType().equals(DigFASTAEntry.Type.UNIPROTTREMBL)) {
//		
//		// Query UniProt
//		this.startUniProtService();					
//		try {			
//			
//			Set<String >accessionList = new TreeSet<String>();
//				accessionList.add(entry.getIdentifier());
//		
//			Query queryUniProt = UniProtQueryBuilder.accessions(accessionList);	
//			for (String string : accessionList) {
//				System.out.println("ACCESSION" + string);
//			}
//		
//			uniProtEntries = uniProtQueryService.getEntries(queryUniProt);	
//			
//			// fetch also UniRef
//			while (uniProtEntries.hasNext()) {
//				UniProtEntry uniProtEntry = (UniProtEntry) uniProtEntries.next();
//				// Query UniRef entries from a query
//				try {
//					this.startUniRefService();
//					Query queryUniref = UniRefQueryBuilder.memberAccession(uniProtEntry.getPrimaryUniProtAccession().toString());
//					
//					QueryResult<UniRefEntry> entriesUniRef = uniRefQueryService.getEntries(queryUniref);
//					
//					uniRefs.put("UniRef100", "UNKNOWN");
//					uniRefs.put("UniRef90", "UNKNOWN");
//					uniRefs.put("UniRef50", "UNKNOWN");
//					
//					// Iterate over all three UniRefs
//					for (int i = 0; i < 3; i++) {
//						UniRefEntry thisUniRefentry = null;
//						try {
//							thisUniRefentry = entriesUniRef.next();
//							if (thisUniRefentry != null) {
//								String[] uniref_split = thisUniRefentry.getUniRefEntryId().toString().split("_");
//								uniRefs.put(uniref_split[0], thisUniRefentry.getUniRefEntryId().toString());
//							}
//						} catch (Exception e) {
//						}
//					}
//					
//				} catch (ServiceException e) {
//					e.printStackTrace();
//				} finally{
//					this.stopUniRefService();
//				}
//				
//				Long taxID = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());
//				// Get EC Numbers.
//				String ecNumbers = "";
//				List<String> ecNumberList = uniProtEntry.getProteinDescription().getEcNumbers();
//				if (ecNumberList.size() > 0) {
//					for (String ecNumber : ecNumberList) {
//						ecNumbers += ecNumber + ";";
//					}
//					ecNumbers = Formatter.removeLastChar(ecNumbers);
//				}
//				// Get ontology keywords.
//				String keywords = "";
//				List<uk.ac.ebi.kraken.interfaces.uniprot.Keyword> keywordsList = uniProtEntry.getKeywords();
//				if (keywordsList.size() > 0) {
//					for (uk.ac.ebi.kraken.interfaces.uniprot.Keyword kw : keywordsList) {
//						keywords += kw.getValue() + ";";
//					}
//					keywords = Formatter.removeLastChar(keywords);
//				}
//				// Get KO numbers.
//				String koNumbers = "";
//				List<DatabaseCrossReference> xRefs = uniProtEntry
//						.getDatabaseCrossReferences(DatabaseType.KO);
//				if (xRefs.size() > 0) {
//					for (DatabaseCrossReference xRef : xRefs) {
//						koNumbers += xRef.getPrimaryId().getValue() + ";";
//					}
//					koNumbers = Formatter.removeLastChar(koNumbers);
//				}
//			
//				try {
//					Uniprotentry.addUniProtEntryWithProteinID(taxID,
//															ecNumbers,
//															koNumbers,
//															keywords,
//															uniRefs.get("UniRef100"),
//															uniRefs.get("UniRef90"),
//															uniRefs.get("UniRef50"),
//															conni);
//					conni.commit();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}finally {
//			// stop services
//			this.stopUniProtService();	
//	
//		}
//		
//		}
//
//		
//	}
	
//	/**
//	 * Returns a protein object queried by the UniProt webservice (if no indexed FASTA is available.
//	 * @param id Protein accession.
//	 * @return Protein object containing header + sequence.
//	 */
//	public Protein getProteinFromWebService(String id) {
//		// start service
//		this.startUniProtService();		
//		// Retrieve UniProt entry by its accession number
//		UniProtEntry entry = null;
//		try {
//			entry = (UniProtEntry) this.uniProtQueryService.getEntry(id);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}		
//		String header = ">";
//		if(entry.getType() == UniProtEntryType.TREMBL) {
//			header += "tr|";
//		} else if(entry.getType() == UniProtEntryType.SWISSPROT) {
//			header += "sw|";
//		}
//		header += id + "|";
//		header += this.getProteinName(entry.getProteinDescription());
//		String sequence = entry.getSequence().getValue();
//		this.stopUniProtService();
//		return new Protein(header, sequence);
//	}
//	
//	/**
//	 * Returns the protein name(s) as formatted string
//	 * @param desc ProteinDescription object.
//	 * @return Protein name(s) as formatted string.
//	 */
//	public String getProteinName(ProteinDescription desc) {
//		Name name = null;
//		
//		if (desc.hasRecommendedName()) {
//			name = desc.getRecommendedName();
//		} else if (desc.hasAlternativeNames()) {
//			name = desc.getAlternativeNames().get(0);
//		} else if (desc.hasSubNames()) {
//			name = desc.getSubNames().get(0);
//		}
//		return (name == null) ? "unknown" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
//	}

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
	
	
	/**
	 * Turns the output stream on and off.
	 * @param silence. Turn outout stream on and off
	 */
	public void silenceOutput(boolean silence){
		// turn off or on? 
		if (silence) {
			// make a backup of the output stream
			 original = System.out;
			    System.setOut(new PrintStream(new OutputStream() {
			                public void write(int b) {
			                }
			            }));
		}else{
			System.setOut(original);
		}
	}
}

