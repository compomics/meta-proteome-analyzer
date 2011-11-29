package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Spectrumfile;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

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
     * Long variable holds the experiment id.
     */
    private long expId;
    
    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The Connection instance.
     */
    private Connection conn;
    
    private HashMap<String, Long> filename2IdMap;
    private HashMap<String, Long> spectrumname2IdMap;
        
    private long start;
    private long end;
    
    /**
     * Constructor with instrumentid as parameter.
     *
     * @param instrumentid
     */
    public SpectrumStorager(Connection conn, File file, long expId) {
    	this.conn = conn;
    	this.file = file;
        this.expId = expId;
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
        filename2IdMap = new HashMap<String, Long>();
        spectrumname2IdMap = new HashMap<String, Long>();
        
        // Iterate over all the spectra.        
        for (int i = 0; i < spectra.size(); i++) {
            MascotGenericFile mgf = spectra.get(i);
            /* Spectrum section */
            HashMap<Object, Object> data = new HashMap<Object, Object>(10);
            // The project id.
            data.put(Libspectrum.FK_EXPERIMENTID, Long.valueOf(expId));
            // The filename.
            String fileName = mgf.getFilename();
            data.put(Libspectrum.FILENAME, fileName);
            
            String spectrumName = "";
            
            // Format the spectrumname first.
            if(mgf.getTitle() != null){
            	spectrumName = mgf.getTitle().replace('\\', '/');
            }
            
            // Remove leading whitespace
            spectrumName = spectrumName.replaceAll("^\\s+", "");
            // Remove trailing whitespace
            spectrumName = spectrumName.replaceAll("\\s+$", "");

            data.put(Libspectrum.SPECTRUMNAME, spectrumName);
            
            // The precursor mass.
            data.put(Libspectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
            
            // The charge
            data.put(Libspectrum.CHARGE, mgf.getCharge());
            
            // The total intensity.
            data.put(Libspectrum.TOTALINTENSITY, mgf.getTotalIntensity());
            
            // The highest intensity.
            data.put(Libspectrum.MAXIMUMINTENSITY, mgf.getHighestIntensity());

            // Create the database object.
            Libspectrum spectrum = new Libspectrum(data);
            spectrum.persist(conn);

            /* Spectrumfile section */

            // Get the spectrumid from the generated keys.
            Long spectrumid = (Long) spectrum.getGeneratedKeys()[0];
            
            // Mark the start and end points.
            if(i == 0) start = spectrumid;
            if(i == (spectra.size() - 1)) end = spectrumid;
            
            //Fill the maps for caching reasons
            filename2IdMap.put(fileName, spectrumid);
            spectrumname2IdMap.put(spectrumName, spectrumid);
            // Create the spectrumFile instance.
            Spectrumfile spectrumFile = new Spectrumfile();
            spectrumFile.setFk_libspectrumid(spectrumid);

            // Set the filecontent
            // Read the contents for the file into a byte[].
            byte[] fileContents = mgf.toString().getBytes();
            // Set the byte[].
            spectrumFile.setUnzippedFile(fileContents);
            // Create the database object.
            spectrumFile.persist(conn);
            conn.commit();
        }
        MapContainer.Filename2IdMap = filename2IdMap;
        MapContainer.Spectrumname2IdMap = spectrumname2IdMap;
    }
    
	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

}
