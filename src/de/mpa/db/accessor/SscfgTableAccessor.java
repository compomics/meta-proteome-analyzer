/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:54:14
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
 * This class is a generated accessor for the Sscfg table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SscfgTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'sscfgid' column.
	 */
	protected long iSscfgid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'precursor_tol' column.
	 */
	protected Number iPrecursor_tol = null;


	/**
	 * This variable represents the contents for the 'annotated_only' column.
	 */
	protected boolean iAnnotated_only = false;


	/**
	 * This variable represents the contents for the 'vectorization_type' column.
	 */
	protected int iVectorization_type = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'bin_width' column.
	 */
	protected Number iBin_width = null;


	/**
	 * This variable represents the contents for the 'bin_shift' column.
	 */
	protected Number iBin_shift = null;


	/**
	 * This variable represents the contents for the 'profile_shape' column.
	 */
	protected String iProfile_shape = null;


	/**
	 * This variable represents the contents for the 'peakbase_width' column.
	 */
	protected Number iPeakbase_width = null;


	/**
	 * This variable represents the contents for the 'pick_count' column.
	 */
	protected long iPick_count = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'transformation_type' column.
	 */
	protected int iTransformation_type = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'measure_type' column.
	 */
	protected int iMeasure_type = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'corr_offsets' column.
	 */
	protected long iCorr_offsets = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'score_threshold' column.
	 */
	protected Number iScore_threshold = null;


	/**
	 * This variable represents the key for the 'sscfgid' column.
	 */
	public static final String SSCFGID = "SSCFGID";

	/**
	 * This variable represents the key for the 'precursor_tol' column.
	 */
	public static final String PRECURSOR_TOL = "PRECURSOR_TOL";

	/**
	 * This variable represents the key for the 'annotated_only' column.
	 */
	public static final String ANNOTATED_ONLY = "ANNOTATED_ONLY";

	/**
	 * This variable represents the key for the 'vectorization_type' column.
	 */
	public static final String VECTORIZATION_TYPE = "VECTORIZATION_TYPE";

	/**
	 * This variable represents the key for the 'bin_width' column.
	 */
	public static final String BIN_WIDTH = "BIN_WIDTH";

	/**
	 * This variable represents the key for the 'bin_shift' column.
	 */
	public static final String BIN_SHIFT = "BIN_SHIFT";

	/**
	 * This variable represents the key for the 'profile_shape' column.
	 */
	public static final String PROFILE_SHAPE = "PROFILE_SHAPE";

	/**
	 * This variable represents the key for the 'peakbase_width' column.
	 */
	public static final String PEAKBASE_WIDTH = "PEAKBASE_WIDTH";

	/**
	 * This variable represents the key for the 'pick_count' column.
	 */
	public static final String PICK_COUNT = "PICK_COUNT";

	/**
	 * This variable represents the key for the 'transformation_type' column.
	 */
	public static final String TRANSFORMATION_TYPE = "TRANSFORMATION_TYPE";

	/**
	 * This variable represents the key for the 'measure_type' column.
	 */
	public static final String MEASURE_TYPE = "MEASURE_TYPE";

	/**
	 * This variable represents the key for the 'corr_offsets' column.
	 */
	public static final String CORR_OFFSETS = "CORR_OFFSETS";

	/**
	 * This variable represents the key for the 'score_threshold' column.
	 */
	public static final String SCORE_THRESHOLD = "SCORE_THRESHOLD";




	/**
	 * Default constructor.
	 */
	public SscfgTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SscfgTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SscfgTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SSCFGID)) {
			this.iSscfgid = ((Long)aParams.get(SSCFGID)).longValue();
		}
		if(aParams.containsKey(PRECURSOR_TOL)) {
			this.iPrecursor_tol = (Number)aParams.get(PRECURSOR_TOL);
		}
		if(aParams.containsKey(ANNOTATED_ONLY)) {
			this.iAnnotated_only = ((Boolean)aParams.get(ANNOTATED_ONLY)).booleanValue();
		}
		if(aParams.containsKey(VECTORIZATION_TYPE)) {
			this.iVectorization_type = ((Integer)aParams.get(VECTORIZATION_TYPE)).intValue();
		}
		if(aParams.containsKey(BIN_WIDTH)) {
			this.iBin_width = (Number)aParams.get(BIN_WIDTH);
		}
		if(aParams.containsKey(BIN_SHIFT)) {
			this.iBin_shift = (Number)aParams.get(BIN_SHIFT);
		}
		if(aParams.containsKey(PROFILE_SHAPE)) {
			this.iProfile_shape = (String)aParams.get(PROFILE_SHAPE);
		}
		if(aParams.containsKey(PEAKBASE_WIDTH)) {
			this.iPeakbase_width = (Number)aParams.get(PEAKBASE_WIDTH);
		}
		if(aParams.containsKey(PICK_COUNT)) {
			this.iPick_count = ((Long)aParams.get(PICK_COUNT)).longValue();
		}
		if(aParams.containsKey(TRANSFORMATION_TYPE)) {
			this.iTransformation_type = ((Integer)aParams.get(TRANSFORMATION_TYPE)).intValue();
		}
		if(aParams.containsKey(MEASURE_TYPE)) {
			this.iMeasure_type = ((Integer)aParams.get(MEASURE_TYPE)).intValue();
		}
		if(aParams.containsKey(CORR_OFFSETS)) {
			this.iCorr_offsets = ((Long)aParams.get(CORR_OFFSETS)).longValue();
		}
		if(aParams.containsKey(SCORE_THRESHOLD)) {
			this.iScore_threshold = (Number)aParams.get(SCORE_THRESHOLD);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'SscfgTableAccessor' object based on a resultset
	 * obtained by a 'select * from Sscfg' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SscfgTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSscfgid = aResultSet.getLong("sscfgid");
		this.iPrecursor_tol = (Number)aResultSet.getObject("precursor_tol");
		this.iAnnotated_only = aResultSet.getBoolean("annotated_only");
		this.iVectorization_type = aResultSet.getInt("vectorization_type");
		this.iBin_width = (Number)aResultSet.getObject("bin_width");
		this.iBin_shift = (Number)aResultSet.getObject("bin_shift");
		this.iProfile_shape = (String)aResultSet.getObject("profile_shape");
		this.iPeakbase_width = (Number)aResultSet.getObject("peakbase_width");
		this.iPick_count = aResultSet.getLong("pick_count");
		this.iTransformation_type = aResultSet.getInt("transformation_type");
		this.iMeasure_type = aResultSet.getInt("measure_type");
		this.iCorr_offsets = aResultSet.getLong("corr_offsets");
		this.iScore_threshold = (Number)aResultSet.getObject("score_threshold");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Sscfgid' column
	 * 
	 * @return	long	with the value for the Sscfgid column.
	 */
	public long getSscfgid() {
		return this.iSscfgid;
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
	 * This method returns the value for the 'Annotated_only' column
	 * 
	 * @return	boolean	with the value for the Annotated_only column.
	 */
	public boolean getAnnotated_only() {
		return this.iAnnotated_only;
	}

	/**
	 * This method returns the value for the 'Vectorization_type' column
	 * 
	 * @return	int	with the value for the Vectorization_type column.
	 */
	public int getVectorization_type() {
		return this.iVectorization_type;
	}

	/**
	 * This method returns the value for the 'Bin_width' column
	 * 
	 * @return	Number	with the value for the Bin_width column.
	 */
	public Number getBin_width() {
		return this.iBin_width;
	}

	/**
	 * This method returns the value for the 'Bin_shift' column
	 * 
	 * @return	Number	with the value for the Bin_shift column.
	 */
	public Number getBin_shift() {
		return this.iBin_shift;
	}

	/**
	 * This method returns the value for the 'Profile_shape' column
	 * 
	 * @return	String	with the value for the Profile_shape column.
	 */
	public String getProfile_shape() {
		return this.iProfile_shape;
	}

	/**
	 * This method returns the value for the 'Peakbase_width' column
	 * 
	 * @return	Number	with the value for the Peakbase_width column.
	 */
	public Number getPeakbase_width() {
		return this.iPeakbase_width;
	}

	/**
	 * This method returns the value for the 'Pick_count' column
	 * 
	 * @return	long	with the value for the Pick_count column.
	 */
	public long getPick_count() {
		return this.iPick_count;
	}

	/**
	 * This method returns the value for the 'Transformation_type' column
	 * 
	 * @return	int	with the value for the Transformation_type column.
	 */
	public int getTransformation_type() {
		return this.iTransformation_type;
	}

	/**
	 * This method returns the value for the 'Measure_type' column
	 * 
	 * @return	int	with the value for the Measure_type column.
	 */
	public int getMeasure_type() {
		return this.iMeasure_type;
	}

	/**
	 * This method returns the value for the 'Corr_offsets' column
	 * 
	 * @return	long	with the value for the Corr_offsets column.
	 */
	public long getCorr_offsets() {
		return this.iCorr_offsets;
	}

	/**
	 * This method returns the value for the 'Score_threshold' column
	 * 
	 * @return	Number	with the value for the Score_threshold column.
	 */
	public Number getScore_threshold() {
		return this.iScore_threshold;
	}

	/**
	 * This method sets the value for the 'Sscfgid' column
	 * 
	 * @param	aSscfgid	long with the value for the Sscfgid column.
	 */
	public void setSscfgid(long aSscfgid) {
		this.iSscfgid = aSscfgid;
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
	 * This method sets the value for the 'Annotated_only' column
	 * 
	 * @param	aAnnotated_only	boolean with the value for the Annotated_only column.
	 */
	public void setAnnotated_only(boolean aAnnotated_only) {
		this.iAnnotated_only = aAnnotated_only;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Vectorization_type' column
	 * 
	 * @param	aVectorization_type	int with the value for the Vectorization_type column.
	 */
	public void setVectorization_type(int aVectorization_type) {
		this.iVectorization_type = aVectorization_type;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Bin_width' column
	 * 
	 * @param	aBin_width	Number with the value for the Bin_width column.
	 */
	public void setBin_width(Number aBin_width) {
		this.iBin_width = aBin_width;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Bin_shift' column
	 * 
	 * @param	aBin_shift	Number with the value for the Bin_shift column.
	 */
	public void setBin_shift(Number aBin_shift) {
		this.iBin_shift = aBin_shift;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Profile_shape' column
	 * 
	 * @param	aProfile_shape	String with the value for the Profile_shape column.
	 */
	public void setProfile_shape(String aProfile_shape) {
		this.iProfile_shape = aProfile_shape;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Peakbase_width' column
	 * 
	 * @param	aPeakbase_width	Number with the value for the Peakbase_width column.
	 */
	public void setPeakbase_width(Number aPeakbase_width) {
		this.iPeakbase_width = aPeakbase_width;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pick_count' column
	 * 
	 * @param	aPick_count	long with the value for the Pick_count column.
	 */
	public void setPick_count(long aPick_count) {
		this.iPick_count = aPick_count;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Transformation_type' column
	 * 
	 * @param	aTransformation_type	int with the value for the Transformation_type column.
	 */
	public void setTransformation_type(int aTransformation_type) {
		this.iTransformation_type = aTransformation_type;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Measure_type' column
	 * 
	 * @param	aMeasure_type	int with the value for the Measure_type column.
	 */
	public void setMeasure_type(int aMeasure_type) {
		this.iMeasure_type = aMeasure_type;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Corr_offsets' column
	 * 
	 * @param	aCorr_offsets	long with the value for the Corr_offsets column.
	 */
	public void setCorr_offsets(long aCorr_offsets) {
		this.iCorr_offsets = aCorr_offsets;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Score_threshold' column
	 * 
	 * @param	aScore_threshold	Number with the value for the Score_threshold column.
	 */
	public void setScore_threshold(Number aScore_threshold) {
		this.iScore_threshold = aScore_threshold;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM sscfg WHERE sscfgid = ?");
		lStat.setLong(1, iSscfgid);
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
		if(!aKeys.containsKey(SSCFGID)) {
			throw new IllegalArgumentException("Primary key field 'SSCFGID' is missing in HashMap!");
		} else {
			iSscfgid = ((Long)aKeys.get(SSCFGID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM sscfg WHERE sscfgid = ?");
		lStat.setLong(1, iSscfgid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSscfgid = lRS.getLong("sscfgid");
			iPrecursor_tol = (Number)lRS.getObject("precursor_tol");
			iAnnotated_only = lRS.getBoolean("annotated_only");
			iVectorization_type = lRS.getInt("vectorization_type");
			iBin_width = (Number)lRS.getObject("bin_width");
			iBin_shift = (Number)lRS.getObject("bin_shift");
			iProfile_shape = (String)lRS.getObject("profile_shape");
			iPeakbase_width = (Number)lRS.getObject("peakbase_width");
			iPick_count = lRS.getLong("pick_count");
			iTransformation_type = lRS.getInt("transformation_type");
			iMeasure_type = lRS.getInt("measure_type");
			iCorr_offsets = lRS.getLong("corr_offsets");
			iScore_threshold = (Number)lRS.getObject("score_threshold");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'sscfg' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'sscfg' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from sscfg";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<SscfgTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<SscfgTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<SscfgTableAccessor>  entities = new ArrayList<SscfgTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new SscfgTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE sscfg SET sscfgid = ?, precursor_tol = ?, annotated_only = ?, vectorization_type = ?, bin_width = ?, bin_shift = ?, profile_shape = ?, peakbase_width = ?, pick_count = ?, transformation_type = ?, measure_type = ?, corr_offsets = ?, score_threshold = ? WHERE sscfgid = ?");
		lStat.setLong(1, iSscfgid);
		lStat.setObject(2, iPrecursor_tol);
		lStat.setBoolean(3, iAnnotated_only);
		lStat.setInt(4, iVectorization_type);
		lStat.setObject(5, iBin_width);
		lStat.setObject(6, iBin_shift);
		lStat.setObject(7, iProfile_shape);
		lStat.setObject(8, iPeakbase_width);
		lStat.setLong(9, iPick_count);
		lStat.setInt(10, iTransformation_type);
		lStat.setInt(11, iMeasure_type);
		lStat.setLong(12, iCorr_offsets);
		lStat.setObject(13, iScore_threshold);
		lStat.setLong(14, iSscfgid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO sscfg (sscfgid, precursor_tol, annotated_only, vectorization_type, bin_width, bin_shift, profile_shape, peakbase_width, pick_count, transformation_type, measure_type, corr_offsets, score_threshold) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		if(iSscfgid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSscfgid);
		}
		if(iPrecursor_tol == null) {
			lStat.setNull(2, 3);
		} else {
			lStat.setObject(2, iPrecursor_tol);
		}
		lStat.setBoolean(3, iAnnotated_only);
		if(iVectorization_type == Integer.MIN_VALUE) {
			lStat.setNull(4, -6);
		} else {
			lStat.setInt(4, iVectorization_type);
		}
		if(iBin_width == null) {
			lStat.setNull(5, 3);
		} else {
			lStat.setObject(5, iBin_width);
		}
		if(iBin_shift == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, iBin_shift);
		}
		if(iProfile_shape == null) {
			lStat.setNull(7, 12);
		} else {
			lStat.setObject(7, iProfile_shape);
		}
		if(iPeakbase_width == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iPeakbase_width);
		}
		if(iPick_count == Long.MIN_VALUE) {
			lStat.setNull(9, 4);
		} else {
			lStat.setLong(9, iPick_count);
		}
		if(iTransformation_type == Integer.MIN_VALUE) {
			lStat.setNull(10, -6);
		} else {
			lStat.setInt(10, iTransformation_type);
		}
		if(iMeasure_type == Integer.MIN_VALUE) {
			lStat.setNull(11, -6);
		} else {
			lStat.setInt(11, iMeasure_type);
		}
		if(iCorr_offsets == Long.MIN_VALUE) {
			lStat.setNull(12, 4);
		} else {
			lStat.setLong(12, iCorr_offsets);
		}
		if(iScore_threshold == null) {
			lStat.setNull(13, 3);
		} else {
			lStat.setObject(13, iScore_threshold);
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
			iSscfgid = ((Number) iKeys[0]).longValue();
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