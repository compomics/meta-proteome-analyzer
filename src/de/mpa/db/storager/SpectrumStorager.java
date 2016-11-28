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
 * @author T.Muth
 * 
 */
public class SpectrumStorager extends BasicStorager {
	
    /**
     * The spectrum file reader instance.
     */
    private MascotGenericFileReader reader;

    /**
     * Map to link spectrum titles to their database searchspectrum ID.
     */
    private HashMap<String, Long> title2SearchIdMap;
    
    /**
     * Map to link spectrum filenames to their database searchspectrum ID.
     */
    private HashMap<String, Long> fileName2IdMap;
    
    /**
     * The experiment's ID.
     */
    private long experimentid;

    /**
     * The list of spectra.
     */
	private List<MascotGenericFile> spectra;

    /**
     * Constructor with experiment id as additional parameter:
     * Used for storing the search spectra.
     * 
     * @param conn The database connection.
     * @param file The spectrum search file.
     * @param experimentid The experiment id.
     */
    public SpectrumStorager(Connection conn, File file, long experimentid) {
    	this.conn = conn;
    	this.file = file;
    	this.experimentid = experimentid;
    }

    /**
     * Loads the MGF-file.
     *
     * @param file
     */
    public void load() {
        try {
            reader = new MascotGenericFileReader(file);
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
        // Get all spectra from the reader.
        spectra = reader.getSpectrumFiles();
        
        // Init cache maps.
        title2SearchIdMap = new HashMap<String, Long>();
        fileName2IdMap = new HashMap<String, Long>();
        
        // Iterate over all spectra.
        for (MascotGenericFile mgf : spectra) {
            
            // The filename, remove leading and trailing whitespace.
            String title = mgf.getTitle();
            Spectrum query =  Spectrum.findFromTitle(title, conn);
            Long searchspectrumid;
			if (query == null) {
				/* New spectrum section */
				// generate a new query 
				query = generateQuery(mgf);
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
				data.put(Spectrum.CHARGEARRAY, SixtyFourBitStringSupport.encodeIntsToBase64String(chInts));
                
				// TODO: insert retention times
				
                // The total intensity.
                data.put(Spectrum.TOTAL_INT, 0.0); //mgf.getTotalIntensity());
                
                // The highest intensity.
                data.put(Spectrum.MAXIMUM_INT, 0.0); //mgf.getHighestIntensity());

                // Create the database object.
                query = new Spectrum(data);
                query.persist(conn);

                // Get the spectrumid from the generated keys.
                Long spectrumid = (Long) query.getGeneratedKeys()[0];
                
                /* Searchspectrum storager*/
                HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);

                searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
                searchData.put(Searchspectrum.FK_EXPERIMENTID, experimentid);

                Searchspectrum searchSpectrum = new Searchspectrum(searchData);
                searchSpectrum.persist(conn);

                // Get the search spectrum id from the generated keys.
                searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];
                
            } else {
            	/* Redundant spectrum section */
            	long spectrumid = query.getSpectrumid();
            	
            	// Find possibly already existing search spectrum for this experiment
                Searchspectrum searchspectrum = Searchspectrum.findFromSpectrumIDAndExperimentID(spectrumid, experimentid, conn);
                
				if (searchspectrum == null) {
                    /* Searchspectrum storager*/
					// No search spectrum exists for this query, generate a new one
                    HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);
                    searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
                    searchData.put(Searchspectrum.FK_EXPERIMENTID, experimentid);
                    Searchspectrum searchSpectrum = new Searchspectrum(searchData);
                    searchSpectrum.persist(conn);
                    
                    // Get the search spectrum id from the generated keys.
                    searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];
                    
                } else {
                	// A search spectrum already exists, grab its ID
                	searchspectrumid = searchspectrum.getSearchspectrumid();
                }
                
            }
            // Fill the cache maps
            title2SearchIdMap.put(query.getTitle(), searchspectrumid);
            fileName2IdMap.put(mgf.getFilename(), searchspectrumid);
            
            conn.commit();			
        }
        
        MapContainer.SpectrumTitle2IdMap = title2SearchIdMap;
        log.debug("No. of spectra: " + title2SearchIdMap.size());
        MapContainer.FileName2IdMap = fileName2IdMap;
        
        reader.close();
    }

	/**
	 * Returns the parsed spectra.
	 * @return the spectra
	 */
	public List<MascotGenericFile> getSpectra() {
		return spectra;
	}

	/**
	 * Helper method to generate a Spectrum instance from information stored
	 * inside the specified spectrum file instance.
	 * @param mgf the spectrum file instance
	 * @return the generated query Spectrum or <code>null</code> if the spectrum 
	 * file does not contain a spectrum ID
	 */
	private Spectrum generateQuery(MascotGenericFile mgf) {
		Long spectrumID = mgf.getSpectrumID();
		if (spectrumID != null) {
			HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put(Spectrum.SPECTRUMID, spectrumID);
			params.put(Spectrum.TITLE, mgf.getTitle());
			return new Spectrum(params);
		}
		return null;
	}
	
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("Could not perform rollback. Error message: " + e.getMessage());
				e1.printStackTrace();
			}
			log.error("Spectrum storing error message: " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Spectra stored to the DB.");
	}

}
