package de.mpa.io.parser.kegg;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node implementation wrapping a KEGG database htext file line.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public abstract class KEGGNode extends DefaultMutableTreeNode {
	
	/**
	 * Creates a KEGG node using the specified user object.
	 * @param userObject the user object
	 */
	public KEGGNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Creates a child node from the specified KEGG htext file line.
	 * @param line the line to parse
	 * @return the created child node or <code>null</code> if parsing failed
	 */
	public abstract KEGGNode createNode(String line);
	
}
