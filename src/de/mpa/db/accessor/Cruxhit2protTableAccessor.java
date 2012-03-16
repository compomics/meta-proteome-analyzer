/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 16/03/2012
 * Time: 14:03:39
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
 * This class is a generated accessor for the Cruxhit2prot table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Cruxhit2protTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'cruxhit2protid' column.
	 */
	protected long iCruxhit2protid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_cruxhitid' column.
	 */
	protected long iFk_cruxhitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_proteinid' column.
	 */
	protected long iFk_proteinid = Long.MIN_VALUE;


	/**
	 * This variable represents the key for the 'cruxhit2protid' column.
	 */
	public static final String CRUXHIT2PROTID = "CRUXHIT2PROTID";

	/**
	 * This variable represents the key for the 'fk_cruxhitid' column.
	 */
	public static final String FK_CRUXHITID = "FK_CRUXHITID";

	/**
	 * This variable represents the key for the 'fk_proteinid' column.
	 */
	public static final String FK_PROTEINID = "FK_PROTEINID";




	/**
	 * Default constructor.
	 */
	public Cruxhit2protTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Cruxhit2protTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Cruxhit2protTableAccessor(HashMap aParams) {
		if(aParams.containsKey(CRUXHIT2PROTID)) {
			this.iCruxhit2protid = ((Long)aParams.get(CRUXHIT2PROTID)).longValue();
		}
		if(aParams.containsKey(FK_CRUXHITID)) {
			this.iFk_cruxhitid = ((Long)aParams.get(FK_CRUXHITID)).longValue();
		}
		if(aParams.containsKey(FK_PROTEINID)) {
			this.iFk_proteinid = ((Long)aParams.get(FK_PROTEINID)).longValue();
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'Cruxhit2protTableAccessor' object based on a resultset
	 * obtained by a 'select * from Cruxhit2prot' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Cruxhit2protTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iCruxhit2protid = aResultSet.getLong("cruxhit2protid");
		this.iFk_cruxhitid = aResultSet.getLong("fk_cruxhitid");
		this.iFk_proteinid = aResultSet.getLong("fk_proteinid");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Cruxhit2protid' column
	 * 
	 * @return	long	with the value for the Cruxhit2protid column.
	 */
	public long getCruxhit2protid() {
		return this.iCruxhit2protid;
	}

	/**
	 * This method returns the value for the 'Fk_cruxhitid' column
	 * 
	 * @return	long	with the value for the Fk_cruxhitid column.
	 */
	public long getFk_cruxhitid() {
		return this.iFk_cruxhitid;
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
	 * This method sets the value for the 'Cruxhit2protid' column
	 * 
	 * @param	aCruxhit2protid	long with the value for the Cruxhit2protid column.
	 */
	public void setCruxhit2protid(long aCruxhit2protid) {
		this.iCruxhit2protid = aCruxhit2protid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_cruxhitid' column
	 * 
	 * @param	aFk_cruxhitid	long with the value for the Fk_cruxhitid column.
	 */
	public void setFk_cruxhitid(long aFk_cruxhitid) {
		this.iFk_cruxhitid = aFk_cruxhitid;
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
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM cruxhit2prot WHERE cruxhit2protid = ?");
		lStat.setLong(1, iCruxhit2protid);
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
		if(!aKeys.containsKey(CRUXHIT2PROTID)) {
			throw new IllegalArgumentException("Primary key field 'CRUXHIT2PROTID' is missing in HashMap!");
		} else {
			iCruxhit2protid = ((Long)aKeys.get(CRUXHIT2PROTID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM cruxhit2prot WHERE cruxhit2protid = ?");
		lStat.setLong(1, iCruxhit2protid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iCruxhit2protid = lRS.getLong("cruxhit2protid");
			iFk_cruxhitid = lRS.getLong("fk_cruxhitid");
			iFk_proteinid = lRS.getLong("fk_proteinid");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'cruxhit2prot' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'cruxhit2prot' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from cruxhit2prot";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Cruxhit2protTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Cruxhit2protTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Cruxhit2protTableAccessor>  entities = new ArrayList<Cruxhit2protTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Cruxhit2protTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE cruxhit2prot SET cruxhit2protid = ?, fk_cruxhitid = ?, fk_proteinid = ? WHERE cruxhit2protid = ?");
		lStat.setLong(1, iCruxhit2protid);
		lStat.setLong(2, iFk_cruxhitid);
		lStat.setLong(3, iFk_proteinid);
		lStat.setLong(4, iCruxhit2protid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO cruxhit2prot (cruxhit2protid, fk_cruxhitid, fk_proteinid) values(?, ?, ?)");
		if(iCruxhit2protid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iCruxhit2protid);
		}
		if(iFk_cruxhitid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_cruxhitid);
		}
		if(iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_proteinid);
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
			iCruxhit2protid = ((Number) iKeys[0]).longValue();
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