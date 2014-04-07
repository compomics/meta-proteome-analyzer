/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:49
 */
package de.mpa.db.accessor;

import java.io.Serializable;
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
 * This class is a generated accessor for the Inspecthit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class InspecthitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'inspecthitid' column.
	 */
	protected long iInspecthitid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_searchspectrumid' column.
	 */
	protected long iFk_searchspectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_proteinid' column.
	 */
	protected long iFk_proteinid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'scannumber' column.
	 */
	protected long iScannumber = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'mq_score' column.
	 */
	protected Number iMq_score = null;


	/**
	 * This variable represents the contents for the 'length' column.
	 */
	protected long iLength = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'total_prm_score' column.
	 */
	protected Number iTotal_prm_score = null;


	/**
	 * This variable represents the contents for the 'median_prm_score' column.
	 */
	protected Number iMedian_prm_score = null;


	/**
	 * This variable represents the contents for the 'fraction_y' column.
	 */
	protected Number iFraction_y = null;


	/**
	 * This variable represents the contents for the 'fraction_b' column.
	 */
	protected Number iFraction_b = null;


	/**
	 * This variable represents the contents for the 'intensity' column.
	 */
	protected Number iIntensity = null;


	/**
	 * This variable represents the contents for the 'ntt' column.
	 */
	protected Number iNtt = null;


	/**
	 * This variable represents the contents for the 'p_value' column.
	 */
	protected Number iP_value = null;


	/**
	 * This variable represents the contents for the 'f_score' column.
	 */
	protected Number iF_score = null;


	/**
	 * This variable represents the contents for the 'deltascore' column.
	 */
	protected Number iDeltascore = null;


	/**
	 * This variable represents the contents for the 'deltascore_other' column.
	 */
	protected Number iDeltascore_other = null;


	/**
	 * This variable represents the contents for the 'recordnumber' column.
	 */
	protected Number iRecordnumber = null;


	/**
	 * This variable represents the contents for the 'dbfilepos' column.
	 */
	protected long iDbfilepos = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'specfilepos' column.
	 */
	protected long iSpecfilepos = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'precursor_mz_error' column.
	 */
	protected Number iPrecursor_mz_error = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'inspecthitid' column.
	 */
	public static final String INSPECTHITID = "INSPECTHITID";

	/**
	 * This variable represents the key for the 'fk_searchspectrumid' column.
	 */
	public static final String FK_SEARCHSPECTRUMID = "FK_SEARCHSPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'fk_proteinid' column.
	 */
	public static final String FK_PROTEINID = "FK_PROTEINID";

	/**
	 * This variable represents the key for the 'scannumber' column.
	 */
	public static final String SCANNUMBER = "SCANNUMBER";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'mq_score' column.
	 */
	public static final String MQ_SCORE = "MQ_SCORE";

	/**
	 * This variable represents the key for the 'length' column.
	 */
	public static final String LENGTH = "LENGTH";

	/**
	 * This variable represents the key for the 'total_prm_score' column.
	 */
	public static final String TOTAL_PRM_SCORE = "TOTAL_PRM_SCORE";

	/**
	 * This variable represents the key for the 'median_prm_score' column.
	 */
	public static final String MEDIAN_PRM_SCORE = "MEDIAN_PRM_SCORE";

	/**
	 * This variable represents the key for the 'fraction_y' column.
	 */
	public static final String FRACTION_Y = "FRACTION_Y";

	/**
	 * This variable represents the key for the 'fraction_b' column.
	 */
	public static final String FRACTION_B = "FRACTION_B";

	/**
	 * This variable represents the key for the 'intensity' column.
	 */
	public static final String INTENSITY = "INTENSITY";

	/**
	 * This variable represents the key for the 'ntt' column.
	 */
	public static final String NTT = "NTT";

	/**
	 * This variable represents the key for the 'p_value' column.
	 */
	public static final String P_VALUE = "P_VALUE";

	/**
	 * This variable represents the key for the 'f_score' column.
	 */
	public static final String F_SCORE = "F_SCORE";

	/**
	 * This variable represents the key for the 'deltascore' column.
	 */
	public static final String DELTASCORE = "DELTASCORE";

	/**
	 * This variable represents the key for the 'deltascore_other' column.
	 */
	public static final String DELTASCORE_OTHER = "DELTASCORE_OTHER";

	/**
	 * This variable represents the key for the 'recordnumber' column.
	 */
	public static final String RECORDNUMBER = "RECORDNUMBER";

	/**
	 * This variable represents the key for the 'dbfilepos' column.
	 */
	public static final String DBFILEPOS = "DBFILEPOS";

	/**
	 * This variable represents the key for the 'specfilepos' column.
	 */
	public static final String SPECFILEPOS = "SPECFILEPOS";

	/**
	 * This variable represents the key for the 'precursor_mz_error' column.
	 */
	public static final String PRECURSOR_MZ_ERROR = "PRECURSOR_MZ_ERROR";

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
	public InspecthitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'InspecthitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public InspecthitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(INSPECTHITID)) {
			this.iInspecthitid = ((Long)aParams.get(INSPECTHITID)).longValue();
		}
		if(aParams.containsKey(FK_SEARCHSPECTRUMID)) {
			this.iFk_searchspectrumid = ((Long)aParams.get(FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(FK_PROTEINID)) {
			this.iFk_proteinid = ((Long)aParams.get(FK_PROTEINID)).longValue();
		}
		if(aParams.containsKey(SCANNUMBER)) {
			this.iScannumber = ((Long)aParams.get(SCANNUMBER)).longValue();
		}
		if(aParams.containsKey(CHARGE)) {
			this.iCharge = ((Long)aParams.get(CHARGE)).longValue();
		}
		if(aParams.containsKey(MQ_SCORE)) {
			this.iMq_score = (Number)aParams.get(MQ_SCORE);
		}
		if(aParams.containsKey(LENGTH)) {
			this.iLength = ((Long)aParams.get(LENGTH)).longValue();
		}
		if(aParams.containsKey(TOTAL_PRM_SCORE)) {
			this.iTotal_prm_score = (Number)aParams.get(TOTAL_PRM_SCORE);
		}
		if(aParams.containsKey(MEDIAN_PRM_SCORE)) {
			this.iMedian_prm_score = (Number)aParams.get(MEDIAN_PRM_SCORE);
		}
		if(aParams.containsKey(FRACTION_Y)) {
			this.iFraction_y = (Number)aParams.get(FRACTION_Y);
		}
		if(aParams.containsKey(FRACTION_B)) {
			this.iFraction_b = (Number)aParams.get(FRACTION_B);
		}
		if(aParams.containsKey(INTENSITY)) {
			this.iIntensity = (Number)aParams.get(INTENSITY);
		}
		if(aParams.containsKey(NTT)) {
			this.iNtt = (Number)aParams.get(NTT);
		}
		if(aParams.containsKey(P_VALUE)) {
			this.iP_value = (Number)aParams.get(P_VALUE);
		}
		if(aParams.containsKey(F_SCORE)) {
			this.iF_score = (Number)aParams.get(F_SCORE);
		}
		if(aParams.containsKey(DELTASCORE)) {
			this.iDeltascore = (Number)aParams.get(DELTASCORE);
		}
		if(aParams.containsKey(DELTASCORE_OTHER)) {
			this.iDeltascore_other = (Number)aParams.get(DELTASCORE_OTHER);
		}
		if(aParams.containsKey(RECORDNUMBER)) {
			this.iRecordnumber = (Number)aParams.get(RECORDNUMBER);
		}
		if(aParams.containsKey(DBFILEPOS)) {
			this.iDbfilepos = ((Long)aParams.get(DBFILEPOS)).longValue();
		}
		if(aParams.containsKey(SPECFILEPOS)) {
			this.iSpecfilepos = ((Long)aParams.get(SPECFILEPOS)).longValue();
		}
		if(aParams.containsKey(PRECURSOR_MZ_ERROR)) {
			this.iPrecursor_mz_error = (Number)aParams.get(PRECURSOR_MZ_ERROR);
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
	 * This constructor allows the creation of the 'InspecthitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Inspecthit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public InspecthitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iInspecthitid = aResultSet.getLong("inspecthitid");
		this.iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iFk_proteinid = aResultSet.getLong("fk_proteinid");
		this.iScannumber = aResultSet.getLong("scannumber");
		this.iCharge = aResultSet.getLong("charge");
		this.iMq_score = (Number)aResultSet.getObject("mq_score");
		this.iLength = aResultSet.getLong("length");
		this.iTotal_prm_score = (Number)aResultSet.getObject("total_prm_score");
		this.iMedian_prm_score = (Number)aResultSet.getObject("median_prm_score");
		this.iFraction_y = (Number)aResultSet.getObject("fraction_y");
		this.iFraction_b = (Number)aResultSet.getObject("fraction_b");
		this.iIntensity = (Number)aResultSet.getObject("intensity");
		this.iNtt = (Number)aResultSet.getObject("ntt");
		this.iP_value = (Number)aResultSet.getObject("p_value");
		this.iF_score = (Number)aResultSet.getObject("f_score");
		this.iDeltascore = (Number)aResultSet.getObject("deltascore");
		this.iDeltascore_other = (Number)aResultSet.getObject("deltascore_other");
		this.iRecordnumber = (Number)aResultSet.getObject("recordnumber");
		this.iDbfilepos = aResultSet.getLong("dbfilepos");
		this.iSpecfilepos = aResultSet.getLong("specfilepos");
		this.iPrecursor_mz_error = (Number)aResultSet.getObject("precursor_mz_error");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Inspecthitid' column
	 * 
	 * @return	long	with the value for the Inspecthitid column.
	 */
	public long getInspecthitid() {
		return this.iInspecthitid;
	}

	/**
	 * This method returns the value for the 'Fk_searchspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_searchspectrumid column.
	 */
	public long getFk_searchspectrumid() {
		return this.iFk_searchspectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return	long	with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return this.iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Fk_proteinid' column
	 * 
	 * @return	long	with the value for the Fk_proteinid column.
	 */
	public long getFk_proteinid() {
		return this.iFk_proteinid;
	}

	/**
	 * This method returns the value for the 'Scannumber' column
	 * 
	 * @return	long	with the value for the Scannumber column.
	 */
	public long getScannumber() {
		return this.iScannumber;
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
	 * This method returns the value for the 'Mq_score' column
	 * 
	 * @return	Number	with the value for the Mq_score column.
	 */
	public Number getMq_score() {
		return this.iMq_score;
	}

	/**
	 * This method returns the value for the 'Length' column
	 * 
	 * @return	long	with the value for the Length column.
	 */
	public long getLength() {
		return this.iLength;
	}

	/**
	 * This method returns the value for the 'Total_prm_score' column
	 * 
	 * @return	Number	with the value for the Total_prm_score column.
	 */
	public Number getTotal_prm_score() {
		return this.iTotal_prm_score;
	}

	/**
	 * This method returns the value for the 'Median_prm_score' column
	 * 
	 * @return	Number	with the value for the Median_prm_score column.
	 */
	public Number getMedian_prm_score() {
		return this.iMedian_prm_score;
	}

	/**
	 * This method returns the value for the 'Fraction_y' column
	 * 
	 * @return	Number	with the value for the Fraction_y column.
	 */
	public Number getFraction_y() {
		return this.iFraction_y;
	}

	/**
	 * This method returns the value for the 'Fraction_b' column
	 * 
	 * @return	Number	with the value for the Fraction_b column.
	 */
	public Number getFraction_b() {
		return this.iFraction_b;
	}

	/**
	 * This method returns the value for the 'Intensity' column
	 * 
	 * @return	Number	with the value for the Intensity column.
	 */
	public Number getIntensity() {
		return this.iIntensity;
	}

	/**
	 * This method returns the value for the 'Ntt' column
	 * 
	 * @return	Number	with the value for the Ntt column.
	 */
	public Number getNtt() {
		return this.iNtt;
	}

	/**
	 * This method returns the value for the 'P_value' column
	 * 
	 * @return	Number	with the value for the P_value column.
	 */
	public Number getP_value() {
		return this.iP_value;
	}

	/**
	 * This method returns the value for the 'F_score' column
	 * 
	 * @return	Number	with the value for the F_score column.
	 */
	public Number getF_score() {
		return this.iF_score;
	}

	/**
	 * This method returns the value for the 'Deltascore' column
	 * 
	 * @return	Number	with the value for the Deltascore column.
	 */
	public Number getDeltascore() {
		return this.iDeltascore;
	}

	/**
	 * This method returns the value for the 'Deltascore_other' column
	 * 
	 * @return	Number	with the value for the Deltascore_other column.
	 */
	public Number getDeltascore_other() {
		return this.iDeltascore_other;
	}

	/**
	 * This method returns the value for the 'Recordnumber' column
	 * 
	 * @return	Number	with the value for the Recordnumber column.
	 */
	public Number getRecordnumber() {
		return this.iRecordnumber;
	}

	/**
	 * This method returns the value for the 'Dbfilepos' column
	 * 
	 * @return	long	with the value for the Dbfilepos column.
	 */
	public long getDbfilepos() {
		return this.iDbfilepos;
	}

	/**
	 * This method returns the value for the 'Specfilepos' column
	 * 
	 * @return	long	with the value for the Specfilepos column.
	 */
	public long getSpecfilepos() {
		return this.iSpecfilepos;
	}

	/**
	 * This method returns the value for the 'Precursor_mz_error' column
	 * 
	 * @return	Number	with the value for the Precursor_mz_error column.
	 */
	public Number getPrecursor_mz_error() {
		return this.iPrecursor_mz_error;
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
	 * This method sets the value for the 'Inspecthitid' column
	 * 
	 * @param	aInspecthitid	long with the value for the Inspecthitid column.
	 */
	public void setInspecthitid(long aInspecthitid) {
		this.iInspecthitid = aInspecthitid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_searchspectrumid' column
	 * 
	 * @param	aFk_searchspectrumid	long with the value for the Fk_searchspectrumid column.
	 */
	public void setFk_searchspectrumid(long aFk_searchspectrumid) {
		this.iFk_searchspectrumid = aFk_searchspectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param	aFk_peptideid	long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
		this.iFk_peptideid = aFk_peptideid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_proteinid' column
	 * 
	 * @param	aFk_proteinid	long with the value for the Fk_proteinid column.
	 */
	public void setFk_proteinid(long aFk_proteinid) {
		this.iFk_proteinid = aFk_proteinid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Scannumber' column
	 * 
	 * @param	aScannumber	long with the value for the Scannumber column.
	 */
	public void setScannumber(long aScannumber) {
		this.iScannumber = aScannumber;
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
	 * This method sets the value for the 'Mq_score' column
	 * 
	 * @param	aMq_score	Number with the value for the Mq_score column.
	 */
	public void setMq_score(Number aMq_score) {
		this.iMq_score = aMq_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Length' column
	 * 
	 * @param	aLength	long with the value for the Length column.
	 */
	public void setLength(long aLength) {
		this.iLength = aLength;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Total_prm_score' column
	 * 
	 * @param	aTotal_prm_score	Number with the value for the Total_prm_score column.
	 */
	public void setTotal_prm_score(Number aTotal_prm_score) {
		this.iTotal_prm_score = aTotal_prm_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Median_prm_score' column
	 * 
	 * @param	aMedian_prm_score	Number with the value for the Median_prm_score column.
	 */
	public void setMedian_prm_score(Number aMedian_prm_score) {
		this.iMedian_prm_score = aMedian_prm_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fraction_y' column
	 * 
	 * @param	aFraction_y	Number with the value for the Fraction_y column.
	 */
	public void setFraction_y(Number aFraction_y) {
		this.iFraction_y = aFraction_y;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fraction_b' column
	 * 
	 * @param	aFraction_b	Number with the value for the Fraction_b column.
	 */
	public void setFraction_b(Number aFraction_b) {
		this.iFraction_b = aFraction_b;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Intensity' column
	 * 
	 * @param	aIntensity	Number with the value for the Intensity column.
	 */
	public void setIntensity(Number aIntensity) {
		this.iIntensity = aIntensity;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Ntt' column
	 * 
	 * @param	aNtt	Number with the value for the Ntt column.
	 */
	public void setNtt(Number aNtt) {
		this.iNtt = aNtt;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'P_value' column
	 * 
	 * @param	aP_value	Number with the value for the P_value column.
	 */
	public void setP_value(Number aP_value) {
		this.iP_value = aP_value;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'F_score' column
	 * 
	 * @param	aF_score	Number with the value for the F_score column.
	 */
	public void setF_score(Number aF_score) {
		this.iF_score = aF_score;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Deltascore' column
	 * 
	 * @param	aDeltascore	Number with the value for the Deltascore column.
	 */
	public void setDeltascore(Number aDeltascore) {
		this.iDeltascore = aDeltascore;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Deltascore_other' column
	 * 
	 * @param	aDeltascore_other	Number with the value for the Deltascore_other column.
	 */
	public void setDeltascore_other(Number aDeltascore_other) {
		this.iDeltascore_other = aDeltascore_other;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Recordnumber' column
	 * 
	 * @param	aRecordnumber	Number with the value for the Recordnumber column.
	 */
	public void setRecordnumber(Number aRecordnumber) {
		this.iRecordnumber = aRecordnumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Dbfilepos' column
	 * 
	 * @param	aDbfilepos	long with the value for the Dbfilepos column.
	 */
	public void setDbfilepos(long aDbfilepos) {
		this.iDbfilepos = aDbfilepos;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Specfilepos' column
	 * 
	 * @param	aSpecfilepos	long with the value for the Specfilepos column.
	 */
	public void setSpecfilepos(long aSpecfilepos) {
		this.iSpecfilepos = aSpecfilepos;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Precursor_mz_error' column
	 * 
	 * @param	aPrecursor_mz_error	Number with the value for the Precursor_mz_error column.
	 */
	public void setPrecursor_mz_error(Number aPrecursor_mz_error) {
		this.iPrecursor_mz_error = aPrecursor_mz_error;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM inspecthit WHERE inspecthitid = ?");
		lStat.setLong(1, iInspecthitid);
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
		if(!aKeys.containsKey(INSPECTHITID)) {
			throw new IllegalArgumentException("Primary key field 'INSPECTHITID' is missing in HashMap!");
		} else {
			iInspecthitid = ((Long)aKeys.get(INSPECTHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM inspecthit WHERE inspecthitid = ?");
		lStat.setLong(1, iInspecthitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iInspecthitid = lRS.getLong("inspecthitid");
			iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iFk_proteinid = lRS.getLong("fk_proteinid");
			iScannumber = lRS.getLong("scannumber");
			iCharge = lRS.getLong("charge");
			iMq_score = (Number)lRS.getObject("mq_score");
			iLength = lRS.getLong("length");
			iTotal_prm_score = (Number)lRS.getObject("total_prm_score");
			iMedian_prm_score = (Number)lRS.getObject("median_prm_score");
			iFraction_y = (Number)lRS.getObject("fraction_y");
			iFraction_b = (Number)lRS.getObject("fraction_b");
			iIntensity = (Number)lRS.getObject("intensity");
			iNtt = (Number)lRS.getObject("ntt");
			iP_value = (Number)lRS.getObject("p_value");
			iF_score = (Number)lRS.getObject("f_score");
			iDeltascore = (Number)lRS.getObject("deltascore");
			iDeltascore_other = (Number)lRS.getObject("deltascore_other");
			iRecordnumber = (Number)lRS.getObject("recordnumber");
			iDbfilepos = lRS.getLong("dbfilepos");
			iSpecfilepos = lRS.getLong("specfilepos");
			iPrecursor_mz_error = (Number)lRS.getObject("precursor_mz_error");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'inspecthit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'inspecthit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from inspecthit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<InspecthitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<InspecthitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<InspecthitTableAccessor>  entities = new ArrayList<InspecthitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new InspecthitTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE inspecthit SET inspecthitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, scannumber = ?, charge = ?, mq_score = ?, length = ?, total_prm_score = ?, median_prm_score = ?, fraction_y = ?, fraction_b = ?, intensity = ?, ntt = ?, p_value = ?, f_score = ?, deltascore = ?, deltascore_other = ?, recordnumber = ?, dbfilepos = ?, specfilepos = ?, precursor_mz_error = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE inspecthitid = ?");
		lStat.setLong(1, iInspecthitid);
		lStat.setLong(2, iFk_searchspectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iFk_proteinid);
		lStat.setLong(5, iScannumber);
		lStat.setLong(6, iCharge);
		lStat.setObject(7, iMq_score);
		lStat.setLong(8, iLength);
		lStat.setObject(9, iTotal_prm_score);
		lStat.setObject(10, iMedian_prm_score);
		lStat.setObject(11, iFraction_y);
		lStat.setObject(12, iFraction_b);
		lStat.setObject(13, iIntensity);
		lStat.setObject(14, iNtt);
		lStat.setObject(15, iP_value);
		lStat.setObject(16, iF_score);
		lStat.setObject(17, iDeltascore);
		lStat.setObject(18, iDeltascore_other);
		lStat.setObject(19, iRecordnumber);
		lStat.setLong(20, iDbfilepos);
		lStat.setLong(21, iSpecfilepos);
		lStat.setObject(22, iPrecursor_mz_error);
		lStat.setObject(23, iCreationdate);
		lStat.setLong(24, iInspecthitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO inspecthit (inspecthitid, fk_searchspectrumid, fk_peptideid, fk_proteinid, scannumber, charge, mq_score, length, total_prm_score, median_prm_score, fraction_y, fraction_b, intensity, ntt, p_value, f_score, deltascore, deltascore_other, recordnumber, dbfilepos, specfilepos, precursor_mz_error, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(iInspecthitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iInspecthitid);
		}
		if(iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_searchspectrumid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_peptideid);
		}
		if(iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iFk_proteinid);
		}
		if(iScannumber == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, iScannumber);
		}
		if(iCharge == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, iCharge);
		}
		if(iMq_score == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, iMq_score);
		}
		if(iLength == Long.MIN_VALUE) {
			lStat.setNull(8, 4);
		} else {
			lStat.setLong(8, iLength);
		}
		if(iTotal_prm_score == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iTotal_prm_score);
		}
		if(iMedian_prm_score == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, iMedian_prm_score);
		}
		if(iFraction_y == null) {
			lStat.setNull(11, 3);
		} else {
			lStat.setObject(11, iFraction_y);
		}
		if(iFraction_b == null) {
			lStat.setNull(12, 3);
		} else {
			lStat.setObject(12, iFraction_b);
		}
		if(iIntensity == null) {
			lStat.setNull(13, 3);
		} else {
			lStat.setObject(13, iIntensity);
		}
		if(iNtt == null) {
			lStat.setNull(14, 3);
		} else {
			lStat.setObject(14, iNtt);
		}
		if(iP_value == null) {
			lStat.setNull(15, 3);
		} else {
			lStat.setObject(15, iP_value);
		}
		if(iF_score == null) {
			lStat.setNull(16, 3);
		} else {
			lStat.setObject(16, iF_score);
		}
		if(iDeltascore == null) {
			lStat.setNull(17, 3);
		} else {
			lStat.setObject(17, iDeltascore);
		}
		if(iDeltascore_other == null) {
			lStat.setNull(18, 3);
		} else {
			lStat.setObject(18, iDeltascore_other);
		}
		if(iRecordnumber == null) {
			lStat.setNull(19, 3);
		} else {
			lStat.setObject(19, iRecordnumber);
		}
		if(iDbfilepos == Long.MIN_VALUE) {
			lStat.setNull(20, 4);
		} else {
			lStat.setLong(20, iDbfilepos);
		}
		if(iSpecfilepos == Long.MIN_VALUE) {
			lStat.setNull(21, 4);
		} else {
			lStat.setLong(21, iSpecfilepos);
		}
		if(iPrecursor_mz_error == null) {
			lStat.setNull(22, 3);
		} else {
			lStat.setObject(22, iPrecursor_mz_error);
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
			iInspecthitid = ((Number) iKeys[0]).longValue();
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