package de.mpa.io.parser.mascot.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class representing a post-translational amino acid modification
 * 
 * @author A. Behne
 */
public class MascotModification {
	
	/**
	 * The modification name.
	 */
	private String name;
	
	/**
	 * The amino acid residue affected by this modification.
	 */
	private char residue;
	
	/**
	 * The nominal mass delta of this modification in Da.
	 */
	private float delta;
	
	/**
	 * Flag indicating whether this is a fixed or variable modification.
	 */
	private boolean fixed;
	
	/**
	 * Creates a variable modification from the provided name string, residue character
	 * and mass delta.
	 * @param name the modification name
	 * @param residue the amino acid residue 1-letter code
	 * @param delta the nominal mass delta
	 */
	public MascotModification(String name, char residue, float delta) {
		this(name, residue, delta, false);
	}
	
	/**
	 * Creates a modification from the provided name string, residue character,
	 * mass delta and flag denoting fixed/variable status.
	 * @param name the modification name
	 * @param residue the amino acid residue 1-letter code
	 * @param delta the nominal mass delta
	 * @param fixed <code>true</code> if this modification is fixed, <code>false</code> if this is a variable modification
	 */
	public MascotModification(String name, char residue, float delta,
			boolean fixed) {
		this.name = name;
		this.residue = residue;
		this.delta = delta;
		this.fixed = fixed;
	}

	/**
	 * Creates a modification from the provided Mascot XML node.
	 * @param modNode the Mascot XML node containing modification information
	 */
	public MascotModification(Node modNode, boolean fixed) {
		NodeList childNodes = modNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if ("name".equals(item.getNodeName())) {
				String text = item.getTextContent();
				this.name = text.substring(0, text.indexOf(' '));
				this.residue = text.charAt(text.lastIndexOf(')') - 1);
			} else if ("delta".equals(item.getNodeName())) {
				this.delta = Float.parseFloat(item.getTextContent());
			}
		}
		this.fixed = fixed;
	}

	/**
	 * Returns the modification's name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the modification's name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the amino acid residue 1-letter code.
	 * @return the residue
	 */
	public char getResidue() {
		return residue;
	}

	/**
	 * Sets the amino acid residue 1-letter code.
	 * @param residue the residue to set
	 */
	public void setResidue(char residue) {
		this.residue = residue;
	}

	/**
	 * Returns the nominal mass delta in Da.
	 * @return the delta
	 */
	public float getDelta() {
		return delta;
	}

	/**
	 * Sets the nominal mass delta in Da.
	 * @param delta the delta to set
	 */
	public void setDelta(float delta) {
		this.delta = delta;
	}

	/**
	 * Returns whether this modification is fixed or variable.
	 * @return <code>true</code> if this modification is fixed, <code>false</code> if this is a variable modification
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Sets whether this modification is fixed or variable.
	 * @param fixed <code>true</code> if this modification is fixed, <code>false</code> if this is a variable modification
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.residue + ((delta > 0) ? "+" : "") + ((int) Math.round(delta)) + ")";
	}
	
}
