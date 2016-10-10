package de.mpa.fastadigest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
	 * @param number. Adds a number before the identifier if selected, Select "0L" if nothing should be added
	 * @return database entry object
	 */
	public static DigFASTAEntry parseEntry(String header, String body, long number) {
			
		DigFASTAEntry dbEntry;
		String identifier = null;
		DigFASTAEntry.Type type = null;
		ArrayList<String> subHeaderList = new ArrayList<String>();
		
		// Parse the header line.
		for (DigFASTAEntry.Type db_Type : DigFASTAEntry.Type.values()) {
			if (header.startsWith(db_Type.dbStartFlag)) {
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
				// Check for empty spaces during identifier add them to sub-header List
				ArrayList<String> identifierList = new ArrayList<String>(Arrays.asList(identifier.split(" ")));
				identifier		= identifierList.get(0).trim();
				identifierList.remove(0);
				// Add remaining info to the subheaderList
				subHeaderList.addAll(identifierList);
				// remove critical chars, which might cause problems
				for (int i = 0; i < subHeaderList.size(); i++) {
					subHeaderList.set(i, subHeaderList.get(i).replaceAll("'", ""));
				}
				
				if (number != 0L) {
					identifier = "" + number + "_" +  identifier;
				} 
				
				type = db_Type;
				break;
			}
		}
		
		dbEntry = new DigFASTAEntry(identifier, header, body, type, subHeaderList);
		
		// Create entry object.
		return dbEntry;
		
	}
	
	
	
}
