package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import de.mpa.db.accessor.CruxhitTableAccessor;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.parser.crux.CruxFile;
import de.mpa.parser.crux.CruxHit;
import de.mpa.parser.crux.CruxParser;

/**
 * This class helps to store the results of the Crux algorithm to the DB.
 * User: Thilo Muth
 * Date: 15.10.2010
 * Time: 16:05:56
 * 
 */
public class CruxStorager implements Storager {

    /**
     * Variable holding a crux file.
     */
    private CruxFile cruxFile;
    
    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The Connection instance.
     */
    private Connection conn;
    
    private HashMap<Integer, Long> scanNumberMap;
    
    /**
     * Default constructor.
     *
     */
    public CruxStorager(Connection conn, File file) {
    	this.conn = conn;
    	this.file = file;
    }

    /**
     * Loads the MsgfFile.
     *
     * @param file
     */
    public void load() {
    	cruxFile = new CruxParser().read(file.getAbsolutePath());
    }

    /**
     * Stores MsgfFile and its contents to the database.
     *
     * @param conn
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
                long spectrumID = Searchspectrum.getSpectrumIdFromFileName(name);            
                hitdata.put(CruxhitTableAccessor.FK_SPECTRUMID, spectrumID);
                
                // Get the peptide id
                long peptideID = PeptideAccessor.findPeptideIDfromSequence(hit.getPeptide(), conn);
                hitdata.put(CruxhitTableAccessor.FK_PEPTIDEID, peptideID);
                
                hitdata.put(CruxhitTableAccessor.SCANNUMBER, Long.valueOf(hit.getScanNumber()));
                hitdata.put(CruxhitTableAccessor.CHARGE, Long.valueOf(hit.getCharge()));
                hitdata.put(CruxhitTableAccessor.NEUTRAL_MASS, hit.getNeutralMass());
                hitdata.put(CruxhitTableAccessor.PEPTIDE_MASS, hit.getPeptideMass());
                hitdata.put(CruxhitTableAccessor.DELTA_CN, hit.getDeltaCN());
                hitdata.put(CruxhitTableAccessor.XCORR_SCORE, hit.getxCorrScore());
                hitdata.put(CruxhitTableAccessor.XCORR_RANK, Long.valueOf(hit.getxCorrRank()));
                hitdata.put(CruxhitTableAccessor.PERCOLATOR_SCORE, hit.getPercolatorScore());
                hitdata.put(CruxhitTableAccessor.PERCOLATOR_RANK, Long.valueOf(hit.getPercolatorRank()));
                hitdata.put(CruxhitTableAccessor.QVALUE, hit.getqValue());
                hitdata.put(CruxhitTableAccessor.MATCHES_SPECTRUM, Long.valueOf(hit.getMatchesSpectrum()));                
                hitdata.put(CruxhitTableAccessor.CLEAVAGE_TYPE, hit.getCleavageType());
                
                Long proteinID;
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
                    
                    ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, conn);
                    if (protein == null) {	// protein not yet in database
        					// Add new protein to the database
        					protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, null, conn);
        				} else {
        					proteinID = protein.getProteinid();
        					// check whether pep2prot link already exists, otherwise create new one
        					Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
        					if (pep2prot == null) {	// link doesn't exist yet
        						// Link peptide to protein.
        						pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
        					}
        			}
                }
                
                hitdata.put(CruxhitTableAccessor.FLANK_AA, hit.getFlankingAA());
                
                // Create the database object.
                if((Double)hitdata.get(CruxhitTableAccessor.QVALUE) < 0.1){
                	// Create the database object.
                    CruxhitTableAccessor cruxhit = new CruxhitTableAccessor(hitdata);
                    cruxhit.persist(conn);
                    
                    // Get the cruxhitid
                    Long cruxhitid = (Long) cruxhit.getGeneratedKeys()[0];
                    scanNumberMap.put(hit.getScanNumber(), cruxhitid);      
                    conn.commit();
                }
                          
        	}   
        }
    }
    
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	} 
}


