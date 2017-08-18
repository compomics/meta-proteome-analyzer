package de.mpa.io.fasta.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArray;
import org.mapdb.serializer.SerializerArrayTuple;

import com.compomics.util.protein.Header;

import de.mpa.client.Client;

/**
 * The OffHeapIndex constructs a file-based mapping database and stores
 * peptide string entries together with its respective protein accessions. 
 * 
 * @author Thilo Muth
 *
 */
public class OffHeapIndex extends StandardIndex implements DatabaseIndex {
	
	/**
	 * The DB instance.
	 */
	private DB mapDb;
	
	/**
	 * The peptide string index is based on lock-free concurrent B-Linked-Tree.
	 */
	private NavigableSet<Object[]> peptideIndex;
	
	/**
	 * The species index maps from species to protein accessions. 
	 */
	private NavigableSet<Object[]> speciesIndex;
	
	/**
	 * Constructs an off-heap index on the basis of peptide strings mapping to sets of protein accessions.
	 * 
 	 * @param fastaFile 		the FASTA database file
	 * @param nMissedCleavages 	the number of missed cleavages
	 * @throws IOException
	 */
	public OffHeapIndex(File fastaFile, int missedCleavages) throws IOException {
		super(fastaFile, missedCleavages);
		this.setupDatabase();
	}
	
	/**
	 * This method contructs the file-based database, which holds peptide strings as keys and a string array of proteins accessions as values. 
	 * @throws IOException 
	 */
	private void setupDatabase() throws IOException {
		File file = new File(fastaFile.getAbsolutePath() + ".db");
		boolean dbExists = file.exists();
		mapDb = DBMaker.fileDB(file).closeOnJvmShutdown().make();
		
		// Initialize multi maps 
		peptideIndex = mapDb.treeSet("peptideToProteins").serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).counterEnable().counterEnable().counterEnable().createOrOpen();
		speciesIndex = mapDb.treeSet("speciesToProteins").serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).counterEnable().counterEnable().counterEnable().createOrOpen();
		
		// If database does not exist, create a new index.
		if(!dbExists) {
			generateIndex();
			mapDb.commit();
		}
	}
	
	/**
	 * Indexes the FASTA file by retrieving every entry and creating an off-heap collection (using MapDB) of tags mapping to protein accessions.
	 * @throws IOException 
	 */
	public void generateIndex() throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
		String nextLine = null;
		boolean firstline = true;
		String header = null;
		int count = 0;
		StringBuilder builder = new StringBuilder();
		while ((nextLine = reader.readLine()) != null) {
			if (!nextLine.isEmpty() && nextLine.charAt(0) == '>') {
				if (firstline) {
					header = nextLine.trim();
					firstline = false;
				} else {
					count++;
					if (count % 1000 == 0) {
						System.out.println(count + " proteins inserted into index.");
						if (Client.getInstance() != null)
							Client.getInstance().firePropertyChange("new message", null, count + " PROTEINS INSERTED INTO PEPTIDE INDEX...");
					} 
					addToPeptideIndex(header, builder.toString());
					builder = new StringBuilder();
					header = nextLine.trim();
				}
			} else {
				builder.append(nextLine.trim());
			}
		}
		addToPeptideIndex(header, builder.toString());
		reader.close();
	}
	
	/**
	 * Adds respective tags and the protein to the index.
	 * @param accession	Protein	header
	 * @param sequence	Protein	sequence
	 */
	private void addToPeptideIndex(String header, String sequence) {
		// Digest peptides with tryptic cleavage.		
		List<String> peptides = performTrypticCleavage(sequence, 6, 40);
		String accession = Header.parseFromFASTA(header).getAccession();
		String species = Header.parseFromFASTA(header).getTaxonomy();
		// Iterate all digested peptides and add them to the index (including protein accessions).
		for (String peptideSequence : peptides) {
			peptideIndex.add(new Object[]{peptideSequence, accession});
		}
		speciesIndex.add(new Object[]{species, accession});
		nProteins++;
	}
	
	
	
	/**
	 * Returns the peptide-to-proteins index (based on a multi map). 
	 * @return The peptide index
	 */
	public NavigableSet<Object[]> getPeptideIndex() {
		return peptideIndex;
	}
	
	/**
	 * Returns the protein-to-species index (based on a tree map). 
	 * @return The species index
	 */
	public NavigableSet<Object[]> getSpeciesIndex() {
		return speciesIndex;
	}
	
	public void close() {
		mapDb.close();
	}
}
