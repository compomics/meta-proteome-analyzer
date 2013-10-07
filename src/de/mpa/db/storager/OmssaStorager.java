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

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.MapContainer;
import de.mpa.db.accessor.OmssahitTableAccessor;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.util.Formatter;
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
     * The file instance.
     */
    private File qValueFile = null;
    
    
    private Map<Double, List<Double>> scoreQValueMap;

	private HashMap<String, Long> hitNumberMap;    
   
    /**
     * Constructor for storing results from a target-only search with OMSSA.
     */
    public OmssaStorager(Connection conn, File file){
    	this.conn = conn;
    	this.file = file;
    	this.searchEngineType = SearchEngineType.OMSSA;
    }
    
    /**
     * Constructor for storing results from a target-decoy search with OMSSA.
     */
    public OmssaStorager(Connection conn, File file, File qValueFile){
    	this.conn = conn;
    	this.file = file;
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
    	List<Double> qvalues;
    	// HitIndex as key, xtandemID as value.
    	hitNumberMap = new HashMap<String, Long>();
        
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
    	    	
    	    	if(spectrumTitle.contains("\\\\")){
    	    		spectrumTitle = spectrumTitle.replace("\\\\", "\\");
    	    	} 
    	    	if(spectrumTitle.contains("\\\"")){
    	    		spectrumTitle = spectrumTitle.replace("\\\"", "\"");
    	    	} 
    	    	if(MapContainer.SpectrumTitle2IdMap.get(spectrumTitle) != null) {
          	      	long searchspectrumid = MapContainer.SpectrumTitle2IdMap.get(spectrumTitle);
        	    	hitdata.put(OmssahitTableAccessor.FK_SEARCHSPECTRUMID, searchspectrumid);
        	    	
        	    	// Get the MSPepHit (for the accession)
        	    	List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
                    Iterator<MSPepHit> pepHitIterator = pepHits.iterator();                
                    MSPepHit pepHit = pepHitIterator.next();               
        	    	
        	    	hitdata.put(OmssahitTableAccessor.HITSETNUMBER, Long.valueOf(msHitSet.MSHitSet_number));
        	    	hitdata.put(OmssahitTableAccessor.EVALUE, msHit.MSHits_evalue);
        	    	hitdata.put(OmssahitTableAccessor.PVALUE, msHit.MSHits_pvalue);
        	    	hitdata.put(OmssahitTableAccessor.CHARGE, Long.valueOf(msHit.MSHits_charge));
        	    	hitdata.put(OmssahitTableAccessor.MASS, msHit.MSHits_mass);
        	    	hitdata.put(OmssahitTableAccessor.THEOMASS, msHit.MSHits_theomass);    	    	
        	    	hitdata.put(OmssahitTableAccessor.START, msHit.MSHits_pepstart);
        	    	hitdata.put(OmssahitTableAccessor.END, msHit.MSHits_pepstop);
        	    	qvalues = scoreQValueMap.get(Formatter.roundBigDecimalDouble(msHit.MSHits_evalue, 5));    	       
        	        
        	    	// If no q-value is found: Assign default values.
                    if(qvalues == null){
                    	hitdata.put(OmssahitTableAccessor.PEP, 1.0);
                        hitdata.put(OmssahitTableAccessor.QVALUE, 1.0);                	
                    } else {
                    	hitdata.put(OmssahitTableAccessor.PEP, qvalues.get(0));
                        hitdata.put(OmssahitTableAccessor.QVALUE, qvalues.get(1));
                    }
                    
                    // Create the database object.
                    if((Double)hitdata.get(OmssahitTableAccessor.QVALUE) < 0.1){
                    	
                    	// Get the peptide id
                        long peptideID = PeptideAccessor.findPeptideIDfromSequence(msHit.MSHits_pepstring, conn);
                        hitdata.put(OmssahitTableAccessor.FK_PEPTIDEID, peptideID);                
                        
                    	 // Parse the FASTA header
                        Header header = Header.parseFromFASTA(pepHit.MSPepHit_defline);
                        String accession = header.getAccession();
                        
                        Long proteinID;
                        // The Protein DAO
                        ProteinAccessor proteinDAO = ProteinAccessor.findFromAttributes(accession, conn);
                        if (proteinDAO == null) {	// Protein not yet in database
        						// Add new protein to the database
                        	 	Protein protein = MapContainer.FastaLoader.getProteinFromFasta(accession);
                        	 	String description = protein.getHeader().getDescription();
        						proteinDAO = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, protein.getSequence().getSequence(), conn);
        						MapContainer.ProteinMap.put(accession, proteinDAO.getProteinid());
        					} else {
        						proteinID = proteinDAO.getProteinid();
        						// check whether pep2prot link already exists, otherwise create new one
        						Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
        						if (pep2prot == null) {	// link doesn't exist yet
        							// Link peptide to protein.
        							pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
        						}
        				}
                        hitdata.put(OmssahitTableAccessor.FK_PROTEINID, proteinDAO.getProteinid());
                        
                    	// Create the database object.
            	    	OmssahitTableAccessor omssahit = new OmssahitTableAccessor(hitdata);
            	    	omssahit.persist(conn);		
            	    	
            	    	// Get the omssahitid
                        Long omssahitid = (Long) omssahit.getGeneratedKeys()[0];
                        hitNumberMap.put(msHitSet.MSHitSet_number + "_" + hitnumber, omssahitid);
                        hitnumber++;
                        conn.commit();
                    }
    	    	}
    	    }
        }
    }
    
    private void processQValues() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(qValueFile));
			scoreQValueMap = new HashMap<Double, List<Double>>();
			String nextLine;
			// Skip the first line
			reader.readLine();
			List<Double> qvalityList;
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(nextLine, "\t");
				List<String> tokenList = new ArrayList<String>();
				qvalityList = new ArrayList<Double>();
				
				// Iterate over all the tokens
				while (tokenizer.hasMoreTokens()) {
					tokenList.add(tokenizer.nextToken());
				}				
				double score = Formatter.roundBigDecimalDouble(Double.valueOf(tokenList.get(0)), 5);
				qvalityList.add(Double.valueOf(tokenList.get(1)));
				qvalityList.add(Double.valueOf(tokenList.get(2)));
				scoreQValueMap.put(score, qvalityList);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	  
}
