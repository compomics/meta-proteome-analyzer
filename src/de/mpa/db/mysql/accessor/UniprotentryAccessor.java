package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import de.mpa.model.dbsearch.UniProtEntryMPA;
import de.mpa.util.Formatter;

//import org.stringtemplate.v4.compiler.STParser.template_return;


public class UniprotentryAccessor extends UniprotentryTableAccessor {
	/**
	 * Calls the super class.
	 * @param params
	 */
	public UniprotentryAccessor(HashMap params) {
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
		PreparedStatement ps = conn.prepareStatement(UniprotentryTableAccessor.getBasicSelect() + " WHERE " + UniprotentryTableAccessor.UNIPROTENTRYID + " = ?");
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
		ResultSet rs = stat.executeQuery(UniprotentryTableAccessor.getBasicSelect());
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
		UniprotentryAccessor uniProtAccessor = UniprotentryAccessor.findFromID(uniprotID, conn);

		if (uniProtAccessor != null) {
			temp = new UniProtEntryMPA(uniProtAccessor);
		}
		return temp;
	}



	/**
	 * Adds a new protein with accession and description to the database..
	 * @param fastaEntryList The list of all FASTA entries.
	 * @param conn The database connection object.
	 * @return Map of proteinID (key) to uniProtID
	 * @throws SQLException when the persistence did not succeed.
	 */
	public static TreeMap<Long, Long> addMultipleUniProtEntriesToDatabase(TreeMap<Long, UniProtEntryMPA> uniprotid2uniprotentrymap, Connection conn) throws SQLException{

		// Map with the the accession and the uniprotID
		TreeMap<Long, Long> uniProtIDMap = new TreeMap<Long, Long>();


		// Create a sql statement
		PreparedStatement lStat = conn.prepareStatement("INSERT INTO uniprotentry (uniprotentryid, taxid, ecnumber, konumber, keywords, uniref100, uniref90, uniref50) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		// TODO: DOES BATCH COMMIT CAUSE PROBLEMS? --> CATCH ERRORS AND RETRY ??
		// Add all FASTA entries to the sql statement 
		for (Long protID : uniprotid2uniprotentrymap.keySet()) {
			if (uniprotid2uniprotentrymap.get(protID) != null) {
				// Get the UniProt Entry
				UniProtEntryMPA uniProtEntry = uniprotid2uniprotentrymap.get(protID);

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
				lStat.addBatch();
            }
			lStat.executeBatch();

			// Get the keys (uniprotEntry ID)
			ResultSet generatedKeys = lStat.getGeneratedKeys();
			while (generatedKeys.next()) {
				long uniProtId = generatedKeys.getLong(1);
				uniProtIDMap.put(protID, uniProtId);
			}
		}
		lStat.close();

		return uniProtIDMap;
	}

	/**
	 * Adds a new protein with accession and description to the database..
	 * @param Single 
	 * @param conn The database connection object.
	 * @return Map of proteinID (key) to uniProtID
	 * @throws SQLException when the persistence did not succeed.
	 */
	public static Long addProtein(UniProtEntryMPA uniprotentry, Connection conn) throws SQLException{

		Long uniProtId = -1L;
		// Create a sql statement
		PreparedStatement lStat = conn.prepareStatement("INSERT INTO uniprotentry (uniprotentryid, taxid, ecnumber, konumber, keywords, uniref100, uniref90, uniref50) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		// Get the UniProt Entry
		UniProtEntryMPA uniProtEntry = uniprotentry;

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
		lStat.addBatch();
        lStat.execute();
		
		// Get the keys (uniprotEntry ID)
		ResultSet generatedKeys = lStat.getGeneratedKeys();
		while (generatedKeys.next()) {
			uniProtId = generatedKeys.getLong(1);
		}
		
		
		lStat.close();
		return uniProtId;
	}
}

