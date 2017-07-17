package de.mpa.db.mysql.storager;


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
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.mpa.client.Constants;
import de.mpa.db.mysql.MapContainer;
import de.mpa.db.mysql.accessor.ProteinAccessor;
import de.mpa.db.mysql.accessor.XtandemhitTableAccessor;
import de.mpa.db.mysql.job.scoring.ValidatedPSMScore;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.io.fasta.DigFASTAEntryParser;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.model.dbsearch.SearchEngineType;
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
    private File qValueFile;
    
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
    public XTandemStorager(Connection conn, File file){
    	this.conn = conn;
    	this.file = file;
        searchEngineType = SearchEngineType.XTANDEM;
    }
    
    /**
     * Constructor for storing results from a target-decoy search with X!Tandem.
     * @param conn Database connection
     * @param file OMSSA file
     * @param targetScoreFile File containing the original PSM scores.
     * @param qValueFile File containing the validated PSM scores.
     */
	public XTandemStorager(Connection conn, File file, File targetScoreFile, File qValueFile) {
		this.conn = conn;
		this.file = file;
		this.targetScoreFile = targetScoreFile;
		this.qValueFile = qValueFile;
        searchEngineType = SearchEngineType.XTANDEM;
	}

    /**
     * Parses and loads the X!Tandem results file(s).
     */
    public void load() {
        try {
            this.xTandemFile = new XTandemFile(this.file.getAbsolutePath());
        } catch (SAXException ex) {
            this.log.error("Error while parsing X!Tandem file: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
        if(this.qValueFile != null) processQValues();
    }
    
    /**
     * Stores X!Tandem results file contents to the database.
     * @throws IOException
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
    	System.out.println("Store method");
        // Iterate over all the spectra
		Iterator<Spectrum> iter = this.xTandemFile.getSpectraIterator();

        // Prepare everything for the peptides.
        PeptideMap pepMap = this.xTandemFile.getPeptideMap();
        
        
        // ProteinMap protMap 
        ProteinMap protMap = this.xTandemFile.getProteinMap();
        
        // DomainID as key, xtandemID as value.
        this.domainMap = new HashMap<String, Long>();

        int counter = 0;
        while (iter.hasNext()) {
            // Get the next spectrum.
            Spectrum spectrum = iter.next();
            int spectrumNumber = spectrum.getSpectrumNumber();
            
            String spectrumTitle = this.xTandemFile.getSupportData(spectrumNumber).getFragIonSpectrumDescription();
            spectrumTitle = this.formatSpectrumTitle(spectrumTitle).trim();
            // Get all identifications from the spectrum
            ArrayList<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
            List<String> peptides = new ArrayList<String>();
            
            // Iterate over all peptide identifications aka. domains
            for (Peptide peptide : pepList) {
            	List<Domain> domains = peptide.getDomains();
            	for (Domain domain : domains) {
                   	String sequence = domain.getDomainSequence();
					if (!peptides.contains(sequence)) {
                        peptides.add(sequence);
                	    HashMap<Object, Object> hitdata = new HashMap<Object, Object>(17);
                	    // Only store if the search spectrum id is referenced.
						if (MapContainer.SpectrumTitle2IdMap.containsKey(spectrumTitle)) {
                	    	long searchspectrumID = MapContainer.SpectrumTitle2IdMap.get(spectrumTitle);
                	        Double qValue = 1.0;
            	            Double pep = 1.0;
                	    	if (this.validatedPSMScores != null) {
                	    		ValidatedPSMScore validatedPSMScore = this.validatedPSMScores.get(domain.getDomainHyperScore());
                	    		if (validatedPSMScore != null) {
                	    	    	qValue = validatedPSMScore.getQvalue();
                	    	    	pep = validatedPSMScore.getPep();
                	    	    } 
                	    	}
            				if (qValue <= Constants.getDefaultQvalueAccepted()) {
        						hitdata.put(XtandemhitTableAccessor.FK_SEARCHSPECTRUMID, searchspectrumID);  
                     	    	  
                                // Set the domain id  
                                String domainID = domain.getDomainID();
                                hitdata.put(XtandemhitTableAccessor.DOMAINID, domainID);
                                domain.getProteinKey();
                                // parse the FASTA header
                                DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(">" + protMap.getProtein(domain.getProteinKey()).getLabel(), "");
                                String accession =  entry.getIdentifier();
                                
                                hitdata.put(XtandemhitTableAccessor.START, Long.valueOf(domain.getDomainStart()));
                                hitdata.put(XtandemhitTableAccessor.END, Long.valueOf(domain.getDomainEnd()));
                                hitdata.put(XtandemhitTableAccessor.EVALUE, domain.getDomainExpect());
                                hitdata.put(XtandemhitTableAccessor.DELTA, domain.getDomainDeltaMh());
                                hitdata.put(XtandemhitTableAccessor.HYPERSCORE, domain.getDomainHyperScore());                
                                hitdata.put(XtandemhitTableAccessor.PRE, domain.getUpFlankSequence());
                                hitdata.put(XtandemhitTableAccessor.POST, domain.getDownFlankSequence());                
                                hitdata.put(XtandemhitTableAccessor.MISSCLEAVAGES, Long.valueOf(domain.getMissedCleavages()));
                                hitdata.put(XtandemhitTableAccessor.PEP, pep);
                                hitdata.put(XtandemhitTableAccessor.QVALUE, qValue);
       						
                                // Get and store the peptide.
                                long peptideID = storePeptide(sequence);
                     	    	hitdata.put(XtandemhitTableAccessor.FK_PEPTIDEID, peptideID);
        						
                     	    	// Store peptide-spectrum association
                                storeSpec2Pep(searchspectrumID, peptideID);
        						
        						// Scan for additional protein hits
                     	    	HashSet<String> accessionSet = new HashSet<String>();
        						FastaLoader loader = FastaLoader.getInstance();
        						if (loader.getPepFile() != null) {
        							// There is a separate digested peptide file available
									accessionSet = loader.getProtHits(sequence);
									accessionSet.add(accession);
        						} else {
									accessionSet.add(accession);
								}
                     	    	
        						for (String acc : accessionSet) {
        							ProteinAccessor protSql = ProteinAccessor.findFromAttributes(acc, this.conn);
        							if (protSql != null) {
        								hitdata.put(XtandemhitTableAccessor.FK_PROTEINID, protSql.getProteinid());

        								// Finalize xtandemhit
        								XtandemhitTableAccessor xtandemhit = new XtandemhitTableAccessor(hitdata);     
        								xtandemhit.persist(this.conn);
        								counter++;

        								// Get the xtandemhitid
        								Long xtandemhitid = (Long) xtandemhit.getGeneratedKeys()[0];
                                        this.domainMap.put(domainID, xtandemhitid);
        							} else {
        								System.err.println("Protein: " + acc + " not found in the database.");
        							}
								}
            				}
                	    }
					}
				}
            }      
        }
        this.conn.commit();
        this.log.debug("No. of X!Tandem hits saved: " + counter);
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
			qValueFileReader = new BufferedReader(new FileReader(this.qValueFile));
			targetFileReader = new BufferedReader(new FileReader(this.targetScoreFile));
            this.validatedPSMScores = new HashMap<Double, ValidatedPSMScore>();
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
                this.validatedPSMScores.put(score, validatedPSMScore);
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
