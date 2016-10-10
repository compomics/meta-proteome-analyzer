package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parser for FASTA entry
 * @author R. Heyer
 *
 */
public class DigFASTAEntryParser {

	/**
	 * Counts the number of entries in a FASTA file.
	 * @param file. The FASTA file.
	 * @return The number of entries.
	 */
	public static int countEntries(String file) {
		
		BufferedReader reader;
		int entryNumber = 0; 
		
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				if ((line.length()>0) && (line.charAt(0) == '>')) {
					entryNumber++;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return entryNumber;
	}
	
	/**
	 * Parse a FASTA database entry.
	 * @param header. first line of FASTA entry.
	 * @param body. Subsequent lines of FASTA entry.
	 * @return database entry object
	 */
	public static DigFASTAEntry parseEntry(String header, String sequence) {
		
		// Sequence
		String protSequence;
		
		protSequence = sequence;

		// The identifier of the FASTA entry
		String identifier = "";
		
		// The type of the FASTA entry
		DigFASTAEntry.Type type = null;
		
		// The subparts of the header
		ArrayList<String> subHeaderList = new ArrayList<String>();
		
		// Description of the fasta entry
		String description = "";
		
		// Parse the header line.
		for (DigFASTAEntry.Type db_Type : DigFASTAEntry.Type.values()) {
			if (header.startsWith(db_Type.dbStartFlag)) {
				// Defines the database entry type, see DigFASTAEntry class
				type = db_Type;
				
				// Remove start tag
				header	= header.substring(db_Type.dbStartFlag.length(), header.length());
				// Remove critical chars
				header 	= header.replaceAll("'", "");
				// Split header in sub entries
				String[] split = header.split("[|]");
				if (split != null && split.length >0) {
					for (int i = 0; i < split.length; i++) {
						subHeaderList.add(split[i]);
					}
				}
				// First entry is the identifier
				identifier 		= subHeaderList.get(0).trim();
				
				// Second entry is the description or in case from NCBI the 4th one 
				if (!type.equals(DigFASTAEntry.Type.NCBIGENBANK)) {
					if (subHeaderList.size() >1 && subHeaderList.get(1)!= null & subHeaderList.get(1).length() >0) {
						description = subHeaderList.get(1).trim();
					}else{
						description = "UNKNOWN";
					}
				}else{
					if (subHeaderList.size() >4 && subHeaderList.get(4)!= null & subHeaderList.get(4).length() >0) {
						description = subHeaderList.get(4).trim();
					}else{
						description = "UNKNOWN";
					}
				}
				
				// Add remaining info to the subheaderList
				subHeaderList.addAll(subHeaderList);
				// remove critical chars, which might cause problems
				for (int i = 0; i < subHeaderList.size(); i++) {
					subHeaderList.set(i, subHeaderList.get(i).replaceAll("'", ""));
				}
				
				break;
			}
		}
		
		DigFASTAEntry dbEntry = new DigFASTAEntry(identifier, header, description, protSequence, type, subHeaderList);
		
		// Create entry object.
		return dbEntry;
		
	}
}
