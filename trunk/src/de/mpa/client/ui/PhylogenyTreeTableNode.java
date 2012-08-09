package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.algorithms.AggregateFunction;
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
	 * Array of per-column aggregate functions to combine numeric values 
	 * of this node's children (given there are any). 
	 */
	private AggregateFunction[] aggFcns;
	
	/**
	 * Constructs a phylogenetic tree table node from an array of arbitrary Objects.<br>
	 * <b>Note</b>: Use {@link ProteinHit} as first parameter for leaves!
	 * @param userObjects
	 */
	public PhylogenyTreeTableNode(Object... userObjects) {
		super(userObjects);
		aggFcns = new AggregateFunction[getColumnCount()];
//		aggFcns = new AggregateFunction[] {
//				null,
//				null,
//				null,
//				AggregateFunction.SUM,
//				AggregateFunction.MEAN,
//				AggregateFunction.MAX,
//				AggregateFunction.MIN,
//				AggregateFunction.MEAN,
//				AggregateFunction.MEAN,
//				AggregateFunction.MEAN
//		};
	}
	
	@Override
	public int getColumnCount() {
		// TODO: parametrize column count?
		return 10;
	}
	
	@Override
	public Object getValueAt(int column) {
		if (userObject instanceof ProteinHit) {
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
//		} else if ((this.getChildCount() > 0) &&
//				(this.getChildAt(0).getValueAt(column) instanceof Number) &&
//				(aggFcns[column] != null)) {
//			// TODO: re-write this part, e.g. cache some values because deep hierarchies 
//			// consume lots of time on repaint like this
//			double[] values = new double[this.getChildCount()];
//			for (int i = 0; i < this.getChildCount(); i++) {
//				TreeTableNode child = this.getChildAt(i);
//				values[i] = ((Number) child.getValueAt(column)).doubleValue();
//			}
//			return aggFcns[column].aggregate(values);
		}
		// fall-back for when none of the above applies
		return super.getValueAt(column);
	}
	
	@Override
	public String toString() {
		if (userObject instanceof ProteinHit) {
			return ((ProteinHit) userObject).getAccession();
		}
		return super.toString();
	};
	
	/**
	 * Returns a child identified by its string representation.<br>
	 * Make sure to use unique names!
	 * @param name The string identifier.
	 * @return The child identified by the provided string.
	 */
	public MutableTreeTableNode getChild(String name) {
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
	
	/**
	 * Returns the array of aggregate functions of this node.
	 * @return
	 */
	public AggregateFunction[] getAggregateFunctions() {
		return aggFcns;
	}
	
	public AggregateFunction getAggregateFunction(int column) {
		return (column < getColumnCount()) ? aggFcns[column] : null;
	}
	
	public void setAggregateFunction(int column, AggregateFunction aggFcn) {
		if (column < getColumnCount()) {
			aggFcns[column] = aggFcn;
		}
	}
	
}
