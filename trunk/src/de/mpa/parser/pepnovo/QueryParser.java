package de.mpa.parser.pepnovo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains only the static method read() which
 * parses the PepNovo+ BLAST query files, e.g. *out_query.txt or *out_full.txt.
 * 
 * @author T.Muth
 */
public class QueryParser {
	
	/**
	 * Reads the PepNovo query output file and returns a QueryFile object.
	 * 
	 * @param filename Query filename
	 * @return queryFile QueryFile object
	 */
	public static QueryFile read(String filename) {
				
		BufferedReader reader = null;
		QueryEntry queryEntry = null;		
		List<QueryEntry> entryList = new ArrayList<QueryEntry>();
		try {
			reader = new BufferedReader(new FileReader(filename));
			String nextLine;
			
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				String[] tokens = nextLine.split("\\s+");
				
				queryEntry = new QueryEntry();
				
				if (tokens.length > 5 && !nextLine.equals("")){
					queryEntry.setSpectrumNumber(Integer.valueOf(tokens[1]));
					queryEntry.setScore(Double.valueOf(tokens[4]));
					
					List<String> queries = Arrays.asList(tokens[5].trim().split("-"));
					queryEntry.setQueries(queries);
				}
				entryList.add(queryEntry);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new QueryFile(filename, entryList);
	}
	
	/**
	 * Writes the formatted query file.
	 * @param queryFile Input query object
	 * @param filename Query output filename
	 */
	public static String write(QueryFile queryFile, String filename) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(filename)));	        
	        List<QueryEntry> queryEntries = queryFile.getQueryEntries();
	        // Iterate the query entries.
	        for (QueryEntry entry : queryEntries) {
	        	// Iterate the queries.
				List<String> queries = entry.getQueries();
				int count = 1;
				for (String querySeq : queries) {
					if(querySeq.length() > 0) {
						writer.write(">" + "Spectrum_" + entry.getSpectrumNumber() + "|" + "Query_" + count++ + "|" + "Score:" + entry.getScore());
						writer.newLine();
						writer.write(querySeq);
						writer.newLine();
					}
				}
			}
			// Close the file writer
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return filename;
	}
}
