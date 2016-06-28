package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.sql.Types;
import java.sql.JDBCType;

import org.stringtemplate.v4.compiler.STParser.template_return;


public class Uniprotentry extends UniprotentryTableAccessor {
	/**
	 * Calls the super class.
	 * @param params
	 */
	public Uniprotentry(@SuppressWarnings("rawtypes") HashMap params) {
		super(params);
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	resultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Uniprotentry(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	/**
	 * This method will find a uniprot entry from the current connection, based on the specified uniprot ID.
	 *
	 * @param uniprotID long with the UniProtEntry ID of the protein to find.
	 * @param conn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static Uniprotentry findFromID(long uniprotID, Connection conn) throws SQLException {
		Uniprotentry temp = null;
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + UNIPROTENTRYID + " = ?");
		ps.setLong(1, uniprotID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new Uniprotentry(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * This method will find a uniprot entry from the current connection, based on the specified protein ID.
	 *
	 * @param uniprotID long with the UniProtEntry ID of the protein to find.
	 * @param conn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static Uniprotentry findFromProteinID(long proteinID, Connection conn) throws SQLException {
		Uniprotentry temp = null;
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + FK_PROTEINID + " = ?");
		ps.setLong(1, proteinID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new Uniprotentry(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * This method will return true if a uniprotentry can be found to a given proteinID.
	 * Returns false otherwise
	 *
	 * @param uniprotID long with the UniProtEntry ID of the protein to find.
	 * @param conn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static boolean check_if_exists_from_proteinID(long proteinID, Connection conn) throws SQLException {
		boolean temp = false;
		PreparedStatement ps = conn.prepareStatement("SELECT up.uniprotentryid FROM uniprotentry up WHERE " + FK_PROTEINID + " = ?");
		ps.setLong(1, proteinID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			temp = true;
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * Retrieves a mapping of proteinIDs to UniProt entry accessor objects. 
	 * @param conn Connection to query the database
	 * @return Map of proteinIDs as keys and UniProt entry accessor objects as values. 
	 * @throws SQLException
	 */
	public static Map<Long, Uniprotentry> retrieveProteinIdToEntryMap(Connection conn) throws SQLException {
		Map<Long, Uniprotentry>  uniprotEntries = new HashMap<Long, Uniprotentry>();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			uniprotEntries.put(rs.getLong("fk_proteinid"), new Uniprotentry(rs));
		}
		rs.close();
		stat.close();
		return uniprotEntries;
	}


	/**
	 * Adds a queried UniProt entry to the database and links it to a certain protein.
	 * @param proteinID ProteinID (Database ID)
	 * @param taxID Taxonomic identifier (NCBI TaxID)
	 * @param ecNumber E.C. Number
	 * @param koNumber KO Number
	 * @param keywords CSV list of the ontology information as keywords 
	 * @param conn Connection to query the database.
	 * @return {@link} {@link Uniprotentry}
	 * @throws SQLException
	 */
	public static Uniprotentry addUniProtEntryWithProteinID(Long proteinID, Long taxID, String ecNumber, String koNumber, String keywords, String uniref100, String uniref90, String uniref50, Connection conn) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(9);
		data.put(Uniprotentry.FK_PROTEINID, proteinID);
		data.put(Uniprotentry.TAXID, taxID);
		data.put(Uniprotentry.ECNUMBER, ecNumber);
		data.put(Uniprotentry.KONUMBER, koNumber);
		data.put(Uniprotentry.KEYWORDS, keywords);
		data.put(Uniprotentry.UNIREF100, uniref100);
		data.put(Uniprotentry.UNIREF90, uniref90);
		data.put(Uniprotentry.UNIREF50, uniref50);
		Uniprotentry uniprotentryAccessor = new Uniprotentry(data);			
		uniprotentryAccessor.persist(conn);
		return uniprotentryAccessor;
	}

	/**
	 * Adds a queried UniProt entry to the database and links it to a certain protein.
	 * 
	 * @param proteinID ProteinID (Database ID)
	 * @param taxID Taxonomic identifier (NCBI TaxID)
	 * @param ecNumber E.C. Number
	 * @param koNumber KO Number
	 * @param keywords CSV list of the ontology information as keywords
	 * @param conn Connection to query the database.
	 * @return {@link} {@link Uniprotentry}
	 * @throws SQLException
	 */
	public static Uniprotentry updateUniProtEntryWithProteinID(Long uniprotentryid, Long proteinID, Long taxID, String ecNumber, String koNumber, String keywords, String uniref100, String uniref90, String uniref50, Connection conn)	throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(9);
		data.put(Uniprotentry.UNIPROTENTRYID,  uniprotentryid);
		data.put(Uniprotentry.FK_PROTEINID, proteinID);
		data.put(Uniprotentry.TAXID, taxID);
		data.put(Uniprotentry.ECNUMBER, ecNumber);
		data.put(Uniprotentry.KONUMBER, koNumber);
		data.put(Uniprotentry.KEYWORDS, keywords);
		data.put(Uniprotentry.UNIREF100, uniref100);
		data.put(Uniprotentry.UNIREF90, uniref90);
		data.put(Uniprotentry.UNIREF50, uniref50);
		Uniprotentry uniprotentryAccessor = new Uniprotentry(data);
		uniprotentryAccessor.update(conn);
		return uniprotentryAccessor;
	}

	public static List<RepairEntry> find_incomplete_uniprot_entries(Connection conn) throws SQLException {
		List<RepairEntry> incomplete_list = new ArrayList<RepairEntry>();
		PreparedStatement ps = conn.prepareStatement("SELECT uniprotentry.*, protein.accession FROM uniprotentry INNER JOIN protein ON protein.proteinid = uniprotentry.fk_proteinid");
//		ps.setFetchSize(1000);	       
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			RepairEntry this_repair_entry = new RepairEntry(rs);
			String prot_accession = this_repair_entry.get_accession();
			if (prot_accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				// do nothing
			} else if (prot_accession.contains("_BLAST_")) {
				this_repair_entry.set_accession(prot_accession.split("_BLAST_")[1]);
			}
			
			System.out.println("test "+rs.getString(("taxid")));
			Uniprotentry temp = this_repair_entry.get_uniprotentry();
			if (rs.getString("ecnumber").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			} else if (rs.getString("konumber").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			} else if (rs.getString("keywords").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			} else if (rs.getInt("taxid") == 0) {
				System.out.println("test");
				count++;
				incomplete_list.add(this_repair_entry);
			} else if (rs.getString("uniref100").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			} else if (rs.getString("uniref90").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			} else if (rs.getString("uniref50").trim().isEmpty()) {
				incomplete_list.add(this_repair_entry);
			}
		}
		System.out.println("count: "+count);
		rs.close();
		ps.close();
		return incomplete_list;
	}
		// helper class to make uniprotrepair faster
		public static class RepairEntry {
			// data
			private Uniprotentry uniprotentry;
			private String accession;
			private Long proteinid;
			// constructor
			public RepairEntry(ResultSet rs) throws SQLException {
				this.set_entry(new Uniprotentry(rs));
				this.set_accession(rs.getString("accession"));
				this.set_proteinid(rs.getLong("fk_proteinid"));
			}
			// methods
			public void set_entry(Uniprotentry up) {
				this.uniprotentry = up;
			}
			public void set_accession(String acc) {
				this.accession = acc;
			}
			public void set_proteinid(Long id) {
				this.proteinid = id;
			}
			public Uniprotentry get_uniprotentry() {
				return this.uniprotentry;	
			}
			public String get_accession() {
				return this.accession;
			}
			public Long get_proteinid() {
				return this.proteinid;
			}
		}

	}

