package de.mpa.io.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

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
	private Map<String, Long> acc2pos;
	
	/**
	 * Collection containing identifier-to-accession mappings.
	 */
	private Map<String, String> id2acc;
	
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
	
	/**
	 * Singleton object instance of the FastaLoader class.
	 */
	private static FastaLoader instance;

	/**
	 * Private constructor as FastaLoader is a singleton object.
	 */
	private FastaLoader() {
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
	 * Returns a specific protein from the FASTA file.
	 * 
	 * @param id The protein identifier. May be the UniProt identifier or accession number.
	 * @return The Protein object.
	 * @throws IOException 
	 */
	public Protein getProteinFromFasta(String id) throws IOException {
		if (acc2pos == null) {
			if ((indexFile == null) || (file == null)) {
				return null;
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
		if (pos == null) {
			// perhaps the provided string is a UniProt identifier
			String accession = id2acc.get(id);
			if (accession == null) {
				throw new IOException("Provided string does not match any protein entry: " +
						id);
			} else {
				pos = acc2pos.get(accession);
			}
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
	 */
	public void writeIndexFile() throws FileNotFoundException, IOException {
		indexFile = new File(file.getAbsolutePath() + ".fb");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile));
		oos.writeObject(acc2pos);
		oos.writeObject(id2acc);
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
		FileInputStream fis = new FileInputStream(indexFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		acc2pos = (Map<String, Long>) ois.readObject();
		id2acc = (Map<String, String>) ois.readObject();
		fis.close();
		ois.close();
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
		try {
			// Initialize the random access file instance
			raf = new RandomAccessFile(file, "r");
			
			// Initialize index maps
			acc2pos = new HashMap<String, Long>();
			id2acc = new HashMap<String, String>();
			
			// Get the first position at the beginning of the file
			Long pos = raf.getFilePointer();
			
			// Iterate FASTA file line by line
			String line;
			while ((line = raf.readLine()) != null) {
				// Check for header
				if (line.startsWith(">")) {
					// Parse header
					Header header = Header.parseFromFASTA(line);
					// Add map entry
					acc2pos.put(header.getAccession(), pos);
					String identifier = header.getDescription();
					identifier = identifier.substring(0, identifier.indexOf(" "));
					id2acc.put(identifier, header.getAccession());
				} else {
					// End of the sequence part == Start of a new header
					pos = raf.getFilePointer();
				}
			}
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Returns the accession-to-position index map.
	 * @return indexMap The index map. 
	 */
	public Map<String, Long> getIndexMap() {
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
		this.indexFile = indexFile;
		// reset map on change of index file
		this.acc2pos = null;
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
				System.out.print(" done.\nWriting output... ");
				fastaLoader.writeIndexFile();
				System.out.println("done.");
			} catch (Exception e) {
				System.err.println("aborted.");
				e.printStackTrace();
			}
		}
	}
	
}
