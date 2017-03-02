package de.mpa.analysis.taxonomy;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.client.Constants;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Taxonomy;
import de.mpa.util.Formatter;

/**
 * This class constructs an index file for the taxonomy (phylogentic tree) from NCBI:
 * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip
 * @author R. Heyer, T. Muth
 */
public class NcbiTaxonomy implements Serializable {

	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 

	/**
	 * Instance of the NCBI taxonomy.
	 */
	private static NcbiTaxonomy instance;

	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'names.dmp'
	 */ 
	private static TIntIntHashMap namesMap;

	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'nodes.dmp'
	 */
	private static TIntIntHashMap nodesMap;

	/**
	 * File of the names.dmp.
	 */
	private static String namesFileString;

	/**
	 * File of the nodes.dmp.
	 */
	private static String nodesFileString; 

	/**
	 * Input reader instance for names.dmp file.
	 */
	private RandomAccessFile namesRaf;

	/**
	 * Input reader instance for nodes.dmp file.
	 */
	private RandomAccessFile nodesRaf;

	/**
	 * Filename of the taxonomy index file.
	 */
	private static final String INDEX_FILENAME = "taxonomy.index";

	/**
	 * The root node constant.
	 */
	public static final TaxonomyNode ROOT_NODE = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");

	/**
	 * Constructor for the NCBI taxonomy.
	 * @param namesFile. The Filepath of the names file.
	 * @param nodesFile. The Filepath of the nodes file.
	 */
	private NcbiTaxonomy(String namesFileString, String nodesFileString) {
		NcbiTaxonomy.namesFileString = namesFileString;
		NcbiTaxonomy.nodesFileString = nodesFileString;
		try {
			namesRaf = new RandomAccessFile(new File(NcbiTaxonomy.namesFileString), "r");
			nodesRaf = new RandomAccessFile(new File(NcbiTaxonomy.nodesFileString), "r");
			createIndexFile();
			readIndexFile();
			storeTaxonomy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get NCBI taxonomy maps instance.
	 * @param namesFile. The filepath of the names file.
	 * @param nodesFile. The filepath of the nodes file.
	 * @return the taxonomy maps instance.
	 * @throws Exception 
	 */
	public static NcbiTaxonomy getInstance(String inputNamesString, String inputNodesString) throws Exception {
		String namesString = inputNamesString;
		String nodesString = inputNodesString;
		if (instance == null) {
			instance = new NcbiTaxonomy(namesString, nodesString);
		}
		return instance;
	}

	/**
	 * Indexes NCBI taxonomy dump files and writes them to an index file in the
	 * specified folder.
	 * @param path path string pointing to the target folder
	 * @throws Exception 
	 */
	public static void createIndexFile() throws Exception {

		// NCBI names.dmp file
		File namesFile = new File(namesFileString);

		// NCBI nodes.dmp file
		File nodesFile = new File(nodesFileString);

		// Map for NCBI names file 
		namesMap = new TIntIntHashMap();

		// Map for NCBI nodes file 
		nodesMap = new TIntIntHashMap();

		// Gets the number of chars for a new line
		int newline = Formatter.determineNewlineChars(nodesFile).length;

		// The start byte position
		int pos = 0;

		BufferedReader br;
		String line;

		// Read names file
		br = new BufferedReader(new FileReader(namesFile));
		// example format: "1	|	root	|		|	scientific name	|"
		while ((line = br.readLine()) != null) {
			if (line.endsWith("c name\t|")) {
				String id = line.substring(0, line.indexOf('\t'));
				namesMap.put(Integer.valueOf(id), pos);
			}
			pos += line.getBytes().length + newline;
		}
		br.close();

		// Read node file and create index map
		br = new BufferedReader(new FileReader(nodesFile));
		// example format: "2	|	131567	|	superkingdom	|		|	0	|	0	|	11	|	0	|	0	|	0	|	0	|	0	|		|"
		pos = 0;
		while ((line = br.readLine()) != null) {
			String id = line.substring(0, line.indexOf('\t'));
			nodesMap.put(Integer.valueOf(id), pos);
			pos += line.getBytes().length + newline;
		}
		br.close();
		System.out.println("Read names.dmp and nodes.dmp" );
		writeIndexFile();
	}

	/**
	 * Write the index files.
	 * @throws Exception if an I/O error occurs
	 */
	private static void writeIndexFile() throws Exception {
		// The file of the createt index file.
		File indexFile;
		String name = Constants.CONFIGURATION_PATH_JAR + INDEX_FILENAME;
		File file = new File(name);
		URL url = file.getClass().getResource(name);
		//		URL url = Client.getInstance().getClass().getResource(name);
		//		URL url = this.getClass().getResource(name);
		if (url == null) {
			indexFile = new File(name);
			indexFile.createNewFile();
		} else {
			indexFile = new File(url.toURI());
		}
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));
		oos.writeObject(namesMap);
		oos.writeObject(nodesMap);
		oos.flush();
		oos.close();
		namesMap.clear();
		nodesMap.clear();
		System.out.println("NCBI taxonomy read successfully");
	}

//	storeTaxonomy.
	/**
	 * Methode to store new taxonomies into the sql database
	 */
	public void storeTaxonomy() throws Exception{
		Connection conn = DBManager.getInstance().getConnection();
		int[] keys = this.getNodesMap().keys();
		for (int taxID : keys) {
			if (taxID != 1) {
				Taxonomy.addTaxonomy((long) taxID, (long) this.getParentTaxId(taxID), this.getTaxonName(taxID), this.getRank(taxID), conn);
				if (taxID % 1000 == 0) {
					System.out.println(taxID);
					conn.commit();
				}
			}
		}
		conn.commit();
	}
	
	
	/**
	 * Reads the index files.
	 * @throws Exception if an I/O error occurs
	 */
	public void readIndexFile() throws Exception {
		try {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(Constants.CONFIGURATION_PATH_JAR + INDEX_FILENAME)));
			namesMap = (TIntIntHashMap) ois.readObject();
			nodesMap = (TIntIntHashMap) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("ERROR: \"" + Constants.CONFIGURATION_PATH_JAR + INDEX_FILENAME + "\" not found!");
		}
	}
	
	/**
	 * Returns the nodes map for external use.
	 * @return Map containing the taxonomy nodes.
	 */
	public TIntIntHashMap getNodesMap() {
		return nodesMap;
	}

	/** 		
	 * Returns the parent taxonomy id of the taxonomy node belonging to the specified taxonomy id. 		
	 * @param taxId the taxonomy id 		
	 * @return the parent taxonomy id 		
	 * @throws Exception if an I/O error occurs 		
	 */ 		
	public int getParentTaxId(int taxId) throws Exception { 		

		// Get mapping 		
		int pos = nodesMap.get(taxId); 		

		// Skip to mapped byte position in nodes file 		
		nodesRaf.seek(pos); 		

		// Read line and isolate second numeric value 		
		String line = nodesRaf.readLine(); 		
		line = line.substring(line.indexOf("\t|\t") + 3); 		
		line = line.substring(0, line.indexOf("\t")); 		
		return Integer.valueOf(line).intValue(); 		
	}
	/** 		
	 * Returns the taxonomic rank identifier of the taxonomy node belonging to the specified taxonomy id. 		
	 * @param taxID the taxonomy id 		
	 * @return the taxonomic rank 		
	 * @throws Exception if an I/O error occurs 		
	 */ 		
	synchronized public String getRank(int taxID) throws Exception { 		

		// Get mapping 		
		int pos = nodesMap.get(taxID); 		

		// Skip to mapped byte position in nodes file 		
		nodesRaf.seek(pos); 		

		// Read line and isolate third non-numeric value 		
		String line = nodesRaf.readLine(); 		
		line = line.substring(line.indexOf("\t|\t") + 3); 		
		line = line.substring(line.indexOf("\t|\t") + 3); 		
		line = line.substring(0, line.indexOf("\t")); 		

		return line; 		
	}

	/** 		
	 * Returns the name of the taxonomy node belonging to the specified taxonomy id. 		
	 * @param taxID the taxonomy id 		
	 * @return the taxonomy node name 		
	 * @throws Exception if an I/O error occurs 		
	 */ 		
	synchronized public String getTaxonName(int taxID) throws Exception { 		

		// Get mapping 		
		int pos = namesMap.get(taxID); 		

		// Skip to mapped byte position in names file 		
		namesRaf.seek(pos); 		

		// Read line and isolate second non-numeric value 		
		String line = namesRaf.readLine(); 		
		line = line.substring(line.indexOf("\t|\t") + 3); 		
		line = line.substring(0, line.indexOf("\t")); 		

		return line; 		
	} 		
}
