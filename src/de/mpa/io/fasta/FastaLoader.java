package de.mpa.io.fasta;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntryType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.client.Client;

/**
 * Singleton class providing FASTA read/write capabilities via random access
 * file.
 * 
 * @author Thilo Muth
 */
public class FastaLoader {
	
	/**
	 * The accession-to-position map.
	 */
	// Hashcodes will not work with strings... LUCENE would be an alternative for indexing of fasta files in the long run.
	private TObjectLongMap<String> acc2pos;
	
	/**
	 * The random access file instance.
	 */
	private RandomAccessFile raf;

	/**
	 * The FASTA file instance.
	 */
	private File file;
	
	/**
	 * The index file.
	 */
	private File indexFile;
	
	private boolean hasChanged;
	
	/**
	 * UniProt Query Service object.
	 */
	private Object uniProtQueryService;
	
	/**
	 * UniProt Entry Retrieval Service object.
	 */
	private EntryRetrievalService entryRetrievalService;
	
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
	public Protein getProteinFromWebService(String id) {
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
	public String getProteinName(ProteinDescription desc) {
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
	 * @throws IOException 
	 */
	public Protein getProteinFromFasta(String id) throws IOException {
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
	public void readIndexFile() throws IOException, ClassNotFoundException {
		if(hasChanged) {
			FileInputStream fis = new FileInputStream(indexFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			acc2pos = (TObjectLongHashMap<String>) ois.readObject();
			fis.close();
			ois.close();
		}
	
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
						client.firePropertyChange("new message", null, "Parsing a fasta file" + count );
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
	
}
