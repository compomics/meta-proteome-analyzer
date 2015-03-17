package de.mpa.io.parser.omssa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GeneralParser;
import de.mpa.job.scoring.ValidatedPSMScore;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSPepHit;
import de.proteinms.omxparser.util.MSSpectrum;

public class OmssaParser extends GeneralParser {
	/**
     * Variable holding an OmssaOmxFile.
     */
	private OmssaOmxFile omxFile; 
	
	/**
	 * Mapping for the original PSM scores to the validated ones.
	 */
	private HashMap<Double, ValidatedPSMScore> validatedPSMScores;
	
   /**
    * Constructor for storing results from a target-decoy search with OMSSA.
    * @param conn Database connection
    * @param file OMSSA file
    * @param targetScoreFile File containing the original PSM scores.
    * @param qValueFile File containing the validated PSM scores.
    */
	public OmssaParser(final File file, File targetScoreFile, File qValueFile) {
		this.file = file;
		this.targetScoreFile = targetScoreFile;
		this.qValueFile = qValueFile;
		this.searchEngineType = SearchEngineType.OMSSA;
		load();
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
    @Override
    public void parse() {
        // Iterate over all the spectra
        HashMap<MSSpectrum, MSHitSet> results = omxFile.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> iterator = results.keySet().iterator();  	
    	nHits = 0;
    	while (iterator.hasNext()) {
    		// Get the next spectrum.
    	    MSSpectrum msSpectrum = iterator.next();   
    	    MSHitSet msHitSet = results.get(msSpectrum);
    	    List<MSHits> hitlist = msHitSet.MSHitSet_hits.MSHits;
    	    for (MSHits msHit : hitlist) {
    	    	// Get the spectrum id for the given spectrumName for the OmssaFile    
    	    	String spectrumTitle = msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString();
    	    	
    	    	spectrumTitle = formatSpectrumTitle(spectrumTitle); 
    	    	if(SpectrumTitle2IdMap.get(spectrumTitle) != null) {
          	      	long spectrumId = SpectrumTitle2IdMap.get(spectrumTitle);
          		  	
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
						OmssaHit hit = new OmssaHit();
						hit.setSpectrumId(spectrumId);
						
						// Get the MSPepHit (for the accession)
						List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
						Iterator<MSPepHit> pepHitIterator = pepHits.iterator();
						MSPepHit pepHit = pepHitIterator.next();
						hit.setpValue(msHit.MSHits_pvalue);
						hit.setCharge(msHit.MSHits_charge);
						hit.setPeptideSequence(msHit.MSHits_pepstring);
						
						try {
							// Parse the FASTA header
							Header header = Header.parseFromFASTA(pepHit.MSPepHit_defline);
							String accession = header.getAccession();
							hit.setAccession(accession);
						    hit.setScore(msHit.MSHits_evalue);                
	                        hit.setPep(pep);
	                        hit.setQValue(qValue);   
	                        hit.setType(searchEngineType);
	                        
	                        Protein protein = FastaLoader.getProteinFromFasta(accession);
							String description = protein.getHeader().getDescription();
                            hit.setProteinSequence(protein.getSequence().getSequence());
                            hit.setProteinDescription(description);
                            
                    		// Add protein for UniProt storing.
                    		UniprotQueryProteins.put(accession, null);
							nHits++;
							SearchHits.add(hit);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
    	    }
        }
	    log.debug("No. of OMSSA hits saved: " + nHits);
    }
    
    /**
     * Format OMSSA spectrum title.
     * @param spectrumTitle Unformatted spectrum title
     * @return Formatted spectrum title.
     */
	protected String formatSpectrumTitle(String spectrumTitle) {
		if(spectrumTitle.contains("\\\\")){
			spectrumTitle = spectrumTitle.replace("\\\\", "\\");
		} 
		if(spectrumTitle.contains("\\\"")){
			spectrumTitle = spectrumTitle.replace("\\\"", "\"");
		}
		return spectrumTitle;
	}
    
    private void processQValues() {
		BufferedReader qValueFileReader;
		BufferedReader targetFileReader;
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
	}	  
}
