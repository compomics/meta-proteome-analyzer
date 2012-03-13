/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 13/03/2012
 * Time: 13:17:04
 */
package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Cruxhit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class CruxhitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'cruxhitid' column.
	 */
	protected long iCruxhitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_spectrumid' column.
	 */
	protected long iFk_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'scannumber' column.
	 */
	protected long iScannumber = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'neutral_mass' column.
	 */
	protected Number iNeutral_mass = null;


	/**
	 * This variable represents the contents for the 'peptide_mass' column.
	 */
	protected Number iPeptide_mass = null;


	/**
	 * This variable represents the contents for the 'delta_cn' column.
	 */
	protected Number iDelta_cn = null;


	/**
	 * This variable represents the contents for the 'xcorr_score' column.
	 */
	protected Number iXcorr_score = null;


	/**
	 * This variable represents the contents for the 'xcorr_rank' column.
	 */
	protected long iXcorr_rank = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'percolator_score' column.
	 */
	protected Number iPercolator_score = null;


	/**
	 * This variable represents the contents for the 'percolator_rank' column.
	 */
	protected long iPercolator_rank = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'qvalue' column.
	 */
	protected Number iQvalue = null;


	/**
	 * This variable represents the contents for the 'matches_spectrum' column.
	 */
	protected long iMatches_spectrum = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'cleavage_type' column.
	 */
	protected String iCleavage_type = null;


	/**
	 * This variable represents the contents for the 'flank_aa' column.
	 */
	protected String iFlank_aa = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected String iModificationdate = null;


	/**
	 * This variable represents the key for the 'cruxhitid' column.
	 */
	public static final String CRUXHITID = "CRUXHITID";

	/**
	 * This variable represents the key for the 'fk_spectrumid' column.
	 */
	public static final String FK_SPECTRUMID = "FK_SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'scannumber' column.
	 */
	public static final String SCANNUMBER = "SCANNUMBER";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'neutral_mass' column.
	 */
	public static final String NEUTRAL_MASS = "NEUTRAL_MASS";

	/**
	 * This variable represents the key for the 'peptide_mass' column.
	 */
	public static final String PEPTIDE_MASS = "PEPTIDE_MASS";

	/**
	 * This variable represents the key for the 'delta_cn' column.
	 */
	public static final String DELTA_CN = "DELTA_CN";

	/**
	 * This variable represents the key for the 'xcorr_score' column.
	 */
	public static final String XCORR_SCORE = "XCORR_SCORE";

	/**
	 * This variable represents the key for the 'xcorr_rank' column.
	 */
	public static final String XCORR_RANK = "XCORR_RANK";

	/**
	 * This variable represents the key for the 'percolator_score' column.
	 */
	public static final String PERCOLATOR_SCORE = "PERCOLATOR_SCORE";

	/**
	 * This variable represents the key for the 'percolator_rank' column.
	 */
	public static final String PERCOLATOR_RANK = "PERCOLATOR_RANK";

	/**
	 * This variable represents the key for the 'qvalue' column.
	 */
	public static final String QVALUE = "QVALUE";

	/**
	 * This variable represents the key for the 'matches_spectrum' column.
	 */
	public static final String MATCHES_SPECTRUM = "MATCHES_SPECTRUM";

	/**
	 * This variable represents the key for the 'cleavage_type' column.
	 */
	public static final String CLEAVAGE_TYPE = "CLEAVAGE_TYPE";

	/**
	 * This variable represents the key for the 'flank_aa' column.
	 */
	public static final String FLANK_AA = "FLANK_AA";

	/**
	 * This variable represents the key for the 'creationdate' column.
	 */
	public static final String CREATIONDATE = "CREATIONDATE";

	/**
	 * This variable represents the key for the 'modificationdate' column.
	 */
	public static final String MODIFICATIONDATE = "MODIFICATIONDATE";




	/**
	 * Default constructor.
	 */
	public CruxhitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'CruxhitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public CruxhitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(CRUXHITID)) {
			this.iCruxhitid = ((Long)aParams.get(CRUXHITID)).longValue();
		}
		if(aParams.containsKey(FK_SPECTRUMID)) {
			this.iFk_spectrumid = ((Long)aParams.get(FK_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(SCANNUMBER)) {
			this.iScannumber = ((Long)aParams.get(SCANNUMBER)).longValue();
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		if(aParams.containsKey(NEUTRAL_MASS)) {
			this.iNeutral_mass = (Number)aParams.get(NEUTRAL_MASS);
		}
		if(aParams.containsKey(PEPTIDE_MASS)) {
			this.iPeptide_mass = (Number)aParams.get(PEPTIDE_MASS);
		}
		if(aParams.containsKey(DELTA_CN)) {
			this.iDelta_cn = (Number)aParams.get(DELTA_CN);
		}
		if(aParams.containsKey(XCORR_SCORE)) {
			this.iXcorr_score = (Number)aParams.get(XCORR_SCORE);
		}
		if(aParams.containsKey(XCORR_RANK)) {
			this.iXcorr_rank = ((Long)aParams.get(XCORR_RANK)).longValue();
		}
		if(aParams.containsKey(PERCOLATOR_SCORE)) {
			this.iPercolator_score = (Number)aParams.get(PERCOLATOR_SCORE);
		}
		if(aParams.containsKey(PERCOLATOR_RANK)) {
			this.iPercolator_rank = ((Long)aParams.get(PERCOLATOR_RANK)).longValue();
		}
		if(aParams.containsKey(QVALUE)) {
			this.iQvalue = (Number)aParams.get(QVALUE);
		}
		if(aParams.containsKey(MATCHES_SPECTRUM)) {
			this.iMatches_spectrum = ((Long)aParams.get(MATCHES_SPECTRUM)).longValue();
		}
		if(aParams.containsKey(CLEAVAGE_TYPE)) {
			this.iCleavage_type = (String)aParams.get(CLEAVAGE_TYPE);
		}
		if(aParams.containsKey(FLANK_AA)) {
			this.iFlank_aa = (String)aParams.get(FLANK_AA);
		}
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (java.sql.Timestamp)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (String)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'CruxhitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Cruxhit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public CruxhitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iCruxhitid = aResultSet.getLong("cruxhitid");
		this.iFk_spectrumid = aResultSet.getLong("fk_spectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iScannumber = aResultSet.getLong("scannumber");
		this.iCharge = aResultSet.getLong("charge");
		this.iNeutral_mass = (Number)aResultSet.getObject("neutral_mass");
		this.iPeptide_mass = (Number)aResultSet.getObject("peptide_mass");
		this.iDelta_cn = (Number)aResultSet.getObject("delta_cn");
		this.iXcorr_score = (Number)aResultSet.getObject("xcorr_score");
		this.iXcorr_rank = aResultSet.getLong("xcorr_rank");
		this.iPercolator_score = (Number)aResultSet.getObject("percolator_score");
		this.iPercolator_rank = aResultSet.getLong("percolator_rank");
		this.iQvalue = (Number)aResultSet.getObject("qvalue");
		this.iMatches_spectrum = aResultSet.getLong("matches_spectrum");
		this.iCleavage_type = (String)aResultSet.getObject("cleavage_type");
		this.iFlank_aa = (String)aResultSet.getObject("flank_aa");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (String)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Cruxhitid' column
	 * 
	 * @return	long	with the value for the Cruxhitid column.
	 */
	public long getCruxhitid() {
		return this.iCruxhitid;
	}

	/**
	 * This method returns the value for the 'Fk_spectrumid' column
	 * 
	 * @return	long	with the value for the Fk_spectrumid column.
	 */
	public long getFk_spectrumid() {
		return this.iFk_spectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return	long	with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return this.iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Scannumber' column
	 * 
	 * @return	long	with the value for the Scannumber column.
	 */
	public long getScannumber() {
		return this.iScannumber;
	}

	/**
	 * This method returns the value for the 'Charge' column
	 * 
	 * @return	long	with the value for the Charge column.
	 */
	public long getCharge() {
		return this.iCharge;
	}

	/**
	 * This method returns the value for the 'Neutral_mass' column
	 * 
	 * @return	Number	with the value for the Neutral_mass column.
	 */
	public Number getNeutral_mass() {
		return this.iNeutral_mass;
	}

	/**
	 * This method returns the value for the 'Peptide_mass' column
	 * 
	 * @return	Number	with the value for the Peptide_mass column.
	 */
	public Number getPeptide_mass() {
		return this.iPeptide_mass;
	}

	/**
	 * This method returns the value for the 'Delta_cn' column
	 * 
	 * @return	Number	with the value for the Delta_cn column.
	 */
	public Number getDelta_cn() {
		return this.iDelta_cn;
	}

	/**
	 * This method returns the value for the 'Xcorr_score' column
	 * 
	 * @return	Number	with the value for the Xcorr_score column.
	 */
	public Number getXcorr_score() {
		return this.iXcorr_score;
	}

	/**
	 * This method returns the value for the 'Xcorr_rank' column
	 * 
	 * @return	long	with the value for the Xcorr_rank column.
	 */
	public long getXcorr_rank() {
		return this.iXcorr_rank;
	}

	/**
	 * This method returns the value for the 'Percolator_score' column
	 * 
	 * @return	Number	with the value for the Percolator_score column.
	 */
	public Number getPercolator_score() {
		return this.iPercolator_score;
	}

	/**
	 * This method returns the value for the 'Percolator_rank' column
	 * 
	 * @return	long	with the value for the Percolator_rank column.
	 */
	public long getPercolator_rank() {
		return this.iPercolator_rank;
	}

	/**
	 * This method returns the value for the 'Qvalue' column
	 * 
	 * @return	Number	with the value for the Qvalue column.
	 */
	public Number getQvalue() {
		return this.iQvalue;
	}

	/**
	 * This method returns the value for the 'Matches_spectrum' column
	 * 
	 * @return	long	with the value for the Matches_spectrum column.
	 */
	public long getMatches_spectrum() {
		return this.iMatches_spectrum;
	}

	/**
	 * This method returns the value for the 'Cleavage_type' column
	 * 
	 * @return	String	with the value for the Cleavage_type column.
	 */
	public String getCleavage_type() {
		return this.iCleavage_type;
	}

	/**
	 * This method returns the value for the 'Flank_aa' column
	 * 
	 * @return	String	with the value for the Flank_aa column.
	 */
	public String getFlank_aa() {
		return this.iFlank_aa;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public java.sql.Timestamp getCreationdate() {
		return this.iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	String	with the value for the Modificationdate column.
	 */
	public String getModificationdate() {
		return this.iModificationdate;
	}

	/**
	 * This method sets the value for the 'Cruxhitid' column
	 * 
	 * @param	aCruxhitid	long with the value for the Cruxhitid column.
	 */
	public void setCruxhitid(long aCruxhitid) {
		this.iCruxhitid = aCruxhitid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_spectrumid' column
	 * 
	 * @param	aFk_spectrumid	long with the value for the Fk_spectrumid column.
	 */
	public void setFk_spectrumid(long aFk_spectrumid) {
		this.iFk_spectrumid = aFk_spectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param	aFk_peptideid	long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
		this.iFk_peptideid = aFk_peptideid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Scannumber' column
	 * 
	 * @param	aScannumber	long with the value for the Scannumber column.
	 */
	public void setScannumber(long aScannumber) {
		this.iScannumber = aScannumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Charge' column
	 * 
	 * @param	aCharge	long with the value for the Charge column.
	 */
	public void setCharge(long aCharge) {
		this.iCharge = aCharge;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Neutral_mass' column
	 * 
	 * @param	aNeutral_mass	Number with the value for the Neutral_mass column.
	 */
	public void setNeutral_mass(Number aNeutral_mass) {
		this.iNeutral_mass = aNeutral_mass;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Peptide_mass' column
	 * 
	 * @param	aPeptide_mass	Number with the value for the Peptide_mass column.
	 */
	public void setPeptide_mass(Number aPeptide_mass) {
		this.iPeptide_mass = aPeptide_mass;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Delta_cn' column
	 * 
	 * @param	aDelta_cn	Number with the value for the Delta_cn column.
	 */
	public void setDelta_cn(Number aDelta_cn) {
		this.iDelta_cn = aDelta_cn;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Xcorr_score' column
	 * 
	 * @param	aXcorr_score	Number with the value for the Xcorr_score column.
	 */
	public void setXcorr_score(Number aXcorr_score) {
		this.iXcorr_score = aXcorr_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Xcorr_rank' column
	 * 
	 * @param	aXcorr_rank	long with the value for the Xcorr_rank column.
	 */
	public void setXcorr_rank(long aXcorr_rank) {
		this.iXcorr_rank = aXcorr_rank;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Percolator_score' column
	 * 
	 * @param	aPercolator_score	Number with the value for the Percolator_score column.
	 */
	public void setPercolator_score(Number aPercolator_score) {
		this.iPercolator_score = aPercolator_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Percolator_rank' column
	 * 
	 * @param	aPercolator_rank	long with the value for the Percolator_rank column.
	 */
	public void setPercolator_rank(long aPercolator_rank) {
		this.iPercolator_rank = aPercolator_rank;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Qvalue' column
	 * 
	 * @param	aQvalue	Number with the value for the Qvalue column.
	 */
	public void setQvalue(Number aQvalue) {
		this.iQvalue = aQvalue;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Matches_spectrum' column
	 * 
	 * @param	aMatches_spectrum	long with the value for the Matches_spectrum column.
	 */
	public void setMatches_spectrum(long aMatches_spectrum) {
		this.iMatches_spectrum = aMatches_spectrum;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Cleavage_type' column
	 * 
	 * @param	aCleavage_type	String with the value for the Cleavage_type column.
	 */
	public void setCleavage_type(String aCleavage_type) {
		this.iCleavage_type = aCleavage_type;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Flank_aa' column
	 * 
	 * @param	aFlank_aa	String with the value for the Flank_aa column.
	 */
	public void setFlank_aa(String aFlank_aa) {
		this.iFlank_aa = aFlank_aa;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(java.sql.Timestamp aCreationdate) {
		this.iCreationdate = aCreationdate;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	String with the value for the Modificationdate column.
	 */
	public void setModificationdate(String aModificationdate) {
		this.iModificationdate = aModificationdate;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM cruxhit WHERE cruxhitid = ?");
		lStat.setLong(1, iCruxhitid);
		int result = lStat.executeUpdate();
		lStat.close();
		return result;
	}


	/**
	 * This method allows the caller to read data for this
	 * object from a persistent store based on the specified keys.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(CRUXHITID)) {
			throw new IllegalArgumentException("Primary key field 'CRUXHITID' is missing in HashMap!");
		} else {
			iCruxhitid = ((Long)aKeys.get(CRUXHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM cruxhit WHERE cruxhitid = ?");
		lStat.setLong(1, iCruxhitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iCruxhitid = lRS.getLong("cruxhitid");
			iFk_spectrumid = lRS.getLong("fk_spectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iScannumber = lRS.getLong("scannumber");
			iCharge = lRS.getLong("charge");
			iNeutral_mass = (Number)lRS.getObject("neutral_mass");
			iPeptide_mass = (Number)lRS.getObject("peptide_mass");
			iDelta_cn = (Number)lRS.getObject("delta_cn");
			iXcorr_score = (Number)lRS.getObject("xcorr_score");
			iXcorr_rank = lRS.getLong("xcorr_rank");
			iPercolator_score = (Number)lRS.getObject("percolator_score");
			iPercolator_rank = lRS.getLong("percolator_rank");
			iQvalue = (Number)lRS.getObject("qvalue");
			iMatches_spectrum = lRS.getLong("matches_spectrum");
			iCleavage_type = (String)lRS.getObject("cleavage_type");
			iFlank_aa = (String)lRS.getObject("flank_aa");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (String)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'cruxhit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'cruxhit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from cruxhit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<CruxhitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<CruxhitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<CruxhitTableAccessor>  entities = new ArrayList<CruxhitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new CruxhitTableAccessor(rs));
		}
		rs.close();
		stat.close();
		return entities;
	}



	/**
	 * This method allows the caller to update the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int update(Connection aConn) throws SQLException {
		if(!this.iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE cruxhit SET cruxhitid = ?, fk_spectrumid = ?, fk_peptideid = ?, scannumber = ?, charge = ?, neutral_mass = ?, peptide_mass = ?, delta_cn = ?, xcorr_score = ?, xcorr_rank = ?, percolator_score = ?, percolator_rank = ?, qvalue = ?, matches_spectrum = ?, cleavage_type = ?, flank_aa = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE cruxhitid = ?");
		lStat.setLong(1, iCruxhitid);
		lStat.setLong(2, iFk_spectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iScannumber);
		lStat.setLong(5, iCharge);
		lStat.setObject(6, iNeutral_mass);
		lStat.setObject(7, iPeptide_mass);
		lStat.setObject(8, iDelta_cn);
		lStat.setObject(9, iXcorr_score);
		lStat.setLong(10, iXcorr_rank);
		lStat.setObject(11, iPercolator_score);
		lStat.setLong(12, iPercolator_rank);
		lStat.setObject(13, iQvalue);
		lStat.setLong(14, iMatches_spectrum);
		lStat.setObject(15, iCleavage_type);
		lStat.setObject(16, iFlank_aa);
		lStat.setObject(17, iCreationdate);
		lStat.setLong(18, iCruxhitid);
		int result = lStat.executeUpdate();
		lStat.close();
		this.iUpdated = false;
		return result;
	}


	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO cruxhit (cruxhitid, fk_spectrumid, fk_peptideid, scannumber, charge, neutral_mass, peptide_mass, delta_cn, xcorr_score, xcorr_rank, percolator_score, percolator_rank, qvalue, matches_spectrum, cleavage_type, flank_aa, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iCruxhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iCruxhitid);
		}
		if(iFk_spectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_spectrumid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_peptideid);
		}
		if(iScannumber == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iScannumber);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, iCharge);
		}
		if(iNeutral_mass == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iNeutral_mass);
		}
		if(iPeptide_mass == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, iPeptide_mass);
		}
		if(iDelta_cn == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iDelta_cn);
		}
		if(iXcorr_score == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iXcorr_score);
		}
		if(iXcorr_rank == Long.MIN_VALUE) {
			lStat.setNull(10, 4);
		} else {
			lStat.setLong(10, iXcorr_rank);
		}
		if(iPercolator_score == null) {
			lStat.setNull(11, 3);
		} else {
			lStat.setObject(11, iPercolator_score);
		}
		if(iPercolator_rank == Long.MIN_VALUE) {
			lStat.setNull(12, 4);
		} else {
			lStat.setLong(12, iPercolator_rank);
		}
		if(iQvalue == null) {
			lStat.setNull(13, 3);
		} else {
			lStat.setObject(13, iQvalue);
		}
		if(iMatches_spectrum == Long.MIN_VALUE) {
			lStat.setNull(14, 4);
		} else {
			lStat.setLong(14, iMatches_spectrum);
		}
		if(iCleavage_type == null) {
			lStat.setNull(15, 12);
		} else {
			lStat.setObject(15, iCleavage_type);
		}
		if(iFlank_aa == null) {
			lStat.setNull(16, 12);
		} else {
			lStat.setObject(16, iFlank_aa);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
		iKeys = new Object[colCount];
		while(lrsKeys.next()) {
			for(int i=0;i<iKeys.length;i++) {
				iKeys[i] = lrsKeys.getObject(i+1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if(iKeys != null && iKeys.length == 1 && iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
			iCruxhitid = ((Number) iKeys[0]).longValue();
		}
		this.iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if 
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return	Object[]	with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return this.iKeys;
	}

}