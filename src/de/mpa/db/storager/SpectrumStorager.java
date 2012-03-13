package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Spectrum;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.SixtyFourBitStringSupport;

/**
 * This class handles the storage of the spectrum files.
 * 
 * @author Thilo Muth
 * 
 */
public class SpectrumStorager extends BasicStorager {
	
    /**
     * Variable holding a MGF-file.
     */
    private MascotGenericFileReader mascotGenericFileReader;
    
    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The Connection instance.
     */
    private Connection conn;

    private HashMap<String, Long> spectrumTitle2IdMap;
    private HashMap<String, Long> fileName2IdMap;
        
    private long start;
    private long end;
    
    /**
     * Constructor with instrumentid as parameter.
     *
     * @param instrumentid
     */
    public SpectrumStorager(Connection conn, File file) {
    	this.conn = conn;
    	this.file = file;
    }

    /**
     * Loads the MGF-file.
     *
     * @param file
     */
    public void load() {
        try {
            mascotGenericFileReader = new MascotGenericFileReader(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the MGF-file to the database.
     *
     * @param conn
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
        // Get all the spectra from the MGF-Reader
        List<MascotGenericFile> spectra = mascotGenericFileReader.getSpectrumFiles();
        
        // Init the hashmap for non-DB caching
        spectrumTitle2IdMap = new HashMap<String, Long>();
        
        // Iterate over all the spectra.        
        for (int i = 0; i < spectra.size(); i++) {
            MascotGenericFile mgf = spectra.get(i);
            
            // The filename.
            String title = mgf.getTitle();
            // Format the spectrum title first.
            if (title != null){
            	title = title.replace('\\', '/');
            }
            // Remove leading whitespace
            title = title.replaceAll("^\\s+", "");
            // Remove trailing whitespace
            title = title.replaceAll("\\s+$", "");
            
            // Condition: Only add if the spectrum is not stored yet!
            Spectrum query = Spectrum.findFromTitle(title, conn);
            if (query == null) {
            	
	            /* Spectrum section */
	            HashMap<Object, Object> data = new HashMap<Object, Object>(12);
            
	            // The spectrum title
                data.put(Spectrum.TITLE, title);
                
                // The precursor mass.
                data.put(Spectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
                
                // The precursor intensity
                data.put(Spectrum.PRECURSOR_INT, mgf.getIntensity());
                
                // The precursor charge
                data.put(Spectrum.PRECURSOR_CHARGE, Long.valueOf(mgf.getCharge()));
                
                // The m/z array
				Double[] mzDoubles = mgf.getPeaks().keySet().toArray(new Double[0]);
                data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(mzDoubles));
                
                // The intensity array
				Double[] inDoubles = mgf.getPeaks().values().toArray(new Double[0]);
                data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(inDoubles));
                
                // The charge array
				Integer[] chInts = mgf.getCharges().values().toArray(new Integer[0]);
                data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeIntsToBase64String(chInts));
                
                // The total intensity.
                data.put(Spectrum.TOTAL_INT, mgf.getTotalIntensity());
                
                // The highest intensity.
                data.put(Spectrum.MAXIMUM_INT, mgf.getHighestIntensity());
                
                // The creation and modification dates
                long now = Calendar.getInstance().getTime().getTime();
                data.put(Spectrum.CREATIONTIME, new java.sql.Timestamp(now));
                data.put(Spectrum.MODIFICATIONTIME, new java.sql.Timestamp(now));

                // Create the database object.
                Spectrum spectrum = new Spectrum(data);
                spectrum.persist(conn);

                // Get the spectrumid from the generated keys.
                Long spectrumid = (Long) spectrum.getGeneratedKeys()[0];
                
                // Mark the start and end points.
                if(i == 0) start = spectrumid;
                if(i == (spectra.size() - 1)) end = spectrumid;
                
                // Fill the maps for caching reasons
                spectrumTitle2IdMap.put(title, spectrumid);
                fileName2IdMap.put(mgf.getFilename(), spectrumid);
                conn.commit();
            } else {
                // Fill the maps for caching reasons
                spectrumTitle2IdMap.put(query.getTitle(), query.getSpectrumid());
                fileName2IdMap.put(mgf.getFilename(), query.getSpectrumid());
            }
 
        }
        MapContainer.SpectrumTitle2IdMap = spectrumTitle2IdMap;
        MapContainer.FileName2IdMap = fileName2IdMap;
    }
    
	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

}
