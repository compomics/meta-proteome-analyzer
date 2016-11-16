package de.mpa.io.fasta;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.compomics.util.protein.Header;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.UniprotentryAccessor;
import de.mpa.db.storager.MascotStorager.MascotProteinHit;
//import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
//import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import de.mpa.io.fasta.DigFASTAEntry.Type;
import de.mpa.main.Starter;

/**
 * Singleton class providing FASTA read/write capabilities via random access
 * file.
 * 
 * @author Thilo Muth, K. Schallert
 */
public class FastaLoader {

	/**
	 * The accession-to-position map.
	 */
	// Hashcodes will not work with strings... LUCENE would be an alternative for indexing of fasta files in the long run.
	private static TObjectLongMap<String> acc2pos;

	/**
	 * The random access file instance.
	 */
	private static RandomAccessFile raf;

	/**
	 * The FASTA file instance. 
	 * TODO: Why is this static?
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
		if (instance == null) {
			instance = new FastaLoader();
		}
		return instance;
	}

	//	/**
	//	 * Returns a specific protein from the FASTA file.
	//	 * 
	//	 * @param id The protein identifier. May be the UniProt identifier or accession number.
	//	 * @return The Protein object.
	//	 * @throws IOExceptiongetProteinFromFasta 
	//	 */
	//	public static Protein getProteinFromFasta(String id) throws IOException {
	//		// No mapping provided.
	//		
	//		if (acc2pos == null) {
	//			// No index file given.
	//			if ((indexFile == null) || (file == null)) {
	//				UniProtUtilities uniprotweb = new UniProtUtilities();
	//				return uniprotweb.getProteinFromWebService(id);
	//			} else {
	//				try {
	//					readIndexFile();
	//				} catch (ClassNotFoundException e) {
	//					e.printStackTrace();
	//					return null;
	//				}
	//			}
	//		}
	//		Long pos = acc2pos.get(id);
	//
	//		if (!acc2pos.containsKey(id) || pos == null)  {
	//				System.out.println("Provided string does not match any protein entry: " + id);
	//				return null;
	//		}
	//
	//		if (raf == null) {
	//			raf = new RandomAccessFile(file, "r");
	//		}
	//
	//		raf.seek(pos);
	//		String line = "";
	//		String temp = "";
	//
	//		String header = "";
	//		while ((line = raf.readLine()) != null) {
	//			line = line.trim();
	//
	//			if (line.startsWith(">")) {
	//				if (!temp.equals("")) {
	//					break;
	//				}
	//				header = line;
	//			} else {
	//				temp += line;
	//			}
	//		}
	//		return new Protein(header, temp);
	//	}

	/**
	 * Writes the FASTA index file to the disk.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void writeIndexFile() throws FileNotFoundException, IOException, ClassNotFoundException {
		indexFile = new File(file.getAbsolutePath() + ".fb");

		if(indexFile.exists()){
			FileInputStream fis = new FileInputStream(indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			TObjectLongHashMap<String> tempMap = (TObjectLongHashMap<String>) ois.readObject();
			fis.close();
			ois.close();

			// Add entries of old map
			acc2pos.putAll(tempMap);
			tempMap.clear();
		}		

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile));
		oos.writeObject(acc2pos);
		oos.flush();
		oos.close();
	}

	/**
	 * Read the FASTA index file and stores its contents to memory.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static void readIndexFile() throws IOException, ClassNotFoundException {
		if(hasChanged) {
			FileInputStream fis = new FileInputStream(indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			acc2pos = (TObjectLongHashMap<String>) ois.readObject();
			fis.close();
			ois.close();
		}
	}

	/**
	 * updates a map with accessions and DigFASTAEntry-Objects and adds data as necessary
	 * 
	 * 
	 * @param proteinmap. A map containing protein hits as MascotProteinHit-objects with protein accessions as keys
	 * @return proteinmap. The updated map is returned 
	 * @throws FileNotFoundException
	 *             when the file could not be found.
	 * @author K. Schallert
	 * @throws SQLException 
	 */
	public HashMap<String, MascotProteinHit> updateProteinMapfromFasta(HashMap<String, MascotProteinHit> proteinmap) throws FileNotFoundException, SQLException {
		// Name and directory of the new fasta
		Connection conn = DBManager.getInstance().getConnection();
		// Initialize the buffered reader
		BufferedReader br = new BufferedReader(new FileReader(this.getFile()));
		// The line in the *.fasta file
		String line;
		// The fasta sequence
		String sequence = "";
		try {
			// Start the parsing of the FASTA file
			line = br.readLine();
			// Check if not an empty row occur and if begin of a new protein entry
			if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
				// Get elements of the database entry
				String header = line;
				// Add Sequence
				while( (line = br.readLine()) != null) {
					// Check whether a new entry starts
					if (line.trim().length() > 0 && line.charAt(0) == '>') {
						// Get parsed fasta-entry and write it to the new fasta
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
						// update the mascotproteinhit with sequence, description and database type data
						MascotProteinHit m_hit = proteinmap.get(entry.getIdentifier());
						// but only if found in the mascot dat file
						// TODO: Show KAY update
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
						sequence+=line;
					}
				}
				// save last entry
				DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
				// update the mascotproteinhit with sequence, description and database type data
				MascotProteinHit m_hit = proteinmap.get(entry.getIdentifier());
				// but only if found in the mascot dat file
				if (m_hit != null) {
					m_hit.setDatabaseType(entry.getType());
					m_hit.setDescription(entry.getDescription());
					m_hit.setSequence(entry.getSequence());
				} 
				br.close();	
			} else{
				System.out.println("ERROR IN FASTA FORMAT, FIRST ROW WRONG FORMATTED");
			}
		} catch (Exception e) {
			e.printStackTrace();}
		conn.close();
		return proteinmap;
	}


	/**
	 * Loads the FASTA file by random access and maps accessions of found 
	 * protein blocks to their respective byte positions in the file.
	 * 
	 * @param file The FASTA file.
	 * @throws FileNotFoundException
	 *             when the file could not be found.
	 */
	public void loadFastaFile() throws FileNotFoundException {

		// Instance of the client to fire progress
		//		Client client = Client.getInstance();

		try {
			// Initialize the random access file instance
			raf = new RandomAccessFile(file, "r");

			// Initialize index maps
			acc2pos = new TObjectLongHashMap<String>();

			// Get the first position at the beginning of the file
			Long pos = raf.getFilePointer();
			int count = 0;
			// Iterate FASTA file line by line
			String line;
			while ((line = raf.readLine()) != null) {
				// Check for header
				if (!line.isEmpty() && line.startsWith(">")) {
					// Parse header
					Header header = Header.parseFromFASTA(line);
					// Add map entry
					acc2pos.put(header.getAccession(), pos);
					count++;
					if(count % 10000 == 0) {						
						System.out.println(count + " sequences parsed...");
						//						client.firePropertyChange("new message", null, "Parsing a fasta file" + count );
					} 	
					//					if(count % 1000000 == 0) {						
					//						System.out.println("Writing index file...");
					//						writeIndexFile();
					//					}

				} else {
					// End of the sequence part == Start of a new header
					pos = raf.getFilePointer();
				}
			}
			System.out.println("Writing index file...");
			writeIndexFile();
			//raf.close(); // Has to stay open for further methods
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 

	}

	/**
	 * Returns the accession-to-position index map.
	 * @return indexMap The index map. 
	 */
	public TObjectLongMap<String> getIndexMap() {
		return acc2pos;
	}

	/**
	 * Sets the FASTA file.
	 * @param file The FASTA file
	 */
	@SuppressWarnings("static-access")
	public void setFastaFile(File file) {
		this.file = file;
		// reset map on change of FASTA file
		this.acc2pos = null;
		// include a in-silico peptide file
		String filestring = file.getAbsolutePath();
		filestring = filestring.substring(0, filestring.lastIndexOf('.'))+".pep";
		this.pepfile = new File(filestring.substring(0, filestring.lastIndexOf('.'))+".pep");
		if (!pepfile.exists() || pepfile.isDirectory()) {
			pepfile = null;
		}
	}

	/**
	 * Sets the peptide FASTA file.
	 * @param file The peptide FASTA file
	 */
	public void setPepFile(File file) {
		this.pepfile = file;
	}

	/**
	 * Returns the current in-silico peptide file if available
	 * @return pepFile the current peptide file.
	 */
	public File getPepFile() {
		return pepfile;
	}

	/**
	 * Returns the active protein FASTA file
	 * @return file the active protein FASTA file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Loads the designated in-silico digested peptide database file.
	 */
	public void loadPepFile() {
		if (pepfile!=null) {
			fastaDigester = new PeptideDigester();
			fastaDigester.parsePeptideDB(pepfile.getAbsolutePath());
		}
	}

	/**
	 * Get all protein hits for a peptide sequence in the digested database.
	 */
	@SuppressWarnings("static-access")
	public HashSet<String> getProtHits(String sequeString) {
		if (pepfile == null) {
			return new HashSet<String>();
		}
		return fastaDigester.fetchProteinsFromPeptideSequence(sequeString, pepfile.getAbsolutePath());
	}

	/**
	 * Returns the current index file instance.
	 * @return indexFile
	 */
	public File getIndexFile() {
		return indexFile;
	}

	/**
	 * Sets the current index file instance.
	 * @param indexFile The current index file.
	 */
	@SuppressWarnings("static-access")
	public void setIndexFile(File indexFile) {
		// Compare two file paths
		if (!indexFile.equals(this.indexFile)) {
			this.indexFile = indexFile;
			hasChanged = true;
			// reset map on change of index file
			this.acc2pos = null;
		}
	}


	/**
	 * Utility method to load a specified FASTA file by hand.
	 * 
	 * @param args String argument containing the path pointing to a FASTA file.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("No file provided.");
		} else {
			File file = new File(args[0]);
			FastaLoader fastaLoader = FastaLoader.getInstance();
			fastaLoader.setFastaFile(file);
			try {
				System.out.print("Loading file... ");
				fastaLoader.loadFastaFile();
				System.out.print(" done.\nWriting final output... ");
				fastaLoader.writeIndexFile();
				System.out.println("done.");
			} catch (Exception e) {
				System.err.println("aborted.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Methode which reads all *.fasta and creates an MPA compatible *.fasta.
	 * @param fastaFiles. Files of the input *.fastas.
	 * @param outpath. The output *.fasta file.
	 * @param batchSize. Number of entries which should be stored in by one query
	 * @param mascotFlag. Flag for the creation of a new Fasta add the specified direction.
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void addFastaDatabases(File[] fastaFiles, File outpath, boolean mascotFlag, int batchSize) throws IOException, SQLException {

		// Instance of the Buffered Reader
		BufferedReader br = null;

		// Name and directory of the new fasta
		File outputFastaFile=  new File(Constants.FASTA_PATHS + outpath.getName());

		// Open buffered writer to write new database.
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFastaFile));

		// Entry number
		long entryNo = 0;

		// Create a Protein entry in the database
		Connection conn = DBManager.getInstance().getConnection();

		// List of all proteins stored already in the database
		TreeMap<String, Long> allProts = ProteinAccessor.findAllProteinsWithID(conn);

		// Time for code optimization
		LocalDateTime timePoint = LocalDateTime.now(); 

		// Iterate over all selected *.fasta-files.
		for (int i = 0; i < fastaFiles.length; i++) {

			// Get the filename as prefix for the identifier
			String filename = fastaFiles[i].getName().toString();
			String filePath = fastaFiles[i].getAbsolutePath();
			// Get just the name of the FASTA
			filename = filename.split("[.]")[0];

			// Get total number of FASTA entries
			int totalCountEntries = DigFASTAEntryParser.countEntries(filePath);

			// Initialize the buffered reader
			br = new BufferedReader(new FileReader(fastaFiles[i]));

			// The line in the *.fasta file
			String line;
			// The fasta sequence
			String sequence = "";

			// Initialize a list of FASTA entries to store them as bulk
			ArrayList<DigFASTAEntry> fastaEntryList  = new ArrayList<DigFASTAEntry>(); 

			// Start the parsing of the FASTA file
			line = br.readLine();
			// Check if not an empty row occur and if begin of a new protein entry
			if (line.trim().length() > 0 && line.charAt(0) == '>' ){	
				entryNo = entryNo + 1;
				// Get elements of the database entry
				String header = line;

				// Add Sequence
				while( (line = br.readLine()) != null) {
					// Check whether a new entry starts
					if (line.trim().length() > 0 && line.charAt(0) == '>') {
						// increase entry number
						entryNo = entryNo + 1;
						// Get parsed fasta-entry and write it to the new fasta
						DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
						writeEntry(bw, "", entry);

						// Safe protein entry in the SQL DB after check if it is already in the DB
						if (allProts.get(entry.getIdentifier()) == null) {
							// If protein entry not already in the database add it to the store list
							fastaEntryList.add(entry);
							// Add new protein to the already stored list
							allProts.put(entry.getIdentifier(), entry.getUniProtID());
						}

						// give an update batchsize entries
						if (entryNo % batchSize == 0) {
							// Store proteins and also UniProtEntries
							saveProteinsAndUniRefBatch(fastaEntryList, conn);
							// Reset the fastaEntryList
							fastaEntryList = new ArrayList<DigFASTAEntry>(); 
							// Show progress
							System.out.println("DB: " + filename + " " + (entryNo) + " of " + totalCountEntries + "time " + Duration.between(LocalDateTime.now(),timePoint));
							timePoint = LocalDateTime.now();
						}

						// Reset the sequence
						sequence = "";
						// Add new header
						header = line;
					} else {
						sequence += line.trim();
					}
				}

				// save last entry
				DigFASTAEntry entry = DigFASTAEntryParser.parseEntry(header, sequence);
				writeEntry(bw, "", entry);
				// Safe protein entry in the SQL DB after check if it is already in the DB
				if (allProts.get(entry.getIdentifier()) == null) {
					// If protein entry not already in the database add it to the store list
					fastaEntryList.add(entry);
					// Add new protein to the already stored list
					allProts.put(entry.getIdentifier(), entry.getUniProtID());
				}
				// Store proteins and also UniProtEntries
				saveProteinsAndUniRefBatch(fastaEntryList, conn);
			} else{
				System.out.println("ERROR IN FASTA FORMAT, FIRST ROW WRONG FORMATTED");
			}
		}
		br.close();
		bw.flush();
		bw.close();

		// Add permissions to the new *.fasta file
		outputFastaFile.setExecutable(true);
		outputFastaFile.setReadable(true); 
		outputFastaFile.setWritable(true);

		// Create a *.fasta for the mascot searches in the specified directory
		if (mascotFlag) {
			Files.copy(outputFastaFile.toPath(), outpath.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			//			Client.getInstance().firePropertyChange("new message", null, "Creating fasta copy for Mascot");
		}
		// Runs the fastaformater script
		RunFastaFormater fastaFromater = new RunFastaFormater();
		fastaFromater.formatFastaDatabase(outputFastaFile.toPath().toString());

		// Add *.fasta filename to the client settings.
		addFastaTotheClientSetting(outputFastaFile.getName());

		conn.close();
	}

	/**
	 * Method to create for a group of FASTA entries the protein and uniProt table
	 * @param fastaEntryList. The list of all parsed FASTA entries
	 * @throws SQLException 
	 */
	private static void saveProteinsAndUniRefBatch(ArrayList<DigFASTAEntry> fastaEntryList, Connection conn) throws SQLException {

		// Store protein entries with fk_uniProtID = "-1"
		// get back map with key=accession and value=proteinid 

		// accession 2 proteinid mapping --> for later identification of proteins
		TreeMap<String, Long> accesion2idMap = ProteinAccessor.addMutlipleProteinsToDatabase(fastaEntryList, conn);


		// Get UniProt informations
		UniProtUtilities utils = new UniProtUtilities();

		// Map with UniProt entries with UniProtAccession as key
		TreeMap<String, UniProtEntryMPA> protacc2uniprotentryMap = utils.fetchUniProtEntriesByFastaEntryList(fastaEntryList, true);

		// combine the maps accession2idMap and uniProtIDMapping --> proteinid2uniprotid-mapping=protein2uniprotids
		TreeMap<Long, UniProtEntryMPA> protein2uniprotEntry = new TreeMap<Long, UniProtEntryMPA>();
		for (String accession : protacc2uniprotentryMap.keySet()) {
			protein2uniprotEntry.put(accesion2idMap.get(accession), protacc2uniprotentryMap.get(accession));
		}

		// Store uniProt entries
		TreeMap<Long, Long> proteinID2uniprotIDmap = UniprotentryAccessor.addMultipleUniProtEntriesToDatabase(protein2uniprotEntry, conn);

		// updates the uniprotIds for the protein entries
		for (DigFASTAEntry fastaentry : fastaEntryList) {
			// Check whether UniProt entries are availble
			if (proteinID2uniprotIDmap.get(fastaentry.getIdentifier()) !=null)  {
				long uniProtID = proteinID2uniprotIDmap.get(fastaentry.getIdentifier());
				fastaentry.setUniProtID(uniProtID);
			}
		}
		// Commit everything
		conn.commit();

	}

	/**
	 * Write a FASTA database entry.
	 * @param bw. BufferedWriter that writes the data.
	 * @throws IOException
	 */
	public void writeEntry(BufferedWriter bw, DigFASTAEntry fastaEntry) throws IOException {
		writeEntry(bw, "",fastaEntry );
	}

	/**
	 * Write a FASTA database entry.
	 * @param bw. BufferedWriter that writes the data.
	 * @param prefix, Prefix for each identifier
	 * @throws IOException
	 */
	public static void writeEntry(BufferedWriter bw, String prefix, DigFASTAEntry fastaEntry) throws IOException {

		// Container for the sequence
		String sequenceContainer;
		sequenceContainer = fastaEntry.getSequence();

		// Keep database Format
		if (fastaEntry.getType().equals(Type.UNIPROTSPROT)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(1));
		}else if (fastaEntry.getType().equals(Type.UNIPROTTREMBL)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(1));
		}		else if (fastaEntry.getType().equals(Type.NCBIGENBANK)) {
			bw.write(fastaEntry.getType().dbStartFlag + prefix + fastaEntry.getIdentifier());
			bw.write("|" + fastaEntry.getSubHeader().get(3));
		} 
		//		else if (getType().equals(Type.NCBIREFERENCE)) {
		//			bw.write(getType().dbStartFlag + prefix + "_" + getIdentifier());
		//			bw.write("|" + getSubHeader().get(3));
		//		} 
		else if (fastaEntry.getType().equals(Type.Database)) {
			bw.write(fastaEntry.getType().dbStartFlag  +  prefix  + fastaEntry.getIdentifier());
			bw.write("|"  + "Metagenome unknown");
		} 
		else if (fastaEntry.getType().equals(Type.SILICO_PEPTIDE)) {
			bw.write(fastaEntry.getType().dbStartFlag+  prefix  + fastaEntry.getIdentifier());
		} 
		else if (fastaEntry.getType().equals(Type.METAGENOME1)) {
			bw.write(fastaEntry.getType().dbStartFlag  +  prefix + fastaEntry.getIdentifier());
			bw.write("|"  + "Metagenome unknown");
		} 
		else if (fastaEntry.getType().equals(Type.METAGENOME2)) {
			bw.write(">generic|"  +   fastaEntry.getIdentifier());
			bw.write("|"  + "Metagenome unknown");
		} 
		else if (fastaEntry.getType().equals(Type.METAGENOME3)) {
			bw.write(">generic|"  +  prefix + fastaEntry.getIdentifier());
			bw.write("|"  + "Metagenome unknown");
		} 
		bw.newLine();



		// Makes a linebreak each 80 chars
		while(sequenceContainer.length()>80){
			bw.append(sequenceContainer.subSequence(0, 79));
			bw.newLine();
			sequenceContainer = sequenceContainer.substring(79);
		}
		bw.append(sequenceContainer);
		bw.newLine();
	}

	/**
	 * Method to add new *.fasta to the client settings
	 * @param fastaFile
	 * @throws IOException 
	 */
	public static void addFastaTotheClientSetting(String fastaFile) throws IOException{

		// Name of the new *.fasta
		String newFastaFileName = fastaFile.split("[.]")[0];

		// Client settings file
		String clientSettingsFileServer = null;
		String clientSettingsFile = null;

		clientSettingsFileServer  = Constants.CONFIGURATION_PATH_JAR + File.separator + "client-settings.txt";
		// Get old parameter dialog
		BufferedReader br 	= new BufferedReader(new FileReader(new File(clientSettingsFileServer)));
		String header 		= br.readLine();
		String fastaFiles 	= br.readLine();
		br.close();

		// Write new parameter dialog with new fasta.
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(clientSettingsFileServer)));
		bw.append(header);
		bw.newLine();
		bw.append(fastaFiles + "," + newFastaFileName);
		bw.flush();
		bw.close();

		// If using eclipse update of resource/conf necessary
		if (!Starter.isJarExport()) {

			clientSettingsFile = Constants.DIR + Constants.SEP +"src"+ Constants.SEP + Constants.CONFIGURATION_PATH + "client-settings.txt";

			// Get old parameter dialog
			BufferedReader br2 	= new BufferedReader(new FileReader(new File(clientSettingsFile)));
			String header2 		= br2.readLine();
			String fastaFiles2 	= br2.readLine();
			br2.close();

			// Write new parameter dialog with new fasta.
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(clientSettingsFile)));
			bw2.append(header2);
			bw2.newLine();
			bw2.append(fastaFiles2 + "," + newFastaFileName);
			bw2.flush();
			bw2.close();
		} 
	}


	//	
	//	
	//	public static void repairSequences(Connection conn) throws SQLException, IOException{
	//		TreeMap<String, Long> findAllProteins = ProteinAccessor.findAllProteinsWithID(conn);
	//		
	//		for (Entry<String, Long> protEntry : findAllProteins.entrySet()) {
	//			
	//			Long protID = protEntry.getValue();
	//			System.out.println(protID + " " + protEntry.getKey());
	//			ProteinAccessor protAcc = ProteinAccessor.findFromID(protID, conn);
	//			if (protAcc.getSequence() == null || protAcc.getSequence().length()<1) {
	//				String accession = protAcc.getAccession();
	//				String desc = protAcc.getDescription();
	//				Timestamp modificationdate = protAcc.getModificationdate();
	//				Protein proteinFromFasta = getProteinFromFasta(accession);
	//				String sequence = proteinFromFasta.getSequence().getSequence();
	//				System.out.println("ENTRY:" + accession +" " + desc + " " + sequence);
	//				ProteinAccessor.upDateProteinEntry(protID, accession, desc, sequence, modificationdate, conn);
	//				conn.commit();
	//			}else{
	////				String accession = protAcc.getAccession();
	////				String desc = protAcc.getDescription();
	////				System.out.println("EGAL:" + accession +" " + desc + " ");
	//			}
	//			
	//			
	//		}
	//	
	//	}

}
