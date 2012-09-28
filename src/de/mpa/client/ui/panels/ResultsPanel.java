package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jfree.chart.ChartPanel;

import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.InstantToolTipMouseListener;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RoundedHoverButtonUI;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.TaxonomyData;
import de.mpa.client.ui.chart.TopData;
import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;
import de.mpa.client.ui.chart.TopBarChart.TopBarChartType;
import de.mpa.client.ui.icons.IconConstants;

public class ResultsPanel extends JPanel {
	
	private ClientFrame clientFrame;
	private DbSearchResultPanel dbPnl;
	private SpecSimResultPanel ssPnl;
	private DeNovoResultPanel dnPnl;
	private ChartPanel chartPnl;
	private OntologyData ontologyData;
	private TaxonomyData taxonomyData;
	private ChartType chartType = OntologyChartType.BIOLOGICAL_PROCESS;
	private TopData topData;
	private MultiSplitLayout msl;
	
	public ResultsPanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.dbPnl = new DbSearchResultPanel(this);
		this.ssPnl = new SpecSimResultPanel();
		this.dnPnl = new DeNovoResultPanel();
		initComponents();
	}

	private void initComponents() {
		// configure main panel layout
		this.setLayout(new FormLayout("p:g, 5dlu", "f:p:g, -40px, b:p, 5dlu"));
		
		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, contentBorderInsets.bottom, 0));
		final JTabbedPane resTpn = new JTabbedPane(JTabbedPane.BOTTOM);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets);
		
		JPanel ovPnl = new JPanel(new FormLayout(
				"5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		String layoutDef =
			"(ROW (LEAF weight=0.5 name=details) (COLUMN weight=0.5 (LEAF weight=0.5 name=chart) (LEAF weight=0.5 name=placeholder)";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		msl = new MultiSplitLayout(modelRoot);
		
		final JXMultiSplitPane msp = new JXMultiSplitPane(msl);
		msp.setDividerSize(12);
		
		msp.add(createDetailsPanel(), "details");
		msp.add(createChartPanel(), "chart");
		msp.add(PanelConfig.createTitledPanel("Placeholder",
				new JLabel("open to suggestions...", SwingConstants.CENTER)), "placeholder");
		
		ovPnl.add(msp, CC.xy(2, 2));
		
		resTpn.addTab(" ", ovPnl);
		resTpn.addTab(" ", dbPnl);
		resTpn.addTab(" ", ssPnl);
		resTpn.addTab(" ", dnPnl);
		
		// TODO: use proper icons, possibly slightly larger ones, e.g. 40x40?
		resTpn.setTabComponentAt(0, clientFrame.createTabButton("Overview",	new ImageIcon(getClass().getResource("/de/mpa/resources/icons/overview32.png")), resTpn));
		resTpn.setTabComponentAt(1, clientFrame.createTabButton("Database Search Results", new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database_search32.png")), resTpn));
		resTpn.setTabComponentAt(2, clientFrame.createTabButton("Spectral Similarity Results", new ImageIcon(getClass().getResource("/de/mpa/resources/icons/spectral_search32.png")), resTpn));
		resTpn.setTabComponentAt(3, clientFrame.createTabButton("Blast Search Results",	new ImageIcon(getClass().getResource("/de/mpa/resources/icons/blast32.png")), resTpn));
		Component tabComp = resTpn.getTabComponentAt(0);
		tabComp.setPreferredSize(new Dimension(tabComp.getPreferredSize().width, 40));

		// create navigation button panel
		final JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));
		navPnl.setOpaque(false);
		
		JButton prevBtn = new JButton("Prev", IconConstants.PREV_ICON);
		prevBtn.setRolloverIcon(IconConstants.PREV_ROLLOVER_ICON);
		prevBtn.setPressedIcon(IconConstants.PREV_PRESSED_ICON);
		prevBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		prevBtn.setFont(prevBtn.getFont().deriveFont(Font.BOLD, prevBtn.getFont().getSize2D()*1.25f));
		prevBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(2);
			}
		});
		prevBtn.setEnabled(!Client.getInstance().isViewer());
		
		JButton nextBtn = new JButton("Next", IconConstants.NEXT_ICON);
		nextBtn.setRolloverIcon(IconConstants.NEXT_ROLLOVER_ICON);
		nextBtn.setPressedIcon(IconConstants.NEXT_PRESSED_ICON);
		nextBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		nextBtn.setFont(nextBtn.getFont().deriveFont(Font.BOLD, nextBtn.getFont().getSize2D()*1.25f));
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(5);
			}
		});
		nextBtn.setEnabled(!Client.getInstance().isViewer());
		
		navPnl.add(prevBtn, CC.xy(1, 1));
		navPnl.add(nextBtn, CC.xy(3, 1));

		// add everything to main panel
		this.add(navPnl, CC.xy(1, 3));
		this.add(resTpn, CC.xyw(1, 1, 2));
	}

	
	private JPanel createDetailsPanel() {
		
		JButton resizeBtn = createResizeButton("chart", "placeholder");
		
		JPanel detailsBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
		detailsBtnPnl.setOpaque(false);
		detailsBtnPnl.add(resizeBtn, CC.xy(1, 1));
		
		JPanel detailsPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu, r:p:g, 5dlu", "5dlu, t:p, 5dlu, t:p, 5dlu, t:p, 5dlu, t:p, 5dlu:g"));

		detailsPnl.add(new JLabel("total"), CC.xy(4, 2));
		detailsPnl.add(new JLabel("unique"), CC.xy(6, 2));

		detailsPnl.add(new JLabel("spectra"), CC.xy(2, 4));
		detailsPnl.add(new JLabel("1234"), CC.xy(4, 4));
		detailsPnl.add(new JLabel("123"), CC.xy(6, 4));
		
		detailsPnl.add(new JLabel("peptides"), CC.xy(2, 6));
		detailsPnl.add(new JLabel("123"), CC.xy(4, 6));
		detailsPnl.add(new JLabel("12"), CC.xy(6, 6));
		
		detailsPnl.add(new JLabel("proteins"), CC.xy(2, 8));
		detailsPnl.add(new JLabel("12"), CC.xy(4, 8));
		detailsPnl.add(new JLabel("1"), CC.xy(6, 8));
		
		JXTitledPanel detailsTtlPnl = PanelConfig.createTitledPanel("Details Summary", detailsPnl);
		detailsTtlPnl.setRightDecoration(detailsBtnPnl);
		
		return detailsTtlPnl;
	}

	private JPanel createChartPanel() {
		
		final ArrayList<String> chartLabels = new ArrayList<String>(Arrays.asList(new String[] {
				"Biological Process",
				"Molecular Function",
				"Cellular Component",
				"Kingdom Taxonomy",
				"Phylum Taxonomy",
				"Class Taxonomy",
				"Species Taxonomy",
				"Top10 Proteins"
				}));
		
		final JToggleButton chartTypeBtn = new JToggleButton(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ICON));
		chartTypeBtn.setRolloverIcon(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ROLLOVER_ICON));
		chartTypeBtn.setPressedIcon(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_PRESSED_ICON));
		chartTypeBtn.setToolTipText("Select Chart Type");
		
		chartTypeBtn.setUI(new RoundedHoverButtonUI());

		chartTypeBtn.setOpaque(false);
		chartTypeBtn.setBorderPainted(false);
		chartTypeBtn.setMargin(new Insets(1, 0, 0, 0));
		
		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
		chartTypeBtn.addMouseListener(ittml);
		
		final JPopupMenu chartPop = new JPopupMenu();
		ActionListener chartListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int chartTypeIndex = chartLabels.indexOf(
						((JMenuItem) e.getSource()).getText());
				switch (chartTypeIndex) {
				case 0:
					chartType = (OntologyChartType) OntologyChartType.BIOLOGICAL_PROCESS;
					break;
				case 1:
					chartType = (OntologyChartType) OntologyChartType.MOLECULAR_FUNCTION;
					break;
				case 2: 
					chartType = (OntologyChartType) OntologyChartType.CELLULAR_COMPONENT;
					break;
				case 3: 
					chartType = (TaxonomyChartType) TaxonomyChartType.KINGDOM;
					break;
				case 4: 
					chartType = (TaxonomyChartType) TaxonomyChartType.PHYLUM;
					break;
				case 5: 
					chartType = (TaxonomyChartType) TaxonomyChartType.CLASS;
					break;
				case 6: 
					chartType = (TaxonomyChartType) TaxonomyChartType.SPECIES;
					break;
				case 7: 
					chartType = (TopBarChartType) TopBarChartType.PROTEINS;
					break;
				default:
					chartType = (OntologyChartType) OntologyChartType.BIOLOGICAL_PROCESS;
					break;
				}
				updateOverview(chartTypeIndex, false);
			}
		};
		for (int j = 0; j < chartLabels.size(); j++) {
			JMenuItem item = new JMenuItem(chartLabels.get(j));
			item.addActionListener(chartListener);
			chartPop.add(item);
		}
		chartPop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				chartTypeBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		
		chartTypeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartPop.show(chartTypeBtn, 0, chartTypeBtn.getHeight());
			}
		});
		
		JButton resizeBtn = createResizeButton("details", "placeholder");
		
		JPanel chartBtnPnl = new JPanel(new FormLayout("p, c:5dlu, p, 1px", "f:p:g"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));
		chartBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		chartBtnPnl.add(resizeBtn, CC.xy(3, 1));

		// Default pie chart
		Map<String, Integer> defaultMap = new HashMap<String, Integer>();
		defaultMap.put("Resembles Pac-Man", 80);
		defaultMap.put("Does not resemble Pac-Man", 20);
		ontologyData = new OntologyData();
		ontologyData.setDefaultMapping(defaultMap);
		
		JPanel chartContPanel = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		Chart chart = ChartFactory.createOntologyPieChart(ontologyData, chartType);
		
		chartPnl = new ChartPanel(chart.getChart());
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setBorder(BorderFactory.createEtchedBorder());
		chartPnl.setBackground(Color.WHITE);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));
		
		chartContPanel.add(chartPnl, CC.xy(2, 2));
		
		JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel("Chart View", chartContPanel);
		chartTtlPnl.setRightDecoration(chartBtnPnl);
		
		return chartTtlPnl;
	}

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
	 * Updates the bar plot.
	 */
	private void updateCharts(int chartTypeIndex) {
		Chart chart = null;
		switch (chartTypeIndex) {
		case 0:
		case 1:
		case 2:
			chart = ChartFactory.createOntologyPieChart(ontologyData, chartType);
			break;
		case 3:
		case 4:
		case 5:
		case 6:
			chart = ChartFactory.createTaxonomyPieChart(taxonomyData, chartType);
			break;
		default:
			chart = ChartFactory.createTopBarChart(topData, chartType);
			break;
		}
		
		chartPnl.setChart(chart.getChart());
	}

	protected void updateOverview(boolean refresh) {
		updateOverview(0, refresh);
	}
	
	protected void updateOverview(int chartTypeIndex, boolean refresh) {
		UpdateTask ut = new UpdateTask(chartTypeIndex, refresh);
		ut.execute();
	}
	
	private class UpdateTask extends SwingWorker {
		private int chartTypeIndex;
		private boolean refresh;
		
		public UpdateTask(int chartTypeIndex, boolean refresh) {
			this.chartTypeIndex = chartTypeIndex;
			this.refresh = refresh;
		}
		
		protected Object doInBackground() {
			DbSearchResult dbSearchResult = null;
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Fetch the database search result.
				try {
					if (refresh) {
						dbSearchResult = Client.getInstance().getDbSearchResult();
						ontologyData = new OntologyData(dbSearchResult);
						taxonomyData = new TaxonomyData(dbSearchResult);
						topData = new TopData(dbSearchResult);
					}
					finished();
				} catch (RemoteDataAccessException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * Continues when the results retrieval has finished.
		 */
		public void finished() {
			updateCharts(chartTypeIndex);
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

}
