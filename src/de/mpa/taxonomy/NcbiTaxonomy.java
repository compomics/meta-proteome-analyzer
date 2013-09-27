package de.mpa.taxonomy;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.TaxonomyRank;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.util.Formatter;

/**
 * This class holds the NCBI taxonomy maps.
 * Class to create the phylogenic tree from NCBI: ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip
 * FIXME Refactoring required: Move helper methods (e.g. getCommonTaxId4EachPeptide()) to TaxonomyUtils
 * @author R. Heyer
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
		
		TaxonomyNode ancestor;
		
		// Find last common element starting from the root
		int len = Math.min(path1.length, path2.length);
		if (len > 1) {
			ancestor = path1[0];	// initialize ancestor as root
			for (int i = 1; i < len; i++) {
				if (!path1[i].equals(path2[i])) {
					break;
				} 
				ancestor = path1[i];
			}
		} else {
			ancestor = taxonNode1;
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

		//		// Open stream on 'taxdmp.zip' resource file
		//		ZipInputStream zis = new ZipInputStream(this.getClass().getResourceAsStream(path + "taxdmp.zip"));
		//		
		//		ZipEntry ze;
		//		while ((ze = zis.getNextEntry()) != null) {
		//			// The number of newline chars (taxdmp.zip's contents are assumed to be unix files)
		//			int newline = 1;
		//	
		//			// The start byte position
		//			int pos = 0;
		//			
		//			// Get name of zipped file
		//			String name = ze.getName();
		//			if ("names.dmp".equals(name)) {
		//				// Map containing taxId-to-byte position pairs
		//				namesMap = new TIntIntHashMap();
		//				// Read zipped file line by line
		//				BufferedReader br = new BufferedReader(new InputStreamReader(zis));
		//				String line;
		//				while ((line = br.readLine()) != null) {
		//					// Check line for 'scientific name' type specifier, ignore other lines
		//					if (line.endsWith("c name\t|")) {
		//						// Extract taxId
		//						String id = line.substring(0, line.indexOf('\t'));
		//						// Map taxId to start-of-line byte position
		//						namesMap.put(Integer.valueOf(id), pos);
		//					}
		//					// Move position index to start of next line
		//					pos += line.getBytes().length + newline;
		//				}
		//			}
		//			if ("nodes.dmp".equals(name)) {
		//				// Map containing taxId-to-byte position pairs
		//				nodesMap = new TIntIntHashMap();
		//				// Read zipped file line by line
		//				BufferedReader br = new BufferedReader(new InputStreamReader(zis));
		//				String line;
		//				while ((line = br.readLine()) != null) {
		//					// Extract taxId
		//					String id = line.substring(0, line.indexOf('\t'));
		//					// Map taxId to start-of-line byte position
		//					nodesMap.put(Integer.valueOf(id), pos);
		//					// Move position index to start of next line
		//					pos += line.getBytes().length + newline;
		//				}
		//			}
		//		}
		//		zis.close();
		//		
		//		// Write maps out to index file
		//		this.writeIndexFile();

		// NCBI names.dmp file
		File namesFile = new File(this.getClass().getResource(path + "names.dmp").toURI());

		// NCBI nodes.dmp file
		File nodesFile = new File(this.getClass().getResource(path + "nodes.dmp").toURI());

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

		this.writeIndexFile();
	}

	/**
	 * Write the index files.
	 * @throws Exception if an I/O error occurs
	 */
	private void writeIndexFile() throws Exception {

		File indexFile;
		String name = Constants.CONFIGURATION_PATH + INDEX_FILENAME;
		URL url = this.getClass().getResource(name);
		if (url == null) {
			indexFile = new File("bin" + name);
			indexFile.createNewFile();
		} else {
			indexFile = new File(url.toURI());
		}
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));
		oos.writeObject(namesMap);
		oos.writeObject(nodesMap);
		oos.flush();
		oos.close();
		System.out.println("done");
		System.out.println(indexFile.getAbsolutePath());
		namesMap.clear();
		nodesMap.clear();
	}

	/**
	 * Reads the index files.
	 * @throws Exception if an I/O error occurs
	 */
	public void readIndexFile() throws Exception {
		InputStream inStream = this.getClass().getResourceAsStream(Constants.CONFIGURATION_PATH + INDEX_FILENAME);
		if (inStream != null) {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(inStream));
			namesMap = (TIntIntHashMap) ois.readObject();
			nodesMap = (TIntIntHashMap) ois.readObject();
			ois.close();
		} else {
			System.err.println("ERROR: \"" + Constants.CONFIGURATION_PATH + INDEX_FILENAME + "\" not found!");
		}
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

	/**
	 * Gets the tax name by the rank from the NCBI taxonomy.
	 * @param proteinHit. The proteinHit
	 * @param taxRank. The taxonomic rank
	 * @return The name of the taxonomy.
	 */
	public static String getTaxNameByRank(TaxonomyNode taxNode, TaxonomyRank taxRank) {

		// Default value for unknown
		String taxName = "unknown";

		while (taxNode.getId() != 1) { // unequal to root
			if (taxNode.getRank().equals(taxRank.toString().toLowerCase())) {
				taxName = taxNode.getName();
				break;
			}
			taxNode = taxNode.getParentNode();
		}

		return taxName; 
	}


	/**
	 * Method to check whether a taxonomy belongs to a certain group determined by a certain NCBI taxonomy number.
	 * @param taxNode. The taxon node.
	 * @param filterTaxId. The NCBI taxonomy ID.
	 * @return belongs to ? true / false
	 */
	public boolean belongsToGroup(TaxonomyNode taxNode, long filterTaxId) {

		// Standard belongs not to group
		boolean belongsToGroup = false;

		if (filterTaxId == taxNode.getId()) { // To care for same taxID and especially for root as filter level
			belongsToGroup = true;
		}else{
			// Get all parents of the taxonNode and check whether they are equal to the filter level
			while (taxNode.getParentNode()!= null || (taxNode.getId() != 1)) { // Root node
				// Get parent taxon node of protein entry
				try {
					taxNode = this.getParentTaxonomyNode(taxNode);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Check for filter ID
				if (filterTaxId == taxNode.getId()) {
					belongsToGroup = true;
					break;
				}
			}
		}
		return belongsToGroup;
	}

	/**
	 * Method to go through a peptide set and define for each peptide hit the common taxonomy of the subsequent proteins.
	 * @param peptideSet. The peptide set.
	 * @throws Exception
	 */
	public void getCommonTaxId4EachPeptide(Set<PeptideHit> peptideSet) throws Exception {
		
		// Map with taxonomy entries
		Map<Integer, TaxonomyNode> nodeMap = new HashMap<Integer, TaxonomyNode>();
		nodeMap.put(1, NcbiTaxonomy.ROOT_NODE);
		
		// Go through whole peptideSet and check for all proteineEntries the common taxonomy
		for (PeptideHit peptideHit : peptideSet) {
			
			// Gather protein taxonomy nodes
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
			
			for (ProteinHit proteinHit : peptideHit.getProteinHits()) {
				taxonNodes.add(proteinHit.getTaxonomyNode());
			}
			
			// Find common ancestor node
			TaxonomyNode ancestor = taxonNodes.get(0);
			for (int i = 0; i < taxonNodes.size(); i++) {
					ancestor = this.createCommonTaxonomyNode(ancestor.getId(), taxonNodes.get(i).getId());
			}

			// Gets the parent node of the taxon node
			TaxonomyNode child = ancestor;
			TaxonomyNode parent = nodeMap.get(ancestor.getId());
			// TODO Stimmt Funktion : Fills up all parents up to the root for the taxon node.
			if (parent == null) {
				//								newNode = ancestor;
				//								while (!newNode.isRoot()) {
				//									oldNode = newNode;
				//									newNode = nodeMap.get(oldNode.getId());
				//									if (newNode == null) {
				//										nodeMap.put(oldNode.getId(), oldNode);
				//										newNode = ncbiTaxonomy.getParentTaxonomyNode(oldNode);
				//										TaxonomyNode temp = nodeMap.get(newNode.getId());
				//										if (temp != null) {
				//											newNode = temp;
				//										}
				//										oldNode.setParentNode(newNode);
				//									} else {
				//										oldNode.setParentNode(newNode);
				//										break;
				//									}
				//								}

				parent = this.getParentTaxonomyNode(child);
				while (true) {
					TaxonomyNode temp = nodeMap.get(parent.getId());
				
					if (temp == null) {
						child.setParentNode(parent);
						nodeMap.put(parent.getId(), parent);
						child = parent;
						parent = this.getParentTaxonomyNode(parent);
					} else {
						child.setParentNode(temp);
						break;
					}
				}
			} else {
				ancestor = parent;
			}

			// set peptide hit taxon node to ancestor
			peptideHit.setTaxonomyNode(ancestor);

			// possible TODO: determine spectrum taxonomy instead of inheriting directly from peptide
			for (SpectrumMatch match : peptideHit.getSpectrumMatches()) {
				match.setTaxonomyNode(ancestor);
			}
			// fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		
	}

	/**
	 * Method to set tax ID of a protein from common taxID of its peptides.
	 * @param proteinList
	 * @throws Exception
	 */
	public void getTaxonbyPeptideTaxons(ProteinHitList proteinList) throws Exception {
		for (ProteinHit proteinHit : proteinList) {
			// gather protein taxonomy nodes
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
			for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
				taxonNodes.add(peptideHit.getTaxonomyNode());
			}
			// find common ancestor node
			TaxonomyNode ancestor = taxonNodes.get(0);
			for (int i = 1; i < taxonNodes.size(); i++) {
				ancestor = this.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
			}
			// set peptide hit taxon node to ancestor
			proteinHit.setTaxonomyNode(ancestor);
			// fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
	}
	
	/**
	 * Returns the nodes map for external use.
	 * @return Map containing the taxonomy nodes.
	 */
	public TIntIntHashMap getNodesMap() {
		return nodesMap;
	}
	
	
}
