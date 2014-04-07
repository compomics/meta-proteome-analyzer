/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 13/02/2013
 * Time: 13:30:48
 */
package de.mpa.db.accessor;

import java.io.Serializable;
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
 * This class is a generated accessor for the Mascothit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class MascothitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'mascothitid' column.
	 */
	protected long iMascothitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_searchspectrumid' column.
	 */
	protected long iFk_searchspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_proteinid' column.
	 */
	protected long iFk_proteinid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'ionscore' column.
	 */
	protected Number iIonscore = null;


	/**
	 * This variable represents the contents for the 'evalue' column.
	 */
	protected Number iEvalue = null;


	/**
	 * This variable represents the contents for the 'delta' column.
	 */
	protected Number iDelta = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'mascothitid' column.
	 */
	public static final String MASCOTHITID = "MASCOTHITID";

	/**
	 * This variable represents the key for the 'fk_searchspectrumid' column.
	 */
	public static final String FK_SEARCHSPECTRUMID = "FK_SEARCHSPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'fk_proteinid' column.
	 */
	public static final String FK_PROTEINID = "FK_PROTEINID";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'ionscore' column.
	 */
	public static final String IONSCORE = "IONSCORE";

	/**
	 * This variable represents the key for the 'evalue' column.
	 */
	public static final String EVALUE = "EVALUE";

	/**
	 * This variable represents the key for the 'delta' column.
	 */
	public static final String DELTA = "DELTA";

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
	public MascothitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'MascothitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public MascothitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(MASCOTHITID)) {
			this.iMascothitid = ((Long)aParams.get(MASCOTHITID)).longValue();
		}
		if(aParams.containsKey(FK_SEARCHSPECTRUMID)) {
			this.iFk_searchspectrumid = ((Long)aParams.get(FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(FK_PROTEINID)) {
			this.iFk_proteinid = ((Long)aParams.get(FK_PROTEINID)).longValue();
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		if(aParams.containsKey(IONSCORE)) {
			this.iIonscore = (Number)aParams.get(IONSCORE);
		}
		if(aParams.containsKey(EVALUE)) {
			this.iEvalue = (Number)aParams.get(EVALUE);
		}
		if(aParams.containsKey(DELTA)) {
			this.iDelta = (Number)aParams.get(DELTA);
		}
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (java.sql.Timestamp)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (java.sql.Timestamp)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'MascothitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Mascothit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public MascothitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iMascothitid = aResultSet.getLong("mascothitid");
		this.iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iFk_proteinid = aResultSet.getLong("fk_proteinid");
		this.iCharge = aResultSet.getLong("charge");
		this.iIonscore = (Number)aResultSet.getObject("ionscore");
		this.iEvalue = (Number)aResultSet.getObject("evalue");
		this.iDelta = (Number)aResultSet.getObject("delta");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Mascothitid' column
	 * 
	 * @return	long	with the value for the Mascothitid column.
	 */
	public long getMascothitid() {
		return this.iMascothitid;
	}

	/**
	 * This method returns the value for the 'Fk_searchspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_searchspectrumid column.
	 */
	public long getFk_searchspectrumid() {
		return this.iFk_searchspectrumid;
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
	 * This method returns the value for the 'Fk_proteinid' column
	 * 
	 * @return	long	with the value for the Fk_proteinid column.
	 */
	public long getFk_proteinid() {
		return this.iFk_proteinid;
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
	 * This method returns the value for the 'Ionscore' column
	 * 
	 * @return	Number	with the value for the Ionscore column.
	 */
	public Number getIonscore() {
		return this.iIonscore;
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
	 * This method returns the value for the 'Delta' column
	 * 
	 * @return	Number	with the value for the Delta column.
	 */
	public Number getDelta() {
		return this.iDelta;
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
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public java.sql.Timestamp getModificationdate() {
		return this.iModificationdate;
	}

	/**
	 * This method sets the value for the 'Mascothitid' column
	 * 
	 * @param	aMascothitid	long with the value for the Mascothitid column.
	 */
	public void setMascothitid(long aMascothitid) {
		this.iMascothitid = aMascothitid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_searchspectrumid' column
	 * 
	 * @param	aFk_searchspectrumid	long with the value for the Fk_searchspectrumid column.
	 */
	public void setFk_searchspectrumid(long aFk_searchspectrumid) {
		this.iFk_searchspectrumid = aFk_searchspectrumid;
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
	 * This method sets the value for the 'Fk_proteinid' column
	 * 
	 * @param	aFk_proteinid	long with the value for the Fk_proteinid column.
	 */
	public void setFk_proteinid(long aFk_proteinid) {
		this.iFk_proteinid = aFk_proteinid;
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
	 * This method sets the value for the 'Ionscore' column
	 * 
	 * @param	aIonscore	Number with the value for the Ionscore column.
	 */
	public void setIonscore(Number aIonscore) {
		this.iIonscore = aIonscore;
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
	 * This method sets the value for the 'Delta' column
	 * 
	 * @param	aDelta	Number with the value for the Delta column.
	 */
	public void setDelta(Number aDelta) {
		this.iDelta = aDelta;
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
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(java.sql.Timestamp aModificationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM mascothit WHERE mascothitid = ?");
		lStat.setLong(1, iMascothitid);
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
		if(!aKeys.containsKey(MASCOTHITID)) {
			throw new IllegalArgumentException("Primary key field 'MASCOTHITID' is missing in HashMap!");
		} else {
			iMascothitid = ((Long)aKeys.get(MASCOTHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM mascothit WHERE mascothitid = ?");
		lStat.setLong(1, iMascothitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iMascothitid = lRS.getLong("mascothitid");
			iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iFk_proteinid = lRS.getLong("fk_proteinid");
			iCharge = lRS.getLong("charge");
			iIonscore = (Number)lRS.getObject("ionscore");
			iEvalue = (Number)lRS.getObject("evalue");
			iDelta = (Number)lRS.getObject("delta");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'mascothit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'mascothit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from mascothit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<MascothitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<MascothitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<MascothitTableAccessor>  entities = new ArrayList<MascothitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new MascothitTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE mascothit SET mascothitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, charge = ?, ionscore = ?, evalue = ?, delta = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE mascothitid = ?");
		lStat.setLong(1, iMascothitid);
		lStat.setLong(2, iFk_searchspectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iFk_proteinid);
		lStat.setLong(5, iCharge);
		lStat.setObject(6, iIonscore);
		lStat.setObject(7, iEvalue);
		lStat.setObject(8, iDelta);
		lStat.setObject(9, iCreationdate);
		lStat.setLong(10, iMascothitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO mascothit (mascothitid, fk_searchspectrumid, fk_peptideid, fk_proteinid, charge, ionscore, evalue, delta, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(iMascothitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iMascothitid);
		}
		if(iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_searchspectrumid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_peptideid);
		}
		if(iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iFk_proteinid);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, iCharge);
		}
		if(iIonscore == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iIonscore);
		}
		if(iEvalue == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, iEvalue);
		}
		if(iDelta == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iDelta);
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
			iMascothitid = ((Number) iKeys[0]).longValue();
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