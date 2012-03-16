package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.compomics.util.protein.Header;

import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.parser.inspect.InspectFile;
import de.mpa.parser.inspect.InspectHit;
import de.mpa.parser.inspect.InspectParser;

/**
 * This class helps to store the results of the Inspect algorithm to the DB.
 * User: Thilo Muth
 * Date: 07.09.2010
 * Time: 16:05:56
 * 
 */
public class InspectStorager extends BasicStorager {

    /**
     * Variable holding an InspectFile.
     */
    private InspectFile inspectFile;
    
    /**
     * The file instance.
     */
    private File file;
    
    /**
     * The Connection instance.
     */
    private Connection conn;
    
    private HashMap<Long, Long> scanNumberMap;
    
    /**
     * Default constructor.
     *
     */
    public InspectStorager(Connection conn, File file) {
    	this.conn = conn;
    	this.file = file;
    }

    /**
     * Loads the InspectFile.
     *
     * @param file
     */
    public void load() {
        inspectFile = new InspectParser().read(file.getAbsolutePath());
    }

    /**
     * Stores InspectFile and its contents to the database.
     *
     * @param conn
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void store() throws IOException, SQLException {
        
        // Get the identifications.
        List<InspectHit> hitList = inspectFile.getIdentifications();
        String filename = inspectFile.getFilename();
        
        // scan number as key, inspecthitid as value.
    	scanNumberMap = new HashMap<Long, Long>();
    	
        // Get the start of the spectrum's filename
        int firstIndex = filename.lastIndexOf("/") + 1;
        int lastIndex = filename.indexOf(".mgf");
        for (InspectHit hit : hitList) {
            HashMap<Object, Object> hitdata = new HashMap<Object, Object>(24);
            
            long scannumber = hit.getScanNumber() + 1;
            String name = filename.substring(firstIndex, lastIndex)+ "_" + scannumber  + ".mgf";
            
            // Get the spectrum id
            long searchspectrumid = MapContainer.FileName2IdMap.get(name);
	    	hitdata.put(Inspecthit.FK_SEARCHSPECTRUMID, searchspectrumid);
            
            // Get the peptide id
            long peptideID = PeptideAccessor.findPeptideIDfromSequence(hit.getAnnotation(), conn);            
            hitdata.put(Inspecthit.FK_PEPTIDEID, peptideID);
            hitdata.put(Inspecthit.SCANNUMBER, Long.valueOf(hit.getScanNumber()));
            
        	Long proteinID;
        	// parse the header
            Header header = Header.parseFromFASTA(hit.getProtein());
            String accession = header.getAccession();
            String description = header.getDescription();
        	
            ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, conn);
            if (protein == null) {	// protein not yet in database
					// Add new protein to the database
					protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, conn);
				} else {
					proteinID = protein.getProteinid();
					// check whether pep2prot link already exists, otherwise create new one
					Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
					if (pep2prot == null) {	// link doesn't exist yet
						// Link peptide to protein.
						pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
					}
			}
            
            hitdata.put(Inspecthit.FK_PROTEINID, protein.getProteinid());
            hitdata.put(Inspecthit.CHARGE, Long.valueOf(hit.getCharge()));
            hitdata.put(Inspecthit.MQ_SCORE, hit.getMqScore());
            hitdata.put(Inspecthit.LENGTH, Long.valueOf(hit.getLength()));
            hitdata.put(Inspecthit.TOTAL_PRM_SCORE, hit.getTotalPRMScore());
            hitdata.put(Inspecthit.MEDIAN_PRM_SCORE, hit.getMedianPRMScore());
            hitdata.put(Inspecthit.FRACTION_Y, hit.getFractionY());
            hitdata.put(Inspecthit.FRACTION_B, hit.getFractionB());
            hitdata.put(Inspecthit.INTENSITY, hit.getIntensity());
            hitdata.put(Inspecthit.NTT, hit.getNtt());
            hitdata.put(Inspecthit.P_VALUE, hit.getpValue());
            hitdata.put(Inspecthit.F_SCORE, hit.getfScore());
            hitdata.put(Inspecthit.DELTASCORE, hit.getDeltaScore());
            hitdata.put(Inspecthit.DELTASCORE_OTHER, hit.getDeltaScoreOther());
            hitdata.put(Inspecthit.RECORDNUMBER, hit.getRecordNumber());
            hitdata.put(Inspecthit.DBFILEPOS, Long.valueOf(hit.getDbFilePos()));
            hitdata.put(Inspecthit.SPECFILEPOS, Long.valueOf(hit.getSpecFilePos()));
            hitdata.put(Inspecthit.PRECURSOR_MZ_ERROR, hit.getPrecursorMZError());

            // Create the database object.
            Inspecthit inspecthit = new Inspecthit(hitdata);
            inspecthit.persist(conn);
            
            // Get the cruxhitid
            Long inspecthitid = (Long) inspecthit.getGeneratedKeys()[0];
            scanNumberMap.put(hit.getScanNumber(), inspecthitid);   
        }
        conn.commit();
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
				conn.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		log.info("Inspect results stored to the DB.");
	} 
}

