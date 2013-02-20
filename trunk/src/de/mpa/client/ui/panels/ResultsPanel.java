package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.MatrixSeries;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.PrimaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.KeggAccessor;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.InstantToolTipMouseListener;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RoundedHoverButtonUI;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HeatMapPane;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.PiePlot3DExt;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.chart.TopData;
import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;
import de.mpa.client.ui.chart.TopBarChart.TopBarChartType;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.util.ColorUtils;

/**
 * Panel providing overview information about fetched results in the form of
 * assorted charts and tables.
 * 
 * @author A. Behne
 */
public class ResultsPanel extends JPanel {

	/**
	 * The split pane layout for the panel.
	 */
	private MultiSplitLayout msl;

	/**
	 * The database search results panel.
	 */
	private DbSearchResultPanel dbPnl;
	
	/**
	 * The spectral similarity results panel.
	 */
	private SpecSimResultPanel ssPnl;
	
	/**
	 * The de novo and BLAST results panel. TODO: decide fate of de novo/BLAST results panel
	 */
	private DeNovoResultPanel dnPnl;

	/**
	 * Button for showing button to select the type of chart to be displayed
	 * inside the chart panel.
	 */
	private JToggleButton chartTypeBtn;

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
	private JComboBox hierarchyCbx;

	/**
	 * Checkbox for flagging whether proteins grouped as 'Unknown' in pie charts
	 * shall be hidden.
	 */
	private JCheckBox hideChk;
	
	/**
	 * The rotation angle of pie plots.
	 */
	private double pieChartAngle = 36.0;

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
	private TaxonomyData taxonomyData;
	
	/**
	 * Data container for 'Top 10 Proteins' chart.
	 */
	private TopData topData;

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
	private JLabel uniquePepLbl;
	
	/**
	 * Label displaying the total count of proteins identified by the search.
	 */
	private JLabel totalProtLbl;
	
	/**
	 * Label displaying the number of proteins specific to unique species
	 * identified in the search.
	 */
	private JLabel specificProtLbl;
	
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
	 * Constructs a results panel containing the detail views for database,
	 * spectral similarity and de novo search results as bottom-aligned tabs.
	 */
	public ResultsPanel() {
		this.dbPnl = new DbSearchResultPanel(this);
		this.ssPnl = new SpecSimResultPanel();
		this.dnPnl = new DeNovoResultPanel();
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
		final JTabbedPane resTpn = new JTabbedPane(JTabbedPane.BOTTOM);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets);

		JPanel ovPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));

		String layoutDef = "(ROW (LEAF weight=0.5 name=summary) (COLUMN weight=0.5 (LEAF weight=0.5 name=chart) (LEAF weight=0.5 name=details)";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

		msl = new MultiSplitLayout(modelRoot);

		final JXMultiSplitPane msp = new JXMultiSplitPane(msl);
		msp.setDividerSize(12);

		msp.add(createSummaryPanel(), "summary");
		msp.add(createChartPanel(), "chart");
		msp.add(createDetailsPanel(), "details");
		// msp.add(PanelConfig.createTitledPanel("Placeholder",
		// new JLabel("open to suggestions...", SwingConstants.CENTER)),
		// "placeholder");

		ovPnl.add(msp, CC.xy(2, 2));

		resTpn.addTab(" ", ovPnl);
		resTpn.addTab(" ", dbPnl);
		resTpn.addTab(" ", ssPnl);
		resTpn.addTab(" ", dnPnl);

		ClientFrame clientFrame = ClientFrame.getInstance();
		resTpn.setTabComponentAt(0, clientFrame.createTabButton(
				"Overview",
				new ImageIcon(getClass().getResource(
						"/de/mpa/resources/icons/overview32.png")), resTpn));
		resTpn.setTabComponentAt(1, clientFrame.createTabButton(
				"Database Search Results",
				new ImageIcon(getClass().getResource(
						"/de/mpa/resources/icons/database_search32.png")),
				resTpn));
		resTpn.setTabComponentAt(2, clientFrame.createTabButton(
				"Spectral Similarity Results",
				new ImageIcon(getClass().getResource(
						"/de/mpa/resources/icons/spectral_search32.png")),
				resTpn));
		resTpn.setTabComponentAt(3, clientFrame.createTabButton(
				"BLAST Search Results",
				new ImageIcon(getClass().getResource(
						"/de/mpa/resources/icons/blast32.png")), resTpn));
		Component tabComp = resTpn.getTabComponentAt(0);
		tabComp.setPreferredSize(new Dimension(
				tabComp.getPreferredSize().width, 40));

		// create navigation button panel
		final JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p",
				"b:p:g"));
		navPnl.setOpaque(false);

		navPnl.add(clientFrame.createNavigationButton(false, !Client
				.getInstance().isViewer()), CC.xy(1, 1));
		navPnl.add(clientFrame.createNavigationButton(true, !Client
				.getInstance().isViewer()), CC.xy(3, 1));

		// add everything to main panel
		this.add(navPnl, CC.xy(1, 3));
		this.add(resTpn, CC.xyw(1, 1, 2));
	}

	/**
	 * Creates and returns the left-hand summary panel (wrapped in a
	 * JXTitledPanel)
	 */
	private JPanel createSummaryPanel() {

		JButton resizeBtn = createResizeButton("chart", "details");

		JPanel summaryBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
		summaryBtnPnl.setOpaque(false);
		summaryBtnPnl.add(resizeBtn, CC.xy(1, 1));

		JPanel summaryPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p, 5dlu, f:p:g, 5dlu"));

		JPanel generalPnl = new JPanel(
				new FormLayout(
						"5dlu, p, 5dlu, r:p, 5dlu, 0px:g, 5dlu, r:p, 5dlu, p, 5dlu",
						"2dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu, f:p, 5dlu"));
		generalPnl.setBorder(BorderFactory
				.createTitledBorder("General Statistics"));

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
		uniquePepLbl = new JLabel("0");
		JPanel pepBarPnl = new BarChartPanel(totalPepLbl, uniquePepLbl);

		generalPnl.add(new JLabel("Total peptides"), CC.xy(2, 4));
		generalPnl.add(totalPepLbl, CC.xy(4, 4));
		generalPnl.add(pepBarPnl, CC.xy(6, 4));
		generalPnl.add(uniquePepLbl, CC.xy(8, 4));
		generalPnl.add(new JLabel("unique peptides"), CC.xy(10, 4));

		// total vs. species-specific proteins
		totalProtLbl = new JLabel("0");
		specificProtLbl = new JLabel("0");
		JPanel protBarPnl = new BarChartPanel(totalProtLbl, specificProtLbl);

		generalPnl.add(new JLabel("Total proteins"), CC.xy(2, 6));
		generalPnl.add(totalProtLbl, CC.xy(4, 6));
		generalPnl.add(protBarPnl, CC.xy(6, 6));
		generalPnl.add(specificProtLbl, CC.xy(8, 6));
		generalPnl.add(new JLabel("specific proteins"), CC.xy(10, 6));

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
		
		JButton fetchRemoteBtn = new JButton("<html><center>Fetch Results<br>from DB</center></html>", IconConstants.GO_DB_ICON);
		fetchRemoteBtn.setRolloverIcon(IconConstants.GO_DB_ROLLOVER_ICON);
		fetchRemoteBtn.setPressedIcon(IconConstants.GO_DB_PRESSED_ICON);
		fetchRemoteBtn.setIconTextGap(7);
		JButton fetchLocalBtn = new JButton("<html><center>Fetch Results<br>from File</center></html>", IconConstants.GO_FOLDER_ICON);
		fetchLocalBtn.setRolloverIcon(IconConstants.GO_FOLDER_ROLLOVER_ICON);
		fetchLocalBtn.setPressedIcon(IconConstants.GO_FOLDER_PRESSED_ICON);
		fetchLocalBtn.setIconTextGap(10);
		
		fetchPnl.add(fetchRemoteBtn, CC.xy(1, 1));
		fetchPnl.add(fetchLocalBtn, CC.xy(3, 1));
		fetchPnl.setPreferredSize(new Dimension());
		// TODO: add functionality to fetch buttons
		
		generalPnl.add(fetchPnl, CC.xywh(6, 8, 5, 5));
		
		summaryPnl.add(generalPnl, CC.xy(2, 2));

		// Add experimental heat map chart
		// TODO: make heat map display useful information
		int height = 25, width = 27;
		MatrixSeries series = new MatrixSeries("1", height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double mij = Math.sin((j + 0.5) / width * Math.PI) *
							 Math.cos((i + 0.5) / height * Math.PI - Math.PI / 2.0);
				series.update(i, j, mij);
			}
		}

		String[] xLabels = new String[width];
		for (int i = 0; i < xLabels.length; i++) {
			xLabels[i] = "" + (i + 1);
		}

		String[] yLabels = new String[height];
		for (int i = 0; i < yLabels.length; i++) {
			yLabels[i] = "" + (char) (i + 'A');
		}

		HeatMapPane heatMap = new HeatMapPane("Heat Map", xLabels, yLabels, series);
		heatMap.setVisibleColumnCount(14);
		heatMap.setVisibleRowCount(13);

		summaryPnl.add(heatMap, CC.xy(2, 4));

		JXTitledPanel summaryTtlPnl = PanelConfig.createTitledPanel("Summary",
				summaryPnl);
		summaryTtlPnl.setRightDecoration(summaryBtnPnl);

		return summaryTtlPnl;
	}

	/**
	 * Creates and returns the top-right chart panel (wrapped in a
	 * JXTitledPanel)
	 */
	@SuppressWarnings("unchecked")
	private JPanel createChartPanel() {

		// init chart types
		final ChartType[] chartTypes = new ChartType[] {
				OntologyChartType.BIOLOGICAL_PROCESS,
				OntologyChartType.MOLECULAR_FUNCTION,
				OntologyChartType.CELLULAR_COMPONENT,
				TaxonomyChartType.KINGDOM, TaxonomyChartType.PHYLUM,
				TaxonomyChartType.CLASS, TaxonomyChartType.SPECIES,
				TopBarChartType.PROTEINS };

		// create and configure button for chart type selection
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
		ActionListener chartListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String title = ((JMenuItem) ae.getSource()).getText();
				ChartType newChartType = null;
				for (ChartType chartType : chartTypes) {
					if (title.equals(chartType.toString())) {
						newChartType = chartType;
					}
				}
				if (newChartType != chartType) {
					// clear details table TODO: maybe this ought to be elsewhere, e.g. inside updateOverview()?
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
			JMenuItem item = new JRadioButtonMenuItem(chartType.toString(),
					(j++ == 0));
			item.addActionListener(chartListener);
			chartBtnGrp.add(item);
			chartTypePop.add(item);
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
		JPanel chartBtnPnl = new JPanel(new FormLayout("p, c:5dlu, p, 1px",
				"f:p:g"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));
		chartBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		chartBtnPnl.add(createResizeButton("summary", "details"), CC.xy(3, 1));

		// create and configure chart panel for plots
		chartPnl = new ChartPanel(null);
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setOpaque(false);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));

		// create mouse adapter to interact with pie plot sections
		MouseAdapter adapter = new MouseAdapter() {
			Comparable highlightedKey = null;
			Comparable selectedKey = null;

			@Override
			public void mouseMoved(MouseEvent me) {
				PiePlot3DExt piePlot = getPiePlot();
				if (piePlot != null) {
					Comparable key = piePlot.getSectionKeyForPoint(me
							.getPoint());
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
				}
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				PiePlot3DExt piePlot = getPiePlot();
				if (piePlot != null) {
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
				}
			}

			@Override
			public void mouseEntered(MouseEvent me) {
				// re-validate fields in case chart type has been changed
				highlightedKey = null;
				selectedKey = null;
				PiePlot3DExt piePlot = getPiePlot();
				if (piePlot != null) {
					for (Object dataKey : piePlot.getDataset().getKeys()) {
						Comparable key = (Comparable) dataKey;
						double explodePercent = piePlot.getExplodePercent(key);
						if (explodePercent > 0.1) {
							selectedKey = key;
						}
					}
				}
			}

			/** Utility method to get valid pie plot. */
			private PiePlot3DExt getPiePlot() {
				JFreeChart chart = chartPnl.getChart();
				if (chart != null) {
					if (chart.getPlot() instanceof PiePlot3DExt) {
						return (PiePlot3DExt) chart.getPlot();
					}
				}
				return null;
			}
		};
		chartPnl.addMouseListener(adapter);
		chartPnl.addMouseMotionListener(adapter);

		// create combobox to control what counts to display in the plots
		// (protein/peptide/spectrum count)
		hierarchyCbx = new JComboBox(new String[] { "Proteins", "Peptides",
				"Spectra" });
		hierarchyCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HierarchyLevel hl = null;
				switch (((JComboBox) e.getSource()).getSelectedIndex()) {
				case 0:
					hl = HierarchyLevel.PROTEIN_LEVEL;
					break;
				case 1:
					hl = HierarchyLevel.PEPTIDE_LEVEL;
					break;
				case 2:
					hl = HierarchyLevel.SPECTRUM_LEVEL;
				}
				ontologyData.setHierarchyLevel(hl);
				taxonomyData.setHierarchyLevel(hl);
				updateChart(chartType);
			}
		});
		hierarchyCbx.setVisible(false);

		chartPnl.setLayout(new FormLayout("r:p:g, 2dlu, l:p:g", "0px:g, p, 2dlu"));
		chartPnl.add(hierarchyCbx, CC.xy(1, 2));
		
		hideChk = new JCheckBox("Hide Unknown", false);
		hideChk.setOpaque(false);
		hideChk.setVisible(false);
		hideChk.addActionListener(new ActionListener() {
			private boolean doHide;
			@Override
			public void actionPerformed(ActionEvent evt) {
				doHide = ((AbstractButton) evt.getSource()).isSelected();
				ontologyData.setHideUnknown(doHide);
				taxonomyData.setHideUnknown(doHide);
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
						hideChk.setEnabled(false);
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
						hideChk.setEnabled(true);
					};
					
				}.execute();
			}
		});
		chartPnl.add(hideChk, CC.xy(3, 2));

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
						pieChartAngle = ae.getValue();
						((PiePlot) chart.getPlot()).setStartAngle(pieChartAngle);
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

		JPanel detailsBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
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
		TableConfig
				.setColumnWidths(detailsTbl, new double[] { 1.0, 3.0, 12.0 });

		// AbstractHyperlinkAction<URI> linkAction = new
		// AbstractHyperlinkAction<URI>() {
		// public void actionPerformed(ActionEvent ev) {
		// try {
		// Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/"
		// + target));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// };

		AbstractHyperlinkAction<String> linkAction = new AbstractHyperlinkAction<String>() {
			public void actionPerformed(ActionEvent ev) {
				try {
					// Use UniProt identifier also for NCBI entries
					if (target.matches("^\\d*$")) { // if contains non-numerical
													// character UniProt
						UniProtEntry uniprotEntry = dbPnl.getDbSearchResult()
								.getProteinHit(target).getUniprotEntry();
						if (uniprotEntry != null) {
							PrimaryUniProtAccession primaryUniProtAccession = uniprotEntry
									.getPrimaryUniProtAccession();
							target = primaryUniProtAccession.getValue();
						} else {
							target = "";
						}
					}
					Desktop.getDesktop()
							.browse(new URI("http://www.uniprot.org/uniprot/"
									+ target));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		detailsTbl.getColumn(1).setCellRenderer(
				new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component comp = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
						compLabel.setHorizontalAlignment(SwingConstants.CENTER);
						return compLabel;
					}
				});

		detailsTbl.setColumnControlVisible(true);
		detailsTbl.getColumnControl()
				.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory
								.createMatteBorder(1, 1, 0, 0, Color.WHITE),
								BorderFactory.createMatteBorder(0, 0, 1, 0,
										Color.GRAY)));
		detailsTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) detailsTbl.getColumnControl())
				.setAdditionalActionsVisible(false);

		// Add nice striping effect
		detailsTbl.addHighlighter(TableConfig.getSimpleStriping());

		JScrollPane detailsScpn = new JScrollPane(detailsTbl);
		detailsScpn
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScpn.setPreferredSize(new Dimension(100, 100));

		detailsPnl.add(detailsScpn, CC.xy(2, 2));

		JXTitledPanel detailsTtlPnl = PanelConfig.createTitledPanel(
				"Chart Details", detailsPnl);
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

		resizeBtn.setUI(new RoundedHoverButtonUI());

		resizeBtn.setOpaque(false);
		resizeBtn.setBorderPainted(false);
		resizeBtn.setMargin(new Insets(1, 0, 0, 0));

		resizeBtn.addMouseListener(new InstantToolTipMouseListener());

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
					msl.displayNode(node, maximized);
				}
				maximized = !maximized;
			}
		});

		return resizeBtn;
	}

	/**
	 * Refreshes the chart updating its content reflecting the specified chart
	 * type.
	 * 
	 * @param chartType
	 *            the type of chart to be displayed.
	 */
	private void updateChart(ChartType chartType) {
		Chart chart = null;
		
		if (chartType instanceof OntologyChartType) {
			chart = ChartFactory.createOntologyPieChart(
					ontologyData, chartType);
		} else if (chartType instanceof TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyPieChart(
					taxonomyData, chartType);
		} else {
			chart = ChartFactory.createTopBarChart(
					topData, chartType);
		}

		if (chart != null) {
			Plot plot = chart.getChart().getPlot();
			if (plot instanceof PiePlot) {
				chartBar.setMaximum(360);
				chartBar.setValue((int) pieChartAngle);
				((PiePlot) chart.getChart().getPlot())
						.setStartAngle(pieChartAngle);
				hierarchyCbx.setVisible(true);
				hideChk.setVisible(true);
			} else {
				double temp = pieChartAngle;
				chartBar.setMaximum(0);
				pieChartAngle = temp;
				hierarchyCbx.setVisible(false);
				hideChk.setVisible(false);
			}
			chartPnl.setChart(chart.getChart());
		}

		this.chartType = chartType;
	}

	/**
	 * Refreshes the overview panel by updating the charts, tables and labels
	 * containing the various experiment statistics.
	 */
	protected void updateOverview() {
		new UpdateTask().execute();
	}

	/**
	 * Worker class to process search results and generate statistics from it in
	 * a background thread.
	 * 
	 * @author T. Muth, A. Behne
	 */
	private class UpdateTask extends SwingWorker {

		protected Object doInBackground() {
			DbSearchResult dbSearchResult = null;
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// Fetch the database search result.
			try {
				dbSearchResult = Client.getInstance().getDbSearchResult();

				Set<String> speciesNames = new HashSet<String>();
				Set<String> ecNumbers = new HashSet<String>();
				Set<String> kNumbers = new HashSet<String>();
				for (ProteinHit ph : dbSearchResult.getProteinHitList()) {
					speciesNames.add(ph.getSpecies());
					UniProtEntry uniprotEntry = ph.getUniprotEntry();
					if (uniprotEntry != null) {
						ecNumbers.addAll(uniprotEntry.getProteinDescription()
								.getEcNumbers());
						for (DatabaseCrossReference xref : uniprotEntry
								.getDatabaseCrossReferences(DatabaseType.KO)) {
							kNumbers.add(((KO) xref).getKOIdentifier()
									.getValue());
						}
						UniProtEntry uniProtEntry = ph.getUniprotEntry();
						if (uniProtEntry != null) {
							ecNumbers.addAll(uniProtEntry
									.getProteinDescription().getEcNumbers());
							for (DatabaseCrossReference xref : ph
									.getUniprotEntry()
									.getDatabaseCrossReferences(DatabaseType.KO)) {
								kNumbers.add(((KO) xref).getKOIdentifier()
										.getValue());
							}
						}
					}
					Set<Short> pathwayIDs = new HashSet<Short>();
					for (String ec : ecNumbers) {
						List<Short> pathwaysByEC = KeggAccessor.getInstance()
								.getPathwaysByEC(ec);
						if (pathwaysByEC != null) {
							pathwayIDs.addAll(pathwaysByEC);
						}
					}
					for (String ko : kNumbers) {
						List<Short> pathwaysByKO = KeggAccessor.getInstance()
								.getPathwaysByKO(ko);
						if (pathwaysByKO != null) {
							pathwayIDs.addAll(pathwaysByKO);
						}
					}

					totalSpecLbl.setText(""
							+ dbSearchResult.getTotalSpectrumCount());
					identSpecLbl.setText(""
							+ dbSearchResult.getIdentifiedSpectrumCount());
					totalPepLbl.setText(""
							+ dbSearchResult.getTotalPeptideCount());
					uniquePepLbl.setText(""
							+ dbSearchResult.getUniquePeptideCount());
					totalProtLbl.setText(""
							+ dbSearchResult.getProteinHitList().size());
					specificProtLbl.setText("" + "??"); // TODO: determining protein redundancy is an unsolved problem!
					speciesLbl.setText("" + speciesNames.size());
					enzymesLbl.setText("" + ecNumbers.size());
					pathwaysLbl.setText("" + pathwayIDs.size());

					HierarchyLevel hl = null;
					switch (hierarchyCbx.getSelectedIndex()) {
					case 0:
						hl = HierarchyLevel.PROTEIN_LEVEL;
						break;
					case 1:
						hl = HierarchyLevel.PEPTIDE_LEVEL;
						break;
					case 2:
						hl = HierarchyLevel.SPECTRUM_LEVEL;
					}
					ontologyData = new OntologyData(dbSearchResult, hl);
					taxonomyData = new TaxonomyData(dbSearchResult, hl);
					topData = new TopData(dbSearchResult);

					updateChart(OntologyChartType.BIOLOGICAL_PROCESS);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void done() {
			// TODO: delegate cursor setting to top-level containers so the whole frame is affected instead of only this panel
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	/**
	 * @return the dbPnl
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return dbPnl;
	}

	/**
	 * @return the ssPnl
	 */
	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
		return ssPnl;
	}

	/**
	 * @return the dnPnl
	 */
	public DeNovoResultPanel getDeNovoSearchResultPanel() {
		return dnPnl;
	}

	/**
	 * Returns the button to select the type of the chart inside the chart view
	 * panel.
	 * 
	 * @return the chart type button
	 */
	public JToggleButton getChartTypeButton() {
		return chartTypeBtn;
	}

	private class BarChartPanel extends JPanel {
		private JLabel totalLbl;
		private JLabel fracLbl;

		public BarChartPanel(JLabel totalLbl, JLabel fracLbl) {
			this.totalLbl = totalLbl;
			this.fracLbl = fracLbl;
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Point pt1 = new Point();
			Point pt2 = new Point(getWidth(), 0);
			g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_GREEN, pt2,
					ColorUtils.LIGHT_GREEN));
			g2.fillRect(0, 0, getWidth(), getHeight());

			try {
				double total = Double.parseDouble(totalLbl.getText());
				if (total > 0.0) {
					double rel = Double.parseDouble(fracLbl.getText()) / total;
					int width = (int) (getWidth() * rel);
					g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_ORANGE,
							pt2, ColorUtils.LIGHT_ORANGE));
					g2.fillRect(getWidth() - width, 0, width, getHeight());
					String str = String.format("%.1f", rel * 100.0) + "%";
					FontMetrics fm = g2.getFontMetrics();
					Rectangle2D bounds = fm.getStringBounds(str, g2);

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					// g2.setPaint(ColorUtils.DARK_ORANGE);
					float x = (float) (getWidth() - width - bounds.getWidth() - 2.0f);
					if (x < 2.0f) {
						x = getWidth() - width + 2.0f;
					}
					float y = (float) (-bounds.getY() + getHeight()) / 2.0f - 1.0f;
					g2.setPaint(Color.BLACK);
					g2.drawString(str, x + 1.0f, y + 1.0f);
					g2.setPaint(Color.GRAY);
					g2.drawString(str, x + 1.0f, y + 1.0f);
					g2.setPaint(Color.WHITE);
					g2.drawString(str, x, y);
					g2.drawString(str, x, y);
				}
			} catch (Exception e) {
				// catch NPEs and failed parse attempts, draw nothing
			}
		};
	}

}
