package de.mpa.client.ui.sharedelements.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * Tree selection model used with JTree containing checkboxes.<br>
 * Based upon <a href="http://www.jroller.com/santhosh/entry/jtree_with_checkboxes">
 * http://www.jroller.com/santhosh/entry/jtree_with_checkboxes</a>
 * 
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @author A. Behne
 *
 */
@SuppressWarnings("serial")
public class CheckBoxTreeSelectionModel extends DefaultTreeSelectionModel {
	
	/**
	 * The underlying tree model on top of which the selection model resides.
	 */
	private final TreeModel model;

	/**
	 * Creates a checkbox tree selection model using the provided tree model.
	 * @param model the tree model
	 */
	public CheckBoxTreeSelectionModel(TreeModel model) {
		this.model = model;
	}
	
	@Override
	public int getSelectionCount() {
		return this.getSelectedChildCount(new TreePath(this.model.getRoot()));
	}
	
	/**
	 * Recursively count selected children below given path.
	 * @param parentPath the parent path
	 * @return the number of selected children
	 */
	private int getSelectedChildCount(TreePath parentPath) {
		int selCount = 0;
		if (this.isPathSelected(parentPath, true) || this.isPartiallySelected(parentPath)) {
			int childCount = this.model.getChildCount(parentPath.getLastPathComponent());
			if (childCount == 0) {
				selCount++;
			} else {
				for (int i = 0; i < childCount; i++) {
					Object child = this.model.getChild(parentPath.getLastPathComponent(), i);
					TreePath childPath = parentPath.pathByAddingChild(child);
					selCount += this.getSelectedChildCount(childPath);
				}
			}
		}
		return selCount;
	}
	
	/**
	 * Checks whether given path is selected.<br>
	 * If <code>dig</code> is <code>true</code>, then a path is assumed to be selected 
	 * if one of its ancestors is selected.
	 * @param path the path to be checked.
	 * @param dig the flag determining whether parent paths shall be taken into account.
	 * @return <code>true</code> if the path is selected, <code>false</code> otherwise.
	 */
	public boolean isPathSelected(TreePath path, boolean dig) {
		if (dig) {
			while ((path != null) && (!isPathSelected(path))) {
				path = path.getParentPath();
			}				
			return path != null;
		} else {
			return isPathSelected(path);
		}
	}

	/**
	 * Checks whether there are any unselected nodes in the sub-tree of a given path.
	 * @param path the path to be checked
	 * @return <i>boolean</i> denoting whether the path is partially selected.
	 */
	public boolean isPartiallySelected(TreePath path) {
		if (!isPathSelected(path, true)) {
			TreePath[] selectionPaths = getSelectionPaths();
			if (selectionPaths != null) {
				for (int i = 0; i < selectionPaths.length; i++) {
					if (isDescendant(selectionPaths[i], path)) {
						return true;
					}
				}            	
			}
		}
		return false;
	}

	/**
	 * Checks whether <code>pathA</code> is descendant of <code>pathB</code>.
	 * @param pathA the first path
	 * @param pathB the second path
	 * @return <code>true</code> if <code>pathA</code> is descendant of 
	 * <code>pathB</code>, <code>false</code> otherwise.
	 */
	private boolean isDescendant(TreePath pathA, TreePath pathB) {
		if ((pathA == null) || (pathB == null) || 	// obligatory null checks
				(pathA.getPathCount() <= pathB.getPathCount())) {
			// pathA needs to be longer than pathB to be a descendant of it
			return false;
		} else {
			// if pathA descends from pathB, the latter's last path component 
			// must also be part of the former at the same position in the path
			return pathA.getPathComponent(pathB.getPathCount() - 1) ==
					pathB.getLastPathComponent();
		}
	}
	
	/**
	 * Convenience method to pass a List of TreePaths instead of an array.
	 * @param paths the new paths to add to the current selection
	 */
	public void addSelectionPaths(List<TreePath> paths) {
		if (!paths.isEmpty()) {
            addSelectionPaths(paths.toArray(new TreePath[0]));
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * <i>Can cause lots of events to be fired when adjusting selections. Fires
	 * an additional dummy event with a <code>null</code> path to notify of
	 * top-level processing being finished.
	 */
	@Override
    public void addSelectionPaths(TreePath[] paths) {
        // deselect all descendants of added paths
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            TreePath[] selectionPaths = getSelectionPaths();
            if (selectionPaths == null) {
                break;
            }
            ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();
            for (int j = 0; j < selectionPaths.length; j++) {
                if (isDescendant(selectionPaths[j], path)) {
                	if (!isPathFixed(path)) {
                        toBeRemoved.add(selectionPaths[j]);
                	}
                }
            }
            super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0]));
        }

        // add each path
        TreePath path = null;
        for (int i = 0; i < paths.length; i++) {
            path = paths[i];
            super.addSelectionPaths(new TreePath[] { path });
        }
		// if all siblings of last added path are selected then deselect them
		// and select parent recursively
        TreePath temp = null;
        while (areSiblingsSelected(path)) {
            temp = path;
            if (path.getParentPath() == null) {
                break;
            }
            path = path.getParentPath();
        }
        if (temp != null) {
        	// some parent has been determined
            if (temp.getParentPath() != null) {
                addSelectionPath(temp.getParentPath());
            } else {
            	// root is about to be added, clear whole selection first
                clearSelection();
				super.addSelectionPaths(new TreePath[] { temp });
            }
//        } else {
//			super.addSelectionPaths(new TreePath[] { path });
        }
        
		// super calls can create a lot of event spam, therefore fire a final
		// dummy event to notify that top-level processing is done
        fireValueChanged(new TreeSelectionEvent(this, null, true, null, null));
    }

    /**
     * Returns whether all siblings of the provided path are selected.
     * @param path the path to the node whose siblings are to be checked
     * @return <code>true</code> if all siblings are selected, 
     * <code>false</code> otherwise
     */
	public boolean areSiblingsSelected(TreePath path) {
//		if (path == null) {
//			return false;
//		}
		TreePath parent = path.getParentPath();
		if (parent == null) {
			return true;
		}
		Object node = path.getLastPathComponent();
		Object parentNode = parent.getLastPathComponent();
		
		try {
			int childCount = this.model.getChildCount(parentNode);
			for (int i = 0; i < childCount; i++) {
				Object childNode = this.model.getChild(parentNode, i);
				if (childNode == node) {
					continue;
				}
				if (!this.isPathSelected(parent.pathByAddingChild(childNode), true)) {
					return false;
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO: Do nothing?
		}
		return true;
	}
	
	/**
	 * Convenience method to pass a List of TreePaths instead of an array.
	 * @param paths the paths to remove from the current selection
	 */
	public void removeSelectionPaths(List<TreePath> paths) {
        removeSelectionPaths(paths.toArray(new TreePath[0]));
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * <i>Can cause lots of events to be fired when adjusting selections. Fires
	 * an additional dummy event with a <code>null</code> path to notify of
	 * top-level processing being finished.
	 */
    @Override
	public void removeSelectionPaths(TreePath[] paths) {
		for (int i = 0; i < paths.length; i++) {
			TreePath path = paths[i];
			if ((path.getPathCount() == 1) && !isPathFixed(path)) {
				// remove single non-fixed sub-path
				super.removeSelectionPaths(new TreePath[] { path });
			} else {
				// remove target selection while adding all siblings
                toggleRemoveSelection(path);
			}
		}
		
		// super calls can create a lot of event spam, therefore fire a final
		// dummy event to notify that top-level processing is done
        fireValueChanged(new TreeSelectionEvent(this, null, true, null, null));
	}
    
    /**
     * Toggles selection state of ancestor and siblings of the given path.
     * @param path the path whose selection shall be toggled
     */
    private void toggleRemoveSelection(TreePath path) {
        // if any ancestor node of given path is selected then deselect it 
        // and select all its descendants except given path and descendants. 
        // otherwise just deselect the given path 
		Stack<TreePath> stack = new Stack<TreePath>();
		TreePath parent = path.getParentPath();
		while ((parent != null) && (!isPathSelected(parent))) {
			stack.push(parent);
			parent = parent.getParentPath();
		}
		if (parent != null)
			stack.push(parent);
		else {
			if (!isPathFixed(path, true)) {
				super.removeSelectionPaths(new TreePath[] { path });
			} else {
				// iterate descendants and remove only non-fixed ones
				List<TreePath> toBeKept = getFixedDescendants(path);
				super.removeSelectionPaths(new TreePath[] { path });
				super.addSelectionPaths(toBeKept.toArray(new TreePath[0]));
			}
			return;
		}

		super.removeSelectionPaths(new TreePath[] { parent });
		while (!stack.isEmpty()) {
			TreePath temp = stack.pop();
            TreePath peekPath = (stack.isEmpty()) ? path : stack.peek();
			Object node = temp.getLastPathComponent();
			Object peekNode = peekPath.getLastPathComponent();
			int childCount = this.model.getChildCount(node);
            TreePath[] childPaths = new TreePath[childCount];
            for (int i = 0; i < childCount; i++) {
                Object childNode = this.model.getChild(node, i);
                if (childNode != peekNode) {
                	childPaths[i] = temp.pathByAddingChild(childNode);
                }
            }
            super.addSelectionPaths(childPaths);
		}
//		super.removeSelectionPaths(new TreePath[] { parent });
	}
	
    // TODO: add instanceof checks to make methods work with non-CheckBoxTreeTableNode objects (default to original behavior)
    /**
     * Recursively determine whether a path is fixed or contains fixed descendants.
     * @param path the path to be examined
     * @param dig the flag determining whether descendants shall be examined
     * @return <code>true</code> if the path itself or at least one of its 
     * descendants is fixed, <code>false</code> otherwise.
     */
	private boolean isPathFixed(TreePath path, boolean dig) {
		if (dig) {
			Object node = path.getLastPathComponent();
//			int childCount = model.getChildCount(node);
			int childCount = ((TreeTableNode) node).getChildCount();
			boolean fixed = ((CheckBoxTreeTableNode) node).isFixed();
			if (!fixed) {
				for (int i = 0; i < childCount; i++) {
//					Object childNode = model.getChild(node, i);
					TreeTableNode childNode = ((TreeTableNode) node).getChildAt(i);
					fixed |= this.isPathFixed(path.pathByAddingChild(childNode), true);
				}
			}
			return fixed;
		} else {
			return this.isPathFixed(path);
		}
	}

    /**
     * Determine whether a given path is fixed.
     * @param path the path to be examined
     * @return <code>true</code> if the path is fixed, false otherwise.
     */
	private boolean isPathFixed(TreePath path) {
		CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) (path.getLastPathComponent());
		return node.isFixed();
	}
	
	/**
	 * Recursively find all fixed descendants of a given path.
	 * @param path the path to be examined.
	 * @return the list of fixed tree paths
	 */
	private List<TreePath> getFixedDescendants(TreePath path) {
		List<TreePath> fixedDesc = new ArrayList<TreePath>();
		Object node = path.getLastPathComponent();
		if (!((CheckBoxTreeTableNode) node).isFixed()) {
			int childCount = this.model.getChildCount(node);
			for (int i = 0; i < childCount; i++) {
				Object childNode = this.model.getChild(node, i);
				fixedDesc.addAll(this.getFixedDescendants(path.pathByAddingChild(childNode)));
			}
		} else {
			fixedDesc.add(path);
		}
		return fixedDesc;
	}
	
}
