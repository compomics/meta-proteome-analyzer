package de.mpa.io.parser.ec;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node implementation representing elements in the ENZYME nomenclature
 * database.
 * @see <a href="http://www.ebi.ac.uk/intenz/downloads.jsp">http://www.ebi.ac.uk/intenz/downloads.jsp</a>
 * @author A. Behne
 */
public class ECNode extends DefaultMutableTreeNode implements Comparable<ECNode> {

	/**
	 * The description string.
	 */
	private String description;
	
	/**
	 * The comments string.
	 */
	private String comments;

	/**
	 * Constructs an ENZYME node from the specified identifier (i.e. E.C.
	 * number).
	 * @param id the identifier
	 */
	public ECNode(String id) {
		this(id, null, null);
	}

	/**
	 * Constructs an ENZYME node from the specified identifier (i.e. E.C.
	 * number) and description.
	 * @param id the identifier
	 * @param description the description string
	 */
	public ECNode(String id, String description) {
		this(id, description, null);
	}

	/**
	 * Constructs an ENZYME node from the specified identifier (i.e. E.C.
	 * number), description and comments.
	 * @param id the identifier
	 * @param description the description string
	 * @param comments the comments string
	 */
	public ECNode(String id, String description, String comments) {
		super(id);
		this.description = description;
		this.comments = comments;
	}
	
	/**
	 * Creates a child node from the specified ENZYME class file line.
	 * @param line the line to parse
	 * @return the created child node or <code>null</code> if parsing failed
	 */
	public ECNode createNode(String line) {
		// separate E.C. number from description, e.g. "1. -. -.-  Oxidoreductases."
		String id = line.substring(0, 10).replaceAll(" ", "");
		String desc = line.substring(10).trim();
		desc = desc.substring(0, desc.length() - 1);	// trim off period
		return new ECNode(id, desc);
	}
	
	/**
	 * Returns the identifier (i.e. the E.C. number).
	 * @return the identifier
	 */
	public String getIdentifier() {
		return (String) this.userObject;
	}

	/**
	 * Returns the description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the comments.
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	@Override
	public int compareTo(ECNode that) {
		short[] thisNums = ECReader.toArray(this.getIdentifier());
		short[] thatNums = ECReader.toArray(that.getIdentifier());
		for (int i = 0; i < 4; i++) {
			int res = Short.compare(thisNums[i], thatNums[i]);
			if (res != 0) {
				return res;
			}
		}
		// fall back to alphabetical ordering
		return this.getIdentifier().compareTo(that.getIdentifier());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ECNode) {
			ECNode that = (ECNode) obj;
			return (this.getIdentifier().equals(that.getIdentifier()));
		}
		return false;
	}
	
}
