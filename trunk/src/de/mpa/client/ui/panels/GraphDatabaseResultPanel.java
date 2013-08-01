package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.GraphQueryDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.graphdb.cypher.CypherQuery;

public class GraphDatabaseResultPanel extends JPanel {
	
	/**
	 * Sortable checkbox treetable instance.
	 */
	private SortableCheckBoxTreeTable treeTable;
	protected Object filePnl;
	private JButton graphDbDialogBtn;
	private GraphDatabaseResultPanel panel;
	private List<String> columnIdentifiers;
	
	/**
	 * Last chosen CypherQuery.
	 */
	private CypherQuery lastCypherQuery = null;
	
	/**
	 * The GraphDatabaseResultPanel.
	 * @param clientFrame The client frame.
	 */
	public GraphDatabaseResultPanel() {
		this.panel = this;
		initComponents();
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p, 5dlu"));
		
        // Build the spectrum overview panel
        JPanel graphDbPanel = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		JXTitledPanel graphDbTtlPnl = PanelConfig.createTitledPanel("GraphDB Content", graphDbPanel);
		
		// Setup the tables
		setupFirstDimTable();
		//setupDenovoTableProperties();

		graphDbDialogBtn = new JButton("GraphDB Query", IconConstants.GO_DB_SMALL_ICON);
		graphDbDialogBtn.setRolloverIcon(IconConstants.GO_DB_SMALL_ROLLOVER_ICON);
		graphDbDialogBtn.setPressedIcon(IconConstants.GO_DB_SMALL_PRESSED_ICON);
		graphDbDialogBtn.setEnabled(false);
		graphDbDialogBtn.setPreferredSize(new Dimension(graphDbDialogBtn.getPreferredSize().width, 20));
		graphDbDialogBtn.setFocusPainted(false);
		graphDbDialogBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				new GraphQueryDialog(ClientFrame.getInstance(), panel, "GraphDB Query Dialog", true);
			}
		});
		
		graphDbTtlPnl.setRightDecoration(graphDbDialogBtn);
		JScrollPane firstDimResultsTblScp = new JScrollPane(treeTable);
		firstDimResultsTblScp.setPreferredSize(new Dimension(400, 210));
		
		firstDimResultsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		graphDbPanel.add(firstDimResultsTblScp,cc.xy(2, 2));
		
	    this.add(graphDbTtlPnl, cc.xyw(2, 2, 3));
	    //this.add(specHitsTtlPnl, cc.xy(2, 4));
	    //this.add(solutionsTtlPnl, cc.xy(4, 4));
	}
  
    /**
     * This method sets the spectra table up.
     */
    private void setupFirstDimTable() {
        // Query table
        treeTable = new SortableCheckBoxTreeTable(
        		new SortableTreeTableModel(new SortableCheckBoxTreeTableNode()));
		
        // register list selection listener
        treeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	//TODO: refreshSpectrumPanel();
            }
        });
        
        
        // Single selection only
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        treeTable.setSelectionBackground(new Color(130, 207, 250));

        // Add nice striping effect
        treeTable.addHighlighter(TableConfig.getSimpleStriping());
        
        treeTable.getTableHeader().setReorderingAllowed(true);

        // Enables column control
        treeTable.setColumnControlVisible(true);
        treeTable.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
        treeTable.getColumnControl().setOpaque(false);
		((ColumnControlButton) treeTable.getColumnControl()).setAdditionalActionsVisible(false);
        
    }

    /**
     * Method invoked when the Get Results button is pressed.
     */
    public void updateResults(ExecutionResult result) {

        SortableCheckBoxTreeTableNode root = new SortableCheckBoxTreeTableNode();
		SortableTreeTableModel model = new SortableTreeTableModel(root);
    	
    	List<String> resultColumns = result.columns();
    	columnIdentifiers = new ArrayList<String>();

		boolean first = true;
		
		for (Map<String, Object> map : result) {
    		
			// Fill column identifiers list
    		if (first) {
    			for (String col : resultColumns) {
    				Node graphNode = (Node) map.get(col);
    				for (String key : graphNode.getPropertyKeys()) {
    					if (!columnIdentifiers.contains(key)) {
        					columnIdentifiers.add(key);
    					}
    				}
				}
    		}
			
    		// iterate map and chain together elements as tree nodes
    		SortableCheckBoxTreeTableNode parentNode = null;
    		for (String col : resultColumns) {
				Node graphNode = (Node) map.get(col);
    			SortableCheckBoxTreeTableNode childNode = convertGraphToTreeNode(graphNode);
    			if (parentNode != null) {
    				parentNode.add(childNode);
    			}
    			parentNode = childNode;
    		}
    		// get first element of node chain
    		TreePath newPath = parentNode.getPath();
			Object newChild = newPath.getPathComponent(0);
    		
			// find node in existing tree to which (part of) the node chain shall be added
    		MutableTreeTableNode insertionNode = (MutableTreeTableNode) findInsertionNode(root, (TreeTableNode) newChild);
    		
    		// determine insertion depth
    		int depth = ((CheckBoxTreeTableNode) insertionNode).getPath().getPathCount() - 1;
    		
    		// insert sub-chain as child of insertion node
    		MutableTreeTableNode node2insert = (MutableTreeTableNode) newPath.getPathComponent(depth);
    		model.insertNodeInto(node2insert, insertionNode, insertionNode.getChildCount());
//    		while (!node2insert.isLeaf()) {
//    			MutableTreeTableNode child = (MutableTreeTableNode) node2insert.getChildAt(0);
//    			child.removeFromParent();
//    			model.insertNodeInto(child, node2insert, insertionNode.getChildCount());
//    			node2insert = child;
//    		}
    		
    		first = false;
		}
		model.setColumnIdentifiers(columnIdentifiers);
    	
    	treeTable.setTreeTableModel(model);
	}
    
    /**
	 * Convenience method to recursively find an existing tree node inside a
	 * tree structure headed by the specified parent node into which the specified
	 * child node shall be inserted.
	 * 
	 * @param parent the root of the tree (or subtree) structure
	 * @param child2find the child to insert into the tree structure
	 * @return the insertion node
	 */
    private TreeTableNode findInsertionNode(TreeTableNode parent, TreeTableNode child2find) {
    	for (int i = 0; i < parent.getChildCount(); i++) {
			TreeTableNode treeChild = parent.getChildAt(i);
			// FIXME: exception is thrown, if child count == 0
			if (treeChild.equals(child2find) && child2find.getChildCount() > 0) {
				return findInsertionNode(treeChild, child2find.getChildAt(0));
			}
		}
    	return parent;
    }
    
    /**
     * Converts a graph-related node into a tree node for visualization.
     * @param node Graph database node
     * @return {@link SortableCheckBoxTreeTableNode}
     */
	private SortableCheckBoxTreeTableNode convertGraphToTreeNode(Node node) {
		Object[] values = new Object[columnIdentifiers.size()];
		for (String key : node.getPropertyKeys()) {
			int index = columnIdentifiers.indexOf(key);
			values[index] = node.getProperty(key);
		}
		SortableCheckBoxTreeTableNode tableNode = new SortableCheckBoxTreeTableNode(values) {
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof TreeTableNode) {
					TreeTableNode that = (TreeTableNode) obj;
					return this.getValueAt(0).equals(that.getValueAt(0));
				}
				return false;
			}
		};
		return tableNode;
	}
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		graphDbDialogBtn.setEnabled(enabled);
	}
	
	/**
	 * Returns the last CypherQuery.
	 * @return Last chosen CypherQuery
	 */
	public CypherQuery getLastCypherQuery() {
		return lastCypherQuery;
	}
	
	/**
	 * Sets the last CypherQuery.
	 * @param lastCypherQuery Last chosen CypherQuery
	 */
	public void setLastCypherQuery(CypherQuery lastCypherQuery) {
		this.lastCypherQuery = lastCypherQuery;
	}
}
