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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	private List<Arc2D> arcList;
	private double maximumExplodePercent = 0.0;
	private List<IndexedFace> faceList;

	public PiePlot3DExt(PieDataset dataset, double maximumExplodePercent) {
		super(dataset);
		setMaximumExplodePercent(maximumExplodePercent);
	}
	
	@Override
	public double getMaximumExplodePercent() {
		return maximumExplodePercent;
	}
	
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

		arcList = new ArrayList<Arc2D>(sectionKeys.size());
		faceList = new ArrayList<IndexedFace>(sectionKeys.size());
		Arc2D.Double arc;
		Paint paint;
		Paint outlinePaint;
		Stroke outlineStroke;

		int cat = 0;
		Iterator iterator = sectionKeys.iterator();
		while (iterator.hasNext()) {

			Comparable currentKey = (Comparable) iterator.next();
			
			lookupSectionPaint(currentKey);
			
			Number dataValue = dataset.getValue(currentKey);
			if (dataValue == null) {
				arcList.add(null);
				cat++;
				continue;
			}
			double value = dataValue.doubleValue();
			if (value <= 0.0) {
				arcList.add(null);
				cat++;
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

				arc = new Arc2D.Double(arcX + xOffset, arcY - yOffset + depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						arcStartAngle, arcAngleExtent, Arc2D.PIE);
				arcList.add(arc);

				faceList.add(new IndexedFace(cat, true, arc.getStartPoint().getX() > state.getPieCenterX()));
				faceList.add(new IndexedFace(cat, false, arc.getEndPoint().getX() < state.getPieCenterX()));
			} else {
				arcList.add(null);
			}
			runningTotal += value;
			cat++;
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
		}

		// sort segment side point list to be in back-to-front order
		Collections.sort(faceList);
		
		// paint faces
		Set<Integer> visitedIndices = new TreeSet<Integer>();	// paint mantle faces only once
		for (IndexedFace indexedFace : faceList) {
			int index = indexedFace.index;
			Arc2D segment = arcList.get(index);
			if (segment == null) {
				continue;
			}
			Comparable key = getSectionKey(index);
			paint = lookupSectionPaint(key);
			outlinePaint = lookupSectionOutlinePaint(key);
			outlineStroke = lookupSectionOutlineStroke(key);
			boolean visited = visitedIndices.contains(index);
			
			// shade and paint mantle back face
			if (!visited) {
				Area backMantle = getArcMantleBackArea(segment, depth);
				
				g2.setPaint(Color.GRAY);
				g2.fill(backMantle);

				g2.setPaint(paint);
				g2.fill(backMantle);
				g2.setStroke(outlineStroke);
				g2.setPaint(outlinePaint);
				g2.draw(backMantle);
			}

			// shade and paint inner face
			double centerX = segment.getCenterX();
			double centerY = segment.getCenterY();

			Path2D path = new Path2D.Double();

			path.moveTo(centerX, centerY);
			path.lineTo(centerX, centerY - depth);

			Point2D point = (indexedFace.startFace) ? segment.getStartPoint() : segment.getEndPoint();

			path.lineTo(point.getX(), point.getY() - depth);
			path.lineTo(point.getX(), point.getY());
			path.closePath();

			g2.setPaint(Color.GRAY);
			g2.fill(path);
			
			g2.setPaint(paint);
			g2.fill(path);
			g2.setStroke(outlineStroke);
			g2.setPaint(outlinePaint);
			g2.draw(path);
			
			// shade and paint mantle front face
			if (visited) {
				Area frontMantle = getArcMantleFrontArea(segment, depth);
				
				g2.setPaint(Color.GRAY);
				g2.fill(frontMantle);

				g2.setPaint(paint);
				g2.fill(frontMantle);
				g2.setStroke(outlineStroke);
				g2.setPaint(outlinePaint);
				g2.draw(frontMantle);
			}
			
			visitedIndices.add(index);
		}

		g2.setClip(oldClip);

		// draw the sections at the top of the pie (and set up tooltips)...
		for (int sectionIndex = 0; sectionIndex < categoryCount; sectionIndex++) {
			arc = (Arc2D.Double) arcList.get(sectionIndex);
			if (arc == null) {
				continue;
			}
			Arc2D upperArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
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
                originalPlotArea.getX(), originalPlotArea.getY() + 8.0,
                originalPlotArea.getWidth(), originalPlotArea.getHeight()
                - getShadowYOffset() - 8.0);
//		setInteriorGap(0.0);
		if (getSimpleLabels()) {
			drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea,
					linkArea, state);
		} else {
			drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea,
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
		drawOutline(g2, originalPlotArea);

	}

	/**
	 * Utility method to return front-facing mantle area of a pie segment.
	 * @param arc The pie segment's bottom arc shape.
	 * @param depth The pie's extrusion height.
	 * @return The front-facing mantle area.
	 */
	private Area getArcMantleFrontArea(Arc2D arc, double depth) {
		
		Area extArc = new Area(arc);
		
		// insert quadrilaterals masking gaps between top and bottom arcs
		double angleStart = fixAngle(arc.getAngleStart());
		double angleEnd = angleStart + arc.getAngleExtent();
		double centerY = arc.getCenterY();
		if (angleStart <= 0.0) {
			// start point is in lower half
			Point2D startPoint = arc.getStartPoint();
			Path2D path = new Path2D.Double();
			path.moveTo(startPoint.getX(), startPoint.getY());
			path.lineTo(startPoint.getX(), startPoint.getY() - depth);
			if (angleEnd < -180.0) {
				// end point reaches into upper half (and possibly beyond)
				path.lineTo(arc.getX(), centerY - depth);
				path.lineTo(arc.getX(), centerY);
			} else {
				// end point is also in lower half
				Point2D endPoint = arc.getEndPoint();
				path.lineTo(endPoint.getX(), endPoint.getY() - depth);
				path.lineTo(endPoint.getX(), endPoint.getY());
			}
			path.closePath();

			extArc.add(new Area(path));
		} else {
			// special case for concave segments
			// TODO: investigate ghosty stroke traces around +90deg
			if (angleEnd <= -180.0) {
				Path2D path = new Path2D.Double();
				path.moveTo(arc.getCenterX(), centerY);
				path.lineTo(arc.getCenterX(), centerY - depth);
				path.lineTo(arc.getMaxX(), centerY - depth);
				path.lineTo(arc.getMaxX(), centerY);
				path.closePath();

				extArc.add(new Area(path));
			}
		}
		double fixAngleEnd = fixAngle(angleEnd);
		if (fixAngleEnd < 0.0) {
			if ((fixAngleEnd - arc.getAngleExtent()) > 0.0) {
				// end point is in lower half while start point is in upper half
				Point2D startPoint = new Point2D.Double(arc.getMaxX(), centerY);
				Path2D path = new Path2D.Double();
				path.moveTo(startPoint.getX(), startPoint.getY());
				path.lineTo(startPoint.getX(), startPoint.getY() - depth);
				if (angleEnd < -180.0) {
					// end point reaches into upper half (and possibly beyond)
					path.lineTo(arc.getX(), centerY - depth);
					path.lineTo(arc.getX(), centerY);
				} else {
					// end point is also in lower half
					Point2D endPoint = arc.getEndPoint();
					path.lineTo(endPoint.getX(), endPoint.getY() - depth);
					path.lineTo(endPoint.getX(), endPoint.getY());
				}
				path.closePath();

				extArc.add(new Area(path));
			}
		} else {
			// special case for concave segments
			if ((fixAngleEnd - arc.getAngleExtent()) > 360.0) {
				Path2D path = new Path2D.Double();
				path.moveTo(arc.getX(), centerY);
				path.lineTo(arc.getX(), centerY - depth);
				path.lineTo(arc.getCenterX(), centerY - depth);
				path.lineTo(arc.getCenterX(), centerY);
				path.closePath();

				extArc.add(new Area(path));
			}
		}
		
		// subtract exposed inner sides
		angleEnd = fixAngleEnd;
		if ((angleEnd >= -90.0) && (angleEnd <= 90.0)) {
			double w = arc.getEndPoint().getX() - arc.getCenterX();
			double h;
			if (angleEnd > 0.0) {
				h = centerY - arc.getY();
			} else {
				h = arc.getHeight();
			}
			Rectangle2D rect = new Rectangle2D.Double(
					arc.getCenterX(), arc.getY(), w, h);
			extArc.subtract(new Area(rect));
		}
		double absAngleStart = Math.abs(angleStart);
		if ((absAngleStart >= 90.0) && (absAngleStart <= 180.0)) {
			double w = arc.getCenterX() - arc.getStartPoint().getX();
			double h;
			if (angleStart > 0.0) {
				h = centerY - arc.getY();
			} else {
				h = arc.getHeight();
			}
			Rectangle2D rect = new Rectangle2D.Double(
					arc.getStartPoint().getX(), arc.getY(), w, h);
			extArc.subtract(new Area(rect));
		}
		
		// subtract top arc
		Arc2D topArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
				arc.getWidth(), arc.getHeight(), arc.getAngleStart(),
				arc.getAngleExtent(), Arc2D.PIE);
		extArc.subtract(new Area(topArc));
		
		return extArc;
	}
	
	/**
	 * Utility method to return back-facing mantle area of a pie segment.
	 * @param arc The pie segment's bottom arc shape.
	 * @param depth The pie's extrusion height.
	 * @return The back-facing mantle area.
	 */
	private Area getArcMantleBackArea(Arc2D arc, double depth) {

		Arc2D topArc = new Arc2D.Double(arc.getX(), arc.getY() - depth,
				arc.getWidth(), arc.getHeight(), arc.getAngleStart(),
				arc.getAngleExtent(), Arc2D.PIE);
		Area extArc = new Area(topArc);
		
		// insert quadrilaterals masking gaps between top and bottom arcs
		double angleStart = fixAngle(arc.getAngleStart());
		double angleEnd = angleStart + arc.getAngleExtent();
		double fixAngleEnd = fixAngle(angleEnd);
		if (angleStart >= 0.0) {
			Point2D startPoint = arc.getStartPoint();
			Path2D path = new Path2D.Double();
			path.moveTo(startPoint.getX(), startPoint.getY());
			path.lineTo(startPoint.getX(), startPoint.getY() - depth);
			if (angleEnd < 0.0) {
				path.lineTo(arc.getMaxX(), arc.getCenterY() - depth);
				path.lineTo(arc.getMaxX(), arc.getCenterY());
			} else {
				Point2D endPoint = arc.getEndPoint();
				path.lineTo(endPoint.getX(), endPoint.getY() - depth);
				path.lineTo(endPoint.getX(), endPoint.getY());
			}
			path.closePath();

			extArc.add(new Area(path));
		} else {
			// special case for concave segments
			if (angleEnd <= -360.0) {
				Point2D startPoint = arc.getStartPoint();
				Path2D path = new Path2D.Double();
				path.moveTo(startPoint.getX(), startPoint.getY());
				path.lineTo(startPoint.getX(), startPoint.getY() - depth);
				path.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
				path.lineTo(arc.getCenterX(), arc.getCenterY());
				path.closePath();

				extArc.add(new Area(path));
			}
		}
		if (fixAngleEnd > 0.0) {
			if (angleStart < 0.0) {
				Point2D endPoint = arc.getEndPoint();
				Path2D path = new Path2D.Double();
				path.moveTo(endPoint.getX(), endPoint.getY());
				path.lineTo(endPoint.getX(), endPoint.getY() - depth);
				path.lineTo(arc.getX(), arc.getCenterY() - depth);
				path.lineTo(arc.getX(), arc.getCenterY());
				path.closePath();

				extArc.add(new Area(path));
			}
		} else {
			if (angleStart < 0.0) {
				Point2D endPoint = arc.getEndPoint();
				Path2D path = new Path2D.Double();
				path.moveTo(endPoint.getX(), endPoint.getY());
				path.lineTo(endPoint.getX(), endPoint.getY() - depth);
				path.lineTo(arc.getCenterX(), arc.getCenterY() - depth);
				path.lineTo(arc.getCenterX(), arc.getCenterY());
				path.closePath();

				extArc.add(new Area(path));
			}
		}
		
		// subtract exposed inner sides
		angleEnd = fixAngleEnd;
		double absAngleEnd = Math.abs(angleEnd);
		if ((absAngleEnd >= 90.0) && (absAngleEnd <= 180.0)) {
			double w = arc.getCenterX() - arc.getEndPoint().getX();
			double y;
			if (angleEnd > 0.0) {
				y = arc.getY() - depth;
			} else {
				y = arc.getCenterY() - depth;
			}
			Rectangle2D rect = new Rectangle2D.Double(
					arc.getEndPoint().getX(), y, w, arc.getHeight());;
			extArc.subtract(new Area(rect));
		}
		if ((angleStart >= -90.0) && (angleStart <= 90.0)) {
			double w = arc.getStartPoint().getX() - arc.getCenterX();
			double y;
			if (angleStart > 0.0) {
				y = arc.getY() - depth;
			} else {
				y = arc.getCenterY() - depth;
			}
			Rectangle2D rect = new Rectangle2D.Double(
					arc.getCenterX(), y, w, arc.getHeight());
			extArc.subtract(new Area(rect));
		}
		
		// subtract bottom arc
		extArc.subtract(new Area(arc));
		
		return extArc;
	}

	private double fixAngle(double angle) {
		return (angle > 180.0) ? fixAngle(angle - 360.0) :
					(angle < -180.0) ? fixAngle(angle + 360.0) : angle;
	}
	
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
	
	/**
	 * Utility class containing all necessary information to create an inner
	 * face of a pie segment.
	 */
	private class IndexedFace implements Comparable<IndexedFace> {
		/** The pie section index */
		private int index;
		/** Flag denoting whether this is a polygon at the start edge of the indexed arc. */
		private boolean startFace;
		/** Flag denoting whether this is a back-facing polygon. */
		private boolean backFace;

		/**
		 * Constructs an IndexedFace object.
		 * @param index the pie section index.
		 * @param startFace <code>true</code> if this face is at the start edge of the
		 *            indexed pie section, <code>false</code> otherwise.
		 * @param backFace <code>true</code> if this face is pointing backwards,
		 *            <code>false</code> otherwise.
		 */
		public IndexedFace(int index, boolean startFace, boolean backFace) {
			this.index = index;
			this.startFace = startFace;
			this.backFace = backFace;
		}

		@Override
		public int compareTo(IndexedFace that) {
			Point2D thisPoint = getNonExplodedPoint(this);
			Point2D thatPoint = getNonExplodedPoint(that);
			int delta = (int) thisPoint.getY() - (int) thatPoint.getY();
			// consider back-faces as greater than front-faces (will be drawn on top) 
			return (delta < 0) ? -1 : (delta > 0) ? 1 : (this.backFace) ? 1 : -1;
		}

		/**
		 * Utility method to extract the point on the pie's non-exploded
		 * circumference that corresponds with the specified IndexedFace.
		 * @param face the indexedFace
		 * @return the desired point
		 */
		public Point2D getNonExplodedPoint(IndexedFace face) {
			Arc2D arc = arcList.get(face.index);
			
			double expPercent = getExplodePercent(getSectionKey(face.index));
			if (expPercent > 0.0) {
				Rectangle2D pieArea = state.getPieArea();
				int depth = (int) (pieArea.getHeight() * getDepthFactor());
				arc = new Arc2D.Double(pieArea.getX(), pieArea.getY() + depth,
						pieArea.getWidth(), pieArea.getHeight() - depth,
						arc.getAngleStart(), arc.getAngleExtent(), Arc2D.PIE);
			}
			
			Point2D point = (face.startFace) ? arc.getStartPoint() : arc.getEndPoint();
			
			return point;
		}
	}
	
}
