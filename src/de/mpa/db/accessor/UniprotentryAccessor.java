package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.util.Formatter;

//import org.stringtemplate.v4.compiler.STParser.template_return;


public class UniprotentryAccessor extends UniprotentryTableAccessor {
	/**
	 * Calls the super class.
	 * @param params
	 */
	public UniprotentryAccessor(@SuppressWarnings("rawtypes") HashMap params) {
		super(params);
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	resultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public UniprotentryAccessor(ResultSet resultSet) throws SQLException {
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
	public static UniprotentryAccessor findFromID(long uniprotID, Connection conn) throws SQLException {
		UniprotentryAccessor temp = null;
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + UNIPROTENTRYID + " = ?");
		ps.setLong(1, uniprotID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new UniprotentryAccessor(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<UniprotentryTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<UniprotentryAccessor> findAllEntries(Connection aConn) throws SQLException {
		ArrayList<UniprotentryAccessor>  entities = new ArrayList<UniprotentryAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new UniprotentryAccessor(rs));
		}
		rs.close();
		stat.close();
		return entities;
	}
	
	
	
	
	/**
	 * This method will find a uniprot entry from the current connection, based on the specified uniprot ID.
	 *
	 * @param uniprotID long with the UniProtEntry ID of the protein to find.
	 * @param conn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static UniProtEntryMPA getMPAUniProtEntry(long uniprotID, Connection conn) throws SQLException {
		
		// The result object
		UniProtEntryMPA temp = null;
		
		// Query the UniProt-table
		UniprotentryAccessor uniProtAccessor = findFromID(uniprotID, conn);
		
		if (uniProtAccessor != null) {
			temp = new UniProtEntryMPA(uniProtAccessor);
		}
		return temp;
	}
	

	//	/**
	//	 * Retrieves a mapping of proteinIDs to UniProt entry accessor objects. 
	//	 * @param conn Connection to query the database
	//	 * @return Map of proteinIDs as keys and UniProt entry accessor objects as values. 
	//	 * @throws SQLException
	//	 */
	//	public static Map<Long, UniprotentryAccessor> retrieveProteinIdToEntryMap(Connection conn) throws SQLException {
	//		Map<Long, UniprotentryAccessor>  uniprotEntries = new HashMap<Long, UniprotentryAccessor>();
	//		Statement stat = conn.createStatement();
	//		ResultSet rs = stat.executeQuery(getBasicSelect());
	//		while(rs.next()) {
	//			uniprotEntries.put(rs.getLong("fk_proteinid"), new UniprotentryAccessor(rs));
	//		}
	//		rs.close();
	//		stat.close();
	//		return uniprotEntries;
	//	}


	//	/**
	//	 * Adds a queried UniProt entry to the database and links it to a certain protein.
	//	 * @param proteinID ProteinID (Database ID)
	//	 * @param taxID Taxonomic identifier (NCBI TaxID)
	//	 * @param ecNumber E.C. Number
	//	 * @param koNumber KO Number
	//	 * @param keywords CSV list of the ontology information as keywords 
	//	 * @param conn Connection to query the database.
	//	 * @return {@link} {@link Uniprotentry}
	//	 * @throws SQLException
	//	 */
	//	public static UniprotentryAccessor addUniProtEntryWithProteinID(Long taxID, String ecNumber, String koNumber, String keywords, String uniref100, String uniref90, String uniref50, Connection conn) throws SQLException {
	//		HashMap<Object, Object> data = new HashMap<Object, Object>(8);
	//		data.put(UniprotentryAccessor.TAXID, taxID);
	//		data.put(UniprotentryAccessor.ECNUMBER, ecNumber);
	//		data.put(UniprotentryAccessor.KONUMBER, koNumber);
	//		data.put(UniprotentryAccessor.KEYWORDS, keywords);
	//		data.put(UniprotentryAccessor.UNIREF100, uniref100);
	//		data.put(UniprotentryAccessor.UNIREF90, uniref90);
	//		data.put(UniprotentryAccessor.UNIREF50, uniref50);
	//		UniprotentryAccessor uniprotentryAccessor = new UniprotentryAccessor(data);			
	//		uniprotentryAccessor.persist(conn);
	//		return uniprotentryAccessor;
	//	}

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
	public static UniprotentryAccessor updateUniProtEntryWithProteinID(Long uniprotentryid, Long taxID, String ecNumber, String koNumber, String keywords, String uniref100, String uniref90, String uniref50, Connection conn)	throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(8);
		data.put(UniprotentryAccessor.UNIPROTENTRYID,  uniprotentryid);
		data.put(UniprotentryAccessor.TAXID, taxID);
		data.put(UniprotentryAccessor.ECNUMBER, ecNumber);
		data.put(UniprotentryAccessor.KONUMBER, koNumber);
		data.put(UniprotentryAccessor.KEYWORDS, keywords);
		data.put(UniprotentryAccessor.UNIREF100, uniref100);
		data.put(UniprotentryAccessor.UNIREF90, uniref90);
		data.put(UniprotentryAccessor.UNIREF50, uniref50);
		UniprotentryAccessor uniprotentryAccessor = new UniprotentryAccessor(data);
		uniprotentryAccessor.update(conn);
		return uniprotentryAccessor;
	}

	//	public static List<RepairEntry> find_incomplete_uniprot_entries(Connection conn) throws SQLException {
	//		List<RepairEntry> incomplete_list = new ArrayList<RepairEntry>();
	//		PreparedStatement ps = conn.prepareStatement("SELECT uniprotentry.*, protein.accession FROM uniprotentry INNER JOIN protein ON protein.proteinid = uniprotentry.fk_proteinid");
	////		ps.setFetchSize(1000);	       
	//		ResultSet rs = ps.executeQuery();
	//		int count = 0;
	//		while (rs.next()) {
	//			RepairEntry this_repair_entry = new RepairEntry(rs);
	//			String prot_accession = this_repair_entry.get_accession();
	//			if (prot_accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
	//				// do nothing
	//			} else if (prot_accession.contains("_BLAST_")) {
	//				this_repair_entry.set_accession(prot_accession.split("_BLAST_")[1]);
	//			}
	//			
	//			System.out.println("test "+rs.getString(("taxid")));
	//			UniprotentryAccessor temp = this_repair_entry.UniprotentryAccessor();
	//			if (rs.getString("ecnumber").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getString("konumber").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getString("keywords").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getInt("taxid") == 0) {
	//				System.out.println("test");
	//				count++;
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getString("uniref100").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getString("uniref90").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			} else if (rs.getString("uniref50").trim().isEmpty()) {
	//				incomplete_list.add(this_repair_entry);
	//			}
	//		}
	//		System.out.println("count: "+count);
	//		rs.close();
	//		ps.close();
	//		return incomplete_list;
	////	}
	//		// helper class to make uniprotrepair faster
	//		public static class RepairEntry {
	//			// data
	//			private UniprotentryAccessor uniprotentry;
	//			private String accession;
	//			private Long proteinid;
	//			// constructor
	//			public RepairEntry(ResultSet rs) throws SQLException {
	//				this.set_entry(new UniprotentryAccessor(rs));
	//				this.set_accession(rs.getString("accession"));
	//				this.set_proteinid(rs.getLong("fk_proteinid"));
	//			}
	//			// methods
	//			public void set_entry(UniprotentryAccessor up) {
	//				this.UniprotentryAccessor = up;
	//			}
	//			public void set_accession(String acc) {
	//				this.accession = acc;
	//			}
	//			public void set_proteinid(Long id) {
	//				this.proteinid = id;
	//			}
	//			public UniprotentryAccessor get_uniprotentry() {
	//				return this.uniprotentry;	
	//			}
	//			public String get_accession() {
	//				return this.accession;
	//			}
	//			public Long get_proteinid() {
	//				return this.proteinid;
	//			}
	//		}



	/**
	 * Adds a new protein with accession and description to the database..
	 * @param fastaEntryList The list of all FASTA entries.
	 * @param conn The database connection object.
	 * @throws SQLException when the persistence did not succeed.
	 */
	public static TreeMap<String, Long> addMulibleUniProtEntriesToDatabase(TreeMap<String, UniProtEntryMPA> uniProtList, Connection conn) throws SQLException{

		// Map with the the accession and the uniprotID
		TreeMap<String, Long> uniProtIDMap = new TreeMap<String, Long>();
		
		
		// Create a sql statement
		PreparedStatement lStat = conn.prepareStatement("INSERT INTO uniprotentry (uniprotentryid, taxid, ecnumber, konumber, keywords, uniref100, uniref90, uniref50) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);


		// Add all FASTA entries to the sql statement
		for (String accession : uniProtList.keySet()) {
			if (uniProtList.get(accession) != null) {
				// Get the UniProt Entry
				UniProtEntryMPA uniProtEntry = uniProtList.get(accession);
				
				// UniProtEntry is unknown at the beginning
				lStat.setNull(1, 4);
				
				// Add UniProtTaxonomy
				Long taxID = uniProtEntry.getTaxid();
				if(taxID == null) {
					lStat.setNull(2, 12);
				} else {
					lStat.setLong(2, taxID);
				}
				// Add EC-numbers
				String ecNumbers = "";
				List<String> ecNumberList = uniProtEntry.getEcnumbers();
				if (ecNumberList.size() > 0) {
					for (String ecNumber : ecNumberList) {
						ecNumbers += ecNumber + ";";
					}
					ecNumbers = Formatter.removeLastChar(ecNumbers);
				}
				if(ecNumbers == null) {
					lStat.setNull(3, -1);
				} else {
					lStat.setObject(3, ecNumbers);
				}

				// Add KO-numbers
				String koNumbers = "";
				List<String> kos = uniProtEntry.getKonumbers();
				if (kos.size() > 0) {
					for (String ko : kos) {
						koNumbers += ko  + ";";
					}
						koNumbers = Formatter.removeLastChar(koNumbers);
				}
				if(koNumbers == null) {
					lStat.setNull(4, -1);
				} else {
					lStat.setObject(4, koNumbers);
				}

				// Add Keywords
				String keywords = "";
				List<String> keywordsList = uniProtEntry.getKeywords();
				if (keywordsList.size() > 0) {
					for (String kw : keywordsList) {
						keywords += kw + ";";
					}
					keywords = Formatter.removeLastChar(keywords);
				}
				if(keywords== null) {
					lStat.setNull(5, -1);
				} else {
					lStat.setObject(5, keywords);
				}

				// Add UniRef100
				 if(uniProtEntry.getUniRefMPA() == null || uniProtEntry.getUniRefMPA().getUniRef100() == null) {
					lStat.setNull(6, 12);
				 	} else {
					lStat.setObject(6, uniProtEntry.getUniRefMPA().getUniRef100());
					}
				 
				 // Add UniReg90
				 if(uniProtEntry.getUniRefMPA() == null || uniProtEntry.getUniRefMPA().getUniRef90() == null) {
					lStat.setNull(7, 12);
				 	} else {
					lStat.setObject(7, uniProtEntry.getUniRefMPA().getUniRef90());
					}
				 
				 // Add UniRef50
				 if(uniProtEntry.getUniRefMPA() == null || uniProtEntry.getUniRefMPA().getUniRef50() == null) {
					lStat.setNull(8, 12);
				 	} else {
					lStat.setObject(8, uniProtEntry.getUniRefMPA().getUniRef50());
					}
					lStat.addBatch();;
			}
			lStat.executeBatch();
			
			// Get the keys (uniprotEntry ID)
			ResultSet generatedKeys = lStat.getGeneratedKeys();
			while (generatedKeys.next()) {
				long uniProtId = generatedKeys.getLong(1);
				uniProtIDMap.put(accession, uniProtId);
			}
		}
		lStat.close();
	
	return uniProtIDMap;
	}
}

