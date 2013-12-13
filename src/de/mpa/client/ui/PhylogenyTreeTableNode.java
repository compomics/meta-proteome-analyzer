package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.commons.lang.ObjectUtils;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.taxonomy.TaxonomyNode;

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
		// TODO: parameterize column count, e.g. length of userObjects array?
		return 12;
	}
	
	@Override
	public Object getValueAt(int column) {
		if (this.isProtein()) {
			ProteinHit ph = (ProteinHit) userObject;
			if (ph instanceof MetaProteinHit) {
				switch (column) {
				case 0:
					return this.toString();
				case 1:
					return ph.getDescription();
				case 2:
//					return ph.getSpecies();
					return ph.getTaxonomyNode().getName();
				case 11:
					// web resource column
					return null;
				default:
					return super.getValueAt(column);
				}
			} else {
				switch (column) {
				case 0:
					return this.toString();
				case 1:
					String description = ph.getDescription();
					int underscore = description.indexOf("_");
					if (underscore > 0) {
						int whitespace = description.indexOf(" ");
						if (whitespace > underscore) {
							return description.substring(whitespace + 1);
						}
					}
					return description;
				case 2:
//					return ph.getSpecies();
					return ph.getTaxonomyNode().getName();
				case 3:
					return ph.getIdentity();	
				case 4:
					return ph.getCoverage();
				case 5:
					return ph.getMolecularWeight();
				case 6:
					return ph.getIsoelectricPoint();
				case 7:
					return ph.getPeptideCount();
				case 8:
					return ph.getSpectralCount();
				case 9:
					return ph.getEmPAI();
				case 10:
					return ph.getNSAF();
				case 11:
					return IconConstants.WEB_RESOURCE_ICON;
				default:
					return super.getValueAt(column);
				}
			}
		} else if (this.isTaxonomy()) {
			TaxonomyNode tn = (TaxonomyNode) userObject;
			switch (column) {
			case 0:
				return tn.getName();
			case 1:
				return tn.getRank();
			default:
				return super.getValueAt(column);
			}
		}
		// fall-back for when none of the above applies
		return super.getValueAt(column);
	}
	
	@Override
	public Collection<?> getValuesAt(int column) {
		if (this.isProtein()) {
			ProteinHit ph = (ProteinHit) userObject;
			switch (column) {
			case 7:
				return ph.getPeptideHitList();
			case 8:
				return ph.getSpectrumIDs();
			}
		}
		return super.getValuesAt(column);
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
	 *  <code>false</code> otherwise
	 */
	public boolean isProtein() {
		return (userObject instanceof ProteinHit);
	}
	
	/**
	 * Returns whether this node stores taxonomy data.
	 * @return <code>true</code> if this node contains taxonomy data,
	 *  <code>false</code> otherwise
	 */
	public boolean isTaxonomy() {
		return (this.userObject instanceof TaxonomyNode);
	}
	
	/**
	 * Returns a child node identified by its string representation.<br>
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
	 * Returns a child identified by its user object.<br>
	 * Make sure to always provide non-null user objects!
	 * @param usrObj the userObject identifier
	 * @return the child identified by the provided user object or
	 *  <code>null</code> if no such child exists.
	 */
	public MutableTreeTableNode getChildByUserObject(Object usrObj) {
		for (MutableTreeTableNode child : children) {
			if (child.getUserObject().equals(usrObj)) {
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
	
	@Override
	public boolean equals(Object obj) {
		// check whether the other object inherits from CheckBoxTreeTableNode
		boolean res = (obj instanceof CheckBoxTreeTableNode);
		if (res) {
			// check whether both objects have the same number of columns
			CheckBoxTreeTableNode that = (CheckBoxTreeTableNode) obj;
			res = (this.getColumnCount() == that.getColumnCount());
			if (res) {
				// check whether all column values match
				for (int i = 0; i < this.getColumnCount(); i++) {
					res &= ObjectUtils.equals(this.getValueAt(i), that.getValueAt(i));
					if (!res) {
						// abort
						break;
					}
				}
			}
		}
		return res;
	}
	
}
