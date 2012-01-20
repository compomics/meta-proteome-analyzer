/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/01/2012
 * Time: 13:27:12
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
 * This class is a generated accessor for the Pep2prot table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Pep2protTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'pep2protid' column.
	 */
	protected long iPep2protid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_proteinsid' column.
	 */
	protected long iFk_proteinsid = Long.MIN_VALUE;


	/**
	 * This variable represents the key for the 'pep2protid' column.
	 */
	public static final String PEP2PROTID = "PEP2PROTID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'fk_proteinsid' column.
	 */
	public static final String FK_PROTEINSID = "FK_PROTEINSID";




	/**
	 * Default constructor.
	 */
	public Pep2protTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Pep2protTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Pep2protTableAccessor(HashMap aParams) {
		if(aParams.containsKey(PEP2PROTID)) {
			this.iPep2protid = ((Long)aParams.get(PEP2PROTID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(FK_PROTEINSID)) {
			this.iFk_proteinsid = ((Long)aParams.get(FK_PROTEINSID)).longValue();
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'Pep2protTableAccessor' object based on a resultset
	 * obtained by a 'select * from Pep2prot' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Pep2protTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iPep2protid = aResultSet.getLong("pep2protid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iFk_proteinsid = aResultSet.getLong("fk_proteinsid");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Pep2protid' column
	 * 
	 * @return	long	with the value for the Pep2protid column.
	 */
	public long getPep2protid() {
		return this.iPep2protid;
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
	 * This method returns the value for the 'Fk_proteinsid' column
	 * 
	 * @return	long	with the value for the Fk_proteinsid column.
	 */
	public long getFk_proteinsid() {
		return this.iFk_proteinsid;
	}

	/**
	 * This method sets the value for the 'Pep2protid' column
	 * 
	 * @param	aPep2protid	long with the value for the Pep2protid column.
	 */
	public void setPep2protid(long aPep2protid) {
		this.iPep2protid = aPep2protid;
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
	 * This method sets the value for the 'Fk_proteinsid' column
	 * 
	 * @param	aFk_proteinsid	long with the value for the Fk_proteinsid column.
	 */
	public void setFk_proteinsid(long aFk_proteinsid) {
		this.iFk_proteinsid = aFk_proteinsid;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM pep2prot WHERE pep2protid = ?");
		lStat.setLong(1, iPep2protid);
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
		if(!aKeys.containsKey(PEP2PROTID)) {
			throw new IllegalArgumentException("Primary key field 'PEP2PROTID' is missing in HashMap!");
		} else {
			iPep2protid = ((Long)aKeys.get(PEP2PROTID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM pep2prot WHERE pep2protid = ?");
		lStat.setLong(1, iPep2protid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iPep2protid = lRS.getLong("pep2protid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iFk_proteinsid = lRS.getLong("fk_proteinsid");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'pep2prot' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'pep2prot' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from pep2prot";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Pep2protTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Pep2protTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Pep2protTableAccessor>  entities = new ArrayList<Pep2protTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Pep2protTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE pep2prot SET pep2protid = ?, fk_peptideid = ?, fk_proteinsid = ? WHERE pep2protid = ?");
		lStat.setLong(1, iPep2protid);
		lStat.setLong(2, iFk_peptideid);
		lStat.setLong(3, iFk_proteinsid);
		lStat.setLong(4, iPep2protid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO pep2prot (pep2protid, fk_peptideid, fk_proteinsid) values(?, ?, ?)");
		if(iPep2protid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iPep2protid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_peptideid);
		}
		if(iFk_proteinsid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_proteinsid);
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
			iPep2protid = ((Number) iKeys[0]).longValue();
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