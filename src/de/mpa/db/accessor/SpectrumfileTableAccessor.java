/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/01/2012
 * Time: 13:27:01
 */
package de.mpa.db.accessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
 * This class is a generated accessor for the Spectrumfile table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SpectrumfileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'fk_libspectrumid' column.
	 */
	protected long iFk_libspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'file' column.
	 */
	protected byte[] iFile = null;


	/**
	 * This variable represents the key for the 'fk_libspectrumid' column.
	 */
	public static final String FK_LIBSPECTRUMID = "FK_LIBSPECTRUMID";

	/**
	 * This variable represents the key for the 'file' column.
	 */
	public static final String FILE = "FILE";




	/**
	 * Default constructor.
	 */
	public SpectrumfileTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SpectrumfileTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SpectrumfileTableAccessor(HashMap aParams) {
		if(aParams.containsKey(FK_LIBSPECTRUMID)) {
			this.iFk_libspectrumid = ((Long)aParams.get(FK_LIBSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FILE)) {
			this.iFile = (byte[])aParams.get(FILE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'SpectrumfileTableAccessor' object based on a resultset
	 * obtained by a 'select * from Spectrumfile' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpectrumfileTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iFk_libspectrumid = aResultSet.getLong("fk_libspectrumid");
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

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Fk_libspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_libspectrumid column.
	 */
	public long getFk_libspectrumid() {
		return this.iFk_libspectrumid;
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
	 * This method sets the value for the 'Fk_libspectrumid' column
	 * 
	 * @param	aFk_libspectrumid	long with the value for the Fk_libspectrumid column.
	 */
	public void setFk_libspectrumid(long aFk_libspectrumid) {
		this.iFk_libspectrumid = aFk_libspectrumid;
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
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM spectrumfile WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
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
		if(!aKeys.containsKey(FK_LIBSPECTRUMID)) {
			throw new IllegalArgumentException("Primary key field 'FK_LIBSPECTRUMID' is missing in HashMap!");
		} else {
			iFk_libspectrumid = ((Long)aKeys.get(FK_LIBSPECTRUMID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM spectrumfile WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iFk_libspectrumid = lRS.getLong("fk_libspectrumid");
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
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'spectrumfile' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'spectrumfile' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from spectrumfile";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SpectrumfileTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SpectrumfileTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SpectrumfileTableAccessor>  entities = new ArrayList<SpectrumfileTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SpectrumfileTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE spectrumfile SET fk_libspectrumid = ?, file = ? WHERE fk_libspectrumid = ?");
		lStat.setLong(1, iFk_libspectrumid);
		ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
		lStat.setBinaryStream(2, bais1, iFile.length);
		lStat.setLong(3, iFk_libspectrumid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO spectrumfile (fk_libspectrumid, file) values(?, ?)");
		if(iFk_libspectrumid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iFk_libspectrumid);
		}
		if(iFile == null) {
			lStat.setNull(2, -4);
		} else {
			ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
			lStat.setBinaryStream(2, bais1, iFile.length);
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
			iFk_libspectrumid = ((Number) iKeys[0]).longValue();
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