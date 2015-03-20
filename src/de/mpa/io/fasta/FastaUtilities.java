package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.util.protein.Header;

public class FastaUtilities {
	
	/**
	 * This method parses proteinIDs and sequences from the FASTA file.
	 * @param filePath File path.
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
	
	/**
	 * This method creates a simple reversed decoy FASTA database
	 * @param targetFastaFile File path.
	 * @throws IOException 
	 */
	public static void createDecoyDatabase(File fastaFile) throws IOException {
		if (fastaFile.getAbsolutePath().contains("decoy")) {
			throw new IOException("Please select a target FASTA database - instead of a decoy FASTA database!");
		}
		final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
		String decoyFilePath = fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + "_decoy.fasta";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(decoyFilePath));
		String nextLine;
		nextLine = reader.readLine();
		boolean firstline = true;
		StringBuilder stringBuilder = new StringBuilder();
		while (nextLine != null) {
			if (nextLine != null && nextLine.trim().length() > 0) {
				if (nextLine.charAt(0) == '>') {
					if (firstline) {
						writer.append(nextLine + " - REVERSED");
						writer.newLine();
					} else {
						writer.append(stringBuilder.reverse().toString());
						writer.newLine();
						stringBuilder = new StringBuilder();
						writer.append(nextLine + " - REVERSED");
						writer.newLine();
					}
				} else {
					stringBuilder.append(nextLine);
				}
			}
			nextLine = reader.readLine();
			firstline = false;
		}
		writer.append(stringBuilder.reverse().toString());
		writer.newLine();
		reader.close();
		writer.flush();
		writer.close();
	}
	
	/**
	 * Extracts a FASTA database defined by start and end offset line number.
	 * @param fastaFilePath Filepath of the FASTA input file.
	 * @param outFilePath Filepath of the FASTA output file.
	 * @param start Line number offset start.
	 * @param end Line number offset end.
	 */
	public static void readAndWriteDatabase(String fastaFilePath, String outFilePath, int start, int end) {
		final File fastaFile = new File(fastaFilePath);
		final File outFile = new File(outFilePath);
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			final BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			int lineCounter = 1;
			String nextLine;
			nextLine = reader.readLine();
			while (nextLine != null) {
				if (nextLine.trim().length() > 0) {
					if(lineCounter >= start) {
						writer.write(nextLine);
						writer.newLine();
					}
					nextLine = reader.readLine();
				}
				if(lineCounter >= end) break;
				lineCounter++;
			}
			reader.close();
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a FASTA file from a defined input Database object.
	 * @param inputDb Input database.
	 * @param outputPath Output path.
	 */
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
	
	/**
	 * This method reads protein IDs from a FASTA file and inserts them to a map (key: accession id - value: header line) 
	 * @param fastaFile FASTA file.
	 * @return Mapping of the identifiers.
	 */
	public static Map<String, String> readIds(String fastaFile) {
		
		Map<String, String> ids = new HashMap<String, String>();
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(fastaFile + "2"));
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			String nextLine;
			int counter = 0;
			while ((nextLine = reader.readLine()) != null) { 
				if (nextLine.trim().length() > 0) {
					if (nextLine.charAt(0) == '>') {
						nextLine = nextLine.replaceFirst("_FWD", " FWD");
						writer.write(nextLine);
						writer.newLine();
						Header header = Header.parseFromFASTA(nextLine);
						String key = header.getAccession();
						if(!ids.containsKey(key)) {
							ids.put(key, nextLine);
							counter++;
						} else {
							// Duplicate entries.
//							System.out.println(key + " - " + nextLine);
						}
					} else {
						writer.write(nextLine);
						writer.newLine();
					}
				}
			}
			System.out.println("no. sequences: " + counter);
			reader.close();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ids;
	}

}
