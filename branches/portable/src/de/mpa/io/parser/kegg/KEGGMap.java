package de.mpa.io.parser.kegg;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Generic map implementation for mapping the leaves of a KEGG tree hierarchy to
 * their primary identifier.
 * @author A. Behne
 */
public class KEGGMap extends LinkedHashMap<Object, List<KEGGNode>> {

	/**
	 * The root of the tree of KEGG nodes.
	 */
	private KEGGNode root;

	/**
	 * Constructs a map of all leaf nodes below the specified KEGG tree root
	 * node.
	 * @param root the root of the KEGG tree to be mapped
	 */
	public KEGGMap(KEGGNode root) {
		this.root = root;
		this.mapLeaves();
	}

	/**
	 * Maps all leaf nodes to their name string.
	 */
	@SuppressWarnings("unchecked")
	protected void mapLeaves() {
		// iterate all tree nodes
		Enumeration<KEGGNode> dfe = root.depthFirstEnumeration();
		while (dfe.hasMoreElements()) {
			KEGGNode child = dfe.nextElement();
			// only leaf nodes are of interest
			if (child.isLeaf()) {
				// extract key
				Object key = child.getUserObject();
				// add mapping
				this.addMapping(key, child);
			}
		}
	}

	/**
	 * Maps the specified node to the specified key by appending the node to the
	 * list of already mapped nodes or by creating and inserting a new list.
	 * @param key the key
	 * @param node the node to map
	 */
	protected void addMapping(Object key, KEGGNode node) {
		List<KEGGNode> nodes = this.get(key);
		// check whether mapping does not exist yet
		if (nodes == null) {
			// create new list and store it
			nodes = new ArrayList<>();
			this.put(key, nodes);
		}
		// add node to mapping
		nodes.add(node);
	}
	
	/**
	 * Returns the root node of the mapped KEGG tree.
	 * @return the root node
	 */
	public KEGGNode getRoot() {
		return root;
	}

}
