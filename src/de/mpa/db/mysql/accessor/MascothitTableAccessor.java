/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 13/02/2013
 * Time: 13:30:48
 */
package de.mpa.db.mysql.accessor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
@SuppressWarnings("serial")
public class MascothitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

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
	protected Number iIonscore;


	/**
	 * This variable represents the contents for the 'evalue' column.
	 */
	protected Number iEvalue;


	/**
	 * This variable represents the contents for the 'delta' column.
	 */
	protected Number iDelta;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected Timestamp iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected Timestamp iModificationdate;


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
		if(aParams.containsKey(MascothitTableAccessor.MASCOTHITID)) {
            iMascothitid = ((Long)aParams.get(MascothitTableAccessor.MASCOTHITID)).longValue();
		}
		if(aParams.containsKey(MascothitTableAccessor.FK_SEARCHSPECTRUMID)) {
            iFk_searchspectrumid = ((Long)aParams.get(MascothitTableAccessor.FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(MascothitTableAccessor.FK_PEPTIDEID)) {
            iFk_peptideid = ((Long)aParams.get(MascothitTableAccessor.FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(MascothitTableAccessor.FK_PROTEINID)) {
            iFk_proteinid = ((Long)aParams.get(MascothitTableAccessor.FK_PROTEINID)).longValue();
		}
		if(aParams.containsKey(MascothitTableAccessor.CHARGE)) {
            iCharge = ((Long)aParams.get(MascothitTableAccessor.CHARGE)).longValue();
		}
		if(aParams.containsKey(MascothitTableAccessor.IONSCORE)) {
            iIonscore = (Number)aParams.get(MascothitTableAccessor.IONSCORE);
		}
		if(aParams.containsKey(MascothitTableAccessor.EVALUE)) {
            iEvalue = (Number)aParams.get(MascothitTableAccessor.EVALUE);
		}
		if(aParams.containsKey(MascothitTableAccessor.DELTA)) {
            iDelta = (Number)aParams.get(MascothitTableAccessor.DELTA);
		}
		if(aParams.containsKey(MascothitTableAccessor.CREATIONDATE)) {
            iCreationdate = (Timestamp)aParams.get(MascothitTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(MascothitTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (Timestamp)aParams.get(MascothitTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'MascothitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Mascothit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public MascothitTableAccessor(ResultSet aResultSet) throws SQLException {
        iMascothitid = aResultSet.getLong("mascothitid");
        iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
        iFk_peptideid = aResultSet.getLong("fk_peptideid");
        iFk_proteinid = aResultSet.getLong("fk_proteinid");
        iCharge = aResultSet.getLong("charge");
        iIonscore = (Number)aResultSet.getObject("ionscore");
        iEvalue = (Number)aResultSet.getObject("evalue");
        iDelta = (Number)aResultSet.getObject("delta");
        iCreationdate = (Timestamp)aResultSet.getObject("creationdate");
        iModificationdate = (Timestamp)aResultSet.getObject("modificationdate");
        iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Mascothitid' column
	 * 
	 * @return	long	with the value for the Mascothitid column.
	 */
	public long getMascothitid() {
		return iMascothitid;
	}

	/**
	 * This method returns the value for the 'Fk_searchspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_searchspectrumid column.
	 */
	public long getFk_searchspectrumid() {
		return iFk_searchspectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return	long	with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Fk_proteinid' column
	 * 
	 * @return	long	with the value for the Fk_proteinid column.
	 */
	public long getFk_proteinid() {
		return iFk_proteinid;
	}

	/**
	 * This method returns the value for the 'Charge' column
	 * 
	 * @return	long	with the value for the Charge column.
	 */
	public long getCharge() {
		return iCharge;
	}

	/**
	 * This method returns the value for the 'Ionscore' column
	 * 
	 * @return	Number	with the value for the Ionscore column.
	 */
	public Number getIonscore() {
		return iIonscore;
	}

	/**
	 * This method returns the value for the 'Evalue' column
	 * 
	 * @return	Number	with the value for the Evalue column.
	 */
	public Number getEvalue() {
		return iEvalue;
	}

	/**
	 * This method returns the value for the 'Delta' column
	 * 
	 * @return	Number	with the value for the Delta column.
	 */
	public Number getDelta() {
		return iDelta;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public Timestamp getCreationdate() {
		return iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public Timestamp getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Mascothitid' column
	 * 
	 * @param	aMascothitid	long with the value for the Mascothitid column.
	 */
	public void setMascothitid(long aMascothitid) {
        iMascothitid = aMascothitid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_searchspectrumid' column
	 * 
	 * @param	aFk_searchspectrumid	long with the value for the Fk_searchspectrumid column.
	 */
	public void setFk_searchspectrumid(long aFk_searchspectrumid) {
        iFk_searchspectrumid = aFk_searchspectrumid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param	aFk_peptideid	long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
        iFk_peptideid = aFk_peptideid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_proteinid' column
	 * 
	 * @param	aFk_proteinid	long with the value for the Fk_proteinid column.
	 */
	public void setFk_proteinid(long aFk_proteinid) {
        iFk_proteinid = aFk_proteinid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Charge' column
	 * 
	 * @param	aCharge	long with the value for the Charge column.
	 */
	public void setCharge(long aCharge) {
        iCharge = aCharge;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Ionscore' column
	 * 
	 * @param	aIonscore	Number with the value for the Ionscore column.
	 */
	public void setIonscore(Number aIonscore) {
        iIonscore = aIonscore;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Evalue' column
	 * 
	 * @param	aEvalue	Number with the value for the Evalue column.
	 */
	public void setEvalue(Number aEvalue) {
        iEvalue = aEvalue;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Delta' column
	 * 
	 * @param	aDelta	Number with the value for the Delta column.
	 */
	public void setDelta(Number aDelta) {
        iDelta = aDelta;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(Timestamp aCreationdate) {
        iCreationdate = aCreationdate;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(Timestamp aModificationdate) {
        iModificationdate = aModificationdate;
        iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM mascothit WHERE mascothitid = ?");
		lStat.setLong(1, this.iMascothitid);
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
		if(!aKeys.containsKey(MascothitTableAccessor.MASCOTHITID)) {
			throw new IllegalArgumentException("Primary key field 'MASCOTHITID' is missing in HashMap!");
		} else {
            this.iMascothitid = ((Long)aKeys.get(MascothitTableAccessor.MASCOTHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM mascothit WHERE mascothitid = ?");
		lStat.setLong(1, this.iMascothitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iMascothitid = lRS.getLong("mascothitid");
            this.iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
            this.iFk_peptideid = lRS.getLong("fk_peptideid");
            this.iFk_proteinid = lRS.getLong("fk_proteinid");
            this.iCharge = lRS.getLong("charge");
            this.iIonscore = (Number)lRS.getObject("ionscore");
            this.iEvalue = (Number)lRS.getObject("evalue");
            this.iDelta = (Number)lRS.getObject("delta");
            this.iCreationdate = (Timestamp)lRS.getObject("creationdate");
            this.iModificationdate = (Timestamp)lRS.getObject("modificationdate");
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
		ResultSet rs = stat.executeQuery(MascothitTableAccessor.getBasicSelect());
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
		if(!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE mascothit SET mascothitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, charge = ?, ionscore = ?, evalue = ?, delta = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE mascothitid = ?");
		lStat.setLong(1, this.iMascothitid);
		lStat.setLong(2, this.iFk_searchspectrumid);
		lStat.setLong(3, this.iFk_peptideid);
		lStat.setLong(4, this.iFk_proteinid);
		lStat.setLong(5, this.iCharge);
		lStat.setObject(6, this.iIonscore);
		lStat.setObject(7, this.iEvalue);
		lStat.setObject(8, this.iDelta);
		lStat.setObject(9, this.iCreationdate);
		lStat.setLong(10, this.iMascothitid);
		int result = lStat.executeUpdate();
		lStat.close();
        iUpdated = false;
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
		if(this.iMascothitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iMascothitid);
		}
		if(this.iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, this.iFk_searchspectrumid);
		}
		if(this.iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, this.iFk_peptideid);
		}
		if(this.iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, this.iFk_proteinid);
		}
		if(this.iCharge == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, this.iCharge);
		}
		if(this.iIonscore == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, this.iIonscore);
		}
		if(this.iEvalue == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, this.iEvalue);
		}
		if(this.iDelta == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, this.iDelta);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
        this.iKeys = new Object[colCount];
		while(lrsKeys.next()) {
			for(int i = 0; i< this.iKeys.length; i++) {
                this.iKeys[i] = lrsKeys.getObject(i+1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if(this.iKeys != null && this.iKeys.length == 1 && this.iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
            this.iMascothitid = ((Number) this.iKeys[0]).longValue();
		}
        iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if 
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return	Object[]	with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return iKeys;
	}

}