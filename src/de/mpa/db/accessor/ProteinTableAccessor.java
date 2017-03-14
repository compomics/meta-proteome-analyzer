/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 02/04/2012
 * Time: 15:26:01
 */
package de.mpa.db.accessor;

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

import de.mpa.io.fasta.DigFASTAEntry;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Protein table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ProteinTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys;

	/**
	 * This variable represents the contents for the 'proteinid' column.
	 */
	protected long iProteinid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'accession' column.
	 */
	protected String iAccession;


	/**
	 * This variable represents the contents for the 'description' column.
	 */
	protected String iDescription;


	/**
	 * This variable represents the contents for the 'sequence' column.
	 */
	protected String iSequence;
	
	
	/**
	 * This variable represents the contents for the 'sequence' column.
	 */
	protected long ifk_UniProtID = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'source' column.
	 */
	protected String iSource;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected Timestamp iCreationdate;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected Timestamp iModificationdate;


	/**
	 * This variable represents the key for the 'proteinid' column.
	 */
	public static final String PROTEINID = "PROTEINID";

	/**
	 * This variable represents the key for the 'accession' column.
	 */
	public static final String ACCESSION = "ACCESSION";

	/**
	 * This variable represents the key for the 'description' column.
	 */
	public static final String DESCRIPTION = "DESCRIPTION";

	/**
	 * This variable represents the key for the 'sequence' column.
	 */
	public static final String SEQUENCE = "SEQUENCE";
	
	
	/**
	 * This variable represents the key for the 'sequence' column.
	 */
	public static final String FK_UNIPROTID = "UNIPROT_ID";

	/**
	 * This variable represents the key for the 'source' column.
	 */
	public static final String SOURCE = "SOURCE";

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
	public ProteinTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ProteinTableAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		if(aParams.containsKey(ProteinTableAccessor.PROTEINID)) {
            iProteinid = ((Long)aParams.get(ProteinTableAccessor.PROTEINID)).longValue();
		}
		if(aParams.containsKey(ProteinTableAccessor.ACCESSION)) {
            iAccession = (String)aParams.get(ProteinTableAccessor.ACCESSION);
		}
		if(aParams.containsKey(ProteinTableAccessor.DESCRIPTION)) {
            iDescription = (String)aParams.get(ProteinTableAccessor.DESCRIPTION);
		}
		if(aParams.containsKey(ProteinTableAccessor.SEQUENCE)) {
            iSequence = (String)aParams.get(ProteinTableAccessor.SEQUENCE);
		}
		if(aParams.containsKey(ProteinTableAccessor.FK_UNIPROTID)) {
            ifk_UniProtID = (Long)aParams.get(ProteinTableAccessor.FK_UNIPROTID);
		}
		if(aParams.containsKey(ProteinTableAccessor.SOURCE)) {
            iSource = (String)aParams.get(ProteinTableAccessor.SOURCE);
		}
		if(aParams.containsKey(ProteinTableAccessor.CREATIONDATE)) {
            iCreationdate = (Timestamp)aParams.get(ProteinTableAccessor.CREATIONDATE);
		}
		if(aParams.containsKey(ProteinTableAccessor.MODIFICATIONDATE)) {
            iModificationdate = (Timestamp)aParams.get(ProteinTableAccessor.MODIFICATIONDATE);
		}
        iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ProteinTableAccessor(ResultSet aResultSet) throws SQLException {
        iProteinid = aResultSet.getLong("proteinid");
        iAccession = (String)aResultSet.getObject("accession");
        iDescription = (String)aResultSet.getObject("description");
        iSequence = (String)aResultSet.getObject("sequence");
        ifk_UniProtID = aResultSet.getLong("fk_uniprotentryid");
        iSource = (String)aResultSet.getObject("source");
        iCreationdate = (Timestamp)aResultSet.getObject("creationdate");
        iModificationdate = (Timestamp)aResultSet.getObject("modificationdate");

        iUpdated = true;
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ProteinTableAccessor(DigFASTAEntry entry) {
        iAccession = entry.getIdentifier();
        iDescription = entry.getDescription();
        iSequence = entry.getSequence();
        ifk_UniProtID = entry.getUniProtID();
        iSource = entry.getType().toString();
	}
	

	/**
	 * This method returns the value for the 'Proteinid' column
	 * 
	 * @return	long	with the value for the Proteinid column.
	 */
	public long getProteinid() {
		return iProteinid;
	}

	/**
	 * This method returns the value for the 'Accession' column
	 * 
	 * @return	String	with the value for the Accession column.
	 */
	public String getAccession() {
		return iAccession;
	}

	/**
	 * This method returns the value for the 'Description' column
	 * 
	 * @return	String	with the value for the Description column.
	 */
	public String getDescription() {
		return iDescription;
	}

	/**
	 * This method returns the value for the 'Sequence' column
	 * 
	 * @return	String	with the value for the Sequence column.
	 */
	public String getSequence() {
		return iSequence;
	}
	
	
	/**
	 * This method returns the value for the 'fk_UniProtID' column
	 * 
	 * @return	Long with the value for the Fk_UniProtID column.
	 */
	public Long getFK_UniProtID() {
		return ifk_UniProtID;
	}
	

	/**
	 * This method returns the value for the 'Source' column
	 * 
	 * @return	String	with the value for the Source column.
	 */
	public String getSource() {
		return iSource;
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
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public Timestamp getModificationdate() {
		return iModificationdate;
	}

	/**
	 * This method sets the value for the 'Proteinid' column
	 * 
	 * @param	aProteinid	long with the value for the Proteinid column.
	 */
	public void setProteinid(long aProteinid) {
        iProteinid = aProteinid;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Accession' column
	 * 
	 * @param	aAccession	String with the value for the Accession column.
	 */
	public void setAccession(String aAccession) {
        iAccession = aAccession;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Description' column
	 * 
	 * @param	aDescription	String with the value for the Description column.
	 */
	public void setDescription(String aDescription) {
        iDescription = aDescription;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Sequence' column
	 * 
	 * @param	aSequence	String with the value for the Sequence column.
	 */
	public void setSequence(String aSequence) {
        iSequence = aSequence;
        iUpdated = true;
	}
	
	/**
	 * This method sets the value for the 'aFK_uniProtID' column
	 * 
	 * @param	aSequence	Long with the value for the aFK_uniProtID column.
	 */
	public void setFK_uniProtID(Long aFK_uniProtID) {
        ifk_UniProtID = aFK_uniProtID;
        iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Source' column
	 * 
	 * @param	aSource	String with the value for the Source column.
	 */
	public void setSource(String aSource) {
        iSource = aSource;
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
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(Timestamp aModificationdate) {
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM protein WHERE proteinid = ?");
		lStat.setLong(1, this.iProteinid);
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
		if(!aKeys.containsKey(ProteinTableAccessor.PROTEINID)) {
			throw new IllegalArgumentException("Primary key field 'PROTEINID' is missing in HashMap!");
		} else {
            this.iProteinid = ((Long)aKeys.get(ProteinTableAccessor.PROTEINID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM protein WHERE proteinid = ?");
		lStat.setLong(1, this.iProteinid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
            this.iProteinid = lRS.getLong("proteinid");
            this.iAccession = (String)lRS.getObject("accession");
            this.iDescription = (String)lRS.getObject("description");
            this.iSequence = (String)lRS.getObject("sequence");
            this.ifk_UniProtID = lRS.getLong("fk_uniprotentryid");
            this.iSource = (String)lRS.getObject("source");
            this.iCreationdate = (Timestamp)lRS.getObject("creationdate");
            this.iModificationdate = (Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'protein' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'protein' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from protein";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ProteinTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ProteinTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ProteinTableAccessor>  entities = new ArrayList<ProteinTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(ProteinTableAccessor.getBasicSelect());
		while(rs.next()) {
			entities.add(new ProteinTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE protein SET proteinid = ?, accession = ?, description = ?, sequence = ?, fk_uniprotentryid = ?, source = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE proteinid = ?");
		lStat.setLong(1, this.iProteinid);
		lStat.setObject(2, this.iAccession);
		lStat.setObject(3, this.iDescription);
		lStat.setObject(4, this.iSequence);
		lStat.setLong(5, this.ifk_UniProtID);
		lStat.setObject(6, this.iSource);
		lStat.setObject(7, this.iCreationdate);
		lStat.setLong(8, this.iProteinid);
		int result = lStat.executeUpdate();
		lStat.close();
        iUpdated = false;
		return result;
	}


	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store. A9CBA2
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO protein (proteinid, accession, description, sequence, fk_uniprotentryid, source, creationdate, modificationdate) values(?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ", Statement.RETURN_GENERATED_KEYS);
		if(this.iProteinid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, this.iProteinid);
		}
		if(this.iAccession == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, this.iAccession);
		}
		if(this.iDescription == null) {
			lStat.setNull(3, -1);
		} else {
			lStat.setObject(3, this.iDescription);
		}
		if(this.iSequence == null) {
			lStat.setNull(4, -1);
		} else {
			lStat.setObject(4, this.iSequence);
		}
		if(this.ifk_UniProtID == Long.MIN_VALUE) {
			lStat.setNull(5, -1);
		} else {
			lStat.setLong(5, this.ifk_UniProtID);
		}
		if(this.iSource == null) {
			lStat.setNull(6, 12);
		} else {
			lStat.setObject(6, this.iSource);
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
            this.iProteinid = ((Number) this.iKeys[0]).longValue();
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