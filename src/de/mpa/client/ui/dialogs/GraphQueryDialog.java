package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
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
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.GraphDatabaseResultPanel;
import de.mpa.graphdb.cypher.CypherMatch;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.cypher.CypherQueryType;
import de.mpa.graphdb.cypher.CypherStartNode;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
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
public class GraphQueryDialog extends JDialog {
	
	/**
	 * The parent panel reference.
	 */
	private GraphDatabaseResultPanel parent;
	
	/**
	 * The list of predefined Cypher queries.
	 */
	private JXList predefinedList;

	/**
	 * The panel inside the compound query pane containing controls for
	 * specifying start nodes of Cypher queries.
	 */
	private StartPanel startPnl;
	
	/**
	 * The panel inside the compound query pane containing controls for
	 * specifying match statements of Cypher queries.
	 */
	private MatchPanel matchPnl;
	
	/**
	 * A rounded border for combo boxes.
	 */
	private Border roundedCbxBorder;
	
	/**
	 * A rounded border for buttons.
	 */
	private Border roundedBtnBorder;
	
	/**
	 * Graph query dialog
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 */
	public GraphQueryDialog(Frame owner, GraphDatabaseResultPanel parent, String title, boolean modal) {
		super(owner, title, modal);
		this.parent = parent;
		setupUI();
		initComponents();
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
				BorderFactory.createEmptyBorder(0, -2, 0, 0),
				plasticXPdefaults.getBorder("ComboBox.arrowButtonBorder"));
		roundedBtnBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, plasticXPdefaults.getColor("controlShadow")),
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(0, 0, 0, -2),
						plasticXPdefaults.getBorder("ToggleButton.border")));
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
		
		// Predefined query section
		JXTaskPane predefinedQueryTaskPane = createPredefinedQueryTaskPane();
		
		// Compound query section
		JXTaskPane compoundQueryTaskPane = createCompoundQueryTaskPane();
		
		// Console query section
		JXTaskPane consoleQueryTaskPane = createConsoleQueryTaskPane();

		tpc.add(predefinedQueryTaskPane);
		tpc.add(compoundQueryTaskPane);
		tpc.add(consoleQueryTaskPane);
		
		// Configure button panel containing 'OK' and 'Cancel' options
		FormLayout layout = new FormLayout("p, 5dlu:g, p", "p");

		JPanel buttonPnl = new JPanel(layout);
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ResultsTask().execute();
			}
		});
		
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
		
		okBtn.setPreferredSize(cancelBtn.getPreferredSize());
		
		// Lay out button panel
		buttonPnl.add(okBtn, CC.xy(1, 1));
		buttonPnl.add(cancelBtn, CC.xy(3, 1));
		
		// Add final components to content pane
		contentPane.add(tpc, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));
		
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
	private JXTaskPane createPredefinedQueryTaskPane() {
		JXTaskPane taskPane = new JXTaskPane("Predefined Queries");
		taskPane.setLayout(new FormLayout("p:g, 5dlu, p:g", "p, 5dlu, f:140px:g"));
		taskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		taskPane.addComponentListener(new ComponentAdapter() {
			private Dimension size = null;
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension newSize = e.getComponent().getSize();
				if (size != null) {
					int delta = newSize.height - size.height;
					Dimension dialogSize = new Dimension(getSize());
					dialogSize.height += delta;
					setSize(dialogSize);
				}
				size = newSize;
			}
		});
		
		Object[] data = CypherQueryType.values();
		
		predefinedList = new JXList(data);
		predefinedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
		JXList favouritesList = new JXList();
		favouritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		taskPane.add(new JLabel("Defaults"), CC.xy(1, 1));
		taskPane.add(new JLabel("Favourites"), CC.xy(3, 1));
		taskPane.add(new JScrollPane(predefinedList), CC.xy(1, 3));
		taskPane.add(new JScrollPane(favouritesList), CC.xy(3, 3));
		
		return taskPane;
	}

	/**
	 * Creates and returns the task pane containing controls for interactively
	 * designing Cypher queries.
	 * @return the compound query task pane
	 */
	private JXTaskPane createCompoundQueryTaskPane() {
		JXTaskPane taskPane = new JXTaskPane("Compound Queries");
		taskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		taskPane.addComponentListener(new ComponentAdapter() {
			private Dimension size = null;
			@Override
			public void componentResized(ComponentEvent evt) {
				Dimension newSize = evt.getComponent().getSize();
				if (size != null) {
					int delta = newSize.height - size.height;
					Dimension dialogSize = new Dimension(getSize());
					dialogSize.height += delta;
					setSize(dialogSize);
				}
				size = newSize;
			}
		});

		FormLayout columnLyt = new FormLayout("p:g, p:g, p:g, p:g", "f:180px:g");
		columnLyt.setColumnGroups(new int[][] { { 1, 2, 3, 4 } });
		JPanel columnPnl = new JPanel(columnLyt);
		
		startPnl = new StartPanel();

		JScrollPane startScpn = new JScrollPane(startPnl);
		startScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		startScpn.setBorder(null);
		
		matchPnl = new MatchPanel(new Object[] { });
		
		JScrollPane matchScpn = new JScrollPane(matchPnl);
		matchScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		matchScpn.setBorder(null);
		
		/* WHERE section */
		WherePanel wherePnl = new WherePanel(null);
		
		JScrollPane whereScpn = new JScrollPane(wherePnl);
		whereScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		whereScpn.setBorder(null);
		
		/* RETURN section */
		JPanel returnPnl = new ReturnPanel(new Object[] { });
		
		JScrollPane returnScpn = new JScrollPane(returnPnl);
		returnScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		returnScpn.setBorder(null);

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
		taskPane.setLayout(new FormLayout("p:g", "f:p:g"));
		taskPane.setUI(new GlossyTaskPaneUI());

		// Apply component listener to synchronize task pane size with dialog size
		taskPane.addComponentListener(new ComponentAdapter() {
			private Dimension size = null;

			@Override
			public void componentResized(ComponentEvent e) {
				Dimension newSize = e.getComponent().getSize();
				if (size != null) {
					int delta = newSize.height - size.height;
					Dimension dialogSize = new Dimension(getSize());
					dialogSize.height += delta;
					setSize(dialogSize);
				}
				size = newSize;
			}
		});

		final JTextArea consoleTxt = new JTextArea(4, 0);
		consoleTxt.setFont(new Font("Courier", consoleTxt.getFont().getStyle(), 12));

		JScrollPane consoleScpn = new JScrollPane(consoleTxt);
		consoleScpn .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScpn .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		taskPane.add(consoleScpn, CC.xy(1, 1));

		predefinedList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if (predefinedList.getSelectedIndex() >= 0) {
					CypherQuery query = ((CypherQueryType) predefinedList.getSelectedValue()).getQuery();
					consoleTxt.setText(query.toString());
					startPnl.setStartNodes(query.getStartNodes());
					matchPnl.setMatches(query.getMatches());
				}
			}
		});

		return taskPane;
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
		public VariableComboBoxPanel(Object[] items) {
			this(items, false);
		}

		/**
		 * Constructs a wrapper panel containing a combo box bearing the
		 * specified items and a button prompting to enter a variable name and
		 * optionally applies a rounded corner border to both components
		 * @param items the items to be displayed inside the combo box
		 * @param rounded <code>true</code> if rounded corners shall be used,
		 *  <code>false</code> otherwise
		 */
		public VariableComboBoxPanel(Object[] items, boolean rounded) {
			super(new FormLayout("21px, p:g", "f:21px"));
			initComponents(items, rounded);
		}

		/**
		 * Creates and initializes the panel components.
		 * @param items the items to be displayed inside the combo box
		 * @param rounded <code>true</code> if rounded corners shall be used,
		 *  <code>false</code> otherwise
		 */
		private void initComponents(Object[] items, boolean rounded) {
			varBtn = new JToggleButton(IconConstants.TEXTFIELD_ICON);
			varBtn.setRolloverIcon(IconConstants.TEXTFIELD_ROLLOVER_ICON);
			varBtn.setPressedIcon(IconConstants.TEXTFIELD_PRESSED_ICON);
			
			final JPopupMenu varPop = new JPopupMenu("test");
			
			varTtf = new JTextField(items[0].toString().toLowerCase());
			varTtf.setMargin(new Insets(0, 1, 0, 0));
			varTtf.setBorder(null);
			
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
//						if (varPop.isVisible()) {
							varPop.setVisible(false);
//						}
					}
				}
			});
			
			varPop.addPopupMenuListener(new PopupMenuListener() {
				public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
					if (varBtn.isSelected()) {
//						SwingUtilities.invokeLater(new Runnable() {
//							public void run() {
//								varBtn.doClick(100);
//							}
//						});
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
			}
			
			varCbx.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					varTtf.setText(evt.getItem().toString().toLowerCase());
				}
			});
			
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
		
		/**
		 * Sets the combo box' items.
		 * @param items the items to set
		 */
		public void setItems(Object[] items) {
			varCbx.setModel(new DefaultComboBoxModel(items));
		}
		
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
	 * Panel implementation for dynamic editing of Cypher query blocks.
	 * 
	 * @author A. Behne
	 */
	private abstract class CompoundQueryPanel extends JPanel {
		
		/**
		 * Context menu item for removing building blocks.
		 */
		protected JMenuItem removeItem;
		
		/**
		 * Constructs a panel containing controls for dynamically editing parts
		 * of a Cypher query.
		 * @param label the label
		 * @param items
		 */
		private CompoundQueryPanel(String label, Object[] items) {
			super();
			initComponents(label, items);
		}
		
		/**
		 * Creates and initializes the basic components of this compound query panel.
		 * @param label the text label to be displayed atop the building blocks of this panel
		 * @param items the items to be displayed in the in
		 */
		private void initComponents(String label, Object[] items) {
			FormLayout layout = new FormLayout("3dlu, 21px, p:g, 21px, 3dlu", "3dlu, f:16px, 3dlu");
			
			this.setLayout(layout);
			
			this.add(new JLabel(label), CC.xyw(2, 2, 2));
			
			int row = 2;
			
			if (items != null) {
				layout.appendRow(RowSpec.decode("f:21px"));
				layout.appendRow(RowSpec.decode("3dlu"));
				
				this.add(new JComboBox(items), CC.xyw(2, 4, 2));
				
				row += 2;
			}

			final JToggleButton blockBtn = new JToggleButton(IconConstants.PLUGIN_ICON);
			blockBtn.setRolloverIcon(IconConstants.PLUGIN_ROLLOVER_ICON);
			blockBtn.setPressedIcon(IconConstants.PLUGIN_PRESSED_ICON);
			blockBtn.setOpaque(false);
			blockBtn.setContentAreaFilled(false);
			blockBtn.setBorder(null);
			blockBtn.setFocusPainted(false);
			blockBtn.setToolTipText("Append/Remove " + label + " Block");

			final JPopupMenu blockPop = new JPopupMenu();
			
			JMenuItem appendItem = new JMenuItem("Append " + label + " Block", IconConstants.ADD_ICON);
			appendItem.setRolloverIcon(IconConstants.ADD_ROLLOVER_ICON);
			appendItem.setPressedIcon(IconConstants.ADD_PRESSED_ICON);
			
			removeItem = new JMenuItem("Remove " + label + " Block", IconConstants.DELETE_ICON);
			removeItem.setRolloverIcon(IconConstants.DELETE_ROLLOVER_ICON);
			removeItem.setPressedIcon(IconConstants.DELETE_PRESSED_ICON);
			removeItem.setEnabled(false);
			
			removeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					removeBlock();
				}
			});

			appendItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					appendBlock();
				}
			});

			blockPop.add(appendItem);
			blockPop.add(removeItem);
			
			blockBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					blockPop.show(blockBtn, 0, blockBtn.getSize().height);
				}
			});
			
			this.add(blockBtn, CC.xy(4, row));
		}
		
//		/**
//		 * Returns the list of Cypher-related object values this panel represents.
//		 * @return the values
//		 */
//		public abstract List<Object> getValues();
		
		/**
		 * Appends a query building block to this panel.
		 */
		public abstract void appendBlock();
		
		/**
		 * Removes a query building block from this panel.
		 */
		public abstract void removeBlock();
		
		/**
		 * Scrolls this panel's parent viewport to the bottom.
		 */
		protected void scrollToBottom() {
			Container parent = this.getParent();
			if (parent instanceof JViewport) {
				((JViewport) parent).setViewPosition(new Point(0, this.getHeight()));
			}
		}
	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query START blocks.
	 * 
	 * @author A. Behne
	 */
	private class StartPanel extends CompoundQueryPanel {
		
		/**
		 * The list of parameter combo boxes.
		 */
		private List<VariableComboBoxPanel> varPnls;

		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query START blocks.
		 */
		public StartPanel() {
			super("Start", NodeType.values());
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);
			
			// replace combo box
			this.remove(1);
			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(NodeType.values());
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

			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(NodeType.values());
			varPnls.add(varPnl);
			
			this.add(varPnl, CC.xyw(2, row, 2));
			this.add(blockBtn, CC.xy(4, row));
			
			if (this.getComponentCount() >= 4) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();
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
		}

		// TODO: propagate selected items to Match panel
		/**
		 * Returns the list of Cypher start nodes that are selected in this panel.
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
				varPnl.setVariableName(startNode.getVariableName());
			}
			// update list of start nodes in match panel
			matchPnl.setStartNodes(startNodes);
		}

	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query MATCH blocks.
	 * 
	 * @author A. Behne
	 */
	private class MatchPanel extends CompoundQueryPanel {

		/**
		 * The combo box containing the list of possible start node options for
		 * the Cypher match statement this panel represents.
		 */
		private JComboBox startCbx;
		
		/**
		 * The list of relation combo box panels.
		 */
		private List<VariableComboBoxPanel> relPnls;
		
		/**
		 * The list of target node combo box panels.
		 */
		private List<VariableComboBoxPanel> trgPnls;

		/**
		 * Constructs a panel containing controls for dynamic editing of Cypher
		 * query MATCH blocks.
		 * @param items
		 */
		public MatchPanel(Object[] items) {
			super("Match", items);
			startCbx = (JComboBox) this.getComponent(1);
			relPnls = new ArrayList<VariableComboBoxPanel>();
			trgPnls = new ArrayList<VariableComboBoxPanel>();
			
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
			
			JButton preBtn = new JButton() {
				{
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// cycle arrow state
//							arrow = (arrow == null) ? true : (Boolean.TRUE.equals(arrow)) ? false : null;
							if (arrow == null) {
								arrow = true;
							} else {
								if (Boolean.TRUE.equals(arrow)) {
									arrow = false;
								} else {
									arrow = null;
								}
							}
						}
					});
					setBorder(null);
					setOpaque(false);
					setFocusPainted(false);
				}
				private Boolean arrow = null;		// true = up, false = down, null = neither
				private Color shadow = UIManager.getColor("controlDkShadow").darker();
				private Color highlight = UIManager.getColor("controlHighlight");
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					int halfHeight = getHeight() / 2 - 1;
					int halfWidth = getWidth() / 2;
					g.setColor(highlight);
					g.drawLine(0, halfHeight + 1, halfWidth, halfHeight + 1);
					g.drawLine(halfWidth + 1, halfHeight, halfWidth + 1, getHeight());
					if (Boolean.TRUE.equals(arrow)) {
						g.fillPolygon(
								new int[] { 0, 5, 5, 4 },
								new int[] { halfHeight + 1, halfHeight - 4, halfHeight + 5, halfHeight + 5 },
								4);
					}
					g.setColor(shadow);
					g.drawLine(0, halfHeight, halfWidth, halfHeight);
					g.drawLine(halfWidth, halfHeight, halfWidth, getHeight());
					if (Boolean.TRUE.equals(arrow)) {
						g.fillPolygon(
								new int[] { 0, 4, 4 },
								new int[] { halfHeight, halfHeight - 4, halfHeight + 4 },
								3);
					}
				}
			};
			
			JButton postBtn = new JButton() {
				{
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// cycle arrow state
//							arrow = (arrow == null) ? true : (Boolean.TRUE.equals(arrow)) ? false : null;
							if (arrow == null) {
								arrow = true;
							} else {
								if (Boolean.TRUE.equals(arrow)) {
									arrow = false;
								} else {
									arrow = null;
								}
							}
						}
					});
					setBorder(null);
					setOpaque(false);
					setFocusPainted(false);
				}
				private Boolean arrow = null;		// true = up, false = down, null = neither
				private Color shadow = UIManager.getColor("controlDkShadow").darker();
				private Color highlight = UIManager.getColor("controlHighlight");
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					int halfHeight = getHeight() / 2 - 1;
					int halfWidth = getWidth() / 2;
					g.setColor(highlight);
					g.drawLine(getWidth(), halfHeight + 1, halfWidth + 1, halfHeight + 1);
					g.drawLine(halfWidth + 1, halfHeight + 1, halfWidth + 1, getHeight() - 1);
					if (Boolean.FALSE.equals(arrow)) {
						g.fillPolygon(
								new int[] { halfWidth - 3, halfWidth + 5, halfWidth, halfWidth - 3 },
								new int[] { getHeight() - 4, getHeight() - 4, getHeight() + 1, getHeight() - 3 },
								4);
					}
					g.setColor(shadow);
					g.drawLine(getWidth(), halfHeight, halfWidth, halfHeight);
					g.drawLine(halfWidth, halfHeight, halfWidth, getHeight() - 1);
					if (Boolean.FALSE.equals(arrow)) {
						g.fillPolygon(
								new int[] { halfWidth - 3, halfWidth + 4, halfWidth },
								new int[] { getHeight() - 4, getHeight() - 4, getHeight() },
								3);
					}
				}
			};
			
			postBtn.setModel(preBtn.getModel());
			
			VariableComboBoxPanel relPnl = new VariableComboBoxPanel(new Object[] { "BELONGS_TO_ENZYME" }, true);
			relPnl.setBackground(new Color(245, 245, 245));
			relPnls.add(relPnl);
			
			VariableComboBoxPanel trgPnl = new VariableComboBoxPanel(new Object[] { "" });
			trgPnls.add(trgPnl);
			
			this.add(preBtn, CC.xywh(4, row - 1, 1, 3));
			this.add(relPnl, CC.xyw(3, row + 2, 2));
			this.add(postBtn, CC.xywh(2, row + 1, 1, 3));
			this.add(trgPnl, CC.xyw(2, row + 4, 2));
			this.add(blockBtn, CC.xy(4, row + 4));
			
			if (this.getComponentCount() >= 11) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			// scroll to bottom
			this.scrollToBottom();
		}

		@Override
		public void removeBlock() {

			FormLayout matchLyt = (FormLayout) this.getLayout();
			
			AbstractButton matchBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = matchLyt.getRowCount();
			int comps = this.getComponentCount();
			
			// remove combo boxes from cache
			trgPnls.remove(this.getComponent(comps - 2));
			relPnls.remove(this.getComponent(comps - 4));

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
		}
		
		/**
		 * 
		 * @param matches
		 */
		public void setMatches(List<CypherMatch> matches) {
			int blocksSize = relPnls.size();
			int matchesSize = matches.size() - 1;
			if (matchesSize > blocksSize) {
				// append more blocks
				for (int i = blocksSize; i < matchesSize; i++) {
					appendBlock();
				}
			} else if (matchesSize < blocksSize) {
				// remove some blocks
				for (int i = matchesSize; i < blocksSize; i++) {
					removeBlock();
				}
			}
			for (int i = 0; i < matchesSize; i++) {
				CypherMatch match = matches.get(i);
				
				VariableComboBoxPanel relPnl = relPnls.get(i);
				relPnl.setItems(new Object[] { match.getRelation() });
				relPnl.setVariableName(match.getRelationVariableName());
				
				VariableComboBoxPanel trgPnl = trgPnls.get(i);
//				trgPnl.setItems(new Object[] { });
				trgPnl.setVariableName(match.getTargetVariableName());
			}
		}
		
		/**
		 * Sets the start node combo box' options to the specified start nodes.
		 * @param startNodes the start nodes
		 */
		public void setStartNodes(List<CypherStartNode> startNodes) {
			Object[] items = new Object[startNodes.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = startNodes.get(i).getVariableName();
			}
			startCbx.setModel(new DefaultComboBoxModel(items));
		}

	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query WHERE blocks.
	 * 
	 * @author A. Behne
	 */
	private class WherePanel extends CompoundQueryPanel {
		
		/**
		 * TODO: API
		 * @param label
		 * @param items
		 */
		public WherePanel(Object[] items) {
			super("Where", items);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() + 1;
			
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(new Object[] { "" });
			
			this.add(varPnl, CC.xyw(2, row, 2));
			this.add(blockBtn, CC.xy(4, row));
			
			if (this.getComponentCount() >= 3) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();
		}

		@Override
		public void removeBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount();
			int comps = this.getComponentCount();

			for (int i = 1; i < 3; i++) {
				this.remove(comps - i);
			}
			for (int i = 0; i < 2; i++) {
				layout.removeRow(row - i);
			}
			
			this.add(blockBtn, CC.xy(4, row - 3));
			
			if (this.getComponentCount() < 3) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
		}
		
	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query RETURN blocks.
	 * 
	 * @author A. Behne
	 */
	private class ReturnPanel extends CompoundQueryPanel {
		
		/**
		 * TODO: API
		 * @param label
		 * @param items
		 */
		public ReturnPanel(Object[] items) {
			super("Return", items);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() + 1;
			
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			VariableComboBoxPanel varPnl = new VariableComboBoxPanel(new Object[] { "" });
			
			this.add(varPnl, CC.xyw(2, row, 2));
			this.add(blockBtn, CC.xy(4, row));
			
			if (this.getComponentCount() >= 3) {
				removeItem.setEnabled(true);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			
			this.scrollToBottom();
		}

		@Override
		public void removeBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount();
			int comps = this.getComponentCount();

			for (int i = 1; i < 3; i++) {
				this.remove(comps - i);
			}
			for (int i = 0; i < 2; i++) {
				layout.removeRow(row - i);
			}
			
			this.add(blockBtn, CC.xy(4, row - 3));
			
			if (this.getComponentCount() < 3) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
		}
		
	}
	
	/**
	 * Class to fetch graph database search results in a background thread.
	 * 
	 * @author T. Muth
	 */
	private class ResultsTask extends SwingWorker {
		
		/**
		 * The execution result of the selected Cypher query.
		 */
		private ExecutionResult result;
	
		@Override
		protected Object doInBackground() {
			try {
				// Begin appearing busy
				//TODO: setBusy(true);
				
				// Get Cypher query
				CypherQueryType queryType = (CypherQueryType) predefinedList.getSelectedValue();
				CypherQuery query = queryType.getQuery();
				
				// Execute query, store result
				GraphDatabaseHandler handler = Client.getInstance().getGraphDatabaseHandler();
				result = handler.executeCypherQuery(query);
				
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				//Client.getInstance().firePropertyChange("new message", null, "FAILED");
				//Client.getInstance().firePropertyChange("indeterminate", true, false);
			}
			return 0;
		}		
		
		/**
		 * Continues when the results retrieval has finished.
		 */
		public void done() {
			parent.updateResults(result);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			dispose();
		}
	}
}
