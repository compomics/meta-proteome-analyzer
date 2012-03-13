package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.PepnovohitTableAccessor;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.parser.pepnovo.PepnovoEntry;
import de.mpa.parser.pepnovo.PepnovoFile;
import de.mpa.parser.pepnovo.PepnovoParser;
import de.mpa.parser.pepnovo.Prediction;

/**
 * This class handles the storage of pepnovo data.
 * 
 * @author Thilo Muth
 */
public class PepnovoStorager extends BasicStorager {
	
    /**
     * The PepnovoFile instance.
     */
    private PepnovoFile pepnovofile;

    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The Connection instance.
     */
    private Connection conn;
    
   
    
    /**
     * Constructor with spectrumid as parameter.
     * @param spectrumid
     */
    public PepnovoStorager(Connection conn, File file){
    	this.conn = conn;
    	this.file = file;
    }
    
    /**
     * Loads the Pepnovo data.
     */
    public void load() {
        pepnovofile = new PepnovoParser().read(file.getAbsolutePath());
    }
    
    /**
     * Stores the Pepnovo data.
     */
    public void store() throws IOException,SQLException {
        // Get all the entries
        List<PepnovoEntry> entryList = pepnovofile.getEntryList();       
        for (PepnovoEntry entry : entryList) {
        	
            List<Prediction> predList = entry.getPredictionList();
            // Get the spectrum id for the given spectrumName for the PepnovoFile     
            long spectrumid = Libspectrum.getSpectrumIdFromSpectrumTitle(entry.getSpectrumName(), false);
            for (Prediction hit : predList) {                
                HashMap<Object, Object> hitdata = new HashMap<Object, Object>(10);
                
                hitdata.put(PepnovohitTableAccessor.FK_SPECTRUMID, spectrumid);
                long peptideID = PeptideAccessor.findPeptideIDfromSequence(hit.getSequence(), conn);
                hitdata.put(PepnovohitTableAccessor.FK_PEPTIDEID, peptideID);
                hitdata.put(PepnovohitTableAccessor.INDEXID, Long.valueOf(hit.getIndex()));
                hitdata.put(PepnovohitTableAccessor.RANKSCORE, hit.getRankScore());
                hitdata.put(PepnovohitTableAccessor.PNVSCORE, hit.getPepNovoScore());
                hitdata.put(PepnovohitTableAccessor.N_GAP, hit.getnTermGap());
                hitdata.put(PepnovohitTableAccessor.C_GAP, hit.getcTermGap());
                hitdata.put(PepnovohitTableAccessor.PRECURSOR_MH, hit.getPrecursorMh());
                hitdata.put(PepnovohitTableAccessor.CHARGE, Long.valueOf(hit.getCharge()));
                
                // Create the database object.
                PepnovohitTableAccessor pepnovohit = new PepnovohitTableAccessor(hitdata);
                pepnovohit.persist(conn);
            }
            conn.commit();
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
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		log.info("Pepnovo results stored to the DB.");
	}   
}

