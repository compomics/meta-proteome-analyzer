package de.mpa.client.ui.panels;

import jaligner.SmithWatermanGotoh;

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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.PrimaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;

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
import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ClientFrameMenuBar;
import de.mpa.client.ui.ComponentHeader;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.InstantToolTipMouseListener;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.PhylogenyTreeTableNode;
import de.mpa.client.ui.RoundedHoverButtonUI;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.TriStateCheckBox;
import de.mpa.client.ui.WrapLayout;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;
import de.mpa.client.ui.dialogs.FilterBalloonTip;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.SearchHit;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;
import de.mpa.main.Parameters;
import de.mpa.parser.ec.ECEntry;
import de.mpa.taxonomy.NcbiTaxonomy;
import de.mpa.taxonomy.TaxonomyNode;
import de.mpa.util.ColorUtils;

/**
 * Panel implementation for viewing of database search results.
 * 
 * @author A. Behne, T. Muth, R. Heyer, F. Kohrs
 */
public class DbSearchResultPanel extends JPanel {
	
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
	 * Multi split pane containing all sub-panels.
	 */
	private JXMultiSplitPane split;

	/**
	 * Flag denoting whether the application shall currently appear busy.
	 */
	private boolean busy;
	
	/*
	 * Toggle buttons for ion annotations in spectrum plot 
	 */
	private JToggleButton aIonsTgl;
	private JToggleButton bIonsTgl;
	private JToggleButton cIonsTgl;
	private JToggleButton yIonsTgl;
	private JToggleButton xIonsTgl;
	private JToggleButton zIonsTgl;
	private JToggleButton waterLossTgl;
	private JToggleButton ammoniumLossTgl;
	private JToggleButton chargeOneTgl;
	private JToggleButton chargeTwoTgl;
	private JToggleButton chargeMoreTgl;
	private JToggleButton precursorTgl;
	
	/**
	 * The currently displayable spectrum annotations.
	 */
	private Vector<SpectrumAnnotation> currentAnnotations;

	/*
	 * Table widget-related variables
	 */
	FilterBalloonTip filterTip;
	protected FilterButton lastSelectedFilterBtn = new FilterButton(0, null,0);
	
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
	 * Flag indicating whether checkbox selections inside tree tables are currently 
	 * in the middle of being synched programmatically.
	 */
	private boolean synching = false;

	/**
	 * The button to show a context menu for selecting which hierarchical protein view to display.
	 */
	private JToggleButton hierarchyBtn;

	/**
	 * The button to query search results from the remote database and refresh detail views.
	 */
	private JButton getResultsFromDbBtn;

	/**
	 * The button to query search results from a specified file and refresh detail views.
	 */
	private JButton getResultsFromFileBtn;
	
	/**
	 * The parent results panel.
	 */
	private ResultsPanel parent;

	/**
	 * Highlighter predicate.
	 */
	private ProtCHighlighterPredicate protCHighlightPredicate;

	/**
	 * Import file.
	 */
	protected File importFile;

	private CardLayout protCardLyt;

	private String[] cardLabels;

	private JPanel protCardPnl;
	

	/**
	 * Constructor for a database results panel.
	 * @param clientFrame The parent frame.
	 */
	public DbSearchResultPanel(ResultsPanel parent) {
		this.clientFrame = ClientFrame.getInstance();
		this.parent = parent;
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
		chartFont = UIManager.getFont("Label.font");
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
		cardLabels = new String[] {"Original", "Flat View",
				"Taxonomic View", "E.C. View", "Pathway View"};
		
		protCardLyt = new CardLayout();
		protCardPnl = new JPanel(protCardLyt);
		protCardPnl.add(proteinTableScp, cardLabels[0]);
		
		setupProteinTreeTables();
		int i = 1;
		for (CheckBoxTreeTable cbtt : linkMap.values()) {
			JScrollPane scrollPane = new JScrollPane(cbtt);
			scrollPane.setPreferredSize(new Dimension(800, 150));
			protCardPnl.add(scrollPane, cardLabels[i++]);
		}
		
		proteinPnl.add(protCardPnl, CC.xy(2, 2));
		
		hierarchyBtn = new JToggleButton(IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ICON));
		hierarchyBtn.setRolloverIcon(IconConstants.createArrowedIcon(IconConstants.HIERARCHY_ROLLOVER_ICON));
		hierarchyBtn.setPressedIcon(IconConstants.createArrowedIcon(IconConstants.HIERARCHY_PRESSED_ICON));
		hierarchyBtn.setToolTipText("Select Hierarchical View");
		
		hierarchyBtn.setUI(new RoundedHoverButtonUI());

		hierarchyBtn.setOpaque(false);
		hierarchyBtn.setBorderPainted(false);
		hierarchyBtn.setMargin(new Insets(1, 0, 0, 0));
		
		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
		hierarchyBtn.addMouseListener(ittml);
		
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
		
		getResultsFromDbBtn = new JButton(IconConstants.GO_DB_SMALL_ICON);
		getResultsFromDbBtn.setRolloverIcon(IconConstants.GO_DB_SMALL_ROLLOVER_ICON);
		getResultsFromDbBtn.setPressedIcon(IconConstants.GO_DB_SMALL_PRESSED_ICON);
		getResultsFromDbBtn.setToolTipText("Get Results from DB");
		
		getResultsFromDbBtn.setUI(new RoundedHoverButtonUI());

		getResultsFromDbBtn.setOpaque(false);
		getResultsFromDbBtn.setBorderPainted(false);
		getResultsFromDbBtn.setMargin(new Insets(1, 0, 0, 0));
		
		getResultsFromDbBtn.addMouseListener(ittml);
		
		getResultsFromDbBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fetchResultsFromDatabase();
			}
		});
		getResultsFromDbBtn.setEnabled(false);
		
		getResultsFromFileBtn = new JButton(IconConstants.GO_PAGE_ICON);
		getResultsFromFileBtn.setRolloverIcon(IconConstants.GO_PAGE_ROLLOVER_ICON);
		getResultsFromFileBtn.setPressedIcon(IconConstants.GO_PAGE_PRESSED_ICON);
		getResultsFromFileBtn.setToolTipText("Get Results from File");
		
		getResultsFromFileBtn.setUI(new RoundedHoverButtonUI());

		getResultsFromFileBtn.setOpaque(false);
		getResultsFromFileBtn.setBorderPainted(false);
		getResultsFromFileBtn.setMargin(new Insets(1, 0, 0, 0));
		
		getResultsFromFileBtn.addMouseListener(ittml);
		
		getResultsFromFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fetchResultsFromFile();
			}
		});
		
		JPanel protBtnPnl = new JPanel(new FormLayout("p, 2dlu, 36px, c:5dlu, p, 2dlu, p, 1px", "0px, f:p:g, 0px"));
		protBtnPnl.setOpaque(false);
		
		protBtnPnl.add(hierarchyBtn, CC.xy(3, 2));
//		protBtnPnl.add(hierarchyCbx, CC.xy(3, 2));
		protBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(4, 2));
		protBtnPnl.add(getResultsFromDbBtn, CC.xy(5, 2));
		protBtnPnl.add(getResultsFromFileBtn, CC.xy(7, 2));
		
		// XXX: only for testing purposes, please remove when appropriate
		final JTextField testTtf = new JTextField(20);
		testTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) { filter(e); }
			public void insertUpdate(DocumentEvent e) { filter(e); }
			public void changedUpdate(DocumentEvent e) { filter(e); }
			private void filter(DocumentEvent e) {
				RowFilter<Object, Object> rowFilter = null;
				String pattern = testTtf.getText();
				if (!pattern.isEmpty()) {
					try {
						rowFilter = RowFilter.regexFilter(pattern, 1);
						testTtf.setForeground(Color.BLACK);
					} catch (PatternSyntaxException pse) {
						testTtf.setForeground(Color.RED);
					}
				}
				protFlatTreeTbl.setRowFilter(rowFilter);
				protTaxonTreeTbl.setRowFilter(rowFilter);
				protEnzymeTreeTbl.setRowFilter(rowFilter);
//				protPathwayTreeTbl.setRowFilter(rowFilter);
			}
		});
		testTtf.setBorder(BorderFactory.createCompoundBorder(testTtf.getBorder(),
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 16, 0, 0,
						new ImageIcon(getClass().getResource("/de/mpa/resources/icons/filter16.png"))),
						BorderFactory.createEmptyBorder(0, 3, 0, 0))));
		
//		protBtnPnl.add(testTtf, CC.xy(1, 2));
		
//		JButton testBtn = new JButton("test");
//		testBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.out.println(protFlatTreeTbl.getCellRect(1, 0, false));
//			}
//		});
//		protBtnPnl.add(testBtn, CC.xy(5, 1));
		
		protTtlPnl = new JXTitledPanel("Proteins", proteinPnl);
//		protTtlPnl.setRightDecoration(getResultsBtn);
		protTtlPnl.setRightDecoration(protBtnPnl);
		protTtlPnl.setTitleFont(ttlFont);
		protTtlPnl.setTitlePainter(ttlPainter);
		protTtlPnl.setBorder(ttlBorder);
				
		// Peptide panel
		final CardLayout cl = new CardLayout();
		final JPanel peptidePnl = new JPanel(cl);
		
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
				cl.previous(peptidePnl);
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
		
		// Build the spectrum overview panel
		JPanel spectrumOverviewPnl = new JPanel(new BorderLayout());
		
		JPanel specCont = new JPanel();
		specCont.setLayout(new BoxLayout(specCont, BoxLayout.LINE_AXIS));
		specCont.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
		specCont.add(specPnl = FilePanel.createDefaultSpectrumPanel());
		specCont.setMinimumSize(new Dimension(200, 200));
		
		spectrumOverviewPnl.add(specCont, BorderLayout.CENTER);
		spectrumOverviewPnl.add(constructSpectrumFilterPanel(), BorderLayout.EAST);
		// FIXME: put spectrum panel in chart panel
		
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", spectrumOverviewPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		JXTitledPanel chartTtlPnl = createChartPanel();
		
		// Set up multi-split pane
		String layoutDef =
			"(COLUMN (LEAF weight=0.35 name=protein) (ROW weight=0.65 (COLUMN weight=0.35 (LEAF weight=0.5 name=peptide) (LEAF weight=0.5 name=psm)) (LEAF weight=0.65 name=chart)))";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		MultiSplitLayout msl = new MultiSplitLayout(modelRoot);
		msl.setLayoutByWeight(true);
		
		split = new JXMultiSplitPane(msl) {
			@Override
			public void setCursor(Cursor cursor) {
				if (busy) {
					if ((cursor == null) || (cursor.getType() == Cursor.DEFAULT_CURSOR)) {
						cursor = clientFrame.getCursor();
					}
				}
				super.setCursor(cursor);
			}
		};
		split.setDividerSize(12);
		
		split.add(protTtlPnl, "protein");
		split.add(pepTtlPnl, "peptide");
		split.add(psmTtlPnl, "psm");
//		split.add(specTtlPnl, "plot");
		split.add(specTtlPnl, "chart");
		
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
	 * Class to fetch protein database search results in a background thread.
	 * It's possible to fetch from the remote SQL database or from a local file instance.
	 * 
	 * @author A. Behne, R. Heyer
	 */
	private class ResultsTask extends SwingWorker {
		
		/**
		 * The file object reference for when file-based results shall be read.
		 */
		private File file;

		/**
		 * Constructs a task instance.
		 * @param file
		 *	the file instance from which results shall be fetched. If
		 *	<code>null</code> the results will be fetched from the SQL
		 *	database.
		 */
		public ResultsTask(File file) {
			this.file = file;
		}
		@Override
		protected Object doInBackground() {
			try {
				// Begin appearing busy
				setBusy(true);
				
				// Fetch the database search result object
				DbSearchResult newResult;
				Client client = Client.getInstance();
				if (file == null) {
					newResult = client.getDbSearchResult(
							clientFrame.getProjectPanel().getCurrentProjectContent(), 
							clientFrame.getProjectPanel().getCurrentExperimentContent());
				} else {
					client.firePropertyChange("new message", null, "READING RESULTS FILE");
					client.firePropertyChange("resetall", 0L, 100L);
					client.firePropertyChange("indeterminate", false, true);
					ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
							new GZIPInputStream(new FileInputStream(file))));
					newResult = (DbSearchResult) ois.readObject();
					ois.close();
					client.setDbSearchResult(newResult);
					client.firePropertyChange("new message", null, "READING RESULTS FILE FINISHED");
					client.firePropertyChange("indeterminate", true, false);
				}
				
				// Fetching UniProt entries and taxonomic information.
				if (!newResult.equals(dbSearchResult)) {
					if (!newResult.isEmpty() && !client.isViewer()) {
						// Retrieve UniProt data
						client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES");
						client.firePropertyChange("indeterminate", false, true);
						try {
							UniprotAccessor.retrieveUniProtEntries(newResult);
						} catch (Exception e) {
							JXErrorPane.showDialog(clientFrame, new ErrorInfo("Error",
									"UniProt access failed. Please try again later.",
									e.getMessage(), null, e, Level.SEVERE, null));
						}
						
						client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES FINISHED");
						client.firePropertyChange("indeterminate", true, false);
						
						// Get various hit lists from result object
						ProteinHitList metaProteinList = newResult.getMetaProteins();
						ProteinHitList proteinList = (ProteinHitList) newResult.getProteinHitList();
						Set<PeptideHit> peptideSet = proteinList.getPeptideSet();	// all distinct peptides
						
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY");
						Client.getInstance().firePropertyChange("resetall", -1L, (long) (peptideSet.size() + proteinList.size() + metaProteinList.size()));
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) peptideSet.size());
						
						// Determine peptide taxonomy
						NcbiTaxonomy ncbiTaxonomy = NcbiTaxonomy.getInstance();
						Map<Integer, TaxonomyNode> nodeMap = new HashMap<Integer, TaxonomyNode>();
						nodeMap.put(1, NcbiTaxonomy.ROOT_NODE);
						for (PeptideHit peptideHit : peptideSet) {
							// gather protein taxonomy nodes
							List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
							for (ProteinHit proteinHit : peptideHit.getProteinHits()) {
								taxonNodes.add(proteinHit.getTaxonomyNode());
							}
							// find common ancestor node
							TaxonomyNode ancestor = taxonNodes.get(0);
							for (int i = 0; i < taxonNodes.size(); i++) {
								ancestor = ncbiTaxonomy.createCommonTaxonomyNode(ancestor.getId(), taxonNodes.get(i).getId());
							}
							
							TaxonomyNode child = ancestor;
							TaxonomyNode parent = nodeMap.get(ancestor.getId());
							if (parent == null) {
//								newNode = ancestor;
//								while (!newNode.isRoot()) {
//									oldNode = newNode;
//									newNode = nodeMap.get(oldNode.getId());
//									if (newNode == null) {
//										nodeMap.put(oldNode.getId(), oldNode);
//										newNode = ncbiTaxonomy.getParentTaxonomyNode(oldNode);
//										TaxonomyNode temp = nodeMap.get(newNode.getId());
//										if (temp != null) {
//											newNode = temp;
//										}
//										oldNode.setParentNode(newNode);
//									} else {
//										oldNode.setParentNode(newNode);
//										break;
//									}
//								}

								parent = ncbiTaxonomy.getParentTaxonomyNode(child);
								while (true) {
									TaxonomyNode temp = nodeMap.get(parent.getId());
									if (temp == null) {
										child.setParentNode(parent);
										nodeMap.put(parent.getId(), parent);
										child = parent;
										parent = ncbiTaxonomy.getParentTaxonomyNode(parent);
									} else {
										child.setParentNode(temp);
										break;
									}
								}
							} else {
								ancestor = parent;
							}
							
							// set peptide hit taxon node to ancestor
							peptideHit.setTaxonomyNode(ancestor);
							
							// possible TODO: determine spectrum taxonomy instead of inheriting directly from peptide
							for (SpectrumMatch match : peptideHit.getSpectrumMatches()) {
								match.setTaxonomyNode(ancestor);
							}
							// fire progress notification
							Client.getInstance().firePropertyChange("progressmade", false, true);
						}
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY FINISHED");

						// Determine protein taxonomy
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY");
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) proteinList.size());
						
						for (ProteinHit proteinHit : proteinList) {
							// gather protein taxonomy nodes
							List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
							for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
								taxonNodes.add(peptideHit.getTaxonomyNode());
							}
							// find common ancestor node
							TaxonomyNode ancestor = taxonNodes.get(0);
							for (int i = 1; i < taxonNodes.size(); i++) {
								ancestor = ncbiTaxonomy.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
							}
							// set peptide hit taxon node to ancestor
							proteinHit.setTaxonomyNode(ancestor);
							// fire progress notification
							Client.getInstance().firePropertyChange("progressmade", false, true);
						}
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY FINISHED");
						
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY");
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) metaProteinList.size());
						
						// Combine protein hits to metaproteins if they share all proteins
						Iterator<ProteinHit> rowIter = metaProteinList.iterator();
						while (rowIter.hasNext()) {
							MetaProteinHit rowMP = (MetaProteinHit) rowIter.next();
							Set<PeptideHit> rowPS = rowMP.getPeptideSet();
							ListIterator<ProteinHit> colIter = metaProteinList.listIterator(metaProteinList.size());
							// Start to iterate beginning from the end to improve cpu time.
							while (colIter.hasPrevious()) {
								MetaProteinHit colMP = (MetaProteinHit) colIter.previous();
								if (rowMP == colMP) {
									break;
								}
								Set<PeptideHit> colPS = colMP.getPeptideSet();
//								if (colPS.containsAll(rowPS) || rowPS.containsAll(colPS)) {
								if (!Collections.disjoint(colPS, rowPS)) {
									colMP.addAll(rowMP.getProteinHits());
									rowIter.remove();
									Client.getInstance().firePropertyChange("progressmade", false, true);
									break;
								}
							}
						}
						
						int metaIndex = 1;
						for (ProteinHit metaProteinHit : metaProteinList) {
							// rename meta-protein
							metaProteinHit.setAccession("Meta-Protein " + metaIndex++);
							// gather protein taxonomy nodes
							List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
							for (ProteinHit proteinHit : ((MetaProteinHit) metaProteinHit).getProteinHits()) {
								taxonNodes.add(proteinHit.getTaxonomyNode());
							}
							// find common ancestor node
							TaxonomyNode ancestor = taxonNodes.get(0);
							for (int i = 0; i < taxonNodes.size(); i++) {
								ancestor = ncbiTaxonomy.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
							}
							// set peptide hit taxon node to ancestor
							metaProteinHit.setTaxonomyNode(ancestor);
							// fire progress notification
							Client.getInstance().firePropertyChange("progressmade", false, true);
						}
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY FINISHED");
					}
					// Update result object reference
					dbSearchResult = newResult;

					// Update overview panel
					parent.updateOverview();
					
					// Populate tables
					refreshProteinTables();
					
					// Enable export functionality
					((ClientFrameMenuBar) clientFrame.getJMenuBar()).setExportResultsEnabled(true);
					// Enable Save Project functionality
					((ClientFrameMenuBar) clientFrame.getJMenuBar()).setSaveprojectFunctionalityEnabled(true);
				}
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				Client.getInstance().firePropertyChange("new message", null, "FAILED");
				Client.getInstance().firePropertyChange("indeterminate", true, false);

			}
			return 0;
		}
		

		@Override
		protected void done() {
			// Stop appearing busy
			setBusy(false);
			
			chartTypeBtn.setEnabled(true);
			ontologyData = new OntologyData(dbSearchResult);
			taxonomyData = new TaxonomyData(dbSearchResult);
			updateChart(chartType);
			
			// Enable export functionality
			((ClientFrameMenuBar) clientFrame.getJMenuBar()).setExportResultsEnabled(true);
			// Enable Save Project functionality
			((ClientFrameMenuBar) clientFrame.getJMenuBar()).setSaveprojectFunctionalityEnabled(true);
		}
		
	}

	// Protein table column indices
	// FIXME: Find another (more elegant) solution for this here:
	private final int PROT_SELECTION 		= 0;
	private final int PROT_INDEX 			= 1;
	private final int PROT_ACCESSION 		= 2;
	private final int PROT_DESCRIPTION 		= 3;
	private final int PROT_SPECIES 			= 4;
	private final int PROT_COVERAGE 		= 5;
	private final int PROT_MW 				= 6;
	private final int PROT_PI 				= 7;
	private final int PROT_PEPTIDECOUNT 	= 8;
	private final int PROT_SPECTRALCOUNT 	= 9;
	private final int PROT_EMPAI 			= 10;
	private final int PROT_NSAF 			= 11;

	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		// Init column selection checkbox header widget
		final JCheckBox selChk = new TriStateCheckBox(2, -1) {
			@Override
			public boolean isPartiallySelected() {
				
//				if (dbSearchResult != null) {
//					List<ProteinHit> hitList = dbSearchResult.getProteinHitList();
//					boolean res = hitList.isEmpty();
//					if (!res) {
//						res = hitList.get(0).isSelected();
//						for (int i = 1; i < hitList.size(); i++) {
//							boolean sel = hitList.get(i).isSelected();
//							if (res != sel) {
//								return true;
//							}
//							res = sel;
//						}
//					}
//				}
				return false;
			}
		};
			
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);
		selChk.setOpaque(false);
		selChk.setBackground(new Color(0, 0, 0, 0));
		
		// Create protein table model
		final TableModel proteinTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { "", "#", "Accession",
						"Description", "Species", "SC", "MW", "pI", "PepC",
						"SpC", "emPAI", "NSAF" });
			}
	
			public boolean isCellEditable(int row, int col) {
				return (col == PROT_SELECTION);
			}
			
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PROT_SELECTION: 
					return Boolean.class;
				case PROT_INDEX:	
				case PROT_PEPTIDECOUNT:
				case PROT_SPECTRALCOUNT:
					return Integer.class;
				case PROT_COVERAGE:
				case PROT_MW:
				case PROT_PI:
				case PROT_EMPAI:
				case PROT_NSAF:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		proteinTbl = new JXTable(proteinTblMdl) {
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
				if (column == PROT_SELECTION) {
					boolean selected = (Boolean) aValue;
					Map<String, ProteinHit> proteinHits = dbSearchResult.getProteinHits();
					String accession = (String) getValueAt(row, PROT_ACCESSION);
					ProteinHit hit = proteinHits.get(accession);
					hit.setSelected(selected);
					if (selected) {
						for (ProteinHit ph : dbSearchResult.getProteinHitList()) {
							selected &= ph.isSelected();
							if (!selected) break;
						}
					}
					selChk.setSelected(selected);
					getTableHeader().repaint(getTableHeader().getHeaderRect(PROT_SELECTION));
				}
			}
		};
		
		// Adjust column widths
		TableConfig.setColumnWidths(proteinTbl, new double[] { 0, 2.5, 5.5, 15, 14, 5, 4, 3, 4, 4, 4.5, 5 });
		TableConfig.setColumnMinWidths(proteinTbl,
				UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(),
				createFilterButton(0, null, 0).getPreferredSize().width + 8);
		
		// Get table column model
		final TableColumnModel tcm = proteinTbl.getColumnModel();

		// Apply component header capabilities as well as column header tooltips
		String[] columnToolTips = {
			    "Selection for Export",
			    "Result Index",
			    "Protein Accession",
			    "Protein Description",
			    "Species",
			    "Sequence Coverage in %",
			    "Molecular Weight in kDa",
			    "Isoelectric Point",
			    "Peptide Count",
			    "Spectral Count",
			    "Exponentially Modified Protein Abundance Index",
			    "Normalized Spectral Abundance Factor"};
		ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
//		ch.setReorderingAllowed(false, PROT_SELECTION);
		proteinTbl.setTableHeader(ch);

		// Add filter button and checkbox widgets to column headers
		for (int col = PROT_SELECTION; col < tcm.getColumnCount(); col++) {
			switch (col) {
			case PROT_SELECTION:
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null, SwingConstants.TRAILING));
				tcm.getColumn(col).setMinWidth(19);
				tcm.getColumn(col).setMaxWidth(19);
				break;
			case PROT_INDEX:
			case PROT_COVERAGE:
			case PROT_MW:
			case PROT_PI:
			case PROT_PEPTIDECOUNT:
			case PROT_SPECTRALCOUNT:
			case PROT_EMPAI:
			case PROT_NSAF:
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.NUMERIC)) {
					protected SortKey getSortKey(JTable table, int column) {
						return table.getRowSorter().getSortKeys().get(1);
					}
				});
				break;
			case PROT_ACCESSION: 
			case PROT_DESCRIPTION:
			case PROT_SPECIES: 
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.ALPHANUMERIC)) {
					protected SortKey getSortKey(JTable table, int column) {
						return table.getRowSorter().getSortKeys().get(1);
					}
				});
				break;
			}
		}
		
		// Apply custom cell renderers/highlighters to columns
		tcm.getColumn(PROT_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
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
					UniProtEntry uniprotEntry = dbSearchResult.getProteinHit(target).getUniprotEntry();
					// change target to UniProt accession inside associated UniProtEntry
					if (uniprotEntry != null) {
						target = uniprotEntry.getPrimaryUniProtAccession().getValue();
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
		tcm.getColumn(PROT_ACCESSION).setCellRenderer(new DefaultTableRenderer(hlp) {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JXRendererHyperlink comp = (JXRendererHyperlink) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
				// when no target is specified replace hyperlink with a simple label
				if (linkAction.getTarget() == null) {
					JLabel label = new JLabel(comp.getText(), SwingConstants.CENTER);
					label.setOpaque(true);
					label.setBackground(comp.getBackground());
					return label;
				}
				return comp;
			}
		});
		tcm.getColumn(PROT_DESCRIPTION).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		tcm.getColumn(PROT_SPECIES).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(PROT_COVERAGE)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN, x100formatter));
		tcm.getColumn(PROT_MW).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		tcm.getColumn(PROT_PI).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.00"));
		((TableColumnExt) tcm.getColumn(PROT_PEPTIDECOUNT)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(PROT_SPECTRALCOUNT)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(PROT_EMPAI)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00")));
		((TableColumnExt) tcm.getColumn(PROT_NSAF)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00000")));
		
		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PROT_SELECTION, SortOrder.DESCENDING);
		
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
					proteinTbl.setValueAt(selected, row, PROT_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});
		
		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PROT_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PROT_SPECTRALCOUNT, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);
		
		// Prevent messing with sort order of selection column
		tsc.setSortable(PROT_SELECTION, false);
		
		// Register list selection listener
		proteinTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				int protRow = proteinTbl.getSelectedRow();
				refreshPeptideViews(protRow);
//				refreshCoverageViewer();
			}
		});
		
		// Only one row is selectable
		proteinTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		proteinTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		protCHighlightPredicate = new ProtCHighlighterPredicate();
		Color selCol = UIManager.getColor("Table.selectionBackground");
		selCol = new Color(232, 122, 0, 41);
		Highlighter protCountHighlighter = new ColorHighlighter(protCHighlightPredicate, selCol, null);
		proteinTbl.addHighlighter(protCountHighlighter);
		
		// Enables column control
		proteinTbl.setColumnControlVisible(true);
		proteinTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		proteinTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) proteinTbl.getColumnControl()).setAdditionalActionsVisible(false);
	}
	
	/**
	 * Initializes all hierarchical protein tree table views
	 */
	private void setupProteinTreeTables() {
		protFlatTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Flat View"));
		
		protTaxonTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Taxonomic View"));
		TableConfig.setColumnWidths(protTaxonTreeTbl, new double[] { 20.25, 22, 0, 4, 5, 4, 3, 4, 4, 4.5, 5 });
		protTaxonTreeTbl.getColumnExt("Species").setVisible(false);
		
		protEnzymeTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of E.C. View"));
		
		protPathwayTreeTbl = createTreeTable(new PhylogenyTreeTableNode("Root of Pathway View"));
		
		linkNodes(new TreePath(protFlatTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protTaxonTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protEnzymeTreeTbl.getTreeTableModel().getRoot()),
				new TreePath(protPathwayTreeTbl.getTreeTableModel().getRoot()));
	}
	
	/**
	 * Creates and returns a protein tree table anchored to the specified root node.
	 * @param root the root node of the tree table
	 * @return he generated tree table
	 */
	private SortableCheckBoxTreeTable createTreeTable(final SortableCheckBoxTreeTableNode root) {
		
		// Set up table model
		SortableTreeTableModel treeTblMdl = new SortableTreeTableModel(root) {
			// Define column header strings
			{ setColumnIdentifiers(Arrays.asList(
					new String[] { "Accession", "Description", "Species", "AId", "SC",
							"MW", "pI", "PepC", "SpC", "emPAI", "NSAF" } ));
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
		
		// Create table from model; make only hierarchical column editable (for checkboxes)
		// TODO: make this default behavior of class?
		// TODO: handle editability via column model
		final SortableCheckBoxTreeTable treeTbl = new SortableCheckBoxTreeTable(treeTblMdl) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column == getHierarchicalColumn());
			}
			@Override
			public String getToolTipText(MouseEvent me) {
				return getTableToolTipText(me);
			}
		};
		
		// Pre-select root node
		final TreePath rootPath = new TreePath(root);
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(rootPath);
		
		// Toggle default sort order (spectral count, descending)
		treeTbl.getRowSorter().toggleSortOrder(7);
		treeTbl.getRowSorter().toggleSortOrder(7);
		
		// Synchronize selection with original view table
		// TODO: find proper way to synchronize selections when original view will be removed in the future 
		treeTbl.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				PhylogenyTreeTableNode node = ((PhylogenyTreeTableNode) tse.getPath().getLastPathComponent());
				if (node.isProtein()) {
					String accession = ((ProteinHit) node.getUserObject()).getAccession();
					for (int row = 0; row < proteinTbl.getRowCount(); row++) {
						if (proteinTbl.getValueAt(row, PROT_ACCESSION).equals(accession)) {
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
					TreePath[] paths = tse.getPaths();
					for (int i = 0; i < paths.length; i++) {
						PhylogenyTreeTableNode node =
							(PhylogenyTreeTableNode) paths[i].getLastPathComponent();
						if (node.hasLinks()) {
							// leaf or root node was selected/deselected -> invoke synch directly
							synching = true;
							synchLinks(node, tse.isAddedPath());
							synching = false;
						} else {
							// intermediate node was selected/deselected -> traverse subtree, synch leaves
							Enumeration<TreeNode> below = node.depthFirstEnumeration();
							while (below.hasMoreElements()) {
								PhylogenyTreeTableNode belowNode =
									(PhylogenyTreeTableNode) below.nextElement();
								if (belowNode.isLeaf()) {
									synching = true;
									synchLinks(belowNode, tse.isAddedPath());
									synching = false;
								}
							}
						}
						if (node.isProtein()) {
							ProteinHit proteinHit = (ProteinHit) node.getUserObject();
							proteinHit.setSelected(cbtsm.isPathSelected(paths[i], true));
						}
					}
				}
			}
			/**
			 * Traverses list of links and updates the corresponding selection models of 
			 * the trees identified by the links' first path elements (i.e. the roots).
			 * @param node The node whose links shall be updated
			 * @param added <code>true</code> if the selection was added, <code>false
			 * </code> otherwise
			 */
			private void synchLinks(PhylogenyTreeTableNode node, boolean added) {
				List<TreePath> links = node.getLinks();
				if (links != null) {
					// Iterate leaf links
					for (TreePath link : links) {
						// Get the appropriate selection model identified by the root name
						String rootName = link.getPathComponent(0).toString();
						CheckBoxTreeSelectionModel tsm =
							linkMap.get(rootName).getCheckBoxTreeSelectionModel();
						// Change foreign selection accordingly
						if (added) {
							tsm.addSelectionPath(link);
						} else {
							tsm.removeSelectionPath(link);
						}
					}
				}
			}
		});
		
		// Install component header
		TableColumnModel tcm = treeTbl.getColumnModel();
		String[] columnToolTips = {
			    "Protein Accession",
			    "Protein Description",
			    "Species",
			    "Alignment Identity",
			    "Sequence Coverage in %",
			    "Molecular Weight in kDa",
			    "Isoelectric Point",
			    "Peptide Count",
			    "Spectral Count",
			    "Exponentially Modified Protein Abundance Index",
			    "Normalized Spectral Abundance Factor"
			    };
		final ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
		treeTbl.setTableHeader(ch);
		
		// Install mouse listeners in header for right-click popup capabilities
		MouseAdapter mouseAdapter = new MouseAdapter() {
			private int col = -1;
			
			private JPopupMenu testPopup = new JPopupMenu() {
				@Override
				public void setVisible(boolean b) {
					if (!b) {
						raise();
					}
					super.setVisible(b);
				}
			};
			
			{
				JMenu sortMenu = new JMenu("Sort... (NYI)");
				JCheckBoxMenuItem ascChk = new JCheckBoxMenuItem("Ascending");
				JCheckBoxMenuItem desChk = new JCheckBoxMenuItem("Descending");
				JCheckBoxMenuItem unsChk = new JCheckBoxMenuItem("Unsorted", true);
				ButtonGroup sortBg = new ButtonGroup();
				sortBg.add(ascChk);
				sortBg.add(desChk);
				sortBg.add(unsChk);
				sortMenu.add(ascChk);
				sortMenu.add(desChk);
				sortMenu.add(unsChk);
				
				testPopup.add(sortMenu);
				testPopup.add(new JMenuItem("Coming soon..."));
			}
			
			@Override
			public void mousePressed(MouseEvent me) {
				int col = ch.columnAtPoint(me.getPoint());
				if ((col != -1) && (me.getButton() == MouseEvent.BUTTON3)) {
					this.col = col;
					lower();
				}
			}
			@Override
			public void mouseReleased(MouseEvent me) {
				if ((me.getButton() == MouseEvent.BUTTON3) && 
						(ch.getBounds().contains(me.getPoint()))) {
					testPopup.show(ch, ch.getHeaderRect(this.col).x - 1, ch.getHeight() - 1);
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
		ch.addMouseListener(mouseAdapter);
		ch.addMouseMotionListener(mouseAdapter);

		// Add widgets to column headers
		for (int i = 1; i < tcm.getColumnCount(); i++) {
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			panel.setPreferredSize(new Dimension(13, 13));
			tcm.getColumn(i).setHeaderRenderer(
					new ComponentHeaderRenderer(panel, null, SwingConstants.TRAILING));
		}
		
		// Create checkbox widget for hierarchical column header
		JCheckBox selChk = new TriStateCheckBox(2, -1) {
			{
				/* Hook into tree table checkbox selection model to synchronize 
				 * root node selection state with checkbox selection state */ 
				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (isSelected()) {
							cbtsm.addSelectionPath(rootPath);
						} else {
							cbtsm.removeSelectionPath(rootPath);
						}
					}
				});
				cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent tse) {
						if (tse.getPath().equals(rootPath)) {
							setSelected(cbtsm.isPathSelected(rootPath));
						}
						// TODO: use column index constants
						ch.repaint(ch.getHeaderRect(0));
					}
				});
			}
			@Override
			public boolean isPartiallySelected() {
				return cbtsm.isPartiallySelected(rootPath);
			}
		};
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);
		selChk.setOpaque(false);
		selChk.setBackground(new Color(0, 0, 0, 0));
		
		tcm.getColumn(0).setHeaderRenderer(
				new ComponentHeaderRenderer(selChk, null, SwingConstants.TRAILING));
		
		// Reduce node indents to make visuals more compact
		treeTbl.setIndents(6, 4, 2);
		treeTbl.setRootVisible(false);
		
		// Set up node icons
		// TODO: add icons for non-leaves
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
				}
				// fall back to defaults
				return context.getIcon();
			}
		};
		treeTbl.setIconValue(iv);

		// Install renderers and highlighters
		// TODO: use proper column index variables
		FontMetrics fm = getFontMetrics(chartFont);
		
		tcm.getColumn(1).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		
		tcm.getColumn(2).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		
		BarChartHighlighter bch = new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.0"));
		bch.setBaseline(1 + fm.stringWidth(bch.getFormatter().format(100.0)));
		((TableColumnExt) tcm.getColumn(3)).addHighlighter(bch);
		
		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(4)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN, x100formatter));
		
		tcm.getColumn(5).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		
		tcm.getColumn(6).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.00"));
		
		((TableColumnExt) tcm.getColumn(7)).addHighlighter(new BarChartHighlighter());
		
		((TableColumnExt) tcm.getColumn(8)).addHighlighter(new BarChartHighlighter());
		
		((TableColumnExt) tcm.getColumn(9)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00")));
		
		((TableColumnExt) tcm.getColumn(10)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00000")));
		
		// Add non-leaf highlighter
		Color hlCol = new Color(237, 246, 255);	// light blue
//		Color hlCol = new Color(255, 255, 237);	// light yellow
		HighlightPredicate notLeaf = new NotHighlightPredicate(HighlightPredicate.IS_LEAF);
		HighlightPredicate notSel = new NotHighlightPredicate(HighlightPredicate.IS_SELECTED);
		treeTbl.addHighlighter(new CompoundHighlighter(
				new ColorHighlighter(new AndHighlightPredicate(
						notSel, notLeaf, HighlightPredicate.EVEN), hlCol, null),
				new ColorHighlighter(new AndHighlightPredicate(
						notSel, notLeaf, HighlightPredicate.ODD), ColorUtils.getRescaledColor(hlCol, 0.95f), null)));
		
		// Configure column widths
		TableConfig.setColumnWidths(treeTbl, new double[] { 8.25, 20, 14, 4, 5, 4, 3, 4, 4, 4.5, 5 });
		TableConfig.setColumnMinWidths(treeTbl,
				UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(),
				createFilterButton(0, null, 0).getPreferredSize().width + 8);
		
		// Enable column control widget
		treeTbl.setColumnControlVisible(true);
		treeTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		treeTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) treeTbl.getColumnControl()).setAdditionalActionsVisible(false);
		
		// Add table to link map
		linkMap.put(root.toString(), treeTbl);
		
		return treeTbl;
	}
	
	/**
	 * Delegate method to display table-specific tooltips.
	 * @param me The MouseEvent triggering the tooltip.
	 * @return The tooltip's text or <code>null</code>.
	 */
	private String getTableToolTipText(MouseEvent me) {
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
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), PROT_ACCESSION));
					proteinHit.getPeptideHits().get(getValueAt(row, PEP_SEQUENCE)).setSelected((Boolean) aValue);
				}
			}
//			@Override
//			public org.jdesktop.swingx.decorator.ComponentAdapter getComponentAdapter(
//					int row, int column) {
//				// TODO Auto-generated method stub
//				return super.getComponentAdapter(row, column);
//			}
		};

		TableConfig.setColumnWidths(peptideTbl, new double[] {0, 0.9, 3.7, 1.7, 1.7,1.7});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();

		String[] columnToolTips = {
			    "Selection for Export",
			    "Peptide Index",
			    "Peptide Sequence",
			    "Protein Count",
			    "Spectral Count",
			    "Peptide Taxonomy"
			    };
		ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
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

		tcm.getColumn(PEP_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		((TableColumnExt) tcm.getColumn(PEP_PROTEINCOUNT)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		((TableColumnExt) tcm.getColumn(PEP_SPECTRALCOUNT)).addHighlighter(new BarChartHighlighter());
		
		((TableColumnExt) tcm.getColumn(PEP_TAXONOMY)).setVisible(false);
		
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
					PeptideHit peptideHit = dbSearchResult.getProteinHit((String) proteinTbl.getValueAt(proteinTbl.getSelectedRow(), PROT_ACCESSION)).getPeptideHit((String) peptideTbl.getValueAt(peptideTbl.getSelectedRow(), PEP_SEQUENCE));
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
		peptideTbl.setColumnControlVisible(true);
		peptideTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		peptideTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) peptideTbl.getColumnControl()).setAdditionalActionsVisible(false);
		
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
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties() {
		// PSM table
		final TableModel psmTblMdl = new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "", "#", "z", "Votes", "X",
						"O", "C", "I", "M" });
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
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), PROT_ACCESSION));
					
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
		final TableColumnModel tcm = psmTbl.getColumnModel();
		
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
		ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
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
		tcm.getColumn(PSM_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(PSM_CHARGE).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "+0"));
//		tcm.getColumn(PSM_VOTES).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		((TableColumnExt) tcm.getColumn(PSM_VOTES)).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED));
		((TableColumnExt) tcm.getColumn(PSM_XTANDEM)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN));
		((TableColumnExt) tcm.getColumn(PSM_OMSSA)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_CYAN, ColorUtils.LIGHT_CYAN));
		((TableColumnExt) tcm.getColumn(PSM_CRUX)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_BLUE, ColorUtils.LIGHT_BLUE));
		((TableColumnExt) tcm.getColumn(PSM_INSPECT)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_MAGENTA, ColorUtils.LIGHT_MAGENTA));
		((TableColumnExt) tcm.getColumn(PSM_MASCOT)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, ColorUtils.DARK_ORANGE, ColorUtils.LIGHT_ORANGE));
		
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
		psmTbl.setColumnControlVisible(true);
		psmTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		psmTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) psmTbl.getColumnControl()).setAdditionalActionsVisible(false);
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
		
		aIonsTgl = createIonToggleButton("a", false, "Show a ions", size, action);
        bIonsTgl = createIonToggleButton("b", true, "Show b ions", size, action);
        cIonsTgl = createIonToggleButton("c", false, "Show c ions", size, action);

        xIonsTgl = createIonToggleButton("x", false, "Show x ions", size, action);
        yIonsTgl = createIonToggleButton("y", true, "Show y ions", size, action);
        zIonsTgl = createIonToggleButton("z", false, "Show z ions", size, action);

        waterLossTgl = createIonToggleButton("\u00B0", false, "<html>Show H<sub>2</sub>0 losses</html>", size, action);
        ammoniumLossTgl = createIonToggleButton("*", false, "<html>Show NH<sub>3</sub> losses</html>", size, action);
        
        chargeOneTgl = createIonToggleButton("+", true, "Show ions with charge 1", size, action);
        chargeTwoTgl = createIonToggleButton("++", false, "Show ions with charge 2", size, action);
        chargeMoreTgl = createIonToggleButton(">2", false, "Show ions with charge >2", size, action);
        
        precursorTgl = createIonToggleButton("MH", true, "Show precursor ion", size, action);

        spectrumFilterPanel.add(aIonsTgl, CC.xy(2, 2));
        spectrumFilterPanel.add(bIonsTgl, CC.xy(2, 4));
        spectrumFilterPanel.add(cIonsTgl, CC.xy(2, 6));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 8));
        spectrumFilterPanel.add(xIonsTgl, CC.xy(2, 10));
        spectrumFilterPanel.add(yIonsTgl, CC.xy(2, 12));
        spectrumFilterPanel.add(zIonsTgl, CC.xy(2, 14));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 16));
        spectrumFilterPanel.add(waterLossTgl, CC.xy(2, 18));
        spectrumFilterPanel.add(ammoniumLossTgl, CC.xy(2, 20));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 22));
        spectrumFilterPanel.add(chargeOneTgl, CC.xy(2, 24));
        spectrumFilterPanel.add(chargeTwoTgl, CC.xy(2, 26));
        spectrumFilterPanel.add(chargeMoreTgl, CC.xy(2, 28));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 30));
        spectrumFilterPanel.add(precursorTgl, CC.xy(2, 32));
        
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
	private JToggleButton createIonToggleButton(String title, boolean selected,
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
	 * Method to refresh protein table contents.
	 * @throws Exception 
	 */
	protected void refreshProteinTables() throws Exception {		
		if (dbSearchResult != null && !dbSearchResult.isEmpty()) {
			
			// Display number of proteins in title area
			int numProteins = dbSearchResult.getProteinHits().size();
			protTtlPnl.setTitle("Proteins (" + numProteins + ")");
			
			// Notify status bar
			Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES");
			Client.getInstance().firePropertyChange("resetall", null, (long) numProteins);
			Client.getInstance().firePropertyChange("resetcur", null, (long) numProteins);
			
			clearTables();
			
			// TODO: Prevent switching table view
//			hierarchyCbx.setEnabled(false);
			
			// Gather models
			DefaultTableModel proteinTblMdl = (DefaultTableModel) proteinTbl.getModel();
			
			// FIXME: Coulde the following code be sourced out please ?!
			// Values for construction of highlighter
			int protIndex = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;
			
			// Iterate over metaproteins to fill the columns in the flatview
//			int metaIndex = 1;
			
//			// Get Matrix for the calculation of the identity
//			Matrix matrix = null;
//			if (!Client.getInstance().isViewer()) {
//				try {
//					Logger.getLogger(MatrixLoader.class.getName()).setLevel(Level.OFF);
//					matrix = MatrixLoader.load("BLOSUM62");
//				} catch (MatrixLoaderException e) {
//					e.printStackTrace();
//				}
//			}
			
			Logger.getLogger(SmithWatermanGotoh.class.getName()).setLevel(Level.OFF);
			ProteinHitList metaProteins = dbSearchResult.getMetaProteins();
			for (ProteinHit metaProtein : metaProteins) {
				// Create default values for meta-ProteinHit;
				String metaDesc 	= "";
				double metaSC		= 0.0;
				double metaIdentity = 100.0;
				double metaMW		= 0.0;
				double metaPI		= 0.0;
				double metaEmPai	= 0.0;
				double metaNsaf     = 0.0;
				
//				ProteinHit metaProteinHit = new ProteinHit("Meta-Protein " + (metaIndex++),"", "", null);
				PhylogenyTreeTableNode metaNode = new PhylogenyTreeTableNode(metaProtein);
				for (ProteinHit proteinHit : ((MetaProteinHit) metaProtein).getProteinHits()) {
					// Extract species string from description
					String desc = proteinHit.getDescription();
					if (desc != null) {
						String[] split = desc.split(" OS=");
						if (split.length > 1) {
							proteinHit.setDescription(split[0]);
							String species = (split[1].contains(" GN=")) ?
									split[1].substring(0, split[1].indexOf(" GN=")) : split[1];
							proteinHit.setSpecies(species);
						}
					}
					
					// Calculate NSAF
					double nsaf = proteinHit.getNSAF();
					if (nsaf < 0.0) {
						// Calculate NSAF
						nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinHit);
						proteinHit.setNSAF(nsaf);
					}
					
					// Determine maximum values for visualization later on
					maxCoverage = Math.max(maxCoverage, proteinHit.getCoverage());
					maxPeptideCount = Math.max(maxPeptideCount, proteinHit.getPeptideCount());
					maxSpecCount = Math.max(maxSpecCount, proteinHit.getSpectralCount());
					max_emPAI = Math.max(max_emPAI, proteinHit.getEmPAI());
					min_emPAI = Math.min(min_emPAI, proteinHit.getEmPAI());
					maxNSAF = Math.max(maxNSAF, nsaf);
					
					// Get common taxonomy for each protein Hit
					if (!Client.getInstance().isViewer()) { // Check for viewer Mode
						TaxonomyNode commonAncestorNode = proteinHit.getTaxonomyNode();
						for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
							// TODO Changed commonAncestorNode = NcbiTaxonomy.getInstance().getCommonAncestor(commonAncestorNode, peptideHit.getTaxonNode());
							commonAncestorNode = NcbiTaxonomy.getInstance().getCommonTaxonomyNode(commonAncestorNode, peptideHit.getTaxonomyNode());
						}
						proteinHit.setSpecies(commonAncestorNode.getName() + " (" + commonAncestorNode.getRank()+ ")" );
						
						// FIXME: Future identity calculation ?
//						// Get minimal identity for the protein
//						if (proteinHit.getSequence().length() > 0 && proteinHit.getSequence().length() <= 5000) {
//							for (ProteinHit furtherproteinHit : metaProtein) {
//								if (furtherproteinHit.getSequence().length() <= 5000) {
//									Alignment align = SmithWatermanGotoh.align(
//											new Sequence(proteinHit.getSequence()),
//											new Sequence(furtherproteinHit.getSequence()),
//											matrix, 10.0f, 0.5f);
//
//									double identity = align.getIdentity() * 100.0 /
//											proteinHit.getSequence().length();
//									if (proteinHit.getIdentity() > identity) {
//										proteinHit.setIdentity(identity);
//									}
//								}
//							}
//						}
					}
					
					// Insert protein data into table
					proteinTblMdl.addRow(new Object[] {
							proteinHit.isSelected(),
							protIndex++,
							proteinHit.getAccession(),
							proteinHit.getDescription(),
							proteinHit.getSpecies(),
							proteinHit.getCoverage(),
							proteinHit.getMolecularWeight(),
							proteinHit.getIsoelectricPoint(),
							proteinHit.getPeptideCount(), 
							proteinHit.getSpectralCount(),
							proteinHit.getEmPAI(),
							nsaf});
					
					// Update MetaproteinHit
					metaDesc = proteinHit.getDescription();
					metaSC = Math.max(metaSC, proteinHit.getCoverage());
					metaIdentity = Math.min(metaIdentity, proteinHit.getIdentity());
					metaMW = metaMW + proteinHit.getMolecularWeight() / metaProteins.size();
					metaPI = metaPI + proteinHit.getIsoelectricPoint() / metaProteins.size();
					metaProtein.addPeptideHits(proteinHit.getPeptideHits());
					metaEmPai = Math.max(metaEmPai, proteinHit.getEmPAI());
					metaNsaf = Math.max(metaNsaf, proteinHit.getNSAF());
					
					if (proteinHit.getUniprotEntry() != null) {
						// Wrap protein data in table node clones and insert them into the relevant trees
						// For case of NCBI accessions
						String target = proteinHit.getAccession();
						//Use UniProt identifier also for NCBI entries
						if (target.matches("^\\d*$")) { // if contains non-numerical character UniProt
							UniProtEntry uniprotEntry = dbSearchResult.getProteinHit(target).getUniprotEntry();
							if (uniprotEntry != null) {
								PrimaryUniProtAccession primaryUniProtAccession = uniprotEntry.getPrimaryUniProtAccession();
								target = primaryUniProtAccession.getValue();
							}else{
								target = "";
							}
						}
						
						URI uri = URI.create("http://www.uniprot.org/uniprot/" + target);
						PhylogenyTreeTableNode flatNode = new PhylogenyTreeTableNode(proteinHit);
						flatNode.setURI(uri);
						metaNode.add(flatNode);
//						TreePath flatPath = insertFlatNode(flatNode);
						PhylogenyTreeTableNode taxonNode = new PhylogenyTreeTableNode(proteinHit);
						taxonNode.setURI(uri);
						insertTaxonomicNode(taxonNode);
						PhylogenyTreeTableNode enzymeNode = new PhylogenyTreeTableNode(proteinHit);
						enzymeNode.setURI(uri);
						insertEnzymeNode(enzymeNode);
						PhylogenyTreeTableNode pathwayNode = new PhylogenyTreeTableNode(proteinHit);
						pathwayNode.setURI(uri);
						insertPathwayNode(pathwayNode);
						
//						// Link nodes to each other
//						linkNodes(flatPath, taxonPath, enzymePath);
					} else {
						System.err.println("Missing UniProt entry: " + proteinHit.getAccession());
					}
					
					Client.getInstance().firePropertyChange("progressmade", false, true);
				}
				
				if (metaNode.getChildCount() == 1) {
					metaNode = (PhylogenyTreeTableNode) metaNode.getChildAt(0);
//				} else {
//					metaProtein.setAccession("Meta-Protein " + metaIndex++);
				}
				
				// Set values for the metaprotein
				metaProtein.setDescription(metaDesc);
				if (!Client.getInstance().isViewer()) {
					// Get highest common Taxonomy
					TaxonomyNode firstNode = metaProtein.getPeptideHitList().get(0).getTaxonomyNode();
					for (PeptideHit peptideHit : metaProtein.getPeptideHitList()) {
						TaxonomyNode taxonNode = peptideHit.getTaxonomyNode();
						firstNode = NcbiTaxonomy.getInstance().getCommonTaxonomyNode(firstNode,taxonNode);
					}
					metaProtein.setSpecies(firstNode.getName() + " (" + firstNode.getRank() +")");
				}
//				metaProtein.setSpecies(metaProtein.getSpecies());
				metaProtein.setIdentity(metaIdentity);
				metaProtein.setCoverage(metaSC);
				metaProtein.setMolecularWeight(metaMW);
				metaProtein.setIsoelectricPoint(metaPI);
				metaProtein.setEmPAI(metaEmPai);
				metaProtein.setNSAF(metaNsaf);
				metaProtein.setCoverage(metaSC);
				
				insertFlatNode(metaNode);
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
							for (DatabaseCrossReference xref : 
								protHit.getUniprotEntry().getDatabaseCrossReferences(DatabaseType.KO)) {
								kNumbers.add(((KO) xref).getKOIdentifier().getValue());
							}
							ecNumbers.addAll(protHit.getUniprotEntry().getProteinDescription().getEcNumbers());
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
				
				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_COVERAGE))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxCoverage)));
				highlighter.setRange(0.0, maxCoverage);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_PEPTIDECOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxPeptideCount)));
				highlighter.setRange(0.0, maxPeptideCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_SPECTRALCOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
				highlighter.setRange(0.0, maxSpecCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_EMPAI))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(max_emPAI)));
				highlighter.setRange(min_emPAI, max_emPAI);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_NSAF))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxNSAF)));
				highlighter.setRange(0.0, maxNSAF);
				
				proteinTbl.getSelectionModel().setSelectionInterval(0, 0);
				
				// XXX: repeat for tree tables
				for (CheckBoxTreeTable table : linkMap.values()) {
					tcm = table.getColumnModel();

					// TODO: use proper column index variables
					// TODO: make use of non-leaf nodes in hierarchical column to determine maxima
					
					highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(table.convertColumnIndexToView(4))).getHighlighters()[0];
					highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxCoverage)));
					highlighter.setRange(0.0, maxCoverage);

					highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(table.convertColumnIndexToView(7))).getHighlighters()[0];
					highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxPeptideCount)));
					highlighter.setRange(0.0, maxPeptideCount);

					highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(table.convertColumnIndexToView(8))).getHighlighters()[0];
					highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
					highlighter.setRange(0.0, maxSpecCount);

					highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(table.convertColumnIndexToView(9))).getHighlighters()[0];
					highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(max_emPAI)));
					highlighter.setRange(min_emPAI, max_emPAI);

//					highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(table.convertColumnIndexToView(10))).getHighlighters()[0];
//					highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxNSAF)));
//					highlighter.setRange(0.0, maxNSAF);
				}
			}
			Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES FINISHED");
			
//			hierarchyCbx.setEnabled(true);
			
			protTaxonTreeTbl.expandAll();
			protEnzymeTreeTbl.expandAll();
			protPathwayTreeTbl.expandAll();
		}
	}

	private void clearTables() {
		// Empty protein tables
		TableConfig.clearTable(proteinTbl);
		
		setupProteinTreeTables();
		
		int i = 1;
		for (CheckBoxTreeTable cbtt : linkMap.values()) {
			JScrollPane scrollPane = new JScrollPane(cbtt);
			scrollPane.setPreferredSize(new Dimension(800, 150));
			protCardPnl.add(scrollPane, cardLabels[i++]);
		}
	}

	/**
	 * Inserts a protein tree node into the 'Flat View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	private TreePath insertFlatNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protFlatTreeTbl.getTreeTableModel();
		
		treeTblMdl.insertNodeInto(protNode,
				(MutableTreeTableNode) treeTblMdl.getRoot(),
				treeTblMdl.getRoot().getChildCount());
		
		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}

	/**
	 * Inserts a protein tree node into the 'Taxonomy View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	private TreePath insertTaxonomicNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protTaxonTreeTbl.getTreeTableModel();
		
		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();
		
		ProteinHit ph = (ProteinHit) protNode.getUserObject();
		
		List<String> names = new ArrayList<String>();
		for (NcbiTaxon ncbiTaxon : ph.getUniprotEntry().getTaxonomy()) {
			names.add(ncbiTaxon.getValue());
		}
		names.add(ph.getUniprotEntry().getOrganism().getScientificName().getValue());

		PhylogenyTreeTableNode parent = root;
		for (String name : names) {
			PhylogenyTreeTableNode child = (PhylogenyTreeTableNode) parent.getChildByName(name);
			if (child == null) {
				child = new PhylogenyTreeTableNode(name);
				treeTblMdl.insertNodeInto(child, parent, 0);
			}
			parent = child;
		}
		treeTblMdl.insertNodeInto(protNode, parent, parent.getChildCount());
		
		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}
	
	/**
	 * Inserts a protein tree node into the 'E.C. View' tree table and returns 
	 * the tree path to where it was inserted.
	 * @param protNode The protein tree node to insert
	 * @return The tree path pointing to the insertion location
	 */
	private TreePath insertEnzymeNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protEnzymeTreeTbl.getTreeTableModel();
		
		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();
		
		ProteinHit ph = (ProteinHit) protNode.getUserObject();
		
		List<String> ecNumbers = ph.getUniprotEntry().getProteinDescription().getEcNumbers();
		
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
				treeTblMdl.insertNodeInto(child, parent, 0);
			}
			parent = child;
		}
		treeTblMdl.insertNodeInto(protNode, parent, parent.getChildCount());
		
		return new TreePath(treeTblMdl.getPathToRoot(protNode));
	}

	/**
	 * Inserts a protein tree node into the 'Pathway View' tree table and
	 * returns the tree paths to where its instances were inserted (in case the
	 * protein is part of multiple pathways).
	 * @param protNode The protein tree node to insert
	 * @return The tree paths pointing to the insertion locations
	 */
	private TreePath[] insertPathwayNode(PhylogenyTreeTableNode protNode) {
		DefaultTreeTableModel treeTblMdl = (DefaultTreeTableModel) protPathwayTreeTbl.getTreeTableModel();

		PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();
		
		ProteinHit ph = (ProteinHit) protNode.getUserObject();

		// gather K and EC numbers for pathway lookup
		List<String> kNumbers = new ArrayList<String>();
		for (DatabaseCrossReference xref : 
			ph.getUniprotEntry().getDatabaseCrossReferences(DatabaseType.KO)) {
			kNumbers.add(((KO) xref).getKOIdentifier().getValue());
		}
		List<String> ecNumbers = ph.getUniprotEntry().getProteinDescription().getEcNumbers();
		
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
								treeTblMdl.insertNodeInto(child, parent, parent.getChildCount());
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
				treeTblMdl.insertNodeInto(cloneNode, parent, parent.getChildCount());
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
				treeTblMdl.insertNodeInto(parent, root, root.getChildCount());
			}
			// add clone of protein node to 'Unclassified' branch
			PhylogenyTreeTableNode cloneNode = new PhylogenyTreeTableNode(protNode.getUserObject());
			cloneNode.setURI(protNode.getURI());
			treeTblMdl.insertNodeInto(cloneNode, parent, parent.getChildCount());
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
	private PhylogenyTreeTableNode findPathway(Short pw) {
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
	private void linkNodes(TreePath... treePaths) {
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
	 * Method to refresh peptide table contents and sequence coverage viewer.
	 */
	protected void refreshPeptideViews(int protRow) {
		// Clear existing contents
		TableConfig.clearTable(peptideTbl);
		coveragePnl.removeAll();

		if (protRow != -1) {
			// Get protein and peptide information 
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
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
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
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
					String actualAccession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
					String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
					int index = psmTbl.convertRowIndexToModel(psmRow);
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence).getSpectrumMatches().get(index);

					MascotGenericFile mgf = null;
					// TODO: Note: loading spectra from database is faster than parsing from file, optimization required?
//					if (conn != null) {
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
	private void addSpectrumAnnotations(Map<String, FragmentIon[]> fragmentIons) {

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
	private void updateFilteredAnnotations() {
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
    private Vector<SpectrumAnnotation> filterAnnotations(Vector<SpectrumAnnotation> annotations) {

        Vector<SpectrumAnnotation> filteredAnnotations = new Vector<SpectrumAnnotation>();
        
		JToggleButton[] ionToggles = new JToggleButton[] { aIonsTgl, bIonsTgl,
				cIonsTgl, xIonsTgl, yIonsTgl, zIonsTgl, precursorTgl };
        String ionTokens = "abcxyzM";
        
		JToggleButton[] miscToggles = new JToggleButton[] { chargeOneTgl,
				chargeMoreTgl, chargeTwoTgl, ammoniumLossTgl, waterLossTgl };
		String[] miscTokens = new String[]  { "+", "+++", "++", "*", "\u00B0" };

		for (SpectrumAnnotation annotation : annotations) {
            String currentLabel = annotation.getLabel();
            boolean useAnnotation = true;
            // check ion type
            for (int i = 0; i < ionToggles.length; i++) {
            	if (currentLabel.lastIndexOf(ionTokens.charAt(i)) != -1) {
    				useAnnotation &= ionToggles[i].isSelected();
            	}
            	if (!useAnnotation) break;
			}

            // check ion charge + ammonium and water-loss
            if (useAnnotation) {
            	for (int i = 0; i < miscToggles.length; i++) {
					if (currentLabel.lastIndexOf(miscTokens[i]) != -1) {
						useAnnotation &= miscToggles[i].isSelected();
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
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsFromDbButtonEnabled(boolean enabled) {
		getResultsFromDbBtn.setEnabled(enabled);
	}

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
	public JPanel createFilterButton(int column, JTable table, int filterType) {
		// button widget with little black triangle
		final FilterButton button = new FilterButton(column, table, filterType);
		button.setPreferredSize(new Dimension(11, 11));
	
		// wrap button in panel to apply padding
		final JPanel panel = new JPanel(new FormLayout("0px, p, 3px", "2px, p, 2px"));
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
	 * Makes the frame and this panel appear busy.
	 * @param busy <code>true</code> if busy, <code>false</code> otherwise.
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		clientFrame.setCursor(cursor);
		split.setCursor(cursor);

		hierarchyBtn.setEnabled(!busy);
		setResultsFromDbButtonEnabled(!busy);
		getResultsFromFileBtn.setEnabled(!busy);
	}
	
	/**
	 * Filter button widget for table headers.
	 * 
	 * @author A. Behne
	 */
	private class FilterButton extends JToggleButton {
		
		private FilterBalloonTip filterTip;
		private FilterButton button;
		
		/**
		 * Constructs a filter toggle button widget which shows its own balloon
		 * tip pop-up containing filtering-related components for the specified
		 * <code>column</code>.
		 * 
		 * @param column
		 */
		public FilterButton(final int column, final JTable table, final int filterType) {
			super();
			this.button = this;
			this.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (button != lastSelectedFilterBtn) {
//						lastSelectedFilterBtn.setSelected(false);
						lastSelectedFilterBtn.setFilterTipVisible(false);
						lastSelectedFilterBtn = button;
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
									updateSelections(column, filterTip.getFilterString());
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
			if (filterTip != null) {
				filterTip.setVisible(visible);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.fillPolygon(new int[] { 3, 8, 8 }, new int[] { 8, 8, 3 }, 3);
		}
		
	}


	/**
	 * Method to update the checkbox column values depending on pattern 
	 * matching in the specified column using the specified pattern string.
	 * @param column The column to check.
	 * @param filterString Comma-separated string patterns.
	 */
	private void updateSelections(int column, String filterString) {
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
				if (!(Boolean) proteinTbl.getValueAt(row, PROT_SELECTION)) {
					break;
				}
				proteinTbl.setValueAt(	interval.containsExclusive(((Number) proteinTbl.getValueAt(row, column)).doubleValue()),
										row, proteinTbl.convertColumnIndexToView(PROT_SELECTION));
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
				if (!(Boolean) proteinTbl.getValueAt(row, PROT_SELECTION)) {
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
				proteinTbl.setValueAt(selected, row, proteinTbl.convertColumnIndexToView(PROT_SELECTION));
			}
		}
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(true);
		// re-sort rows after iteration finished
		((TableSortController) proteinTbl.getRowSorter()).sort();
	}

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
			// TODO: use binding to synchronize selection state with table
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
	 * Method to fetch the processed results from file.
	 */
	public void fetchResultsFromFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(Constants.MPA_FILE_FILTER);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnValue = chooser.showOpenDialog(clientFrame);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			importFile = chooser.getSelectedFile();
			new ResultsTask(importFile).execute();
		}
	}
	
	/**
	 * Method to fetch the processed results from the database.
	 */
	public void fetchResultsFromDatabase() {
		new ResultsTask(null).execute();
	}
	
	/**
	 * Custom highlighter predicate for protein count visualization.
	 * @author kohrs
	 *
	 */
	private class ProtCHighlighterPredicate implements HighlightPredicate {

		/**
		 * The list of proteins to be highlighted.
		 */
		private List<ProteinHit> protHits = new ArrayList<ProteinHit>();
		
		@Override
		public boolean isHighlighted(Component renderer,
				org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
			for (ProteinHit protHit : getProteinHits()) {
				if (protHit.getAccession().equals(adapter.getValue(PROT_ACCESSION))) {
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
	
	
	private ChartType chartType = OntologyChartType.BIOLOGICAL_PROCESS;;

	private ChartPanel chartPnl;
	private JToggleButton chartTypeBtn;
	private JComboBox chartPieHierCbx;
	private JCheckBox chartPieHideChk;
	private JCheckBox chartPieGroupChk;
	private JScrollBar chartBar;
	protected double chartPieAngle;

	protected OntologyData ontologyData;
	protected TaxonomyData taxonomyData;
	
	private ChartType spectrumType = new ChartType() {
		public String getTitle() { return ""; }
		public String toString() { return "Spectrum Viewer"; };
	};

	/**
	 * Creates and returns the top-right chart panel (wrapped in a
	 * JXTitledPanel)
	 */
	private JXTitledPanel createChartPanel() {

		// init chart types
		final ChartType[] chartTypes = new ChartType[] {
				OntologyChartType.BIOLOGICAL_PROCESS,
				OntologyChartType.MOLECULAR_FUNCTION,
				OntologyChartType.CELLULAR_COMPONENT,
				TaxonomyChartType.KINGDOM,
				TaxonomyChartType.PHYLUM,
				TaxonomyChartType.CLASS,
				TaxonomyChartType.ORDER,
				TaxonomyChartType.FAMILY,
				TaxonomyChartType.GENUS,
				TaxonomyChartType.SPECIES,
				spectrumType
		};

		chartTypeBtn = new JToggleButton(
				IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ICON));
		chartTypeBtn.setRolloverIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_ROLLOVER_ICON));
		chartTypeBtn.setPressedIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_PRESSED_ICON));
		chartTypeBtn.setToolTipText("Select Chart Type");
		chartTypeBtn.setEnabled(false);

		chartTypeBtn.setUI(new RoundedHoverButtonUI());

		chartTypeBtn.setOpaque(false);
		chartTypeBtn.setBorderPainted(false);
		chartTypeBtn.setMargin(new Insets(1, 0, 0, 0));

		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
		chartTypeBtn.addMouseListener(ittml);

		// create chart type selection popup
		final JPopupMenu chartTypePop = new JPopupMenu();
		
		JMenu ontologyMenu = new JMenu("Ontology");
		ontologyMenu.setIcon(IconConstants.PIE_CHART_ICON);
		chartTypePop.add(ontologyMenu);
		JMenu taxonomyMenu = new JMenu("Taxonomy");
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
			}
		}
		chartTypePop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				chartTypeBtn.setSelected(false);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});

		// link popup to button
		chartTypeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartTypePop.show(chartTypeBtn, 0, chartTypeBtn.getHeight());
			}
		});

		// arrange button in panel
		JPanel chartBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));

		// create and configure chart panel for plots
		chartPnl = new ChartPanel(null);
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setOpaque(false);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));

		// create combobox to control what counts to display in the plots
		// (protein/peptide/spectrum count)
		chartPieHierCbx = new JComboBox(HierarchyLevel.values());
		chartPieHierCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					HierarchyLevel hl = (HierarchyLevel) evt.getItem();
					
					ontologyData.setHierarchyLevel(hl);
					taxonomyData.setHierarchyLevel(hl);
					
					updateChart(chartType);
				}
			}
		});
		chartPieHierCbx.setVisible(false);

		chartPnl.setLayout(new FormLayout("r:p:g, 2dlu, p, 2dlu, l:p:g", "0px:g, p, 2dlu"));
		
		chartPieHideChk = new JCheckBox("Hide Unknown", false);
		chartPieHideChk.setOpaque(false);
		chartPieHideChk.setVisible(false);
		chartPieHideChk.addItemListener(new ItemListener() {
			private boolean doHide;
			@Override
			public void itemStateChanged(ItemEvent evt) {
				doHide = (evt.getStateChange() == ItemEvent.SELECTED);
				ontologyData.setHideUnknown(doHide);
				taxonomyData.setHideUnknown(doHide);
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
						chartPieHideChk.setEnabled(false);
						Plot plot = chartPnl.getChart().getPlot();
						if (plot instanceof PiePlot) {
							DefaultPieDataset dataset =
									(DefaultPieDataset) ((PiePlot) plot).getDataset();
							if (doHide) {
								double val = dataset.getValue("Unknown").doubleValue();
								for (int i = 0; i < 11; i++) {
									double tmp = 1.0 - i / 10.0;
									double newVal = val * tmp * tmp;
									dataset.setValue("Unknown", newVal);
									if (newVal > 0.0) {
										Thread.sleep(33);
									} else {
										break;
									}
								}
							} else {
								double val;
								if (chartType instanceof OntologyChartType) {
									val = ontologyData.getDataset().getValue("Unknown").doubleValue();
								} else {
									val = taxonomyData.getDataset().getValue("Unknown").doubleValue();
								}
								for (int i = 0; i < 11; i++) {
									double tmp = i / 10.0;
									double newVal = val * tmp * tmp;
									if (newVal <= 0.0) {
										continue;
									}
									dataset.setValue("Unknown", newVal);
									if (newVal < val) {
										Thread.sleep(33);
									} else {
										break;
									}
								}
							}
						}
						return null;
					}
					@Override
					protected void done() {
						chartPieHideChk.setEnabled(true);
					};
					
				}.execute();
			}
		});
		
		chartPieGroupChk = new JCheckBox("Group minor segments", true);
		chartPieGroupChk.setOpaque(false);
		chartPieGroupChk.setVisible(false);
		chartPieGroupChk.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					ontologyData.setLimit(0.01);
					taxonomyData.setLimit(0.01);
				} else {
					ontologyData.setLimit(0.0);
					taxonomyData.setLimit(0.0);
				}
				Plot plot = chartPnl.getChart().getPlot();
				if ((chartType instanceof OntologyChartType)) {
					((PiePlot) plot).setDataset(ontologyData.getDataset());
				} else if (chartType instanceof TaxonomyChartType) {
					((PiePlot) plot).setDataset(taxonomyData.getDataset());
				}
			}
		});
		
		chartPnl.add(chartPieHierCbx, CC.xy(1, 2));
		chartPnl.add(chartPieHideChk, CC.xy(3, 2));
		chartPnl.add(chartPieGroupChk, CC.xy(5, 2));

		JScrollPane chartScp = new JScrollPane(chartPnl,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		for (ChangeListener cl : chartScp.getViewport().getChangeListeners()) {
			chartScp.getViewport().removeChangeListener(cl);
		}
		chartScp.getViewport().setBackground(Color.WHITE);

		chartBar = chartScp.getVerticalScrollBar();
		chartBar.setValues(0, 0, 0, 0);
		chartBar.setBlockIncrement(36);
		DefaultBoundedRangeModel chartBarMdl =
				(DefaultBoundedRangeModel) chartBar.getModel();
		ChangeListener[] cbcl = chartBarMdl.getChangeListeners();
		chartBarMdl.removeChangeListener(cbcl[0]);

		chartBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				JFreeChart chart = chartPnl.getChart();
				if (chart != null) {
					if (chart.getPlot() instanceof PiePlot) {
						chartPieAngle = ae.getValue();
						((PiePlot) chart.getPlot()).setStartAngle(chartPieAngle);
					}
				}
			}
		});

		// wrap scroll pane in panel with 5dlu margin around it
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		chartMarginPnl.add(chartScp, CC.xy(2, 2));

		// wrap everything in titled panel containing button panel in the top
		// right corner
		JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel(
				"Chart View", chartMarginPnl);
		chartTtlPnl.setRightDecoration(chartBtnPnl);

		return chartTtlPnl;
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
			ontologyData.init();
			chart = ChartFactory.createOntologyPieChart(
					ontologyData, chartType);
		} else if (chartType instanceof TaxonomyChartType) {
			taxonomyData.init();
			chart = ChartFactory.createTaxonomyPieChart(
					taxonomyData, chartType);
		} else if (chartType == spectrumType) {
			// TODO: integrate spectrum panels into chart structure
			refreshPlot();
			this.chartType = spectrumType;
			return;
		}

		if (chart != null) {
			Plot plot = chart.getChart().getPlot();
			boolean isPie = plot instanceof PiePlot;
			if (isPie) {
				// enable chart scroll bar
				chartBar.setMaximum(360);
				chartBar.setValue((int) chartPieAngle);
				((PiePlot) chart.getChart().getPlot()).setStartAngle(chartPieAngle);
			} else {
				// disable chart scroll bar
				double temp = chartPieAngle;
				chartBar.setMaximum(0);
				chartPieAngle = temp;
			}
			// hide/show pie chart-related controls
			chartPieHierCbx.setVisible(isPie);
			chartPieHideChk.setVisible(isPie);
			chartPieGroupChk.setVisible(isPie);
			
			// insert chart into panel
			chartPnl.setChart(chart.getChart());
		} else {
			System.err.println("Chart type could not be determined!");
		}

		this.chartType = chartType;
	}
	
}