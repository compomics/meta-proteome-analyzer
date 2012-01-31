package de.mpa.algorithms.quantification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FastaLoader {
	
	public static void main(String[] args) {
		FastaDB fastaDB = FastaLoader.read(new File("C:/Documents and Settings/muth/Desktop/uniprot_sprot.fasta"));
		//fastaDB.getEntry(accession)
		Entry entry = fastaDB.getEntry(1);
		System.out.println(entry.getSequence());
		System.out.println(entry.getLength());
	}
	/**
	 * This method parses proteinIDs and sequences from the FASTA file.
	 * 
	 * @return FastaDB db
	 */
	public static FastaDB read(File fastaFile) {
		FastaDB database = null;
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
							nextLine = nextLine.substring(1);
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
			Map<Integer, Entry> entryMap = new HashMap<Integer, Entry>();
			Map<String, Integer> entryIdMap = new HashMap<String, Integer>();
			
			// Iterate over the proteinIDs
			// TODO: Use utitlities
			//Header header = Header.parseFromFASTA("");
			//String accession = header.getAccession(); 
			
			for(int i = 0; i < proteinIDs.size(); i++){
				entryMap.put(i+1, new Entry((i+1), getFormattedName(proteinIDs.get(i)), proteinSeqs.get(i)));
				//TODO: entryMap.put(i+1, new Entry((i+1), accession, proteinSeqs.get(i)));
				entryIdMap.put(getFormattedName(proteinIDs.get(i)), i+1);
			}
			
			// Instantiate the FastaDB object.
			database = new FastaDB(fastaFile.getName(), entryMap, entryIdMap);			
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return database;
	}
	
	public static String getFormattedName(String accession) {
		String[] split = accession.split(" ");
		return split[0];
	}
}
