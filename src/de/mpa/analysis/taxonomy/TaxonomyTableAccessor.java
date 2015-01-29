package de.mpa.analysis.taxonomy;

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

/**
 * This class is a generated accessor for the Taxonomy table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class TaxonomyTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'taxonomyid' column.
	 */
	protected long iTaxonomyid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'parentid' column.
	 */
	protected long iParentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'description' column.
	 */
	protected String iDescription = null;


	/**
	 * This variable represents the contents for the 'rank' column.
	 */
	protected String iRank = null;


	/**
	 * This variable represents the key for the 'taxonomyid' column.
	 */
	public static final String TAXONOMYID = "TAXONOMYID";

	/**
	 * This variable represents the key for the 'parentid' column.
	 */
	public static final String PARENTID = "PARENTID";

	/**
	 * This variable represents the key for the 'description' column.
	 */
	public static final String DESCRIPTION = "DESCRIPTION";

	/**
	 * This variable represents the key for the 'rank' column.
	 */
	public static final String RANK = "RANK";




	/**
	 * Default constructor.
	 */
	public TaxonomyTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'TaxonomyTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public TaxonomyTableAccessor(HashMap aParams) {
		if(aParams.containsKey(TAXONOMYID)) {
			this.iTaxonomyid = ((Long)aParams.get(TAXONOMYID)).longValue();
		}
		if(aParams.containsKey(PARENTID)) {
			this.iParentid = ((Long)aParams.get(PARENTID)).longValue();
		}
		if(aParams.containsKey(DESCRIPTION)) {
			this.iDescription = (String)aParams.get(DESCRIPTION);
		}
		if(aParams.containsKey(RANK)) {
			this.iRank = (String)aParams.get(RANK);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'TaxonomyTableAccessor' object based on a resultset
	 * obtained by a 'select * from Taxonomy' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public TaxonomyTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iTaxonomyid = aResultSet.getLong("taxonomyid");
		this.iParentid = aResultSet.getLong("parentid");
		this.iDescription = (String)aResultSet.getObject("description");
		this.iRank = (String)aResultSet.getObject("rank");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Taxonomyid' column
	 * 
	 * @return	long	with the value for the Taxonomyid column.
	 */
	public long getTaxonomyid() {
		return this.iTaxonomyid;
	}

	/**
	 * This method returns the value for the 'Parentid' column
	 * 
	 * @return	long	with the value for the Parentid column.
	 */
	public long getParentid() {
		return this.iParentid;
	}

	/**
	 * This method returns the value for the 'Description' column
	 * 
	 * @return	String	with the value for the Description column.
	 */
	public String getDescription() {
		return this.iDescription;
	}

	/**
	 * This method returns the value for the 'Rank' column
	 * 
	 * @return	String	with the value for the Rank column.
	 */
	public String getRank() {
		return this.iRank;
	}

	/**
	 * This method sets the value for the 'Taxonomyid' column
	 * 
	 * @param	aTaxonomyid	long with the value for the Taxonomyid column.
	 */
	public void setTaxonomyid(long aTaxonomyid) {
		this.iTaxonomyid = aTaxonomyid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Parentid' column
	 * 
	 * @param	aParentid	long with the value for the Parentid column.
	 */
	public void setParentid(long aParentid) {
		this.iParentid = aParentid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Description' column
	 * 
	 * @param	aDescription	String with the value for the Description column.
	 */
	public void setDescription(String aDescription) {
		this.iDescription = aDescription;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Rank' column
	 * 
	 * @param	aRank	String with the value for the Rank column.
	 */
	public void setRank(String aRank) {
		this.iRank = aRank;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM taxonomy WHERE taxonomyid = ?");
		lStat.setLong(1, iTaxonomyid);
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
		if(!aKeys.containsKey(TAXONOMYID)) {
			throw new IllegalArgumentException("Primary key field 'TAXONOMYID' is missing in HashMap!");
		} else {
			iTaxonomyid = ((Long)aKeys.get(TAXONOMYID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM taxonomy WHERE taxonomyid = ?");
		lStat.setLong(1, iTaxonomyid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iTaxonomyid = lRS.getLong("taxonomyid");
			iParentid = lRS.getLong("parentid");
			iDescription = (String)lRS.getObject("description");
			iRank = (String)lRS.getObject("rank");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'taxonomy' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'taxonomy' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from taxonomy";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<TaxonomyTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<TaxonomyTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<TaxonomyTableAccessor>  entities = new ArrayList<TaxonomyTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new TaxonomyTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE taxonomy SET taxonomyid = ?, parentid = ?, description = ?, rank = ? WHERE taxonomyid = ?");
		lStat.setLong(1, iTaxonomyid);
		lStat.setLong(2, iParentid);
		lStat.setObject(3, iDescription);
		lStat.setObject(4, iRank);
		lStat.setLong(5, iTaxonomyid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO taxonomy (taxonomyid, parentid, description, rank) values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		if(iTaxonomyid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iTaxonomyid);
		}
		if(iParentid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iParentid);
		}
		if(iDescription == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iDescription);
		}
		if(iRank == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iRank);
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
			iTaxonomyid = ((Number) iKeys[0]).longValue();
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