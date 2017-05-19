/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 17/05/2013
 * Time: 12:48:55
 */
package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'uniprotentryid' column.
	 */
	protected long iUniprotentryid = Long.MIN_VALUE;

	/**
	 * This variable represents the contents for the 'taxid' column.
	 */
	protected long iTaxid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'ecnumber' column.
	 */
	protected String iEcnumber;


	/**
	 * This variable represents the contents for the 'konumber' column.
	 */
	protected String iKonumber;


	/**
	 * This variable represents the contents for the 'keywords' column.
	 */
	protected String iKeywords;
	
	/**
	 * This variable represents the contents for the 'uniref50' column.
	 */
	protected String iUniref50;
	
	/**
	 * This variable represents the contents for the 'uniref90' column.
	 */
	protected String iUniref90;
	
	/**
	 * This variable represents the contents for the 'uniref100' column.
	 */
	protected String iUniref100;

	/**
	 * This variable represents the key for the 'uniprotentryid' column.
	 */
	public static final String UNIPROTENTRYID = "UNIPROTENTRYID";

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
		if(aParams.containsKey(UniprotentryTableAccessor.UNIPROTENTRYID)) {
            iUniprotentryid = ((Long)aParams.get(UniprotentryTableAccessor.UNIPROTENTRYID)).longValue();
		}
		if(aParams.containsKey(UniprotentryTableAccessor.TAXID)) {
            iTaxid = ((Long)aParams.get(UniprotentryTableAccessor.TAXID)).longValue();
		}
		if(aParams.containsKey(UniprotentryTableAccessor.ECNUMBER)) {
            iEcnumber = (String)aParams.get(UniprotentryTableAccessor.ECNUMBER);
		}
		if(aParams.containsKey(UniprotentryTableAccessor.KONUMBER)) {
            iKonumber = (String)aParams.get(UniprotentryTableAccessor.KONUMBER);
		}
		if(aParams.containsKey(UniprotentryTableAccessor.KEYWORDS)) {
            iKeywords = (String)aParams.get(UniprotentryTableAccessor.KEYWORDS);
		}
		if(aParams.containsKey(UniprotentryTableAccessor.UNIREF50)) {
            iUniref50 = (String)aParams.get(UniprotentryTableAccessor.UNIREF50);
		}
		if(aParams.containsKey(UniprotentryTableAccessor.UNIREF90)) {
            iUniref90 = (String)aParams.get(UniprotentryTableAccessor.UNIREF90);
		}
		if(aParams.containsKey(UniprotentryTableAccessor.UNIREF100)) {
            iUniref100 = (String)aParams.get(UniprotentryTableAccessor.UNIREF100);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'UniprotentryTableAccessor' object based on a resultset
	 * obtained by a 'select * from Uniprotentry' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public UniprotentryTableAccessor(ResultSet aResultSet) throws SQLException {
        iUniprotentryid = aResultSet.getLong("uniprotentryid");
        iTaxid = aResultSet.getLong("taxid");
        iEcnumber = (String)aResultSet.getObject("ecnumber");
        iKonumber = (String)aResultSet.getObject("konumber");
        iKeywords = (String)aResultSet.getObject("keywords");
        iUniref50 = (String)aResultSet.getObject("uniref50");
        iUniref90 = (String)aResultSet.getObject("uniref90");
        iUniref100 = (String)aResultSet.getObject("uniref100");
        iUpdated = true;
	}

	/**
	 * This method returns the value for the 'Uniprotentryid' column
	 * 
	 * @return	long	with the value for the Uniprotentryid column.
	 */
	public long getUniprotentryid() {
		return iUniprotentryid;
	}

	/**
	 * This method returns the value for the 'Taxid' column
	 * 
	 * @return	long	with the value for the Taxid column.
	 */
	public long getTaxid() {
		return iTaxid;
	}

	/**
	 * This method returns the value for the 'Ecnumber' column
	 * 
	 * @return	String	with the value for the Ecnumber column.
	 */
	public String getEcnumber() {
		return iEcnumber;
	}

	/**
	 * This method returns the value for the 'Konumber' column
	 * 
	 * @return	String	with the value for the Konumber column.
	 */
	public String getKonumber() {
		return iKonumber;
	}

	/**
	 * This method returns the value for the 'Keywords' column
	 * 
	 * @return	String	with the value for the Keywords column.
	 */
	public String getKeywords() {
		return iKeywords;
	}
	
	/**
	 * This method returns the value for the 'Uniref100' column
	 * 
	 * @return	String	with the value for the Uniref100 column.
	 */
	public String getUniref100() {
		return this.iUniref100;
	}
	
	/**
	 * This method returns the value for the 'Uniref90' column
	 * 
	 * @return	String	with the value for the Uniref90 column.
	 */
	public String getUniref90() {
		return this.iUniref90;
	}

	/**
	 * This method returns the value for the 'Uniref50' column
	 * 
	 * @return	String	with the value for the Uniref50 column.
	 */
	public String getUniref50() {
		return this.iUniref50;
	}

	/**
	 * This method sets the value for the 'Uniprotentryid' column
	 * 
	 * @param	aUniprotentryid	long with the value for the Uniprotentryid column.
	 */
	public void setUniprotentryid(long aUniprotentryid) {
        iUniprotentryid = aUniprotentryid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Taxid' column
	 * 
	 * @param	aTaxid	long with the value for the Taxid column.
	 */
	public void setTaxid(long aTaxid) {
        iTaxid = aTaxid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Ecnumber' column
	 * 
	 * @param	aEcnumber	String with the value for the Ecnumber column.
	 */
	public void setEcnumber(String aEcnumber) {
        iEcnumber = aEcnumber;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Konumber' column
	 * 
	 * @param	aKonumber	String with the value for the Konumber column.
	 */
	public void setKonumber(String aKonumber) {
        iKonumber = aKonumber;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Keywords' column
	 * 
	 * @param	aKeywords	String with the value for the Keywords column.
	 */
	public void setKeywords(String aKeywords) {
        iKeywords = aKeywords;
        iUpdated = true;
	}
	
	/**
	 * This method sets the value for the 'Uniref50' column
	 * 
	 * @param	aUniref50	String with the value for the Uniref50 column.
	 */
	public void setUniref50(String aUniref50) {
        iUniref50 = aUniref50;
        iUpdated = true;
	}
	
	/**
	 * This method sets the value for the 'Uniref90' column
	 * 
	 * @param	aUniref90	String with the value for the Uniref90 column.
	 */
	public void setUniref90(String aUniref90) {
        iUniref90 = aUniref90;
        iUpdated = true;
	}
	
	public void setUniref100(String aUniref100) {
        iUniref100 = aUniref100;
        iUpdated = true;
	}

	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM uniprotentry WHERE uniprotentryid = ?");
		lStat.setLong(1, this.iUniprotentryid);
		int result = lStat.executeUpdate();
		lStat.close();
		return result;
	}


	/**
	 * This method allows the caller to read data for this
	 * object from a persistent store based on the specified keys.
	 *
	 * @param   aConn Connection to the persistent store.
	 */
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(UniprotentryTableAccessor.UNIPROTENTRYID)) {
			throw new IllegalArgumentException("Primary key field 'UNIPROTENTRYID' is missing in HashMap!");
		} else {
            this.iUniprotentryid = ((Long)aKeys.get(UniprotentryTableAccessor.UNIPROTENTRYID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM uniprotentry WHERE uniprotentryid = ?");
		lStat.setLong(1, this.iUniprotentryid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iUniprotentryid = lRS.getLong("uniprotentryid");
            this.iTaxid = lRS.getLong("taxid");
            this.iEcnumber = (String)lRS.getObject("ecnumber");
            this.iKonumber = (String)lRS.getObject("konumber");
            this.iKeywords = (String)lRS.getObject("keywords");
            this.iUniref100 = (String)lRS.getObject("uniref100");
            this.iUniref90 = (String)lRS.getObject("uniref90");
            this.iUniref50 = (String)lRS.getObject("uniref50");
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
		ResultSet rs = stat.executeQuery(UniprotentryTableAccessor.getBasicSelect());
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
			statement = " WHERE uniref50 IS NULL";
		} else {
			statement = " WHERE uniref50 IS NULL LIMIT ?, ?";
		}
		PreparedStatement ps = aConn.prepareStatement(UniprotentryTableAccessor.getBasicSelect() +  statement);
		
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
	
	public static List<UniprotentryTableAccessor> retrieveAllEntriesWithEmptyUniRefAnnotation(
			Connection aConn) throws SQLException {
		return UniprotentryTableAccessor.retrieveAllEntriesWithEmptyUniRefAnnotation(aConn, 0, Long.MAX_VALUE);
	}
	
	/**
	 * Retrieve all entries with empty UniRef annotation.
	 * @param aConn
	 * @param increase
	 * @return
	 * @throws SQLException
	 */
	public static List<UniprotentryTableAccessor> retrieveAllEntriesWithEmptyUniRefAnnotation(
			Connection aConn, long start, long length) throws SQLException {
		List<UniprotentryTableAccessor> entities = new ArrayList<UniprotentryTableAccessor>();
		
//		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE uniref100 LIKE '' LIMIT ?, ?");
		PreparedStatement ps = aConn.prepareStatement(UniprotentryTableAccessor.getBasicSelect() + " WHERE uniref50 IS NULL LIMIT ?, ?");
		
		ps.setLong(1, start);
		ps.setLong(2, length);
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
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
		if(!iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE uniprotentry SET uniprotentryid = ?,taxid = ?, ecnumber = ?, konumber = ?, keywords = ?, uniref100 = ?, uniref90 = ?, uniref50 = ? WHERE uniprotentryid = ?");
		lStat.setLong(1, this.iUniprotentryid);
		lStat.setLong(2, this.iTaxid);
		lStat.setObject(3, this.iEcnumber);
		lStat.setObject(4, this.iKonumber);
		lStat.setObject(5, this.iKeywords);
		lStat.setObject(6, this.iUniref100);
		lStat.setObject(7, this.iUniref90);
		lStat.setObject(8, this.iUniref50);
		lStat.setLong(9, this.iUniprotentryid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO uniprotentry (uniprotentryid, taxid, ecnumber, konumber, keywords, uniref100, uniref90, uniref50) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		if(this.iUniprotentryid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iUniprotentryid);
		}
		if(this.iTaxid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, this.iTaxid);
		}
		if(this.iEcnumber == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, this.iEcnumber);
		}
		if(this.iKonumber == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, this.iKonumber);
		}
		if(this.iKeywords == null) {
			lStat.setNull(5, -1);
		} else {
			lStat.setObject(5, this.iKeywords);
		}
		if(this.iUniref100 == null) {
			lStat.setNull(6, -1);
		} else {
			lStat.setObject(6, this.iUniref100);
		}
		if(this.iUniref90 == null) {
			lStat.setNull(7, -1);
		} else {
			lStat.setObject(7, this.iUniref90);
		}
		if(this.iUniref50 == null) {
			lStat.setNull(8, -1);
		} else {
			lStat.setObject(8, this.iUniref50);
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
            this.iUniprotentryid = ((Number) this.iKeys[0]).longValue();
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