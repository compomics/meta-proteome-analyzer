package de.mpa.io.fasta;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.compomics.util.protein.Protein;

import de.mpa.client.Client;
import de.mpa.db.DBConfiguration;
import de.mpa.db.accessor.ProteinAccessor;

/**
 * Utility class containing static methods to perform clean-up and repair tasks
 * on the database.
 * 
 * @author A. Behne
 */
public class DatabaseRepairer {

	/**
	 * Utility method to repair protein entries in the database that were stored
	 * using their UniProt identifier instead of their respective accession
	 * number. Will also reset description and sequence information inferred
	 * from the provided FASTA file.
	 * 
	 * @param args
	 *            path string pointing to the FASTA file to be used for protein
	 *            information lookup. An index file with an identical filename
	 *            prefix must be present in the same directory (see
	 *            {@link FastaLoader}).
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException(
					"Not enough input arguments. Must specify a path pointing to a FASTA file.");
		}
		
		FastaLoader fl;
		try {
			fl = FastaLoader.getInstance();
			fl.setFastaFile(new File(args[0]));
			fl.setIndexFile(new File(args[0] + ".fb"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			// connect to database
			DBConfiguration dbconfig = new DBConfiguration(Client.getInstance().getConnectionParameters());
			Connection conn = dbconfig.getConnection();

			// generate SQL statement
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM protein WHERE accession LIKE '%\\_%'");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// build protein accessor object from resultset row
				ProteinAccessor pa = new ProteinAccessor(rs);
				String accession = pa.getAccession();

				// perform FASTA lookup
				Protein pf;
				try {
					pf = fl.getProteinFromFasta(accession);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					continue;
				}
				// replace incorrect protein information with data from FASTA file
				pa.setAccession(pf.getHeader().getAccession());
				pa.setDescription(pf.getHeader().getDescription());
				pa.setSequence(pf.getSequence().getSequence());

				// update modification date
				pa.setModificationdate(new Timestamp(new Date().getTime()));
				// store modified protein in database
				pa.update(conn);
			}

			rs.close();
			ps.close();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
	}

}
