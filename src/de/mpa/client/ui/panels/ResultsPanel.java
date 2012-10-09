package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

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
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.PiePlot3DExt;
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
	private JScrollBar chartBar;
	private JToggleButton chartTypeBtn;
	private JXTable detailsTbl;
	
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
			"(ROW (LEAF weight=0.5 name=summary) (COLUMN weight=0.5 (LEAF weight=0.5 name=chart) (LEAF weight=0.5 name=details)";
		Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		msl = new MultiSplitLayout(modelRoot);
		
		final JXMultiSplitPane msp = new JXMultiSplitPane(msl);
		msp.setDividerSize(12);
		
		msp.add(createSummaryPanel(), "summary");
		msp.add(createChartPanel(), "chart");
		msp.add(createDetailsPanel(), "details");
//		msp.add(PanelConfig.createTitledPanel("Placeholder",
//				new JLabel("open to suggestions...", SwingConstants.CENTER)), "placeholder");
		
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

	/**
	 * Creates and returns the left-hand summary panel (wrapped in a JXTitledPanel)
	 */
	private JPanel createSummaryPanel() {
		
		JButton resizeBtn = createResizeButton("chart", "details");
		
		JPanel summaryBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
		summaryBtnPnl.setOpaque(false);
		summaryBtnPnl.add(resizeBtn, CC.xy(1, 1));
		
		JPanel summaryPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu, r:p:g, 5dlu", "5dlu, t:p, 5dlu, t:p, 5dlu, t:p, 5dlu, t:p, 5dlu:g"));

		summaryPnl.add(new JLabel("total"), CC.xy(4, 2));
		summaryPnl.add(new JLabel("unique"), CC.xy(6, 2));

		summaryPnl.add(new JLabel("spectra"), CC.xy(2, 4));
		summaryPnl.add(new JLabel("1234"), CC.xy(4, 4));
		summaryPnl.add(new JLabel("123"), CC.xy(6, 4));
		
		summaryPnl.add(new JLabel("peptides"), CC.xy(2, 6));
		summaryPnl.add(new JLabel("123"), CC.xy(4, 6));
		summaryPnl.add(new JLabel("12"), CC.xy(6, 6));
		
		summaryPnl.add(new JLabel("proteins"), CC.xy(2, 8));
		summaryPnl.add(new JLabel("12"), CC.xy(4, 8));
		summaryPnl.add(new JLabel("1"), CC.xy(6, 8));
		
		JXTitledPanel summaryTtlPnl = PanelConfig.createTitledPanel("Summary", summaryPnl);
		summaryTtlPnl.setRightDecoration(summaryBtnPnl);
		
		return summaryTtlPnl;
	}

	/**
	 * Creates and returns the top-right chart panel (wrapped in a JXTitledPanel)
	 */
	private JPanel createChartPanel() {
		
		final List<ChartType> chartTypes = new ArrayList<ChartType>(Arrays.asList(new ChartType[] {
				OntologyChartType.BIOLOGICAL_PROCESS,
				OntologyChartType.MOLECULAR_FUNCTION,
				OntologyChartType.CELLULAR_COMPONENT,
				TaxonomyChartType.KINGDOM,
				TaxonomyChartType.PHYLUM,
				TaxonomyChartType.CLASS,
				TaxonomyChartType.SPECIES,
				TopBarChartType.PROTEINS
		}));
		
		chartTypeBtn = new JToggleButton(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ICON));
		chartTypeBtn.setRolloverIcon(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_ROLLOVER_ICON));
		chartTypeBtn.setPressedIcon(IconConstants.createArrowedIcon(IconConstants.PIE_CHART_PRESSED_ICON));
		chartTypeBtn.setToolTipText("Select Chart Type");
		chartTypeBtn.setEnabled(false);
		
		chartTypeBtn.setUI(new RoundedHoverButtonUI());

		chartTypeBtn.setOpaque(false);
		chartTypeBtn.setBorderPainted(false);
		chartTypeBtn.setMargin(new Insets(1, 0, 0, 0));
		
		InstantToolTipMouseListener ittml = new InstantToolTipMouseListener();
		chartTypeBtn.addMouseListener(ittml);
		
		final JPopupMenu chartPop = new JPopupMenu();
		ActionListener chartListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String title = ((JMenuItem) ae.getSource()).getText();
				int chartTypeIndex;
				for (chartTypeIndex = 0; chartTypeIndex < chartTypes.size(); chartTypeIndex++) {
					if (title.equals(chartTypes.get(chartTypeIndex).getTitle())) {
						break;
					}
				}
				ChartType newChartType = chartTypes.get(chartTypeIndex);
				if (newChartType != chartType) {
					// clear details table TODO: maybe this ought to be elsewhere, e.g. inside updateOverview()?
					TableConfig.clearTable(detailsTbl);
					
					chartType = newChartType;
					updateOverview(chartTypeIndex, false);
				}
			}
		};
		ButtonGroup chartBtnGrp = new ButtonGroup();
		for (int j = 0; j < chartTypes.size(); j++) {
			JMenuItem item = new JCheckBoxMenuItem(chartTypes.get(j).getTitle(), (j == 0));
			item.addActionListener(chartListener);
			chartBtnGrp.add(item);
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
		
		JButton resizeBtn = createResizeButton("summary", "details");
		
		JPanel chartBtnPnl = new JPanel(new FormLayout("p, c:5dlu, p, 1px", "f:p:g"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));
		chartBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		chartBtnPnl.add(resizeBtn, CC.xy(3, 1));

		// Default pie chart
		final Map<String, List<ProteinHit>> defaultMap = new LinkedHashMap<String, List<ProteinHit>>();
		defaultMap.put("Resembles Pac-Man", Arrays.asList(new ProteinHit[80]));
		defaultMap.put("Does not resemble Pac-Man", Arrays.asList(new ProteinHit[20]));
		ontologyData = new OntologyData();
		ontologyData.setDefaultMapping(defaultMap);
		
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		JPanel chartScrollPnl = new JPanel(new BorderLayout());
		chartScrollPnl.setBorder(new Border() {
			private Border delegate = BorderFactory.createEtchedBorder();
			public void paintBorder(Component c, Graphics g, int x, int y, int width,
					int height) {
				delegate.paintBorder(c, g, x, y, width, height);
			}
			public boolean isBorderOpaque() { return true; }
			public Insets getBorderInsets(Component c) {
				Insets insets = delegate.getBorderInsets(c);
				insets.set(1, 2, 2, 1);
				return insets;
			}
		});
		
		chartPnl = new ChartPanel(null);
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setBackground(Color.WHITE);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));
		
		MouseAdapter adapter = new MouseAdapter() {
			Comparable highlightedKey = null;
			Comparable selectedKey = null;
			@Override
			public void mouseMoved(MouseEvent me) {
				PiePlot3DExt piePlot = getPiePlot();
				if (piePlot != null) {
					Comparable key = piePlot.getSectionKeyForPoint(me.getPoint());
					if (key != highlightedKey) {
						for (Object dataKey : piePlot.getDataset().getKeys()) {
							if (dataKey != selectedKey) {
								piePlot.setExplodePercent((Comparable) dataKey, 0.0);
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
		
		chartBar = new JScrollBar(JScrollBar.VERTICAL, 39, 0, 0, 360);
		chartBar.setBorder(BorderFactory.createEmptyBorder(-1, 0, -1, -1));
		chartBar.setBlockIncrement(36);
		
		chartBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				JFreeChart chart = chartPnl.getChart();
				if (chart != null) {
					if (chart.getPlot() instanceof PiePlot) {
						((PiePlot) chart.getPlot()).setStartAngle(ae.getValue());
					}
				}
			}
		});
		
		chartScrollPnl.add(chartPnl, BorderLayout.CENTER);
		chartScrollPnl.add(chartBar, BorderLayout.EAST);

		chartMarginPnl.add(chartScrollPnl, CC.xy(2, 2));
		
		JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel("Chart View", chartMarginPnl);
		chartTtlPnl.setRightDecoration(chartBtnPnl);
		
		return chartTtlPnl;
	}

	/**
	 * Creates and returns the bottom-right chart details panel (wrapped in a JXTitledPanel)
	 */
	private Component createDetailsPanel() {
		
		JButton resizeBtn = createResizeButton("summary", "chart");

		JPanel detailsBtnPnl = new JPanel(new FormLayout("p, 1px", "f:p:g"));
		detailsBtnPnl.setOpaque(false);
		detailsBtnPnl.add(resizeBtn, CC.xy(1, 1));

		JPanel detailsPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		detailsTbl = new JXTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Accession", "Description"});
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
		
		detailsTbl.setColumnControlVisible(true);
		detailsTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		detailsTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) detailsTbl.getColumnControl()).setAdditionalActionsVisible(false);
		
		// Add nice striping effect
		detailsTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		JScrollPane detailsScpn = new JScrollPane(detailsTbl);
		detailsScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScpn.setPreferredSize(new Dimension(100, 100));
		
		detailsPnl.add(detailsScpn, CC.xy(2, 2));

		JXTitledPanel detailsTtlPnl = PanelConfig.createTitledPanel("Chart Details", detailsPnl);
		detailsTtlPnl.setRightDecoration(detailsBtnPnl);

		return detailsTtlPnl;
	}

	protected void updateDetailsTable(Comparable key) {
		DefaultTableModel detailsTblMdl = (DefaultTableModel) detailsTbl.getModel();
		detailsTblMdl.setRowCount(0);
		List<ProteinHit> proteinHits = null;
		if (chartType instanceof OntologyChartType) {
			proteinHits = ontologyData.getProteinHits((String) key);
		}
		if (proteinHits != null) {
			int i = 1;
			for (ProteinHit proteinHit : proteinHits) {
				detailsTblMdl.addRow(new Object[] {
						i++,
						proteinHit.getAccession(),
						proteinHit.getDescription()
				});
			}
		}
	}

	/**
	 * Utility method to create the resize/restore widget for use in titled panels.
	 * @param nodes2hide The names of the MultiSplitPane nodes that are to be hidden/restored.
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
	 * Updates the bar plot.
	 */
	private void updateCharts(int chartTypeIndex) {
		Chart chart = null;
		switch (chartTypeIndex) {
		case 0:
		case 1:
		case 2:
			if (ontologyData != null) {
				chart = ChartFactory.createOntologyPieChart(ontologyData, chartType);
//				((PiePlot) chart.getChart().getPlot()).setExplodePercent(
//						ontologyData.getDataset().getKey(0), 0.2);
				((PiePlot3DExt) chart.getChart().getPlot()).setMaximumExplodePercent(0.2);
			}
			break;
		case 3:
		case 4:
		case 5:
		case 6:
			if (taxonomyData != null) {
				chart = ChartFactory.createTaxonomyPieChart(taxonomyData, chartType);
//				((PiePlot) chart.getChart().getPlot()).setExplodePercent(
//						ontologyData.getDataset().getKey(0), 0.2);
				((PiePlot3DExt) chart.getChart().getPlot()).setMaximumExplodePercent(0.2);
			}
			break;
		default:
			chart = ChartFactory.createTopBarChart(topData, chartType);
			break;
		}
		
		if (chart != null) {
			if (chart.getChart().getPlot() instanceof PiePlot) {
//				chartBar.setVisibleAmount(0);
				((PiePlot) chart.getChart().getPlot()).setStartAngle(chartBar.getValue());
			} else {
				// TODO: find way to make scroll bar non-interactive (without disabling it) for non-pie charts
//				chartBar.setVisibleAmount(Integer.MAX_VALUE);
			}
			chartPnl.setChart(chart.getChart());
		}
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

	/**
	 * Returns the button to select the type of the chart inside the chart view panel.
	 * @return the chart type button
	 */
	public JToggleButton getChartTypeButton() {
		return chartTypeBtn;
	}

}
