package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractButton;
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
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.cypher.CypherQueryType;
import de.mpa.graphdb.cypher.CypherStartNode;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.nodes.NodeType;

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
	 * The list of predefined Cypher queries.
	 */
	private JXList predefinedList;
	
	/**
	 * The parent panel reference.
	 */
	private GraphDatabaseResultPanel parent;
	
	/**
	 * A rounded border for combo boxes.
	 */
	private Border roundedCbxBorder;
	
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
		roundedCbxBorder = new PlasticXPLookAndFeel().getDefaults().getBorder("ComboBox.arrowButtonBorder");
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
		
		// Predefined query section.
		JXTaskPane queryTaskPane = new JXTaskPane("Predefined Queries");
		queryTaskPane.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu "));
		queryTaskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		queryTaskPane.addComponentListener(new ComponentAdapter() {
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
		
		queryTaskPane.add(new JLabel("Query: "), CC.xy(2, 2));
		final JTextField queryTtf = new JTextField(30);
		queryTaskPane.add(queryTtf, CC.xy(2, 4));		
		
		Object[] data = CypherQueryType.values();
		
		predefinedList = new JXList(data);
		predefinedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		queryTaskPane.add(new JScrollPane(predefinedList), CC.xyw(2, 6, 3));
		
		JLabel paramLbl = new JLabel("Parameter X: ");
		queryTaskPane.add(paramLbl, CC.xy(4, 2));
		JTextField paramTtf = new JTextField(15);
		queryTaskPane.add(paramTtf, CC.xy(4, 4));
		tpc.add(queryTaskPane);
		
		// Compound query section.
		JXTaskPane compoundQueryTaskPane = new JXTaskPane("Compound Queries");
		compoundQueryTaskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		compoundQueryTaskPane.addComponentListener(new ComponentAdapter() {
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
		
		FormLayout columnLyt = new FormLayout("p:g, p:g, p:g, p:g", "f:p:g");
		columnLyt.setColumnGroups(new int[][] { { 1, 2, 3, 4 } });
		JPanel columnPnl = new JPanel(columnLyt);
		
		/* START section */
		JPanel startPnl = new StartPanel("Start", NodeType.values());

		JScrollPane startScpn = new JScrollPane(startPnl);
		startScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		startScpn.setBorder(null);
		
		/* MATCH section */
		JPanel matchPnl = new MatchPanel("Match", new Object[] { });
		
		final JScrollPane matchScpn = new JScrollPane(matchPnl);
		matchScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		matchScpn.setBorder(null);
		
		/* WHERE section */
		JPanel wherePnl = new WherePanel("Where", null);
		
		JScrollPane whereScpn = new JScrollPane(wherePnl);
		whereScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		whereScpn.setBorder(null);
		
		/* RETURN section */
		JPanel returnPnl = new WherePanel("Return", new Object[] { });
		
		JScrollPane returnScpn = new JScrollPane(returnPnl);
		returnScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		returnScpn.setBorder(null);

		columnPnl.add(startScpn, CC.xy(1, 1));
		columnPnl.add(matchScpn, CC.xy(2, 1));
		columnPnl.add(whereScpn, CC.xy(3, 1));
		columnPnl.add(returnScpn, CC.xy(4, 1));
		
		JScrollPane columnScpn = new JScrollPane(columnPnl);
		columnScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		columnScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		columnScpn.setPreferredSize(new Dimension(0, 200));
		
		compoundQueryTaskPane.add(columnScpn);
		
		
		// Console query section.
		JXTaskPane consoleQueryTaskPane = new JXTaskPane("Cypher Console");	
		consoleQueryTaskPane.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu"));
		consoleQueryTaskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		consoleQueryTaskPane.addComponentListener(new ComponentAdapter() {
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
		consoleScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		consoleQueryTaskPane.add(consoleScpn, CC.xy(2, 2));
//		consoleTxt.setBackground(Color.BLACK);
//		consoleTxt.setForeground(Color.GREEN.darker());
//		consoleTxt.setCaretColor(Color.GREEN.darker());
		
		tpc.add(compoundQueryTaskPane);
		tpc.add(consoleQueryTaskPane);
		
		predefinedList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(!evt.getValueIsAdjusting()) {
					queryTtf.setText(predefinedList.getSelectedValue().toString());
					consoleTxt.setText(((CypherQueryType) predefinedList.getSelectedValue()).getQuery().toString());
				}
			}
		});
		
		
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
			FormLayout layout = new FormLayout("3dlu, 20px, p:g, 20px, 3dlu", "3dlu, p, 3dlu");
			
			this.setLayout(layout);
			
			this.add(new JLabel(label), CC.xyw(2, 2, 2));
			
			int row = 2;
			
			if (items != null) {
				layout.appendRow(RowSpec.decode("p"));
				layout.appendRow(RowSpec.decode("3dlu"));
				JComboBox comboBox = new JComboBox(items);
				row += 2;
				this.add(comboBox, CC.xyw(2, 4, 2));
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
//					wrapper.remove(panel, removeItem);
					removeBlock();
				}
			});

			appendItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					wrapper.append(panel, removeItem);
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
		 * 
		 * @param label
		 * @param items
		 */
		public StartPanel(String label, Object[] items) {
			super(label, items);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() - 1;
			
			layout.appendRow(RowSpec.decode("p"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			this.add(new JComboBox(new Object[] {
					new BigInteger(32, new Random()).toString(32),
					new BigInteger(32, new Random()).toString(32) }),
					CC.xyw(2, row + 2, 2));
			
			this.add(blockBtn, CC.xy(4, row + 2));
			
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

			for (int i = 1; i < 3; i++) {
				this.remove(comps - i);
			}
			for (int i = 0; i < 2; i++) {
				layout.removeRow(row - i);
			}
			
			this.add(blockBtn, CC.xy(4, row - 3));
			
			if (this.getComponentCount() < 4) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public List<CypherStartNode> getStartNodes() {
			return null;
		}
		
	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query MATCH blocks.
	 * 
	 * @author A. Behne
	 */
	private class MatchPanel extends CompoundQueryPanel {

		/**
		 * 
		 * @param label
		 * @param items
		 */
		public MatchPanel(String label, Object[] items) {
			super(label, items);
			appendBlock();
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() - 1;
			
			layout.appendRow(RowSpec.decode("p"));
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			JButton preBtn = new JButton() {
				{
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// cycle arrow state
//							arrow = (arrow == null) ? true : (Boolean.TRUE.equals(arrow)) ? false : null;
							if (arrow == null) {
								arrow = true;
							} else if (Boolean.TRUE.equals(arrow)) {
								arrow = false;
							} else {
								arrow = null;
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
							} else if (Boolean.TRUE.equals(arrow)) {
								arrow = false;
							} else {
								arrow = null;
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

			Border oldBorder = UIManager.getBorder("ComboBox.arrowButtonBorder");
			UIManager.put("ComboBox.arrowButtonBorder", roundedCbxBorder);
			JComboBox relCbx = new JComboBox(new Object[] {
					new BigInteger(32, new Random()).toString(32),
					new BigInteger(32, new Random()).toString(32) });
			UIManager.put("ComboBox.arrowButtonBorder", oldBorder);
			relCbx.setBackground(UIManager.getColor("controlHighlight"));
			
			this.add(preBtn, CC.xywh(4, row - 1, 1, 3));
			this.add(postBtn, CC.xywh(2, row + 1, 1, 3));
			this.add(relCbx, CC.xyw(3, row + 2, 2));
			
			this.add(new JComboBox(new Object[] {
					new BigInteger(32, new Random()).toString(32),
					new BigInteger(32, new Random()).toString(32) }),
					CC.xyw(2, row + 4, 2));
			
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

			for (int i = 1; i < 6; i++) {
				this.remove(comps - i);
			}
			for (int i = 0; i < 4; i++) {
				matchLyt.removeRow(row - i);
			}
			
			this.add(matchBtn, CC.xy(4, row - 5));
			
			if (this.getComponentCount() < 11) {
				removeItem.setEnabled(false);
			}
			
			// revalidate panel as layout has been modified
			this.revalidate();
			this.repaint();
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * Panel implementation for dynamic editing of Cypher query WHERE blocks.
	 * 
	 * @author A. Behne
	 */
	private class WherePanel extends CompoundQueryPanel {
		
		/**
		 * 
		 * @param label
		 * @param items
		 */
		public WherePanel(String label, Object[] items) {
			super(label, items);
		}

		@Override
		public void appendBlock() {
			FormLayout layout = (FormLayout) this.getLayout();
			
			AbstractButton blockBtn = (AbstractButton) this.getComponent(this.getComponentCount() - 1);

			int row = layout.getRowCount() - 1;
			
			layout.appendRow(RowSpec.decode("p"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			this.add(new JComboBox(new Object[] {
					new BigInteger(32, new Random()).toString(32),
					new BigInteger(32, new Random()).toString(32) }),
					CC.xyw(2, row + 2, 2));
			
			this.add(blockBtn, CC.xy(4, row + 2));
			
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

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
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
