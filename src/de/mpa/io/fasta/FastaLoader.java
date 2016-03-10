package de.mpa.io.fasta;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntryType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.util.io.filefilters.PeffFileFilter;
import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.fastadigest.PeptideDigester;

import de.mpa.db.storager.MascotStorager.MascotProteinHit;
import de.mpa.db.storager.MascotStorager.MascotPeptideHit; 

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
	 * UniProt Query Service object.
	 */
	private Object uniProtQueryService;
	
	/**
	 * UniProt Entry Retrieval Service object.
	 */
	private static EntryRetrievalService entryRetrievalService;
	
	/**
	 * Singleton object instance of the FastaLoader class.
	 */
	private static FastaLoader instance;

	/**
	 * Private constructor as FastaLoader is a singleton object.
	 */
	private FastaLoader() {
		setupUniProtQueryService();
	}

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
	
	/**
	 * This method setups the uniprot query service.
	 */
	private void setupUniProtQueryService() {
		// Check whether UniProt query service has been established yet.
		if (uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
			
			// Create entry retrival service
			entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();
		}		
	}
	
	/**
	 * Returns a protein object queried by the UniProt webservice (if no indexed FASTA is available.
	 * @param id Protein accession.
	 * @return Protein object containing header + sequence.
	 */
	public static Protein getProteinFromWebService(String id) {
		// Retrieve UniProt entry by its accession number
		UniProtEntry entry = (UniProtEntry) entryRetrievalService.getUniProtEntry(id);
		String header = ">";
		if(entry.getType() == UniProtEntryType.TREMBL) {
			header += "tr|";
		} else if(entry.getType() == UniProtEntryType.SWISSPROT) {
			header += "sw|";
		}
		header += id + "|";
		
		header += getProteinName(entry.getProteinDescription());
		String sequence = entry.getSequence().getValue();
		return new Protein(header, sequence);
	}
	
	/**
	 * Returns the protein name(s) as formatted string
	 * @param desc ProteinDescription object.
	 * @return Protein name(s) as formatted string.
	 */
	public static String getProteinName(ProteinDescription desc) {
		Name name = null;
		
		if (desc.hasRecommendedName()) {
			name = desc.getRecommendedName();
		} else if (desc.hasAlternativeNames()) {
			name = desc.getAlternativeNames().get(0);
		} else if (desc.hasSubNames()) {
			name = desc.getSubNames().get(0);
		}
		return (name == null) ? "unknown" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
	}
	
	/**
	 * Returns a specific protein from the FASTA file.
	 * 
	 * @param id The protein identifier. May be the UniProt identifier or accession number.
	 * @return The Protein object.
	 * @throws IOExceptiongetProteinFromFasta 
	 */
	public static Protein getProteinFromFasta(String id) throws IOException {
		// No mapping provided.
		
		if (acc2pos == null) {
			// No index file given.
			if ((indexFile == null) || (file == null)) {
				return getProteinFromWebService(id);
			} else {
				try {
					readIndexFile();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		Long pos = acc2pos.get(id);

		if (!acc2pos.containsKey(id) || pos == null)  {
				System.out.println("Provided string does not match any protein entry: " + id);
				return null;
		}

		if (raf == null) {
			raf = new RandomAccessFile(file, "r");
		}

		raf.seek(pos);
		String line = "";
		String temp = "";

		String header = "";
		while ((line = raf.readLine()) != null) {
			line = line.trim();

			if (line.startsWith(">")) {
				if (!temp.equals("")) {
					break;
				}
				header = line;
			} else {
				temp += line;
			}
		}
		return new Protein(header, temp);
	}
	
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
	 * updates a map with accessions and MascotProteinHits and adds data as necessary
	 * 
	 * @throws FileNotFoundException
	 *             when the file could not be found.
	 * @author K. Schallert
	 */
	public HashMap<String, MascotProteinHit> updateProteinMapfromFasta(HashMap<String, MascotProteinHit> proteinmap) throws FileNotFoundException {
		// just open the thing and read every line .....
        try {
			// load the dat file, TODO: file is static for some reason ...
            BufferedReader fastareader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
            // init stuff
            String line;
            boolean parse_sequence_mode = false;
            String current_sequence = "";
			String current_accession = null;
			String current_description = null;			
            // read through all lines ...            
            while ((line = fastareader.readLine()) != null) {            
            	// headers start with ">"
            	if (line.startsWith(">")) {
            		// reached new header, stop parsing sequence
            		if (parse_sequence_mode) {
            			parse_sequence_mode = false;
            			proteinmap.get(current_accession).adddescription(current_description);
            			proteinmap.get(current_accession).addsequence(current_sequence);         			
            		}            		
            		// parse accession
            		String[] header_split = line.split("[|]");            		
            		if (proteinmap.containsKey(header_split[1])) {
            			// got a match -> parse data, this may crash if the accession is not properly formatted
            			// TODO: find permanent solution for misformed accessions
            			current_accession = header_split[1];
            			current_description = header_split[2];
            			current_sequence = "";
            			parse_sequence_mode = true;            			
            		}
            	} else if (parse_sequence_mode) {
            		// append current line to the sequence string
            		current_sequence = current_sequence + line;
            	}
            }
            fastareader.close();	
        } catch (IOException e) {
        	e.printStackTrace();
        }
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
		Client client = Client.getInstance();
		
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
	
	
	public static void repairSequences(Connection conn) throws SQLException, IOException{
		Map<String, Long> findAllProteins = ProteinAccessor.findAllProteins(conn);
		
		for (Entry<String, Long> protEntry : findAllProteins.entrySet()) {
			
			Long protID = protEntry.getValue();
			System.out.println(protID + " " + protEntry.getKey());
			ProteinAccessor protAcc = ProteinAccessor.findFromID(protID, conn);
			if (protAcc.getSequence() == null || protAcc.getSequence().length()<1) {
				String accession = protAcc.getAccession();
				String desc = protAcc.getDescription();
				Timestamp modificationdate = protAcc.getModificationdate();
				Protein proteinFromFasta = getProteinFromFasta(accession);
				String sequence = proteinFromFasta.getSequence().getSequence();
				System.out.println("ENTRY:" + accession +" " + desc + " " + sequence);
				ProteinAccessor.upDateProteinEntry(protID, accession, desc, sequence, modificationdate, conn);
				conn.commit();
			}else{
//				String accession = protAcc.getAccession();
//				String desc = protAcc.getDescription();
//				System.out.println("EGAL:" + accession +" " + desc + " ");
			}
			
			
		}
	
	}

}
