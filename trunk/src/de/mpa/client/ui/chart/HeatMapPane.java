package de.mpa.client.ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.Title;
import org.jfree.data.Range;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.MatrixSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;

/**
 * Scrollable heat map chart implementation.
 * 
 * @author A. Behne
 */
public class HeatMapPane extends JScrollPane {
	
	public enum Axis {
		X_AXIS,
		Y_AXIS,
		Z_AXIS
	}
	
	/**
	 * The amount of data rows in the dataset.
	 */
	private int rowCount;
	
	/**
	 * The amount of data columns in the dataset.
	 */
	private int colCount;

	/**
	 * The preferred number of visible data rows.
	 */
	private int visRowCount;
	
	/**
	 * The preferred number of visible data columns.
	 */
	private int visColCount;

	/**
	 * The secondary vertical scroll bar.
	 */
	private JScrollBar secVertBar;

	/**
	 * The button for choosing the type of x axis.
	 */
	private AbstractButton xBtn;

	/**
	 * The button for choosing the type of y axis.
	 */
	private AbstractButton yBtn;

	/**
	 * The button for choosing the type of z axis.
	 */
	private AbstractButton zBtn;
	
	/**
	 * Creates a scrollable heat map chart from the specified data container.
	 * @param data the data container object
	 */
	public HeatMapPane(HeatMapData data) {
		this("Heat Map", "x axis", "y axis", "z axis",
				data.getXLabels(), data.getYLabels(), 
				0.0, data.getMaximum(),
				data.getMatrix());
	}
	
	/**
	 * Creates a scrollable heat map chart displaying the specified title, axis
	 * tick labels and data values.
	 * @param title the title to be displayed above the plot
	 * @param xLabels the tick labels for the x axis 
	 * @param yLabels the tick labels for the y axis
	 * @param matrix the matrix of values to be displayed as colored blocks
	 */
	public HeatMapPane(String title, String xTitle, String yTitle, String zTitle, 
			String[] xLabels, String[] yLabels, double lowerBound, double upperBound, MatrixSeries matrix) {
		
		// create plots and embed them in scroll pane
		super(null, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		XYPlot plot = this.createPlot(xTitle, yTitle, xLabels, yLabels, lowerBound, upperBound, matrix);
		
		PaintScaleLegend psl = this.createLegend(zTitle, plot);
		
		ChartPanel chartPnl = this.createChart(title, psl, plot);
		this.setViewportView(chartPnl);
		
		// cache data dimensions
		this.rowCount = yLabels.length;
		this.colCount = xLabels.length;
		
		// modify scroll pane's scrolling behavior by removing default listeners
		// and installing new ones
		for (ChangeListener cl : this.getViewport().getChangeListeners()) {
			this.getViewport().removeChangeListener(cl);
		}
		
		// modify vertical scroll bar behavior
		JScrollBar vertBar = this.getVerticalScrollBar(true);
		vertBar.setValues(0, 1, 0, 100);
		vertBar.setBlockIncrement(25);
		DefaultBoundedRangeModel vertBarMdl = (DefaultBoundedRangeModel) vertBar.getModel();
		ChangeListener[] vcl = vertBarMdl.getChangeListeners();
		vertBarMdl.removeChangeListener(vcl[0]);
		
		vertBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				JScrollBar src = (JScrollBar) e.getSource();
				int val = src.getMaximum() - e.getValue();
				ChartPanel chartPnl = (ChartPanel) getViewport().getView();
				PaintScaleLegend psl = (PaintScaleLegend) chartPnl.getChart().getSubtitle(0);
				((RainbowPaintScale) psl.getScale()).setUpperBound(val);
				psl.getAxis().setRange(0.0, val);
			}
		});
		vertBar.putClientProperty("JScrollBar.isFreeStanding", false);
		
		// modify horizontal scroll bar behavior
		final JScrollBar horzBar = this.getHorizontalScrollBar();
		horzBar.setValues(0, colCount, 0, colCount);
		horzBar.setBlockIncrement(colCount / 4);
		DefaultBoundedRangeModel horzBarMdl = (DefaultBoundedRangeModel) horzBar.getModel();
		ChangeListener[] hcl = horzBarMdl.getChangeListeners();
		horzBarMdl.removeChangeListener(hcl[0]);
		
		horzBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int val = e.getValue();
				ChartPanel chartPnl = (ChartPanel) getViewport().getView();
				XYPlot plot = chartPnl.getChart().getXYPlot();
				ValueAxis xAxis = plot.getDomainAxis();
//				xAxis.setRange(range);
				xAxis.setRange(val - 0.5, val + horzBar.getModel().getExtent() - 0.5);
				xAxis.setLowerBound(val - 0.5);
				xAxis.setUpperBound(val + horzBar.getModel().getExtent() - 0.5);
			}
		});
		horzBar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIManager.getColor("ScrollBar.thumbDarkShadow")));
		
		secVertBar = new JScrollBar(JScrollBar.VERTICAL, 0, rowCount, 0, rowCount);
		secVertBar.putClientProperty("JScrollBar.isFreeStanding", false);
		secVertBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		secVertBar.getComponent(0).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		secVertBar.getComponent(1).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		secVertBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, -1));
		Dimension tmp = secVertBar.getPreferredSize();
		tmp.width = 15;
		secVertBar.setPreferredSize(tmp);

		secVertBar.setBlockIncrement(rowCount / 4);
		
		secVertBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int val = e.getValue();
				ChartPanel chartPnl = (ChartPanel) getViewport().getView();
				XYPlot plot = chartPnl.getChart().getXYPlot();
				ValueAxis yAxis = plot.getRangeAxis();
				yAxis.setRange(val - 0.5, val + secVertBar.getModel().getExtent() - 0.5);
			}
		});
		
		this.setRowHeaderView(secVertBar);
		
	}

	/**
	 * Sets the plot instance displayed inside the heat map view.
	 * @param barTitle
	 * @param plot
	 */
	public void updateData(HeatMapData data) {
		
		// get old chart and plot instances
		ChartPanel chartPnl = (ChartPanel) this.getViewport().getView();
		JFreeChart oldChart = chartPnl.getChart();
		XYPlot oldPlot = (XYPlot) oldChart.getPlot();

		// create new plot
		XYPlot newPlot = this.createPlot(
				oldPlot.getDomainAxis().getLabel(),
				oldPlot.getRangeAxis().getLabel(),
				data.getXLabels(), data.getYLabels(),
				0.0, data.getMaximum(), data.getMatrix());
		
		// create new chart
		JFreeChart newChart = new JFreeChart(oldChart.getTitle().getText(), newPlot);
		newChart.removeLegend();
		PaintScaleLegend psl = this.createLegend(
				((PaintScaleLegend) oldChart.getSubtitle(0)).getAxis().getLabel(), newPlot);
		newChart.addSubtitle(psl);
		newChart.setBackgroundPaint(Color.WHITE);
		
		// apply new chart
		chartPnl.setChart(newChart);
		
		// adjust view
		MatrixSeries series = data.getMatrix();
		this.colCount = series.getColumnsCount();
		this.rowCount = series.getRowCount();

		this.setVisibleColumnCount(this.visColCount);
		this.setVisibleRowCount(this.visRowCount);
		
		JScrollBar vertBar = this.getVerticalScrollBar(true);
		double max = data.getMaximum();
		vertBar.setValues(0, 1, 0, (int) max);
		vertBar.setBlockIncrement((int) (max / 4.0));
		vertBar.revalidate();
		psl.getAxis().setDefaultAutoRange(new Range(0.0, max));

		this.updateChartLayout(chartPnl);
	}
	
	/**
	 * Convenience method to refresh the layout positions of the axis buttons
	 * and color legend.
	 * @param chartPnl the chart panel
	 * @param chart the chart
	 */
	private void updateChartLayout(ChartPanel chartPnl) {
		JFreeChart chart = chartPnl.getChart();
		
		// draw chart to image to get at rendering info object
		BufferedImage img = new BufferedImage(chartPnl.getWidth(), chartPnl.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Rectangle bounds = chartPnl.getBounds();
		chart.draw(img.createGraphics(), bounds, null, chartPnl.getChartRenderingInfo());
		Rectangle2D dataBounds = chartPnl.getChartRenderingInfo().getPlotInfo().getDataArea().getBounds2D();

		// adjust layout of chart panel
		FormLayout layout = (FormLayout) chartPnl.getLayout();
		layout.setColumnSpec(2, ColumnSpec.decode(
				"l:" + ((int) dataBounds.getX() - 3) + "px"));
		layout.setColumnSpec(4, ColumnSpec.decode(
				"r:" + (int) (bounds.getWidth() - dataBounds.getWidth() - dataBounds.getX() - 2.0) + "px")); 
		layout.setRowSpec(3, RowSpec.decode(
				"b:" + (int) (bounds.getHeight() - dataBounds.getHeight() - dataBounds.getY() - 2.0) + "px"));
		
		PaintScaleLegend legend = (PaintScaleLegend) chart.getSubtitle(0);
		double btm = bounds.getHeight() - dataBounds.getHeight() - dataBounds.getY() - 1.0;
		legend.setMargin(4.0, 8.0, btm, 9.0);
		
		chartPnl.revalidate();
		chartPnl.setRefreshBuffer(true);
		chartPnl.repaint();

	}
	
	/**
	 * Creates a chart panel from the specified plot and title.
	 * @param title the title to be displayed above the plot
	 * @param subtitle 
	 * @param plot the plot to be displayed in the heat map view
	 * @return the chart panel
	 */
	private ChartPanel createChart(String title, Title subtitle, XYPlot plot) {
		
		// create chart, add color bar next to it
		JFreeChart chart = new JFreeChart(title, plot);
		chart.removeLegend();
		chart.addSubtitle(subtitle);
		chart.setBackgroundPaint(Color.WHITE);
		
		// wrap chart in panel
		ChartPanel chartPnl = new ChartPanel(chart) {
			/**
			 * The decimal formatter.
			 */
			Format formatter = new DecimalFormat("0");
			
			/**
			 * {@inheritDoc}<p>
			 * Overridden to show axis labels or matrix values below the mouse cursor.
			 */
			@Override
			public String getToolTipText(MouseEvent evt) {
				Point2D p = evt.getPoint();
				Rectangle2D plotArea = getScreenDataArea();
				XYPlot plot = (XYPlot) getChart().getPlot();
				int chartX = (int) Math.round(plot.getDomainAxis().java2DToValue(
						p.getX(), plotArea, plot.getDomainAxisEdge()));
				int chartY = (int) Math.round(plot.getRangeAxis().java2DToValue(
						p.getY(), plotArea, plot.getRangeAxisEdge()));
				Range rangeX = plot.getDomainAxis().getRange();
				Range rangeY = plot.getRangeAxis().getRange();
				if (rangeX.contains(chartX) && rangeY.contains(chartY)) {
					MatrixSeriesCollection dataset = (MatrixSeriesCollection) plot.getDataset();
					double chartZ = dataset.getSeries(0).get(chartY, chartX);
					return formatter.format(chartZ);
//					return Double.toString(chartZ);
				} else {
					if (rangeY.contains(chartY) && (chartX < rangeX.getLowerBound())) {
						return ((SymbolAxis) plot.getRangeAxis()).getSymbols()[chartY];
					} else if (rangeX.contains(chartX) && (chartY > rangeY.getUpperBound())) {
						return ((SymbolAxis) plot.getDomainAxis()).getSymbols()[chartX];
					}
				}
				return null;
			}
			
			@Override
			public Point getToolTipLocation(MouseEvent evt) {
				if (getToolTipText(evt) != null) {
					Point point = evt.getPoint();
					point.translate(0, 20);
					return point;
				}
				return null;
			}
		};
		chartPnl.setPreferredSize(new Dimension());
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		
		// remove default context menu
		chartPnl.removeMouseListener(chartPnl.getMouseListeners()[1]);
		
		// Setup defaul layout
		FormLayout layout = new FormLayout("3px, l:43px, c:0px:g, r:74px, 2px", "27px, c:0px:g, b:47px, 2px");
		chartPnl.setLayout(layout);
		
		Object[][] xyValues = new Object[][] { TaxonomyChartType.values(), OntologyChartType.values(), HierarchyLevel.values() };
		Object[][] zValues = new Object[][] { HierarchyLevel.values() };
		
		xBtn = createPopupButton("Choose X Axis", 0.0, xyValues);
		yBtn = createPopupButton("Choose Y Axis", -Math.PI / 2.0, xyValues);
		zBtn = createPopupButton("Choose Z Axis", Math.PI / 2.0, zValues );
		
		chartPnl.add(xBtn, CC.xy(3, 3));
		chartPnl.add(yBtn, CC.xy(2, 2));
		chartPnl.add(zBtn, CC.xy(4, 2));
		
		return chartPnl;
	}

	/**
	 * Creates a color bar legend for the specified plot and displaying the specified title string
	 * @param label the legend label
	 * @param plot the plot
	 * @return a color bar legend
	 */
	private PaintScaleLegend createLegend(String label, XYPlot plot) {
		
		// create color bar
		PaintScale scale = ((XYBlockRenderer) plot.getRenderer()).getPaintScale();
		
		NumberAxis scaleAxis = new NumberAxis(label) {
			@Override
			protected double findMaximumTickLabelWidth(List ticks,
					Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
				RectangleInsets insets = this.getTickLabelInsets();
		        Font font = this.getTickLabelFont();
		        FontMetrics fm = g2.getFontMetrics(font);
		        
		        double upperBound = this.getDefaultAutoRange().getUpperBound();
		        NumberTickUnit ntu = (NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(upperBound);
				Tick tick = new NumberTick(upperBound, ntu.valueToString(upperBound),
						TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 0.0);
				
		        Rectangle2D labelBounds = TextUtilities.getTextBounds(
		        		tick.getText(), g2, fm);
		       
		        return labelBounds.getWidth() + insets.getLeft() + insets.getRight();
			}
		};
		scaleAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		scaleAxis.setDefaultAutoRange(new Range(0.0, 100.0));
		
		PaintScaleLegend psl = new PaintScaleLegend(scale, scaleAxis);
		psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
		psl.setMargin(4.0, 8.0, 48.0, 9.0);
		psl.setPosition(RectangleEdge.RIGHT);
		
		return psl;
	}

	/**
	 * Creates a heat map plot using the specified axis tick labels as well as
	 * data values and applies the default blue-green-red color scale renderer
	 * to it.
	 * @param xTitle 
	 * @param yTitle 
	 * @param xLabels the tick labels of the x axis
	 * @param yLabels the tick labels of the y axis
	 * @param data the matrix of values to be displayed as colored blocks
	 * @return the heat map plot
	 */
	private XYPlot createPlot(String xTitle, String yTitle, String[] xLabels, String[] yLabels, 
			double lowerBound, double upperBound, MatrixSeries data) {
		return createPlot(xTitle, yTitle, xLabels, yLabels, data, createRenderer(lowerBound, upperBound));
	}

	/**
	 * Creates a heat map plot using the specified axis tick labels, data values
	 * and renderer.
	 * @param xTitle 
	 * @param yTitle 
	 * @param xLabels the tick labels of the x axis
	 * @param yLabels the tick labels of the y axis
	 * @param data the matrix of values to be displayed as colored blocks
	 * @param renderer the renderer to draw colored blocks
	 * @return the heat map plot
	 */
	private XYPlot createPlot(String xTitle, String yTitle, String[] xLabels, String[] yLabels, 
			MatrixSeries data, XYBlockRenderer renderer) {
	
		// TODO: parameterize maximum width variable
		// create x axis
		SymbolAxis xAxis = new SymbolAxis(xTitle, xLabels) {
			/**
			 * {@inheritDoc}<p>
			 * Overriden to take into account for all tick labels, not only the
			 * currently visible ones.
			 */
			@Override
			protected double findMaximumTickLabelHeight(List ticks,
					Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
				List<NumberTick> newTicks = new ArrayList<NumberTick>();
				for (String str : this.getSymbols()) {
					newTicks.add(new NumberTick(0.0, trimToSize(str, 60, getTickLabelFont()), 
							TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -Math.PI / 2.0));
				}
				return super.findMaximumTickLabelHeight(newTicks, g2, drawArea, vertical);
			}

			/**
			 * {@inheritDoc}<p>
			 * Overridden to shorten overly long tick labels. 
			 */
			@Override
			public String valueToString(double value) {
				return trimToSize(super.valueToString(value), 60, this.getTickLabelFont());
			}
		};
		xAxis.setGridBandsVisible(false);
		xAxis.setVerticalTickLabels(true);
		xAxis.setTickLabelInsets(new RectangleInsets(4, 2, 4, 2));
		
		// create y axis
		SymbolAxis yAxis = new SymbolAxis(yTitle, yLabels) {
			/**
			 * {@inheritDoc}<p>
			 * Overridden to take into account for all tick labels, not only the
			 * currently visible ones.
			 */
			@Override
			protected double findMaximumTickLabelWidth(List ticks, Graphics2D g2,
					Rectangle2D drawArea, boolean vertical) {
				List<NumberTick> newTicks = new ArrayList<NumberTick>();
				for (String str : this.getSymbols()) {
					newTicks.add(new NumberTick(0.0, trimToSize(str, 60, getTickLabelFont()), 
							TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -Math.PI / 2.0));
				}
				return super.findMaximumTickLabelWidth(newTicks, g2, drawArea, vertical);
			}

			/**
			 * {@inheritDoc}<p>
			 * Overridden to shorten overly long tick labels. 
			 */
			@Override
			public String valueToString(double value) {
				return trimToSize(super.valueToString(value), 60, this.getTickLabelFont());
			}
		};
		yAxis.setInverted(true);
		
		// create plot, hide grid lines
		XYPlot plot = new XYPlot(new MatrixSeriesCollection(data),
				xAxis, yAxis, renderer) {
			@Override
			public void draw(Graphics2D g2, Rectangle2D area,
					Point2D anchor, PlotState parentState,
					PlotRenderingInfo info) {
				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				super.draw(g2, area, anchor, parentState, info);
			}
		};
	    plot.setRangeGridlinesVisible(false);
	    plot.setDomainGridlinesVisible(false);
	    plot.setInsets(new RectangleInsets(4.0, 8.0, 8.0, 8.0));
	    
		return plot;
	}
	
	/**
	 * Convenience method to trim a specified string down to the specified size
	 * by removing characters from its end and adding an ellipsis instead.
	 * @param str the target string
	 * @param size the desired pixel width
	 * @param font the font
	 * @return a trimmed string or the original string if it is smaller than the specified size
	 */
	private String trimToSize(String str, int size, Font font) {
		FontMetrics fm = getFontMetrics(font);
		if (fm.stringWidth(str) > size) {
			// delete characters from end, add ellipsis (...) instead
			StringBuilder sb = new StringBuilder(str.trim() + "\u2026");
			while (fm.stringWidth(sb.toString()) > size) {
				sb.deleteCharAt(sb.length() - 2);
			}
			return sb.toString().trim();
		}
		return str;
	}

	/**
	 * Creates a renderer for drawing colored rectangles for values in the range
	 * between the specified lower and upper bounds
	 * @param lowerBound the lower value boundary
	 * @param upperBound the upper value boundary
	 * @return a block renderer for colored gradients
	 */
	private XYBlockRenderer createRenderer(double lowerBound, double upperBound) {
		// init renderer
		XYBlockRenderer renderer = new XYBlockRenderer() {
			// Override draw method to add grey border around item blocks
			@Override
			public void drawItem(Graphics2D g2, XYItemRendererState state,
					Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
					ValueAxis domainAxis, ValueAxis rangeAxis,
					XYDataset dataset, int series, int item,
					CrosshairState crosshairState, int pass) {
				
				double x = dataset.getXValue(series, item);
		        double y = dataset.getYValue(series, item);
		        double z = 0.0;
		        if (dataset instanceof XYZDataset) {
		            z = ((XYZDataset) dataset).getZValue(series, item);
		        }
				Paint p = this.getPaintScale().getPaint(z);
//	            if (item == 0) {
//	            	System.out.println("" + z + "\n" + p);
//	            }
				
				double xx0 = domainAxis.valueToJava2D(x - 0.5, dataArea,
		                plot.getDomainAxisEdge());
		        double yy0 = rangeAxis.valueToJava2D(y - 0.5, dataArea,
		                plot.getRangeAxisEdge());
		        double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth() - 0.5,
		        		dataArea, plot.getDomainAxisEdge());
		        double yy1 = rangeAxis.valueToJava2D(y + this.getBlockHeight() - 0.5,
		        		dataArea, plot.getRangeAxisEdge());
		        Rectangle2D block;
//		        PlotOrientation orientation = plot.getOrientation();
//		        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
//		            block = new Rectangle2D.Double(Math.min(yy0, yy1),
//		                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
//		                    Math.abs(xx0 - xx1));
//		        } else {
		            block = new Rectangle2D.Double(Math.min(xx0, xx1),
		                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
		                    Math.abs(yy1 - yy0));
//		        }
		        g2.setPaint(p);
		        g2.fill(block);
		        
		        g2.setPaint(Color.GRAY);
		        g2.setStroke(new BasicStroke(1.0f));
		        g2.draw(block);
	
		        EntityCollection entities = state.getEntityCollection();
		        if (entities != null) {
		            addEntity(entities, block, dataset, series, item, 0.0, 0.0);
		        }
			}
		};
		renderer.setPaintScale(new RainbowPaintScale(lowerBound, upperBound));
		
		return renderer;
	}

	/**
	 * Convenience method to create a popup-toggling button for use as chart
	 * axis labels.
	 * @param label the initial button label
	 * @param angle the angle of the button label (in radians)
	 * @param values the values to be displayed in the popup, objects along the second 
	 * 		  array dimension are grouped under items along the first array dimension
	 * @return a popup button
	 */
	private AbstractButton createPopupButton(String label, double angle, Object[][] values) {
		
		AbstractButton btn = new JToggleButton();
		btn.putClientProperty("value", label);
		btn.setIcon(new RotatedTextIcon(btn, angle));
		angle = Math.abs(angle);
		btn.setMargin(new Insets(
				1 + (int) (3 * Math.sin(angle)),
				1 + (int) (3 * Math.cos(angle)),
				1 + (int) (3 * Math.sin(angle)),
				1 + (int) (3 * Math.cos(angle))));
		btn.setBackground(new Color(245, 245, 245));
		btn.setFont(btn.getFont().deriveFont(Font.BOLD));
		
		JPopupMenu pop = new JPopupMenu();
		ButtonGroup grp = new ButtonGroup();
		for (Object[] group : values) {
			JComponent subMenu = pop;
			// identify sub-menu type
			if (values.length > 1) {
				if (group[0] instanceof OntologyChartType) {
					subMenu = new JMenu("Ontology");
				} else if (group[0] instanceof TaxonomyChartType) {
					subMenu = new JMenu("Taxonomy");
				} else if (group[0] instanceof HierarchyLevel) {
					subMenu = new JMenu("Hierarchy");
				}
			}
			// generate sub-menu items
			for (Object obj : group) {
				JMenuItem item = new JRadioButtonMenuItem(obj.toString());
				item.addActionListener(new ClientPropertyPopupActionListener("value", obj));
				subMenu.add(item);
				grp.add(item);
			}
			// add sub-menu to main menu
			if (subMenu != pop) {
				pop.add(subMenu);
			}
		}
		pop.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) { }
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				((AbstractButton) ((JPopupMenu) e.getSource()).getInvoker()).setSelected(false);
			}
		});
		
		btn.setComponentPopupMenu(pop);
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComponent src = (JComponent) e.getSource();
				src.getComponentPopupMenu().show(src, 0, src.getHeight());
			}
		});
		
		return btn;
	}

	/**
	 * {@inheritDoc}<p>
	 * Overridden to return the secondary vertical scroll bar.
	 */
	@Override
	public JScrollBar getVerticalScrollBar() {
		return this.getVerticalScrollBar(false);
	}
	
	/**
	 * Returns either one of the vertical scroll bars.
	 * @param primary if <code>true</code> the primary scroll bar will be returned, 
	 * 				  otherwise the secondary scroll bar will be returned
	 * @return the primary or secondary vertical scroll bar
	 */
	public JScrollBar getVerticalScrollBar(boolean primary) {
		if (primary) {
			return super.getVerticalScrollBar();
		} else {
			return this.getSecondaryVerticalScrollBar();
		}
	}
	
	/**
	 * Returns the secondary vertical scroll bar.
	 * @return the secondary vertical scroll bar
	 */
	public JScrollBar getSecondaryVerticalScrollBar() {
		return secVertBar;
	}

	/**
	 * Returns the preferred number of data columns being displayed in the scrollable view.
	 * @return the number of visible columns
	 */
	public int getVisibleColumnCount() {
		return this.visColCount;
	}

	/**
	 * Sets the number of data columns being displayed in the scrollable view.
	 * @param visColCount the number of visible columns
	 */
	public void setVisibleColumnCount(int visColCount) {
		this.visColCount = visColCount;
		if (visColCount > this.colCount) {
			visColCount = this.colCount;
		}
		JScrollBar horzBar = getHorizontalScrollBar();
		horzBar.setValues(horzBar.getValue(), visColCount, 0, this.colCount);
		horzBar.setBlockIncrement(this.colCount / 4);

		ChartPanel chartPnl = (ChartPanel) getViewport().getView();
		XYPlot plot = chartPnl.getChart().getXYPlot();
		ValueAxis xAxis = plot.getDomainAxis();
		xAxis.setRange(-0.5, visColCount - 0.5);
	}

	/**
	 * Returns the preferred number of data rows being displayed in the scrollable view.
	 * @return the number of visible rows
	 */
	public int getVisibleRowCount() {
		return this.visRowCount;
	}

	/**
	 * Sets the number of data rows being displayed in the scrollable view.
	 * @param visRowCount the number of visible rows
	 */
	public void setVisibleRowCount(int visRowCount) {
		this.visRowCount = visRowCount;
		if (visRowCount > this.rowCount) {
			visRowCount = this.rowCount;
		}
		JScrollBar secVertBar = getSecondaryVerticalScrollBar();
		secVertBar.setValues(secVertBar.getValue(), visRowCount, 0, this.rowCount);
		secVertBar.setBlockIncrement(this.rowCount / 4);

		ChartPanel chartPnl = (ChartPanel) getViewport().getView();
		XYPlot plot = chartPnl.getChart().getXYPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(-0.5, visRowCount - 0.5);
	}
	
	/**
	 * Returns the value client property of the axis button corresponding to the
	 * specified axis.
	 * @param axis the axis
	 * @return the axis button value property
	 */
	public Object getAxisButtonValue(Axis axis) {
		AbstractButton axisBtn = this.getAxisButton(axis);
		if (axisBtn != null) {
			return axisBtn.getClientProperty("value");
		}
		System.err.println("ERROR: unknown axis specified");
		return null;
	}
	
	/**
	 * Sets the value client property of the axis button corresponding to the
	 * specified axis to the specified value object.
	 * @param axis the axis
	 * @param obj the balue object
	 */
	public void setAxisButtonValue(Axis axis, Object obj) {
		AbstractButton axisBtn = this.getAxisButton(axis);
		if (axisBtn != null) {
			axisBtn.putClientProperty("value", obj);
		} else {
			System.err.println("ERROR: unknown axis specified");
		}
	}
	
	/**
	 * Returns the button corresponding to the specified axis
	 * @param axis the axis
	 * @return the axis button
	 */
	public AbstractButton getAxisButton(Axis axis) {
		switch (axis) {
		case X_AXIS:
			return this.xBtn;
		case Y_AXIS:
			return this.yBtn;
		case Z_AXIS:
			return this.zBtn;
		default:
			System.err.println("ERROR: unknown axis specified");
			return null;
		}
	}
	
	/**
	 * Custom paint scale implementation providing a continuous rainbow-like color gradient.
	 * 
	 * @author A. Behne
	 */
	public class RainbowPaintScale implements PaintScale {
		
		/**
		 * The lower value boundary of the scale.
		 */
		private double lowerBound;
		
		/**
		 * The upper value boundary of the scale.
		 */
		private double upperBound;
		
		/**
		 * The opacity of the scale.
		 */
		private float alpha;
		
		/**
		 * The background color of the scale.
		 */
		private Color background;
		
		/**
		 * Creates a rainbow paint scale with the default value range of [0.0
		 * 1.0] and 50% opacity blended into a white background.
		 */
		public RainbowPaintScale() {
			this(0.0, 1.0);
		}
		
		/**
		 * Creates a rainbow paint scale with the specified upper and lower
		 * range boundaries and colors with 50% opacity blended into a white
		 * background.
		 * @param lowerBound the lower value boundary of the scale
		 * @param upperBound the upper value boundary of the scale
		 */
		public RainbowPaintScale(double lowerBound, double upperBound) {
			this(lowerBound, upperBound, 0.5f, Color.WHITE);
		}
		
		/**
		 * Creates a rainbow paint scale with the specified upper and lower
		 * range boundaries as well as the specified opacity and background color.
		 * @param lowerBound the lower value boundary of the scale
		 * @param upperBound the upper value boundary of the scale
		 * @param alpha the opacity of the scale
		 * @param background the background color of the scale
		 */
		public RainbowPaintScale(double lowerBound, double upperBound,
				float alpha, Color background) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			if (alpha < 0.0f) {
				alpha = 0.0f;
			} else if (alpha > 1.0f) {
				alpha = 1.0f;
			}
			this.alpha = alpha;
			if (background == null) {
				background = Color.WHITE;
			}
			this.background = background;
		}

		@Override
		public double getLowerBound() {
			return lowerBound;
		}

		@Override
		public double getUpperBound() {
			return upperBound;
		}
		
		/**
		 * Sets the upper bound for the scale.
		 * @param the upper bound to set
		 */
		public void setUpperBound(double upperBound) {
			this.upperBound = upperBound;
		}

		@Override
		public Paint getPaint(double value) {
			// take lower/upper bounds into account
			value = value / (upperBound - lowerBound) + lowerBound;
			if (value > 1.0) {
				value = 1.0;
			}
			float r = (float) (-Math.abs(value - 0.75) * 4.0 + 1.5);
			float g = (float) (-Math.abs(value - 0.50) * 4.0 + 1.5);
			float b = (float) (-Math.abs(value - 0.25) * 4.0f+ 1.5);
			r = (r > 1.0f) ? 1.0f : (r < 0.0f) ? 0.0f : r;
			g = (g > 1.0f) ? 1.0f : (g < 0.0f) ? 0.0f : g;
			b = (b > 1.0f) ? 1.0f : (b < 0.0f) ? 0.0f : b;
			float[] bg = background.getColorComponents(null);
			r = r * alpha + bg[0] * (1.0f - alpha);
			g = g * alpha + bg[1] * (1.0f - alpha);
			b = b * alpha + bg[2] * (1.0f - alpha);
			
			Color col = new Color(r, g, b);
			return col;
		}
		
	}
	
	/**
	 * Convenience action listener implementation for popup callbacks.
	 * @author A. Behne
	 */
	public class ClientPropertyPopupActionListener implements ActionListener {

		/**
		 * The client property key.
		 */
		private Object key;
		
		/**
		 * The client property value.
		 */
		private Object value;
	
		/**
		 * Constructs an action listener using the specified client property key/value pair.
		 * @param key the client property key
		 * @param value the client property value
		 */
		public ClientPropertyPopupActionListener(Object key, Object value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * {@inheritDoc}<p>
		 * Overridden to set a specific client property of the source popup's invoking component.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			while (!(src instanceof JPopupMenu)) {
				src = ((Component) src).getParent();
			}
			JComponent invoker = (JComponent) ((JPopupMenu) src).getInvoker();
			while (invoker instanceof JMenu) {
				invoker = (JComponent) ((JPopupMenu) invoker.getParent()).getInvoker();
			}
			invoker.putClientProperty(key, value);
			invoker.revalidate();
		}
		
	}

	/**
	 * Class implementing rotated text in the form of an icon for use in UI components.
	 * @author A. Behne
	 */
	public class RotatedTextIcon implements Icon {

		/**
		 * The target component.
		 */
		private JComponent comp;
		
		/**
		 * The rotation angle in radians.
		 */
		private double angle;
		
		/**
		 * Constructs an icon targeting the specified component and displaying
		 * text rotated by the specified angle.
		 * @param comp the target component
		 * @param angle the rotation angle in radians
		 */
		public RotatedTextIcon(JComponent comp, double angle) {
			this.comp = comp;
			this.angle = angle;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			
			JLabel label = new JLabel(comp.getClientProperty("value").toString());
			label.setFont(comp.getFont());
			label.setEnabled(comp.isEnabled());
			if (angle != 0.0) {
				g2d.translate(0, (getIconHeight() - getIconWidth()) / 2.0);
				g2d.rotate(angle, x + getIconWidth() / 2.0, y + getIconWidth() / 2.0);
				g2d.translate(-(getIconHeight() - getIconWidth() - x) / 2.0 - 1.0, 0.0);
				
				SwingUtilities.paintComponent(g, label, new JPanel(), 
						new Rectangle(x + ((comp.isEnabled()) ? 0 : 2), y, getIconHeight(), getIconWidth()));

				g2d.translate((getIconHeight() - getIconWidth() - x) / 2.0 + 1.0, 0.0);
				g2d.rotate(-angle, x + getIconWidth() / 2.0, y + getIconWidth() / 2.0);
				g2d.translate(0, -(getIconHeight() - getIconWidth()) / 2.0);
			} else {
				SwingUtilities.paintComponent(g, label,
						new JPanel(), new Rectangle(x, y, getIconWidth(), getIconHeight()));
			}
		}

		// TODO: parameterize client property key
		@Override
		public int getIconWidth() {
			int width = comp.getFontMetrics(comp.getFont()).stringWidth(comp.getClientProperty("value").toString());
			int height = comp.getFontMetrics(comp.getFont()).getHeight();
			return (int) Math.round(Math.abs(width * Math.cos(angle) + height * Math.sin(angle)));
		}
		
		@Override
		public int getIconHeight() {
			int width = comp.getFontMetrics(comp.getFont()).stringWidth((String) comp.getClientProperty("value").toString());
			int height = comp.getFontMetrics(comp.getFont()).getHeight();
			return (int) Math.round(Math.abs(width * Math.sin(angle) + height * Math.cos(angle)));
		}
		
	}
	
}
