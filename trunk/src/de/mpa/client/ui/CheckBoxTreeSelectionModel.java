package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Tree selection model used with JTree containing checkboxes.<br>
 * Based upon <a href="http://www.jroller.com/santhosh/entry/jtree_with_checkboxes">
 * http://www.jroller.com/santhosh/entry/jtree_with_checkboxes</a>
 * 
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @author Alexander Behne
 *
 */
public class CheckBoxTreeSelectionModel extends DefaultTreeSelectionModel {
	
	private TreeModel model;

	/**
	 * Class constructor using tree model.
	 * 
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
	 * 
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
	 * If <code>dig</code> is true, then a path is assumed to be selected if one of its ancestors is selected.
	 * 
	 * @param path The TreePath to be checked.
	 * @param dig The flag determining whether parent paths shall be taken into account.
	 * @return <i>boolean</i> denoting whether the path is selected.
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
	 * 
	 * @param path The TreePath to be checked.
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
	 * Checks whether <code>path1</code> is descendant of <code>path2</code>.
	 * 
	 * @param path1
	 * @param path2
	 * @return
	 */
	private boolean isDescendant(TreePath path1, TreePath path2) {
		Object obj1[] = path1.getPath();
		Object obj2[] = path2.getPath();
		for (int i = 0; i < obj2.length; i++) {
			if (obj1[i] != obj2[i]) {
				return false;
			}
		}
		return true;
	}

	// TODO: make path addition honor fixed state
	@Override
    public void addSelectionPaths(TreePath[] paths) {
        // deselect all descendants of paths[]
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

        // if all siblings are selected then deselect them and select parent recursively
        // otherwise just select that path.
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            TreePath temp = null;
            while (areSiblingsSelected(path)) {
                temp = path;
                if (path.getParentPath() == null) {
                    break;
                }
                path = path.getParentPath();
            }
            if (temp != null) {
                if (temp.getParentPath() != null) {
                    super.addSelectionPath(temp.getParentPath());
                } else {
                    if (!isSelectionEmpty()) {
                        removeSelectionPaths(getSelectionPaths());
                    }
                    super.addSelectionPaths(new TreePath[]{temp});
                }
            } else {
                super.addSelectionPaths(new TreePath[]{ path});
            }
        }
    }

    /**
     * Returns whether all siblings of given path are selected.
     * @param path
     * @return
     */
	private boolean areSiblingsSelected(TreePath path) {
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
			if (!isPathSelected(parent.pathByAddingChild(childNode))) {
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
     * Toggles selection state of ancestor and siblings of given path.
     * 
     * @param path
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
		super.removeSelectionPaths(new TreePath[] { parent });
	}
	
    // TODO: add instanceof checks to make methods work with non-CheckBoxTreeTableNode objects (default to original behavior)
    /**
     * Recursively determine whether a path is fixed or contains fixed descendants.
     * 
     * @param path The path to be examined.
     * @param dig The flag determining whether descendants shall be examined.
     * @return <code>true</code> if the path itself or at least one of its 
     * descendants is fixed, <code>false</code> otherwise.
     */
	private boolean isPathFixed(TreePath path, boolean dig) {
		if (dig) {
			Object node = path.getLastPathComponent();
			int childCount = model.getChildCount(node);
			boolean fixed = ((CheckBoxTreeTableNode) node).isFixed();
			if (!fixed) {
				for (int i = 0; i < childCount; i++) {
					Object childNode = model.getChild(node, i);
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
     * 
     * @param path The path to be examined.
     * @return <code>true</code> if the path is fixed, false otherwise.
     */
	private boolean isPathFixed(TreePath path) {
		CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) path.getLastPathComponent();
		return node.isFixed();
	}
	
	/**
	 * Recursively find all fixed descendants of a given path.
	 * 
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
