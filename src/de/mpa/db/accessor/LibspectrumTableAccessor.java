/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 29/11/2011
 * Time: 14:13:49
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
 * This class is a generated accessor for the Libspectrum table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class LibspectrumTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'libspectrumid' column.
	 */
	protected long iLibspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_experimentid' column.
	 */
	protected long iFk_experimentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_consensusspecid' column.
	 */
	protected long iFk_consensusspecid = Long.MIN_VALUE;


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
	 * This variable represents the key for the 'libspectrumid' column.
	 */
	public static final String LIBSPECTRUMID = "LIBSPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_experimentid' column.
	 */
	public static final String FK_EXPERIMENTID = "FK_EXPERIMENTID";

	/**
	 * This variable represents the key for the 'fk_consensusspecid' column.
	 */
	public static final String FK_CONSENSUSSPECID = "FK_CONSENSUSSPECID";

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
	public LibspectrumTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'LibspectrumTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public LibspectrumTableAccessor(HashMap aParams) {
		if(aParams.containsKey(LIBSPECTRUMID)) {
			this.iLibspectrumid = ((Long)aParams.get(LIBSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_EXPERIMENTID)) {
			this.iFk_experimentid = ((Long)aParams.get(FK_EXPERIMENTID)).longValue();
		}
		if(aParams.containsKey(FK_CONSENSUSSPECID)) {
			this.iFk_consensusspecid = ((Long)aParams.get(FK_CONSENSUSSPECID)).longValue();
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
	 * This constructor allows the creation of the 'LibspectrumTableAccessor' object based on a resultset
	 * obtained by a 'select * from Libspectrum' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public LibspectrumTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iLibspectrumid = aResultSet.getLong("libspectrumid");
		this.iFk_experimentid = aResultSet.getLong("fk_experimentid");
		this.iFk_consensusspecid = aResultSet.getLong("fk_consensusspecid");
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
	 * This method returns the value for the 'Libspectrumid' column
	 * 
	 * @return	long	with the value for the Libspectrumid column.
	 */
	public long getLibspectrumid() {
		return this.iLibspectrumid;
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
	 * This method returns the value for the 'Fk_consensusspecid' column
	 * 
	 * @return	long	with the value for the Fk_consensusspecid column.
	 */
	public long getFk_consensusspecid() {
		return this.iFk_consensusspecid;
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
	 * This method sets the value for the 'Libspectrumid' column
	 * 
	 * @param	aLibspectrumid	long with the value for the Libspectrumid column.
	 */
	public void setLibspectrumid(long aLibspectrumid) {
		this.iLibspectrumid = aLibspectrumid;
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
	 * This method sets the value for the 'Fk_consensusspecid' column
	 * 
	 * @param	aFk_consensusspecid	long with the value for the Fk_consensusspecid column.
	 */
	public void setFk_consensusspecid(long aFk_consensusspecid) {
		this.iFk_consensusspecid = aFk_consensusspecid;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM libspectrum WHERE libspectrumid = ?");
		lStat.setLong(1, iLibspectrumid);
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
		if(!aKeys.containsKey(LIBSPECTRUMID)) {
			throw new IllegalArgumentException("Primary key field 'LIBSPECTRUMID' is missing in HashMap!");
		} else {
			iLibspectrumid = ((Long)aKeys.get(LIBSPECTRUMID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM libspectrum WHERE libspectrumid = ?");
		lStat.setLong(1, iLibspectrumid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iLibspectrumid = lRS.getLong("libspectrumid");
			iFk_experimentid = lRS.getLong("fk_experimentid");
			iFk_consensusspecid = lRS.getLong("fk_consensusspecid");
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
			throw new SQLException("More than one hit found for the specified primary keys in the 'libspectrum' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'libspectrum' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from libspectrum";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<LibspectrumTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<LibspectrumTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<LibspectrumTableAccessor>  entities = new ArrayList<LibspectrumTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new LibspectrumTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE libspectrum SET libspectrumid = ?, fk_experimentid = ?, fk_consensusspecid = ?, filename = ?, spectrumname = ?, precursor_mz = ?, charge = ?, totalintensity = ?, maximumintensity = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE libspectrumid = ?");
		lStat.setLong(1, iLibspectrumid);
		lStat.setLong(2, iFk_experimentid);
		lStat.setLong(3, iFk_consensusspecid);
		lStat.setObject(4, iFilename);
		lStat.setObject(5, iSpectrumname);
		lStat.setObject(6, iPrecursor_mz);
		lStat.setLong(7, iCharge);
		lStat.setObject(8, iTotalintensity);
		lStat.setObject(9, iMaximumintensity);
		lStat.setObject(10, iCreationdate);
		lStat.setLong(11, iLibspectrumid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO libspectrum (libspectrumid, fk_experimentid, fk_consensusspecid, filename, spectrumname, precursor_mz, charge, totalintensity, maximumintensity, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iLibspectrumid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iLibspectrumid);
		}
		if(iFk_experimentid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_experimentid);
		}
		if(iFk_consensusspecid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_consensusspecid);
		}
		if(iFilename == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iFilename);
		}
		if(iSpectrumname == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iSpectrumname);
		}
		if(iPrecursor_mz == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iPrecursor_mz);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(7, 4);
		} else {
			lStat.setLong(7, iCharge);
		}
		if(iTotalintensity == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iTotalintensity);
		}
		if(iMaximumintensity == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iMaximumintensity);
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
			iLibspectrumid = ((Number) iKeys[0]).longValue();
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