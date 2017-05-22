package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import de.mpa.client.Constants;
import de.mpa.util.PropertyLoader;

public class PeptideDigester {

	private DB database;

	private Map<String, HashSet<String>> peptideToProteinMap;

	private AtomicInteger proteinNumber;
	private AtomicInteger peptideNumber;
	private AtomicInteger peptideNumberUnique;
	
	private JProgressBar progressbar = null;
	private final Logger log = Logger.getLogger(this.getClass());

	public PeptideDigester() {
        this.peptideNumberUnique = new AtomicInteger();
        this.peptideNumber = new AtomicInteger();
        this.proteinNumber = new AtomicInteger();
	}

	public PeptideDigester(JProgressBar progressbar) {
        this.peptideNumberUnique = new AtomicInteger();
        this.peptideNumber = new AtomicInteger();
        this.proteinNumber = new AtomicInteger();
        this.progressbar = progressbar;
	}

	/**
	 * Parse a peptide database to retrieve peptide to protein relations.
	 * 
	 * @param inFile
	 *            . Digested FASTA database.
	 */
	public void parsePeptideDB(String inFile) {
		try {
            this.log.info("Parsing digested FASTA >> " + inFile);
			long time = System.currentTimeMillis();
			SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");

			// Setup database and temporary file folder
			File tempDir = new File(inFile.substring(0,
					inFile.lastIndexOf(File.separator))
					+ File.separator + "temp" + File.separator);
			tempDir.mkdir();
			if (this.database != null) {
                this.database.close();
			}
            this.database = DBMaker
					.newFileDB(new File(tempDir + File.separator + "mapdata"))
					.transactionDisable().closeOnJvmShutdown()
					.deleteFilesAfterClose().mmapFileEnableIfSupported().make();
            this.peptideToProteinMap = this.database.createTreeMap("peptoprot").make();

			// Parse the peptide database file
            this.log.info("Parsing sequences and accessions ..");

			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			String line;
			while ((line = reader.readLine()) != null) {
				// Check if this is a sequence line
				if (line.trim().length() > 0 && line.startsWith(">pep|")) {
					String[] split = line.split("[|]");
					String sequence = reader.readLine().trim();
					HashSet<String> proteins = new HashSet<String>();
					for (int i = 2; i < split.length; i++) {
						proteins.add(split[i]);
					}
					// Add the data to the map
                    this.peptideToProteinMap.put(sequence, proteins);
				}
			}
            this.log.info("..  done. This took "
					+ timeformat.format(new Date(System.currentTimeMillis()
							- time)) + "min.");
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve peptide to protein information from a loaded database.
	 * 
	 * @param sequence
	 *            . The AA peptide sequence.
	 * @return Set of protein accession strings.
	 */
	public HashSet<String> getProteinsFromPeptideSequence(String sequence) {
		if (this.peptideToProteinMap.isEmpty()) {
			return null;
		} else {
			if (!this.peptideToProteinMap.containsKey(sequence)) {
				return new HashSet<String>();
			}
			return new HashSet<String>(this.peptideToProteinMap.get(sequence));
		}
	}

	/**
	 * Retrieve peptide to protein information from a given peptide database
	 * file.
	 * 
	 * @param sequence
	 *            . The AA peptide sequence.
	 * @param file
	 *            . File of the peptide FASTA.
	 * @return Set of protein accession strings.
	 */
	public static HashSet<String> fetchProteinsFromPeptideSequence(
			String inSequence, String inFile) {

		HashSet<String> proteins = new HashSet<String>();
		try {
			// select the correct .pep-file
			String correctFile = inFile.substring(0,
					inFile.lastIndexOf(File.separator))
					+ File.separator
					+ "Pep"
					+ File.separator
					+ inFile.substring(inFile.lastIndexOf(File.separator),
							inFile.length());
			// and open it
			File file = new File(correctFile + "."
					+ inSequence.subSequence(0, 2));
			//
			if (file.exists() && !file.isDirectory()) {
				// Parse the peptide database file
				BufferedReader reader = new BufferedReader(new FileReader(
						correctFile + "." + inSequence.subSequence(0, 2)));
				String line;
				while ((line = reader.readLine()) != null) {
					// Check if this is a sequence line
					String sequence;
					if (line.trim().length() > 0 && line.startsWith(">pep|")) {
						String nextline = reader.readLine();
						if (nextline != null) {
							sequence = nextline.trim();
						} else {
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
				// log.info("..  done. This took " + timeformat.format(new
				// Date(System.currentTimeMillis()-time)) + "min.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return proteins;
	}

	/**
	 * Write a concatenated in-silico database based on the given list of
	 * protein databases in FASTA format. Database is saved in multiple files
	 * based on first Amino acid of peptide sequence
	 * 
	 * @param dbFiles
	 *            . List of input database files.
	 * @param outFile
	 *            . The output database files. Add + .XX where XX are 2 first
	 *            Amino Acids for peptides
	 * @param missedCleavage
	 *            . Number of missed cleavages in in-silico digest. -1 to bypass
	 *            the digest.
	 * @param minLength
	 *            . Minimal size of AA sequences to be recorded.
	 * @param maxLength
	 *            . Maximal size of AA sequences to be recorded.
	 */
	public void createPeptidDB(String dbFile, String outFile,
			int missedCleavage, int minLength, int maxLength) {
		
		// NEW IDEA:
		// 3. TODO: Later: the number of aminoacids necassary (1, 2 ,3 or 4) should be estimated by fasta-file-size
		
		// The String contains all permissible Amino acids (including * and X)
		// Should be implemented as parameter (not hard-coded here)
		String AllAminoAcids;
		if (Constants.winOS = true) {
			AllAminoAcids = "GPAVLIMCFYWHKRQNEDSTJBOXZU_";
		} else {
			AllAminoAcids = "GPAVLIMCFYWHKRQNEDSTJBOXZU*";
		}
		
		
		
		// Constructs File extensions from all combinations of 2 Amino acids
		List<String> FileExtensions = new ArrayList<String>();
		for (char AminoAcid1 : AllAminoAcids.toCharArray()) {
			for (char AminoAcid2 : AllAminoAcids.toCharArray()) {
				FileExtensions.add(Character.toString(AminoAcid1)
						+ Character.toString(AminoAcid2));
			}
		}

		// catch IO-Exceptions
		try {
			// Setup pep-file folder
			File PepDir = new File(
						PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
							+ PropertyLoader
									.getProperty(PropertyLoader.PATH_FASTA)
							+ File.separator + "Pep" + File.separator
							+ outFile.substring(0, outFile.lastIndexOf(".")));
			PepDir.mkdir();


			// set new outFile to new path
			String corretOutFile = PepDir.getAbsolutePath() + File.separator + outFile;

			// Count the number of protein entries, for feedback
			long protein_count = 0L;
			protein_count = DigFASTAEntryParser.countEntries(dbFile);
			// feedback counter
			long prots_parsed = 0L;

			// Main loop (for multiple in-Files?)

			// Initialize BufferedReader for FASTA
			BufferedReader reader = new BufferedReader(new FileReader(new File(dbFile)));
			// Initialize BufferedReader for FASTA
			File temp_file = new File(PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_FASTA) + "prot2pep.temp");
			BufferedWriter writer_1 = new BufferedWriter(new FileWriter(temp_file));

			// String of the current line.
			String line;

			// First pass: there is only one pass (you shall not pass)
			line = reader.readLine();
			
			// parse proteins and digest
			while (line != null) {
				// Check if not an empty row and if begin of protein entry
				// For the case that the fasta contains a SINGLE empty line
				if (line.isEmpty()) {
					line = reader.readLine();
				}
				// Parse out protein Entry
				if (line.trim().length() > 0 && line.charAt(0) == '>') {
					// Get elements of the database entry
					String header = line;
					String sequence = "";
					// Add Sequence
					while ((line = reader.readLine()) != null) {
						if (line.isEmpty() || line.charAt(0) == '>') {
							break;
						} else {
							sequence += line;
						}
					}

					// Create Entry get some protein hits
					DigFASTAEntry protein_entry = DigFASTAEntryParser.parseEntry(header, sequence);
					
					// digest protein
					List<DigFASTAEntry> peptides = Arrays.asList(this.digestEntry(protein_entry, missedCleavage, minLength, maxLength));

					// write protein identifier + linebreak
					writer_1.write(">" + protein_entry.getIdentifier() + "\n");

					// write peptides (each == line)
					for (DigFASTAEntry pep : peptides) {
						writer_1.write(pep.getSequence() + "\n");
					}
				}
			}
			// Close the reader and writer
			reader.close();
			writer_1.close();
			
			int count = 0;
			// Create multiple writers/readers for 21^2 different files
			for (String AminoAcids : FileExtensions) {
				// read temp file
				BufferedReader read_1 = new BufferedReader(new FileReader(temp_file));
				// init peptide2protein map
				HashMap<String, HashSet<String>> pep2prot = new HashMap<String, HashSet<String>>();
				
				// parse and write
				line = read_1.readLine();
				while (line != null) {
					// Check if not an empty row and if begin of protein entry
					// For the case that the fasta contains a SINGLE empty line
					if (line.isEmpty()) {
						line = read_1.readLine();
					}
					// Parse out protein Entry
					if (line.trim().length() > 0 && line.charAt(0) == '>') {
						// Get elements of the database entry
						String protein = line.replace(">", "");
						String sequence = "";
						// Add Sequence
						while ((line = read_1.readLine()) != null) {
							if (line.isEmpty() || line.charAt(0) == '>') {
								break;
							} else {
								// got a new peptide, check if its good
								if (line.startsWith(AminoAcids)) {
									String pep_seq = line.trim();
									if (pep2prot.containsKey(pep_seq)) {
										pep2prot.get(pep_seq).add(protein);
									} else {
										HashSet<String> prots = new HashSet<String>();
										prots.add(protein);
										pep2prot.put(pep_seq, prots);
									}
								}
							}
						}
					}
				}
				read_1.close();
				// create correct writer
				BufferedWriter pepwriter = new BufferedWriter(new FileWriter(corretOutFile + "." + AminoAcids));
				// write peptide file
				long i = 0L;
				for (String peptide : pep2prot.keySet()) {
					i++;
					pepwriter.write(">pep|" + i);
					for (String protein : pep2prot.get(peptide)) {
						pepwriter.write("|" + protein);
					}
					pepwriter.write("\n" + peptide + "\n");
				}
				pepwriter.close();
				pep2prot = null;
				
				// peptideparser progress is from 75% to 100%
				if (progressbar != null) {
					
					int progress = (int) (75 + (count*1.0 / (FileExtensions.size())*1.0) * 25 ); 
					this.progressbar.setValue(progress);
				}
				
			}
			// delete temp_file
			temp_file.delete();
			
			// create empty .pep file (so it can be found) --> terrible :D
			String dummy_pepfile = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader
					.getProperty(PropertyLoader.PATH_FASTA) + outFile;
			BufferedWriter pepfile = new BufferedWriter(new FileWriter(
					dummy_pepfile));
			pepfile.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Digest a protein FASTA database and write the result into a temporary
	 * binary database.
	 * 
	 * @param inFiles
	 *            . The original protein FASTA file.
	 * @param missedCleavage
	 *            . Number of missed cleavages in in-silico digest. -1 to bypass
	 *            the digest.
	 * @param minLength
	 *            . Minimal size of AA sequences to be recorded.
	 * @param maxLength
	 *            . Maximal size of AA sequences to be recorded.
	 */
	public void createTemporaryPeptidDB(String inFile, int missedCleavage,
			int minLength, int maxLength) {

		long time = System.currentTimeMillis();
		SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");

		// Setup database and temporary file folder
		File tempDir = new File(inFile.substring(0,
				inFile.lastIndexOf(File.separator))
				+ File.separator + "temp" + File.separator);
		tempDir.mkdir();
        this.database = DBMaker
				.newFileDB(new File(tempDir + File.separator + "mapdata"))
				.transactionDisable().closeOnJvmShutdown()
				.deleteFilesAfterClose().mmapFileEnableIfSupported().make();
        this.peptideToProteinMap = this.database.createTreeMap("peptoprot").make();
        this.peptideNumberUnique = new AtomicInteger();
        this.peptideNumber = new AtomicInteger();
        this.proteinNumber = new AtomicInteger();

        this.log.info("Parsing and processing sequences ..");
		// Count the number of protein entries
        this.proteinNumber.set((int) (this.proteinNumber.get()
				+ DigFASTAEntryParser.countEntries(inFile)));
        this.log.info("  parsed " + this.proteinNumber.get() + " protein entries.");

		try {
			File file = new File(inFile);
			if (file.exists() && !file.isDirectory()) {
				BufferedReader reader = new BufferedReader(new FileReader(
						new File(inFile)));
				String line;
				// Number of the current entry
				long i = 0;
				// First pass to process all database sequences
				line = reader.readLine();
				while (line != null) {
					// Check if not an empty row and if begin of protein entry
					if (line.trim().length() > 0 && line.charAt(0) == '>') {
						i++;
						// Get elements of the database entry
						String header = line;
						String sequence = "";
						// Add Sequence
						while ((line = reader.readLine()) != null) {
							if (line.charAt(0) == '>') {
								break;
							} else {
								sequence += line;
							}
						}
						// Create Entry
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(
								header, sequence);

						if (missedCleavage >= 0) {
							// Process Entry
							DigFASTAEntry[] peptides = this.digestEntry(entry,
									missedCleavage, minLength, maxLength);
							for (int j = 0; j < peptides.length; j++) {
								// Note peptide to protein relation
								String pep = peptides[j].getSequence();
								if (this.peptideToProteinMap.containsKey(pep)) {
                                    this.peptideToProteinMap.get(pep).add(
											entry.getIdentifier());
								} else {
									HashSet<String> newSet = new HashSet<>();
									newSet.add(entry.getIdentifier());
                                    this.peptideToProteinMap.put(pep, newSet);
									// new sequence
                                    this.peptideNumberUnique.incrementAndGet();
								}
							}
						}
					}
				}
				// Close the reader
				reader.close();
                this.database.commit();
                this.log.info("  created " + this.peptideNumber.get()
						+ " total peptides.");
                this.log.info("  and found " + this.peptideNumberUnique.get()
						+ " unique sequences.");
                this.log.info("..  done. This took "
						+ timeformat.format(new Date(System.currentTimeMillis()
								- time)) + "min.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create in-silico peptides from protein database entry.
	 * 
	 * @param entry
	 *            . FASTA database entry.
	 * @return peptide database entry.
	 */
	private DigFASTAEntry[] digestEntry(DigFASTAEntry entry,
			int missedCleavages, int minlength, int maxLength) {

		List<String> cleavedPeptides = PeptideDigester.trypticCleave(entry.getSequence(),
				missedCleavages, minlength, maxLength);

		// Result list
		DigFASTAEntry[] results = new DigFASTAEntry[cleavedPeptides.size()];
		int i = 0;
		for (String peptide : cleavedPeptides) {
			String identifier = String.valueOf(this.peptideNumber.incrementAndGet());
			results[i] = new DigFASTAEntry(identifier, identifier, "", peptide,
					DigFASTAEntry.Type.SILICO_PEPTIDE, new ArrayList<String>());

			i++;
		}

		return results;
	}

	/**
	 * Perform tryptic cleavage on a AA sequence.
	 * 
	 * @param sequence
	 *            . sequence as String.
	 * @param missedCleavages
	 *            . number of missed cleavages.
	 * @return
	 */
	public static List<String> trypticCleave(String sequence,
			int missedCleavages, int minLength, int maxLength) {

		// Cleavage rules for enzymatic digestion by Trypsin
		String[] cleaves = sequence.split("(?<=[RK])(?!=[P])");

		// Final result object
		List<String> peptides = new ArrayList<String>(cleaves.length);

		// Add all peptide sequences with OK length
		for (int i = 0; i < cleaves.length; i++) {
			if (cleaves[i].length() >= minLength
					&& cleaves[i].length() <= maxLength) {
				peptides.add(cleaves[i]);
			}
		}

		// Loop for each number of missed cleavages
		for (int miss = 1; miss <= missedCleavages; miss++) {
			for (int i = 0; i < cleaves.length - miss; i++) {
				// Build a concatenated sequences from neighbours
				StringBuilder builder = new StringBuilder(cleaves[i]);
				for (int j = 1; j <= miss; j++) {
					builder.append(cleaves[i + j]);
					String newSequence = builder.toString();
					if (newSequence.length() >= minLength
							&& newSequence.length() <= maxLength) {
						peptides.add(newSequence);
					}
				}
			}
		}

		return peptides;
	}

	/**
	 * A map which represents a histogram of how often in-silico peptides are
	 * redundant between proteins.
	 */
	public int[] getPeptideRedundancyHistogram() {
		int[] histo = new int[1000000];
		for (Map.Entry<String, HashSet<String>> entry : this.peptideToProteinMap
				.entrySet()) {
			int count = entry.getValue().size();
			histo[count] = histo[count] + 1;
		}
		return histo;
	}
}


