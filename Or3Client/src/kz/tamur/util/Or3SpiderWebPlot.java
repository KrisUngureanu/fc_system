package kz.tamur.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.TableOrder;

public class Or3SpiderWebPlot extends SpiderWebPlot {

	// put this many labels on each axis.
	private int ticks = DEFAULT_TICKS;
	private static final int DEFAULT_TICKS = 5;

	private NumberFormat format = NumberFormat.getInstance();
	// constant for creating perpendicular tick marks.
	private static final double PERPENDICULAR = 90;
	// the size of a tick mark, as a percentage of the entire line length.
	private static final double TICK_SCALE = 0.005;
	// the gap between the axis line and the numeric label itself.
	private int valueLabelGap = DEFAULT_GAP;
	private static final int DEFAULT_GAP = 2;
	// the threshold used for determining if something is "on" the axis
	private static final double THRESHOLD = 10;

    /** The maximum value we are plotting against on each category axis */
    private double maxValue;

    private Font tickFont;
    private transient Paint tickPaint;

	public Or3SpiderWebPlot(CategoryDataset dataset) {
		super(dataset);
        this.maxValue = DEFAULT_MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea, final double value, final int cat,
			final double startAngle, final double extent) {
		super.drawLabel(g2, plotArea, value, cat, startAngle, extent);

		final FontRenderContext frc = g2.getFontRenderContext();
		final double[] transformed = new double[2];
		final double[] transformer = new double[2];
		final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);

		double maxVal = getMaxValue();
		double tick = calculateTick(maxVal);
		
		for (double ti = 0; ti < maxVal; ti += tick) { // for each web grid

			final Point2D point1 = arc1.getEndPoint();

			final double deltaX = plotArea.getCenterX();
			final double deltaY = plotArea.getCenterY();
			double labelX = point1.getX() - deltaX;
			double labelY = point1.getY() - deltaY;

			final double scale = (ti / maxVal);
			final AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
			// for getting the tick mark start points.
			final AffineTransform pointTrans = AffineTransform.getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
			transformer[0] = labelX;
			transformer[1] = labelY;
			pointTrans.transform(transformer, 0, transformed, 0, 1);
			final double pointX = transformed[0] + deltaX;
			final double pointY = transformed[1] + deltaY;
			tx.transform(transformer, 0, transformed, 0, 1);
			labelX = transformed[0] + deltaX;
			labelY = transformed[1] + deltaY;

			double rotated = (PERPENDICULAR);

			AffineTransform rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);
			transformer[0] = pointX;
			transformer[1] = pointY;
			rotateTrans.transform(transformer, 0, transformed, 0, 1);
			final double x1 = transformed[0];
			final double y1 = transformed[1];

			rotated = (-PERPENDICULAR);
			rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);

			rotateTrans.transform(transformer, 0, transformed, 0, 1);

			final Composite saveComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

			g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));

			if (startAngle == this.getStartAngle()) {
				final String label = format.format(ti);

				final LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
				final double ascent = lm.getAscent();
		        Rectangle2D labelBounds = getLabelFont().getStringBounds(label, frc);

				// move based on quadrant.
				if (Math.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
					// on Y Axis, label to right.
					labelX -= labelBounds.getWidth() + valueLabelGap;
					// center vertically.
					labelY += ascent / (float) 3;
				} else if (Math.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
					// on X Axis, label underneath.
					labelY += valueLabelGap;
				} else if (labelX >= plotArea.getCenterX()) {
					if (labelY < plotArea.getCenterY()) {
						// quadrant 1
						labelX += valueLabelGap;
						labelY += valueLabelGap;
					} else {
						// quadrant 2
						labelX -= valueLabelGap;
						labelY += valueLabelGap;
					}
				} else {
					if (labelY > plotArea.getCenterY()) {
						// quadrant 3
						labelX -= valueLabelGap;
						labelY -= valueLabelGap;
					} else {
						// quadrant 4
						labelX += valueLabelGap;
						labelY -= valueLabelGap;
					}
				}
				g2.setPaint(getTickPaint());
				g2.setFont(getTickFont());
				g2.drawString(label, (float) labelX, (float) labelY);
			}
			g2.setComposite(saveComposite);
		}
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
		// changed code lines to implement web grid are not inlined:
		// adjust for insets...
		RectangleInsets insets = getInsets();
		insets.trim(area);

		if (info != null) {
			info.setPlotArea(area);
			info.setDataArea(area);
		}

		drawBackground(g2, area);
		drawOutline(g2, area);
		Shape savedClip = g2.getClip();
		g2.clip(area);
		Composite originalComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));

		if (!DatasetUtilities.isEmptyOrNull(getDataset())) {
			int seriesCount = 0, catCount = 0;
			if (getDataExtractOrder() == TableOrder.BY_ROW) {
				seriesCount = getDataset().getRowCount();
				catCount = getDataset().getColumnCount();
			} else {
				seriesCount = getDataset().getColumnCount();
				catCount = getDataset().getRowCount();
			}

			// ensure we have a maximum value to use on the axes
			if (getMaxValue() == DEFAULT_MAX_VALUE)
				calculateMaxValue(seriesCount, catCount);

			// Next, setup the plot area
			// adjust the plot area by the interior spacing value
			double gapHorizontal = area.getWidth() * getInteriorGap();
			double gapVertical = area.getHeight() * getInteriorGap();

			double X = area.getX() + gapHorizontal / 2;
			double Y = area.getY() + gapVertical / 2;
			double W = area.getWidth() - gapHorizontal;
			double H = area.getHeight() - gapVertical;

			double headW = area.getWidth() * this.headPercent;
			double headH = area.getHeight() * this.headPercent;

			// make the chart area a square
			double min = Math.min(W, H) / 2;
			X = (X + X + W) / 2 - min;
			Y = (Y + Y + H) / 2 - min;
			W = 2 * min;
			H = 2 * min;

			Point2D centre = new Point2D.Double(X + W / 2, Y + H / 2);
			Rectangle2D radarArea = new Rectangle2D.Double(X, Y, W, H);

			// draw the axis and category label
			for (int cat = 0; cat < catCount; cat++) { // for each category
				double angle = getStartAngle() + (getDirection().getFactor() * cat * 360 / catCount);
				Point2D endPoint = getWebPoint(radarArea, angle, 1); // 1 = end
																		// of
																		// axis
				Line2D line = new Line2D.Double(centre, endPoint);
				g2.setPaint(getAxisLinePaint());
				g2.setStroke(getAxisLineStroke());
				g2.draw(line);
				drawLabel(g2, radarArea, 0.0, cat, angle, 360.0 / catCount);

			} // next category

			drawRadarGrid(g2, radarArea, catCount); // <<<!!! call new method to draw web grid polygons

			// Now actually plot each of the series polygons:
			for (int series = 0; series < seriesCount; series++) {
				drawRadarPoly(g2, radarArea, centre, info, series, catCount, headH, headW);
			}
		} else {
			drawNoDataMessage(g2, area);
		}
		g2.setClip(savedClip);
		g2.setComposite(originalComposite);
		drawOutline(g2, area);
	}

	/**
	 * Draws a radar grid polygon.
	 *
	 * @param g2
	 *            the graphics device.
	 * @param plotArea
	 *            the area we are plotting in (already adjusted).
	 * @param catCount
	 *            the number of categories per radar plot
	 * @author mh
	 */
	protected void drawRadarGrid(Graphics2D g2, Rectangle2D plotArea, int catCount) {
		// code based on drawRadarPoly()!

		double maxVal = getMaxValue();
		
		double tick = calculateTick(maxVal);
		
		double dataValue = 0;
		for (double i = 0; i < maxVal; i += tick) { // for each web grid
														// polygon
			Polygon polygon = new Polygon();
			dataValue += tick;
			// plot the data...
			for (int cat = 0; cat < catCount; cat++) { // for each axis on
														// current web grid
				if (0.0 < dataValue) {
					double value = dataValue;
					if (value >= 0) { // draw the polygon series...
						// Finds our starting angle from the centre for this
						// axis
						double angle = getStartAngle() + (getDirection().getFactor() * cat * 360 / catCount);

						// find the point at the appropriate distance end point
						// along the axis/angle identified above and add it to
						// the polygon

						Point2D point = getWebPoint(plotArea, angle, value / getMaxValue());
						polygon.addPoint((int) point.getX(), (int) point.getY());
					}
				}
			}
			// Plot the polygon
			Paint paint = getTickPaint();
			g2.setPaint(paint);
			g2.setStroke(new BasicStroke(0.5f));
			g2.draw(polygon);
		} // next web grid
	}// drawRadarGrid()

    /**
     * loop through each of the series to get the maximum value
     * on each category axis
     *
     * @param seriesCount  the number of series
     * @param catCount  the number of categories
     */
    private void calculateMaxValue(int seriesCount, int catCount) {
        double v;
        Number nV;

        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            for (int catIndex = 0; catIndex < catCount; catIndex++) {
                nV = getPlotValue(seriesIndex, catIndex);
                if (nV != null) {
                    v = nV.doubleValue();
                    if (v > this.maxValue) {
                        this.maxValue = v;
                    }
                }
            }
        }
        
        super.setMaxValue(this.maxValue);
    }

    private double calculateTick(double maxValue) {
    	double tick = 0.01;
    	
    	while (maxValue/tick > 10) {
    		tick *= 10;
    	}
    	
    	if (maxValue/tick < 2) {
    		tick *= 5;
    	} else if (maxValue/tick < 5) {
    		tick *= 2;
    	}
    	return tick;
    }
    
    /**
	 * sets the number of tick marks on this spider chart.
	 * 
	 * @param ticks
	 *            the new number of tickmarks.
	 */
	public void setTicks(final int ticks) {
		this.ticks = ticks;
	}

	/**
	 * sets the numberformat for the tick labels on this spider chart.
	 * 
	 * @param format
	 *            the new number format object.
	 */
	public void setFormat(final NumberFormat format) {
		this.format = format;
	}
	
    /**
     * Returns the maximum value any category axis can take.
     *
     * @return The maximum value.
     *
     * @see #setMaxValue(double)
     */
    public double getMaxValue() {
        return super.getMaxValue();
    }

    /**
     * Sets the maximum value any category axis can take and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param value  the maximum value.
     *
     * @see #getMaxValue()
     */
    public void setMaxValue(double value) {
        this.maxValue = value;
        super.setMaxValue(value);
    }

    public Font getTickFont() {
        return this.tickFont;
    }

    public void setTickFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.tickFont = font;
        fireChangeEvent();
    }

    public Paint getTickPaint() {
        return this.tickPaint;
    }

    public void setTickPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickPaint = paint;
        fireChangeEvent();
    }
}
