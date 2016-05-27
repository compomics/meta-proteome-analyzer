/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:54:16
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
 * This class is a generated accessor for the Dbcfg table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class DbcfgTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'dbcfgid' column.
	 */
	protected long iDbcfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fastadb' column.
	 */
	protected String iFastadb = null;


	/**
	 * This variable represents the contents for the 'precursor_tol' column.
	 */
	protected Number iPrecursor_tol = null;


	/**
	 * This variable represents the contents for the 'fragmention_tol' column.
	 */
	protected Number iFragmention_tol = null;


	/**
	 * This variable represents the contents for the 'missed_cleavages' column.
	 */
	protected int iMissed_cleavages = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'enzyme' column.
	 */
	protected int iEnzyme = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'isdecoy' column.
	 */
	protected boolean iIsdecoy = false;


	/**
	 * This variable represents the contents for the 'xtandem_cmd' column.
	 */
	protected String iXtandem_cmd = null;


	/**
	 * This variable represents the contents for the 'omssa_cmd' column.
	 */
	protected String iOmssa_cmd = null;


	/**
	 * This variable represents the contents for the 'inspect_cmd' column.
	 */
	protected String iInspect_cmd = null;


	/**
	 * This variable represents the contents for the 'crux_cmd' column.
	 */
	protected String iCrux_cmd = null;


	/**
	 * This variable represents the key for the 'dbcfgid' column.
	 */
	public static final String DBCFGID = "DBCFGID";

	/**
	 * This variable represents the key for the 'fastadb' column.
	 */
	public static final String FASTADB = "FASTADB";

	/**
	 * This variable represents the key for the 'precursor_tol' column.
	 */
	public static final String PRECURSOR_TOL = "PRECURSOR_TOL";

	/**
	 * This variable represents the key for the 'fragmention_tol' column.
	 */
	public static final String FRAGMENTION_TOL = "FRAGMENTION_TOL";

	/**
	 * This variable represents the key for the 'missed_cleavages' column.
	 */
	public static final String MISSED_CLEAVAGES = "MISSED_CLEAVAGES";

	/**
	 * This variable represents the key for the 'enzyme' column.
	 */
	public static final String ENZYME = "ENZYME";

	/**
	 * This variable represents the key for the 'isdecoy' column.
	 */
	public static final String ISDECOY = "ISDECOY";

	/**
	 * This variable represents the key for the 'xtandem_cmd' column.
	 */
	public static final String XTANDEM_CMD = "XTANDEM_CMD";

	/**
	 * This variable represents the key for the 'omssa_cmd' column.
	 */
	public static final String OMSSA_CMD = "OMSSA_CMD";

	/**
	 * This variable represents the key for the 'inspect_cmd' column.
	 */
	public static final String INSPECT_CMD = "INSPECT_CMD";

	/**
	 * This variable represents the key for the 'crux_cmd' column.
	 */
	public static final String CRUX_CMD = "CRUX_CMD";




	/**
	 * Default constructor.
	 */
	public DbcfgTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'DbcfgTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public DbcfgTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(DBCFGID)) {
			this.iDbcfgid = ((Long)aParams.get(DBCFGID)).longValue();
		}
		if(aParams.containsKey(FASTADB)) {
			this.iFastadb = (String)aParams.get(FASTADB);
		}
		if(aParams.containsKey(PRECURSOR_TOL)) {
			this.iPrecursor_tol = (Number)aParams.get(PRECURSOR_TOL);
		}
		if(aParams.containsKey(FRAGMENTION_TOL)) {
			this.iFragmention_tol = (Number)aParams.get(FRAGMENTION_TOL);
		}
		if(aParams.containsKey(MISSED_CLEAVAGES)) {
			this.iMissed_cleavages = ((Integer)aParams.get(MISSED_CLEAVAGES)).intValue();
		}
		if(aParams.containsKey(ENZYME)) {
			this.iEnzyme = ((Integer)aParams.get(ENZYME)).intValue();
		}
		if(aParams.containsKey(ISDECOY)) {
			this.iIsdecoy = ((Boolean)aParams.get(ISDECOY)).booleanValue();
		}
		if(aParams.containsKey(XTANDEM_CMD)) {
			this.iXtandem_cmd = (String)aParams.get(XTANDEM_CMD);
		}
		if(aParams.containsKey(OMSSA_CMD)) {
			this.iOmssa_cmd = (String)aParams.get(OMSSA_CMD);
		}
		if(aParams.containsKey(INSPECT_CMD)) {
			this.iInspect_cmd = (String)aParams.get(INSPECT_CMD);
		}
		if(aParams.containsKey(CRUX_CMD)) {
			this.iCrux_cmd = (String)aParams.get(CRUX_CMD);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'DbcfgTableAccessor' object based on a resultset
	 * obtained by a 'select * from Dbcfg' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public DbcfgTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iDbcfgid = aResultSet.getLong("dbcfgid");
		this.iFastadb = (String)aResultSet.getObject("fastadb");
		this.iPrecursor_tol = (Number)aResultSet.getObject("precursor_tol");
		this.iFragmention_tol = (Number)aResultSet.getObject("fragmention_tol");
		this.iMissed_cleavages = aResultSet.getInt("missed_cleavages");
		this.iEnzyme = aResultSet.getInt("enzyme");
		this.iIsdecoy = aResultSet.getBoolean("isdecoy");
		this.iXtandem_cmd = (String)aResultSet.getObject("xtandem_cmd");
		this.iOmssa_cmd = (String)aResultSet.getObject("omssa_cmd");
		this.iInspect_cmd = (String)aResultSet.getObject("inspect_cmd");
		this.iCrux_cmd = (String)aResultSet.getObject("crux_cmd");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Dbcfgid' column
	 * 
	 * @return	long	with the value for the Dbcfgid column.
	 */
	public long getDbcfgid() {
		return this.iDbcfgid;
	}

	/**
	 * This method returns the value for the 'Fastadb' column
	 * 
	 * @return	String	with the value for the Fastadb column.
	 */
	public String getFastadb() {
		return this.iFastadb;
	}

	/**
	 * This method returns the value for the 'Precursor_tol' column
	 * 
	 * @return	Number	with the value for the Precursor_tol column.
	 */
	public Number getPrecursor_tol() {
		return this.iPrecursor_tol;
	}

	/**
	 * This method returns the value for the 'Fragmention_tol' column
	 * 
	 * @return	Number	with the value for the Fragmention_tol column.
	 */
	public Number getFragmention_tol() {
		return this.iFragmention_tol;
	}

	/**
	 * This method returns the value for the 'Missed_cleavages' column
	 * 
	 * @return	int	with the value for the Missed_cleavages column.
	 */
	public int getMissed_cleavages() {
		return this.iMissed_cleavages;
	}

	/**
	 * This method returns the value for the 'Enzyme' column
	 * 
	 * @return	int	with the value for the Enzyme column.
	 */
	public int getEnzyme() {
		return this.iEnzyme;
	}

	/**
	 * This method returns the value for the 'Isdecoy' column
	 * 
	 * @return	boolean	with the value for the Isdecoy column.
	 */
	public boolean getIsdecoy() {
		return this.iIsdecoy;
	}

	/**
	 * This method returns the value for the 'Xtandem_cmd' column
	 * 
	 * @return	String	with the value for the Xtandem_cmd column.
	 */
	public String getXtandem_cmd() {
		return this.iXtandem_cmd;
	}

	/**
	 * This method returns the value for the 'Omssa_cmd' column
	 * 
	 * @return	String	with the value for the Omssa_cmd column.
	 */
	public String getOmssa_cmd() {
		return this.iOmssa_cmd;
	}

	/**
	 * This method returns the value for the 'Inspect_cmd' column
	 * 
	 * @return	String	with the value for the Inspect_cmd column.
	 */
	public String getInspect_cmd() {
		return this.iInspect_cmd;
	}

	/**
	 * This method returns the value for the 'Crux_cmd' column
	 * 
	 * @return	String	with the value for the Crux_cmd column.
	 */
	public String getCrux_cmd() {
		return this.iCrux_cmd;
	}

	/**
	 * This method sets the value for the 'Dbcfgid' column
	 * 
	 * @param	aDbcfgid	long with the value for the Dbcfgid column.
	 */
	public void setDbcfgid(long aDbcfgid) {
		this.iDbcfgid = aDbcfgid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fastadb' column
	 * 
	 * @param	aFastadb	String with the value for the Fastadb column.
	 */
	public void setFastadb(String aFastadb) {
		this.iFastadb = aFastadb;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_tol' column
	 * 
	 * @param	aPrecursor_tol	Number with the value for the Precursor_tol column.
	 */
	public void setPrecursor_tol(Number aPrecursor_tol) {
		this.iPrecursor_tol = aPrecursor_tol;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fragmention_tol' column
	 * 
	 * @param	aFragmention_tol	Number with the value for the Fragmention_tol column.
	 */
	public void setFragmention_tol(Number aFragmention_tol) {
		this.iFragmention_tol = aFragmention_tol;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Missed_cleavages' column
	 * 
	 * @param	aMissed_cleavages	int with the value for the Missed_cleavages column.
	 */
	public void setMissed_cleavages(int aMissed_cleavages) {
		this.iMissed_cleavages = aMissed_cleavages;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Enzyme' column
	 * 
	 * @param	aEnzyme	int with the value for the Enzyme column.
	 */
	public void setEnzyme(int aEnzyme) {
		this.iEnzyme = aEnzyme;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Isdecoy' column
	 * 
	 * @param	aIsdecoy	boolean with the value for the Isdecoy column.
	 */
	public void setIsdecoy(boolean aIsdecoy) {
		this.iIsdecoy = aIsdecoy;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Xtandem_cmd' column
	 * 
	 * @param	aXtandem_cmd	String with the value for the Xtandem_cmd column.
	 */
	public void setXtandem_cmd(String aXtandem_cmd) {
		this.iXtandem_cmd = aXtandem_cmd;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Omssa_cmd' column
	 * 
	 * @param	aOmssa_cmd	String with the value for the Omssa_cmd column.
	 */
	public void setOmssa_cmd(String aOmssa_cmd) {
		this.iOmssa_cmd = aOmssa_cmd;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Inspect_cmd' column
	 * 
	 * @param	aInspect_cmd	String with the value for the Inspect_cmd column.
	 */
	public void setInspect_cmd(String aInspect_cmd) {
		this.iInspect_cmd = aInspect_cmd;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Crux_cmd' column
	 * 
	 * @param	aCrux_cmd	String with the value for the Crux_cmd column.
	 */
	public void setCrux_cmd(String aCrux_cmd) {
		this.iCrux_cmd = aCrux_cmd;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM dbcfg WHERE dbcfgid = ?");
		lStat.setLong(1, iDbcfgid);
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
	public void retrieve(Connection aConn, @SuppressWarnings("rawtypes") HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(DBCFGID)) {
			throw new IllegalArgumentException("Primary key field 'DBCFGID' is missing in HashMap!");
		} else {
			iDbcfgid = ((Long)aKeys.get(DBCFGID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM dbcfg WHERE dbcfgid = ?");
		lStat.setLong(1, iDbcfgid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iDbcfgid = lRS.getLong("dbcfgid");
			iFastadb = (String)lRS.getObject("fastadb");
			iPrecursor_tol = (Number)lRS.getObject("precursor_tol");
			iFragmention_tol = (Number)lRS.getObject("fragmention_tol");
			iMissed_cleavages = lRS.getInt("missed_cleavages");
			iEnzyme = lRS.getInt("enzyme");
			iIsdecoy = lRS.getBoolean("isdecoy");
			iXtandem_cmd = (String)lRS.getObject("xtandem_cmd");
			iOmssa_cmd = (String)lRS.getObject("omssa_cmd");
			iInspect_cmd = (String)lRS.getObject("inspect_cmd");
			iCrux_cmd = (String)lRS.getObject("crux_cmd");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'dbcfg' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'dbcfg' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from dbcfg";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<DbcfgTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<DbcfgTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<DbcfgTableAccessor>  entities = new ArrayList<DbcfgTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new DbcfgTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE dbcfg SET dbcfgid = ?, fastadb = ?, precursor_tol = ?, fragmention_tol = ?, missed_cleavages = ?, enzyme = ?, isdecoy = ?, xtandem_cmd = ?, omssa_cmd = ?, inspect_cmd = ?, crux_cmd = ? WHERE dbcfgid = ?");
		lStat.setLong(1, iDbcfgid);
		lStat.setObject(2, iFastadb);
		lStat.setObject(3, iPrecursor_tol);
		lStat.setObject(4, iFragmention_tol);
		lStat.setInt(5, iMissed_cleavages);
		lStat.setInt(6, iEnzyme);
		lStat.setBoolean(7, iIsdecoy);
		lStat.setObject(8, iXtandem_cmd);
		lStat.setObject(9, iOmssa_cmd);
		lStat.setObject(10, iInspect_cmd);
		lStat.setObject(11, iCrux_cmd);
		lStat.setLong(12, iDbcfgid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO dbcfg (dbcfgid, fastadb, precursor_tol, fragmention_tol, missed_cleavages, enzyme, isdecoy, xtandem_cmd, omssa_cmd, inspect_cmd, crux_cmd) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		if(iDbcfgid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iDbcfgid);
		}
		if(iFastadb == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iFastadb);
		}
		if(iPrecursor_tol == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iPrecursor_tol);
		}
		if(iFragmention_tol == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iFragmention_tol);
		}
		if(iMissed_cleavages == Integer.MIN_VALUE) {
			lStat.setNull(5, -6);
		} else {
			lStat.setInt(5, iMissed_cleavages);
		}
		if(iEnzyme == Integer.MIN_VALUE) {
			lStat.setNull(6, -6);
		} else {
			lStat.setInt(6, iEnzyme);
		}
		lStat.setBoolean(7, iIsdecoy);
		if(iXtandem_cmd == null) {
			lStat.setNull(8, 12);
		} else {
			lStat.setObject(8, iXtandem_cmd);
		}
		if(iOmssa_cmd == null) {
			lStat.setNull(9, 12);
		} else {
			lStat.setObject(9, iOmssa_cmd);
		}
		if(iInspect_cmd == null) {
			lStat.setNull(10, 12);
		} else {
			lStat.setObject(10, iInspect_cmd);
		}
		if(iCrux_cmd == null) {
			lStat.setNull(11, 12);
		} else {
			lStat.setObject(11, iCrux_cmd);
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
			iDbcfgid = ((Number) iKeys[0]).longValue();
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