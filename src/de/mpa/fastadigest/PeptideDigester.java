package de.mpa.fastadigest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import de.mpa.db.job.JobStatus;
import de.mpa.db.job.instances.CommonJob;
import de.mpa.fastadigest.DigFASTAEntry.Type;
import de.mpa.webservice.Message;

public class PeptideDigester {
	
	private DB database = null;
	
	private Map<String,HashSet<String>> peptideToProteinMap = null;
	
	private AtomicInteger proteinNumber;
	private AtomicInteger peptideNumber;
	private AtomicInteger peptideNumberUnique;
	
	private Logger log = Logger.getLogger(getClass());
	
	public PeptideDigester() {
		
		peptideNumberUnique = new AtomicInteger();
		peptideNumber = new AtomicInteger();
		proteinNumber = new AtomicInteger();
	}
	
	/**
	 * Parse a peptide database to retrieve peptide to protein relations.
	 * @param inFile. Digested FASTA database.
	 */
	public void parsePeptideDB(String inFile) {
		try {
			
			log.info("Parsing digested FASTA >> "+inFile);
			long time = System.currentTimeMillis();
			SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");   
			
			// Setup database and temporary file folder
			File tempDir = new File(inFile.substring(0, inFile.lastIndexOf(File.separator))
					+File.separator+"temp"+File.separator);
			tempDir.mkdir();
			if (database != null) {
				database.close();
			}
			database = DBMaker.newFileDB(new File(tempDir+File.separator+"mapdata"))
					.transactionDisable()
					.closeOnJvmShutdown()
					.deleteFilesAfterClose()
					.mmapFileEnableIfSupported()
					.make();		
			peptideToProteinMap = database.createTreeMap("peptoprot").make();
			
			// Parse the peptide database file
			log.info("Parsing sequences and accessions ..");
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			String line;
			while ((line = reader.readLine()) != null) {
				// Check if this is a sequence line
				if (line.trim().length() > 0 && line.startsWith(">pep|") ){
					String[] split = line.split("[|]");
					String sequence = reader.readLine().trim();
					HashSet<String> proteins = new HashSet<String>();
					for (int i = 2; i < split.length; i++) {
						proteins.add(split[i]);
					}
					// Add the data to the map
					peptideToProteinMap.put(sequence, proteins);
				}
			}
			log.info("..  done. This took " + timeformat
					.format(new Date(System.currentTimeMillis()-time)) + "min.");
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve peptide to protein information from a loaded database.
	 * @param sequence. The AA peptide sequence.
	 * @return Set of protein accession strings.
	 */
	public HashSet<String> getProteinsFromPeptideSequence(String sequence) {
		if (peptideToProteinMap.isEmpty()) {
			return null;
		} else {
			if (!peptideToProteinMap.containsKey(sequence)) {
				return new HashSet<String>();
			}
			return new HashSet<String>(peptideToProteinMap.get(sequence));
		}
	}
	
	/**
	 * Retrieve peptide to protein information from a given peptide database file.
	 * @param sequence. The AA peptide sequence.
	 * @param file. File of the peptide FASTA.
	 * @return Set of protein accession strings.
	 */
	public static HashSet<String> fetchProteinsFromPeptideSequence(String inSequence, String inFile) {
		
		
		HashSet<String> proteins = new HashSet<String>();
		try {
			// select the correct .pep-file			
			String correctFile = inFile.substring(0, inFile.lastIndexOf(File.separator)) 
							 + File.separator + "Pep" + File.separator   
							 + inFile.substring(inFile.lastIndexOf(File.separator), inFile.length());
			// and open it
			File file = new File(correctFile + "." + inSequence.subSequence(0, 2));
			// 
			if (file.exists() && !file.isDirectory()) {				
				// Parse the peptide database file
				BufferedReader reader = new BufferedReader(new FileReader(correctFile + "." + inSequence.subSequence(0, 2)));
				String line;
				while ((line = reader.readLine()) != null) {
					// Check if this is a sequence line
					String sequence;
					if (line.trim().length() > 0 && line.startsWith(">pep|") ){
						String nextline = reader.readLine();
						if (nextline != null) { 
							sequence = nextline.trim();
						}
						else {
							sequence = "";
						}
						if (!sequence.equals(inSequence)) {
							continue;
						}
						String[] split = line.split("[|]");
						for (int i = 2; i < split.length; i++) {
							proteins.add(split[i]);
						}
					}
				}
				reader.close();
//				log.info("..  done. This took " + timeformat.format(new Date(System.currentTimeMillis()-time)) + "min.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return proteins;	
	}
	
	/**
	 * Write a concatenated in-silico database based on the given list of protein databases in FASTA format.
	 * Database is saved in multiple files based on first Amino acid of peptide sequence
	 * @param dbFiles. List of input database files.
	 * @param outFile. The output database files. Add + .XX where XX are 2 first Amino Acids for peptides
	 * @param missedCleavage. Number of missed cleavages in in-silico digest. -1 to bypass the digest.
	 * @param minLength. Minimal size of AA sequences to be recorded.
	 * @param maxLength. Maximal size of AA sequences to be recorded.
	 */
	public void createPeptidDB(String[] dbFiles, String outFile, int missedCleavage, int minLength, int maxLength) {
		
		// The String contains all permissible Amino acids (including * and X)
		// Should be implemented as parameter (not hard-coded here)  
		String AllAminoAcids = "GPAVLIMCFYWHKRQNEDSTJBOXZU*";
				
		// Constructs File extensions from all combinations of 2 Amino acids
		List<String> FileExtensions = new ArrayList<String>(); 
		for(char AminoAcid1 : AllAminoAcids.toCharArray()) {		
			for(char AminoAcid2 : AllAminoAcids.toCharArray()) {				
				FileExtensions.add(Character.toString(AminoAcid1) + Character.toString(AminoAcid2));				
			}								
		}			
		// catch IO-Exceptions
		try {
			// logging and time-keeping
			log.info("Creating digested FASTA >> " + outFile);
			long time = System.currentTimeMillis();
			SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");    

			// outFile should be implemented on higher level using a path and a filename instead of both combined
			// Setup pep-file folder
			File PepDir = new File(outFile.substring(0, outFile.lastIndexOf(File.separator))
					                + File.separator + "Pep" + File.separator);
			PepDir.mkdir();
			// create empty .pep file (so it can be found) --> terrible
			BufferedWriter pepfile = new BufferedWriter(new FileWriter(outFile));
			pepfile.close();
			// set new outFile to new path --> this is pretty ugly
			String corretOutFile = PepDir.getAbsolutePath() + outFile.substring(outFile.lastIndexOf(File.separator), outFile.lastIndexOf(".")) + ".pep";
			
			// Setup database and temporary file folder
			File tempDir = new File(corretOutFile.substring(0, corretOutFile.lastIndexOf(File.separator))
					                + File.separator + "temp" + File.separator);
			tempDir.mkdir();
			File tempFile = File.createTempFile("FASTA", ".tmp", tempDir);
			database = DBMaker.newFileDB(new File(tempDir+File.separator+"mapdata"))
					.transactionDisable()
					.closeOnJvmShutdown()
					.deleteFilesAfterClose()
					.mmapFileEnableIfSupported()
					.make();		
			peptideToProteinMap = database.createTreeMap("peptoprot").make();
			peptideNumberUnique = new AtomicInteger();
			peptideNumber = new AtomicInteger();
			proteinNumber = new AtomicInteger();
			log.info("Parsing and processing sequences ..");

			
			// Count the number of protein entries
			for (String inFile : dbFiles) {
				proteinNumber.set(proteinNumber.get() + DigFASTAEntryParser.countEntries(inFile));
			}
			log.info("  parsed "+proteinNumber.get() + " protein entries.");
			
			
			// Initialize List of BufferedWriters
			List<BufferedWriter> writer = new ArrayList<BufferedWriter>();
			// Create multiple writers for all different temp files
			for(String AminoAcids : FileExtensions) {		
				BufferedWriter w = new BufferedWriter(new FileWriter(tempFile + "." + AminoAcids));
				writer.add(w);				
			}	
			
			// Main loop (for multiple in-Files?)
			for (String inFile : dbFiles) {
				
				// Initialize BufferedReader for FASTA
				BufferedReader reader = new BufferedReader(new FileReader(new File(inFile)));				
				// String of the current line.
				String line;
				// Number of the current entry
				long i = 0;
				// First pass to process all database sequences
				line = reader.readLine();
				int count = 0;
				while (line != null) {
					// Check if not an empty row and if begin of protein entry
					// For the case that the fasta contains an empty line
					if (line.isEmpty()) {
						line = reader.readLine();
					}
					// Parse out peptide Entry
					if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
						//System.out.println("digested Entry number: " + i);
						i++;
						// Get elements of the database entry
						String header = line;
						String sequence = "";
						// Add Sequence
						while((line = reader.readLine()) != null){
								if (line.isEmpty() || line.charAt(0) == '>') {
									break;
								}else{
									sequence+=line;
								}
						}
						// Create Entry
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence, i);					
						// Process Entry
						DigFASTAEntry[] peptides = digestEntry(entry,
								missedCleavage,minLength,maxLength);
						for (int j = 0; j < peptides.length; j++) {
							// Note peptide to protein relation
							String pep = peptides[j].getSequence();
							if (peptideToProteinMap.containsKey(pep)) {
								peptideToProteinMap.get(pep).add(entry.getIdentifier());
							} else {
								HashSet<String> newSet = new HashSet<>();
								newSet.add(entry.getIdentifier());
								peptideToProteinMap.put(pep, newSet);
								// generate correct index for file
								int AA;
								if (pep.length() != 1) {
									AA = FileExtensions.indexOf(pep.subSequence(0, 2));
								}
								else {
									// Only 1 Amino Acid exception --> uses file extension with this AA twice 
									AA = FileExtensions.indexOf(Character.toString(pep.charAt(0)) + Character.toString(pep.charAt(0)));									
								}
								// new sequence - write the entry to corresponding file
								peptides[j].writeEntry(writer.get(AA));								
								peptideNumberUnique.incrementAndGet();
								count++;
								if(count % 10000 == 0) {						
									System.out.println(count + " sequences parsed...");
//									client.firePropertyChange("new message", null, "Parsing a fasta file" + count );
								} 	
								
							}
						}
					
					}
				}
				// Close the reader
				reader.close();
			}
			log.info("Finished writing peptide database");
			// Close the writers
			for(String AminoAcids : FileExtensions) {
				int index = FileExtensions.indexOf(AminoAcids);
				writer.get(index).close();
			}
			database.commit();
			
			// Second pass to write protein identifiers
			log.info("  created "+peptideNumber.get()+" total peptides.");
			log.info("  and found "+peptideNumberUnique.get()+" unique sequences.");
			log.info("..  done. This took " + timeformat.format(new Date(System.currentTimeMillis()-time)) + "min.");
			time = System.currentTimeMillis();
			log.info("Adding protein identifiers to entries.");

			// Initialize List of BufferedWriter
			List<BufferedWriter> finalwriter = new ArrayList<BufferedWriter>();
			// Create multiple writers/readers for 21 different files
			for(String AminoAcids : FileExtensions) {			
				BufferedWriter w = new BufferedWriter(new FileWriter(corretOutFile + "." + AminoAcids));
				finalwriter.add(w);	
			}
			// loop through all temp-files for respective Amino Acid 
			for(String AminoAcids : FileExtensions) {
				// 
				log.info("Writing Database-File for " +  AminoAcids);
				// open correct temp-file
				BufferedReader tempreader = new BufferedReader(new FileReader(tempFile + "." + AminoAcids));							
				
				// initialize variables
				int AA = 0;
				String line;
				// loop through all lines
				while ((line = tempreader.readLine()) != null) {
					// Check if this is a sequence line
					if (line.trim().length() > 0 && line.charAt(0) == '>' ){
						String sequence = tempreader.readLine().trim();
						HashSet<String> proteins = peptideToProteinMap.get(sequence);
						// generate correct index for file
						if (sequence.length() != 1) {
							AA = FileExtensions.indexOf(sequence.subSequence(0, 2));
						}
						else {
							// Only 1 Amino Acid exception --> uses file extension with this AA twice 
							AA = FileExtensions.indexOf(Character.toString(sequence.charAt(0)) + Character.toString(sequence.charAt(0)));									
						}						
						// Append all protein identifiers to the peptide header
						StringBuilder builder = new StringBuilder();
						builder.append(line);
						for (String prot : proteins) {
							builder.append("|"+prot);
						}
						finalwriter.get(AA).append(builder.toString());
						finalwriter.get(AA).append("\n");
						finalwriter.get(AA).append(sequence);
					} else {
						finalwriter.get(AA).append(line);
					}
					finalwriter.get(AA).append("\n");								
				}
				// BUG: Delete Temp-File doesnt work 
				tempreader.close();
				tempFile.delete();
			}
			
			// Close and finish.
			for(String AminoAcids : FileExtensions) { 
				int index = FileExtensions.indexOf(AminoAcids);
				finalwriter.get(index).close();	
			}
			// delete Temp-Folder
			tempDir.delete();
			log.info("..  done. This took " + timeformat.format(new Date(System.currentTimeMillis()-time)) + "min.");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Digest a protein FASTA database and write the result into a temporary binary database.
	 * @param inFiles. The original protein FASTA file.
	 * @param missedCleavage. Number of missed cleavages in in-silico digest. -1 to bypass the digest.
	 * @param minLength. Minimal size of AA sequences to be recorded.
	 * @param maxLength. Maximal size of AA sequences to be recorded.
	 */
	public void createTemporaryPeptidDB(String inFile, int missedCleavage, int minLength, int maxLength) {
		
			long time = System.currentTimeMillis();
			SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");    
			
			// Setup database and temporary file folder
			File tempDir = new File(inFile.substring(0, inFile.lastIndexOf(File.separator))
					+File.separator+"temp"+File.separator);
			tempDir.mkdir();
			database = DBMaker.newFileDB(new File(tempDir+File.separator+"mapdata"))
					.transactionDisable()
					.closeOnJvmShutdown()
					.deleteFilesAfterClose()
					.mmapFileEnableIfSupported()
					.make();		
			peptideToProteinMap = database.createTreeMap("peptoprot").make();		
			peptideNumberUnique = new AtomicInteger();
			peptideNumber = new AtomicInteger();
			proteinNumber = new AtomicInteger();
			
			log.info("Parsing and processing sequences ..");
			// Count the number of protein entries
			proteinNumber.set(proteinNumber.get()+DigFASTAEntryParser.countEntries(inFile));
			log.info("  parsed "+proteinNumber.get()+" protein entries.");
			
			try {
				File file = new File(inFile);
				if (file.exists() && !file.isDirectory()) {
					BufferedReader reader = new BufferedReader(new FileReader(new File(inFile)));
					String line;
					// Number of the current entry
					long i = 0;
					// First pass to process all database sequences
					line = reader.readLine();
					while (line != null) {
						// Check if not an empty row and if begin of protein entry
						if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
							i++;
							// Get elements of the database entry
							String header 		= line;
							String sequence = "";
							// Add Sequence
							while((line=reader.readLine()) != null){
									if (line.charAt(0) == '>') {
										break;
									}else{
										sequence+=line;
									}
							}
							// Create Entry
							DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence, i);
							
							if (missedCleavage >= 0) {
								// Process Entry
								DigFASTAEntry[] peptides = digestEntry(entry,
										missedCleavage,minLength,maxLength);
								for (int j = 0; j < peptides.length; j++) {
									// Note peptide to protein relation
									String pep = peptides[j].getSequence();
									if (peptideToProteinMap.containsKey(pep)) {
										peptideToProteinMap.get(pep).add(entry.getIdentifier());
									} else {
										HashSet<String> newSet = new HashSet<>();
										newSet.add(entry.getIdentifier());
										peptideToProteinMap.put(pep, newSet);
										// new sequence
										peptideNumberUnique.incrementAndGet();
									}
								}
							}
						}
					}
					// Close the reader
					reader.close();
					database.commit();
					log.info("  created "+peptideNumber.get()+" total peptides.");
					log.info("  and found "+peptideNumberUnique.get()+" unique sequences.");
					log.info("..  done. This took " + timeformat
							.format(new Date(System.currentTimeMillis()-time)) + "min.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

	/**
	 * Create in-silico peptides from protein database entry.
	 * @param entry. FASTA database entry.
	 * @return peptide database entry.
	 */
	private DigFASTAEntry[] digestEntry(DigFASTAEntry entry, int missedCleavages, int minlength, int maxLength) {
		
	    List<String> cleavedPeptides = trypticCleave(entry.getSequence(),missedCleavages,minlength,maxLength);
		
		// Result list
		DigFASTAEntry[] results = new DigFASTAEntry[cleavedPeptides.size()];
		int i = 0;
		for (String peptide : cleavedPeptides) {
			String identifier = String.valueOf(peptideNumber.incrementAndGet());
			results[i] = new DigFASTAEntry(identifier, "pep|"+identifier, 
					peptide, Type.SILICO_PEPTIDE, new ArrayList<String>());
			i++;
		}
		
		return results;
	}

	/**
	 * Perform tryptic cleavage on a AA sequence.
	 * @param sequence. sequence as String.
	 * @param missedCleavages. number of missed cleavages.
	 * @return
	 */
	public static List<String> trypticCleave(String sequence, int missedCleavages, int minLength, int maxLength) {
		
		// Cleavage rules for enzymatic digestion by Trypsin 
		String[] cleaves = sequence.split("(?<=[RK])(?!=[P])");
		
		// Final result object
		List<String> peptides = new ArrayList<String>(cleaves.length);
		
		// Add all peptide sequences with OK length
		for (int i = 0; i < cleaves.length; i++) {
			if ( cleaves[i].length()>=minLength &&
					cleaves[i].length()<=maxLength ) {
				peptides.add(cleaves[i]);
			}
		}
		
		// Loop for each number of missed cleavages
		for (int miss = 1; miss <= missedCleavages; miss++) {
			for (int i = 0; i < cleaves.length-miss; i++) {
				// Build a concatenated sequences from neighbours
				StringBuilder builder = new StringBuilder(cleaves[i]);
				for (int j = 1; j <= miss; j++) {
					builder.append(cleaves[i+j]);
					String newSequence = builder.toString();
					if ( newSequence.length()>=minLength &&
							newSequence.length()<=maxLength ) {
						peptides.add(newSequence);
					}
				}
			}
		}
		
		return peptides;
	}
	
	/**
	 * A map which represents a histogram of how often in-silico
	 * peptides are redundant between proteins.
	 */
	public int[] getPeptideRedundancyHistogram() {
		int[] histo = new int[1000000];
		for (java.util.Map.Entry<String, HashSet<String>> entry: peptideToProteinMap.entrySet()) {
			int count = entry.getValue().size();
			histo[count] = histo[count]+1;
		}
		return histo;
	}
	
}
