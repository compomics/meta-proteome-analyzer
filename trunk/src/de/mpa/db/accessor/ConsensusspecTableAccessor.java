/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 15/03/2012
 * Time: 14:59:07
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
 * This class is a generated accessor for the Consensusspec table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ConsensusspecTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'consensusspecid' column.
	 */
	protected long iConsensusspecid = Long.MIN_VALUE;


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
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'consensusspecid' column.
	 */
	public static final String CONSENSUSSPECID = "CONSENSUSSPECID";

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
	public ConsensusspecTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ConsensusspecTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ConsensusspecTableAccessor(HashMap aParams) {
		if(aParams.containsKey(CONSENSUSSPECID)) {
			this.iConsensusspecid = ((Long)aParams.get(CONSENSUSSPECID)).longValue();
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
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (java.sql.Timestamp)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (java.sql.Timestamp)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ConsensusspecTableAccessor' object based on a resultset
	 * obtained by a 'select * from Consensusspec' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ConsensusspecTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iConsensusspecid = aResultSet.getLong("consensusspecid");
		this.iPrecursor_mz = (Number)aResultSet.getObject("precursor_mz");
		this.iPrecursor_int = (Number)aResultSet.getObject("precursor_int");
		this.iPrecursor_charge = aResultSet.getLong("precursor_charge");
		this.iMzarray = (String)aResultSet.getObject("mzarray");
		this.iIntarray = (String)aResultSet.getObject("intarray");
		this.iChargearray = (String)aResultSet.getObject("chargearray");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Consensusspecid' column
	 * 
	 * @return	long	with the value for the Consensusspecid column.
	 */
	public long getConsensusspecid() {
		return this.iConsensusspecid;
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
	 * This method sets the value for the 'Consensusspecid' column
	 * 
	 * @param	aConsensusspecid	long with the value for the Consensusspecid column.
	 */
	public void setConsensusspecid(long aConsensusspecid) {
		this.iConsensusspecid = aConsensusspecid;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM consensusspec WHERE consensusspecid = ?");
		lStat.setLong(1, iConsensusspecid);
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
		if(!aKeys.containsKey(CONSENSUSSPECID)) {
			throw new IllegalArgumentException("Primary key field 'CONSENSUSSPECID' is missing in HashMap!");
		} else {
			iConsensusspecid = ((Long)aKeys.get(CONSENSUSSPECID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM consensusspec WHERE consensusspecid = ?");
		lStat.setLong(1, iConsensusspecid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iConsensusspecid = lRS.getLong("consensusspecid");
			iPrecursor_mz = (Number)lRS.getObject("precursor_mz");
			iPrecursor_int = (Number)lRS.getObject("precursor_int");
			iPrecursor_charge = lRS.getLong("precursor_charge");
			iMzarray = (String)lRS.getObject("mzarray");
			iIntarray = (String)lRS.getObject("intarray");
			iChargearray = (String)lRS.getObject("chargearray");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'consensusspec' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'consensusspec' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from consensusspec";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ConsensusspecTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ConsensusspecTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ConsensusspecTableAccessor>  entities = new ArrayList<ConsensusspecTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ConsensusspecTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE consensusspec SET consensusspecid = ?, precursor_mz = ?, precursor_int = ?, precursor_charge = ?, mzarray = ?, intarray = ?, chargearray = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE consensusspecid = ?");
		lStat.setLong(1, iConsensusspecid);
		lStat.setObject(2, iPrecursor_mz);
		lStat.setObject(3, iPrecursor_int);
		lStat.setLong(4, iPrecursor_charge);
		lStat.setObject(5, iMzarray);
		lStat.setObject(6, iIntarray);
		lStat.setObject(7, iChargearray);
		lStat.setObject(8, iCreationdate);
		lStat.setLong(9, iConsensusspecid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO consensusspec (consensusspecid, precursor_mz, precursor_int, precursor_charge, mzarray, intarray, chargearray, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iConsensusspecid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iConsensusspecid);
		}
		if(iPrecursor_mz == null) {
			lStat.setNull(2, 3);
		} else {
			lStat.setObject(2, iPrecursor_mz);
		}
		if(iPrecursor_int == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iPrecursor_int);
		}
		if(iPrecursor_charge == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iPrecursor_charge);
		}
		if(iMzarray == null) {
			lStat.setNull(5, -1);
		} else {
			lStat.setObject(5, iMzarray);
		}
		if(iIntarray == null) {
			lStat.setNull(6, -1);
		} else {
			lStat.setObject(6, iIntarray);
		}
		if(iChargearray == null) {
			lStat.setNull(7, -1);
		} else {
			lStat.setObject(7, iChargearray);
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
			iConsensusspecid = ((Number) iKeys[0]).longValue();
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