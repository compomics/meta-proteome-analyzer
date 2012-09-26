package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Custom tree table node that supports multiple parents by index.
 * 
 * @author A. Behne
 */
public class PhylogenyTreeTableNode extends SortableCheckBoxTreeTableNode {
	
	/**
	 * List of tree paths of other nodes which are part of different trees. 
	 */
	private List<TreePath> linkPaths;
	
	/**
	 * Constructs a phylogenetic tree table node from an array of arbitrary Objects.<br>
	 * <b>Note</b>: Use {@link ProteinHit} as first parameter for leaves!
	 * @param userObjects
	 */
	public PhylogenyTreeTableNode(Object... userObjects) {
		super(userObjects);
	}
	
	@Override
	public int getColumnCount() {
		// TODO: parametrize column count?
		return 10;
	}
	
	@Override
	public Object getValueAt(int column) {
		if (isProtein()) {
			ProteinHit ph = (ProteinHit) userObject;
			switch (column) {
			case 0:
				return this.toString();
			case 1:
				return ph.getDescription();
			case 2:
				return ph.getSpecies();
			case 3:
				return ph.getCoverage();
			case 4:
				return ph.getMolecularWeight();
			case 5:
				return ph.getIsoelectricPoint();
			case 6:
				return ph.getPeptideCount();
			case 7:
				return ph.getSpectralCount();
			case 8:
				return ph.getEmPAI();
			case 9:
				return ph.getNSAF();
			default:
				return null;
			}
		}
		// fall-back for when none of the above applies
		return super.getValueAt(column);
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		// obligatory range check
		if (column < getColumnCount()) {
			// pad user objects array with nulls if it is too small
			if (column >= userObjects.length) {
				this.userObjects = Arrays.copyOf(userObjects, column + 1);
			}
			super.setValueAt(aValue, column);
		}
	}
	
	/**
	 * Returns whether this node stores protein hit data.
	 * @return <code>true</code> if this node contains protein hit data, 
	 * <code>false</code> otherwise
	 */
	public boolean isProtein() {
		return (userObject instanceof ProteinHit);
	}
	
	/**
	 * Returns a child identified by its string representation.<br>
	 * Make sure to use unique names!
	 * @param name The string identifier.
	 * @return The child identified by the provided string or 
	 * <code>null</code> if no such child exists.
	 */
	public MutableTreeTableNode getChildByName(String name) {
		for (MutableTreeTableNode child : children) {
			if (child.toString().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Adds a tree path of another node belonging to a different tree to 
	 * this node's list of such links. 
	 * @param treePath The foreign tree path.
	 */
	public void addLink(TreePath treePath) {
		if (linkPaths == null) {
			linkPaths = new ArrayList<TreePath>();
		}
		linkPaths.add(treePath);
	}

	/**
	 * Returns the list of links to other nodes belonging to different 
	 * trees.
	 * @return List of tree paths.
	 */
	public List<TreePath> getLinks() {
		return linkPaths;
	}
	
	/**
	 * Returns whether this node was linked to any other node via 
	 * <code>addLink()</code>.
	 * @return <code>true</code> if one or more links exist, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasLinks() {
		return ((linkPaths != null) && !linkPaths.isEmpty());
	}
	
	@Override
	public String toString() {
		if (isProtein()) {
			return ((ProteinHit) userObject).getAccession();
		}
		return super.toString();
	};
	
}
