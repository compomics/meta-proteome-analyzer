package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.JProgressBar;

import com.compomics.util.protein.Header;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.db.mysql.DBManager;
import de.mpa.db.mysql.accessor.ProteinAccessor;
import de.mpa.db.mysql.accessor.TaxonomyTableAccessor;
import de.mpa.db.mysql.accessor.UniprotentryAccessor;
import de.mpa.db.mysql.storager.MascotStorager;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.dbsearch.UniProtEntryMPA;
import de.mpa.util.PropertyLoader;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

/**
 * Singleton class providing FASTA read/write capabilities via random access
 * file.
 * 
 * @author Thilo Muth, K. Schallert
 */
public class FastaLoader {

	/**
	 * Counts the occurences of Uniprot webservice fails
	 */
	private static Long uniprot_webservice_fail_count = 0L;

	/**
	 * The accession-to-position map.
	 */
	// Hashcodes will not work with strings... LUCENE would be an alternative
	// for indexing of fasta files in the long run.
	private static TObjectLongMap<String> acc2pos;

	/**
	 * The random access file instance.
	 */
	private static RandomAccessFile raf;

	/**
	 * The FASTA file instance. TODO: Why is this static?
	 */
	private static File file;

	/**
	 * The Peptide FASTA instance.
	 */
	private File pepfile;
	/**
	 * The peptide database digester.
	 */
	private PeptideDigester fastaDigester;

	/**
	 * The index file.
	 */
	private static File indexFile;

	private static boolean hasChanged;

	/**
	 * Singleton object instance of the FastaLoader class.
	 */
	private static FastaLoader instance;

	/**
	 * Returns the singleton object of the FastaLoader.
	 * 
	 * @return FastaLoader object instance.
	 */
	public static FastaLoader getInstance() {
		// Lazy instantation
		if (FastaLoader.instance == null) {
			FastaLoader.instance = new FastaLoader();
		}
		return FastaLoader.instance;
	}

	/**
	 * Writes the FASTA index file to the disk.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void writeIndexFile() throws IOException, ClassNotFoundException {
		FastaLoader.indexFile = new File(FastaLoader.file.getAbsolutePath() + ".fb");

		if (FastaLoader.indexFile.exists()) {
			FileInputStream fis = new FileInputStream(FastaLoader.indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			TObjectLongHashMap<String> tempMap = (TObjectLongHashMap<String>) ois.readObject();
			fis.close();
			ois.close();

			// Add entries of old map
			FastaLoader.acc2pos.putAll(tempMap);
			tempMap.clear();
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FastaLoader.indexFile));
		oos.writeObject(FastaLoader.acc2pos);
		oos.flush();
		oos.close();
	}

	/**
	 * Read the FASTA index file and stores its contents to memory.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void readIndexFile() throws IOException, ClassNotFoundException {
		if (FastaLoader.hasChanged) {
			FileInputStream fis = new FileInputStream(FastaLoader.indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			FastaLoader.acc2pos = (TObjectLongHashMap<String>) ois.readObject();
			fis.close();
			ois.close();
		}
	}

	/**
	 * updates a map with accessions and DigFASTAEntry-Objects and adds data as
	 * necessary
	 * 
	 * 
	 * @param proteinmap.
	 *            A map containing protein hits as MascotProteinHit-objects with
	 *            protein accessions as keys
	 * @return proteinmap. The updated map is returned
	 * @throws FileNotFoundException
	 *             when the file could not be found.
	 * @author K. Schallert
	 * @throws SQLException
	 */
	public HashMap<String, MascotStorager.MascotProteinHit> updateProteinMapfromFasta(
			HashMap<String, MascotStorager.MascotProteinHit> proteinmap) throws FileNotFoundException, SQLException {
		// Name and directory of the new fasta
		// Initialize the buffered reader
		BufferedReader br = new BufferedReader(new FileReader(this.getFile()));
		// The line in the *.fasta file
		String line;
		// The fasta sequence
		String sequence = "";
		try {
			// Start the parsing of the FASTA file
			line = br.readLine();
			// Check if not an empty row occur and if begin of a new protein
			// entry
			if (line.trim().length() > 0 && line.charAt(0) == '>') {
				// Get elements of the database entry
				String header = line;
				// Add Sequence
				while ((line = br.readLine()) != null) {
					// Check whether a new entry starts
					if (line.trim().length() > 0 && line.charAt(0) == '>') {
						// Get parsed fasta-entry and write it to the new fasta
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
						// update the mascotproteinhit with sequence,
						// description and database type data
						MascotStorager.MascotProteinHit m_hit = proteinmap.get(entry.getIdentifier());
						// but only if found in the mascot dat file
						if (m_hit != null) {
							m_hit.setDatabaseType(entry.getType());
							m_hit.setDescription(entry.getDescription());
							m_hit.setSequence(entry.getSequence());
						}

						// Reset the sequence
						sequence = "";
						// Add new header
						header = line;
					} else {
						sequence += line;
					}
				}
				// save last entry
				DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
				// update the mascotproteinhit with sequence, description and
				// database type data
				MascotStorager.MascotProteinHit m_hit = proteinmap.get(entry.getIdentifier());
				// but only if found in the mascot dat file
				if (m_hit != null) {
					m_hit.setDatabaseType(entry.getType());
					m_hit.setDescription(entry.getDescription());
					m_hit.setSequence(entry.getSequence());
				}
				br.close();
			} else {
				System.err.println("ERROR IN FASTA FORMAT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// conn.close();
		return proteinmap;
	}

	/**
	 * Loads the FASTA file by random access and maps accessions of found
	 * protein blocks to their respective byte positions in the file.
	 * 
	 * @param file
	 *            The FASTA file.
	 * @throws FileNotFoundException
	 *             when the file could not be found.
	 */
	public void loadFastaFile() throws FileNotFoundException {

		// Instance of the client to fire progress
		// Client client = Client.getInstance();

		try {
			// Initialize the random access file instance
			FastaLoader.raf = new RandomAccessFile(FastaLoader.file, "r");

			// Initialize index maps
			FastaLoader.acc2pos = new TObjectLongHashMap<String>();

			// Get the first position at the beginning of the file
			Long pos = FastaLoader.raf.getFilePointer();
			// Iterate FASTA file line by line
			String line;
			while ((line = FastaLoader.raf.readLine()) != null) {
				// Check for header
				if (!line.isEmpty() && line.startsWith(">")) {
					// Parse header
					Header header = Header.parseFromFASTA(line);
					// Add map entry
					FastaLoader.acc2pos.put(header.getAccession(), pos);
				} else {
					// End of the sequence part == Start of a new header
					pos = FastaLoader.raf.getFilePointer();
				}
			}
			this.writeIndexFile();
			// raf.close(); // Has to stay open for further methods
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the accession-to-position index map.
	 * 
	 * @return indexMap The index map.
	 */
	public TObjectLongMap<String> getIndexMap() {
		return FastaLoader.acc2pos;
	}

	/**
	 * Sets the FASTA file.
	 * 
	 * @param file
	 *            The FASTA file
	 */
	public void setFastaFile(File file) {
		this.file = file;
		// reset map on change of FASTA file
		acc2pos = null;
		// include a in-silico peptide file
		String filestring = file.getAbsolutePath();
		filestring = filestring.substring(0, filestring.lastIndexOf('.')) + ".pep";
		pepfile = new File(filestring.substring(0, filestring.lastIndexOf('.')) + ".pep");
		if (!this.pepfile.exists() || this.pepfile.isDirectory()) {
			this.pepfile = null;
		}
	}

	/**
	 * Sets the peptide FASTA file.
	 * 
	 * @param file
	 *            The peptide FASTA file
	 */
	public void setPepFile(File file) {
		pepfile = file;
	}

	/**
	 * Returns the current in-silico peptide file if available
	 * 
	 * @return pepFile the current peptide file.
	 */
	public File getPepFile() {
		return this.pepfile;
	}

	/**
	 * Returns the active protein FASTA file
	 * 
	 * @return file the active protein FASTA file.
	 */
	public File getFile() {
		return FastaLoader.file;
	}

	/**
	 * Loads the designated in-silico digested peptide database file.
	 */
	public void loadPepFile() {
		if (this.pepfile != null) {
			this.fastaDigester = new PeptideDigester();
			this.fastaDigester.parsePeptideDB(this.pepfile.getAbsolutePath());
		}
	}

	/**
	 * Get all protein hits for a peptide sequence in the digested database.
	 */
	public HashSet<String> getProtHits(String sequeString) {
		if (this.pepfile == null) {
			return new HashSet<String>();
		}
		return this.fastaDigester.fetchProteinsFromPeptideSequence(sequeString, this.pepfile.getAbsolutePath());
	}

	/**
	 * Returns the current index file instance.
	 * 
	 * @return indexFile
	 */
	public File getIndexFile() {
		return FastaLoader.indexFile;
	}

	/**
	 * Sets the current index file instance.
	 * 
	 * @param indexFile
	 *            The current index file.
	 */
	public void setIndexFile(File indexFile) {
		// Compare two file paths
		if (!indexFile.equals(this.indexFile)) {
			this.indexFile = indexFile;
			FastaLoader.hasChanged = true;
			// reset map on change of index file
			acc2pos = null;
		}
	}

	/**
	 * Utility method to load a specified FASTA file by hand.
	 * 
	 * @param args
	 *            String argument containing the path pointing to a FASTA file.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("No file provided.");
		} else {
			File file = new File(args[0]);
			FastaLoader fastaLoader = getInstance();
			fastaLoader.setFastaFile(file);
			try {
				fastaLoader.loadFastaFile();
				fastaLoader.writeIndexFile();
			} catch (Exception e) {
				System.err.println("aborted.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Methode which reads ONE *.fasta and creates an MPA compatible *.fasta.
	 * 
	 * @param fastaFile.
	 *            File of the input *.fastas.
	 * @param outpath.
	 *            The output *.fasta file.
	 * @param batchSize.
	 *            Number of entries which should be stored in by one query
	 * @param mascotFlag.
	 *            Flag for the creation of a new Fasta add the specified
	 *            direction.
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void addFastaDatabases(File fastaFile, String dbName, int batchSize, JProgressBar progressbar)
			throws IOException, SQLException {

		FastaLoader.uniprot_webservice_fail_count = 0L;
		// Instance of the Buffered Reader
		BufferedReader br = null;

		// Name and directory of the new fasta
		File outputFastaFile = new File(Constants.FASTA_PATHS + dbName + ".fasta");

		// Open buffered writer to write new database.
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFastaFile));

		// Entry number
		long entryNo = 0;

		// Create a Protein entry in the database
		Connection conn = DBManager.getInstance().getConnection();

		// Get the filename as prefix for the identifier
		String filename = fastaFile.getName().toString();
		String filePath = fastaFile.getAbsolutePath();

		// Get just the name of the FASTA
		filename = filename.split("[.]")[0];

		// Get total number of FASTA entries
		Long totalCountEntries = DigFASTAEntryParser.countEntries(filePath);

		// Initialize the buffered reader
		br = new BufferedReader(new FileReader(fastaFile));

		// The line in the *.fasta file
		String line;
		// The fasta sequence
		String sequence = "";

		// Initialize a list of FASTA entries to store them as bulk
		ArrayList<DigFASTAEntry> fastaEntryList = new ArrayList<DigFASTAEntry>();
		Client.getInstance().firePropertyChange("new message", null, "READING FASTA FILE " + fastaFile);
		// Start the parsing of the FASTA file
		line = br.readLine();
		// Check if first line is formatted correctly
		if (line.trim().length() > 0 && line.charAt(0) == '>') {

			// Get started
			String header = line;
			// loop until end of file
			while (line != null) {
				// loop to read sequence
				line = br.readLine();
				while (true) {
					if ((line == null) || (line.startsWith(">"))) {
						break;
					} else {
						sequence += line.trim();
						// read next line
						line = br.readLine();
					}
				}

				// Create an entry
				// increase entry number
				entryNo = entryNo + 1;
				// Get parsed fasta-entry and write it to the new fasta
				DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
				FastaLoader.writeEntry(bw, "", entry);
				// add protein to list
				fastaEntryList.add(entry);
				// Reset the sequence
				sequence = "";
				// Add new header
				header = line;

				// do a batch of proteins, redundancy is handled by
				// unique-accession property of protein table
				if (entryNo % batchSize == 0 || line == null) {
					ProteinAccessor.addMutlipleProteinsToDatabase(fastaEntryList, conn);
					// Reset the fastaEntryList
					fastaEntryList = new ArrayList<DigFASTAEntry>();
					
					// first 25% is the DB-stuff
					int progress = (int) ((entryNo*1.0 / totalCountEntries*1.0) * 25);
					progressbar.setValue(progress);
					progressbar.setString(progress+ "%");
				}
			}
		} else {
			System.err.println("Fasta File is formatted wrong");
		}
		
		Client.getInstance().firePropertyChange("new message", null, "SAVING PROTEINS TO DB");
		
		progressbar.setValue(25);
		progressbar.setString("25%");
		ArrayList<Long> empty_up = ProteinAccessor.find_uniprot_proteins_without_upentry(conn);
		FastaLoader.createNewUniprotEntries(empty_up, conn, progressbar);
		empty_up = ProteinAccessor.find_uniprot_proteins_without_upentry(conn);
		
		for (Long protid : empty_up) {
			ProteinAccessor protein = ProteinAccessor.findFromID(protid, conn);
			protein.delete(conn);
			FastaLoader.uniprot_webservice_fail_count++;
		}
		conn.commit();
		br.close();
		bw.flush();
		bw.close();

		// Add permissions to the new *.fasta file
		outputFastaFile.setExecutable(true);
		outputFastaFile.setReadable(true);
		outputFastaFile.setWritable(true);

		// Create a *.fasta for the mascot searches in the specified directory
		// XXX: this should be handled differently
		// if (mascotFlag) {
		// Files.copy(outputFastaFile.toPath(), outpath.toPath(),
		// StandardCopyOption.COPY_ATTRIBUTES);
		// // Client.getInstance().firePropertyChange("new message", null,
		// "Creating fasta copy for Mascot");
		// }

		// fasta formatter script is progress from 50% to 75%
		progressbar.setValue(50);
		progressbar.setString("50%");
		Client.getInstance().firePropertyChange("new message", null, "CREATING DECOY DATABASES");
		// Runs the fastaformater script
		RunFastaFormater fastaFromater = new RunFastaFormater();
		fastaFromater.formatFastaDatabase(outputFastaFile.toPath().toString());

		// fasta formatter script is progress from 50% to 75%
		progressbar.setValue(75);
		progressbar.setString("75%");
		
		// create peptide fasta
		String pep_out = "";
		if (Constants.winOS == true) {
			pep_out = outputFastaFile.getAbsolutePath().substring(outputFastaFile.getAbsolutePath().lastIndexOf("\\"),
					outputFastaFile.getAbsolutePath().lastIndexOf(".")) + ".pep";
		} else {
			pep_out = outputFastaFile.getAbsolutePath().substring(outputFastaFile.getAbsolutePath().lastIndexOf("/"),
					outputFastaFile.getAbsolutePath().lastIndexOf(".")) + ".pep";
		}
		
		Client.getInstance().firePropertyChange("new message", null, "CREATING PEPTIDE DATABASE");
		PeptideDigester digester = new PeptideDigester(progressbar);
		digester.createPeptidDB(outputFastaFile.getAbsolutePath(), pep_out, 1, 5, 50);
		
		// very last step
		// Add *.fasta filename to the client settings.
		FastaLoader.addFastaTotheClientSetting(dbName);
	}

	/**
	 * Method to create for a group of FASTA entries the protein and uniProt
	 * table
	 * 
	 * @param fastaEntryList.
	 *            The list of all parsed FASTA entries
	 * @param conn
	 *            - SQL database connection
	 * @throws SQLException
	 */
	private static void createNewUniprotEntries(ArrayList<Long> proteinid_List, Connection conn, JProgressBar progressbar) throws SQLException {
		
		// TODO: catch number format exception and figure out source of this bug

		// init stuff
		HashMap<String, Long> protein_acc2id_mapping = new HashMap<String, Long>();
		HashMap<String, ProteinAccessor> protein_acc2prot_map = new HashMap<String, ProteinAccessor>();
		UniProtUtilities utils = new UniProtUtilities();
		TreeMap<String, UniProtEntryMPA> uniprotentries = null;
		// this set is filled up to 200 entries and then processed
		HashSet<String> accessions = new HashSet<String>();
		int count = 0;

		// cylce list, processing every 16 (prvsly: 200) entries
		for (Long protid : proteinid_List) {
			// get proteinentry and make mappings
			ProteinAccessor curr_protein = ProteinAccessor.findFromID(protid, conn);
			String this_acc = curr_protein.getAccession();
			protein_acc2id_mapping.put(this_acc, protid);
			protein_acc2prot_map.put(this_acc, curr_protein);
			accessions.add(this_acc);
			count++;
			// time for processing
			if ((accessions.size() > 16) || (count == proteinid_List.size())) {
				try {
					uniprotentries = utils.processBatch(accessions, true);
				} catch (Exception e) {
					// try again
					try {
						uniprotentries = utils.processBatch(accessions, true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if (uniprotentries != null) {
					for (String protein : uniprotentries.keySet()) {
						// prepare
						UniProtEntryMPA up_entry = uniprotentries.get(protein);
						ProteinAccessor prot_entry = protein_acc2prot_map.get(protein);
						// create/add stuff to sql-objects
						if(TaxonomyTableAccessor.taxIDExists(conn, up_entry.getTaxid())){
							Long upid = UniprotentryAccessor.addProtein(up_entry, conn);
							prot_entry.setFK_uniProtID(upid);
							try {
								prot_entry.update(conn);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}						
					}
					// commit all 200 together
					conn.commit();
					// save commit success (upids??)
				}
				// clear maps
				accessions.clear();
				protein_acc2id_mapping.clear();
				protein_acc2prot_map.clear();
			}
			
			// progress from 25% to 50%
			int progress = (int) (25 + (count*1.0 / (proteinid_List.size()*1.0)) * 25);
			progressbar.setValue(progress);
			progressbar.setString(progress + "%");
		}
	}

	

	/**
	 * Write a FASTA database entry.
	 * 
	 * @param bw.
	 *            BufferedWriter that writes the data.
	 * @throws IOException
	 */
	public void writeEntry(BufferedWriter bw, DigFASTAEntry fastaEntry) throws IOException {
		FastaLoader.writeEntry(bw, "", fastaEntry);
	}

	/**
	 * Write a FASTA database entry.
	 * 
	 * @param bw.
	 *            BufferedWriter that writes the data.
	 * @param prefix,
	 *            Prefix for each identifier
	 * @throws IOException
	 */
	public static void writeEntry(BufferedWriter bw, String prefix, DigFASTAEntry fastaEntry) throws IOException {

		// Container for the sequence
		String sequenceContainer;
		sequenceContainer = fastaEntry.getSequence();
		// Keep database Format
		// TODO: FIX NCBI ENTRIES
		if (fastaEntry.getType().equals(DigFASTAEntry.Type.UNIPROTSPROT)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(1));
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.UNIPROTTREMBL)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(1));
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.NCBIGENBANK)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(3));
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.NCBIREFERENCE)) {
			// TODO: FIX NCBI ENTRIES
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
//			bw.write("|" + fastaEntry.getSubHeader().get(2));
//			bw.write(getType().dbStartFlag + prefix + "_" + getIdentifier());
//			bw.write("|" + getSubHeader().get(3));
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.Database)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + "Metagenome unknown");
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.SILICO_PEPTIDE)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.METAGENOME1)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + "Metagenome unknown");
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.METAGENOME2)) {
			bw.write(">generic|" + fastaEntry.getIdentifier());
			bw.write("|" + "Metagenome unknown");
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.METAGENOME3)) {
			bw.write(">generic|" + prefix + fastaEntry.getIdentifier());
			bw.write("|" + "Metagenome unknown");
		} else if (fastaEntry.getType().equals(DigFASTAEntry.Type.METAGENOME4)) {
			bw.write(">generic|" + prefix + fastaEntry.getIdentifier());
			bw.write("|" + "Metagenome unknown");
		}
		bw.newLine();
		
		// Makes a linebreak each 80 chars
		while (sequenceContainer.length() > 80) {
			bw.append(sequenceContainer.subSequence(0, 79));
			bw.newLine();
			sequenceContainer = sequenceContainer.substring(79);
		}
		bw.append(sequenceContainer);
		bw.newLine();
	}

	/**
	 * Method to add new *.fasta to the client settings
	 * 
	 * @param fastaFile
	 * @throws IOException
	 */
	public static void addFastaTotheClientSetting(String fastaFile) throws IOException {

		// Name of the new *.fasta
		String newFastaFileName = fastaFile.split("[.]")[0];

		// Client settings file
		String clientSettingsFileServer = null;
		String clientSettingsFile = null;

		clientSettingsFileServer = PropertyLoader.getProperty("base_path") + PropertyLoader.getProperty("path.fasta")
				+ File.separator + PropertyLoader.getProperty("file.fastalist");
		// Get old parameter dialog
		BufferedReader br = new BufferedReader(new FileReader(new File(clientSettingsFileServer)));
		String header = br.readLine();
		String fastaFiles = br.readLine();
		br.close();

		// Write new parameter dialog with new fasta.
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(clientSettingsFileServer)));
		bw.append(header);
		bw.newLine();
		bw.append(fastaFiles + "," + newFastaFileName);
		bw.flush();
		bw.close();
	}
}
