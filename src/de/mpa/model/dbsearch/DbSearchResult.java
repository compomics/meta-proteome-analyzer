package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.db.mysql.DBManager;
import de.mpa.db.mysql.accessor.Mascothit;
import de.mpa.db.mysql.accessor.Omssahit;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.db.mysql.accessor.Taxonomy;
import de.mpa.db.mysql.accessor.UniprotentryAccessor;
import de.mpa.db.mysql.accessor.XTandemhit;
import de.mpa.model.MPAExperiment;
import de.mpa.model.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.model.taxonomy.TaxonomyNode;
import de.mpa.model.taxonomy.TaxonomyUtils;

/**
 * This class represents the set of proteins which may hold multiple peptides
 * for each protein hit (identified by its accession number).
 * 
 * @author T. Muth, R. Heyer
 */
public class DbSearchResult implements Serializable {

	/**
	 * Mandatory serial ID, not used
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Taxonomy map containing all Taxonomies in the DB (unneccessary?)
	 */
	private HashMap<Long, Taxonomy> taxonomyMap;
	
	/**
	 * TaxonomyNode map containing all Taxonomy Nodes we use
	 */
	private HashMap<Long, TaxonomyNode> taxonomyNodeMap;

//	/**
//	 * The shared taxonomy node instance for undefined taxonomies.
//	 */
//	private TaxonomyNode unclassifiedNode;

	/**
	 * The experiment list.
	 */
	private ArrayList<MPAExperiment> experimentList;

	/**
	 * The list of meta-proteins.
	 */
	private ArrayList<MetaProteinHit> metaProteins = new ArrayList<MetaProteinHit>();

	/**
	 * The total amount of spectra.
	 */
	private int totalSpectra;
	
	/**
	 * MySQL connection
	 */
	private Connection connection;


	/**
	 * Constructs a result object from the specified project title, experiment
	 * title and FASTA database name.
	 * @param experimentTitle The experiment title.
	 */
	public DbSearchResult(ArrayList<MPAExperiment> expList) {
		this.experimentList = expList;
//		this.taxonomyMap = new HashMap<Long, Taxonomy>();
	}

	/**
	 * Quickly return search results by using 3 views: omssaresult, mascotresult, xtandemresult.
	 * The enum 'SearchEngineView' encodes the names of the views.
	 * An FDR value of 0.2 is applied at the end to set all proteins to "visible".
	 * 
	 * @return searchResult - returns this object search result, which is filled with data in this method 
	 */
	public void getSearchResultByView() throws SQLException {
		// initialize stuff
		Client client = Client.getInstance();
		Connection conn = client.getConnection();
		Set<Long> experimentIds = new HashSet<Long>();
		for (MPAExperiment experiment : this.experimentList) {
			experimentIds.add(experiment.getID());
		}
		// init our 3 necassary mappings: psms / peptides / proteins
		HashMap<String, PeptideSpectrumMatch> psm_mapping = new HashMap<String, PeptideSpectrumMatch>();
		HashMap<String, PeptideHit> peptide_mapping = new HashMap<String, PeptideHit>();
		HashMap<String, ProteinHit> protein_mapping = new HashMap<String, ProteinHit>();

		// user feedback
		client.firePropertyChange("new message", null, "BUILDING RESULT OBJECT");
		client.firePropertyChange("indeterminate", true, false);
		// progress bar
		Long maxProgress = findMaxProgress(conn);
		client.firePropertyChange("resetall", 0L, maxProgress);
		client.firePropertyChange("resetcur", 0L, maxProgress);
		
		// initialize taxonomies
		// taxonomy map for common ancestor retrieval
		try {
			taxonomyMap = Taxonomy.retrieveTaxonomyMap(DBManager.getInstance().getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// node map containing the actual nodes we use
		taxonomyNodeMap = new HashMap<Long, TaxonomyNode>();
		// doing it in this order is important!!
		taxonomyNodeMap.put(1L, TaxonomyUtils.createTaxonomyNode(1L, taxonomyMap, taxonomyNodeMap));
		// TODO: test if this works properly
//		taxonomyNodeMap.put(0L, TaxonomyUtils.createTaxonomyNode(0L, taxonomyMap, taxonomyNodeMap));
		// this is the "Uncategorized" Node, NCBI-ID=0, just use "root"
		taxonomyNodeMap.put(0L, taxonomyNodeMap.get(1L));
		// this is the "cellular organism" Node, NCBI-ID=131567, just use "root"
		taxonomyNodeMap.put(131567L, taxonomyNodeMap.get(1L));
		
		
		// experiment-wise data retrieval
		for (MPAExperiment experiment : experimentList) {
			Long expID = experiment.getID();
			// loop for each search engine (3: omssa, xtandem, mascot)
			for (SearchEngineType current_view : SearchEngineType.values()) {
				// TODO: add paging (LIMIT / OFFSET) 
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + current_view.getResultView() + " WHERE fk_experimentid = ?");						
				ps.setLong(1, expID);
				ResultSet rs = ps.executeQuery();
				// cylce the rows given by view
				while (rs.next()) {
					// get all values from resultset
					int charge = rs.getInt("precursor_charge");
					String accession = rs.getString("accession");
					String prot_seq = rs.getString("protseq");
					String pep_seq = rs.getString("pepseq");
					String prot_description = rs.getString("description");
					Long spectrumid = rs.getLong("spectrumid");
					Long spectrum_titlehash = rs.getLong("titlehash");
					Long uniprotid = rs.getLong("fk_uniprotentryid");
					// search algorithm specific vars

					// DEALS WITH SEARCHHIT
					SearchHit hit = null;
					switch (current_view) {
					case OMSSA:
						hit = new Omssahit(rs, false); // MEMORY: may have to deal with pep-sequence and prot-accession
						// omssahits report a false precursor-charge, which is replaced by the specturm charge here 
						((Omssahit) hit).setCharge(charge);
						break;
					case XTANDEM:
						hit = new XTandemhit(rs, false); // MEMORY: may have to deal with pep-sequence and prot-accession
						break;
					case MASCOT:
						hit = new Mascothit(rs, false); // MEMORY: may have to deal with pep-sequence and prot-accession
						break;							
					}
					// initial FDR
					if (hit.getQvalue().doubleValue() <= Constants.getDefaultFDR()) {
						// DEALS WITH PSM
						// construct PSM key
						// new psm-key spec-titlehash + pepseq
						String psm_key = spectrum_titlehash.toString() + pep_seq;
						PeptideSpectrumMatch psm;
						// check psm redundancy and find psm
						if (psm_mapping.containsKey(psm_key)) {
							// existing psm
							psm = psm_mapping.get(psm_key);
							psm.addSearchHit(hit);
						} else {
							// new psm
							psm = new PeptideSpectrumMatch(spectrumid, hit);
							psm_mapping.put(psm_key, psm);
							psm.setTitle(spectrum_titlehash.toString());
						}
						psm.addExperimentID(expID);

						// DEALS WITH PEPTIDE
						PeptideHit pephit = null;
						if (peptide_mapping.containsKey(pep_seq)) {
							pephit = peptide_mapping.get(pep_seq);
							pephit.addPeptideSpectrumMatch(psm);
						} else {
							// new pepitde
							pephit = new PeptideHit(pep_seq, psm);
							pephit.addExperimentID(expID);
							peptide_mapping.put(pep_seq, pephit);
						}
						pephit.addExperimentID(expID);
						psm.setPeptideHit(pephit);

						// DEALS WITH PROTEIN, UNPROT ANNOTATION AND TAXONOMY
						ProteinHit prot = null;
						if (protein_mapping.containsKey(accession)) {
							prot = protein_mapping.get(accession);
							prot.addPeptideHit(pephit);
							pephit.addProteinHit(prot);
							// TODO: requires common ancestor taxonomy
							pephit.setTaxonomyNode(prot.getTaxonomyNode());
							psm.setTaxonomyNode(prot.getTaxonomyNode());

						} else {
							prot = new ProteinHit(accession);
							protein_mapping.put(accession, prot);
							prot.addPeptideHit(pephit);
							pephit.addProteinHit(prot);
							// TODO: DEBUG
							prot.setSequence(prot_seq);
//							prot.setSequence("");
							// DEBUG END
							prot.setDescription(prot_description);
							prot.addExperimentID(expID);
							UniProtEntryMPA uniprot = this.makeUPEntry(uniprotid, conn);
							TaxonomyNode taxonode = uniprot.getTaxonomyNode();
							prot.setUniprotEntry(uniprot);
							prot.setTaxonomyNode(taxonode);
							pephit.setTaxonomyNode(taxonode);
							psm.setTaxonomyNode(taxonode);

							// make One metaprotein for Each Protein
							String metaprot_str = "Meta-Protein " + prot.getAccession();
							MetaProteinHit mph = new MetaProteinHit(metaprot_str, prot, uniprot);
							mph.setTaxonomyNode(taxonode);
							mph.setUniprotEntry(uniprot);
							mph.addExperimentID(expID);
							prot.setMetaProteinHit(mph);
							this.addMetaProtein(mph);
						}
						if (this.metaProteins.size() % 1000 == 0) {
							long heapSize = Runtime.getRuntime().totalMemory(); 
						}
						// report progress .DISTINCT
						client.firePropertyChange("progressmade", true, false);
					}
				}
				rs.close();
				ps.close();
			}
			// next experiment
		}
		// determine total spectral count
		this.setTotalSpectrumCount(this.countTotalSpectraFromExperimentList(conn));
		this.setFDR(Constants.getDefaultFDR());
		
		//		// XXX: DEBUG OUTPUT POPULATING TABLES
//				System.out.println("MP: " + this.metaProteins.size());
		//		System.out.println("VMP: " + this.visMetaProteins.size());
//				System.out.println("P: " + this.proteins.size());
		//		System.out.println("VP: " + this.visProteins.size());
//				System.out.println("p: " + this.peptides.size());
		//		System.out.println("Vp: " + this.visPeptides.size());
//				System.out.println("psm: " + this.searchHits.size());
		//		System.out.println("Vpsm: " + this.visSearchHits.size());
		//		
//				for (MetaProteinHit mp : this.getAllMetaProteins()) {
//					
//					System.out.println("MP : " + mp.getAccession());
//					System.out.println("MP : " + mp.getDescription());
//					System.out.println("MP : " + mp.getProteinHitList().size());
//					for (ProteinHit ph : mp.getProteinHitList()) {
//						System.out.println("P : " + ph.getAccession());
//						System.out.println("P : " + ph.getDescription());
//						for (PeptideHit pep : ph.getPeptideHitList()) {
//							System.out.println("PEP : "+pep.getSequence());
//							System.out.println("PEP : "+pep.getPeptideSpectrumMatches().size());
//							System.out.println("TaxNode : "+pep.getTaxonomyNode().getName());
//							for (PeptideSpectrumMatch psm : pep.getPeptideSpectrumMatches()) {
//								System.out.println("PSM : " + psm.getSpectrumID());
//								System.out.println("PSM : " + psm.getTitle());
//								System.out.println("PSM : " + psm.getSearchHits());
//							}
//						}
//					}
//				}
				
	}
	
	/**
	 * Returns an empty up-entry to attach to metaprotins/proteins without one
	 * 
	 * @return UniProtEntryMPA - initialized empty
	 * @throws SQLException 
	 */
	public UniProtEntryMPA createEmptyUPEntry() throws SQLException {
		return this.makeUPEntry(-1L, connection);
	}

	/**
	 * Constructs an uniprotentry from the uniprotentry-id by quering SQL and creating a taxonomy node.
	 * 
	 * @param uniprotentryid - id for the uniprotentry-table
	 * @param conn - sql connection
	 * @return uniprot - uniprot entry in the form of UniProtEntryMPA-object
	 * @throws SQLException
	 */
	private UniProtEntryMPA makeUPEntry(Long uniprotentryid, Connection conn) throws SQLException {
		// Define uniProt entry
		UniProtEntryMPA uniprot = new UniProtEntryMPA();
		// Define taxon node
		TaxonomyNode taxonomyNode;
		// Fetch Uniprot entry
		if (uniprotentryid != -1L) {
			UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(uniprotentryid, conn);
			uniprot = new UniProtEntryMPA(uniprotAccessor);
			// retrieve taxonomy branch
			if (taxonomyNodeMap.containsKey(uniprotAccessor.getTaxid())) {
				taxonomyNode = taxonomyNodeMap.get(uniprotAccessor.getTaxid());
			} else {
				taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniprotAccessor.getTaxid(), taxonomyMap, taxonomyNodeMap);
//				taxonomyNodeMap.put(uniprotAccessor.getTaxid(), taxonomyNode);
			}
		} else {
			// There is no uniprot entry
			// mark taxonomy as 'unclassified'
			taxonomyNode = taxonomyNodeMap.get(0L);
		}
		uniprot.setTaxonomyNode(taxonomyNode);
		return uniprot;
	}

	private Long findMaxProgress(Connection conn) throws SQLException {
		Long maxProgress = 0L;
		for (MPAExperiment experiment : experimentList) {
			// count number of omssahits
			PreparedStatement countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, experiment.getID());
			ResultSet counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			// count number of xtandemhits
			countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, experiment.getID());
			counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			// count number of mascothits
			countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, experiment.getID());
			counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			countps.close();
			counter.close();
		}
		return maxProgress;
	}
	
	/**
	 * Method for adding inital Metaproteins, makes all Metaproteins visibile by default 
	 * 
	 * @param mph - Metaprotein to be added
	 */
	private void addMetaProtein(MetaProteinHit mph) {
		this.metaProteins.add(mph);
	}
	
	/**
	 * Returns the list of metaproteins containing grouped protein hits.
	 * @return the list of metaproteins.
	 */
	public ArrayList<MetaProteinHit> getAllMetaProteins() {
		return this.metaProteins;
	}

	/**
	 * Set the list of metaproteins containing grouped protein hits. 
	 * This method should only be called when metaproteins are generated.
	 * 
	 * @return the list of metaproteins.
	 */
	public void setMetaProteins(ArrayList<MetaProteinHit> metaproteinlist) {
		this.metaProteins = metaproteinlist;
	}
	
	/**
	 * Returns the protein hit for a particular accession.
	 * @param accession the protein accession
	 * @return the protein hit or <code>null</code> if no such hit exists
	 */
	public ProteinHit getProteinHit(String accession) {
		ProteinHit ph = null;
		for (MetaProteinHit mph : metaProteins) {
			ph = mph.getProteinHit(accession);
			if (ph != null) {
				break;
			}
		}
		return ph;
	}

	/**
	 * Returns the total amount of queried spectra.
	 * @return The total spectral count.
	 */
	public int getTotalSpectrumCount() {
		return this.totalSpectra;
	}

	/**
	 * Returns the non-redundant count of ms-spectra from the experiment list of this result-object 
	 * @throws SQLException
	 */
	public int countTotalSpectraFromExperimentList(Connection conn) throws SQLException {
		HashSet<Long> spectrumids = new HashSet<Long>();
		for (MPAExperiment experiment : experimentList) {
			PreparedStatement ps = conn.prepareStatement("SELECT spectrum.spectrumid FROM spectrum "
					+ "INNER JOIN searchspectrum ON searchspectrum.fk_spectrumid = spectrum.spectrumid "
					+ "WHERE searchspectrum.fk_experimentid = ?");
			ps.setLong(1, experiment.getID());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				spectrumids.add(rs.getLong("spectrum.spectrumid"));
			}
			rs.close();
			ps.close();
		}
		int spectralCount = spectrumids.size();
		return spectralCount;
	}

	/**
	 * Sets the total amount of queried spectra.
	 * @param totalSpectra The total spectral count.
	 */
	public void setTotalSpectrumCount(int totalSpectra) {
		this.totalSpectra = totalSpectra;
	}

	/**
	 * Sets the FDR threshold for the visible metaproteins.
	 * @param fdr the FDR threshold.
	 */
	public void setFDR(double fdr) {
		ArrayList<MetaProteinHit> newMetaProteins = new ArrayList<MetaProteinHit>();
		for (MetaProteinHit mph : metaProteins) {
			mph.setFDR(fdr);
			if (mph.isVisible()) {
				newMetaProteins.add(mph);
			}
		}
		metaProteins.clear();
		metaProteins.addAll(newMetaProteins);
	}

	public ArrayList<ProteinHit> getAllProteinHits() {
		ArrayList<ProteinHit> phlist = new ArrayList<ProteinHit>();
		for (MetaProteinHit mph : this.metaProteins) {
			phlist.addAll(mph.getProteinHitList());
		}
		return phlist;
	}
	
	public ArrayList<PeptideHit> getAllPeptideHits() {
		HashSet<PeptideHit> pepset = new HashSet<PeptideHit>();
		for (MetaProteinHit mph : this.metaProteins) {
			for (ProteinHit ph : mph.getProteinHitList()) {
				pepset.addAll(ph.getPeptideHitList());
			}
		}
		ArrayList<PeptideHit> peplist = new ArrayList<PeptideHit>();
		peplist.addAll(pepset);
		return peplist;
	}
	
	public ArrayList<PeptideSpectrumMatch> getAllPSMS() {
		ArrayList<PeptideHit> peplist = this.getAllPeptideHits();
		HashMap<Long, PeptideSpectrumMatch> psmMap = new HashMap<Long, PeptideSpectrumMatch>();
		for (PeptideHit peptide : peplist) {
			for (PeptideSpectrumMatch psm : peptide.getPeptideSpectrumMatches()) {
				// remove duplication check (should not occur)
				if (!psmMap.containsKey(psm.getSpectrumID())) {
					psmMap.put(psm.getSpectrumID(), psm);
				}
			}
		}
		ArrayList<PeptideSpectrumMatch> psmlist = new ArrayList<PeptideSpectrumMatch>();
		psmlist.addAll(psmMap.values());
		return psmlist;
	}
	
	
	public Long getVisibleIdentifiedSpectrumCount() {
		HashSet<Long> specIDs = new HashSet<Long>();
		for (PeptideHit pephit : this.getAllPeptideHits()) {
			for (PeptideSpectrumMatch psm : pephit.getPeptideSpectrumMatches()) {
				if (psm.isVisible()) {
					specIDs.add(psm.getSpectrumID());
				}
			}
		}
		return (long) specIDs.size();
	}

	public Long getUniquePeptideCount() {
		Long uniquePeps = 0L;
		ArrayList<PeptideHit> peplist = getAllPeptideHits();
		for (PeptideHit pep : peplist) {
			if (pep.isSelected() && pep.isVisible()) {
				if (pep.getProteinHits().size() == 1) {
					uniquePeps++;
				}
			}
		}
		return uniquePeps;
	}
	
	public HashMap<Long, TaxonomyNode> getTaxonomyNodeMap() {
		return this.taxonomyNodeMap;
	}
	public HashMap<Long, Taxonomy> getTaxonomyMap() {
		return this.taxonomyMap;
	}

}