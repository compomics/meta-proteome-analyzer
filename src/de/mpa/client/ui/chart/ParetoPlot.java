//package de.mpa.client.ui.chart;
//
//import java.awt.BasicStroke;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.ComponentOrientation;
//import java.awt.Container;
//import java.awt.ContainerOrderFocusTraversalPolicy;
//import java.awt.Dimension;
//import java.awt.FocusTraversalPolicy;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Insets;
//import java.awt.Paint;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.RenderingHints;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.AdjustmentEvent;
//import java.awt.event.AdjustmentListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseWheelEvent;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.text.DecimalFormat;
//import java.text.Format;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;
//
//import javax.imageio.ImageIO;
//import javax.swing.BorderFactory;
//import javax.swing.ButtonGroup;
//import javax.swing.DefaultBoundedRangeModel;
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JFileChooser;
//import javax.swing.JLabel;
//import javax.swing.JMenu;
//import javax.swing.JMenuItem;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.JRadioButtonMenuItem;
//import javax.swing.JScrollBar;
//import javax.swing.JScrollPane;
//import javax.swing.JSlider;
//import javax.swing.JSpinner;
//import javax.swing.JToggleButton;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.SwingConstants;
//import javax.swing.SwingWorker;
//import javax.swing.UIManager;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.PopupMenuEvent;
//import javax.swing.event.PopupMenuListener;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.plaf.basic.BasicLabelUI;
//
//import org.jdesktop.swingx.JXBusyLabel;
//import org.jdesktop.swingx.JXErrorPane;
//import org.jdesktop.swingx.error.ErrorInfo;
//import org.jdesktop.swingx.error.ErrorLevel;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.AxisLocation;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.axis.NumberTick;
//import org.jfree.chart.axis.NumberTickUnit;
//import org.jfree.chart.axis.SymbolAxis;
//import org.jfree.chart.axis.Tick;
//import org.jfree.chart.axis.ValueAxis;
//import org.jfree.chart.entity.EntityCollection;
//import org.jfree.chart.plot.CrosshairState;
//import org.jfree.chart.plot.PlotRenderingInfo;
//import org.jfree.chart.plot.PlotState;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.PaintScale;
//import org.jfree.chart.renderer.xy.XYBlockRenderer;
//import org.jfree.chart.renderer.xy.XYItemRendererState;
//import org.jfree.chart.title.PaintScaleLegend;
//import org.jfree.chart.title.Title;
//import org.jfree.data.Range;
//import org.jfree.data.xy.MatrixSeries;
//import org.jfree.data.xy.MatrixSeriesCollection;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYZDataset;
//import org.jfree.text.TextUtilities;
//import org.jfree.ui.RectangleEdge;
//import org.jfree.ui.RectangleInsets;
//import org.jfree.ui.TextAnchor;
//
//import com.jgoodies.forms.factories.CC;
//import com.jgoodies.forms.layout.ColumnSpec;
//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.forms.layout.RowSpec;
//
//import de.mpa.client.Constants;
//import de.mpa.client.model.dbsearch.DbSearchResult;
//import de.mpa.client.ui.Busyable;
//import de.mpa.client.ui.ClientFrame;
//import de.mpa.client.ui.ConfirmFileChooser;
//import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
//import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
//import de.mpa.client.ui.icons.IconConstants;
//
///**
// * Plot the cumulative Pareto-distribution for the current experiment
// * 
// * Can plot Pareto distribution for: taxonomy, function and metaproteins
// * 
// * @author K. Schallert
// */
//public class ParetoPlot extends JScrollPane implements Busyable {
//	
//	/**
//	 * Enum holding axis identifiers.
//	 */
//	public enum Axis {
//		X_AXIS,
//		Y_AXIS,
//		Z_AXIS
//	}
//
//	/**
//	 * The heat map data container object reference.
//	 */
//	private HeatMapData data;
//	
//	/**
//	 * The amount of data rows in the dataset.
//	 */
//	private int rowCount;
//	
//	/**
//	 * The amount of data columns in the dataset.
//	 */
//	private int colCount;
//
//	/**
//	 * The preferred number of visible data rows.
//	 */
//	private int visRowCount;
//	
//	/**
//	 * The preferred number of visible data columns.
//	 */
//	private int visColCount;
//
//	/**
//	 * The button for choosing the type of x axis.
//	 */
//	private AxisPopupButton xBtn;
//
//	/**
//	 * The button for choosing the type of y axis.
//	 */
//	private AxisPopupButton yBtn;
//
//	/**
//	 * The button for choosing the type of z axis.
//	 */
//	private AxisPopupButton zBtn;
//
//	/**
//	 * The button widget for changing the number of visible rows/columns.
//	 */
//	private JToggleButton zoomBtn;
//
//	/**
//	 * The button widget providing export options.
//	 */
//	private JButton saveBtn;
//	
//	/**
//	 * Busy label for showing that updating the heat map is currently in process.
//	 */
//	private JXBusyLabel busyLbl;
//	
//	/**
//	 * Creates a scrollable heat map chart from the specified data container.
//	 * @param data the data container object
//	 */
//	public ParetoPlot(HeatMapData data) {
//		// Invoke super constructor to create basic scrollpane with 2 primary scrollbars
//		super(null, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		
//		// Store heat map data container reference
//		this.data = data;
//
//		// Create plot
//		String[] xLabels = data.getXLabels();
//		String[] yLabels = data.getYLabels();
//		XYPlot plot = this.createPlot("x axis", "y axis",
//				xLabels, yLabels,
//				0.0, data.getMaximum(), data.getMatrix());
//		// Create z axis color bar legend
//		PaintScaleLegend psl = this.createLegend("z axis", plot);
//		// Create chart from plot and legend
//		ChartPanel chartPnl = this.createChart("Heat Map", psl, plot);
//		
//		// Insert chart into pane
//		this.setViewportView(chartPnl);
//		
//		// Cache data dimensions
//		this.rowCount = yLabels.length;
//		this.colCount = xLabels.length;
//		
//		// modify scroll pane's scrolling behavior by removing default listeners
//		// and installing new ones
//		for (ChangeListener cl : this.getViewport().getChangeListeners()) {
//			this.getViewport().removeChangeListener(cl);
//		}
//		this.configureScrollBars();
//		
//	}
//	
//	/**
//	 * Refreshes the heat map data container and chart.
//	 */
//	public void updateData() {
//		this.updateData(null);
//	}
//
//	/**
//	 * Refreshes the heat map data container and chart using the specified result object.
//	 * @param result the result object
//	 */
//	public void updateData(final DbSearchResult result) {
//		// Process refresh operation in separate worker thread
//		new UpdateWorker(result).execute();
//	}
//
//	/**
//	 * Convenience method to refresh the layout positions of the axis buttons
//	 * and color legend of the chart
//	 * @param chartPnl the chart panel
//	 * @param chart the chart
//	 */
//	private void updateChartLayout() {
//		ChartPanel chartPnl = (ChartPanel) this.getViewport().getView();
//		JFreeChart chart = chartPnl.getChart();
//		
//		// Draw chart to image to get at rendering info object
//		BufferedImage img = new BufferedImage(chartPnl.getWidth(), chartPnl.getHeight(), BufferedImage.TYPE_INT_ARGB);
//		Rectangle bounds = chartPnl.getBounds();
//		chart.draw(img.createGraphics(), bounds, null, chartPnl.getChartRenderingInfo());
//		Rectangle2D dataBounds = chartPnl.getChartRenderingInfo().getPlotInfo().getDataArea().getBounds2D();
//	
//		// Adjust layout of chart panel
//		FormLayout layout = (FormLayout) chartPnl.getLayout();
//		layout.setColumnSpec(3, ColumnSpec.decode(
//				"l:" + ((int) dataBounds.getX() - 26) + "px"));
//		layout.setColumnSpec(5, ColumnSpec.decode(
//				"r:" + (int) (bounds.getWidth() - dataBounds.getWidth() - dataBounds.getX() - 25.0) + "px")); 
//		layout.setRowSpec(5, RowSpec.decode(
//				"b:" + (int) (bounds.getHeight() - dataBounds.getHeight() - dataBounds.getY() - 25.0) + "px"));
//		
//		// Adjust z axis color bar size
//		PaintScaleLegend legend = (PaintScaleLegend) chart.getSubtitle(0);
//		double btm = bounds.getHeight() - dataBounds.getHeight() - dataBounds.getY() - 1.0;
//		legend.setMargin(4.0, 8.0, btm, 9.0);
//		
//		// Re-paint panel
//		chartPnl.revalidate();
//		chartPnl.setRefreshBuffer(true);
//		chartPnl.repaint();
//		
//	}
//
//	/**
//	 * Convenience method to configure the existing two scrollbars and add a
//	 * third one.
//	 */
//	private void configureScrollBars() {
//		// Configure z axis scrollbar
//		JScrollBar vertBar = this.getPrimaryVerticalScrollbar();
//		vertBar.setValues(0, 1, 0, 100);
//		vertBar.setBlockIncrement(25);
//		DefaultBoundedRangeModel vertBarMdl = (DefaultBoundedRangeModel) vertBar.getModel();
//		ChangeListener[] vcl = vertBarMdl.getChangeListeners();
//		vertBarMdl.removeChangeListener(vcl[0]);
//		
//		vertBar.addAdjustmentListener(new AdjustmentListener() {
//			@Override
//			public void adjustmentValueChanged(AdjustmentEvent e) {
//				JScrollBar src = (JScrollBar) e.getSource();
//				int val = src.getMaximum() - e.getValue();
//				ChartPanel chartPnl = (ChartPanel) getViewport().getView();
//				PaintScaleLegend psl = (PaintScaleLegend) chartPnl.getChart().getSubtitle(0);
//				((RainbowPaintScale) psl.getScale()).setUpperBound(val);
//				psl.getAxis().setRange(-0.5, val + 0.5);
//			}
//		});
//		vertBar.putClientProperty("JScrollBar.isFreeStanding", false);
//		
//		// Configure x axis scrollbar
//		final JScrollBar horzBar = this.getHorizontalScrollBar();
//		horzBar.setValues(0, colCount, 0, colCount);
//		horzBar.setBlockIncrement(colCount / 4);
//		DefaultBoundedRangeModel horzBarMdl = (DefaultBoundedRangeModel) horzBar.getModel();
//		ChangeListener[] hcl = horzBarMdl.getChangeListeners();
//		horzBarMdl.removeChangeListener(hcl[0]);
//		
//		horzBar.addAdjustmentListener(new AdjustmentListener() {
//			@Override
//			public void adjustmentValueChanged(AdjustmentEvent e) {
//				if (HeatMapPane.this.isEnabled()) {
//					int val = e.getValue();
//					ChartPanel chartPnl = (ChartPanel) getViewport().getView();
//					XYPlot plot = chartPnl.getChart().getXYPlot();
//					ValueAxis xAxis = plot.getDomainAxis();
//					xAxis.setRange(val - 0.5, val + horzBar.getModel().getExtent() - 0.5);
//				}
//			}
//		});
//		horzBar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIManager.getColor("ScrollBar.thumbDarkShadow")));
//		
//		// Create y axis scrollbar
//		final JScrollBar secVertBar = new JScrollBar(JScrollBar.VERTICAL, 0, rowCount, 0, rowCount);
//		secVertBar.putClientProperty("JScrollBar.isFreeStanding", false);
//		secVertBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//		secVertBar.getComponent(0).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//		secVertBar.getComponent(1).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//		
//		secVertBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, -1));
//		Dimension tmp = secVertBar.getPreferredSize();
//		tmp.width = 15;
//		secVertBar.setPreferredSize(tmp);
//
//		secVertBar.setBlockIncrement(rowCount / 4);
//		
//		secVertBar.addAdjustmentListener(new AdjustmentListener() {
//			@Override
//			public void adjustmentValueChanged(AdjustmentEvent e) {
//				if (HeatMapPane.this.isEnabled()) {
//					int val = e.getValue();
//					ChartPanel chartPnl = (ChartPanel) getViewport().getView();
//					XYPlot plot = chartPnl.getChart().getXYPlot();
//					ValueAxis yAxis = plot.getRangeAxis();
//					yAxis.setRange(val - 0.5, val + secVertBar.getModel().getExtent() - 0.5);
//				}
//			}
//		});
//		
//		// Install property change listener for when the visible column/row counts are modified
//		this.addPropertyChangeListener(new PropertyChangeListener() {
//			
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				String propName = evt.getPropertyName();
//				// Determine property change type
//				if ("visRowCount".equals(propName)) {
//					// Get current row value, clamp to maximum if necessary
//					int row = secVertBar.getValue();
//					int rowCount = HeatMapPane.this.rowCount;
//					if (row > rowCount) {
//						row = rowCount;
//					}
//					// Get new new value from event, update scrollbar model
//					int visRowCount = (Integer) evt.getNewValue();
//					secVertBar.setValues(row, visRowCount, 0, rowCount);
//					secVertBar.setBlockIncrement(rowCount / 4);
//					// Update chart y axis value range
//					ChartPanel chartPnl = (ChartPanel) getViewport().getView();
//					XYPlot plot = chartPnl.getChart().getXYPlot();
//					ValueAxis yAxis = plot.getRangeAxis();
//					yAxis.setRange(-0.5, visRowCount - 0.5);
//				} else if ("visColCount".equals(propName)) {
//					// Get current column value, clamp to maximum if necessary
//					int col = horzBar.getValue();
//					int colCount = HeatMapPane.this.colCount;
//					if (col > colCount) {
//						col = colCount;
//					}
//					// Get new value from event, update scrollbar model
//					int visColCount = (Integer) evt.getNewValue();
//					horzBar.setValues(col, visColCount, 0, colCount);
//					horzBar.setBlockIncrement(colCount / 4);
//					// Update chart x axis value range
//					ChartPanel chartPnl = (ChartPanel) getViewport().getView();
//					XYPlot plot = chartPnl.getChart().getXYPlot();
//					ValueAxis xAxis = plot.getDomainAxis();
//					xAxis.setRange(-0.5, visColCount - 0.5);
//				}
//			}
//		});
//		
//		// Attach secondary vertical scroll bar to left-hand edge of pane
//		this.setRowHeaderView(secVertBar);
//		this.putClientProperty("secVertBar", secVertBar);
//	}
//
//	/**
//	 * Creates a chart panel from the specified plot and title.
//	 * @param title the title to be displayed above the plot
//	 * @param subtitle 
//	 * @param plot the plot to be displayed in the heat map view
//	 * @return the chart panel
//	 */
//	private ChartPanel createChart(String title, Title subtitle, XYPlot plot) {
//		
//		// create chart, add color bar next to it
//		JFreeChart chart = new JFreeChart(title, plot);
//		chart.removeLegend();
//		chart.addSubtitle(subtitle);
//		chart.setBackgroundPaint(Color.WHITE);
//		
//		// wrap chart in panel
//		ChartPanel chartPnl = new ChartPanel(chart) {
//			/**
//			 * The decimal formatter.
//			 */
//			Format formatter = new DecimalFormat("0");
//			
//			/**
//			 * {@inheritDoc}<p>
//			 * Overridden to show axis labels or matrix values below the mouse cursor.
//			 */
//			@Override
//			public String getToolTipText(MouseEvent evt) {
//				Point2D p = evt.getPoint();
//				Rectangle2D plotArea = getScreenDataArea();
//				XYPlot plot = (XYPlot) getChart().getPlot();
//				int chartX = (int) Math.round(plot.getDomainAxis().java2DToValue(
//						p.getX(), plotArea, plot.getDomainAxisEdge()));
//				int chartY = (int) Math.round(plot.getRangeAxis().java2DToValue(
//						p.getY(), plotArea, plot.getRangeAxisEdge()));
//				Range rangeX = plot.getDomainAxis().getRange();
//				Range rangeY = plot.getRangeAxis().getRange();
//				if (rangeX.contains(chartX) && rangeY.contains(chartY)) {
//					MatrixSeriesCollection dataset = (MatrixSeriesCollection) plot.getDataset();
//					double chartZ = dataset.getSeries(0).get(chartY, chartX);
//					return formatter.format(chartZ);
////					return Double.toString(chartZ);
//				} else {
//					if (rangeY.contains(chartY) && (chartX < rangeX.getLowerBound())) {
//						return ((SymbolAxis) plot.getRangeAxis()).getSymbols()[chartY];
//					} else if (rangeX.contains(chartX) && (chartY > rangeY.getUpperBound())) {
//						return ((SymbolAxis) plot.getDomainAxis()).getSymbols()[chartX];
//					}
//				}
//				return null;
//			}
//			
//			@Override
//			public Point getToolTipLocation(MouseEvent evt) {
//				if (getToolTipText(evt) != null) {
//					Point point = evt.getPoint();
//					point.translate(0, 20);
//					return point;
//				}
//				return null;
//			}
//			
//			@Override
//			protected void paintChildren(Graphics g) {
//				// Fade chart if disabled
//				if (!HeatMapPane.this.isEnabled()) {
//					g.setColor(new Color(255, 255, 255, 192));
//					g.fillRect(0, 0, this.getWidth(), this.getHeight());
//					
//					// Paint notification string if no data has been loaded yet
//					if (!HeatMapPane.this.isBusy()) {
//						Graphics2D g2d = (Graphics2D) g;
//						String str = "no results loaded";
//						int strWidth = g2d.getFontMetrics().stringWidth(str);
//						int strHeight = g2d.getFontMetrics().getHeight();
//						float xOffset = this.getWidth() / 2.125f - strWidth / 2.0f;
//						float yOffset = this.getHeight() / 2.05f;
//						g2d.fillRect((int) xOffset - 2, (int) yOffset - g2d.getFontMetrics().getAscent() - 1, strWidth + 4, strHeight + 4);
//						
//						g2d.setColor(Color.BLACK);
//						g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//		                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//						g2d.drawString(str, xOffset, yOffset);
//					}
//				}
//				super.paintChildren(g);
//			}
//			
//		};
//		chartPnl.setPreferredSize(new Dimension());
//		chartPnl.setMinimumDrawHeight(0);
//		chartPnl.setMaximumDrawHeight(1440);
//		chartPnl.setMinimumDrawWidth(0);
//		chartPnl.setMaximumDrawWidth(2560);
//		
//		// remove default context menu
//		chartPnl.removeMouseListener(chartPnl.getMouseListeners()[1]);
//		
//		// Set up default layout
//		FormLayout layout = new FormLayout(
//				"3px, 23px, 20px, c:0px:g, 51px, 23px, 2px",
//				"3px, t:23px, 1px, c:0px:g, 24px, f:23px, 2px");
//		chartPnl.setLayout(layout);
//		
//		Object[][] xyValues = new Object[][] { 
//				TaxonomyChartType.values(), 
//				OntologyChartType.values(), 
//				HierarchyLevel.values() };
//		Object[][] zValues = new Object[][] { HierarchyLevel.values() };
//
//		// Create axis buttons using default values
//		this.xBtn = new AxisPopupButton(Axis.X_AXIS, xyValues, OntologyChartType.BIOLOGICAL_PROCESS);
//		this.yBtn = new AxisPopupButton(Axis.Y_AXIS, xyValues, TaxonomyChartType.SPECIES);
//		this.zBtn = new AxisPopupButton(Axis.Z_AXIS, zValues, HierarchyLevel.PROTEIN_LEVEL);
//		
//		this.zoomBtn = this.createZoomButton();
//		this.saveBtn = this.createSaveButton();
//		
//		// Create busy label to visualize heat map updating being in progress
//		busyLbl = new JXBusyLabel(new Dimension(100, 100));
//		busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
//		busyLbl.setVisible(false);
//		
//		chartPnl.add(xBtn, CC.xy(4, 6));
//		chartPnl.add(yBtn, CC.xy(2, 4));
//		chartPnl.add(zBtn, CC.xy(6, 4));
//		chartPnl.add(zoomBtn, CC.xy(2, 6));
//		chartPnl.add(saveBtn, CC.xy(6, 2));
//		chartPnl.add(busyLbl, CC.xy(4, 4));
//		
//		return chartPnl;
//	}
//
//	/**
//	 * Convenience method to create a zoom button widget for showing a popup
//	 * containing axis zoom controls.
//	 * @return a zoom button widget
//	 */
//	private JToggleButton createZoomButton() {
//		
//		final JToggleButton zoomBtn = new JToggleButton(IconConstants.ZOOM_ICON);
//		zoomBtn.setRolloverIcon(IconConstants.ZOOM_ROLLOVER_ICON);
//		zoomBtn.setPressedIcon(IconConstants.ZOOM_PRESSED_ICON);
//		zoomBtn.setMargin(new Insets(2, 1, 1, 1));
//		zoomBtn.setPreferredSize(new Dimension(23, 23));
//		
//		// Create popup menu containing zoom controls, remove its background and border
//		final JPopupMenu zoomPop = new JPopupMenu();
//		zoomPop.setBorderPainted(false);
//		zoomPop.setOpaque(false);
//		zoomPop.setBackground(new Color(0, true));
//		
//		final JPanel zoomPnl = new JPanel(new FormLayout("21px, 1px, p:g", "f:p:g, 1px, f:21px"));
//		zoomPnl.setOpaque(false);
//
//		// Create sub-panel containing vertical zoom slider and label
//		final JPanel zoomVertPnl = new JPanel(new FormLayout("p", "2dlu, p, 2dlu, p"));
//		zoomVertPnl.setBorder(zoomPop.getBorder());
//		
//		final JSlider zoomVertSld = new JSlider(JSlider.VERTICAL, 1, 26, 13);
//		zoomVertSld.setSnapToTicks(true);
//		
//		JLabel zoomVertLbl = new JLabel("" + zoomVertSld.getValue(), SwingConstants.RIGHT) {
//			@Override
//			public Dimension getPreferredSize() {
//				String text = this.getText();
//				this.setText("" + ((JSlider) this.getClientProperty("slider")).getMaximum());
//				Dimension size = super.getPreferredSize();
//				this.setText(text);
//				return size;
//			}
//		};
//		zoomVertLbl.setUI(new VerticalLabelUI(false));
//		zoomVertLbl.setFocusable(false);
//		
//		// Link label to slider via client property
//		zoomVertSld.putClientProperty("label", zoomVertLbl);
//		zoomVertLbl.putClientProperty("slider", zoomVertSld);
//
//		zoomVertPnl.add(zoomVertLbl, CC.xy(1, 2));
//		zoomVertPnl.add(zoomVertSld, CC.xy(1, 4));
//		
//		// Create sub-panel containing horizontal zoom slider and label
//		JPanel zoomHorzPnl = new JPanel(new FormLayout("p, 2dlu, p, 2dlu", "p"));
//		zoomHorzPnl.setBorder(zoomPop.getBorder());
//		
//		final JSlider zoomHorzSld = new JSlider(JSlider.HORIZONTAL, 1, 26, 13);
//		zoomHorzSld.setSnapToTicks(true);
//		
//		JLabel zoomHorzLbl = new JLabel("" + zoomHorzSld.getValue(), SwingConstants.RIGHT) {
//			@Override
//			public Dimension getPreferredSize() {
//				String text = this.getText();
//				this.setText("" + ((JSlider) this.getClientProperty("slider")).getMaximum());
//				Dimension size = super.getPreferredSize();
//				this.setText(text);
//				return size;
//			}
//		};
//		zoomHorzLbl.setFocusable(false);
//
//		// Link label to slider via client property
//		zoomHorzSld.putClientProperty("label", zoomHorzLbl);
//		zoomHorzLbl.putClientProperty("slider", zoomHorzSld);
//		
//		zoomHorzPnl.add(zoomHorzSld, CC.xy(1, 1));
//		zoomHorzPnl.add(zoomHorzLbl, CC.xy(3, 1));
//		
//		// Create change listener for sliders
//		ChangeListener cl = new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent evt) {
//				JSlider slider = (JSlider) evt.getSource();
//				int value = slider.getValue();
//				JLabel label = (JLabel) slider.getClientProperty("label");
//				label.setText("" + value);
//				if (slider.getOrientation() == JSlider.VERTICAL) {
//					HeatMapPane.this.setVisibleRowCount(value);
//				} else {
//					HeatMapPane.this.setVisibleColumnCount(value);
//				}
//			}
//		};
//		zoomVertSld.addChangeListener(cl);
//		zoomHorzSld.addChangeListener(cl);
//		
//		// create mouse listener for sliders
//		MouseAdapter ma;
//		ma = new MouseAdapter() {
////				@Override
////				public void mouseEntered(MouseEvent evt) {
////					Component src = (Component) evt.getSource();
////					src.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
////				}
////				@Override
////				public void mouseExited(MouseEvent evt) {
////					Component src = (Component) evt.getSource();
////					src.setCursor(Cursor.getDefaultCursor());
////				}
//			@Override
//			public void mouseWheelMoved(MouseWheelEvent evt) {
//				JSlider src = (JSlider) evt.getSource();
//				if (src.getOrientation() == SwingConstants.HORIZONTAL) {
//					src.setValue(src.getValue() + evt.getWheelRotation());
//				} else {
//					src.setValue(src.getValue() - evt.getWheelRotation());
//				}
//			}
//		};
//		zoomVertSld.addMouseWheelListener(ma);
////		zoomVertSld.addMouseListener(ma);
//		zoomHorzSld.addMouseWheelListener(ma);
////		zoomHorzSld.addMouseListener(ma);
//		
//		zoomPnl.add(zoomVertPnl, CC.xywh(1, 1, 2, 2));
//		zoomPnl.add(zoomHorzPnl, CC.xywh(2, 2, 2, 2));
//		
//		// Make focus cycle between both sliders
//		FocusTraversalPolicy policy = new ContainerOrderFocusTraversalPolicy() {
//			@Override
//			public Component getComponentAfter(Container aContainer,
//					Component aComponent) {
//				if (aComponent == zoomHorzSld) {
//					return zoomVertSld;
//				} else {
//					return zoomHorzSld;
//				}
//			}
//			@Override
//			public Component getComponentBefore(Container aContainer,
//					Component aComponent) {
//				if (aComponent == zoomVertSld) {
//					return zoomHorzSld;
//				} else {
//					return zoomVertSld;
//				}
//			}
//		};
//		zoomPnl.setFocusCycleRoot(true);
//		zoomPnl.setFocusTraversalPolicy(policy);
//		
//		// Add mouse listener to dismiss popup when clicking on invisible panel background
//		zoomPnl.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent evt) {
//				Component comp = zoomPnl.getComponentAt(evt.getPoint());
//				if (comp == zoomPnl) {
//					zoomPop.setVisible(false);
//				}
//			}
//		});
//		
//		zoomPop.add(zoomPnl);
//		
//		// Add popup listener to synchronize popup visibility with button selection state
//		zoomPop.addPopupMenuListener(new PopupMenuListener() {
//			@Override
//			public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
//			@Override
//			public void popupMenuCanceled(PopupMenuEvent e) { }
//			@Override
//			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//				zoomBtn.setSelected(false);
//			}
//		});
//		
//		// Add item listener to button to display popup above button on click
//		zoomBtn.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent evt) {
//				if (evt.getStateChange() == ItemEvent.SELECTED) {
//					Component src = (Component) evt.getSource();
//					zoomPop.show(src, -3, -2 - zoomVertPnl.getPreferredSize().height);
//					zoomVertSld.requestFocus();
//				}
//			}
//		});
//
//		// listen to vertical/horizontal row count changes and update sliders accordingly
//		this.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				String propName = evt.getPropertyName();
//				Object value = evt.getNewValue();
//				if ("visRowCount".equals(propName)) {
//					zoomVertSld.setValue((Integer) value);
//				} else if ("rowCount".equals(propName)) {
//					zoomVertSld.setMaximum((Integer) value);
//				} else if ("visColCount".equals(propName)) {
//					zoomHorzSld.setValue((Integer) value);
//				} else if ("colCount".equals(propName)) {
//					zoomHorzSld.setMaximum((Integer) value);
//				}
//			}
//		});
//		
//		return zoomBtn;
//	}
//
//	/**
//	 * Convenience method to create a button widget for showing options to
//	 * export heat map chart data.
//	 * @return a save button widget
//	 */
//	private JButton createSaveButton() {
//		
//			// Init button
//			JButton saveBtn = new JButton(IconConstants.SAVE_ICON);
//			saveBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
//			saveBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
//			
//			// Add action listener showing a save dialog
//			saveBtn.addActionListener(new ActionListener() {
//				
//				/**
//				 * The inner margin of the chart panel around the central plot.
//				 */
//				private Insets margin = new Insets(27, 46, 48, 87);
//				
//				/**
//				 * The height of a data row in pixels.
//				 */
//				private int rowHeight = 30;
//				
//				/**
//				 * The width of a data column in pixels.
//				 */
//				private int colWidth = 30;
//	
//				@Override
//				public void actionPerformed(ActionEvent evt) {
//					HeatMapPane heatMap = HeatMapPane.this;
//					
//					// Calculate margins from plot area
//					ChartPanel chartPnl = (ChartPanel) heatMap.getViewport().getView();
//					XYPlot plot = (XYPlot) chartPnl.getChart().getPlot();
//					SymbolAxisExt xAxis = (SymbolAxisExt) plot.getDomainAxis();
//					SymbolAxisExt yAxis = (SymbolAxisExt) plot.getRangeAxis();
//					// Disable axis tick label truncation
//					int oldXLabelSize = xAxis.getMaximumTickLabelSize();
//					int oldYLabelSize = yAxis.getMaximumTickLabelSize();
//					xAxis.setMaximumTickLabelSize(-1);
//					yAxis.setMaximumTickLabelSize(-1);
//
//					// Paint chart into image to get access to rendering info object
//					BufferedImage bi = new BufferedImage(chartPnl.getWidth(), chartPnl.getHeight(), BufferedImage.TYPE_INT_ARGB);
//					Rectangle bounds = chartPnl.getBounds();
//					chartPnl.getChart().draw(bi.createGraphics(), bounds, null, chartPnl.getChartRenderingInfo());
//					Rectangle dataBounds = chartPnl.getChartRenderingInfo().getPlotInfo().getDataArea().getBounds();
//
//					this.margin.set(dataBounds.y, dataBounds.x,
//							bounds.height - dataBounds.y - dataBounds.height,
//							bounds.width - dataBounds.x - dataBounds.width);
//
//					// Init file chooser
//					ConfirmFileChooser chooser = new ConfirmFileChooser();
//					// Create panel containing image size controls
//					final JPanel accessoryPnl = new JPanel(new FormLayout(
//							"5dlu, p, 2dlu, 45px, 1dlu, p, 0dlu",
//							"p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p"));
//
//					JPanel matDimPnl = new JPanel(new FormLayout("p, 2dlu, p, 2dlu, p", "p"));
//					JLabel matRowLbl = new JLabel("" + heatMap.data.getYLabels().length);
//					JLabel matColLbl = new JLabel("" + heatMap.data.getXLabels().length);
//					matDimPnl.add(matRowLbl, CC.xy(1, 1));
//					matDimPnl.add(new JLabel("x"), CC.xy(3, 1));
//					matDimPnl.add(matColLbl, CC.xy(5, 1));
//					
//					// Init spinners for manipulating row/column pixel size
//					JSpinner rowSpn = new JSpinner(new SpinnerNumberModel(this.rowHeight, 15, null, 5));
//					JSpinner colSpn = new JSpinner(new SpinnerNumberModel(this.colWidth, 15, null, 5));
//	
//					// Init and Lay out labels for previewing component size
//					JPanel imgDimPnl = new JPanel(new FormLayout("p, 2dlu, p, 2dlu, p, 1dlu, p", "p")) {
//						@Override
//						public void setEnabled(boolean enabled) {
//							super.setEnabled(enabled);
//							// Propagate enable state to children
//							for (Component comp : this.getComponents()) {
//								comp.setEnabled(enabled);
//							}
//						}
//					};
//					JLabel imgRowLbl = new JLabel("" + this.calculateHeight((Integer) rowSpn.getValue()));
//					JLabel imgColLbl = new JLabel("" + this.calculateWidth((Integer) colSpn.getValue()));
//					imgDimPnl.add(imgRowLbl, CC.xy(1, 1));
//					imgDimPnl.add(new JLabel("x"), CC.xy(3, 1));
//					imgDimPnl.add(imgColLbl, CC.xy(5, 1));
//					imgDimPnl.add(new JLabel("px"), CC.xy(7, 1));
//					
//					// Link labels to spinners
//					rowSpn.putClientProperty("label", imgRowLbl);
//					rowSpn.putClientProperty("dimension", "height");
//					colSpn.putClientProperty("label", imgColLbl);
//	
//					// Create change listener for updating spinner labels on value change
//					ChangeListener cl = new ChangeListener() {
//						@Override
//						public void stateChanged(ChangeEvent evt) {
//							// Get spinner reference and current value
//							JSpinner spinner = (JSpinner) evt.getSource();
//							Integer value = (Integer) spinner.getValue();
//							// Determine which spinner fired the event
//							if ("height".equals(spinner.getClientProperty("dimension"))) {
//								// Calculate total height
//								rowHeight = value.intValue();
//								value = calculateHeight(value);
//							} else {
//								// Calculate total width
//								colWidth = value.intValue();
//								value = calculateWidth(value);
//							}
//							// Update corresponding spinner label
//							((JLabel) spinner.getClientProperty("label")).setText(value.toString());
//						}
//					};
//					rowSpn.addChangeListener(cl);
//					colSpn.addChangeListener(cl);
//					
//					// Lay out accessory components
//					accessoryPnl.add(new JLabel("Matrix dimensions:"), CC.xyw(2, 1, 5));
//					accessoryPnl.add(matDimPnl, CC.xyw(2, 3, 5));
//					
//					accessoryPnl.add(new JLabel("Row height"), CC.xy(2, 5));
//					accessoryPnl.add(rowSpn, CC.xy(4, 5));
//					accessoryPnl.add(new JLabel("px"), CC.xy(6, 5));
//					accessoryPnl.add(new JLabel("Col. height"), CC.xy(2, 7));
//					accessoryPnl.add(colSpn, CC.xy(4, 7));
//					accessoryPnl.add(new JLabel("px"), CC.xy(6, 7));
//					
//					accessoryPnl.add(new JLabel("Image dimensions:"), CC.xyw(2, 9, 5));
//					accessoryPnl.add(imgDimPnl, CC.xyw(2, 11, 5));
//					
//					// Attach accessory panel to file chooser
//					chooser.setAccessory(accessoryPnl);
//					chooser.addChoosableFileFilter(Constants.PNG_FILE_FILTER);
//					chooser.addChoosableFileFilter(Constants.CSV_FILE_FILTER);
//					chooser.addChoosableFileFilter(Constants.EXCEL_XML_FILE_FILTER);
//					chooser.setAcceptAllFileFilterUsed(false);
//					chooser.setFileFilter(Constants.PNG_FILE_FILTER);
//					
//					// Install listener to track file filter selection changes
//					chooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
//							new PropertyChangeListener() {
//								@Override
//								public void propertyChange(PropertyChangeEvent evt) {
//									// Show image-related GUI elements only when PNG filter is selected
//									boolean showImageControls =
//											(evt.getNewValue() == Constants.PNG_FILE_FILTER);
//									for (int i = 2; i < accessoryPnl.getComponentCount(); i++) {
//										accessoryPnl.getComponent(i).setEnabled(showImageControls);
//									}
//								}
//							});
//					
//					// Show dialog
//					int res = chooser.showSaveDialog(heatMap);
//					if (res == JFileChooser.APPROVE_OPTION) {
//						// Get single selected file
//						File file = chooser.getSelectedFile();
//						
//						// Determine file type
//						FileFilter filter = chooser.getFileFilter();
//						
//						if (filter == Constants.PNG_FILE_FILTER) {
//							this.exportPNG(file, bi);
//						} else if (filter == Constants.CSV_FILE_FILTER) {
//							this.exportCSV(file);
//						} else if (filter == Constants.EXCEL_XML_FILE_FILTER) {
//							this.exportXML(file);
//						}
//						
//					}
//					// Restore tick label truncation
//					xAxis.setMaximumTickLabelSize(oldXLabelSize);
//					yAxis.setMaximumTickLabelSize(oldYLabelSize);
//				}
//				
//				private void exportXML(File file) {
//					// Get data container
//					HeatMapData data = HeatMapPane.this.data;
//					
//					// Extract values and labels
//					MatrixSeries matrix = data.getMatrix();
//					String[] xLabels = data.getXLabels();
//					String[] yLabels = data.getYLabels();
//
//					try {
//						// Init writer
//						BufferedWriter bw = new BufferedWriter(new FileWriter(file)) {
//							public Writer append(CharSequence csq) throws IOException {
//								Writer writer = super.append(csq);
//								// automatically append newline
//								this.newLine();
//								return writer;
//							};
//						};
//						
//						// write header
//						bw.append("<?xml version=\"1.0\"?>\n"
//								+ "<?mso-application progid=\"Excel.Sheet\"?>");
//						// write root tag
//						bw.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
//						bw.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
//						bw.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
//						bw.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
//						bw.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
//						// write style tags
//						bw.append(" <Styles>");
//						// TODO: borders
//						// title style
//						bw.append("  <Style ss:ID=\"title\" ss:Name=\"title\">");
//						bw.append("   <Alignment ss:Horizontal=\"Center\"/>");
//						bw.append("   <Font ss:Bold=\"1\"/>");
//						bw.append("  </Style>");
//						// x label style
//						bw.append("  <Style ss:ID=\"xLabels\" ss:Name=\"xLabels\">");
//						bw.append("   <Alignment ss:Vertical=\"Top\" ss:Rotate=\"90\"/>");
//						bw.append("  </Style>");
//						// y label style
//						bw.append("  <Style ss:ID=\"yLabels\" ss:Name=\"yLabels\">");
//						bw.append("   <Alignment ss:Horizontal=\"Right\"/>");
//						bw.append("  </Style>");
//						// cell styles
//						// TODO: so far we only support integer values in heat maps, this needs adjusting if decimal values get supported
//						Set<Integer> matrixValues = new TreeSet<Integer>();
//						for (int i = 0; i < matrix.getRowCount(); i++) {
//							for (int j = 0; j < matrix.getColumnsCount(); j++) {
//								matrixValues.add((int) matrix.get(i, j));
//							}
//						}
//						// get paint scale
//						ChartPanel chartPnl = (ChartPanel) HeatMapPane.this.getViewport().getView();
//						XYPlot plot = (XYPlot) chartPnl.getChart().getPlot();
//						PaintScale paintScale = ((XYBlockRenderer) plot.getRenderer()).getPaintScale();
//						for (Integer val : matrixValues) {
//							// name styles like the values they represent
//							bw.append("  <Style ss:ID=\"" + val + "\" ss:Name=\"" + val + "\">");
//							bw.append("   <Alignment ss:Horizontal=\"Center\"/>");
//							// get color from paint scale
//							Color color = (Color) paintScale.getPaint(val);
//							// use inverted color for text
//							String hex = String.format("#%02x%02x%02x",
//									255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
//							bw.append("   <Font ss:Color=\"" + hex + "\"/>");
//							hex = String.format("#%02x%02x%02x",
//									color.getRed(), color.getGreen(), color.getBlue());
//							bw.append("   <Interior ss:Color=\"" + hex + "\" ss:Pattern=\"Solid\"/>");
//							bw.append("  </Style>");
//						}
//						bw.append(" </Styles>");
//						// write worksheet tag
//						String title = "Heat Map";
//						bw.append(" <Worksheet ss:Name=\"" + title + "\">");
//						// write table tag
//						bw.append("  <Table>");
//						// write columns
//						bw.append("   <Column ss:AutoFitWidth=\"1\"/>");
//						bw.append("   <Column ss:AutoFitWidth=\"1\" ss:Span=\"" + (xLabels.length - 1) + "\"/>");
//						// write title row
//						bw.append("   <Row>");
//						bw.append("    <Cell ss:Index=\"2\" ss:MergeAcross=\"" + (xLabels.length - 2) + "\" ss:StyleID=\"title\">");
//						bw.append("     <Data ss:Type=\"String\">" + title + "</Data>");
//						bw.append("    </Cell>");
//						bw.append("   </Row>");
//						// write cell rows
//						for (int i = 0; i < yLabels.length; i++) {
//							bw.append("   <Row>");
//							// write y label
//							bw.append("    <Cell ss:StyleID=\"yLabels\">");
//							bw.append("     <Data ss:Type=\"String\">" + yLabels[i] + "</Data>");
//							bw.append("    </Cell>");
//							// write cells
//							for (int j = 0; j < xLabels.length; j++) {
//								int value = (int) matrix.get(i, j);
//								// use random style
//								bw.append("    <Cell ss:StyleID=\"" + value + "\">");
//								// use random value
//								bw.append("     <Data ss:Type=\"Number\">" + value + "</Data>");
//								bw.append("    </Cell>");
//							}
//							bw.append("   </Row>");
//						}
//						// write x label row
//						bw.append("   <Row ss:AutoFitHeight=\"1\">");
//						bw.append("    <Cell/>");	// empty cell
//						for (int i = 0; i < xLabels.length; i++) {
//							bw.append("    <Cell ss:StyleID=\"xLabels\">");
//							bw.append("     <Data ss:Type=\"String\">" + xLabels[i] + "</Data>");
//							bw.append("    </Cell>");
//						}
//						bw.append("   </Row>");
//						// close tags
//						bw.append("  </Table>");
//						bw.append(" </Worksheet>");
//						bw.append("</Workbook>");
//						
//						// Clean up
//						bw.flush();
//						bw.close();
//					} catch (IOException e) {
//						JXErrorPane.showDialog(ClientFrame.getInstance(),
//								new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//					}
//				}
//				
//				/**
//				 * Exports the heat map data as a tab-separated CSV file.
//				 * @param file the file to save
//				 */
//				private void exportCSV(File file) {
//					// Get data container
//					HeatMapData data = HeatMapPane.this.data;
//					
//					// Extract values and labels
//					MatrixSeries matrix = data.getMatrix();
//					String[] xLabels = data.getXLabels();
//					String[] yLabels = data.getYLabels();
//
//					try {
//						// Init writer
//						BufferedWriter bw = new BufferedWriter(new FileWriter(file));
//
//						// Build CSV text rows
//						StringBuilder sb = new StringBuilder();
//						// Add x axis labels in header line
//						for (String xLabel : xLabels) {
//							sb.append("\t");
//							sb.append(xLabel);
//						}
//						// Write header to file
//						bw.write(sb.toString());
//						bw.newLine();
//						// Add matrix rows headed by y axis labels
//						for (int i = 0; i < matrix.getRowCount(); i++) {
//							// Clear string builder
//							sb.setLength(0);
//							// Add y axis label
//							sb.append(yLabels[i]);
//							// Add matrix values
//							for (int j = 0; j < matrix.getColumnsCount(); j++) {
//								sb.append("\t");
//								sb.append(matrix.get(i, j));
//							}
//							// Write line to file
//							bw.write(sb.toString());
//							bw.newLine();
//						}
//						// Clean up
//						bw.flush();
//						bw.close();
//					} catch (IOException e) {
//						JXErrorPane.showDialog(ClientFrame.getInstance(),
//								new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//					}
//				}
//
//				/**
//				 * Exports the heat map chart as a PNG image.
//				 * @param file the file to save
//				 * @param bi the buffered image instance
//				 */
//				private void exportPNG(File file, BufferedImage bi) {
//					// Gather chart references
//					HeatMapPane heatMap = HeatMapPane.this;
//					ChartPanel chartPnl = (ChartPanel) heatMap.getViewport().getView();
//					XYPlot plot = (XYPlot) chartPnl.getChart().getPlot();
//					SymbolAxisExt xAxis = (SymbolAxisExt) plot.getDomainAxis();
//					SymbolAxisExt yAxis = (SymbolAxisExt) plot.getRangeAxis();
//					
//					int width = this.calculateWidth(this.colWidth);
//					int height = this.calculateHeight(this.rowHeight);
//					
//					// Modify chart panel size to fit all rows/columns, hide GUI controls
//					for (Component comp : chartPnl.getComponents()) {
//						comp.setVisible(false);
//					}
//					chartPnl.setSize(width, height);
//					// Modify draw size to prevent stretched text on up-scaling
//					chartPnl.setMaximumDrawHeight(height);
//					chartPnl.setMaximumDrawWidth(width);
//
//					// Make axis labels visible
//					xAxis.setLabelPaint(Color.BLACK);
//					yAxis.setLabelPaint(Color.BLACK);
//
//					// Adjust z axis color bar size, make label visible
//					PaintScaleLegend legend = (PaintScaleLegend) chartPnl.getChart().getSubtitle(0);
//					RectangleInsets oldMargin = legend.getMargin();
//					// FIXME: (low priority) bottom margin does not appear to be correct in some cases
//					legend.setMargin(4.0, 8.0, this.margin.bottom, 9.0);
//					legend.getAxis().setLabelPaint(Color.BLACK);
//					
//					// Cache old visible row/column counts, set to maximum row/column counts
//					int oldRowCount = heatMap.getVisibleRowCount();
//					int oldColCount = heatMap.getVisibleColumnCount();
//					heatMap.setVisibleRowCount(heatMap.rowCount);
//					heatMap.setVisibleColumnCount(heatMap.colCount);
//					
//					// Paint adjusted component into buffered image
//					bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//					chartPnl.paint(bi.createGraphics());
//					
//					// Write image to disk using loss-less PNG compression
//					try {
//						ImageIO.write(bi, "png", file);
//					} catch (IOException e) {
//						JXErrorPane.showDialog(ClientFrame.getInstance(),
//								new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//					}
//					
//					// Restore original state of chart panel
//					for (Component comp : chartPnl.getComponents()) {
//						comp.setVisible(true);
//					}
//					chartPnl.setMaximumDrawHeight(1440);
//					chartPnl.setMaximumDrawWidth(2560);
//					heatMap.setVisibleRowCount(oldRowCount);
//					heatMap.setVisibleColumnCount(oldColCount);
//					xAxis.setLabelPaint(Color.WHITE);
//					yAxis.setLabelPaint(Color.WHITE);
//					legend.getAxis().setLabelPaint(Color.WHITE);
//					legend.setMargin(oldMargin);
//				}
//
//				/**
//				 * Convenience method to calculate the total component width based
//				 * on the specified pixel width for individual data columns and the
//				 * chart margins.
//				 * @param colWidth the pixel width of a data column
//				 * @return the total component width
//				 */
//				private int calculateWidth(int colWidth) {
//					return margin.left + HeatMapPane.this.colCount * (colWidth + 2) + margin.right;
//				}
//	
//				/**
//				 * Convenience method to calculate the total component height based
//				 * on the specified pixel height for individual data rows and the
//				 * chart margins.
//				 * @param rowHeight the pixel height of a data row
//				 * @return the total component height
//				 */
//				private int calculateHeight(int rowHeight) {
//					return  margin.top + HeatMapPane.this.rowCount * (rowHeight + 1) + margin.bottom;
//				}
//			});
//			
//			return saveBtn;
//		}
//
//	/**
//	 * Creates a color bar legend for the specified plot and displaying the specified title string
//	 * @param label the legend label
//	 * @param plot the plot
//	 * @return a color bar legend
//	 */
//	private PaintScaleLegend createLegend(String label, XYPlot plot) {
//		
//		// create color bar
//		PaintScale scale = ((XYBlockRenderer) plot.getRenderer()).getPaintScale();
//		
//		NumberAxis scaleAxis = new NumberAxis(label) {
//			@Override
//			protected double findMaximumTickLabelWidth(List ticks,
//					Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
//				RectangleInsets insets = this.getTickLabelInsets();
//		        Font font = this.getTickLabelFont();
//		        FontMetrics fm = g2.getFontMetrics(font);
//		        
//		        double upperBound = this.getDefaultAutoRange().getUpperBound();
//		        NumberTickUnit ntu = (NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(upperBound);
//				Tick tick = new NumberTick(upperBound, ntu.valueToString(upperBound),
//						TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 0.0);
//				
//		        Rectangle2D labelBounds = TextUtilities.getTextBounds(
//		        		tick.getText(), g2, fm);
//		       
//		        return labelBounds.getWidth() + insets.getLeft() + insets.getRight();
//			}
//		};
//		scaleAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		scaleAxis.setDefaultAutoRange(new Range(-0.5, 100.5));
//		scaleAxis.setRange(-0.5, 100.5);
//		scaleAxis.setLabelPaint(Color.WHITE);
//		
//		PaintScaleLegend psl = new PaintScaleLegend(scale, scaleAxis);
//		psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
//		psl.setMargin(4.0, 8.0, 48.0, 9.0);
//		psl.setPosition(RectangleEdge.RIGHT);
//		
//		return psl;
//	}
//
//	/**
//	 * Creates a heat map plot using the specified axis tick labels as well as
//	 * data values and applies the default blue-green-red color scale renderer
//	 * to it.
//	 * @param xTitle 
//	 * @param yTitle 
//	 * @param xLabels the tick labels of the x axis
//	 * @param yLabels the tick labels of the y axis
//	 * @param data the matrix of values to be displayed as colored blocks
//	 * @return the heat map plot
//	 */
//	private XYPlot createPlot(String xTitle, String yTitle, String[] xLabels, String[] yLabels, 
//			double lowerBound, double upperBound, MatrixSeries data) {
//		return this.createPlot(xTitle, yTitle, xLabels, yLabels, data, this.createRenderer(lowerBound, upperBound));
//	}
//
//	/**
//	 * Creates a heat map plot using the specified axis tick labels, data values
//	 * and renderer.
//	 * @param xTitle 
//	 * @param yTitle 
//	 * @param xLabels the tick labels of the x axis
//	 * @param yLabels the tick labels of the y axis
//	 * @param data the matrix of values to be displayed as colored blocks
//	 * @param renderer the renderer to draw colored blocks
//	 * @return the heat map plot
//	 */
//	private XYPlot createPlot(String xTitle, String yTitle, String[] xLabels, String[] yLabels, 
//			MatrixSeries data, XYBlockRenderer renderer) {
//	
//		// create x axis
//		FontMetrics fm = this.getFontMetrics(org.jfree.chart.axis.Axis.DEFAULT_TICK_LABEL_FONT);
//		SymbolAxis xAxis = new SymbolAxisExt(xTitle, xLabels, fm);
//		xAxis.setGridBandsVisible(false);
//		xAxis.setVerticalTickLabels(true);
//		xAxis.setTickLabelInsets(new RectangleInsets(4, 2, 4, 2));
//		xAxis.setLabelPaint(Color.WHITE);
//		
//		// create y axis
//		SymbolAxis yAxis = new SymbolAxisExt(yTitle, yLabels, fm);
//		yAxis.setInverted(true);
//		yAxis.setLabelPaint(Color.WHITE);
//		
//		// create plot, hide grid lines
//		XYPlot plot = new XYPlot(new MatrixSeriesCollection(data),
//				xAxis, yAxis, renderer) {
//			@Override
//			public void draw(Graphics2D g2, Rectangle2D area,
//					Point2D anchor, PlotState parentState,
//					PlotRenderingInfo info) {
//				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//						RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//				super.draw(g2, area, anchor, parentState, info);
//			}
//		};
//	    plot.setRangeGridlinesVisible(false);
//	    plot.setDomainGridlinesVisible(false);
//	    plot.setInsets(new RectangleInsets(4.0, 8.0, 8.0, 8.0));
//	    
//		return plot;
//	}
//
//	/**
//	 * Creates a renderer for drawing colored rectangles for values in the range
//	 * between the specified lower and upper bounds
//	 * @param lowerBound the lower value boundary
//	 * @param upperBound the upper value boundary
//	 * @return a block renderer for colored gradients
//	 */
//	private XYBlockRenderer createRenderer(double lowerBound, double upperBound) {
//		// init renderer
//		XYBlockRenderer renderer = new XYBlockRenderer() {
//			// Override draw method to add grey border around item blocks
//			@Override
//			public void drawItem(Graphics2D g2, XYItemRendererState state,
//					Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
//					ValueAxis domainAxis, ValueAxis rangeAxis,
//					XYDataset dataset, int series, int item,
//					CrosshairState crosshairState, int pass) {
//				
//				double x = dataset.getXValue(series, item);
//		        double y = dataset.getYValue(series, item);
//		        double z = 0.0;
//		        if (dataset instanceof XYZDataset) {
//		            z = ((XYZDataset) dataset).getZValue(series, item);
//		        }
//				Paint p = this.getPaintScale().getPaint(z);
////	            if (item == 0) {
////	            	System.out.println("" + z + "\n" + p);
////	            }
//				
//				double xx0 = domainAxis.valueToJava2D(x - 0.5, dataArea,
//		                plot.getDomainAxisEdge());
//		        double yy0 = rangeAxis.valueToJava2D(y - 0.5, dataArea,
//		                plot.getRangeAxisEdge());
//		        double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth() - 0.5,
//		        		dataArea, plot.getDomainAxisEdge());
//		        double yy1 = rangeAxis.valueToJava2D(y + this.getBlockHeight() - 0.5,
//		        		dataArea, plot.getRangeAxisEdge());
//		        Rectangle2D block;
////		        PlotOrientation orientation = plot.getOrientation();
////		        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
////		            block = new Rectangle2D.Double(Math.min(yy0, yy1),
////		                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
////		                    Math.abs(xx0 - xx1));
////		        } else {
//		            block = new Rectangle2D.Double(Math.min(xx0, xx1),
//		                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
//		                    Math.abs(yy1 - yy0));
////		        }
//		        g2.setPaint(p);
//		        g2.fill(block);
//		        
//		        g2.setPaint(Color.GRAY);
//		        g2.setStroke(new BasicStroke(1.0f));
//		        g2.draw(block);
//	
//		        EntityCollection entities = state.getEntityCollection();
//		        if (entities != null) {
//		            addEntity(entities, block, dataset, series, item, 0.0, 0.0);
//		        }
//			}
//		};
//		renderer.setPaintScale(new RainbowPaintScale(lowerBound, upperBound));
//		
//		return renderer;
//	}
//
//	/**
//	 * {@inheritDoc}<p>
//	 * Overridden to return the secondary vertical scroll bar.
//	 */
//	@Override
//	public JScrollBar getVerticalScrollBar() {
//		return this.getVerticalScrollBar(false);
//	}
//	
//	/**
//	 * Returns either one of the vertical scroll bars.
//	 * @param primary if <code>true</code> the primary scroll bar will be returned, 
//	 * 				  otherwise the secondary scroll bar will be returned
//	 * @return the primary or secondary vertical scroll bar
//	 */
//	public JScrollBar getVerticalScrollBar(boolean primary) {
//		if (primary) {
//			return super.getVerticalScrollBar();
//		} else {
//			return this.getSecondaryVerticalScrollBar();
//		}
//	}
//	
//	/**
//	 * Returns the primary vertical scroll bar (associated with the z axis).
//	 * @return the primary vertical scroll bar
//	 */
//	public JScrollBar getPrimaryVerticalScrollbar() {
//		return getVerticalScrollBar(true);
//	}
//	
//	/**
//	 * Returns the secondary vertical scroll bar (associated with the y axis).
//	 * @return the secondary vertical scroll bar
//	 */
//	public JScrollBar getSecondaryVerticalScrollBar() {
//		return (JScrollBar) this.getClientProperty("secVertBar");
//	}
//
//	/**
//	 * Returns the preferred number of data columns being displayed in the scrollable view.
//	 * @return the number of visible columns
//	 */
//	public int getVisibleColumnCount() {
//		return this.visColCount;
//	}
//
//	/**
//	 * Sets the number of data columns being displayed in the scrollable view.
//	 * @param visColCount the number of visible columns
//	 */
//	public void setVisibleColumnCount(int visColCount) {
//		// Clamp value to upper boundary if necessary
//		if (visColCount > this.colCount) {
//			visColCount = this.colCount;
//		}
//		// Propagate change event
//		this.firePropertyChange("visColCount", -1, visColCount);
//		
//		this.visColCount = visColCount;
//	}
//
//	/**
//	 * Returns the preferred number of data rows being displayed in the scrollable view.
//	 * @return the number of visible rows
//	 */
//	public int getVisibleRowCount() {
//		return this.visRowCount;
//	}
//
//	/**
//	 * Sets the number of data rows being displayed in the scrollable view.
//	 * @param visRowCount the number of visible rows
//	 */
//	public void setVisibleRowCount(int visRowCount) {
//		// Clamp value to upper boundary if necessary
//		if (visRowCount > this.rowCount) {
//			visRowCount = this.rowCount;
//		}
//		// Propagate change event
//		this.firePropertyChange("visRowCount", -1, visRowCount);
//		
//		this.visRowCount = visRowCount;
//		
//	}
//	
//	/**
//	 * Returns the value client property of the axis button corresponding to the
//	 * specified axis.
//	 * @param axis the axis
//	 * @return the axis button value property
//	 */
//	public Object getAxisButtonValue(Axis axis) {
//		AxisPopupButton axisBtn = this.getAxisButton(axis);
//		if (axisBtn != null) {
//			return axisBtn.getValue();
//		}
//		System.err.println("ERROR: unknown axis specified");
//		return null;
//	}
//	
//	/**
//	 * Sets the value client property of the axis button corresponding to the
//	 * specified axis to the specified value object.
//	 * @param axis the axis
//	 * @param obj the balue object
//	 */
//	public void setAxisButtonValue(Axis axis, Object obj) {
//		AxisPopupButton axisBtn = this.getAxisButton(axis);
//		if (axisBtn != null) {
//			axisBtn.setValue(obj);
//			this.updateData();
//		} else {
//			System.err.println("ERROR: unknown axis specified");
//		}
//	}
//	
//	/**
//	 * Returns the button corresponding to the specified axis
//	 * @param axis the axis
//	 * @return the axis button
//	 */
//	public AxisPopupButton getAxisButton(Axis axis) {
//		switch (axis) {
//		case X_AXIS:
//			return this.xBtn;
//		case Y_AXIS:
//			return this.yBtn;
//		case Z_AXIS:
//			return this.zBtn;
//		default:
//			System.err.println("ERROR: unknown axis specified");
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns whether the heat map is currently in the process of being updated.
//	 * @return <code>true</code> if the heat map is updating, <code>false</code> otherwise
//	 */
//	public boolean isBusy() {
//		return this.busyLbl.isBusy();
//	}
//
//	/**
//	 * Sets the flag denoting whether the heat map is currently in the process of being updated.
//	 * @param updating the flag value to set
//	 */
//	public void setBusy(boolean busy) {
//		this.busyLbl.setBusy(busy);
//		this.busyLbl.setVisible(busy);
//		this.setEnabled(!busy);
//		this.repaint();
//	}
//
//	@Override
//	public void setEnabled(boolean enabled) {
//		super.setEnabled(enabled);
//		// Set enable state of button widgets
//		this.xBtn.setEnabled(enabled);
//		this.yBtn.setEnabled(enabled);
//		this.zBtn.setEnabled(enabled);
//		this.saveBtn.setEnabled(enabled);
//		this.zoomBtn.setEnabled(enabled);
//		// Set state of scrollbars
//		this.getHorizontalScrollBar().getModel().setExtent(
//				(enabled) ? this.visColCount : Integer.MAX_VALUE);
//		this.getPrimaryVerticalScrollbar().getModel().setExtent(
//				(enabled) ? 1 : Integer.MAX_VALUE);
//		this.getSecondaryVerticalScrollBar().getModel().setExtent(
//				(enabled) ? this.visRowCount : Integer.MAX_VALUE);
//		this.getHorizontalScrollBar().revalidate();
//		this.getPrimaryVerticalScrollbar().revalidate();
//		this.getSecondaryVerticalScrollBar().revalidate();
//	};
//	
//	/**
//	 * Convenience class for a popup button control for use in the heat map component.
//	 * 
//	 * @author A. Behne
//	 */
//	private class AxisPopupButton extends JToggleButton {
//		
//		/**
//		 * The currently selected value.
//		 */
//		private Object value;
//
//		/**
//		 * Creates an axis popup button with the specified default label,
//		 * rotation angle, popup item values and resolution flag.
//		 * @param label the default label
//		 * @param angle the rotation angle
//		 * @param values the values to use inside the associated popup
//		 */
//		public AxisPopupButton(final Axis axis, Object[][] values, Object initValue) {
//			super();
//			
//			// Cache initial value
//			this.value = initValue;
//			
//			// Configure button properties
//			this.setLayout(new BorderLayout());
//			this.setBackground(new Color(245, 245, 245));
//			
//			// Create text label to be placed on button
//			JLabel buttonLbl = new JLabel(initValue.toString());
//			buttonLbl.setHorizontalAlignment(SwingConstants.CENTER);
//			// Rotate and align text label depending on axis
//			// TODO: check insets on Windows machine
//			if (axis == Axis.Y_AXIS) {
//				buttonLbl.setUI(new VerticalLabelUI(false));
//				this.setMargin(new Insets(3, 1, 5, 1));
//			} else if (axis == Axis.Z_AXIS) {
//				buttonLbl.setUI(new VerticalLabelUI(true));
//				this.setMargin(new Insets(5, 1, 3, 1));
//			} else {
//				this.setMargin(new Insets(1, 4, 1, 4));
//			}
//			buttonLbl.setFont(this.getFont().deriveFont(Font.BOLD));
//			this.add(buttonLbl, BorderLayout.CENTER);
//			
//			// Create dynamic cascading popup adding items derived from the provided objects
//			final JPopupMenu pop = new JPopupMenu();
//			ButtonGroup grp = new ButtonGroup();
//			for (Object[] group : values) {
//				JComponent subMenu = pop;
//				// Identify sub-menu type
//				if (values.length > 1) {
//					if (group[0] instanceof OntologyChartType) {
//						subMenu = new JMenu("Ontology");
//					} else if (group[0] instanceof TaxonomyChartType) {
//						subMenu = new JMenu("Taxonomy");
//					} else if (group[0] instanceof HierarchyLevel) {
//						subMenu = new JMenu("Hierarchy");
//					}
//				}
//				// Generate sub-menu items
//				for (final Object obj : group) {
//					JMenuItem item = new JRadioButtonMenuItem(obj.toString());
//					item.setSelected(obj.equals(initValue));
//					item.addActionListener(new ActionListener() {
//						@Override
//						public void actionPerformed(ActionEvent evt) {
//							// Update button label and refresh heat map
//							HeatMapPane.this.setAxisButtonValue(axis, obj);
//						}
//					});
//					subMenu.add(item);
//					grp.add(item);
//				}
//				// Add sub-menu to main menu
//				if (subMenu != pop) {
//					pop.add(subMenu);
//				}
//			}
//			
//			// Synchronize popup visibility with button selection state
//			pop.addPopupMenuListener(new PopupMenuListener() {
//				@Override
//				public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
//				@Override
//				public void popupMenuCanceled(PopupMenuEvent e) { }
//				@Override
//				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//					AxisPopupButton.this.setSelected(false);
//				}
//			});
//			
//			// Install action listener to show popup on click
//			this.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					JComponent src = (JComponent) e.getSource();
//					pop.show(src, 0, src.getHeight());
//				}
//			});
//			
//		}
//		
//		/**
//		 * Returns the currently selected value.
//		 * @return the selected value
//		 */
//		public Object getValue() {
//			return this.value;
//		}
//		
//		/**
//		 * Sets the currently selected value.
//		 * @param value the value to set
//		 */
//		public void setValue(Object value) {
//			this.value = value;
//			((JLabel) this.getComponent(0)).setText(value.toString());
//		}
//		
//		@Override
//		public void setEnabled(boolean enabled) {
//			super.setEnabled(enabled);
//			this.getComponent(0).setEnabled(enabled);
//		}
//		
//	}
//	
//	/**
//	 * Custom paint scale implementation providing a continuous rainbow-like color gradient.
//	 * 
//	 * @author A. Behne
//	 */
//	private class RainbowPaintScale implements PaintScale {
//		
//		/**
//		 * The lower value boundary of the scale.
//		 */
//		private double lowerBound;
//		
//		/**
//		 * The upper value boundary of the scale.
//		 */
//		private double upperBound;
//		
//		/**
//		 * The opacity of the scale.
//		 */
//		private float alpha;
//		
//		/**
//		 * The background color of the scale.
//		 */
//		private Color background;
//		
//		/**
//		 * Creates a rainbow paint scale with the specified upper and lower
//		 * range boundaries and colors with 50% opacity blended into a white
//		 * background.
//		 * @param lowerBound the lower value boundary of the scale
//		 * @param upperBound the upper value boundary of the scale
//		 */
//		public RainbowPaintScale(double lowerBound, double upperBound) {
//			this(lowerBound, upperBound, 0.5f, Color.WHITE);
//		}
//		
//		/**
//		 * Creates a rainbow paint scale with the specified upper and lower
//		 * range boundaries as well as the specified opacity and background color.
//		 * @param lowerBound the lower value boundary of the scale
//		 * @param upperBound the upper value boundary of the scale
//		 * @param alpha the opacity of the scale
//		 * @param background the background color of the scale
//		 */
//		public RainbowPaintScale(double lowerBound, double upperBound,
//				float alpha, Color background) {
//			this.lowerBound = lowerBound;
//			this.upperBound = upperBound;
//			if (alpha < 0.0f) {
//				alpha = 0.0f;
//			} else if (alpha > 1.0f) {
//				alpha = 1.0f;
//			}
//			this.alpha = alpha;
//			if (background == null) {
//				background = Color.WHITE;
//			}
//			this.background = background;
//		}
//
//		@Override
//		public double getLowerBound() {
//			return lowerBound;
//		}
//
//		@Override
//		public double getUpperBound() {
//			return upperBound;
//		}
//		
//		/**
//		 * Sets the upper bound for the scale.
//		 * @param the upper bound to set
//		 */
//		public void setUpperBound(double upperBound) {
//			this.upperBound = upperBound;
//		}
//
//		@Override
//		public Paint getPaint(double value) {
//			// take lower/upper bounds into account
//			value = Math.round(value) / (upperBound - lowerBound) + lowerBound;
//			if (value > 1.0) {
//				value = 1.0;
//			}
//			float r = (float) (-Math.abs(value - 0.75) * 4.0 + 1.5);
//			float g = (float) (-Math.abs(value - 0.50) * 4.0 + 1.5);
//			float b = (float) (-Math.abs(value - 0.25) * 4.0 + 1.5);
//			r = (r > 1.0f) ? 1.0f : (r < 0.0f) ? 0.0f : r;
//			g = (g > 1.0f) ? 1.0f : (g < 0.0f) ? 0.0f : g;
//			b = (b > 1.0f) ? 1.0f : (b < 0.0f) ? 0.0f : b;
//			float[] bg = background.getColorComponents(null);
//			r = r * alpha + bg[0] * (1.0f - alpha);
//			g = g * alpha + bg[1] * (1.0f - alpha);
//			b = b * alpha + bg[2] * (1.0f - alpha);
//			
//			Color col = new Color(r, g, b);
//			return col;
//		}
//		
//	}
//	
//	/**
//	 * UI class for vertical labels.<br>
//	 * @see <a href="http://www.codeguru.com/java/articles/199.shtml">http://www.codeguru.com/java/articles/199.shtml</a>
//	 */
//	public class VerticalLabelUI extends BasicLabelUI {
//
//		protected boolean clockwise;
//
//		private Rectangle paintIconR = new Rectangle();
//		private Rectangle paintTextR = new Rectangle();
//		private Rectangle paintViewR = new Rectangle();
//		private Insets paintViewInsets = new Insets(0, 0, 0, 0);
//
//		public VerticalLabelUI( boolean clockwise ){
//			super();
//			this.clockwise = clockwise;
//		}
//
//		public Dimension getPreferredSize(JComponent c){
//			Dimension dim = super.getPreferredSize(c);
//			return new Dimension( dim.height, dim.width );
//		}
//
//		public void paint(Graphics g, JComponent c) {
//			JLabel label = (JLabel) c;
//			String text = label.getText();
//			Icon icon = (label.isEnabled()) ? label.getIcon() : label
//					.getDisabledIcon();
//
//			if ((icon == null) && (text == null)) {
//				return;
//			}
//
//			FontMetrics fm = g.getFontMetrics();
//			paintViewInsets = c.getInsets(paintViewInsets);
//
//			paintViewR.x = paintViewInsets.left;
//			paintViewR.y = paintViewInsets.top;
//
//			// Use inverted height & width
//			paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
//			paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
//
//			paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
//			paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
//
//			String clippedText = layoutCL(label, fm, text, icon, paintViewR,
//					paintIconR, paintTextR);
//
//			Graphics2D g2 = (Graphics2D) g;
//			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//			AffineTransform tr = g2.getTransform();
//			if (clockwise) {
//				g2.rotate(Math.PI / 2);
//				g2.translate(0, -c.getWidth());
//			} else {
//				g2.rotate(-Math.PI / 2);
//				g2.translate(-c.getHeight(), 0);
//			}
//
//			if (icon != null) {
//				icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
//			}
//
//			if (text != null) {
//				int textX = paintTextR.x;
//				int textY = paintTextR.y + fm.getAscent();
//
//				if (label.isEnabled()) {
//					paintEnabledText(label, g, clippedText, textX, textY);
//				} else {
//					paintDisabledText(label, g, clippedText, textX, textY);
//				}
//			}
//
//			g2.setTransform(tr);
//		}
//	}
//	
//	/**
//	 * Convenience extension of the SymbolAxis class to allow dynamic truncation
//	 * of axis tick labels.
//	 */
//	public static class SymbolAxisExt extends SymbolAxis {
//		
//		/**
//		 * Maximum pixel length of label strings.
//		 */
//		private int maxLabelSize = 60;
//		
//		/**
//		 * The font metrics used for measuring axis label sizes.
//		 */
//		private FontMetrics fm;
//
//		/**
//		 * Constructs a symbol axis, using default attribute values where necessary.
//		 * @param label the axis label (<code>null</code> permitted)
//		 * @param sv the list of symbols to display instead of the numeric values
//		 * @param fm the font metrics used for measuring axis label sizes
//		 */
//		public SymbolAxisExt(String label, String[] sv, FontMetrics fm) {
//			super(label, sv);
//			this.fm = fm;
//		}
//		
//		/**
//		 * {@inheritDoc}<p>
//		 * Overriden to take into account for all tick labels, not only the
//		 * currently visible ones.
//		 */
//		@Override
//		protected double findMaximumTickLabelHeight(List ticks,
//				Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
//			if (vertical && (this.maxLabelSize > 0)) {
//				List<NumberTick> newTicks = new ArrayList<NumberTick>();
//				for (String str : this.getSymbols()) {
//					newTicks.add(new NumberTick(0.0, trimToSize(str, 60), 
//							TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -Math.PI / 2.0));
//				}
//				return super.findMaximumTickLabelHeight(newTicks, g2, drawArea, vertical);
//			}
//			return super.findMaximumTickLabelHeight(ticks, g2, drawArea, vertical);
//		}
//		
//
//		/**
//		 * {@inheritDoc}<p>
//		 * Overridden to take into account for all tick labels, not only the
//		 * currently visible ones.
//		 */
//		@Override
//		protected double findMaximumTickLabelWidth(List ticks, Graphics2D g2,
//				Rectangle2D drawArea, boolean vertical) {
//			if (!vertical && (this.maxLabelSize > 0)) {
//				List<NumberTick> newTicks = new ArrayList<NumberTick>();
//				for (String str : this.getSymbols()) {
//					newTicks.add(new NumberTick(0.0, trimToSize(str, 60), 
//							TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -Math.PI / 2.0));
//				}
//				return super.findMaximumTickLabelWidth(newTicks, g2, drawArea, vertical);
//			}
//			return super.findMaximumTickLabelWidth(ticks, g2, drawArea, vertical);
//		}
//
//		/**
//		 * {@inheritDoc}<p>
//		 * Overridden to shorten overly long tick labels. 
//		 */
//		@Override
//		public String valueToString(double value) {
//			if (this.maxLabelSize > 0) {
//				return trimToSize(super.valueToString(value), 60);
//			} else {
//				return super.valueToString(value);
//			}
//		}
//
//		/**
//		 * Convenience method to trim a specified string down to the specified size
//		 * by removing characters from its end and adding an ellipsis instead.
//		 * @param str the target string
//		 * @param size the desired pixel width
//		 * @return a trimmed string or the original string if it is smaller than the specified size
//		 */
//		private String trimToSize(String str, int size) {
//			if (this.fm.stringWidth(str) > size) {
//				// delete characters from end, add ellipsis (...) instead
//				StringBuilder sb = new StringBuilder(str.trim() + "\u2026");
//				while (this.fm.stringWidth(sb.toString()) > size) {
//					sb.deleteCharAt(sb.length() - 2);
//				}
//				return sb.toString().trim();
//			}
//			return str;
//		}
//
//		/**
//		 * {@inheritDoc}<p>
//		 * Overridden to ignore maximum tick count of 500.
//		 */
//		@Override
//	    protected List refreshTicksHorizontal(Graphics2D g2,
//	                                          Rectangle2D dataArea,
//	                                          RectangleEdge edge) {
//
//	        List<Tick> ticks = new ArrayList<Tick>();
//
//	        Font tickLabelFont = getTickLabelFont();
//	        g2.setFont(tickLabelFont);
//
//	        double size = getTickUnit().getSize();
//	        int count = calculateVisibleTickCount();
//	        double lowestTickValue = calculateLowestVisibleTickValue();
//
//	        double previousDrawnTickLabelPos = 0.0;
//	        double previousDrawnTickLabelLength = 0.0;
//
//            for (int i = 0; i < count; i++) {
//                double currentTickValue = lowestTickValue + (i * size);
//                double xx = valueToJava2D(currentTickValue, dataArea, edge);
//                String tickLabel;
//                NumberFormat formatter = getNumberFormatOverride();
//                if (formatter != null) {
//                    tickLabel = formatter.format(currentTickValue);
//                }
//                else {
//                    tickLabel = valueToString(currentTickValue);
//                }
//
//                // avoid to draw overlapping tick labels
//                Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2,
//                        g2.getFontMetrics());
//                double tickLabelLength = isVerticalTickLabels()
//                        ? bounds.getHeight() : bounds.getWidth();
//                boolean tickLabelsOverlapping = false;
//                if (i > 0) {
//                    double avgTickLabelLength = (previousDrawnTickLabelLength
//                            + tickLabelLength) / 2.0;
//                    if (Math.abs(xx - previousDrawnTickLabelPos)
//                            < avgTickLabelLength) {
//                        tickLabelsOverlapping = true;
//                    }
//                }
//                if (tickLabelsOverlapping) {
//                    tickLabel = ""; // don't draw this tick label
//                }
//                else {
//                    // remember these values for next comparison
//                    previousDrawnTickLabelPos = xx;
//                    previousDrawnTickLabelLength = tickLabelLength;
//                }
//
//                TextAnchor anchor = null;
//                TextAnchor rotationAnchor = null;
//                double angle = 0.0;
//                if (isVerticalTickLabels()) {
//                    anchor = TextAnchor.CENTER_RIGHT;
//                    rotationAnchor = TextAnchor.CENTER_RIGHT;
//                    if (edge == RectangleEdge.TOP) {
//                        angle = Math.PI / 2.0;
//                    }
//                    else {
//                        angle = -Math.PI / 2.0;
//                    }
//                }
//                else {
//                    if (edge == RectangleEdge.TOP) {
//                        anchor = TextAnchor.BOTTOM_CENTER;
//                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
//                    }
//                    else {
//                        anchor = TextAnchor.TOP_CENTER;
//                        rotationAnchor = TextAnchor.TOP_CENTER;
//                    }
//                }
//                Tick tick = new NumberTick(new Double(currentTickValue),
//                        tickLabel, anchor, rotationAnchor, angle);
//                ticks.add(tick);
//            }
//	        return ticks;
//
//	    }
//
//		/**
//		 * {@inheritDoc}<p>
//		 * Overridden to ignore maximum tick count of 500.
//		 */
//	    @Override
//	    protected List refreshTicksVertical(Graphics2D g2,
//	                                        Rectangle2D dataArea,
//	                                        RectangleEdge edge) {
//
//	        List<Tick> ticks = new ArrayList<Tick>();
//
//	        Font tickLabelFont = getTickLabelFont();
//	        g2.setFont(tickLabelFont);
//
//	        double size = getTickUnit().getSize();
//	        int count = calculateVisibleTickCount();
//	        double lowestTickValue = calculateLowestVisibleTickValue();
//
//	        double previousDrawnTickLabelPos = 0.0;
//	        double previousDrawnTickLabelLength = 0.0;
//
//	        for (int i = 0; i < count; i++) {
//	        	double currentTickValue = lowestTickValue + (i * size);
//	        	double yy = valueToJava2D(currentTickValue, dataArea, edge);
//	        	String tickLabel;
//	        	NumberFormat formatter = getNumberFormatOverride();
//	        	if (formatter != null) {
//	        		tickLabel = formatter.format(currentTickValue);
//	        	}
//	        	else {
//	        		tickLabel = valueToString(currentTickValue);
//	        	}
//
//	        	// avoid to draw overlapping tick labels
//	        	Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2,
//	        			g2.getFontMetrics());
//	        	double tickLabelLength = isVerticalTickLabels()
//	        			? bounds.getWidth() : bounds.getHeight();
//	        			boolean tickLabelsOverlapping = false;
//	        			if (i > 0) {
//	        				double avgTickLabelLength = (previousDrawnTickLabelLength
//	        						+ tickLabelLength) / 2.0;
//	        				if (Math.abs(yy - previousDrawnTickLabelPos)
//	        						< avgTickLabelLength) {
//	        					tickLabelsOverlapping = true;
//	        				}
//	        			}
//	        			if (tickLabelsOverlapping) {
//	        				tickLabel = ""; // don't draw this tick label
//	        			}
//	        			else {
//	        				// remember these values for next comparison
//	        				previousDrawnTickLabelPos = yy;
//	        				previousDrawnTickLabelLength = tickLabelLength;
//	        			}
//
//	        			TextAnchor anchor = null;
//	        			TextAnchor rotationAnchor = null;
//	        			double angle = 0.0;
//	        			if (isVerticalTickLabels()) {
//	                    anchor = TextAnchor.BOTTOM_CENTER;
//	                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
//	                    if (edge == RectangleEdge.LEFT) {
//	                        angle = -Math.PI / 2.0;
//	                    }
//	                    else {
//	                        angle = Math.PI / 2.0;
//	                    }
//	                }
//	                else {
//	                    if (edge == RectangleEdge.LEFT) {
//	                        anchor = TextAnchor.CENTER_RIGHT;
//	                        rotationAnchor = TextAnchor.CENTER_RIGHT;
//	                    }
//	                    else {
//	                        anchor = TextAnchor.CENTER_LEFT;
//	                        rotationAnchor = TextAnchor.CENTER_LEFT;
//	                    }
//	                }
//	                Tick tick = new NumberTick(new Double(currentTickValue),
//	                        tickLabel, anchor, rotationAnchor, angle);
//	                ticks.add(tick);
//	            }
//	        return ticks;
//
//	    }
//
//		/**
//		 * Returns the maximum pixel length of this axis' tick labels.
//		 * @return the maximum tick label size
//		 */
//		public int getMaximumTickLabelSize() {
//			return maxLabelSize;
//		}
//
//		/**
//		 * Sets the maximum pixel length of this axis' tick labels.
//		 * @param maxLabelSize the tick label length to set
//		 */
//		public void setMaximumTickLabelSize(int maxLabelSize) {
//			this.maxLabelSize = maxLabelSize;
//		}
//		
//	}
//	
//	/**
//	 * Worker implementation for updating heat map contents based on a search
//	 * result object.
//	 * 
//	 * @author A. Behne
//	 */
//	private class UpdateWorker extends SwingWorker<Object, Object> {
//		
//		/**
//		 * The result object reference.
//		 */
//		private DbSearchResult result;
//
//		/**
//		 * Constructs a SwingWorker for updating the heat map contents based on
//		 * the provided search result object.
//		 * @param result the search result container object.
//		 */
//		public UpdateWorker(DbSearchResult result) {
//			this.result = result;
//		}
//
//		@Override
//		protected Object doInBackground() {
//			Thread.currentThread().setName("UpdateHeatMapThread");
//			
//			HeatMapPane heatMap = HeatMapPane.this;
//			heatMap.setBusy(true);
//			
//			// Refresh data container contents
//			if (result != null) {
//				heatMap.data.setResult(result);
//			}
//			heatMap.data.setAxisTypes(
//					(ChartType) heatMap.getAxisButtonValue(Axis.X_AXIS),
//					(ChartType) heatMap.getAxisButtonValue(Axis.Y_AXIS),
//					(HierarchyLevel) heatMap.getAxisButtonValue(Axis.Z_AXIS));
//			
//			// Get existing chart instances
//			ChartPanel chartPnl = (ChartPanel) heatMap.getViewport().getView();
//			JFreeChart oldChart = chartPnl.getChart();
//			
//			// Re-create plot
//			XYPlot newPlot = heatMap.createPlot(
//					heatMap.xBtn.getValue().toString(),
//					heatMap.yBtn.getValue().toString(),
//					heatMap.data.getXLabels(), heatMap.data.getYLabels(),
//					0.0, heatMap.data.getMaximum(), heatMap.data.getMatrix());
//			
//			double max = data.getMaximum();
//			
//			// Re-create chart
//			JFreeChart newChart = new JFreeChart(oldChart.getTitle().getText(), newPlot);
//			newChart.removeLegend();
//			PaintScaleLegend psl = heatMap.createLegend(
//					heatMap.zBtn.getValue().toString(), newPlot);
//			psl.getAxis().setDefaultAutoRange(new Range(-0.5, max + 0.5));
//			psl.getAxis().setRange(-0.5, max + 0.5);
//			newChart.addSubtitle(psl);
//			newChart.setBackgroundPaint(Color.WHITE);
//			
//			// Apply new chart
//			chartPnl.setChart(newChart);
//			
//			// Adjust view
//			MatrixSeries series = data.getMatrix();
//			heatMap.colCount = series.getColumnsCount();
//			heatMap.firePropertyChange("colCount", -1, series.getColumnsCount());
//			heatMap.rowCount = series.getRowCount();
//			heatMap.firePropertyChange("rowCount", -1, series.getRowCount());
//
//			heatMap.setVisibleColumnCount(heatMap.visColCount);
//			heatMap.setVisibleRowCount(heatMap.visRowCount);
//			
//			// TODO: maybe add 'preferred visible row/column count' or 'preferred row/column size'
//			
//			JScrollBar vertBar = heatMap.getPrimaryVerticalScrollbar();
//			vertBar.setValues(0, 1, 0, (int) max);
//			vertBar.setBlockIncrement((int) (max / 4.0));
//			vertBar.revalidate();
//			
//			return null;
//		}
//		
//		@Override
//		protected void done() {
//			// Refresh chart controls
//			HeatMapPane.this.updateChartLayout();
//			
//			HeatMapPane.this.setBusy(false);
//		}
//		
//	}
//	
//}
//
