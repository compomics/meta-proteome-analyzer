package de.mpa.client.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Taxonomy;
import de.mpa.db.accessor.UniprotentryAccessor;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.accessor.Spectrum.ChargeAndTitle;


/**
 * Implementation of the experiment interface for multiple fused database-linked experiments.
 * @author R. Heyer
 */
public class MultipleDatabaseExperiments extends AbstractExperiment {

	/**
	 * The search result object.
	 */
	private DbSearchResult searchResult;

	/**
	 * Taxonomy map containing all entries from taxonomy db table.
	 */
	private Map<Long, Taxonomy> taxonomyMap;

	/**
	 * The shared taxonomy node instance for undefined taxonomies.
	 */
	private TaxonomyNode unclassifiedNode;

	/**
	 * The list of all experiments, which should be fused
	 */
	private LinkedList<Long> experimentList;

	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param creationDate
	 * @param project
	 */
	public MultipleDatabaseExperiments(Long id, String title,
			Date creationDate, AbstractProject project) {
		super(id, title, creationDate, project);
		// init properties
		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}

	/**
	 * Constructor
	 * @param experimentList
	 * @param title
	 * @param creationDate
	 * @param project
	 */
	public MultipleDatabaseExperiments(LinkedList<Long> experimentList, String title,
		Date creationDate, AbstractProject project) {
		super(0L, title, creationDate, project);
		this.experimentList = experimentList;
		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
	}

	@Override
	public void update(String title, Map<String, String> properties,
			Object... params) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete() {
		try {
			ProjectManager manager = ProjectManager.getInstance();

			// remove experiment and all its properties
			for (Long expID : experimentList) {
				manager.deleteExperiment(expID);
			}
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	public boolean hasSearchResult() {
		try {
			for (long expID : experimentList) {
				return Searchspectrum.hasSearchSpectra(expID, Client.getInstance().getConnection());
			}
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		};
		return false;
	}

	/**
	 * This enum encodes the names of the result-views for later use
	 * 
	 * @author user
	 *
	 */
	private enum SerchEngineViewMulti {
		OMSSA("omssaresult"),
		XTANDEM("xtandemresult"),
		MASCOT("mascotresult");
		private final String text;
		private SerchEngineViewMulti(final String text) {
			this.text = text;
		}
		@Override
		public String toString() {
			return text;
		}
	}
	
	/**
	 * Quickly return search results by using 3 views: omssaresult, mascotresult, xtandemresult.
	 * The enum 'SearchEngineView' encodes the names of the views.
	 * An FDR value of 0.2 is applied at the end to set all proteins to "visible".
	 * 
	 * @return searchResult - returns this object search result, which is filled with data in this method 
	 */
	public DbSearchResult getSearchResultByView() {
		// TODO: memory footprint: REMOVE sequence strings from hits!!!
		// TODO: add proper client-frame feedback
		// only retrieve result if there is none so far
		if (searchResult == null) {
			try {
				// initialize stuff
				this.searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
				Client client = Client.getInstance();
				Connection conn = client.getConnection();
				Set<Long> experimentIds = new HashSet<Long>();
				for (Long experiment : this.experimentList) {
					experimentIds.add(experiment);
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
				
				for (Long expID : experimentList) {
					// loop for each search engine (3: omssa, xtandem, mascot)
					for (SerchEngineViewMulti current_view : SerchEngineViewMulti.values()) {
						// TODO: add paging (LIMIT / OFFSET) 
						PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + current_view.toString() + " WHERE fk_experimentid = ?");						
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
							Long ssid = rs.getLong("searchspectrumid");
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
								psm = new PeptideSpectrumMatch(ssid, hit);
								psm_mapping.put(psm_key, psm);
							}
							psm.addExperimentID(expID);
							psm.setTitle(spectrum_titlehash.toString());
							// DEALS WITH PEPTIDE
							PeptideHit pephit = null;
							if (peptide_mapping.containsKey(pep_seq)) {
								pephit = peptide_mapping.get(pep_seq);
								pephit.addSpectrumMatch(pep_seq, psm);
							} else {
								pephit = new PeptideHit(pep_seq, psm);
								pephit.addExperimentID(expID);
								peptide_mapping.put(pep_seq, pephit);
							}
							// DEALS WITH PROTEIN
							ProteinHit prot = null;
							if (protein_mapping.containsKey(accession)) {
								prot = protein_mapping.get(accession);
								prot.addPeptideHit(pephit);
								// counts peptide hits twice per peptide
								prot.getMetaProteinHit().addPeptideHit(pephit);
								// add required data to protein
								// sequence, uniprotentry, taxonomy
							} else {
								prot = new ProteinHit(accession);
								protein_mapping.put(accession, prot);
								prot.addPeptideHit(pephit);
								prot.setSequence(prot_seq);
								prot.setDescription(prot_description);
								prot.addExperimentID(expID);
								UniProtEntryMPA uniprot = this.makeUPEntry(uniprotid, conn);
								TaxonomyNode taxonode = uniprot.getTaxonomyNode();
								prot.setUniprotEntry(uniprot);
								prot.setTaxonomyNode(taxonode);
								// make metaprotein and finalize
								String metaprot_str = "Meta-Protein " + prot.getAccession();
								MetaProteinHit mph = new MetaProteinHit(metaprot_str, prot, uniprot);
								mph.addPeptideHit(pephit);
								mph.addExperimentID(expID);
								mph.setTaxonomyNode(taxonode);
								mph.setUniprotEntry(uniprot);
								prot.setMetaProteinHit(mph);
								this.searchResult.addMetaProtein(mph);
							}
							// report progress
							client.firePropertyChange("progressmade", true, false);
						}
					}
					// next experiment
				}
				// determine total spectral count
				int spectralCount = 0;
				for (Long expID : experimentList) {
					spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(expID, conn);
				}
				searchResult.setTotalSpectrumCount(spectralCount);
			} catch (SQLException errSQL) {
				errSQL.printStackTrace();
				return null;
			}
			// setting the FDR to 0.2 to set all proteins/peptides/spectra to "Visible" 
			searchResult.setFDR(0.2);
			Client.getInstance().firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
		}
		return searchResult;
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
	
	/**
	 * Implements abstract method, actual result-data is retrieved by 'getSearchResultByView()' and passed along.
	 * 
	 * @return searchResult - this objects search result object
	 * @author K. Schallert
	 *  
	 */ 
	@Override
	public DbSearchResult getSearchResult() {
		// call for data retrieval by view
		this.getSearchResultByView();
		return searchResult;
	}

	private Long findMaxProgress(Connection conn) throws SQLException {
		Long maxProgress = 0L;
		for (Long expID : experimentList) {
			// count number of omssahits
			PreparedStatement countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, expID);
			ResultSet counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			// count number of xtandemhits
			countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, expID);
			counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			// count number of mascothits
			countps = conn.prepareStatement("SELECT COUNT(*) " +
					"AS count " +
					"FROM searchspectrum " +
					"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
					"WHERE searchspectrum.fk_experimentid = ?");
			countps.setLong(1, expID);
			counter = countps.executeQuery();
			counter.next();
			maxProgress = maxProgress + counter.getInt(1);
			countps.close();
			counter.close();
		}
		return maxProgress;
	}
	

	@Override
	@Deprecated
	public void setSearchResult(DbSearchResult result) {
		// TODO Auto-generated method stub
	}
	
}
