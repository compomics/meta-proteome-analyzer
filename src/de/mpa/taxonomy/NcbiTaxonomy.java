package de.mpa.taxonomy;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.Constants;

/**
 * This class holds the NCBI taxonomy maps.
 * Class to create the phylogenic tree from NCBI: ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip
 * @author heyer
 */
public class NcbiTaxonomy implements Serializable {

	/**
	 * Instance of the NCBI taxonomy.
	 */
	private static NcbiTaxonomy instance;
	
	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'names.dmp'
	 */ 
	private TIntIntHashMap namesMap;
	
	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'nodes.dmp'
	 */
	private TIntIntHashMap nodesMap;

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
	 * Map containing known taxonomic ranks.
	 */
	private List<String> ranks = new ArrayList<String>(UniprotAccessor.TAXONOMY_MAP.keySet());
	
	/**
	 * The root node constant.
	 */
	public static final TaxonomyNode ROOT_NODE = new TaxonomyNode(1, "no rank", "root");

	/**
	 * Empty constructor for NCBI tax maps
	 */
	private NcbiTaxonomy() {
		try {
			namesRaf = new RandomAccessFile(new File(
					this.getClass().getResource(Constants.CONFIGURATION_PATH + "names.dmp").toURI()), "r");
			nodesRaf = new RandomAccessFile(new File(
					this.getClass().getResource(Constants.CONFIGURATION_PATH + "nodes.dmp").toURI()), "r");
			readIndexFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get NCBI taxonomy maps instance.
	 * @return the taxonomy maps instance.
	 */
	public static NcbiTaxonomy getInstance() {
		if (instance == null) {
			instance = new NcbiTaxonomy();
		}
		return instance;
	}
	
	/**
	 * Creates and returns the common taxonomy node above taxonomy nodes belonging to the
	 * specified taxonomy IDs.
	 * @param taxId1 the first taxonomy ID
	 * @param taxId2 the second taxonomy ID
	 * @return the common taxonomy node
	 * @throws Exception 
	 */
	synchronized public TaxonomyNode createCommonTaxonomyNode(int taxId1, int taxId2) throws Exception {
		return this.createTaxonNode(this.getCommonTaxonomyId(taxId1, taxId2));
	}

	/**
	 * Returns the common taxonomy node above the two specified taxonomy nodes.
	 * @param taxonNode1 the first taxonomy node
	 * @param taxonNode2 the second taxonomy node
	 * @return the common taxonomy node
	 * @throws Exception 
	 */
	public TaxonomyNode getCommonTaxonomyNode(TaxonomyNode taxonNode1, TaxonomyNode taxonNode2) throws Exception {
		
		// Get root paths of both taxonomy nodes
		TaxonomyNode[] path1 = taxonNode1.getPath();
		TaxonomyNode[] path2 = taxonNode2.getPath();
		
		// Find last common element starting from the root
		int len = Math.min(path1.length, path2.length);
		TaxonomyNode ancestor = path1[0];	// initialize ancestor as root
		for (int i = 1; i < len; i++) {
			if (!path1[i].equals(path2[i])) {
				break;
			}
			ancestor = path1[i];
		}
		
		return ancestor;
	}
	
	/**
	 * Finds the taxonomy level (taxID) were the 2 taxonomy levels intersect.
	 * @param taxId1 NCBI taxonomy of the first entry
	 * @param taxId2 NCBI taxonomy of the second entry
	 * @return the NCBI taxonomy ID where both entries intersect (or 0 when something went wrong)
	 * @throws Exception 
	 */
	synchronized public int getCommonTaxonomyId(int taxId1, int taxId2) throws Exception {
		
		// List of taxonomy entries for the first taxonomy entry.
		List<Integer> taxList1 = new ArrayList<Integer>();
		taxList1.add(taxId1);
		while (taxId1 != 1) {	// 1 is the root node
			try {
				taxId1 = this.getParentTaxId(taxId1);
				taxList1.add(taxId1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// List of taxonomy entries for the second taxonomy entry.
		List<Integer> taxList2 = new ArrayList<Integer>();
		taxList2.add(taxId2);
		while (taxId2 != 1) {	// 1 is the root node
			try {
				taxId2 = this.getParentTaxId(taxId2);
				taxList2.add(taxId2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Get common ancestor
		Integer taxId = 0;
		for (int i = 0; i < taxList1.size(); i++) {
			taxId = taxList1.get(i);
			if (taxList2.contains(taxId)) {
				break;
			}
		}
		
		// Find ancestor of closest known rank type
		while (!ranks.contains(this.getRank(taxId)) && (taxId != 1)) {
			taxId = this.getParentTaxId(taxId);
		}
		
		return taxId;
	}

	/**
	 * Method to determine linebreak format.
	 * @return amount of line-breaking characters per line
	 */
	private static int determineNewlineCharCount(File nodesFile) {
		int res = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(nodesFile));
			int character;
			boolean eol = false;
			while ((character = br.read()) != -1) {
				if ((character == 13) || (character == 10)) {	// 13 = carriage return '\r', 10 = newline '\n'
					res++;
					eol = true;
				} else if (eol) {
					break;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Indexes NCBI taxonomy dump files and writes them to an index file in the
	 * default folder.
	 * @throws Exception if an I/O error occurs
	 */
	public void createIndexFile() throws Exception {
		this.createIndexFile(Constants.CONFIGURATION_PATH);
	}

	/**
	 * Indexes NCBI taxonomy dump files and writes them to an index file in the
	 * specified folder.
	 * @param path path string pointing to the target folder
	 * @throws Exception 
	 */
	public void createIndexFile(String path) throws Exception {

		// NCBI names.dmp file
		File namesFile = new File(this.getClass().getResource(path + "names.dmp").toURI());

		// NCBI nodes.dmp file
		File nodesFile = new File(this.getClass().getResource(path + "nodes.dmp").toURI());
		
		// Map for NCBI names file 
		namesMap = new TIntIntHashMap();

		// Map for NCBI nodes file 
		nodesMap = new TIntIntHashMap();

		// Gets the number of chars for a new line
		int newline = determineNewlineCharCount(nodesFile);

		// The start byt position
		int pos = 0;
		
		// Read names file
		try {
			BufferedReader br = new BufferedReader(new FileReader(namesFile));
			// 1	|	root	|		|	scientific name	|
			String line;
			while ((line = br.readLine()) != null) {
				if (line.endsWith("c name\t|")) {
					String id = line.substring(0, line.indexOf('\t'));
					namesMap.put(Integer.valueOf(id), pos);
				}
				pos += line.getBytes().length + newline;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Read node file and create index map
		try {
			BufferedReader br = new BufferedReader(new FileReader(nodesFile));
			// 2	|	131567	|	superkingdom	|		|	0	|	0	|	11	|	0	|	0	|	0	|	0	|	0	|		|
			String line;
			pos = 0;
			while ((line = br.readLine()) != null) {
				String id = line.substring(0, line.indexOf('\t'));
				nodesMap.put(Integer.valueOf(id), pos);
				pos += line.getBytes().length + newline;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.writeIndexFile();
	}
	
	/**
	 * Write the index files.
	 * @throws Exception if an I/O error occurs
	 */
	private void writeIndexFile() throws Exception {
		
		File indexFile = new File(
				this.getClass().getResource(Constants.CONFIGURATION_PATH + INDEX_FILENAME).toURI());
		
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));
		oos.writeObject(namesMap);
		oos.writeObject(nodesMap);
		oos.flush();
		oos.close();
		namesMap.clear();
		nodesMap.clear();
	}
	
	/**
	 * Reads the index files.
	 * @throws Exception if an I/O error occurs
	 */
	public void readIndexFile() throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(
				this.getClass().getResourceAsStream(Constants.CONFIGURATION_PATH + INDEX_FILENAME)));
		namesMap = (TIntIntHashMap) ois.readObject();
		nodesMap = (TIntIntHashMap) ois.readObject();
		ois.close();
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
	
	/**
	 * Returns the parent taxonomy id of the taxonomy node belonging to the specified taxonomy id.
	 * @param taxId the taxonomy id
	 * @return the parent taxonomy id
	 * @throws Exception if an I/O error occurs
	 */
	synchronized public int getParentTaxId(int taxId) throws Exception {
		
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
	 * Returns a parent taxonomy node of the specified child taxonomy node.
	 * @param childNode the child node
	 * @return a parent node
	 * @throws Exception if an I/O error occurs
	 */
	synchronized public TaxonomyNode getParentTaxonomyNode(TaxonomyNode childNode) throws Exception {
		return this.getParentTaxonomyNode(childNode, true);
	}
	
	/**
	 * Returns a parent taxonomy node of the specified child taxonomy node. The
	 * parent node's rank may be forced to conform to the list of known ranks.
	 * @param childNode the child node
	 * @param knownRanksOnly <code>true</code> if the parent node's rank shall be
	 *  one of those specified in the list of known ranks, <code>false</code> otherwise
	 * @return a parent node
	 * @throws Exception if an I/O error occurs
	 */
	synchronized public TaxonomyNode getParentTaxonomyNode(TaxonomyNode childNode, boolean knownRanksOnly) throws Exception {
		
		// Get parent data
		int parentTaxId = this.getParentTaxId(childNode.getId());
		String rank = this.getRank(parentTaxId);
		
		if (knownRanksOnly) {
			// As long as parent rank is not inside list of known ranks move up in taxonomic tree
			while ((parentTaxId != 1) && !ranks.contains(rank)) {
				parentTaxId = this.getParentTaxId(parentTaxId);
				rank = this.getRank(parentTaxId);
			}
		}
		
		// Wrap parent data in taxonomy node
		return new TaxonomyNode(parentTaxId, rank, this.getTaxonName(parentTaxId));
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
	 * This method creates a TaxonNode for a certain taxID.
	 * @param taxId the taxonomy id
	 * @return The taxonNode containing the taxID, rank, taxName
	 * @throws Exception if an I/O error occurs
	 */
	public TaxonomyNode createTaxonNode(int taxId) throws Exception {
		TaxonomyNode taxNode = null;
		taxNode = new TaxonomyNode(taxId,
				this.getRank(taxId),
				this.getTaxonName(taxId));
		return taxNode;
	}
	
}