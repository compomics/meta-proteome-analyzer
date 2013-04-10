package de.mpa.client.ui.panels;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTitledPanel;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		
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

        // Enables column control
        treeTable.setColumnControlVisible(true);
    }

    /**
     * Method invoked when the Get Results button is pressed.
     */
    public void updateResults(ExecutionResult result) {
    	
    	// Map to assign node objects in the tree table.
    	TLongObjectMap<SortableCheckBoxTreeTableNode> idToNodeMap = new TLongObjectHashMap<SortableCheckBoxTreeTableNode>();
    	
    	// Iterator for the execution result.
    	Iterator<Map<String, Object>> iterator = result.iterator();
    	while (iterator.hasNext()) {
    		int nodeCounter = 0;
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			SortableCheckBoxTreeTableNode oldNode = null, newNode = null;
			boolean rootReached = true;
			for(Entry<String, Object> e : map.entrySet()) {				
				
				Node graphNode = (Node) e.getValue();		
				// Get new node
				if(nodeCounter++ == 0) {
					newNode = convertGraphToTreeNode(graphNode);
					idToNodeMap.put(graphNode.getId(), newNode);
				} else {
					oldNode = idToNodeMap.get(graphNode.getId());
					
					if(oldNode != null) {
						sortableTreeTableModel.insertNodeInto(newNode, oldNode, oldNode.getChildCount());
						rootReached = false;
						break;
					} else {
						SortableCheckBoxTreeTableNode parentNode = convertGraphToTreeNode(graphNode);
						parentNode.add(newNode);
						//sortableTreeTableModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
						newNode = parentNode;
						idToNodeMap.put(graphNode.getId(), newNode);
					}
				}
				oldNode = newNode;
			}
			// Root of tree was reached: Add children to root.
			if(rootReached) {
				SortableCheckBoxTreeTableNode root = (SortableCheckBoxTreeTableNode) sortableTreeTableModel.getRoot();
				sortableTreeTableModel.insertNodeInto(oldNode, root, root.getChildCount());
			}
		}
    	// sortableTreeTableModel.updateDataModel(resultObjects);
	}

	private SortableCheckBoxTreeTableNode convertGraphToTreeNode(Node node) {
		List<Object> values = new ArrayList<Object>();
		for (String propertyKey : node.getPropertyKeys()) {
			values.add(node.getProperty(propertyKey));
		}
		
		SortableCheckBoxTreeTableNode tableNode = new SortableCheckBoxTreeTableNode(values.toArray());
		return tableNode;
	}
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}

}
