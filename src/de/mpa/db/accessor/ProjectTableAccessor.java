/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 05/10/2011
 * Time: 10:34:53
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
 * This class is a generated accessor for the Project table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ProjectTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'projectid' column.
	 */
	protected long iProjectid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'title' column.
	 */
	protected String iTitle = null;


	/**
	 * This variable represents the contents for the 'taxon' column.
	 */
	protected String iTaxon = null;


	/**
	 * This variable represents the contents for the 'fragment_tol' column.
	 */
	protected Number iFragment_tol = null;


	/**
	 * This variable represents the contents for the 'precursor_tol' column.
	 */
	protected Number iPrecursor_tol = null;


	/**
	 * This variable represents the contents for the 'precursor_unit' column.
	 */
	protected String iPrecursor_unit = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'projectid' column.
	 */
	public static final String PROJECTID = "PROJECTID";

	/**
	 * This variable represents the key for the 'title' column.
	 */
	public static final String TITLE = "TITLE";

	/**
	 * This variable represents the key for the 'taxon' column.
	 */
	public static final String TAXON = "TAXON";

	/**
	 * This variable represents the key for the 'fragment_tol' column.
	 */
	public static final String FRAGMENT_TOL = "FRAGMENT_TOL";

	/**
	 * This variable represents the key for the 'precursor_tol' column.
	 */
	public static final String PRECURSOR_TOL = "PRECURSOR_TOL";

	/**
	 * This variable represents the key for the 'precursor_unit' column.
	 */
	public static final String PRECURSOR_UNIT = "PRECURSOR_UNIT";

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
	public ProjectTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ProjectTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ProjectTableAccessor(HashMap aParams) {
		if(aParams.containsKey(PROJECTID)) {
			this.iProjectid = ((Long)aParams.get(PROJECTID)).longValue();
		}
		if(aParams.containsKey(TITLE)) {
			this.iTitle = (String)aParams.get(TITLE);
		}
		if(aParams.containsKey(TAXON)) {
			this.iTaxon = (String)aParams.get(TAXON);
		}
		if(aParams.containsKey(FRAGMENT_TOL)) {
			this.iFragment_tol = (Number)aParams.get(FRAGMENT_TOL);
		}
		if(aParams.containsKey(PRECURSOR_TOL)) {
			this.iPrecursor_tol = (Number)aParams.get(PRECURSOR_TOL);
		}
		if(aParams.containsKey(PRECURSOR_UNIT)) {
			this.iPrecursor_unit = (String)aParams.get(PRECURSOR_UNIT);
		}
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (java.sql.Timestamp)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (java.sql.Timestamp)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ProjectTableAccessor' object based on a resultset
	 * obtained by a 'select * from Project' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ProjectTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iProjectid = aResultSet.getLong("projectid");
		this.iTitle = (String)aResultSet.getObject("title");
		this.iTaxon = (String)aResultSet.getObject("taxon");
		this.iFragment_tol = (Number)aResultSet.getObject("fragment_tol");
		this.iPrecursor_tol = (Number)aResultSet.getObject("precursor_tol");
		this.iPrecursor_unit = (String)aResultSet.getObject("precursor_unit");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Projectid' column
	 * 
	 * @return	long	with the value for the Projectid column.
	 */
	public long getProjectid() {
		return this.iProjectid;
	}

	/**
	 * This method returns the value for the 'Title' column
	 * 
	 * @return	String	with the value for the Title column.
	 */
	public String getTitle() {
		return this.iTitle;
	}

	/**
	 * This method returns the value for the 'Taxon' column
	 * 
	 * @return	String	with the value for the Taxon column.
	 */
	public String getTaxon() {
		return this.iTaxon;
	}

	/**
	 * This method returns the value for the 'Fragment_tol' column
	 * 
	 * @return	Number	with the value for the Fragment_tol column.
	 */
	public Number getFragment_tol() {
		return this.iFragment_tol;
	}

	/**
	 * This method returns the value for the 'Precursor_tol' column
	 * 
	 * @return	Number	with the value for the Precursor_tol column.
	 */
	public Number getPrecursor_tol() {
		return this.iPrecursor_tol;
	}

	/**
	 * This method returns the value for the 'Precursor_unit' column
	 * 
	 * @return	String	with the value for the Precursor_unit column.
	 */
	public String getPrecursor_unit() {
		return this.iPrecursor_unit;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public java.sql.Timestamp getCreationdate() {
		return this.iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public java.sql.Timestamp getModificationdate() {
		return this.iModificationdate;
	}

	/**
	 * This method sets the value for the 'Projectid' column
	 * 
	 * @param	aProjectid	long with the value for the Projectid column.
	 */
	public void setProjectid(long aProjectid) {
		this.iProjectid = aProjectid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Title' column
	 * 
	 * @param	aTitle	String with the value for the Title column.
	 */
	public void setTitle(String aTitle) {
		this.iTitle = aTitle;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Taxon' column
	 * 
	 * @param	aTaxon	String with the value for the Taxon column.
	 */
	public void setTaxon(String aTaxon) {
		this.iTaxon = aTaxon;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fragment_tol' column
	 * 
	 * @param	aFragment_tol	Number with the value for the Fragment_tol column.
	 */
	public void setFragment_tol(Number aFragment_tol) {
		this.iFragment_tol = aFragment_tol;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_tol' column
	 * 
	 * @param	aPrecursor_tol	Number with the value for the Precursor_tol column.
	 */
	public void setPrecursor_tol(Number aPrecursor_tol) {
		this.iPrecursor_tol = aPrecursor_tol;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_unit' column
	 * 
	 * @param	aPrecursor_unit	String with the value for the Precursor_unit column.
	 */
	public void setPrecursor_unit(String aPrecursor_unit) {
		this.iPrecursor_unit = aPrecursor_unit;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(java.sql.Timestamp aCreationdate) {
		this.iCreationdate = aCreationdate;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(java.sql.Timestamp aModificationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM project WHERE projectid = ?");
		lStat.setLong(1, iProjectid);
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
		if(!aKeys.containsKey(PROJECTID)) {
			throw new IllegalArgumentException("Primary key field 'PROJECTID' is missing in HashMap!");
		} else {
			iProjectid = ((Long)aKeys.get(PROJECTID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM project WHERE projectid = ?");
		lStat.setLong(1, iProjectid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iProjectid = lRS.getLong("projectid");
			iTitle = (String)lRS.getObject("title");
			iTaxon = (String)lRS.getObject("taxon");
			iFragment_tol = (Number)lRS.getObject("fragment_tol");
			iPrecursor_tol = (Number)lRS.getObject("precursor_tol");
			iPrecursor_unit = (String)lRS.getObject("precursor_unit");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'project' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'project' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from project";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ProjectTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ProjectTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ProjectTableAccessor>  entities = new ArrayList<ProjectTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ProjectTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE project SET projectid = ?, title = ?, taxon = ?, fragment_tol = ?, precursor_tol = ?, precursor_unit = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE projectid = ?");
		lStat.setLong(1, iProjectid);
		lStat.setObject(2, iTitle);
		lStat.setObject(3, iTaxon);
		lStat.setObject(4, iFragment_tol);
		lStat.setObject(5, iPrecursor_tol);
		lStat.setObject(6, iPrecursor_unit);
		lStat.setObject(7, iCreationdate);
		lStat.setLong(8, iProjectid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO project (projectid, title, taxon, fragment_tol, precursor_tol, precursor_unit, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iProjectid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iProjectid);
		}
		if(iTitle == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iTitle);
		}
		if(iTaxon == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iTaxon);
		}
		if(iFragment_tol == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iFragment_tol);
		}
		if(iPrecursor_tol == null) {
			lStat.setNull(5, 3);
		} else {
			lStat.setObject(5, iPrecursor_tol);
		}
		if(iPrecursor_unit == null) {
			lStat.setNull(6, 12);
		} else {
			lStat.setObject(6, iPrecursor_unit);
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
			iProjectid = ((Number) iKeys[0]).longValue();
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