package de.mpa.client.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

public class CheckBoxTreeTableNode extends DefaultMutableTreeTableNode {

	/**
	 * The node's user objects.
	 */
	protected transient Object[] userObjects;
	
	/**
	 * The flag determining whether the selection state may be changed. 
	 * Defaults to <code>false</code>.
	 */
	private boolean fixed = false;
	
	/**
	 * URI reference for hyperlink interactivity in tree views.
	 */
	private URI uri;
	
	/**
	 * Constructs a tree table node bearing checkbox-related properties.
	 */
	public CheckBoxTreeTableNode() {
		this(new Object());
	}
	
	/**
	 * Constructs a tree table node bearing checkbox-related properties 
	 * accepting one or more objects corresponding to the node's columns.
	 * @param userObjects The objects to store.
	 */
	public CheckBoxTreeTableNode(Object... userObjects) {
		super(userObjects[0]);
		this.userObjects = userObjects;
	}

	/**
	 * Constructs a tree table node bearing checkbox-related properties 
	 * accepting a single user object and setting the node's <code>fixed</code> state flag.
	 * @param userObject The object to store.
	 * @param fixed <code>true</code> if this node's checkbox selection state 
	 * cannot be changed, <code>false</code> otherwise.
	 */
	public CheckBoxTreeTableNode(Object userObject, boolean fixed) {
		this(userObject);
		this.fixed = fixed;
	}
	
	@Override
	public Object getUserObject() {
		return this.getUserObject(0);
	}

	/**
	 * Returns this node's i-th user object. 
	 * @param i The index of the user object to retrieve.
	 * @return The i-th object stored in this node.
	 */
	private Object getUserObject(int i) {
		return this.userObjects[i];
	}

	/**
	 * Sets the user objects stored in this node. 
	 * @param userObjects The objects to store.
	 */
	public void setUserObjects(Object... userObjects) {
		this.userObjects = userObjects;
	}
	
	@Override
	public int getColumnCount() {
		return this.userObjects.length;
	}
	
	@Override
	public Object getValueAt(int column) {
		return (column >= this.userObjects.length) ? null : this.userObjects[column];
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		this.userObjects[column] = aValue;
	}
	
	/**
	 * Returns this node's <code>fixed</code> flag.
	 * @return
	 */
	public boolean isFixed() {
		return fixed;
	}
	
	/**
	 * Sets this node's <code>fixed</code> flag.
	 * @param fixed <code>true</code> if this node's checkbox selection state 
	 * cannot be changed, <code>false</code> otherwise.
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	/**
	 * Returns the {@link TreePath} leading from the tree's root to this node.
	 * @return The path to this node.
	 */
	public TreePath getPath() {
		CheckBoxTreeTableNode node = this;
	    List<CheckBoxTreeTableNode> list = new ArrayList<CheckBoxTreeTableNode>();

	    // Add all nodes to list
	    while (node != null) {
	        list.add(node);
	        node = (CheckBoxTreeTableNode) node.getParent();
	    }
	    Collections.reverse(list);

	    // Convert array of nodes to TreePath
	    return new TreePath(list.toArray());
	}
	
	/**
	 * Returns whether this node contains a valid URI reference.
	 * @return <code>true</code> if the URI is valid, <code>false</code> otherwise.
	 */
	public boolean hasURI() {
		return (uri != null);
	}

	/**
	 * Returns this node's URI reference.
	 * @return This node's URI.
	 */
	public URI getURI() {
		return uri;
	}
	
	/**
	 * Sets this node's URI reference.
	 * @param uri The URI to set.
	 */
	public void setURI(URI uri) {
		this.uri = uri;
	}
	
	/* starting here everything was copied from DefaultMutableTreeNode */

	/**
	 * Removes all child nodes from this node.
	 */
	public void removeAllChildren() {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			remove(i);
		}
	}

    /**
     * Returns true if <code>aNode</code> is a child of this node.  If
     * <code>aNode</code> is null, this method returns false.
     *
     * @return	true if <code>aNode</code> is a child of this node; false if 
     *  		<code>aNode</code> is null
     */
	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;

		if (aNode == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}

		return retval;
	}

    /**
     * Returns this node's first child.  If this node has no children,
     * throws NoSuchElementException.
     *
     * @return	the first child of this node
     * @exception	NoSuchElementException	if this node has no children
     */
	public TreeNode getFirstChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChildAt(0);
	}

    /**
     * Returns the child in this node's child array that immediately
     * follows <code>aChild</code>, which must be a child of this node.  If
     * <code>aChild</code> is the last child, returns null.  This method
     * performs a linear search of this node's children for
     * <code>aChild</code> and is O(n) where n is the number of children; to
     * traverse the entire array of children, use an enumeration instead.
     *
     * @see		#children
     * @exception	IllegalArgumentException if <code>aChild</code> is
     *					null or is not a child of this node
     * @return	the child of this node that immediately follows
     *		<code>aChild</code>
     */
	public TreeNode getChildAfter(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild); // linear search

		if (index == -1) {
			throw new IllegalArgumentException("node is not a child");
		}

		if (index < getChildCount() - 1) {
			return getChildAt(index + 1);
		} else {
			return null;
		}
	}
	
	/**
     * Returns true if <code>anotherNode</code> is a sibling of (has the
     * same parent as) this node.  A node is its own sibling.  If
     * <code>anotherNode</code> is null, returns false.
     *
     * @param	anotherNode	node to test as sibling of this node
     * @return	true if <code>anotherNode</code> is a sibling of this node
     */
	public boolean isNodeSibling(TreeNode anotherNode) {
		boolean retval;

		if (anotherNode == null) {
			retval = false;
		} else if (anotherNode == this) {
			retval = true;
		} else {
			TreeNode myParent = getParent();
			retval = (myParent != null && myParent == anotherNode.getParent());

			if (retval
					&& !((CheckBoxTreeTableNode) getParent())
							.isNodeChild(anotherNode)) {
				throw new Error("sibling has different parent");
			}
		}

		return retval;
	}

	/**
     * Returns the next sibling of this node in the parent's children array.
     * Returns null if this node has no parent or is the parent's last child.
     * This method performs a linear search that is O(n) where n is the number
     * of children; to traverse the entire array, use the parent's child
     * enumeration instead.
     *
     * @see     #children
     * @return  the sibling of this node that immediately follows this node
     */
	public CheckBoxTreeTableNode getNextSibling() {
		CheckBoxTreeTableNode retval;

		CheckBoxTreeTableNode myParent = (CheckBoxTreeTableNode) getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = (CheckBoxTreeTableNode) myParent.getChildAfter(this);	// linear search
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}
    
	/**
     * Finds and returns the first leaf that is a descendant of this node --
     * either this node or its first child's first leaf.
     * Returns this node if it is a leaf.
     *
     * @see	#isLeaf
     * @see	#isNodeDescendant
     * @return	the first leaf in the subtree rooted at this node
     */
	public CheckBoxTreeTableNode getFirstLeaf() {
		CheckBoxTreeTableNode node = this;

		while (!node.isLeaf()) {
			node = (CheckBoxTreeTableNode) node.getFirstChild();
		}

		return node;
	}
	
	public boolean isRoot() {
		return (getParent() == null);
	}

	/**
     * Returns the leaf after this node or null if this node is the
     * last leaf in the tree.
     * <p>
     * In this implementation of the <code>MutableNode</code> interface,
     * this operation is very inefficient. In order to determine the
     * next node, this method first performs a linear search in the 
     * parent's child-list in order to find the current node. 
     * <p>
     * That implementation makes the operation suitable for short
     * traversals from a known position. But to traverse all of the 
     * leaves in the tree, you should use <code>depthFirstEnumeration</code>
     * to enumerate the nodes in the tree and use <code>isLeaf</code>
     * on each node to determine which are leaves.
     *
     * @see	#depthFirstEnumeration
     * @see	#isLeaf
     * @return	returns the next leaf past this node
     */
	public CheckBoxTreeTableNode getNextLeaf() {
		CheckBoxTreeTableNode nextSibling;
		CheckBoxTreeTableNode myParent = (CheckBoxTreeTableNode) getParent();

		if (myParent == null)
			return null;

		nextSibling = getNextSibling();	// linear search

		if (nextSibling != null)
			return nextSibling.getFirstLeaf();

		return myParent.getNextLeaf();	// tail recursion
	}
	
	/**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in depth-first order.  The first node returned by the
     * enumeration's <code>nextElement()</code> method is the leftmost leaf.
     * This is the same as a postorder traversal.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see     #breadthFirstEnumeration
     * @see     #postorderEnumeration
     * @return  an enumeration for traversing the tree in depth-first order
     */
    public Enumeration<TreeNode> depthFirstEnumeration() {
        return new PostorderEnumeration(this);
    }

	final class PostorderEnumeration implements Enumeration<TreeNode> {
        protected TreeNode root;
        protected Enumeration<TreeNode> children;
        protected Enumeration<TreeNode> subtree;

        @SuppressWarnings("unchecked")
		public PostorderEnumeration(TreeNode rootNode) {
            super();
            root = rootNode;
            children = root.children();
            subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
        }

        public boolean hasMoreElements() {
            return root != null;
        }

        public TreeNode nextElement() {
            TreeNode retval;

            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderEnumeration(
                                (TreeNode)children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }

            return retval;
        }

    }  // End of class PostorderEnumeration
	
}
