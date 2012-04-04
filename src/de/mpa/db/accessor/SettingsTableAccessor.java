/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:54:13
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
 * This class is a generated accessor for the Settings table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SettingsTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'settingsid' column.
	 */
	protected long iSettingsid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_sscfgid' column.
	 */
	protected long iFk_sscfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_dbcfgid' column.
	 */
	protected long iFk_dbcfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_dncfgid' column.
	 */
	protected long iFk_dncfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the key for the 'settingsid' column.
	 */
	public static final String SETTINGSID = "SETTINGSID";

	/**
	 * This variable represents the key for the 'fk_sscfgid' column.
	 */
	public static final String FK_SSCFGID = "FK_SSCFGID";

	/**
	 * This variable represents the key for the 'fk_dbcfgid' column.
	 */
	public static final String FK_DBCFGID = "FK_DBCFGID";

	/**
	 * This variable represents the key for the 'fk_dncfgid' column.
	 */
	public static final String FK_DNCFGID = "FK_DNCFGID";




	/**
	 * Default constructor.
	 */
	public SettingsTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SettingsTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SettingsTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SETTINGSID)) {
			this.iSettingsid = ((Long)aParams.get(SETTINGSID)).longValue();
		}
		if(aParams.containsKey(FK_SSCFGID)) {
			this.iFk_sscfgid = ((Long)aParams.get(FK_SSCFGID)).longValue();
		}
		if(aParams.containsKey(FK_DBCFGID)) {
			this.iFk_dbcfgid = ((Long)aParams.get(FK_DBCFGID)).longValue();
		}
		if(aParams.containsKey(FK_DNCFGID)) {
			this.iFk_dncfgid = ((Long)aParams.get(FK_DNCFGID)).longValue();
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'SettingsTableAccessor' object based on a resultset
	 * obtained by a 'select * from Settings' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SettingsTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSettingsid = aResultSet.getLong("settingsid");
		this.iFk_sscfgid = aResultSet.getLong("fk_sscfgid");
		this.iFk_dbcfgid = aResultSet.getLong("fk_dbcfgid");
		this.iFk_dncfgid = aResultSet.getLong("fk_dncfgid");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Settingsid' column
	 * 
	 * @return	long	with the value for the Settingsid column.
	 */
	public long getSettingsid() {
		return this.iSettingsid;
	}

	/**
	 * This method returns the value for the 'Fk_sscfgid' column
	 * 
	 * @return	long	with the value for the Fk_sscfgid column.
	 */
	public long getFk_sscfgid() {
		return this.iFk_sscfgid;
	}

	/**
	 * This method returns the value for the 'Fk_dbcfgid' column
	 * 
	 * @return	long	with the value for the Fk_dbcfgid column.
	 */
	public long getFk_dbcfgid() {
		return this.iFk_dbcfgid;
	}

	/**
	 * This method returns the value for the 'Fk_dncfgid' column
	 * 
	 * @return	long	with the value for the Fk_dncfgid column.
	 */
	public long getFk_dncfgid() {
		return this.iFk_dncfgid;
	}

	/**
	 * This method sets the value for the 'Settingsid' column
	 * 
	 * @param	aSettingsid	long with the value for the Settingsid column.
	 */
	public void setSettingsid(long aSettingsid) {
		this.iSettingsid = aSettingsid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_sscfgid' column
	 * 
	 * @param	aFk_sscfgid	long with the value for the Fk_sscfgid column.
	 */
	public void setFk_sscfgid(long aFk_sscfgid) {
		this.iFk_sscfgid = aFk_sscfgid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_dbcfgid' column
	 * 
	 * @param	aFk_dbcfgid	long with the value for the Fk_dbcfgid column.
	 */
	public void setFk_dbcfgid(long aFk_dbcfgid) {
		this.iFk_dbcfgid = aFk_dbcfgid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_dncfgid' column
	 * 
	 * @param	aFk_dncfgid	long with the value for the Fk_dncfgid column.
	 */
	public void setFk_dncfgid(long aFk_dncfgid) {
		this.iFk_dncfgid = aFk_dncfgid;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM settings WHERE settingsid = ?");
		lStat.setLong(1, iSettingsid);
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
		if(!aKeys.containsKey(SETTINGSID)) {
			throw new IllegalArgumentException("Primary key field 'SETTINGSID' is missing in HashMap!");
		} else {
			iSettingsid = ((Long)aKeys.get(SETTINGSID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM settings WHERE settingsid = ?");
		lStat.setLong(1, iSettingsid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSettingsid = lRS.getLong("settingsid");
			iFk_sscfgid = lRS.getLong("fk_sscfgid");
			iFk_dbcfgid = lRS.getLong("fk_dbcfgid");
			iFk_dncfgid = lRS.getLong("fk_dncfgid");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'settings' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'settings' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from settings";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SettingsTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SettingsTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SettingsTableAccessor>  entities = new ArrayList<SettingsTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SettingsTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE settings SET settingsid = ?, fk_sscfgid = ?, fk_dbcfgid = ?, fk_dncfgid = ? WHERE settingsid = ?");
		lStat.setLong(1, iSettingsid);
		lStat.setLong(2, iFk_sscfgid);
		lStat.setLong(3, iFk_dbcfgid);
		lStat.setLong(4, iFk_dncfgid);
		lStat.setLong(5, iSettingsid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO settings (settingsid, fk_sscfgid, fk_dbcfgid, fk_dncfgid) values(?, ?, ?, ?)");
		if(iSettingsid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSettingsid);
		}
		if(iFk_sscfgid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_sscfgid);
		}
		if(iFk_dbcfgid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_dbcfgid);
		}
		if(iFk_dncfgid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iFk_dncfgid);
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
			iSettingsid = ((Number) iKeys[0]).longValue();
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