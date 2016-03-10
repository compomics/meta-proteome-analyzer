package de.mpa.client.ui.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXMultiSplitPane.DividerPainter;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import scala.Array;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.Hit;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.settings.Parameter.BooleanMatrixParameter;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ButtonTabbedPane;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTableHeader;
import de.mpa.client.ui.CoverageViewerPane;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.PhylogenyTreeTableNode;
import de.mpa.client.ui.ProteinTreeTables;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTable.TableColumnExt2;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.FormatHighlighter;
import de.mpa.client.ui.TreeTableRowSorter;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.ScrollableChartPane;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.dialogs.SelectExperimentDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.MascotGenericFile;
import de.mpa.util.ColorUtils;

/**
 * Panel implementation for viewing database search results.
 * 
 * @author A. Behne, T. Muth, R. Heyer, F. Kohrs
 */
public class DbSearchResultPanel extends JPanel implements Busyable {
	
	/**
	 * The database search result object.
	 */
	private DbSearchResult dbSearchResult;

	/**
	 * Peptide table.
	 */
	private CheckBoxTreeTable peptideTbl;

	/**
	 * Protein sequence coverage panel.
	 */
	private CoverageViewerPane coveragePane;

	/**
	 * PSM table.
	 */
	private CheckBoxTreeTable psmTbl;

	/**
	 * Multi-split pane containing all sub-panels.
	 */
	private JXMultiSplitPane split;

	/**
	 * The titled panel containing protein views.
	 */
	private JXTitledPanel protTtlPnl;
	
	/**
	 * The titled panel containing peptide views.
	 */
	private JXTitledPanel pepTtlPnl;
	
	/**
	 * The titled panel containing spectrum match views.
	 */
	private JXTitledPanel psmTtlPnl;

	/**
	 * Flag indicating whether unselected checkbox tree elements shall be hidden
	 * from view.
	 */
	protected boolean hideUnselected = true;

	/**
	 * The spectrum viewer
	 */
	private SpectrumViewerPanel spectrumPnl;

	/**
	 * Chart panel capable of displaying various charts (mainly ontology/taxonomy pie charts).
	 */
	private ScrollableChartPane chartPane;
	
	/**
	 * The current type of chart to be displayed in the bottom right figure panel.
	 */
	private ChartType chartType = OntologyChartType.BIOLOGICAL_PROCESS;

	/**
	 * Data container for ontological meta-information of the fetched results.
	 */
	private OntologyData ontologyData = new OntologyData();

	/**
	 * Data container for taxonomic meta-information of the fetched results.
	 */
	private TaxonomyData taxonomyData = new TaxonomyData();

	/**
	 * The shared protein count highlighter instance.
	 */
	private ColorHighlighter protCountHighlighter;

	/**
	 * The shared chart selection protein highlighter instance.
	 */
	private ColorHighlighter chartSelHighlighter;
	
	/**
	 * The "Hide unselected" checkbox in the main protein tree table view
	 */
	private JCheckBox hideUnselChk;
	
	/**
	 * Selection of possible panels to focus
	 */
	private enum focus {SPECTRUM, PEPTIDE, PROTEIN};
	public focus view = focus.PROTEIN;
	public JToggleButton focusSpectraBtn, focusPeptideBtn;
	
	/**
	 * Creates the database search results detail view.
	 */
	public DbSearchResultPanel() {
		this.initComponents();
	}

	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		// Define layout
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		// Setup components
		this.setupProteinTables();
		peptideTbl = this.setupPeptideTable();
		coveragePane = this.setupCoverageViewer();
		psmTbl = this.setupPSMTable();
		spectrumPnl = this.setupSpectrumViewer();
		chartPane = this.setupChartPane();

		// Create titled panels
		protTtlPnl = this.createProteinPanel();
		pepTtlPnl = this.createPeptidePanel();
		psmTtlPnl = this.createPSMPanel();
		JXTitledPanel chartTtlPnl = this.createChartPanel();

		// Set up multi-split pane
		String layoutDef =
				"(COLUMN (LEAF weight=0.35 name=protein) (ROW weight=0.65 (COLUMN weight=0.375 (LEAF weight=0.5 name=peptide) (LEAF weight=0.5 name=psm)) (LEAF weight=0.625 name=chart)))";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

		MultiSplitLayout msl = new MultiSplitLayout(modelRoot);
		msl.setLayoutByWeight(true);

		split = new JXMultiSplitPane(msl) {
//			@Override
//			public void setCursor(Cursor cursor) {
//				if (busy) {
//					if ((cursor == null) || (cursor.getType() == Cursor.DEFAULT_CURSOR)) {
//						cursor = clientFrame.getCursor();
//					}
//				}
//				super.setCursor(cursor);
//			}
		};
		split.setDividerSize(12);

		split.add(protTtlPnl, "protein");
		split.add(pepTtlPnl, "peptide");
		split.add(psmTtlPnl, "psm");
		split.add(chartTtlPnl, "chart");

		this.add(split, CC.xy(2, 2));

		// Apply one-touch-collapsible capabilities to divider between protein table and lower parts
		final Divider mainDivider = (Divider) ((Split) modelRoot).getChildren().get(1);

		// After initializing the layout fix it in place to avoid the layout
		// manager falsely distributing excess space when resizing
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				split.getMultiSplitLayout().setLayoutByWeight(false);
				split.getMultiSplitLayout().setFloatingDividers(false);
				// weights don't matter anymore at this point, make protein
				// table receive maximum weight
				mainDivider.nextSibling().setWeight(0.0);
				mainDivider.previousSibling().setWeight(1.0);
			}
		});

		/** Custom painter to draw one-touch-collapsible widget onto main divider. */
		class CollapsibleDividerPainter extends DividerPainter {
			/** Denotes arrow direction. Either SwingConstants.NORTH or SOUTH. */
			private int direction;
			/** Constructs a custom divider painter */
			public CollapsibleDividerPainter(int direction) {
				this.direction = direction;
			}
			@Override
			protected void doPaint(Graphics2D g, Divider divider, int width, int height) {
				// we only want a specific divider to be affected
				if (divider == mainDivider) {
					int x = width - 25;
					int y = height / 2 - 2;
					// paint triangle indicator
					g.setColor(Color.BLACK);
					g.fillPolygon(calcTriX(x - 1, 10), calcTriY(y - 1, 5), 3);
					g.setColor(Color.WHITE);
					g.fillPolygon(calcTriX(x, 10), calcTriY(y, 5), 3);
					g.setColor(Color.GRAY);
					g.fillPolygon(calcTriX((direction == SwingUtilities.SOUTH) ? x : x + 1, 8), calcTriY(y, 4), 3);
					// paint rivet decorations
					int[] yPoints = (direction == SwingUtilities.SOUTH) ?
							new int[] { y - 2, y - 2, y + 1, y + 1, y + 4, y + 4, y + 4 } :
								new int[] { y + 4, y + 4, y + 1, y + 1, y - 2, y - 2, y - 2 };
							paintRivets(g, new int[] { x - 13, x - 7, x - 10 , x - 4, x - 13, x - 7, x - 1 },
									yPoints);
							paintRivets(g, new int[] { x + 18, x + 24, x + 15 , x + 21, x + 12, x + 18, x + 24 },
									yPoints);
				}
			}
			/** Aux method to calculate x-coordinates of a triangular polygon. */
			private int[] calcTriX(int x, int w) {
				return new int[] { x, x + w, x + w / 2 };
			}
			/** Aux method to calculate y-coordinates of a triangular polygon. */
			private int[] calcTriY(int y, int h) {
				return (direction == SwingUtilities.SOUTH) ?
						new int[] { y, y, y + h } : new int[] { y + h, y + h, y };
			}
			/** Aux method to paint rivet-like decorations. */
			private void paintRivets(Graphics g, int[] xPoints, int[] yPoints) {
				for (int i = 0; i < xPoints.length; i++) {
					int x = xPoints[i], y = yPoints[i];
					g.setColor(Color.BLACK);
					g.fillPolygon(new int[] { x - 2, x, x - 2 }, new int[] { y, y, y + 2 }, 3);
					g.setColor(Color.WHITE);
					g.fillPolygon(new int[] { x, x, x - 2 }, new int[] { y, y + 2, y + 2 }, 3);
				}
			}

		}

		// Set aside different painters for expanded/collapsed main divider
		final DividerPainter southPainter = new CollapsibleDividerPainter(SwingConstants.SOUTH);
		final DividerPainter northPainter = new CollapsibleDividerPainter(SwingConstants.NORTH);
		split.setDividerPainter(southPainter);

		// Add mouse listener to split pane to detect clicks on one-touch-collapsible widget
		split.addMouseListener(new MouseAdapter() {
			/** State flag denoting whether the lower parts are collapsed. */
			private boolean visible = false;
			/** Point where collapse action was triggered. Used for restoring divider position on restore. */
			private Point clickPoint = null;
			@Override
			public void mouseClicked(MouseEvent me) {
				final MultiSplitLayout msl = split.getMultiSplitLayout();
				Divider divider = msl.dividerAt(me.getX(), me.getY());
				// we only want a specific divider to be targetable
				if (divider == mainDivider) {
					// check whether arrow part got clicked
					int x = mainDivider.getBounds().width - me.getX();
					if ((x > 15) && (x < 26)) {
						// hide nodes below main divider
						msl.displayNode("chart", visible);
						msl.displayNode("psm", visible);
						msl.displayNode("peptide", visible);
						if (!visible) {
							// unhide main divider and change painter to display upward-pointing triangle
							mainDivider.setVisible(true);
							split.setDividerPainter(northPainter);
							clickPoint = me.getPoint();
						} else {
							//							// fake mouse events to move divider near original position
							split.dispatchEvent(convertMouseEvent(me, MouseEvent.MOUSE_PRESSED, me.getPoint()));
							split.dispatchEvent(convertMouseEvent(me, MouseEvent.MOUSE_DRAGGED, clickPoint));
							split.dispatchEvent(convertMouseEvent(me, MouseEvent.MOUSE_RELEASED, clickPoint));
							// reset painter to display downward-pointing triangle
							split.setDividerPainter(southPainter);
						}
						visible = !visible;
					}
				}
			}
			/** Aux method to ease creation of fake mouse events */
			private MouseEvent convertMouseEvent(MouseEvent me, int id, Point p) {
				return new MouseEvent((Component) me.getSource(), id, me.getWhen(),
						me.getModifiers(), p.x, p.y, me.getXOnScreen(), me.getYOnScreen(),
						me.getClickCount(), me.isPopupTrigger(), me.getButton());
			}
		});
	}

	/**
	 * Configures the protein tree table views.
	 */
	private void setupProteinTables() {
		
		for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
			
			final CheckBoxTreeTable treeTbl = ptt.getTreeTable();
			
			// checkbox tree selection accounting for changes on pathway string building
			final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
			
			cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent tse) {
					if (tse.getPath() == null) {
						refreshPathwayStrings();
					}
				}
			});
			
			// install selection listener to populate peptide table on selection
			treeTbl.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent evt) {
					Set<ProteinHit> proteins = new LinkedHashSet<>();
					int[] rows = treeTbl.getSelectedRows();
					for (int row : rows) {
						TreePath path = treeTbl.getPathForRow(row);
						PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) path.getLastPathComponent();
				
						if (node.isLeaf()) {
							proteins.add((ProteinHit) node.getUserObject());
						} else {
							proteins.addAll(this.getProteins(node));
						}
					}
					// REFRESH peptide view
					if (view == focus.PROTEIN) {
						Set<PeptideHit> peptides = new LinkedHashSet<PeptideHit>();
						if (proteins != null) {
							for (ProteinHit protein : proteins) {
								peptides.addAll(protein.getPeptideHitList());
							}
						}
						refreshPeptideViews(peptides);
					}
				}
				
				/**
				 * Helper method to recursively gather all protein hits (i.e. leaf nodes)
				 * below the specified protein tree table node.
				 * @param node the parent tree table node
				 * @return the set of protein hits
				 */
				private Set<ProteinHit> getProteins(PhylogenyTreeTableNode node) {
					Set<ProteinHit> proteins = new LinkedHashSet<ProteinHit>();
					if (node != null) {
						if (node.isProtein() && !node.isMetaProtein()) {
							ProteinHit ph = (ProteinHit) node.getUserObject();
							proteins.add(ph);
						} else {
							Enumeration<? extends MutableTreeTableNode> children = node.children();
							while (children.hasMoreElements()) {
								MutableTreeTableNode child = children.nextElement();
								proteins.addAll(this.getProteins((PhylogenyTreeTableNode) child));
							}
						}
					}
					return proteins;
				}
			});		
			
			treeTbl.addPropertyChangeListener("checkboxSelectionDone", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					refreshChart(true);
				}
			});
			
			// install selection row filter
			treeTbl.setRowFilter(new SelectionRowFilter(treeTbl));
			
			// install protein count highlighter
			Color protCountCol = new Color(232, 122, 55, 132);
			protCountHighlighter = new ColorHighlighter(
					new ProteinHighlightPredicate(), protCountCol, null, protCountCol, null);
			treeTbl.addHighlighter(protCountHighlighter);
			
			Color chartSelCol = new Color(0, 232, 122, 45);
			chartSelHighlighter = new ColorHighlighter(
					new ProteinHighlightPredicate(), chartSelCol, null, chartSelCol, null);
			treeTbl.addHighlighter(chartSelHighlighter);
		}
	}
	
	/**
	 * Creates and configures the peptide tree table view.
	 * @return the peptide tree table
	 */
	private CheckBoxTreeTable setupPeptideTable() {
		final PhylogenyTreeTableNode root = new PhylogenyTreeTableNode("Peptide View");
		
		// Set up table model
		final SortableTreeTableModel treeTblMdl = new SortableTreeTableModel(root) {
			// Install column names
			{
				setColumnIdentifiers(Arrays.asList(new String[] {
						"Sequence", "ProtC", "Proteins", "SpC", "Tax" }));
			}
			// Fool-proof table by allowing only one type of node
			@Override
			public void insertNodeInto(MutableTreeTableNode newChild,
					MutableTreeTableNode parent, int index) {
				if (newChild instanceof PhylogenyTreeTableNode) {
					super.insertNodeInto(newChild, parent, index);
				} else {
					throw new IllegalArgumentException("This tree table requires Phylogeny nodes!");
				}
			}
			
		};

		// Create table from model
		final SortableCheckBoxTreeTable treeTbl = new SortableCheckBoxTreeTable(treeTblMdl) {
			@Override
			public void updateHighlighters(int column, Object... params) {
				int viewIndex = this.convertColumnIndexToView(column);
				if (viewIndex != -1) {
					TableColumnExt columnExt = this.getColumnExt(viewIndex);
					Highlighter[] highlighters = columnExt.getHighlighters();
					if (highlighters.length > 0) {
						if (highlighters.length > 1) {
							// typically there should be only a single highlighter per column
							System.err.println("WARNING: multiple highlighters specified for column " + column
									+ " of tree table " + this.getTreeTableModel().getRoot().toString());
						}
						Highlighter hl = highlighters[0];
						if (hl instanceof BarChartHighlighter) {
							// we may need to update the highlighter's baseline width to accommodate for aggregate values
							BarChartHighlighter bchl = (BarChartHighlighter) hl;
							FontMetrics fm = this.getFontMetrics(UIManager.getFont("Label.font"));
							NumberFormat formatter = bchl.getFormatter();
							// iterate all nodes to get elements in desired column
							int maxWidth = this.getMaximumStringWidth(
									(TreeTableNode) this.getTreeTableModel().getRoot(), column, formatter, fm);
							bchl.setBaseline(maxWidth + 1);
							if (params.length > 1) {
								bchl.setRange(((Number) params[0]).doubleValue(), ((Number) params[1]).doubleValue());
							}
						}
					}
					// repaint column
					Rectangle rect = this.getTableHeader().getHeaderRect(this.convertColumnIndexToView(column));
					rect.height = this.getHeight();
					this.repaint(rect);
				}
			}
			
			/** Convenience method to recursively traverse the tree in
			 *  search of the widest string inside the specified column. */
			private int getMaximumStringWidth(TreeTableNode node, int column, Format formatter, FontMetrics fm) {
				int strWidth = 0;
				Object value = node.getValueAt(column);
				if (value != null) {
					strWidth = fm.stringWidth(formatter.format(value));
				}
				Enumeration<? extends TreeTableNode> children = node.children();
				while (children.hasMoreElements()) {
					TreeTableNode child = (TreeTableNode) children.nextElement();
					strWidth = Math.max(strWidth, this.getMaximumStringWidth(child, column, formatter, fm));
				}
				return strWidth;
			}
			
			/** The text to display on top of the table when it's empty */
			private final String emptyStr = "no protein(s) selected";
			/** The cached width of the empty table text */
			private int emptyStrW = 0;
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (this.getRowCount() == 0) {
					if (emptyStrW == 0) {
						// Cache string width
						emptyStrW = g.getFontMetrics().stringWidth(emptyStr);
					}
					// Enable text anti-aliasing
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					// Draw string on top of empty table
					g.drawString(emptyStr, (this.getWidth() - emptyStrW) / 2, this.getVisibleRect().height / 2);
				}
			}
			
			@Override
			public String getToolTipText(MouseEvent me) {
				JXTreeTable table = (JXTreeTable) me.getSource();
				int col = table.columnAtPoint(me.getPoint());
				if (col != -1) {
					int row = table.rowAtPoint(me.getPoint());
					if (row != -1) {
						TreePath pathForRow = table.getPathForRow(row);
						if (pathForRow != null) {
							PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) pathForRow.getLastPathComponent();
							if (node != null) {
								Object value = node.getValueAt(table.convertColumnIndexToModel(col));
								if (value != null) {
									return value.toString();
								}
							}
						}
					}
				}
				return null;
			}
		};
		
		// Install component header
		TableColumnModelExt tcm = (TableColumnModelExt) treeTbl.getColumnModel();
		String[] columnToolTips = {
				"Peptide Sequence",
				"Protein Count",
				"Protein Accessions",
				"Spectral Count",
				"Taxonomy"
		};
		final ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		treeTbl.setTableHeader(ch);
		for (int i = 0; i < columnToolTips.length; i++) {
			treeTbl.getColumnExt(i).setToolTipText(columnToolTips[i]);
		}

		// Install mouse listeners in header for right-click popup capabilities
		MouseAdapter ma = ProteinTreeTables.createHeaderMouseAdapter(treeTbl);
		ch.addMouseListener(ma);
		ch.addMouseMotionListener(ma);
		
		// Force column factory to generate columns to properly cache widths
		((AbstractTableModel) treeTbl.getModel()).fireTableStructureChanged();
		
		TableConfig.setColumnWidths(treeTbl, new double[] { 4, 1, 0, 1, 0 });
		TableConfig.setColumnMinWidths(
				treeTbl, UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(), 22, treeTbl.getFont());
		
		// Pre-select root node
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(new TreePath(root));
		
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				// process only the final selection event
				if (tse.getPath() == null) {
					Enumeration<? extends MutableTreeTableNode> children = root.children();
					while (children.hasMoreElements()) {
						MutableTreeTableNode child = children.nextElement();
						((Hit) child.getUserObject()).setSelected(
								cbtsm.isPathSelected(new TreePath(treeTblMdl.getPathToRoot(child)), true));
					}
					refreshChart(true);
				}
			}
		});
		
		// Set default sort order (spectral count, descending)
		((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(3, SortOrder.DESCENDING);

		treeTbl.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				// Clear peptide selection in coverage pane
				coveragePane.clearSelection();
				// Extract peptides from current selection
				Set<SpectrumMatch> matches = new LinkedHashSet<>();
				Set<ProteinHit> proteins = new LinkedHashSet<>();
				int[] rows = treeTbl.getSelectedRows();
				for (int row : rows) {
					PhylogenyTreeTableNode node =
							(PhylogenyTreeTableNode) treeTbl.getPathForRow(row).getLastPathComponent();
					PeptideHit peptide = (PeptideHit) node.getUserObject();
					
					// Update sequence coverage viewer selection
					coveragePane.setSelected(peptide.getSequence(), true);
					
					matches.addAll(peptide.getSpectrumMatches());
					proteins.addAll(peptide.getProteinHits());
				}
				
				// REFRESH peptide spectrum match view
				if (view != focus.SPECTRUM) {
					refreshPSMView(matches);

					// Update protein highlighting
					ProteinHighlightPredicate pchp = (ProteinHighlightPredicate) protCountHighlighter
							.getHighlightPredicate();
					pchp.setProteinHits(proteins);
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						CheckBoxTreeTable treeTbl = ptt.getTreeTable();
						// re-apply highlighter
						treeTbl.removeHighlighter(protCountHighlighter);
						treeTbl.addHighlighter(protCountHighlighter);
					}
				}
					
				if (view == focus.PEPTIDE) {
					// Narrow protein selection down to highlights
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						CheckBoxTreeTable treeTable = ptt.getTreeTable();
						TreeTableModel ttm = treeTable.getTreeTableModel();
						CheckBoxTreeSelectionModel cbtsm = treeTable.getCheckBoxTreeSelectionModel();
						
						List<TreePath> paths = new ArrayList<>();
						PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) ttm.getRoot();
						Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
						while (dfe.hasMoreElements()) {
							PhylogenyTreeTableNode treeNode = (PhylogenyTreeTableNode) dfe.nextElement();
							if (treeNode.isProtein()) {
								Object userObject = treeNode.getUserObject();
								for (ProteinHit proteinHit : proteins) {
									if (proteinHit.equals(userObject)) {
										paths.add(treeNode.getPath());
										break;
									}
								}
							}
						}
						cbtsm.setSelectionPaths(paths.toArray(new TreePath[0]));
					}
					// Carry out selection in table view 
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						ptt.getTreeTable().setRowFilter((hideUnselected) ? new SelectionRowFilter(ptt.getTreeTable()) : null);
					}
				}
				
				// refresh spectrum view
				if (view == focus.SPECTRUM) {
					int selRow = psmTbl.getSelectedRow();
					if (selRow != -1) {
						PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) psmTbl
								.getPathForRow(selRow).getLastPathComponent();
						PeptideSpectrumMatch match = (PeptideSpectrumMatch) node
								.getUserObject();
						refreshSpectrumView(match);
					}
				}
			}
		});
		
		// Reduce node indents to make tree more compact horizontally
		treeTbl.setIndents(0, 0, 2);
		// Hide root node
		treeTbl.setRootVisible(false);

		// Set up node icons
		IconValue iv = new IconValue() {
			@Override
			public Icon getIcon(Object value) {
				return IconConstants.PEPTIDE_TREE_ICON;
			}
		};
		treeTbl.setIconValue(iv);
		
		/* Install highlighters */
		// sequence
		tcm.getColumnExt(0).addHighlighter(new AbstractHighlighter() {
			private Font normalFont = getFont();
			private Font boldFont = normalFont.deriveFont(Font.BOLD);
			@Override
			protected Component doHighlight(Component component,
					ComponentAdapter adapter) {
				component.setFont(normalFont);
				if (!adapter.isLeaf()) {
					if (adapter.getValue(1).equals(1)) {
						component.setFont(boldFont);
					}
				}
				return component;
			}
		});
		// protein count
		tcm.getColumnExt(1).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		// spectral count
		tcm.getColumnExt(3).addHighlighter(new BarChartHighlighter());

		// initially hide protein accession and taxonomy columns
		TableColumnExt col2 = tcm.getColumnExt(2);
		TableColumnExt col4 = tcm.getColumnExt(4);
		col2.setVisible(false);
		col4.setVisible(false);

		// Enable column control widget
		TableConfig.configureColumnControl(treeTbl);
		
		return treeTbl;
	}
	

	/**
	 * Creates and configures the peptide coverage viewer pane.
	 * @return the coverage viewer
	 */
	private CoverageViewerPane setupCoverageViewer() {
		CoverageViewerPane covPane = new CoverageViewerPane();
		
		covPane.addPropertyChangeListener("selection", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				int seqCol = peptideTbl.getColumn("Sequence").getModelIndex();
				for (int i = 0; i < peptideTbl.getRowCount(); i++) {
					Object sequence = peptideTbl.getValueAt(
							i, peptideTbl.convertColumnIndexToView(seqCol));
					if ((sequence != null) && sequence.equals(evt.getNewValue())) {
						if (((Boolean) evt.getOldValue()).booleanValue()) {
							peptideTbl.getSelectionModel().addSelectionInterval(i, i);
						} else {
							peptideTbl.getSelectionModel().removeSelectionInterval(i, i);
						}
						return;
					}
				}
			}
		});
		
		return covPane;
	}

	/**
	 * Creates and configures the spectrum matches tree table view.
	 * @return the spectrum matches tree table
	 */
	private CheckBoxTreeTable setupPSMTable() {
		final PhylogenyTreeTableNode root = new PhylogenyTreeTableNode("PSM View");
		
		// Set up table model
		final SortableTreeTableModel treeTblMdl = new SortableTreeTableModel(root) {
			// Install column names
			{
				setColumnIdentifiers(Arrays.asList(new String[] {
						"ID", "z", "Pep", "X", "O", "C", "I", "M" }));
			}
			// Fool-proof table by allowing only one type of node
			@Override
			public void insertNodeInto(MutableTreeTableNode newChild,
					MutableTreeTableNode parent, int index) {
				if (newChild instanceof PhylogenyTreeTableNode) {
					super.insertNodeInto(newChild, parent, index);
				} else {
					throw new IllegalArgumentException("This tree table requires Phylogeny nodes!");
				}
			}
		};

		// Create table from model
		final SortableCheckBoxTreeTable treeTbl = new SortableCheckBoxTreeTable(treeTblMdl) {
			/** The text to display on top of the table when it's empty */
			private final String emptyStr = "no peptide(s) selected";
			/** The cached width of the empty table text */
			private int emptyStrW = 0;
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (this.getRowCount() == 0) {
					if (emptyStrW == 0) {
						// Cache string width
						emptyStrW = g.getFontMetrics().stringWidth(emptyStr);
					}
					// Enable text anti-aliasing
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					// Draw string on top of empty table
					g.drawString(emptyStr, (this.getWidth() - emptyStrW) / 2, this.getVisibleRect().height / 2);
				}
			}
			
			@Override
			public String getToolTipText(MouseEvent me) {
				JXTreeTable table = (JXTreeTable) me.getSource();
				int col = table.columnAtPoint(me.getPoint());
				if (col != -1) {
					int row = table.rowAtPoint(me.getPoint());
					if (row != -1) {
						TreePath pathForRow = table.getPathForRow(row);
						if (pathForRow != null) {
							PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) pathForRow.getLastPathComponent();
							if (node != null) {
								Object value = node.getValueAt(table.convertColumnIndexToModel(col));
								if (value != null) {
									return value.toString();
								}
							}
						}
					}
				}
				return null;
			}
		};
		
		// Install component header
		TableColumnModelExt tcm = (TableColumnModelExt) treeTbl.getColumnModel();
		String[] columnToolTips = {
				"Spectrum Match ID",
				"Precursor Charge",
				"Peptide Sequences",
				"X!Tandem Confidence",
				"Omssa Confidence",
				"Crux Confidence",
				"InsPecT Confidence",
				"Mascot Confidence"
		};
		final ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		treeTbl.setTableHeader(ch);
		for (int i = 0; i < columnToolTips.length; i++) {
			treeTbl.getColumnExt(i).setToolTipText(columnToolTips[i]);
		}

		// Install mouse listeners in header for right-click popup capabilities
		MouseAdapter ma = ProteinTreeTables.createHeaderMouseAdapter(treeTbl);
		ch.addMouseListener(ma);
		ch.addMouseMotionListener(ma);
		
		// Force column factory to generate columns to properly cache widths
		((AbstractTableModel) treeTbl.getModel()).fireTableStructureChanged();
		
		TableConfig.setColumnWidths(treeTbl, new double[] { 10, 1, 0, 1, 1, 1, 1, 1 });
		TableConfig.setColumnMinWidths(
				treeTbl, UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(), 22, treeTbl.getFont());
		
		// Pre-select root node
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(new TreePath(root));
		
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				// process only the final selection event
				if (tse.getPath() == null) {
					Enumeration<? extends MutableTreeTableNode> children = root.children();
					while (children.hasMoreElements()) {
						MutableTreeTableNode child = children.nextElement();
						((Hit) child.getUserObject()).setSelected(
								cbtsm.isPathSelected(new TreePath(treeTblMdl.getPathToRoot(child)), true));
					}
					refreshChart(true);
				}
			}
		});

		// Set default sort order (PSM ID, ascending)
		((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(0, SortOrder.ASCENDING);

		// Install selection listener to update spectrum panel
		treeTbl.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				if (view != focus.SPECTRUM) {
					// refresh spectrum view
					PhylogenyTreeTableNode node = ((PhylogenyTreeTableNode) evt
							.getPath().getLastPathComponent());
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) node
							.getUserObject();
					refreshSpectrumView(psm);
				}
				// REFRESH peptide view
				if (view == focus.SPECTRUM) {
					// select the view of peptides and proteins depending on the highlighted spectrum
					Collection<PeptideHit> peptides = new HashSet<PeptideHit>();
					int[] rows = treeTbl.getSelectedRows();
					for (int row : rows) {
						PhylogenyTreeTableNode thisNode =
								(PhylogenyTreeTableNode) treeTbl.getPathForRow(row).getLastPathComponent();
						peptides.addAll(((SpectrumMatch) thisNode.getUserObject()).getPeptideHits());
					}
					refreshPeptideViews(peptides);
					// Update protein highlighting
					ProteinHighlightPredicate pchp =
							(ProteinHighlightPredicate) protCountHighlighter.getHighlightPredicate();
					Collection<ProteinHit> proteins = new HashSet<ProteinHit>();
					for (PeptideHit pepHit : peptides) {
						proteins.addAll(pepHit.getProteinHits());
					}
					pchp.setProteinHits(proteins);
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						CheckBoxTreeTable treeTbl = ptt.getTreeTable();
						// re-apply highlighter
						treeTbl.removeHighlighter(protCountHighlighter);
						treeTbl.addHighlighter(protCountHighlighter);
					}
					
					// Narrow protein selection down to highlights
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						CheckBoxTreeTable treeTable = ptt.getTreeTable();
						TreeTableModel ttm = treeTable.getTreeTableModel();
						CheckBoxTreeSelectionModel cbtsm = treeTable.getCheckBoxTreeSelectionModel();
						
						List<TreePath> paths = new ArrayList<>();
						PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) ttm.getRoot();
						Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
						while (dfe.hasMoreElements()) {
							PhylogenyTreeTableNode treeNode = (PhylogenyTreeTableNode) dfe.nextElement();
							if (treeNode.isProtein()) {
								Object userObject = treeNode.getUserObject();
								for (ProteinHit proteinHit : proteins) {
									if (proteinHit.equals(userObject)) {
										paths.add(treeNode.getPath());
										break;
									}
								}
							}
						}
						cbtsm.setSelectionPaths(paths.toArray(new TreePath[0]));
					}
					// Carry out selection in table view 
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						ptt.getTreeTable().setRowFilter((hideUnselected) ? new SelectionRowFilter(ptt.getTreeTable()) : null);
					}
				}
			}
		});
		treeTbl.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Reduce node indents to make tree more compact horizontally
		treeTbl.setIndents(0, 0, 2);
		// Hide root node
		treeTbl.setRootVisible(false);

		// Set up node icons
		IconValue iv = new IconValue() {
			@Override
			public Icon getIcon(Object value) {
				return IconConstants.SPECTRUM_TREE_ICON;
			}
		};
		treeTbl.setIconValue(iv);
		
		// Install renderers and highlighters
		tcm.getColumnExt(1).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "+0"));
		tcm.getColumnExt(3).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN));
		tcm.getColumnExt(4).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_CYAN, ColorUtils.LIGHT_CYAN));
		tcm.getColumnExt(5).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_BLUE, ColorUtils.LIGHT_BLUE));
		tcm.getColumnExt(6).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_MAGENTA, ColorUtils.LIGHT_MAGENTA));
		tcm.getColumnExt(7).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		
		// Initially hide parent peptides column
		tcm.getColumnExt(2).setVisible(false);

		// Enable column control widget
		TableConfig.configureColumnControl(treeTbl);
		
		return treeTbl;
	}
	
	/**
	 * Creates and configures the spectrum viewer panel.
	 * @return the spectrum viewer
	 */
	private SpectrumViewerPanel setupSpectrumViewer() {
		return new SpectrumViewerPanel();
	}
	
	private ScrollableChartPane setupChartPane() {
		// create and configure chart panel for plots
		OntologyData dummyData = new OntologyData() {
			@Override
			public PieDataset getDataset() {
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				pieDataset.setValue("RPM", 8);
				pieDataset.setValue("DNRPM", 2);
				return pieDataset;
			}
		};
		
		final ScrollableChartPane chartPane = new ScrollableChartPane(ChartFactory.createOntologyChart(
				dummyData, OntologyChartType.BIOLOGICAL_PROCESS).getChart());
		
		chartPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String property = evt.getPropertyName();
				Object value = evt.getNewValue();
				if ("hierarchy".equals(property)) {
					HierarchyLevel hl = (HierarchyLevel) value;
					ontologyData.setHierarchyLevel(hl);
					taxonomyData.setHierarchyLevel(hl);
					refreshChart(false);
				} else if ("hideUnknown".equals(property)) {
					boolean doHide = (Boolean) value;
					ontologyData.setHideUnknown(doHide);
					taxonomyData.setHideUnknown(doHide);
				} else if ("groupingLimit".equals(property)) {
					double limit = (Double) value;
					ontologyData.setMinorGroupingLimit(limit);
					taxonomyData.setMinorGroupingLimit(limit);
					refreshChart(false);
				} else if ("selection".equals(property)) {
					Collection<ProteinHit> proteinHits = null;
					if (chartType instanceof OntologyChartType) {
						proteinHits = ontologyData.getProteinHits((String) value);
					} else if (chartType instanceof TaxonomyChartType) {
						proteinHits = taxonomyData.getProteinHits((String) value);
					}
					if (proteinHits != null) {
						if (chartPane.getHierarchyLevel() == HierarchyLevel.META_PROTEIN_LEVEL) {
							proteinHits = ((ProteinHitList) proteinHits).getProteinSet();
						}
					}
					// Update protein highlighting
					ProteinHighlightPredicate pchp =
							(ProteinHighlightPredicate) chartSelHighlighter.getHighlightPredicate();
					pchp.setProteinHits(proteinHits);
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						CheckBoxTreeTable treeTbl = ptt.getTreeTable();
						// re-apply highlighter
						treeTbl.removeHighlighter(chartSelHighlighter);
						treeTbl.addHighlighter(chartSelHighlighter);
					}
				}
			}
		});
		
		return chartPane;
	}

	/**
	 * Creates the titled panel containing the protein tree table views.
	 * @return the protein panel
	 */
	private JXTitledPanel createProteinPanel() {
		JPanel proteinPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		// Wrap protein tree tables in card layout
		final CardLayout protCardLyt = new CardLayout();
		final JPanel protCardPnl = new JPanel(protCardLyt);
		
		for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
			JScrollPane scrollPane = new JScrollPane(ptt.getTreeTable());
			scrollPane.setPreferredSize(new Dimension(800, 150));
			
			scrollPane.setName(ptt.getLabel());
			protCardPnl.add(scrollPane, ptt.getLabel());
		}

		proteinPnl.add(protCardPnl, CC.xy(2, 2));
		
		/* Create button panels for header of titled panel around protein table */
		// Create left-hand button panel
		JPanel protLeftBtnPnl = new JPanel(new FormLayout(
				"19px, 19px", "20px"));
		protLeftBtnPnl.setOpaque(false);
		
		// Create 'Expand All' button
		JButton expandAllBtn = new JButton(IconConstants.EXPAND_ALL_ICON);
		expandAllBtn.setRolloverIcon(IconConstants.EXPAND_ALL_ROLLOVER_ICON);
		expandAllBtn.setPressedIcon(IconConstants.EXPAND_ALL_PRESSED_ICON);
		expandAllBtn.setToolTipText("Expand All");
		expandAllBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(expandAllBtn));
		
		expandAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				for (Component comp : protCardPnl.getComponents() ) {
			        if (comp.isVisible() == true) {
			            Component view = ((JScrollPane) comp).getViewport().getView();
			            if (view instanceof CheckBoxTreeTable) {
			            	((CheckBoxTreeTable) view).expandAll();
			            }
			            break;
			        }
			    }
			}
		});
		
		// Create 'Collapse All' button
		JButton collapseAllBtn = new JButton(IconConstants.COLLAPSE_ALL_ICON);
		collapseAllBtn.setRolloverIcon(IconConstants.COLLAPSE_ALL_ROLLOVER_ICON);
		collapseAllBtn.setPressedIcon(IconConstants.COLLAPSE_ALL_PRESSED_ICON);
		collapseAllBtn.setToolTipText("Collapse All");
		collapseAllBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(collapseAllBtn));
		
		collapseAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				for (Component comp : protCardPnl.getComponents() ) {
			        if (comp.isVisible() == true) {
			            Component view = ((JScrollPane) comp).getViewport().getView();
			            if (view instanceof CheckBoxTreeTable) {
			            	((CheckBoxTreeTable) view).collapseAll();
			            }
			            break;
			        }
			    }
			}
		});
		
		protLeftBtnPnl.add(expandAllBtn, CC.xy(1, 1));
		protLeftBtnPnl.add(collapseAllBtn, CC.xy(2, 1));

		// Create right-hand button panel
		JPanel protRightBtnPnl = new JPanel(new FormLayout(
				"p:g, 4dlu, 22px, 1dlu, 36px, 1px", "20px"));
		protRightBtnPnl.setOpaque(false);
		
		// Create 'Hide Unselected" checkbox
		hideUnselChk = new JCheckBox("Hide Unselected", hideUnselected) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(this.getBackground());
				g.fillRect(2, 5, 9, 9);
				super.paintComponent(g);
			}
		};
		hideUnselChk.setOpaque(false);
		hideUnselChk.setForeground(Color.WHITE);
		hideUnselChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean sel = ((AbstractButton) e.getSource()).isSelected();
				hideUnselected = sel;
				for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
					ptt.getTreeTable().setRowFilter((sel) ? new SelectionRowFilter(ptt.getTreeTable()) : null);
				}
			}
		});
		
		JButton exportBtn = new JButton(IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
		exportBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
		exportBtn.setToolTipText("Export Table Contents");
		exportBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(exportBtn));
		
		exportBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Determine currently visible tree table
				for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
					CheckBoxTreeTable treeTbl = ptt.getTreeTable();
					if (treeTbl.getParent().getParent().isVisible()) {
						// dump table contents
//						TableColumnExt wrCol = treeTbl.getColumnExt(
//								treeTbl.convertColumnIndexToView(ProteinTreeTables.WEB_RESOURCES_COLUMN));
//						boolean visible = wrCol.isVisible();
//						wrCol.setVisible(false);
						DbSearchResultPanel.this.dumpTableContents(treeTbl);
//						wrCol.setVisible(visible);
						return;
					}
				}
			}
		});

		// Create button for showing table the type selection popup menu
		final JToggleButton tableTypeBtn = new JToggleButton(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ICON));
		tableTypeBtn.setRolloverIcon(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ROLLOVER_ICON));
		tableTypeBtn.setPressedIcon(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_PRESSED_ICON));
		tableTypeBtn.setToolTipText("Select Hierarchical View");
		tableTypeBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(tableTypeBtn));
		
		// Create the table type selection popup menu
		final JPopupMenu tableTypePop = new JPopupMenu();
		ActionListener hierarchyListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Determine tree table to be made visible
				String label = ((AbstractButton) evt.getSource()).getText();
				ProteinTreeTables ptt = ProteinTreeTables.valueOfLabel(label);
				
				// Determine currently visible tree table
				for (ProteinTreeTables curPtt : ProteinTreeTables.values()) {
					if (curPtt.getTreeTable().getParent().getParent().isVisible()) {
						// check whether checkbox selection has changed
						if (curPtt.hasCheckSelectionChanged()) {
							curPtt.cacheCheckSelection();
							// mark other tree tables for updating the next time they're displayed
							for (ProteinTreeTables ptt2 : ProteinTreeTables.values()) {
								if (ptt2 != curPtt) {
									ptt2.setCheckSelectionNeedsUpdating(true);
								}
							}
						}
						// Update checkbox selection of targeted tree table, also re-sorts/filters
						ptt.updateCheckSelection();
						
						break;
					}
				}
				
				// Show card
				protCardLyt.show(protCardPnl, label);
				
				// Reset peptide table
				refreshPeptideViews(null);
			}
		};
		
		ButtonGroup tableTypeGroup = new ButtonGroup();
		boolean first = true;
		for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
			JMenuItem item = new JRadioButtonMenuItem(ptt.getLabel(), first);
			first = false;
			item.addActionListener(hierarchyListener);
			tableTypeGroup.add(item);
			tableTypePop.add(item);
		}
		
		tableTypePop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				tableTypeBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});

		tableTypeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableTypePop.show(tableTypeBtn, 0, tableTypeBtn.getHeight());
			}
		});

		protRightBtnPnl.add(hideUnselChk, CC.xy(1, 1));
		protRightBtnPnl.add(exportBtn, CC.xy(3, 1));
		protRightBtnPnl.add(tableTypeBtn, CC.xy(5, 1));

		// Create titled panel around protein table
		return PanelConfig.createTitledPanel(
				"Proteins", proteinPnl, protLeftBtnPnl, protRightBtnPnl);
	}
	
	/**
	 * Creates the titled panel containing the peptide views (i.e. tree table and coverage viewer).
	 * @return the peptide panel
	 */
	private JXTitledPanel createPeptidePanel() {
		JPanel peptidePnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		// wrap peptide views in card layout
		final CardLayout pepCardLyt = new CardLayout();
		final JPanel pepCardPnl = new JPanel(pepCardLyt);
		
		final JScrollPane peptideTableScpn = new JScrollPane(peptideTbl);
		peptideTableScpn.setPreferredSize(new Dimension(350, 100));
		
		pepCardPnl.add(peptideTableScpn, "Table");
		pepCardPnl.add(coveragePane, "Coverage");

		peptidePnl.add(pepCardPnl, CC.xy(2, 2));
		
		// create button panel
		JPanel buttonPnl = new JPanel(new FormLayout("22px, 1dlu, 20px, 1dlu, 20px, 1px", "20px"));
		buttonPnl.setOpaque(false);
		
		// create focus selection button
				focusPeptideBtn = new JToggleButton(IconConstants.ZOOM_ICON);
				focusPeptideBtn.setRolloverIcon(IconConstants.ZOOM_ROLLOVER_ICON);
				focusPeptideBtn.setPressedIcon(IconConstants.ZOOM_PRESSED_ICON);
				focusPeptideBtn.setToolTipText("Focus Peptide Selection");
				focusPeptideBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(focusPeptideBtn));
				focusPeptideBtn.setPreferredSize(new Dimension(22, 20));
				
				focusPeptideBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// lock focus onto peptides - all peptides will be displayed and dependencies shifted onto this table
						if (focusPeptideBtn.isSelected()) {
							focusSpectraBtn.setSelected(false);
							view = focus.PEPTIDE;
							refreshPeptideViews(dbSearchResult.getMetaProteins().getPeptideSet());
						} else {
							view = focus.PROTEIN;
						}
					}
				});
		
				// create export buuton
		JButton exportBtn = new JButton(IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
		exportBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
		exportBtn.setToolTipText("Export Table Contents");
		exportBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(exportBtn));
		
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (peptideTbl.getRowCount() > 0) {
					dumpTableContents(peptideTbl);
				} else {
					JOptionPane.showMessageDialog(DbSearchResultPanel.this,
							"Table is empty, nothing to export", "Note", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	
		// create control buttons for card layout
		JToggleButton tableBtn = new JToggleButton(IconConstants.CHECKBOX_TABLE_ICON, true);
		tableBtn.setRolloverIcon(IconConstants.CHECKBOX_TABLE_ROLLOVER_ICON);
		tableBtn.setPressedIcon(IconConstants.CHECKBOX_TABLE_PRESSED_ICON);
		tableBtn.setToolTipText("Table View");
		tableBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(tableBtn));
		
		tableBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				pepCardLyt.show(pepCardPnl, "Table");
				pepTtlPnl.setTitle("Peptides (" + peptideTbl.getRowCount() + ")");
			}
		});

		JToggleButton coverageBtn = new JToggleButton(IconConstants.COVERAGE_ICON);
		coverageBtn.setRolloverIcon(IconConstants.COVERAGE_ROLLOVER_ICON);
		coverageBtn.setPressedIcon(IconConstants.COVERAGE_PRESSED_ICON);
		coverageBtn.setToolTipText("Coverage View");
		coverageBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(coverageBtn));
		
		coverageBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				pepCardLyt.show(pepCardPnl, "Coverage");
				pepTtlPnl.setTitle("Sequence Coverage");
			}
		});
		
		ButtonGroup viewGrp = new ButtonGroup();
		viewGrp.add(tableBtn);
		viewGrp.add(coverageBtn);
		
		buttonPnl.add(exportBtn, CC.xy(1, 1));
		buttonPnl.add(tableBtn, CC.xy(3, 1));
		buttonPnl.add(coverageBtn, CC.xy(5, 1));
	
		// wrap peptide panel in titled panel with control buttons in title
		return PanelConfig.createTitledPanel("Peptides", peptidePnl, focusPeptideBtn, buttonPnl);
	}
	
	/**
	 * Creates the titled panel containing the spectrum match table view.
	 * @return the PSM panel
	 */
	private JXTitledPanel createPSMPanel() {
		JScrollPane psmTableScp = new JScrollPane(psmTbl);
		psmTableScp.setPreferredSize(new Dimension(350, 100));
		
		final JPanel psmPanel = new JPanel();
		psmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		psmPanel.add(psmTableScp, CC.xy(2, 2));
		
		// create focus selection button
		focusSpectraBtn = new JToggleButton(IconConstants.ZOOM_ICON);
		focusSpectraBtn.setRolloverIcon(IconConstants.ZOOM_ROLLOVER_ICON);
		focusSpectraBtn.setPressedIcon(IconConstants.ZOOM_PRESSED_ICON);
		focusSpectraBtn.setToolTipText("Focus Spectrum Selection");
		focusSpectraBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(focusSpectraBtn));
		focusSpectraBtn.setPreferredSize(new Dimension(22, 20));
		
		focusSpectraBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// lock focus onto spectra - all spectra will be displayed and dependencies shifted onto spectra table
				if (focusSpectraBtn.isSelected()) {
					focusPeptideBtn.setSelected(false);
					view = focus.SPECTRUM;
					List<SpectrumMatch> matches = new ArrayList<SpectrumMatch>();
					for (ProteinHit protHit : dbSearchResult.getProteinHitList()) {
						for (PeptideHit pepHit : protHit.getPeptideHitList()) {
							matches.addAll(pepHit.getSpectrumMatches());
						}
					}
					refreshPSMView(matches);
				} else {
					view = focus.PROTEIN;
				}
			}
		});
		
		// create export button decoration
		JButton exportBtn = new JButton(IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
		exportBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
		exportBtn.setToolTipText("Export Table Contents");
		exportBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(exportBtn));
		exportBtn.setPreferredSize(new Dimension(22, 20));
		
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (psmTbl.getRowCount() > 0) {
					dumpTableContents(psmTbl);					
				} else {
					JOptionPane.showMessageDialog(DbSearchResultPanel.this,
							"Table is empty, nothing to export", "Note", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	
		return PanelConfig.createTitledPanel("Spectrum Matches", psmPanel, focusSpectraBtn, exportBtn);
	}

	/**
	 * Creates the titled panel containing the chart views (i.e. the spectrum viewer and pie/bar charts).
	 * @return the chart panel
	 */
	private JXTitledPanel createChartPanel() {
		
		// Build the spectrum panel containing annotation filter buttons
		JPanel spectrumPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		spectrumPnl.add(this.spectrumPnl, CC.xy(2, 2));
		
		// Build the chart panel containing pie/bar charts
		JPanel chartPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		chartPnl.add(chartPane, CC.xy(2, 2));
		
		// Add chart/spectrum panels to card layout
		final CardLayout chartCl = new CardLayout();
		final JPanel chartCardPnl = new JPanel(chartCl);

		// Add cards
		chartCardPnl.add(spectrumPnl, "Spectrum");
		chartCardPnl.add(chartPnl, "Charts");

		// Wrap chart/spectrum panels in titled panel
		final JXTitledPanel chartTtlPnl =
				PanelConfig.createTitledPanel("Spectrum Viewer", chartCardPnl);
		
		// Create button panel for chart/spectrum titled panel
		JPanel buttonPnl = new JPanel(new FormLayout("22px, 1dlu, 21px, 1px, 11px, 1px", "20px"));
		buttonPnl.setOpaque(false);
		
		// Link chart/spectrum buttons together using button group
		ButtonGroup bg = new ButtonGroup();

		// Create chart selection button
		final JToggleButton chartTgl = new JToggleButton(IconConstants.PIE_CHART_ICON);
		chartTgl.setRolloverIcon(IconConstants.PIE_CHART_ROLLOVER_ICON);
		chartTgl.setPressedIcon(IconConstants.PIE_CHART_PRESSED_ICON);
		chartTgl.setToolTipText("Show Detail Charts");
		chartTgl.setUI((RolloverButtonUI) RolloverButtonUI.createUI(chartTgl));
		chartTgl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				chartCl.show(chartCardPnl, "Charts");
				chartTtlPnl.setTitle("Detail Charts");
			}
		});

		// Create chart type selection button
		final JToggleButton chartTypeTgl = this.createChartTypeButton();
		chartTypeTgl.setBorder(BorderFactory.createCompoundBorder(
				chartTypeTgl.getBorder(), BorderFactory.createEmptyBorder(0, -2, 0, 0)));
//		chartTypeTgl.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				chartTgl.setSelected(true);
//			}
//		});
		chartTypeTgl.setModel(new JToggleButton.ToggleButtonModel() {
			private ButtonModel delegate = chartTgl.getModel();
			@Override
			public void setRollover(boolean b) {
				super.setRollover(b);
				delegate.setRollover(b);
			}
			@Override
			public void setArmed(boolean b) {
				super.setArmed(b);
				delegate.setArmed(b);
			}
			@Override
			public void setPressed(boolean b) {
				super.setPressed(b);
				delegate.setPressed(b);
			}
		});
		
		// Create spectrum viewer button
		JToggleButton specTgl = new JToggleButton(null, IconConstants.SPECTRUM_ICON, true);
		specTgl.setRolloverIcon(IconConstants.SPECTRUM_ROLLOVER_ICON);
		specTgl.setPressedIcon(IconConstants.SPECTRUM_PRESSED_ICON);
		specTgl.setToolTipText("Show Spectrum Viewer");
		specTgl.setUI((RolloverButtonUI) RolloverButtonUI.createUI(specTgl));
		specTgl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				chartCl.show(chartCardPnl, "Spectrum");
				chartTtlPnl.setTitle("Spectrum Viewer");
			}
		});

		bg.add(chartTgl);
		bg.add(specTgl);

		buttonPnl.add(specTgl, CC.xy(1, 1));
		buttonPnl.add(chartTgl, CC.xyw(3, 1, 2));
		buttonPnl.add(chartTypeTgl, CC.xyw(4, 1, 2));
		
		chartTtlPnl.setRightDecoration(buttonPnl);
		
		return chartTtlPnl;
	}

	/**
	 * Creates and returns a button prompting to select a chart type.
	 */
	private JToggleButton createChartTypeButton() {
		// init chart types
		List<ChartType> tmp = new ArrayList<ChartType>();
		for (ChartType oct : OntologyChartType.values()) {
			tmp.add(oct);
		}
		for (ChartType tct : TaxonomyChartType.values()) {
			tmp.add(tct);
		}
		final ChartType[] chartTypes = tmp.toArray(new ChartType[0]);
		
		// create and configure button for chart type selection
		// TODO: link enable state to busy/enable state of chart panel
		Icon emptyIcon = IconConstants.createEmptyIcon(0, 16);
		final JToggleButton chartTypeBtn = new JToggleButton(
				IconConstants.createArrowedIcon(emptyIcon));
		chartTypeBtn.setRolloverIcon(
				IconConstants.createArrowedIcon(emptyIcon));
		chartTypeBtn.setPressedIcon(
				IconConstants.createArrowedIcon(emptyIcon));
		chartTypeBtn.setToolTipText("Select Chart Type");
	
		chartTypeBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(chartTypeBtn));
	
		// create chart type selection popup
		final JPopupMenu chartTypePop = new JPopupMenu();
	
		final JMenu ontologyMenu = new JMenu("Ontology");
		ontologyMenu.setIcon(IconConstants.PIE_CHART_ICON);
		chartTypePop.add(ontologyMenu);
		final JMenu taxonomyMenu = new JMenu("Taxonomy");
		taxonomyMenu.setIcon(IconConstants.PIE_CHART_ICON);
		chartTypePop.add(taxonomyMenu);
	
		ActionListener chartListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String title = ((JMenuItem) evt.getSource()).getText();
				ChartType newChartType = null;
				for (ChartType chartType : chartTypes) {
					if (title.equals(chartType.toString())) {
						newChartType = chartType;
					}
				}
				if (newChartType != chartType) {
					chartType = newChartType;
					refreshChart(false);
				}
			}
		};
	
		ButtonGroup chartBtnGrp = new ButtonGroup();
		int j = 0;
		for (ChartType chartType : chartTypes) {
			JMenuItem item = new JRadioButtonMenuItem(chartType.toString(), (j++ == 0));
			item.addActionListener(chartListener);
			chartBtnGrp.add(item);
			if (chartType instanceof OntologyChartType) {
				ontologyMenu.add(item);
			} else if (chartType instanceof TaxonomyChartType) {
				taxonomyMenu.add(item);
			} else {
				item.setIcon(IconConstants.BAR_CHART_ICON);
				chartTypePop.add(item);
			}
		}
	
		ButtonGroup ontoPieOrBarGroup = new ButtonGroup();
		JMenuItem ontoAsPieItem = new JRadioButtonMenuItem("Show As Pie Chart", IconConstants.PIE_CHART_ICON, true);
		ontoAsPieItem.putClientProperty("pie", true);
		ontoAsPieItem.putClientProperty("data", "onto");
		ontoPieOrBarGroup.add(ontoAsPieItem);
	
		JMenuItem ontoAsBarItem = new JRadioButtonMenuItem("Show As Bar Chart", IconConstants.BAR_CHART_ICON);
		ontoAsBarItem.putClientProperty("pie", false);
		ontoAsBarItem.putClientProperty("data", "onto");
		ontoPieOrBarGroup.add(ontoAsBarItem);
	
		ontologyMenu.addSeparator();
		ontologyMenu.add(ontoAsPieItem);
		ontologyMenu.add(ontoAsBarItem);
	
		ButtonGroup taxPieOrBarGroup = new ButtonGroup();
		JMenuItem taxAsPieItem = new JRadioButtonMenuItem("Show As Pie Chart", IconConstants.PIE_CHART_ICON, true);
		taxAsPieItem.putClientProperty("pie", true);
		taxAsPieItem.putClientProperty("data", "tax");
		taxPieOrBarGroup.add(taxAsPieItem);
	
		JMenuItem taxAsBarItem = new JRadioButtonMenuItem("Show As Bar Chart", IconConstants.BAR_CHART_ICON);
		taxAsBarItem.putClientProperty("pie", false);
		taxAsBarItem.putClientProperty("data", "tax");
		taxPieOrBarGroup.add(taxAsBarItem);
	
		taxonomyMenu.addSeparator();
		taxonomyMenu.add(taxAsPieItem);
		taxonomyMenu.add(taxAsBarItem);
	
		ActionListener pieOrBarListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JComponent source = (JComponent) evt.getSource();
				Object data = source.getClientProperty("data");
				Boolean showAsPie = (Boolean) source.getClientProperty("pie");
				if ("onto".equals(data)) {
					ontologyData.setShowAsPie(showAsPie);
					ontologyMenu.setIcon(showAsPie ? IconConstants.PIE_CHART_ICON : IconConstants.BAR_CHART_ICON);
				} else if ("tax".equals(data)) {
					taxonomyData.setShowAsPie(showAsPie);
					taxonomyMenu.setIcon(showAsPie ? IconConstants.PIE_CHART_ICON : IconConstants.BAR_CHART_ICON);
				} else {
					// abort (shouldn't happen, actually)
					return;
				}
				refreshChart(false);
			}
		};
	
		ontoAsPieItem.addActionListener(pieOrBarListener);
		ontoAsBarItem.addActionListener(pieOrBarListener);
		taxAsPieItem.addActionListener(pieOrBarListener);
		taxAsBarItem.addActionListener(pieOrBarListener);
	
		chartTypePop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				chartTypeBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
			public void popupMenuCanceled(PopupMenuEvent e) { }
		});
	
		// link popup to button
		chartTypeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartTypePop.show(chartTypeBtn, 0, chartTypeBtn.getHeight());
			}
		});
		return chartTypeBtn;
	}
	
	/**
	 * Prompts selection of columns and exports contents of the specified tree
	 * table to a CSV file.
	 * @param treeTbl the tree table to dump
	 */
	private void dumpTableContents(final CheckBoxTreeTable treeTbl) {
		// cache visibility state of table columns before dumping
		List<TableColumn> columns = treeTbl.getColumns(true);
		List<Boolean> visible = new ArrayList<>();
		for (TableColumn column : columns) {
			visible.add(((TableColumnExt) column).isVisible());
		}
		
		ParameterMap params = new ParameterMap() {
			@Override
			public void initDefaults() {
				List<TableColumn> columns = treeTbl.getColumns(true);
				List<Integer> indexes = new ArrayList<>();
				for (int i = 0; i < columns.size(); i++) {
					Object id = columns.get(i).getIdentifier();
					if (id instanceof String) {
						indexes.add(i);
					}
				}
//				int size = columns.size();
				int size = indexes.size();
				int width = 2;
				int height = (size + 1) / 2;
				Boolean[] selected = new Boolean[size];
				String[] names = new String[size];
//				for (int i = 0; i < size; i++) {
				for (Integer index : indexes) {
					TableColumnExt column = (TableColumnExt) columns.get(index);
					Object id = column.getIdentifier();
					if (id instanceof String) {
						
					}
					selected[index] = column.isVisible();
					names[index] = column.getToolTipText();
				}
				this.put("columns", new BooleanMatrixParameter(width, height, selected, names, names, "Columns"));
			}
			@Override
			public File toFile(String path) throws IOException {
				return null;
			}
		};
		int res = AdvancedSettingsDialog.showDialog(
				ClientFrame.getInstance(), "Export Table Contents", true, params);
		
		if (res == AdvancedSettingsDialog.DIALOG_ACCEPTED) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Select Export Destination");
			chooser.setFileFilter(Constants.TSV_FILE_FILTER);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setMultiSelectionEnabled(false);
			res = chooser.showSaveDialog(ClientFrame.getInstance());
			if (res == JFileChooser.APPROVE_OPTION) {
				try {
					// set visibility state of columns to reflect parameter dialog selection
					Boolean[] selections = (Boolean[]) params.get("columns").getValue();
					for (int i = 0; i < selections.length; i++) {
						((TableColumnExt) columns.get(i)).setVisible(selections[i]);
					}
					
					// dump table contents to selected file
					TableConfig.dumpTableToCSV(treeTbl, chooser.getSelectedFile());
					
					// show success message
					JOptionPane.showMessageDialog(
							ClientFrame.getInstance(), "Successfully dumped table data to file.",
							"Export Success", JOptionPane.INFORMATION_MESSAGE);
//					res = JOptionPane.showOptionDialog(ClientFrame.getInstance(),
//							"Successfully dumped table data to file.",
//							"Export Success", JOptionPane.OK_CANCEL_OPTION,
//							JOptionPane.INFORMATION_MESSAGE, null,
//							new String[] { "OK", "Go to File" }, "OK");
//					if (res == 1) {
//						File parent = chooser.getSelectedFile().getParentFile();
////						Desktop.getDesktop().open(parent);
//						Desktop.getDesktop().browse(parent.toURI());
//					}
				} catch (IOException e) {
					// show error message
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), e.getMessage(),
									"I/O Error", e, ErrorLevel.SEVERE, null));
				} finally {
					// restore visibility state of columns
					int i = 0;
					for (TableColumn column : columns) {
						((TableColumnExt) column).setVisible(visible.get(i++));
					}
				}
			}
		}
	}

	/**
	 * Updates the protein view contents from the client's search result object.
	 */
	public void refreshProteinViews() {
		new RefreshTablesTask().execute();
	}

	/**
	 * Updates the peptide views from the specified protein hits.
	 * @param proteins the protein hits containing the peptides to display
	 */
	protected void refreshPeptideViews(Collection<PeptideHit> peptides) {
		
		// Clear tables, etc.
		TableConfig.clearTable(peptideTbl);
		coveragePane.clear();
		if (view != focus.SPECTRUM) {
			TableConfig.clearTable(psmTbl);
			spectrumPnl.clearSpectrum();
		}
		for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
			CheckBoxTreeTable treeTbl = ptt.getTreeTable();
			treeTbl.removeHighlighter(protCountHighlighter);
		}
		
		// Insert peptide nodes
		SortableTreeTableModel treeTblMdl;
		DefaultMutableTreeTableNode root;
		int maxProtCount, maxSpecCount = 0;
		treeTblMdl = (SortableTreeTableModel) peptideTbl
					.getTreeTableModel();
		root = (DefaultMutableTreeTableNode) treeTblMdl.getRoot();
		maxProtCount = 0;
		if (peptides != null) {
			for (PeptideHit peptide : peptides) {
				PhylogenyTreeTableNode pepNode = new PhylogenyTreeTableNode(
						peptide);
				root.add(pepNode);
				maxProtCount = Math.max(maxProtCount, peptide.getProteinCount());
				maxSpecCount = Math.max(maxSpecCount, peptide.getSpectralCount());
			}
		int numPeptides = peptides.size();
		if (numPeptides > 0) {
			// Refresh tree table by resetting root node
			treeTblMdl.sort();
			treeTblMdl.setRoot(root);
		}
			
			// Adjust highlighters
			peptideTbl.updateHighlighters(1, 0, maxProtCount);
			peptideTbl.updateHighlighters(3, 0, maxSpecCount);
			
			// Update coverage viewer
			Collection<ProteinHit> proteins = new HashSet<ProteinHit>();
			for (PeptideHit peptide : peptides) {
				proteins.addAll(peptide.getProteinHits());
			}
			coveragePane.setData(proteins, peptides);

			// Update checkbox selections
			CheckBoxTreeSelectionModel cbtsm = peptideTbl.getCheckBoxTreeSelectionModel();
			Enumeration<? extends MutableTreeTableNode> children = root.children();
			while (children.hasMoreElements()) {
				MutableTreeTableNode child = children.nextElement();
				if (!((PeptideHit) child.getUserObject()).isSelected()) {
					cbtsm.removeSelectionPath(new TreePath(treeTblMdl.getPathToRoot(child)));
				}
			}
			
			// Select first row
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
				}
			});
			
			// Update panel title
			if (!coveragePane.isVisible()) {
				pepTtlPnl.setTitle("Peptides (" + numPeptides + ")");
			}
		} else {
			if (!coveragePane.isVisible()) {
				pepTtlPnl.setTitle("Peptides");
			}
		}
	}
	
	/**
	 * Updates the spectrum match views from the specified list of spectrum
	 * matches.
	 * @param matches the spectrum matches to display
	 */
	protected void refreshPSMView(Collection<SpectrumMatch> matches) {
		// Clear table
		TableConfig.clearTable(psmTbl);
		spectrumPnl.clearSpectrum();
		
		// Insert PSM nodes
		SortableTreeTableModel treeTblMdl =
				(SortableTreeTableModel) psmTbl.getTreeTableModel();
		DefaultMutableTreeTableNode root =
				(DefaultMutableTreeTableNode) treeTblMdl.getRoot();
		for (SpectrumMatch match : matches) {
			root.add(new PhylogenyTreeTableNode(match));
		}
		
		if (matches.size() > 0) {
			// Refresh tree table by resetting root node
			treeTblMdl.sort();
			treeTblMdl.setRoot(treeTblMdl.getRoot());
			
			// Select first row
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					psmTbl.getSelectionModel().setSelectionInterval(0, 0);
				}
			});
			
			// Update panel title
			psmTtlPnl.setTitle("Spectrum Matches (" + matches.size() + ")");
		} else {
			psmTtlPnl.setTitle("Spectrum Matches)");
		}
	}
	
	/**
	 * Updates the spectrum viewer using the spectrum associated with the
	 * provided spectrum match.
	 * @param psm the spectrum match
	 */
	protected void refreshSpectrumView(PeptideSpectrumMatch psm) {
		MascotGenericFile mgf = null;
		String sequence = null;
		
		// Extract peptide sequence from peptide table
		int selRow = peptideTbl.getSelectedRow();
		if (selRow != -1) {
			sequence = (String) peptideTbl.getValueAt(selRow, peptideTbl.getHierarchicalColumn());
			// Extract spectrum file
			if (!Client.isViewer()) {
				// Get spectrum file from database when connected
				try {
					mgf = Client.getInstance().getSpectrumBySearchSpectrumID(psm.getSearchSpectrumID());
				} catch (SQLException e) {
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				}
			} else {
				// Read spectrum from MGF file accompanying imported result object, if possible
				FileExperiment experiment = 
						(FileExperiment) ClientFrame.getInstance().getProjectPanel().getCurrentExperiment();
				mgf = Client.getInstance().readSpectrumFromFile(
						experiment.getSpectrumFile().getPath(), psm.getStartIndex(), psm.getEndIndex());
			}
		}
		spectrumPnl.refreshSpectrum(mgf, sequence);
	}
	
	/**
	 * Updates the chart view component and optionally the underlying data container.
	 * @param refreshData <code>true</code> if the underlying data shall be updated, too,
	 *  <code>false</code> otherwise
	 */
	protected void refreshChart(boolean refreshData) {
		if (refreshData) {
			// build list of visible meta-proteins from result object
//			ProteinHitList metaProteins = new ProteinHitList();
//			for (ProteinHit mph : dbSearchResult.getMetaProteins()) {
//				for (ProteinHit ph : ((MetaProteinHit) mph).getProteinHitList()) {
//					if (ph.isSelected()) {
//						metaProteins.add(mph);
//						break;
//					}
//				}
//			}
			ProteinHitList metaProteins = new ProteinHitList(dbSearchResult.getMetaProteins());
			// update chart data containers
			ontologyData.setData(metaProteins);
			taxonomyData.setData(metaProteins);
		}
		
		Chart chart = null;

		// create chart instance
		if (chartType instanceof OntologyChartType) {
			chart = ChartFactory.createOntologyChart(
					ontologyData, chartType);
		} else if (chartType instanceof TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyChart(
					taxonomyData, chartType);
		}
		
		if (chart != null) {
			// insert chart into panel
			chartPane.setChart(chart.getChart(), true);
			// reset protein highlighting
			ProteinHighlightPredicate pchp =
					(ProteinHighlightPredicate) chartSelHighlighter.getHighlightPredicate();
			pchp.setProteinHits(new ArrayList<ProteinHit>());
			for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
				CheckBoxTreeTable treeTbl = ptt.getTreeTable();
				// re-apply highlighter
				treeTbl.removeHighlighter(chartSelHighlighter);
				treeTbl.addHighlighter(chartSelHighlighter);
			}
		} else {
			System.err.println("Chart type could not be determined!");
		}
	}

	/**
	 * Worker implementation to refresh all detail view tables.
	 * 
	 * @author A. Behne
	 */
	private class RefreshTablesTask extends SwingWorker {
		
		@Override
		protected Object doInBackground() throws Exception {
			Thread.currentThread().setName("RefreshTablesThread");
			
			try {
				DbSearchResultPanel resultPnl = DbSearchResultPanel.this;
				
				
				// Begin by clearing all views
				for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
					TableConfig.clearTable(ptt.getTreeTable());
				}
				TableConfig.clearTable(peptideTbl);
				coveragePane.clear();
				TableConfig.clearTable(psmTbl);
				spectrumPnl.clearSpectrum();
				
				// Fetch search result object				
				resultPnl.dbSearchResult = Client.getInstance().fetchResults();					

				// Build local chart data objects
				HierarchyLevel hl = resultPnl.chartPane.getHierarchyLevel();
				resultPnl.ontologyData = new OntologyData(resultPnl.dbSearchResult, hl);
				resultPnl.taxonomyData = new TaxonomyData(resultPnl.dbSearchResult, hl);

				// Insert new result data into tables
				this.refreshProteinTables();
				
				// Refresh chart
				resultPnl.refreshChart(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		/**
		 * Refreshes the contents of the protein tree tables.
		 */
		private void refreshProteinTables() {		
			if (dbSearchResult != null && !dbSearchResult.isEmpty()) {
		
				ProteinHitList metaProteins = dbSearchResult.getMetaProteins();
				long metaProtCount = metaProteins.size();
		
				// Notify status bar
				Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES");
				Client.getInstance().firePropertyChange("resetall", null, metaProtCount);
				Client.getInstance().firePropertyChange("resetcur", null, metaProtCount);
				
				// Values for construction of highlighter
				int protCount = 0, maxPeptideCount = 0, maxSpecCount = 0;
				double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;
		
				/* Build tree table trees from (meta-)proteins */
				// Iterate meta-proteins
				for (ProteinHit metaProtein : metaProteins) {
					
					PhylogenyTreeTableNode metaNode = new PhylogenyTreeTableNode(metaProtein);
					
					ProteinHitList proteinHits = ((MetaProteinHit) metaProtein).getProteinHitList();
					for (ProteinHit proteinHit : proteinHits) {
		
						// Calculate NSAF
						double nsaf = proteinHit.getNSAF();
						if (nsaf < 0.0) {
							// Calculate NSAF
							nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(),	proteinHit);
							proteinHit.setNSAF(nsaf);
						}
		
						// Determine maximum values for visualization later on
						maxCoverage = Math.max(maxCoverage,	proteinHit.getCoverage());
						maxPeptideCount = Math.max(maxPeptideCount, proteinHit.getPeptideCount());
						maxSpecCount = Math.max(maxSpecCount, proteinHit.getSpectralCount());
						max_emPAI = Math.max(max_emPAI, proteinHit.getEmPAI());
						min_emPAI = Math.min(min_emPAI, proteinHit.getEmPAI());
						maxNSAF = Math.max(maxNSAF, nsaf);
		
						// Wrap protein data in table node clones and insert them into the relevant trees
						URI uri = URI.create("http://www.uniprot.org/uniprot/" + proteinHit.getAccession().trim());
						
						for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
							if (ptt == ProteinTreeTables.META) {
								continue;
							}
							PhylogenyTreeTableNode node = new PhylogenyTreeTableNode(proteinHit);
							node.setURI(uri);
							ptt.insertNode(node);
						}
		
						// // Link nodes to each other
						// linkNodes(flatPath, taxonPath, enzymePath);
						
						PhylogenyTreeTableNode metaChildNode = new PhylogenyTreeTableNode(proteinHit);
						metaChildNode.setURI(uri);
						metaNode.add(metaChildNode);
						
						if (proteinHit.getUniProtEntry() == null) {
							if (Client.isDebug()) {
								System.err.println("Missing UniProt entry: " + proteinHit.getAccession());
							}
						}
						
						protCount++;
					}
					
					ProteinTreeTables.META.insertNode(metaNode);
		
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
				 
				// Display number of proteins in title area
				protTtlPnl.setTitle("Proteins (" + protCount + ")");
				
				if (protCount > 0) {
					// Refresh tree tables by resetting root node
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						SortableTreeTableModel sttm = (SortableTreeTableModel) ptt.getTreeTable().getTreeTableModel();
						sttm.setSortKeys(sttm.getSortKeys());
						sttm.setRoot(sttm.getRoot());
					}
		
					refreshPathwayStrings();
		
					// Adjust highlighters
					for (ProteinTreeTables ptt : ProteinTreeTables.values()) {
						final CheckBoxTreeTable treeTbl = ptt.getTreeTable();
						
						// let table method determine baseline automatically, provide ranges
						treeTbl.updateHighlighters(ProteinTreeTables.SEQUENCE_COVERAGE_COLUMN, 0, maxCoverage);
						treeTbl.updateHighlighters(ProteinTreeTables.PEPTIDE_COUNT_COLUMN, 0, maxPeptideCount);
						treeTbl.updateHighlighters(ProteinTreeTables.SPECTRAL_COUNT_COLUMN, 0, maxSpecCount);
						treeTbl.updateHighlighters(ProteinTreeTables.EMPAI_COLUMN, min_emPAI, max_emPAI);
						treeTbl.updateHighlighters(ProteinTreeTables.NSAF_COLUMN, 0, maxNSAF);
						
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								for (int i = ProteinTreeTables.SEQUENCE_COVERAGE_COLUMN;
										i < ProteinTreeTables.WEB_RESOURCES_COLUMN; i++) {
									int index = treeTbl.convertColumnIndexToView(i);
									if (index != -1) {
										((TableColumnExt2) treeTbl.getColumn(index)).aggregate();
									}
								}
							}
						});
						
//						treeTbl.expandAll();
					}
				}
				Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES FINISHED");
			}
		
		}

	

		@Override
		protected void done() {
			// Stop appearing busy
			DbSearchResultPanel.this.setBusy(false);
			
			// Set up graph database contents
			ResultsPanel resPnl =
					(ResultsPanel) DbSearchResultPanel.this.getParent().getParent();
			resPnl.setProcessingEnabled(true);
			
			resPnl.getGraphDatabaseResultPanel().buildGraphDatabase();
		}
		
	}
	
	private void refreshPathwayStrings() {
		// Iterate pathway nodes in respective hierarchical view and update URIs
		Enumeration childrenA = ((TreeNode) ProteinTreeTables.PATHWAY
				.getTreeTable().getTreeTableModel().getRoot()).children();
		while (childrenA.hasMoreElements()) {
			// Level A: pathway supergroups
			TreeNode childA = (TreeNode) childrenA.nextElement();
			Enumeration childrenB = childA.children();
			while (childrenB.hasMoreElements()) {
				// Level B: pathway groups
				TreeNode childB = (TreeNode) childrenB.nextElement();
				Enumeration childrenC = childB.children();
				while (childrenC.hasMoreElements()) {
					// Level C: pathways
					PhylogenyTreeTableNode childC = (PhylogenyTreeTableNode) childrenC.nextElement();
					StringBuilder sb = new StringBuilder("http://www.kegg.jp/kegg-bin/show_pathway?map");
					// Append pathway ID to URL
					sb.append(childC.getUserObject());
					// Gather K and EC numbers
					// TODO: maybe gather only numbers that are in some way connected to the pathway in question
					Set<String> kNumbers = new HashSet<String>();
					Set<String> ecNumbers = new HashSet<String>();
					Enumeration<? extends MutableTreeTableNode> children = childC.children();
					while (children.hasMoreElements()) {
						PhylogenyTreeTableNode protNode = (PhylogenyTreeTableNode) children.nextElement();
						ProteinHit protHit = (ProteinHit) protNode.getUserObject();
						if(((Hit) protNode.getUserObject()).isSelected()) {
							kNumbers.addAll(protHit.getUniProtEntry().getKNumbers());
							ecNumbers.addAll(protHit.getUniProtEntry().getEcNumbers());
						}
					}
					// Append K and EC numbers to URL
					for (String kNumber : kNumbers) {
						sb.append("+");
						sb.append(kNumber);
					}
					for (String ecNumber : ecNumbers) {
						sb.append("+");
						sb.append(ecNumber);
					}
					childC.setURI(URI.create(sb.toString()));
				}
			}
		}
	}
	
	/**
	 * Convenience filter implementation based on checkbox tree selection.
	 * @author A. Behne
	 */
	private class SelectionRowFilter extends RowFilter<Object, Object> {
		private CheckBoxTreeTable treeTable;
		public SelectionRowFilter(CheckBoxTreeTable treeTable) {
			this.treeTable = treeTable;
		}
		@Override
		public boolean include(
				RowFilter.Entry<? extends Object, ? extends Object> entry) {
			PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) entry.getValue(-1);
			TreePath path = node.getPath();
			CheckBoxTreeSelectionModel cbtsm = treeTable.getCheckBoxTreeSelectionModel();
			boolean selected = cbtsm.isPathSelected(path, true)
					|| cbtsm.isPartiallySelected(path);
			return selected;
		}
	}

	/**
	 * Custom highlighter predicate for highlighting specific proteins.
	 * @author F. Kohrs, A. Behne
	 */
	private class ProteinHighlightPredicate implements HighlightPredicate {
	
		/**
		 * The protein hits to highlight.
		 */
		private Collection<ProteinHit> protHits;
		
		/**
		 * Constructs a protein highlight predicate.
		 */
		public ProteinHighlightPredicate() {
			this(null);
		}
		
		/**
		 * Constructs a protein highlight predicate from the specified
		 * collection of protein hits to highlight.
		 * @param protHits the protein hits
		 */
		public ProteinHighlightPredicate(Collection<ProteinHit> protHits) {
			this.protHits = protHits;
		}
	
		@Override
		public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
			Object accession = adapter.getValue(adapter.getColumnIndex("Accession"));
			if (protHits != null) {
				for (ProteinHit protHit : protHits) {
					if (protHit.getAccession().equals(accession)) {
						return true;
					}
				}
			}
			return false;
		}
	
		/**
		 * Sets the protein hits to highlight.
		 * @param protHits the protein hits
		 */
		public void setProteinHits(Collection<ProteinHit> protHits) {
			this.protHits = protHits;
		}
		
	}

	@Override
	public boolean isBusy() {
		// we don't need this (yet)
		return false;
	}

	@Override
	public void setBusy(boolean busy) {
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		ClientFrame.getInstance().setCursor(cursor);
		split.setCursor(cursor);
		
		ButtonTabbedPane tabPane = (ButtonTabbedPane) this.getParent();
		int index = tabPane.indexOfComponent(this);
		tabPane.setBusyAt(index, busy);
		tabPane.setEnabledAt(index, !busy);
	}

}