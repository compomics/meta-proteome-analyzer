/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:54:18
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
 * This class is a generated accessor for the Dncfg table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class DncfgTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'dncfgid' column.
	 */
	protected long iDncfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'model' column.
	 */
	protected String iModel = null;


	/**
	 * This variable represents the contents for the 'fragment_tol' column.
	 */
	protected Number iFragment_tol = null;


	/**
	 * This variable represents the contents for the 'precursor_tol' column.
	 */
	protected Number iPrecursor_tol = null;


	/**
	 * This variable represents the contents for the 'enzyme' column.
	 */
	protected int iEnzyme = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'num_solutions' column.
	 */
	protected long iNum_solutions = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'tag_length' column.
	 */
	protected int iTag_length = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'ptms' column.
	 */
	protected String iPtms = null;


	/**
	 * This variable represents the key for the 'dncfgid' column.
	 */
	public static final String DNCFGID = "DNCFGID";

	/**
	 * This variable represents the key for the 'model' column.
	 */
	public static final String MODEL = "MODEL";

	/**
	 * This variable represents the key for the 'fragment_tol' column.
	 */
	public static final String FRAGMENT_TOL = "FRAGMENT_TOL";

	/**
	 * This variable represents the key for the 'precursor_tol' column.
	 */
	public static final String PRECURSOR_TOL = "PRECURSOR_TOL";

	/**
	 * This variable represents the key for the 'enzyme' column.
	 */
	public static final String ENZYME = "ENZYME";

	/**
	 * This variable represents the key for the 'num_solutions' column.
	 */
	public static final String NUM_SOLUTIONS = "NUM_SOLUTIONS";

	/**
	 * This variable represents the key for the 'tag_length' column.
	 */
	public static final String TAG_LENGTH = "TAG_LENGTH";

	/**
	 * This variable represents the key for the 'ptms' column.
	 */
	public static final String PTMS = "PTMS";




	/**
	 * Default constructor.
	 */
	public DncfgTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'DncfgTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public DncfgTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(DNCFGID)) {
			this.iDncfgid = ((Long)aParams.get(DNCFGID)).longValue();
		}
		if(aParams.containsKey(MODEL)) {
			this.iModel = (String)aParams.get(MODEL);
		}
		if(aParams.containsKey(FRAGMENT_TOL)) {
			this.iFragment_tol = (Number)aParams.get(FRAGMENT_TOL);
		}
		if(aParams.containsKey(PRECURSOR_TOL)) {
			this.iPrecursor_tol = (Number)aParams.get(PRECURSOR_TOL);
		}
		if(aParams.containsKey(ENZYME)) {
			this.iEnzyme = ((Integer)aParams.get(ENZYME)).intValue();
		}
		if(aParams.containsKey(NUM_SOLUTIONS)) {
			this.iNum_solutions = ((Long)aParams.get(NUM_SOLUTIONS)).longValue();
		}
		if(aParams.containsKey(TAG_LENGTH)) {
			this.iTag_length = ((Integer)aParams.get(TAG_LENGTH)).intValue();
		}
		if(aParams.containsKey(PTMS)) {
			this.iPtms = (String)aParams.get(PTMS);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'DncfgTableAccessor' object based on a resultset
	 * obtained by a 'select * from Dncfg' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public DncfgTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iDncfgid = aResultSet.getLong("dncfgid");
		this.iModel = (String)aResultSet.getObject("model");
		this.iFragment_tol = (Number)aResultSet.getObject("fragment_tol");
		this.iPrecursor_tol = (Number)aResultSet.getObject("precursor_tol");
		this.iEnzyme = aResultSet.getInt("enzyme");
		this.iNum_solutions = aResultSet.getLong("num_solutions");
		this.iTag_length = aResultSet.getInt("tag_length");
		this.iPtms = (String)aResultSet.getObject("ptms");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Dncfgid' column
	 * 
	 * @return	long	with the value for the Dncfgid column.
	 */
	public long getDncfgid() {
		return this.iDncfgid;
	}

	/**
	 * This method returns the value for the 'Model' column
	 * 
	 * @return	String	with the value for the Model column.
	 */
	public String getModel() {
		return this.iModel;
	}

	/**
	 * This method returns the value for the 'Fragment_tol' column
	 * 
	 * @return	Number	with the value for the Fragment_tol column.
	 */
	public Number getFragment_tol() {
		return this.iFragment_tol;
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
	 * This method returns the value for the 'Enzyme' column
	 * 
	 * @return	int	with the value for the Enzyme column.
	 */
	public int getEnzyme() {
		return this.iEnzyme;
	}

	/**
	 * This method returns the value for the 'Num_solutions' column
	 * 
	 * @return	long	with the value for the Num_solutions column.
	 */
	public long getNum_solutions() {
		return this.iNum_solutions;
	}

	/**
	 * This method returns the value for the 'Tag_length' column
	 * 
	 * @return	int	with the value for the Tag_length column.
	 */
	public int getTag_length() {
		return this.iTag_length;
	}

	/**
	 * This method returns the value for the 'Ptms' column
	 * 
	 * @return	String	with the value for the Ptms column.
	 */
	public String getPtms() {
		return this.iPtms;
	}

	/**
	 * This method sets the value for the 'Dncfgid' column
	 * 
	 * @param	aDncfgid	long with the value for the Dncfgid column.
	 */
	public void setDncfgid(long aDncfgid) {
		this.iDncfgid = aDncfgid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Model' column
	 * 
	 * @param	aModel	String with the value for the Model column.
	 */
	public void setModel(String aModel) {
		this.iModel = aModel;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fragment_tol' column
	 * 
	 * @param	aFragment_tol	Number with the value for the Fragment_tol column.
	 */
	public void setFragment_tol(Number aFragment_tol) {
		this.iFragment_tol = aFragment_tol;
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
	 * This method sets the value for the 'Enzyme' column
	 * 
	 * @param	aEnzyme	int with the value for the Enzyme column.
	 */
	public void setEnzyme(int aEnzyme) {
		this.iEnzyme = aEnzyme;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Num_solutions' column
	 * 
	 * @param	aNum_solutions	long with the value for the Num_solutions column.
	 */
	public void setNum_solutions(long aNum_solutions) {
		this.iNum_solutions = aNum_solutions;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Tag_length' column
	 * 
	 * @param	aTag_length	int with the value for the Tag_length column.
	 */
	public void setTag_length(int aTag_length) {
		this.iTag_length = aTag_length;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Ptms' column
	 * 
	 * @param	aPtms	String with the value for the Ptms column.
	 */
	public void setPtms(String aPtms) {
		this.iPtms = aPtms;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM dncfg WHERE dncfgid = ?");
		lStat.setLong(1, iDncfgid);
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
		if(!aKeys.containsKey(DNCFGID)) {
			throw new IllegalArgumentException("Primary key field 'DNCFGID' is missing in HashMap!");
		} else {
			iDncfgid = ((Long)aKeys.get(DNCFGID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM dncfg WHERE dncfgid = ?");
		lStat.setLong(1, iDncfgid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iDncfgid = lRS.getLong("dncfgid");
			iModel = (String)lRS.getObject("model");
			iFragment_tol = (Number)lRS.getObject("fragment_tol");
			iPrecursor_tol = (Number)lRS.getObject("precursor_tol");
			iEnzyme = lRS.getInt("enzyme");
			iNum_solutions = lRS.getLong("num_solutions");
			iTag_length = lRS.getInt("tag_length");
			iPtms = (String)lRS.getObject("ptms");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'dncfg' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'dncfg' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from dncfg";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<DncfgTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<DncfgTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<DncfgTableAccessor>  entities = new ArrayList<DncfgTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new DncfgTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE dncfg SET dncfgid = ?, model = ?, fragment_tol = ?, precursor_tol = ?, enzyme = ?, num_solutions = ?, tag_length = ?, ptms = ? WHERE dncfgid = ?");
		lStat.setLong(1, iDncfgid);
		lStat.setObject(2, iModel);
		lStat.setObject(3, iFragment_tol);
		lStat.setObject(4, iPrecursor_tol);
		lStat.setInt(5, iEnzyme);
		lStat.setLong(6, iNum_solutions);
		lStat.setInt(7, iTag_length);
		lStat.setObject(8, iPtms);
		lStat.setLong(9, iDncfgid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO dncfg (dncfgid, model, fragment_tol, precursor_tol, enzyme, num_solutions, tag_length, ptms) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		if(iDncfgid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iDncfgid);
		}
		if(iModel == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iModel);
		}
		if(iFragment_tol == null) {
			lStat.setNull(3, 3);
		} else {
			lStat.setObject(3, iFragment_tol);
		}
		if(iPrecursor_tol == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iPrecursor_tol);
		}
		if(iEnzyme == Integer.MIN_VALUE) {
			lStat.setNull(5, -6);
		} else {
			lStat.setInt(5, iEnzyme);
		}
		if(iNum_solutions == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, iNum_solutions);
		}
		if(iTag_length == Integer.MIN_VALUE) {
			lStat.setNull(7, -6);
		} else {
			lStat.setInt(7, iTag_length);
		}
		if(iPtms == null) {
			lStat.setNull(8, 12);
		} else {
			lStat.setObject(8, iPtms);
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
			iDncfgid = ((Number) iKeys[0]).longValue();
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