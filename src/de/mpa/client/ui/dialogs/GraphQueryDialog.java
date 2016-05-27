package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;
import org.neo4j.cypher.javacompat.ExecutionResult;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.BoundsPopupMenuListener;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.GraphDatabaseResultPanel;
import de.mpa.graphdb.cypher.CypherFunctionType;
import de.mpa.graphdb.cypher.CypherMatch;
import de.mpa.graphdb.cypher.CypherOperatorType;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.cypher.CypherQueryType;
import de.mpa.graphdb.cypher.CypherStartNode;
import de.mpa.graphdb.edges.DirectionType;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.io.QueryHandler;
import de.mpa.graphdb.io.UserQueries;
import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.ElementProperty;

/**
 * The graph query dialog allows three modes:
 * a) Predefined queries
 * b) Composite queries
 * c) Console Cypher queries
 * @author Thilo Muth
 * @date 21/02/2013
 */
@SuppressWarnings("serial")
public class GraphQueryDialog extends JDialog {
	
	/**
	 * The parent panel reference.
	 */
	private GraphDatabaseResultPanel parent;

	/**
	 * The Cypher query this dialog generates.
	 */
	protected CypherQuery selectedQuery;
	
	/**
	 * The list of predefined Cypher queries.
	 */
	private JXList defaultQueryList;

	/**
	 * The panel inside the compound query pane containing controls for
	 * specifying START statements of Cypher queries.
	 */
	private StartPanel startPnl;
	
	/**
	 * The panel inside the compound query pane containing controls for
	 * specifying MATCH statements of Cypher queries.
	 */
	private MatchPanel matchPnl;

	/**
	 * The panel inside the compound query pane containing controls for
	 * specifying RETURN statements of Cypher queries.
	 */
	private ReturnPanel returnPnl;

	/**
	 * The Cypher console text area.
	 */
	private JTextArea consoleTxt;
	
	/**
	 * A rounded border for combo boxes.
	 */
	private Border roundedCbxBorder;
	
	/**
	 * A rounded border for buttons.
	 */
	private Border roundedBtnBorder;

	private boolean init;

	protected boolean consoleChanged;

	private GraphQueryDialog graphQueryDialog;
	
	private UserQueries userQueries;

	private JXList savedQueryList;

	private ComponentAdapter resizeListener;

	
	/**
	 * Graph query dialog
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 */
	public GraphQueryDialog(Frame owner, GraphDatabaseResultPanel parent, String title, boolean modal) {
		super(owner, title, modal);
		this.parent = parent;
		this.graphQueryDialog = this;
		setupUI();
		this.init = true;
		loadUserQueries();
		initComponents();
	}
	
	/**
	 * Loads the available user queries and adds them to the list view.
	 */
	private void loadUserQueries() {
		try {
			File queryFile = Constants.getUserQueriesFile();
			userQueries = QueryHandler.importUserQueries(queryFile);
		} catch (Exception e) {
			userQueries = new UserQueries();
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), "Unable to load the user queries", null, e, ErrorLevel.SEVERE, null));
			e.printStackTrace();
		}
	}

	/**
	 * Setup user interface.
	 */
	private void setupUI() {
		UIManager.put("TaskPane.titleForeground", PanelConfig.getTitleForeground());
		GradientPaint paint = (GradientPaint) (((MattePainter) PanelConfig.getTitlePainter()).getFillPaint());
		UIManager.put("TaskPane.titleBackgroundGradientStart", paint.getColor1());
		UIManager.put("TaskPane.titleBackgroundGradientEnd", paint.getColor2());
		UIManager.put("TaskPane.titleOver", paint.getColor2().darker());
		UIManager.put("TaskPane.borderColor", paint.getColor2());
		UIDefaults plasticXPdefaults = new PlasticXPLookAndFeel().getDefaults();
		roundedCbxBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 1, 0, 0, plasticXPdefaults.getColor("controlShadow")),
				BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(0, -2, 0, 0),
					plasticXPdefaults.getBorder("ComboBox.arrowButtonBorder")));
		roundedBtnBorder = BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(0, 0, 0, -2),
					plasticXPdefaults.getBorder("ToggleButton.border"));
	}

	/**
	 * Initializes and lays out all components inside this dialog.
	 */
	private void initComponents() {
		// Define dialog content pane layout
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, r:p:g, 5dlu",
				"5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Initialize task pane container
		final JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		tpc.setBackground(UIManager.getColor("ProgressBar.foreground"));
		tpc.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Init listener for dialog resizing
		resizeListener = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				pack();
			}
		};
		
		// Predefined query section
		JXTaskPane predefinedQueryTaskPane = createPredefinedQueryTaskPane();
		
		// Compound query section
//		JXTaskPane compoundQueryTaskPane = createCompoundQueryTaskPane();
		
		// Console query section
		JXTaskPane consoleQueryTaskPane = createConsoleQueryTaskPane();
		
		// Gets the last chosen query (if any).
		if (parent.getLastCypherQuery() != null) {
			
			selectedQuery = parent.getLastCypherQuery();
			
			// Compound query check.
//			if (!selectedQuery.isCustom()) {
//				startPnl.setStartNodes(selectedQuery.getStartNodes());
//				matchPnl.setMatches(selectedQuery.getMatches());
//				returnPnl.setReturnIndices(selectedQuery.getReturnIndices());
//			}
			int index = defaultQueryList.getElementCount() - 1;
			defaultQueryList.setSelectedIndex(index);
			defaultQueryList.ensureIndexIsVisible(index);
			consoleTxt.setText(selectedQuery.toString());
		} else {
			defaultQueryList.setSelectedIndex(0);
		}

		tpc.add(predefinedQueryTaskPane);
//		tpc.add(compoundQueryTaskPane);
		tpc.add(consoleQueryTaskPane);
		
		// Configure button panel containing 'OK' and 'Cancel' options
		FormLayout layout = new FormLayout("p, 5dlu:g, p, 5dlu:g, p", "p");
		JPanel buttonPnl = new JPanel(layout);
		
		// Configure 'OK' button
		JButton okBtn = new JButton("Execute", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ResultsTask().execute();
			}
		});		
		okBtn.setPreferredSize(okBtn.getPreferredSize());
		

		
		// Configure 'Cancel' button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});		
		cancelBtn.setPreferredSize(cancelBtn.getPreferredSize());
		
		// Lay out button panel
		buttonPnl.add(okBtn, CC.xy(1, 1));
		buttonPnl.add(cancelBtn, CC.xy(3, 1));
		
		// Add final components to content pane
		contentPane.add(tpc, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));
		
		init = false;
		
		// Configure size and position
		this.pack();
		Dimension size = this.getSize();
		this.setSize(new Dimension(size.width, size.height + 7));
		this.setResizable(false);
		ScreenConfig.centerInScreen(this);
		
		// Show dialog
		this.setVisible(true);
	}

	/**
	 * Creates and returns the task pane containing controls for selecting
	 * pre-defined Cypher queries.
	 * @return the predefined query task pane
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JXTaskPane createPredefinedQueryTaskPane() {
		JXTaskPane taskPane = new JXTaskPane("Predefined Queries");
		FormLayout layout = new FormLayout("p:g, 5dlu, p:g", "p, 5dlu, f:140px:g");
		layout.setColumnGroups(new int[][] { { 1, 3 } });
		taskPane.setLayout(layout);
		taskPane.setUI(new GlossyTaskPaneUI());
		
		taskPane.addComponentListener(resizeListener);
		
		Object[] defaultQueryData = CypherQueryType.values();		
		defaultQueryList = new JXList(defaultQueryData);
		// Default queries
		defaultQueryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Saved queries		
		savedQueryList = new JXList(new DefaultListModel());
		DefaultListModel savedQueryListModel = (DefaultListModel) savedQueryList.getModel();
		
		Object[] savedQueryData = userQueries.getTitleObjects();
		for (Object obj : savedQueryData) {
			savedQueryListModel.addElement(obj);
		}
		savedQueryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		taskPane.add(new JLabel("Default Queries"), CC.xy(1, 1));
		taskPane.add(new JLabel("Saved Queries"), CC.xy(3, 1));
		taskPane.add(new JScrollPane(defaultQueryList), CC.xy(1, 3));
		taskPane.add(new JScrollPane(savedQueryList), CC.xy(3, 3));
		
		return taskPane;
	}

	/**
	 * Creates and returns the task pane containing controls for interactively
	 * designing Cypher queries.
	 * @return the compound query task pane
	 */
	@SuppressWarnings("unused")
	private JXTaskPane createCompoundQueryTaskPane() {
		JXTaskPane taskPane = new JXTaskPane("Compound Queries");
		taskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		taskPane.addComponentListener(resizeListener);

		FormLayout columnLyt = new FormLayout("p:g, p:g, p:g, p:g", "f:180px:g");
		columnLyt.setColumnGroups(new int[][] { { 1, 2, 3, 4 } });
		JPanel columnPnl = new JPanel(columnLyt);
		
		int scrollInc = 16;
		
		/* START section */
		startPnl = new StartPanel();

		JScrollPane startScpn = new JScrollPane(startPnl);
		startScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		startScpn.setBorder(null);
		startScpn.getVerticalScrollBar().setUnitIncrement(scrollInc);
		
		/* MATCH section */
		matchPnl = new MatchPanel(new Object[] { });
		
		JScrollPane matchScpn = new JScrollPane(matchPnl);
		matchScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		matchScpn.setBorder(null);
		matchScpn.getVerticalScrollBar().setUnitIncrement(scrollInc);
		
		/* WHERE section */
		WherePanel wherePnl = new WherePanel(null);
		
		JScrollPane whereScpn = new JScrollPane(wherePnl);
		whereScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		whereScpn.setBorder(null);
		whereScpn.getVerticalScrollBar().setUnitIncrement(scrollInc);
		
		returnPnl = new ReturnPanel(new Object[] { });
		
		JScrollPane returnScpn = new JScrollPane(returnPnl);
		returnScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		returnScpn.setBorder(null);
		returnScpn.getVerticalScrollBar().setUnitIncrement(scrollInc);

		// Add all sections to panel
		columnPnl.add(startScpn, CC.xy(1, 1));
		columnPnl.add(matchScpn, CC.xy(2, 1));
		columnPnl.add(whereScpn, CC.xy(3, 1));
		columnPnl.add(returnScpn, CC.xy(4, 1));

		// Wrap panel in scroll pane
		JScrollPane columnScpn = new JScrollPane(columnPnl);
		columnScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		columnScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// Add scroll pane to task pane
		taskPane.add(columnScpn);
		
		return taskPane;
	}

	/**
	 * Creates and returns the task pane displaying the raw Cypher query.
	 * @return the console query task pane
	 */
	private JXTaskPane createConsoleQueryTaskPane() {
		JXTaskPane taskPane = new JXTaskPane("Cypher Console");
		taskPane.setLayout(new FormLayout("p:g", "f:p:g, 5dlu, p"));
		taskPane.setUI(new GlossyTaskPaneUI());

		// Apply component listener to synchronize task pane size with dialog size
		taskPane.addComponentListener(resizeListener);

		consoleTxt = new JTextArea(4, 0);
//		consoleTxt.setFont(new Font("Courier", consoleTxt.getFont().getStyle(), 12));
		
		// Key Listener to check whether the user has changed the console manually.
		consoleTxt.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {				
				consoleChanged = true;
				int index = defaultQueryList.getElementCount() - 1;
				defaultQueryList.setSelectedIndex(index);
				defaultQueryList.ensureIndexIsVisible(index);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				consoleChanged = true;
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				consoleChanged = true;
			}
		});

		JScrollPane consoleScpn = new JScrollPane(consoleTxt);
		consoleScpn .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScpn .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		taskPane.add(consoleScpn, CC.xy(1, 1));
		
		// Configure 'Save' button
		JButton saveBtn = new JButton("Save...", IconConstants.SAVE_ICON);
		saveBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		saveBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		saveBtn.setHorizontalAlignment(SwingConstants.LEFT);
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveQueryDialog(graphQueryDialog, "Save GraphDB Query", true);
			}
		});		
		saveBtn.setPreferredSize(saveBtn.getPreferredSize());
		
		JPanel buttonPnl = new JPanel(new FormLayout("r:p:g", "p"));
		buttonPnl.add(saveBtn, CC.xy(1, 1));
		taskPane.add(buttonPnl, CC.xy(1, 3));
		
		defaultQueryList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				int index = defaultQueryList.getSelectedIndex();
				// Disable other lists selection (if any).
				savedQueryList.clearSelection();
				if (index >= 0 && !evt.getValueIsAdjusting()) {
					if (index < (defaultQueryList.getElementCount() - 1)) {						
						selectedQuery = ((CypherQueryType) defaultQueryList.getSelectedValue()).getQuery();
						consoleTxt.setText(selectedQuery.toString());
						// If the selected query is not custom-built, then look for compound query features 
//						if(!selectedQuery.isCustom()) {							
//							startPnl.setStartNodes(selectedQuery.getStartNodes());
//							matchPnl.setMatches(selectedQuery.getMatches());
//							returnPnl.setReturnIndices(selectedQuery.getReturnIndices());
//						}
						
					}
				}
			}
		});
		
		savedQueryList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				int index = savedQueryList.getSelectedIndex();
				// Disable other lists selection (if any).
				defaultQueryList.clearSelection();
				if (index >= 0 && !evt.getValueIsAdjusting()) {					
					selectedQuery = userQueries.getQuery(index);
					consoleTxt.setText(selectedQuery.toString());
					// TODO: Enable custom-build queries to be saved.
					// if(!selectedQuery.isCustom()) {
					// startPnl.setStartNodes(selectedQuery.getStartNodes());
					// matchPnl.setMatches(selectedQuery.getMatches());
					// returnPnl.setReturnIndices(selectedQuery.getReturnIndices());
					// }
				}
			}
		});
		return taskPane;
	}
	
	/**
	 * Returns the currently selected <code>CypherQuery</code> instance.
	 * @return selected query
	 * @see CypherQuery
	 */
	public CypherQuery getSelectedQuery() {
		if(consoleChanged) selectedQuery = new CypherQuery(consoleTxt.getText());
		return selectedQuery;
	}
	
	/**
	 * Returns the user queries.
	 * @return user queries
	 * @see UserQueries
	 */
	public UserQueries getUserQueries() {
		return userQueries;
	}
	
	
	// TODO: Refactor / move to external classes!





	/**
	 * Panel implementation for dynamic editing of Cypher query START blocks.
	 * 
	 * @author A. Behne
	 */
	protected class StartPanel extends CompoundQueryPanel {
		
		/**
		 * The list of parameter combo boxes.
		 */
		private List<VariableComboBoxPanel> varPnls;
		
		/**
		 * Shared document listener instance notifying of changes made to the
		 * variable text fields.
		 */
		private DocumentListener docListener;

		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query START blocks.
		 */
		public StartPanel() {
			super("Start", NodeType.values());
			
			docListener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent evt) {
					updateStartNodes();
				}
				@Override
				public void insertUpdate(DocumentEvent evt) {
					updateStartNodes();
				}
				@Override
				public void changedUpdate(DocumentEvent evt) {
//					updateStartNodes();
				}
				/** Refresh start nodes inside Match panel */
				private void updateStartNodes() {
					matchPnl.setStartNodes(getStartNodes(), true);
				}
			};
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);
			
			// replace combo box
			this.remove(1);
			NodeType[] nodeTypes = NodeType.values();
			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(nodeTypes, nodeTypes[0].toString());
//			varPnl.addItemListener(listener);
			varPnl.addDocumentListener(docListener);
			varPnls = new ArrayList<VariableComboBoxPanel>();
			varPnls.add(varPnl);
			this.add(varPnl, CC.xyw(2, 4, 2));
			
			this.add(blockBtn, CC.xy(4, 4));
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() + 1;
			
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));

			NodeType[] nodeTypes = NodeType.values();
			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(nodeTypes, nodeTypes[0].toString());
//			varPnl.addItemListener(listener);
			varPnl.addDocumentListener(docListener);
			varPnls.add(varPnl);
			
			this.add(varPnl, CC.xyw(2, row, 2));
			this.add(blockBtn, CC.xy(4, row));
			
			if (this.getComponentCount() >= 4) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();

			// propagate change to other components
			defaultQueryList.setSelectedIndex(defaultQueryList.getElementCount() - 1);
			
			matchPnl.setStartNodes(getStartNodes(), true);
			
			updateQuery();
		}

		@Override
		public void removeBlock() {
			
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount();
			int comps = this.getComponentCount();
			
			// remove combo box from cache
			varPnls.remove(this.getComponent(comps - 2));

			// remove block button from container
			this.remove(--comps);
			// remove combo box from container
			this.remove(--comps);
			
			// remove layout rows
			layout.removeRow(row);
			layout.removeRow(row - 1);
			
			// re-add block button to container
			this.add(blockBtn, CC.xy(4, row - 3));
			
			// disable remove option when minimum number of components was reached
			if (this.getComponentCount() < 4) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();

			// propagate change to other components
			defaultQueryList.setSelectedIndex(defaultQueryList.getElementCount() - 1);
			
			updateQuery();
		}

		/**
		 * Returns the list of Cypher start nodes that are defined in this panel.
		 * @see CypherStartNode
		 * @return the list of Cypher start nodes
		 */
		public List<CypherStartNode> getStartNodes() {
			List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
			for (VariableComboBoxPanel varPnl : varPnls) {
				startNodes.add(new CypherStartNode(
						varPnl.getVariableName(),
						(NodeType) varPnl.getSelectedItem(),
						ElementProperty.IDENTIFIER, "*"));
			}
			return startNodes;
		}

		/**
		 * Resets the panel's variable combo boxes to match the provided Cypher
		 * start nodes.
		 * @param startNodes the Cypher start nodes
		 */
		public void setStartNodes(List<CypherStartNode> startNodes) {
			// add/remove blocks depending on number of existing blocks vs.
			// number of start nodes
			int blocksSize = varPnls.size();
			int nodesSize = startNodes.size();
			if (nodesSize > blocksSize) {
				// append more blocks
				for (int i = blocksSize; i < nodesSize; i++) {
					appendBlock();
				}
			} else if (nodesSize < blocksSize) {
				// remove some blocks
				for (int i = nodesSize; i < blocksSize; i++) {
					removeBlock();
				}
			}
			// set selected items and variable names
			for (int i = 0; i < nodesSize; i++) {
				CypherStartNode startNode = startNodes.get(i);
				VariableComboBoxPanel varPnl = varPnls.get(i);
				varPnl.setSelectedItem(startNode.getIndex());
				varPnl.setVariableName(startNode.getIdentifier());
			}
			// update list of start nodes in match panel
			matchPnl.setStartNodes(startNodes, false);
		}

	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query MATCH blocks.
	 * 
	 * @author A. Behne
	 */
	protected class MatchPanel extends CompoundQueryPanel {

		/**
		 * The combo box containing the list of possible start node options for
		 * the Cypher match statement this panel represents.
		 */
		@SuppressWarnings("rawtypes")
		private JComboBox startCbx;
		
		/**
		 * The list of pre-relation buttons.
		 */
		private List<DirectionButton> preBtns;
		
		/**
		 * The list of relation combo box panels.
		 */
		private List<VariableComboBoxPanel> relPnls;
		
		/**
		 * The list of target node combo box panels.
		 */
		private List<JTextField> trgTtfs;

		/**
		 * Document listener for relation variable text fields.
		 */
		private DocumentListener docListener;

		/**
		 * Item listener for relation combo boxes.
		 */
		private ItemListener itemListener;

		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query MATCH blocks.
		 * @param items the initial array of items to display in the primary combo box
		 */
		@SuppressWarnings("rawtypes")
		public MatchPanel(Object[] items) {
			super("Match", items);
			startCbx = (JComboBox) this.getComponent(1);
			preBtns = new ArrayList<DirectionButton>();
			relPnls = new ArrayList<VariableComboBoxPanel>();
			trgTtfs = new ArrayList<JTextField>();
			
			docListener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent evt) {
					updateVariables();
				}
				@Override
				public void insertUpdate(DocumentEvent evt) {
					updateVariables();
				}
				@Override
				public void changedUpdate(DocumentEvent evt) {
//					updateVariables();
				}
				private void updateVariables() {
					returnPnl.setIdentifiers(getNodeIdentifiers());
					updateQuery();
				}
			};
			itemListener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						updateQuery();
					}
				}
			};
			
			((JComboBox) this.getComponent(1)).addItemListener(itemListener);
			
			appendBlock();
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() - 1;
			
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			DirectionButtonModel model = new DirectionButtonModel();
			
			// pre-relation button
			DirectionButton preBtn = new DirectionButton(true, model);
			preBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					updateQuery();
				}
			});
			preBtns.add(preBtn);
			
			// relation combo box panel
			VariableComboBoxPanel relPnl = new VariableComboBoxPanel(RelationType.values(), "", true);
			relPnl.setBackground(new Color(245, 245, 245));
			relPnl.addDocumentListener(docListener);
			relPnl.addItemListener(itemListener);
			relPnls.add(relPnl);
			
			// post-relation button
			JButton postBtn = new DirectionButton(false, model);
			
			// target node text field
			JTextField trgTtf = new JTextField();
			trgTtf.getDocument().addDocumentListener(docListener);
			trgTtfs.add(trgTtf);
			
			this.add(preBtn, CC.xywh(4, row - 1, 1, 3));
			this.add(relPnl, CC.xyw(3, row + 2, 2));
			this.add(postBtn, CC.xywh(2, row + 1, 1, 3));
			this.add(trgTtf, CC.xyw(2, row + 4, 2));
			this.add(blockBtn, CC.xy(4, row + 4));
			
			if (this.getComponentCount() >= 11) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			// scroll to bottom
			this.scrollToBottom();
			
			updateQuery();
		}

		@Override
		public void removeBlock() {

			FormLayout matchLyt = (FormLayout) this.getLayout();
			
			AbstractButton matchBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = matchLyt.getRowCount();
			int comps = this.getComponentCount();
			
			// remove combo boxes from cache
			trgTtfs.remove(this.getComponent(comps - 2));
			relPnls.remove(this.getComponent(comps - 4));
			preBtns.remove(this.getComponent(comps - 5));

			// remove components from container
			for (int i = 1; i < 6; i++) {
				this.remove(comps - i);
			}
			// remove rows from layout
			for (int i = 0; i < 4; i++) {
				matchLyt.removeRow(row - i);
			}

			// re-add block button to container
			this.add(matchBtn, CC.xy(4, row - 5));

			// disable remove option when minimum number of components was reached
			if (this.getComponentCount() < 11) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
			
			// propagate change to other components
			returnPnl.setIdentifiers(getNodeIdentifiers());
			
			updateQuery();
		}
		
		/**
		 * Returns the list of Cypher match blocks that are defined in this panel.
		 * @see CypherMatch
		 * @return the list of Cypher match blocks
		 */
		public List<CypherMatch> getMatches() {
			List<CypherMatch> matches = new ArrayList<CypherMatch>();
			if (startCbx.getSelectedIndex() >= 0) {
				String nodeVar = startCbx.getSelectedItem().toString();
				for (int i = 0; i < relPnls.size(); i++) {
					VariableComboBoxPanel relPnl = relPnls.get(i);
					DirectionButton preBtn = preBtns.get(i);
					matches.add(new CypherMatch(
							nodeVar,
							(RelationType) relPnl.getSelectedItem(),
							relPnl.getVariableName(),
							preBtn.getDirection()));
					nodeVar = trgTtfs.get(i).getText();
				}
				matches.add(new CypherMatch(nodeVar, null, null, null));
			}
			return matches;
		}

		/**
		 * Resets the panel's variable combo boxes to match the provided Cypher
		 * match blocks.
		 * @param matches the Cypher match blocks
		 */
		public void setMatches(List<CypherMatch> matches) {
			int blocksSize = relPnls.size();
			int matchesSize = matches.size() - 1;
			if (matchesSize > blocksSize) {
				// append more blocks
				for (int i = blocksSize; i < matchesSize; i++) {
					this.appendBlock();
				}
			} else if (matchesSize < blocksSize) {
				// remove some blocks
				for (int i = matchesSize; i < blocksSize; i++) {
					this.removeBlock();
				}
			}
			// 
			for (int i = 0; i < matchesSize; i++) {
				CypherMatch match = matches.get(i);
				
				startCbx.setSelectedItem(match.getNodeIdentifier());
				
				DirectionButton preBtn = preBtns.get(i);
				preBtn.setDirection(match.getDirection());
				
				VariableComboBoxPanel relPnl = relPnls.get(i);
//				relPnl.setItems(new Object[] { match.getRelation() });
				relPnl.setSelectedItem(match.getRelation());
				relPnl.setVariableName(match.getRelationIdentifier());
				
				// target variable is stored in next match
				match = matches.get(i + 1);
				
				JTextField trgTtf = trgTtfs.get(i);
				trgTtf.setText(match.getNodeIdentifier());
			}
			
			// update list of start nodes in match panel
			returnPnl.setIdentifiers(this.getNodeIdentifiers());
		}
		
		/**
		 * Returns the vector of node identifiers that are defined in this panel.
		 * @return the node identifiers
		 */
		public Vector<String> getNodeIdentifiers() {
			Vector<String> identifiers = new Vector<String>(trgTtfs.size() + 1);
			
			identifiers.add(startCbx.getSelectedItem().toString());
			for (JTextField trgTtf : trgTtfs) {
				String var = trgTtf.getText();
				if (!var.isEmpty()) {
					identifiers.add(var);
				}
			}
			
			return identifiers;
		}
		
		/**
		 * Sets the start node combo box' options to the specified start nodes.
		 * @param startNodes the start nodes
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void setStartNodes(List<CypherStartNode> startNodes, boolean keepSelection) {
			int selectedIndex = (keepSelection) ? startCbx.getSelectedIndex() : 0;
			Object[] items = new Object[startNodes.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = startNodes.get(i).getIdentifier();
			}
			startCbx.setModel(new DefaultComboBoxModel(items));
			startCbx.setSelectedIndex(selectedIndex);
		}

		/**
		 * Button implementation for directional connector elements in Cypher query
		 * MATCH blocks.
		 * 
		 * @author A. Behne
		 */
		private class DirectionButton extends JButton {
			
			/**
			 * Flag indicating whether this component is located before or after its respective relation control.
			 */
			private boolean pre;
			
			/**
			 * The shadow color used in painting this component.
			 */
			private Color shadow = UIManager.getColor("controlDkShadow").darker();
			
			/**
			 * The highlight color used in painting this component.
			 */
			private Color highlight = UIManager.getColor("controlHighlight");
			
			/**
			 * Constructs a button containing a directional decoration for an
			 * associated relationship control.
			 * @param pre <code>true</code> if the button is located before its
			 *  associated relation, <code>false</code> if it's placed after
			 * @param model the button model to use
			 */
			public DirectionButton(boolean pre, DirectionButtonModel model) {
				// Set the model
		        this.setModel(model);
		
		        // initialize
		        this.init(null, null);
		        
				this.pre = pre;
				
				// configure visuals
				this.setBorder(null);
				this.setOpaque(false);
				this.setFocusPainted(false);
				
				// install listener to cycle direction type
				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (getDirection() == DirectionType.BOTH) {
							setDirection(DirectionType.OUT);
						} else {
							if (getDirection() == DirectionType.OUT) {
								setDirection(DirectionType.IN);
							} else {
								setDirection(DirectionType.BOTH);
							}
						}
					}
				});
			}
		
			/**
			 * Returns the direction of the relation associated with this component.
			 * @return the direction
			 */
			public DirectionType getDirection() {
				return ((DirectionButtonModel) getModel()).getDirection();
			}
			
			/**
			 * Sets the direction of the relation associated with this component.
			 * @param direction the direction to set
			 */
			public void setDirection(DirectionType direction) {
				((DirectionButtonModel) getModel()).setDirection(direction);
			}
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (pre) {
					paintPreArrow(g);
				} else {
					paintPostArrow(g);
				}
			}
		
			/**
			 * Paint arrow decoration on pre-relation button.
			 * @param g the Graphics context in which to paint
			 */
			private void paintPreArrow(Graphics g) {
				int halfHeight = getHeight() / 2 - 1;
				int halfWidth = getWidth() / 2;
				g.setColor(highlight);
				g.drawLine(0, halfHeight + 1, halfWidth, halfHeight + 1);
				g.drawLine(halfWidth + 1, halfHeight, halfWidth + 1, getHeight());
				if (getDirection() == DirectionType.IN) {
					g.fillPolygon(
							new int[] { 0, 5, 5, 4 },
							new int[] { halfHeight + 1, halfHeight - 4, halfHeight + 5, halfHeight + 5 },
							4);
				}
				g.setColor(shadow);
				g.drawLine(0, halfHeight, halfWidth, halfHeight);
				g.drawLine(halfWidth, halfHeight, halfWidth, getHeight());
				if (getDirection() == DirectionType.IN) {
					g.fillPolygon(
							new int[] { 0, 4, 4 },
							new int[] { halfHeight, halfHeight - 4, halfHeight + 4 },
							3);
				}
			}
			
			/**
			 * Paint arrow decoration on post-relation button.
			 * @param g the Graphics context in which to paint
			 */
			private void paintPostArrow(Graphics g) {
				int halfHeight = getHeight() / 2 - 1;
				int halfWidth = getWidth() / 2;
				g.setColor(highlight);
				g.drawLine(getWidth(), halfHeight + 1, halfWidth + 1, halfHeight + 1);
				g.drawLine(halfWidth + 1, halfHeight + 1, halfWidth + 1, getHeight() - 1);
				if (getDirection() == DirectionType.OUT) {
					g.fillPolygon(
							new int[] { halfWidth - 3, halfWidth + 5, halfWidth, halfWidth - 3 },
							new int[] { getHeight() - 4, getHeight() - 4, getHeight() + 1, getHeight() - 3 },
							4);
				}
				g.setColor(shadow);
				g.drawLine(getWidth(), halfHeight, halfWidth, halfHeight);
				g.drawLine(halfWidth, halfHeight, halfWidth, getHeight() - 1);
				if (getDirection() == DirectionType.OUT) {
					g.fillPolygon(
							new int[] { halfWidth - 3, halfWidth + 4, halfWidth },
							new int[] { getHeight() - 4, getHeight() - 4, getHeight() },
							3);
				}
			}
			
		}

		/**
		 * Custom button model containing directional information.
		 * 
		 * @author A. Behne
		 */
		private class DirectionButtonModel extends DefaultButtonModel {
			
			/**
			 * The direction type.
			 */
			private DirectionType direction = DirectionType.BOTH;
		
			/**
			 * Returns the direction of the relation associated with this component.
			 * @return the direction
			 */
			public DirectionType getDirection() {
				return direction;
			}
		
			/**
			 * Sets the direction of the relation associated with this component.
			 * @param direction the direction to set
			 */
			public void setDirection(DirectionType direction) {
				this.direction = direction;
				fireStateChanged();
			}
			
		}

	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query WHERE blocks.
	 * 
	 * @author A. Behne
	 */
	protected class WherePanel extends CompoundQueryPanel {
		
		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query WHERE blocks.
		 * @param items the initial array of items to display in the primary combo box
		 */
		public WherePanel(Object[] items) {
			super("Where", items);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			int row = layout.getRowCount() + 1;
			
			layout.appendRow(RowSpec.decode("f:p"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			
			CellConstraints cc = CC.xyw(2, row, 3);
			
			OperationBlock block = new OperationBlock(cc);
			
			this.add(block, cc);

			appendItem.setEnabled(false);
			removeItem.setEnabled(true);
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();
		}

		@Override
		public void removeBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			int row = layout.getRowCount();
			int comps = this.getComponentCount();

			for (int i = 1; i < 2; i++) {
				this.remove(comps - i);
			}
			for (int i = 0; i < 2; i++) {
				layout.removeRow(row - i);
			}

			appendItem.setEnabled(true);
			removeItem.setEnabled(false);
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
		}

		/**
		 * Panel containing controls for specifying parameter values for Cypher
		 * WHERE blocks.
		 * 
		 * @author A. Behne
		 */
		private class ValueBlock extends JPanel {
			
			/**
			 * This block's self-reference.
			 */
			private final ValueBlock block = this;

			/**
			 * Constructs a value block.
			 * @param cc the block's position inside its parent container
			 */
			public ValueBlock(CellConstraints cc) {
				initComponents(cc);
			}
			
			/**
			 * Initializes and configures this block's components.
			 * @param cc the block's position inside its parent container
			 */
			private void initComponents(final CellConstraints cc) {
	
				this.setLayout(new FormLayout("35px, p:g", "f:21px"));
				
				@SuppressWarnings("rawtypes")
				final JComboBox typeCbx = new TermComboBox();

				final JTextField valTtf = new JTextField();
				valTtf.setPreferredSize(new Dimension(0, 21));

				typeCbx.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent evt) {
						if (evt.getStateChange() == ItemEvent.SELECTED) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									int index = typeCbx.getSelectedIndex();
									if (index > 0) {
										Container parent = block.getParent();
										parent.remove(block);
										if (index == 2) {
											parent.add(new OperationBlock(cc), cc);
										} else {
											parent.add(new FunctionBlock(cc), cc);
										}
										((JComponent) parent).revalidate();
									}
								}
							});
						}
					}
				});
				
				this.add(typeCbx, CC.xy(1, 1));
				this.add(valTtf, CC.xy(2, 1));
			}
			
			/**
			 * ComboBox implementation for choosing term types (e.g. value,
			 * function, condition) in conditional blocks.
			 * 
			 * @author A. Behne
			 */
			@SuppressWarnings("rawtypes")
			private class TermComboBox extends JComboBox {
				
				/**
				 * Constructs a conditional term combo box displaying choices
				 * relating to sub-blocks of Cypher query MATCH blocks.
				 */
				@SuppressWarnings("unchecked")
				public TermComboBox() {
					super(new Object[] {
							new JLabel("value", IconConstants.PLUGIN_BLUE_ICON, 2),
							new JLabel("function", IconConstants.PLUGIN_PURPLE_ICON, 2),
							new JLabel("condition", IconConstants.PLUGIN_ICON, 2) });

					this.setRenderer(new DefaultListCellRenderer() {
						private Border marginBorder = BorderFactory.createEmptyBorder(0, 2, 0, 0);
						@Override
						public Component getListCellRendererComponent(JList list,
								Object value, int index, boolean isSelected,
								boolean cellHasFocus) {
							JComponent comp = (JComponent) value;
							comp.setOpaque(true);
							comp.setBackground((isSelected) ? getBackground().darker() : getBackground());
							comp.setBorder((index >= 0) ? marginBorder : BorderFactory.createEmptyBorder(2, 0, 0, 0));
							((JLabel) comp).setVerticalAlignment(SwingConstants.TOP);
							return comp;
						}
					});
					this.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
				}
				
			}
			
		}

		/**
		 * Panel containing controls for specifying functions with a single
		 * parameter for Cypher WHERE blocks.
		 * 
		 * @author A. Behne
		 */
		private class FunctionBlock extends JPanel {
			
			/**
			 * Self-reference.
			 */
			private FunctionBlock block = this;
			
			/**
			 * Constructs a function block.
			 * @param cc the block's position inside its parent container
			 */
			public FunctionBlock(CellConstraints cc) {
				super();
				initComponents(cc);
			}
			
			/**
			 * Initializes and configures this block's components.
			 * @param cc the block's position inside its parent container
			 */
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private void initComponents(final CellConstraints cc) {
				
				this.setLayout(new FormLayout("11px, p:g", "f:21px, 3dlu, f:21px"));
		
				BracketButton bracketBtn = new BracketButton();
				
				bracketBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						Container parent = getParent();
						parent.remove(block);
						parent.add(new ValueBlock(cc), cc);
						((JComponent) parent).revalidate();
					}
				});
				
				this.add(bracketBtn, CC.xywh(1, 1, 1, 3));
				this.add(new JComboBox(CypherFunctionType.values()), CC.xy(2, 1));
				this.add(new JTextField("variable"), CC.xy(2, 3));
			}
			
		}

		/**
		 * Panel containing controls for specifying parameter operations for
		 * Cypher WHERE blocks.
		 * 
		 * @author A. Behne
		 */
		private class OperationBlock extends JPanel {

			/**
			 * This block's self-reference.
			 */
			private OperationBlock block = this;
			
			/**
			 * Constructs an operation block.
			 * @param cc the block's position inside its parent container
			 */
			public OperationBlock(CellConstraints cc) {
				super();
				initComponents(cc);
			}
			
			/**
			 * Initializes and configures this block's components.
			 * @param cc the block's position inside its parent container
			 */
			private void initComponents(final CellConstraints cc) {
	
				this.setLayout(new FormLayout("11px, 35px, p:g, p, p:g", "f:p, 3dlu, f:p, 3dlu, f:p"));
				
				ValueBlock leftTerm = new ValueBlock(CC.xyw(2, 1, 4));
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JComboBox operatorCbx = new JComboBox(CypherOperatorType.values());
				
				ValueBlock rightTerm = new ValueBlock(CC.xyw(2, 5, 4));
	
				BracketButton bracketBtn = new BracketButton(true);
				
				bracketBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						Container parent = getParent();
						if (parent instanceof WherePanel) {
							removeBlock();
						} else {
							parent.remove(block);
							parent.add(new ValueBlock(cc), cc);
							((JComponent) parent).revalidate();
						}
					}
				});
				
				this.add(bracketBtn, CC.xywh(1, 1, 1, 5));
				this.add(leftTerm, CC.xyw(2, 1, 4));
				this.add(operatorCbx, CC.xy(4, 3));
				this.add(rightTerm, CC.xyw(2, 5, 4));
			}
			
		}

		/**
		 * Button implementation to display an enclosing bracket for conditional
		 * blocks.
		 * 
		 * @author A. Behne
		 */
		private class BracketButton extends JButton {
			
			private boolean rounded;
			
			/**
			 * The shadow color used in painting this component.
			 */
			private Color shadow = UIManager.getColor("controlDkShadow").darker();
			
			/**
			 * The highlight color used in painting this component.
			 */
			private Color highlight = UIManager.getColor("controlHighlight");
			
			/**
			 * Constructs a button looking like a bracket enclosing a conditional block.
			 */
			public BracketButton() {
				this(false);
			}
			
			/**
			 * Constructs a button looking like a bracket enclosing a conditional block.
			 * @param rounded <code>true</code> if the bracket's corner shall be rounded, 
			 * <code>false</code> otherwise
			 */
			public BracketButton(boolean rounded) {
				super(IconConstants.createEmptyIcon(9, 10));
				
				this.rounded = rounded;
				
				// initialize icons
				this.setRolloverEnabled(true);
				this.setRolloverIcon(IconConstants.CROSS_SMALL_ROLLOVER_ICON);
				this.setPressedIcon(IconConstants.CROSS_SMALL_PRESSED_ICON);
				// configure visuals
				this.setBorder(null);
				this.setContentAreaFilled(false);
				this.setFocusPainted(false);
				this.setMargin(new Insets(0, 0, 0, 0));
				this.setHorizontalAlignment(SwingConstants.LEFT);
			}
			
			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(
				        RenderingHints.KEY_ANTIALIASING,
				        RenderingHints.VALUE_ANTIALIAS_ON);
				// draw highlights
				g2d.setColor(highlight);
				if (rounded) {
					g2d.drawArc(5, 1, 7, 7, 90, 90);
					g2d.drawLine(5, 5, 5, getHeight() - 5);
					g2d.drawArc(4, getHeight() - 9, 8, 8, 180, 90);
				} else {
					g2d.drawLine(5, 1, 8, 1);
					g2d.drawLine(5, 1, 5, getHeight() - 1);
					g2d.drawLine(4, getHeight() - 1, 8, getHeight() - 1);
				}
				// draw shadows
				g2d.setColor(shadow);
				if (rounded) {
					g2d.drawArc(4, 0, 7, 7, 90, 90);
					g2d.drawLine(4, 4, 4, getHeight() - 6);
					g2d.drawArc(4, getHeight() - 9, 7, 7, 180, 90);
				} else {
					g2d.drawLine(4, 0, 7, 0);
					g2d.drawLine(4, 1, 4, getHeight() - 2);
					g2d.drawLine(4, getHeight() - 2, 7, getHeight() - 2);
				}
				
				super.paint(g2d);
			}
		}
		}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query RETURN blocks.
	 * 
	 * @author A. Behne
	 */
	protected class ReturnPanel extends CompoundQueryPanel {
		
		/**
		 * The vector of available node identifiers.
		 */
		private Vector<String> identifiers;
		
		/**
		 * The list of identifier combo boxes.
		 */
		@SuppressWarnings("rawtypes")
		private List<JComboBox> idCbxs;
		
		/**
		 * The item listener instance shared among the identifier combo boxes.
		 */
		private ItemListener listener;
		
		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query RETURN blocks.
		 * @param items the initial array of items to display in the primary combo box
		 */
		@SuppressWarnings("rawtypes")
		public ReturnPanel(Object[] items) {
			super("Return", items);
			
			listener = new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						updateQuery();
					}
				}
			};
			
			JComboBox varCbx = (JComboBox) this.getComponent(1);
			varCbx.addItemListener(listener);
			
			idCbxs = new ArrayList<JComboBox>();
			idCbxs.add(varCbx);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() + 1;
			
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			JComboBox varCbx = new JComboBox(identifiers);
			varCbx.addItemListener(listener);
			idCbxs.add(varCbx);
			
			this.add(varCbx, CC.xyw(2, row, 2));
			this.add(blockBtn, CC.xy(4, row));
			
			if (this.getComponentCount() >= 3) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();
			
			updateQuery();
		}

		@Override
		public void removeBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount();
			int comps = this.getComponentCount();
			
			// remove combo box from cache
			idCbxs.remove(this.getComponent(comps - 2));

			// remove block button from container
			this.remove(--comps);
			// remove combo box from container
			this.remove(--comps);
			
			// remove layout rows
			layout.removeRow(row);
			layout.removeRow(row - 1);
			
			// re-add block button to container
			this.add(blockBtn, CC.xy(4, row - 3));
			
			// disable remove option when minimum number of components was reached
			if (this.getComponentCount() < 4) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
			
			updateQuery();
		}
		
		/**
		 * Sets the stored identifiers to the specified vector.
		 * @param identifiers the identifiers to set
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void setIdentifiers(Vector<String> identifiers) {
			this.identifiers = identifiers;
			for (JComboBox idCbx : idCbxs) {
				int index = idCbx.getSelectedIndex();
				idCbx.setModel(new DefaultComboBoxModel(identifiers));
				if (identifiers.size() > index) {
					idCbx.setSelectedIndex(index);
				}
			}
		}
		
		/**
		 * Sets the stored indices to the specified list.
		 * @param indices the indices to set
		 */
		public void setReturnIndices(List<Integer> indices) {
			// add/remove blocks depending on number of existing blocks vs.
			// number of start nodes
			int blocksSize = idCbxs.size();
			int indicesSize = indices.size();
			if (indicesSize > blocksSize) {
				// append more blocks
				for (int i = blocksSize; i < indicesSize; i++) {
					appendBlock();
				}
			} else if (indicesSize < blocksSize) {
				// remove some blocks
				for (int i = indicesSize; i < blocksSize; i++) {
					removeBlock();
				}
			}
			// set selected items and variable names
			for (int i = 0; i < indicesSize; i++) {
				idCbxs.get(i).setSelectedIndex(indices.get(i));
			}
		}
		
		/**
		 * Returns the identifier indices
		 * @return the indices
		 */
		public List<Integer> getReturnIndices() {
			List<Integer> indices = new ArrayList<Integer>();
			for (@SuppressWarnings("rawtypes") JComboBox idCbx : idCbxs) {
				indices.add(idCbx.getSelectedIndex());
			}
			return indices;
		}
		
	}
	
	/**
		 * Wrapper panel containing a combo box and a button which prompts to enter
		 * a variable name.
		 * 
		 * @author A. Behne
		 */
		private class VariableComboBoxPanel extends JPanel {
	
			/**
			 * The button for toggling the popup's visibility.
			 */
			private JToggleButton varBtn;
			
			/**
			 * The parameter combo box.
			 */
			@SuppressWarnings("rawtypes")
			private JComboBox varCbx;
	
			/**
			 * The variable name text field.
			 */
			private JTextField varTtf;
	
			/**
			 * Constructs a wrapper panel containing a combo box bearing the
			 * specified items and a button prompting to enter a variable name.
			 * @param items the items to be displayed inside the combo box
			 */
			public VariableComboBoxPanel(Object[] items, String var) {
				this(items, var, false);
			}
	
			/**
			 * Constructs a wrapper panel containing a combo box bearing the
			 * specified items and a button prompting to enter a variable name and
			 * optionally applies a rounded corner border to both components
			 * @param items the items to be displayed inside the combo box
			 * @param rounded <code>true</code> if rounded corners shall be used,
			 *  <code>false</code> otherwise
			 */
			public VariableComboBoxPanel(Object[] items, String var, boolean rounded) {
				super(new FormLayout("21px, p:g", "f:21px"));
				initComponents(items, var, rounded);
			}
	
			/**
			 * Creates and initializes the panel components.
			 * @param items the items to be displayed inside the combo box
			 * @param rounded <code>true</code> if rounded corners shall be used,
			 *  <code>false</code> otherwise
			 */
			@SuppressWarnings({ "rawtypes", "unchecked" })
			private void initComponents(Object[] items, String var, boolean rounded) {
				varBtn = new JToggleButton(IconConstants.TEXTFIELD_ICON);
				varBtn.setRolloverIcon(IconConstants.TEXTFIELD_ROLLOVER_ICON);
				varBtn.setPressedIcon(IconConstants.TEXTFIELD_PRESSED_ICON);
				
				final JPopupMenu varPop = new JPopupMenu("test");
				
				varTtf = new JTextField(var);
				varTtf.setMargin(new Insets(0, 1, 0, 0));
				varTtf.setBorder(null);
				
				varTtf.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent e) {
						updateQuery();
					}
					@Override
					public void insertUpdate(DocumentEvent e) {
						updateQuery();
					}
					@Override
					public void changedUpdate(DocumentEvent e) {
	//					updateQuery();
					}
				});
				
				varPop.add(varTtf);
				
				varBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if (varBtn.isSelected()) {
							// synchronize popup size with panel size
							varPop.setPreferredSize(getSize());
							// show popup and transfer focus to text field inside
							varPop.show(varBtn, 0, varBtn.getHeight());
							varTtf.requestFocus();
						} else {
							varPop.setVisible(false);
						}
					}
				});
				
				varPop.addPopupMenuListener(new PopupMenuListener() {
					public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
						if (varBtn.isSelected()) {
							varBtn.getModel().setSelected(false);
						}
					}
					public void popupMenuWillBecomeVisible(PopupMenuEvent evt) { }
					public void popupMenuCanceled(PopupMenuEvent evt) { }
				});
				
				if (rounded) {
					varBtn.setBorder(roundedBtnBorder);
					varBtn.setMargin(new Insets(2, 4, 1, 4));
					
					Border oldBorder = UIManager.getBorder("ComboBox.arrowButtonBorder");
					UIManager.put("ComboBox.arrowButtonBorder", roundedCbxBorder);
					varCbx = new JComboBox(items);
					UIManager.put("ComboBox.arrowButtonBorder", oldBorder);
				} else {
					varBtn.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(0, 0, 0, -2),
							varBtn.getBorder()));
					
					varCbx = new JComboBox(items);
					
					varCbx.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent evt) {
							varTtf.setText(evt.getItem().toString().toLowerCase());
							updateQuery();
						}
					});
				}
				varCbx.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
				varCbx.setPreferredSize(new Dimension(0, varCbx.getPreferredSize().height));
				
				// prevent button clicks dismissing popups
		        varBtn.putClientProperty("doNotCancelPopup",
		        		varCbx.getClientProperty("doNotCancelPopup"));
				
				this.add(varBtn, CC.xy(1, 1));
				this.add(varCbx, CC.xy(2, 1));
			}
			
			@Override
			public void setBackground(Color bg) {
				super.setBackground(bg);
				if (varBtn != null) {
					varBtn.setBackground(bg);
				}
				if (varCbx != null) {
					varCbx.setBackground(bg);
				}
			}
			
			/**
			 * Adds an item listener to this panel's combo box.
			 * @param listener the listener to add
			 */
			public void addItemListener(ItemListener listener) {
				varCbx.addItemListener(listener);
			}
			
			/**
			 * Adds a document listener to this panel's variable text field.
			 * @param listener the listener to add
			 */
			public void addDocumentListener(DocumentListener listener) {
				varTtf.getDocument().addDocumentListener(listener);
			}
			
			/**
			 * Returns the variable name.
			 * @return the variable name
			 */
			public String getVariableName() {
				return varTtf.getText();
			}
			
			/**
			 * Sets the variable name.
			 * @param var the variable name to set
			 */
			public void setVariableName(String var) {
				varTtf.setText(var);
			}
			
	//		/**
	//		 * Sets the combo box' items.
	//		 * @param items the items to set
	//		 */
	//		public void setItems(Object[] items) {
	//			varCbx.setModel(new DefaultComboBoxModel(items));
	//		}
			
			/**
			 * Returns the combo box' selected item.
			 * @return the selected item
			 */
			public Object getSelectedItem() {
				return varCbx.getSelectedItem();
			}
			
			/**
			 * Sets the combo box' selected item.
			 * @param item the item to select
			 */
			public void setSelectedItem(Object item) {
				varCbx.setSelectedItem(item);
			}
			
		}

	/**
	 * Convenience method to gather all blocks of the compound Cypher query from
	 * their respective components.
	 */
	private void updateQuery() {
		if (!init) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					selectedQuery.setStartNodes(startPnl.getStartNodes());
					selectedQuery.setMatches(matchPnl.getMatches());
					// TODO: query.setConditions(wherePnl.getConditions());
					selectedQuery.setReturnIndices(returnPnl.getReturnIndices());
					consoleTxt.setText(selectedQuery.toString());
				}
			});
		}
	}
	
	/**
	 * Class to fetch graph database search results in a background thread.
	 * 
	 * @author T. Muth
	 */
	@SuppressWarnings("rawtypes")
	private class ResultsTask extends SwingWorker {
		
		/**
		 * The execution result of the selected Cypher query.
		 */
		private ExecutionResult result;
	
		@Override
		protected Object doInBackground() {
			try {
				// Retrieve the query from the console, if user has changed text there.
				if (consoleChanged) {
					selectedQuery = new CypherQuery(consoleTxt.getText());					
				}
				
				// Set last cypher query
				parent.setLastCypherQuery(selectedQuery);
				
				// Execute query, store result
				GraphDatabaseHandler handler = Client.getInstance().getGraphDatabaseHandler();
				result = handler.executeCypherQuery(selectedQuery);
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Cypher Query Syntax Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			return 0;
		}		
		
		/**
		 * Continues when the results retrieval has finished.
		 */
		public void done() {			
			// Check for result != NULL
			if (result != null) {
				// Empty result set.
				parent.updateResults(result);
				consoleChanged = false;
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				dispose();
			}
		}
	}
	
	/**
	 * Updates the user queries for the 
	 */
	@SuppressWarnings("unchecked")
	public void updateUserQueries() {
		@SuppressWarnings("rawtypes")
		DefaultListModel model = (DefaultListModel) savedQueryList.getModel();
		model.removeAllElements();
		
		Object[] savedQueryData = userQueries.getTitleObjects();
		for (Object obj : savedQueryData) {
			model.addElement(obj);
		}
	}
}
