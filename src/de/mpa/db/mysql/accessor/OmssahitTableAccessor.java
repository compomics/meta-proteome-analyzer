/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:47
 */
package de.mpa.db.mysql.accessor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
 * This class is a generated accessor for the Omssahit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
@SuppressWarnings("serial")
public class OmssahitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'omssahitid' column.
	 */
	protected long iOmssahitid = Long.MIN_VALUE;


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
	 * This variable represents the contents for the 'hitsetnumber' column.
	 */
	protected long iHitsetnumber = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'evalue' column.
	 */
	protected Number iEvalue;


	/**
	 * This variable represents the contents for the 'pvalue' column.
	 */
	protected Number iPvalue;


	/**
	 * This variable represents the contents for the 'charge' column.
	 */
	protected long iCharge = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'mass' column.
	 */
	protected Number iMass;


	/**
	 * This variable represents the contents for the 'theomass' column.
	 */
	protected Number iTheomass;


	/**
	 * This variable represents the contents for the 'start' column.
	 */
	protected String iStart;


	/**
	 * This variable represents the contents for the 'end' column.
	 */
	protected String iEnd;


	/**
	 * This variable represents the contents for the 'qvalue' column.
	 */
	protected Number iQvalue;


	/**
	 * This variable represents the contents for the 'pep' column.
	 */
	protected Number iPep;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected Timestamp iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected String iModificationdate;


	/**
	 * This variable represents the key for the 'omssahitid' column.
	 */
	public static final String OMSSAHITID = "OMSSAHITID";

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
	 * This variable represents the key for the 'hitsetnumber' column.
	 */
	public static final String HITSETNUMBER = "HITSETNUMBER";

	/**
	 * This variable represents the key for the 'evalue' column.
	 */
	public static final String EVALUE = "EVALUE";

	/**
	 * This variable represents the key for the 'pvalue' column.
	 */
	public static final String PVALUE = "PVALUE";

	/**
	 * This variable represents the key for the 'charge' column.
	 */
	public static final String CHARGE = "CHARGE";

	/**
	 * This variable represents the key for the 'mass' column.
	 */
	public static final String MASS = "MASS";

	/**
	 * This variable represents the key for the 'theomass' column.
	 */
	public static final String THEOMASS = "THEOMASS";

	/**
	 * This variable represents the key for the 'start' column.
	 */
	public static final String START = "START";

	/**
	 * This variable represents the key for the 'end' column.
	 */
	public static final String END = "END";

	/**
	 * This variable represents the key for the 'qvalue' column.
	 */
	public static final String QVALUE = "QVALUE";

	/**
	 * This variable represents the key for the 'pep' column.
	 */
	public static final String PEP = "PEP";

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
	public OmssahitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'OmssahitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public OmssahitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(OmssahitTableAccessor.OMSSAHITID)) {
            iOmssahitid = ((Long)aParams.get(OmssahitTableAccessor.OMSSAHITID)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.FK_SEARCHSPECTRUMID)) {
            iFk_searchspectrumid = ((Long)aParams.get(OmssahitTableAccessor.FK_SEARCHSPECTRUMID)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.FK_PEPTIDEID)) {
            iFk_peptideid = ((Long)aParams.get(OmssahitTableAccessor.FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.FK_PROTEINID)) {
            iFk_proteinid = ((Long)aParams.get(OmssahitTableAccessor.FK_PROTEINID)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.HITSETNUMBER)) {
            iHitsetnumber = ((Long)aParams.get(OmssahitTableAccessor.HITSETNUMBER)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.EVALUE)) {
            iEvalue = (Number)aParams.get(OmssahitTableAccessor.EVALUE);
		}
		if(aParams.containsKey(OmssahitTableAccessor.PVALUE)) {
            iPvalue = (Number)aParams.get(OmssahitTableAccessor.PVALUE);
		}
		if(aParams.containsKey(OmssahitTableAccessor.CHARGE)) {
            iCharge = ((Long)aParams.get(OmssahitTableAccessor.CHARGE)).longValue();
		}
		if(aParams.containsKey(OmssahitTableAccessor.MASS)) {
            iMass = (Number)aParams.get(OmssahitTableAccessor.MASS);
		}
		if(aParams.containsKey(OmssahitTableAccessor.THEOMASS)) {
            iTheomass = (Number)aParams.get(OmssahitTableAccessor.THEOMASS);
		}
		if(aParams.containsKey(OmssahitTableAccessor.START)) {
            iStart = (String)aParams.get(OmssahitTableAccessor.START);
		}
		if(aParams.containsKey(OmssahitTableAccessor.END)) {
            iEnd = (String)aParams.get(OmssahitTableAccessor.END);
		}
		if(aParams.containsKey(OmssahitTableAccessor.QVALUE)) {
            iQvalue = (Number)aParams.get(OmssahitTableAccessor.QVALUE);
		}
		if(aParams.containsKey(OmssahitTableAccessor.PEP)) {
            iPep = (Number)aParams.get(OmssahitTableAccessor.PEP);
		}
		if(aParams.containsKey(OmssahitTableAccessor.CREATIONDATE)) {
            iCreationdate = (Timestamp)aParams.get(OmssahitTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(OmssahitTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (String)aParams.get(OmssahitTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'OmssahitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Omssahit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public OmssahitTableAccessor(ResultSet aResultSet) throws SQLException {
        iOmssahitid = aResultSet.getLong("omssahitid");
        iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
        iFk_peptideid = aResultSet.getLong("fk_peptideid");
        iFk_proteinid = aResultSet.getLong("fk_proteinid");
        iHitsetnumber = aResultSet.getLong("hitsetnumber");
        iEvalue = (Number)aResultSet.getObject("evalue");
        iPvalue = (Number)aResultSet.getObject("pvalue");
        iCharge = aResultSet.getLong("charge");
        iMass = (Number)aResultSet.getObject("mass");
        iTheomass = (Number)aResultSet.getObject("theomass");
        iStart = (String)aResultSet.getObject("start");
        iEnd = (String)aResultSet.getObject("end");
        iQvalue = (Number)aResultSet.getObject("qvalue");
        iPep = (Number)aResultSet.getObject("pep");
        iCreationdate = (Timestamp)aResultSet.getObject("creationdate");
        iModificationdate = (String)aResultSet.getObject("modificationdate");

        iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Omssahitid' column
	 * 
	 * @return	long	with the value for the Omssahitid column.
	 */
	public long getOmssahitid() {
		return iOmssahitid;
	}

	/**
	 * This method returns the value for the 'Fk_searchspectrumid' column
	 * 
	 * @return	long	with the value for the Fk_searchspectrumid column.
	 */
	public long getFk_searchspectrumid() {
		return iFk_searchspectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return	long	with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Fk_proteinid' column
	 * 
	 * @return	long	with the value for the Fk_proteinid column.
	 */
	public long getFk_proteinid() {
		return iFk_proteinid;
	}

	/**
	 * This method returns the value for the 'Hitsetnumber' column
	 * 
	 * @return	long	with the value for the Hitsetnumber column.
	 */
	public long getHitsetnumber() {
		return iHitsetnumber;
	}

	/**
	 * This method returns the value for the 'Evalue' column
	 * 
	 * @return	Number	with the value for the Evalue column.
	 */
	public Number getEvalue() {
		return iEvalue;
	}

	/**
	 * This method returns the value for the 'Pvalue' column
	 * 
	 * @return	Number	with the value for the Pvalue column.
	 */
	public Number getPvalue() {
		return iPvalue;
	}

	/**
	 * This method returns the value for the 'Charge' column
	 * 
	 * @return	long	with the value for the Charge column.
	 */
	public long getCharge() {
		return iCharge;
	}

	/**
	 * This method returns the value for the 'Mass' column
	 * 
	 * @return	Number	with the value for the Mass column.
	 */
	public Number getMass() {
		return iMass;
	}

	/**
	 * This method returns the value for the 'Theomass' column
	 * 
	 * @return	Number	with the value for the Theomass column.
	 */
	public Number getTheomass() {
		return iTheomass;
	}

	/**
	 * This method returns the value for the 'Start' column
	 * 
	 * @return	String	with the value for the Start column.
	 */
	public String getStart() {
		return iStart;
	}

	/**
	 * This method returns the value for the 'End' column
	 * 
	 * @return	String	with the value for the End column.
	 */
	public String getEnd() {
		return iEnd;
	}

	/**
	 * This method returns the value for the 'Qvalue' column
	 * 
	 * @return	Number	with the value for the Qvalue column.
	 */
	public Number getQvalue() {
		return iQvalue;
	}

	/**
	 * This method returns the value for the 'Pep' column
	 * 
	 * @return	Number	with the value for the Pep column.
	 */
	public Number getPep() {
		return iPep;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public Timestamp getCreationdate() {
		return iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	String	with the value for the Modificationdate column.
	 */
	public String getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Omssahitid' column
	 * 
	 * @param	aOmssahitid	long with the value for the Omssahitid column.
	 */
	public void setOmssahitid(long aOmssahitid) {
        iOmssahitid = aOmssahitid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_searchspectrumid' column
	 * 
	 * @param	aFk_searchspectrumid	long with the value for the Fk_searchspectrumid column.
	 */
	public void setFk_searchspectrumid(long aFk_searchspectrumid) {
        iFk_searchspectrumid = aFk_searchspectrumid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param	aFk_peptideid	long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
        iFk_peptideid = aFk_peptideid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_proteinid' column
	 * 
	 * @param	aFk_proteinid	long with the value for the Fk_proteinid column.
	 */
	public void setFk_proteinid(long aFk_proteinid) {
        iFk_proteinid = aFk_proteinid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Hitsetnumber' column
	 * 
	 * @param	aHitsetnumber	long with the value for the Hitsetnumber column.
	 */
	public void setHitsetnumber(long aHitsetnumber) {
        iHitsetnumber = aHitsetnumber;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Evalue' column
	 * 
	 * @param	aEvalue	Number with the value for the Evalue column.
	 */
	public void setEvalue(Number aEvalue) {
        iEvalue = aEvalue;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pvalue' column
	 * 
	 * @param	aPvalue	Number with the value for the Pvalue column.
	 */
	public void setPvalue(Number aPvalue) {
        iPvalue = aPvalue;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Charge' column
	 * 
	 * @param	aCharge	long with the value for the Charge column.
	 */
	public void setCharge(long aCharge) {
        iCharge = aCharge;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Mass' column
	 * 
	 * @param	aMass	Number with the value for the Mass column.
	 */
	public void setMass(Number aMass) {
        iMass = aMass;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Theomass' column
	 * 
	 * @param	aTheomass	Number with the value for the Theomass column.
	 */
	public void setTheomass(Number aTheomass) {
        iTheomass = aTheomass;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Start' column
	 * 
	 * @param	aStart	String with the value for the Start column.
	 */
	public void setStart(String aStart) {
        iStart = aStart;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'End' column
	 * 
	 * @param	aEnd	String with the value for the End column.
	 */
	public void setEnd(String aEnd) {
        iEnd = aEnd;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Qvalue' column
	 * 
	 * @param	aQvalue	Number with the value for the Qvalue column.
	 */
	public void setQvalue(Number aQvalue) {
        iQvalue = aQvalue;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pep' column
	 * 
	 * @param	aPep	Number with the value for the Pep column.
	 */
	public void setPep(Number aPep) {
        iPep = aPep;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(Timestamp aCreationdate) {
        iCreationdate = aCreationdate;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	String with the value for the Modificationdate column.
	 */
	public void setModificationdate(String aModificationdate) {
        iModificationdate = aModificationdate;
        iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM omssahit WHERE omssahitid = ?");
		lStat.setLong(1, this.iOmssahitid);
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
		if(!aKeys.containsKey(OmssahitTableAccessor.OMSSAHITID)) {
			throw new IllegalArgumentException("Primary key field 'OMSSAHITID' is missing in HashMap!");
		} else {
            this.iOmssahitid = ((Long)aKeys.get(OmssahitTableAccessor.OMSSAHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM omssahit WHERE omssahitid = ?");
		lStat.setLong(1, this.iOmssahitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iOmssahitid = lRS.getLong("omssahitid");
            this.iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
            this.iFk_peptideid = lRS.getLong("fk_peptideid");
            this.iFk_proteinid = lRS.getLong("fk_proteinid");
            this.iHitsetnumber = lRS.getLong("hitsetnumber");
            this.iEvalue = (Number)lRS.getObject("evalue");
            this.iPvalue = (Number)lRS.getObject("pvalue");
            this.iCharge = lRS.getLong("charge");
            this.iMass = (Number)lRS.getObject("mass");
            this.iTheomass = (Number)lRS.getObject("theomass");
            this.iStart = (String)lRS.getObject("start");
            this.iEnd = (String)lRS.getObject("end");
            this.iQvalue = (Number)lRS.getObject("qvalue");
            this.iPep = (Number)lRS.getObject("pep");
            this.iCreationdate = (Timestamp)lRS.getObject("creationdate");
            this.iModificationdate = (String)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'omssahit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'omssahit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from omssahit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<OmssahitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<OmssahitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<OmssahitTableAccessor>  entities = new ArrayList<OmssahitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(OmssahitTableAccessor.getBasicSelect());
		while(rs.next()) {
			entities.add(new OmssahitTableAccessor(rs));
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
		if(!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE omssahit SET omssahitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, hitsetnumber = ?, evalue = ?, pvalue = ?, charge = ?, mass = ?, theomass = ?, start = ?, end = ?, qvalue = ?, pep = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE omssahitid = ?");
		lStat.setLong(1, this.iOmssahitid);
		lStat.setLong(2, this.iFk_searchspectrumid);
		lStat.setLong(3, this.iFk_peptideid);
		lStat.setLong(4, this.iFk_proteinid);
		lStat.setLong(5, this.iHitsetnumber);
		lStat.setObject(6, this.iEvalue);
		lStat.setObject(7, this.iPvalue);
		lStat.setLong(8, this.iCharge);
		lStat.setObject(9, this.iMass);
		lStat.setObject(10, this.iTheomass);
		lStat.setObject(11, this.iStart);
		lStat.setObject(12, this.iEnd);
		lStat.setObject(13, this.iQvalue);
		lStat.setObject(14, this.iPep);
		lStat.setObject(15, this.iCreationdate);
		lStat.setLong(16, this.iOmssahitid);
		int result = lStat.executeUpdate();
		lStat.close();
        iUpdated = false;
		return result;
	}


	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO omssahit (omssahitid, fk_searchspectrumid, fk_peptideid, fk_proteinid, hitsetnumber, evalue, pvalue, charge, mass, theomass, start, end, qvalue, pep, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(this.iOmssahitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iOmssahitid);
		}
		if(this.iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, this.iFk_searchspectrumid);
		}
		if(this.iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, this.iFk_peptideid);
		}
		if(this.iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, this.iFk_proteinid);
		}
		if(this.iHitsetnumber == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, this.iHitsetnumber);
		}
		if(this.iEvalue == null) {
			lStat.setNull(6, 3);
		} else {
			lStat.setObject(6, this.iEvalue);
		}
		if(this.iPvalue == null) {
			lStat.setNull(7, 3);
		} else {
			lStat.setObject(7, this.iPvalue);
		}
		if(this.iCharge == Long.MIN_VALUE) {
			lStat.setNull(8, 4);
		} else {
			lStat.setLong(8, this.iCharge);
		}
		if(this.iMass == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, this.iMass);
		}
		if(this.iTheomass == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, this.iTheomass);
		}
		if(this.iStart == null) {
			lStat.setNull(11, 12);
		} else {
			lStat.setObject(11, this.iStart);
		}
		if(this.iEnd == null) {
			lStat.setNull(12, 12);
		} else {
			lStat.setObject(12, this.iEnd);
		}
		if(this.iQvalue == null) {
			lStat.setNull(13, 3);
		} else {
			lStat.setObject(13, this.iQvalue);
		}
		if(this.iPep == null) {
			lStat.setNull(14, 3);
		} else {
			lStat.setObject(14, this.iPep);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
        this.iKeys = new Object[colCount];
		while(lrsKeys.next()) {
			for(int i = 0; i< this.iKeys.length; i++) {
                this.iKeys[i] = lrsKeys.getObject(i+1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if(this.iKeys != null && this.iKeys.length == 1 && this.iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
            this.iOmssahitid = ((Number) this.iKeys[0]).longValue();
		}
        iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if 
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return	Object[]	with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return iKeys;
	}

}