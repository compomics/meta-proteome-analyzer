package de.mpa.db.storager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.MapContainer;
import de.mpa.db.accessor.OmssahitTableAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.job.scoring.ValidatedPSMScore;
import de.mpa.fastadigest.DigFASTAEntry;
import de.mpa.fastadigest.DigFASTAEntryParser;
import de.mpa.io.fasta.FastaLoader;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSPepHit;
import de.proteinms.omxparser.util.MSSpectrum;

/**
 * This class stores OMSSA results to the DB.
 * 
 * @author T.Muth
 */
public class OmssaStorager extends BasicStorager {

	/**
	 * Variable holding an OmssaOmxFile.
	 */
	private OmssaOmxFile omxFile; 

	/**
	 * File containing the validated PSM scores.
	 */
	private File qValueFile;

	/**
	 * Mapping for OMSSA hit numbers.
	 */
	private HashMap<String, Long> hitNumberMap;

	/**
	 * File containing the original PSM scores.
	 */
	private File targetScoreFile;

	/**
	 * Mapping for the original PSM scores to the validated ones.
	 */
	private HashMap<Double, ValidatedPSMScore> validatedPSMScores;    

	/**
	 * Constructor for storing results from a target-only search with OMSSA.
	 */
	public OmssaStorager(Connection conn, File file) {
		this.conn = conn;
		this.file = file;
		this.searchEngineType = SearchEngineType.OMSSA;
	}

	/**
	 * Constructor for storing results from a target-decoy search with OMSSA.
	 * @param conn Database connection
	 * @param file OMSSA file
	 * @param targetScoreFile File containing the original PSM scores.
	 * @param qValueFile File containing the validated PSM scores.
	 */
	public OmssaStorager(Connection conn, File file, File targetScoreFile, File qValueFile) {
		this.conn = conn;
		this.file = file;
		this.targetScoreFile = targetScoreFile;
		this.qValueFile = qValueFile;
		this.searchEngineType = SearchEngineType.OMSSA;
	}

	/**
	 * Parses and loads the OMSSA results file(s).
	 */
	public void load() {
		omxFile = new OmssaOmxFile(file.getAbsolutePath());
		if (qValueFile != null) this.processQValues();
	}

	/**
	 * Stores results from the OMSSA search engine to the database.
	 * @param conn
	 * @throws IOException
	 * @throws SQLException
	 */
	public void store() throws IOException, SQLException {
		// Iterate over all the spectra
		HashMap<MSSpectrum, MSHitSet> results = omxFile.getSpectrumToHitSetMap();
		Iterator<MSSpectrum> iterator = results.keySet().iterator();  	
		// HitIndex as key, xtandemID as value.
		hitNumberMap = new HashMap<String, Long>();
		int counter = 0;
		while (iterator.hasNext()) {
			// Get the next spectrum.
			MSSpectrum msSpectrum = iterator.next();   
			MSHitSet msHitSet = results.get(msSpectrum);
			List<MSHits> hitlist = msHitSet.MSHitSet_hits.MSHits;
			int hitnumber = 1;
			for (MSHits msHit : hitlist) {
				HashMap<Object, Object> hitdata = new HashMap<Object, Object>(16);    	    	

				// Get the spectrum id for the given spectrumName for the OmssaFile    
				String spectrumTitle = msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString();

				spectrumTitle = formatSpectrumTitle(spectrumTitle); 
				if (MapContainer.SpectrumTitle2IdMap.get(spectrumTitle) != null) {
					long searchspectrumID = MapContainer.SpectrumTitle2IdMap.get(spectrumTitle);

					Double qValue = 1.0;
					Double pep = 1.0;
					if (validatedPSMScores != null) {
						ValidatedPSMScore validatedPSMScore = validatedPSMScores.get(msHit.MSHits_evalue);
						if (validatedPSMScore != null) {
							qValue = validatedPSMScore.getQvalue();
							pep = validatedPSMScore.getPep();
						} 
					}

					if (qValue < 0.1) {
						hitdata.put(OmssahitTableAccessor.FK_SEARCHSPECTRUMID, searchspectrumID);

						// Get the MSPepHit (for the accession)
						List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
						Iterator<MSPepHit> pepHitIterator = pepHits.iterator();
						MSPepHit pepHit = pepHitIterator.next();
						hitdata.put(OmssahitTableAccessor.HITSETNUMBER,	Long.valueOf(msHitSet.MSHitSet_number));
						hitdata.put(OmssahitTableAccessor.EVALUE, msHit.MSHits_evalue);
						hitdata.put(OmssahitTableAccessor.PVALUE, msHit.MSHits_pvalue);
						hitdata.put(OmssahitTableAccessor.CHARGE, Long.valueOf(msHit.MSHits_charge));
						hitdata.put(OmssahitTableAccessor.MASS,	msHit.MSHits_mass);
						hitdata.put(OmssahitTableAccessor.THEOMASS,	msHit.MSHits_theomass);
						hitdata.put(OmssahitTableAccessor.START, msHit.MSHits_pepstart);
						hitdata.put(OmssahitTableAccessor.END, msHit.MSHits_pepstop);
						hitdata.put(OmssahitTableAccessor.PEP, pep);
						hitdata.put(OmssahitTableAccessor.QVALUE, qValue);

						// Get the peptide id
						String sequence = msHit.MSHits_pepstring;
						long peptideID = this.storePeptide(sequence);
						hitdata.put(OmssahitTableAccessor.FK_PEPTIDEID,	peptideID);

						// Store peptide-spectrum association
						this.storeSpec2Pep(searchspectrumID, peptideID);

						// Parse the FASTA header
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(">" +pepHit.MSPepHit_defline, "", 0L);
						String accession = entry.getIdentifier();

						// Scan for additional protein hits
						HashSet<String> accessionSet = new HashSet<String>();
						FastaLoader loader = FastaLoader.getInstance();
						if (loader.getPepFile()!=null) {
							// There is a separate digested peptide file available
							accessionSet = loader.getProtHits(sequence);
							accessionSet.add(accession);
						} else {
							accessionSet.add(accession);
						}

						for (String acc : accessionSet) {
							
							ProteinAccessor protSql = ProteinAccessor.findFromAttributes(acc, conn);
							if (protSql != null) {
								hitdata.put(OmssahitTableAccessor.FK_PROTEINID,	protSql.getProteinid());

								// Create the database object.
								OmssahitTableAccessor omssahit = new OmssahitTableAccessor(hitdata);
								omssahit.persist(conn);
								counter++;

								// Get the omssahitid
								Long omssahitid = (Long) omssahit.getGeneratedKeys()[0];
								hitNumberMap.put(msHitSet.MSHitSet_number + "_"	+ hitnumber, omssahitid);
								hitnumber++;
							} else {
								System.err.println("Protein: " + acc + " not found in the database.");
							}
						}

					}
				}
			}
		}
		conn.commit();
		log.debug("No. of OMSSA hits saved: " + counter);
	}

	/**
	 * Format OMSSA spectrum title.
	 * @param spectrumTitle Unformatted spectrum title
	 * @return Formatted spectrum title.
	 */
	private String formatSpectrumTitle(String spectrumTitle) {
		if(spectrumTitle.contains("\\\\")){
			spectrumTitle = spectrumTitle.replace("\\\\", "\\");
		} 
		if(spectrumTitle.contains("\\\"")){
			spectrumTitle = spectrumTitle.replace("\\\"", "\"");
		}
		return spectrumTitle;
	}

	// Get the qvalues from the results
	private void processQValues() {
		// buffered Reader for qvalues 
		BufferedReader qValueFileReader;

		// buffered reasder for target files
		BufferedReader targetFileReader;
		if (qValueFile.exists()) {

			try {
				qValueFileReader = new BufferedReader(new FileReader(qValueFile));
				targetFileReader = new BufferedReader(new FileReader(targetScoreFile));
				validatedPSMScores = new HashMap<Double, ValidatedPSMScore>();
				String nextLine;
				// Skip the first line
				qValueFileReader.readLine();
				// Iterate over all the lines of the file.
				while ((nextLine = qValueFileReader.readLine()) != null) {
					StringTokenizer tokenizer = new StringTokenizer(nextLine, "\t");
					List<String> tokenList = new ArrayList<String>();
					// Iterate over all the tokens
					while (tokenizer.hasMoreTokens()) {
						tokenList.add(tokenizer.nextToken());
					}

					ValidatedPSMScore validatedPSMScore = new ValidatedPSMScore(Double.valueOf(tokenList.get(0)), Double.valueOf(tokenList.get(1)), Double.valueOf(tokenList.get(2)));

					// Get original target score
					double score = Double.valueOf(targetFileReader.readLine());
					validatedPSMScores.put(score, validatedPSMScore);
				}
				qValueFileReader.close();
				targetFileReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{System.out.println("No OMMSA results");}
	}
}
