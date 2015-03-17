package de.mpa.io.parser.xtandem;

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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GeneralParser;
import de.mpa.job.scoring.ValidatedPSMScore;
import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.ProteinMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

public class XTandemParser extends GeneralParser {
	
    /**
     * Variable holding an xTandemFile.
     */
    private XTandemFile xTandemFile;
	
	/**
	 * Mapping for the original PSM scores to the validated ones.
	 */
	private HashMap<Double, ValidatedPSMScore> validatedPSMScores;
  
	
    /**
     * Constructor for storing results from a target-decoy search with X!Tandem.
     * @param conn Database connection
     * @param file X!Tandem file
     * @param targetScoreFile File containing the original PSM scores.
     * @param qValueFile File containing the validated PSM scores.
     */
	public XTandemParser(final File file, File targetScoreFile, File qValueFile) {
		this.file = file;
		this.targetScoreFile = targetScoreFile;
		this.qValueFile = qValueFile;
		this.searchEngineType = SearchEngineType.XTANDEM;
		load();
	}

    /**
     * Parses and loads the X!Tandem results file(s).
     */
    public void load() {
        try {
            xTandemFile = new XTandemFile(file.getAbsolutePath());
        } catch (SAXException ex) {
            log.error("Error while parsing X!Tandem file: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
        if(qValueFile != null) this.processQValues();
    }
    
    /**
     * Parse X!Tandem results file contents and adds them to a searchhit list.
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public void parse() {
               
        // Iterate over all the spectra
        @SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();

        // Prepare everything for the peptides.
        PeptideMap pepMap = xTandemFile.getPeptideMap();
        
        // ProteinMap protMap 
        ProteinMap protMap = xTandemFile.getProteinMap();
        
        nHits = 0;
        while (iter.hasNext()) {

            // Get the next spectrum.
            Spectrum spectrum = iter.next();
            int spectrumNumber = spectrum.getSpectrumNumber();
            
            String spectrumTitle = xTandemFile.getSupportData(spectrumNumber).getFragIonSpectrumDescription();
            spectrumTitle = formatSpectrumTitle(spectrumTitle);
            // Get all identifications from the spectrum
            ArrayList<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
            List<String> peptides = new ArrayList<String>();
            
            // Iterate over all peptide identifications aka. domains
            for (Peptide peptide : pepList) {
            	
            	List<Domain> domains = peptide.getDomains();
            	for (Domain domain : domains) {
                   	String sequence = domain.getDomainSequence();
					if (!peptides.contains(sequence)) {
						
                	    // Only store if the search spectrum id is referenced.
						if (SpectrumTitle2IdMap.containsKey(spectrumTitle)) {
                	    	long spectrumId = SpectrumTitle2IdMap.get(spectrumTitle);
                	        Double qValue = 1.0;
            	            Double pep = 1.0;
                	    	if (validatedPSMScores != null) {
                	    		ValidatedPSMScore validatedPSMScore = validatedPSMScores.get(domain.getDomainHyperScore());
                	    		if (validatedPSMScore != null) {
                	    	    	qValue = validatedPSMScore.getQvalue();
                	    	    	pep = validatedPSMScore.getPep();
                	    	    } 
                	    	}
            	    	    	
            				if (qValue < 0.1) {
            					XTandemHit hit = new XTandemHit();
        						hit.setSpectrumId(spectrumId);
                     	    	  
                                // parse the FASTA header
                                Header header = Header.parseFromFASTA(protMap.getProtein(domain.getProteinKey()).getLabel());
                                String accession = header.getAccession();
                                hit.setAccession(accession);
                                
//                                hitdata.put(XtandemhitTableAccessor.EVALUE, domain.getDomainExpect());
                                hit.setScore(domain.getDomainHyperScore());                
                                hit.setPep(pep);
                                hit.setQValue(qValue);
                                hit.setType(searchEngineType);
       						
                                // Get and store the peptide.
                                hit.setPeptideSequence(sequence);
                                hit.setCharge(xTandemFile.getSupportData(spectrumNumber).getFragIonCharge());
        						
        						// Store peptide-spectrum association
                                Protein protein;
								try {
									protein = FastaLoader.getProteinFromFasta(accession);
									String description = protein.getHeader().getDescription();
	                                hit.setProteinSequence(protein.getSequence().getSequence());
	                                hit.setProteinDescription(description);
	                                
	                        		// Add protein for UniProt storing.
	                        		UniprotQueryProteins.put(accession, null);
	                                nHits++;
	                                peptides.add(sequence);
	                                SearchHits.add(hit);
								} catch (IOException e) {
									e.printStackTrace();
								}
            				}
                	    }
					}
				}
            }      
        }
        log.debug("No. of X!Tandem hits saved: " + nHits);
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
