/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 17/05/2013
 * Time: 12:48:55
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
 * This class is a generated accessor for the Uniprotentry table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class UniprotentryTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'uniprotentryid' column.
	 */
	protected long iUniprotentryid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'fk_proteinid' column.
	 */
	protected long iFk_proteinid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'taxid' column.
	 */
	protected long iTaxid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'ecnumber' column.
	 */
	protected String iEcnumber = null;


	/**
	 * This variable represents the contents for the 'konumber' column.
	 */
	protected String iKonumber = null;


	/**
	 * This variable represents the contents for the 'keywords' column.
	 */
	protected String iKeywords = null;
	
	/**
	 * This variable represents the contents for the 'uniref50' column.
	 */
	protected String iUniref50 = null;
	
	/**
	 * This variable represents the contents for the 'uniref90' column.
	 */
	protected String iUniref90 = null;
	
	/**
	 * This variable represents the contents for the 'uniref100' column.
	 */
	protected String iUniref100 = null;

	/**
	 * This variable represents the key for the 'uniprotentryid' column.
	 */
	public static final String UNIPROTENTRYID = "UNIPROTENTRYID";

	/**
	 * This variable represents the key for the 'fk_proteinid' column.
	 */
	public static final String FK_PROTEINID = "FK_PROTEINID";

	/**
	 * This variable represents the key for the 'taxid' column.
	 */
	public static final String TAXID = "TAXID";

	/**
	 * This variable represents the key for the 'ecnumber' column.
	 */
	public static final String ECNUMBER = "ECNUMBER";

	/**
	 * This variable represents the key for the 'konumber' column.
	 */
	public static final String KONUMBER = "KONUMBER";

	/**
	 * This variable represents the key for the 'keywords' column.
	 */
	public static final String KEYWORDS = "KEYWORDS";
	
	/**
	 * This variable represents the key for the 'uniref50' column.
	 */
	public static final String UNIREF50 = "UNIREF50";
	
	/**
	 * This variable represents the key for the 'uniref90' column.
	 */
	public static final String UNIREF90 = "UNIREF90";
	
	/**
	 * This variable represents the key for the 'uniref100' column.
	 */
	public static final String UNIREF100 = "UNIREF100";

	/**
	 * Default constructor.
	 */
	public UniprotentryTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'UniprotentryTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public UniprotentryTableAccessor(HashMap aParams) {
		if(aParams.containsKey(UNIPROTENTRYID)) {
			this.iUniprotentryid = ((Long)aParams.get(UNIPROTENTRYID)).longValue();
		}
		if(aParams.containsKey(FK_PROTEINID)) {
			this.iFk_proteinid = ((Long)aParams.get(FK_PROTEINID)).longValue();
		}
		if(aParams.containsKey(TAXID)) {
			this.iTaxid = ((Long)aParams.get(TAXID)).longValue();
		}
		if(aParams.containsKey(ECNUMBER)) {
			this.iEcnumber = (String)aParams.get(ECNUMBER);
		}
		if(aParams.containsKey(KONUMBER)) {
			this.iKonumber = (String)aParams.get(KONUMBER);
		}
		if(aParams.containsKey(KEYWORDS)) {
			this.iKeywords = (String)aParams.get(KEYWORDS);
		}
		if(aParams.containsKey(UNIREF50)) {
			this.iUniref50 = (String)aParams.get(UNIREF50);
		}
		if(aParams.containsKey(UNIREF90)) {
			this.iUniref90 = (String)aParams.get(UNIREF90);
		}
		if(aParams.containsKey(UNIREF100)) {
			this.iUniref100 = (String)aParams.get(UNIREF100);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'UniprotentryTableAccessor' object based on a resultset
	 * obtained by a 'select * from Uniprotentry' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public UniprotentryTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iUniprotentryid = aResultSet.getLong("uniprotentryid");
		this.iFk_proteinid = aResultSet.getLong("fk_proteinid");
		this.iTaxid = aResultSet.getLong("taxid");
		this.iEcnumber = (String)aResultSet.getObject("ecnumber");
		this.iKonumber = (String)aResultSet.getObject("konumber");
		this.iKeywords = (String)aResultSet.getObject("keywords");
		this.iUniref50 = (String)aResultSet.getObject("uniref50");
		this.iUniref90 = (String)aResultSet.getObject("uniref90");
		this.iUniref100 = (String)aResultSet.getObject("uniref100");
		this.iUpdated = true;
	}

	/**
	 * This method returns the value for the 'Uniprotentryid' column
	 * 
	 * @return	long	with the value for the Uniprotentryid column.
	 */
	public long getUniprotentryid() {
		return this.iUniprotentryid;
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
	 * This method returns the value for the 'Taxid' column
	 * 
	 * @return	long	with the value for the Taxid column.
	 */
	public long getTaxid() {
		return this.iTaxid;
	}

	/**
	 * This method returns the value for the 'Ecnumber' column
	 * 
	 * @return	String	with the value for the Ecnumber column.
	 */
	public String getEcnumber() {
		return this.iEcnumber;
	}

	/**
	 * This method returns the value for the 'Konumber' column
	 * 
	 * @return	String	with the value for the Konumber column.
	 */
	public String getKonumber() {
		return this.iKonumber;
	}

	/**
	 * This method returns the value for the 'Keywords' column
	 * 
	 * @return	String	with the value for the Keywords column.
	 */
	public String getKeywords() {
		return this.iKeywords;
	}
	
	/**
	 * This method returns the value for the 'Uniref100' column
	 * 
	 * @return	String	with the value for the Uniref100 column.
	 */
	public String getUniref100() {
		return iUniref100;
	}
	
	/**
	 * This method returns the value for the 'Uniref90' column
	 * 
	 * @return	String	with the value for the Uniref90 column.
	 */
	public String getUniref90() {
		return iUniref90;
	}

	/**
	 * This method returns the value for the 'Uniref50' column
	 * 
	 * @return	String	with the value for the Uniref50 column.
	 */
	public String getUniref50() {
		return iUniref50;
	}

	/**
	 * This method sets the value for the 'Uniprotentryid' column
	 * 
	 * @param	aUniprotentryid	long with the value for the Uniprotentryid column.
	 */
	public void setUniprotentryid(long aUniprotentryid) {
		this.iUniprotentryid = aUniprotentryid;
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
	 * This method sets the value for the 'Taxid' column
	 * 
	 * @param	aTaxid	long with the value for the Taxid column.
	 */
	public void setTaxid(long aTaxid) {
		this.iTaxid = aTaxid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Ecnumber' column
	 * 
	 * @param	aEcnumber	String with the value for the Ecnumber column.
	 */
	public void setEcnumber(String aEcnumber) {
		this.iEcnumber = aEcnumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Konumber' column
	 * 
	 * @param	aKonumber	String with the value for the Konumber column.
	 */
	public void setKonumber(String aKonumber) {
		this.iKonumber = aKonumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Keywords' column
	 * 
	 * @param	aKeywords	String with the value for the Keywords column.
	 */
	public void setKeywords(String aKeywords) {
		this.iKeywords = aKeywords;
		this.iUpdated = true;
	}
	
	/**
	 * This method sets the value for the 'Uniref50' column
	 * 
	 * @param	aUniref50	String with the value for the Uniref50 column.
	 */
	public void setUniref50(String aUniref50) {
		this.iUniref50 = aUniref50;
		this.iUpdated = true;
	}
	
	/**
	 * This method sets the value for the 'Uniref90' column
	 * 
	 * @param	aUniref90	String with the value for the Uniref90 column.
	 */
	public void setUniref90(String aUniref90) {
		this.iUniref90 = aUniref90;
		this.iUpdated = true;
	}
	
	public void setUniref100(String aUniref100) {
		this.iUniref100 = aUniref100;
		this.iUpdated = true;
	}

	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM uniprotentry WHERE uniprotentryid = ?");
		lStat.setLong(1, iUniprotentryid);
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
		if(!aKeys.containsKey(UNIPROTENTRYID)) {
			throw new IllegalArgumentException("Primary key field 'UNIPROTENTRYID' is missing in HashMap!");
		} else {
			iUniprotentryid = ((Long)aKeys.get(UNIPROTENTRYID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM uniprotentry WHERE uniprotentryid = ?");
		lStat.setLong(1, iUniprotentryid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iUniprotentryid = lRS.getLong("uniprotentryid");
			iFk_proteinid = lRS.getLong("fk_proteinid");
			iTaxid = lRS.getLong("taxid");
			iEcnumber = (String)lRS.getObject("ecnumber");
			iKonumber = (String)lRS.getObject("konumber");
			iKeywords = (String)lRS.getObject("keywords");
			iUniref100 = (String)lRS.getObject("uniref100");
			iUniref90 = (String)lRS.getObject("uniref90");
			iUniref50 = (String)lRS.getObject("uniref50");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'uniprotentry' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'uniprotentry' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from uniprotentry";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<UniprotentryTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<UniprotentryTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<UniprotentryTableAccessor>  entities = new ArrayList<UniprotentryTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new UniprotentryTableAccessor(rs));
		}
		rs.close();
		stat.close();
		return entities;
	}
	
	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<UniprotentryTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<UniprotentryTableAccessor> retrieveAllEntriesWithNoUniRefAnnotation(Connection aConn, Long increase) throws SQLException {
		ArrayList<UniprotentryTableAccessor> entities = new ArrayList<UniprotentryTableAccessor>();
		String statement;
		if(increase == null) {
			statement = " WHERE uniref100 IS NULL";
		} else {
			statement = " WHERE uniref100 IS NULL LIMIT ?, ?";
		}
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +  statement);
		
		if(increase != null) {
			ps.setLong(1, increase);
			ps.setLong(2, 100);
		}
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			entities.add(new UniprotentryTableAccessor(rs));
		}
		rs.close();
		ps.close();
		return entities;
	}
	
	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<UniprotentryTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<UniprotentryTableAccessor> retrieveAllEntriesWithEmptyUniRefAnnotation(Connection aConn, Long increase) throws SQLException {
		ArrayList<UniprotentryTableAccessor> entities = new ArrayList<UniprotentryTableAccessor>();
		String statement;
		if(increase == null) {
			statement = " WHERE uniref100 LIKE ''";
		} else {
			statement = " WHERE uniref100 LIKE '' LIMIT ?, ?";
		}
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +  statement);
		
		if(increase != null) {
			ps.setLong(1, increase);
			ps.setLong(2, 100);
		}
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			entities.add(new UniprotentryTableAccessor(rs));
		}
		rs.close();
		ps.close();
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE uniprotentry SET uniprotentryid = ?, fk_proteinid = ?, taxid = ?, ecnumber = ?, konumber = ?, keywords = ?, uniref100 = ?, uniref90 = ?, uniref50 = ? WHERE uniprotentryid = ?");
		lStat.setLong(1, iUniprotentryid);
		lStat.setLong(2, iFk_proteinid);
		lStat.setLong(3, iTaxid);
		lStat.setObject(4, iEcnumber);
		lStat.setObject(5, iKonumber);
		lStat.setObject(6, iKeywords);
		lStat.setObject(7, iUniref100);
		lStat.setObject(8, iUniref90);
		lStat.setObject(9, iUniref50);
		lStat.setLong(10, iUniprotentryid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO uniprotentry (uniprotentryid, fk_proteinid, taxid, ecnumber, konumber, keywords, uniref100, uniref90, uniref50) values(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		if(iUniprotentryid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iUniprotentryid);
		}
		if(iFk_proteinid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iFk_proteinid);
		}
		if(iTaxid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iTaxid);
		}
		if(iEcnumber == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iEcnumber);
		}
		if(iKonumber == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iKonumber);
		}
		if(iKeywords == null) {
			lStat.setNull(6, -1);
		} else {
			lStat.setObject(6, iKeywords);
		}
		if(iUniref100 == null) {
			lStat.setNull(7, -1);
		} else {
			lStat.setObject(7, iUniref100);
		}
		if(iUniref90 == null) {
			lStat.setNull(8, -1);
		} else {
			lStat.setObject(8, iUniref90);
		}
		if(iUniref50 == null) {
			lStat.setNull(9, -1);
		} else {
			lStat.setObject(9, iUniref50);
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
			iUniprotentryid = ((Number) iKeys[0]).longValue();
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