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
	 * retrieve search-results from a experimentID and construct a results object
	 * currently only includes searchresults from omssa/xtandem/mascot
	 * 
	 * --> this new implementation should be divided into smaller methods particularly the sql-querys 
	 * --> many functions can be optimized further
	 * 
	 * @author K. Schallert
	 *  
	 */ 
	@Override
	public DbSearchResult getSearchResult() {
		if (searchResult == null) {
			Client client = Client.getInstance();
			try {				
				// initialize the result object
				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
				// initialize connection
				Connection conn = client.getConnection();
				// initialize maps and variables
				HashMap<String, ProteinHit> protmap = new HashMap<String, ProteinHit>();
				HashMap<String, PeptideHit> pepmap = new HashMap<String, PeptideHit>();	
				HashMap<String, PeptideSpectrumMatch> psmmap = new HashMap<String, PeptideSpectrumMatch>();
				HashMap<String, PeptideSpectrumMatch> mascotspectrummap = new HashMap<String, PeptideSpectrumMatch>();
				PeptideHit peptideHit = null;
				PeptideSpectrumMatch psm = null;
				Set<Long> experimentIDs = new HashSet<Long>();
				experimentIDs.add(this.getID());
				// count number of omssahits
				PreparedStatement countps = conn.prepareStatement("SELECT COUNT(*) " +
						"AS count " +
						"FROM searchspectrum " +
						"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
						"WHERE searchspectrum.fk_experimentid = ?");
				countps.setLong(1, this.getID());
				ResultSet counter = countps.executeQuery();
				counter.next();
				long maxProgress = counter.getInt(1);
				// count number of xtandemhits
				countps = conn.prepareStatement("SELECT COUNT(*) " +
						"AS count " +
						"FROM searchspectrum " +
						"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
						"WHERE searchspectrum.fk_experimentid = ?");
				countps.setLong(1, this.getID());
				counter = countps.executeQuery();
				counter.next();
				maxProgress = maxProgress + counter.getInt(1);
				// count number of mascothits
				countps = conn.prepareStatement("SELECT COUNT(*) " +
						"AS count " +
						"FROM searchspectrum " +
						"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
						"WHERE searchspectrum.fk_experimentid = ?");
				countps.setLong(1, this.getID());
				counter = countps.executeQuery();
				counter.next();
				maxProgress = maxProgress + counter.getInt(1);
				countps.close();
				counter.close();
				// set up progress monitoring
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("resetall", 0L, maxProgress);
				client.firePropertyChange("resetcur", 0L, maxProgress);
				// three almost identical blocks for omssa/xtandem/mascot
				// start of omssa-block
				// construct result set --> should reduce select * to specific columns to save memory? 
				PreparedStatement ps = conn.prepareStatement("SELECT omssahit.omssahitid, omssahit.fk_searchspectrumid, omssahit.fk_proteinid, " +
						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, " + 
						"searchspectrum.fk_experimentid " +
						"FROM searchspectrum " +
						"INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
						"INNER JOIN protein on protein.proteinid=omssahit.fk_proteinid " +
						"WHERE searchspectrum.fk_experimentid = ?");
				ps.setLong(1, this.getID());
				ps.setFetchSize(512);
				ResultSet rs = ps.executeQuery();
				// go through ommsa result table and construct object
				while (rs.next()) {
					String complete_accession = rs.getString("protein.accession");
					PreparedStatement ps2 = conn.prepareStatement("SELECT omssahit.*, " +
							"protein.description, protein.sequence, protein.proteinid, protein.accession, protein.fk_uniprotentryid," + 
							"peptide.sequence " +
							"FROM omssahit " +
							"INNER JOIN protein on protein.proteinid=omssahit.fk_proteinid " +
							"INNER JOIN peptide on peptide.peptideid=omssahit.fk_peptideid " +
							"WHERE omssahit.omssahitid = ?");
					ps2.setLong(1, rs.getLong("omssahit.omssahitid"));
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					// load searchhit from database and create new searchit object
					SearchHit hit = new Omssahit(rs2, true);
					// either add peptidespectrummatch to existing data or create new one 
					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
					if (psmmap.containsKey(psmkey)) {
						psm = psmmap.get(psmkey);
						psm.addSearchHit(hit);
						psm.addExperimentIDs(experimentIDs);
					} else {
						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
						// getting the spectrum-title
						//Spectrum spectrum = Spectrum.findFromSpectrumID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
						// workaround to deal with the second charge value provided by omssa (which is usally wrong) 
						ChargeAndTitle current_spectrum = Spectrum.getTitleAndCharge(rs.getLong("searchspectrum.fk_spectrumid"), conn);
						String spectrum_title = current_spectrum.getTitle();
						int spectrum_charge = current_spectrum.getCharge();
						if (spectrum_charge != 0) {
							psm.setCharge(spectrum_charge);
						}
						psm.setTitle(spectrum_title);							
						// mapping the psms to spectrum titles to link mascot results to correct spectra 
						String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
						String spectitlekey;
						if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
							if (spectrum_title.contains("( \\(id)")) {
								spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
							} else {
								spectitlekey = spectitle1[1] + spectitle1[2];
							}
						} else {
							spectitlekey = spectrum_title;
						}
						mascotspectrummap.put(spectitlekey, psm);
						psmmap.put(psmkey, psm);
					}	

					// either add peptide data to existing peptide or create new one
					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						peptideHit.addExperimentIDs(experimentIDs);
					} else {
						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
					}
					// either add protein data to existing protein or create new one
					if (protmap.containsKey(complete_accession)) {
						ProteinHit prothit = protmap.get(complete_accession);				        
						prothit.addPeptideHit(peptideHit);
						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
						prothit.addExperimentIDs(experimentIDs);				        
					} else {
						// Define uniProt entry
						UniProtEntryMPA uniprot = new UniProtEntryMPA();
						// Define taxon node
						TaxonomyNode taxonomyNode;
						// Fetch Uniprot entry
						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
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
						// create new protein-hit
						ProteinHit prothit = new ProteinHit(complete_accession, rs2.getString("protein.description"), 
								rs2.getString("protein.sequence"),
								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
						protmap.put(rs.getString("protein.accession"), prothit);
						// add peptidehit - maybe unnecessary --> why unnecessary?
						prothit.addPeptideHit(peptideHit);
						// wrap new protein in meta-protein
						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
						mph.addPeptideHit(peptideHit);
						prothit.setMetaProteinHit(mph);
						// and add to database
						searchResult.addMetaProtein(mph);
					}
					ps2.close();
					rs2.close();
				}
				// progress bar
				client.firePropertyChange("progressmade", true, false);
				// close and finish
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				System.gc();
				// end of omssa-block

				//*****************************************************************************************************
				//*****************************************************************************************************
				//*****************************************************************************************************

				// start of xtandemblock
				// construct result set --> should reduce select * to specific columns to save memory?
				ps = conn.prepareStatement("SELECT xtandemhit.xtandemhitid, xtandemhit.fk_searchspectrumid, xtandemhit.fk_proteinid, " +
						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, " + 
						"searchspectrum.fk_experimentid " +
						"FROM searchspectrum " +
						"INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
						"INNER JOIN protein on protein.proteinid=xtandemhit.fk_proteinid " +
						"WHERE searchspectrum.fk_experimentid = ?" );											
				ps.setLong(1, this.getID());
				ps.setFetchSize(512);
				rs = ps.executeQuery();			    

				// go through table and construct object
				while (rs.next()) {
					String complete_accession = rs.getString("protein.accession");
					PreparedStatement ps2 = conn.prepareStatement("SELECT xtandemhit.*, " +
							"protein.description, protein.sequence, protein.proteinid, protein.accession, protein.fk_uniprotentryid, " + 
							"peptide.sequence " +
							"FROM xtandemhit " +
							"INNER JOIN protein on protein.proteinid=xtandemhit.fk_proteinid " +
							"INNER JOIN peptide on peptide.peptideid=xtandemhit.fk_peptideid " +
							"WHERE xtandemhit.xtandemhitid = ?");
					ps2.setLong(1, rs.getLong("xtandemhit.xtandemhitid"));
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					// create searchit
					SearchHit hit = new XTandemhit(rs2, true);
					// either add peptidespectrummatch to existing data or create new one 
					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
					if (psmmap.containsKey(psmkey)) {
						psm = psmmap.get(psmkey);
						psm.addSearchHit(hit);
						psm.addExperimentIDs(experimentIDs);
					} else {
						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
						// getting the spectrum-title
						String spectrum_title = Spectrum.getSpectrumTitleFromID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
						psm.setTitle(spectrum_title);							
						// mapping the psms to spectrum titles to link mascot results to correct spectra 
						String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
						String spectitlekey;
						if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
							if (spectrum_title.contains("( \\(id)")) {
								spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
							} else {
								spectitlekey = spectitle1[1] + spectitle1[2];
							}
						} else {
							spectitlekey = spectrum_title;
						}
						mascotspectrummap.put(spectitlekey, psm);
						psmmap.put(psmkey, psm);
					}				
					// either add peptide data to existing peptide or create new one
					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						peptideHit.addExperimentIDs(experimentIDs);
					} else {
						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
					}

					// either add protein data to existing protein or create new one
					if (protmap.containsKey(complete_accession)) {
						ProteinHit prothit = protmap.get(complete_accession);				        
						prothit.addPeptideHit(peptideHit);
						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
						prothit.addExperimentIDs(experimentIDs);
					} else {
						UniProtEntryMPA uniprot = new UniProtEntryMPA();
						// Fetch Uniprot entry
						TaxonomyNode taxonomyNode;
						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
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
						// create new protein-hit
						ProteinHit prothit = new ProteinHit(complete_accession, 
								rs2.getString("protein.description"), 
								rs2.getString("protein.sequence"),
								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
						protmap.put(rs.getString("protein.accession"), prothit);
						// add peptidehit - maybe unneccassary 
						//					prothit.addPeptideHit(peptideHit);
						// wrap new protein in meta-protein
						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
						mph.addPeptideHit(peptideHit);
						prothit.setMetaProteinHit(mph);
						// and add to database
						searchResult.addMetaProtein(mph);
					}
					ps2.close();
					rs2.close();
				}

				// progress bar
				client.firePropertyChange("progressmade", true, false);
				// close and finish
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				System.gc();
				//*****************************************************************************************************
				//*****************************************************************************************************
				//*****************************************************************************************************				

				// end of xtandem-block
				// start of mascot block
				// construct result set --> should reduce select * to specific columns to save memory? 
				ps = conn.prepareStatement("SELECT mascothit.mascothitid, mascothit.fk_searchspectrumid, mascothit.fk_proteinid, " +
						"searchspectrum.searchspectrumid, searchspectrum.fk_spectrumid, protein.accession, protein.fk_uniprotentryid, " + 
						"searchspectrum.fk_experimentid " +
						"FROM searchspectrum " +
						"INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
						"INNER JOIN protein on protein.proteinid=mascothit.fk_proteinid " +
						"WHERE searchspectrum.fk_experimentid = ?");
				ps.setLong(1, this.getID());
				ps.setFetchSize(512);
				rs = ps.executeQuery();
				// go through table and construct object
				while (rs.next()) {
					String complete_accession = rs.getString("protein.accession");

					PreparedStatement ps2 = conn.prepareStatement("SELECT mascothit.*, " +
							"protein.description, protein.sequence, protein.proteinid, protein.accession,protein.fk_uniprotentryid, " + 
							"peptide.sequence " +
							"FROM mascothit " +
							"INNER JOIN protein on protein.proteinid=mascothit.fk_proteinid " +
							"INNER JOIN peptide on peptide.peptideid=mascothit.fk_peptideid " +
							"WHERE mascothit.mascothitid = ?");
					ps2.setLong(1, rs.getLong("mascothit.mascothitid"));
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					SearchHit hit = new Mascothit(rs2, true);
					// either add peptidespectrummatch to existing data or create new one
					String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs2.getString("peptide.sequence");
					// getting the spectrum-title
					String spectrum_title = Spectrum.getSpectrumTitleFromID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
					// mapping the psms to spectrum titles to link mascot results to correct spectra 
					String[] spectitle1 = spectrum_title.split("(File)|(Spectrum)|(scans)");							
					String spectitlekey;
					if (spectrum_title.contains("File") && spectrum_title.contains("Spectrum") && spectrum_title.contains("scans")) {
						if (spectrum_title.contains("( \\(id)")) {
							spectitlekey = spectitle1[1] + spectitle1[2].split("( \\(id)")[0];
						} else {
							spectitlekey = spectitle1[1] + spectitle1[2];
						}
					} else {
						spectitlekey = spectrum_title;
					}
					if (mascotspectrummap.containsKey(spectitlekey)) {			    			
						psm = mascotspectrummap.get(spectitlekey);
						psm.addSearchHit(hit);
						psm.addExperimentIDs(experimentIDs);			    			
					} else {
						psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
						psm.setTitle(spectrum_title);
						psmmap.put(psmkey, psm);
					}
					// either add peptide data to existing peptide or create new one
					if (pepmap.containsKey(rs2.getString("peptide.sequence"))) {
						peptideHit = pepmap.get(rs2.getString("peptide.sequence"));
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						peptideHit.addExperimentIDs(experimentIDs);
					} else {
						peptideHit = new PeptideHit(rs2.getString("peptide.sequence"), psm);
						peptideHit.addSpectrumMatch(rs2.getString("peptide.sequence"), psm);
						pepmap.put(rs2.getString("peptide.sequence"), peptideHit);
					}
					// either add protein data to existing protein or create new one
					if (protmap.containsKey(rs.getObject("protein.accession"))) {
						ProteinHit prothit = protmap.get(rs.getObject("protein.accession"));
						prothit.addPeptideHit(peptideHit);
						prothit.getMetaProteinHit().addPeptideHit(peptideHit);
						peptideHit.addExperimentIDs(experimentIDs);
					} else {
						UniProtEntryMPA uniprot = new UniProtEntryMPA();
						// Fetch Uniprot entry
						TaxonomyNode taxonomyNode;
						if (rs2.getLong("protein.fk_uniprotentryid") != -1L) {
							UniprotentryAccessor uniprotAccessor = UniprotentryAccessor.findFromID(rs2.getLong("protein.fk_uniprotentryid"), conn);
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
						// create new protein-hit
						ProteinHit prothit = new ProteinHit(complete_accession, 
								rs2.getString("protein.description"), 
								rs2.getString("protein.sequence"),
								peptideHit, uniprot, taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
						protmap.put(rs.getString("protein.accession"), prothit);
						// add peptidehit - maybe unneccassary 
						prothit.addPeptideHit(peptideHit);
						// wrap new protein in meta-protein
						MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit, prothit.getUniProtEntry());
						mph.addPeptideHit(peptideHit);
						prothit.setMetaProteinHit(mph);
						// and add to database
						searchResult.addMetaProtein(mph);
					}
					ps2.close();
					rs2.close();
				}
				// progress bar
				client.firePropertyChange("progressmade", true, false);
				// close and finish
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				System.gc();

				// end of mascot block
				// close Resultset and finish data acquisition

				// determine total spectral count
				int spectralCount = 0;
				spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(this.getID(), conn);
				searchResult.setTotalSpectrumCount(spectralCount);
				// progress bar: finished
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
				// finalize results
				this.searchResult = searchResult;				
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			
			// setting the FDR to 0.2 to set all proteins/peptides/spectra to "Visible" 
			searchResult.setFDR(0.2);
		
			/* 
			 *  		test searchresults		
			 */
//			searchResult.setFDR(0.01);
			// test searchresults
//			Connection conn = null;
//			try {
//				conn = Client.getInstance().getConnection();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			for (ProteinHit mp : searchResult.getMetaProteins()) {
//				if (mp.getUniProtEntry().getKeywords().contains("Apoptosis")) {
//					System.out.println("Metaprotein: " + mp.getAccession() + " specids: " + mp.getSpectrumIDs());
//					for (PeptideHit peptide : mp.getPeptideHitList()) {
//						if (peptide.isVisible()) {
//							System.out.println("Peptide: " + peptide.getSequence());
//							for (SpectrumMatch specmatch : peptide.getSpectrumMatches()) {
//								PeptideSpectrumMatch psm = (PeptideSpectrumMatch) specmatch;
//								if ((psm.isVisible())) {
//									for (SearchHit sh : psm.getSearchHits()) {
//										
//										System.out.println("PSM, pepid: " + sh.getFk_peptideid() + " accession: " + sh.getAccession() + " ssID:" + sh.getFk_searchspectrumid() + " qval: " + sh.getQvalue());
//										System.out.println("Charge: " + sh.getCharge() + " Charge: " + psm.getCharge());
//										try {
//											PreparedStatement ps = conn.prepareStatement("SELECT fk_spectrumid FROM searchspectrum WHERE searchspectrumid = ?");
//											ps.setLong(1, psm.getSearchSpectrumID());
//											ResultSet rs = ps.executeQuery();
//											rs.next();
//											Long specid = rs.getLong("fk_spectrumid");
//										} catch (SQLException e) {
//											e.printStackTrace();
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			/* 
			 *  		test searchresults END		
			 */
		}
		return searchResult;
	}




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
