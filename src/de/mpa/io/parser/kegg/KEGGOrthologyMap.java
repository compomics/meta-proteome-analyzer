package de.mpa.io.parser.kegg;

import java.util.Enumeration;
import java.util.List;

/**
 * Specific map implementation for mapping the leaves of the KEGG Orthology tree
 * to both their associated K and E.C. numbers.
 * @see <a href="http://www.genome.jp/kegg-bin/get_htext?htext=ko00001.keg">
 * http://www.genome.jp/kegg-bin/get_htext?htext=ko00001.keg</a>
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class KEGGOrthologyMap extends KEGGMap {

	/**
	 * Constructs a map of all leaf nodes below the specified KEGG tree root
	 * node using K and E.C. numbers as keys.
	 * @param root the root of the KEGG orthology tree to be mapped
	 */
	public KEGGOrthologyMap(KEGGOrthologyNode root) {
		super(root);
	}

	@Override
	protected void mapLeaves() {
		// iterate all tree nodes
		Enumeration<KEGGOrthologyNode> dfe = getRoot().depthFirstEnumeration();
		while (dfe.hasMoreElements()) {
			KEGGOrthologyNode child = dfe.nextElement();
			// only leaf nodes are of interest
			if (child.isLeaf()) {
				// extract K number
				String name = child.getName();
				// add K number mapping
                addMapping(name, child);
				
				// extract E.C. numbers
				List<String> ecNumbers = child.getECNumbers();
				if (ecNumbers != null) {
					for (String ecNumber : ecNumbers) {
						// add E.C. mapping
                        this.addMapping(ecNumber, child);
					}
				}
			}
		}
	}
	
}
