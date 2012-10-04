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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.Vector;
import org.jfree.ui.RectangleInsets;

public class PiePlot3DExt extends PiePlot3D {

	private PiePlotState state;

	public PiePlot3DExt(PieDataset dataset) {
		super(dataset);
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
		if (getShadowGenerator() != null) {
			dataImage = new BufferedImage((int) plotArea.getWidth(),
					(int) plotArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
			g2 = dataImage.createGraphics();
			g2.translate(-plotArea.getX(), -plotArea.getY());
			g2.setRenderingHints(savedG2.getRenderingHints());
			originalPlotArea = (Rectangle2D) plotArea.clone();
		}
		// adjust the plot area by the interior spacing value
		double gapPercent = getInteriorGap();
		double labelPercent = 0.0;
		if (getLabelGenerator() != null) {
			labelPercent = getLabelGap() + getMaximumLabelWidth();
		}
		double gapHorizontal = plotArea.getWidth() * (gapPercent
				+ labelPercent) * 2.0;
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

		state = initialise(g2, plotArea, this, null, info);

		// the link area defines the dog leg points for the linking lines to
		// the labels
		Rectangle2D linkAreaXX = new Rectangle2D.Double(linkX, linkY, linkW,
				linkH * (1 - getDepthFactor()));
		state.setLinkArea(linkAreaXX);

		// the explode area defines the max circle/ellipse for the exploded pie
		// sections.
		// it is defined by shrinking the linkArea by the linkMargin factor.
		double hh = linkW * getLabelLinkMargin();
		double vv = linkH * getLabelLinkMargin();
		Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0,
				linkY + vv / 2.0, linkW - hh, linkH - vv);

		state.setExplodedPieArea(explodeArea);

		// the pie area defines the circle/ellipse for regular pie sections.
		// it is defined by shrinking the explodeArea by the explodeMargin
		// factor.
		double maximumExplodePercent = getMaximumExplodePercent();
		double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);

		double h1 = explodeArea.getWidth() * percent;
		double v1 = explodeArea.getHeight() * percent;
		Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX()
				+ h1 / 2.0, explodeArea.getY() + v1 / 2.0,
				explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);

		// the link area defines the dog-leg point for the linking lines to
		// the labels
		int depth = (int) (pieArea.getHeight() * getDepthFactor());
		Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW,
				linkH - depth);
		state.setLinkArea(linkArea);

		state.setPieArea(pieArea);
		state.setPieCenterX(pieArea.getCenterX());
		state.setPieCenterY(pieArea.getCenterY() - depth / 2.0);
		state.setPieWRadius(pieArea.getWidth() / 2.0);
		state.setPieHRadius((pieArea.getHeight() - depth) / 2.0);

		// get the data source - return if null;
		PieDataset dataset = getDataset();
		if (DatasetUtilities.isEmptyOrNull(getDataset())) {
			drawNoDataMessage(g2, plotArea);
			g2.setClip(savedClip);
			drawOutline(g2, plotArea);
			return;
		}

		// if too any elements
		if (dataset.getKeys().size() > plotArea.getWidth()) {
			String text = localizationResources.getString("Too_many_elements");
			Font sfont = new Font("dialog", Font.BOLD, 10);
			g2.setFont(sfont);
			FontMetrics fm = g2.getFontMetrics(sfont);
			int stringWidth = fm.stringWidth(text);

			g2.drawString(text, (int) (plotArea.getX() + (plotArea.getWidth()
					- stringWidth) / 2), (int) (plotArea.getY()
							+ (plotArea.getHeight() / 2)));
			return;
		}
		// if we are drawing a perfect circle, we need to readjust the top left
		// coordinates of the drawing area for the arcs to arrive at this
		// effect.
		if (isCircular()) {
			double min = Math.min(plotArea.getWidth(),
					plotArea.getHeight()) / 2;
			plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min,
					plotArea.getCenterY() - min, 2 * min, 2 * min);
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
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				getForegroundAlpha()));

		double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
		double runningTotal = 0;
		if (depth < 0) {
			return;  // if depth is negative don't draw anything
		}

		List<Arc2D> arcList = new ArrayList<Arc2D>();
		Arc2D.Double arc;
		Paint paint;
		Paint outlinePaint;
		Stroke outlineStroke;

		Iterator iterator = sectionKeys.iterator();
		while (iterator.hasNext()) {

			Comparable currentKey = (Comparable) iterator.next();
			Number dataValue = dataset.getValue(currentKey);
			if (dataValue == null) {
				arcList.add(null);
				continue;
			}
			double value = dataValue.doubleValue();
			if (value <= 0) {
				arcList.add(null);
				continue;
			}
			double startAngle = getStartAngle();
			double direction = getDirection().getFactor();
			double arcStartAngle = startAngle + (direction * (runningTotal * 360))
					/ totalValue;
			double arcAngleExtent = direction * value / totalValue * 360.0;
			if (Math.abs(arcAngleExtent) > getMinimumArcAngleToDraw()) {

				double expPercent = getExplodePercent(currentKey);
				double centerAngle = Math.toRadians(arcStartAngle + arcAngleExtent / 2.0);
				double xOffset = expPercent * state.getPieWRadius() * Math.cos(centerAngle);
				double yOffset = expPercent * state.getPieHRadius() * Math.sin(centerAngle);
				//				double xOffset = 0.0;
				//				double yOffset = 0.0;

				arcList.add(new Arc2D.Double(arcX + xOffset, arcY - yOffset + depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						arcStartAngle, arcAngleExtent, Arc2D.PIE));
			} else {
				arcList.add(null);
			}
			runningTotal += value;
		}

		Shape oldClip = g2.getClip();

		// draw the bottom segments
		int categoryCount = arcList.size();
		for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
			arc = (Arc2D.Double) arcList.get(categoryIndex);
			if (arc == null) {
				continue;
			}
			Comparable key = getSectionKey(categoryIndex);
			paint = lookupSectionPaint(key);
			outlinePaint = lookupSectionOutlinePaint(key);
			outlineStroke = lookupSectionOutlineStroke(key);
			g2.setPaint(paint);
			g2.fill(arc);
			g2.setPaint(outlinePaint);
			g2.setStroke(outlineStroke);
			g2.draw(arc);
			g2.setPaint(paint);

//			Point2D p1 = arc.getStartPoint();
//
//			// draw the height at start edge
//			Path2D path = new Path2D.Double();
//			path.moveTo(arc.getCenterX(), arc.getCenterY());
//			path.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
//			path.lineTo(p1.getX(), p1.getY() - depth);
//			path.lineTo(p1.getX(), p1.getY());
//			path.closePath();
//			
//			g2.setPaint(Color.LIGHT_GRAY);
//			g2.fill(path);
//			g2.setPaint(outlinePaint);
//			g2.setStroke(outlineStroke);
//			g2.draw(path);
//
//			Point2D p2 = arc.getEndPoint();
//
//			// draw the height at end edge
//			path = new Path2D.Double();
//			path.moveTo(arc.getCenterX(), arc.getCenterY());
//			path.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
//			path.lineTo(p2.getX(), p2.getY() - depth);
//			path.lineTo(p2.getX(), p2.getY());
//			path.closePath();
//
//			g2.fill(path);
//			g2.setPaint(outlinePaint);
//			g2.setStroke(outlineStroke);
//			g2.draw(path);

		}

		// shade back faces
		g2.setPaint(Color.GRAY);
		iterator = arcList.iterator();
		while (iterator.hasNext()) {
			arc = (Arc2D.Double) iterator.next();
			if (arc != null) {
				Area extSeg = getExtrudedArcArea(arc, depth);
				extSeg.subtract(new Area(arc));
				g2.fill(extSeg);
			}
		}
		
		// cycle through once drawing only the sides at the back...
		int cat = 0;
		iterator = arcList.iterator();
		while (iterator.hasNext()) {
			Arc2D segment = (Arc2D) iterator.next();
			if (segment != null) {
				Comparable key = getSectionKey(cat);
				paint = lookupSectionPaint(key);
				outlinePaint = lookupSectionOutlinePaint(key);
				outlineStroke = lookupSectionOutlineStroke(key);
				
				Area extSeg = getExtrudedArcArea(segment, depth);
				extSeg.subtract(new Area(segment));

				g2.setPaint(paint);
				g2.fill(extSeg);
//				g2.setPaint(outlinePaint);
//				g2.setStroke(outlineStroke);
//				g2.draw(extSeg);
//				g2.setPaint(paint);
			}
			cat++;
		}
		
		cat = 0;

		// shade front faces
		g2.setPaint(Color.GRAY);
		Arc2D upperArc;
		iterator = arcList.iterator();
		while (iterator.hasNext()) {
			Arc2D segment = (Arc2D) iterator.next();
			if (segment != null) {
				Area extSeg = getExtrudedArcArea(segment, depth);
				upperArc = new Arc2D.Double(segment.getX(), segment.getY() - depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						segment.getAngleStart(), segment.getAngleExtent(), Arc2D.PIE);
				extSeg.subtract(new Area(upperArc));
				g2.fill(extSeg);
			}
		}

		// cycle through again drawing only the sides at the front...
		ArrayList<Arc2D> sortedArcList = new ArrayList<Arc2D>(arcList);
		Collections.sort(sortedArcList, new Comparator<Arc2D>() {
			@Override
			public int compare(Arc2D arcA, Arc2D arcB) {
				double distA = 1.0 - Math.sin(Math.toRadians(fixAngle(
						arcA.getAngleStart() + arcA.getAngleExtent() / 2.0)));
				double distB = 1.0 - Math.sin(Math.toRadians(fixAngle(
						arcB.getAngleStart() + arcB.getAngleExtent() / 2.0)));
				return (distA < distB) ? -1 : (distA > distB) ? 1 : 0;
			}
			private double fixAngle(double angle) {
				return (angle > 180.0) ? angle - 360.0 : (angle < -180.0) ? angle + 360.0 : angle;
			}
		});

		Paint shade = new Color(0, 0, 0, 4);
		Path2D path;
		iterator = sortedArcList.iterator();
		while (iterator.hasNext()) {
			Arc2D segment = (Arc2D) iterator.next();
			if (segment != null) {
				cat = arcList.indexOf(segment);
				Comparable key = getSectionKey(cat);
				paint = lookupSectionPaint(key);
				outlinePaint = lookupSectionOutlinePaint(key);
				outlineStroke = lookupSectionOutlineStroke(key);

				double centerX = segment.getCenterX();
				double centerY = segment.getCenterY();
				
				// draw the height at start edge
				Point2D p1 = segment.getStartPoint();

				path = new Path2D.Double();
				
				path.moveTo(centerX, centerY);
				path.lineTo(centerX, centerY - depth);
				path.lineTo(p1.getX(), p1.getY() - depth);
				path.lineTo(p1.getX(), p1.getY());
				path.closePath();
				
				g2.setPaint(shade);
				g2.fill(path);
				g2.setPaint(outlinePaint);
				g2.setStroke(outlineStroke);
//				g2.draw(path);
				
				// draw the height at end edge
				Point2D p2 = segment.getEndPoint();

				path = new Path2D.Double();
				path.moveTo(centerX, centerY);
				path.lineTo(centerX, centerY - depth);
				path.lineTo(p2.getX(), p2.getY() - depth);
				path.lineTo(p2.getX(), p2.getY());
				path.closePath();

				g2.setPaint(shade);
				g2.fill(path);
				g2.setPaint(outlinePaint);
				g2.setStroke(outlineStroke);
//				g2.draw(path);
				
				Area extSeg = getExtrudedArcArea(segment, depth);
				upperArc = new Arc2D.Double(segment.getX(), segment.getY() - depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						segment.getAngleStart(), segment.getAngleExtent(), Arc2D.PIE);
				extSeg.subtract(new Area(upperArc));

				paint = lookupSectionPaint(key, true);
				g2.setPaint(paint);
				g2.fill(extSeg);
				g2.setPaint(outlinePaint);
				g2.setStroke(outlineStroke);
				g2.draw(extSeg);
				
				g2.draw(new Line2D.Double(p1, new Point2D.Double(p1.getX(), p1.getY() - depth)));
				g2.draw(new Line2D.Double(p2, new Point2D.Double(p2.getX(), p2.getY() - depth)));
				g2.draw(new Line2D.Double(new Point2D.Double(centerX, centerY),
						new Point2D.Double(centerX, centerY - depth)));
			}
		}
		
		g2.setClip(oldClip);

		// draw the sections at the top of the pie (and set up tooltips)...
		for (int sectionIndex = 0; sectionIndex < categoryCount; sectionIndex++) {
			arc = (Arc2D.Double) arcList.get(sectionIndex);
			if (arc == null) {
				continue;
			}
			upperArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
					pieArea.getWidth(), pieArea.getHeight() - depth,
					arc.getAngleStart(), arc.getAngleExtent(), Arc2D.PIE);

			Comparable currentKey = (Comparable) sectionKeys.get(sectionIndex);
			paint = lookupSectionPaint(currentKey, true);
			outlinePaint = lookupSectionOutlinePaint(currentKey);
			outlineStroke = lookupSectionOutlineStroke(currentKey);
			g2.setPaint(paint);
			g2.fill(upperArc);
			g2.setStroke(outlineStroke);
			g2.setPaint(outlinePaint);
			g2.draw(upperArc);

			// add a tooltip for the section...
			if (info != null) {
				EntityCollection entities
				= info.getOwner().getEntityCollection();
				if (entities != null) {
					String tip = null;
					PieToolTipGenerator tipster = getToolTipGenerator();
					if (tipster != null) {
						// @mgs: using the method's return value was missing
						tip = tipster.generateToolTip(dataset, currentKey);
					}
					String url = null;
					if (getURLGenerator() != null) {
						url = getURLGenerator().generateURL(dataset, currentKey,
								getPieIndex());
					}
					PieSectionEntity entity = new PieSectionEntity(
							upperArc, dataset, getPieIndex(), sectionIndex,
							currentKey, tip, url);
					entities.add(entity);
				}
			}
		}

		List keys = dataset.getKeys();
		Rectangle2D adjustedPlotArea = new Rectangle2D.Double(
				originalPlotArea.getX(), originalPlotArea.getY(),
				originalPlotArea.getWidth(), originalPlotArea.getHeight()
				- depth);
		if (getSimpleLabels()) {
			drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea,
					linkArea, state);
		} else {
			drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea,
					state);
		}

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
		drawOutline(g2, originalPlotArea);

	}
	
	private Area getExtrudedArcArea(Arc2D arc, int depth) {
		
		Area extArc = new Area(arc);
		
		// add inner sides
		Path2D startFace = new Path2D.Double();
		startFace.moveTo(arc.getCenterX(), arc.getCenterY());
		startFace.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
		startFace.lineTo(arc.getStartPoint().getX(), arc.getStartPoint().getY() - depth);
		startFace.lineTo(arc.getStartPoint().getX(), arc.getStartPoint().getY());
		startFace.closePath();

		extArc.add(new Area(startFace));
		
		Path2D endFace = new Path2D.Double();
		endFace.moveTo(arc.getCenterX(), arc.getCenterY());
		endFace.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
		endFace.lineTo(arc.getEndPoint().getX(), arc.getEndPoint().getY() - depth);
		endFace.lineTo(arc.getEndPoint().getX(), arc.getEndPoint().getY());
		endFace.closePath();
		
		extArc.add(new Area(endFace));

		// fill in missing outer side space if necessary
		double angleStart = arc.getAngleStart();
		double angleEnd = angleStart + arc.getAngleExtent();
		double width = arc.getWidth() * 0.5;
		width -= Math.sqrt(width * width - depth * depth);
		if ((angleStart >= 0.0) && (angleEnd < 0.0)) {
			extArc.add(new Area(new Rectangle2D.Double(
					arc.getMaxX() - width, arc.getCenterY() - depth,
					width + 0.5, depth)));
		}
		if (((angleStart > -180.0) && (angleEnd <= -180.0)) ||
				((angleStart > 180.0) && (angleEnd <= 180.0))) {
			extArc.add(new Area(new Rectangle2D.Double(
					arc.getX() - 0.5, arc.getCenterY() - depth,
					width + 0.5, depth)));
		}
		
		// add top arc
		Arc2D topArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
				arc.getWidth(), arc.getHeight(), arc.getAngleStart(),
				arc.getAngleExtent(), Arc2D.PIE);
		extArc.add(new Area(topArc));
		
		return extArc;
	}
	
	public Comparable getSectionKeyForPoint(Point2D point) {
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
	
}
