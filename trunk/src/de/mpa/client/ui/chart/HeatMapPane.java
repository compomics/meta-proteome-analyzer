package de.mpa.client.ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.MatrixSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * Scrollable heat map chart implementation.
 * 
 * @author A. Behne
 */
public class HeatMapPane extends JScrollPane {
	
	/**
	 * The amount of data rows in the dataset.
	 */
	private int rowCount;
	
	/**
	 * The amount of data columns in the dataset.
	 */
	private int colCount;
	
	/**
	 * Creates a scrollable heat map chart displaying the specified title, axis
	 * tick labels and data values.
	 * @param title the title to be displayed above the plot
	 * @param xLabels the tick labels for the x axis 
	 * @param yLabels the tick labels for the y axis
	 * @param data the matrix of values to be displayed as colored blocks
	 */
	public HeatMapPane(String title, String[] xLabels, String[] yLabels, MatrixSeries data) {
		// create plots and embed them in default scroll pane
		super(createChart(title, createPlot(xLabels, yLabels, data)),
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// cache data dimensions
		this.rowCount = yLabels.length;
		this.colCount = xLabels.length;
		
		// grab a few plot components for later use
		ChartPanel chartPnl = (ChartPanel) this.getViewport().getView();
		XYPlot plot = chartPnl.getChart().getXYPlot();
		final ValueAxis xAxis = plot.getDomainAxis();
		final ValueAxis yAxis = plot.getRangeAxis();
		
		// modify scroll pane's scrolling behavior by removing default listeners
		// and installing new ones
		for (ChangeListener cl : this.getViewport().getChangeListeners()) {
			this.getViewport().removeChangeListener(cl);
		}
		
		// modify vertical scroll bar behavior
		final JScrollBar vertBar = this.getVerticalScrollBar();
		vertBar.setValues(0, rowCount, 0, rowCount);
		vertBar.setBlockIncrement(rowCount / 4);
		DefaultBoundedRangeModel vertBarMdl = (DefaultBoundedRangeModel) vertBar.getModel();
		ChangeListener[] vcl = vertBarMdl.getChangeListeners();
		vertBarMdl.removeChangeListener(vcl[0]);
		
		vertBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int val = e.getValue();
				yAxis.setLowerBound(val - 0.5);
				yAxis.setUpperBound(val + vertBar.getModel().getExtent() - 0.5);
			}
		});
		
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
				xAxis.setLowerBound(val - 0.5);
				xAxis.setUpperBound(val + horzBar.getModel().getExtent() - 0.5);
			}
		});
	}
	
	/**
	 * Sets the number of data rows being displayed in the scrollable view.
	 * @param visRowCount the number of visible rows
	 */
	public void setVisibleRowCount(int visRowCount) {
		if (visRowCount > rowCount) {
			visRowCount = rowCount;
		}
		JScrollBar vertBar = getVerticalScrollBar();
		vertBar.setValues(vertBar.getValue(), visRowCount, 0, rowCount);
	}
	
	/**
	 * Sets the number of data columns being displayed in the scrollable view.
	 * @param visColCount the number of visible columns
	 */
	public void setVisibleColumnCount(int visColCount) {
		if (visColCount > colCount) {
			visColCount = colCount;
		}
		JScrollBar horzBar = getHorizontalScrollBar();
		horzBar.setValues(horzBar.getValue(), visColCount, 0, colCount);
	}
	
	/**
	 * Sets the plot instance displayed inside the heat map view.
	 * @param plot the plot instance
	 */
	public void setPlot(XYPlot plot) {
		ChartPanel chartPnl = (ChartPanel) this.getViewport().getView();
		this.setViewportView(createChart(chartPnl.getChart().getTitle().getText(), plot));
	}

	/**
	 * Creates a chart panel from the specified plot and title.
	 * @param title the title to be displayed above the plot
	 * @param plot the plot to be displayed in the heat map view
	 * @return the chart panel
	 */
	private static ChartPanel createChart(String title, XYPlot plot) {
		
		// create color bar
		NumberAxis scaleAxis = new NumberAxis();
		scaleAxis.setTickUnit(new NumberTickUnit(0.1));
		scaleAxis.setNumberFormatOverride(new DecimalFormat("0.0"));
		PaintScaleLegend psl = new PaintScaleLegend(
				((XYBlockRenderer) plot.getRenderer()).getPaintScale(), scaleAxis);
		psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
		psl.setMargin(4.0, 10.0, 19.0, 5.0);
		psl.setPosition(RectangleEdge.RIGHT);

		// create chart, add color bar next to it
		JFreeChart chart = new JFreeChart(title, plot);
		chart.removeLegend();
		chart.addSubtitle(psl);
		chart.setBackgroundPaint(Color.WHITE);
		
		// wrap chart in panel, remove default context menu capabilities
		ChartPanel chartPnl = new ChartPanel(chart);
		chartPnl.setPreferredSize(new Dimension());
		chartPnl.removeMouseListener(chartPnl.getMouseListeners()[1]);
		
		return chartPnl;
	}

	/**
	 * Creates a heat map plot using the specified axis tick labels as well as
	 * data values and applies the default blue-green-red color scale renderer
	 * to it.
	 * @param xLabels the tick labels of the x axis
	 * @param yLabels the tick labels of the y axis
	 * @param data the matrix of values to be displayed as colored blocks
	 * @return the heat map plot
	 */
	public static XYPlot createPlot(String[] xLabels, String[] yLabels,
			MatrixSeries data) {
		return createPlot(xLabels, yLabels, data, createDefaultRenderer());
	}

	/**
	 * Creates a heat map plot using the specified axis tick labels, data values
	 * and renderer.
	 * @param xLabels the tick labels of the x axis
	 * @param yLabels the tick labels of the y axis
	 * @param data the matrix of values to be displayed as colored blocks
	 * @param renderer the renderer to draw colored blocks
	 * @return the heat map plot
	 */
	public static XYPlot createPlot(String[] xLabels, String[] yLabels,
			MatrixSeries data, XYBlockRenderer renderer) {
		
		// create x axis
		SymbolAxis xAxis = new SymbolAxis(null, xLabels);
		xAxis.setGridBandsVisible(false);
		
		// create y axis
		SymbolAxis yAxis = new SymbolAxis(null, yLabels) {
			/**
			 * {@inheritDoc}<p>
			 * Overriden to take into account all tick labels, not only the
			 * currently visible ones.
			 */
			@Override
			protected double findMaximumTickLabelWidth(List ticks, Graphics2D g2,
					Rectangle2D drawArea, boolean vertical) {
				List<NumberTick> newTicks = new ArrayList<NumberTick>();
				for (String symbol : getSymbols()) {
					newTicks.add(new NumberTick(0.0,
							symbol, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0));
				}
				return super.findMaximumTickLabelWidth(newTicks, g2, drawArea, vertical);
			}
		};
		yAxis.setInverted(true);
		
		// create plot, hide grid lines
		XYPlot plot = new XYPlot(new MatrixSeriesCollection(data),
				xAxis, yAxis, renderer);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        
		return plot;
	}

	/**
	 * Creates a block renderer with the default [0.0 1.0] rainbow color gradient scale.
	 * @return the default block renderer instance
	 */
	private static XYBlockRenderer createDefaultRenderer() {
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
				
				double xx0 = domainAxis.valueToJava2D(x - 0.5, dataArea,
		                plot.getDomainAxisEdge());
		        double yy0 = rangeAxis.valueToJava2D(y - 0.5, dataArea,
		                plot.getRangeAxisEdge());
		        double xx1 = domainAxis.valueToJava2D(x + this.getBlockWidth() - 0.5,
		        		dataArea, plot.getDomainAxisEdge());
		        double yy1 = rangeAxis.valueToJava2D(y + this.getBlockHeight() - 0.5,
		        		dataArea, plot.getRangeAxisEdge());
		        Rectangle2D block;
		        PlotOrientation orientation = plot.getOrientation();
		        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
		            block = new Rectangle2D.Double(Math.min(yy0, yy1),
		                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
		                    Math.abs(xx0 - xx1));
		        } else {
		            block = new Rectangle2D.Double(Math.min(xx0, xx1),
		                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
		                    Math.abs(yy1 - yy0));
		        }
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
		renderer.setPaintScale(new RainbowPaintScale());
		
		return renderer;
	}

	/**
	 * Custom paint scale implementation providing a continuous rainbow-like color gradient.
	 * 
	 * @author A. Behne
	 */
	private static class RainbowPaintScale implements PaintScale {
		// TODO: add getters/setters to allow dynamically modifying bounds
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
			this(0.0, 1.0, 0.5f, Color.WHITE);
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

		@Override
		public Paint getPaint(double value) {
//			int size = 10;
//			int i = (int) (value * size);
//			float r = -Math.abs(i+1 - size*0.75f) * 4.0f/size + 1.5f;
//			float g = -Math.abs(i+1 - size*0.50f) * 4.0f/size + 1.5f;
//			float b = -Math.abs(i+1 - size*0.25f) * 4.0f/size + 1.5f;
			// TODO: take lower/upper bounds into account
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
			
			return new Color(r, g, b);
		}
		
	}
	
}
