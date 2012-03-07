/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/01/2012
 * Time: 13:27:09
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
 * This class is a generated accessor for the Expproperty table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ExppropertyTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'exppropertyid' column.
	 */
	protected long iExppropertyid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_experimentid' column.
	 */
	protected long iFk_experimentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'name' column.
	 */
	protected String iName = null;


	/**
	 * This variable represents the contents for the 'value' column.
	 */
	protected String iValue = null;


	/**
	 * This variable represents the key for the 'exppropertyid' column.
	 */
	public static final String EXPPROPERTYID = "EXPPROPERTYID";

	/**
	 * This variable represents the key for the 'fk_experimentid' column.
	 */
	public static final String FK_EXPERIMENTID = "FK_EXPERIMENTID";

	/**
	 * This variable represents the key for the 'name' column.
	 */
	public static final String NAME = "NAME";

	/**
	 * This variable represents the key for the 'value' column.
	 */
	public static final String VALUE = "VALUE";




	/**
	 * Default constructor.
	 */
	public ExppropertyTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ExppropertyTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ExppropertyTableAccessor(HashMap aParams) {
		if(aParams.containsKey(EXPPROPERTYID)) {
			this.iExppropertyid = ((Long)aParams.get(EXPPROPERTYID)).longValue();
		}
		if(aParams.containsKey(FK_EXPERIMENTID)) {
			this.iFk_experimentid = ((Long)aParams.get(FK_EXPERIMENTID)).longValue();
		}
		if(aParams.containsKey(NAME)) {
			this.iName = (String)aParams.get(NAME);
		}
		if(aParams.containsKey(VALUE)) {
			this.iValue = (String)aParams.get(VALUE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ExppropertyTableAccessor' object based on a resultset
	 * obtained by a 'select * from Expproperty' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ExppropertyTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iExppropertyid = aResultSet.getLong("exppropertyid");
		this.iFk_experimentid = aResultSet.getLong("fk_experimentid");
		this.iName = (String)aResultSet.getObject("name");
		this.iValue = (String)aResultSet.getObject("value");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Exppropertyid' column
	 * 
	 * @return	long	with the value for the Exppropertyid column.
	 */
	public long getExppropertyid() {
		return this.iExppropertyid;
	}

	/**
	 * This method returns the value for the 'Fk_experimentid' column
	 * 
	 * @return	long	with the value for the Fk_experimentid column.
	 */
	public long getFk_experimentid() {
		return this.iFk_experimentid;
	}

	/**
	 * This method returns the value for the 'Name' column
	 * 
	 * @return	String	with the value for the Name column.
	 */
	public String getName() {
		return this.iName;
	}

	/**
	 * This method returns the value for the 'Value' column
	 * 
	 * @return	String	with the value for the Value column.
	 */
	public String getValue() {
		return this.iValue;
	}

	/**
	 * This method sets the value for the 'Exppropertyid' column
	 * 
	 * @param	aExppropertyid	long with the value for the Exppropertyid column.
	 */
	public void setExppropertyid(long aExppropertyid) {
		this.iExppropertyid = aExppropertyid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_experimentid' column
	 * 
	 * @param	aFk_experimentid	long with the value for the Fk_experimentid column.
	 */
	public void setFk_experimentid(long aFk_experimentid) {
		this.iFk_experimentid = aFk_experimentid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Name' column
	 * 
	 * @param	aName	String with the value for the Name column.
	 */
	public void setName(String aName) {
		this.iName = aName;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Value' column
	 * 
	 * @param	aValue	String with the value for the Value column.
	 */
	public void setValue(String aValue) {
		this.iValue = aValue;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM expproperty WHERE exppropertyid = ?");
		lStat.setLong(1, iExppropertyid);
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
		if(!aKeys.containsKey(EXPPROPERTYID)) {
			throw new IllegalArgumentException("Primary key field 'EXPPROPERTYID' is missing in HashMap!");
		} else {
			iExppropertyid = ((Long)aKeys.get(EXPPROPERTYID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM expproperty WHERE exppropertyid = ?");
		lStat.setLong(1, iExppropertyid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iExppropertyid = lRS.getLong("exppropertyid");
			iFk_experimentid = lRS.getLong("fk_experimentid");
			iName = (String)lRS.getObject("name");
			iValue = (String)lRS.getObject("value");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'expproperty' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'expproperty' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from expproperty";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ExppropertyTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ExppropertyTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ExppropertyTableAccessor>  entities = new ArrayList<ExppropertyTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ExppropertyTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE expproperty SET exppropertyid = ?, fk_experimentid = ?, name = ?, value = ? WHERE exppropertyid = ?");
		lStat.setLong(1, iExppropertyid);
		lStat.setLong(2, iFk_experimentid);
		lStat.setObject(3, iName);
		lStat.setObject(4, iValue);
		lStat.setLong(5, iExppropertyid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO expproperty (exppropertyid, fk_experimentid, name, value) values(?, ?, ?, ?)");
		if(iExppropertyid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iExppropertyid);
		}
		if(iFk_experimentid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_experimentid);
		}
		if(iName == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iName);
		}
		if(iValue == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iValue);
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
			iExppropertyid = ((Number) iKeys[0]).longValue();
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