/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/12/2011
 * Time: 15:03:17
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
 * This class is a generated accessor for the Experiment table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ExperimentTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'experimentid' column.
	 */
	protected long iExperimentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_projectid' column.
	 */
	protected long iFk_projectid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'instrument' column.
	 */
	protected String iInstrument = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'experimentid' column.
	 */
	public static final String EXPERIMENTID = "EXPERIMENTID";

	/**
	 * This variable represents the key for the 'fk_projectid' column.
	 */
	public static final String FK_PROJECTID = "FK_PROJECTID";

	/**
	 * This variable represents the key for the 'instrument' column.
	 */
	public static final String INSTRUMENT = "INSTRUMENT";

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
	public ExperimentTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ExperimentTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ExperimentTableAccessor(HashMap aParams) {
		if(aParams.containsKey(EXPERIMENTID)) {
			this.iExperimentid = ((Long)aParams.get(EXPERIMENTID)).longValue();
		}
		if(aParams.containsKey(FK_PROJECTID)) {
			this.iFk_projectid = ((Long)aParams.get(FK_PROJECTID)).longValue();
		}
		if(aParams.containsKey(INSTRUMENT)) {
			this.iInstrument = (String)aParams.get(INSTRUMENT);
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
	 * This constructor allows the creation of the 'ExperimentTableAccessor' object based on a resultset
	 * obtained by a 'select * from Experiment' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ExperimentTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iExperimentid = aResultSet.getLong("experimentid");
		this.iFk_projectid = aResultSet.getLong("fk_projectid");
		this.iInstrument = (String)aResultSet.getObject("instrument");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Experimentid' column
	 * 
	 * @return	long	with the value for the Experimentid column.
	 */
	public long getExperimentid() {
		return this.iExperimentid;
	}

	/**
	 * This method returns the value for the 'Fk_projectid' column
	 * 
	 * @return	long	with the value for the Fk_projectid column.
	 */
	public long getFk_projectid() {
		return this.iFk_projectid;
	}

	/**
	 * This method returns the value for the 'Instrument' column
	 * 
	 * @return	String	with the value for the Instrument column.
	 */
	public String getInstrument() {
		return this.iInstrument;
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
	 * This method sets the value for the 'Experimentid' column
	 * 
	 * @param	aExperimentid	long with the value for the Experimentid column.
	 */
	public void setExperimentid(long aExperimentid) {
		this.iExperimentid = aExperimentid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_projectid' column
	 * 
	 * @param	aFk_projectid	long with the value for the Fk_projectid column.
	 */
	public void setFk_projectid(long aFk_projectid) {
		this.iFk_projectid = aFk_projectid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Instrument' column
	 * 
	 * @param	aInstrument	String with the value for the Instrument column.
	 */
	public void setInstrument(String aInstrument) {
		this.iInstrument = aInstrument;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM experiment WHERE experimentid = ?");
		lStat.setLong(1, iExperimentid);
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
		if(!aKeys.containsKey(EXPERIMENTID)) {
			throw new IllegalArgumentException("Primary key field 'EXPERIMENTID' is missing in HashMap!");
		} else {
			iExperimentid = ((Long)aKeys.get(EXPERIMENTID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM experiment WHERE experimentid = ?");
		lStat.setLong(1, iExperimentid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iExperimentid = lRS.getLong("experimentid");
			iFk_projectid = lRS.getLong("fk_projectid");
			iInstrument = (String)lRS.getObject("instrument");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'experiment' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'experiment' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from experiment";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ExperimentTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ExperimentTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ExperimentTableAccessor>  entities = new ArrayList<ExperimentTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ExperimentTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE experiment SET experimentid = ?, fk_projectid = ?, instrument = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE experimentid = ?");
		lStat.setLong(1, iExperimentid);
		lStat.setLong(2, iFk_projectid);
		lStat.setObject(3, iInstrument);
		lStat.setObject(4, iCreationdate);
		lStat.setLong(5, iExperimentid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO experiment (experimentid, fk_projectid, instrument, creationdate, modificationdate) values(?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iExperimentid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iExperimentid);
		}
		if(iFk_projectid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_projectid);
		}
		if(iInstrument == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iInstrument);
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
			iExperimentid = ((Number) iKeys[0]).longValue();
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