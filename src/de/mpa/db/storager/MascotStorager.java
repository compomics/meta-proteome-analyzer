package de.mpa.db.storager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.compomics.mascotdatfile.util.mascot.Peak;
import com.compomics.mascotdatfile.util.mascot.Query;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.SearchSettings;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.client.settings.MascotParameters.FilteringParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.SpectrumFetchParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.SpectrumTableAccessor;
import de.mpa.db.accessor.UniprotentryAccessor;
import de.mpa.db.accessor.UniprotentryTableAccessor;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.SixtyFourBitStringSupport;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.io.fasta.FastaLoader;

public class MascotStorager extends BasicStorager {

	/**
	 * The client instance.
	 */
	private Client client;

	/**
	 * Maximum ion threshold value.
	 */
	private static final int MAX_ION_THRESHOLD = 1000;

	// MascotDatfile instance now obsolete	
	/**
	 * MascotDatfile instance.
	 */
	//	private MascotDatfile mascotDatFile;

	/**
	 * SearchSettings instance. 
	 */
	private final SearchSettings searchSettings;

	/**
	 * Mascot parameters.
	 */
	private final ParameterMap mascotParams;

	/**
	 * The list of protein accessions for which UniProt entries need to be retrieved.
	 */
	private final Map<String, List<Long>> uniProtCandidates = new TreeMap<String, List<Long>>();

	/**
	 * Loader for FASTA entries from a FASTA DB.
	 */
	private final FastaLoader fastaLoader;

	/**
	 * Constructs a {@link MascotStorager} for parsing and storing of Mascot .dat files to the DB. 
	 * @param conn Connection instance.
	 * @param file File instance. 
	 */
	public MascotStorager(Connection conn, File file, SearchSettings searchSettings, ParameterMap mascotParams,FastaLoader fastaLoader ){
		this.conn = conn;		
		this.file = file;
		this.searchSettings = searchSettings;
		this.mascotParams = mascotParams;
        searchEngineType = SearchEngineType.MASCOT;
		this.fastaLoader = fastaLoader;
	}


	// this method is now obsolete
	@Override
	public void load() {
        this.client = Client.getInstance();
	}

	/**
	 * The store()-method parses files from Mascot in .dat format, updates the protein information from a fasta-Database
	 * and stores the information non-redundantly into the SQL-Database
	 * Mascot Dat-files are divided into sections separated by a boundary-String at the beginning of the file.
	 * The order of sections is: index, header, summary, peptides, decoy_peptides, proteins and queryX (where X is the query number).
	 * 
	 * How this works:
	 * 1. the ScoreThreshold is calculated, which includes a parsing section of its own  
	 * 2. header, summary and peptides are read and relevant data is mapped, the score-threshold is applied here
	 * 3. if the protein section is reached, the provided fasta-file is read to get sequences and descriptions of proteins
	 * 4. proteins with accessions that are already found in the database are marked
	 * 5. the query sections are read completely, mapped and parsed using the MascotDatFile-Query-Class
	 * 6. data for each query is then submitted right away, this saves memory and time
	 * 
	 * @param file File instance from the .dat-file.
	 * @param fastaloader Fastaloader instance for protein description and sequence lookup  
	 * @author K. Schallert
	 */
	@SuppressWarnings("resource")
	@Override
	public void store() throws Exception {

		// boolean whether I have a fasta in background or not
		boolean fetchProteinSequenceFromFasta = ((Boolean) this.mascotParams.get("useFasta").getValue()).booleanValue();

        this.client.firePropertyChange("new message", null, "PARSING MASCOT FILE");
        this.client.firePropertyChange("indeterminate", false, true);
		// generate scoreThreshold from queries --> uses scores of ALL peptides from a query, this may be wrong
		// TODO: check if this kind of FDR-based scorethreshold calculation is correct
        this.client.firePropertyChange("new message", null, "CACLULATING SCORE THRESHOLD");
        this.client.firePropertyChange("indeterminate", true, true);
		double scoreThreshold = getScoreThreshold();
		// instantiate uniprot-webservice
		UniProtUtilities uniprotweb = new UniProtUtilities();
		// Get experiment id.
		long experimentId = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID();
		// This code extracts spectra that are added through other search engines
		// if in different experiment, is empty and just returns empty specTitleMap 
		List<MascotGenericFile> dbSpectra = new SpectrumExtractor(this.conn).getSpectraByExperimentID(
				experimentId, SpectrumFetchParameters.AnnotationType.IGNORE_ANNOTATIONS, false, true);
		// Put titles and spectrum Id's of "mgf" in list --> what does "mgf" mean here, omssa search?
		Map<String,Long> specTitleMap = new TreeMap<String,Long>();
		for (int i = 0; i < dbSpectra.size(); i++) {
			specTitleMap.put(dbSpectra.get(i).getTitle(), dbSpectra.get(i).getSpectrumID());
		}		
		// disable mysql autocommit to speed up batch INSERTs
        this.conn.setAutoCommit(false);
		
		// initialize stuff
		int query_number = 0;
		Double precursor_mass;
		Double precursor_intensity;
		Double precursor_mz;
		String precursor_charge;
		boolean did_i_do_the_fasta_stuff = false;
		boolean do_redundancy_check = true;
		// maps that stores query information from summary section with query number as key 
		HashMap<Integer, Double> precursor_mass_map = new HashMap<>();
		HashMap<Integer, Double> precursor_intensity_map = new HashMap<>();
		HashMap<Integer, Double> precursor_mz_map = new HashMap<>();
		HashMap<Integer, String> precursor_charge_map = new HashMap<>();
		// hashmap for current query
		HashMap<String, String> currentquerymap = new HashMap<>();		
		// nested hashmaps with peptide info and querynumber and peptidenumber-keys directly from dat-file
		//HashMap<Integer, HashMap<Integer, List<Object>>> query_peptide_map = new HashMap<>();
		// query to peptide hashmap
		HashMap<Integer, HashMap<Integer, MascotStorager.MascotPeptideHit>> query_peptide_map = new HashMap<>();
		// protein map with accession as key
		HashMap<String, MascotProteinHit> protein_map = new HashMap<>();
		// we can't store all spectra in memory, instead we have to do this while parsing
		try {
			if (!(this.file.exists())) {
				throw new IllegalArgumentException("raw Mascot datfile from " + this.file + " does not exist.");
			}
			// load the dat file
			BufferedReader datreader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
			// start parsing
			// Parse!
			String line = null;
			if (datreader != null) {
				// First line is to be ignored.
				line = datreader.readLine();
				// Find the boundary.
				line = datreader.readLine();
				while (line != null && line.indexOf("boundary") < 0) {
					line = datreader.readLine();
				}
				// If the line is 'null' here, we read the entire datfile without encountering a
				// boundary.
				if (line == null) {
					throw new IllegalArgumentException("Did not find 'boundary' definition in the datfile!");
				}
				// boundary is a hash that denotes a new section in the dat file and should be given at the beginning of the file
				String boundary = this.getBoundary(line);
				String currentsection =null;
				// skip until we reach first boundary
				while (!(datreader.readLine().contains(boundary))) {}
				// find first section name
				currentsection = getSectionName(datreader.readLine());
				@SuppressWarnings("unused")
				int testing_count = 0;
				// Cycle the stream.
				while ((line = datreader.readLine()) != null) {
					testing_count++;
					// check for boundary
					if (line.contains(boundary)) {
						// check if last line
						if (line.endsWith(boundary + "--")) {
							break;
						}
						// if we arent at end of file the next line should tell us the name of the next section
						currentsection = getSectionName(datreader.readLine());
						// at the start of a new section we must initialize some stuff
						if (currentsection.contains("query")) {
							query_number = Integer.parseInt(currentsection.substring(5));
							currentquerymap.clear();
							// progress report here
							//if ((query_number % 1000) == 0) {System.out.println("New query: "+query_number);}
							client.firePropertyChange("progressmade", true, false);
						}
						// after we get the section name or break we are good
					} else {
						// check which section we are in and do stuff
						// header section contains number of queries
						if ("header".equalsIgnoreCase(currentsection)) {
							if (line.contains("queries")) {
								String[] num_query_split = line.split("=");
								int num_queries = Integer.parseInt(num_query_split[1]);
								// progress report here
								client.firePropertyChange("resetall", 0L, num_queries);
								client.firePropertyChange("resetcur", 0L, num_queries);
							}
						}
						// summary section contains precursor masses
						// reusing the querynumber variable here for different purpose
						if ("summary".equalsIgnoreCase(currentsection)) {
							if (line.contains("qmass")) {
								String[] qmass_split = line.split("=");
								query_number = Integer.parseInt(qmass_split[0].substring(5));
								precursor_mass = Double.parseDouble(qmass_split[1]);
								precursor_mass_map.put(query_number, precursor_mass);
							}
							if (line.contains("qintensity")) {
								String[] qintensity_split = line.split("=");
								query_number = Integer.parseInt(qintensity_split[0].substring(10));
								precursor_intensity = Double.parseDouble(qintensity_split[1]);
								precursor_intensity_map.put(query_number, precursor_intensity);
							}
							if (line.contains("qexp")) {
								String[] qintensity_split = line.split("=");
								query_number = Integer.parseInt(qintensity_split[0].substring(4));
								String[] mzandcharge_split = qintensity_split[1].split(",");
								precursor_mz = Double.parseDouble(mzandcharge_split[0]);
								precursor_charge = mzandcharge_split[1];
								precursor_mz_map.put(query_number, precursor_mz);
								precursor_charge_map.put(query_number, precursor_charge);
							}
						}
						// peptide section contains a lot of useful stuff
						if ("peptides".equalsIgnoreCase(currentsection)) {
							if (line.startsWith("q")) {
								String[] general_peptideentry_split = line.split("=");
								// we only need the main entries (not "terms" and "subst")
								// and we need to check if the query is empty
								String[] entryname_split = general_peptideentry_split[0].split("_");
								// we need to divide proteins and peptidedata
								String[] alldata_split =  general_peptideentry_split[1].split(";");
								if ((entryname_split.length == 2) && (alldata_split.length > 1)) {
									// now we parse the querynumber and peptidenumber
									int query_num = Integer.parseInt(entryname_split[0].substring(1));
									int peptide_num = Integer.parseInt(entryname_split[1].substring(1));
									// now we parse the peptide information we need
									String[] pepdata_split =  alldata_split[0].split(",");
									String[] protdata_split =  alldata_split[1].split(",");
									// the score is parsed first and tested against the threshold
									Double pepscore = Double.parseDouble(pepdata_split[7]);
									if (pepscore >= scoreThreshold) {
										// the sequence is for storing the peptide itself
										String pepsequence = pepdata_split[4];
										// delta mass and evalue are needed for
										Double deltamass = Double.parseDouble(pepdata_split[2]);
										// TODO: get proper evalues
										Double evalue = 0.0;
										// the proteins are needed to to create proteins later
										// parsing the proteins is kind of painful
										List<String> proteinlist = new ArrayList<String>();
										for (String prot_substring : protdata_split) {
											// TODO: handle malformed accessions
											String[] accession_split = prot_substring.split("\"");
											// we might need to go one level deeper before adding the accession (we do here)
											String[] accession = accession_split[1].split("[|]");
											if (accession.length>1) {
												// TODO: does it work? (fix from 0 -> 1)
												proteinlist.add(accession[0]);
											} else {
												proteinlist.add(accession_split[1]);
											}

										}
										// lastly we put it all into the map
										MascotStorager.MascotPeptideHit pep_hit = new MascotStorager.MascotPeptideHit(pepsequence, pepscore, proteinlist, deltamass, evalue);
										// does query already contain data? If not create new peptidemap
										if (query_peptide_map.containsKey(query_num)) {
											query_peptide_map.get(query_num).put(peptide_num, pep_hit);
										} else {
											HashMap<Integer, MascotStorager.MascotPeptideHit> peptidemap = new HashMap<>();
											peptidemap.put(peptide_num, pep_hit);
											query_peptide_map.put(query_num, peptidemap);
										}
										// at this point i can add protein data
										for (String accession : proteinlist) {
											if (protein_map.containsKey(accession)) {
												// TODO: this if clause is obsolete
												// add new peptide to protein
												protein_map.get(accession).addPeptideHit(pep_hit);
											} else {
												// add new protein to map
												MascotProteinHit protein_hit = new MascotProteinHit(accession);
												// and then add the peptide
												//TODO KAY this order was changed might be the reason of the error
//												protein_hit.addPeptide(pep_hit);
												protein_map.put(accession, protein_hit);

											}
										}
									}
								}
							}
						}
						if ("proteins".equalsIgnoreCase(currentsection)) {
							// dont get description from protein section, just get it from fasta
							// if entering we should do the full redundancy check and retrieve data from fasta once

							// Get proteinmap containing the descriptions and sequence in case of fasta.
							if (!fetchProteinSequenceFromFasta) {
								if (line !=null && line.length()>0) {
									String[] split = line.split("[=]",2);
									if (!(split[0].contains("_tax"))) {
										String accession = split[0].split("[|]")[1].replace("\"","");
										String description = split[1].split("[,]",2)[1];
										// If map does not contain this entry, it was filtered out.
										if (protein_map.containsKey(accession)) {
											protein_map.get(accession).setDescription(description);
										}
									}
								}
							} else {
								if (did_i_do_the_fasta_stuff == false) {
									protein_map = this.fastaLoader.updateProteinMapfromFasta(protein_map);
									// this ensures this code runs just once
									did_i_do_the_fasta_stuff = true;
									// call the fastaloader and get descriptions and sequences
									client.firePropertyChange("new message", null, "RETRIEVING DATA FROM FASTA");
									client.firePropertyChange("indeterminate", true, true);
								}
							}
						}
						// if we reach queries we can do the main thing
						// here we also decide if this query is above the threshold
						if ((currentsection.contains("query")) && (query_peptide_map.containsKey(query_number))) {
							if (do_redundancy_check) {
								do_redundancy_check = false;
								// after updating description and sequence, we also check the database for redundancy
								// first get accessions and proteinids from the protein table
								client.firePropertyChange("new message", null, "QUERYING DATABASE FOR PROTEIN ENTRIES");
								client.firePropertyChange("indeterminate", true, true);
								boolean rows_left = true;
								long limit = 1000000L; // 1 Million
								long offset = 0L;
								// only retrieve 1 Mio proteins at a time
								while (rows_left) {
									// get a limited amount of proteins from the protein table to make sure memory is not a problem
									PreparedStatement prs = conn.prepareStatement("SELECT protein.proteinid, protein.accession, protein.fk_uniprotentryid "
											+ "FROM protein LIMIT ? OFFSET ?");
									prs.setFetchSize(512);
									prs.setLong(1, limit);
									prs.setLong(2, offset);
									ResultSet aRS = prs.executeQuery();
									// look through them
									int test_count_1 = 0;
									while (aRS.next()) {
										test_count_1++;
//										if ((test_count_1 % 100000) == 0) {System.out.println("DB-lookup: "+test_count_1);}
										// and determine if an accession is already in there
										String accession = (String) aRS.getObject("accession");
										if (protein_map.containsKey(accession)) {
											// Check whether this protein is redundant in the DAT file
											if (protein_map.get(accession).was_this_protein_submitted()) {
												System.out.println("Duplicate protein entry: "+accession);
											} else {
												// and store the proteinid
												long proteinID = aRS.getLong("proteinid");
												protein_map.get(accession).setProteinID(proteinID);
												protein_map.get(accession).set_this_protein_is_in_DB();
											}
										}
									}
									if (test_count_1 == 0) {
										rows_left = false;
									} else {
										offset += 1000000L;
									}
									prs.close();
									aRS.close();
								}
								client.firePropertyChange("new message", null, "PARSING MASCOT FILE");
								client.firePropertyChange("indeterminate", true, false);
							}
							// get the query number and check if it got any peptides
							// we need querynumber, charge(String), pre-MZ(Double), preINT(Double), peaklist(Array), maxINT(Double), Title(String)
							// fill in the currentquerydata
							String[] querysplit = line.split("=");
							if (querysplit.length == 2) {
								currentquerymap.put(querysplit[0], querysplit[1]);
								// if we reached ions then we can process this thing
								if (querysplit[0].equalsIgnoreCase("ions1")) {
									// intensity values might be missing, check and set to 1 if missing
									double current_intensity = 1;
									if (precursor_intensity_map.containsKey(query_number)) {
										current_intensity = precursor_intensity_map.get(query_number);
									}
									Query current_query = new Query(currentquerymap, precursor_mz_map.get(query_number),
											precursor_charge_map.get(query_number), precursor_mass_map.get(query_number),
											current_intensity, query_number);
									// here we finally have the query and can start storing stuff
									// submit spectrum and searchspectrum, if not already stored
									Long spectrumId = specTitleMap.get(current_query.getTitle());
									Long searchspectrumID = null;
									// Not already stored for this file
									if (spectrumId != null) {
										// If already stored get searchspectra ID
										searchspectrumID = Searchspectrum.findFromSpectrumIDAndExperimentID(spectrumId, experimentId, conn).getSearchspectrumid();
									} else {
										// Not stored for this file, then get title
										String spectrum_title = current_query.getTitle();
										// title formatting
										spectrum_title = spectrum_title.split("( \\(id)")[0];
										
										// TODO: new method for spectrum storage
										
										// Redundancy check if already in sql database (e.g. from other search engines)
//										Spectrum query =  Spectrum.findFromTitle(spectrum_title, conn);
							            long titlehash = SpectrumTableAccessor.createTitleHash(spectrum_title, current_query.getPrecursorMZ(), current_query.getPrecursorIntensity());
							            Spectrum query =  Spectrum.findFromTitleQuicker(titlehash, this.conn);
										if (query == null) {
											// update the spectrum title to the shortened one
//											current_query.setTitle(spectrum_title);
											// not stored spectra will be stored
											spectrumId = this.storeSpectrum(current_query, titlehash);
										} else {
											// get id if already stored
											spectrumId = query.getSpectrumid();
										}
										HashMap<Object, Object> data = new HashMap<Object, Object>(5);
										data.put(Searchspectrum.FK_SPECTRUMID, spectrumId);
										data.put(Searchspectrum.FK_EXPERIMENTID, searchSettings.getExpID());
										Searchspectrum searchSpectrum = new Searchspectrum(data);
										searchSpectrum.persist(conn);
										searchspectrumID = (Long) searchSpectrum.getGeneratedKeys()[0];
										// Add new spectrum to map with title and spectrum IDs
										specTitleMap.put(current_query.getTitle(),spectrumId);
									}
									// submit peptide and spec2pep reference
									HashMap<Integer, MascotStorager.MascotPeptideHit> peptidemap = query_peptide_map.get(query_number);
									for (int peptide_number : peptidemap.keySet()) {
										// Fill the peptide table and get peptideID
										MascotStorager.MascotPeptideHit current_pephit = peptidemap.get(peptide_number);
										String pep_sequence = current_pephit.getSequence();
										long peptideID = this.storePeptide(pep_sequence);
										// Store peptide-spectrum association
										this.storeSpec2Pep(searchspectrumID, peptideID);
										// missing protein, pep2prot and mascothit (and uniprotentry/taxonomy which should work afterwards?)
										// this code submits a protein entry and the pep2prot ref
										// we need accession protein description and full sequence and the proteinID
										Long proteinID = null;
										// get all accessions from current peptide
										for (String prot_acc : peptidemap.get(peptide_number).getProteinHitlist()) {
											// if protein was already submitted, just update pep2prot ref
											if (protein_map.get(prot_acc).was_this_protein_submitted()) {
												proteinID = protein_map.get(prot_acc).getProteinID();
												// this just updates the pep2prot to a given protein
												Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
											} else {
												String accession = protein_map.get(prot_acc).getAccession();

												// Get the protein description from fasta or from the dat file
												String description = protein_map.get(prot_acc).getDescription();
												if (!(description == null || description.length()<1)) {
													String sequence = protein_map.get(prot_acc).getSequence();
													// save the protein
													ProteinAccessor prot_accessor = ProteinAccessor.addProteinToDatabase(accession, description, pep_sequence, protein_map.get(prot_acc).getDatabaseType(), -1L, conn);
													proteinID = prot_accessor.getProteinid();
													// save the peptide to protein link
													Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
													// fetch uniprot data (if applicable) and store the uniprot entry
													// Length of uniProt accessions is 6 for swissprot and 6 or 12 for trembl
													if (accession.length() == 6 || accession.length() == 10 ) {
														ArrayList<String> this_accession_as_list = new ArrayList<String>();
														this_accession_as_list.add(accession);
														TreeMap<String, UniProtEntryMPA> uniprotentry_as_map = uniprotweb.fetchUniProtEntriesByAccessions(this_accession_as_list, true);
														for (UniProtEntryMPA mpa_entry : uniprotentry_as_map.values()) {
															TreeMap<Long, UniProtEntryMPA> prot_id_2_uniprotentry = new TreeMap<Long, UniProtEntryMPA>();
															prot_id_2_uniprotentry.put(proteinID, mpa_entry);
															TreeMap<Long, Long> proteinid2uniprotid = UniprotentryAccessor.addMultipleUniProtEntriesToDatabase(prot_id_2_uniprotentry, conn);
															prot_accessor.setFK_uniProtID(proteinid2uniprotid.get(proteinID));
														}
													}
													// this adds a new protein
//													ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, sequence, conn);
//													proteinID = (Long) protAccessor.getGeneratedKeys()[0];
													// update this protein so its not submitted twice
													protein_map.get(prot_acc).set_this_protein_is_in_DB();
													protein_map.get(prot_acc).setProteinID(proteinID);
													// this is a new protein so we mark for uniprot lookup
													if (uniProtCandidates.containsKey(accession)) {
														uniProtCandidates.get(accession).add(proteinID);
													} else {
														List<Long> prot_id_list = new ArrayList<Long>();
														prot_id_list.add(proteinID);
														uniProtCandidates.put(accession, prot_id_list);
													}
												} else {
													System.out.println("Protein-entries not found in 'proteins' section, but found in the 'queryPeptideMap'");
													System.out.println("Protein Accession: " + accession );
												}

											}
										}
										if (proteinID !=null) {
											// finally we submit the mascothit
											this.storeMascotHit(searchspectrumID, peptideID, proteinID, current_query, current_pephit);

										} else {
											System.out.println("Protein hit for " + pep_sequence + " not stored");
										}
									}
									// finally commit data, is done once for every query
									conn.commit();
								}
							}
						}
					}
				}
			}
			datreader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// final commit
		conn.commit();
		client.firePropertyChange("new message", null, "PROCESSING MASCOT QUERIES FINISHED");
	}

	/**
	 * Retrieves the score threshold based on local FDR or absolute limit.
	 * In case of local FDR, scores for peptides and decoypeptides are retrieved from the dat-file
	 *
	 * @return {@link Double} Score threshold
	 * @throws Exception
	 * @author K. Schallert
	 */
	private double getScoreThreshold() throws Exception {
		boolean filterType = ((Boolean) mascotParams.get("filterType").getValue()).booleanValue();
		if (filterType == FilteringParameters.ION_SCORE) {
			return (Integer) mascotParams.get("ionScore").getValue();
		} else {
			// we need to retrieve queryScores and queryDecoyScores
			// Init score lists of identified target and decoy queries
			List<Double> queryScores = new ArrayList<Double>();
			List<Double> queryDecoyScores = new ArrayList<Double>();
			// parse through file and retrieve queries one by one
			try {
				if (!(this.file.exists())) {
					throw new IllegalArgumentException("raw Mascot datfile from " + this.file + " does not exist.");
				}
				// load the dat file
				@SuppressWarnings("resource")
				BufferedReader datreader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
				// start parsing
				// Parse!
				String line = null;
				if (datreader != null) {
					// First line is to be ignored.
					line = datreader.readLine();
					// Find the boundary.
					line = datreader.readLine();
					while (line != null && line.indexOf("boundary") < 0) {
						line = datreader.readLine();
					}
					// If the line is 'null' here, we read the entire datfile without encountering a
					// boundary.
					if (line == null) {
						throw new IllegalArgumentException("Did not find 'boundary' definition in the datfile!");
					}
					// boundary is a hash that denotes a new section in the dat file and should be given at the beginning of the file
					String boundary = this.getBoundary(line);
					String currentsection =null;
					// read until we reach first boundary
					while (!(datreader.readLine().contains(boundary))) {}
					// find first section name
					currentsection = getSectionName(datreader.readLine());
					// Cycle the stream.
					while ((line = datreader.readLine()) != null) {
						// check for boundary
						if (line.contains(boundary)) {
							// check if last line
							if (line.endsWith(boundary + "--")) {
								break;
							}
							// if we arent at end of file the next line should tell us the name of the next section
							currentsection = getSectionName(datreader.readLine());
							// after we get the section name or break we are good
						} else {
							// if we reach queries we can abort
							if (currentsection.contains("query")) {
								//
								break;
							}
							// here we read all the data-containing lines
							// we need peptide scores and decoy peptide scores, only look in these section
							if ("peptides".equalsIgnoreCase(currentsection)) {
								// only look at lines that start with "q" (for query, which are all but empty lines)
								if (line.startsWith("q")) {
									String[] general_peptideentry_split = line.split("=");
									// correct split?
									if (general_peptideentry_split.length == 2) {
										// check wich section we have, we want the normal peptidedata which should split into 2 tokens
										// q170_p9 --> peptidedata, q170_p9_terms --> other stuff
										String[] header_split = general_peptideentry_split[0].split("_");
										if (header_split.length == 2) {
											String[] pepdata_split = general_peptideentry_split[1].split(",");
											// check if this is a proper peptide entry, empty entries  equal -1
											if (pepdata_split.length >= 6) {
												// 	finally retrieve score
												queryScores.add(Double.parseDouble(pepdata_split[7]));
											} else if (!(Integer.parseInt(general_peptideentry_split[1]) == -1)) {
												throw new IllegalArgumentException("Parsing error: improper peptide entry");
											}
										}
									} else {
										throw new IllegalArgumentException("Parsing error: at getionthreshold()/split(peptideentry)");
									}
								}
							}
							if ("decoy_peptides".equalsIgnoreCase(currentsection)) {
								// only look at lines that start with "q" (for query, which are all but empty lines)
								if (line.startsWith("q")) {
									String[] general_peptideentry_split = line.split("=");
									// correct split?
									if (general_peptideentry_split.length == 2) {
										// check wich section we have, we want the normal peptidedata which should split into 2 tokens
										// q170_p9 --> peptidedata, q170_p9_terms --> other stuff
										String[] header_split = general_peptideentry_split[0].split("_");
										if (header_split.length == 2) {
											String[] pepdata_split = general_peptideentry_split[1].split(",");
											// check if this is a proper peptide entry, empty entries  equal -1
											if (pepdata_split.length >= 6) {
												// 	finally retrieve score
												queryDecoyScores.add(Double.parseDouble(pepdata_split[7]));
											} else if (!(Integer.parseInt(general_peptideentry_split[1]) == -1)) {
												throw new IllegalArgumentException("Parsing error: improper peptide entry");
											}
										}
									} else {
										throw new IllegalArgumentException("Parsing error: at getionthreshold()/split(peptideentry)");
									}
								}
							}
						}
					}
				}
				datreader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// this code remained unchanged, TODO: using all peptides from dat-file, is this correct?
			Collections.sort(queryScores);
			Collections.sort(queryDecoyScores);
			// Extract maximum false discovery rate threshold from parameters
			double fdrThreshold = (Double) mascotParams.get("fdrScore").getValue();
			// Calculate FDR by increasing ion score until threshold is reached
			for (int ionThreshold = 0; ionThreshold <= MAX_ION_THRESHOLD + 1; ionThreshold++) {
				// Remove query entries below ion score threshold
				Iterator<Double> queryScoresIt = queryScores.iterator();
				while (queryScoresIt.hasNext()) {
					if (queryScoresIt.next() < ionThreshold) {
						queryScoresIt.remove();
					} else {
						break;
					}
				}
				// Remove decoy query entries below ion score threshold
				Iterator<Double> queryDecoyScoresIt = queryDecoyScores.iterator();
				while (queryDecoyScoresIt.hasNext()) {
					if (queryDecoyScoresIt.next() < ionThreshold) {
						queryDecoyScoresIt.remove();
					} else {
						break;
					}
				}

				// See definition of Mascot FDR http://www.matrixscience.com/help/decoy_help.html
				double fdr = 1.0 * queryDecoyScores.size() / (queryScores.size() + queryDecoyScores.size()) ;
				if (fdr <= fdrThreshold ) {
					return ionThreshold;
				}
			}
			JOptionPane.showMessageDialog(ClientFrame.getInstance(),
					"Unable to calculate FDR (ion score threshold of " + MAX_ION_THRESHOLD + " reached).",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return MAX_ION_THRESHOLD;
		}
	}


	/**
	 * This method puts a spectrum from a Mascot .dat file into the database,
	 * @param  query from MascotDatFileParser
	 * @param mDat The MascotDatFileParser
	 * @return spectrumID The ID of the spectra in the database.
	 * @throws SQLException
	 */
	private Long storeSpectrum(Query query, long titlehash) throws SQLException {

		Long spectrumid = null;
		HashMap<Object, Object> data = new HashMap<Object, Object>(12);
		data.put(Spectrum.TITLE, query.getTitle().trim());
		data.put(Spectrum.TITLEHASH, titlehash);
		data.put(Spectrum.PRECURSOR_MZ, query.getPrecursorMZ());
		data.put(Spectrum.PRECURSOR_INT, query.getPrecursorIntensity());
		String chargeString = query.getChargeString();
		chargeString = chargeString.replaceAll("[^\\d]", "");
		data.put(Spectrum.PRECURSOR_CHARGE, Long.valueOf(chargeString));
		Peak[] peakList = query.getPeakList();
		Double[] mzArray = new Double[peakList.length];
		Double[] intArray = new Double[peakList.length];
		Double[] chargeArray = new Double[peakList.length];
		double totalInt = 0.0;
		if (peakList != null && peakList.length > 0) {
			for (int j = 0; j < peakList.length; j++) {
				Peak peak = peakList[j];
				mzArray[j] = peak.getMZ();
				intArray[j] = peak.getIntensity();
				chargeArray[j] = 0.0;
				totalInt += peak.getIntensity();
			}
		}
		data.put(Spectrum.MZARRAY,SixtyFourBitStringSupport.encodeDoublesToBase64String(mzArray));
		data.put(Spectrum.INTARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(intArray));
		data.put(Spectrum.CHARGEARRAY,SixtyFourBitStringSupport.encodeDoublesToBase64String(chargeArray) );
		data.put(Spectrum.TOTAL_INT, totalInt); // Add
		data.put(Spectrum.MAXIMUM_INT, query.getMaxIntensity());
		// Save spectrum in database
		Spectrum spec = new Spectrum(data);
		spec.persist(conn);
		spectrumid = (Long) spec.getGeneratedKeys()[0];
		return spectrumid;
	}

//	/**
//	 * This method puts the proteins and pep2proteins entries in the database
//	 * @param peptideID. The ID of the peptide in the database
//	 * @param datProtHit. A proteinHit from the MascotDatFile parser.
//	 * @param proteinMap. The proteinMap from MascotDatFile parser, containing the link between accession and description
//	 * @return proteinID. The proteinID in the database.
//	 * @throws SQLException
//	 */
//	// this method is now obsolete
//	@SuppressWarnings("unused")
//	private StoredProtein storeProtein(long peptideID, ProteinHit proteinHit, ProteinMap proteinMap) throws IOException, SQLException {
//		// save information of the protein storing
//		StoredProtein storedProt;
//		String protAccession = proteinHit.getAccession();
//		// protein hit accession is typically not a proper accession (e.g. 'sp|P86909|SCP_CHIOP'),
//		// therefore convert it to a FASTA header and parse accession from it
//		String composedHeader = "";
//		Header header;
//		String accession = null;	// true accession
//		String description = null;
//		// CASE UNIPROT
//		if (protAccession.startsWith("sp") || protAccession.startsWith("tr")) {
//			composedHeader = ">" + protAccession + " " + proteinMap.getProteinDescription(protAccession);
//			header = Header.parseFromFASTA(composedHeader);
//			accession = header.getAccession();
//			description = header.getDescription();
//		}
//		// CASE NCBI--- try to get mapping to UNIPROT
//		else if(protAccession.startsWith("gi")) {
//			composedHeader = ">" + protAccession + "|" + proteinMap.getProteinDescription(protAccession);
//			header = Header.parseFromFASTA(composedHeader);
//			protAccession = header.getAccession();
//			description = header.getDescription();
//			//			Map<String, String> gi2up = UniProtGiMapper.retrieveGiToUniProtMapping(protAccession);
//			//			accession = gi2up.get(protAccession);
//			if (accession == null) {
//				// revert to GI number
//				accession = protAccession;
//			}
//		}
//		else if(protAccession.startsWith("generic")) {
//			composedHeader = ">" + protAccession + " " + proteinMap.getProteinDescription(protAccession);
//			header = Header.parseFromFASTA(composedHeader);
//			accession = header.getAccession();
//			description = header.getDescription();
//		}
//
//		// If not UNIPROT or NCBI Header.parseFromFASTA(composedHeader) may fail.... hence set new accessions.
//		if ((accession == null) || description == null) {
//			ProteinMap proteinMap2 = proteinMap;
//			String[] split = protAccession.split("[|]");
//			accession = split[1].trim();
//			// changed from 0 to 1
//			description = proteinMap.getProteinDescription(protAccession);
//			//TODO MAYBE here an Mistake with other accession rules
//		}
//		// Check whether protein is already in database
//		// this old implementation created a hashmap which led to memory issues
//		//HashMap<String, Long> proteinIdMap = MapContainer.getProteinIdMap();
//		//Long proteinID = proteinIdMap.get(accession);
//		// this new implementation just gets the proteinID if the accession is already in the database
//		PreparedStatement prs = conn.prepareStatement("select * from protein " +
//				"where protein.accession = ?");
//		prs.setString(1, accession);
//		ResultSet rs = prs.executeQuery();
//		Long proteinID = null;
//		while(rs.next()) {
//			ProteinTableAccessor currententry = new ProteinTableAccessor(rs);
//			if (currententry.getAccession() == accession) {
//				proteinID = currententry.getProteinid();
//			}
//		}
//		rs.close();
//		prs.close();
//		// Protein is not in database, create new one
//		if (proteinID == null) {
//			// Try to fetch sequence
//			String sequence = ""; // Sequence is normally empty because the dat file do not contain a sequence
//			if (fastaLoader != null) {
//				@SuppressWarnings("static-access")
//				Protein fastaProt = fastaLoader.getProteinFromFasta(accession);
//				// this does nothing here, so removed
//				//TObjectLongMap<String> indexMap = fastaLoader.getInstance().getIndexMap();
//				sequence = fastaProt.getSequence().getSequence();
//			}
//
//			ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, sequence, conn);
//			proteinID = (Long) protAccessor.getGeneratedKeys()[0];
//			// Mark protein for UniProt lookup
//			uniProtCandidates.add(accession);
//		} else {
//			// Protein is already stored in database, re-use existing ID
//			Uniprotentry upe = Uniprotentry.findFromProteinID(proteinID, conn);
//			// If protein is missing a sequence or a UniProt entry mark it for UniProt lookup later on
//			if (upe == null) {
//				uniProtCandidates.add(accession);
//			}
//		}
//		storedProt = new StoredProtein(proteinHit.getAccession(), proteinID);
//		return storedProt;
//	}

	/**
	 * This method puts a MascotHit entry to the database.
	 * @param searchspectrumID. The spectrumID in the database.
	 * @param peptideID. The peptideID in the database.
	 * @param proteinID. The proteinID in the database.
	 * @param query. The MascotDatFile parser query.
	 * @param datPeptideHit. The MascotDatFile peptide. ---> was changed to local class MascotPeptideHit
	 * @return mascothitID. The ID of the MascotHit in the database.
	 * @throws SQLException
	 */
	private long storeMascotHit(long searchspectrumID, long peptideID, long proteinID, Query query, MascotStorager.MascotPeptideHit peptideHit) throws SQLException {
		long mascotHitID = 0;
		HashMap<Object, Object> data = new HashMap<Object, Object>(10);
		data.put(Mascothit.FK_SEARCHSPECTRUMID, searchspectrumID);
		data.put(Mascothit.FK_PEPTIDEID, peptideID);
		data.put(Mascothit.FK_PROTEINID, proteinID);
		String chargeString = query.getChargeString();
		chargeString = chargeString.replaceAll("[^\\d]", "");
		data.put(Mascothit.CHARGE, Long.valueOf(chargeString));
		data.put(Mascothit.IONSCORE, peptideHit.getscore());
		data.put(Mascothit.EVALUE, peptideHit.getEvalue());
		data.put(Mascothit.DELTA, peptideHit.getdeltamass());
		// Save spectrum in database
		Mascothit mascotHit	 = new Mascothit(data);
		mascotHit.persist(this.conn);
		mascotHitID = (Long) mascotHit.getGeneratedKeys()[0];
		return mascotHitID;
	}

	/**
	 * Helper class to store a protein hit. Shows whether a protein from the mascot dat file was already
	 * in the sql database or was already stored to the sql database
	 *
	 * @author Kay Schallert
	 */	
	public class MascotProteinHit extends ProteinHit {

		/**
		 * Default serial number
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The boolean added through this class, denotes if protein was submitted to database.
		 */
		private boolean wassubmitted;
		
		/**
		 * The boolean added through this class, denotes if protein was submitted to database.
		 * The proteinID needs to be stored for proteins which were found in the SQL-database.
		 */
		private long proteinID;
		
		/**
		 * Set proteinID (from SQL-database)
		 * 
		 * @return proteinID
		 */
		public long getProteinID() {
			return this.proteinID;
		}

		/**
		 * Return proteinID (from SQL-database)
		 * 
		 * @param proteinID
		 */
		public void setProteinID(long proteinID) {
			this.proteinID = proteinID;
		}

		/**
		 * Constructor, pass on the accession to parent class. 
		 * 
		 * @param accession
		 */
		public MascotProteinHit(String accession) {
			super(accession);
		}
		
		/**
		 * Set the protein to submitted 
		 */
		public void set_this_protein_is_in_DB() {
            wassubmitted = true;
		}
		
		/**
		 * Return the boolean value for database-protein submitted or not. 
		 * 
		 * @return wassubmitted
		 */
		public boolean was_this_protein_submitted() {
			return wassubmitted;
		}
	}

	/**
	 * Helper class to store additional peptide information
	 * 
	 * @author Kay Schallert
	 */	
	public class MascotPeptideHit extends PeptideHit {
		
		/**
		 * Because we want to make comparisons with this type
		 */
		private static final long serialVersionUID = 1L;

		/**
		 *  Mascot query peptide score         
		 */
		private final Double peptide_score;
		
		/**
		 *  Deltamass of peptide hit
		 */
		private final double deltamass;
		
		/**
		 *  Evalue of the peptide hit.
		 */
		private final double Evalue;

		/**
		 * Constructor method.        
		 * 
		 * @param pepseq --> peptide sequence
		 * @param pepscore --> peptide score
		 * @param proteins --> List of proteins (accessions) this peptide belongs to
		 * @param dmass --> delta mass
		 * @param eval --> excpectancy value
		 */
		public MascotPeptideHit(String pepseq, Double pepscore, List<String> proteins, Double dmass, Double eval) {
			super(pepseq, 0, 1);
            peptide_score = pepscore;
            deltamass = dmass;
            Evalue = eval;
            setProteinHitlist(proteins);
		}		
		
		/**
		 * Return score
		 * 
		 *  @return peptide_score        
		 */
		public Double getscore() {
			return peptide_score;
		}
		
		/**
		 * Return evalue
		 * 
		 *  @return Evalue        
		 */
		public double getEvalue() {
			return Evalue;
		}
		
		/**
		 * Return deltamass
		 * 
		 *  @return deltamass        
		 */
		public double getdeltamass() {
			return deltamass;
		}		
	}


	/**
	 * Helper class to store information for a stored protein
	 * @author R. Heyer
	 */
	// this class is now obsolete
	private class StoredProtein{
		/* Protein accession*/
		private final String accession;
		/* Protein ID from the database*/
		private final long protID;

		/**
		 * Default Constructor
		 * @param accession
		 * @param protID
		 */
		StoredProtein(String accession, Long protID){
			this.accession = accession;
			this.protID = protID;
		}
		/**
		 * Gets the accession of the stored protein
		 * @return The accession.
		 */
		@SuppressWarnings("unused")
		public String getAccession() {
			return this.accession;
		}
		/**
		 * Gets the proteinID from the stored protein
		 * @return. The protein ID from the database
		 */
		@SuppressWarnings("unused")
		public long getProtID() {
			return this.protID;
		}
	}

	// added from MascotRawParser
	/**
	 * This method parses a section definition line for the name of that section.
	 *
	 * @param sectionDefLine String with the section definition line.
	 * @return String  with the section name.
	 */
	private String getSectionName(String sectionDefLine) {
		return getProp(sectionDefLine, "name");
	}

	/**
	 * This method parses the boundary definition line for the boundary String.
	 *
	 * @param boundaryDefLine String with the boundary definition line.
	 * @return String with the boundary.
	 */
	private String getBoundary(String boundaryDefLine) {
		String lookFor = "boundary";
		String found = getProp(boundaryDefLine, lookFor);
		return found;
	}

	/**
	 * This method finds a property, associated by a name in the following
	 * context: <br />
	 * NAME=VALUE
	 *
	 * @param line     String with the line on which the 'KEY=VALUE' pair is to be found.
	 * @param propName String with the name of the KEY.
	 * @return String  with the VALUE
	 */
	private String getProp(String line, String propName) {
		propName += "=";
		int start = line.indexOf(propName);
		int offset = propName.length();
		String found = line.substring(start + offset).trim();
		// Trim away opening and closing '"'.
		if (found.startsWith("\"")) {
			found = found.substring(1);
		}
		if (found.endsWith("\"")) {
			found = found.substring(0, found.length() - 1);
		}
		return found.trim();
	}    

}

