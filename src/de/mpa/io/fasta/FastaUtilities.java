package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	 * Extracts a FASTA database by excluding a set of FASTA entries.
	 * @param fastaFilePath Filepath of the FASTA input file.
	 * @param outFilePath Filepath of the FASTA output file.
	 */
	public static void readAndWriteExclusiveDatabase(String fastaFilePath, String outFilePath, Set<String> excludedProteins) {
		final File fastaFile = new File(fastaFilePath);
		final File outFile = new File(outFilePath);
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			final BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			String nextLine;
			nextLine = reader.readLine();
			boolean doWrite = true;
			while (nextLine != null) {
				if (nextLine.trim().length() > 0) {
					if (nextLine.charAt(0) == '>') {	
						if (excludedProteins.contains(nextLine)) {
							doWrite = false;
						} else {
							doWrite = true;
						}
					}
					
					if (doWrite) {
						writer.write(nextLine);
						writer.newLine();
					}
					nextLine = reader.readLine();
				}
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
			int count = 0;
			
			for (Entry e : entries) {
				String header = e.getName();
				String gi = " ";
			
				if(!header.contains(">sp")){
					/*
					 
					String[] split = e.getName().split("\\s+");
					for (String temp : split) {
						if(temp.contains("gi")) {
							gi = temp.substring(2);
						}
					}
					header = header.replaceFirst(">", ">gi|" + gi + "|");
				} else {
					header = e.getName().replaceFirst(">", ">gi|GENSEQ" + count + "|");
					
					*/
					bfWriter.write(header);
					bfWriter.newLine();
					bfWriter.write(e.getSequenceAsString());
					bfWriter.newLine();
					count++;
				}
				
//				header = header.trim().replaceAll(" +", " ");
				System.out.println("entries: " + count + " / " + entries.size());
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
	public static void readHeaders(String fastaFile) {
		
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(fastaFile + "_headers"));
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			String nextLine;
			int counter = 0;
			while ((nextLine = reader.readLine()) != null) { 
				if (nextLine.trim().length() > 0) {
					if (nextLine.charAt(0) == '>') {
						writer.write(nextLine);
						writer.newLine();
						counter++;
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
	}
	
	
	
	/**
	 * This method counts the database Ids.
	 * @param fastaFile FASTA file.
	 */
	public static void countDatabaseIds(String fastaFile) {
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			int bact594 = 0, qin2010 = 0, kurokawa2007 = 0, human2010 = 0, human2010_alt = 0, food = 0;
			String nextLine;
			int counter = 0;
			while ((nextLine = reader.readLine()) != null) { 
				if (nextLine.trim().length() > 0) {
					if (nextLine.charAt(0) == '>') {
						String string = nextLine.substring(nextLine.indexOf("FWD_") +9);
						int id = Integer.parseInt(string);
						
						if (id <= 1850744) {
							bact594++;
						} else if (id >= 1850745 && id <= 5118348) {
							qin2010++;
						} else if (id >= 5118349 && id <= 5719100) {
							kurokawa2007++;
						} else if (id >= 5719101 && id <= 5788979) {
							human2010++;
						} else if (id >= 5788980 && id <= 5905697) {
							human2010_alt++; 
						} else if (id >= 5905698 && id <= 6153068) {
							food++;
						} else {
							System.out.println("else: " + id + "!!!");
						}
						counter++;
					} 
				}
			}
			System.out.println("no. sequences: " + counter);
			System.out.println("bact 594: " + bact594);
			System.out.println("qin2010: " + qin2010);
			System.out.println("kurokawa2007: " + kurokawa2007);
			System.out.println("human2010: " + human2010);
			System.out.println("human2010_alt: " + human2010_alt);
			System.out.println("food: " + food);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> retrievePeptides(File peptideFastaFile) {
		BufferedReader reader;
		
		Set<String> peptideSet = new TreeSet<String>();
		try {
			LineNumberReader  lnr = new LineNumberReader(new FileReader(peptideFastaFile));
			lnr.skip(Long.MAX_VALUE);
			int nLines = lnr.getLineNumber();
			System.out.println(nLines);
			lnr.close();
			reader = new BufferedReader(new FileReader(peptideFastaFile));
			
			String nextLine;
			int lineCounter = 0;
			while ((nextLine = reader.readLine()) != null) {
						
						if (nextLine.length() > 0 && nextLine.charAt(0) != '>') {
							peptideSet.add(nextLine);
						}
						if (lineCounter % 1000000 == 0) {
							System.out.println(lineCounter + "/" + nLines);
						}
						lineCounter++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peptideSet;
	}
	
	public static Map<String, Set<String>> matchPeptides(File fastaFile, Set<String> peptides) {
		Map<String, Set<String>> multiMap = new HashMap<String, Set<String>>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
			String nextLine;
			boolean firstline = true;
			StringBuffer stringBf = new StringBuffer();
			String header = null;
			int counter = 0;
			while ((nextLine = reader.readLine()) != null) {
						if (nextLine.length() > 0 && nextLine.charAt(0) == '>') {
							if (!firstline) {
								String proteinSequence = stringBf.toString();
								Iterator<String> iterator = peptides.iterator();
								counter++;
								if (counter % 10000 == 0) System.out.println(counter);
								while (iterator.hasNext()) {
									String peptide = iterator.next();
									if (proteinSequence.contains(peptide)) {
										addPeptideToMultiMap(multiMap, peptide, header);
									}
								}
							} else {
								header = nextLine;
								firstline = false;
							}
							stringBf = new StringBuffer(); 		
						} else {
							stringBf.append(nextLine);
						}
					}
			reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return multiMap;
	}
	
	/**
	 * Helper method that references a peptide to multiple protein headers.
	 * @param multiMap Mapping from peptide string to multiple protein headers.
	 * @param peptide Peptide string
	 * @param header Protein header
	 */
	public static void addPeptideToMultiMap(Map<String, Set<String>> multiMap, String peptide, String header) {
		Set<String> headerSet;
		if (multiMap.get(peptide) != null) {
			headerSet = multiMap.get(peptide);

		} else {
			headerSet = new HashSet<String>();

		}
		headerSet.add(header);
		multiMap.put(peptide, headerSet);
	}
	
	
	
	public static void main(String[] args) {
		Database readDB = FastaUtilities.read("C:\\temp\\MG_Biogas_SwissProt.fasta");
		FastaUtilities.writeDatabase(readDB, "C:\\temp\\MG_Biogas.fasta");
		
//		FastaUtilities.readAndWriteDatabase("C, outFilePath, start, end);
	}
}
