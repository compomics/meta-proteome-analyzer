package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
public class CheckBoxTreeSelectionModel extends DefaultTreeSelectionModel {
	
	private TreeModel model;

	/**
	 * Class constructor using tree model.
	 * @param model
	 */
	public CheckBoxTreeSelectionModel(TreeModel model) {
		this.model = model;
	}
	
	@Override
	public int getSelectionCount() {
		return getSelectedChildCount(new TreePath(model.getRoot()));
	}
	
	/**
	 * Recursively count selected children below given path.
	 * @param parentPath
	 * @return
	 */
	private int getSelectedChildCount(TreePath parentPath) {
		int selCount = 0;
		if (isPathSelected(parentPath, true) || isPartiallySelected(parentPath)) {
			int childCount = model.getChildCount(parentPath.getLastPathComponent());
			if (childCount == 0) {
				selCount++;
			} else {
				for (int i = 0; i < childCount; i++) {
					Object child = model.getChild(parentPath.getLastPathComponent(), i);
					TreePath childPath = parentPath.pathByAddingChild(child);
					selCount += getSelectedChildCount(childPath);
				}
			}
		}
		return selCount;
	}
	
	/**
	 * Checks whether given path is selected.<br>
	 * If <code>dig</code> is <code>true</code>, then a path is assumed to be selected 
	 * if one of its ancestors is selected.
	 * @param path The TreePath to be checked.
	 * @param dig The flag determining whether parent paths shall be taken into account.
	 * @return <code>true</code> if the path is selected, <code>false</code> otherwise.
	 */
	public boolean isPathSelected(TreePath path, boolean dig) {
		if (dig) {
			while ((path != null) && (!super.isPathSelected(path))) {
				path = path.getParentPath();
			}				
			return path != null;
		} else {
			return super.isPathSelected(path);
		}
	}

	/**
	 * Checks whether there are any unselected nodes in the sub-tree of a given path.
	 * @param path The TreePath to be checked
	 * @return <i>boolean</i> denoting whether the path is partially selected.
	 */
	public boolean isPartiallySelected(TreePath path) {
		if (!isPathSelected(path, true)) {
			TreePath[] selectionPaths = this.getSelectionPaths();
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
	 * @param pathA The first path
	 * @param pathB The second path
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
	 * @param paths The new paths to add to the current selection
	 */
	public void addSelectionPaths(List<TreePath> paths) {
		addSelectionPaths((TreePath[]) paths.toArray(new TreePath[0]));
	}

	// TODO: make path addition honor fixed state
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
            super.removeSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[0]));
        }

//        // if all siblings are selected then deselect them and select parent recursively
//        // otherwise just select that path.
//        for (int i = 0; i < paths.length; i++) {
//            TreePath path = paths[i];
//            TreePath temp = null;
//            while (areSiblingsSelected(path)) {
//                temp = path;
//                if (path.getParentPath() == null) {
//                    break;
//                }
//                path = path.getParentPath();
//            }
//            if (temp != null) {
//            	// some parent has been determined
//                if (temp.getParentPath() != null) {
//                    super.addSelectionPath(temp.getParentPath());
//                } else {
//                	// root is about to be added, clear whole selection first
//                	clearSelection();
//					super.addSelectionPaths(new TreePath[] { temp });
//                }
//            } else {
//				super.addSelectionPaths(new TreePath[] { path });
//            }
//        }

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
                super.addSelectionPath(temp.getParentPath());
            } else {
            	// root is about to be added, clear whole selection first
            	clearSelection();
				super.addSelectionPaths(new TreePath[] { temp });
            }
//        } else {
//			super.addSelectionPaths(new TreePath[] { path });
        }
    }

    /**
     * Returns whether all siblings of given path are selected.
     * @param path
     * @return <code>true</code> if all siblings are selected, 
     * <code>false</code> otherwise
     */
	public boolean areSiblingsSelected(TreePath path) {
		TreePath parent = path.getParentPath();
		if (parent == null) {
			return true;
		}
		Object node = path.getLastPathComponent();
		Object parentNode = parent.getLastPathComponent();

		int childCount = model.getChildCount(parentNode);
		for (int i = 0; i < childCount; i++) {
			Object childNode = model.getChild(parentNode, i);
			if (childNode == node) {
				continue;
			}
			if (!isPathSelected(parent.pathByAddingChild(childNode), true)) {
				return false;
			}
		}
		return true;
	}
	
    @Override
	public void removeSelectionPaths(TreePath[] paths) {
		for (int i = 0; i < paths.length; i++) {
			TreePath path = paths[i];
			if ((path.getPathCount() == 1) && !isPathFixed(path)) {
				super.removeSelectionPaths(new TreePath[] { path });
			} else {
				toggleRemoveSelection(path);
			}
		}
	}
    
    /**
     * Toggles selection state of ancestor and siblings of the given path.
     * @param path The path whose selection shall be toggled
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
				ArrayList<TreePath> toBeKept = getFixedDescendants(path);
				super.removeSelectionPaths(new TreePath[] { path });
				super.addSelectionPaths((TreePath[]) toBeKept.toArray(new TreePath[0]));
			}
			return;
		}

		super.removeSelectionPaths(new TreePath[] { parent });
		while (!stack.isEmpty()) {
			TreePath temp = (TreePath) stack.pop();
            TreePath peekPath = (stack.isEmpty()) ? path : (TreePath) stack.peek();
			Object node = temp.getLastPathComponent();
			Object peekNode = peekPath.getLastPathComponent();
			int childCount = model.getChildCount(node);
            TreePath[] childPaths = new TreePath[childCount];
            for (int i = 0; i < childCount; i++) {
                Object childNode = model.getChild(node, i);
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
     * @param path The path to be examined.
     * @param dig The flag determining whether descendants shall be examined.
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
					fixed |= isPathFixed(path.pathByAddingChild(childNode), true);
				}
			}
			return fixed;
		} else {
			return isPathFixed(path);
		}
	}

    /**
     * Determine whether a given path is fixed.
     * @param path The path to be examined.
     * @return <code>true</code> if the path is fixed, false otherwise.
     */
	private boolean isPathFixed(TreePath path) {
		CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) (path.getLastPathComponent());
		return node.isFixed();
	}
	
	/**
	 * Recursively find all fixed descendants of a given path.
	 * @param path The path to be examined.
	 * @return ArrayList of fixed TreePaths
	 */
	private ArrayList<TreePath> getFixedDescendants(TreePath path) {
		ArrayList<TreePath> fixedDesc = new ArrayList<TreePath>();
		Object node = path.getLastPathComponent();
		if (!((CheckBoxTreeTableNode) node).isFixed()) {
			int childCount = model.getChildCount(node);
			for (int i = 0; i < childCount; i++) {
				Object childNode = model.getChild(node, i);
				fixedDesc.addAll(getFixedDescendants(path.pathByAddingChild(childNode)));
			}
		} else {
			fixedDesc.add(path);
		}
		return fixedDesc;
	}
}
