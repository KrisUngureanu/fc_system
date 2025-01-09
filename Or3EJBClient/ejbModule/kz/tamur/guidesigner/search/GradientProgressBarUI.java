package kz.tamur.guidesigner.search;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class GradientProgressBarUI extends BasicProgressBarUI {
	
	private Color backgroundColor;
	private Color foregroundColor;
	private Color gradientColor;
	
	
	public GradientProgressBarUI(Color backgroundColor, Color foregroundColor, Color gradientColor) {
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.gradientColor = gradientColor;
	}
	
    public void paint(Graphics graphics, JComponent component) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paint(graphics, component);
    }

    protected void paintDeterminate(Graphics graphics, JComponent component) {
        if (progressBar.getOrientation() == JProgressBar.VERTICAL) {
            super.paintDeterminate(graphics, component);
            return;
        }
        Insets insets = progressBar.getInsets();
        int width = progressBar.getWidth();
        int height = progressBar.getHeight();
        int barRectWidth = width - (insets.right + insets.left);
        int barRectHeight = height - (insets.top + insets.bottom);
        int arcSize = height / 2 - 1;
        int amountFull = getAmountFull(insets, barRectWidth, barRectHeight);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);        
        GradientPaint gradient = new GradientPaint(width / 2, 0, gradientColor, width / 2, height / 2, foregroundColor, false);
        g2.setPaint(gradient);
        g2.fillRoundRect(insets.left, insets.top, amountFull - 1, barRectHeight - 1, arcSize, arcSize);
        if (progressBar.isStringPainted()) {
            paintString(graphics, insets.left, insets.top, barRectWidth, barRectHeight, amountFull, insets);
        }
    }

    public Dimension getPreferredSize(JComponent component) {
        Dimension dimension = super.getPreferredSize(component);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            if (dimension.width < dimension.height * 2)
            	dimension.width = dimension.height * 2;
        }
        return dimension;
    }
}