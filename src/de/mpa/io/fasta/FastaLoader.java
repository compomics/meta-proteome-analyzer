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

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

/**
 * This class a fasta file via random access.
 * @author Thilo Muth
 *
 */
public class FastaLoader {
	
	/**
	 * The accession-To-Position map.
	 */
	private HashMap<String, Long> acc2pos;

	private RandomAccessFile raf;

	private File file;
	
	/**
	 * Singleton object of the FastaLoader.
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
	 * @return instance FastaLoader object.
	 */
	public static FastaLoader getInstance() {
		// Lazy instantation
		if (instance == null) {
			instance = new FastaLoader();
		}
		return instance;
	}

	/**
	 * Returns a specific protein from the fasta file.
	 * 
	 * @param accession The protein accession as key.
	 * @return The Protein object.
	 * @throws IOException 
	 */
	public Protein getProteinFromFasta(String accession) throws IOException {
		Long pos = acc2pos.get(accession);
		raf = new RandomAccessFile(file, "r");
		raf.seek(pos);
		String line = raf.readLine();
		// Parse the header
		String header = line;
		StringBuilder sb = new StringBuilder();
		while((line=raf.readLine() )!= null) {
			if (line.startsWith(">")) break;
			sb.append(line);
		}

		return new Protein(header, sb.toString());
	}
	
	/**
	 * Write the FASTA index file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeIndexFile() throws FileNotFoundException, IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(file.getName() + ".fb")));
		oos.writeObject(acc2pos);
		oos.flush();
		oos.close();
	}
	
	/**
	 * Read the FASTA index file.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readIndexFile() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(new File(file.getName() + ".fb"));
		ObjectInputStream ois = new ObjectInputStream(fis);
		acc2pos = (HashMap<String, Long>) ois.readObject();
		fis.close();
		ois.close();
	}
	
	/**
	 * This method loads the fast file by random access and fills the acc2Pos
	 * map for mapping accession to byte positions in the fasta file.
	 * 
	 * @param file The fasta file.
	 * @throws FileNotFoundException when the file could not be found.
	 */
	public void loadFastaFile() throws FileNotFoundException {
		try {
			// Initialize the random access file.
			raf = new RandomAccessFile(file, "r");
			
			// Initialize the acc2Pos map for indexing.
			acc2pos = new HashMap<String, Long>();
			
			// Get the first position at the beginning of the file.
			Long pos = raf.getFilePointer();
			
			// Line in the file
			String line;
			
			// Iterate the fasta file line by line.
			while ((line = raf.readLine()) != null) {
				
				// Header
				if (line.startsWith(">")) {
					// Parse the header
					Header header = Header.parseFromFASTA(line);
					
					// Fill the map.
					acc2pos.put(header.getAccession(), pos);

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
	 * Gets the index map.
	 * @return indexMap The index map. 
	 */
	public HashMap<String, Long> getIndexMap() {
		return acc2pos;
	}

	/**
	 * Sets the FASTA file.
	 * @param file The FASTA file
	 */
	public void setFastaFile(File file) {
		this.file = file;
	}
	
	
}
