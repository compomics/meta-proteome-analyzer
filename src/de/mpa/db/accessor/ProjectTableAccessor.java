/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:40
 */
package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
 * This class is a generated accessor for the Project table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ProjectTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'projectid' column.
	 */
	protected long iProjectid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'title' column.
	 */
	protected String iTitle;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected Timestamp iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected Timestamp iModificationdate;


	/**
	 * This variable represents the key for the 'projectid' column.
	 */
	public static final String PROJECTID = "PROJECTID";

	/**
	 * This variable represents the key for the 'title' column.
	 */
	public static final String TITLE = "TITLE";

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
	public ProjectTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(ProjectTableAccessor.PROJECTID)) {
            iProjectid = ((Long)aParams.get(ProjectTableAccessor.PROJECTID)).longValue();
		}
		if(aParams.containsKey(ProjectTableAccessor.TITLE)) {
            iTitle = (String)aParams.get(ProjectTableAccessor.TITLE);
		}
		if(aParams.containsKey(ProjectTableAccessor.CREATIONDATE)) {
            iCreationdate = (Timestamp)aParams.get(ProjectTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(ProjectTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (Timestamp)aParams.get(ProjectTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ProjectTableAccessor' object based on a resultset
	 * obtained by a 'select * from Project' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ProjectTableAccessor(ResultSet aResultSet) throws SQLException {
        iProjectid = aResultSet.getLong("projectid");
        iTitle = (String)aResultSet.getObject("title");
        iCreationdate = (Timestamp)aResultSet.getObject("creationdate");
        iModificationdate = (Timestamp)aResultSet.getObject("modificationdate");

        iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Projectid' column
	 * 
	 * @return	long	with the value for the Projectid column.
	 */
	public long getProjectid() {
		return iProjectid;
	}

	/**
	 * This method returns the value for the 'Title' column
	 * 
	 * @return	String	with the value for the Title column.
	 */
	public String getTitle() {
		return iTitle;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public Timestamp getCreationdate() {
		return iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public Timestamp getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Projectid' column
	 * 
	 * @param	aProjectid	long with the value for the Projectid column.
	 */
	public void setProjectid(long aProjectid) {
        iProjectid = aProjectid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Title' column
	 * 
	 * @param	aTitle	String with the value for the Title column.
	 */
	public void setTitle(String aTitle) {
        iTitle = aTitle;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(Timestamp aCreationdate) {
        iCreationdate = aCreationdate;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(Timestamp aModificationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM project WHERE projectid = ?");
		lStat.setLong(1, this.iProjectid);
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
		if(!aKeys.containsKey(ProjectTableAccessor.PROJECTID)) {
			throw new IllegalArgumentException("Primary key field 'PROJECTID' is missing in HashMap!");
		} else {
            this.iProjectid = ((Long)aKeys.get(ProjectTableAccessor.PROJECTID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM project WHERE projectid = ?");
		lStat.setLong(1, this.iProjectid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iProjectid = lRS.getLong("projectid");
            this.iTitle = (String)lRS.getObject("title");
            this.iCreationdate = (Timestamp)lRS.getObject("creationdate");
            this.iModificationdate = (Timestamp)lRS.getObject("modificationdate");
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
		ResultSet rs = stat.executeQuery(ProjectTableAccessor.getBasicSelect());
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
		if(!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE project SET projectid = ?, title = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE projectid = ?");
		lStat.setLong(1, this.iProjectid);
		lStat.setObject(2, this.iTitle);
		lStat.setObject(3, this.iCreationdate);
		lStat.setLong(4, this.iProjectid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO project (projectid, title, creationdate, modificationdate) values(?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(this.iProjectid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iProjectid);
		}
		if(this.iTitle == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, this.iTitle);
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
            this.iProjectid = ((Number) this.iKeys[0]).longValue();
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