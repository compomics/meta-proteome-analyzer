/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:25:45
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
 * This class is a generated accessor for the Xtandemhit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
@SuppressWarnings("serial")
public class XtandemhitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable, Serializable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'xtandemhitid' column.
	 */
	protected long iXtandemhitid = Long.MIN_VALUE;

	/**
	 * This variable represents the contents for the 'fk_searchspectrumid'
	 * column.
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
	protected String iDomainid;

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
	protected Number iEvalue;

	/**
	 * This variable represents the contents for the 'delta' column.
	 */
	protected Number iDelta;

	/**
	 * This variable represents the contents for the 'hyperscore' column.
	 */
	protected Number iHyperscore;

	/**
	 * This variable represents the contents for the 'pre' column.
	 */
	protected String iPre;

	/**
	 * This variable represents the contents for the 'post' column.
	 */
	protected String iPost;

	/**
	 * This variable represents the contents for the 'misscleavages' column.
	 */
	protected long iMisscleavages = Long.MIN_VALUE;

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
	protected Timestamp iModificationdate;

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
	 * This constructor allows the creation of the 'XtandemhitTableAccessor'
	 * object based on a set of values in the HashMap.
	 *
	 * @param aParams
	 *            HashMap with the parameters to initialize this object with.
	 *            <i>Please use only constants defined on this class as keys in
	 *            the HashMap!</i>
	 */
	public XtandemhitTableAccessor(HashMap aParams) {
		if (aParams.containsKey(XtandemhitTableAccessor.XTANDEMHITID)) {
			iXtandemhitid = ((Long) aParams.get(XtandemhitTableAccessor.XTANDEMHITID)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.FK_SEARCHSPECTRUMID)) {
			iFk_searchspectrumid = ((Long) aParams.get(XtandemhitTableAccessor.FK_SEARCHSPECTRUMID)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.FK_PEPTIDEID)) {
			iFk_peptideid = ((Long) aParams.get(XtandemhitTableAccessor.FK_PEPTIDEID)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.FK_PROTEINID)) {
			iFk_proteinid = ((Long) aParams.get(XtandemhitTableAccessor.FK_PROTEINID)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.DOMAINID)) {
			iDomainid = (String) aParams.get(XtandemhitTableAccessor.DOMAINID);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.START)) {
			iStart = ((Long) aParams.get(XtandemhitTableAccessor.START)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.END)) {
			iEnd = ((Long) aParams.get(XtandemhitTableAccessor.END)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.EVALUE)) {
			iEvalue = (Number) aParams.get(XtandemhitTableAccessor.EVALUE);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.DELTA)) {
			iDelta = (Number) aParams.get(XtandemhitTableAccessor.DELTA);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.HYPERSCORE)) {
			iHyperscore = (Number) aParams.get(XtandemhitTableAccessor.HYPERSCORE);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.PRE)) {
			iPre = (String) aParams.get(XtandemhitTableAccessor.PRE);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.POST)) {
			iPost = (String) aParams.get(XtandemhitTableAccessor.POST);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.MISSCLEAVAGES)) {
			iMisscleavages = ((Long) aParams.get(XtandemhitTableAccessor.MISSCLEAVAGES)).longValue();
		}
		if (aParams.containsKey(XtandemhitTableAccessor.QVALUE)) {
			iQvalue = (Number) aParams.get(XtandemhitTableAccessor.QVALUE);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.PEP)) {
			iPep = (Number) aParams.get(XtandemhitTableAccessor.PEP);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.CREATIONDATE)) {
			iCreationdate = (Timestamp) aParams.get(XtandemhitTableAccessor.CREATIONDATE);
		}
		if (aParams.containsKey(XtandemhitTableAccessor.MODIFICATIONDATE)) {
			iModificationdate = (Timestamp) aParams.get(XtandemhitTableAccessor.MODIFICATIONDATE);
		}
		iUpdated = true;
	}

	/**
	 * This constructor allows the creation of the 'XtandemhitTableAccessor'
	 * object based on a resultset obtained by a 'select * from Xtandemhit'
	 * query.
	 *
	 * @param aResultSet
	 *            ResultSet with the required columns to initialize this object
	 *            with.
	 * @exception SQLException
	 *                when the ResultSet could not be read.
	 */
	public XtandemhitTableAccessor(ResultSet aResultSet) throws SQLException {
		iXtandemhitid = aResultSet.getLong("xtandemhitid");
		iFk_searchspectrumid = aResultSet.getLong("fk_searchspectrumid");
		iFk_peptideid = aResultSet.getLong("fk_peptideid");
		iFk_proteinid = aResultSet.getLong("fk_proteinid");
		iDomainid = (String) aResultSet.getObject("domainid");
		iStart = aResultSet.getLong("start");
		iEnd = aResultSet.getLong("end");
		iEvalue = (Number) aResultSet.getObject("evalue");
		iDelta = (Number) aResultSet.getObject("delta");
		iHyperscore = (Number) aResultSet.getObject("hyperscore");
		iPre = (String) aResultSet.getObject("pre");
		iPost = (String) aResultSet.getObject("post");
		iMisscleavages = aResultSet.getLong("misscleavages");
		iQvalue = (Number) aResultSet.getObject("qvalue");
		iPep = (Number) aResultSet.getObject("pep");
		iCreationdate = (Timestamp) aResultSet.getObject("creationdate");
		iModificationdate = (Timestamp) aResultSet.getObject("modificationdate");

		iUpdated = true;
	}

	/**
	 * This method returns the value for the 'Xtandemhitid' column
	 * 
	 * @return long with the value for the Xtandemhitid column.
	 */
	public long getXtandemhitid() {
		return iXtandemhitid;
	}

	/**
	 * This method returns the value for the 'Fk_searchspectrumid' column
	 * 
	 * @return long with the value for the Fk_searchspectrumid column.
	 */
	public long getFk_searchspectrumid() {
		return iFk_searchspectrumid;
	}

	/**
	 * This method returns the value for the 'Fk_peptideid' column
	 * 
	 * @return long with the value for the Fk_peptideid column.
	 */
	public long getFk_peptideid() {
		return iFk_peptideid;
	}

	/**
	 * This method returns the value for the 'Fk_proteinid' column
	 * 
	 * @return long with the value for the Fk_proteinid column.
	 */
	public long getFk_proteinid() {
		return iFk_proteinid;
	}

	/**
	 * This method returns the value for the 'Domainid' column
	 * 
	 * @return String with the value for the Domainid column.
	 */
	public String getDomainid() {
		return iDomainid;
	}

	/**
	 * This method returns the value for the 'Start' column
	 * 
	 * @return long with the value for the Start column.
	 */
	public long getStart() {
		return iStart;
	}

	/**
	 * This method returns the value for the 'End' column
	 * 
	 * @return long with the value for the End column.
	 */
	public long getEnd() {
		return iEnd;
	}

	/**
	 * This method returns the value for the 'Evalue' column
	 * 
	 * @return Number with the value for the Evalue column.
	 */
	public Number getEvalue() {
		return iEvalue;
	}

	/**
	 * This method returns the value for the 'Delta' column
	 * 
	 * @return Number with the value for the Delta column.
	 */
	public Number getDelta() {
		return iDelta;
	}

	/**
	 * This method returns the value for the 'Hyperscore' column
	 * 
	 * @return Number with the value for the Hyperscore column.
	 */
	public Number getHyperscore() {
		return iHyperscore;
	}

	/**
	 * This method returns the value for the 'Pre' column
	 * 
	 * @return String with the value for the Pre column.
	 */
	public String getPre() {
		return iPre;
	}

	/**
	 * This method returns the value for the 'Post' column
	 * 
	 * @return String with the value for the Post column.
	 */
	public String getPost() {
		return iPost;
	}

	/**
	 * This method returns the value for the 'Misscleavages' column
	 * 
	 * @return long with the value for the Misscleavages column.
	 */
	public long getMisscleavages() {
		return iMisscleavages;
	}

	/**
	 * This method returns the value for the 'Qvalue' column
	 * 
	 * @return Number with the value for the Qvalue column.
	 */
	public Number getQvalue() {
		return iQvalue;
	}

	/**
	 * This method returns the value for the 'Pep' column
	 * 
	 * @return Number with the value for the Pep column.
	 */
	public Number getPep() {
		return iPep;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return java.sql.Timestamp with the value for the Creationdate column.
	 */
	public Timestamp getCreationdate() {
		return iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return java.sql.Timestamp with the value for the Modificationdate
	 *         column.
	 */
	public Timestamp getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Xtandemhitid' column
	 * 
	 * @param aXtandemhitid
	 *            long with the value for the Xtandemhitid column.
	 */
	public void setXtandemhitid(long aXtandemhitid) {
		iXtandemhitid = aXtandemhitid;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_searchspectrumid' column
	 * 
	 * @param aFk_searchspectrumid
	 *            long with the value for the Fk_searchspectrumid column.
	 */
	public void setFk_searchspectrumid(long aFk_searchspectrumid) {
		iFk_searchspectrumid = aFk_searchspectrumid;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_peptideid' column
	 * 
	 * @param aFk_peptideid
	 *            long with the value for the Fk_peptideid column.
	 */
	public void setFk_peptideid(long aFk_peptideid) {
		iFk_peptideid = aFk_peptideid;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_proteinid' column
	 * 
	 * @param aFk_proteinid
	 *            long with the value for the Fk_proteinid column.
	 */
	public void setFk_proteinid(long aFk_proteinid) {
		iFk_proteinid = aFk_proteinid;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Domainid' column
	 * 
	 * @param aDomainid
	 *            String with the value for the Domainid column.
	 */
	public void setDomainid(String aDomainid) {
		iDomainid = aDomainid;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Start' column
	 * 
	 * @param aStart
	 *            long with the value for the Start column.
	 */
	public void setStart(long aStart) {
		iStart = aStart;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'End' column
	 * 
	 * @param aEnd
	 *            long with the value for the End column.
	 */
	public void setEnd(long aEnd) {
		iEnd = aEnd;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Evalue' column
	 * 
	 * @param aEvalue
	 *            Number with the value for the Evalue column.
	 */
	public void setEvalue(Number aEvalue) {
		iEvalue = aEvalue;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Delta' column
	 * 
	 * @param aDelta
	 *            Number with the value for the Delta column.
	 */
	public void setDelta(Number aDelta) {
		iDelta = aDelta;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Hyperscore' column
	 * 
	 * @param aHyperscore
	 *            Number with the value for the Hyperscore column.
	 */
	public void setHyperscore(Number aHyperscore) {
		iHyperscore = aHyperscore;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pre' column
	 * 
	 * @param aPre
	 *            String with the value for the Pre column.
	 */
	public void setPre(String aPre) {
		iPre = aPre;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Post' column
	 * 
	 * @param aPost
	 *            String with the value for the Post column.
	 */
	public void setPost(String aPost) {
		iPost = aPost;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Misscleavages' column
	 * 
	 * @param aMisscleavages
	 *            long with the value for the Misscleavages column.
	 */
	public void setMisscleavages(long aMisscleavages) {
		iMisscleavages = aMisscleavages;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Qvalue' column
	 * 
	 * @param aQvalue
	 *            Number with the value for the Qvalue column.
	 */
	public void setQvalue(Number aQvalue) {
		iQvalue = aQvalue;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Pep' column
	 * 
	 * @param aPep
	 *            Number with the value for the Pep column.
	 */
	public void setPep(Number aPep) {
		iPep = aPep;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param aCreationdate
	 *            java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(Timestamp aCreationdate) {
		iCreationdate = aCreationdate;
		iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param aModificationdate
	 *            java.sql.Timestamp with the value for the Modificationdate
	 *            column.
	 */
	public void setModificationdate(Timestamp aModificationdate) {
		iModificationdate = aModificationdate;
		iUpdated = true;
	}

	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param aConn
	 *            Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM xtandemhit WHERE xtandemhitid = ?");
		lStat.setLong(1, this.iXtandemhitid);
		int result = lStat.executeUpdate();
		lStat.close();
		return result;
	}

	/**
	 * This method allows the caller to read data for this object from a
	 * persistent store based on the specified keys.
	 *
	 * @param aConn
	 *            Connection to the persitent store.
	 */
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if (!aKeys.containsKey(XtandemhitTableAccessor.XTANDEMHITID)) {
			throw new IllegalArgumentException("Primary key field 'XTANDEMHITID' is missing in HashMap!");
		} else {
			this.iXtandemhitid = ((Long) aKeys.get(XtandemhitTableAccessor.XTANDEMHITID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So
		// let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM xtandemhit WHERE xtandemhitid = ?");
		lStat.setLong(1, this.iXtandemhitid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while (lRS.next()) {
			hits++;
			this.iXtandemhitid = lRS.getLong("xtandemhitid");
			this.iFk_searchspectrumid = lRS.getLong("fk_searchspectrumid");
			this.iFk_peptideid = lRS.getLong("fk_peptideid");
			this.iFk_proteinid = lRS.getLong("fk_proteinid");
			this.iDomainid = (String) lRS.getObject("domainid");
			this.iStart = lRS.getLong("start");
			this.iEnd = lRS.getLong("end");
			this.iEvalue = (Number) lRS.getObject("evalue");
			this.iDelta = (Number) lRS.getObject("delta");
			this.iHyperscore = (Number) lRS.getObject("hyperscore");
			this.iPre = (String) lRS.getObject("pre");
			this.iPost = (String) lRS.getObject("post");
			this.iMisscleavages = lRS.getLong("misscleavages");
			this.iQvalue = (Number) lRS.getObject("qvalue");
			this.iPep = (Number) lRS.getObject("pep");
			this.iCreationdate = (Timestamp) lRS.getObject("creationdate");
			this.iModificationdate = (Timestamp) lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if (hits > 1) {
			throw new SQLException(
					"More than one hit found for the specified primary keys in the 'xtandemhit' table! Object is initialized to last row returned.");
		} else if (hits == 0) {
			throw new SQLException(
					"No hits found for the specified primary keys in the 'xtandemhit' table! Object is not initialized correctly!");
		}
	}

	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return String with the basic select statement for this table.
	 */
	public static String getBasicSelect() {
		return "select * from xtandemhit";
	}

	/**
	 * This method allows the caller to obtain all rows for this table from a
	 * persistent store.
	 *
	 * @param aConn
	 *            Connection to the persitent store.
	 * @return ArrayList<XtandemhitTableAccessor> with all entries for this
	 *         table.
	 */
	public static ArrayList<XtandemhitTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<XtandemhitTableAccessor> entities = new ArrayList<XtandemhitTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(XtandemhitTableAccessor.getBasicSelect());
		while (rs.next()) {
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
	 * @param aConn
	 *            Connection to the persitent store.
	 */
	public int update(Connection aConn) throws SQLException {
		if (!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement(
				"UPDATE xtandemhit SET xtandemhitid = ?, fk_searchspectrumid = ?, fk_peptideid = ?, fk_proteinid = ?, domainid = ?, start = ?, end = ?, evalue = ?, delta = ?, hyperscore = ?, pre = ?, post = ?, misscleavages = ?, qvalue = ?, pep = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE xtandemhitid = ?");
		lStat.setLong(1, this.iXtandemhitid);
		lStat.setLong(2, this.iFk_searchspectrumid);
		lStat.setLong(3, this.iFk_peptideid);
		lStat.setLong(4, this.iFk_proteinid);
		lStat.setObject(5, this.iDomainid);
		lStat.setLong(6, this.iStart);
		lStat.setLong(7, this.iEnd);
		lStat.setObject(8, this.iEvalue);
		lStat.setObject(9, this.iDelta);
		lStat.setObject(10, this.iHyperscore);
		lStat.setObject(11, this.iPre);
		lStat.setObject(12, this.iPost);
		lStat.setLong(13, this.iMisscleavages);
		lStat.setObject(14, this.iQvalue);
		lStat.setObject(15, this.iPep);
		lStat.setObject(16, this.iCreationdate);
		lStat.setLong(17, this.iXtandemhitid);
		int result = lStat.executeUpdate();
		lStat.close();
		iUpdated = false;
		return result;
	}

	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param aConn
	 *            Connection to the persitent store.
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement(
				"INSERT INTO xtandemhit (xtandemhitid, fk_searchspectrumid, fk_peptideid, fk_proteinid, domainid, start, end, evalue, delta, hyperscore, pre, post, misscleavages, qvalue, pep, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				Statement.RETURN_GENERATED_KEYS);
		if (this.iXtandemhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iXtandemhitid);
		}
		if (this.iFk_searchspectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, this.iFk_searchspectrumid);
		}
		if (this.iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, this.iFk_peptideid);
		}
		if (this.iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, this.iFk_proteinid);
		}
		if (this.iDomainid == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, this.iDomainid);
		}
		if (this.iStart == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, this.iStart);
		}
		if (this.iEnd == Long.MIN_VALUE) {
			lStat.setNull(7, 4);
		} else {
			lStat.setLong(7, this.iEnd);
		}
		if (this.iEvalue == null) {
			lStat.setNull(8, 3);
		} else {
			if (this.iEvalue.doubleValue() <= 1.0d) {
				lStat.setObject(8, this.iEvalue);
			} else {
				lStat.setObject(8, 1.0);
			}
		}
		if (this.iDelta == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, this.iDelta);
		}
		if (this.iHyperscore == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, this.iHyperscore);
		}
		if (this.iPre == null) {
			lStat.setNull(11, 12);
		} else {
			lStat.setObject(11, this.iPre);
		}
		if (this.iPost == null) {
			lStat.setNull(12, 12);
		} else {
			lStat.setObject(12, this.iPost);
		}
		if (this.iMisscleavages == Long.MIN_VALUE) {
			lStat.setNull(13, 4);
		} else {
			lStat.setLong(13, this.iMisscleavages);
		}
		if (this.iQvalue == null) {
			lStat.setNull(14, 3);
		} else {
			lStat.setObject(14, this.iQvalue);
		}
		if (this.iPep == null) {
			lStat.setNull(15, 3);
		} else {
			lStat.setObject(15, this.iPep);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
		this.iKeys = new Object[colCount];
		while (lrsKeys.next()) {
			for (int i = 0; i < this.iKeys.length; i++) {
				this.iKeys[i] = lrsKeys.getObject(i + 1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if (this.iKeys != null && this.iKeys.length == 1 && this.iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
			this.iXtandemhitid = ((Number) this.iKeys[0]).longValue();
		}
		iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return Object[] with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return iKeys;
	}

}