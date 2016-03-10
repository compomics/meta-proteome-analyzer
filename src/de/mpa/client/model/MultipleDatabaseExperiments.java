package de.mpa.client.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.ClientFrame;
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
import de.mpa.db.accessor.Taxonomy;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.extractor.SearchHitExtractor;

/**
 * Implementation of the experiment interface for multiple fused database-linked experiments.
 * @author R. Heyer
 */
public class MultipleDatabaseExperiments extends AbstractExperiment{
	
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
	 * retrieve search-results from a experimentID and construct a results object
	 * currently only includes searchresults from omssa/xtandem/mascot
	 * This code is copied from DatabaseEperiment.java and now includes a for loop for multiple experiments
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
				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), "MultipleDBresult" +this.getTitle(), null);
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
			    
			    
				for (Long expID : experimentList) {
				    experimentIDs.add(expID);
				    // count number of omssahits
					PreparedStatement countps = conn.prepareStatement("SELECT COUNT(*) " +
																	  "AS count " +
																	  "FROM searchspectrum " +
																	  "INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " +
																	  "WHERE searchspectrum.fk_experimentid = ?");
					countps.setLong(1, expID);
					ResultSet counter = countps.executeQuery();
					counter.next();
				    long maxProgress = counter.getInt(1);
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
					// set up progress monitoring
				    client.firePropertyChange("new message", null, "BUILDING EXPERIMENT " + expID + " RESULT OBJECT");
					client.firePropertyChange("indeterminate", true, false);
					client.firePropertyChange("resetall", 0L, maxProgress);
					client.firePropertyChange("resetcur", 0L, maxProgress);
					// three almost identical blocks for omssa/xtandem/mascot
					// start of omssa-block
					// consturct result set --> should reduce select * to specific columns to save memory? 
					PreparedStatement ps = conn.prepareStatement("SELECT * from searchspectrum " +
																 "INNER JOIN omssahit on omssahit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
																 "INNER JOIN protein on protein.proteinid=omssahit.fk_proteinid " +
																 "INNER JOIN peptide on peptide.peptideid=omssahit.fk_peptideid " +
					 											 "WHERE searchspectrum.fk_experimentid = ?");
											
				    ps.setLong(1, expID);
				    ResultSet rs = ps.executeQuery();
					// go through table and construct object
				    while (rs.next()) {
				    	// progress bar
				    	client.firePropertyChange("progressmade", true, false);
				    	// load searchhit from database and create new searchit object
				    	// --> how can i get this from rs directly?
				    	PreparedStatement prs = conn.prepareStatement("SELECT o.*, p.sequence, pr.accession " +
				    												  "FROM omssahit o, searchspectrum s, peptide p, protein pr " +
				    												  "WHERE o.fk_peptideid = p.peptideid " +
				    												  "AND o.fk_proteinid = pr.proteinid " +
				    												  "AND s.searchspectrumid = o.fk_searchspectrumid " +
				    												  "AND o.omssahitid = ?");
				    	prs.setLong(1, rs.getLong("omssahit.omssahitid"));
				    	ResultSet aRS = prs.executeQuery();			    				    	
				    	aRS.next();
				    	// create searchit
				    	SearchHit hit = new Omssahit(aRS);			    	
				    	prs.close();
				    	aRS.close();			        
				    	// either add peptidespectrummatch to existing data or create new one 
				    	String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs.getString("peptide.sequence");
				    	if (psmmap.containsKey(psmkey)) {
				    		psm = psmmap.get(psmkey);
				    		psm.addSearchHit(hit);
				    		psm.addExperimentIDs(experimentIDs);
				    	}
				    	else {
					    	psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
					    	// getting the spectrum-title
							Spectrum spectrum = Spectrum.findFromSpectrumID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
							psm.setTitle(spectrum.getTitle());
							// mapping the psms to spectrum titles to link mascot results to correct spectra 
							String[] spectitle1 = spectrum.getTitle().split("(File)|(Spectrum)|(scans)");
							String spectitlekey = spectitle1[1] + spectitle1[2];
							mascotspectrummap.put(spectitlekey, psm);
							psmmap.put(psmkey, psm);
				    	}			    		
					    // either add peptide data to existing peptide or create new one
					    if (pepmap.containsKey(rs.getString("peptide.sequence"))) {
					    	peptideHit = pepmap.get(rs.getString("peptide.sequence"));
					    	peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
					    	peptideHit.addExperimentIDs(experimentIDs);
					    }
					    else {
							peptideHit = new PeptideHit(rs.getString("peptide.sequence"), psm);
							peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
							pepmap.put(rs.getString("peptide.sequence"), peptideHit);
					    }
					    // either add protein data to existing protein or create new one
					    if (protmap.containsKey(rs.getObject("protein.accession"))) {
					        ProteinHit prothit = protmap.get(rs.getObject("protein.accession"));				        
					        prothit.addPeptideHit(peptideHit);
					        prothit.addExperimentIDs(experimentIDs);				        
					    }
					    else {
							// retrieve UniProt meta-data
							Uniprotentry uniprotEntryAccessor = Uniprotentry.findFromProteinID(rs.getLong("protein.proteinid"), conn);
							ReducedUniProtEntry uniprotEntry = null;
							TaxonomyNode taxonomyNode = null;
							// if meta-data exists...
							if (uniprotEntryAccessor != null) {
								// ... wrap it in a UniProt entry container class
								long taxID = uniprotEntryAccessor.getTaxid();
								uniprotEntry = new ReducedUniProtEntry(taxID,
										uniprotEntryAccessor.getKeywords(),
										uniprotEntryAccessor.getEcnumber(),
										uniprotEntryAccessor.getKonumber(),
										uniprotEntryAccessor.getUniref100(),
										uniprotEntryAccessor.getUniref90(),
										uniprotEntryAccessor.getUniref50());
								
								// retrieve taxonomy branch
								taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);			
							} else {
								// create dummy UniProt entry
								uniprotEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
								
								// mark taxonomy as 'unclassified'
								if (unclassifiedNode == null) {
									TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
									unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
								}
								taxonomyNode = unclassifiedNode;
							}						
							// create protein-hit
					    	ProteinHit prothit = new ProteinHit(rs.getString("protein.accession"), 
					    			   			rs.getString("protein.description"), 
					    			   			rs.getString("protein.sequence"), peptideHit, uniprotEntry, 
					    			   			taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
					    	protmap.put(rs.getString("protein.accession"), prothit);
					    	// add peptidehit - maybe unneccassary 
					    	prothit.addPeptideHit(peptideHit);
							// wrap new protein in meta-protein
							MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + prothit.getAccession(), prothit);
							prothit.setMetaProteinHit(mph);
							// and add to database
							searchResult.addMetaProtein(mph);
							}
					    }				    			    
				    // close and finish
				    ps.close();
				    rs.close();
				    // end of omssa-block
				    // start of xtandemblock
					// consturct result set --> should reduce select * to specific columns to save memory?
					ps = conn.prepareStatement("SELECT * from searchspectrum " +
											   "INNER JOIN xtandemhit on xtandemhit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
											   "INNER JOIN protein on protein.proteinid=xtandemhit.fk_proteinid " +
											   "INNER JOIN peptide on peptide.peptideid=xtandemhit.fk_peptideid " +
					 						   "WHERE searchspectrum.fk_experimentid = ?");
											
				    ps.setLong(1, expID);
				    rs = ps.executeQuery();			    
					// go through table and construct object
				    while (rs.next()) {
				    	client.firePropertyChange("progressmade", true, false);
				    	// load searchhit from database and create new searchit object
				    	// --> how can i get this from rs directly?
				    	PreparedStatement prs = conn.prepareStatement("SELECT x.*, p.sequence, pr.accession " +
				    											      "FROM xtandemhit x, searchspectrum s, peptide p, protein pr " +
				    												  "WHERE x.fk_peptideid = p.peptideid " +
				    												  "AND x.fk_proteinid = pr.proteinid " +
				    												  "AND s.searchspectrumid = x.fk_searchspectrumid " +
				    												  "AND x.xtandemhitid = ?");
				    	prs.setLong(1, rs.getLong("xtandemhit.xtandemhitid"));
				    	ResultSet aRS = prs.executeQuery();			    				    	
				    	aRS.next();
				    	// create searchit
				    	SearchHit hit = new XTandemhit(aRS);			    	
				    	prs.close();
				    	aRS.close();			        
				    	// either add peptidespectrummatch to existing data or create new one 
				    	String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs.getString("peptide.sequence");
				    	if (psmmap.containsKey(psmkey)) {
				    		psm = psmmap.get(psmkey);
				    		psm.addSearchHit(hit);
				    		psm.addExperimentIDs(experimentIDs);
				    	}
				    	else {
					    	psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
					    	// getting the spectrum-title
							Spectrum spectrum = Spectrum.findFromSpectrumID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
							psm.setTitle(spectrum.getTitle());
							// mapping the psms to spectrum titles to link mascot results to correct spectra 
							String[] spectitle1 = spectrum.getTitle().split("(File)|(Spectrum)|(scans)");
							String spectitlekey = spectitle1[1] + spectitle1[2];
							mascotspectrummap.put(spectitlekey, psm);
							psmmap.put(psmkey, psm);
				    	}				
					    // either add peptide data to existing peptide or create new one
					    if (pepmap.containsKey(rs.getString("peptide.sequence"))) {
					    	peptideHit = pepmap.get(rs.getString("peptide.sequence"));
					    	peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
					    	peptideHit.addExperimentIDs(experimentIDs);
					    }
					    else {
							peptideHit = new PeptideHit(rs.getString("peptide.sequence"), psm);
					    	peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
							pepmap.put(rs.getString("peptide.sequence"), peptideHit);
					    }
					    // either add protein data to existing protein or create new one
					    if (protmap.containsKey(rs.getObject("protein.accession"))) {
					        ProteinHit prothit = protmap.get(rs.getObject("protein.accession"));
					        prothit.addPeptideHit(peptideHit);				        
					    }
					    else {
							// retrieve UniProt meta-data
							Uniprotentry uniprotEntryAccessor = Uniprotentry.findFromProteinID(rs.getLong("protein.proteinid"), conn);
							ReducedUniProtEntry uniprotEntry = null;
							TaxonomyNode taxonomyNode = null;
							// if meta-data exists...
							if (uniprotEntryAccessor != null) {
								// ... wrap it in a UniProt entry container class
								long taxID = uniprotEntryAccessor.getTaxid();
								uniprotEntry = new ReducedUniProtEntry(taxID,
										uniprotEntryAccessor.getKeywords(),
										uniprotEntryAccessor.getEcnumber(),
										uniprotEntryAccessor.getKonumber(),
										uniprotEntryAccessor.getUniref100(),
										uniprotEntryAccessor.getUniref90(),
										uniprotEntryAccessor.getUniref50());
								
								// retrieve taxonomy branch
								taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);			
							} else {
								// create dummy UniProt entry
								uniprotEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
								
								// mark taxonomy as 'unclassified'
								if (unclassifiedNode == null) {
									TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
									unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
								}
								taxonomyNode = unclassifiedNode;
							}						
							// create protein-hit
					    	ProteinHit prothit = new ProteinHit(rs.getString("protein.accession"), 
					    			   			rs.getString("protein.description"), 
					    			   			rs.getString("protein.sequence"), peptideHit, uniprotEntry, 
					    			   			taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
					    	protmap.put(rs.getString("protein.accession"), prothit);
					    	prothit.addPeptideHit(peptideHit);
							// wrap new protein in meta-protein
							MetaProteinHit mph = new MetaProteinHit(
									"Meta-Protein " + prothit.getAccession(), prothit);
							prothit.setMetaProteinHit(mph);
							// and add to database
							searchResult.addMetaProtein(mph);
							}
					    }				    			    
				    // close and finish
				    ps.close();
				    rs.close();
				    // end of xtandem-block
				    // start of mascot block
					// consturct result set --> should reduce select * to specific columns to save memory? 
				    ps = conn.prepareStatement("SELECT * from searchspectrum " +
										 	   "INNER JOIN mascothit on mascothit.fk_searchspectrumid=searchspectrum.searchspectrumid " + 
											   "INNER JOIN protein on protein.proteinid=mascothit.fk_proteinid " +
											   "INNER JOIN peptide on peptide.peptideid=mascothit.fk_peptideid " +															 
				    						   "WHERE searchspectrum.fk_experimentid = ?");
				    ps.setLong(1, expID);
				    rs = ps.executeQuery();
					// go through table and construct object
				    while (rs.next()) {
				    	client.firePropertyChange("progressmade", true, false);
				    	// load searchhit from database and create new searchit object
				    	// --> how can i get this from rs directly?
				    	PreparedStatement prs = conn.prepareStatement("SELECT m.*, p.sequence, pr.accession " +
				    												  "FROM mascothit m, searchspectrum s, peptide p, protein pr " +
				    												  "WHERE m.fk_peptideid = p.peptideid " +
				    												  "AND m.fk_proteinid = pr.proteinid " +
				    												  "AND s.searchspectrumid = m.fk_searchspectrumid " +
				    												  "AND m.mascothitid = ?");
				    	prs.setLong(1, rs.getLong("mascothit.mascothitid"));
				    	ResultSet aRS = prs.executeQuery();			    				    	
				    	// create searchhit
				    	aRS.next();
				    	SearchHit hit = new Mascothit(aRS);			    	
				    	prs.close();
				    	aRS.close();			        
				    	// either add peptidespectrummatch to existing data or create new one
				    	String psmkey = rs.getString("searchspectrum.searchspectrumid") + rs.getString("peptide.sequence");
				    	if (psmmap.containsKey(psmkey)) {
				    		psm = psmmap.get(psmkey);
				    		psm.addSearchHit(hit);
				    		psm.addExperimentIDs(experimentIDs);
				    	}
				    	else {
					    	// get spectrum title
							Spectrum spectrum = Spectrum.findFromSpectrumID(rs.getLong("searchspectrum.fk_spectrumid"), conn);
							// getting the spectitlekey and check if the same spectrum is already there with different spectrumid 
							String[] spectitle1 = spectrum.getTitle().split("(File)|(Spectrum)|(scans)");
							String spectitlekey = spectitle1[1] + spectitle1[2];
				    		if (mascotspectrummap.containsKey(spectitlekey)) {			    			
					    		psm = mascotspectrummap.get(spectitlekey);
					    		psm.addSearchHit(hit);
					    		psm.addExperimentIDs(experimentIDs);			    			
				    		}
				    		else {
				    			psm = new PeptideSpectrumMatch(rs.getLong("searchspectrum.searchspectrumid"), hit);
				    			psm.setTitle(spectrum.getTitle());
				    			psmmap.put(psmkey, psm);
				    		}
				    	}				
					    // either add peptide data to existing peptide or create new one
					    if (pepmap.containsKey(rs.getString("peptide.sequence"))) {
					    	peptideHit = pepmap.get(rs.getString("peptide.sequence"));
					    	peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
					    	peptideHit.addExperimentIDs(experimentIDs);
					    }
					    else {
							peptideHit = new PeptideHit(rs.getString("peptide.sequence"), psm);
					    	peptideHit.addSpectrumMatch(rs.getString("peptide.sequence"), psm);
							pepmap.put(rs.getString("peptide.sequence"), peptideHit);
					    }
					    // either add protein data to existing protein or create new one
					    if (protmap.containsKey(rs.getObject("protein.accession"))) {
					        ProteinHit prothit = protmap.get(rs.getObject("protein.accession"));
					        prothit.addPeptideHit(peptideHit);
					    }
					    else {
							// retrieve UniProt meta-data
							Uniprotentry uniprotEntryAccessor = Uniprotentry.findFromProteinID(rs.getLong("protein.proteinid"), conn);
							ReducedUniProtEntry uniprotEntry = null;
							TaxonomyNode taxonomyNode = null;
							// if meta-data exists...
							if (uniprotEntryAccessor != null) {
								// ... wrap it in a UniProt entry container class
								long taxID = uniprotEntryAccessor.getTaxid();
								uniprotEntry = new ReducedUniProtEntry(taxID,
										uniprotEntryAccessor.getKeywords(),
										uniprotEntryAccessor.getEcnumber(),
										uniprotEntryAccessor.getKonumber(),
										uniprotEntryAccessor.getUniref100(),
										uniprotEntryAccessor.getUniref90(),
										uniprotEntryAccessor.getUniref50());
								
								// retrieve taxonomy branch
								taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);			
							} else {
								// create dummy UniProt entry
								uniprotEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
								
								// mark taxonomy as 'unclassified'
								if (unclassifiedNode == null) {
									TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
									unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
								}
								taxonomyNode = unclassifiedNode;
							}						
							// create protein-hit
					    	ProteinHit prothit = new ProteinHit(rs.getString("protein.accession"), 
					    			   			rs.getString("protein.description"), 
					    			   			rs.getString("protein.sequence"), peptideHit, uniprotEntry, 
					    			   			taxonomyNode, rs.getLong("searchspectrum.fk_experimentid"));
					    	protmap.put(rs.getString("protein.accession"), prothit);
					    	prothit.addPeptideHit(peptideHit);
							// wrap new protein in meta-protein
							MetaProteinHit mph = new MetaProteinHit(
									"Meta-Protein " + prothit.getAccession(), prothit);
							prothit.setMetaProteinHit(mph);
							// and add to database
							searchResult.addMetaProtein(mph);
							}
					    }	
				    ps.close();
				    rs.close();
				}
			    // end of mascot block
			    // close Resultset and finish data acquisition

				// determine total spectral count
				int spectralCount = 0;
				for (Long expID : experimentList) {
					spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(expID, conn);
				}
				searchResult.setTotalSpectrumCount(spectralCount);

				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");

				this.searchResult = searchResult;			
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
		return searchResult;
	}

	@Override
	public void setSearchResult(DbSearchResult result) {
		// TODO Auto-generated method stub
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
		
		// wrap the PSM in a new peptide
		PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
		PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);
		
		// retrieve the protein database entry
		long proteinID = hit.getFk_proteinid();
		ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
		
		// retrieve UniProt meta-data
		Uniprotentry uniprotEntryAccessor = Uniprotentry.findFromProteinID(proteinID, conn);
		ReducedUniProtEntry uniprotEntry = null;
		TaxonomyNode taxonomyNode = null;
		// if meta-data exists...
		if (uniprotEntryAccessor != null) {
			// ... wrap it in a UniProt entry container class
			long taxID = uniprotEntryAccessor.getTaxid();
			uniprotEntry = new ReducedUniProtEntry(taxID,
					uniprotEntryAccessor.getKeywords(),
					uniprotEntryAccessor.getEcnumber(),
					uniprotEntryAccessor.getKonumber(),
					uniprotEntryAccessor.getUniref100(),
					uniprotEntryAccessor.getUniref90(),
					uniprotEntryAccessor.getUniref50());
			
			// retrieve taxonomy branch
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);
		} else {
			// create dummy UniProt entry
			uniprotEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
			
			// mark taxonomy as 'unclassified'
			if (unclassifiedNode == null) {
				TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
				unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
			}
			taxonomyNode = unclassifiedNode;
		}
		
		// create a new protein hit and add it to the result
		result.addProtein(new ProteinHit(
				protein.getAccession(), protein.getDescription(), protein.getSequence(),
				peptideHit, uniprotEntry, taxonomyNode, experimentID));
	}
}
