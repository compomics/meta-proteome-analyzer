package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastaUtilities {
	/**
	 * This method parses proteinIDs and sequences from the FASTA file.
	 * 
	 * @return Database db
	 */
	public static Database read(String filePath) {
		Database database = null;
		final File fastaFile = new File(filePath);
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			String nextLine;
			nextLine = reader.readLine();
			boolean firstline = true;
			final ArrayList<String> proteinIDs = new ArrayList<String>();
			final ArrayList<String> proteinSeqs = new ArrayList<String>();
			StringBuffer stringBf = new StringBuffer();
			while (nextLine != null) {					
					if (nextLine != null && nextLine.trim().length() > 0){	
						if (nextLine.charAt(0) == '>') {	
							if(firstline){
								proteinIDs.add(nextLine);
							} else {								
								proteinSeqs.add(stringBf.toString());
								stringBf = new StringBuffer(); 
								proteinIDs.add(nextLine);
							}							
						} else {
							stringBf.append(nextLine);		
						}	
					}
					nextLine = reader.readLine();
					firstline = false;	
			}
			proteinSeqs.add(stringBf.toString());
			
			// Map of entries
			List<Entry> entries = new ArrayList<Entry>();
			
			// Iterate over the proteinIDs
			for(int i = 0; i < proteinIDs.size(); i++){
				entries.add(new Entry((i+1),proteinIDs.get(i), proteinSeqs.get(i)));
			}
			
			// Instantiate the Database object.
			database = new Database(fastaFile.getName(), entries);			
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return database;
	}
	
	public static void writeDatabase(Database inputDb, String outputPath) {
		try {
			BufferedWriter bfWriter = new BufferedWriter(new FileWriter(outputPath));
			List<Entry> entries = inputDb.getEntries();
			int count = 1;
			for (Entry e : entries) {
				String header = e.getName();
				String gi = " ";
				if(header.contains("gi")){
					String[] split = e.getName().split("\\s+");
					for (String temp : split) {
						if(temp.contains("gi")) {
							gi = temp.substring(2);
						}
					}
					header = header.replaceFirst(">", ">gi|" + gi + "|");
				} else {
					header = e.getName().replaceFirst(">", ">gi|GENSEQ" + count + "|");
				}
				
				header = header.trim().replaceAll(" +", " ");
				bfWriter.write(header);
				bfWriter.newLine();
				bfWriter.write(e.getSequenceAsString());
				bfWriter.newLine();
				count++;
			}
			bfWriter.flush();
			bfWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Database readDB = FastaUtilities.read("metadb_potsdam.fasta");
		FastaUtilities.writeDatabase(readDB, "metadb_potsdam_formatted.fasta");
	}
}
