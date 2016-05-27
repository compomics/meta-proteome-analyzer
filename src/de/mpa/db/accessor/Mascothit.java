package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.client.model.dbsearch.SearchEngineType;

/**
 * This class holds the results of a Mascot hit
 * @author F. Kohrs and R. Heyer
 */
public class Mascothit extends MascothitTableAccessor implements SearchHit {
	
	private static final long serialVersionUID = 1L;
	
	// The AS sequence of the search hit
	private String sequence;
	
	// The accession of the search hit.
	private String accession;
	
	// The title of the search hit
	private String title;
	
	/**
	 * Default constructor for parsing of mascot dat.files
	 */
	public Mascothit(){
	}
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Mascothit(ResultSet aRS) throws SQLException {
        super(aRS); 
        this.sequence = (String) aRS.getObject("sequence");
        this.accession = (String) aRS.getObject("accession");
    }
    
	public Mascothit(HashMap<Object, Object> hitdata) {
		super(hitdata);  
	}

	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Mascothit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Mascothit> temp = new ArrayList<Mascothit>();
    	//TODO check cast
    	PreparedStatement ps =  aConn.prepareStatement("select i.*, p.sequence, pr.accession from mascothit i, peptide p, protein pr where i.fk_peptideid = p.peptideid and i.fk_proteinid = pr.proteinid and i.fk_searchspectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Mascothit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param experimentID long with the experimentID.
     * @param conn DB connection.
     * @return List of Inspect hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Mascothit> getHitsFromExperimentID(long experimentID, Connection conn) throws SQLException {
    	List<Mascothit> temp = new ArrayList<Mascothit>();
    	PreparedStatement ps = conn.prepareStatement("select i.*, p.sequence, pr.accession " +
    												 "from mascothit i, searchspectrum s, peptide p, protein pr " +
    												 "where i.fk_peptideid = p.peptideid " +
    												 "and i.fk_proteinid = pr.proteinid " +
    												 "and s.searchspectrumid = i.fk_searchspectrumid " +
    												 "and s.fk_experimentid = ?");
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Mascothit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
	/**
     * This method will find the hits from the current connection, based on the specified proteinid
     *
     * @param proteinid long with the proteinid.
     * @param conn DB connection.
     * @return List of Mascot hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<MascothitTableAccessor> getHitsFromProteinID(long proteinid, Connection conn) throws SQLException {
    	List<MascothitTableAccessor> temp = new ArrayList<MascothitTableAccessor>();
    	PreparedStatement ps = conn.prepareStatement("select m.* from mascothit m where m.fk_proteinid = ?");
        ps.setLong(1, proteinid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new MascothitTableAccessor(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
	/**
     * This method create a new mascothit from the data of another mascothit
     *
     * @param proteinid 	the proteinid from the new protein (why we copy in the first place)
     * @param hit  	the old mascothit that is copied
     * @param conn DB connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static void copymascothit(long proteinid, MascothitTableAccessor hit, Connection conn) throws SQLException {
    		@SuppressWarnings("unused")
			long mascotHitID = 0;
    		HashMap<Object, Object> data = new HashMap<Object, Object>(10);
    		data.put(Mascothit.FK_SEARCHSPECTRUMID, hit.getFk_searchspectrumid());
    		data.put(Mascothit.FK_PEPTIDEID, hit.getFk_peptideid());
    		data.put(Mascothit.FK_PROTEINID, proteinid);
    		data.put(Mascothit.CHARGE, hit.getCharge());
    		data.put(Mascothit.IONSCORE, hit.getIonscore());
    		data.put(Mascothit.EVALUE, hit.getEvalue());
    		data.put(Mascothit.DELTA, hit.getDelta());
    		// Save spectrum in database
    		MascothitTableAccessor mascotHit	 = new MascothitTableAccessor(data);
    		mascotHit.persist(conn);
    		mascotHitID = (Long) mascotHit.getGeneratedKeys()[0];
    		return;
    }
    
	@Override
	public SearchEngineType getType() {
		return SearchEngineType.MASCOT;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchHit) {
			SearchHit hit = ((SearchHit) obj);
			if (hit.getType() == this.getType()) {
				if (hit.getFk_searchspectrumid() == this.getFk_searchspectrumid()) {
					if (hit.getFk_peptideid() == this.getFk_peptideid()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets the title of the Mascot query
	 * @return The title of the Mascot query
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the Mascot query
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Number getQvalue() {
		// Mascot has not q-value calculation => Default q-value is set to 0.
		return 0.0;
	}
	
	@Override
	public double getScore() {		
		return iIonscore.doubleValue();
	}
}
