package de.mpa.client.ui.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.Vector;
import org.jfree.ui.RectangleInsets;

/**
 * 3D pie plot with explodable pieces.
 * 
 * @author A. Behne
 */
public class PiePlot3DExt extends PiePlot3D {

	/**
	 * The renderer state.
	 */
	private PiePlotState state;
	
	/**
	 * The maximum relative amount a pie section can extend from the pie center.
	 */
	private double maximumExplodePercent = 0.0;

	/**
	 * Constructs an extend 3D pie plot using the specified dataset and maximum explode ratio.
	 * @param dataset the pie dataset
	 * @param maximumExplodePercent the maximum relative amount a pie section can extend from the pie center
	 */
	public PiePlot3DExt(PieDataset dataset, double maximumExplodePercent) {
		super(dataset);
		setMaximumExplodePercent(maximumExplodePercent);
	}
	
	@Override
	public double getMaximumExplodePercent() {
		return maximumExplodePercent;
	}
	
	/**
	 * Sets the maximum relative amount a pie section can extend from the pie center.
	 * @param maximumExplodePercent the maximum explode ratio
	 */
	public void setMaximumExplodePercent(double maximumExplodePercent) {
		this.maximumExplodePercent = maximumExplodePercent;
	}

	/**
	 * Draws the plot on a Java 2D graphics device (such as the screen or a
	 * printer).  This method is called by the
	 * {@link org.jfree.chart.JFreeChart} class, you don't normally need
	 * to call it yourself.
	 *
	 * @param g2  the graphics device.
	 * @param plotArea  the area within which the plot should be drawn.
	 * @param anchor  the anchor point.
	 * @param parentState  the state from the parent plot, if there is one.
	 * @param info  collects info about the drawing
	 *              (<code>null</code> permitted).
	 */
	@Override
	public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D anchor,
			PlotState parentState,
			PlotRenderingInfo info) {

		// adjust for insets...
		RectangleInsets insets = getInsets();
		insets.trim(plotArea);

		Rectangle2D originalPlotArea = (Rectangle2D) plotArea.clone();
		if (info != null) {
			info.setPlotArea(plotArea);
			info.setDataArea(plotArea);
		}

		drawBackground(g2, plotArea);

		Shape savedClip = g2.getClip();
		g2.clip(plotArea);

		Graphics2D savedG2 = g2;
		BufferedImage dataImage = null;
		if (this.getShadowGenerator() != null) {
			dataImage = new BufferedImage((int) plotArea.getWidth(),
					(int) plotArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
			g2 = dataImage.createGraphics();
			g2.translate(-plotArea.getX(), -plotArea.getY());
			g2.setRenderingHints(savedG2.getRenderingHints());
			originalPlotArea = (Rectangle2D) plotArea.clone();
		}
		// adjust the plot area by the interior spacing value
		double gapPercent = this.getInteriorGap();
		double labelPercent = 0.0;
		if (getLabelGenerator() != null) {
			labelPercent = this.getLabelGap() + this.getMaximumLabelWidth();
		}
		double gapHorizontal = plotArea.getWidth() * (gapPercent + labelPercent) * 2.0;
		double gapVertical = plotArea.getHeight() * gapPercent * 2.0;

		double linkX = plotArea.getX() + gapHorizontal / 2;
		double linkY = plotArea.getY() + gapVertical / 2;
		double linkW = plotArea.getWidth() - gapHorizontal;
		double linkH = plotArea.getHeight() - gapVertical;

		// make the link area a square if the pie chart is to be circular...
		if (isCircular()) { // is circular?
			double min = Math.min(linkW, linkH) / 2;
			linkX = (linkX + linkX + linkW) / 2 - min;
			linkY = (linkY + linkY + linkH) / 2 - min;
			linkW = 2 * min;
			linkH = 2 * min;
		}

		state = this.initialise(g2, plotArea, this, null, info);

		// the link area defines the dog leg points for the linking lines to
		// the labels
		Rectangle2D linkAreaXX = new Rectangle2D.Double(
				linkX, linkY, linkW, linkH * (1 - getDepthFactor()));
		state.setLinkArea(linkAreaXX);

		// the explode area defines the max circle/ellipse for the exploded pie
		// sections.
		// it is defined by shrinking the linkArea by the linkMargin factor.
		double hh = linkW * getLabelLinkMargin();
		double vv = linkH * getLabelLinkMargin();
		Rectangle2D explodedArea = new Rectangle2D.Double(
				linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv);

		state.setExplodedPieArea(explodedArea);

		// the pie area defines the circle/ellipse for regular pie sections.
		// it is defined by shrinking the explodeArea by the explodeMargin
		// factor.
		double maximumExplodePercent = this.getMaximumExplodePercent();
		double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);

		double h1 = explodedArea.getWidth() * percent;
		double v1 = explodedArea.getHeight() * percent;
		Rectangle2D pieArea = new Rectangle2D.Double(
				explodedArea.getX() + h1 / 2.0, explodedArea.getY() + v1 / 2.0,
				explodedArea.getWidth() - h1, explodedArea.getHeight() - v1);

		// the link area defines the dog-leg point for the linking lines to
		// the labels
		int depth = (int) (pieArea.getHeight() * getDepthFactor());
		Rectangle2D linkArea = new Rectangle2D.Double(
				linkX, linkY, linkW, linkH - depth);
		state.setLinkArea(linkArea);

		state.setPieArea(pieArea);
		state.setPieCenterX(pieArea.getCenterX());
		state.setPieCenterY(pieArea.getCenterY() - depth / 2.0);
		state.setPieWRadius(pieArea.getWidth() / 2.0);
		state.setPieHRadius((pieArea.getHeight() - depth) / 2.0);

		// get the data source - return if null;
		PieDataset dataset = this.getDataset();
		if (DatasetUtilities.isEmptyOrNull(dataset)) {
			this.drawNoDataMessage(g2, plotArea);
			g2.setClip(savedClip);
			this.drawOutline(g2, plotArea);
			return;
		}

		// if too any elements
		if (dataset.getKeys().size() > plotArea.getWidth()) {
			String text = localizationResources.getString("Too_many_elements");
			Font sfont = new Font("dialog", Font.BOLD, 10);
			g2.setFont(sfont);
			FontMetrics fm = g2.getFontMetrics(sfont);
			int stringWidth = fm.stringWidth(text);

			g2.drawString(text,
					(int) (plotArea.getX() + (plotArea.getWidth() - stringWidth) / 2),
					(int) (plotArea.getY() + (plotArea.getHeight() / 2)));
			return;
		}
		// if we are drawing a perfect circle, we need to readjust the top left
		// coordinates of the drawing area for the arcs to arrive at this
		// effect.
		if (isCircular()) {
			double min = Math.min(plotArea.getWidth(), plotArea.getHeight()) / 2;
			plotArea = new Rectangle2D.Double(
					plotArea.getCenterX() - min, plotArea.getCenterY() - min, 2 * min, 2 * min);
		}
		// get a list of keys...
		List sectionKeys = dataset.getKeys();

		if (sectionKeys.size() == 0) {
			return;
		}

		// establish the coordinates of the top left corner of the drawing area
		double arcX = pieArea.getX();
		double arcY = pieArea.getY();

		//g2.clip(clipArea);
		Composite originalComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, this.getForegroundAlpha()));

		double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
		double runningTotal = 0;
		if (depth < 0) {
			return;  // if depth is negative don't draw anything
		}

		List<Arc2D> arcList = new ArrayList<Arc2D>(sectionKeys.size());
		Iterator iterator = sectionKeys.iterator();
		while (iterator.hasNext()) {

			Comparable currentKey = (Comparable) iterator.next();
			
			this.lookupSectionPaint(currentKey);
			
			Number dataValue = dataset.getValue(currentKey);
			if (dataValue == null) {
				arcList.add(null);
				continue;
			}
			double value = dataValue.doubleValue();
			if (value <= 0.0) {
				arcList.add(null);
				continue;
			}
			double startAngle = this.getStartAngle();
			double direction = this.getDirection().getFactor();
			double arcStartAngle = startAngle + (direction * (runningTotal * 360))
					/ totalValue;
			double arcAngleExtent = direction * value / totalValue * 360.0;
			if (Math.abs(arcAngleExtent) > this.getMinimumArcAngleToDraw()) {
				double expPercent = this.getExplodePercent(currentKey);
				double centerAngle = Math.toRadians(arcStartAngle + arcAngleExtent / 2.0);
				double xOffset = expPercent * state.getPieWRadius() * Math.cos(centerAngle);
				double yOffset = expPercent * state.getPieHRadius() * Math.sin(centerAngle);

				Arc2D arc = new Arc2D.Double(arcX + xOffset, arcY - yOffset + depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						arcStartAngle, arcAngleExtent, Arc2D.PIE);
				arcList.add(arc);
			} else {
				arcList.add(null);
			}
			runningTotal += value;
		}
		
		Shape oldClip = g2.getClip();

		// draw the bottom segments
		int categoryCount = arcList.size();
		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			Arc2D arc = arcList.get(categoryIndex);
			if (arc == null) {
				continue;
			}
			Comparable key = this.getSectionKey(categoryIndex);
			Paint sectionPaint = this.lookupSectionPaint(key);
			Paint outlinePaint = this.lookupSectionOutlinePaint(key);
			Stroke outlineStroke = this.lookupSectionOutlineStroke(key);
			
			g2.setPaint(sectionPaint);
			
			g2.fill(arc);
			g2.setPaint(outlinePaint);
			g2.setStroke(outlineStroke);
			g2.draw(arc);
		}
		
		// paint mantle back faces
		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			Arc2D arc = arcList.get(categoryIndex);
			if (arc == null) {
				continue;
			}
			Comparable key = this.getSectionKey(categoryIndex);
			Paint outlinePaint = this.lookupSectionOutlinePaint(key);
			Stroke outlineStroke = this.lookupSectionOutlineStroke(key);
			
			// shade mantle back face
			Shape backMantle = this.getArcMantleBackArea(arc, depth);
			
			g2.setPaint(Color.GRAY);
			g2.fill(backMantle);
			
			g2.setStroke(outlineStroke);
			g2.setPaint(outlinePaint);
			g2.draw(backMantle);
		}
		
		// build lists of faces
		List<IndexedFace> faces = new ArrayList<IndexedFace>();
		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			Arc2D arc = arcList.get(categoryIndex);
			if (arc == null) {
				continue;
			}
			faces.addAll(this.getArcInnerBackFaces(categoryIndex, arc, depth));
			faces.addAll(this.getArcInnerFrontFaces(categoryIndex, arc, depth));
			faces.addAll(this.getArcMantleFrontFaces(categoryIndex, arc, depth));
		}
		// sort faces
		Collections.sort(faces);
		
		// paint faces
		for (IndexedFace face : faces) {
			int categoryIndex = face.getIndex();
			Shape shape = face.getShape();

			Comparable key = this.getSectionKey(categoryIndex);
			Paint sectionPaint = (face.isBackFace()) ? Color.GRAY : this.lookupSectionPaint(key);
			Paint outlinePaint = this.lookupSectionOutlinePaint(key);
			Stroke outlineStroke = this.lookupSectionOutlineStroke(key);
			
			if (face.isMantleFace()) {
				g2.setPaint(Color.GRAY);
				g2.fill(shape);
			}

			g2.setPaint(sectionPaint);
			g2.fill(shape);
			g2.setStroke(outlineStroke);
			g2.setPaint(outlinePaint);
			g2.draw(shape);
		}
		
		g2.setClip(oldClip);

		// draw the sections at the top of the pie (and set up tooltips)...
		for (int sectionIndex = 0; sectionIndex < categoryCount; sectionIndex++) {
			Arc2D arc = arcList.get(sectionIndex);
			if (arc == null) {
				continue;
			}
			Arc2D upperArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
					pieArea.getWidth(), pieArea.getHeight() - depth,
					arc.getAngleStart(), arc.getAngleExtent(), Arc2D.PIE);

			Comparable currentKey = (Comparable) sectionKeys.get(sectionIndex);
			Paint sectionPaint = this.lookupSectionPaint(currentKey, true);
			Paint outlinePaint = this.lookupSectionOutlinePaint(currentKey);
			Stroke outlineStroke = this.lookupSectionOutlineStroke(currentKey);
			g2.setPaint(sectionPaint);
			g2.fill(upperArc);
			g2.setStroke(outlineStroke);
			g2.setPaint(outlinePaint);
			g2.draw(upperArc);

			// add a tooltip for the section...
			if (info != null) {
				EntityCollection entities = info.getOwner().getEntityCollection();
				if (entities != null) {
					String tip = null;
					PieToolTipGenerator tipster = this.getToolTipGenerator();
					if (tipster != null) {
						// @mgs: using the method's return value was missing
						tip = tipster.generateToolTip(dataset, currentKey);
					}
					String url = null;
					if (getURLGenerator() != null) {
						url = this.getURLGenerator().generateURL(
								dataset, currentKey, this.getPieIndex());
					}
					PieSectionEntity entity = new PieSectionEntity(
							upperArc, dataset, this.getPieIndex(), sectionIndex,
							currentKey, tip, url);
					entities.add(entity);
				}
			}
		}

		List keys = dataset.getKeys();
		Rectangle2D adjustedPlotArea = new Rectangle2D.Double(
                originalPlotArea.getX(), originalPlotArea.getY() + 8.0,
                originalPlotArea.getWidth(), originalPlotArea.getHeight()
                - getShadowYOffset() - 8.0);
//		setInteriorGap(0.0);
		if (this.getSimpleLabels()) {
			this.drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea,
					linkArea, state);
		} else {
			this.drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea,
					state);
		}
//		setInteriorGap(gapPercent);

		if (getShadowGenerator() != null) {
			BufferedImage shadowImage 
			= getShadowGenerator().createDropShadow(dataImage);
			g2 = savedG2;
			g2.drawImage(shadowImage, (int) plotArea.getX() 
					+ getShadowGenerator().calculateOffsetX(),
					(int) plotArea.getY() 
					+ getShadowGenerator().calculateOffsetY(), null);
			g2.drawImage(dataImage, (int) plotArea.getX(),
					(int) plotArea.getY(), null);
		}

		g2.setClip(savedClip);
		g2.setComposite(originalComposite);
		this.drawOutline(g2, originalPlotArea);

	}

	/**
	 * Utility method to return back-facing mantle area of a pie segment.
	 * @param arc The pie segment's bottom arc shape.
	 * @param depth The pie's extrusion height.
	 * @return The back-facing mantle area.
	 */
	protected Shape getArcMantleBackArea(Arc2D arc, float depth) {
		
		Point2D startPoint = arc.getStartPoint();
		Point2D endPoint = arc.getEndPoint();
	
		float startX = (float) startPoint.getX();
		float startY = (float) startPoint.getY();
		float endX = (float) endPoint.getX();
		float endY = (float) (endPoint.getY());
		
		double start = arc.getAngleStart();
		double extent = Math.abs(arc.getAngleExtent());
	
		float cx = (float) arc.getCenterX();
		float cy = (float) arc.getCenterY();
		float rx = (float) state.getPieWRadius();
		float ry = (float) state.getPieHRadius();
		
		GeneralPathExt path = new GeneralPathExt();
		path.moveTo(startX, startY - depth);
		if (start > 180.0) {
			if (extent > (start - 180.0)) {
				// crossing left boundary from the front
				path.moveTo(cx - rx, cy - depth);
				if (extent > start) {
					// also crossing right boundary from the back
					path.arcTo(rx, ry, 0, false, true, cx + rx, cy - depth);
					path.lineTo(cx + rx, cy);
				} else {
					// arc ends before right boundary
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
				}
				path.arcTo(rx, ry, 0, false, false, cx - rx, cy);
				path.closePath();
			}
		} else if (start > 0.0) {
			if (extent > start) {
				// crossing right boundary from the back
				path.arcTo(rx, ry, 0, false, true, cx + rx, cy - depth);
				path.lineTo(cx + rx, cy);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				if (extent > (start + 180.0)) {
					// also crossing left boundary from the front
					path.moveTo(cx - rx, cy - depth);
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
					path.arcTo(rx, ry, 0, false, false, cx - rx, cy);
					path.closePath();
				}
			} else {
				// arc ends before right boundary
				path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
				path.lineTo(endX, endY);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
			}
		} else if (start > -180.0) {
			if (extent > (start + 180.0)) {
				// crossing left boundary from the front
				path.moveTo(cx - rx, cy - depth);
				if (extent > (start + 360.0)) {
					// also crossing right boundary from the back
					path.arcTo(rx, ry, 0, false, true, cx + rx, cy - depth);
					path.lineTo(cx + rx, cy);
				} else {
					// arc ends before right boundary
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
				}
				path.arcTo(rx, ry, 0, false, false, cx - rx, cy);
				path.closePath();
			}
		} else {
			if (extent > (start + 360)) {
				// crossing right boundary from the back
				path.arcTo(rx, ry, 0, false, true, cx + rx, cy - depth);
				path.lineTo(cx + rx, cy);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				if (extent > (start + 540.0)) {
					// also crossing left boundary from the front
					path.moveTo(cx - rx, cy - depth);
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
					path.arcTo(rx, ry, 0, false, false, cx - rx, cy);
					path.closePath();
				}
			} else {
				// arc ends before right boundary
				path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
				path.lineTo(endX, endY);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
			}
		}
		
		return path;
	}

	/**
	 * TODO: API
	 * @param segment
	 * @param depth
	 * @return
	 */
	protected List<IndexedFace> getArcInnerBackFaces(int index, Arc2D arc, float depth) {
		// TODO: add a few comments
		Point2D startPoint = arc.getStartPoint();
		Point2D endPoint = arc.getEndPoint();
	
		float startX = (float) startPoint.getX();
		float startY = (float) startPoint.getY();
		float endX = (float) endPoint.getX();
		float endY = (float) (endPoint.getY());
		
		double start = arc.getAngleStart();
		double extent = Math.abs(arc.getAngleExtent());
		double end = start + arc.getAngleExtent();
	
		float cx = (float) arc.getCenterX();
		float cy = (float) arc.getCenterY();
		
		List<IndexedFace> faces = new ArrayList<IndexedFace>();
		
		// TODO: simplify structure
		if (start < -270.0) {
			Shape face = this.createInnerFace(depth, startX, startY, cx, cy);
			double delta = Math.abs(-270.0 - start);
			faces.add(new IndexedFace(index, face, delta, true));
		} else if (start < -90.0) {
			if (extent < (start + 270.0)) {
				Shape face = this.createInnerFace(depth, endX, endY, cx, cy);
				double delta = Math.abs(-270.0 - end);
				faces.add(new IndexedFace(index, face, delta, true));
			}
		} else if (start < 90.0) {
			// start face in right half
			Shape face = this.createInnerFace(depth, startX, startY, cx, cy);
			double delta = Math.abs(90.0 - start);
			faces.add(new IndexedFace(index, face, delta, true));
			if ((end < -90.0) && (end > -270.0)) {
				// end face in left half
				face = this.createInnerFace(depth, endX, endY, cx, cy);
				delta = Math.abs(-270.0 - end);
				faces.add(new IndexedFace(index, face, delta, true));
			}
		} else if (start < 270.0) {
			// start face in left half
			if ((end > 90.0) || (end < -90.0)) {
				// end face also in left half
				Shape face = this.createInnerFace(depth, endX, endY, cx, cy);
				double delta = (end > 90.0) ? Math.abs(90.0 - end) : Math.abs(-270.0 - end);
				faces.add(new IndexedFace(index, face, delta, true));
			}
		} else {
			Shape face = this.createInnerFace(depth, startX, startY, cx, cy);
			double delta = Math.abs(-270.0 - start);
			faces.add(new IndexedFace(index, face, delta, true));
			if (extent < (start - 90.0)) {
				face = this.createInnerFace(depth, endX, endY, cx, cy);
				delta = Math.abs(-270.0 - end);
				faces.add(new IndexedFace(index, face, delta, true));
			}
		}
		
		return faces;
	}

	/**
	 * TODO: API
	 * @param arc
	 * @param depth
	 * @return
	 */
	protected List<IndexedFace> getArcInnerFrontFaces(int index, Arc2D arc, float depth) {
		// TODO: add a few comments
		Point2D startPoint = arc.getStartPoint();
		Point2D endPoint = arc.getEndPoint();
	
		float startX = (float) startPoint.getX();
		float startY = (float) startPoint.getY();
		float endX = (float) endPoint.getX();
		float endY = (float) (endPoint.getY());
		
		double start = arc.getAngleStart();
		double end = start + arc.getAngleExtent();
	
		float cx = (float) arc.getCenterX();
		float cy = (float) arc.getCenterY();
		
		List<IndexedFace> faces = new ArrayList<IndexedFace>();
		
		// TODO: simplify cases
		if (start > 270.0) {
			// start face in right half
			if (end < 90.0) {
				// end face also in right half
				Shape face = this.createInnerFace(depth, endX, endY, cx, cy);
				double delta = Math.abs(90.0 - end);
				faces.add(new IndexedFace(index, face, delta, false));
			}
		} else if (start > 90.0) {
			// start face in left half
			Shape face = this.createInnerFace(depth, startX, startY, cx, cy);
			double delta = Math.abs(90.0 - start);
			faces.add(new IndexedFace(index, face, delta, false));
			if ((end < 90.0) && (end > -90.0)) {
				// end face in right half
				face = this.createInnerFace(depth, endX, endY, cx, cy);
				delta = Math.abs(90.0 - end);
				faces.add(new IndexedFace(index, face, delta, false));
			}
		} else if (start > -90.0) {
			// start face in right half
			if ((end < -270.0) || (end > -90.0)) {
				// end face in top right quadrant
				Shape face = this.createInnerFace(depth, endX, endY, cx, cy);
				double delta = (end < -270.0) ? Math.abs(-270.0 - end) : Math.abs(90.0 - end);
				faces.add(new IndexedFace(index, face, delta, false));
			}
		} else if (start > -270.0) {
			// start face in left half
			Shape face = this.createInnerFace(depth, startX, startY, cx, cy);
			double delta = Math.abs(-270.0 - start);
			faces.add(new IndexedFace(index, face, delta, false));
			if (end < -270.0) {
				// end face in right half
				face = this.createInnerFace(depth, endX, endY, cx, cy);
				delta = Math.abs(-270.0 - end);
				faces.add(new IndexedFace(index, face, delta, false));
			}
		} else {
			// ??
			Shape face = this.createInnerFace(depth, endX, endY, cx, cy);
			double delta = Math.abs(-270.0 - end);
			faces.add(new IndexedFace(index, face, delta, false));
		}
		
		return faces;
	}

	/**
	 * Creates an inner face shape of a pie segment using the provided center
	 * and target coordinates and extrusion height.
	 * @param depth the extrusion height
	 * @param x the target x coordinate
	 * @param y the target y coordinate
	 * @param cx the pie center x coordinate
	 * @param cy the pie center y coordinate
	 * @return an inner face
	 */
	private Shape createInnerFace(float depth, float x, float y, float cx, float cy) {
		GeneralPath path = new GeneralPath();
		path.moveTo(cx, cy - depth);
		path.lineTo(x, y - depth);
		path.lineTo(x, y);
		path.lineTo(cx, cy);
		path.closePath();
		
		return path;
	}

	/**
	 * Utility method to return front-facing mantle area of a pie segment.
	 * @param arc The pie segment's bottom arc shape.
	 * @param depth The pie's extrusion height.
	 * @return The front-facing mantle area.
	 */
	protected List<IndexedFace> getArcMantleFrontFaces(int index, Arc2D arc, float depth) {
		
		Point2D startPoint = arc.getStartPoint();
		Point2D endPoint = arc.getEndPoint();

		float startX = (float) startPoint.getX();
		float startY = (float) startPoint.getY();
		float endX = (float) endPoint.getX();
		float endY = (float) (endPoint.getY());
		
		double start = arc.getAngleStart();
		double extent = Math.abs(arc.getAngleExtent());
		double end = start + arc.getAngleExtent();

		float cx = (float) arc.getCenterX();
		float cy = (float) arc.getCenterY();
		float rx = (float) state.getPieWRadius();
		float ry = (float) state.getPieHRadius();
		
		List<IndexedFace> faces = new ArrayList<IndexedFace>();
		
		// TODO: simplify pathing by using path.append() instead of path.arcTo()
		if (start > 180.0) {
			// start face in bottom half
			if (extent > (start - 180.0)) {
				// crossing left boundary from the front
				GeneralPathExt path = new GeneralPathExt();
				path.moveTo(startX, startY - depth);
				path.arcTo(rx, ry, 0, false, true, cx - rx, cy - depth);
				path.lineTo(cx - rx, cy);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				double delta = (start > 270.0) ? 180.0 : 90.0 + (start - 180.0) / 2.0;
				faces.add(new IndexedFace(index, path, delta, false, true));
				if (extent > start) {
					// also crossing right boundary from the back
					path = new GeneralPathExt();
					path.moveTo(cx + rx, cy - depth);
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
					path.arcTo(rx, ry, 0, false, false, cx + rx, cy);
					path.closePath();
					delta = (end < -90.0) ? 180.0 : 90.0 - (end / 2.0);
					faces.add(new IndexedFace(index, path, delta, false, true));
				}
			} else {
				// arc ends before left boundary
				GeneralPathExt path = new GeneralPathExt();
				path.moveTo(startX, startY - depth);
				path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
				path.lineTo(endX, endY);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				double delta = (end < 270.0) ? 180.0 : Math.abs(90.0 - (start + arc.getAngleExtent() / 2.0));
				faces.add(new IndexedFace(index, path, delta, false, true));
			}
		} else if (start > 0.0) {
			// start face in top half
			if (extent > start) {
				// crossing right boundary from the back
				GeneralPathExt path = new GeneralPathExt();
				path.moveTo(cx + rx, cy - depth);
				double delta;
				if (extent > (start + 180.0)) {
					// also crossing left boundary from the front
					path.arcTo(rx, ry, 0, false, true, cx - rx, cy - depth);
					path.lineTo(cx - rx, cy);
					delta = 180.0;
				} else {
					// arc ends before left boundary
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
					delta = (end < -90.0) ? 180.0 : 90.0 - (end / 2.0);
				}
				path.arcTo(rx, ry, 0, false, false, cx + rx, cy);
				path.closePath();
				faces.add(new IndexedFace(index, path, delta, false, true));
			}
		} else if (start > -180.0) {
			// start face in bottom half
			if (end < -180.0) {
				// crossing left boundary from the front
				GeneralPathExt path = new GeneralPathExt();
				path.moveTo(startX, startY - depth);
				path.arcTo(rx, ry, 0, false, true, cx - rx, cy - depth);
				path.lineTo(cx - rx, cy);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				double delta = (start > -90) ? 180.0 : 90.0 + (start + 180.0) / 2.0;
				faces.add(new IndexedFace(index, path, delta, false, true));
				if (end < -360.0) {
					// also crossing right boundary from the back
					path = new GeneralPathExt();
					path.moveTo(cx + rx, cy - depth);
					path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
					path.lineTo(endX, endY);
					path.arcTo(rx, ry, 0, false, false, cx + rx, cy);
					path.closePath();
					delta = 90.0 - ((end + 360.0) / 2.0);
					faces.add(new IndexedFace(index, path, delta, false, true));
				}
			} else {
				// arc ends before left boundary
				GeneralPathExt path = new GeneralPathExt();
				path.moveTo(startX, startY - depth);
				path.arcTo(rx, ry, 0, false, true, endX, endY - depth);
				path.lineTo(endX, endY);
				path.arcTo(rx, ry, 0, false, false, startX, startY);
				path.closePath();
				double delta = ((start > -90.0) && (end < -90.0)) ? 180.0 :
					180.0 - Math.abs(-90.0 - (start + arc.getAngleExtent() / 2.0));
				faces.add(new IndexedFace(index, path, delta, false, true));
			}
		}
		
		return faces;
	}
	
	/**
	 * Returns the key of the pie section at the specified point.
	 * @param point the point
	 * @return the key of the section or <code>null</code> if the point is outside the pie's bounds
	 */
	public Comparable getSectionKeyForPoint(Point2D point) {
		if (state == null) {
			return null;
		}
		Rectangle2D explodedPieArea = state.getExplodedPieArea();
		Ellipse2D explodedEllipse = new Ellipse2D.Double(
				explodedPieArea.getX(),
				explodedPieArea.getY(),
				explodedPieArea.getWidth(),
				explodedPieArea.getHeight());
		if (!explodedEllipse.contains(point)) {
			return null;
		}
		
		Point2D center = new Point2D.Double(state.getPieCenterX(), state.getPieCenterY());
		Vector delta = new Vector(center.getX() - point.getX(), center.getY() - point.getY());
		double theta = (Math.atan2(-delta.getY(), delta.getX()) / Math.PI - 1.0) * 180.0;
		
		theta -= getStartAngle();
		
		if (theta < -360.0) {
			theta += 360.0;
		}
		
		PieDataset dataset = getDataset();
		double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
		double runningTotal = 0.0;
		for (Object key : dataset.getKeys()) {
			Number value = dataset.getValue((Comparable) key);
			if (value != null) {
				double direction = getDirection().getFactor();
				double arcStartAngle = (direction * (runningTotal * 360)) / totalValue;
				if (theta <= arcStartAngle) {
					double arcAngleExtent = direction * value.doubleValue() / totalValue * 360.0;
					double arcEndAngle = arcStartAngle + arcAngleExtent;
					if (theta >= arcEndAngle) {
						return (Comparable) key;
					}
				}
				runningTotal += value.doubleValue();
			}
		}
		
		return null;
	}

	@Override
	public void clearSectionPaints(boolean notify) {
		super.clearSectionPaints(notify);
		this.setSectionPaint("Unknown", Color.DARK_GRAY);
//		this.setSectionPaint("Unknown", Color.CYAN);
		this.setSectionPaint("Others", Color.LIGHT_GRAY);
	}
	
	/**
	 * {@inheritDoc}<p>
	 * Overridden to prevent line wrapping.
	 */
	@Override
	protected void drawLeftLabels(KeyedValues leftKeys, Graphics2D g2,
			Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth,
			PiePlotState state) {
		super.drawLeftLabels(leftKeys, g2, plotArea, linkArea, Float.MAX_VALUE, state);
	}
	
	/**
	 * {@inheritDoc}<p>
	 * Overridden to prevent line wrapping.
	 */
	@Override
	protected void drawRightLabels(KeyedValues keys, Graphics2D g2,
			Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth,
			PiePlotState state) {
		super.drawRightLabels(keys, g2, plotArea, linkArea, Float.MAX_VALUE, state);
	}
	
	/**
	 * Convenience wrapper class for a face shape associated with a category index.
	 * @author A. Behne
	 */
	private class IndexedFace implements Comparable<IndexedFace> {
		
		/**
		 * The category index.
		 */
		private int index;
		
		/**
		 * The wrapped face shape.
		 */
		private Shape shape;
		
		/**
		 * The angle delta of the face to the top of the pie.
		 */
		private double delta;

		/**
		 * Flag indicating whether the wrapped face is back-facing.
		 */
		private boolean backFace;
		
		/**
		 * Flag indicating whether the wrapped face is a mantle face.
		 */
		private boolean mantle;
		
		/**
		 * Constructs a face wrapper using the specified category index, face shape, angle delta and back-facing flag.
		 * @param index the category index
		 * @param shape the face shape
		 * @param delta the delta to the angle signifying the top of the pie in degrees
		 * @param backFace <code>true</code> if the face is back-facing, <code>false</code> otherwise
		 */
		private IndexedFace(int index, Shape shape, double delta, boolean backFace) {
			this(index, shape, delta, backFace, false);
		}

		/**
		 * Constructs a face wrapper using the specified category index, face shape, angle delta, back-facing flag and mantle flag.
		 * @param index the category index
		 * @param shape the face shape
		 * @param delta the delta to the angle signifying the top of the pie in degrees
		 * @param backFace <code>true</code> if the face is back-facing, <code>false</code> otherwise
		 * @param mantle <code>true</code> if the face is a mantle face, <code>false</code> otherwise
		 */
		public IndexedFace(int index, Shape shape, double delta,
				boolean backFace, boolean mantle) {
			this.index = index;
			this.shape = shape;
			this.delta = delta;
			this.backFace = backFace;
			this.mantle = mantle;
		}

		/**
		 * Returns the category index.
		 * @return the category index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Returns the face shape.
		 * @return the face shape
		 */
		public Shape getShape() {
			return shape;
		}

		/**
		 * Returns the back-facing flag.
		 * @return <code>true</code> if the face is back-facing, <code>false</code> otherwise
		 */
		public boolean isBackFace() {
			return backFace;
		}
		
		/**
		 * Returns the mantle flag.
		 * @return <code>true</code> if the face is a mantle face, <code>false</code> otherwise
		 */
		public boolean isMantleFace() {
			return this.mantle;
		}

		@Override
		public int compareTo(IndexedFace that) {
			// backfaces always after frontfaces
			if (this.backFace != that.backFace) {
				return (this.backFace) ? -1 : 1;
			}
			// faces closer to the top before lower ones
			return Double.compare(this.delta, that.delta);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IndexedFace) {
				IndexedFace that = (IndexedFace) obj;
				if (this.backFace != that.backFace) {
					return false;
				}
				return (this.delta == that.delta);
			}
			return false;
		}

	}
	
	/**
	 * Convenience extension to allow for pathing along elliptical arcs.
	 * @author A. Behne
	 */
	private class GeneralPathExt extends Path2D.Float {

		/**
		 * Adds an elliptical arc, defined by two radii, an angle from the
		 * x-axis, a flag to choose the large arc or not, a flag to indicate if
		 * we increase or decrease the angles and the final point of the arc.
		 * @param rx the x radius of the ellipse
		 * @param ry the y radius of the ellipse
		 * @param theta the angle from the x-axis of the current coordinate system
		 *  to the x-axis of the ellipse in degrees
		 * @param largeArcFlag the large arc flag. If true the arc spanning less than or
		 *  equal to 180 degrees is chosen, otherwise the arc spanning
		 *  greater than 180 degrees is chosen
		 * @param sweepFlag the sweep flag. If true the line joining center to arc
		 *  sweeps through decreasing angles otherwise it sweeps
		 *  through increasing angles
		 * @param x the absolute x coordinate of the final point of the arc
		 * @param y the absolute y coordinate of the final point of the arc
		 */
		public void arcTo(float rx, float ry, float theta, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
			// Ensure radii are valid
			if (rx == 0 || ry == 0) {
				this.lineTo(x, y);
				return;
			}
		
			// Get the current (x, y) coordinates of the path
			Point2D p2d = this.getCurrentPoint();
			float x0 = (float) p2d.getX();
			float y0 = (float) p2d.getY();
			// Compute the half distance between the current and the final point
			float dx2 = (x0 - x) / 2.0f;
			float dy2 = (y0 - y) / 2.0f;
			// Convert theta from degrees to radians
			theta = (float) Math.toRadians(theta % 360f);
		
			/* Step 1 : Compute (x1, y1) */
			float x1 = (float) (Math.cos(theta) * (double) dx2 + Math.sin(theta)
					* (double) dy2);
			float y1 = (float) (-Math.sin(theta) * (double) dx2 + Math.cos(theta)
					* (double) dy2);
			// Ensure radii are large enough
			rx = Math.abs(rx);
			ry = Math.abs(ry);
			float Prx = rx * rx;
			float Pry = ry * ry;
			float Px1 = x1 * x1;
			float Py1 = y1 * y1;
			double d = Px1 / Prx + Py1 / Pry;
			if (d > 1) {
				rx = Math.abs((float) (Math.sqrt(d) * (double) rx));
				ry = Math.abs((float) (Math.sqrt(d) * (double) ry));
				Prx = rx * rx;
				Pry = ry * ry;
			}
		
			/* Step 2 : Compute (cx1, cy1) */
			double sign = (largeArcFlag == sweepFlag) ? -1d : 1d;
			float coef = (float) (sign * Math.sqrt(
					((Prx * Pry) - (Prx * Py1) - (Pry * Px1))
					/ ((Prx * Py1) + (Pry * Px1))));
			float cx1 = coef * ((rx * y1) / ry);
			float cy1 = coef * -((ry * x1) / rx);
		
			/* Step 3 : Compute (cx, cy) from (cx1, cy1) */
			float sx2 = (x0 + x) / 2.0f;
			float sy2 = (y0 + y) / 2.0f;
			float cx = sx2
					+ (float) (Math.cos(theta) * (double) cx1 - Math.sin(theta)
							* (double) cy1);
			float cy = sy2
					+ (float) (Math.sin(theta) * (double) cx1 + Math.cos(theta)
							* (double) cy1);
		
			/* Step 4 : Compute the angleStart (theta1) and the angleExtent (dtheta) */
			float ux = (x1 - cx1) / rx;
			float uy = (y1 - cy1) / ry;
			float vx = (-x1 - cx1) / rx;
			float vy = (-y1 - cy1) / ry;
			float p, n;
			// Compute the angle start
		    n = (float) Math.sqrt((ux * ux) + (uy * uy));
			p = ux; // (1 * ux) + (0 * uy)
			sign = (uy < 0) ? -1d : 1d;
			float angleStart = (float) Math.toDegrees(sign * Math.acos(p / n));
			// Compute the angle extent
			n = (float) Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
			p = ux * vx + uy * vy;
			sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
			float angleExtent = (float) Math.toDegrees(sign * Math.acos(p / n));
			if (!sweepFlag && angleExtent > 0) {
				angleExtent -= 360f;
			} else if (sweepFlag && angleExtent < 0) {
				angleExtent += 360f;
			}
			angleExtent %= 360f;
			angleStart %= 360f;
		
			Arc2D.Float arc = new Arc2D.Float();
			arc.x = cx - rx;
			arc.y = cy - ry;
			arc.width = rx * 2.0f;
			arc.height = ry * 2.0f;
			arc.start = -angleStart;
			arc.extent = -angleExtent;
			this.append(arc, true);
		}
		
	}
	
}
