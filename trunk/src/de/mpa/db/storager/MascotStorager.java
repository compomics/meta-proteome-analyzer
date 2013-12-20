package de.mpa.db.storager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
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
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.SixtyFourBitStringSupport;

public class MascotStorager extends BasicStorager {
	
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

	private Client client;
    
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
		
		// Fetch the peptides for all queries.
		QueryToPeptideMap queryToPeptideMap = mascotDatFile.getQueryToPeptideMap();
		Vector<Query> queryList = mascotDatFile.getQueryList();
		
		/* The proteinAccession2Description Map */
		ProteinMap proteinMap = mascotDatFile.getProteinMap();		
		Set<PeptideHit> totalPeptideHits = new HashSet<PeptideHit>();
		
		// TODO: Use settings.
		double scoreThreshold = getScoreThreshold(mascotParams, queryList);
		Map<String, PeptideHit> bestPeptideHits = new HashMap<String, PeptideHit>();
		
		//client.firePropertyChange("new message", null, "RETRIEVING MASCOT PEPTIDE HITS");
		client.firePropertyChange("resetall", -1L, (long) queryList.size());
		client.firePropertyChange("resetcur", -1L, (long) queryList.size());
		
		for (Query query : queryList) {
			Vector<PeptideHit> peptideHitsFromQuery = queryToPeptideMap.getAllPeptideHits(query.getQueryNumber());
			if (peptideHitsFromQuery != null) {
				for (PeptideHit peptideHit : peptideHitsFromQuery) {
					
					if (peptideHit.getIonsScore() >= scoreThreshold) {
						totalPeptideHits.add(peptideHit);
						String sequence = peptideHit.getSequence();
						
						if(bestPeptideHits.get(sequence) != null) {
							PeptideHit peptideHit2 = bestPeptideHits.get(sequence);
							if(peptideHit.getIonsScore() > peptideHit2.getIonsScore()){
								bestPeptideHits.put(sequence, peptideHit);
							}
						} else {
							bestPeptideHits.put(sequence, peptideHit);
						}
					}
				}
			}
			client.firePropertyChange("progressmade", false, true);
		}
//		client.firePropertyChange("new message", null, "RETRIEVING MASCOT PEPTIDE HITS FINISHED");
		
		
		List<String> identifierList = new ArrayList<String>();
		Set<String> giSet = new HashSet<String>();
		Map<String, Double> proteinScores = new HashMap<String, Double>();
		List<PeptideHit> uniquePeptides = new ArrayList<PeptideHit>();
		for (PeptideHit peptideHit : bestPeptideHits.values()) {
			double score = peptideHit.getIonsScore();
			
			ArrayList<ProteinHit> proteinHits = peptideHit.getProteinHits();
			for (ProteinHit proteinHit : proteinHits) {
				String accession = proteinHit.getAccession();
				if(proteinScores.get(accession) != null) {
					score = proteinScores.get(accession);
					score += peptideHit.getIonsScore();
				} 
				if(accession.equals("gi|30794280")){
					uniquePeptides.add(peptideHit);
				}
				proteinScores.put(accession, score);
			}
		}
		
		for (PeptideHit peptideHit : totalPeptideHits) {
			ArrayList<ProteinHit> proteinHits = peptideHit.getProteinHits();
			for (ProteinHit proteinHit : proteinHits) {
				String accession = proteinHit.getAccession();
				if(accession.startsWith("sp") || accession.startsWith("tr")) {
					String shortAcc = accession.substring(3, accession.lastIndexOf("|"));
					identifierList.add(shortAcc);
				} else if (accession.startsWith("gi")){
					giSet.add(accession.substring(3, accession.length()));
				}
			}
		}
		
//		System.out.println("no. of peptides: " + uniquePeptides.size());
//		System.out.println("protein score: " + proteinScores.get("gi|30794280"));
//		for (PeptideHit peptideHit : uniquePeptides) {
//			System.out.println(peptideHit.getSequence()+ " - score: " + peptideHit.getIonsScore());
//			System.out.println("e-value: " + peptideHit.getExpectancy(0.05));
//		}
		Map<String, String> uniProtAccessions = null;
		if(!giSet.isEmpty()) {
			uniProtAccessions =	UniProtGiMapper.retrieveGiToUniProtMapping(new ArrayList<String>(giSet));
			for (String gi : giSet) {
				String acc = uniProtAccessions.get(gi);
				if (acc != null) {
					identifierList.add(uniProtAccessions.get(gi));
				}
			}
		}
		
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES");
		client.firePropertyChange("resetall", 0L, 100L);
		client.firePropertyChange("indeterminate", false, true);
		Map<String, ReducedProteinData> proteinDataMap = null;			
		if (!identifierList.isEmpty()) {
			//TODO: Progress bars.
			proteinDataMap = UniProtUtilities.retrieveProteinData(identifierList);
		}
		
		client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES FINISHED");
		client.firePropertyChange("indeterminate", true, false);
		
		// TODO: Set progress bar
//		Client client = Client.getInstance();
//		client.firePropertyChange("resetall", 0L, queryList.size());

		// Get experiment id.
		long experimentId = ClientFrame.getInstance().getProjectPanel().getCurrentExperimentId();

		// MGF list of all spectra from a certain experiment.
		List<MascotGenericFile> dbSpectra = null;
		dbSpectra = new SpectrumExtractor(conn).getSpectraByExperimentID(experimentId, false, false, true);
		
		// Put titles and spectrum Id's of mgf in list
		Map<String,Long> specTitleMap = new TreeMap<String,Long>();
		for (int i = 0; i < dbSpectra.size(); i++) {
			specTitleMap.put(dbSpectra.get(i).getTitle(), dbSpectra.get(i).getSpectrumID());
		}
		
		// Cache the proteins.
		Map<String, Long> proteinsDbMap = ProteinAccessor.findAllProteins(conn);
		
		int idCounter = 0, queryCounter = 0;
		for (Query query : queryList) {
			client.firePropertyChange("progressmade", 0L, queryCounter + 1);
			Vector<PeptideHit> peptideHitsFromQuery = queryToPeptideMap.getPeptideHitsAboveIdentityThreshold(query.getQueryNumber(), 0.05);
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
					 // Check whether spectrum is already in the database otherwise add it.
					Long spectrumId = specTitleMap.get(query.getTitle());	
					Long searchSpectrumId = null;
					if (spectrumId != null) {
							searchSpectrumId = Searchspectrum.findFromSpectrumIDAndExperimentID(spectrumId, experimentId, conn).getSearchspectrumid();
					} else {
						spectrumId = storeSpectrum(query);

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
							long peptideID = storePeptide(peptideHit.getSequence());
							
							// Get proteins and fill them into the table
							ArrayList<ProteinHit> proteinHits = peptideHit.getProteinHits();
							for (ProteinHit datProtHit : proteinHits) {
								long proteinID = storeProtein(peptideID, datProtHit, proteinMap, proteinsDbMap, proteinDataMap, uniProtAccessions);
								storeMascotHit(searchSpectrumId, peptideID, proteinID, query, peptideHit);
							}
						}
					}
				}
			}
			if(idCounter % 100 == 0) {
				conn.commit();
			}
			queryCounter++;
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
	private long storeProtein(long peptideID, ProteinHit proteinHit, ProteinMap proteinMap, Map<String, Long> proteinsDbMap, Map<String, ReducedProteinData> proteinDataMap, Map<String, String> uniProtAccessions) throws SQLException {
		String accession = proteinHit.getAccession();
		
		Header header;
		String composedHeader = "";
		if(accession.startsWith("sp") || accession.startsWith("tr")) {
			composedHeader = ">" + accession + " " + proteinMap.getProteinDescription(accession);
		} else {
			composedHeader = ">" + accession + "|" + proteinMap.getProteinDescription(accession);
		}
		header = Header.parseFromFASTA(composedHeader);
		
		String description = header.getDescription();
		accession = header.getAccession();
		String sequence = "";
		
		if(uniProtAccessions != null) {
			// If UniProt accession mapping is available.
			if(uniProtAccessions.get(accession) != null) {
				accession = uniProtAccessions.get(accession);
			}
		}
		
		if (proteinDataMap != null) {
			ReducedProteinData proteinData = proteinDataMap.get(accession);
			
			// UniProt entry must be available.
			if(proteinData != null && proteinData.getUniProtEntry() != null) {
				UniProtEntry uniProtEntry = proteinData.getUniProtEntry();
				accession = uniProtEntry.getPrimaryUniProtAccession().getValue();
				description = getProteinName(uniProtEntry.getProteinDescription());
				sequence = uniProtEntry.getSequence().getValue();
			} 
		}
		
		// Change UniProt identifier to uniProt accession
		long proteinID = 0;
		if (proteinsDbMap.get(accession) == null) {
				ProteinAccessor protAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, "", conn);
				proteinID = (Long) protAccessor.getGeneratedKeys()[0];
				proteinsDbMap.put(accession, proteinID);
		} else {
			proteinID = proteinsDbMap.get(accession);
			ProteinAccessor pa = ProteinAccessor.findFromID(proteinID, conn);
			// replace incorrect protein information with data from UniProt
			pa.setAccession(accession);
			pa.setDescription(description);
			pa.setSource(header.getDatabaseType().name());
			pa.setSequence(sequence);
			pa.setModificationdate(new Timestamp(new Date().getTime()));
			// store modified protein in database
			pa.update(conn);
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
