package de.mpa.db.mysql.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import de.mpa.client.Client;
import de.mpa.db.mysql.MapContainer;
import de.mpa.db.mysql.accessor.Searchspectrum;
import de.mpa.db.mysql.accessor.Spectrum;
import de.mpa.db.mysql.accessor.SpectrumTableAccessor;
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
    private final long experimentid;

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
            this.reader = new MascotGenericFileReader(this.file);
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
    	
    	// TODO: batchinserts and paging for redundancy check, title-hash
    	
        // Get all spectra from the reader.
        this.spectra = this.reader.getSpectrumFiles();
        
        // reopen connection
        if (this.conn.isClosed()) {
            this.conn = Client.getInstance().getConnection();
        }
        // Init cache maps.
        this.title2SearchIdMap = new HashMap<String, Long>();
        this.fileName2IdMap = new HashMap<String, Long>();
        
        // commit in batches of "commit_size"-length
        int commit_size = 100;
        int commit_counter = 0;
        
        // Iterate over all spectra.
        for (MascotGenericFile mgf : this.spectra) {
            // The filename, remove leading and trailing whitespace.
            String title = mgf.getTitle();
            long titlehash = SpectrumTableAccessor.createTitleHash(title, mgf.getPrecursorMZ(), mgf.getIntensity());
            /* New spectrum section */
            // generate a new query 
            Spectrum query = this.generateQuery(mgf);
            HashMap<Object, Object> data = new HashMap<Object, Object>(13);

            // The spectrum title
            data.put(Spectrum.TITLE, title);

            // The title hash (for quick identification and unique insert)
            data.put(Spectrum.TITLEHASH, titlehash);

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

            Long spectrumid = null;
            // just insert, if spectrum already exists catch exception and grab spectrumid
            try {
            	// Create the database object.
            	query = new Spectrum(data);
            	query.persist(this.conn);
            	// Get the spectrumid from the generated keys.
            	spectrumid = (Long) query.getGeneratedKeys()[0];
            } catch (MySQLIntegrityConstraintViolationException errConstraint) {
            	spectrumid = Spectrum.findFromTitleQuicker(titlehash, conn).getSpectrumid();
            } catch (SQLException errSql){
            	errSql.printStackTrace();
            }

            HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);
            searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
            searchData.put(Searchspectrum.FK_EXPERIMENTID, this.experimentid);
            Searchspectrum searchSpectrum = new Searchspectrum(searchData);
            searchSpectrum.persist(this.conn);
            Long searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];

            // Fill the cache maps
            this.title2SearchIdMap.put(query.getTitle().trim(), searchspectrumid);
            this.fileName2IdMap.put(mgf.getFilename(), searchspectrumid);
        	// commit if count reaches batch-value
            commit_counter++;
            if ((commit_counter % commit_size) == 0) {
            	this.conn.commit();
            }
        }
        // commit the rest
        this.conn.commit();
        // prepare maps for later 
        MapContainer.SpectrumTitle2IdMap = this.title2SearchIdMap;
        this.log.debug("No. of spectra: " + this.title2SearchIdMap.size());
        MapContainer.FileName2IdMap = this.fileName2IdMap;
        this.reader.close();
    }

	/**
	 * Returns the parsed spectra.
	 * @return the spectra
	 */
	public List<MascotGenericFile> getSpectra() {
		return this.spectra;
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
        load();
		try {
            store();
		} catch (Exception e) {
			try {
                this.conn.rollback();
			} catch (SQLException e1) {
                this.log.error("Could not perform rollback. Error message: " + e.getMessage());
				e1.printStackTrace();
			}
            this.log.error("Spectrum storing error message: " + e.getMessage());
			e.printStackTrace();
		}
        this.log.info("Spectra stored to the DB.");
	}

}
