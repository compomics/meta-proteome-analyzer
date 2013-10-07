package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Cruxhit2protTableAccessor;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.parser.crux.CruxFile;
import de.mpa.parser.crux.CruxHit;
import de.mpa.parser.crux.CruxParser;

/**
 * This class stores Crux results to the DB.
 * @author T.Muth
 * 
 */
public class CruxStorager extends BasicStorager {

    /**
     * Variable holding a crux file.
     */
    private CruxFile cruxFile;
        
    private HashMap<Integer, Long> scanNumberMap;
    
    /**
     * Default constructor.
     *
     */
    public CruxStorager(Connection conn, File file) {
    	this.conn = conn;
    	this.file = file;
    	this.searchEngineType = SearchEngineType.CRUX;
    }

    /**
     * Parses and loads the Crux results file.
     *
     * @param file Crux results file.
     */
    public void load() {
    	cruxFile = new CruxParser().read(file.getAbsolutePath());
    }

    /**
     * Stores Crux results to the database.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void store() throws IOException, SQLException {
        List<CruxHit> hitList = cruxFile.getHits();
        String filename = cruxFile.getFilename();
        // Get the start of the spectrum's filename
        int firstIndex = filename.lastIndexOf("/") + 1;
        int lastIndex = filename.indexOf("_percolated");
        
        // scan number as key, cruxhitid as value.
    	scanNumberMap = new HashMap<Integer, Long>();
    	
        for (CruxHit hit : hitList) {
        	if(hit.getxCorrRank() == 1 || hit.getPercolatorRank() == 1){
                HashMap<Object, Object> hitdata = new HashMap<Object, Object>(18);
                String name = filename.substring(firstIndex, lastIndex)+ "_" + hit.getScanNumber() + ".mgf";
                
                // Get the spectrum id
                long searchspectrumid = MapContainer.FileName2IdMap.get(name);
    	    	hitdata.put(Cruxhit.FK_SEARCHSPECTRUMID, searchspectrumid);
                hitdata.put(Cruxhit.SCANNUMBER, Long.valueOf(hit.getScanNumber()));
                hitdata.put(Cruxhit.CHARGE, Long.valueOf(hit.getCharge()));
                hitdata.put(Cruxhit.NEUTRAL_MASS, hit.getNeutralMass());
                hitdata.put(Cruxhit.PEPTIDE_MASS, hit.getPeptideMass());
                hitdata.put(Cruxhit.DELTA_CN, hit.getDeltaCN());
                hitdata.put(Cruxhit.XCORR_SCORE, hit.getxCorrScore());
                hitdata.put(Cruxhit.XCORR_RANK, Long.valueOf(hit.getxCorrRank()));
                hitdata.put(Cruxhit.PERCOLATOR_SCORE, hit.getPercolatorScore());
                hitdata.put(Cruxhit.PERCOLATOR_RANK, Long.valueOf(hit.getPercolatorRank()));
                hitdata.put(Cruxhit.QVALUE, hit.getqValue());
                hitdata.put(Cruxhit.MATCHES_SPECTRUM, Long.valueOf(hit.getMatchesSpectrum()));                
                hitdata.put(Cruxhit.CLEAVAGE_TYPE, hit.getCleavageType());
                hitdata.put(Cruxhit.FLANK_AA, hit.getFlankingAA());
                
                // Create the database object.
                if((Double)hitdata.get(Cruxhit.QVALUE) < 0.1){
                	// Get the peptide id
                    long peptideID = PeptideAccessor.findPeptideIDfromSequence(hit.getPeptide(), conn);
                    hitdata.put(Cruxhit.FK_PEPTIDEID, peptideID);
                	
                    // Create the database object.
                    Cruxhit cruxhit = new Cruxhit(hitdata);
                    cruxhit.persist(conn);
                    
                    // Get the cruxhitid
                    Long cruxhitid = (Long) cruxhit.getGeneratedKeys()[0];
                    
                    // parse the header
                    StringTokenizer tokenizer = new StringTokenizer(hit.getProteinid(), ",");
                    while(tokenizer.hasMoreTokens()){
                    	String token = tokenizer.nextToken();
                    	StringTokenizer tokenizer2 = new StringTokenizer(token, "|");
                    	List<String> tokenList = new ArrayList<String>();
                    	// Iterate over all the tokens
                    	while (tokenizer2.hasMoreTokens()) {
                    		tokenList.add(tokenizer2.nextToken());
                    	}

                    	String accession = tokenList.get(1);
                        Protein protein = MapContainer.FastaLoader.getProteinFromFasta(accession);                        
                      
                        String description = protein.getHeader().getDescription();
                        
                    	ProteinAccessor proteinDAO = ProteinAccessor.findFromAttributes(accession, conn);
                    	if (proteinDAO == null) { // protein not yet in database
                    		// Add new protein to the database
                    		proteinDAO = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, protein.getSequence().getSequence(), conn);
                    		MapContainer.ProteinMap.put(accession, proteinDAO.getProteinid());
                    	} else {
                    		// check whether pep2prot link already exists,
                    		// otherwise create new one
                    		Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinDAO.getProteinid(), conn);
                    		// If no link from peptide to protein is given.
                    		if (pep2prot == null) { 
                    			// Link peptide to protein.
                    			pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinDAO.getProteinid(), conn);
                    		}
                    	}
                    	// Update the cruxhit2prot table
                        HashMap<Object, Object> cruxhitdata = new HashMap<Object, Object>(3);
                        cruxhitdata.put(Cruxhit2protTableAccessor.FK_CRUXHITID, cruxhitid);
                        cruxhitdata.put(Cruxhit2protTableAccessor.FK_PROTEINID, proteinDAO.getProteinid());
                        
                        // Cruxhit2prot: Save the protein ids separately. 
                        Cruxhit2protTableAccessor cruxhit2prot = new Cruxhit2protTableAccessor(cruxhitdata);
                        cruxhit2prot.persist(conn);
                    }
                    scanNumberMap.put(hit.getScanNumber(), cruxhitid);      
                    conn.commit();
                }
        	}   
        }
    }
}


