/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 05/10/2011
 * Time: 13:49:31
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
 * This class is a generated accessor for the Speclibentry table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SpeclibentryTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'speclibid' column.
	 */
	protected long iSpeclibid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_spectrumid' column.
	 */
	protected long iL_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'precursor_mz' column.
	 */
	protected Number iPrecursor_mz = null;


	/**
	 * This variable represents the contents for the 'sequence' column.
	 */
	protected String iSequence = null;


	/**
	 * This variable represents the contents for the 'annotation' column.
	 */
	protected String iAnnotation = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'speclibid' column.
	 */
	public static final String SPECLIBID = "SPECLIBID";

	/**
	 * This variable represents the key for the 'l_spectrumid' column.
	 */
	public static final String L_SPECTRUMID = "L_SPECTRUMID";

	/**
	 * This variable represents the key for the 'precursor_mz' column.
	 */
	public static final String PRECURSOR_MZ = "PRECURSOR_MZ";

	/**
	 * This variable represents the key for the 'sequence' column.
	 */
	public static final String SEQUENCE = "SEQUENCE";

	/**
	 * This variable represents the key for the 'annotation' column.
	 */
	public static final String ANNOTATION = "ANNOTATION";

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
	public SpeclibentryTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SpeclibentryTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SpeclibentryTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPECLIBID)) {
			this.iSpeclibid = ((Long)aParams.get(SPECLIBID)).longValue();
		}
		if(aParams.containsKey(L_SPECTRUMID)) {
			this.iL_spectrumid = ((Long)aParams.get(L_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(PRECURSOR_MZ)) {
			this.iPrecursor_mz = (Number)aParams.get(PRECURSOR_MZ);
		}
		if(aParams.containsKey(SEQUENCE)) {
			this.iSequence = (String)aParams.get(SEQUENCE);
		}
		if(aParams.containsKey(ANNOTATION)) {
			this.iAnnotation = (String)aParams.get(ANNOTATION);
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
	 * This constructor allows the creation of the 'SpeclibentryTableAccessor' object based on a resultset
	 * obtained by a 'select * from Speclibentry' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpeclibentryTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpeclibid = aResultSet.getLong("speclibid");
		this.iL_spectrumid = aResultSet.getLong("l_spectrumid");
		this.iPrecursor_mz = (Number)aResultSet.getObject("precursor_mz");
		this.iSequence = (String)aResultSet.getObject("sequence");
		this.iAnnotation = (String)aResultSet.getObject("annotation");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Speclibid' column
	 * 
	 * @return	long	with the value for the Speclibid column.
	 */
	public long getSpeclibid() {
		return this.iSpeclibid;
	}

	/**
	 * This method returns the value for the 'L_spectrumid' column
	 * 
	 * @return	long	with the value for the L_spectrumid column.
	 */
	public long getL_spectrumid() {
		return this.iL_spectrumid;
	}

	/**
	 * This method returns the value for the 'Precursor_mz' column
	 * 
	 * @return	Number	with the value for the Precursor_mz column.
	 */
	public Number getPrecursor_mz() {
		return this.iPrecursor_mz;
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
	 * This method returns the value for the 'Annotation' column
	 * 
	 * @return	String	with the value for the Annotation column.
	 */
	public String getAnnotation() {
		return this.iAnnotation;
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
	 * This method sets the value for the 'Speclibid' column
	 * 
	 * @param	aSpeclibid	long with the value for the Speclibid column.
	 */
	public void setSpeclibid(long aSpeclibid) {
		this.iSpeclibid = aSpeclibid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_spectrumid' column
	 * 
	 * @param	aL_spectrumid	long with the value for the L_spectrumid column.
	 */
	public void setL_spectrumid(long aL_spectrumid) {
		this.iL_spectrumid = aL_spectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_mz' column
	 * 
	 * @param	aPrecursor_mz	Number with the value for the Precursor_mz column.
	 */
	public void setPrecursor_mz(Number aPrecursor_mz) {
		this.iPrecursor_mz = aPrecursor_mz;
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
	 * This method sets the value for the 'Annotation' column
	 * 
	 * @param	aAnnotation	String with the value for the Annotation column.
	 */
	public void setAnnotation(String aAnnotation) {
		this.iAnnotation = aAnnotation;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM speclibentry WHERE speclibid = ?");
		lStat.setLong(1, iSpeclibid);
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
		if(!aKeys.containsKey(SPECLIBID)) {
			throw new IllegalArgumentException("Primary key field 'SPECLIBID' is missing in HashMap!");
		} else {
			iSpeclibid = ((Long)aKeys.get(SPECLIBID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM speclibentry WHERE speclibid = ?");
		lStat.setLong(1, iSpeclibid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpeclibid = lRS.getLong("speclibid");
			iL_spectrumid = lRS.getLong("l_spectrumid");
			iPrecursor_mz = (Number)lRS.getObject("precursor_mz");
			iSequence = (String)lRS.getObject("sequence");
			iAnnotation = (String)lRS.getObject("annotation");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'speclibentry' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'speclibentry' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from speclibentry";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SpeclibentryTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SpeclibentryTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SpeclibentryTableAccessor>  entities = new ArrayList<SpeclibentryTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SpeclibentryTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE speclibentry SET speclibid = ?, l_spectrumid = ?, precursor_mz = ?, sequence = ?, annotation = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE speclibid = ?");
		lStat.setLong(1, iSpeclibid);
		lStat.setLong(2, iL_spectrumid);
		lStat.setObject(3, iPrecursor_mz);
		lStat.setObject(4, iSequence);
		lStat.setObject(5, iAnnotation);
		lStat.setObject(6, iCreationdate);
		lStat.setLong(7, iSpeclibid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO speclibentry (speclibid, l_spectrumid, precursor_mz, sequence, annotation, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iSpeclibid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpeclibid);
		}
		if(iL_spectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_spectrumid);
		}
		if(iPrecursor_mz == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iPrecursor_mz);
		}
		if(iSequence == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iSequence);
		}
		if(iAnnotation == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iAnnotation);
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
			iSpeclibid = ((Number) iKeys[0]).longValue();
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