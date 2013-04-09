package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.neo4j.cypher.javacompat.ExecutionResult;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.GraphDbResultTableModel;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.GraphQueryDialog;
import de.mpa.client.ui.icons.IconConstants;

public class GraphDatabaseResultPanel extends JPanel {

	private JXTable firstDimResultsTbl;
	protected Object filePnl;
	private JButton getResultsBtn;
	private GraphDbResultTableModel graphDbResultTableModel;
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
		JScrollPane firstDimResultsTblScp = new JScrollPane(firstDimResultsTbl);
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
        graphDbResultTableModel = new GraphDbResultTableModel();
		firstDimResultsTbl = new JXTable(graphDbResultTableModel);
        // register list selection listener
        firstDimResultsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	//TODO: refreshSpectrumPanel();
            }
        });

        // Single selection only
        firstDimResultsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        firstDimResultsTbl.setSelectionBackground(new Color(130, 207, 250));

        // Add nice striping effect
        firstDimResultsTbl.addHighlighter(TableConfig.getSimpleStriping());

        // Enables column control
        firstDimResultsTbl.setColumnControlVisible(true);
    }

    /**
     * Method invoked when the Get Results button is pressed.
     */
    public void updateResults(Iterator<Object> resultObjects) {
    	graphDbResultTableModel.updateDataModel(resultObjects);
	}
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}

}
