package de.mpa.taxonomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.tree.TreeNode;

/**
 * This class holds the ncbi taxonomy maps
 * Class to create the Phylogenic Tree from NCBI: ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip
 * @author heyer
 */
public class NcbiTaxonomy implements Serializable {
	
	private static NcbiTaxonomy instance;
	private TaxonNode rootNode;
	private Map<Integer, TaxonNode> leafMap;
	public static String DUMP_PATH = "src/de/mpa/resources/conf/NcbiTaxonomy.tax";

	/**
	 * Empty constructor for NCBI tax maps
	 */
	public NcbiTaxonomy() {
		readDumpedTaxonomies();
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
	 * Read the NCBI taxonomy from dumped object.
	 */
	@SuppressWarnings("unchecked")
	private void readDumpedTaxonomies() {
		try {
			File file = new File(DUMP_PATH);  
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
			rootNode = (TaxonNode) ois.readObject();
			leafMap = (Map<Integer, TaxonNode>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Dumps hierarchical NCBI taxonomy information inferred from flat file at the specified path.
	 * @param flatPath The path to the NCBI taxonomy flat file.
	 * @throws IOException
	 */
	public static void dumpTaxonomies(String flatPath) throws IOException {
		String line;
		
		// Parse nodes file
		File namesFile = new File(flatPath + "names.dmp");
		FileReader fr 		= new FileReader(namesFile);
		BufferedReader br 	= new BufferedReader(fr);
		
		Map<Integer,String> namesMap = new HashMap<Integer, String>();
		while ((line = br.readLine()) != null) {
			
			// Split after '\t|\t'
			String[] split 	= line.split("(\t\\|\t)");
			int taxid 		= Integer.parseInt(split[0]);
			
			// sci for scientific name
			if (!namesMap.containsKey(taxid) && split[3].startsWith("sci")) {
				namesMap.put(taxid, split[1]);
			}
		}
		
		// Parse nodes file
		File nodesFile = new File(flatPath + "nodes.dmp");
		fr 		= new FileReader(nodesFile);
		br 	= new BufferedReader(fr);

		Map<Integer, TaxonNode> taxMap = new TreeMap<Integer, TaxonNode>();
		Map<Integer, Integer> parentMap = new TreeMap<Integer, Integer>();
				
		while ((line = br.readLine()) != null) {
			// Split after '\t|\t'
			String[] split = line.split("(\t\\|\t)");

			// First column == taxonomy ID
			int taxId = Integer.parseInt(split[0]);

			// Second column == parent taxonomy ID
			int parentTaxId = Integer.parseInt(split[1]);
			
			// Create new taxon node from taxonomy ID and third column (rank)
			TaxonNode newTaxon = new TaxonNode(taxId, split[2], namesMap.get(taxId));
			
			// Store taxon node in map
			taxMap.put(taxId, newTaxon);
			
			// Store taxId-to-parentId in map
			if (taxId != parentTaxId) {	// ignore self-referencing root
				parentMap.put(taxId, parentTaxId);
			}
		}
		
		// Iterate taxon nodes and fill in missing parent/child links
		for (TaxonNode node : taxMap.values()) {
			if (node.getTaxId() > 1) {
				TaxonNode parentNode = taxMap.get(parentMap.get(node.getTaxId()));
				parentNode.add(node);
			}
		}
		// Gather leaf nodes in map
		TaxonNode rootNode = taxMap.get(1);
		Map<Integer, TaxonNode> leafMap = getTaxonomyLeafNodes(rootNode);
		
		// Dump everything to file
		File file = new File(DUMP_PATH);  
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
		oos.writeObject(rootNode);
		oos.writeObject(leafMap);
		oos.close();
	}
	
	/**
	 * Convenience method to recursively find all leaf nodes below an arbitrary
	 * node of an NCBI taxonomy tree.
	 * @param parent The taxon node below which the desired leaves are located.
	 * @return A map containing id-to-taxon pairs.
	 */
	private static Map<Integer, TaxonNode> getTaxonomyLeafNodes(TaxonNode parent) {
		Map<Integer, TaxonNode> res = new TreeMap<Integer, TaxonNode>();
		
		if (parent.isLeaf()) {
			res.put(parent.getTaxId(), parent);
		} else {
			int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				res.putAll(getTaxonomyLeafNodes((TaxonNode) parent.getChildAt(i)));
			}
		}
		return res;
	}
	
	/**
	 * Finds and returns the taxonomy node where two paths from the specified taxon
	 * nodes to the tree's root intersect.
	 * @param taxId1 The NCBI taxonomy ID of a leaf node.
	 * @param taxId2 The NCBI taxonomy ID of another leaf node.
	 * @return The common ancestor taxonomy node.
	 */
	public TaxonNode getCommonAncestor(int taxId1, int taxId2){
		return getCommonAncestor(leafMap.get(taxId1), leafMap.get(taxId2));
	}
	
	/**
	 * Finds and returns the taxonomy node where two paths from the specified taxon
	 * nodes to the tree's root intersect.
	 * @param taxNode1 A NCBI taxonomy node.
	 * @param taxNode2 Another NCBI taxonomy node.
	 * @return The common ancestor taxonomy node.
	 */
	public TaxonNode getCommonAncestor(TaxonNode taxNode1, TaxonNode taxNode2) {
		if (taxNode1 != taxNode2) {
			// Get path of taxon 1.
			TreeNode[] path1 = taxNode1.getPath();
			// Get path of taxon 2.
			TreeNode[] path2 = taxNode2.getPath();
			
			for (int i = 1; i < path1.length; i++) {
				// Test for difference in taxonomic path	
				if (path1[i] != path2[i]) {
					return (TaxonNode) path1[i-1];
				}
			}
		}
		return taxNode1;
	}

	/**
	 * Returns the root node of the NCBI taxonomy tree.
	 * @return The taxonomy root node.
	 */
	public TaxonNode getRootNode() {
		return rootNode;
	}
	
	/**
	 * Returns a map containing all leaf nodes of the NCBI taxonomy tree
	 * identified by their taxonomy id.
	 * @return The leaf map containing id-to-taxon pairs.
	 */
	public Map<Integer, TaxonNode> getLeafMap() {
		return leafMap;
	}
}
