package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Searchspectrum;
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

    private HashMap<String, Long> title2SearchIdMap;
    private HashMap<String, Long> fileName2IdMap;
    
    private long experimentid = -1;

	private List<MascotGenericFile> spectra;
    
	private boolean redundancyCheck;
	
    /**
     * Initializes the spectrum storager. 
     *
     * @param conn The database connection.
     * @param file The spectrum search file.
     */
    public SpectrumStorager(Connection conn, File file) {
    	this.conn = conn;
    	this.file = file;
    }
    
    /**
     * Constructor with experiment id as additional parameter:
     * Used for storing the search spectra.
     * 
     * @param conn The database connection.
     * @param file The spectrum search file.
     * @param experimentid The experiment id.
     */
    public SpectrumStorager(Connection conn, File file, long experimentid, boolean redundancyCheck) {
    	this.conn = conn;
    	this.file = file;
    	this.experimentid = experimentid;
    	this.redundancyCheck = redundancyCheck;
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
        spectra = mascotGenericFileReader.getSpectrumFiles();
        
        // Init the hashmap for non-DB caching
        title2SearchIdMap = new HashMap<String, Long>();
        fileName2IdMap = new HashMap<String, Long>();
        
        // Iterate over all the spectra.        
        for (int i = 0; i < spectra.size(); i++) {
            MascotGenericFile mgf = spectra.get(i);
            
            // The filename.
            String title = mgf.getTitle().trim();

            // Remove leading whitespace
//            title = title.replaceAll("^\\s+", "");
              // Remove trailing whitespace
//            title = title.replaceAll("\\s+$", "");
            
            // TO Condition: Only add if the spectrum is not stored yet!
            Spectrum query = null;
            
            if(redundancyCheck)
            	query = Spectrum.findFromTitle(title, conn);
            
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
                TreeMap<Double, Double> peakMap = new TreeMap<Double, Double>(mgf.getPeaks());
				Double[] mzDoubles = peakMap.keySet().toArray(new Double[0]);
                data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(mzDoubles));
                
                // The intensity array
				Double[] inDoubles = peakMap.values().toArray(new Double[0]);
                data.put(Spectrum.INTARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(inDoubles));
                
                // The charge array
                TreeMap<Double, Integer> chargeMap = new TreeMap<Double, Integer>(mgf.getCharges());
                for (Double mz : peakMap.keySet()) {
					if (!chargeMap.containsKey(mz)) {
						chargeMap.put(mz, 0);
					}
				}
				Integer[] chInts = chargeMap.values().toArray(new Integer[0]);
//				Integer[] chInts = mgf.getCharges().values().toArray(new Integer[0]);
				data.put(Spectrum.CHARGEARRAY, SixtyFourBitStringSupport.encodeIntsToBase64String(chInts));
                
                // The total intensity.
                data.put(Spectrum.TOTAL_INT, mgf.getTotalIntensity());
                
                // The highest intensity.
                data.put(Spectrum.MAXIMUM_INT, mgf.getHighestIntensity());

                // Create the database object.
                Spectrum spectrum = new Spectrum(data);
                spectrum.persist(conn);

                // Get the spectrumid from the generated keys.
                Long spectrumid = (Long) spectrum.getGeneratedKeys()[0];
                
                /* Searchspectrum storager*/
                HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);

                searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
                searchData.put(Searchspectrum.FK_EXPERIMENTID, experimentid);

                Searchspectrum searchSpectrum = new Searchspectrum(searchData);
                searchSpectrum.persist(conn);

                // Get the search spectrum id from the generated keys.
                Long searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];
                // Fill the maps for caching reasons
                title2SearchIdMap.put(title, searchspectrumid);
                fileName2IdMap.put(mgf.getFilename(), searchspectrumid);
            } else {
            	long spectrumid = query.getSpectrumid();
             
                Searchspectrum searchspectrum = Searchspectrum.findFromSpectrumIDAndExperimentID(spectrumid, experimentid, conn);
                long searchspectrumid;
                
                if(searchspectrum == null){
                    /* Searchspectrum storager*/
                    HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);
                    searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
                    searchData.put(Searchspectrum.FK_EXPERIMENTID, experimentid);
                    Searchspectrum searchSpectrum = new Searchspectrum(searchData);
                    searchSpectrum.persist(conn);
                    
                    // Get the search spectrum id from the generated keys.
                    searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];
                    
                } else {
                	searchspectrumid = searchspectrum.getSearchspectrumid();
                }
                
                // Fill the maps for caching reasons
                title2SearchIdMap.put(query.getTitle(), searchspectrumid);
                fileName2IdMap.put(mgf.getFilename(), searchspectrumid);
            }
            conn.commit();
        }
        MapContainer.SpectrumTitle2IdMap = title2SearchIdMap;
        MapContainer.FileName2IdMap = fileName2IdMap;
    }

	/**
	 * Returns the parsed spectra.
	 * @return the spectra
	 */
	public List<MascotGenericFile> getSpectra() {
		return spectra;
	}

}