/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 01/12/2011
 * Time: 14:55:06
 */
package de.mpa.db.accessor;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Omssahit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class OmssahitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'omssahitid' column.
	 */
	protected long iOmssahitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_spectrumid' column.
	 */
	protected long iFk_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'hitsetnumber' column.
	 */
	protected long iHitsetnumber = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'evalue' column.
	 */
	protected Number iEvalue = null;


	/**
	 * This variable represents the contents for the 'pvalue' column.
	 */
	protected Number iPvalue = null;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'mass' column.
	 */
	protected Number iMass = null;


	/**
	 * This variable represents the contents for the 'theomass' column.
	 */
	protected Number iTheomass = null;


	/**
	 * This variable represents the contents for the 'start' column.
	 */
	protected long iStart = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'end' column.
	 */
	protected long iEnd = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'protein' column.
	 */
	protected String iProtein = null;


	/**
	 * This variable represents the contents for the 'qvalue' column.
	 */
	protected Number iQvalue = null;


	/**
	 * This variable represents the key for the 'omssahitid' column.
	 */
	public static final String OMSSAHITID = "OMSSAHITID";

	/**
	 * This variable represents the key for the 'fk_spectrumid' column.
	 */
	public static final String FK_SPECTRUMID = "FK_SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'hitsetnumber' column.
	 */
	public static final String HITSETNUMBER = "HITSETNUMBER";

	/**
	 * This variable represents the key for the 'evalue' column.
	 */
	public static final String EVALUE = "EVALUE";

	/**
	 * This variable represents the key for the 'pvalue' column.
	 */
	public static final String PVALUE = "PVALUE";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'mass' column.
	 */
	public static final String MASS = "MASS";

	/**
	 * This variable represents the key for the 'theomass' column.
	 */
	public static final String THEOMASS = "THEOMASS";

	/**
	 * This variable represents the key for the 'start' column.
	 */
	public static final String START = "START";

	/**
	 * This variable represents the key for the 'end' column.
	 */
	public static final String END = "END";

	/**
	 * This variable represents the key for the 'protein' column.
	 */
	public static final String PROTEIN = "PROTEIN";

	/**
	 * This variable represents the key for the 'qvalue' column.
	 */
	public static final String QVALUE = "QVALUE";




	/**
	 * Default constructor.
	 */
	public OmssahitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'OmssahitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public OmssahitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(OMSSAHITID)) {
			this.iOmssahitid = ((Long)aParams.get(OMSSAHITID)).longValue();
		}
		if(aParams.containsKey(FK_SPECTRUMID)) {
			this.iFk_spectrumid = ((Long)aParams.get(FK_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(HITSETNUMBER)) {
			this.iHitsetnumber = ((Long)aParams.get(HITSETNUMBER)).longValue();
		}
		if(aParams.containsKey(EVALUE)) {
			this.iEvalue = (Number)aParams.get(EVALUE);
		}
		if(aParams.containsKey(PVALUE)) {
			this.iPvalue = (Number)aParams.get(PVALUE);
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		if(aParams.containsKey(MASS)) {
			this.iMass = (Number)aParams.get(MASS);
		}
		if(aParams.containsKey(THEOMASS)) {
			this.iTheomass = (Number)aParams.get(THEOMASS);
		}
		if(aParams.containsKey(START)) {
			this.iStart = ((Long)aParams.get(START)).longValue();
		}
		if(aParams.containsKey(END)) {
			this.iEnd = ((Long)aParams.get(END)).longValue();
		}
		if(aParams.containsKey(PROTEIN)) {
			this.iProtein = (String)aParams.get(PROTEIN);
		}
		if(aParams.containsKey(QVALUE)) {
			this.iQvalue = (Number)aParams.get(QVALUE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'OmssahitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Omssahit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public OmssahitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iOmssahitid = aResultSet.getLong("omssahitid");
		this.iFk_spectrumid = aResultSet.getLong("fk_spectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iHitsetnumber = aResultSet.getLong("hitsetnumber");
		this.iEvalue = (Number)aResultSet.getObject("evalue");
		this.iPvalue = (Number)aResultSet.getObject("pvalue");
		this.iCharge = aResultSet.getLong("charge");
		this.iMass = (Number)aResultSet.getObject("mass");
		this.iTheomass = (Number)aResultSet.getObject("theomass");
		this.iStart = aResultSet.getLong("start");
		this.iEnd = aResultSet.getLong("end");
		this.iProtein = (String)aResultSet.getObject("protein");
		this.iQvalue = (Number)aResultSet.getObject("qvalue");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Omssahitid' column
	 * 
	 * @return	long	with the value for the Omssahitid column.
	 */
	public long getOmssahitid() {
		return this.iOmssahitid;
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
	 * This method returns the value for the 'Hitsetnumber' column
	 * 
	 * @return	long	with the value for the Hitsetnumber column.
	 */
	public long getHitsetnumber() {
		return this.iHitsetnumber;
	}

	/**
	 * This method returns the value for the 'Evalue' column
	 * 
	 * @return	Number	with the value for the Evalue column.
	 */
	public Number getEvalue() {
		return this.iEvalue;
	}

	/**
	 * This method returns the value for the 'Pvalue' column
	 * 
	 * @return	Number	with the value for the Pvalue column.
	 */
	public Number getPvalue() {
		return this.iPvalue;
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
	 * This method returns the value for the 'Mass' column
	 * 
	 * @return	Number	with the value for the Mass column.
	 */
	public Number getMass() {
		return this.iMass;
	}

	/**
	 * This method returns the value for the 'Theomass' column
	 * 
	 * @return	Number	with the value for the Theomass column.
	 */
	public Number getTheomass() {
		return this.iTheomass;
	}

	/**
	 * This method returns the value for the 'Start' column
	 * 
	 * @return	long	with the value for the Start column.
	 */
	public long getStart() {
		return this.iStart;
	}

	/**
	 * This method returns the value for the 'End' column
	 * 
	 * @return	long	with the value for the End column.
	 */
	public long getEnd() {
		return this.iEnd;
	}

	/**
	 * This method returns the value for the 'Protein' column
	 * 
	 * @return	String	with the value for the Protein column.
	 */
	public String getProtein() {
		return this.iProtein;
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
	 * This method sets the value for the 'Omssahitid' column
	 * 
	 * @param	aOmssahitid	long with the value for the Omssahitid column.
	 */
	public void setOmssahitid(long aOmssahitid) {
		this.iOmssahitid = aOmssahitid;
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
	 * This method sets the value for the 'Hitsetnumber' column
	 * 
	 * @param	aHitsetnumber	long with the value for the Hitsetnumber column.
	 */
	public void setHitsetnumber(long aHitsetnumber) {
		this.iHitsetnumber = aHitsetnumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Evalue' column
	 * 
	 * @param	aEvalue	Number with the value for the Evalue column.
	 */
	public void setEvalue(Number aEvalue) {
		this.iEvalue = aEvalue;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pvalue' column
	 * 
	 * @param	aPvalue	Number with the value for the Pvalue column.
	 */
	public void setPvalue(Number aPvalue) {
		this.iPvalue = aPvalue;
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
	 * This method sets the value for the 'Mass' column
	 * 
	 * @param	aMass	Number with the value for the Mass column.
	 */
	public void setMass(Number aMass) {
		this.iMass = aMass;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Theomass' column
	 * 
	 * @param	aTheomass	Number with the value for the Theomass column.
	 */
	public void setTheomass(Number aTheomass) {
		this.iTheomass = aTheomass;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Start' column
	 * 
	 * @param	aStart	long with the value for the Start column.
	 */
	public void setStart(long aStart) {
		this.iStart = aStart;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'End' column
	 * 
	 * @param	aEnd	long with the value for the End column.
	 */
	public void setEnd(long aEnd) {
		this.iEnd = aEnd;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Protein' column
	 * 
	 * @param	aProtein	String with the value for the Protein column.
	 */
	public void setProtein(String aProtein) {
		this.iProtein = aProtein;
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
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM omssahit WHERE omssahitid = ?");
		lStat.setLong(1, iOmssahitid);
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
		if(!aKeys.containsKey(OMSSAHITID)) {
			throw new IllegalArgumentException("Primary key field 'OMSSAHITID' is missing in HashMap!");
		} else {
			iOmssahitid = ((Long)aKeys.get(OMSSAHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM omssahit WHERE omssahitid = ?");
		lStat.setLong(1, iOmssahitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iOmssahitid = lRS.getLong("omssahitid");
			iFk_spectrumid = lRS.getLong("fk_spectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iHitsetnumber = lRS.getLong("hitsetnumber");
			iEvalue = (Number)lRS.getObject("evalue");
			iPvalue = (Number)lRS.getObject("pvalue");
			iCharge = lRS.getLong("charge");
			iMass = (Number)lRS.getObject("mass");
			iTheomass = (Number)lRS.getObject("theomass");
			iStart = lRS.getLong("start");
			iEnd = lRS.getLong("end");
			iProtein = (String)lRS.getObject("protein");
			iQvalue = (Number)lRS.getObject("qvalue");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'omssahit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'omssahit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from omssahit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<OmssahitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<OmssahitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<OmssahitTableAccessor>  entities = new ArrayList<OmssahitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new OmssahitTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE omssahit SET omssahitid = ?, fk_spectrumid = ?, fk_peptideid = ?, hitsetnumber = ?, evalue = ?, pvalue = ?, charge = ?, mass = ?, theomass = ?, start = ?, end = ?, protein = ?, qvalue = ? WHERE omssahitid = ?");
		lStat.setLong(1, iOmssahitid);
		lStat.setLong(2, iFk_spectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iHitsetnumber);
		lStat.setObject(5, iEvalue);
		lStat.setObject(6, iPvalue);
		lStat.setLong(7, iCharge);
		lStat.setObject(8, iMass);
		lStat.setObject(9, iTheomass);
		lStat.setLong(10, iStart);
		lStat.setLong(11, iEnd);
		lStat.setObject(12, iProtein);
		lStat.setObject(13, iQvalue);
		lStat.setLong(14, iOmssahitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO omssahit (omssahitid, fk_spectrumid, fk_peptideid, hitsetnumber, evalue, pvalue, charge, mass, theomass, start, end, protein, qvalue) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		if(iOmssahitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iOmssahitid);
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
		if(iHitsetnumber == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iHitsetnumber);
		}
		if(iEvalue == null) {
			lStat.setNull(5, 3);
		} else {
			lStat.setObject(5, iEvalue);
		}
		if(iPvalue == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iPvalue);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(7, 4);
		} else {
			lStat.setLong(7, iCharge);
		}
		if(iMass == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iMass);
		}
		if(iTheomass == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iTheomass);
		}
		if(iStart == Long.MIN_VALUE) {
			lStat.setNull(10, 4);
		} else {
			lStat.setLong(10, iStart);
		}
		if(iEnd == Long.MIN_VALUE) {
			lStat.setNull(11, 4);
		} else {
			lStat.setLong(11, iEnd);
		}
		if(iProtein == null) {
			lStat.setNull(12, 12);
		} else {
			lStat.setObject(12, iProtein);
		}
		if(iQvalue == null) {
			lStat.setNull(13, 3);
		} else {
			lStat.setObject(13, iQvalue);
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
			iOmssahitid = ((Number) iKeys[0]).longValue();
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