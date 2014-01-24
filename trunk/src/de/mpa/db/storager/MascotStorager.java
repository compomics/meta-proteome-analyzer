package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
import java.util.Vector;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.Peak;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.mascotdatfile.util.mascot.ProteinMap;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.mascotdatfile.util.mascot.QueryToPeptideMap;
import com.compomics.util.protein.Header;

import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtGiMapper;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.SearchSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.SixtyFourBitStringSupport;
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
    
    /**
     * MascotDatfile instance.
     */
	private MascotDatfile mascotDatFile;

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
	 * Constructs a {@link MascotStorager} for parsing and storing of Mascot .dat files to the DB. 
	 * @param conn Connection instance.
	 * @param file File instance. 
	 */
	public MascotStorager(Connection conn, File file, SearchSettings searchSettings, ParameterMap mascotParams){
    	this.conn = conn;
    	this.file = file;
    	this.searchSettings = searchSettings;
		this.mascotParams = mascotParams;
		this.searchEngineType = SearchEngineType.MASCOT;
    }
	

	@Override
	public void load() {
		client = Client.getInstance();
//		client.firePropertyChange("new message", null, "LOADING MASCOT FILE");
//		client.firePropertyChange("resetall", 0L, 100L);
//		client.firePropertyChange("indeterminate", false, true);
		mascotDatFile = new MascotDatfile(file.getAbsolutePath());
//		client.firePropertyChange("new message", null, "LOADING MASCOT FILE FINISHED");
//		client.firePropertyChange("indeterminate", true, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void store() throws Exception {
		
		client.firePropertyChange("new message", null, "PARSING MASCOT FILE");
		client.firePropertyChange("resetall", 0L, 100L);
		client.firePropertyChange("indeterminate", false, true);
		
		// Fetch the peptides for all queries.
		QueryToPeptideMap queryToPeptideMap = mascotDatFile.getQueryToPeptideMap();
		Vector<Query> queryList = mascotDatFile.getQueryList();
		
		/* The proteinAccession2Description Map */
		ProteinMap proteinMap = mascotDatFile.getProteinMap();		
		double scoreThreshold = this.getScoreThreshold(mascotParams, queryList);
		
		// Get experiment id.
		long experimentId = ClientFrame.getInstance().getProjectPanel().getCurrentExperimentId();

		// MGF list of all spectra from a certain experiment.
		List<MascotGenericFile> dbSpectra =
				new SpectrumExtractor(conn).getSpectraByExperimentID(experimentId, false, false, true);
		
		// Put titles and spectrum Id's of mgf in list
		Map<String,Long> specTitleMap = new TreeMap<String,Long>();
		for (int i = 0; i < dbSpectra.size(); i++) {
			specTitleMap.put(dbSpectra.get(i).getTitle(), dbSpectra.get(i).getSpectrumID());
		}
		

		client.firePropertyChange("new message", null, "PARSING MASCOT FILE FINISHED");
		client.firePropertyChange("indeterminate", true, false);
		
		client.firePropertyChange("new message", null, "PROCESSING MASCOT QUERIES");
		client.firePropertyChange("resetall", 0L, (long) queryList.size());
		client.firePropertyChange("resetcur", null, (long) queryList.size());
		
		int idCounter = 0;
//		int queryCounter = 0;
		for (Query query : queryList) {
			Vector<PeptideHit> peptideHitsFromQuery =
					queryToPeptideMap.getPeptideHitsAboveIdentityThreshold(query.getQueryNumber(), 0.05);
			if (peptideHitsFromQuery != null) {
				
				boolean identificationFound = false;
				// Check whether query contains identification.
				for (PeptideHit peptideHit : peptideHitsFromQuery) {
					if (peptideHit.getIonsScore() >= scoreThreshold) {
						identificationFound = true;
					}
				}
				
				if (identificationFound) {
					idCounter++;
					 // Check whether spectrum is already in the database, otherwise add it.
					Long spectrumId = specTitleMap.get(query.getTitle());	
					Long searchSpectrumId = null;
					if (spectrumId != null) {
							searchSpectrumId = Searchspectrum.findFromSpectrumIDAndExperimentID(
									spectrumId, experimentId, conn).getSearchspectrumid();
					} else {
						spectrumId = this.storeSpectrum(query);

						/* Search spectrum storager */
						HashMap<Object, Object> data = new HashMap<Object, Object>(5);
						data.put(Searchspectrum.FK_SPECTRUMID, spectrumId);
						data.put(Searchspectrum.FK_EXPERIMENTID, searchSettings.getExpID());
						Searchspectrum searchSpectrum = new Searchspectrum(data);
						searchSpectrum.persist(conn);
						searchSpectrumId = (Long) searchSpectrum.getGeneratedKeys()[0];
						// Add new spectrum to map with title and spectrum IDs
						specTitleMap.put(query.getTitle(),spectrumId);
					}
					
					for (PeptideHit peptideHit : peptideHitsFromQuery) {
						if (peptideHit.getIonsScore() >= scoreThreshold) {
							// Fill the peptide table and get peptideID
							long peptideID = this.storePeptide(peptideHit.getSequence());
							
							// Get proteins and fill them into the table
							List<ProteinHit> proteinHits = peptideHit.getProteinHits();
							for (ProteinHit datProtHit : proteinHits) {
								long proteinID = this.storeProtein(
										peptideID, datProtHit, proteinMap);
								this.storeMascotHit(
										searchSpectrumId, peptideID, proteinID, query, peptideHit);
							}
						}
					}
				}
			}
			if (idCounter % 100 == 0) {
				conn.commit();
			}
//			queryCounter++;
			client.firePropertyChange("progressmade", 0L, 1L);
		}
		conn.commit();
		client.firePropertyChange("new message", null, "PROCESSING MASCOT QUERIES FINISHED");
		
		// retrieve UniProt entries
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES");
		client.firePropertyChange("resetall", 0L, 100L);
		client.firePropertyChange("indeterminate", false, true);
		Map<String, ReducedProteinData> proteinData =
				UniProtUtilities.retrieveProteinData(new ArrayList<String>(this.uniProtCandidates));
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES FINISHED");
		client.firePropertyChange("indeterminate", true, false);
		
		client.firePropertyChange("resetall", 0L, (long) this.uniProtCandidates.size());
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
					client.firePropertyChange("progressmade", 0L, 1L);
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
						koNumbers += (((KO) xRef).getKOIdentifier().getValue()) + ";";
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
			client.firePropertyChange("progressmade", 0L, 1L);
		}
		conn.commit();
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
	 * This method puts the peptide from a Mascot-dat. file into the database
	 * @return peptideID. The peptide ID in the database.
	 * @throws SQLException 
	 */
	private long storePeptide(String sequence) throws SQLException {
		long peptideID;
		PeptideAccessor peptide = null;
		peptide = PeptideAccessor.findFromSequence(sequence, conn);
		if (peptide == null) {	// sequence not yet in database
			HashMap<Object, Object> dataPeptide = new HashMap<Object, Object>(2);
			dataPeptide.put(PeptideAccessor.SEQUENCE, sequence);
			peptide = new PeptideAccessor(dataPeptide);
			peptide.persist(conn);
			peptideID = (Long) peptide.getGeneratedKeys()[0];
		} else {
			peptideID = peptide.getPeptideid();
		}
		return peptideID;
	}

	/**
	 * This method puts the proteins and pep2proteins entries in the database
	 * @param peptideID. The ID of the peptide in the database
	 * @param datProtHit. A proteinHit from the MascotDatFile parser.
	 * @param proteinMap. The proteinMap from MascotDatFile parser, containing the link between accession and description 
	 * @return proteinID. The proteinID in the database.
	 * @throws SQLException 
	 */
	private long storeProtein(long peptideID, ProteinHit proteinHit, ProteinMap proteinMap) throws IOException, SQLException {
		String protAccession = proteinHit.getAccession();
		
		// protein hit accession is typically not a proper accession (e.g. 'sp|P86909|SCP_CHIOP'),
		// therefore convert it to a FASTA header and parse accession from it
		String composedHeader = "";
		Header header;
		String accession;	// true accession
		if (protAccession.startsWith("sp") || protAccession.startsWith("tr")) {
			composedHeader = ">" + protAccession + " " + proteinMap.getProteinDescription(protAccession);
			header = Header.parseFromFASTA(composedHeader);
			accession = header.getAccession();
		} else {
			composedHeader = ">" + protAccession + "|" + proteinMap.getProteinDescription(protAccession);
			header = Header.parseFromFASTA(composedHeader);
			if (protAccession.startsWith("gi")) {
				protAccession = header.getAccession();
				Map<String, String> gi2up = UniProtGiMapper.retrieveGiToUniProtMapping(protAccession);
				accession = gi2up.get(protAccession);
				if (accession == null) {
					// revert to GI number
					accession = protAccession;
				}
			} else {
				accession = header.getAccession();
			}
		}
		String description = header.getDescription();
		
		// Change UniProt identifier to uniProt accession
		long proteinID = 0;
		ProteinAccessor protAcc = ProteinAccessor.findFromAttributes(accession, conn);
		// check whether protein is already in database
		// TODO: querying single accessions may be slow, find proper work-around (e.g. querying a list of accessions)
		if (protAcc == null) {
			// protein is not in database, create new one
			ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(
					peptideID, accession, description, "", conn);
			proteinID = (Long) protAccessor.getGeneratedKeys()[0];
			// mark protein for UniProt lookup
			uniProtCandidates.add(accession);
		} else {
			// protein is already stored in database, re-use existing ID
			proteinID = protAcc.getProteinid();
			Uniprotentry upe = Uniprotentry.findFromProteinID(proteinID, conn);
			// if protein is missing a sequence or a UniProt entry mark it for UniProt lookup later on
			if (protAcc.getSequence().isEmpty() || (upe == null)) {
				uniProtCandidates.add(accession);
			}
		}
		return proteinID;
	}

	/**
	 * This method puts a MascotHit entry to the database.
	 * @param searchspectrumID. The spectrumID in the database.
	 * @param peptideID. The peptideID in the database.
	 * @param proteinID. The proteinID in the database. 
	 * @param query. The MascotDatFile parser query.
	 * @param datPeptideHit. The MascotDatFile peptide.
	 * @return mascothitID. The ID of the MascotHit in the database.
	 * @throws SQLException 
	 */
	private long storeMascotHit(long searchspectrumID, long peptideID, long proteinID, Query query, PeptideHit peptideHit) throws SQLException {
		long mascotHitID = 0;
		HashMap<Object, Object> data = new HashMap<Object, Object>(10);
		data.put(Mascothit.FK_SEARCHSPECTRUMID, searchspectrumID);
		data.put(Mascothit.FK_PEPTIDEID, peptideID);
		data.put(Mascothit.FK_PROTEINID, proteinID);
		String chargeString = query.getChargeString();
		chargeString = chargeString.replaceAll("[^\\d]", "");
		data.put(Mascothit.CHARGE, Long.valueOf(chargeString));
		data.put(Mascothit.IONSCORE, peptideHit.getIonsScore());
		data.put(Mascothit.EVALUE, peptideHit.getExpectancy());
		data.put(Mascothit.DELTA, peptideHit.getDeltaMass());
		// Save spectrum in database
		Mascothit mascotHit	 = new Mascothit(data);
		mascotHit.persist(conn);
		mascotHitID = (Long) mascotHit.getGeneratedKeys()[0];
		return mascotHitID;
	}
	
	/**
	 * Retrieves the score threshold based on local FDR or absolute limit.
	 * @param mascotParams Mascot ParameterMap.
	 * @return {@link Double} Score threshold
	 * @throws Exception 
	 */
	private double getScoreThreshold(ParameterMap mascotParams, Vector<Query> queryList) throws Exception {
		// Init FDR score threshold.
		double scoreThreshold = 0.0;	
		mascotParams.get("filter");
		Object[][] values = (Object[][]) mascotParams.get("filter").getValue();
		if ((Boolean) values[0][0]) {
			scoreThreshold = (Integer) values[0][1];
		} else {
			/* Score list of identified queries */
			List<Double> queryScores = new ArrayList<Double>();

			/* Score list of identified decoy queries */
			List<Double> queryDecoyScores = new ArrayList<Double>();
			
			QueryToPeptideMap queryToPeptideMap = mascotDatFile.getQueryToPeptideMap();
			// Calculate query scores for decoy
			QueryToPeptideMap decoyQueryToPeptideMap = mascotDatFile.getDecoyQueryToPeptideMap();

			for (Query query : queryList) {
				@SuppressWarnings("unchecked")
				Vector<PeptideHit> allPeptideHits = queryToPeptideMap.getAllPeptideHits(query.getQueryNumber());
				@SuppressWarnings("unchecked")
				Vector<PeptideHit> allDecoyPeptideHits = decoyQueryToPeptideMap.getAllPeptideHits(query.getQueryNumber());
				if (allPeptideHits != null) {
					for (PeptideHit peptideHit : allPeptideHits) {
						queryScores.add(peptideHit.getIonsScore());
					}
				}
				if (allDecoyPeptideHits != null) {
					for (PeptideHit peptideHit : allDecoyPeptideHits) {
						queryDecoyScores.add(peptideHit.getIonsScore());
					}
				}
			}
			Collections.sort(queryScores);
			Collections.sort(queryDecoyScores);

			/* False discovery rate which should be reached */
			double targetFDR = (Double) values[1][1];

			/* Actual false discovery rate*/
			double fdr;
			// Increase IonScore until FDR is reached
			int ionThreshold;
			for (ionThreshold = 0; ionThreshold <= MAX_ION_THRESHOLD + 1; ionThreshold++) {
				if (queryScores.size() != 0) {// Check for 0 as divisor

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
					fdr = 1.0 * queryDecoyScores.size() / queryScores.size() ;
					if (fdr <= targetFDR ) {
						scoreThreshold = ionThreshold;
						break;
					}
				}
			}
			if (ionThreshold > 1000) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", "Not possible to calculate the FDR", null, null, null, ErrorLevel.SEVERE, null));
				throw new Exception("Ion threshold above 1000.");
			}
		}
		return scoreThreshold;
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
}
