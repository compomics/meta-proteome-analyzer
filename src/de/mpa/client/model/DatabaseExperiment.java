package de.mpa.client.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.compomics.util.experiment.biology.Peptide;

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
import de.mpa.client.model.dbsearch.UniRefEntryMPA;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.dialogs.GeneralDialog.Operation;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Spectrum.ChargeAndTitle;
import de.mpa.db.accessor.Taxonomy;
import de.mpa.db.accessor.UniprotentryAccessor;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.graphdb.nodes.MetaProtein;
// new imports
/**
 * Implementation of the experiment interface for database-linked experiments.
 * 
 * @author A. Behne
 */
public class DatabaseExperiment extends AbstractExperiment {

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
	 * Creates an empty database-linked experiment.
	 */
	public DatabaseExperiment() {
		this(null, null, null, null, null);
	}

	/**
	 * Creates a database-linked experiment using the specified database
	 * accessor object, experiment properties and parent project.
	 * @param experimentAcc the database accessor wrapping the experiment
	 * @param properties the experiment property accessor objects
	 * @param project the parent project
	 */
	public DatabaseExperiment(ExperimentAccessor experimentAcc, List<ExpProperty> properties, AbstractProject project) {
		this(experimentAcc.getExperimentid(), experimentAcc.getTitle(),
				experimentAcc.getCreationdate(), properties, project);
	}

	/**
	 * Creates a database-linked experiment using the specified title, database
	 * id, experiment properties and parent project.
	 * @param title the experiment title
	 * @param id the experiment's database id
	 * @param creationDate the project's creation date
	 * @param properties the experiment property accessor objects
	 * @param project the parent project
	 */
	public DatabaseExperiment(Long id, String title, Date creationDate, List<ExpProperty> properties, AbstractProject project) {
		super(id, title, creationDate, project);

		// init properties
		if (properties != null) {
			for (ExpProperty property : properties) {
				this.addProperty(property.getName(), property.getValue());
			}
		}

		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}

	@Override
	public boolean hasSearchResult() {
		try {
			return Searchspectrum.hasSearchSpectra(
					this.getID(), Client.getInstance().getConnection());
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		};
		return false;
	}
	
	/**
	 * This enum encodes the names of the result-views for later use
	 * 
	 * @author user
	 *
	 */
	private enum SerchEngineView {
		OMSSA("omssaresult"),
		XTANDEM("xtandemresult"),
		MASCOT("mascotresult");
		private final String text;
		private SerchEngineView(final String text) {
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
		// TODO: paging for sql queries
		// TODO: add proper client-frame feedback
		
		// only retrieve result if there is none so far
		if (searchResult == null) {
			try {
				// initialize stuff
				this.searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
				Connection conn = Client.getInstance().getConnection();
				Long expID = this.getID();
				Set<Long> experimentIds = new HashSet<Long>();
				experimentIds.add(expID);
				// init our 3 necassary mappings: psms / peptides / proteins
				HashMap<String, PeptideSpectrumMatch> psm_mapping = new HashMap<String, PeptideSpectrumMatch>();
				HashMap<String, PeptideHit> peptide_mapping = new HashMap<String, PeptideHit>();
				HashMap<String, ProteinHit> protein_mapping = new HashMap<String, ProteinHit>();
				// loop for each search engine (3: omssa, xtandem, mascot)
				for (SerchEngineView current_view : SerchEngineView.values()) {
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
						String spectrum_title = rs.getString("title");
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
						String psm_key = null;
						if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
							String[] spectitle_split = spectrum_title.split("(File)|(Spectrum)|(scans)");
							if (spectrum_title.contains("( \\(id)")) {
								psm_key = spectitle_split[1] + spectitle_split[2].split("( \\(id)")[0];
							} else {
								psm_key = spectitle_split[1] + spectitle_split[2];
							}
						} else {
							psm_key = spectrum_title;
						}
						// new psm-key spec-title + pepseq
						psm_key = psm_key + pep_seq;
						PeptideSpectrumMatch psm;
						// check psm redundancy and find psm
						if (psm_mapping.containsKey(psm_key)) {
							// existing psm
							psm = psm_mapping.get(psm_key);
							psm.addSearchHit(hit);
						} else {
							// new psm
							psm = new PeptideSpectrumMatch(ssid, hit);
							// TODO: HOW DID IT WORK WITHOUT THIS LINE!!
							psm_mapping.put(psm_key, psm);
						}
						psm.addExperimentIDs(experimentIds);
						psm.setTitle(spectrum_title);
						// DEALS WITH PEPTIDE
						PeptideHit pephit = null;
						if (peptide_mapping.containsKey(pep_seq)) {
							pephit = peptide_mapping.get(pep_seq);
							pephit.addSpectrumMatch(pep_seq, psm);
						} else {
							pephit = new PeptideHit(pep_seq, psm);
							pephit.addExperimentIDs(experimentIds);
							peptide_mapping.put(pep_seq, pephit);
						}
						// DEALS WITH PROTEIN
						ProteinHit prot = null;
						if (protein_mapping.containsKey(accession)) {
							prot = protein_mapping.get(accession);
							prot.addPeptideHit(pephit);
							prot.getMetaProteinHit().addPeptideHit(pephit);
							// add required data to protein
							// sequence, uniprotentry, taxonomy
							
						} else {
							prot = new ProteinHit(accession);
							protein_mapping.put(accession, prot);
							prot.addPeptideHit(pephit);
							prot.setSequence(prot_seq);
							prot.setDescription(prot_description);
							prot.addExperimentIDs(experimentIds);
							UniProtEntryMPA uniprot = this.makeUPEntry(uniprotid, conn);
							TaxonomyNode taxonode = uniprot.getTaxonomyNode();
							prot.setUniprotEntry(uniprot);
							prot.setTaxonomyNode(taxonode);
							// make metaprotein and finalize
							String metaprot_str = "Meta-Protein " + prot.getAccession();
							MetaProteinHit mph = new MetaProteinHit(metaprot_str, prot, uniprot);
							mph.addPeptideHit(pephit);
							mph.addExperimentIDs(experimentIds);
							mph.setTaxonomyNode(taxonode);
							mph.setUniprotEntry(uniprot);
							prot.setMetaProteinHit(mph);
							this.searchResult.addMetaProtein(mph);
						}
					}
				}
				// determine total spectral count
				int spectralCount = 0;
				spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(this.getID(), conn);
				searchResult.setTotalSpectrumCount(spectralCount);
			} catch (SQLException errSQL) {
				errSQL.printStackTrace();
			}
			// setting the FDR to 0.2 to set all proteins/peptides/spectra to "Visible" 
			searchResult.setFDR(0.2);
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
	@Deprecated
	public DbSearchResult getSearchResult() {
		// call for data retrieval by view
		this.getSearchResultByView();
		return searchResult;
	}

		
		
		// DEAD CODE:
//		if (searchResult == null) {
//			Client client = Client.getInstance();
//			try {
//				// initialize the result object
//				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
//				// initialize connection
//				Connection conn = client.getConnection();
//				// initialize maps and variables
//				HashMap<String, ProteinHit> protmap = new HashMap<String, ProteinHit>();
//				HashMap<String, PeptideHit> pepmap = new HashMap<String, PeptideHit>();	
//				HashMap<String, PeptideSpectrumMatch> psmmap = new HashMap<String, PeptideSpectrumMatch>();
//				HashMap<String, PeptideSpectrumMatch> mascotspectrummap = new HashMap<String, PeptideSpectrumMatch>();
//				PeptideHit peptideHit = null;
//				PeptideSpectrumMatch psm = null;
//				Set<Long> experimentIDs = new HashSet<Long>();
//				experimentIDs.add(this.getID());
//				// count number of omssahits
//				PreparedStatement countps = conn.prepareStatement("SELECT COUNT(*) " +
//						"AS count " +
//						"FROM searchspectrum " +
//						"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
//						"WHERE searchspectrum.fk_experimentid = ?");
//				countps.setLong(1, this.getID());
//				ResultSet counter = countps.executeQuery();
//				counter.next();
//				long maxProgress = counter.getInt(1);
//				// count number of xtandemhits
//				countps = conn.prepareStatement("SELECT COUNT(*) " +
//						"AS count " +
//						"FROM searchspectrum " +
//						"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
//						"WHERE searchspectrum.fk_experimentid = ?");
//				countps.setLong(1, this.getID());
//				counter = countps.executeQuery();
//				counter.next();
//				maxProgress = maxProgress + counter.getInt(1);
//				// count number of mascothits
//				countps = conn.prepareStatement("SELECT COUNT(*) " +
//						"AS count " +
//						"FROM searchspectrum " +
//						"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
//						"WHERE searchspectrum.fk_experimentid = ?");
//				countps.setLong(1, this.getID());
//				counter = countps.executeQuery();
//				counter.next();
//				maxProgress = maxProgress + counter.getInt(1);
//				countps.close();
//				counter.close();
//				// set up progress monitoring
//				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
//				client.firePropertyChange("indeterminate", true, false);
//				client.firePropertyChange("resetall", 0L, maxProgress);
//				client.firePropertyChange("resetcur", 0L, maxProgress);
//				// three almost identical blocks for omssa/xtandem/mascot
//				// start of omssa-block
//				// construct result set --> should reduce select * to specific columns to save memory? 
//				PreparedStatement ps = conn.prepareStatement("SELECT omssahit.omssahitid, omssahit.fk_searchspectrumid, omssahit.fk_proteinid, " +
//						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, " + 
//						"searchspectrum.fk_experimentid " +
//						"FROM searchspectrum " +
//						"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
//						"INNER JOIN protein on protein.proteinid=omssahit.fk_proteinid " +
//						"WHERE searchspectrum.fk_experimentid = ?");
//				ps.setLong(1, this.getID());
//				ps.setFetchSize(512);
//				ResultSet rs = ps.executeQuery();
//				// go through ommsa result table and construct object
//				while (rs.next()) {
//					String complete_accession = rs.getString("protein.accession");
//					PreparedStatement ps2 = conn.prepareStatement("SELECT omssahit.*, " +
//							"protein.description, protein.sequence, protein.proteinid, protein.accession, protein.fk_uniprotentryid," + 
//							"peptide.sequence " +
//							"FROM omssahit " +
//							"INNER JOIN protein on protein.proteinid=omssahit.fk_proteinid " +
//							"INNER JOIN peptide on peptide.peptideid=omssahit.fk_peptideid " +
//							"WHERE omssahit.omssahitid = ?");
//					ps2.setLong(1, rs.getLong("omssahit.omssahitid"));
//					ResultSet rs2 = ps2.executeQuery();
//					rs2.next();
//					// load searchhit from database and create new searchit object
//					SearchHit hit = new Omssahit(rs2, true);
//					// either add peptidespectrummatch to existing data or create new one 
//					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
//					if (psmmap.containsKey(psmkey)) {
//						psm = psmmap.get(psmkey);
//						psm.addSearchHit(hit);
//						psm.addExperimentIDs(experimentIDs);
//					} else {
//						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
//						// getting the spectrum-title
//						//Spectrum spectrum = Spectrum.findFromSpectrumID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
//						// workaround to deal with the second charge value provided by omssa (which is usally wrong) 
//						ChargeAndTitle current_spectrum = Spectrum.getTitleAndCharge(rs.getLong("searchspectrum.fk_spectrumid"), conn);
//						String spectrum_title = current_spectrum.getTitle();
//						int spectrum_charge = current_spectrum.getCharge();
//						if (spectrum_charge != 0) {
//							psm.setCharge(spectrum_charge);
//						}
//						psm.setTitle(spectrum_title);
//						// mapping the psms to spectrum titles to link mascot results to correct spectra 
//						String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
//						String spectitlekey;
//						if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
//							if (spectrum_title.contains("( \\(id)")) {
//								spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
//							} else {
//								spectitlekey = spectitle1[1] + spectitle1[2];
//							}
//						} else {
//							spectitlekey = spectrum_title;
//						}
//						mascotspectrummap.put(spectitlekey, psm);
//						psmmap.put(psmkey, psm);
//					}	
//
//					// either add peptide data to existing peptide or create new one
//					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
//						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addExperimentIDs(experimentIDs);
//					} else {
//						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
//					}
//					// either add protein data to existing protein or create new one
//					if (protmap.containsKey(complete_accession)) {
//						ProteinHit prothit = protmap.get(complete_accession);
//						prothit.addPeptideHit(peptideHit);
//						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
//						prothit.addExperimentIDs(experimentIDs);
//					} else {
//						// Define uniProt entry
//						UniProtEntryMPA uniprot = new UniProtEntryMPA();
//						// Define taxon node
//						TaxonomyNode taxonomyNode;
//						// Fetch Uniprot entry
//						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
//							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
//							uniprot = new UniProtEntryMPA(uniprotAccessor);
//							// retrieve taxonomy branch
//							taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniprotAccessor.getTaxid(), taxonomyMap, conn);		
//						} else {
//							// There is no uniprot entry
//							// mark taxonomy as 'unclassified'
//							if (unclassifiedNode == null) {
//								TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
//								unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
//							}
//							taxonomyNode = unclassifiedNode;
//						}
//						uniprot.setTaxonomyNode(taxonomyNode);
//						// create new protein-hit
//						ProteinHit prothit = new ProteinHit(complete_accession, rs2.getString("protein.description"), 
//								rs2.getString("protein.sequence"),
//								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
//						protmap.put(rs.getString("protein.accession"), prothit);
//						// add peptidehit - maybe unnecessary --> why unnecessary?
//						prothit.addPeptideHit(peptideHit);
//						// wrap new protein in meta-protein
//						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
////						mph.addPeptideHit(peptideHit);
//						prothit.setMetaProteinHit(mph);
//						// and add to database
//						searchResult.addMetaProtein(mph);
//					}
//					ps2.close();
//					rs2.close();
//				}
//				// progress bar
//				client.firePropertyChange("progressmade", true, false);
//				// close and finish
//				rs.close();
//				ps.close();
//				rs = null;
//				ps = null;
//				System.gc();
//				// end of omssa-block
//
//				//*****************************************************************************************************
//				//*****************************************************************************************************
//				//*****************************************************************************************************
//
//				// start of xtandemblock
//				// construct result set --> should reduce select * to specific columns to save memory?
//				ps = conn.prepareStatement("SELECT xtandemhit.xtandemhitid, xtandemhit.fk_searchspectrumid, xtandemhit.fk_proteinid, " +
//						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, " + 
//						"searchspectrum.fk_experimentid " +
//						"FROM searchspectrum " +
//						"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
//						"INNER JOIN protein on protein.proteinid=xtandemhit.fk_proteinid " +
//						"WHERE searchspectrum.fk_experimentid = ?" );											
//				ps.setLong(1, this.getID());
//				ps.setFetchSize(512);
//				rs = ps.executeQuery();			    
//
//				// go through table and construct object
//				while (rs.next()) {
//					String complete_accession = rs.getString("protein.accession");
//					PreparedStatement ps2 = conn.prepareStatement("SELECT xtandemhit.*, " +
//							"protein.description, protein.sequence, protein.proteinid, protein.accession, protein.fk_uniprotentryid, " + 
//							"peptide.sequence " +
//							"FROM xtandemhit " +
//							"INNER JOIN protein on protein.proteinid=xtandemhit.fk_proteinid " +
//							"INNER JOIN peptide on peptide.peptideid=xtandemhit.fk_peptideid " +
//							"WHERE xtandemhit.xtandemhitid = ?");
//					ps2.setLong(1, rs.getLong("xtandemhit.xtandemhitid"));
//					ResultSet rs2 = ps2.executeQuery();
//					rs2.next();
//					// create searchit
//					SearchHit hit = new XTandemhit(rs2, true);
//					// either add peptidespectrummatch to existing data or create new one 
//					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
//					if (psmmap.containsKey(psmkey)) {
//						psm = psmmap.get(psmkey);
//						psm.addSearchHit(hit);
//						psm.addExperimentIDs(experimentIDs);
//					} else {
//						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
//						// getting the spectrum-title
////						String spectrum_title = Spectrum.getSpectrumTitleFromID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
//						ChargeAndTitle current_spectrum = Spectrum.getTitleAndCharge(rs.getLong("searchspectrum.fk_spectrumid"), conn);
//						String spectrum_title = current_spectrum.getTitle();
//						int spectrum_charge = current_spectrum.getCharge();
//						if (spectrum_charge != 0) {
//							psm.setCharge(spectrum_charge);
//						}
//						psm.setTitle(spectrum_title);						
//						// mapping the psms to spectrum titles to link mascot results to correct spectra 
//						String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
//						String spectitlekey;
//						if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
//							if (spectrum_title.contains("( \\(id)")) {
//								spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
//							} else {
//								spectitlekey = spectitle1[1] + spectitle1[2];
//							}
//						} else {
//							spectitlekey = spectrum_title;
//						}
//						mascotspectrummap.put(spectitlekey, psm);
//						psmmap.put(psmkey, psm);
//					}				
//					// either add peptide data to existing peptide or create new one
//					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
//						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addExperimentIDs(experimentIDs);
//					} else {
//						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
//					}
//
//					// either add protein data to existing protein or create new one
//					if (protmap.containsKey(complete_accession)) {
//						ProteinHit prothit = protmap.get(complete_accession);				        
//						prothit.addPeptideHit(peptideHit);
//						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
//						prothit.addExperimentIDs(experimentIDs);
//					} else {
//						UniProtEntryMPA uniprot = new UniProtEntryMPA();
//						// Fetch Uniprot entry
//						TaxonomyNode taxonomyNode;
//						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
//							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
//							uniprot = new UniProtEntryMPA(uniprotAccessor);
//							// retrieve taxonomy branch
//							taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniprotAccessor.getTaxid(), taxonomyMap, conn);		
//						} else {
//							// There is no uniprot entry
//							// mark taxonomy as 'unclassified'
//							if (unclassifiedNode == null) {
//								TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
//								unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
//							}
//							taxonomyNode = unclassifiedNode;
//						}
//						uniprot.setTaxonomyNode(taxonomyNode);
//						// create new protein-hit
//						ProteinHit prothit = new ProteinHit(complete_accession, 
//								rs2.getString("protein.description"), 
//								rs2.getString("protein.sequence"),
//								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
//						protmap.put(rs.getString("protein.accession"), prothit);
//						// add peptidehit - maybe unneccassary 
//						prothit.addPeptideHit(peptideHit);
//						// wrap new protein in meta-protein
//						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
////						mph.addPeptideHit(peptideHit);
//						prothit.setMetaProteinHit(mph);
//						// and add to database
//						searchResult.addMetaProtein(mph);
//					}
//					ps2.close();
//					rs2.close();
//				}
//
//				// progress bar
//				client.firePropertyChange("progressmade", true, false);
//				// close and finish
//				rs.close();
//				ps.close();
//				rs = null;
//				ps = null;
//				System.gc();
//				//*****************************************************************************************************
//				//*****************************************************************************************************
//				//*****************************************************************************************************				
//
//				// end of xtandem-block
//				// start of mascot block
//				// construct result set --> should reduce select * to specific columns to save memory? 
//				ps = conn.prepareStatement("SELECT mascothit.mascothitid, mascothit.fk_searchspectrumid, mascothit.fk_proteinid, " +
//						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, protein.fk_uniprotentryid, " + 
//						"searchspectrum.fk_experimentid " +
//						"FROM searchspectrum " +
//						"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
//						"INNER JOIN protein on protein.proteinid=mascothit.fk_proteinid " +
//						"WHERE searchspectrum.fk_experimentid = ?");
//				ps.setLong(1, this.getID());
//				ps.setFetchSize(512);
//				rs = ps.executeQuery();
//				// go through table and construct object
//				while (rs.next()) {
//					String complete_accession = rs.getString("protein.accession");
//
//					PreparedStatement ps2 = conn.prepareStatement("SELECT mascothit.*, " +
//							"protein.description, protein.sequence, protein.proteinid, protein.accession,protein.fk_uniprotentryid, " + 
//							"peptide.sequence " +
//							"FROM mascothit " +
//							"INNER JOIN protein on protein.proteinid=mascothit.fk_proteinid " +
//							"INNER JOIN peptide on peptide.peptideid=mascothit.fk_peptideid " +
//							"WHERE mascothit.mascothitid = ?");
//					ps2.setLong(1, rs.getLong("mascothit.mascothitid"));
//					ResultSet rs2 = ps2.executeQuery();
//					rs2.next();
//					SearchHit hit = new Mascothit(rs2, true);
//					// either add peptidespectrummatch to existing data or create new one
//					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
//					// getting the spectrum-title
//					String spectrum_title = Spectrum.getSpectrumTitleFromID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
//					// mapping the psms to spectrum titles to link mascot results to correct spectra 
//					String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
//					String spectitlekey;
//					if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
//						if (spectrum_title.contains("( \\(id)")) {
//							spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
//						} else {
//							spectitlekey = spectitle1[1] + spectitle1[2];
//						}
//					} else {
//						spectitlekey = spectrum_title;
//					}
//					if (mascotspectrummap.containsKey(spectitlekey)) {			    			
//						psm = mascotspectrummap.get(spectitlekey);
//						psm.addSearchHit(hit);
//						psm.addExperimentIDs(experimentIDs);			    			
//					} else {
//						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
//						psm.setTitle(spectrum_title);
//						psmmap.put(psmkey, psm);
//					}
//					// either add peptide data to existing peptide or create new one
//					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
//						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addExperimentIDs(experimentIDs);
//					} else {
//						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
//						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
//						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
//					}
//					// either add protein data to existing protein or create new one
//					if (protmap.containsKey(rs.getObject("protein.accession"))) {
//						ProteinHit prothit = protmap.get(rs.getObject("protein.accession"));
//						prothit.addPeptideHit(peptideHit);
//						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
//						peptideHit.addExperimentIDs(experimentIDs);
//					} else {
//						UniProtEntryMPA uniprot = new UniProtEntryMPA();
//						// Fetch Uniprot entry
//						TaxonomyNode taxonomyNode;
//						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
//							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
//							uniprot = new UniProtEntryMPA(uniprotAccessor);
//							// retrieve taxonomy branch
//							taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniprotAccessor.getTaxid(), taxonomyMap, conn);		
//						} else {
//							// There is no uniprot entry
//							// mark taxonomy as 'unclassified'
//							if (unclassifiedNode == null) {
//								TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
//								unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
//							}
//							taxonomyNode = unclassifiedNode;
//						}
//						uniprot.setTaxonomyNode(taxonomyNode);
//						// create new protein-hit
//						ProteinHit prothit = new ProteinHit(complete_accession, 
//								rs2.getString("protein.description"), 
//								rs2.getString("protein.sequence"),
//								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
//						protmap.put(rs.getString("protein.accession"), prothit);
//						// add peptidehit - maybe unneccassary 
//						prothit.addPeptideHit(peptideHit);
//						// wrap new protein in meta-protein
//						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
////						mph.addPeptideHit(peptideHit);
//						prothit.setMetaProteinHit(mph);
//						// and add to database
//						searchResult.addMetaProtein(mph);
//					}
//					ps2.close();
//					rs2.close();
//				}
//				// progress bar
//				client.firePropertyChange("progressmade", true, false);
//				// close and finish
//				rs.close();
//				ps.close();
//				rs = null;
//				ps = null;
//				System.gc();
//				// end of mascot block
//				
//				// close Resultset and finish data acquisition
//				// determine total spectral count
//				int spectralCount = 0;
//				spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(this.getID(), conn);
//				searchResult.setTotalSpectrumCount(spectralCount);
//				// progress bar: finished
//				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
//				// finalize results
//				this.searchResult = searchResult;				
//			} catch (Exception e) {
//				JXErrorPane.showDialog(ClientFrame.getInstance(),
//						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//			}
//			// setting the FDR to 0.2 to set all proteins/peptides/spectra to "Visible" 
//			searchResult.setFDR(0.2);
//		}




	@Override
	public void setSearchResult(DbSearchResult searchResult) {
		this.searchResult = searchResult;
	}

	/**
	 * This method converts a search hit into a protein hit and adds it to the current protein hit set.
	 * @param result the database search result
	 * @param hit the search hit implementation
	 * @param experimentID the experiment ID
	 */
	public void addProteinSearchHit(DbSearchResult result, SearchHit hit,
			long experimentID, Connection conn) throws Exception {
		// wrap the search hit in a new PSM
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getFk_searchspectrumid(), hit);
		// XXX: make me faster!		
		Searchspectrum searchspectrum = Searchspectrum.findFromSearchSpectrumID(hit.getFk_searchspectrumid(), conn);
		Spectrum spectrum = Spectrum.findFromSpectrumID(searchspectrum.getFk_spectrumid(), conn);
		psm.setTitle(spectrum.getTitle());		
		// wrap the PSM in a new peptide
		PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
		PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);
		// retrieve the protein database entry
		long proteinID = hit.getFk_proteinid();
		ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);

		// retrieve UniProt meta-data
		UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(protein.getFK_UniProtID(), conn);
		UniProtEntryMPA uniProtEntryMPA = null;

		TaxonomyNode taxonomyNode = null;
		// if meta-data exists...
		if (uniprotAccessor != null) {
			// Create new uniprot entry
			uniProtEntryMPA = new UniProtEntryMPA(uniprotAccessor);

			// ... wrap it in a UniProt entry container class
			long taxID = uniprotAccessor.getTaxid();

			// retrieve taxonomy branch
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);			
		} else {
			// create dummy UniProt entry
			uniProtEntryMPA = null;

			// mark taxonomy as 'unclassified'
			if (unclassifiedNode == null) {
				TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
				unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
			}
			taxonomyNode = unclassifiedNode;
		}		
		// create a new protein hit and add it to the result
		result.addProtein(new ProteinHit(protein.getAccession(), protein.getDescription(), protein.getSequence(),
				peptideHit, uniProtEntryMPA, taxonomyNode, experimentID));
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().putAll(properties);

			ProjectManager manager = ProjectManager.getInstance();

			// create new experiment in the remote database
			ExperimentAccessor experimentAcc = manager.createNewExperiment(this.getProject().getID(), this.getTitle());
			this.setId(experimentAcc.getExperimentid());
			this.setCreationDate(experimentAcc.getCreationdate());

			// store experiment properties in the remote database
			manager.addExperimentProperties(this.getID(), this.getProperties());

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().clear();
			this.getProperties().putAll(properties);

			ProjectManager manager = ProjectManager.getInstance();

			// modify the experiment name
			manager.modifyExperimentName(this.getID(), this.getTitle());

			// modify the experiment properties
			manager.modifyExperimentProperties(this.getID(), this.getProperties(), (List<Operation>) params[0]);

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	public void delete() {
		try {			
			ProjectManager manager = ProjectManager.getInstance();

			// remove experiment and all its properties
			manager.deleteExperiment(this.getID());

		} catch (SQLException e) {
			e.printStackTrace();
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

}
