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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.compomics.util.protein.Header;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.MapContainer;
import de.mpa.db.accessor.XtandemhitTableAccessor;
import de.mpa.db.job.scoring.ValidatedPSMScore;
import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.ProteinMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

/**
 * This class stores X!Tandem results to the DB.
 * @author T.Muth
 */
public class XTandemStorager extends BasicStorager {

    /**
     * Variable holding an xTandemFile.
     */
    private XTandemFile xTandemFile;
    
    /**
     * The q-value file.
     */
    private File qValueFile = null;
    
	/**
	 * File containing the original PSM scores.
	 */
	private File targetScoreFile;
	
	/**
	 * Mapping for the original PSM scores to the validated ones.
	 */
	private HashMap<Double, ValidatedPSMScore> validatedPSMScores;    

	private Map<String, Long> domainMap;
	
    /**
     * Constructor for storing results from a target-only search with X!Tandem.
     */
    public XTandemStorager(final Connection conn, final File file){
    	this.conn = conn;
    	this.file = file;
    	this.searchEngineType = SearchEngineType.XTANDEM;
    }
    
    /**
     * Constructor for storing results from a target-decoy search with X!Tandem.
     * @param conn Database connection
     * @param file OMSSA file
     * @param targetScoreFile File containing the original PSM scores.
     * @param qValueFile File containing the validated PSM scores.
     */
	public XTandemStorager(final Connection conn, final File file, File targetScoreFile, File qValueFile) {
		this.conn = conn;
		this.file = file;
		this.targetScoreFile = targetScoreFile;
		this.qValueFile = qValueFile;
		this.searchEngineType = SearchEngineType.XTANDEM;
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
     * Stores X!Tandem results file contents to the database.
     * @throws IOException
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
               
        // Iterate over all the spectra
        @SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();

        // Prepare everything for the peptides.
        PeptideMap pepMap = xTandemFile.getPeptideMap();
        
        // ProteinMap protMap 
        ProteinMap protMap = xTandemFile.getProteinMap();
        
        // DomainID as key, xtandemID as value.
        domainMap = new HashMap<String, Long>();

        int counter = 0;
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
						
                	    HashMap<Object, Object> hitdata = new HashMap<Object, Object>(17);
                	      
                	    // Only store if the search spectrum id is referenced.
                	    if(MapContainer.SpectrumTitle2IdMap.containsKey(spectrumTitle)) {
                	    	long searchspectrumID = MapContainer.SpectrumTitle2IdMap.get(spectrumTitle);
                	    	
                    	    ValidatedPSMScore validatedPSMScore = validatedPSMScores.get(domain.getDomainHyperScore());
            	            Double qValue = 1.0;
            	    	    if (validatedPSMScore != null) {
            	    	    	qValue = validatedPSMScore.getQvalue();
            	    	    } 
            	    	    	
            				if (qValue < 0.1) {
        						hitdata.put(XtandemhitTableAccessor.FK_SEARCHSPECTRUMID, searchspectrumID);  
                     	    	  
                                // Set the domain id  
                                String domainID = domain.getDomainID();
                                hitdata.put(XtandemhitTableAccessor.DOMAINID, domainID);
                                domain.getProteinKey();
                                // parse the FASTA header
                                Header header = Header.parseFromFASTA(protMap.getProtein(domain.getProteinKey()).getLabel());
                                String accession = header.getAccession();
                                 
                                hitdata.put(XtandemhitTableAccessor.START, Long.valueOf(domain.getDomainStart()));
                                hitdata.put(XtandemhitTableAccessor.END, Long.valueOf(domain.getDomainEnd()));
                                hitdata.put(XtandemhitTableAccessor.EVALUE, domain.getDomainExpect());
                                hitdata.put(XtandemhitTableAccessor.DELTA, domain.getDomainDeltaMh());
                                hitdata.put(XtandemhitTableAccessor.HYPERSCORE, domain.getDomainHyperScore());                
                                hitdata.put(XtandemhitTableAccessor.PRE, domain.getUpFlankSequence());
                                hitdata.put(XtandemhitTableAccessor.POST, domain.getDownFlankSequence());                
                                hitdata.put(XtandemhitTableAccessor.MISSCLEAVAGES, Long.valueOf(domain.getMissedCleavages()));
                                hitdata.put(XtandemhitTableAccessor.PEP, validatedPSMScore.getPep());
                                hitdata.put(XtandemhitTableAccessor.QVALUE, qValue);
       						
                                // Get and store the peptide.
                                long peptideID = this.storePeptide(sequence);
                     	    	hitdata.put(XtandemhitTableAccessor.FK_PEPTIDEID, peptideID);
        						
        						// Store peptide-spectrum association
        						this.storeSpec2Pep(searchspectrumID, peptideID);
                     	        Long proteinID = storeProtein(peptideID, accession);
                                hitdata.put(XtandemhitTableAccessor.FK_PROTEINID, proteinID);
                           	    XtandemhitTableAccessor xtandemhit = new XtandemhitTableAccessor(hitdata);     
                                xtandemhit.persist(conn);
                                counter++;
                                // Get the xtandemhitid
                                Long xtandemhitid = (Long) xtandemhit.getGeneratedKeys()[0];
                                domainMap.put(domainID, xtandemhitid);   
                                peptides.add(sequence);
            				}
                	    }
					}
				}
            }      
        }
        conn.commit();
        log.debug("No. of X!Tandem hits saved: " + counter);
    }
    
    /**
     * Formatting X!Tandem spectrum titles (latest X!Tandem version).
     * @param spectrumTitle Unformatted spectrum title
     * @return Formatted spectrumTitle
     */
    private String formatSpectrumTitle(String spectrumTitle) {
    	if (spectrumTitle.contains("RTINSECONDS")) {
    		spectrumTitle = spectrumTitle.substring(0, spectrumTitle.indexOf("RTINSECONDS") - 1);
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
