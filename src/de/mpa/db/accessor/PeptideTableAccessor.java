/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/12/2011
 * Time: 15:03:19
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
 * This class is a generated accessor for the Peptide table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class PeptideTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'peptideid' column.
	 */
	protected long iPeptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'sequence' column.
	 */
	protected String iSequence = null;


	/**
	 * This variable represents the key for the 'peptideid' column.
	 */
	public static final String PEPTIDEID = "PEPTIDEID";

	/**
	 * This variable represents the key for the 'sequence' column.
	 */
	public static final String SEQUENCE = "SEQUENCE";




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
		if(aParams.containsKey(PEPTIDEID)) {
			this.iPeptideid = ((Long)aParams.get(PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(SEQUENCE)) {
			this.iSequence = (String)aParams.get(SEQUENCE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'PeptideTableAccessor' object based on a resultset
	 * obtained by a 'select * from Peptide' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public PeptideTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iPeptideid = aResultSet.getLong("peptideid");
		this.iSequence = (String)aResultSet.getObject("sequence");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Peptideid' column
	 * 
	 * @return	long	with the value for the Peptideid column.
	 */
	public long getPeptideid() {
		return this.iPeptideid;
	}

	/**
	 * This method returns the value for the 'Sequence' column
	 * 
	 * @return	String	with the value for the Sequence column.
	 */
	public String getSequence() {
		return this.iSequence;
	}

	/**
	 * This method sets the value for the 'Peptideid' column
	 * 
	 * @param	aPeptideid	long with the value for the Peptideid column.
	 */
	public void setPeptideid(long aPeptideid) {
		this.iPeptideid = aPeptideid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Sequence' column
	 * 
	 * @param	aSequence	String with the value for the Sequence column.
	 */
	public void setSequence(String aSequence) {
		this.iSequence = aSequence;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM peptide WHERE peptideid = ?");
		lStat.setLong(1, iPeptideid);
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
		if(!aKeys.containsKey(PEPTIDEID)) {
			throw new IllegalArgumentException("Primary key field 'PEPTIDEID' is missing in HashMap!");
		} else {
			iPeptideid = ((Long)aKeys.get(PEPTIDEID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM peptide WHERE peptideid = ?");
		lStat.setLong(1, iPeptideid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iPeptideid = lRS.getLong("peptideid");
			iSequence = (String)lRS.getObject("sequence");
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
		ResultSet rs = stat.executeQuery(getBasicSelect());
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
		if(!this.iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE peptide SET peptideid = ?, sequence = ? WHERE peptideid = ?");
		lStat.setLong(1, iPeptideid);
		lStat.setObject(2, iSequence);
		lStat.setLong(3, iPeptideid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO peptide (peptideid, sequence) values(?, ?)");
		if(iPeptideid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iPeptideid);
		}
		if(iSequence == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iSequence);
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
			iPeptideid = ((Number) iKeys[0]).longValue();
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