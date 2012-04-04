/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:26:13
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
 * This class is a generated accessor for the Spectrum table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SpectrumTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

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
	 * This variable represents the contents for the 'title' column.
	 */
	protected String iTitle = null;


	/**
	 * This variable represents the contents for the 'precursor_mz' column.
	 */
	protected Number iPrecursor_mz = null;


	/**
	 * This variable represents the contents for the 'precursor_int' column.
	 */
	protected Number iPrecursor_int = null;


	/**
	 * This variable represents the contents for the 'precursor_charge' column.
	 */
	protected long iPrecursor_charge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'mzarray' column.
	 */
	protected String iMzarray = null;


	/**
	 * This variable represents the contents for the 'intarray' column.
	 */
	protected String iIntarray = null;


	/**
	 * This variable represents the contents for the 'chargearray' column.
	 */
	protected String iChargearray = null;


	/**
	 * This variable represents the contents for the 'total_int' column.
	 */
	protected Number iTotal_int = null;


	/**
	 * This variable represents the contents for the 'maximum_int' column.
	 */
	protected Number iMaximum_int = null;


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
	 * This variable represents the key for the 'title' column.
	 */
	public static final String TITLE = "TITLE";

	/**
	 * This variable represents the key for the 'precursor_mz' column.
	 */
	public static final String PRECURSOR_MZ = "PRECURSOR_MZ";

	/**
	 * This variable represents the key for the 'precursor_int' column.
	 */
	public static final String PRECURSOR_INT = "PRECURSOR_INT";

	/**
	 * This variable represents the key for the 'precursor_charge' column.
	 */
	public static final String PRECURSOR_CHARGE = "PRECURSOR_CHARGE";

	/**
	 * This variable represents the key for the 'mzarray' column.
	 */
	public static final String MZARRAY = "MZARRAY";

	/**
	 * This variable represents the key for the 'intarray' column.
	 */
	public static final String INTARRAY = "INTARRAY";

	/**
	 * This variable represents the key for the 'chargearray' column.
	 */
	public static final String CHARGEARRAY = "CHARGEARRAY";

	/**
	 * This variable represents the key for the 'total_int' column.
	 */
	public static final String TOTAL_INT = "TOTAL_INT";

	/**
	 * This variable represents the key for the 'maximum_int' column.
	 */
	public static final String MAXIMUM_INT = "MAXIMUM_INT";

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
	public SpectrumTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SpectrumTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SpectrumTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPECTRUMID)) {
			this.iSpectrumid = ((Long)aParams.get(SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(TITLE)) {
			this.iTitle = (String)aParams.get(TITLE);
		}
		if(aParams.containsKey(PRECURSOR_MZ)) {
			this.iPrecursor_mz = (Number)aParams.get(PRECURSOR_MZ);
		}
		if(aParams.containsKey(PRECURSOR_INT)) {
			this.iPrecursor_int = (Number)aParams.get(PRECURSOR_INT);
		}
		if(aParams.containsKey(PRECURSOR_CHARGE)) {
			this.iPrecursor_charge = ((Long)aParams.get(PRECURSOR_CHARGE)).longValue();
		}
		if(aParams.containsKey(MZARRAY)) {
			this.iMzarray = (String)aParams.get(MZARRAY);
		}
		if(aParams.containsKey(INTARRAY)) {
			this.iIntarray = (String)aParams.get(INTARRAY);
		}
		if(aParams.containsKey(CHARGEARRAY)) {
			this.iChargearray = (String)aParams.get(CHARGEARRAY);
		}
		if(aParams.containsKey(TOTAL_INT)) {
			this.iTotal_int = (Number)aParams.get(TOTAL_INT);
		}
		if(aParams.containsKey(MAXIMUM_INT)) {
			this.iMaximum_int = (Number)aParams.get(MAXIMUM_INT);
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
	 * This constructor allows the creation of the 'SpectrumTableAccessor' object based on a resultset
	 * obtained by a 'select * from Spectrum' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpectrumTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpectrumid = aResultSet.getLong("spectrumid");
		this.iTitle = (String)aResultSet.getObject("title");
		this.iPrecursor_mz = (Number)aResultSet.getObject("precursor_mz");
		this.iPrecursor_int = (Number)aResultSet.getObject("precursor_int");
		this.iPrecursor_charge = aResultSet.getLong("precursor_charge");
		this.iMzarray = (String)aResultSet.getObject("mzarray");
		this.iIntarray = (String)aResultSet.getObject("intarray");
		this.iChargearray = (String)aResultSet.getObject("chargearray");
		this.iTotal_int = (Number)aResultSet.getObject("total_int");
		this.iMaximum_int = (Number)aResultSet.getObject("maximum_int");
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
	 * This method returns the value for the 'Title' column
	 * 
	 * @return	String	with the value for the Title column.
	 */
	public String getTitle() {
		return this.iTitle;
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
	 * This method returns the value for the 'Precursor_int' column
	 * 
	 * @return	Number	with the value for the Precursor_int column.
	 */
	public Number getPrecursor_int() {
		return this.iPrecursor_int;
	}

	/**
	 * This method returns the value for the 'Precursor_charge' column
	 * 
	 * @return	long	with the value for the Precursor_charge column.
	 */
	public long getPrecursor_charge() {
		return this.iPrecursor_charge;
	}

	/**
	 * This method returns the value for the 'Mzarray' column
	 * 
	 * @return	String	with the value for the Mzarray column.
	 */
	public String getMzarray() {
		return this.iMzarray;
	}

	/**
	 * This method returns the value for the 'Intarray' column
	 * 
	 * @return	String	with the value for the Intarray column.
	 */
	public String getIntarray() {
		return this.iIntarray;
	}

	/**
	 * This method returns the value for the 'Chargearray' column
	 * 
	 * @return	String	with the value for the Chargearray column.
	 */
	public String getChargearray() {
		return this.iChargearray;
	}

	/**
	 * This method returns the value for the 'Total_int' column
	 * 
	 * @return	Number	with the value for the Total_int column.
	 */
	public Number getTotal_int() {
		return this.iTotal_int;
	}

	/**
	 * This method returns the value for the 'Maximum_int' column
	 * 
	 * @return	Number	with the value for the Maximum_int column.
	 */
	public Number getMaximum_int() {
		return this.iMaximum_int;
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
	 * This method sets the value for the 'Title' column
	 * 
	 * @param	aTitle	String with the value for the Title column.
	 */
	public void setTitle(String aTitle) {
		this.iTitle = aTitle;
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
	 * This method sets the value for the 'Precursor_int' column
	 * 
	 * @param	aPrecursor_int	Number with the value for the Precursor_int column.
	 */
	public void setPrecursor_int(Number aPrecursor_int) {
		this.iPrecursor_int = aPrecursor_int;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_charge' column
	 * 
	 * @param	aPrecursor_charge	long with the value for the Precursor_charge column.
	 */
	public void setPrecursor_charge(long aPrecursor_charge) {
		this.iPrecursor_charge = aPrecursor_charge;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Mzarray' column
	 * 
	 * @param	aMzarray	String with the value for the Mzarray column.
	 */
	public void setMzarray(String aMzarray) {
		this.iMzarray = aMzarray;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Intarray' column
	 * 
	 * @param	aIntarray	String with the value for the Intarray column.
	 */
	public void setIntarray(String aIntarray) {
		this.iIntarray = aIntarray;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Chargearray' column
	 * 
	 * @param	aChargearray	String with the value for the Chargearray column.
	 */
	public void setChargearray(String aChargearray) {
		this.iChargearray = aChargearray;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Total_int' column
	 * 
	 * @param	aTotal_int	Number with the value for the Total_int column.
	 */
	public void setTotal_int(Number aTotal_int) {
		this.iTotal_int = aTotal_int;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Maximum_int' column
	 * 
	 * @param	aMaximum_int	Number with the value for the Maximum_int column.
	 */
	public void setMaximum_int(Number aMaximum_int) {
		this.iMaximum_int = aMaximum_int;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM spectrum WHERE spectrumid = ?");
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
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM spectrum WHERE spectrumid = ?");
		lStat.setLong(1, iSpectrumid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpectrumid = lRS.getLong("spectrumid");
			iTitle = (String)lRS.getObject("title");
			iPrecursor_mz = (Number)lRS.getObject("precursor_mz");
			iPrecursor_int = (Number)lRS.getObject("precursor_int");
			iPrecursor_charge = lRS.getLong("precursor_charge");
			iMzarray = (String)lRS.getObject("mzarray");
			iIntarray = (String)lRS.getObject("intarray");
			iChargearray = (String)lRS.getObject("chargearray");
			iTotal_int = (Number)lRS.getObject("total_int");
			iMaximum_int = (Number)lRS.getObject("maximum_int");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'spectrum' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'spectrum' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from spectrum";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SpectrumTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SpectrumTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SpectrumTableAccessor>  entities = new ArrayList<SpectrumTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SpectrumTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE spectrum SET spectrumid = ?, title = ?, precursor_mz = ?, precursor_int = ?, precursor_charge = ?, mzarray = ?, intarray = ?, chargearray = ?, total_int = ?, maximum_int = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE spectrumid = ?");
		lStat.setLong(1, iSpectrumid);
		lStat.setObject(2, iTitle);
		lStat.setObject(3, iPrecursor_mz);
		lStat.setObject(4, iPrecursor_int);
		lStat.setLong(5, iPrecursor_charge);
		lStat.setObject(6, iMzarray);
		lStat.setObject(7, iIntarray);
		lStat.setObject(8, iChargearray);
		lStat.setObject(9, iTotal_int);
		lStat.setObject(10, iMaximum_int);
		lStat.setObject(11, iCreationdate);
		lStat.setLong(12, iSpectrumid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO spectrum (spectrumid, title, precursor_mz, precursor_int, precursor_charge, mzarray, intarray, chargearray, total_int, maximum_int, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iSpectrumid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpectrumid);
		}
		if(iTitle == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iTitle);
		}
		if(iPrecursor_mz == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iPrecursor_mz);
		}
		if(iPrecursor_int == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iPrecursor_int);
		}
		if(iPrecursor_charge == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, iPrecursor_charge);
		}
		if(iMzarray == null) {
			lStat.setNull(6, -1);
		} else {
			lStat.setObject(6, iMzarray);
		}
		if(iIntarray == null) {
			lStat.setNull(7, -1);
		} else {
			lStat.setObject(7, iIntarray);
		}
		if(iChargearray == null) {
			lStat.setNull(8, -1);
		} else {
			lStat.setObject(8, iChargearray);
		}
		if(iTotal_int == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iTotal_int);
		}
		if(iMaximum_int == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, iMaximum_int);
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