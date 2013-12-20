package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXMultiSplitPane.DividerPainter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.renderer.TreeCellContext;
import org.jdesktop.swingx.sort.SortUtils;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import scala.actors.threadpool.Arrays;

import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.binding.adapter.RadioButtonAdapter;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.Interval;
import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.KeggAccessor;
import de.mpa.analysis.Masses;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.AggregateFunction;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ButtonColumn;
import de.mpa.client.ui.ButtonTabbedPane;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.ComponentTableHeader;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.PhylogenyTreeTableNode;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTable.TableColumnExt2;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.FormatHighlighter;
import de.mpa.client.ui.TreeTableRowSorter;
import de.mpa.client.ui.TriStateCheckBox;
import de.mpa.client.ui.WrapLayout;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.HistogramChart.HistogramChartType;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.ScrollableChartPane;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.dialogs.FilterBalloonTip;
import de.mpa.client.ui.dialogs.TaxonomySelectionDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.SearchHit;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;
import de.mpa.main.Parameters;
import de.mpa.parser.ec.ECEntry;
import de.mpa.taxonomy.Taxonomic;
import de.mpa.taxonomy.TaxonomyNode;
import de.mpa.util.ColorUtils;

/**
 * Panel implementation for viewing of database search results.
 * 
 * @author A. Behne, T. Muth, R. Heyer, F. Kohrs
 */
public class DbSearchResultPanel extends JPanel implements Busyable {

	/**
	 * The client frame instance.
	 */
	private ClientFrame clientFrame;
	
	/**
	 * The database search result object.
	 */
	private DbSearchResult dbSearchResult;
	
	/**
	 * Protein table.
	 */
	private JXTable proteinTbl;

	/**
	 * Peptide table.
	 */
	private JXTable peptideTbl;

	/**
	 * Protein sequence coverage panel.
	 */
	private JPanel coveragePnl;

	/**
	 * PSM table.
	 */
	private JXTable psmTbl;

	/**
	 * Spectrum panel.
	 */
	private SpectrumPanel specPnl;

	/**
	 * The maximum x value to which the spectrum view shall be scaled.
	 */
	private double maxX;

	/**
	 * Multi-split pane containing all sub-panels.
	 */
	private JXMultiSplitPane split;

	/**
	 * Panel containing buttons to select which annotations to display in the
	 * spectrum viewer.
	 */
	private JPanel specFiltPnl;

	/**
	 * The currently displayable spectrum annotations.
	 */
	private Vector<SpectrumAnnotation> currentAnnotations;

	/**
	 * The font to be used in table highlighters.
	 */
	private Font chartFont;

	/*
	 * Titled panels for protein, peptide and PSM views
	 */
	private JXTitledPanel protTtlPnl;
	private JXTitledPanel pepTtlPnl;
	private JXTitledPanel psmTtlPnl;

	/**
	 * Tree table displaying proteins in a flat non-hierarchical view.
	 */
	private SortableCheckBoxTreeTable protFlatTreeTbl;

	/**
	 * Tree table displaying proteins hierarchically by their taxonomy.
	 */
	private SortableCheckBoxTreeTable protTaxonTreeTbl;

	/**
	 * Tree table displaying proteins hierarchically by their Enzyme Commission number.
	 */
	private SortableCheckBoxTreeTable protEnzymeTreeTbl;

	/**
	 * Tree table displaying proteins hierarchically by their KEGG pathway.
	 */
	private SortableCheckBoxTreeTable protPathwayTreeTbl;

	/**
	 * Map linking root node names to their respective tree table
	 */
	private Map<String, CheckBoxTreeTable> linkMap = new LinkedHashMap<String, CheckBoxTreeTable>();

	/**
	 * Highlighter predicate.
	 */
	private ProteinCountHighlightPredicate protCHighlightPredicate;

	/**
	 * Import file.
	 */
	protected File importFile;

	private CardLayout protCardLyt;

	private String[] cardLabels;

	private JPanel protCardPnl;

//	/**
//	 * The taxonomy model for the taxonomy filtering tree.
//	 */
//	private DefaultTreeModel taxModel;
//
//	/**
//	 * Taxonomy ID for the filter, standard is 1 for root.
//	 */
//	private long filterTaxId = 1;

	/**
	 * Constructor for a database results panel.
	 * @param clientFrame The parent frame.
	 */
	public DbSearchResultPanel() {
		this.clientFrame = ClientFrame.getInstance();
		initComponents();
	}

	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		// Define layout
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		// Init titled panel variables.
		Font ttlFont = PanelConfig.getTitleFont();
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();

		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		// Setup tables
		chartFont = UIManager.getFont("Label.font");	// TODO: @AB maybe simple getFont() calls suffice, no need for caching the label font
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupCoverageViewer();
		setupPsmTableProperties();

		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 150));
		JScrollPane peptideTableScpn = new JScrollPane(peptideTbl);
		peptideTableScpn.setPreferredSize(new Dimension(350, 100));
		JScrollPane coverageScpn = new JScrollPane(coveragePnl);
		coverageScpn.setPreferredSize(new Dimension(350, 100));
		coverageScpn.getVerticalScrollBar().setUnitIncrement(16);
		coverageScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane psmTableScp = new JScrollPane(psmTbl);
		psmTableScp.setPreferredSize(new Dimension(350, 100));

		// Hierarchical view options
		cardLabels = new String[] {"Basic View", "Meta-Protein View", "Taxonomy View", "Enzyme View", "Pathway View"};

		protCardLyt = new CardLayout();
		protCardPnl = new JPanel(protCardLyt);
		proteinTableScp.setName(cardLabels[0]);
		protCardPnl.add(proteinTableScp, cardLabels[0]);

		setupProteinTreeTables();
		int i = 1;
		for (CheckBoxTreeTable cbtt : linkMap.values()) {
			JScrollPane scrollPane = new JScrollPane(cbtt);
			scrollPane.setPreferredSize(new Dimension(800, 150));
			scrollPane.setName(cardLabels[i]);
			protCardPnl.add(scrollPane, cardLabels[i++]);
		}

		proteinPnl.add(protCardPnl, CC.xy(2, 2));

		// Button for taxonomic filtering	
		JButton taxFilterBtn = new JButton(IconConstants.FILTER_ICON);
		taxFilterBtn.setRolloverIcon(IconConstants.FILTER_ROLLOVER_ICON);
		taxFilterBtn.setPressedIcon(IconConstants.FILTER_PRESSED_ICON);
		taxFilterBtn.setToolTipText("Select Taxonomic Filtering");
		
		taxFilterBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(taxFilterBtn));
		
		taxFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TaxonomySelectionDialog taxDlg = TaxonomySelectionDialog.getInstance();
				taxDlg.setVisible(true);
				
				TreePath[] selPaths = taxDlg.getSelectionPaths();
				final CheckBoxTreeSelectionModel cbtsm = new CheckBoxTreeSelectionModel(null);
				cbtsm.setSelectionPaths(selPaths);
				
				RowFilter<Object, Object> taxFilter = new RowFilter<Object, Object>() {
					@Override
					public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
						boolean res = true;
						// extract tree table node from row entry
						PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) entry.getValue(-1);
						// extract taxonomy node from tree table node
						TaxonomyNode taxNode = null;
						if (node.isProtein()) {
							taxNode = ((Taxonomic) node.getUserObject()).getTaxonomyNode();
						} else if (node.isTaxonomy()) {
							taxNode = (TaxonomyNode) node.getUserObject();
						}
						if (taxNode != null) {
							// since we hijacked the taxonomic tree view we need to prepend its root user object
							Object[] path = Constants.concat(new Object[] { "Root of Taxonomic View" }, taxNode.getPath());
							TreePath taxPath = new TreePath(path);
							// return whether the constructed path is (partially) selected
							res = cbtsm.isPathSelected(taxPath, true) || cbtsm.isPartiallySelected(taxPath);
						}
						if (!res) {
							((ProteinHit) node.getUserObject()).setSelected(false);
						}
						return res;
					}
				};
				// TODO: implement taxonomy filtering for 'classic' view
				protFlatTreeTbl.setRowFilter(taxFilter);
				protTaxonTreeTbl.setRowFilter(taxFilter);
				protEnzymeTreeTbl.setRowFilter(taxFilter);
				protPathwayTreeTbl.setRowFilter(taxFilter);
				
				synchSelection();
				updateChartData();
			}
		});
		
		final JToggleButton hierarchyBtn = new JToggleButton(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ICON));
		hierarchyBtn.setRolloverIcon(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ROLLOVER_ICON));
		hierarchyBtn.setPressedIcon(
				IconConstants.createArrowedIcon(IconConstants.HIERARCHY_PRESSED_ICON));
		hierarchyBtn.setToolTipText("Select Hierarchical View");
		
		hierarchyBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(taxFilterBtn));
		
//		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
//		hierarchyBtn.addMouseListener(ittml);

		final JPopupMenu hierarchyPop = new JPopupMenu();
		ActionListener hierarchyListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				protCardLyt.show(protCardPnl, ((AbstractButton) e.getSource()).getText());
			}
		};
		ButtonGroup bg = new ButtonGroup();
		for (int j = 0; j < cardLabels.length; j++) {
			JMenuItem item = new JRadioButtonMenuItem(cardLabels[j], (j == 0));
			item.addActionListener(hierarchyListener);
			bg.add(item);
			hierarchyPop.add(item);
		}
		hierarchyPop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				hierarchyBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});

		hierarchyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hierarchyPop.show(hierarchyBtn, 0, hierarchyBtn.getHeight());
			}
		});

		JPanel protBtnPnl = new JPanel(new FormLayout(
//				"p, 2dlu, 36px, 2dlu, 36px, c:5dlu, p, 2dlu, p, 1px",
				"22px, 2dlu, 36px, 1px",
				"20px"));
		protBtnPnl.setOpaque(false);
		
		protBtnPnl.add(taxFilterBtn, CC.xy(1, 1));
		protBtnPnl.add(hierarchyBtn, CC.xy(3, 1));

		protTtlPnl = new JXTitledPanel("Proteins", proteinPnl);
		protTtlPnl.setRightDecoration(protBtnPnl);
		protTtlPnl.setTitleFont(ttlFont);
		protTtlPnl.setTitlePainter(ttlPainter);
		protTtlPnl.setBorder(ttlBorder);

		// Peptide panel
		final CardLayout pepCl = new CardLayout();
		final JPanel peptidePnl = new JPanel(pepCl);

		final JPanel pepTblScpnPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		pepTblScpnPnl.add(peptideTableScpn, CC.xy(2, 2));
		final JPanel cvgTblScpnPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		cvgTblScpnPnl.add(coverageScpn, CC.xy(2, 2));

		// Add cards
		peptidePnl.add(pepTblScpnPnl, "Table");
		peptidePnl.add(cvgTblScpnPnl, "Coverage");

		// build control button panel for card layout
		JButton nextBtn = new JButton("\u203A");
		nextBtn.setPreferredSize(new Dimension(19, 18));

		// Wrap peptide panel in titled panel with control buttons in title
		pepTtlPnl = new JXTitledPanel("Peptides", peptidePnl);
		pepTtlPnl.setTitleFont(ttlFont);
		pepTtlPnl.setTitlePainter(ttlPainter);
		pepTtlPnl.setBorder(ttlBorder);
		pepTtlPnl.setRightDecoration(nextBtn);

		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pepCl.previous(peptidePnl);
				Component[] components = peptidePnl.getComponents();
				for (Component component : components) {
					if (component.isVisible()) {
						if (component == pepTblScpnPnl) {
							pepTtlPnl.setTitle("Peptides");
						} else {
							pepTtlPnl.setTitle("Sequence Coverage Viewer");
						}
					}
				}
			}
		});

		// PSM panel
		final JPanel psmPanel = new JPanel();
		psmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		psmPanel.add(psmTableScp, CC.xy(2, 2));

		psmTtlPnl = new JXTitledPanel("Peptide-Spectrum-Matches", psmPanel);
		psmTtlPnl.setTitleFont(ttlFont);
		psmTtlPnl.setTitlePainter(ttlPainter);
		psmTtlPnl.setBorder(ttlBorder);

		// Build the spectrum panel containing annotation filter buttons
		JPanel spectrumPnl = new JPanel(new BorderLayout());

		JPanel specCont = new JPanel();
		specCont.setLayout(new BoxLayout(specCont, BoxLayout.LINE_AXIS));
		specCont.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
		specCont.add(specPnl = FilePanel.createDefaultSpectrumPanel());
		specCont.setMinimumSize(new Dimension(200, 200));

		spectrumPnl.add(specCont, BorderLayout.CENTER);
		specFiltPnl = this.constructSpectrumFilterPanel();
		spectrumPnl.add(specFiltPnl, BorderLayout.EAST);
		
		// Build the chart panel containing pie/bar charts
		JPanel chartPnl = this.createChartPanel();
		
		// Add chart/spectrum panels to card layout
		final CardLayout chartCl = new CardLayout();
		final JPanel chartCardPnl = new JPanel(chartCl);

		// Add cards
		chartCardPnl.add(spectrumPnl, "Spectrum");
		chartCardPnl.add(chartPnl, "Charts");

		// Wrap chart/spectrum panels in titled panel
		final JXTitledPanel chartTtlPnl = new JXTitledPanel("Spectrum Viewer", chartCardPnl); 
		chartTtlPnl.setTitleFont(ttlFont);
		chartTtlPnl.setTitlePainter(ttlPainter);
		chartTtlPnl.setBorder(ttlBorder);
		
		// Create button panel for chart/spectrum titled panel
		JPanel specBtnPnl = new JPanel(new FormLayout("21px, 1px, 11px, 2dlu, 22px, 1px", "20px"));
		specBtnPnl.setOpaque(false);
		
		ButtonGroup specBg = new ButtonGroup();

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

		specBg.add(chartTgl);
		specBg.add(specTgl);

		specBtnPnl.add(chartTgl, CC.xyw(1, 1, 2));
		specBtnPnl.add(chartTypeTgl, CC.xyw(2, 1, 2));
		specBtnPnl.add(specTgl, CC.xy(5, 1));
		
		chartTtlPnl.setRightDecoration(specBtnPnl);

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
		//		split.add(specTtlPnl, "plot");
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
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		
	// Create protein table model
	final TableModel proteinTblMdl = new DefaultTableModel() {
		// instance initializer block
		{
			setColumnIdentifiers(new Object[] { "", "#", "Accession",
					"Description", "Taxonomy", "SC", "MW", "pI", "PepC",
					"SpC", "emPAI", "NSAF", ""});
		}

		public boolean isCellEditable(int row, int col) {
			return (col == Constants.PROT_SELECTION) || (col == Constants.PROT_WEBRESOURCE);
		}

		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case Constants.PROT_SELECTION: 
				return Boolean.class;
			case Constants.PROT_INDEX:	
			case Constants.PROT_PEPTIDECOUNT:
			case Constants.PROT_SPECTRALCOUNT:
				return Integer.class;
			case Constants.PROT_COVERAGE:
			case Constants.PROT_MW:
			case Constants.PROT_PI:
			case Constants.PROT_EMPAI:
			case Constants.PROT_NSAF:
				return Double.class;
			default:
				return String.class;
			}
		}
	};

	// Init column selection checkbox header widget
	final JCheckBox selChk = new TriStateCheckBox() {
		@Override
		public boolean isPartiallySelected() {
			// selected state needs to be false to be partially selected
			if (!this.isSelected()) {
				// check all values in selection column, if at least one was found we have a partial selection
				for (int i = 0; i < proteinTblMdl.getRowCount(); i++) {
					if ((Boolean) proteinTblMdl.getValueAt(i, Constants.PROT_SELECTION)) {
						return true;
					}
					// typically this loop terminates after the first element due to the table being sorted
				}
			}
			return false;
		}
	};
	selChk.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
	selChk.setSelected(true);
	selChk.setOpaque(false);
	selChk.setBackground(new Color(0, 0, 0, 0));
	
	this.proteinTbl = new JXTable(proteinTblMdl) {
		public Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
		@Override
		public Component prepareRenderer(TableCellRenderer renderer,
				int row, int column) {
			Component comp = super.prepareRenderer(renderer, row, column);
			if (comp instanceof JComponent) {
				((JComponent) comp).setBorder(padding);
			}
			return comp;
		}
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			super.setValueAt(aValue, row, column);
			if (column == Constants.PROT_SELECTION) {
				boolean selected = (Boolean) aValue;
				Map<String, ProteinHit> proteinHits = dbSearchResult.getProteinHits();
				String accession = (String) getValueAt(row, Constants.PROT_ACCESSION);
				ProteinHit hit = proteinHits.get(accession);
				hit.setSelected(selected);
				if (selected) {
					for (ProteinHit ph : dbSearchResult.getProteinHitList()) {
						selected &= ph.isSelected();
						if (!selected) break;
					}
				}
				selChk.setSelected(selected);
				getTableHeader().repaint(getTableHeader().getHeaderRect(Constants.PROT_SELECTION));
			}
		}
	};

	// Adjust column widths
	TableConfig.setColumnWidths(proteinTbl, new double[] { 0, 2.5, 5.5, 15, 14, 5, 4, 3, 4, 4, 4.5, 5, 1 });
	TableConfig.setColumnMinWidths(proteinTbl, UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(),
			createFilterButton(0, null, 0).getPreferredSize().width + 8, new JLabel().getFont());

	// Get table column model
	final TableColumnModelExt tcm = (TableColumnModelExt) proteinTbl.getColumnModel();

	// Apply component header capabilities as well as column header tooltips
	String[] columnToolTips = {
			"Selection for Export",
			"Result Index",
			"Protein Accession",				
			"Protein Description",
			"Taxonomy",
			"Sequence Coverage in %",
			"Molecular Weight in kDa",
			"Isoelectric Point",
			"Peptide Count",
			"Spectral Count",
			"Exponentially Modified Protein Abundance Index",
			"Normalized Spectral Abundance Factor", 
			"External Web Resource Links"};
	ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
	//		ch.setReorderingAllowed(false, Constants.Constants.PROT_SELECTION);
	proteinTbl.setTableHeader(ch);

	// Add filter button and checkbox widgets to column headers
	for (int col = Constants.PROT_SELECTION; col < tcm.getColumnCount(); col++) {
		switch (col) {
		case Constants.PROT_SELECTION:
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null, SwingConstants.TRAILING));
			tcm.getColumn(col).setMinWidth(19);
			tcm.getColumn(col).setMaxWidth(19);
			break;
		case Constants.PROT_INDEX:
		case Constants.PROT_COVERAGE:
		case Constants.PROT_MW:
		case Constants.PROT_PI:
		case Constants.PROT_PEPTIDECOUNT:
		case Constants.PROT_SPECTRALCOUNT:
		case Constants.PROT_EMPAI:
		case Constants.PROT_NSAF:
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.NUMERIC)) {
				protected SortKey getSortKey(JTable table, int column) {
					RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
					if (rowSorter != null) {
						return rowSorter.getSortKeys().get(1);
					} else {
						return super.getSortKey(table, column);
					}
				}
			});
			break;
		case Constants.PROT_ACCESSION: 
		case Constants.PROT_DESCRIPTION:
		case Constants.PROT_TAXONOMY: 
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.ALPHANUMERIC)) {
				protected SortKey getSortKey(JTable table, int column) {
					RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
					if (rowSorter != null) {
						return rowSorter.getSortKeys().get(1);
					} else {
						return super.getSortKey(table, column);
					}
				}
			});
			break;

		case Constants.PROT_WEBRESOURCE:
			tcm.getColumn(col).setMinWidth(19);
			tcm.getColumn(col).setMaxWidth(19);
		}
	}

	// Apply custom cell renderers/highlighters to columns
	tcm.getColumnExt(Constants.PROT_INDEX).addHighlighter(new FormatHighlighter(SwingConstants.RIGHT));

	// Hyperlink renderer for Accession column
	final AbstractHyperlinkAction<String> linkAction = new AbstractHyperlinkAction<String>() {
		public void actionPerformed(ActionEvent ev) {
			try {
				if (target != null) {
					Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
				}
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
	};		
	HyperlinkProvider hlp = new HyperlinkProvider(linkAction) {
		@Override
		protected JXHyperlink createRendererComponent() {
			JXHyperlink comp = super.createRendererComponent();
			comp.setHorizontalAlignment(SwingConstants.CENTER);
			return comp;
		}
		@Override
		protected void format(CellContext context) {
			super.format(context);
			Object value = context.getValue();
			String target = value.toString();
			if (target.matches("^\\d*$")) {
				// target string contains only numeric characters -> probably GI number
				ReducedUniProtEntry uniprotEntry = dbSearchResult.getProteinHit(target).getUniProtEntry();
				// change target to UniProt accession inside associated UniProtEntry
				if (uniprotEntry != null) {
					target = dbSearchResult.getProteinHit(target).getAccession();
				} else {
					target = null;
				}
			} else {
				// target string contains non-numeric characters -> probably UniProt accession
				// do nothing
			}
			linkAction.setTarget(target);
			rendererComponent.setText(value.toString());
		}
	};
	
	tcm.getColumn(Constants.PROT_ACCESSION).setCellRenderer(new DefaultTableRenderer(hlp) {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JXRendererHyperlink hyperlink = (JXRendererHyperlink) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// when no target is specified replace hyperlink with a simple label
			if (linkAction.getTarget() == null) {
				JLabel label = new JLabel(hyperlink.getText(), SwingConstants.CENTER);
				label.setOpaque(true);
				label.setBackground(hyperlink.getBackground());
				return label;
			} else {
				hyperlink.setOpaque(true);
			}
			return hyperlink;
		}
	});
	
	// Web resource action
	Action webResourceAction = new AbstractAction() {			
		@Override
		public void actionPerformed(ActionEvent evt) {
			setupWebresourceMenu(proteinTbl);
		}
	};
	new ButtonColumn(proteinTbl, webResourceAction, Constants.PROT_WEBRESOURCE);

	tcm.getColumnExt(Constants.PROT_DESCRIPTION).addHighlighter(new FormatHighlighter(SwingConstants.LEFT));
	tcm.getColumnExt(Constants.PROT_TAXONOMY).addHighlighter(new FormatHighlighter(SwingConstants.LEFT));
	DecimalFormat x100formatter = new DecimalFormat("0.00");
	x100formatter.setMultiplier(100);
	tcm.getColumnExt(Constants.PROT_COVERAGE).addHighlighter(new BarChartHighlighter(
			0.0, 100.0, 50, SwingConstants.HORIZONTAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN, x100formatter));
//	tcm.getColumnExt(Constants.PROT_MW).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "0.000"));
//	tcm.getColumnExt(Constants.PROT_PI).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "0.00"));
	tcm.getColumnExt(Constants.PROT_PEPTIDECOUNT).addHighlighter(new BarChartHighlighter());
	tcm.getColumnExt(Constants.PROT_SPECTRALCOUNT).addHighlighter(new BarChartHighlighter());
	tcm.getColumnExt(Constants.PROT_EMPAI).addHighlighter(new BarChartHighlighter(
			ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00")));
	tcm.getColumnExt(Constants.PROT_NSAF).addHighlighter(new BarChartHighlighter(
			ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00000")));

	// Make table always sort primarily by selection state of selection column
	final SortKey selKey = new SortKey(Constants.PROT_SELECTION, SortOrder.DESCENDING);

	final TableSortController<TableModel> tsc = new TableSortController<TableModel>(proteinTblMdl) {
		@Override
		public void toggleSortOrder(int column) {
			List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
			SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
			if (keys.indexOf(sortKey) == 1)  {
				// primary key: cycle sort order
				SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
						SortOrder.DESCENDING : SortOrder.ASCENDING;
				keys.set(1, new SortKey(column, so));
			} else {
				// all others: insert new 
				keys.remove(sortKey);
				keys.add(1, new SortKey(column, SortOrder.DESCENDING));
			}
			if (keys.size() > getMaxSortKeys()) {
				keys = keys.subList(0, getMaxSortKeys());
			}
			setSortKeys(keys);
		}
		@Override
		public void setSortKeys(
				List<? extends SortKey> sortKeys) {
			List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
			// make sure selection column sorting always occurs before other columns
			newKeys.remove(selKey);
			newKeys.add(0, selKey);
			super.setSortKeys(newKeys);
		}
	};
	proteinTbl.setRowSorter(tsc);

	// register action listener on selection column header checkbox
	selChk.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean selected = selChk.isSelected();
			// prevent auto-resorting while iterating rows for performance reasons
			tsc.setSortsOnUpdates(false);
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				proteinTbl.setValueAt(selected, row, Constants.PROT_SELECTION);
			}
			tsc.setSortsOnUpdates(true);
			// re-sort rows after iteration finished
			tsc.sort();
		}
	});

	// Specify initial sort order
	List<SortKey> sortKeys = new ArrayList<SortKey>(2);
	sortKeys.add(0, new SortKey(Constants.PROT_SELECTION, SortOrder.DESCENDING));
	sortKeys.add(1, new SortKey(Constants.PROT_SPECTRALCOUNT, SortOrder.DESCENDING));
	tsc.setSortKeys(sortKeys);

	// Prevent messing with sort order of selection column
	tsc.setSortable(Constants.PROT_SELECTION, false);

	// Register list selection listener
	proteinTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent evt) {
			int protRow = proteinTbl.getSelectedRow();
			refreshPeptideViews(protRow);
		}
	});

	// Only one row is selectable
	proteinTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	// Add nice striping effect
	proteinTbl.addHighlighter(TableConfig.getSimpleStriping());

	protCHighlightPredicate = new ProteinCountHighlightPredicate();
	Color selCol = UIManager.getColor("Table.selectionBackground");
	selCol = new Color(232, 122, 0, 41);
	Highlighter protCountHighlighter = new ColorHighlighter(protCHighlightPredicate, selCol, null);
	proteinTbl.addHighlighter(protCountHighlighter);

	// Enables column control
	TableConfig.configureColumnControl(proteinTbl);
}

//	/**
//	 * This method sets the enabled state of the get results button.
//	 */
//	public void setResultsFromDbButtonEnabled(boolean enabled) {
//		getResultsFromDbBtn.setEnabled(enabled);
//	}

	/**
	 * Creates a filter button widget and installs it in the specified column header 
	 * of the specified table.
	 * 
	 * @param column The column index.
	 * @param table The table reference.
	 * @param filterType Either <code>Constants.NUMERICAL</code> or 
	 * <code>Constants.ALPHA_NUMERICAL</code>.
	 * @return The filter button widget reference.
	 */
	protected JPanel createFilterButton(int column, JTable table, int filterType) {
		// button widget with little black triangle
		final FilterButton button = new FilterButton(column, table, filterType);
//		button.setPreferredSize(new Dimension(11, 11));

		// wrap button in panel to apply padding
		final JPanel panel = new JPanel(new FormLayout("0px, 11px, 3px", "2px, f:11px, 2px"));
		panel.setOpaque(false);
		panel.add(button, CC.xy(2, 2));

		// forward mouse events on panel to button
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseReleased(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseClicked(MouseEvent me) {
				forwardEvent(me);
			}
			private void forwardEvent(MouseEvent me) {
				button.dispatchEvent(SwingUtilities.convertMouseEvent(panel, me, button));
			}
		});
		return panel;
	}

	/**
	 * Filter button widget for table headers.
	 * 
	 * @author A. Behne
	 */
	private class FilterButton extends JToggleButton {

		private FilterBalloonTip filterTip;

		/**
		 * Constructs a filter toggle button widget which shows its own balloon
		 * tip pop-up containing filtering-related components for the specified
		 * <code>column</code>.
		 * 
		 * @param column
		 */
		public FilterButton(final int column, final JTable table, final int filterType) {
			super();
			this.addActionListener(new ActionListener() {

				/** The reference to the last selected filter button. */
				protected FilterButton lastSelected;
				
				@Override
				public void actionPerformed(ActionEvent evt) {
					final FilterButton button = (FilterButton) evt.getSource();
					if (button != this.lastSelected) {
						if (this.lastSelected != null) {
							this.lastSelected.setFilterTipVisible(false);
						}
						this.lastSelected = button;
					}
					if (button.isSelected()) {
						JTableHeader th = table.getTableHeader();
						String filterString = (filterTip != null) ? filterTip.getFilterString() : "";	

						Rectangle rect = th.getHeaderRect(table.convertColumnIndexToView(column));
						rect.x += rect.width - 9;
						rect.y += 1;
						filterTip = new FilterBalloonTip(th, rect, filterType);

						filterTip.addComponentListener(new ComponentAdapter() {
							public void componentHidden(ComponentEvent ce) {
								button.setSelected(false);
								table.getTableHeader().repaint();
								// TODO: maybe store and restore original focus instead of shifting it always onto the table?
								table.requestFocus();
								if (filterTip.isConfirmed()) {
									filterFlatSelections(column, filterTip.getFilterString());
								}
							};
						});
						filterTip.setFilterString(filterString);
						filterTip.requestFocus();
					} else {
						filterTip.setVisible(false);
					}
				}
			});
		}

		/**
		 * Method to remotely hide/show the filter balloon tip.
		 * @param visible
		 */
		protected void setFilterTipVisible(boolean visible) {
			if (this.filterTip != null) {
				this.filterTip.setVisible(visible);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			// paint black triangle in bottom right corner
			g.setColor(Color.BLACK);
			g.fillPolygon(new int[] { 3, 8, 8 }, new int[] { 8, 8, 3 }, 3);
		}

	}

	protected JPopupMenu setupWebresourceMenu(JXTable table) {
		JPopupMenu webresourceMenu = new JPopupMenu();		
		ActionListener popupMenuItemEventListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JMenuItem menuItem = (JMenuItem) evt.getSource();
				openTargetInBrowser(menuItem.getClientProperty("url").toString());
			}
		};
		JMenuItem uniProtMenuItem = new JMenuItem("UniProt", IconConstants.WEB_UNIPROT_ICON);
		int col = Constants.PROT_ACCESSION;
		if (table instanceof JXTreeTable) {
			col = 0;
		}
		String accession = (String) table.getValueAt(table.getSelectedRow(), table.convertColumnIndexToView(col));
		uniProtMenuItem.putClientProperty("url", "http://www.uniprot.org/uniprot/" + accession);
		uniProtMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(uniProtMenuItem);
		
		JMenuItem ncbiMenuItem = new JMenuItem("NCBI", IconConstants.WEB_NCBI_ICON);
		ncbiMenuItem.putClientProperty("url", "http://www.ncbi.nlm.nih.gov/protein/" + accession);
		ncbiMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(ncbiMenuItem);
		
		JMenuItem keggMenuItem = new JMenuItem("KEGG", IconConstants.WEB_KEGG_ICON);
		String keggURL = "";
		ProteinHit proteinHit = dbSearchResult.getProteinHit(accession);
		if (proteinHit != null) {
			ReducedUniProtEntry uniprotEntry = proteinHit.getUniProtEntry();
			if (uniprotEntry != null) {
				List<String> kNumbers = uniprotEntry.getKNumbers();
				int size = kNumbers.size();
	
				for (int i = 0; i < size; i++) {
					keggURL  = "http://www.genome.jp/dbget-bin/www_bget?ko:" + kNumbers.get(i);
					if(i < size - 1) keggURL += "+";
				}
			}
		}
		keggMenuItem.putClientProperty("url", keggURL);
		keggMenuItem.addActionListener(popupMenuItemEventListener);
		// disable KEGG item if no UniProt entry is present or it contains no K numbers
		keggMenuItem.setEnabled(!keggURL.isEmpty());
		webresourceMenu.add(keggMenuItem);
		
		JMenuItem blastMenuItem = new JMenuItem("BLAST", IconConstants.WEB_BLAST_ICON);
		String sequence = proteinHit.getSequence();
		String blastURL = "http://blast.ncbi.nlm.nih.gov/Blast.cgi?PROGRAM=blastp&BLAST_PROGRAMS=blastp&PAGE_TYPE=BlastSearch&SHOW_DEFAULTS=on&LINK_LOC=blasthome&QUERY=" + sequence;
		blastMenuItem.putClientProperty("url", blastURL);
		blastMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(blastMenuItem);
		
		JMenuItem pfamMenuItem = new JMenuItem("PFAM", IconConstants.WEB_PFAM_ICON);
		pfamMenuItem.putClientProperty("url", "http://pfam.sanger.ac.uk/protein/" + accession);
		pfamMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(pfamMenuItem);
		
		JMenuItem interproMenuItem = new JMenuItem("InterPro", IconConstants.WEB_INTERPRO_ICON);
		interproMenuItem.putClientProperty("url", "http://www.ebi.ac.uk/interpro/protein/" + accession);
		interproMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(interproMenuItem);
		
		JMenuItem pdbMenuItem = new JMenuItem("PDB", IconConstants.WEB_PDB_ICON);
		pdbMenuItem.putClientProperty("url", "http://www.rcsb.org/pdb/protein/" + accession);
		pdbMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(pdbMenuItem);
		
		JMenuItem prideMenuItem = new JMenuItem("PRIDE", IconConstants.WEB_PRIDE_ICON);
		prideMenuItem.putClientProperty("url", "http://www.ebi.ac.uk/pride/identification.do?proteinIdentifier=" + accession);
		prideMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(prideMenuItem);
		
		JMenuItem reactomeMenuItem = new JMenuItem("Reactome", IconConstants.WEB_REACTOME_ICON);
		reactomeMenuItem.putClientProperty("url", "http://www.reactome.org/cgi-bin/search2?DB=test_reactome_46&SPECIES=&OPERATOR=ALL&QUERY=" + accession);
		reactomeMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(reactomeMenuItem);
		
		JMenuItem quickGoMenuItem = new JMenuItem("QuickGO", IconConstants.WEB_QUICKGO_ICON);
		quickGoMenuItem.putClientProperty("url", "http://www.ebi.ac.uk/QuickGO/GProtein?ac=" + accession);
		quickGoMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(quickGoMenuItem);
		
		JMenuItem eggNogMenuItem = new JMenuItem("eggNOG", IconConstants.WEB_EGGNOG_ICON);
		eggNogMenuItem.putClientProperty("url", "http://eggnog.embl.de/uniprot=" + accession);
		eggNogMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(eggNogMenuItem);
		
		Rectangle cellRect = table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true);
		webresourceMenu.show(table, cellRect.x, cellRect.y + cellRect.height);
		return webresourceMenu;
	}

	/**
	 * Opens a target in a browser window.
	 * @param url Browser URL string.
	 */
	protected void openTargetInBrowser(String url) {
		try {
			if (url != null) {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Method to update the checkbox column values depending on pattern 
	 * matching in the specified column using the specified pattern string.
	 * @param column The column to check.
	 * @param filterString Comma-separated string patterns.
	 */
	protected void filterFlatSelections(int column, String filterString) {
		column = proteinTbl.convertColumnIndexToView(column);
		// prevent auto-resorting while iterating rows for performance reasons
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(false);
		// Check whether we're dealing with numerical values or with text
		if (Number.class.isAssignableFrom(proteinTbl.getColumnClass(column))) {
			//			List<Number> greater = new ArrayList<Number>();
			//			List<Number> less = new ArrayList<Number>();
			// parse filter strings
			String[] filterStrings = filterString.split(",");
			double left = Double.MIN_VALUE, right = Double.MAX_VALUE;
			for (int i = 0; i < filterStrings.length; i++) {
				String s = filterStrings[i].trim();
				if (s.startsWith(">")) {
					double val = Double.parseDouble(s.substring(1));
					if (left == Double.MIN_VALUE) {
						left = val;
					} else {
						left = Math.min(left, val);
					}
					// greater.add(val);
				} else if (s.startsWith("<")) {
					double val = Double.parseDouble(s.substring(1));
					if (right == Double.MAX_VALUE) {
						right = val;
					} else {
						right = Math.max(right, val);
					}
				}
			}
			Interval interval = new Interval(left, right);
			// iterate table rows and check intervals
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				if (!(Boolean) proteinTbl.getValueAt(row, Constants.PROT_SELECTION)) {
					break;
				}
				proteinTbl.setValueAt(	interval.containsExclusive(((Number) proteinTbl.getValueAt(row, column)).doubleValue()),
						row, proteinTbl.convertColumnIndexToView(Constants.PROT_SELECTION));
			}
		} else {
			List<String> restrictive = new ArrayList<String>();
			List<String> permissive = new ArrayList<String>();
			// parse filter strings
			String[] filterStrings = filterString.split(",");
			for (int i = 0; i < filterStrings.length; i++) {
				String s = filterStrings[i].trim();
				if (s.startsWith("-")) {
					restrictive.add(s.substring(1));
				} else {
					permissive.add(s);
				}
			}
			// iterate table rows and check filter patterns
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				if (!(Boolean) proteinTbl.getValueAt(row, Constants.PROT_SELECTION)) {
					break;
				}
				String value = proteinTbl.getValueAt(row, column).toString();
				boolean selected;
				// check includes
				if (!permissive.isEmpty()) {
					selected = false;
					for (String s : permissive) {
						selected |= value.contains(s);
					}
				} else {
					// check excludes
					selected = true;
					for (String s : restrictive) {
						selected &= !value.contains(s);
						if (!selected) break;
					}
				}
				proteinTbl.setValueAt(selected, row, proteinTbl.convertColumnIndexToView(Constants.PROT_SELECTION));
			}
		}
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(true);
		// re-sort rows after iteration finished
		((TableSortController) proteinTbl.getRowSorter()).sort();
	}

	/**
	 * Custom highlighter predicate for protein count visualization.
	 * @author kohrs
	 *
	 */
	private class ProteinCountHighlightPredicate implements HighlightPredicate {
	
		/**
		 * The list of proteins to be highlighted.
		 */
		private List<ProteinHit> protHits = new ArrayList<ProteinHit>();
	
		@Override
		public boolean isHighlighted(Component renderer,
				org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
			for (ProteinHit protHit : getProteinHits()) {
				if (protHit.getAccession().equals(adapter.getValue(Constants.PROT_ACCESSION))) {
					return true;
				}
			}
			return false;
		}
	
		/**
		 * Returns the list of protein hits.
		 * @return the list of protein hits.
		 */
		public List<ProteinHit> getProteinHits() {
			return protHits;
		}
	
		/**
		 * Sets the list of protein hits.
		 * @param protHits the list of protein hits to set.
		 */
		public void setProteinHits(List<ProteinHit> protHits) {
			this.protHits = protHits;
		}
	}

	/**
	 * Initializes all hierarchical protein tree table views
	 */
	private void setupProteinTreeTables() {
		protFlatTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Flat View"));
		protTaxonTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Taxonomic View"));
		TableConfig.setColumnWidths(protTaxonTreeTbl, new double[] { 20.25, 22, 0, 4, 5, 4, 3, 4, 4, 4.5, 5, 0 });
		protTaxonTreeTbl.getColumnExt("Taxonomy").setVisible(false);
		protEnzymeTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Enzyme View"));
		protPathwayTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Pathway View"));

		linkNodes(new TreePath(protFlatTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protTaxonTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protEnzymeTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protPathwayTreeTbl.getTreeTableModel().getRoot()));
	}

	/**
	 * Flag indicating whether checkbox selections inside tree tables are currently 
	 * in the middle of being synched programmatically.
	 */
	private boolean synching = false;

	/**
	 * Creates and returns a protein tree table anchored to the specified root node.
	 * @param root the root node of the tree table
	 * @return he generated tree table
	 */
	protected SortableCheckBoxTreeTable createTreeTable(final SortableCheckBoxTreeTableNode root) {

		final SortableCheckBoxTreeTable treeTbl;
		
		// Set up table model
		SortableTreeTableModel treeTblMdl = new SortableTreeTableModel(root) {
			// Install column names
			{
				setColumnIdentifiers(Arrays.asList(new String[] {
						"Accession", "Description", "Taxonomy", "AId", "SC",
						"MW", "pI", "PepC", "SpC", "emPAI", "NSAF", " " }));
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

		// Create table from model, redirect tooltip text generation
		treeTbl = new SortableCheckBoxTreeTable(treeTblMdl) {
			@Override
			public String getToolTipText(MouseEvent me) {
				return DbSearchResultPanel.this.getTableToolTipText(me);
			}
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
							FontMetrics fm = this.getFontMetrics(DbSearchResultPanel.this.chartFont);
							NumberFormat formatter = bchl.getFormatter();
							// iterate all nodes to get elements in desired column
//							// TODO: this is probably slow for large tables, maybe perform only once on largest value or root aggregate value or cached set of values
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
		};
		
		// Install component header
		TableColumnModel tcm = treeTbl.getColumnModel();
		String[] columnToolTips = {
				"Protein Accession",
				"Protein Description",
				"Taxonomy",
				"Alignment Identity",
				"Sequence Coverage in %",
				"Molecular Weight in kDa",
				"Isoelectric Point",
				"Peptide Count",
				"Spectral Count",
				"Exponentially Modified Protein Abundance Index",
				"Normalized Spectral Abundance Factor",
				"External Web Resources"
		};
		final ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		treeTbl.setTableHeader(ch);

		// Install mouse listeners in header for right-click popup capabilities
		MouseAdapter ma = this.createHeaderMouseAdapter(treeTbl);
		ch.addMouseListener(ma);
		ch.addMouseMotionListener(ma);
		
		// Force column factory to generate columns to properly cache widths
		((AbstractTableModel) treeTbl.getModel()).fireTableStructureChanged();
		
		// Initialize table column aggregate functions (all NONE except for accession, description 
		// and web resource columns, which are not aggregatable)
		for (int i = 2; i < 11; i++) {
			((TableColumnExt2) tcm.getColumn(i)).setAggregateFunction(AggregateFunction.NONE);
		}
		((TableColumnExt2) tcm.getColumn(7)).setAggregateFunction(AggregateFunction.DISTINCT);
		((TableColumnExt2) tcm.getColumn(8)).setAggregateFunction(AggregateFunction.DISTINCT);
		
		TableConfig.setColumnWidths(treeTbl, new double[] { 8.25, 20, 14, 4, 5, 4, 3, 4, 4, 4.5, 5, 1 });
		TableConfig.setColumnMinWidths(
				treeTbl, UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(), 22, getFont());
		TableColumnExt webColumn = (TableColumnExt) tcm.getColumn(11);
		webColumn.setMinWidth(19);
		webColumn.setMaxWidth(19);
		webColumn.setResizable(false);
		webColumn.setSortable(false);
		
		// Web resource action
		Action webResourceAction = new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				setupWebresourceMenu(treeTbl);
			}
		};
		ButtonColumn bc = new ButtonColumn(treeTbl, webResourceAction, 11);
		
		webColumn.setCellRenderer(bc);
		webColumn.setCellEditor(bc);
		treeTbl.addMouseListener(bc);
		
		// Pre-select root node
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(new TreePath(root));

		// Set default sort order (spectral count, descending)
		((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(7, SortOrder.DESCENDING);

		// Synchronize selection with original view table
		// TODO: find proper way to synchronize selections when original view will be removed in the future 
		treeTbl.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				PhylogenyTreeTableNode node = ((PhylogenyTreeTableNode) tse.getPath().getLastPathComponent());
				if (node.isProtein()) {
					String accession = ((ProteinHit) node.getUserObject()).getAccession();
					for (int row = 0; row < proteinTbl.getRowCount(); row++) {
						if (proteinTbl.getValueAt(row, Constants.PROT_ACCESSION).equals(accession)) {
							proteinTbl.getSelectionModel().setSelectionInterval(row, row);
							return;
						}
					}
				} else {
					proteinTbl.clearSelection();
					refreshPeptideViews(-1);
				}
			}
		});

		// Add listener to synchronize selection state of nodes throughout multiple trees
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				if (!synching) {
					if (tse.getPath() == null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								synching = true;
								synchSelection(treeTbl);
								synching = false;
								updateChartData();
							}
						});
					}
				}
			}
//			/**
//			 * Traverses list of links and updates the corresponding selection models of 
//			 * the trees identified by the links' first path elements (i.e. the roots).
//			 * @param node The node whose links shall be updated
//			 * @param added <code>true</code> if the selection was added, <code>false
//			 * </code> otherwise
//			 */
//			private void synchLinks(PhylogenyTreeTableNode node, boolean added) {
//				List<TreePath> links = node.getLinks();
//				if (links != null) {
//					// Iterate leaf links
//					for (TreePath link : links) {
//						// Get the appropriate selection model identified by the root name
//						String rootName = link.getPathComponent(0).toString();
//						CheckBoxTreeSelectionModel tsm =
//								linkMap.get(rootName).getCheckBoxTreeSelectionModel();
//						// Change foreign selection accordingly
//						if (added) {
//							tsm.addSelectionPath(link);
//						} else {
//							tsm.removeSelectionPath(link);
//						}
//					}
//				}
//			}
		});

		// Reduce node indents to make tree more compact horizontally
		treeTbl.setIndents(6, 4, 2);
		// Hide root node
		treeTbl.setRootVisible(false);

		// Set up node icons
		// TODO: add icons for non-leaves, e.g. for different taxonomic levels
		IconValue iv = new IconValue() {
			@Override
			public Icon getIcon(Object value) {
				TreeCellContext context = (TreeCellContext) value;
				PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) context.getValue();
				if (context.isLeaf()) {
					return IconConstants.PROTEIN_TREE_ICON;
				} else if (node.isProtein()) {
					ProteinHit ph = (ProteinHit) node.getUserObject();
					if (ph.getAccession().startsWith("Meta-Protein")) {
						return IconConstants.METAPROTEIN_TREE_ICON;
					}
//					} else if ("Unclassified".equals(context.getValue().toString())) {
//						
				}
				// fall back to defaults
				return context.getIcon();
			}
		};
		treeTbl.setIconValue(iv);
		
		// Install renderers and highlighters
		// TODO: use proper column index variables
		FontMetrics fm = getFontMetrics(chartFont);

		FormatHighlighter leftHL = new FormatHighlighter(SwingConstants.LEFT);
		((TableColumnExt) tcm.getColumn(1)).addHighlighter(leftHL);
		((TableColumnExt) tcm.getColumn(2)).addHighlighter(leftHL);

		BarChartHighlighter bch = new BarChartHighlighter(ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.0"));
		bch.setBaseline(1 + fm.stringWidth(bch.getFormatter().format(100.0)));
		((TableColumnExt) tcm.getColumn(3)).addHighlighter(bch);

		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(4)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN, x100formatter));

		((TableColumnExt) tcm.getColumn(5)).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "0.000"));

		((TableColumnExt) tcm.getColumn(6)).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "0.00"));

		((TableColumnExt) tcm.getColumn(7)).addHighlighter(new BarChartHighlighter());

		((TableColumnExt) tcm.getColumn(8)).addHighlighter(new BarChartHighlighter());

		((TableColumnExt) tcm.getColumn(9)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00")));

		((TableColumnExt) tcm.getColumn(10)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00000")));
		
		// Install non-leaf highlighter
		Color hlCol = new Color(237, 246, 255);	// light blue
//		Color hlCol = new Color(255, 255, 237);	// light yellow
		HighlightPredicate notLeaf = new NotHighlightPredicate(HighlightPredicate.IS_LEAF);
		HighlightPredicate notSel = new NotHighlightPredicate(HighlightPredicate.IS_SELECTED);
		treeTbl.addHighlighter(new CompoundHighlighter(
				new ColorHighlighter(new AndHighlightPredicate(
						notSel, notLeaf, HighlightPredicate.EVEN), hlCol, null),
						new ColorHighlighter(new AndHighlightPredicate(
								notSel, notLeaf, HighlightPredicate.ODD), ColorUtils.getRescaledColor(hlCol, 0.95f), null)));

		// Enable column control widget
		TableConfig.configureColumnControl(treeTbl);
		
		// Add table to link map
		linkMap.put(root.toString(), treeTbl);

		return treeTbl;
	}
	
	/**
	 * Creates and configures a mouse adapter for tree table headers to display a context menu.
	 * @return the header mouse adapter
	 */
	private MouseAdapter createHeaderMouseAdapter(final JXTreeTable treeTbl) {
		// TODO: maybe integrate functionality into tree table class
		final ComponentTableHeader ch = (ComponentTableHeader) treeTbl.getTableHeader();
		MouseAdapter ma = new MouseAdapter() {
			/**
			 * The column index of the last pressed column header.<br>
			 */
			private int col = -1;

			/**
			 * Creates and configures the current column header's context menu.
			 * @return the context menu
			 */
			private JPopupMenu createPopup() {
				JPopupMenu popup = new JPopupMenu() {
					@Override
					public void setVisible(boolean b) {
						// automatically raise the column header when popup is dismissed
						if (!b) {
							raise();
						}
						super.setVisible(b);
					}
				};

				// Create sub-menu containing sorting-related items
				JMenu sortMenu = new JMenu("Sort");
				sortMenu.setIcon(IconConstants.SORT_ICON);
				
				SortOrder order = ((TreeTableRowSorter) treeTbl.getRowSorter()).getSortOrder(this.col);
				JMenuItem ascChk = new JRadioButtonMenuItem("Ascending", order == SortOrder.ASCENDING);
				JMenuItem desChk = new JRadioButtonMenuItem("Descending", order == SortOrder.DESCENDING);
				JMenuItem unsChk = new JRadioButtonMenuItem("Unsorted", order == SortOrder.UNSORTED);
				
				ActionListener sortListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						SortOrder order = SortOrder.valueOf(
								((AbstractButton) evt.getSource()).getText().toUpperCase());
						((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(col, order);
					}
				};
				ascChk.addActionListener(sortListener);
				desChk.addActionListener(sortListener);
				unsChk.addActionListener(sortListener);
				
				sortMenu.add(ascChk);
				sortMenu.add(desChk);
				sortMenu.add(unsChk);
				
				// Create item for selecting column filter dialog
				// TODO: create a column filter dialog :)
				JMenuItem filterItem = new JMenuItem("Filter... (not implem.)", IconConstants.FILTER_ICON);
				
				// Create sub-menu containing non-leaf value aggregation functions
				JMenu aggrMenu = new JMenu("Aggregate Function");
				aggrMenu.setIcon(IconConstants.CALCULATOR_ICON);

				ActionListener aggrListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						TableColumnExt2 column = (TableColumnExt2) treeTbl.getColumnExt(col);
						AggregateFunction aggFcn =
								(AggregateFunction) ((JComponent) evt.getSource()).getClientProperty("aggFcn");
						column.setAggregateFunction(aggFcn);
					}
				};
				TableColumnExt2 column = (TableColumnExt2) treeTbl.getColumnExt(col);
				if (column.canAggregate()) {
					AggregateFunction colFcn = column.getAggregateFunction();
					for (AggregateFunction aggFcn : AggregateFunction.values()) {
						String text = StringUtils.capitalize(aggFcn.name().toLowerCase());
						JMenuItem aggrItem = new JRadioButtonMenuItem(text, aggFcn == colFcn);
						aggrItem.putClientProperty("aggFcn", aggFcn);
						aggrItem.addActionListener(aggrListener);
						aggrMenu.add(aggrItem);
					}
				} else {
					aggrMenu.add(new JMenuItem());
					aggrMenu.setEnabled(false);
				}

				popup.add(sortMenu);
				popup.add(filterItem);
				popup.add(aggrMenu);
				return popup;
			}

			@Override
			public void mousePressed(MouseEvent me) {
				int col = ch.columnAtPoint(me.getPoint());
				// Check whether right mouse button has been pressed
				if ((col != -1) && (me.getButton() == MouseEvent.BUTTON3)) {
					this.col = col;
					lower();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me) {
				if ((me.getButton() == MouseEvent.BUTTON3) && 
						(ch.getBounds().contains(me.getPoint()))) {
					// don't show popup for web resources column
					if (!" ".equals(treeTbl.getColumn(this.col).getIdentifier())) {
						this.createPopup().show(ch, ch.getHeaderRect(this.col).x - 1, ch.getHeight() - 1);
					} else {
						this.raise();
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent me) {
				TableColumn draggedColumn = ch.getDraggedColumn();
				if (draggedColumn != null) {
					int col = treeTbl.convertColumnIndexToView(draggedColumn.getModelIndex());
					if ((col != -1) && (col != this.col)) {
						this.col = col;
					}
				}
			}
			
			@Override
			public void mouseExited(MouseEvent me) {
				if ((this.col != -1) && 
						((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
					raise();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent me) {
				if ((this.col != -1) && 
						((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
					lower();
				}
			}
			
			/**
			 * Convenience method to configure the column header to appear pressed.
			 */
			private void lower() {
				TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
				if (hr instanceof ComponentHeaderRenderer) {
					ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
					chr.getPanel().setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Color.GRAY),
							BorderFactory.createEmptyBorder(1, 1, 0, -1)));
					chr.getPanel().setOpaque(true);
					ch.repaint(ch.getHeaderRect(this.col));
				}
			}
			
			/**
			 * Convenience method to configure the column header to not appear pressed.
			 */
			private void raise() {
				TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
				if (hr instanceof ComponentHeaderRenderer) {
					ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
					chr.getPanel().setBorder(UIManager.getBorder("TableHeader.cellBorder"));
					chr.getPanel().setOpaque(false);
					ch.repaint(ch.getHeaderRect(this.col));
				}
			}
		};
		
		return ma;
	}

	/**
	 * Inserts a protein tree node into the 'Flat View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	protected TreePath insertFlatNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protFlatTreeTbl.getTreeTableModel();

//		treeTblMdl.insertNodeInto(protNode,
//				(MutableTreeTableNode) treeTblMdl.getRoot(),
//				treeTblMdl.getRoot().getChildCount());
		((PhylogenyTreeTableNode) treeTblMdl.getRoot()).add(protNode);

		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}

	/**
	 * Inserts a protein tree node into the 'Taxonomy View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	protected TreePath insertTaxonomicNode(PhylogenyTreeTableNode protNode) {
		// get taxonomic tree table root
		DefaultTreeTableModel treeTblMdl =
				(DefaultTreeTableModel) protTaxonTreeTbl.getTreeTableModel();
		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

		// extract protein hit from provided node
		ProteinHit ph = (ProteinHit) protNode.getUserObject();
		
//		List<String> names = new ArrayList<String>();
//		TaxonomyNode[] path = ph.getTaxonomyNode().getPath();
//		for (TaxonomyNode taxNode : path) {
//			names.add(taxNode.getName());
//		}

//		PhylogenyTreeTableNode parent = root;
//		for (String name : names) {
//			PhylogenyTreeTableNode child = (PhylogenyTreeTableNode) parent.getChildByName(name);
//			if (child == null) {
//				child = new PhylogenyTreeTableNode(name);
////				treeTblMdl.insertNodeInto(child, parent, 0);
//				parent.insert(child, 0);
//			}
//			parent = child;
//		}
////		treeTblMdl.insertNodeInto(protNode, parent, parent.getChildCount());
//		parent.add(protNode);

//		return new TreePath(treeTblMdl.getPathToRoot(protNode));
		
		// go from the root of the tree along the taxonomy path
		TaxonomyNode[] taxPath = ph.getTaxonomyNode().getPath();
		PhylogenyTreeTableNode parent = root;
		for (TaxonomyNode taxNode : taxPath) {
			// get child node associated with current taxonomy node
			PhylogenyTreeTableNode child = (PhylogenyTreeTableNode) parent.getChildByUserObject(taxNode);
			if (child == null) {
				// no child with this taxonomy node exists currently, create a new one
				child = new PhylogenyTreeTableNode(taxNode);
				// add new child to parent
				parent.add(child);
			}
			// the child node becomes the parent in the next iteration
			parent = child;
		}
		// insert protein node as leaf
		parent.add(protNode);
		
		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}

	/**
	 * Inserts a protein tree node into the 'E.C. View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	protected TreePath insertEnzymeNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protEnzymeTreeTbl.getTreeTableModel();

		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

		ProteinHit ph = (ProteinHit) protNode.getUserObject();

		List<String> ecNumbers;
		ReducedUniProtEntry upe = ph.getUniProtEntry();
		if (upe != null) {
			ecNumbers = upe.getEcNumbers();
		} else {
			ecNumbers = new ArrayList<String>();
		}

		if (ecNumbers.isEmpty()) {
			ecNumbers.add("Unclassified");
		}

		// Split primary E.C. number string into multiple tokens (typically 4)
		// TODO: what to do when more than one E.C. number exists for the protein hit?
		String[] ecTokens = ecNumbers.get(0).split("[.]");

		PhylogenyTreeTableNode parent = root;
		for (int i = 0; i < ecTokens.length; i++) {
			String name = "";
			for (int j = 0; j < ecTokens.length; j++) {
				if (j > 0) name += ".";
				name += (j <= i) ? ecTokens[j] : "-";
			}
			PhylogenyTreeTableNode child = (PhylogenyTreeTableNode) parent.getChildByName(name);
			if (child == null) {
				ECEntry entry = Parameters.getInstance().getEcMap().get(name);
				child = new PhylogenyTreeTableNode(name, (entry != null) ? entry.getName() : "");
				if (!name.equals("Unclassified")) {
					URI uri = URI.create("http://enzyme.expasy.org/EC/" + name);
					child.setURI(uri);	
				}
//				treeTblMdl.insertNodeInto(child, parent, 0);
//				parent.insert(child, 0);
				parent.add(child);
			}
			parent = child;
		}
//		treeTblMdl.insertNodeInto(protNode, parent, parent.getChildCount());
		parent.add(protNode);

		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}

	/**
	 * Inserts a protein tree node into the 'Pathway View' tree table and
	 * returns the tree paths to where its instances were inserted (in case the
	 * protein is part of multiple pathways).
	 * @param protNode The protein tree node to insert
	 * @return The tree paths pointing to the insertion locations
	 */
	protected TreePath[] insertPathwayNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protPathwayTreeTbl.getTreeTableModel();

		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

		ProteinHit ph = (ProteinHit) protNode.getUserObject();

		// gather K and EC numbers for pathway lookup
		List<String> kNumbers;
		List<String> ecNumbers;
		ReducedUniProtEntry upe = ph.getUniProtEntry();
		if (upe != null) {
			kNumbers = upe.getKNumbers();
			ecNumbers = upe.getEcNumbers();
		} else {
			kNumbers = new ArrayList<String>();
			ecNumbers = new ArrayList<String>();
		}

		// perform pathway lookup
		Set<Short> pathways = new HashSet<Short>();
		for (String ko : kNumbers) {
			List<Short> pathwaysByKO = KeggAccessor.getInstance().getPathwaysByKO(ko.substring(1));
			if (pathwaysByKO != null) {
				pathways.addAll(pathwaysByKO);
			}
		}
		for (String ec : ecNumbers) {
			List<Short> pathwaysByEC = KeggAccessor.getInstance().getPathwaysByEC(ec);
			if (pathwaysByEC != null) {
				pathways.addAll(pathwaysByEC);
			}
		}

		List<TreePath> treePaths = new ArrayList<TreePath>();
		PhylogenyTreeTableNode parent = root;

		// iterate found pathways
		if (!pathways.isEmpty()) {
			// iterate pathway IDs
			for (Short pw : pathways) {
				// look for pathway in existing tree
				parent = findPathway(pw);

				// check whether pathway retrieval succeeded
				if (parent == null) {
					// look for pathway in global pathway tree
					Object[] nodeNames = Constants.getKEGGPathwayPath(pw);

					parent = root;
					for (int i = 1; i < 4; i++) {
						String nodeName = (String) nodeNames[i];
						String[] splitName = nodeName.split("  ");
						PhylogenyTreeTableNode child = 
								(PhylogenyTreeTableNode) parent.getChildByName(splitName[0]);
						if (child == null) {
							for (int j = i; j < 4; j++) {
								child = new PhylogenyTreeTableNode(
										(Object[]) ((String) nodeNames[j]).split("  "));
//								treeTblMdl.insertNodeInto(child, parent, parent.getChildCount());
								parent.add(child);
								parent = child;
							}
							break;
						} else {
							parent = child;
						}
					}						
				}

				// add clone of protein node to each pathway
				PhylogenyTreeTableNode cloneNode = new PhylogenyTreeTableNode(protNode.getUserObject());
				cloneNode.setURI(protNode.getURI());
//				treeTblMdl.insertNodeInto(cloneNode, parent, parent.getChildCount());
				parent.add(cloneNode);
				treePaths.add(new TreePath(treeTblMdl.getPathToRoot(cloneNode)));
			}
		} else {
			// no pathways were found, therefore look for 'Unclassified' node in tree
			Enumeration<? extends MutableTreeTableNode> children = root.children();
			while (children.hasMoreElements()) {
				MutableTreeTableNode child = (MutableTreeTableNode) children.nextElement();
				if (child.getUserObject().equals("Unclassified")) {
					parent = (PhylogenyTreeTableNode) child;
					break;
				}
			}
			if (parent == root) {
				// 'Unclassified' node does not exist yet, therefore create it
				parent = new PhylogenyTreeTableNode("Unclassified");
//				treeTblMdl.insertNodeInto(parent, root, root.getChildCount());
				root.add(parent);
			}
			// add clone of protein node to 'Unclassified' branch
			PhylogenyTreeTableNode cloneNode = new PhylogenyTreeTableNode(protNode.getUserObject());
			cloneNode.setURI(protNode.getURI());
//			treeTblMdl.insertNodeInto(cloneNode, parent, parent.getChildCount());
			parent.add(cloneNode);
			treePaths.add(new TreePath(treeTblMdl.getPathToRoot(cloneNode)));
		}
		// TODO: add redundancy check in case a protein has multiple KOs pointing to the same pathway

		return treePaths.toArray(new TreePath[0]);
	}

	/**
	 * Auxiliary method to find the pathway node which is identified by the
	 * provided pathway ID in the pathway tree.
	 * @param pw The pathway ID
	 * @return The desired pathway node or <code>null</code> if it could not be found
	 */
	protected PhylogenyTreeTableNode findPathway(Short pw) {
		Enumeration<? extends MutableTreeTableNode> childrenA = 
				((MutableTreeTableNode) protPathwayTreeTbl.getTreeTableModel().getRoot()).children();
		while (childrenA.hasMoreElements()) {
			MutableTreeTableNode childA = (MutableTreeTableNode) childrenA.nextElement();
			Enumeration<? extends MutableTreeTableNode> childrenB = childA.children();
			while (childrenB.hasMoreElements()) {
				MutableTreeTableNode childB = (MutableTreeTableNode) childrenB.nextElement();
				Enumeration<? extends MutableTreeTableNode> childrenC = childB.children();
				while (childrenC.hasMoreElements()) {
					MutableTreeTableNode childC = (MutableTreeTableNode) childrenC.nextElement();
					if (((String) childC.getUserObject()).startsWith(String.format("%05d", pw))) {
						return (PhylogenyTreeTableNode) childC;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Links phylogeny nodes to each other. Used for communicating state changes 
	 * between different tree tables.
	 * @param treePaths The tree paths pointing to the nodes that are to be linked
	 */
	protected void linkNodes(TreePath... treePaths) {
		for (int i = 0; i < treePaths.length; i++) {
			PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) treePaths[i].getLastPathComponent();
			for (int j = 0; j < treePaths.length; j++) {
				if (j != i) {
					node.addLink(treePaths[j]);
				}
			}
		}
	}

	/**
	 * Synchronizes checkbox selections of all tree tables.
	 */
	private void synchSelection() {
		this.synchSelection(null);
	}
	
	/**
	 * Synchronizes checkbox selections of all tree tables based on the
	 * selection in the specified table.
	 * @param source the source checkbox tree table
	 */
	private void synchSelection(CheckBoxTreeTable source) {
		// propagate selection state to protein hits in search result object
		if (source != null) {
			CheckBoxTreeSelectionModel cbtsm = source.getCheckBoxTreeSelectionModel();
			DefaultTreeTableModel model = (DefaultTreeTableModel) source.getTreeTableModel();
			PhylogenyTreeTableNode root =
					(PhylogenyTreeTableNode) model.getRoot();
			Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
			while (dfe.hasMoreElements()) {
				TreeNode treeNode = (TreeNode) dfe.nextElement();
				if (treeNode.isLeaf()) {
					Object userObject = ((TreeTableNode) treeNode).getUserObject();
					if (userObject instanceof ProteinHit) {	// sanity check
						TreePath path = new TreePath(model.getPathToRoot((TreeTableNode) treeNode));
						boolean selected = cbtsm.isPathSelected(path, true);
						((ProteinHit) userObject).setSelected(selected);
					}
				}
			}
		}
		// iterate other tables and apply selection based on protein hit selection state
		Collection<CheckBoxTreeTable> allTables = linkMap.values();
		for (CheckBoxTreeTable table : allTables) {
			if (table != source) {
				CheckBoxTreeSelectionModel cbtsm = table.getCheckBoxTreeSelectionModel();
				DefaultTreeTableModel model = (DefaultTreeTableModel) table.getTreeTableModel();
				PhylogenyTreeTableNode root =
						(PhylogenyTreeTableNode) model.getRoot();
				Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
				while (dfe.hasMoreElements()) {
					TreeNode treeNode = dfe.nextElement();
					if (treeNode.isLeaf()) {
						Object userObject = ((TreeTableNode) treeNode).getUserObject();
						if (userObject instanceof ProteinHit) {	// sanity check
							boolean selected = ((ProteinHit) userObject).isSelected();
							TreePath path = new TreePath(model.getPathToRoot((TreeTableNode) treeNode));
//							System.out.println("" + selected + " " + path.getLastPathComponent());
							if (selected) {
								cbtsm.addSelectionPath(path);
							} else {
								cbtsm.removeSelectionPath(path);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Delegate method to display table-specific tooltips.
	 * @param me The MouseEvent triggering the tooltip.
	 * @return The tooltip's text or <code>null</code>.
	 */
	protected String getTableToolTipText(MouseEvent me) {
		JXTreeTable table = (JXTreeTable) me.getSource();
		if (table == protEnzymeTreeTbl) {
			String ecDesc = null;
			int col = table.columnAtPoint(me.getPoint());
			if (col == 1) {
				int row = table.rowAtPoint(me.getPoint());
				TreePath pathForRow = table.getPathForRow(row);
				if (pathForRow != null){
					PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) pathForRow.getLastPathComponent();
					ECEntry ecEntry = Parameters.getInstance().getEcMap().get(node.getValueAt(0));
					ecDesc = (ecEntry != null) ? ecEntry.getDescription() : null;
					if (ecDesc != null) {
						ecDesc = "<html>" + ecDesc + "</html>";
						StringBuffer sb = new StringBuffer (ecDesc);  
						String newLine = "<br>"; 
						for (int i = 70; i < ecDesc.length(); i += 70) {
							int linebreak = sb.indexOf(" ", i);
							if (linebreak != -1) {
								sb.insert(linebreak, newLine);
							}
						}
						ecDesc = sb.toString();
					}
				}
			}
			return ecDesc;
		}
		return null;
	}

	// Peptide table column indices
	private final int PEP_SELECTION		= 0;
	private final int PEP_INDEX			= 1;
	private final int PEP_SEQUENCE		= 2;
	private final int PEP_PROTEINCOUNT	= 3;
	private final int PEP_SPECTRALCOUNT	= 4;
	private final int PEP_TAXONOMY		= 5;

	/**
	 * This method sets up the peptide results table.
	 */
	private void setupPeptideTableProperties() {
		// Peptide table
		final TableModel peptideTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { "", "#", "Sequence", "ProtC", "SpC", "Tax" });
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PEP_SELECTION:
					return Boolean.class;
				case PEP_INDEX:
				case PEP_SPECTRALCOUNT:
					return Integer.class;
				case PEP_PROTEINCOUNT:
					return Integer.class;
				case PEP_SEQUENCE:
				case PEP_TAXONOMY:
				default:
					return String.class;
				}
			}

			public boolean isCellEditable(int row, int col) {
				return (col == PEP_SELECTION || col == PEP_SEQUENCE);
			}
		};
		peptideTbl = new JXTable(peptideTblMdl) {
			private Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(padding);
				}
				return comp;
			}
			@Override
			public Component prepareEditor(TableCellEditor editor, int row,
					int column) {
				Component comp = super.prepareEditor(editor, row, column);
				comp.setFont(chartFont);
				compoundHighlighter.highlight(comp, getComponentAdapter(row, column));
				return comp;
			}
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if (column == PEP_SELECTION) {
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), Constants.PROT_ACCESSION));
					proteinHit.getPeptideHits().get(getValueAt(row, PEP_SEQUENCE)).setSelected((Boolean) aValue);
				}
			}
			//			@Override
			//			public org.jdesktop.swingx.decorator.ComponentAdapter getComponentAdapter(
			//					int row, int column) {
			//				return super.getComponentAdapter(row, column);
			//			}
		};

		TableConfig.setColumnWidths(peptideTbl, new double[] {0, 0.9, 3.7, 1.7, 1.7,1.7});

		TableColumnModelExt tcm = (TableColumnModelExt) peptideTbl.getColumnModel();

		String[] columnToolTips = {
				"Selection for Export",
				"Peptide Index",
				"Peptide Sequence",
				"Protein Count",
				"Spectral Count",
				"Peptide Taxonomy"
		};
		ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		peptideTbl.setTableHeader(ch);

		final JCheckBox selChk = new JCheckBox();
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);

		// Add filter button widgets to column headers
		tcm.getColumn(PEP_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null, SwingConstants.TRAILING));
		tcm.getColumn(PEP_SELECTION).setMinWidth(19);
		tcm.getColumn(PEP_SELECTION).setMaxWidth(19);
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(13, 13));
		for (int col = PEP_INDEX; col < tcm.getColumnCount(); col++) {
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(panel) {
				protected SortKey getSortKey(JTable table, int column) {
					return table.getRowSorter().getSortKeys().get(1);
				}
			});
		}

		tcm.getColumnExt(PEP_INDEX).addHighlighter(new FormatHighlighter(SwingConstants.RIGHT));
		tcm.getColumnExt(PEP_PROTEINCOUNT).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		tcm.getColumnExt(PEP_SPECTRALCOUNT).addHighlighter(new BarChartHighlighter());

		tcm.getColumnExt(PEP_TAXONOMY).setVisible(false);

		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PEP_SELECTION, SortOrder.DESCENDING);

		final TableSortController<TableModel> tsc = new TableSortController<TableModel>(peptideTblMdl) {
			@Override
			public void toggleSortOrder(int column) {
				List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
				SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
				if (keys.indexOf(sortKey) == 1)  {
					// primary key: cycle sort order
					SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
							SortOrder.DESCENDING : SortOrder.ASCENDING;
					keys.set(1, new SortKey(column, so));
				} else {
					// all others: insert new 
					keys.remove(sortKey);
					keys.add(1, new SortKey(column, SortOrder.DESCENDING));
				}
				if (keys.size() > getMaxSortKeys()) {
					keys = keys.subList(0, getMaxSortKeys());
				}
				setSortKeys(keys);
			}
			@Override
			public void setSortKeys(List<? extends SortKey> sortKeys) {
				List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
				// make sure selection column sorting always occurs before other columns
				newKeys.remove(selKey);
				newKeys.add(0, selKey);
				super.setSortKeys(newKeys);
			}
		};
		peptideTbl.setRowSorter(tsc);

		// register action listener on selection column header checkbox
		selChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = selChk.isSelected();
				// prevent auto-resorting while iterating rows for performance reasons
				tsc.setSortsOnUpdates(false);
				for (int row = 0; row < peptideTbl.getRowCount(); row++) {
					peptideTbl.setValueAt(selected, row, PEP_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});

		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PEP_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PEP_SPECTRALCOUNT, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);

		// register list selection listener
		peptideTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int selRow = peptideTbl.getSelectedRow();
				if (selRow != -1) {
					coverageSelectionModel.setValue(peptideTbl.convertRowIndexToModel(selRow));
					PeptideHit peptideHit = dbSearchResult.getProteinHit((String) proteinTbl.getValueAt(proteinTbl.getSelectedRow(), Constants.PROT_ACCESSION)).getPeptideHit((String) peptideTbl.getValueAt(peptideTbl.getSelectedRow(), PEP_SEQUENCE));
					protCHighlightPredicate.setProteinHits(peptideHit.getProteinHits());
					proteinTbl.repaint();
				}
				refreshPsmTable();
			}
		});

		// Single selection only
		peptideTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		peptideTbl.addHighlighter(TableConfig.getSimpleStriping());

		// Mark unique peptides by making sequence font bold
		peptideTbl.addHighlighter(new FontHighlighter(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
				if (adapter.convertColumnIndexToModel(adapter.column) == PEP_SEQUENCE) {
					//					int protCount = (Integer) adapter.getValueAt(
					//							adapter.convertRowIndexToModel(adapter.row), PEP_PROTEINCOUNT);
					int protCount = (Integer) adapter.getValue(PEP_PROTEINCOUNT);
					return (protCount == 1);
				}
				return false;
			}
		}, chartFont.deriveFont(Font.BOLD)));

		// Enables column control
		TableConfig.configureColumnControl(peptideTbl);

		JTextField editorTtf = new JTextField();
		editorTtf.setEditable(false);
		editorTtf.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		editorTtf.setSelectionColor(Color.WHITE);
		editorTtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((JTextComponent) e.getSource()).selectAll();
			}
		});
		tcm.getColumn(PEP_SEQUENCE).setCellEditor(new DefaultCellEditor(editorTtf));
	}

	/**
	 * This method sets up the protein peptide coverage viewer panel.
	 */
	private void setupCoverageViewer() {

		coveragePnl = new JPanel(new WrapLayout(FlowLayout.LEFT));
		coveragePnl.setBackground(Color.WHITE);

	}

	// PSM table column indices
	private final int PSM_SELECTION	= 0;
	private final int PSM_INDEX		= 1;
	//	private final int PSM_SEQUENCE	= 2;
	private final int PSM_CHARGE	= 2;
	private final int PSM_VOTES		= 3;
	private final int PSM_XTANDEM 	= 4;
	private final int PSM_OMSSA 	= 5;
	private final int PSM_CRUX 		= 6;
	private final int PSM_INSPECT 	= 7;
	private final int PSM_MASCOT 	= 8;

	private ValueHolder coverageSelectionModel = new ValueHolder(-1);



	/**
	 * A clickable label with rollover effect.
	 * 
	 * @author A. Behne
	 */
	private class HoverLabel extends JToggleButton {
	
		private Color selected;
		private List<HoverLabel> siblings;
	
		public HoverLabel(String text) {
			this(text, Color.RED);
		}
	
		public HoverLabel(String text, Color foreground) {
			this(text, foreground, new Color(foreground.getRed(), foreground.getGreen(),
					foreground.getBlue(), (foreground.getAlpha() + 1) / 8));
		}
	
		public HoverLabel(String text, Color foreground, Color selected) {
			super(text);
			this.selected = selected;
			this.siblings = new ArrayList<HoverLabel>();
			setForeground(foreground);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setBorder(null);
			setFont(new Font("Monospaced", Font.PLAIN, 12));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			addMouseListener(new MouseAdapter() {
				private Font originalFont, underlineFont;
				{
					originalFont = getFont();
					Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					underlineFont = originalFont.deriveFont(attributes);
				}
				public void mouseEntered(MouseEvent me) {
					for (HoverLabel sibling : siblings) {
						sibling.setFont(underlineFont);
					}
				}
				public void mouseExited(MouseEvent me) {
					for (HoverLabel sibling : siblings) {
						sibling.setFont(originalFont);
					}
				}
			});
			// TODO: synchronize selection state with table
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = peptideTbl.convertRowIndexToView((Integer) coverageSelectionModel.getValue());
					peptideTbl.getSelectionModel().setSelectionInterval(index, index);
				}
			});
		}
	
		public void setSiblings(List<HoverLabel> siblings) {
			this.siblings = siblings;
		}
	
		@Override
		protected void paintComponent(Graphics g) {
			if (isSelected()) {
				g.setColor(selected);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			super.paintComponent(g);
		}
	}

	/**
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties() {
		// PSM table
		final TableModel psmTblMdl = new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] {
						"", "#", "z", "Votes", "X", "O", "C", "I", "M" });
			}

			public boolean isCellEditable(int row, int col) {
				return (col == PSM_SELECTION) ? true : false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PSM_SELECTION:
					return Boolean.class;
				case PSM_INDEX:
				case PSM_CHARGE:
				case PSM_VOTES:
					return Integer.class;
				case PSM_XTANDEM:
				case PSM_OMSSA:
				case PSM_CRUX:
				case PSM_INSPECT:
				case PSM_MASCOT:
					return Double.class;
//				case PSM_SEQUENCE:
				default: 
					return String.class;
				}
			}
		};
		final DecimalFormat df = new DecimalFormat("0.000%");
		psmTbl = new JXTable(psmTblMdl) {
			private Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(padding);
				}
				return comp;
			}
			@Override
			public String getToolTipText(MouseEvent me) {
				int col = columnAtPoint(me.getPoint());
				if (col == convertColumnIndexToModel(PSM_XTANDEM) ||
						col == convertColumnIndexToModel(PSM_OMSSA) ||
						col == convertColumnIndexToModel(PSM_CRUX) ||
						col == convertColumnIndexToModel(PSM_INSPECT) ||
						col == convertColumnIndexToModel(PSM_MASCOT)) {
					int row = rowAtPoint(me.getPoint());
					if (row != -1) {
						return df.format(getValueAt(row, col));
					}
				}
				return null;
			}
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if (column == PSM_SELECTION) {
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), Constants.PROT_ACCESSION));

					Map<String, PeptideHit> peptideHits = proteinHit.getPeptideHits();
					int selectedPeptideRow = peptideTbl.getSelectedRow();
					String sequence = (String) peptideTbl.getValueAt(selectedPeptideRow, PEP_SEQUENCE);
					PeptideHit peptideHit = peptideHits.get(sequence);
					peptideHit.getSpectrumMatches().get((Integer) getValueAt(row, PSM_INDEX)-1).setSelected((Boolean) aValue);
				}
			}
		};

		// adjust column widths
		TableConfig.setColumnWidths(psmTbl, new double[] { 0, 1, 1, 2, 1, 1, 1, 1 ,1 });

		// Get table column model
		final TableColumnModelExt tcm = (TableColumnModelExt) psmTbl.getColumnModel();

		// Apply component header capabilities as well as column header tooltips
		String[] columnToolTips = {
				"Selection for Export",
				"PSM Index",
				"Precursor Charge",
				"Number of Votes",
				"X!Tandem Confidence",
				"Omssa Confidence",
				"Crux Confidence",
				"InsPecT Confidence",
		"Mascot Confidence"};
		ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		psmTbl.setTableHeader(ch);

		final JCheckBox selChk = new JCheckBox();
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);

		// Add filter button widgets to column headers
		tcm.getColumn(PSM_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null, SwingConstants.TRAILING));
		tcm.getColumn(PSM_SELECTION).setMinWidth(19);
		tcm.getColumn(PSM_SELECTION).setMaxWidth(19);
		for (int col = PSM_INDEX; col < tcm.getColumnCount(); col++) {
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(13, 13));
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(panel) {
				protected SortKey getSortKey(JTable table, int column) {
					return table.getRowSorter().getSortKeys().get(1);
				}
			});
		}

		// Apply custom cell renderers/highlighters to columns 
		tcm.getColumnExt(PSM_INDEX).addHighlighter(new FormatHighlighter(SwingConstants.RIGHT));
		tcm.getColumnExt(PSM_CHARGE).addHighlighter(new FormatHighlighter(SwingConstants.CENTER, "+0"));
//		tcm.getColumnExt(PSM_VOTES).addHighlighter(new FormatHighlighter(SwingConstants.CENTER));
		tcm.getColumnExt(PSM_VOTES).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED));
		tcm.getColumnExt(PSM_XTANDEM).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN));
		tcm.getColumnExt(PSM_OMSSA).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_CYAN, ColorUtils.LIGHT_CYAN));
		tcm.getColumnExt(PSM_CRUX).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_BLUE, ColorUtils.LIGHT_BLUE));
		tcm.getColumnExt(PSM_INSPECT).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_MAGENTA, ColorUtils.LIGHT_MAGENTA));
		tcm.getColumnExt(PSM_MASCOT).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		// Hide Mascot column
		// TODO: retrieve search settings when fetching database search result and unhide Mascot column if Mascot results are contained within
		tcm.getColumnExt(PSM_MASCOT).setVisible(false);

		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PSM_SELECTION, SortOrder.DESCENDING);

		final TableSortController<TableModel> tsc = new TableSortController<TableModel>(psmTblMdl) {
			@Override
			public void toggleSortOrder(int column) {
				List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
				SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
				if (keys.indexOf(sortKey) == 1)  {
					// primary key: cycle sort order
					SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
							SortOrder.DESCENDING : SortOrder.ASCENDING;
					keys.set(1, new SortKey(column, so));
				} else {
					// all others: insert new 
					keys.remove(sortKey);
					keys.add(1, new SortKey(column, SortOrder.DESCENDING));
				}
				if (keys.size() > getMaxSortKeys()) {
					keys = keys.subList(0, getMaxSortKeys());
				}
				setSortKeys(keys);
			}
			@Override
			public void setSortKeys(List<? extends SortKey> sortKeys) {
				List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
				// make sure selection column sorting always occurs before other columns
				newKeys.remove(selKey);
				newKeys.add(0, selKey);
				super.setSortKeys(newKeys);
			}
		};
		psmTbl.setRowSorter(tsc);

		// register action listener on selection column header checkbox
		selChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = selChk.isSelected();
				// prevent auto-resorting while iterating rows for performance reasons
				tsc.setSortsOnUpdates(false);
				for (int row = 0; row < psmTbl.getRowCount(); row++) {
					psmTbl.setValueAt(selected, row, PSM_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});

		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PSM_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PSM_VOTES, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);

		// Prevent messing with sort order of selection column
		//		tsc.setSortable(PSM_SELECTION, false);

		// Register list selection listener
		psmTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshPlot();
			}
		});

		// Only one row is selectable
		psmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		psmTbl.addHighlighter(TableConfig.getSimpleStriping());

		// Enables column control
		TableConfig.configureColumnControl(psmTbl);
	}

	/**
	 * This method constructs the spectrum filter panel.
	 */
	private JPanel constructSpectrumFilterPanel() {

		JPanel spectrumFilterPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 5dlu"));

		Dimension size = new Dimension(39, 0);
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updateFilteredAnnotations();
			}
		};

		spectrumFilterPanel.add(createIonToggleButton("a", false, "Show a ions", size, action), CC.xy(2, 2));
		spectrumFilterPanel.add(createIonToggleButton("b", true, "Show b ions", size, action), CC.xy(2, 4));
		spectrumFilterPanel.add(createIonToggleButton("c", false, "Show c ions", size, action), CC.xy(2, 6));
		spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 8));
		spectrumFilterPanel.add(createIonToggleButton("x", false, "Show x ions", size, action), CC.xy(2, 10));
		spectrumFilterPanel.add(createIonToggleButton("y", true, "Show y ions", size, action), CC.xy(2, 12));
		spectrumFilterPanel.add(createIonToggleButton("z", false, "Show z ions", size, action), CC.xy(2, 14));
		spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 16));
		spectrumFilterPanel.add(createIonToggleButton("\u00B0", false, "<html>Show H<sub>2</sub>0 losses</html>", size, action), CC.xy(2, 18));
		spectrumFilterPanel.add(createIonToggleButton("*", false, "<html>Show NH<sub>3</sub> losses</html>", size, action), CC.xy(2, 20));
		spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 22));
		spectrumFilterPanel.add(createIonToggleButton("+", true, "Show ions with charge 1", size, action), CC.xy(2, 24));
		spectrumFilterPanel.add(createIonToggleButton("++", false, "Show ions with charge 2", size, action), CC.xy(2, 26));
		spectrumFilterPanel.add(createIonToggleButton(">2", false, "Show ions with charge >2", size, action), CC.xy(2, 28));
		spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 30));
		spectrumFilterPanel.add(createIonToggleButton("MH", true, "Show precursor ion", size, action), CC.xy(2, 32));

		return spectrumFilterPanel;
	}

	/**
	 * Utility method to ease checkbox construction.
	 * @param title
	 * @param selected
	 * @param toolTip
	 * @param size
	 * @param action
	 * @return A JCheckBox with the defined parameters.
	 */
	protected JToggleButton createIonToggleButton(String title, boolean selected,
			String toolTip, Dimension size, Action action) {
		JToggleButton toggleButton = new JToggleButton(action);
		toggleButton.setText(title);
		toggleButton.setSelected(selected);
		toggleButton.setToolTipText(toolTip);
		toggleButton.setMaximumSize(size);
		toggleButton.setMinimumSize(size);
		toggleButton.setPreferredSize(size);
		return toggleButton;
	}

	/**
	 * Chart panel capable of displaying various charts (mainly ontology/taxonomy pie charts).
	 */
	private ScrollableChartPane chartPnl;
	
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
	 * Creates and returns the bottom-left chart panel.
	 */
	private JPanel createChartPanel() {
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
		
		this.chartPnl = new ScrollableChartPane(ChartFactory.createOntologyChart(
				dummyData, OntologyChartType.BIOLOGICAL_PROCESS).getChart());
		
		this.chartPnl.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String property = evt.getPropertyName();
				Object value = evt.getNewValue();
				if ("hierarchy".equals(property)) {
					HierarchyLevel hl = (HierarchyLevel) value;
					ontologyData.setHierarchyLevel(hl);
					taxonomyData.setHierarchyLevel(hl);

					updateChart();
				} else if ("hideUnknown".equals(property)) {
					boolean doHide = (Boolean) value;
					ontologyData.setHideUnknown(doHide);
					taxonomyData.setHideUnknown(doHide);
				} else if ("groupingLimit".equals(property)) {
					double limit = (Double) value;
					ontologyData.setMinorGroupingLimit(limit);
					taxonomyData.setMinorGroupingLimit(limit);
					
					updateChart();
				} else if ("selection".equals(property)) {
					// TODO: find some use for selecting items here or remove functionality altogether
				}
			}
		});

		// wrap scroll pane in panel with 5dlu margin around it
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		chartMarginPnl.add(chartPnl, CC.xy(2, 2));

		return chartMarginPnl;
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
					updateChart(newChartType);
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
				// TODO: re-implement chart or remove altogether
				if (chartType == HistogramChartType.TOTAL_ION_HIST) {
					item.setEnabled(false);
				}
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
				updateChart();
			}
		};

		ontoAsPieItem.addActionListener(pieOrBarListener);
		ontoAsBarItem.addActionListener(pieOrBarListener);
		taxAsPieItem.addActionListener(pieOrBarListener);
		taxAsBarItem.addActionListener(pieOrBarListener);
		// TODO: implement bar charts for taxonomy chart view

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
	 * Refreshes the chart's data containers using the currently visible
	 * meta-proteins in the Meta-Protein View
	 */
	private void updateChartData() {
		ProteinHitList metaProteins = new ProteinHitList();
		
		TreeTableModel model = protFlatTreeTbl.getTreeTableModel();
		TreeTableNode root = (TreeTableNode) model.getRoot();
		
		int childCount = model.getChildCount(root);
		for (int i = 0; i < childCount; i++) {
			Object child = model.getChild(root, i);
			Object userObject = ((TreeTableNode) child).getUserObject();
			if (userObject instanceof MetaProteinHit) {
				metaProteins.add((MetaProteinHit) userObject);
			} else if (userObject instanceof ProteinHit) {
				metaProteins.add(((ProteinHit) userObject).getMetaProteinHit());
			} else {
				// technically we should never get here
				System.out.println("UH OH");
			}
		}
		
		this.ontologyData.setData(metaProteins);
		this.taxonomyData.setData(metaProteins);
		
		// TODO: maybe cache colors to make them consistent when manipulating the selection (cf. PaintMap)
		this.updateChart();
	}
	
	/**
	 * Refreshes the chart using the current chart type.
	 */
	private void updateChart() {
		this.updateChart(this.chartType);
	}
	
	/**
	 * Refreshes the chart updating its content reflecting the specified chart
	 * type.
	 * @param chartType the type of chart to be displayed.
	 */
	private void updateChart(ChartType chartType) {
		Chart chart = null;

		// create chart instance
		if (chartType instanceof OntologyChartType) {
			chart = ChartFactory.createOntologyChart(
					this.ontologyData, chartType);
		} else if (chartType instanceof TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyChart(
					this.taxonomyData, chartType);
		}
		
		if (chart != null) {
			// insert chart into panel
			this.chartPnl.setChart(chart.getChart(), true);
		} else {
			System.err.println("Chart type could not be determined!");
		}

		this.chartType = chartType;
	}
	
	/**
	 * Loads a search result object and refreshes all detail view tables with it.
	 */
	public void refreshTables(File importFile) {
		this.importFile = importFile;
		new RefreshTablesTask().execute();
	}
	
	/**
	 * Worker implementation to refresh all detail view tables.
	 * 
	 * @author A. Behne
	 */
	private class RefreshTablesTask extends SwingWorker {
		
		@Override
		protected Object doInBackground() throws Exception {
			DbSearchResultPanel parent = DbSearchResultPanel.this;
			
			// Begin by clearing all tables
			parent.clearTables();
			
			// Fetch search result object
			parent.dbSearchResult = Client.getInstance().getDatabaseSearchResult();
			
			// Build local chart data objects
			HierarchyLevel hl = parent.chartPnl.getHierarchyLevel();
			parent.ontologyData = new OntologyData(
					parent.dbSearchResult, hl);
			parent.taxonomyData = new TaxonomyData(
					parent.dbSearchResult, hl);
			
			// Insert new result data into tables
			try {
				parent.refreshProteinTables();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Refresh chart
			parent.updateChart();
			
			return null;
		}
		
		@Override
		protected void done() {
			// Stop appearing busy
			DbSearchResultPanel.this.setBusy(false);
			
			// Set up graph database contents
			ResultsPanel resPnl =
					(ResultsPanel) DbSearchResultPanel.this.getParent().getParent();
			resPnl.getDeNovoSearchResultPanel().buildGraphDatabase();
		}
		
	}

	/**
	 * Convenience method to empty the various protein tables.
	 */
	protected void clearTables() {
		// Empty 'classic' protein table
		TableConfig.clearTable(proteinTbl);
		// Empty protein tree tables
		for (CheckBoxTreeTable cbtt : linkMap.values()) {
			TableConfig.clearTable(cbtt);
		}
	}

	/**
	 * Method to refresh protein table contents.
	 * @throws Exception 
	 */
	protected void refreshProteinTables() {		
		if (dbSearchResult != null && !dbSearchResult.isEmpty()) {

			ProteinHitList metaProteins = dbSearchResult.getMetaProteins();
			 
			// Display number of proteins in title area
			int numProteins = metaProteins.size();
			protTtlPnl.setTitle("Proteins (" + numProteins + ")");

			// Notify status bar
			Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES");
			Client.getInstance().firePropertyChange("resetall", null, (long) numProteins);
			Client.getInstance().firePropertyChange("resetcur", null, (long) numProteins);
			
//			// Create taxonomy tree for filtering
//			TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
//			DefaultMutableTreeNode taxRoot = new DefaultMutableTreeNode(rootNode);
//			taxModel = new DefaultTreeModel(taxRoot);
			
			// Gather models
			DefaultTableModel proteinTblMdl = (DefaultTableModel) proteinTbl.getModel();

			// Values for construction of highlighter
			int protIndex = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;

			// Iterate meta-proteins
			for (ProteinHit metaProtein : metaProteins) {
				
//				// Insert entry into the taxonomy filtering tree of the filtering dialog
//				this.insertTaxNode(metaProtein);
//				
//				// Filter to investigate only proteins which belongs to the defined taxonomic group, based on the UniProt TaxID
//				TaxonomyNode taxNode = metaProtein.getTaxonomyNode();
//				boolean doInclude = TaxonomyUtils.belongsToGroup(taxNode,filterTaxId);
				// TODO: replace filtering via applying RowFilter to table(s), see TaxonomySelectionDialog class
				boolean doInclude = true;
				
				// If filtering was ok, add to files
				if (doInclude) {
					// Create default values for meta-ProteinHit;
					String metaDesc 	= "";

					PhylogenyTreeTableNode metaNode = new PhylogenyTreeTableNode(metaProtein);
					ProteinHitList proteinHits = ((MetaProteinHit) metaProtein).getProteinHits();
					for (ProteinHit proteinHit : proteinHits) {
						metaProtein.setUniprotEntry(proteinHit.getUniProtEntry());

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
						maxSpecCount = Math.max(maxSpecCount,proteinHit.getSpectralCount());
						max_emPAI = Math.max(max_emPAI, proteinHit.getEmPAI());
						min_emPAI = Math.min(min_emPAI, proteinHit.getEmPAI());
						maxNSAF = Math.max(maxNSAF, nsaf);

//						// Get common taxonomy for each protein hit
//						if (!Client.getInstance().isViewer()) { 
//							TaxonomyNode commonAncestorNode = proteinHit.getTaxonomyNode();
//							for (PeptideHit peptideHit : proteinHit	.getPeptideHitList()) {
//								commonAncestorNode = TaxonomyUtils.getCombinedTaxonomyNode(
//										commonAncestorNode, 
//										peptideHit.getTaxonomyNode(),
//										(Boolean) Client.getInstance().getMetaProteinParameters().get("proteinTaxonomy").getValue());
//							}
//							// TODO: do we really still need the species string? calculating common taxonomy here seems redundant
//							proteinHit.setCommonTaxonomyNode(commonAncestorNode.toString());
//
//							// FIXME: Calculate sequence alignment
//						}

						// Insert protein data into table
						proteinTblMdl.addRow(new Object[] {
								proteinHit.isSelected(), protIndex++,
								proteinHit.getAccession(),
								proteinHit.getDescription(),
//								proteinHit.getSpecies(),
								proteinHit.getTaxonomyNode().getName(),
								proteinHit.getCoverage(),
								proteinHit.getMolecularWeight(),
								proteinHit.getIsoelectricPoint(),
								proteinHit.getPeptideCount(),
								proteinHit.getSpectralCount(),
								proteinHit.getEmPAI(), 
								nsaf,
								IconConstants.WEB_RESOURCE_ICON});

						// Update MetaproteinHit
						metaDesc = proteinHit.getDescription();
						
						metaProtein.addPeptideHits(proteinHit.getPeptideHits());	// this should probably happen elsewhere, e.g. in condenseMetaProteins()

						// Wrap protein data in table node clones and insert them into the relevant trees
						URI uri = URI.create("http://www.uniprot.org/uniprot/" + proteinHit.getAccession());
						
						PhylogenyTreeTableNode flatNode = new PhylogenyTreeTableNode(proteinHit);
						metaNode.add(flatNode);
						// TreePath flatPath = insertFlatNode(flatNode);
						PhylogenyTreeTableNode taxonomicNode = new PhylogenyTreeTableNode(proteinHit);
						DbSearchResultPanel.this.insertTaxonomicNode(taxonomicNode);
						PhylogenyTreeTableNode enzymeNode = new PhylogenyTreeTableNode(proteinHit);
						DbSearchResultPanel.this.insertEnzymeNode(enzymeNode);
						PhylogenyTreeTableNode pathwayNode = new PhylogenyTreeTableNode(proteinHit);
						DbSearchResultPanel.this.insertPathwayNode(pathwayNode);
						
						flatNode.setURI(uri);
						taxonomicNode.setURI(uri);
						enzymeNode.setURI(uri);
						pathwayNode.setURI(uri);

						// // Link nodes to each other
						// linkNodes(flatPath, taxonPath, enzymePath);
						
						if (proteinHit.getUniProtEntry() == null) {
							if (Client.getInstance().isDebug()) {
								System.err.println("Missing UniProt entry: " + proteinHit.getAccession());
							}
						}
					}

					if (metaNode.getChildCount() == 1) {
						metaNode = (PhylogenyTreeTableNode) metaNode.getChildAt(0);
					}

					// Set values for the meta-protein
					metaProtein.setDescription(metaDesc);
					
//					if (!Client.getInstance().isViewer()) {
//						// Get highest common Taxonomy
//						TaxonomyNode firstNode = metaProtein.getPeptideHitList().get(0).getTaxonomyNode();
//						for (PeptideHit peptideHit : metaProtein.getPeptideHitList()) {
//							TaxonomyNode taxonNode = peptideHit.getTaxonomyNode();
//							
//							firstNode = TaxonomyUtils.getCombinedTaxonomyNode(
//									firstNode,
//									taxonNode,
//									((Boolean) Client.getInstance().getMetaProteinParameters().get("metaProteinTaxonomy").getValue()));
//						}
//						metaProtein.setCommonTaxonomyNode(firstNode.getName() + " (" + firstNode.getRank() +")");
//					}
//					metaProtein.setIdentity(metaIdentity);
//					metaProtein.setCoverage(metaSC);
//					metaProtein.setMolecularWeight(metaMW);
//					metaProtein.setIsoelectricPoint(metaPI);
//					metaProtein.setEmPAI(metaEmPai);
//					metaProtein.setNSAF(metaNsaf);
//					metaProtein.setCoverage(metaSC);
					
					DbSearchResultPanel.this.insertFlatNode(metaNode);
				}

				Client.getInstance().firePropertyChange("progressmade", false, true);
			}
			
			// Refresh tree tables by re-setting root node
			for (CheckBoxTreeTable cbtt : linkMap.values()) {
				DefaultTreeTableModel dttm = (DefaultTreeTableModel) cbtt.getTreeTableModel();
				dttm.setRoot(dttm.getRoot());
			}

			// Iterate pathway nodes in respective hierarchical view and update URIs
			Enumeration childrenA = ((TreeNode) protPathwayTreeTbl.getTreeTableModel().getRoot()).children();
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
							PhylogenyTreeTableNode protNode = 
									(PhylogenyTreeTableNode) children.nextElement();
							ProteinHit protHit = (ProteinHit) protNode.getUserObject();
							kNumbers.addAll(protHit.getUniProtEntry().getKNumbers());
							ecNumbers.addAll(protHit.getUniProtEntry().getEcNumbers());
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

			// Adjust highlighters
			if (proteinTbl.getRowCount() > 0) {
				FontMetrics fm = getFontMetrics(chartFont);
				TableColumnModel tcm = proteinTbl.getColumnModel();

				BarChartHighlighter highlighter;

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(Constants.PROT_COVERAGE))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxCoverage)));
				highlighter.setRange(0.0, maxCoverage);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(Constants.PROT_PEPTIDECOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxPeptideCount)));
				highlighter.setRange(0.0, maxPeptideCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(Constants.PROT_SPECTRALCOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
				highlighter.setRange(0.0, maxSpecCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(Constants.PROT_EMPAI))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(max_emPAI)));
				highlighter.setRange(min_emPAI, max_emPAI);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(Constants.PROT_NSAF))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxNSAF)));
				highlighter.setRange(0.0, maxNSAF);

				proteinTbl.getSelectionModel().setSelectionInterval(0, 0);

				// repeat for tree tables
				for (CheckBoxTreeTable table : linkMap.values()) {
					// TODO: use proper column index variables
					// let table method determine baseline automatically, provide ranges
					table.updateHighlighters(4, 0, maxCoverage);
					table.updateHighlighters(7, 0, maxPeptideCount);
					table.updateHighlighters(8, 0, maxSpecCount);
					table.updateHighlighters(9, min_emPAI, max_emPAI);
					table.updateHighlighters(10, 0, maxNSAF);
					
					tcm = table.getColumnModel();
					for (int i = 3; i < 11; i++) {
						((TableColumnExt2) tcm.getColumn(i)).aggregate();
					}

				}
			}

			// Update taxonomy filter selection dialog
			TaxonomySelectionDialog.getInstance().setRoot(
					(SortableCheckBoxTreeTableNode) protTaxonTreeTbl.getTreeTableModel().getRoot());
			
			Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES FINISHED");
			
			protTaxonTreeTbl.expandAll();
			protEnzymeTreeTbl.expandAll();
			protPathwayTreeTbl.expandAll();
			protFlatTreeTbl.expandAll();
		}

	}

	/**
	 * Method to refresh peptide table contents and sequence coverage viewer.
	 */
	protected void refreshPeptideViews(int protRow) {
		// Clear existing contents
		TableConfig.clearTable(peptideTbl);
		coveragePnl.removeAll();

		if (protRow != -1) {
			// Get protein and peptide information 
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(Constants.PROT_ACCESSION));
			ProteinHit proteinHit = dbSearchResult.getProteinHits().get(accession);
			String sequence = proteinHit.getSequence();
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();
			pepTtlPnl.setTitle("Peptides (" + peptideHits.size() + ")");

			// Iterate peptide hit list to fill table and build coverage view
			List<Interval> peptideIntervals = new ArrayList<Interval>(peptideHits.size());
			DefaultTableModel peptideTblMdl = (DefaultTableModel) peptideTbl.getModel();
			int row = 0, maxProtCount = 0, maxSpecCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				// Find occurences of peptide sequences in protein sequence
				String pepSeq = peptideHit.getSequence();
				int index = -1;
				while (true) {
					index = sequence.indexOf(pepSeq, index + 1);
					if (index == -1) break;
					// Store position of peptide sequence match in interval object
					Interval interval = new Interval(index, index + pepSeq.length(), row++);
					peptideIntervals.add(interval);
				}
				// Determine maximum spectral count
				int protCount = peptideHit.getProteinCount();
				int specCount = peptideHit.getSpectralCount();
				maxProtCount = Math.max(maxProtCount, protCount);
				maxSpecCount = Math.max(maxSpecCount, specCount);
				// Add table row
				TaxonomyNode taxonNode = peptideHit.getTaxonomyNode();
				String taxonName = "";
				if (taxonNode != null) {
					taxonName += taxonNode.getName() + " (" + taxonNode.getId() + ")";
				}

				peptideTblMdl.addRow(new Object[] {
						peptideHit.isSelected(),
						row,
						peptideHit.getSequence(),
						protCount,
						specCount,
						taxonName
				});
			}

			// Update table highlighters
			FontMetrics fm = getFontMetrics(chartFont);
			TableColumnModel tcm = peptideTbl.getColumnModel();

			BarChartHighlighter highlighter;
			// FIXME: ArrayIndexOut...
			highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(peptideTbl.convertColumnIndexToView(PEP_PROTEINCOUNT))).getHighlighters()[0];
			highlighter.setBaseline(fm.stringWidth(highlighter.getFormatter().format(maxProtCount)));
			highlighter.setRange(0.0, maxProtCount);
			highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(peptideTbl.convertColumnIndexToView(PEP_SPECTRALCOUNT))).getHighlighters()[0];
			highlighter.setBaseline(fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
			highlighter.setRange(0.0, maxSpecCount);

			// Build coverage view
			RadioButtonAdapter rba = null;
			List<HoverLabel> hovers = null;

			// Iterate protein sequence blocks
			int blockSize = 10, length = sequence.length(), openIntervals = 0;
			for (int start = 0; start < length; start += blockSize) {
				int end = start + blockSize;
				end = (end > length) ? length : end;
				// Create upper label containing position index on light green background
				StringBuilder indexRow = new StringBuilder("<html><code>");
				int spaces = blockSize - 2 - (int) Math.floor(Math.log10(end));
				for (int i = 0; i < spaces; i++) {
					indexRow.append("&nbsp");
				}
				indexRow.append(" " + (start + blockSize));
				indexRow.append("</html></code>");

				JPanel blockPnl = new JPanel(new BorderLayout());
				blockPnl.setOpaque(false);
				JLabel indexLbl = new JLabel(indexRow.toString());
				indexLbl.setBackground(new Color(0, 255, 0, 32));
				indexLbl.setOpaque(true);
				blockPnl.add(indexLbl, BorderLayout.NORTH);

				// Create lower panel containing label-like buttons and plain labels
				JPanel subBlockPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				subBlockPnl.setOpaque(false);

				JComponent label = null;

				// Iterate characters inside block to find upper/lower interval boundaries
				String blockSeq = sequence.substring(start, end);
				int blockLength = blockSeq.length(), subIndex = 0;
				for (int i = 0; i < blockLength; i++) {
					int pos = start + i;
					boolean isBorder = false;
					// Compare all intervals' bounds with current absolute character position
					for (Interval interval : peptideIntervals) {
						if ((pos == (int) interval.getLeftBorder())) {
							// Highlightable part begins here, store contents up to this point in label
							label = new JLabel("<html><code>" + blockSeq.substring(subIndex, i) + "</code></html>");

							// Init data binding adapter to synchronize selection state of hover labels
							rba = new RadioButtonAdapter(coverageSelectionModel,
									((Integer) interval.getUserObject()).intValue());
							hovers = new ArrayList<HoverLabel>();

							isBorder = true;
							openIntervals++;
						}
						if ((pos == (int) interval.getRightBorder())) {
							// Highlightable part ends here, store contents in hover label
							label = new HoverLabel(blockSeq.substring(subIndex, i));
							((HoverLabel) label).setModel(rba);
							hovers.add((HoverLabel) label);
							// Make hover label aware of its surrounding siblings for synchronized rollover effects
							for (HoverLabel hl : hovers) {
								hl.setSiblings(hovers);
							}

							isBorder = true;
							openIntervals--;
						}
						if (isBorder) {
							// Add new label to panel, move index pointer forward
							subBlockPnl.add(label);
							subIndex = i;
						}
					}
				}
				// Store any remaining subsequences in (hover) label
				if (openIntervals > 0) {
					label = new HoverLabel(blockSeq.substring(subIndex));
					((HoverLabel) label).setModel(rba);
					hovers.add((HoverLabel) label);
				} else {
					label = new JLabel("<html><code>" + blockSeq.substring(subIndex) + "</code></html>");
				}
				subBlockPnl.add(label);

				blockPnl.add(subBlockPnl, BorderLayout.SOUTH);

				coveragePnl.add(blockPnl);
			}

			// Select first row in table
			peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
		} else {
			pepTtlPnl.setTitle("Peptides");
		}
		coveragePnl.revalidate();
	}

	/**
	 * Method to refresh PSM table contents.
	 */
	protected void refreshPsmTable() {
		TableConfig.clearTable(psmTbl);
		maxX = 0.0;

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(Constants.PROT_ACCESSION));
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
				PeptideHit peptideHit = dbSearchResult.getProteinHits().get(accession).getPeptideHits().get(sequence);

				DefaultTableModel psmTblMdl = (DefaultTableModel) psmTbl.getModel();
				int i = 1, maxVotes = 0;
				List<SpectrumMatch> spectrumMatches = peptideHit.getSpectrumMatches();
				psmTtlPnl.setTitle("Peptide-Spectrum-Matches (" + spectrumMatches.size() + ")");

				for (SpectrumMatch sm : spectrumMatches) {
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
					List<SearchHit> searchHits = psm.getSearchHits();
					double[] qValues = { 0.0, 0.0, 0.0, 0.0, 0.0 };
					for (SearchHit searchHit : searchHits) {
						switch (searchHit.getType()) {
						case XTANDEM:
							qValues[0] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case OMSSA:
							qValues[1] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case CRUX:
							qValues[2] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case INSPECT:
							qValues[3] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case MASCOT:
							qValues[4] = 0.95; break; // TODO Robbie Find Qvalue for MASCOT
						}
					}
					maxVotes = Math.max(maxVotes, psm.getVotes());
					psmTblMdl.addRow(new Object[] {
							sm.isSelected(),
							i++,
							psm.getCharge(),
							psm.getVotes(),
							qValues[0],
							qValues[1],
							qValues[2],
							qValues[3],
							qValues[4]});
				}

				FontMetrics fm = getFontMetrics(chartFont);
				TableColumnModel tcm = psmTbl.getColumnModel();

				BarChartHighlighter highlighter;

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(psmTbl.convertColumnIndexToView(PSM_VOTES))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxVotes)));
				highlighter.setRange(0.0, maxVotes);

				psmTbl.getSelectionModel().setSelectionInterval(0, 0);
			}
		} else {
			psmTtlPnl.setTitle("Peptide-Spectrum-Matches");
		}
	}

	protected void refreshPlot() {
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				int psmRow = psmTbl.getSelectedRow();
				if (psmRow != -1) {
					// clear the spectrum panel
					Container specCont = specPnl.getParent();
//					while (specCont.getComponents().length > 0) {
//						specCont.remove(0);
//		            }
					specCont.removeAll();
					// get the list of spectrum matches
					String actualAccession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(Constants.PROT_ACCESSION));
					String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
					int index = psmTbl.convertRowIndexToModel(psmRow);
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence).getSpectrumMatches().get(index);

					MascotGenericFile mgf = null;
					if (!Client.getInstance().isViewer()) {
						// Get spectrum file from database when connected
						try {
							mgf = Client.getInstance().getSpectrumBySearchSpectrumID(psm.getSearchSpectrumID());
						} catch (SQLException e) {
							JXErrorPane.showDialog(ClientFrame.getInstance(),
									new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
						}
					} else {
						// Read spectrum from MGF file accompanying imported result object, if possible
						String mgfPath = importFile.getPath();
						mgfPath = mgfPath.substring(0, mgfPath.lastIndexOf('.')) + ".mgf";
						mgf = Client.getInstance().readSpectrumFromFile(
								mgfPath, psm.getStartIndex(), psm.getEndIndex());
					}

					specPnl = new SpectrumPanel(mgf);
					specPnl.showAnnotatedPeaksOnly(true);
					specPnl.setShowResolution(false);

					maxX = Math.max(maxX, specPnl.getMaxXAxisValue());
					specPnl.rescale(0.0, maxX);

					specCont.add(specPnl);
					specCont.validate();
					specCont.repaint();

					Fragmentizer fragmentizer = new Fragmentizer(sequence, Masses.getInstance(), psm.getCharge());
					addSpectrumAnnotations(fragmentizer.getFragmentIons());

					//					// show popup
					//					showPopup(psm);
				}
			}
		} else {
			Container specCont = specPnl.getParent();
			specCont.removeAll();
			specPnl = FilePanel.createDefaultSpectrumPanel();
			specCont.add(specPnl);
			specCont.validate();
		}
	}

	//	/**
	//	 * Method to generate and show a popup containing PSM-specific details.
	//	 * @param psm
	//	 */
	//	private void showPopup(PeptideSpectrumMatch psm) {
	//		JPopupMenu popup = new JPopupMenu();
	//		
	//		JPanel panel = new JPanel(new FormLayout("p, 5dlu, p", "p, 2dlu, p, 2dlu, p, 2dlu, p"));
	//		panel.setOpaque(true);
	//		panel.setBackground(new Color(240,240,240));
	//		
	//		JLabel xTandemLbl = new JLabel("");
	//		xTandemLbl.setBorder(BorderFactory.createCompoundBorder(
	//				BorderFactory.createEmptyBorder(0, 0, 2, 0),
	//				BorderFactory.createLineBorder(Color.GREEN)));
	//
	//		JLabel omssaLbl = new JLabel("");
	//		omssaLbl.setBorder(BorderFactory.createCompoundBorder(
	//				BorderFactory.createEmptyBorder(2, 0, 2, 0),
	//				BorderFactory.createLineBorder(Color.CYAN)));
	//		JLabel cruxLbl = new JLabel("");
	//		cruxLbl.setBorder(BorderFactory.createCompoundBorder(
	//				BorderFactory.createEmptyBorder(2, 0, 2, 0),
	//				BorderFactory.createLineBorder(Color.BLUE)));
	//		JLabel inspectLbl = new JLabel("");
	//		inspectLbl.setBorder(BorderFactory.createCompoundBorder(
	//				BorderFactory.createEmptyBorder(2, 0, 0, 0),
	//				BorderFactory.createLineBorder(Color.MAGENTA)));
	//		DecimalFormat df = new DecimalFormat("0.00");
	//		for (SearchHit hit : psm.getSearchHits()) {
	//			switch (hit.getType()) {
	//			case XTANDEM:
	//				XTandemhit xtandemhit = (XTandemhit) hit;
	//				xTandemLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
	//						" | HScore: " + df.format(xtandemhit.getHyperscore().doubleValue()) + 
	//						" | E-value: "  + df.format(xtandemhit.getEvalue().doubleValue()));
	//				panel.add(new JLabel("<html><font color='#00FF00'>\u25cf</font> X!Tandem</html>"), CC.xy(1,1));
	//				panel.add(xTandemLbl, CC.xy(3, 1));
	//				break;
	//			case OMSSA:
	//				Omssahit omssahit = (Omssahit) hit;
	//				omssaLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
	//						" | P-value: " + df.format(omssahit.getPvalue().doubleValue()) + 
	//						" | E-value: "  + df.format(omssahit.getEvalue().doubleValue()));
	//				panel.add(new JLabel("<html><font color='#00FFFF'>\u25cf</font> OMSSA</html>"), CC.xy(1,3));
	//				panel.add(omssaLbl, CC.xy(3,3));
	//				break;
	//			case CRUX:
	//				Cruxhit cruxhit = (Cruxhit) hit;
	//				cruxLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
	//						" | XCorr: " + df.format(cruxhit.getXcorr_score().doubleValue()));
	//				panel.add(new JLabel("<html><font color='#0000FF'>\u25cf</font> Crux</html>"), CC.xy(1,5));
	//				panel.add(cruxLbl, CC.xy(3,5));
	//				break;
	//			case INSPECT:
	//				Inspecthit inspecthit = (Inspecthit) hit;
	//				inspectLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
	//						" | FScore: " + df.format(inspecthit.getF_score().doubleValue()) + 
	//						" | DeltaScore: "  + df.format(inspecthit.getDeltascore().doubleValue()));
	//				panel.add(new JLabel("<html><font color='#FF00FF'>\u25cf</font> InsPecT</html>"), CC.xy(1,7));
	//				panel.add(inspectLbl, CC.xy(3,7));
	//				break;
	//			default:
	//				break;
	//			}
	//		}
	//		popup.add(panel);
	////		Rectangle cellRect = psmTbl.getCellRect(psmTbl.getSelectedRow(), psmTbl.getColumnCount()-1, true);
	////		popup.show(psmTbl, cellRect.x + cellRect.width, cellRect.y - popup.getPreferredSize().height/2 + cellRect.height/2);
	//	}

	/**
	 * Method to add annotations to spectrum plot.
	 * @param fragmentIons
	 */
	protected void addSpectrumAnnotations(Map<String, FragmentIon[]> fragmentIons) {

		currentAnnotations = new Vector<SpectrumAnnotation>();
		Set<Entry<String, FragmentIon[]>> entrySet = fragmentIons.entrySet();
		int i = 0;
		for (Entry<String, FragmentIon[]> entry : entrySet) {
			FragmentIon[] ions = entry.getValue();

			for (FragmentIon ion : ions) {
				int ionNumber = ion.getNumber();
				double mzValue = ion.getMZ();
				Color color;
				if (i % 2 == 0) {
					color = Color.BLUE;
				} else {
					color = Color.BLACK;
				}

				// Use standard ion type names, such as y5++
				String ionDesc = ion.getLetter();
				if (ionNumber > 0) {
					ionDesc += ionNumber;
				}
				if (ion.getCharge() > 1) {
					for (int j = 0; j < ion.getCharge(); j++) {
						ionDesc += "+";
					}
				}
				// TODO: Get fragment ion mass error from db or settings file!
				currentAnnotations.add(new DefaultSpectrumAnnotation(mzValue, 0.5, color, ionDesc));
			}
			i++;
		}
		//allAnnotations.put(currentSelectedPeptide, currentAnnotations);

		if(currentAnnotations.size() > 0 ){
			specPnl.setAnnotations(filterAnnotations(currentAnnotations));
			specPnl.validate();
			specPnl.repaint();
		}

	}

	/**
	 * This method updates the filtered annotations due to the respective selected checkboxes.
	 */
	protected void updateFilteredAnnotations() {
		if (currentAnnotations != null) {
			specPnl.setAnnotations(filterAnnotations(currentAnnotations));
			specPnl.validate();
			specPnl.repaint();
		}
	}

	/**
	 * This method filters the annotations.
	 *
	 * @param annotations the annotations to be filtered
	 * @return the filtered annotations
	 */
	protected Vector<SpectrumAnnotation> filterAnnotations(Vector<SpectrumAnnotation> annotations) {

		Vector<SpectrumAnnotation> filteredAnnotations = new Vector<SpectrumAnnotation>();
		
		// gather component indexes of toggle buttons in spectrum filter button
		// panel and associated token identifiers
		int[] ionToggles = new int[] { 0, 1, 2, 4, 5, 6, 15 };
		String ionTokens = "abcxyzM";
		int[] miscToggles = new int[] { 8, 9, 11, 12, 13 };
		String[] miscTokens = new String[]  { "\u00B0", "*", "+", "++", "+++" };

		for (SpectrumAnnotation annotation : annotations) {
			String currentLabel = annotation.getLabel();
			boolean useAnnotation = true;
			// check ion type
			for (int i = 0; i < ionToggles.length; i++) {
				if (currentLabel.lastIndexOf(ionTokens.charAt(i)) != -1) {
					useAnnotation &= ((AbstractButton) specFiltPnl.getComponent(ionToggles[i])).isSelected();
				}
				if (!useAnnotation) break;
			}

			// check ion charge + ammonium and water-loss
			if (useAnnotation) {
				for (int i = 0; i < miscToggles.length; i++) {
					if (currentLabel.lastIndexOf(miscTokens[i]) != -1) {
						useAnnotation &= ((AbstractButton) specFiltPnl.getComponent(miscToggles[i])).isSelected();
					}
					if (!useAnnotation) break;
				}
			}

			// If not used, don't add the annotation.
			if (useAnnotation) {
				filteredAnnotations.add(annotation);
			}
		}

		return filteredAnnotations;
	}

//	/**
//	 * This method sets the enabled state of the get results button.
//	 */
//	public void setResultsFromDbButtonEnabled(boolean enabled) {
//		getResultsFromDbBtn.setEnabled(enabled);
//	}

	@Override
	public boolean isBusy() {
		// we don't need this (yet)
		return false;
	}

	@Override
	public void setBusy(boolean busy) {
//		this.busy = busy;
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		clientFrame.setCursor(cursor);
		split.setCursor(cursor);
		
		ButtonTabbedPane tabPane = (ButtonTabbedPane) this.getParent();
		int index = tabPane.indexOfComponent(this);
		tabPane.setBusyAt(index, busy);
		tabPane.setEnabledAt(index, !busy);
	}

}