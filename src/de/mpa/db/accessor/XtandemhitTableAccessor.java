/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:45
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
 * This class is a generated accessor for the Xtandemhit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
@SuppressWarnings("serial")
public class XtandemhitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'xtandemhitid' column.
	 */
	protected long iXtandemhitid = Long.MIN_VALUE;


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
	 * This variable represents the contents for the 'domainid' column.
	 */
	protected String iDomainid = null;


	/**
	 * This variable represents the contents for the 'start' column.
	 */
	protected long iStart = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'end' column.
	 */
	protected long iEnd = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'evalue' column.
	 */
	protected Number iEvalue = null;


	/**
	 * This variable represents the contents for the 'delta' column.
	 */
	protected Number iDelta = null;


	/**
	 * This variable represents the contents for the 'hyperscore' column.
	 */
	protected Number iHyperscore = null;


	/**
	 * This variable represents the contents for the 'pre' column.
	 */
	protected String iPre = null;


	/**
	 * This variable represents the contents for the 'post' column.
	 */
	protected String iPost = null;


	/**
	 * This variable represents the contents for the 'misscleavages' column.
	 */
	protected long iMisscleavages = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'qvalue' column.
	 */
	protected Number iQvalue = null;


	/**
	 * This variable represents the contents for the 'pep' column.
	 */
	protected Number iPep = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'xtandemhitid' column.
	 */
	public static final String XTANDEMHITID = "XTANDEMHITID";

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
	 * This variable represents the key for the 'domainid' column.
	 */
	public static final String DOMAINID = "DOMAINID";

	/**
	 * This variable represents the key for the 'start' column.
	 */
	public static final String START = "START";

	/**
	 * This variable represents the key for the 'end' column.
	 */
	public static final String END = "END";

	/**
	 * This variable represents the key for the 'evalue' column.
	 */
	public static final String EVALUE = "EVALUE";

	/**
	 * This variable represents the key for the 'delta' column.
	 */
	public static final String DELTA = "DELTA";

	/**
	 * This variable represents the key for the 'hyperscore' column.
	 */
	public static final String HYPERSCORE = "HYPERSCORE";

	/**
	 * This variable represents the key for the 'pre' column.
	 */
	public static final String PRE = "PRE";

	/**
	 * This variable represents the key for the 'post' column.
	 */
	public static final String POST = "POST";

	/**
	 * This variable represents the key for the 'misscleavages' column.
	 */
	public static final String MISSCLEAVAGES = "MISSCLEAVAGES";

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
	public XtandemhitTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'XtandemhitTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public XtandemhitTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(XTANDEMHITID)) {
			this.iXtandemhitid = ((Long)aParams.get(XTANDEMHITID)).longValue();
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
		if(aParams.containsKey(DOMAINID)) {
			this.iDomainid = (String)aParams.get(DOMAINID);
		}
		if(aParams.containsKey(START)) {
			this.iStart = ((Long)aParams.get(START)).longValue();
		}
		if(aParams.containsKey(END)) {
			this.iEnd = ((Long)aParams.get(END)).longValue();
		}
		if(aParams.containsKey(EVALUE)) {
			this.iEvalue = (Number)aParams.get(EVALUE);
		}
		if(aParams.containsKey(DELTA)) {
			this.iDelta = (Number)aParams.get(DELTA);
		}
		if(aParams.containsKey(HYPERSCORE)) {
			this.iHyperscore = (Number)aParams.get(HYPERSCORE);
		}
		if(aParams.containsKey(PRE)) {
			this.iPre = (String)aParams.get(PRE);
		}
		if(aParams.containsKey(POST)) {
			this.iPost = (String)aParams.get(POST);
		}
		if(aParams.containsKey(MISSCLEAVAGES)) {
			this.iMisscleavages = ((Long)aParams.get(MISSCLEAVAGES)).longValue();
		}
		if(aParams.containsKey(QVALUE)) {
			this.iQvalue = (Number)aParams.get(QVALUE);
		}
		if(aParams.containsKey(PEP)) {
			this.iPep = (Number)aParams.get(PEP);
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
	 * This constructor allows the creation of the 'XtandemhitTableAccessor' object based on a resultset
	 * obtained by a 'select * from Xtandemhit' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public XtandemhitTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iXtandemhitid = aResultSet.getLong("xtandemhitid");
		this.iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iFk_proteinid = aResultSet.getLong("fk_proteinid");
		this.iDomainid = (String)aResultSet.getObject("domainid");
		this.iStart = aResultSet.getLong("start");
		this.iEnd = aResultSet.getLong("end");
		this.iEvalue = (Number)aResultSet.getObject("evalue");
		this.iDelta = (Number)aResultSet.getObject("delta");
		this.iHyperscore = (Number)aResultSet.getObject("hyperscore");
		this.iPre = (String)aResultSet.getObject("pre");
		this.iPost = (String)aResultSet.getObject("post");
		this.iMisscleavages = aResultSet.getLong("misscleavages");
		this.iQvalue = (Number)aResultSet.getObject("qvalue");
		this.iPep = (Number)aResultSet.getObject("pep");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Xtandemhitid' column
	 * 
	 * @return	long	with the value for the Xtandemhitid column.
	 */
	public long getXtandemhitid() {
		return this.iXtandemhitid;
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
	 * This method returns the value for the 'Domainid' column
	 * 
	 * @return	String	with the value for the Domainid column.
	 */
	public String getDomainid() {
		return this.iDomainid;
	}

	/**
	 * This method returns the value for the 'Start' column
	 * 
	 * @return	long	with the value for the Start column.
	 */
	public long getStart() {
		return this.iStart;
	}

	/**
	 * This method returns the value for the 'End' column
	 * 
	 * @return	long	with the value for the End column.
	 */
	public long getEnd() {
		return this.iEnd;
	}

	/**
	 * This method returns the value for the 'Evalue' column
	 * 
	 * @return	Number	with the value for the Evalue column.
	 */
	public Number getEvalue() {
		return this.iEvalue;
	}

	/**
	 * This method returns the value for the 'Delta' column
	 * 
	 * @return	Number	with the value for the Delta column.
	 */
	public Number getDelta() {
		return this.iDelta;
	}

	/**
	 * This method returns the value for the 'Hyperscore' column
	 * 
	 * @return	Number	with the value for the Hyperscore column.
	 */
	public Number getHyperscore() {
		return this.iHyperscore;
	}

	/**
	 * This method returns the value for the 'Pre' column
	 * 
	 * @return	String	with the value for the Pre column.
	 */
	public String getPre() {
		return this.iPre;
	}

	/**
	 * This method returns the value for the 'Post' column
	 * 
	 * @return	String	with the value for the Post column.
	 */
	public String getPost() {
		return this.iPost;
	}

	/**
	 * This method returns the value for the 'Misscleavages' column
	 * 
	 * @return	long	with the value for the Misscleavages column.
	 */
	public long getMisscleavages() {
		return this.iMisscleavages;
	}

	/**
	 * This method returns the value for the 'Qvalue' column
	 * 
	 * @return	Number	with the value for the Qvalue column.
	 */
	public Number getQvalue() {
		return this.iQvalue;
	}

	/**
	 * This method returns the value for the 'Pep' column
	 * 
	 * @return	Number	with the value for the Pep column.
	 */
	public Number getPep() {
		return this.iPep;
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
	 * This method sets the value for the 'Xtandemhitid' column
	 * 
	 * @param	aXtandemhitid	long with the value for the Xtandemhitid column.
	 */
	public void setXtandemhitid(long aXtandemhitid) {
		this.iXtandemhitid = aXtandemhitid;
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
	 * This method sets the value for the 'Domainid' column
	 * 
	 * @param	aDomainid	String with the value for the Domainid column.
	 */
	public void setDomainid(String aDomainid) {
		this.iDomainid = aDomainid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Start' column
	 * 
	 * @param	aStart	long with the value for the Start column.
	 */
	public void setStart(long aStart) {
		this.iStart = aStart;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'End' column
	 * 
	 * @param	aEnd	long with the value for the End column.
	 */
	public void setEnd(long aEnd) {
		this.iEnd = aEnd;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Evalue' column
	 * 
	 * @param	aEvalue	Number with the value for the Evalue column.
	 */
	public void setEvalue(Number aEvalue) {
		this.iEvalue = aEvalue;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Delta' column
	 * 
	 * @param	aDelta	Number with the value for the Delta column.
	 */
	public void setDelta(Number aDelta) {
		this.iDelta = aDelta;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Hyperscore' column
	 * 
	 * @param	aHyperscore	Number with the value for the Hyperscore column.
	 */
	public void setHyperscore(Number aHyperscore) {
		this.iHyperscore = aHyperscore;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pre' column
	 * 
	 * @param	aPre	String with the value for the Pre column.
	 */
	public void setPre(String aPre) {
		this.iPre = aPre;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Post' column
	 * 
	 * @param	aPost	String with the value for the Post column.
	 */
	public void setPost(String aPost) {
		this.iPost = aPost;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Misscleavages' column
	 * 
	 * @param	aMisscleavages	long with the value for the Misscleavages column.
	 */
	public void setMisscleavages(long aMisscleavages) {
		this.iMisscleavages = aMisscleavages;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Qvalue' column
	 * 
	 * @param	aQvalue	Number with the value for the Qvalue column.
	 */
	public void setQvalue(Number aQvalue) {
		this.iQvalue = aQvalue;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pep' column
	 * 
	 * @param	aPep	Number with the value for the Pep column.
	 */
	public void setPep(Number aPep) {
		this.iPep = aPep;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM xtandemhit WHERE xtandemhitid = ?");
		lStat.setLong(1, iXtandemhitid);
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
		if(!aKeys.containsKey(XTANDEMHITID)) {
			throw new IllegalArgumentException("Primary key field 'XTANDEMHITID' is missing in HashMap!");
		} else {
			iXtandemhitid = ((Long)aKeys.get(XTANDEMHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM xtandemhit WHERE xtandemhitid = ?");
		lStat.setLong(1, iXtandemhitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iXtandemhitid = lRS.getLong("xtandemhitid");
			iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iFk_proteinid = lRS.getLong("fk_proteinid");
			iDomainid = (String)lRS.getObject("domainid");
			iStart = lRS.getLong("start");
			iEnd = lRS.getLong("end");
			iEvalue = (Number)lRS.getObject("evalue");
			iDelta = (Number)lRS.getObject("delta");
			iHyperscore = (Number)lRS.getObject("hyperscore");
			iPre = (String)lRS.getObject("pre");
			iPost = (String)lRS.getObject("post");
			iMisscleavages = lRS.getLong("misscleavages");
			iQvalue = (Number)lRS.getObject("qvalue");
			iPep = (Number)lRS.getObject("pep");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'xtandemhit' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'xtandemhit' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from xtandemhit";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<XtandemhitTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<XtandemhitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<XtandemhitTableAccessor>  entities = new ArrayList<XtandemhitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new XtandemhitTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE xtandemhit SET xtandemhitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, domainid = ?, start = ?, end = ?, evalue = ?, delta = ?, hyperscore = ?, pre = ?, post = ?, misscleavages = ?, qvalue = ?, pep = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE xtandemhitid = ?");
		lStat.setLong(1, iXtandemhitid);
		lStat.setLong(2, iFk_searchspectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setLong(4, iFk_proteinid);
		lStat.setObject(5, iDomainid);
		lStat.setLong(6, iStart);
		lStat.setLong(7, iEnd);
		lStat.setObject(8, iEvalue);
		lStat.setObject(9, iDelta);
		lStat.setObject(10, iHyperscore);
		lStat.setObject(11, iPre);
		lStat.setObject(12, iPost);
		lStat.setLong(13, iMisscleavages);
		lStat.setObject(14, iQvalue);
		lStat.setObject(15, iPep);
		lStat.setObject(16, iCreationdate);
		lStat.setLong(17, iXtandemhitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO xtandemhit (xtandemhitid, fk_searchspectrumid, fk_peptideid, fk_proteinid, domainid, start, end, evalue, delta, hyperscore, pre, post, misscleavages, qvalue, pep, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		if(iXtandemhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iXtandemhitid);
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
		if(iDomainid == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iDomainid);
		}
		if(iStart == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, iStart);
		}
		if(iEnd == Long.MIN_VALUE) {
			lStat.setNull(7, 4);
		} else {
			lStat.setLong(7, iEnd);
		}
		if(iEvalue == null) {
			lStat.setNull(8, 3);
		} else {
			lStat.setObject(8, iEvalue);
		}
		if(iDelta == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iDelta);
		}
		if(iHyperscore == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, iHyperscore);
		}
		if(iPre == null) {
			lStat.setNull(11, 12);
		} else {
			lStat.setObject(11, iPre);
		}
		if(iPost == null) {
			lStat.setNull(12, 12);
		} else {
			lStat.setObject(12, iPost);
		}
		if(iMisscleavages == Long.MIN_VALUE) {
			lStat.setNull(13, 4);
		} else {
			lStat.setLong(13, iMisscleavages);
		}
		if(iQvalue == null) {
			lStat.setNull(14, 3);
		} else {
			lStat.setObject(14, iQvalue);
		}
		if(iPep == null) {
			lStat.setNull(15, 3);
		} else {
			lStat.setObject(15, iPep);
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
			iXtandemhitid = ((Number) iKeys[0]).longValue();
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