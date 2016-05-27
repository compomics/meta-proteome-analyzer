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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import com.compomics.mascotdatfile.util.mascot.Peak;
import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.mascotdatfile.util.mascot.ProteinMap;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.SearchSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.settings.MascotParameters.FilteringParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.SpectrumFetchParameters.AnnotationType;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.ProteinTableAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.SixtyFourBitStringSupport;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.util.Formatter;

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
	private SearchSettings searchSettings;
	
	/**
	 * Mascot parameters.
	 */
	private ParameterMap mascotParams;
	
	/**
	 * The list of protein accessions for which UniProt entries need to be retrieved.
	 */
	private Set<String> uniProtCandidates = new HashSet<String>();

	/**
	 * Loader for FASTA entries from a FASTA DB.
	 */
	private FastaLoader fastaLoader;
		
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
		this.searchEngineType = SearchEngineType.MASCOT;
		this.fastaLoader = fastaLoader;
    }
	
	
	// this method is now obsolete
	@Override
	public void load() {
		client = Client.getInstance();
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
		client.firePropertyChange("new message", null, "PARSING MASCOT FILE");
		client.firePropertyChange("indeterminate", false, true); 
		// generate scoreThreshold from queries --> uses scores of ALL peptides from a query, this may be wrong
		// TODO: check if this kind of FDR-based scorethreshold calculation is correct
		client.firePropertyChange("new message", null, "CACLULATING SCORE THRESHOLD");
		client.firePropertyChange("indeterminate", true, true);
		double scoreThreshold = this.getScoreThreshold();	
		// Get experiment id.
		long experimentId = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID();
		// This code extracts spectra that are added through other search engines
		// if in different experiment, is empty and just returns empty specTitleMap 
		List<MascotGenericFile> dbSpectra = new SpectrumExtractor(conn).getSpectraByExperimentID(
				experimentId, AnnotationType.IGNORE_ANNOTATIONS, false, true);		
		// Put titles and spectrum Id's of "mgf" in list --> what does "mgf" mean here, omssa search?
		Map<String,Long> specTitleMap = new TreeMap<String,Long>();
		for (int i = 0; i < dbSpectra.size(); i++) {
			specTitleMap.put(dbSpectra.get(i).getTitle(), dbSpectra.get(i).getSpectrumID());
		}		
		// disable mysql autocommit to speed up batch INSERTs
		conn.setAutoCommit(false);
		// initialize stuff
		int query_number = 0;
		Double precursor_mass;
		Double precursor_intensity;
		Double precursor_mz;
		String precursor_charge;
		boolean did_i_do_the_fasta_stuff = false;
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
		HashMap<Integer, HashMap<Integer, MascotPeptideHit>> query_peptide_map = new HashMap<>();
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
		        	//if ((testing_count % 10000) == 0) {System.out.println("Dat-File Line: "+ testing_count);}		        	
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
		            						proteinlist.add(accession[1]);
		            					}
		            					// lastly we put it all into the map
		            					MascotPeptideHit pep_hit = new MascotPeptideHit(pepsequence, pepscore, proteinlist, deltamass, evalue);
		            					// does query already contain data? If not create new peptidemap
		            					if (query_peptide_map.containsKey(query_num)) {
		            						query_peptide_map.get(query_num).put(peptide_num, pep_hit);
		            					} else {
		            						HashMap<Integer, MascotPeptideHit> peptidemap = new HashMap<>();
		            						peptidemap.put(peptide_num, pep_hit);
		                					query_peptide_map.put(query_num, peptidemap);
		            					}
		            					// at this point i can add protein data
		            					for (String accession : proteinlist) {
		            						if (protein_map.containsKey(accession)) {
		            							// add new peptide to protein
		            							protein_map.get(accession).addPeptide(pep_hit);
		            						} else {
		            							// add new protein to map
		            							MascotProteinHit protein_hit = new MascotProteinHit(accession);
		            							protein_map.put(accession, protein_hit);
		            							// and then add the peptide
		            							protein_hit.addPeptide(pep_hit);
		            						}
		            					}		                					
		            				}
		            			}			                		
		                	}
		        		}	
		        		if ("proteins".equalsIgnoreCase(currentsection)) {
		        			// dont get description from protein section, just get it from fasta
		        			// if entering we should do the full redundancy check and retrieve data from fasta once
		        			if (did_i_do_the_fasta_stuff == false) {
		        				// this ensures this code runs just once
		        				did_i_do_the_fasta_stuff = true;
		        				// call the fastaloader and get descriptions and sequences
		        				client.firePropertyChange("new message", null, "RETRIEVING DATA FROM FASTA");
		        				client.firePropertyChange("indeterminate", true, true);
		        				protein_map = this.fastaLoader.updateProteinMapfromFasta(protein_map);
		        				@SuppressWarnings("unused")
								int test_count_1 = 0;
		            			// after updating description and sequence, we also check the database for redundancy
		        				// first get accessions and proteinids from the protein table
		        				client.firePropertyChange("new message", null, "QUERYING DATABASE FOR PROTEIN ENTRIES");
		        				client.firePropertyChange("indeterminate", true, true);
		        				PreparedStatement prs = conn.prepareStatement("SELECT protein.proteinid, protein.accession FROM protein");
		        				ResultSet aRS = prs.executeQuery();
		        				// look through them
		        				while (aRS.next()) {
		            				test_count_1++;			            				
		            				//if ((test_count_1 % 1000) == 0) {System.out.println("DB-lookup: "+test_count_1);}
		            				// and determine if an accession is already in there
		        					String accession = (String)aRS.getObject("accession");
		        					if (protein_map.containsKey(accession)) {
		        						if (protein_map.get(accession).was_this_protein_submitted()) {
		        							System.out.println("Duplicate protein entry: "+accession);		            							
		        						} else {
		        							// and store the info
		            						long proteinID = aRS.getLong("proteinid");
		            						protein_map.get(accession).addproteinid(proteinID);
		        							protein_map.get(accession).this_protein_is_in_DB();	
		        							// for uniprot, protein is already stored in database, re-use existing ID
		        							Uniprotentry upe = Uniprotentry.findFromProteinID(proteinID, conn);
		        							// unless it misses a uniprot entry
		        							if (upe == null) {
		        								uniProtCandidates.add(accession);
		        							}
		        						}
		        					}
		        				}
		    			    	prs.close();
		    			    	aRS.close();
		    			    	client.firePropertyChange("new message", null, "PARSING MASCOT FILE");
		    			    	client.firePropertyChange("indeterminate", true, false);
		        			}
		        		}
		        		// if we reach queries we can do the main thing
		        		// here we also decide if this query is above the threshold
		        		if ((currentsection.contains("query")) && (query_peptide_map.containsKey(query_number))) {
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
		            				// submit spectrum and searchspectrum
		            				Long spectrumId = specTitleMap.get(current_query.getTitle());
		        					Long searchspectrumID = null;
		        					if (spectrumId != null) {
		        						//TODO: CHECK IF THIS METHOD WORKS PROPERLY
		        						searchspectrumID = Searchspectrum.findFromSpectrumIDAndExperimentID(spectrumId, experimentId, conn).getSearchspectrumid();
		        					} else {
		        						spectrumId = this.storeSpectrum(current_query);			        						
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
		        					HashMap<Integer, MascotPeptideHit> peptidemap = query_peptide_map.get(query_number); 
		        					for (int peptide_number : peptidemap.keySet()) {					
		    							// Fill the peptide table and get peptideID
			        					MascotPeptideHit current_pephit = peptidemap.get(peptide_number);
		        						String pep_sequence = current_pephit.getsequence();	        						
		    							long peptideID = this.storePeptide(pep_sequence);
		    							// Store peptide-spectrum association 
		    							this.storeSpec2Pep(searchspectrumID, peptideID);
		    							// missing protein, pep2prot and mascothit (and uniprotentry/taxonomy which should work afterwards?)
		    							// this code submits a protein entry and the pep2prot ref
		    							// we need accession protein description and full sequence and the proteinID
		    							Long proteinID = null;
		    							// get all accessions from current peptide
		    							for (String prot_acc : peptidemap.get(peptide_number).getproteins()) {
		    								// if protein was already submitted, just update pep2prot ref
		    								if (protein_map.get(prot_acc).was_this_protein_submitted()) {
		    									proteinID = protein_map.get(prot_acc).getproteinid();
			        							// this just updates the pep2prot to a given protein
			        							Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);	
		    								} else {
		    									String accession = protein_map.get(prot_acc).getaccession();
		    									String description = protein_map.get(prot_acc).getdescription();
		    									String sequence = protein_map.get(prot_acc).getsequence();
			        							// this adds a new protein
			        							ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, sequence, conn);
			        							proteinID = (Long) protAccessor.getGeneratedKeys()[0];
			        							// update this protein so its not submitted twice
			        							protein_map.get(prot_acc).this_protein_is_in_DB();
			        							protein_map.get(prot_acc).addproteinid(proteinID);
			        							// this is a new protein so we mark for uniprot lookup
			        							uniProtCandidates.add(accession);
		    								}
		    							}
		    							// finally we submit the mascothit
										this.storeMascotHit(searchspectrumID, peptideID, proteinID, current_query, current_pephit);
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
		
		
		// final commit can probably go
		conn.commit();
		client.firePropertyChange("new message", null, "PROCESSING MASCOT QUERIES FINISHED");
		// this part of the code remained unchanged
		// retrieve UniProt entries
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES");
		client.firePropertyChange("resetall", 0L, 100L);
		client.firePropertyChange("indeterminate", false, true);
		UniProtUtilities uniprotweb = new UniProtUtilities();
		Map<String, ReducedProteinData> proteinData = uniprotweb.getUniProtData(new ArrayList<String>(this.uniProtCandidates));
		client.firePropertyChange("indeterminate", true, false);		
		client.firePropertyChange("resetall", 0L, (long) this.uniProtCandidates.size());
		client.firePropertyChange("resetcur", 0L, (long) this.uniProtCandidates.size());
		// iterate entries
		for (String accession : this.uniProtCandidates) {
			// retrieve protein data from local cache
			ReducedProteinData rpd = proteinData.get(accession);
			if (rpd == null) {
				// candidate accession apparently is not a primary UniProt accession,
				// therefore look in secondary accessions
				outerloop:
				for (ReducedProteinData value : proteinData.values()) {
					List<SecondaryUniProtAccession> secondaryAccessions =
							value.getUniProtEntry().getSecondaryUniProtAccessions();
					for (SecondaryUniProtAccession secAcc : secondaryAccessions) {
						if (secAcc.getValue().equals(accession)) {
							rpd = value;
							break outerloop;
						}
					}
				}
				// if accession still cannot be found skip altogether, tough luck!
				if (rpd == null) {
					client.firePropertyChange("progressmade", true, false);
					continue;
				}
			}
			// retrieve protein from database
			ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, conn);
			long proteinID = protein.getProteinid();
			// look for already stored UniProt entry in database
			Uniprotentry upe = Uniprotentry.findFromProteinID(proteinID, conn);
			if (upe != null) {
				// a UniProt entry already exists, we therefore probably like to update only the protein sequence
				protein.setSequence(rpd.getUniProtEntry().getSequence().getValue());
				protein.update(conn);
			} else {
				// no UniProt entry exists, therefore we create a new one
				UniProtEntry uniProtEntry = rpd.getUniProtEntry();
				// Get taxonomy id
				Long taxID = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());
				// Get EC Numbers
				String ecNumbers = "";
				List<String> ecNumberList = uniProtEntry.getProteinDescription().getEcNumbers();
				if (ecNumberList.size() > 0) {
					for (String ecNumber : ecNumberList) {
						ecNumbers += ecNumber + ";";
					}
					ecNumbers = Formatter.removeLastChar(ecNumbers);
				}
				// Get ontology keywords
				String keywords = "";
				List<Keyword> keywordsList = uniProtEntry.getKeywords();
		
				if (keywordsList.size() > 0) {
					for (Keyword kw : keywordsList) {
						keywords += kw.getValue() + ";";
					}
					keywords = Formatter.removeLastChar(keywords);
				}
				// Get KO numbers
				String koNumbers = "";
				List<DatabaseCrossReference> xRefs = uniProtEntry.getDatabaseCrossReferences(DatabaseType.KO);
				if (xRefs.size() > 0) {
					for (DatabaseCrossReference xRef : xRefs) {
						koNumbers += xRef.getPrimaryId().getValue() + ";";
					}
					koNumbers = Formatter.removeLastChar(koNumbers);
				}
				// get UniRef identifiers
				String uniref100 = rpd.getUniRef100EntryId();
				String uniref90 = rpd.getUniRef90EntryId();
				String uniref50 = rpd.getUniRef50EntryId();
				Uniprotentry.addUniProtEntryWithProteinID(proteinID,
						taxID, ecNumbers, koNumbers, keywords,
						uniref100, uniref90, uniref50, conn);
			}			
			client.firePropertyChange("progressmade", true, false);
		}		
		conn.commit();
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES FINISHED");
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
					double fdr = 1.0 * queryDecoyScores.size() / queryScores.size() ;
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
	private Long storeSpectrum(Query query) throws SQLException {
		Long spectrumid = null;
		HashMap<Object, Object> data = new HashMap<Object, Object>(12);
		data.put(Spectrum.TITLE, query.getTitle().trim());
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
	
	/**
	 * This method puts the proteins and pep2proteins entries in the database
	 * @param peptideID. The ID of the peptide in the database
	 * @param datProtHit. A proteinHit from the MascotDatFile parser.
	 * @param proteinMap. The proteinMap from MascotDatFile parser, containing the link between accession and description 
	 * @return proteinID. The proteinID in the database.
	 * @throws SQLException 
	 */
	// this method is now obsolete
	@SuppressWarnings("unused")
	private StoredProtein storeProtein(long peptideID, ProteinHit proteinHit, ProteinMap proteinMap) throws IOException, SQLException {
		// save information of the protein storing
		StoredProtein storedProt;		
		String protAccession = proteinHit.getAccession();
		// protein hit accession is typically not a proper accession (e.g. 'sp|P86909|SCP_CHIOP'),
		// therefore convert it to a FASTA header and parse accession from it
		String composedHeader = "";
		Header header;
		String accession = null;	// true accession
		String description = null;
		// CASE UNIPROT
		if (protAccession.startsWith("sp") || protAccession.startsWith("tr")) {
			composedHeader = ">" + protAccession + " " + proteinMap.getProteinDescription(protAccession);
			header = Header.parseFromFASTA(composedHeader);
			accession = header.getAccession();
			description = header.getDescription(); 
		}  
		// CASE NCBI--- try to get mapping to UNIPROT
		else if(protAccession.startsWith("gi")) {
			composedHeader = ">" + protAccession + "|" + proteinMap.getProteinDescription(protAccession);
			header = Header.parseFromFASTA(composedHeader);
			protAccession = header.getAccession();
			description = header.getDescription(); 
//			Map<String, String> gi2up = UniProtGiMapper.retrieveGiToUniProtMapping(protAccession);
//			accession = gi2up.get(protAccession);
			if (accession == null) {
				// revert to GI number
				accession = protAccession;
			}
		}
		else if(protAccession.startsWith("generic")) {
			composedHeader = ">" + protAccession + " " + proteinMap.getProteinDescription(protAccession);
			header = Header.parseFromFASTA(composedHeader);
			accession = header.getAccession();
			description = header.getDescription(); 
		}

		// If not UNIPROT or NCBI Header.parseFromFASTA(composedHeader) may fail.... hence set new accessions.
		if ((accession == null) || description == null) {
			ProteinMap proteinMap2 = proteinMap;
			String[] split = protAccession.split("[|]");
			accession = split[1].trim();
			// changed from 0 to 1
			description = proteinMap.getProteinDescription(protAccession);
			//TODO MAYBE here an Mistake with other accession rules 
		}
		// Check whether protein is already in database		
		// this old implementation created a hashmap which led to memory issues
			//HashMap<String, Long> proteinIdMap = MapContainer.getProteinIdMap();
			//Long proteinID = proteinIdMap.get(accession);		
		// this new implementation just gets the proteinID if the accession is already in the database		
		PreparedStatement prs = conn.prepareStatement("select * from protein " + 
									     			  "where protein.accession = ?");
		prs.setString(1, accession);
		ResultSet rs = prs.executeQuery();
		Long proteinID = null;
		while(rs.next()) {			
			ProteinTableAccessor currententry = new ProteinTableAccessor(rs);
			if (currententry.getAccession() == accession) {				
				proteinID = currententry.getProteinid();
			}
		}
		rs.close();
		prs.close();
		// Protein is not in database, create new one
		if (proteinID == null) {
			// Try to fetch sequence
			String sequence = ""; // Sequence is normally empty because the dat file do not contain a sequence
			if (fastaLoader != null) {
				@SuppressWarnings("static-access")
				Protein fastaProt = fastaLoader.getProteinFromFasta(accession);
				// this does nothing here, so removed
				//TObjectLongMap<String> indexMap = fastaLoader.getInstance().getIndexMap();
				sequence = fastaProt.getSequence().getSequence();
			}
			
			ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, sequence, conn);
			proteinID = (Long) protAccessor.getGeneratedKeys()[0];
			// Mark protein for UniProt lookup
			uniProtCandidates.add(accession);
		} else {
			// Protein is already stored in database, re-use existing ID
			Uniprotentry upe = Uniprotentry.findFromProteinID(proteinID, conn);
			// If protein is missing a sequence or a UniProt entry mark it for UniProt lookup later on
			if (upe == null) {
				uniProtCandidates.add(accession);
			}
		}
		storedProt = new StoredProtein(proteinHit.getAccession(), proteinID);
		return storedProt;
	}

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
	private long storeMascotHit(long searchspectrumID, long peptideID, long proteinID, Query query, MascotPeptideHit peptideHit) throws SQLException {
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
		mascotHit.persist(conn);
		mascotHitID = (Long) mascotHit.getGeneratedKeys()[0];
		return mascotHitID;
	}
	
	/**
	 * Helper class to store a protein hit
	 * @author Kay Schallert
	 */	
	public class MascotProteinHit {
        /**
         * local values        
         */
		private String accession;
		private String description;
		private String sequence;
		private long protid;
		private List<MascotPeptideHit> peptides = new ArrayList<MascotPeptideHit>();
		private boolean wassubmitted = false;		
        /**
         * Constructor method.        
         * @param accession -> the accession of this protein (its main identifier)
         */
		public MascotProteinHit(String accession) {		
			this.accession = accession;		
		}
        /**
         * methods        
         */
		public void addPeptide(MascotPeptideHit pephit) {
			this.peptides.add(pephit);
		}
		public void addsequence(String protsequence) {
			this.sequence = protsequence;
		}
		public void adddescription(String protdescription) {
			this.description = protdescription;
		}
		public void addproteinid(Long protid) {
			this.protid = protid;
		}
		public void this_protein_is_in_DB() {
			this.wassubmitted = true;
		}
		public boolean was_this_protein_submitted() {
			return this.wassubmitted;
		}
		public Long getproteinid() {
			return this.protid;
		}
		public String getaccession() {
			return this.accession;
		}
		public String getdescription() {
			return this.description;
		}
		public String getsequence() {
			return this.sequence;
		}
	}
	
	/**
	 * Helper class to store a peptide hit
	 * @author Kay Schallert
	 */	
	public class MascotPeptideHit {		
        /**
         * local values        
         */
		private String peptide_sequence;
		private Double peptide_score;
		private List<String> protein_accesion_list;
		private double deltamass; 
		private double Evalue;
		
        /**
         * Constructor method.        
         * @param pepseq --> peptide sequence
         * @param pepscore --> peptide score
         * @param proteins --> List of proteins (accessions) this peptide belongs to
         * @param dmass --> delta mass
         * @param eval --> excpectancy value
         */
		public MascotPeptideHit(String pepseq, Double pepscore, List<String> proteins, Double dmass, Double eval) {
			this.peptide_score = pepscore;
			this.peptide_sequence = pepseq;
			this.protein_accesion_list = proteins;	
			this.deltamass = dmass;
			this.Evalue = eval;
		}		
        /**
         *  methods        
         */
		public String getsequence() {
			return this.peptide_sequence;
		}
		public Double getscore() {
			return this.peptide_score;
		}
		public List<String> getproteins() {
			return this.protein_accesion_list;			
		}
		public double getEvalue() {
			return this.Evalue;
		}
		public double getdeltamass() {
			return this.deltamass;
		}		
	}
		
	
	/**
	 * Helper class to store information for a stored protein
	 * @author R. Heyer
	 */
	// this class is now obsolete
	private class StoredProtein{
		/* Protein accession*/
		private String accession;
		/* Protein ID from the database*/
		private long protID;
		
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
			return accession;
		}
		/**
		 * Gets the proteinID from the stored protein
		 * @return. The protein ID from the database
		 */
		@SuppressWarnings("unused")
		public long getProtID() {
			return protID;
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
        return this.getProp(sectionDefLine, "name");
    }

    /**
     * This method parses the boundary definition line for the boundary String.
     *
     * @param boundaryDefLine String with the boundary definition line.
     * @return String with the boundary.
     */
    private String getBoundary(String boundaryDefLine) {
        String lookFor = "boundary";
        String found = this.getProp(boundaryDefLine, lookFor);
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

