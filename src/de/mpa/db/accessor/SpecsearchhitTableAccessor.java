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
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

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
	protected Number iSimilarity;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected String iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected String iModificationdate;


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
	public SpecsearchhitTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(SpecsearchhitTableAccessor.SPECSEARCHHITID)) {
            iSpecsearchhitid = ((Long)aParams.get(SpecsearchhitTableAccessor.SPECSEARCHHITID)).longValue();
		}
		if(aParams.containsKey(SpecsearchhitTableAccessor.FK_SEARCHSPECTRUMID)) {
            iFk_searchspectrumid = ((Long)aParams.get(SpecsearchhitTableAccessor.FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(SpecsearchhitTableAccessor.FK_LIBSPECTRUMID)) {
            iFk_libspectrumid = ((Long)aParams.get(SpecsearchhitTableAccessor.FK_LIBSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(SpecsearchhitTableAccessor.SIMILARITY)) {
            iSimilarity = (Number)aParams.get(SpecsearchhitTableAccessor.SIMILARITY);
		}
		if(aParams.containsKey(SpecsearchhitTableAccessor.CREATIONDATE)) {
            iCreationdate = (String)aParams.get(SpecsearchhitTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(SpecsearchhitTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (String)aParams.get(SpecsearchhitTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'SpecsearchhitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Specsearchhit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpecsearchhitTableAccessor(ResultSet aResultSet) throws SQLException {
        iSpecsearchhitid = aResultSet.getLong("specsearchhitid");
        iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
        iFk_libspectrumid = aResultSet.getLong("fk_libspectrumid");
        iSimilarity = (Number)aResultSet.getObject("similarity");
        iCreationdate = (String)aResultSet.getObject("creationdate");
        iModificationdate = (String)aResultSet.getObject("modificationdate");

        iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Specsearchhitid' column
	 * 
	 * @return	long	with the value for the Specsearchhitid column.
	 */
	public long getSpecsearchhitid() {
		return iSpecsearchhitid;
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
	 * This method returns the value for the 'Fk_libspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_libspectrumid column.
	 */
	public long getFk_libspectrumid() {
		return iFk_libspectrumid;
	}

	/**
	 * This method returns the value for the 'Similarity' column
	 * 
	 * @return	Number	with the value for the Similarity column.
	 */
	public Number getSimilarity() {
		return iSimilarity;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	String	with the value for the Creationdate column.
	 */
	public String getCreationdate() {
		return iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	String	with the value for the Modificationdate column.
	 */
	public String getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Specsearchhitid' column
	 * 
	 * @param	aSpecsearchhitid	long with the value for the Specsearchhitid column.
	 */
	public void setSpecsearchhitid(long aSpecsearchhitid) {
        iSpecsearchhitid = aSpecsearchhitid;
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
	 * This method sets the value for the 'Fk_libspectrumid' column
	 * 
	 * @param	aFk_libspectrumid	long with the value for the Fk_libspectrumid column.
	 */
	public void setFk_libspectrumid(long aFk_libspectrumid) {
        iFk_libspectrumid = aFk_libspectrumid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Similarity' column
	 * 
	 * @param	aSimilarity	Number with the value for the Similarity column.
	 */
	public void setSimilarity(Number aSimilarity) {
        iSimilarity = aSimilarity;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	String with the value for the Creationdate column.
	 */
	public void setCreationdate(String aCreationdate) {
        iCreationdate = aCreationdate;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	String with the value for the Modificationdate column.
	 */
	public void setModificationdate(String aModificationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM specsearchhit WHERE specsearchhitid = ?");
		lStat.setLong(1, this.iSpecsearchhitid);
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
	public void retrieve(Connection aConn, @SuppressWarnings("rawtypes") HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(SpecsearchhitTableAccessor.SPECSEARCHHITID)) {
			throw new IllegalArgumentException("Primary key field 'SPECSEARCHHITID' is missing in HashMap!");
		} else {
            this.iSpecsearchhitid = ((Long)aKeys.get(SpecsearchhitTableAccessor.SPECSEARCHHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM specsearchhit WHERE specsearchhitid = ?");
		lStat.setLong(1, this.iSpecsearchhitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iSpecsearchhitid = lRS.getLong("specsearchhitid");
            this.iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
            this.iFk_libspectrumid = lRS.getLong("fk_libspectrumid");
            this.iSimilarity = (Number)lRS.getObject("similarity");
            this.iCreationdate = (String)lRS.getObject("creationdate");
            this.iModificationdate = (String)lRS.getObject("modificationdate");
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
		ResultSet rs = stat.executeQuery(SpecsearchhitTableAccessor.getBasicSelect());
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
		if(!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE specsearchhit SET specsearchhitid = ?, fk_searchspectrumid = ?, fk_libspectrumid = ?, similarity = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE specsearchhitid = ?");
		lStat.setLong(1, this.iSpecsearchhitid);
		lStat.setLong(2, this.iFk_searchspectrumid);
		lStat.setLong(3, this.iFk_libspectrumid);
		lStat.setObject(4, this.iSimilarity);
		lStat.setObject(5, this.iCreationdate);
		lStat.setLong(6, this.iSpecsearchhitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO specsearchhit (specsearchhitid, fk_searchspectrumid, fk_libspectrumid, similarity, creationdate, modificationdate) values(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(this.iSpecsearchhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iSpecsearchhitid);
		}
		if(this.iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, this.iFk_searchspectrumid);
		}
		if(this.iFk_libspectrumid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, this.iFk_libspectrumid);
		}
		if(this.iSimilarity == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, this.iSimilarity);
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
            this.iSpecsearchhitid = ((Number) this.iKeys[0]).longValue();
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