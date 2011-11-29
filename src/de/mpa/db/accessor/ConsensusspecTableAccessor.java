/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 29/11/2011
 * Time: 14:37:25
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
	 * This variable represents the contents for the 'file' column.
	 */
	protected byte[] iFile = null;


	/**
	 * This variable represents the contents for the 'precursor' column.
	 */
	protected Number iPrecursor = null;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


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
	 * This variable represents the key for the 'file' column.
	 */
	public static final String FILE = "FILE";

	/**
	 * This variable represents the key for the 'precursor' column.
	 */
	public static final String PRECURSOR = "PRECURSOR";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

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
		if(aParams.containsKey(FILE)) {
			this.iFile = (byte[])aParams.get(FILE);
		}
		if(aParams.containsKey(PRECURSOR)) {
			this.iPrecursor = (Number)aParams.get(PRECURSOR);
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
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
		InputStream is1 = aResultSet.getBinaryStream("file");
		Vector bytes1 = new Vector();
		int reading = -1;
		try {
			while((reading = is1.read()) != -1) {
				bytes1.add(new Byte((byte)reading));
			}
			is1.close();
		} catch(IOException ioe) {
			bytes1 = new Vector();
		}
		reading = bytes1.size();
		this.iFile = new byte[reading];
		for(int i=0;i<reading;i++) {
			this.iFile[i] = ((Byte)bytes1.get(i)).byteValue();
		}
		this.iPrecursor = (Number)aResultSet.getObject("precursor");
		this.iCharge = aResultSet.getLong("charge");
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
	 * This method returns the value for the 'File' column
	 * 
	 * @return	byte[]	with the value for the File column.
	 */
	public byte[] getFile() {
		return this.iFile;
	}

	/**
	 * This method returns the value for the 'Precursor' column
	 * 
	 * @return	Number	with the value for the Precursor column.
	 */
	public Number getPrecursor() {
		return this.iPrecursor;
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
	 * This method sets the value for the 'File' column
	 * 
	 * @param	aFile	byte[] with the value for the File column.
	 */
	public void setFile(byte[] aFile) {
		this.iFile = aFile;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor' column
	 * 
	 * @param	aPrecursor	Number with the value for the Precursor column.
	 */
	public void setPrecursor(Number aPrecursor) {
		this.iPrecursor = aPrecursor;
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
			InputStream is1 = lRS.getBinaryStream("file");
			Vector bytes1 = new Vector();
			int reading = -1;
			try {
				while((reading = is1.read()) != -1) {
					bytes1.add(new Byte((byte)reading));
				}
				is1.close();
			} catch(IOException ioe) {
				bytes1 = new Vector();
			}
			reading = bytes1.size();
			iFile = new byte[reading];
			for(int i=0;i<reading;i++) {
				iFile[i] = ((Byte)bytes1.get(i)).byteValue();
			}
			iPrecursor = (Number)lRS.getObject("precursor");
			iCharge = lRS.getLong("charge");
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE consensusspec SET consensusspecid = ?, file = ?, precursor = ?, charge = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE consensusspecid = ?");
		lStat.setLong(1, iConsensusspecid);
		ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
		lStat.setBinaryStream(2, bais1, iFile.length);
		lStat.setObject(3, iPrecursor);
		lStat.setLong(4, iCharge);
		lStat.setObject(5, iCreationdate);
		lStat.setLong(6, iConsensusspecid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO consensusspec (consensusspecid, file, precursor, charge, creationdate, modificationdate) values(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iConsensusspecid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iConsensusspecid);
		}
		if(iFile == null) {
			lStat.setNull(2, -4);
		} else {
			ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
			lStat.setBinaryStream(2, bais1, iFile.length);
		}
		if(iPrecursor == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iPrecursor);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iCharge);
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