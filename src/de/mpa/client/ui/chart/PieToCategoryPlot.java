package de.mpa.client.ui.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.DataUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleEdge;

/**
 * Adapter class for a category plot wrapping a pie plot.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class PieToCategoryPlot extends CategoryPlot {
	
	/**
	 * The pie plot reference.
	 */
	private final PiePlot piePlot;
	
	/**
	 * The column key of the highlighted item.
	 */
	@SuppressWarnings("rawtypes")
	private Comparable highlightedKey;
	
	/**
	 * The column key of the selected item.
	 */
	@SuppressWarnings("rawtypes")
	private Comparable selectedKey;
	
	/**
	 * Constructs a pie-to-category wrapper plot using the specified pie plot.
	 * @param piePlot the pie plot to reference
	 */
	public PieToCategoryPlot(PiePlot piePlot) {
		this(piePlot, PieToCategoryPlot.createDefaultCategoryAxis(), PieToCategoryPlot.createDefaultValueAxis());
	}

	/**
	 * Constructs a pie-to-category wrapper plot using the specified pie plot and axes.
	 * @param piePlot the pie plot to reference
	 * @param domainAxis the domain axis
	 * @param rangeAxis the range axis
	 */
	public PieToCategoryPlot(PiePlot piePlot, CategoryAxis domainAxis, ValueAxis rangeAxis) {
		this(piePlot, domainAxis, rangeAxis, null);
	}

	/**
	 * Constructs a pie-to-category wrapper plot using the specified pie plot, axes and renderer.
	 * @param piePlot the pie plot to reference
	 * @param domainAxis the domain axis
	 * @param rangeAxis the range axis
	 * @param renderer the renderer
	 */
	public PieToCategoryPlot(PiePlot piePlot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
		super(new PieToCategoryDataset(piePlot.getDataset()), domainAxis, rangeAxis, renderer);
		this.piePlot = piePlot;
		if (renderer == null) {
			renderer = createDefaultRenderer(piePlot);
		}
        setRenderer(renderer);
		// TODO: implement interactivity (clickable bars, maybe scrollable view)
	}
	
	/**
	 * Makes the underlying dataset to reference the specified pie dataset.
	 * @param dataset the pie dataset to reference
	 */
	public void setDataset(PieDataset dataset) {
        piePlot.setDataset(dataset);
        setDataset(new PieToCategoryDataset(dataset));
	}
	
	/**
	 * Convenience method to create and configure the default category axis.
	 * @return the default category axis
	 */
	private static CategoryAxis createDefaultCategoryAxis() {
		CategoryAxis categoryAxis = new CategoryAxis(" ") {
			@SuppressWarnings("rawtypes")
			@Override
			protected TextBlock createLabel(Comparable category,
					float width, RectangleEdge edge, Graphics2D g2) {
				// overridden to avoid label truncation
				TextBlock block = new TextBlock();
				block.addLine(category.toString(),
                        getTickLabelFont(category), getTickLabelPaint(category));
				return block;
			}
		};
		categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		
		return categoryAxis;
	}
	
	/**
	 * Convenience method to create and configure the default range axis.
	 * @return the default range axis
	 */
	private static ValueAxis createDefaultValueAxis() {
        ValueAxis valueAxis = new NumberAxis(null);
        valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
		return valueAxis;
	}
	
	/**
	 * Convenience method to create the default renderer referencing the specified pie plot.
	 * @param piePlot the pie plot to reference
	 * @return the default renderer
	 */
	private CategoryItemRenderer createDefaultRenderer(PiePlot piePlot) {
		
		piePlot.clearSectionPaints(false);
		
		BarRenderer renderer = new BarRenderer3DExt(piePlot);
		renderer.setDrawBarOutline(true);
		renderer.setShadowVisible(false);
		
		CategoryToolTipGenerator generator = new StandardCategoryToolTipGenerator(
				"<html>{1}<br><center>{2} ({3})</center></html>", new DecimalFormat("0")) {
			private final NumberFormat percentFormat = new DecimalFormat("0.00%");
			@Override
			protected Object[] createItemArray(
					CategoryDataset dataset, int row, int column) {
				Object[] result = super.createItemArray(dataset, row, column);
				
		        Number value = dataset.getValue(row, column);
	            double total = DataUtilities.calculateRowTotal(dataset, row);
	            double percent = value.doubleValue() / total;
	            result[3] = percentFormat.format(percent);

		        return result;
			}
		};
		renderer.setBaseToolTipGenerator(generator);
		
		return renderer;
	}

	/**
	 * Sets the key to highlight.
	 * @param key the key to highlight
	 */
	@SuppressWarnings("rawtypes")
	public void setHighlightedKey(Comparable key) {
        highlightedKey = key;
        this.fireChangeEvent();
	}

	/**
	 * Sets the key to select.
	 * @param key the key to select
	 */
	@SuppressWarnings("rawtypes")
	public void setSelectedKey(Comparable key) {
        selectedKey = key;
        this.fireChangeEvent();
	}
	
	/**
	 * Sets the starting angle of the underlying pie plot.
	 * @param angle the angle in degrees
	 */
	public void setStartAngle(double angle) {
        piePlot.setStartAngle(angle);
	}
	
	/**
	 * Extended 3D bar renderer for displaying offset bars for
	 * highlighting/selecting purposes.
	 * 
	 * @author A. Behne
	 */
	private class BarRenderer3DExt extends BarRenderer3D {

		/**
		 * The reference pie plot
		 */
		private final PiePlot piePlot;
		
		public BarRenderer3DExt(PiePlot piePlot) {
			this.piePlot = piePlot;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Paint getItemPaint(int row, int column) {
			// fetch color from underlying pie plot
			PieDataset dataset = this.piePlot.getDataset();
			Comparable key = dataset.getKey(column);
			Paint paint = this.piePlot.getSectionPaint(key);
			if (paint == null) {
				// refresh colors and try again
                this.piePlot.getLegendItems();
				paint = this.piePlot.getSectionPaint(key);
			}
			return paint;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void drawItem(Graphics2D g2,
				CategoryItemRendererState state, Rectangle2D dataArea,
				CategoryPlot plot, CategoryAxis domainAxis,
				ValueAxis rangeAxis, CategoryDataset dataset, int row,
				int column, int pass) {
			// check the value we are plotting...
	        Number dataValue = dataset.getValue(row, column);
	        if (dataValue == null) {
	            return;
	        }
	        
	        // check whether the item shall be drawn highlighted/selected
			Comparable key = dataset.getColumnKey(column);
			boolean highlighted = key.equals(PieToCategoryPlot.this.highlightedKey);
			boolean selected = key.equals(PieToCategoryPlot.this.selectedKey);

	        double value = dataValue.doubleValue();

	        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
	                dataArea.getY() + this.getYOffset(),
	                dataArea.getWidth() - this.getXOffset(),
	                dataArea.getHeight() - this.getYOffset());

	        PlotOrientation orientation = plot.getOrientation();

	        double barW0 = this.calculateBarW0(plot, orientation, adjusted, domainAxis,
	                state, row, column);
	        double[] barL0L1 = this.calculateBarL0L1(value);
	        if (barL0L1 == null) {
	            return;  // the bar is not visible
	        }

	        RectangleEdge edge = plot.getRangeAxisEdge();
	        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
	        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
	        double barL0 = Math.min(transL0, transL1);
	        double barLength = Math.abs(transL1 - transL0);

	        double xOffset = getXOffset() / 2.0;
	        double yOffset = getYOffset() / 2.0;
	        
	        if (selected) {
	        	// do nothing
	        } else if (highlighted) {
	        	barW0 += xOffset / 2.0;
		        barL0 -= yOffset / 2.0;
			} else {
		        barW0 += xOffset;
		        barL0 -= yOffset;
			}
	        
	        // draw the bar...
	        Rectangle2D bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(),
                    barLength);
	        Paint itemPaint = this.getItemPaint(row, column);
	        g2.setPaint(itemPaint);
	        g2.fill(bar);
	        
	        double x0 = bar.getMinX();
	        double x1 = x0 + xOffset;
	        double x2 = bar.getMaxX();
	        double x3 = x2 + xOffset;

	        double y0 = bar.getMinY() - yOffset;
	        double y1 = bar.getMinY();
	        double y2 = bar.getMaxY() - yOffset;
	        double y3 = bar.getMaxY();

	        GeneralPath bar3dRight = null;
	        GeneralPath bar3dTop = null;
	        if (barLength > 0.0) {
	            bar3dRight = new GeneralPath();
	            bar3dRight.moveTo((float) x2, (float) y3);
	            bar3dRight.lineTo((float) x2, (float) y1);
	            bar3dRight.lineTo((float) x3, (float) y0);
	            bar3dRight.lineTo((float) x3, (float) y2);
	            bar3dRight.closePath();

	            if (itemPaint instanceof Color) {
	                g2.setPaint(((Color) itemPaint).darker());
	            }
	            g2.fill(bar3dRight);
	        }

	        bar3dTop = new GeneralPath();
	        bar3dTop.moveTo((float) x0, (float) y1);
	        bar3dTop.lineTo((float) x1, (float) y0);
	        bar3dTop.lineTo((float) x3, (float) y0);
	        bar3dTop.lineTo((float) x2, (float) y1);
	        bar3dTop.closePath();
	        g2.fill(bar3dTop);

	        if (this.isDrawBarOutline()
	                && state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) {
	            g2.setStroke(this.getItemOutlineStroke(row, column));
	            g2.setPaint(this.getItemOutlinePaint(row, column));
	            g2.draw(bar);
	            if (bar3dRight != null) {
	                g2.draw(bar3dRight);
	            }
	            if (bar3dTop != null) {
	                g2.draw(bar3dTop);
	            }
	        }

	        CategoryItemLabelGenerator generator
	            = this.getItemLabelGenerator(row, column);
	        if (generator != null && this.isItemLabelVisible(row, column)) {
                this.drawItemLabel(g2, dataset, row, column, plot, generator, bar,
	                    (value < 0.0));
	        }

	        // add an item entity, if this information is being collected
	        EntityCollection entities = state.getEntityCollection();
	        if (entities != null) {
	            GeneralPath barOutline = new GeneralPath();
	            barOutline.moveTo((float) x0, (float) y3);
	            barOutline.lineTo((float) x0, (float) y1);
	            barOutline.lineTo((float) x1, (float) y0);
	            barOutline.lineTo((float) x3, (float) y0);
	            barOutline.lineTo((float) x3, (float) y2);
	            barOutline.lineTo((float) x2, (float) y3);
	            barOutline.closePath();
                this.addItemEntity(entities, dataset, row, column, barOutline);
	        }
		}
	}
	
}
