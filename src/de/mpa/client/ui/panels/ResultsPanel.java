package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.border.Border;
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

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.KeggAccessor;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.InstantToolTipMouseListener;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RoundedHoverButtonUI;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.Chart;
import de.mpa.client.ui.chart.ChartFactory;
import de.mpa.client.ui.chart.ChartType;
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

public class ResultsPanel extends JPanel {
	
	private ClientFrame clientFrame;
	
	private MultiSplitLayout msl;
	
	private DbSearchResultPanel dbPnl;
	private SpecSimResultPanel ssPnl;
	private DeNovoResultPanel dnPnl;

	private JToggleButton chartTypeBtn;
	private ChartPanel chartPnl;
	private JScrollBar chartBar;
	private JComboBox hierarchyCbx;
	private double pieChartAngle = 36.0;

	private ChartType chartType = OntologyChartType.BIOLOGICAL_PROCESS;
	private OntologyData ontologyData;
	private TaxonomyData taxonomyData;
	private TopData topData;
	
	private JXTable detailsTbl;
	
	private JLabel totalSpecLbl;
	private JLabel identSpecLbl;
	private JLabel totalPepLbl;
	private JLabel uniquePepLbl;
	private JLabel totalProtLbl;
	private JLabel specificProtLbl;
	private JLabel speciesLbl;
	private JLabel enzymesLbl;
	private JLabel pathwaysLbl;
	
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
		
		JPanel summaryPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p, 5dlu"));
		
		JPanel generalPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, r:p, 5dlu, 0px:g, 5dlu, r:p, 5dlu, p, 5dlu",
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
		
		summaryPnl.add(generalPnl, CC.xy(2, 2));
		
		JXTitledPanel summaryTtlPnl = PanelConfig.createTitledPanel("Summary", summaryPnl);
		summaryTtlPnl.setRightDecoration(summaryBtnPnl);
		
		return summaryTtlPnl;
	}

	/**
	 * Creates and returns the top-right chart panel (wrapped in a JXTitledPanel)
	 */
	private JPanel createChartPanel() {
		
		final ChartType[] chartTypes = new ChartType[] {
				OntologyChartType.BIOLOGICAL_PROCESS,
				OntologyChartType.MOLECULAR_FUNCTION,
				OntologyChartType.CELLULAR_COMPONENT,
				TaxonomyChartType.KINGDOM,
				TaxonomyChartType.PHYLUM,
				TaxonomyChartType.CLASS,
				TaxonomyChartType.SPECIES,
				TopBarChartType.PROTEINS
		};
		
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
				}
			}
		};
		ButtonGroup chartBtnGrp = new ButtonGroup();
		int j = 0;
		for (ChartType chartType : chartTypes) {
			JMenuItem item = new JRadioButtonMenuItem(chartType.toString(), (j++ == 0));
			item.addActionListener(chartListener);
			chartBtnGrp.add(item);
			chartTypePop.add(item);
		}
		chartTypePop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				chartTypeBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		
		chartTypeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartTypePop.show(chartTypeBtn, 0, chartTypeBtn.getHeight());
			}
		});
		
		JButton resizeBtn = createResizeButton("summary", "details");
		
		JPanel chartBtnPnl = new JPanel(new FormLayout("p, c:5dlu, p, 1px", "f:p:g"));
		chartBtnPnl.setOpaque(false);
		chartBtnPnl.add(chartTypeBtn, CC.xy(1, 1));
		chartBtnPnl.add(new JSeparator(SwingConstants.VERTICAL), CC.xy(2, 1));
		chartBtnPnl.add(resizeBtn, CC.xy(3, 1));

		// Default pie chart
		final Map<String, ProteinHitList> defaultMap = new LinkedHashMap<String, ProteinHitList>();
		defaultMap.put("Resembles Pac-Man", new ProteinHitList(Arrays.asList(new ProteinHit[80])));
		defaultMap.put("Does not resemble Pac-Man", new ProteinHitList(Arrays.asList(new ProteinHit[20])));
		ontologyData = new OntologyData();
		ontologyData.setDefaultMapping(defaultMap);
		
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		JPanel chartScrollPnl = new JPanel(new FormLayout("0:g, p, 0:g, r:p", "f:p:g, b:p, 2dlu"));
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
		chartScrollPnl.setBackground(Color.WHITE);
		
		chartPnl = new ChartPanel(null);
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setOpaque(false);
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

		chartBar = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
		chartBar.setBorder(BorderFactory.createEmptyBorder(-1, 0, -1, -1));
		chartBar.setBlockIncrement(36);
		
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

		hierarchyCbx = new JComboBox(new String[] { "Proteins", "Peptides", "Spectra" });
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

		chartScrollPnl.add(hierarchyCbx, CC.xy(2, 2));
		chartScrollPnl.add(chartPnl, CC.xywh(1, 1, 3, 3));
		chartScrollPnl.add(chartBar, CC.xywh(4, 1, 1, 3));

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
		
		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
		    public void actionPerformed(ActionEvent ev) {
		        try {
		            Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		detailsTbl.getColumn(1).setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
				compLabel.setHorizontalAlignment(SwingConstants.CENTER);
				return compLabel;
			}
		});
		
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
	 * Refreshes the chart updating its content reflecting the specified chart type.
	 * @param chartType the type of chart to be displayed.
	 */
	private void updateChart(ChartType chartType) {
		Chart chart = null;
		
		if (chartType instanceof OntologyChartType) {
			chart = ChartFactory.createOntologyPieChart(ontologyData, chartType);
		} else if (chartType instanceof TaxonomyChartType) {
			chart = ChartFactory.createTaxonomyPieChart(taxonomyData, chartType);
		} else {
			chart = ChartFactory.createTopBarChart(topData, chartType);
		}
		
		if (chart != null) {
			if (chart.getChart().getPlot() instanceof PiePlot) {
				chartBar.setMaximum(360);
				chartBar.setValue((int) pieChartAngle);
				((PiePlot) chart.getChart().getPlot()).setStartAngle(pieChartAngle);
				hierarchyCbx.setVisible(true);
			} else {
				double temp = pieChartAngle;
				chartBar.setMaximum(0);
				pieChartAngle = temp;
				hierarchyCbx.setVisible(false);
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
					ecNumbers.addAll(ph.getUniprotEntry().getProteinDescription().getEcNumbers());
					for (DatabaseCrossReference xref : 
						ph.getUniprotEntry().getDatabaseCrossReferences(DatabaseType.KO)) {
						kNumbers.add(((KO) xref).getKOIdentifier().getValue());
					}
				}
				Set<Short> pathwayIDs = new HashSet<Short>();
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

				totalSpecLbl.setText("" + dbSearchResult.getTotalSpectrumCount());
				identSpecLbl.setText("" + dbSearchResult.getIdentifiedSpectrumCount());
				totalPepLbl.setText("" + dbSearchResult.getTotalPeptideCount());
				uniquePepLbl.setText("" + dbSearchResult.getUniquePeptideCount());
				totalProtLbl.setText("" + dbSearchResult.getProteinHitList().size());
				specificProtLbl.setText("" + "??");	// TODO: determining protein redundancy is an unsolved problem!
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
	 * Returns the button to select the type of the chart inside the chart view panel.
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
			g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_GREEN, pt2, ColorUtils.LIGHT_GREEN));
			g2.fillRect(0, 0, getWidth(), getHeight());

			try {
				double total = Double.parseDouble(totalLbl.getText());
				if (total > 0.0) {
					double rel = Double.parseDouble(fracLbl.getText()) / total;
					int width = (int) (getWidth() * rel);
					g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_ORANGE, pt2, ColorUtils.LIGHT_ORANGE));
					g2.fillRect(getWidth() - width, 0, width, getHeight());
					String str = String.format("%.1f", rel * 100.0) + "%";
					FontMetrics fm = g2.getFontMetrics();
					Rectangle2D bounds = fm.getStringBounds(str, g2);

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//						g2.setPaint(ColorUtils.DARK_ORANGE);
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
