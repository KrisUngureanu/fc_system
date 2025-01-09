package kz.tamur.guidesigner.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;


public class RoundedCornerBorder extends AbstractBorder {
	
	private Insets insets;
	private Color borderColor;
	private Color cornersColor;

	public RoundedCornerBorder(Insets insets, Color borderColor) {
		this.insets = insets;
		this.borderColor = borderColor;
		this.cornersColor = null;
	}
	
	public RoundedCornerBorder(Insets insets, Color borderColor, Color cornersColor) {
		this.insets = insets;
		this.borderColor = borderColor;
		this.cornersColor = cornersColor;
	}
	
	public RoundedCornerBorder(Color borderColor) {
		this.insets = new Insets(4, 4, 4, 4);
		this.borderColor = borderColor;
	}
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RoundRectangle2D round = new RoundRectangle2D.Double (x, y, width - 1, height - 1, 20, 20);
		Container parent = c.getParent();
		if (parent != null) {
			g2.setColor(cornersColor == null ? parent.getBackground() : cornersColor);
			Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
			corner.subtract(new Area(round));
			g2.fill(corner);
		}
		g2.setColor(borderColor);
		g2.draw(round);
		g2.dispose();
	}

	public Insets getBorderInsets(Component c) {
		return insets;
	}
	
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.right = 4;
		insets.top = insets.bottom = 4;
		return insets;
	}
}
