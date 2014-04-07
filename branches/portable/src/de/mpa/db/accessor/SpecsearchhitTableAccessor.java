/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:26:10
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
 * This class is a generated accessor for the Specsearchhit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SpecsearchhitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'specsearchhitid' column.
	 */
	protected long iSpecsearchhitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_searchspectrumid' column.
	 */
	protected long iFk_searchspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_libspectrumid' column.
	 */
	protected long iFk_libspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'similarity' column.
	 */
	protected Number iSimilarity = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected String iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected String iModificationdate = null;


	/**
	 * This variable represents the key for the 'specsearchhitid' column.
	 */
	public static final String SPECSEARCHHITID = "SPECSEARCHHITID";

	/**
	 * This variable represents the key for the 'fk_searchspectrumid' column.
	 */
	public static final String FK_SEARCHSPECTRUMID = "FK_SEARCHSPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_libspectrumid' column.
	 */
	public static final String FK_LIBSPECTRUMID = "FK_LIBSPECTRUMID";

	/**
	 * This variable represents the key for the 'similarity' column.
	 */
	public static final String SIMILARITY = "SIMILARITY";

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
	public SpecsearchhitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SpecsearchhitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SpecsearchhitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPECSEARCHHITID)) {
			this.iSpecsearchhitid = ((Long)aParams.get(SPECSEARCHHITID)).longValue();
		}
		if(aParams.containsKey(FK_SEARCHSPECTRUMID)) {
			this.iFk_searchspectrumid = ((Long)aParams.get(FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_LIBSPECTRUMID)) {
			this.iFk_libspectrumid = ((Long)aParams.get(FK_LIBSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(SIMILARITY)) {
			this.iSimilarity = (Number)aParams.get(SIMILARITY);
		}
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (String)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (String)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'SpecsearchhitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Specsearchhit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpecsearchhitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpecsearchhitid = aResultSet.getLong("specsearchhitid");
		this.iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
		this.iFk_libspectrumid = aResultSet.getLong("fk_libspectrumid");
		this.iSimilarity = (Number)aResultSet.getObject("similarity");
		this.iCreationdate = (String)aResultSet.getObject("creationdate");
		this.iModificationdate = (String)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Specsearchhitid' column
	 * 
	 * @return	long	with the value for the Specsearchhitid column.
	 */
	public long getSpecsearchhitid() {
		return this.iSpecsearchhitid;
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
	 * This method returns the value for the 'Fk_libspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_libspectrumid column.
	 */
	public long getFk_libspectrumid() {
		return this.iFk_libspectrumid;
	}

	/**
	 * This method returns the value for the 'Similarity' column
	 * 
	 * @return	Number	with the value for the Similarity column.
	 */
	public Number getSimilarity() {
		return this.iSimilarity;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	String	with the value for the Creationdate column.
	 */
	public String getCreationdate() {
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
	 * This method sets the value for the 'Specsearchhitid' column
	 * 
	 * @param	aSpecsearchhitid	long with the value for the Specsearchhitid column.
	 */
	public void setSpecsearchhitid(long aSpecsearchhitid) {
		this.iSpecsearchhitid = aSpecsearchhitid;
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
	 * This method sets the value for the 'Fk_libspectrumid' column
	 * 
	 * @param	aFk_libspectrumid	long with the value for the Fk_libspectrumid column.
	 */
	public void setFk_libspectrumid(long aFk_libspectrumid) {
		this.iFk_libspectrumid = aFk_libspectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Similarity' column
	 * 
	 * @param	aSimilarity	Number with the value for the Similarity column.
	 */
	public void setSimilarity(Number aSimilarity) {
		this.iSimilarity = aSimilarity;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	String with the value for the Creationdate column.
	 */
	public void setCreationdate(String aCreationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM specsearchhit WHERE specsearchhitid = ?");
		lStat.setLong(1, iSpecsearchhitid);
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
		if(!aKeys.containsKey(SPECSEARCHHITID)) {
			throw new IllegalArgumentException("Primary key field 'SPECSEARCHHITID' is missing in HashMap!");
		} else {
			iSpecsearchhitid = ((Long)aKeys.get(SPECSEARCHHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM specsearchhit WHERE specsearchhitid = ?");
		lStat.setLong(1, iSpecsearchhitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpecsearchhitid = lRS.getLong("specsearchhitid");
			iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
			iFk_libspectrumid = lRS.getLong("fk_libspectrumid");
			iSimilarity = (Number)lRS.getObject("similarity");
			iCreationdate = (String)lRS.getObject("creationdate");
			iModificationdate = (String)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'specsearchhit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'specsearchhit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from specsearchhit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SpecsearchhitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SpecsearchhitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SpecsearchhitTableAccessor>  entities = new ArrayList<SpecsearchhitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SpecsearchhitTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE specsearchhit SET specsearchhitid = ?, fk_searchspectrumid = ?, fk_libspectrumid = ?, similarity = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE specsearchhitid = ?");
		lStat.setLong(1, iSpecsearchhitid);
		lStat.setLong(2, iFk_searchspectrumid);
		lStat.setLong(3, iFk_libspectrumid);
		lStat.setObject(4, iSimilarity);
		lStat.setObject(5, iCreationdate);
		lStat.setLong(6, iSpecsearchhitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO specsearchhit (specsearchhitid, fk_searchspectrumid, fk_libspectrumid, similarity, creationdate, modificationdate) values(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(iSpecsearchhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpecsearchhitid);
		}
		if(iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_searchspectrumid);
		}
		if(iFk_libspectrumid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_libspectrumid);
		}
		if(iSimilarity == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iSimilarity);
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
			iSpecsearchhitid = ((Number) iKeys[0]).longValue();
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