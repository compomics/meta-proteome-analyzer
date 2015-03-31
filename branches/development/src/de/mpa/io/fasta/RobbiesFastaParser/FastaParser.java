package de.mpa.io.fasta.RobbiesFastaParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to parse a FASTA-file: +
 * http://de.wikipedia.org/wiki/FASTA-Format
 * @author R. Heyer
 *
 */
public class FastaParser {
	
	/**
	 * ENUM for DATABASE Types and Begins.
	 * @author R. Heyer
	 */
	public enum DB_Type {
		UNIPROTSPROT 	(">sp|"),
		UNIPROTTREMBL 	(">tr|"),
		NCBIGENBANK 	(">gi|"), 
		NCBIREFERENCE 	(">ref|"),
		GENERIC 	(">generic|"),
		METAGENOME1 	(">|"),
		METAGENOME2 	(">");		

		/**
		 * String of header begin.
		 */
		private final String dbStartFlag;

		/**
		 * Constructor for DB_TYPE
		 * @param dbStart
		 */
		DB_Type(String dbStart) {
			this.dbStartFlag = dbStart;
		}
	}

	/**
	 * Method, which reads a fasta file and writes a new one (unused)
	 * @param file. The name and path of the input database
	 */
	public void readAndWriteFasta(String file){

		// Initalize Buffered Reader.
		BufferedReader reader;

		// Go through FASTA-file
		try {
			// Initialize BufferedReader
			reader = new BufferedReader(new FileReader(new File(file)));
			// Initialize BufferedWriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("" + file.substring(0, file.lastIndexOf('.')) + "_out.fasta" )));
			// String of the current line.
			String line;
			// Number of the current entry
			int i = 0;
			// Read every line in FASTA-file
						line = reader.readLine();
						while (line != null) {
							// Check if not an empty row and if begin of protein entry
							if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
								// Get elements of the database entry
								String header 		= line;
								DbEntry entry 		= parseHeader(header);
								String sequence = "";
								// Add Sequence
								while((line=reader.readLine()) != null){
										if (line.charAt(0) == '>') {
											break;
										}else{
											sequence+=line;
										}
								}
								entry.setSequence(sequence);
							}
						}
			// Close the writer
			bw.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method which search for a certain FASTA entry into a FASTA database
	 * @param file. The name and path of the input database
	 * @param accession. The accession of the searched fasta entry
	 */
	public DbEntry returnFastaEntry(String file, String accession){
		// The database entry
		DbEntry entry = null;
		
		// Initalize Buffered Reader.
		BufferedReader reader;

		// Go through FASTA-file
		try {
			// Initialize BufferedReader
			reader = new BufferedReader(new FileReader(new File(file)));
			// Initialize BufferedWriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("" + file.substring(0, file.lastIndexOf('.')) + "_out.fasta" )));
			// String of the current line.
			String line;
			// Number of the current entry
			int i = 0;
			// Read every line in FASTA-file
						line = reader.readLine();
						while (line != null) {
							// Check if not an empty row and if begin of protein entry
							if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
								// Get elements of the database entry
								String header 		= line;
								entry 		= parseHeader(header);
								String sequence = "";
								// Add Sequence
								while((line=reader.readLine()) != null){
										if (!line.isEmpty() &&line.charAt(0) == '>') {
											break;
										}else{
											sequence+=line;
										}
								}
								entry.setSequence(sequence);
								
								if (entry.getIdentifier().equals(accession)) {
									System.out.println("I AM THE BEST" + entry.getIdentifier() + " " + accession);
									return entry;
								}
							}
						}
			// Close the writer
			bw.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	

	/**
	 * Parse the FASTA-header and returns the accession/identifier or equal identifier (First entry after the "<" and before the "|"). 
	 * Checks for wrong characters into the identifier
	 * @param header. The FASTA-header
	 * @return identifier. The identifier of the FASTA-entry.
	 */
	private DbEntry parseHeader(String headerline){
	
		// Get elements of the entry
		String header 		= headerline;
		
		// Identifier for the header.
		String identifier = null;
		// DB entry type.
		DB_Type type = null;
		// The List of substrings, separated by "|"
		ArrayList<String> subHeaderList = new ArrayList<String>();

		// Splits the header line
		for (DB_Type db_Type : DB_Type.values()) {
			if (header.startsWith(db_Type.dbStartFlag)) {
				// Remove start tag
				headerline		= headerline.substring(db_Type.dbStartFlag.length(), headerline.length());
				// Remove critical chars
				headerline 		= headerline.replaceAll("'", "");
				// Split header in sub entries
				String[] split = headerline.split("[|]");
				if (split != null && split.length >0) {
					for (int i = 0; i < split.length; i++) {
						subHeaderList.add(split[i]);
					}
				}
				// First entry is the identifier
				identifier 		= subHeaderList.get(0).trim();
				// Check for empty spaces during identifier add them to subheader List
				ArrayList<String> identifierList = new ArrayList<String>(Arrays.asList(identifier.split(" ")));
				identifier		= identifierList.get(0).trim();
				identifierList.remove(0);
				// Add remaining infos to the subheaderList
				subHeaderList.addAll(identifierList);
				// remove critical chars, which might cause problems
				type = db_Type;
				break;
			}
		}

		// Create database entry
		DbEntry entry  = new DbEntry(identifier, header, type, subHeaderList);
		
		// Check for mistakes in header parsing
		if (identifier == null || identifier.length()<1) {
			System.out.println("ERROR");
		}
		return entry;
	}
}







