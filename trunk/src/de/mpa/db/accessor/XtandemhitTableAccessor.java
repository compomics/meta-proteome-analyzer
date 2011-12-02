/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/12/2011
 * Time: 15:03:10
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
 * This class is a generated accessor for the Xtandemhit table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class XtandemhitTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

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
	 * This variable represents the contents for the 'fk_spectrumid' column.
	 */
	protected long iFk_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_peptideid' column.
	 */
	protected long iFk_peptideid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'domainid' column.
	 */
	protected String iDomainid = null;


	/**
	 * This variable represents the contents for the 'protein' column.
	 */
	protected String iProtein = null;


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
	 * This variable represents the contents for the 'precursor' column.
	 */
	protected Number iPrecursor = null;


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
	 * This variable represents the key for the 'xtandemhitid' column.
	 */
	public static final String XTANDEMHITID = "XTANDEMHITID";

	/**
	 * This variable represents the key for the 'fk_spectrumid' column.
	 */
	public static final String FK_SPECTRUMID = "FK_SPECTRUMID";

	/**
	 * This variable represents the key for the 'fk_peptideid' column.
	 */
	public static final String FK_PEPTIDEID = "FK_PEPTIDEID";

	/**
	 * This variable represents the key for the 'domainid' column.
	 */
	public static final String DOMAINID = "DOMAINID";

	/**
	 * This variable represents the key for the 'protein' column.
	 */
	public static final String PROTEIN = "PROTEIN";

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
	 * This variable represents the key for the 'precursor' column.
	 */
	public static final String PRECURSOR = "PRECURSOR";

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
	public XtandemhitTableAccessor(HashMap aParams) {
		if(aParams.containsKey(XTANDEMHITID)) {
			this.iXtandemhitid = ((Long)aParams.get(XTANDEMHITID)).longValue();
		}
		if(aParams.containsKey(FK_SPECTRUMID)) {
			this.iFk_spectrumid = ((Long)aParams.get(FK_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(FK_PEPTIDEID)) {
			this.iFk_peptideid = ((Long)aParams.get(FK_PEPTIDEID)).longValue();
		}
		if(aParams.containsKey(DOMAINID)) {
			this.iDomainid = (String)aParams.get(DOMAINID);
		}
		if(aParams.containsKey(PROTEIN)) {
			this.iProtein = (String)aParams.get(PROTEIN);
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
		if(aParams.containsKey(PRECURSOR)) {
			this.iPrecursor = (Number)aParams.get(PRECURSOR);
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
		this.iFk_spectrumid = aResultSet.getLong("fk_spectrumid");
		this.iFk_peptideid = aResultSet.getLong("fk_peptideid");
		this.iDomainid = (String)aResultSet.getObject("domainid");
		this.iProtein = (String)aResultSet.getObject("protein");
		this.iStart = aResultSet.getLong("start");
		this.iEnd = aResultSet.getLong("end");
		this.iEvalue = (Number)aResultSet.getObject("evalue");
		this.iPrecursor = (Number)aResultSet.getObject("precursor");
		this.iDelta = (Number)aResultSet.getObject("delta");
		this.iHyperscore = (Number)aResultSet.getObject("hyperscore");
		this.iPre = (String)aResultSet.getObject("pre");
		this.iPost = (String)aResultSet.getObject("post");
		this.iMisscleavages = aResultSet.getLong("misscleavages");
		this.iQvalue = (Number)aResultSet.getObject("qvalue");

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
	 * This method returns the value for the 'Fk_spectrumid' column
	 * 
	 * @return	long	with the value for the Fk_spectrumid column.
	 */
	public long getFk_spectrumid() {
		return this.iFk_spectrumid;
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
	 * This method returns the value for the 'Domainid' column
	 * 
	 * @return	String	with the value for the Domainid column.
	 */
	public String getDomainid() {
		return this.iDomainid;
	}

	/**
	 * This method returns the value for the 'Protein' column
	 * 
	 * @return	String	with the value for the Protein column.
	 */
	public String getProtein() {
		return this.iProtein;
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
	 * This method returns the value for the 'Precursor' column
	 * 
	 * @return	Number	with the value for the Precursor column.
	 */
	public Number getPrecursor() {
		return this.iPrecursor;
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
	 * This method sets the value for the 'Xtandemhitid' column
	 * 
	 * @param	aXtandemhitid	long with the value for the Xtandemhitid column.
	 */
	public void setXtandemhitid(long aXtandemhitid) {
		this.iXtandemhitid = aXtandemhitid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Fk_spectrumid' column
	 * 
	 * @param	aFk_spectrumid	long with the value for the Fk_spectrumid column.
	 */
	public void setFk_spectrumid(long aFk_spectrumid) {
		this.iFk_spectrumid = aFk_spectrumid;
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
	 * This method sets the value for the 'Domainid' column
	 * 
	 * @param	aDomainid	String with the value for the Domainid column.
	 */
	public void setDomainid(String aDomainid) {
		this.iDomainid = aDomainid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Protein' column
	 * 
	 * @param	aProtein	String with the value for the Protein column.
	 */
	public void setProtein(String aProtein) {
		this.iProtein = aProtein;
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
	 * This method sets the value for the 'Precursor' column
	 * 
	 * @param	aPrecursor	Number with the value for the Precursor column.
	 */
	public void setPrecursor(Number aPrecursor) {
		this.iPrecursor = aPrecursor;
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
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
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
			iFk_spectrumid = lRS.getLong("fk_spectrumid");
			iFk_peptideid = lRS.getLong("fk_peptideid");
			iDomainid = (String)lRS.getObject("domainid");
			iProtein = (String)lRS.getObject("protein");
			iStart = lRS.getLong("start");
			iEnd = lRS.getLong("end");
			iEvalue = (Number)lRS.getObject("evalue");
			iPrecursor = (Number)lRS.getObject("precursor");
			iDelta = (Number)lRS.getObject("delta");
			iHyperscore = (Number)lRS.getObject("hyperscore");
			iPre = (String)lRS.getObject("pre");
			iPost = (String)lRS.getObject("post");
			iMisscleavages = lRS.getLong("misscleavages");
			iQvalue = (Number)lRS.getObject("qvalue");
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE xtandemhit SET xtandemhitid = ?, fk_spectrumid = ?, fk_peptideid = ?, domainid = ?, protein = ?, start = ?, end = ?, evalue = ?, precursor = ?, delta = ?, hyperscore = ?, pre = ?, post = ?, misscleavages = ?, qvalue = ? WHERE xtandemhitid = ?");
		lStat.setLong(1, iXtandemhitid);
		lStat.setLong(2, iFk_spectrumid);
		lStat.setLong(3, iFk_peptideid);
		lStat.setObject(4, iDomainid);
		lStat.setObject(5, iProtein);
		lStat.setLong(6, iStart);
		lStat.setLong(7, iEnd);
		lStat.setObject(8, iEvalue);
		lStat.setObject(9, iPrecursor);
		lStat.setObject(10, iDelta);
		lStat.setObject(11, iHyperscore);
		lStat.setObject(12, iPre);
		lStat.setObject(13, iPost);
		lStat.setLong(14, iMisscleavages);
		lStat.setObject(15, iQvalue);
		lStat.setLong(16, iXtandemhitid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO xtandemhit (xtandemhitid, fk_spectrumid, fk_peptideid, domainid, protein, start, end, evalue, precursor, delta, hyperscore, pre, post, misscleavages, qvalue) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		if(iXtandemhitid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iXtandemhitid);
		}
		if(iFk_spectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_spectrumid);
		}
		if(iFk_peptideid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iFk_peptideid);
		}
		if(iDomainid == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iDomainid);
		}
		if(iProtein == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iProtein);
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
		if(iPrecursor == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iPrecursor);
		}
		if(iDelta == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, iDelta);
		}
		if(iHyperscore == null) {
			lStat.setNull(11, 3);
		} else {
			lStat.setObject(11, iHyperscore);
		}
		if(iPre == null) {
			lStat.setNull(12, 12);
		} else {
			lStat.setObject(12, iPre);
		}
		if(iPost == null) {
			lStat.setNull(13, 12);
		} else {
			lStat.setObject(13, iPost);
		}
		if(iMisscleavages == Long.MIN_VALUE) {
			lStat.setNull(14, 4);
		} else {
			lStat.setLong(14, iMisscleavages);
		}
		if(iQvalue == null) {
			lStat.setNull(15, 3);
		} else {
			lStat.setObject(15, iQvalue);
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