package kz.tamur.comps.ui.scrollbar;

import javax.swing.*;
import java.awt.*;

import kz.tamur.comps.ui.ext.ShapeProvider;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrScrollPane extends JScrollPane implements ShapeProvider {
    private int preferredWidth = -1;
    private int minimumWidth = -1;
    private int preferredHeight = -1;
    private int minimumHeight = -1;
    
    public OrScrollPane() {
        this(null);
    }
    public OrScrollPane(Component view) {
        this(view, true);
    }

    public OrScrollPane(Component view, boolean drawBorder) {
        this(view, drawBorder, OrScrollBarStyle.drawBorder);
    }

    public OrScrollPane(Component view, boolean drawBorder, boolean drawInnerBorder) {
        super(view);
        setDrawBorder(drawBorder);
        getWebHorizontalScrollBar().setDrawBorder(drawInnerBorder);
        getWebVerticalScrollBar().setDrawBorder(drawInnerBorder);
        if (!drawInnerBorder) {
            setCorner(JScrollPane.LOWER_RIGHT_CORNER, null);
        }
    }

    public OrScrollBar createVerticalScrollBar() {
        return new OrScrollBar(OrScrollBar.VERTICAL);
    }

    public OrScrollBar createHorizontalScrollBar() {
        return new OrScrollBar(OrScrollBar.HORIZONTAL);
    }

    /**
     * Additional Web-component methods
     */

    public OrScrollBar getWebVerticalScrollBar() {
        return (OrScrollBar) super.getVerticalScrollBar();
    }

    public OrScrollBar getWebHorizontalScrollBar() {
        return (OrScrollBar) super.getHorizontalScrollBar();
    }

    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getMinimumWidth() {
        return minimumWidth;
    }

    public void setMinimumWidth(int minimumWidth) {
        this.minimumWidth = minimumWidth;
    }

    public int getPreferredHeight() {
        return preferredHeight;
    }

    public void setPreferredHeight(int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public int getMinimumHeight() {
        return minimumHeight;
    }

    public void setMinimumHeight(int minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    /**
     * UI methods
     */

    public boolean isDrawBorder() {
        return getWebUI().isDrawBorder();
    }

    public void setDrawBorder(boolean drawBorder) {
        getWebUI().setDrawBorder(drawBorder);
    }

    public int getRound() {
        return getWebUI().getRound();
    }

    public void setRound(int round) {
        getWebUI().setRound(round);
    }

    public int getShadeWidth() {
        return getWebUI().getShadeWidth();
    }

    public void setShadeWidth(int shadeWidth) {
        getWebUI().setShadeWidth(shadeWidth);
    }

    public Insets getMargin() {
        return getWebUI().getMargin();
    }

    public void setMargin(Insets margin) {
        getWebUI().setMargin(margin);
    }

    public void setMargin(int top, int left, int bottom, int right) {
        setMargin(new Insets(top, left, bottom, right));
    }

    public void setMargin(int spacing) {
        setMargin(spacing, spacing, spacing, spacing);
    }

    public boolean isDrawFocus() {
        return getWebUI().isDrawFocus();
    }

    public void setDrawFocus(boolean drawFocus) {
        getWebUI().setDrawFocus(drawFocus);
    }

    public boolean isDrawBackground() {
        return getWebUI().isDrawBackground();
    }

    public void setDrawBackground(boolean drawBackground) {
        getWebUI().setDrawBackground(drawBackground);
    }

    public Color getBorderColor() {
        return getWebUI().getBorderColor();
    }

    public void setBorderColor(Color borderColor) {
        getWebUI().setBorderColor(borderColor);
    }

    public Color getDarkBorder() {
        return getWebUI().getDarkBorder();
    }

    public void setDarkBorder(Color darkBorder) {
        getWebUI().setDarkBorder(darkBorder);
    }

    public Shape provideShape() {
        return getWebUI().provideShape();
    }

    public OrScrollPaneUI getWebUI() {
        return (OrScrollPaneUI) getUI();
    }

    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (getViewport() != null) {
            getViewport().setOpaque(isOpaque);
        }
    }

    public Dimension getPreferredSize() {
        Dimension ps = super.getPreferredSize();
        if (preferredWidth != -1) {
            ps.width = preferredWidth;
        } else if (minimumWidth != -1) {
            ps.width = Math.max(minimumWidth, ps.width);
        }
        if (preferredHeight != -1) {
            ps.height = preferredHeight;
        } else if (minimumHeight != -1) {
            ps.height = Math.max(minimumHeight, ps.height);
        }
        return ps;
    }
}
