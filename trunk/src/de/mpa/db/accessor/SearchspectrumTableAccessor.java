/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/01/2012
 * Time: 13:27:13
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
 * This class is a generated accessor for the Searchspectrum table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SearchspectrumTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'spectrumid' column.
	 */
	protected long iSpectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_experimentid' column.
	 */
	protected long iFk_experimentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'filename' column.
	 */
	protected String iFilename = null;


	/**
	 * This variable represents the contents for the 'spectrumname' column.
	 */
	protected String iSpectrumname = null;


	/**
	 * This variable represents the contents for the 'precursor_mz' column.
	 */
	protected Number iPrecursor_mz = null;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'totalintensity' column.
	 */
	protected Number iTotalintensity = null;


	/**
	 * This variable represents the contents for the 'maximumintensity' column.
	 */
	protected Number iMaximumintensity = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'spectrumid' column.
	 */
	public static final String SPECTRUMID = "SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_experimentid' column.
	 */
	public static final String FK_EXPERIMENTID = "FK_EXPERIMENTID";

	/**
	 * This variable represents the key for the 'filename' column.
	 */
	public static final String FILENAME = "FILENAME";

	/**
	 * This variable represents the key for the 'spectrumname' column.
	 */
	public static final String SPECTRUMNAME = "SPECTRUMNAME";

	/**
	 * This variable represents the key for the 'precursor_mz' column.
	 */
	public static final String PRECURSOR_MZ = "PRECURSOR_MZ";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'totalintensity' column.
	 */
	public static final String TOTALINTENSITY = "TOTALINTENSITY";

	/**
	 * This variable represents the key for the 'maximumintensity' column.
	 */
	public static final String MAXIMUMINTENSITY = "MAXIMUMINTENSITY";

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
	public SearchspectrumTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SearchspectrumTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SearchspectrumTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPECTRUMID)) {
			this.iSpectrumid = ((Long)aParams.get(SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_EXPERIMENTID)) {
			this.iFk_experimentid = ((Long)aParams.get(FK_EXPERIMENTID)).longValue();
		}
		if(aParams.containsKey(FILENAME)) {
			this.iFilename = (String)aParams.get(FILENAME);
		}
		if(aParams.containsKey(SPECTRUMNAME)) {
			this.iSpectrumname = (String)aParams.get(SPECTRUMNAME);
		}
		if(aParams.containsKey(PRECURSOR_MZ)) {
			this.iPrecursor_mz = (Number)aParams.get(PRECURSOR_MZ);
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		if(aParams.containsKey(TOTALINTENSITY)) {
			this.iTotalintensity = (Number)aParams.get(TOTALINTENSITY);
		}
		if(aParams.containsKey(MAXIMUMINTENSITY)) {
			this.iMaximumintensity = (Number)aParams.get(MAXIMUMINTENSITY);
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
	 * This constructor allows the creation of the 'SearchspectrumTableAccessor' object based on a resultset
	 * obtained by a 'select * from Searchspectrum' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SearchspectrumTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpectrumid = aResultSet.getLong("spectrumid");
		this.iFk_experimentid = aResultSet.getLong("fk_experimentid");
		this.iFilename = (String)aResultSet.getObject("filename");
		this.iSpectrumname = (String)aResultSet.getObject("spectrumname");
		this.iPrecursor_mz = (Number)aResultSet.getObject("precursor_mz");
		this.iCharge = aResultSet.getLong("charge");
		this.iTotalintensity = (Number)aResultSet.getObject("totalintensity");
		this.iMaximumintensity = (Number)aResultSet.getObject("maximumintensity");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Spectrumid' column
	 * 
	 * @return	long	with the value for the Spectrumid column.
	 */
	public long getSpectrumid() {
		return this.iSpectrumid;
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
	 * This method returns the value for the 'Filename' column
	 * 
	 * @return	String	with the value for the Filename column.
	 */
	public String getFilename() {
		return this.iFilename;
	}

	/**
	 * This method returns the value for the 'Spectrumname' column
	 * 
	 * @return	String	with the value for the Spectrumname column.
	 */
	public String getSpectrumname() {
		return this.iSpectrumname;
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
	 * This method returns the value for the 'Charge' column
	 * 
	 * @return	long	with the value for the Charge column.
	 */
	public long getCharge() {
		return this.iCharge;
	}

	/**
	 * This method returns the value for the 'Totalintensity' column
	 * 
	 * @return	Number	with the value for the Totalintensity column.
	 */
	public Number getTotalintensity() {
		return this.iTotalintensity;
	}

	/**
	 * This method returns the value for the 'Maximumintensity' column
	 * 
	 * @return	Number	with the value for the Maximumintensity column.
	 */
	public Number getMaximumintensity() {
		return this.iMaximumintensity;
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
	 * This method sets the value for the 'Spectrumid' column
	 * 
	 * @param	aSpectrumid	long with the value for the Spectrumid column.
	 */
	public void setSpectrumid(long aSpectrumid) {
		this.iSpectrumid = aSpectrumid;
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
	 * This method sets the value for the 'Filename' column
	 * 
	 * @param	aFilename	String with the value for the Filename column.
	 */
	public void setFilename(String aFilename) {
		this.iFilename = aFilename;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Spectrumname' column
	 * 
	 * @param	aSpectrumname	String with the value for the Spectrumname column.
	 */
	public void setSpectrumname(String aSpectrumname) {
		this.iSpectrumname = aSpectrumname;
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
	 * This method sets the value for the 'Charge' column
	 * 
	 * @param	aCharge	long with the value for the Charge column.
	 */
	public void setCharge(long aCharge) {
		this.iCharge = aCharge;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Totalintensity' column
	 * 
	 * @param	aTotalintensity	Number with the value for the Totalintensity column.
	 */
	public void setTotalintensity(Number aTotalintensity) {
		this.iTotalintensity = aTotalintensity;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Maximumintensity' column
	 * 
	 * @param	aMaximumintensity	Number with the value for the Maximumintensity column.
	 */
	public void setMaximumintensity(Number aMaximumintensity) {
		this.iMaximumintensity = aMaximumintensity;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM searchspectrum WHERE spectrumid = ?");
		lStat.setLong(1, iSpectrumid);
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
		if(!aKeys.containsKey(SPECTRUMID)) {
			throw new IllegalArgumentException("Primary key field 'SPECTRUMID' is missing in HashMap!");
		} else {
			iSpectrumid = ((Long)aKeys.get(SPECTRUMID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM searchspectrum WHERE spectrumid = ?");
		lStat.setLong(1, iSpectrumid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpectrumid = lRS.getLong("spectrumid");
			iFk_experimentid = lRS.getLong("fk_experimentid");
			iFilename = (String)lRS.getObject("filename");
			iSpectrumname = (String)lRS.getObject("spectrumname");
			iPrecursor_mz = (Number)lRS.getObject("precursor_mz");
			iCharge = lRS.getLong("charge");
			iTotalintensity = (Number)lRS.getObject("totalintensity");
			iMaximumintensity = (Number)lRS.getObject("maximumintensity");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'searchspectrum' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'searchspectrum' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from searchspectrum";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SearchspectrumTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SearchspectrumTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SearchspectrumTableAccessor>  entities = new ArrayList<SearchspectrumTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SearchspectrumTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE searchspectrum SET spectrumid = ?, fk_experimentid = ?, filename = ?, spectrumname = ?, precursor_mz = ?, charge = ?, totalintensity = ?, maximumintensity = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE spectrumid = ?");
		lStat.setLong(1, iSpectrumid);
		lStat.setLong(2, iFk_experimentid);
		lStat.setObject(3, iFilename);
		lStat.setObject(4, iSpectrumname);
		lStat.setObject(5, iPrecursor_mz);
		lStat.setLong(6, iCharge);
		lStat.setObject(7, iTotalintensity);
		lStat.setObject(8, iMaximumintensity);
		lStat.setObject(9, iCreationdate);
		lStat.setLong(10, iSpectrumid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO searchspectrum (spectrumid, fk_experimentid, filename, spectrumname, precursor_mz, charge, totalintensity, maximumintensity, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iSpectrumid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpectrumid);
		}
		if(iFk_experimentid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_experimentid);
		}
		if(iFilename == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iFilename);
		}
		if(iSpectrumname == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iSpectrumname);
		}
		if(iPrecursor_mz == null) {
			lStat.setNull(5, 3);
		} else {
			lStat.setObject(5, iPrecursor_mz);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, iCharge);
		}
		if(iTotalintensity == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, iTotalintensity);
		}
		if(iMaximumintensity == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iMaximumintensity);
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
			iSpectrumid = ((Number) iKeys[0]).longValue();
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