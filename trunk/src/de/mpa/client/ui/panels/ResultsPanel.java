package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXBusyLabel;
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
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticButtonUI;

import de.mpa.analysis.KeggAccessor;
import de.mpa.analysis.MetaProteinFactory;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
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
import de.mpa.client.ui.chart.HistogramChart.HistogramChartType;
import de.mpa.client.ui.chart.HistogramData;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.PiePlot3DExt;
import de.mpa.client.ui.chart.PieToCategoryDataset;
import de.mpa.client.ui.chart.PieToCategoryPlot;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.chart.TopBarChart.TopBarChartType;
import de.mpa.client.ui.chart.TopData;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.taxonomy.TaxonomyUtils;

/**
 * Panel providing overview information about fetched results in the form of
 * assorted charts and tables.
 * 
 * @author A. Behne
 */
public class ResultsPanel extends JPanel implements Busyable {

	/**
	 * The tab pane containing the various results sub-panels.
	 */
	private ButtonTabbedPane resultsTpn;
	
	/**
	 * The database search results sub-panel.
	 */
	private DbSearchResultPanel dbPnl;

	/**
	 * The spectral similarity results sub-panel.
	 */
	private SpecSimResultPanel ssPnl;

	/**
	 * The graph database sub-panel.
	 */
	private GraphDatabaseResultPanel gdbPnl;

	/**
	 * The split pane layout of the overview panel.
	 */
	// TODO: make split pane references local
	private JXMultiSplitPane msp;

	/**
	 * Chart panel capable of displaying various charts (mainly ontology pie
	 * charts).
	 */
	private ChartPanel chartPnl;

	/**
	 * Scrollbar for the top right chart pane.
	 */
	private JScrollBar chartBar = new JScrollBar(0, 0, 36, 0, 360);

	/**
	 * Combobox for specifying the displayed hierarchy level of pie plots
	 * (protein, peptide, spectrum level).
	 */
	private JComboBox chartHierarchyCbx;

	/**
	 * Checkbox for flagging whether proteins grouped as 'Unknown' in pie charts
	 * shall be hidden.
	 */
	private JCheckBox chartHideUnknownChk;

	/**
	 * Panel containing a checkbox and a spinner for flagging whether pie
	 * segments of a relative size below a certain value threshold shall be
	 * merged into a category labeled 'Others'.
	 */
	private JPanel chartGroupMinorPnl;

	/**
	 * The rotation angle of pie plots.
	 */
	private double chartPieAngle = 36.0;

	/**
	 * The type of chart to be displayed in the top right chart pane.
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
	 * Data container for 'Top 10 Proteins' chart.
	 */
	private TopData topData = new TopData();

	/**
	 * Data container for total ion current histogram chart.
	 */
	private HistogramData histogramData;

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
	private JLabel totalPepLbl;

	/**
	 * Label displaying the number of peptides with unique sequences in the result.
	 */
	private JLabel distPepLbl;

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
	private JButton fetchRemoteBtn;

	/**
	 * Button for fetching search results from a file.
	 */
	private JButton fetchLocalBtn;

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
	 * Constructs a results panel containing the detail views for database,
	 * spectral similarity and de novo search results as bottom-aligned tabs.
	 */
	public ResultsPanel() {
		this.dbPnl = new DbSearchResultPanel();
		this.ssPnl = new SpecSimResultPanel();
		this.gdbPnl = new GraphDatabaseResultPanel();
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
		resultsTpn.addTab("Spectral Similarity Results",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/spectral_search32.png")),
				ssPnl);
		resultsTpn.addTab("Graph Database Results",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/graph32.png")),
				gdbPnl);
		
		// force tab heights by setting size of first tab component
		Component tabComp = resultsTpn.getTabComponentAt(0);
		tabComp.setPreferredSize(new Dimension(
				tabComp.getPreferredSize().width, 40));

		// initially disable all but the overview tab
		for (int i = 1; i < 4; i++) {
			resultsTpn.setEnabledAt(i, false);
		}

		// create navigation button panel
		final JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p",
				"b:p:g"));
		navPnl.setOpaque(false);

		navPnl.add(ClientFrame.getInstance().createNavigationButton(false, !Client
				.getInstance().isViewer()), CC.xy(1, 1));
		navPnl.add(ClientFrame.getInstance().createNavigationButton(true, !Client
				.getInstance().isViewer()), CC.xy(3, 1));

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

		generalPnl.add(new JLabel("Total spectra"), CC.xy(2, 2));
		generalPnl.add(totalSpecLbl, CC.xy(4, 2));
		generalPnl.add(specBarPnl, CC.xy(6, 2));
		generalPnl.add(identSpecLbl, CC.xy(8, 2));
		generalPnl.add(new JLabel("identified spectra"), CC.xy(10, 2));

		// total vs. unique peptides
		totalPepLbl = new JLabel("0");
		distPepLbl = new JLabel("0");
		JPanel pepBarPnl = new BarChartPanel(totalPepLbl, distPepLbl);

		generalPnl.add(new JLabel("Total peptides"), CC.xy(2, 4));
		generalPnl.add(totalPepLbl, CC.xy(4, 4));
		generalPnl.add(pepBarPnl, CC.xy(6, 4));
		generalPnl.add(distPepLbl, CC.xy(8, 4));
		generalPnl.add(new JLabel("distinct peptides"), CC.xy(10, 4));

		// total vs. species-specific proteins
		totalProtLbl = new JLabel("0");
		metaProtLbl = new JLabel("0");
		JPanel protBarPnl = new BarChartPanel(totalProtLbl, metaProtLbl);

		generalPnl.add(new JLabel("Total proteins"), CC.xy(2, 6));
		generalPnl.add(totalProtLbl, CC.xy(4, 6));
		generalPnl.add(protBarPnl, CC.xy(6, 6));
		generalPnl.add(metaProtLbl, CC.xy(8, 6));
		generalPnl.add(new JLabel("meta-proteins"), CC.xy(10, 6));

		speciesLbl = new JLabel("0");
		enzymesLbl = new JLabel("0");
		pathwaysLbl = new JLabel("0");
		generalPnl.add(new JLabel("Distinct species"), CC.xy(2, 8));
		generalPnl.add(speciesLbl, CC.xy(4, 8));
		generalPnl.add(new JLabel("Distinct enzymes"), CC.xy(2, 10));
		generalPnl.add(enzymesLbl, CC.xy(4, 10));
		generalPnl.add(new JLabel("Distinct pathways"), CC.xy(2, 12));
		generalPnl.add(pathwaysLbl, CC.xy(4, 12));

		// panel containing buttons to fetch results
		FormLayout layout = new FormLayout("p:g, 5dlu, p:g", "f:p:g");
		layout.setColumnGroups(new int[][] { { 1, 3 } });
		JPanel fetchPnl = new JPanel(layout);
		
		final JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		settingsBtn.setEnabled(!Client.getInstance().isViewer()); 
		settingsBtn.setRolloverIcon(IconConstants.SETTINGS_SMALL_ROLLOVER_ICON);
		settingsBtn.setPressedIcon(IconConstants.SETTINGS_SMALL_PRESSED_ICON);		
		settingsBtn.setPreferredSize(new Dimension(23, 23));

		fetchRemoteBtn = new JButton(
				"<html><center>Fetch Results<br>from DB</center></html>",
				IconConstants.GO_DB_ICON) {
			@Override
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				settingsBtn.setEnabled(b);
			}
		};
		fetchRemoteBtn.setEnabled(!Client.getInstance().isViewer()); 
		fetchRemoteBtn.setRolloverIcon(IconConstants.GO_DB_ROLLOVER_ICON);
		fetchRemoteBtn.setPressedIcon(IconConstants.GO_DB_PRESSED_ICON);		
		fetchRemoteBtn.setIconTextGap(7);
		fetchRemoteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				new FetchResultsTask(null).execute();
			}
		});
		fetchRemoteBtn.setUI(new PlasticButtonUI() {
			@Override
			protected void paintFocus(Graphics g, AbstractButton b,
					Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
				int topLeftInset = 3;
		        int width = b.getWidth() - 1 - topLeftInset * 2;
		        int height = b.getHeight() - 1 - topLeftInset * 2;
				
				g.setColor(this.getFocusColor());
				g.drawLine(2, 2, 2, 2 + height + 1);
				g.drawLine(2, 2 + height + 1, 2 + width - 20, 2 + height + 1);
				g.drawLine(2 + width - 20, 2 + height + 1, 2 + width - 20, 2 + height - 20);
				g.drawLine(2 + width - 20, 2 + height - 20, 2 + width + 1, 2 + height - 20);
				g.drawLine(2 + width + 1, 2 + height - 20, 2 + width + 1, 2);
				g.drawLine(2 + width + 1, 2, 2, 2);
			}
		});
		
		fetchRemoteBtn.setLayout(new FormLayout("0px:g, p", "0px:g, p"));
		fetchRemoteBtn.add(settingsBtn, CC.xy(2, 2));
		fetchRemoteBtn.setMargin(new Insets(-3, -3, -3, -3));
		
		fetchLocalBtn = new JButton("<html><center>Fetch Results<br>from File</center></html>", IconConstants.GO_FOLDER_ICON);
		fetchLocalBtn.setRolloverIcon(IconConstants.GO_FOLDER_ROLLOVER_ICON);
		fetchLocalBtn.setPressedIcon(IconConstants.GO_FOLDER_PRESSED_ICON);
		fetchLocalBtn.setIconTextGap(10);

		fetchLocalBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(Constants.MPA_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnValue = chooser.showOpenDialog(ClientFrame.getInstance());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File importFile = chooser.getSelectedFile();
					new FetchResultsTask(importFile).execute();
				}
			}
		});

		fetchPnl.add(fetchRemoteBtn, CC.xy(1, 1));
		fetchPnl.add(fetchLocalBtn, CC.xy(3, 1));

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
		for (ChartType oct : OntologyChartType.values()) {
			tmp.add(oct);
		}
		for (ChartType tct : TaxonomyChartType.values()) {
			tmp.add(tct);
		}
		tmp.add(TopBarChartType.PROTEINS);
		tmp.add(HistogramChartType.TOTAL_ION_HIST);
		final ChartType[] chartTypes = tmp.toArray(new ChartType[0]);

		// create and configure button for chart type selection
		final JToggleButton chartTypeBtn = new JToggleButton(
				IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ICON));
		chartTypeBtn.setRolloverIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_ROLLOVER_ICON));
		chartTypeBtn.setPressedIcon(IconConstants
				.createArrowedIcon(IconConstants.PIE_CHART_PRESSED_ICON));
		chartTypeBtn.setToolTipText("Select Chart Type");

		chartTypeBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(chartTypeBtn));

//		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
//		chartTypeBtn.addMouseListener(ittml);

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

					if (newChartType instanceof TopBarChartType) {
						updateDetailsTable(null);
					}
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
				ResultsPanel.this.updateChart(chartType);
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
				pieDataset.setValue("RPM", 8);
				pieDataset.setValue("DNRPM", 2);
				return pieDataset;
			}
		};
		chartPnl = new ChartPanel(ChartFactory.createOntologyChart(
				dummyData, OntologyChartType.BIOLOGICAL_PROCESS).getChart()) {
			@Override
			protected void paintChildren(Graphics g) {
				// Fade chart if disabled
				if (!this.isEnabled()) {
					g.setColor(new Color(255, 255, 255, 192));
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					
					// Paint notification string if no data has been loaded yet
					if (!ResultsPanel.this.isBusy()) {
						Graphics2D g2d = (Graphics2D) g;
						String str = "no results loaded";
						int strWidth = g2d.getFontMetrics().stringWidth(str);
						int strHeight = g2d.getFontMetrics().getHeight();
						float xOffset = this.getWidth() / 2.0f - strWidth / 2.0f;
						float yOffset = this.getHeight() / 1.95f;
						g2d.fillRect((int) xOffset - 2, (int) yOffset - g2d.getFontMetrics().getAscent() - 1, strWidth + 4, strHeight + 4);
						
						g2d.setColor(Color.BLACK);
						g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
						g2d.drawString(str, xOffset, yOffset);
					}
				}
				super.paintChildren(g);
			}
			
			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				chartTypeBtn.setEnabled(enabled);
				if ((chartType instanceof OntologyChartType) || (chartType instanceof TaxonomyChartType)) {
					chartHierarchyCbx.setEnabled(enabled);
					chartHideUnknownChk.setEnabled(enabled);
					chartGroupMinorPnl.setEnabled(enabled);
				}
				
			}
		};
		chartPnl.setLayout(new FormLayout(
				"r:p:g, 2dlu, p, 2dlu, l:p:g",
				"0px:g, p, 2dlu"));
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setOpaque(false);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));
		
		// create mouse adapter to interact with plot sections
		MouseAdapter ma = new MouseAdapter() {
			private Comparable highlightedKey = null;
			private Comparable selectedKey = null;
			private Action saveAsAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					Plot plot = getPlot();
					if (plot != null) {
						JFileChooser fc = new JFileChooser();
						fc.setFileFilter(Constants.CSV_FILE_FILTER);
						fc.setAcceptAllFileFilterUsed(false);
						//						fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fc.setMultiSelectionEnabled(false);
						int option = fc.showSaveDialog(ClientFrame.getInstance());

						if (option == JFileChooser.APPROVE_OPTION) {
							File selectedFile = fc.getSelectedFile();

							if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
								selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
							}

							setCursor(new Cursor(Cursor.WAIT_CURSOR));

							try {
								if (selectedFile.exists()) {
									selectedFile.delete();
								}
								selectedFile.createNewFile();

								BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));

								if (plot instanceof PiePlot) {
									PieDataset dataset = ((PiePlot) plot).getDataset();
									for (int i = 0; i < dataset.getItemCount(); i++) {
										Comparable key = dataset.getKey(i);
										Number value = dataset.getValue(key);
										writer.append("\"" + key + "\"\t" + value);
										writer.newLine();
									}
								} else {
									// TODO: implement bar chart export
								}
								writer.flush();
								writer.close();

							} catch (IOException ex) {
								JXErrorPane.showDialog(ClientFrame.getInstance(),
										new ErrorInfo("Severe Error", ex.getMessage(), null, null, ex, ErrorLevel.SEVERE, null));
							}

							setCursor(null);
						}
					}
				}
			};

			@Override
			public void mouseMoved(MouseEvent me) {
				Plot plot = getPlot();
				if (plot instanceof PiePlot3DExt) {
					PiePlot3DExt piePlot = (PiePlot3DExt) plot;
					Comparable key = piePlot.getSectionKeyForPoint(me.getPoint());
					if (key != highlightedKey) {
						for (Object dataKey : piePlot.getDataset().getKeys()) {
							if (dataKey != selectedKey) {
								piePlot.setExplodePercent((Comparable) dataKey,
										0.0);
							}
						}
						if ((key != null) && (key != selectedKey)) {
							piePlot.setExplodePercent(key, 0.1);
						}
						highlightedKey = key;
					}
				} else if (plot instanceof PieToCategoryPlot) {
					PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
					EntityCollection entities = chartPnl.getChartRenderingInfo().getEntityCollection();
					if (entities != null) {
		                Insets insets = chartPnl.getInsets();
		                ChartEntity entity = entities.getEntity(
		                        (int) ((me.getX() - insets.left) / chartPnl.getScaleX()),
		                        (int) ((me.getY() - insets.top) / chartPnl.getScaleY()));
		                if ((entity != null) && (entity instanceof CategoryItemEntity)) {
		                	CategoryItemEntity catEntity = (CategoryItemEntity) entity;
		                	Comparable key = catEntity.getColumnKey();
		                	if (key != highlightedKey) {
		                		catPlot.setHighlightedKey(key);
								highlightedKey = key;
		                	}
		                } else {
		                	catPlot.setHighlightedKey(null);
							highlightedKey = null;
		                }
		            }
				}
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.isPopupTrigger()) {
					maybeShowPopup(me);
				} else {
					Plot plot = getPlot();
					if (plot instanceof PiePlot3DExt) {
						PiePlot3DExt piePlot = (PiePlot3DExt) plot;
						if (selectedKey != null) {
							// clear old selection
							piePlot.setExplodePercent(selectedKey, 0.0);
						}
						if (highlightedKey != null) {
							piePlot.setExplodePercent(highlightedKey, 0.2);
							if (highlightedKey != selectedKey) {
								// update table if new section got clicked
								updateDetailsTable(highlightedKey);
							}
						} else {
							TableConfig.clearTable(detailsTbl);
						}
						selectedKey = highlightedKey;
					} else if (plot instanceof PieToCategoryPlot) {
						PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
						if (highlightedKey != null) {
							if (highlightedKey != selectedKey) {
								// update table if new section got clicked
								updateDetailsTable(highlightedKey);
							}
						} else {
							TableConfig.clearTable(detailsTbl);
						}
						catPlot.setSelectedKey(highlightedKey);
						selectedKey = highlightedKey;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent me) {
				// re-validate fields in case chart type has been changed
				highlightedKey = null;
				selectedKey = null;
				Plot plot = getPlot();
				if (plot instanceof PiePlot3DExt) {
					PiePlot3DExt piePlot = (PiePlot3DExt) plot;
					for (Object dataKey : piePlot.getDataset().getKeys()) {
						Comparable key = (Comparable) dataKey;
						double explodePercent = piePlot.getExplodePercent(key);
						if (explodePercent > 0.1) {
							selectedKey = key;
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent me) {
				maybeShowPopup(me);
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				maybeShowPopup(me);
			}

			/**
			 * Convenience method to show a 'Save as CSV...' context menu.
			 * @param me the mouse event
			 */
			private void maybeShowPopup(MouseEvent me) {
				if (me.isPopupTrigger()) {
					Plot plot = getPlot();
					if (plot != null) {
						JPopupMenu popup = new JPopupMenu();

						JMenuItem item = new JMenuItem(saveAsAction);
						item.setText("Save as CSV...");
						item.setIcon(IconConstants.FILE_CSV);

						popup.add(item);

						popup.show(me.getComponent(), me.getX(), me.getY());
					}
				}
			}

			/** 
			 * Utility method to get valid pie plot.
			 */
			private Plot getPlot() {
				if (chartPnl.isEnabled()) {
					JFreeChart chart = chartPnl.getChart();
					if (chart != null) {
//						if (chart.getPlot() instanceof PiePlot3DExt) {
//							return (PiePlot3DExt) chart.getPlot();
//						}
						return chart.getPlot();
					}
				}
				return null;
			}
		};
		chartPnl.removeMouseListener(chartPnl.getMouseListeners()[1]);
		chartPnl.removeMouseMotionListener(chartPnl.getMouseMotionListeners()[1]);
		chartPnl.addMouseListener(ma);
		chartPnl.addMouseMotionListener(ma);

		// create combobox to control what counts to display in the plots
		// (protein/peptide/spectrum count)
		chartHierarchyCbx = new JComboBox(HierarchyLevel.values());
		chartHierarchyCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					HierarchyLevel hl = (HierarchyLevel) evt.getItem();

					ontologyData.setHierarchyLevel(hl);
					taxonomyData.setHierarchyLevel(hl);

					updateChart(chartType);

					updateDetailsTable("");
				}
			}
		});
		chartHierarchyCbx.setEnabled(false);

		chartHideUnknownChk = new JCheckBox("Hide Unknown", false);
		chartHideUnknownChk.setOpaque(false);
		chartHideUnknownChk.setEnabled(false);
		chartHideUnknownChk.addItemListener(new ItemListener() {
			private boolean doHide;
			@Override
			public void itemStateChanged(ItemEvent evt) {
				doHide = (evt.getStateChange() == ItemEvent.SELECTED);
				ontologyData.setHideUnknown(doHide);
				taxonomyData.setHideUnknown(doHide);
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
						chartHideUnknownChk.setEnabled(false);
						Plot plot = chartPnl.getChart().getPlot();
						DefaultPieDataset dataset;
						if (plot instanceof PiePlot) {
							dataset = (DefaultPieDataset) ((PiePlot) plot).getDataset();
						} else if (plot instanceof PieToCategoryPlot) {
							dataset = (DefaultPieDataset) ((PieToCategoryDataset) ((PieToCategoryPlot) plot).getDataset()).getPieDataset();
						} else {
							// abort
							return null;
						}
						String unknownKey = "Unknown";
						if (doHide) {
							double val = dataset.getValue(unknownKey).doubleValue();
							for (int i = 0; i < 11; i++) {
								double tmp = 1.0 - i / 10.0;
								double newVal = val * tmp * tmp;
								dataset.setValue(unknownKey, newVal);
								if (newVal > 0.0) {
									Thread.sleep(33);
								} else {
									break;
								}
							}
							dataset.remove(unknownKey);
						} else {
							double val;
							if (chartType instanceof OntologyChartType) {
								val = ontologyData.getDataset().getValue(unknownKey).doubleValue();
							} else {
								val = taxonomyData.getDataset().getValue(unknownKey).doubleValue();
							}
							dataset.insertValue(0, unknownKey, 0);
							for (int i = 0; i < 11; i++) {
								double tmp = i / 10.0;
								double newVal = val * tmp * tmp;
								if (newVal <= 0.0) {
									continue;
								}
								dataset.setValue(unknownKey, newVal);
								if (newVal < val) {
									Thread.sleep(33);
								} else {
									break;
								}
							}
						}
						return null;
					}
					@Override
					protected void done() {
						chartHideUnknownChk.setEnabled(true);
					};

				}.execute();
			}
		});

		final JCheckBox chartGroupMinorChk = new JCheckBox("Group segments <", true);
		chartGroupMinorChk.setOpaque(false);
		chartGroupMinorChk.setEnabled(false);
		
		final JSpinner chartGroupMinorSpn = new JSpinner(new SpinnerNumberModel(0.01, 0.0, 1.0, 0.001));
		chartGroupMinorSpn.setEditor(new JSpinner.NumberEditor(chartGroupMinorSpn, "0.0%"));
		final ChangeListener groupListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				if (chartGroupMinorSpn.isEnabled()) {
					// update grouping limit of data containers
					Double value = (Double) chartGroupMinorSpn.getValue();
					ontologyData.setMinorGroupingLimit(value);
					taxonomyData.setMinorGroupingLimit(value);
				}
				// refresh datasets and repaint chart
				Plot plot = chartPnl.getChart().getPlot();
				if ((chartType instanceof OntologyChartType)) {
					PieDataset dataset = ontologyData.getDataset();
					if (ontologyData.getShowAsPie()) {
						PiePlot piePlot = (PiePlot) plot;
//						// hack to reset colors
//						piePlot.clearSectionPaints(false);
//						piePlot.setDrawingSupplier(new DefaultDrawingSupplier(), false);
						piePlot.setDataset(dataset);
					} else {
						PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
						catPlot.setDataset(dataset);
					}
				} else if (chartType instanceof TaxonomyChartType) {
					PieDataset dataset = taxonomyData.getDataset();
					if (taxonomyData.getShowAsPie()) {
						PiePlot piePlot = (PiePlot) plot;
//						// hack to reset colors
//						piePlot.clearSectionPaints(false);
//						piePlot.setDrawingSupplier(new DefaultDrawingSupplier(), false);
						piePlot.setDataset(dataset);
					} else {
						PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
						catPlot.setDataset(dataset);
					}
				}
			}
		};
		chartGroupMinorSpn.addChangeListener(groupListener);

		chartGroupMinorChk.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					Double value = (Double) chartGroupMinorSpn.getValue();
					ontologyData.setMinorGroupingLimit(value);
					taxonomyData.setMinorGroupingLimit(value);
					chartGroupMinorSpn.setEnabled(true);
				} else {
					ontologyData.setMinorGroupingLimit(0.0);
					taxonomyData.setMinorGroupingLimit(0.0);
					chartGroupMinorSpn.setEnabled(false);
				}
				groupListener.stateChanged(new ChangeEvent(chartGroupMinorChk));
			}
		});

		chartGroupMinorPnl = new JPanel(new FormLayout("p, 2dlu, 50px", "p")) {
			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				chartGroupMinorChk.setEnabled(enabled);
				chartGroupMinorSpn.setEnabled(enabled);
			}
		};
		chartGroupMinorPnl.setOpaque(false);
		chartGroupMinorPnl.add(chartGroupMinorChk, CC.xy(1, 1));
		chartGroupMinorPnl.add(chartGroupMinorSpn, CC.xy(3, 1));
		
		JXBusyLabel busyLbl = new JXBusyLabel(new Dimension(70, 70));
		busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
		busyLbl.setVisible(false);
		chartPnl.setEnabled(false);
		
		chartPnl.add(busyLbl, CC.xywh(1, 1, 5, 3));
		chartPnl.add(chartHierarchyCbx, CC.xy(1, 2));
		chartPnl.add(chartHideUnknownChk, CC.xy(3, 2));
		chartPnl.add(chartGroupMinorPnl, CC.xy(5, 2));

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
					if (target.matches("^\\d*$")) {
						// if target contains only numerical characters it's probably an NCBI accession,
						// try to use accession of corresponding UniProt entry (if possible)
						ProteinHit proteinHit = Client.getInstance().getDatabaseSearchResult().getProteinHit(target);
						ReducedUniProtEntry uniprotEntry = proteinHit.getUniProtEntry();
						if (uniprotEntry != null) {
							target = proteinHit.getAccession();
						} else {
							target = "";
						}
					} else if (target.startsWith("Meta-Protein")) {
						return;
					}
					Desktop.getDesktop().browse(
							new URI("http://www.uniprot.org/uniprot/" + target));
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
	protected void updateDetailsTable(Comparable key) {
		DefaultTableModel detailsTblMdl = 
				(DefaultTableModel) detailsTbl.getModel();
		detailsTblMdl.setRowCount(0);
		List<ProteinHit> proteinHits = null;
		if (chartType instanceof OntologyChartType) {
			proteinHits = ontologyData.getProteinHits((String) key);
		} else if (chartType instanceof TaxonomyChartType) {
			proteinHits = taxonomyData.getProteinHits((String) key);
		} else if (chartType instanceof TopBarChartType) {
			proteinHits = topData.getProteinHits(null);
		}
		if (proteinHits != null) {
			int i = 1;
			for (ProteinHit proteinHit : proteinHits) {
				detailsTblMdl.addRow(new Object[] {
						i++, proteinHit.getAccession(),
						proteinHit.getDescription() });
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
	 * Refreshes the chart updating its content reflecting the specified chart
	 * type.
	 * @param chartType the type of chart to be displayed.
	 */
	private void updateChart(ChartType chartType) {
		Chart chart = null;

		// create chart instance
		boolean showAdditionalControls = false;
		if (chartType instanceof OntologyChartType) {
			chart = ChartFactory.createOntologyChart(
					ontologyData, chartType);
			showAdditionalControls = true;
		} else if (chartType instanceof TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyChart(
					taxonomyData, chartType);
			showAdditionalControls = true;
		} else if (chartType instanceof TopBarChartType) {
			chart = ChartFactory.createTopBarChart(
					topData, chartType);
		} else if (chartType instanceof HistogramChartType) {
			chart = ChartFactory.createHistogramChart(
					histogramData, chartType);
		}
		
		if (chart != null) {
			JFreeChart jChart = chart.getChart();
			final Plot plot = jChart.getPlot();
			boolean isPie = plot instanceof PiePlot;
			if (isPie) {
				// enable chart scroll bar
				chartBar.setMaximum(360);
				chartBar.setValue((int) chartPieAngle);
				((PiePlot) plot).setStartAngle(chartPieAngle);
			} else {
				// disable chart scroll bar
				double temp = chartPieAngle;
				chartBar.setMaximum(0);
				chartPieAngle = temp;
			}
			// hide/show additional controls
			// TODO: show controls for ontology/taxonomy bar charts (and make sure they work)
			chartHierarchyCbx.setVisible(showAdditionalControls);
			chartHierarchyCbx.setEnabled(showAdditionalControls);
			chartHideUnknownChk.setVisible(showAdditionalControls);
			chartHideUnknownChk.setEnabled(showAdditionalControls);
			chartGroupMinorPnl.setVisible(showAdditionalControls);
			chartGroupMinorPnl.setEnabled(showAdditionalControls);

			// insert chart into panel
			chartPnl.setChart(jChart);
		} else {
			System.err.println("Chart type could not be determined!");
		}

		this.chartType = chartType;
	}

	/**
	 * Refreshes the overview panel by updating the charts, tables and labels
	 * containing the various experiment statistics.
	 */
	protected void updateOverview() {
		new UpdateOverviewTask().execute();
	}
	
	public boolean isBusy() {
		return this.busy;
	}

	/**
	 * Sets the busy state of this panel.
	 * @param busy <code>true</code> if busy, <code>false</code> otherwise
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
		
		// Set enable state of fetch buttons
		// TODO: probably unsafe as GraphDB is still in the progress of being created
		this.fetchRemoteBtn.setEnabled(!busy && !Client.getInstance().isViewer());
		this.fetchLocalBtn.setEnabled(!busy);

		// Hide/unhide busy label of chart panel
		JXBusyLabel busyLbl = (JXBusyLabel) this.chartPnl.getComponent(0);
		busyLbl.setBusy(busy);
		busyLbl.setVisible(busy);
		this.chartPnl.setEnabled(!busy);
		
		// Only ever make heat map pane busy, it takes care of turning not busy on its own
		this.heatMapPn.setBusy(busy || heatMapPn.isBusy());
		this.heatMapPn.setEnabled(!busy);
		
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		if (!dbPnl.isBusy()) {
			ClientFrame.getInstance().setCursor(cursor);
		}
		msp.setCursor(cursor);
		for (Component comp : msp.getComponents()) {
			comp.setCursor(cursor);
		}
	}

//	/**
//	 * Convenience method to allow sub-panels inside the tab pane to control the
//	 * busy state of their corresponding tab button.
//	 * @param panel the sub-panel
//	 * @param busy <code>true</code> if the panel is busy, <code>false</code> otherwise
//	 */
//	public void setBusy(JPanel panel, boolean busy) {
//		this.resultsTpn.setBusyAt(this.resultsTpn.indexOfComponent(panel), busy);
//	}

	/**
	 * Returns the database search result panel.
	 * @return the database search result panel
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return dbPnl;
	}

	/**
	 * Returns the spectral similarity search result panel.
	 * @return the spectral similarity search result panel
	 */
	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
		return ssPnl;
	}

	/**
	 * Returns the de novo search result panel.
	 * @return the de novo search result panel
	 */
	// TODO: rename method
	public GraphDatabaseResultPanel getDeNovoSearchResultPanel() {
		return gdbPnl;
	}

	/**
	 * Class to fetch protein database search results in a background thread.
	 * It's possible to fetch from the remote SQL database or from a local file instance.
	 * 
	 * @author A. Behne, R. Heyer
	 */
	private class FetchResultsTask extends SwingWorker<Integer, Object> {
	
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
		public FetchResultsTask(File file) {
			this.file = file;
		}
		
		@Override
		protected Integer doInBackground() {
			try {
				// Begin appearing busy
				ResultsPanel.this.setBusy(true);
				ResultsPanel.this.dbPnl.setBusy(true);
				ResultsPanel.this.gdbPnl.setBusy(true);
	
				Client client = Client.getInstance();
				ClientFrame clientFrame = ClientFrame.getInstance();
				DbSearchResult newResult;

				// Fetch the database search result object
				if (this.file == null) {
					newResult = client.getDatabaseSearchResult(
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
					clientFrame.getGraphDatabaseResultPanel().setResultsButtonEnabled(true);
					client.firePropertyChange("new message", null, "READING RESULTS FILE FINISHED");
					client.firePropertyChange("indeterminate", true, false);
				}
	
				// Fetching UniProt entries and taxonomic information.
				if (!newResult.equals(ResultsPanel.this.dbSearchResult)) {
					if (!newResult.isEmpty() && !client.isViewer()) {
	
						// Get various hit lists from result object
						ProteinHitList metaProteins = newResult.getMetaProteins();
						ProteinHitList proteinList = (ProteinHitList) newResult.getProteinHitList();
						Set<PeptideHit> peptideSet = proteinList.getPeptideSet();	// all distinct peptides
	
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY");
						Client.getInstance().firePropertyChange("resetall", -1L, (long) (peptideSet.size() + proteinList.size() + metaProteins.size()));
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) peptideSet.size());
	
						// Define common peptide taxonomy for each peptide
						TaxonomyUtils.determinePeptideTaxonomy(peptideSet);
						
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY FINISHED");
	
						// Determine protein taxonomy
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY");
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) proteinList.size());
	
						// Define protein taxonomy by common tax ID of peptides
						TaxonomyUtils.determineProteinTaxonomy(proteinList);
						
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY FINISHED");
	
						Client.getInstance().firePropertyChange("new message", null, "CONDENSING META-PROTEINS");
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) metaProteins.size());
	
						// Combine proteins to metaproteins
						// TODO: configure leucine/isoleucine merging parameter
						MetaProteinFactory.condenseMetaProteins(metaProteins, false);
	
						Client.getInstance().firePropertyChange("new message", null, "CONDENSING META-PROTEINS FINISHED");
	
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY");
						Client.getInstance().firePropertyChange("resetcur", -1L, (long) metaProteins.size());
						
						// Determine meta-protein taxonomy
						MetaProteinFactory.determineMetaProteinTaxonomy(metaProteins);
						
						Client.getInstance().firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY FINISHED");
					}
					
					// Update result object reference
					ResultsPanel.this.dbSearchResult = newResult;
					client.setDatabaseSearchResult(newResult);
					
					return 1;
				} else {
					// Stop appearing busy
					ResultsPanel.this.setBusy(false);
					ResultsPanel.this.dbPnl.setBusy(false);
					ResultsPanel.this.gdbPnl.setBusy(false);
					ResultsPanel.this.heatMapPn.setBusy(false);
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
			// Get worker result
			int res = 0;
			try {
				res = this.get().intValue();
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			
			// If new results have been fetched...
			if (res == 1) {
				// Update overview panel
				ResultsPanel.this.updateOverview();
				// Populate tables in database search result panel
				ResultsPanel.this.dbPnl.refreshTables(this.file);
			}
			
			// Enable 'Export' and 'Save Project' functionalities of menu bar
			ClientFrameMenuBar menuBar = (ClientFrameMenuBar) ClientFrame.getInstance().getJMenuBar();
			menuBar.setExportCSVResultsEnabled(true);
			menuBar.setSaveProjectEnabled(true);
			menuBar.setExportGraphMLEnabled(true);
		}
	
	}

	/**
	 * Worker class to process search results and generate statistics from it in
	 * a background thread.
	 * 
	 * @author T. Muth, A. Behne
	 */
	private class UpdateOverviewTask extends SwingWorker {

		@Override
		protected Object doInBackground() {
			Set<String> speciesNames = new HashSet<String>();
			Set<String> ecNumbers = new HashSet<String>();
			Set<String> kNumbers = new HashSet<String>();
			Set<Short> pathwayIDs = new HashSet<Short>();
			for (ProteinHit ph : ResultsPanel.this.dbSearchResult.getProteinHitList()) {
				speciesNames.add(ph.getSpecies());
				ReducedUniProtEntry uniprotEntry = ph.getUniProtEntry();
				if (uniprotEntry != null) {
					ecNumbers.addAll(uniprotEntry.getEcNumbers());		
					kNumbers.addAll(uniprotEntry.getKNumbers());
				}
				for (String ec : ecNumbers) {
					List<Short> pathwaysByEC = KeggAccessor.getInstance().getPathwaysByEC(ec);
					if (pathwaysByEC != null) {
						pathwayIDs.addAll(pathwaysByEC);
					}
				}
				for (String ko : kNumbers) {
					List<Short> pathwaysByKO = KeggAccessor.getInstance().getPathwaysByKO(ko);
					if (pathwaysByKO != null) {
						pathwayIDs.addAll(pathwaysByKO);
					}
				}
			}

			// Update statistics labels
			totalSpecLbl.setText("" + dbSearchResult.getTotalSpectrumCount());
			identSpecLbl.setText("" + dbSearchResult.getIdentifiedSpectrumCount());
			totalPepLbl.setText("" + dbSearchResult.getTotalPeptideCount());
			distPepLbl.setText("" + dbSearchResult.getUniquePeptideCount());
			totalProtLbl.setText("" + dbSearchResult.getProteinHitList().size());
			metaProtLbl.setText("" + dbSearchResult.getMetaProteins().size());
			speciesLbl.setText("" + speciesNames.size());
			enzymesLbl.setText("" + ecNumbers.size());
			pathwaysLbl.setText("" + pathwayIDs.size());

			// Generate chart data objects
			HierarchyLevel hl = (HierarchyLevel) chartHierarchyCbx.getSelectedItem();
			
			ontologyData.setHierarchyLevel(hl);
			ontologyData.setResult(dbSearchResult);
			
			taxonomyData.setHierarchyLevel(hl);
			taxonomyData.setResult(dbSearchResult);
			
			topData.setResult(dbSearchResult);
			// FIXME: Is this histogram really necessary?
//			histogramData = new HistogramData(dbSearchResult, 40);

			// Refresh chart panel showing default ontology pie chart
			updateChart(OntologyChartType.BIOLOGICAL_PROCESS);
			// Refresh heat map
			heatMapPn.updateData(Client.getInstance().getDatabaseSearchResult());
			
			return 0;
		}

		@Override
		protected void done() {
			// Enable chart panel and heat map
			chartPnl.setEnabled(true);
			heatMapPn.setEnabled(true);

			// Stop appearing busy
			ResultsPanel.this.setBusy(false);
		}

	}
	
}
