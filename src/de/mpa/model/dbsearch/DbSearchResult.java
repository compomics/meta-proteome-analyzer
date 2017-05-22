package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mpa.client.Client;
import de.mpa.db.mysql.accessor.Mascothit;
import de.mpa.db.mysql.accessor.Omssahit;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.db.mysql.accessor.Searchspectrum;
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
	
	/*
	 * FIELDS  Unknown Taxonomic instance:
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Flag indicating whether this result object has not been subject to
	 * further processing.
	 */
	private boolean raw = true;
	
	/**
	 * Taxonomy map containing all entries from taxonomy db table.
	 */
	private Map<Long, Taxonomy> taxonomyMap;
	
	/**
	 * The shared taxonomy node instance for undefined taxonomies.
	 */
	private TaxonomyNode unclassifiedNode;
	
	/**
	 * The project title.
	 */
	private final String projectTitle;

	/**
	 * The experiment title.
	 */
	@Deprecated
	private String experimentTitle = "";
//	private final String experimentTitle;
	
	/**
	 * The experiment list.
	 */
	private ArrayList<MPAExperiment> experimentList;
//	private final String experimentTitle;

	/**
	 * The fastaDB.
	 */
	private final String fastaDB;

	/**
	 * The search date.
	 */
	private Date searchDate;

	/**
	 * The list of meta-proteins.
	 */
	private final ArrayList<MetaProteinHit> metaProteins = new ArrayList<MetaProteinHit>();

	/**
	 * The list of visible meta-proteins.
	 */
	private ArrayList<MetaProteinHit> visMetaProteins = new ArrayList<MetaProteinHit>();
	
	/**
	 * The list of proteins.
	 */
	private final ArrayList<ProteinHit> proteins = new ArrayList<ProteinHit>();

	/**
	 * The list of visible proteins.
	 */
	private ArrayList<ProteinHit> visProteins = new ArrayList<ProteinHit>();
	
	/**
	 * The list of peptides.
	 */
	private final ArrayList<PeptideHit> peptides = new ArrayList<PeptideHit>();

	/**
	 * The list of visible peptides.
	 */
	private ArrayList<PeptideHit> visPeptides = new ArrayList<PeptideHit>();
	
	/**
	 * The list of peptides.
	 */
	private final ArrayList<PeptideSpectrumMatch> psms = new ArrayList<PeptideSpectrumMatch>();

	/**
	 * The list of visible peptides.
	 */
	private ArrayList<PeptideSpectrumMatch> visPsms = new ArrayList<PeptideSpectrumMatch>();
	
	/**
	 * The list of peptides.
	 */
	private final ArrayList<SearchHit> searchHits = new ArrayList<SearchHit>();

	/**
	 * The list of visible peptides.
	 */
	private ArrayList<SearchHit> visSearchHits = new ArrayList<SearchHit>();
	
	/**
	 * The total amount of spectra.
	 */
	private int totalSpectra;
	

	/**
	 * Constructs a result object from the specified project title, experiment
	 * title and FASTA database name.
	 * @param projectTitle The project title.
	 * @param experimentTitle The experiment title.
	 * @param fastaDB The FASTA database.
	 */
	public DbSearchResult(String projectTitle, ArrayList<MPAExperiment> expList, String fastaDB) {
		this.projectTitle = projectTitle;
		this.experimentList = expList;
		this.taxonomyMap = new HashMap<>();
		this.fastaDB = fastaDB;
        this.searchDate = new Date();
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
					this.searchHits.add(hit);
					this.visSearchHits.add(hit);

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
						psm.addExperimentID(experiment.getID());
						psm_mapping.put(psm_key, psm);
					}
					psm.addExperimentID(expID);
					psm.setTitle(spectrum_titlehash.toString());
					this.psms.add(psm);
					this.visPsms.add(psm);

					// DEALS WITH PEPTIDE
					PeptideHit pephit = null;
					if (peptide_mapping.containsKey(pep_seq)) {
						pephit = peptide_mapping.get(pep_seq);
						pephit.addPeptideSpectrumMatch(pep_seq, psm);
					} else {
						pephit = new PeptideHit(pep_seq, psm);
						pephit.addExperimentID(expID);
						peptide_mapping.put(pep_seq, pephit);
					}
					psm.setPeptideHit(pephit);
					this.peptides.add(pephit);
					this.visPeptides.add(pephit);

					// DEALS WITH PROTEIN
					ProteinHit prot = null;
					if (protein_mapping.containsKey(accession)) {
						prot = protein_mapping.get(accession);
						prot.addPeptideHit(pephit);
						pephit.addProteinHit(prot);
						pephit.setTaxonomyNode(prot.getTaxonomyNode());
						psm.setTaxonomyNode(prot.getTaxonomyNode());
					} else {
						prot = new ProteinHit(accession);
						protein_mapping.put(accession, prot);
						prot.addPeptideHit(pephit);
						pephit.addProteinHit(prot);
						prot.setSequence(prot_seq);
						prot.setDescription(prot_description);
						prot.addExperimentID(expID);
						UniProtEntryMPA uniprot = this.makeUPEntry(uniprotid, conn);
						TaxonomyNode taxonode = uniprot.getTaxonomyNode();
						prot.setUniprotEntry(uniprot);
						prot.setTaxonomyNode(taxonode);
						pephit.setTaxonomyNode(taxonode);
						psm.setTaxonomyNode(taxonode);
						this.proteins.add(prot);
						this.visProteins.add(prot);
						
						// make metaprotein and finalize
						String metaprot_str = "Meta-Protein " + prot.getAccession();
						MetaProteinHit mph = new MetaProteinHit(metaprot_str, prot, uniprot);
						mph.setTaxonomyNode(taxonode);
						mph.setUniprotEntry(uniprot);
						mph.addProteinHit(prot);
						mph.addExperimentID(expID);
						prot.setMetaProteinHit(mph);
						this.addMetaProtein(mph);
					}
					// report progress
					client.firePropertyChange("progressmade", true, false);
				}
			}
			// next experiment
		}
		// determine total spectral count
		int spectralCount = 0;
		for (MPAExperiment experiment : experimentList) {
			spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(experiment.getID(), conn);
		}
		this.setTotalSpectrumCount(spectralCount);
		
//		// XXX: DEBUG OUTPUT POPULATING TABLES
//		System.out.println("MP: " + this.metaProteins.size());
//		System.out.println("VMP: " + this.visMetaProteins.size());
//		System.out.println("P: " + this.proteins.size());
//		System.out.println("VP: " + this.visProteins.size());
//		System.out.println("p: " + this.peptides.size());
//		System.out.println("Vp: " + this.visPeptides.size());
//		System.out.println("psm: " + this.searchHits.size());
//		System.out.println("Vpsm: " + this.visSearchHits.size());
//		
//		for (MetaProteinHit mp : this.getMetaProteins()) {
//			System.out.println("MP : " + mp.getAccession());
//			System.out.println("MP : " + mp.getDescription());
//			System.out.println("MP : " + mp.getProteinHitList().size());
//			for (ProteinHit ph : mp.getProteinHitList()) {
//				System.out.println("P : " + ph.getAccession());
//				System.out.println("P : " + ph.getDescription());
//				for (PeptideHit pep : ph.getPeptideHitList()) {
//					System.out.println("PEP : "+pep.getSequence());
//					System.out.println("PEP : "+pep.getPeptideSpectrumMatches().size());
//					for (PeptideSpectrumMatch psm : pep.getPeptideSpectrumMatches()) {
//						System.out.println("PSM : " + psm.getSpectrumID());
//						System.out.println("PSM : " + psm.getTitle());
//						System.out.println("PSM : " + psm.getSearchHits());
//					}
//				}
//			}
//		}
		
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
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniprotAccessor.getTaxid(), taxonomyMap, conn);
		} else {
			// There is no uniprot entry
			// mark taxonomy as 'unclassified'
			if (unclassifiedNode == null) {
				TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
				unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
			}
			taxonomyNode = unclassifiedNode;
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

	public void addMetaProtein(MetaProteinHit mph) {
        this.metaProteins.add(mph);
        this.visMetaProteins.add(mph);
	}	

	/**
	 * Returns whether this result object has not been processed yet.
	 * @return <code>true</code> if this is an unprocessed result, <code>false</code> otherwise
	 */
	public boolean isRaw() {
		return this.raw;
	}

	/**
	 * Sets the state flag denoting whether this result object has been
	 * processed to the specified value.
	 * @param raw <code>true</code> if this result has not been processed, <code>false</code> otherwise
	 */
	public void setRaw(boolean raw) {
		this.raw = raw;
	}

	/**
	 * Returns whether this result object contains no protein hits.
	 * @return <code>true</code> if empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return visMetaProteins.isEmpty();
	}

	/**
	 * Returns the list of metaproteins containing grouped protein hits.
	 * @return the list of metaproteins.
	 */
	public ArrayList<MetaProteinHit> getMetaProteins() {
		return metaProteins;
	}
	
	public ArrayList<ProteinHit> getAllProteinHits() {
		return this.proteins;
	}
	
	public ArrayList<PeptideHit> getAllPeptideHits() {
		return this.peptides;
	}
	
	public ArrayList<PeptideSpectrumMatch> getAllPSMS() {
		return this.psms;
	}
	
//	/**
//	 * Resets the mapping of visible meta-proteins.
//	 */
//	public void clearVisibleMetaProteins() {
//        visMetaProteins.clear();
//	}

//	/**
//	 * Returns the list of protein hits.
//	 * 
//	 * @return the list of protein hits.
//	 */
//	public List<ProteinHit> getProteinHitList() {
//		ProteinHitList metaProteins =
//				(this.visMetaProteins == null) ? this.metaProteins : visMetaProteins;
//		ProteinHitList proteinHits = new ProteinHitList();
//		for (ProteinHit mph : metaProteins) {
//			proteinHits.addAll(((MetaProteinHit) mph).getProteinHitList());
//		}
//		return proteinHits;
//	}

//	/**
//	 * Returns the map of protein hits.
//	 * @return the map of protein hits
//	 */
//	public Map<String, ProteinHit> getProteinHits() {
//		ProteinHitList metaProteins =
//				(this.visMetaProteins == null) ? this.metaProteins : visMetaProteins;
//		Map<String, ProteinHit> proteinHits = new LinkedHashMap<String, ProteinHit>();
//		for (ProteinHit mph : metaProteins) {
//			proteinHits.putAll(((MetaProteinHit) mph).getProteinHits());
//		}
//		return proteinHits ;
//	}

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
	 * Returns the peptide hit for a particular sequence.
	 * @param sequence the peptide sequence
	 * @return the peptide hit or <code>null</code> if no such hit exists
	 */
	public PeptideHit getPeptideHit(String sequence) {
		PeptideHit peptideHit = null;
		for (MetaProteinHit mph : metaProteins) {
			peptideHit = mph.getPeptideHit(sequence);
			if (peptideHit != null) {
				break;
			}
		}
		return peptideHit;
	}
	
//	/**
//	 * Returns the spectrum match for a particular search spectrum ID.
//	 * @param key the search spectrum database key
//	 * @return the spectrum match or <code>null</code> if no such match exists
//	 */
//	public PeptideSpectrumMatch getSpectrumMatch(String key) {
//		for (ProteinHit proteinHit : getProteinHits().values()) {
//			for (PeptideHit peptideHit : proteinHit.getPeptideHits().values()) {
//				PeptideSpectrumMatch psm = peptideHit.getPeptideSpectrumMatch(key);
//				if (psm != null) {
//					return psm;
//				}
//			}
//		}
//		return null;
//	}

	/**
	 * Returns the project title.
	 * @return the project title
	 */
	public String getProjectTitle() {
		return this.projectTitle;
	}

	/**
	 * Returns the experiment title.
	 * @return the experiment title
	 */
	public String getExperimentTitle() {
		return this.experimentTitle;
	}

	/**
	 * Returns the FASTA database identifier.
	 * @return the FASTA database identifier.
	 */
	public String getFastaDB() {
		return this.fastaDB;
	}

	/**
	 * Returns the search date.
	 * @return the search date
	 */
	public Date getSearchDate() {
		return this.searchDate;
	}

	/**
	 * Sets the search date
	 * @param searchDate the search date to set
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	/**
	 * Returns the total amount of queried spectra.
	 * @return The total spectral count.
	 */
	public int getTotalSpectrumCount() {
		return this.totalSpectra;
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
        visMetaProteins.clear();
		for (MetaProteinHit mph : this.metaProteins) {
			mph.setFDR(fdr);
			if (mph.isVisible()) {
                visMetaProteins.add(mph);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = (obj instanceof DbSearchResult);
		if (result) {
			DbSearchResult that = (DbSearchResult) obj;
			result = getProjectTitle().equals(that.getProjectTitle())
					&& getExperimentTitle().equals(that.getExperimentTitle());
		}
		return result;
	}

	public Long getIdentifiedSpectrumCount() {
		HashSet<Long> specIDs = new HashSet<Long>();
		for (PeptideHit pephit : this.getAllPeptideHits()) {
			for (PeptideSpectrumMatch psm : pephit.getPeptideSpectrumMatches()) {
				specIDs.add(psm.getSpectrumID());
			}
		}
		return (long) specIDs.size();
	}

	public Long getUniquePeptideCount() {
		Long uniquePeps = 0L;
		for (PeptideHit pep : this.peptides) {
			if (pep.isSelected() && pep.isVisible()) {
				if (pep.getProteinHits().size() == 1) {
					uniquePeps++;
				}
			}
		}
		return uniquePeps;
	}
	
}