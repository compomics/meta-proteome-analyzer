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

public class GraphDatabaseResultPanel extends JPanel {

	private SortableCheckBoxTreeTable treeTable;
	protected Object filePnl;
	private JButton getResultsBtn;
	private SortableTreeTableModel sortableTreeTableModel;
	private GraphDatabaseResultPanel panel;
	private List<String> columnIdentifiers;
	
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

		getResultsBtn = new JButton("GraphDB Query", IconConstants.GO_DB_SMALL_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.GO_DB_SMALL_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.GO_DB_SMALL_PRESSED_ICON);
		getResultsBtn.setEnabled(false);
		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);
		getResultsBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				new GraphQueryDialog(ClientFrame.getInstance(), panel, "Graph Database", true);
			}
		});
		
		graphDbTtlPnl.setRightDecoration(getResultsBtn);
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
        sortableTreeTableModel = new SortableTreeTableModel(new SortableCheckBoxTreeTableNode());
		treeTable = new SortableCheckBoxTreeTable(sortableTreeTableModel);
		
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
    	
    	List<String> resultColumns = result.columns();
    	columnIdentifiers = new ArrayList<String>();
    	
		int nodeCounter = 0;
		
		for(Map<String, Object> map : result) {
    		
    		// TODO investigate result maps: entry order seems to be deterministic, but cannot be inferred from Cypher query alone
			//Map<String, Object> map = new TreeMap<String, Object>((Map<String, Object>) resIter.next());
		
    		if (nodeCounter == 0) {
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
    		MutableTreeTableNode insertionNode = (MutableTreeTableNode) findInsertionNode(
    				(SortableCheckBoxTreeTableNode) sortableTreeTableModel.getRoot(), (TreeTableNode) newChild);
    		// determine insertion depth
    		int depth = ((CheckBoxTreeTableNode) insertionNode).getPath().getPathCount() - 1;
    		// insert sub-chain as child of insertion node
    		MutableTreeTableNode node2insert = (MutableTreeTableNode) newPath.getPathComponent(depth);
    		sortableTreeTableModel.insertNodeInto(node2insert, insertionNode, insertionNode.getChildCount());
    		
		}
    	sortableTreeTableModel.setColumnIdentifiers(columnIdentifiers);
    	
	}
    
    private TreeTableNode findInsertionNode(TreeTableNode parent, TreeTableNode child2find) {
    	for (int i = 0; i < parent.getChildCount(); i++) {
			TreeTableNode treeChild = parent.getChildAt(i);
			if (treeChild.equals(child2find)) {
				return findInsertionNode(treeChild, child2find.getChildAt(0));
			}
		}
    	return parent;
    }

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
		getResultsBtn.setEnabled(enabled);
	}

}