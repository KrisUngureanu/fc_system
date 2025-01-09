/**
 * 
 */
package kz.tamur.or3.client.props.inspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import kz.tamur.comps.Constants;

public class CellBorder implements Border {
	
	private Color gridColor;
	private Insets insets = Constants.INSETS_1;
	
	public CellBorder(Color gridColor) {
		this.gridColor = gridColor;
	}

	public Insets getBorderInsets(Component c) {
		return insets;
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y,
			int width, int height) {
		
		Color color = g.getColor();
		g.setColor(gridColor);
		int x1 = x + width - 1;
		int y1 = y + height - 1;
		g.drawLine(x, y1, x1, y1);
		g.drawLine(x1, y1, x1, y);
		g.setColor(color);
	}
	
}