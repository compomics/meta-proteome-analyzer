package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.accessor.Pepnovo;
import de.mpa.db.accessor.Pepnovofile;
import de.mpa.db.accessor.PepnovohitTableAccessor;
import de.mpa.db.accessor.Spectrum;
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
         /* XTandem section */
        HashMap<Object, Object> data = new HashMap<Object, Object>(3);

        // The filename.
        data.put(Pepnovo.FILENAME, pepnovofile.getFilename());
        
        // Create the database object.
        Pepnovo pepnovo = new Pepnovo(data);
        pepnovo.persist(conn);

        /* Pepnovofile section */
        // Get the spectrumid from the generated keys.
        Long pepnovoid = (Long) pepnovo.getGeneratedKeys()[0];

        // Create the spectrumFile instance.
        Pepnovofile pepnovofileDAO = new Pepnovofile();
        pepnovofileDAO.setL_pepnovoid1(pepnovoid);

        // Set the filecontent
        // Read the contents for the file into a byte[].
        byte[] fileContents = pepnovofile.toString().getBytes();

        // Set the byte[].
        pepnovofileDAO.setUnzippedFile(fileContents);

        // Create the database object.
        pepnovofileDAO.persist(conn);
        conn.commit();
        
        // Get all the entries
        List<PepnovoEntry> entryList = pepnovofile.getEntryList();       
        for (PepnovoEntry entry : entryList) {            
            List<Prediction> predList = entry.getPredictionList();
            // Get the spectrum id for the given spectrumName for the PepnovoFile     
            long spectrumid = Spectrum.getSpectrumIdFromSpectrumName(entry.getSpectrumName(), false);
            for (Prediction hit : predList) {                
                HashMap<Object, Object> hitdata = new HashMap<Object, Object>(11);
                hitdata.put(PepnovohitTableAccessor.L_SPECTRUMID, spectrumid);
                hitdata.put(PepnovohitTableAccessor.L_PEPNOVOID, pepnovoid);
                hitdata.put(PepnovohitTableAccessor.INDEXID, Long.valueOf(hit.getIndex()));
                hitdata.put(PepnovohitTableAccessor.RANKSCORE, hit.getRankScore());
                hitdata.put(PepnovohitTableAccessor.PNVSCORE, hit.getPepNovoScore());
                hitdata.put(PepnovohitTableAccessor.N_GAP, hit.getnTermGap());
                hitdata.put(PepnovohitTableAccessor.C_GAP, hit.getcTermGap());
                hitdata.put(PepnovohitTableAccessor.PRECURSOR_MH, hit.getPrecursorMh());
                hitdata.put(PepnovohitTableAccessor.CHARGE, Long.valueOf(hit.getCharge()));
                hitdata.put(PepnovohitTableAccessor.SEQUENCE, hit.getSequence());
                // Create the database object.
                PepnovohitTableAccessor pepnovohit = new PepnovohitTableAccessor(hitdata);
                pepnovohit.persist(conn);
            }
            conn.commit();
        }
    }
}

