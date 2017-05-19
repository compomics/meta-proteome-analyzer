/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:59
 */
package de.mpa.db.mysql.accessor;

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
 * This class is a generated accessor for the Peptide table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class PeptideTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'peptideid' column.
	 */
	protected long iPeptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'sequence' column.
	 */
	protected String iSequence;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected Timestamp iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected Timestamp iModificationdate;


	/**
	 * This variable represents the key for the 'peptideid' column.
	 */
	public static final String PEPTIDEID = "PEPTIDEID";

	/**
	 * This variable represents the key for the 'sequence' column.
	 */
	public static final String SEQUENCE = "SEQUENCE";

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
	public PeptideTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'PeptideTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public PeptideTableAccessor(HashMap aParams) {
		if(aParams.containsKey(PeptideTableAccessor.PEPTIDEID)) {
            iPeptideid = ((Long)aParams.get(PeptideTableAccessor.PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(PeptideTableAccessor.SEQUENCE)) {
            iSequence = (String)aParams.get(PeptideTableAccessor.SEQUENCE);
		}
		if(aParams.containsKey(PeptideTableAccessor.CREATIONDATE)) {
            iCreationdate = (Timestamp)aParams.get(PeptideTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(PeptideTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (Timestamp)aParams.get(PeptideTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'PeptideTableAccessor' object based on a resultset
	 * obtained by a 'select * from Peptide' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public PeptideTableAccessor(ResultSet aResultSet) throws SQLException {
        iPeptideid = aResultSet.getLong("peptideid");
        iSequence = (String)aResultSet.getObject("sequence");
        iCreationdate = (Timestamp)aResultSet.getObject("creationdate");
        iModificationdate = (Timestamp)aResultSet.getObject("modificationdate");

        iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Peptideid' column
	 * 
	 * @return	long	with the value for the Peptideid column.
	 */
	public long getPeptideid() {
		return iPeptideid;
	}

	/**
	 * This method returns the value for the 'Sequence' column
	 * 
	 * @return	String	with the value for the Sequence column.
	 */
	public String getSequence() {
		return iSequence;
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
	 * This method sets the value for the 'Peptideid' column
	 * 
	 * @param	aPeptideid	long with the value for the Peptideid column.
	 */
	public void setPeptideid(long aPeptideid) {
        iPeptideid = aPeptideid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Sequence' column
	 * 
	 * @param	aSequence	String with the value for the Sequence column.
	 */
	public void setSequence(String aSequence) {
        iSequence = aSequence;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM peptide WHERE peptideid = ?");
		lStat.setLong(1, this.iPeptideid);
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
		if(!aKeys.containsKey(PeptideTableAccessor.PEPTIDEID)) {
			throw new IllegalArgumentException("Primary key field 'PEPTIDEID' is missing in HashMap!");
		} else {
            this.iPeptideid = ((Long)aKeys.get(PeptideTableAccessor.PEPTIDEID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM peptide WHERE peptideid = ?");
		lStat.setLong(1, this.iPeptideid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iPeptideid = lRS.getLong("peptideid");
            this.iSequence = (String)lRS.getObject("sequence");
            this.iCreationdate = (Timestamp)lRS.getObject("creationdate");
            this.iModificationdate = (Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'peptide' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'peptide' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from peptide";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<PeptideTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<PeptideTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<PeptideTableAccessor>  entities = new ArrayList<PeptideTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(PeptideTableAccessor.getBasicSelect());
		while(rs.next()) {
			entities.add(new PeptideTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE peptide SET peptideid = ?, sequence = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE peptideid = ?");
		lStat.setLong(1, this.iPeptideid);
		lStat.setObject(2, this.iSequence);
		lStat.setObject(3, this.iCreationdate);
		lStat.setLong(4, this.iPeptideid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO peptide (peptideid, sequence, creationdate, modificationdate) values(?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(this.iPeptideid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iPeptideid);
		}
		if(this.iSequence == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, this.iSequence);
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
            this.iPeptideid = ((Number) this.iKeys[0]).longValue();
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