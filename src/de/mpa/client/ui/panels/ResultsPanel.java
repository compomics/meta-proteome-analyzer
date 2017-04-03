package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.MetaProteinFactory;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ButtonTabbedPane;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ClientFrameMenuBar;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HeatMapData;
import de.mpa.client.ui.chart.HeatMapPane;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.ScrollableChartPane;
import de.mpa.client.ui.chart.TaxonomyChart;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.dialogs.SelectExperimentDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.parser.kegg.KEGGNode;
import de.mpa.util.URLstarter;

/**
 * Panel providing overview information about fetched results in the form of
 * assorted charts and tables.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class ResultsPanel extends JPanel implements Busyable {

	/**
	 * The tab pane containing the various results sub-panels.
	 */
	private ButtonTabbedPane resultsTpn;

	/** 
	 * The database search results sub-panel.
	 */
	private final DbSearchResultPanel dbPnl;

	/**
	 * The spectral similarity results sub-panel.
	 */
	//	private SpecSimResultPanel ssPnl;

	/**
	 * The graph database sub-panel.
	 */
	private final GraphDatabaseResultPanel gdbPnl;

	/**
	 * The result comparison sub-panel.
	 */
	private final ComparePanel compPnl;

	/**
	 * The split pane layout of the overview panel.
	 */
	private JXMultiSplitPane msp;

	/**
	 * Chart panel capable of displaying various charts (mainly ontology pie charts).
	 */
	private ScrollableChartPane chartPane;

	/**
	 * The type of chart to be displayed in the top right chart pane.
	 */
	private ChartType chartType = OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS;

	/**
	 * Data container for ontological meta-information of the fetched results.
	 */
	private OntologyData ontologyData = new OntologyData();

	/**
	 * Data container for taxonomic meta-information of the fetched results.
	 */
	private TaxonomyData taxonomyData = new TaxonomyData();

	/**
	 * Table containing expanded details for proteins selected/displayed in the
	 * chart pane.
	 */
	private JXTable detailsTbl;

	/**
	 * Label displaying the total count of spectra used in the search.
	 */
	private JLabel totalSpecLbl;

	/**
	 * Label displaying the number of spectra annotated by the search.
	 */
	private JLabel identSpecLbl;

	/**
	 * Label displaying the total count of peptide associations in the result.
	 */
	private JLabel distPepLbl;

	/**
	 * Label displaying the number of peptides with unique sequences in the result.
	 */
	private JLabel uniqPepLbl;

	/**
	 * Label displaying the total count of proteins identified by the search.
	 */
	private JLabel totalProtLbl;

	/**
	 * Label displaying the number of proteins specific to unique species
	 * identified in the search.
	 */
	private JLabel metaProtLbl;

	/**
	 * Label displaying the number of unique species identified by protein
	 * associations by the search.
	 */
	private JLabel speciesLbl;

	/**
	 * Label displaying the number of unique enzymes identified by protein
	 * associations by the search.
	 */
	private JLabel enzymesLbl;

	/**
	 * Label displaying the number of unique pathways identified by protein
	 * associations by the search.
	 */
	private JLabel pathwaysLbl;

	/**
	 * Button for fetching search results from a remote database.
	 */
	private JButton fetchResultsBtn;

	/**
	 * Button for fetching search results from several experiments from a remote database.
	 */
	private JButton fetchMultipleResultsBtn;

	/**
	 * Button for fetching search results from a file.
	 */
	private JButton processResultsBtn;

	/**
	 * The database search result object
	 */
	private DbSearchResult dbSearchResult;

	/**
	 * The heat map component.
	 */
	private HeatMapPane heatMapPn;

	/**
	 * Flag denoting whether this panel is currently busy.
	 */
	private boolean busy;

	/**
	 * Constructs a results panel containing overview and detail views for
	 * search results.
	 */
	public ResultsPanel() {
		this.dbPnl = new DbSearchResultPanel();
		//		this.ssPnl = new SpecSimResultPanel();
		this.gdbPnl = new GraphDatabaseResultPanel();
		this.compPnl = new ComparePanel();
		initComponents();
	}

	/**
	 * Creates and initializes the various GUI components.
	 */
	private void initComponents() {
		// configure main panel layout
		this.setLayout(new FormLayout("p:g, 5dlu", "f:p:g, -40px, b:p, 5dlu"));

		// Modify tab pane visuals for this single instance, restore defaults
		// afterwards
		Insets contentBorderInsets = UIManager
				.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0,
				contentBorderInsets.bottom, 0));
		resultsTpn = new ButtonTabbedPane(JTabbedPane.BOTTOM);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets);

		JPanel ovPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));

		String layoutDef = "(ROW (LEAF weight=0.4 name=summary) (COLUMN weight=0.6 (LEAF weight=0.5 name=chart) (LEAF weight=0.5 name=details)";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

		MultiSplitLayout msl = new MultiSplitLayout(modelRoot);

		msp = new JXMultiSplitPane(msl);
		msp.setDividerSize(12);

		msp.add(createSummaryPanel(), "summary");
		msp.add(createChartPanel(), "chart");
		msp.add(createDetailsPanel(), "details");

		ovPnl.add(msp, CC.xy(2, 2));

		resultsTpn.addTab("Overview",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/overview32.png")),
				ovPnl);
		resultsTpn.addTab("Database Search Results",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database_search32.png")),
				dbPnl);
		//		resultsTpn.addTab("Spectral Similarity Results",
		//				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/spectral_search32.png")),
		//				ssPnl);
		resultsTpn.addTab("Graph Database Results",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/graph32.png")),
				gdbPnl);
		resultsTpn.addTab("Compare Results", IconConstants.COMPARE_ICON, compPnl);

		// force tab heights by setting size of first tab component
		Component tabComp = resultsTpn.getTabComponentAt(0);
		tabComp.setPreferredSize(new Dimension(
				tabComp.getPreferredSize().width, 40));

		// initially disable all but the overview tab
		for (int i = 1; i < 3; i++) {
			resultsTpn.setEnabledAt(i, false);
		}

		// create navigation button panel
		final JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p",
				"b:p:g"));
		navPnl.setOpaque(false);

		navPnl.add(ClientFrame.getInstance().createNavigationButton(false, !Client.isViewer()), CC.xy(1, 1));
		navPnl.add(ClientFrame.getInstance().createNavigationButton(true, !Client.isViewer()), CC.xy(3, 1));

		// add everything to main panel
		this.add(navPnl, CC.xy(1, 3));
		this.add(resultsTpn, CC.xyw(1, 1, 2));
	}

	/**
	 * Creates and returns the left-hand summary panel (wrapped in a
	 * JXTitledPanel)
	 */
	private JPanel createSummaryPanel() {

		JButton resizeBtn = createResizeButton("chart", "details");

		JPanel summaryBtnPnl = new JPanel(new FormLayout("22px, 1px", "20px"));
		summaryBtnPnl.setOpaque(false);
		summaryBtnPnl.add(resizeBtn, CC.xy(1, 1));

		JPanel summaryPnl = new JPanel(new FormLayout(
				"5dlu, m:g, 5dlu",
				"5dlu, f:p, 5dlu, f:p:g, 5dlu"));

		JPanel generalPnl = new JPanel(new FormLayout(
				"5dlu, p, 5dlu, r:p, 5dlu, m:g, 5dlu, r:p, 5dlu, p, 5dlu",
				"2dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu"));
		generalPnl.setBorder(BorderFactory.createTitledBorder("General Statistics"));

		// total vs. identified spectra
		totalSpecLbl = new JLabel("0");
		identSpecLbl = new JLabel("0");
		JPanel specBarPnl = new BarChartPanel(totalSpecLbl, identSpecLbl);

		generalPnl.add(new JLabel("Total Spectra"), CC.xy(2, 2));
		generalPnl.add(totalSpecLbl, CC.xy(4, 2));
		generalPnl.add(specBarPnl, CC.xy(6, 2));
		generalPnl.add(identSpecLbl, CC.xy(8, 2));
		generalPnl.add(new JLabel("Identified Spectra"), CC.xy(10, 2));

		// total vs. unique peptides
		distPepLbl = new JLabel("0");
		uniqPepLbl = new JLabel("0");
		JPanel pepBarPnl = new BarChartPanel(distPepLbl, uniqPepLbl);

		generalPnl.add(new JLabel("Distinct Peptides"), CC.xy(2, 4));
		generalPnl.add(distPepLbl, CC.xy(4, 4));
		generalPnl.add(pepBarPnl, CC.xy(6, 4));
		generalPnl.add(uniqPepLbl, CC.xy(8, 4));
		generalPnl.add(new JLabel("Unique Peptides"), CC.xy(10, 4));

		// total vs. species-specific proteins
		totalProtLbl = new JLabel("0");
		metaProtLbl = new JLabel("0");
		JPanel protBarPnl = new BarChartPanel(totalProtLbl, metaProtLbl);

		generalPnl.add(new JLabel("Total Proteins"), CC.xy(2, 6));
		generalPnl.add(totalProtLbl, CC.xy(4, 6));
		generalPnl.add(protBarPnl, CC.xy(6, 6));
		generalPnl.add(metaProtLbl, CC.xy(8, 6));
		generalPnl.add(new JLabel("Meta-Proteins"), CC.xy(10, 6));

		speciesLbl = new JLabel("0");
		enzymesLbl = new JLabel("0");
		pathwaysLbl = new JLabel("0");
		generalPnl.add(new JLabel("Distinct Species"), CC.xy(2, 8));
		generalPnl.add(speciesLbl, CC.xy(4, 8));
		generalPnl.add(new JLabel("Distinct Enzymes"), CC.xy(2, 10));
		generalPnl.add(enzymesLbl, CC.xy(4, 10));
		generalPnl.add(new JLabel("Distinct Pathways"), CC.xy(2, 12));
		generalPnl.add(pathwaysLbl, CC.xy(4, 12));

		// panel containing buttons to fetch/process results
		FormLayout layout = new FormLayout("p:g, 5dlu, p:g, 5dlu, p:g", "f:p:g");
		layout.setColumnGroups(new int[][] { { 1, 3 } });
		JPanel fetchPnl = new JPanel(layout);

		fetchResultsBtn = new JButton("Fetch Results", IconConstants.RESULTS_FETCH_ICON);
		fetchResultsBtn.setRolloverIcon(IconConstants.RESULTS_FETCH_ROLLOVER_ICON);
		fetchResultsBtn.setPressedIcon(IconConstants.RESULTS_FETCH_PRESSED_ICON);
		fetchResultsBtn.setToolTipText("Press button to fetch results of the selected experiment.");
		fetchResultsBtn.setIconTextGap(7);
		fetchResultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				LinkedList<Long> expList = new LinkedList<Long>();
				expList.add(ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
				new FetchResultsTask(expList, false).execute();
			}
		});

		// Fetch multiple results
		fetchMultipleResultsBtn = new JButton("Fetch Multi-Results", IconConstants.RESULTS_FETCH_ICON);
		fetchMultipleResultsBtn.setRolloverIcon(IconConstants.RESULTS_FETCH_ROLLOVER_ICON);
		fetchMultipleResultsBtn.setPressedIcon(IconConstants.RESULTS_FETCH_PRESSED_ICON);
		fetchMultipleResultsBtn.setToolTipText("Fetches the results from multiple experiments and process them together");
		fetchMultipleResultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Get selected experiments
				LinkedList<Long> expList = new LinkedList<Long>();
				expList = SelectExperimentDialog.showDialog(ClientFrame.getInstance(), "Select experiments");
				// Fetches experiments
				if (!(expList.isEmpty())) {
					new FetchResultsTask(expList, true).execute();
				}
			}
		});

		processResultsBtn = new JButton("Process Results", IconConstants.RESULTS_PROCESS_ICON);
		processResultsBtn.setRolloverIcon(IconConstants.RESULTS_PROCESS_ROLLOVER_ICON);
		processResultsBtn.setPressedIcon(IconConstants.RESULTS_PROCESS_PRESSED_ICON);
		processResultsBtn.setIconTextGap(10);

		processResultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int res = AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(),
															"Result Processing Settings",
															true, Client.getInstance().getResultParameters());
					if ((res != AdvancedSettingsDialog.DIALOG_CANCELLED)) {
//					if (dbSearchResult.isRaw() ||(res == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED)) {
						new ProcessResultsTask().execute();
					}
				}
		});

		// initially disable process button
		processResultsBtn.setEnabled(false);

		fetchPnl.add(fetchResultsBtn, CC.xy(1, 1));
		fetchPnl.add(fetchMultipleResultsBtn, CC.xy(3, 1));
		fetchPnl.add(processResultsBtn, CC.xy(5, 1));

		generalPnl.add(fetchPnl, CC.xywh(6, 8, 5, 5));

		// create default heat map
		heatMapPn = new HeatMapPane(new HeatMapData());
		heatMapPn.setVisibleColumnCount(13);
		heatMapPn.setVisibleRowCount(13);
		heatMapPn.setEnabled(false);

		// insert subcomponents into main panel
		summaryPnl.add(generalPnl, CC.xy(2, 2));
		summaryPnl.add(heatMapPn, CC.xy(2, 4));

		JXTitledPanel summaryTtlPnl =
				PanelConfig.createTitledPanel("Summary", summaryPnl);
		summaryTtlPnl.setRightDecoration(summaryBtnPnl);

		return summaryTtlPnl;
	}

	/**
	 * Creates and returns the top-right chart panel (wrapped in a
	 * JXTitledPanel)
	 */
	private JPanel createChartPanel() {

		// init chart types
		List<ChartType> tmp = new ArrayList<ChartType>();
		for (ChartType oct : OntologyChart.OntologyChartType.values()) {
			tmp.add(oct);
		}
		for (ChartType tct : TaxonomyChart.TaxonomyChartType.values()) {
			tmp.add(tct);
		}
		final ChartType[] chartTypes = tmp.toArray(new ChartType[0]);

		// create and configure button for chart type selection
		// TODO: link enable state to busy/enable state of chart panel
		final JToggleButton chartTypeBtn = new JToggleButton(
				IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ICON));
		chartTypeBtn.setRolloverIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_ROLLOVER_ICON));
		chartTypeBtn.setPressedIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_PRESSED_ICON));
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
					// clear details table
					// TODO: maybe this ought to be elsewhere, e.g. inside updateOverview()?
					TableConfig.clearTable(detailsTbl);
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
			if (chartType instanceof OntologyChart.OntologyChartType) {
				ontologyMenu.add(item);
			} else if (chartType instanceof TaxonomyChart.TaxonomyChartType) {
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
				ResultsPanel.this.updateChart();
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

		// arrange button in panel
		JPanel chartBtnPnl = new JPanel(new FormLayout("36px, c:5dlu, 22px, 1px",
				"f:20px"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));
		chartBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		chartBtnPnl.add(createResizeButton("summary", "details"), CC.xy(3, 1));

		// create and configure chart panel for plots
		OntologyData dummyData = new OntologyData() {
			@Override
			public PieDataset getDataset() {
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				pieDataset.setValue("DNRPM", 2);
				pieDataset.setValue("RPM", 8);
				return pieDataset;
			}
		};

		chartPane = new ScrollableChartPane(ChartFactory.createOntologyChart(
				dummyData, OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS).getChart()) {
			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				// synchronize type button enable state
				chartTypeBtn.setEnabled(enabled);
			}
		};
		chartPane.setEnabled(false);

		chartPane.addPropertyChangeListener(new PropertyChangeListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String property = evt.getPropertyName();
				Object value = evt.getNewValue();
				if ("hierarchy".equals(property)) {
					HierarchyLevel hl = (HierarchyLevel) value;
					ontologyData.setHierarchyLevel(hl);
					taxonomyData.setHierarchyLevel(hl);

					updateChart();
					updateDetailsTable("");
				} else if ("hideUnknown".equals(property)) {
					boolean doHide = (Boolean) value;
					ontologyData.setHideUnknown(doHide);
					taxonomyData.setHideUnknown(doHide);
				} else if ("groupingLimit".equals(property)) {
					double limit = (Double) value;
					ontologyData.setMinorGroupingLimit(limit);
					taxonomyData.setMinorGroupingLimit(limit);

					updateChart();
					updateDetailsTable("");
				} else if ("selection".equals(property)) {
					updateDetailsTable((Comparable) value);
				}
			}
		});

		// wrap scroll pane in panel with 5dlu margin around it
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		chartMarginPnl.add(chartPane, CC.xy(2, 2));

		// wrap everything in titled panel containing button panel in the top
		// right corner
		JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel(
				"Chart View", chartMarginPnl);
		chartTtlPnl.setRightDecoration(chartBtnPnl);

		return chartTtlPnl;
	}

	/**
	 * Creates and returns the bottom-right chart details panel (wrapped in a
	 * JXTitledPanel)
	 */
	private Component createDetailsPanel() {

		JButton resizeBtn = createResizeButton("summary", "chart");

		JPanel detailsBtnPnl = new JPanel(new FormLayout("22px, 1px", "20px"));
		detailsBtnPnl.setOpaque(false);
		detailsBtnPnl.add(resizeBtn, CC.xy(1, 1));

		JPanel detailsPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));

		detailsTbl = new JXTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Accession",
				"Description" });
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Integer.class;
				case 1:
				case 2:
					return String.class;
				default:
					return getValueAt(0, col).getClass();
				}
			}
		});
		TableConfig.setColumnWidths(detailsTbl, new double[] { 1.0, 3.0, 12.0 });

		final AbstractHyperlinkAction<String> linkAction = new AbstractHyperlinkAction<String>() {
			public void actionPerformed(ActionEvent ev) {
				try {
					System.out.println("click on hyperlink in ResultPanel");
					if (target.matches("^\\d*$")) {
						// if target contains only numerical characters it's probably an NCBI accession,
						// try to use accession of corresponding UniProt entry (if possible)
						ProteinHit proteinHit = Client.getInstance().getDatabaseSearchResult().getProteinHit(target);
						UniProtEntryMPA uniprotEntry = proteinHit.getUniProtEntry();
						if (uniprotEntry != null) {
							target = proteinHit.getAccession();
						} else {
							target = "";
						}
					} else if (target.startsWith("Meta-Protein")) {
						return;
					}
					URLstarter.openURL("http://www.uniprot.org/uniprot/" + target);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		detailsTbl.getColumn(1).setCellRenderer(
				new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component comp = super.getTableCellRendererComponent(
								table, value, isSelected, hasFocus, row, column);
						JXRendererHyperlink hyperlink = (JXRendererHyperlink) comp;
						hyperlink.setHorizontalAlignment(SwingConstants.CENTER);
						
						return hyperlink;
					}
				});

		TableConfig.configureColumnControl(detailsTbl);

		// Add nice striping effect
		detailsTbl.addHighlighter(TableConfig.getSimpleStriping());

		JScrollPane detailsScpn = new JScrollPane(detailsTbl);
		detailsScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScpn.setPreferredSize(new Dimension(100, 100));

		detailsPnl.add(detailsScpn, CC.xy(2, 2));

		JXTitledPanel detailsTtlPnl =
				PanelConfig.createTitledPanel("Chart Details", detailsPnl);
		detailsTtlPnl.setRightDecoration(detailsBtnPnl);

		return detailsTtlPnl;
	}

	/**
	 * Refreshes the details table with proteins stored inside the active data
	 * collection and which is identified by the specified key.
	 * @param key the key by which the proteins are identified
	 */
	private void updateDetailsTable(@SuppressWarnings("rawtypes") Comparable key) {
		if ("".equals(key)) {
			TableConfig.clearTable(detailsTbl);
		} else {
			DefaultTableModel detailsTblMdl =
					(DefaultTableModel) detailsTbl.getModel();
			// clear details table
			detailsTblMdl.setRowCount(0);
			List<ProteinHit> proteinHits = null;
			if (this.chartType instanceof OntologyChart.OntologyChartType) {
				proteinHits = ontologyData.getProteinHits((String) key);
			} else if (this.chartType instanceof TaxonomyChart.TaxonomyChartType) {
				proteinHits = taxonomyData.getProteinHits((String) key);
			}

			if (proteinHits != null) {
				int i = 1;
				for (ProteinHit proteinHit : proteinHits) {
					if (proteinHit instanceof MetaProteinHit) {
						MetaProteinHit metaProteinHit = (MetaProteinHit) proteinHit;
						ProteinHitList proteinHitList = metaProteinHit.getProteinHitList();
						if (proteinHitList.size() == 1) {
							proteinHit = proteinHitList.get(0);
						}
					}
					detailsTblMdl.addRow(new Object[] {
							i++, proteinHit.getAccession(),
							proteinHit.getDescription() });
				}
			}
		}
	}

	/**
	 * Utility method to create the resize/restore widget for use in titled
	 * panels.
	 *
	 * @param nodes2hide
	 *            The names of the MultiSplitPane nodes that are to be
	 *            hidden/restored.
	 * @return The widget button.
	 */
	private JButton createResizeButton(final String... nodes2hide) {

		JButton resizeBtn = new JButton(IconConstants.FRAME_FULL_ICON);
		resizeBtn.setRolloverIcon(IconConstants.FRAME_FULL_ROLLOVER_ICON);
		resizeBtn.setPressedIcon(IconConstants.FRAME_FULL_PRESSED_ICON);
		resizeBtn.setToolTipText("Maximize");

		resizeBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(resizeBtn));

		//		resizeBtn.addMouseListener(new InstantToolTipMouseListener());

		resizeBtn.addActionListener(new ActionListener() {
			boolean maximized = false;

			public void actionPerformed(ActionEvent ae) {
				JButton btn = (JButton) ae.getSource();
				if (maximized) {
					btn.setIcon(IconConstants.FRAME_FULL_ICON);
					btn.setRolloverIcon(IconConstants.FRAME_FULL_ROLLOVER_ICON);
					btn.setPressedIcon(IconConstants.FRAME_FULL_PRESSED_ICON);
					btn.setToolTipText("Maximize");
				} else {
					btn.setIcon(IconConstants.FRAME_TILED_ICON);
					btn.setRolloverIcon(IconConstants.FRAME_TILED_ROLLOVER_ICON);
					btn.setPressedIcon(IconConstants.FRAME_TILED_PRESSED_ICON);
					btn.setToolTipText("Restore");
				}
				for (String node : nodes2hide) {
					msp.getMultiSplitLayout().displayNode(node, maximized);
				}
				maximized = !maximized;
			}
		});

		return resizeBtn;
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
		boolean showAdditionalControls = false;
		if (chartType instanceof OntologyChart.OntologyChartType) {
			chart = ChartFactory.createOntologyChart(
					ontologyData, chartType);
			showAdditionalControls = true;
		} else if (chartType instanceof TaxonomyChart.TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyChart(
                    this.taxonomyData, chartType);
			showAdditionalControls = true;
		} 

		if (chart != null) {
			// insert chart into panel
            this.chartPane.setChart(chart.getChart(), showAdditionalControls);
		} else {
			System.err.println("Chart type could not be determined!");
		}

		this.chartType = chartType;
	}

	/**
	 * Refreshes the overview panel by updating the charts, tables and labels
	 * containing the various experiment statistics.
	 */
	private void updateOverview() {
		new UpdateOverviewTask().execute();
	}

	public boolean isBusy() {
		return busy;
	}

	/**
	 * Sets the busy state of this panel.
	 * @param busy <code>true</code> if busy, <code>false</code> otherwise
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;

		// Set enable state of fetch buttons
		// TODO: probably unsafe as GraphDB is still in the progress of being created
        fetchResultsBtn.setEnabled(!busy);
        processResultsBtn.setEnabled(!busy);

		// Propagate busy state to chart panel
        chartPane.setBusy(busy);

		// Only ever make heat map pane busy, it takes care of turning not busy on its own
        heatMapPn.setBusy(busy || this.heatMapPn.isBusy());
        heatMapPn.setEnabled(!busy);

		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		if (!this.dbPnl.isBusy()) {
			ClientFrame.getInstance().setCursor(cursor);
		}
        this.msp.setCursor(cursor);
		for (Component comp : this.msp.getComponents()) {
			comp.setCursor(cursor);
		}
	}

	/**
	 * Sets the enable state of the 'Process Results' button to the specified
	 * value.
	 * @param b <code>true</code> if enabled, <code>false</code> otherwise
	 */
	public void setProcessingEnabled(boolean b) {
        this.processResultsBtn.setEnabled(b && !busy);
	}

	/**
	 * Returns the database search result panel.
	 * @return the database search result panel
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return this.dbPnl;
	}

	//	/**
	//	 * Returns the spectral similarity search result panel.
	//	 * @return the spectral similarity search result panel
	//	 */
	//	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
	//		return ssPnl;
	//	}

	/**
	 * Returns the graph database result panel.
	 * @return the graph database result panel
	 */
	public GraphDatabaseResultPanel getGraphDatabaseResultPanel() {
		return this.gdbPnl;
	}

	/**
	 * Class to fetch protein database search results in a background thread.
	 * It's possible to fetch from the remote SQL database or from a local file instance.
	 * 
	 * @author A. Behne, R. Heyer
	 */
	private class FetchResultsTask extends SwingWorker<Integer, Object> {
		
		/**
		 * The list of experiments
		 */
		private final LinkedList<Long> experimentList;
		
		/**
		 * Denotes if single or multiple experiments  
		 */
		private final boolean multi_exp;

		/**
		 * Constructor for the FetchResultTask. The experiment list ask which experiments 
		 * should be fetched. If not given it uses the selected experiment into the project panel/ 
		 * @param experimentList. the list of experiment titles
		 */
		public FetchResultsTask(LinkedList<Long> experimentList, boolean multi) {
            this.multi_exp = multi;
			this.experimentList = experimentList;
		}

		@Override
		protected Integer doInBackground() {
			
			// Name of the current thread
			Thread.currentThread().setName("FetchResultsThread");

			/**
			 * Instance of the client
			 */
			Client client = Client.getInstance();

			/**
			 * The new result object.
			 */
			DbSearchResult newResult = null;

			try {
				// Begin appearing busy
                setBusy(true);
                dbPnl.setBusy(true);
                gdbPnl.setBusy(true);

				// Fetch the search result object
				if (!this.multi_exp) {
					// No experiment list selected, get single experiment from client
					newResult = client.getSingleSearchResult();
				} else {
					// Fetch all experiments from the list
					// FIXME processing multiple experiments may result in metaproteins not being formed
					//		 in that case rerun the processing and it should work
					newResult = client.getMultipleSearchResults(this.experimentList);
				}
				// --> this is obsolete? 
				if (newResult != null) {
					// remove this check --> leads to problems based on searchresult-Name
//					if (!newResult.equals(ResultsPanel.this.dbSearchResult)) {
					// Update result object reference
                    dbSearchResult = newResult;
					client.dumpBackupDatabaseSearchResult();
//					}
				}
				client.firePropertyChange("indeterminate", true, false);
				
				return 1;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				client.firePropertyChange("new message", null, "FAILED");
				client.firePropertyChange("indeterminate", true, false);

			}
			return 0;
		}

		@Override
		protected void done() {
			// Get worker result
			int res = 0;
			try {
				res = get().intValue();
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			// If new results have been fetched...
			if (res == 1) {
				// Update overview panel
                updateOverview();
				// Populate tables in database search result panel
                dbPnl.refreshProteinViews();
                ResultsPanel.this.dbPnl.refreshChart(true);
			} else {
				// Stop appearing busy
                setBusy(false);
                dbPnl.setBusy(false);
                gdbPnl.setBusy(false);
                heatMapPn.setBusy(false);
			}
			// Enable 'Export' menu
			ClientFrameMenuBar menuBar =
					(ClientFrameMenuBar) ClientFrame.getInstance().getJMenuBar();
			menuBar.setExportMenuEnabled(true);
		}

	}

	/**
	 * Worker implementation to restore a raw result object cached in a temporary
	 * binary file and to optionally run taxonomy/meta-protein processing on it.
	 * @author A. Behne
	 */
	private class ProcessResultsTask extends SwingWorker<Object, Object> {

		@Override
		protected Object doInBackground() {
			try {
				Thread.currentThread().setName("ProcessResultsThread");

				// begin appearing busy
                setBusy(true);
                ResultsPanel.this.dbPnl.setBusy(true);
                ResultsPanel.this.gdbPnl.setBusy(true);

				// restore result object from backup file if it was processed before
				Client client = Client.getInstance();
				if (!ResultsPanel.this.dbSearchResult.isRaw()) {
                    ResultsPanel.this.dbSearchResult = client.restoreBackupDatabaseSearchResult();
				}

				// process results
				MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(ResultsPanel.this.dbSearchResult, client.getResultParameters());
                ResultsPanel.this.dbSearchResult.setRaw(false);

				return 1;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), e.getMessage(), null, e, ErrorLevel.SEVERE, null));
			}
			return 0;
		}

		@Override
		protected void done() {
			// Update overview panel
            updateOverview();
			// Populate tables in database search result panel
            ResultsPanel.this.dbPnl.refreshProteinViews();
            ResultsPanel.this.dbPnl.refreshChart(true);
		}
	}

	/**
	 * Worker class to process search results and generate statistics from it in
	 * a background thread.
	 * 
	 * @author T. Muth, A. Behne
	 */
	private class UpdateOverviewTask extends SwingWorker<Integer, Object> {

		@Override
		protected Integer doInBackground() {
			Thread.currentThread().setName("UpdateOverviewThread");
			
			if (ResultsPanel.this.dbSearchResult != null) {
				try {
					Set<String> speciesNames = new HashSet<>();
					Set<String> ecNumbers = new HashSet<>();
					Set<KEGGNode> pwNodes = new HashSet<>();

					// gather K and EC numbers for pathway lookup
					for (ProteinHit ph : ResultsPanel.this.dbSearchResult.getProteinHitList()) {
						speciesNames.add(ph.getSpecies());

						// gather K and EC numbers for pathway lookup
						List<String> keys = new ArrayList<>();
						UniProtEntryMPA upe = ph.getUniProtEntry();
						if (upe != null) {
							keys.addAll(upe.getKonumbers());
							keys.addAll(upe.getEcnumbers());
						}

						// perform pathway lookup
						for (String key : keys) {
							List<KEGGNode> nodes = Constants.KEGG_ORTHOLOGY_MAP.get(key);
							if (nodes != null) {
								for (KEGGNode node : nodes) {
									pwNodes.add((KEGGNode) node.getParent());
								}
							}
						}
					}

					// Update statistics labels
                    ResultsPanel.this.totalSpecLbl.setText("" + ResultsPanel.this.dbSearchResult.getTotalSpectrumCount());
                    ResultsPanel.this.identSpecLbl.setText("" + ResultsPanel.this.dbSearchResult.getIdentifiedSpectrumCount());
                    ResultsPanel.this.distPepLbl.setText("" + ResultsPanel.this.dbSearchResult.getDistinctPeptideCount());
					//TODO may throws errors due to concurrent methods interaction
                    ResultsPanel.this.uniqPepLbl.setText("" + ResultsPanel.this.dbSearchResult.getUniquePeptideCount());
                    ResultsPanel.this.totalProtLbl.setText("" + ResultsPanel.this.dbSearchResult.getProteinHitList().size());
                    ResultsPanel.this.metaProtLbl.setText("" + ResultsPanel.this.dbSearchResult.getMetaProteins().size());
                    ResultsPanel.this.speciesLbl.setText("" + speciesNames.size());
                    ResultsPanel.this.enzymesLbl.setText("" + ecNumbers.size());
                    ResultsPanel.this.pathwaysLbl.setText("" + pwNodes.size());

					// Generate chart data objects
					HierarchyLevel hl = ResultsPanel.this.chartPane.getHierarchyLevel();

                    ResultsPanel.this.ontologyData.setHierarchyLevel(hl);
                    ResultsPanel.this.ontologyData.setResult(ResultsPanel.this.dbSearchResult);

                    ResultsPanel.this.taxonomyData.setHierarchyLevel(hl);
                    ResultsPanel.this.taxonomyData.setResult(ResultsPanel.this.dbSearchResult);
					// Refresh chart panel
                    ResultsPanel.this.updateChart(ResultsPanel.this.chartType);

					// Refresh heat map
                    ResultsPanel.this.heatMapPn.updateData(ResultsPanel.this.dbSearchResult);
					
					return 1;
					
				} catch (Exception e) {
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), e.getMessage(), null, e, ErrorLevel.SEVERE, null));
				}
			} 
			return 0;
		}

		@Override
		protected void done() {
			// Get worker result
			int res = 0;
			try {
				res = get().intValue();
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			// If new results have been fetched...
			if (res == 1) {
				// Enable chart panel and heat map
                ResultsPanel.this.chartPane.setEnabled(true);
                ResultsPanel.this.heatMapPn.setEnabled(true);
			}
			// Stop appearing busy
            setBusy(false);
		}

	}

	/**
	 * Returns the dbSearchResultObject from the Resultpanel.
	 * @return dbSearchResultObject
	 */
	public DbSearchResult getDBSearchResultObj(){
		return this.dbSearchResult;
	}
	/**
	 * Sets the dbSearchResultObject of the Resultpanel.
	 * @param 
	 */
	public void setDBSearchResultObj(DbSearchResult dbsearchresults){
        dbSearchResult = dbsearchresults;
	}
	
}
