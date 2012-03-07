/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/02/2012
 * Time: 10:20:06
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
 * This class is a generated accessor for the Arrayspectrum table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ArrayspectrumTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'fk_libspectrumid' column.
	 */
	protected long iFk_libspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'mzarray' column.
	 */
	protected String iMzarray = null;


	/**
	 * This variable represents the contents for the 'intarray' column.
	 */
	protected String iIntarray = null;


	/**
	 * This variable represents the key for the 'fk_libspectrumid' column.
	 */
	public static final String FK_LIBSPECTRUMID = "FK_LIBSPECTRUMID";

	/**
	 * This variable represents the key for the 'mzarray' column.
	 */
	public static final String MZARRAY = "MZARRAY";

	/**
	 * This variable represents the key for the 'intarray' column.
	 */
	public static final String INTARRAY = "INTARRAY";




	/**
	 * Default constructor.
	 */
	public ArrayspectrumTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ArrayspectrumTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ArrayspectrumTableAccessor(HashMap aParams) {
		if(aParams.containsKey(FK_LIBSPECTRUMID)) {
			this.iFk_libspectrumid = ((Long)aParams.get(FK_LIBSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(MZARRAY)) {
			this.iMzarray = (String)aParams.get(MZARRAY);
		}
		if(aParams.containsKey(INTARRAY)) {
			this.iIntarray = (String)aParams.get(INTARRAY);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ArrayspectrumTableAccessor' object based on a resultset
	 * obtained by a 'select * from Arrayspectrum' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ArrayspectrumTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iFk_libspectrumid = aResultSet.getLong("fk_libspectrumid");
		this.iMzarray = (String)aResultSet.getObject("mzarray");
		this.iIntarray = (String)aResultSet.getObject("intarray");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Fk_libspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_libspectrumid column.
	 */
	public long getFk_libspectrumid() {
		return this.iFk_libspectrumid;
	}

	/**
	 * This method returns the value for the 'Mzarray' column
	 * 
	 * @return	String	with the value for the Mzarray column.
	 */
	public String getMzarray() {
		return this.iMzarray;
	}

	/**
	 * This method returns the value for the 'Intarray' column
	 * 
	 * @return	String	with the value for the Intarray column.
	 */
	public String getIntarray() {
		return this.iIntarray;
	}

	/**
	 * This method sets the value for the 'Fk_libspectrumid' column
	 * 
	 * @param	aFk_libspectrumid	long with the value for the Fk_libspectrumid column.
	 */
	public void setFk_libspectrumid(long aFk_libspectrumid) {
		this.iFk_libspectrumid = aFk_libspectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Mzarray' column
	 * 
	 * @param	aMzarray	String with the value for the Mzarray column.
	 */
	public void setMzarray(String aMzarray) {
		this.iMzarray = aMzarray;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Intarray' column
	 * 
	 * @param	aIntarray	String with the value for the Intarray column.
	 */
	public void setIntarray(String aIntarray) {
		this.iIntarray = aIntarray;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM arrayspectrum WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
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
		if(!aKeys.containsKey(FK_LIBSPECTRUMID)) {
			throw new IllegalArgumentException("Primary key field 'FK_LIBSPECTRUMID' is missing in HashMap!");
		} else {
			iFk_libspectrumid = ((Long)aKeys.get(FK_LIBSPECTRUMID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM arrayspectrum WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iFk_libspectrumid = lRS.getLong("fk_libspectrumid");
			iMzarray = (String)lRS.getObject("mzarray");
			iIntarray = (String)lRS.getObject("intarray");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'arrayspectrum' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'arrayspectrum' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from arrayspectrum";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ArrayspectrumTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ArrayspectrumTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ArrayspectrumTableAccessor>  entities = new ArrayList<ArrayspectrumTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ArrayspectrumTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE arrayspectrum SET fk_libspectrumid = ?, mzarray = ?, intarray = ? WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
		lStat.setObject(2, iMzarray);
		lStat.setObject(3, iIntarray);
		lStat.setLong(4, iFk_libspectrumid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO arrayspectrum (fk_libspectrumid, mzarray, intarray) values(?, ?, ?)");
		if(iFk_libspectrumid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iFk_libspectrumid);
		}
		if(iMzarray == null) {
			lStat.setNull(2, -1);
		} else {
			lStat.setObject(2, iMzarray);
		}
		if(iIntarray == null) {
			lStat.setNull(3, -1);
		} else {
			lStat.setObject(3, iIntarray);
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
			iFk_libspectrumid = ((Number) iKeys[0]).longValue();
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