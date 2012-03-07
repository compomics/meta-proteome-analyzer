package de.mpa.algorithms.quantification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FastaLoader {
	
	public static void main(String[] args) {
//		FastaDB fastaDB = FastaLoader.read(new File("C:/Documents and Settings/kohrs/My Documents/Downloads/uniprot_sprot.fasta"));
//		//fastaDB.getEntry(accession)
//		Entry entry = fastaDB.getEntry(1);
//		System.out.println(entry.getSequence());
//		System.out.println(entry.getLength());
		File fastaFile = new File("C:/Documents and Settings/kohrs/My Documents/Downloads/uniprot_sprot.fasta");
//		filter(fastaFile, "Escherichia");
		ArrayList<String> filterStrings = new ArrayList<String>();
		filterStrings.add("_ECO");
		filterStrings.add("Escherichia coli");

		// Methode zum Filtern einer unter dem Pfad hinterlegten FASTA-Datei nach den angegebenen FilterStrings
		// Ausgabe ist eine gefilterte FASTA-Datei
		
		filter(fastaFile, filterStrings);
	}
	
	public static void filter(File fastaFile, ArrayList<String> filterStrings) {
		
		try {
			File fastaOutput = new File(fastaFile.getPath().replace(".", "_filtered."));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fastaOutput)));

			BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			String line;
			boolean ofInterest = false;
//			int numEntries = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(">")) {		// new header found
					ofInterest = false;
					for (String filterString : filterStrings) {
						ofInterest |= line.contains(filterString);
						if (ofInterest) {
//							numEntries++;
//							if (numEntries == 22889) {
//								System.out.println("stopp");
//							} 
							break;
						}
					}
//					ofInterest = line.contains(filterString);
				}
				if (ofInterest) {
					bw.write(line);
					bw.flush();
					bw.write("\n");
				}
			}
//			System.out.println(numEntries);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
