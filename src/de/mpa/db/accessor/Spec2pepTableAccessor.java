/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 13/03/2012
 * Time: 13:17:20
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
 * This class is a generated accessor for the Spec2pep table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Spec2pepTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'spec2pepid' column.
	 */
	protected long iSpec2pepid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_spectrumid' column.
	 */
	protected long iFk_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the key for the 'spec2pepid' column.
	 */
	public static final String SPEC2PEPID = "SPEC2PEPID";

	/**
	 * This variable represents the key for the 'fk_spectrumid' column.
	 */
	public static final String FK_SPECTRUMID = "FK_SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";




	/**
	 * Default constructor.
	 */
	public Spec2pepTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Spec2pepTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Spec2pepTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPEC2PEPID)) {
			this.iSpec2pepid = ((Long)aParams.get(SPEC2PEPID)).longValue();
		}
		if(aParams.containsKey(FK_SPECTRUMID)) {
			this.iFk_spectrumid = ((Long)aParams.get(FK_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'Spec2pepTableAccessor' object based on a resultset
	 * obtained by a 'select * from Spec2pep' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Spec2pepTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpec2pepid = aResultSet.getLong("spec2pepid");
		this.iFk_spectrumid = aResultSet.getLong("fk_spectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Spec2pepid' column
	 * 
	 * @return	long	with the value for the Spec2pepid column.
	 */
	public long getSpec2pepid() {
		return this.iSpec2pepid;
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
	 * This method sets the value for the 'Spec2pepid' column
	 * 
	 * @param	aSpec2pepid	long with the value for the Spec2pepid column.
	 */
	public void setSpec2pepid(long aSpec2pepid) {
		this.iSpec2pepid = aSpec2pepid;
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
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM spec2pep WHERE spec2pepid = ?");
		lStat.setLong(1, iSpec2pepid);
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
		if(!aKeys.containsKey(SPEC2PEPID)) {
			throw new IllegalArgumentException("Primary key field 'SPEC2PEPID' is missing in HashMap!");
		} else {
			iSpec2pepid = ((Long)aKeys.get(SPEC2PEPID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM spec2pep WHERE spec2pepid = ?");
		lStat.setLong(1, iSpec2pepid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpec2pepid = lRS.getLong("spec2pepid");
			iFk_spectrumid = lRS.getLong("fk_spectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'spec2pep' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'spec2pep' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from spec2pep";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Spec2pepTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Spec2pepTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Spec2pepTableAccessor>  entities = new ArrayList<Spec2pepTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Spec2pepTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE spec2pep SET spec2pepid = ?, fk_spectrumid = ?, fk_peptideid = ? WHERE spec2pepid = ?");
		lStat.setLong(1, iSpec2pepid);
		lStat.setLong(2, iFk_spectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iSpec2pepid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO spec2pep (spec2pepid, fk_spectrumid, fk_peptideid) values(?, ?, ?)");
		if(iSpec2pepid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpec2pepid);
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
			iSpec2pepid = ((Number) iKeys[0]).longValue();
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