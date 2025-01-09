package kz.tamur.comps.ui.scrollbar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import kz.tamur.comps.ui.OrLookAndFeel;
import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.focus.FocusTracker;
import kz.tamur.comps.ui.ext.focus.FocusManager;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;


/**
 * Класс реализует панель с прокруткой для UI.
 * 
 * @author Lebedev Sergey
 * 
 */
public class OrScrollPaneUI extends BasicScrollPaneUI implements ShapeProvider, FocusTracker {
    private boolean drawBorder = OrScrollPaneStyle.drawBorder;
    private Color borderColor = OrScrollPaneStyle.borderColor;
    private Color darkBorder = OrScrollPaneStyle.darkBorder;

    private int round = OrScrollPaneStyle.round;
    private int shadeWidth = OrScrollPaneStyle.shadeWidth;
    private Insets margin = OrScrollPaneStyle.margin;

    private boolean drawFocus = OrScrollPaneStyle.drawFocus;
    private boolean drawBackground = OrScrollPaneStyle.drawBackground;

    private boolean focusOwner = false;

    private OrScrollPaneCorner corner;
    private PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
        return new OrScrollPaneUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        // Настройки по умолчанию
        SwingUtils.setOrientation(scrollpane);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
      //  scrollpane.setBackground(Color.WHITE);
    //    scrollpane.getViewport().setBackground(Color.WHITE);

        // Обновление рамки
        updateBorder(scrollpane);

        // Угол стилизованной полосы прокрутки
        scrollpane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, getCornerComponent());
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                scrollpane.setCorner(JScrollPane.LOWER_TRAILING_CORNER, getCornerComponent());
            }
        };
        scrollpane.addPropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);

        // Менеджер отслеживания фокуса для содержимого области прокрутки
        FocusManager.registerFocusTracker(OrScrollPaneUI.this);
    }

    public void uninstallUI(JComponent c) {
        scrollpane.removePropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);
        scrollpane.remove(getCornerComponent());

        FocusManager.unregisterFocusTracker(OrScrollPaneUI.this);

        super.uninstallUI(c);
    }

    private OrScrollPaneCorner getCornerComponent() {
        if (corner == null) {
            corner = new OrScrollPaneCorner();
        }
        return corner;
    }

    public Shape provideShape() {
        return LafUtils.getWebBorderShape(scrollpane, getShadeWidth(), getRound());
    }

    private void updateBorder(JComponent scrollPane) {
        if (scrollPane != null) {
            if (drawBorder) {
                scrollPane.setBorder(BorderFactory.createEmptyBorder(shadeWidth + 1 + margin.top, shadeWidth + 1 + margin.left,
                        shadeWidth + 1 + margin.bottom, shadeWidth + 1 + margin.right));
            } else {
                scrollPane.setBorder(BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right));
            }
        }
    }

    public boolean isTrackingEnabled() {
        return drawBorder && drawFocus;
    }

    public Component getTrackedComponent() {
        return scrollpane;
    }

    public boolean isUniteWithChilds() {
        return true;
    }

    public boolean isListenGlobalChange() {
        return false;
    }

    public void focusChanged(boolean focused) {
        focusOwner = focused;
        if (scrollpane != null) {
            scrollpane.repaint();
        }
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        updateBorder(scrollpane);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getShadeWidth() {
        return shadeWidth;
    }

    public void setShadeWidth(int shadeWidth) {
        this.shadeWidth = shadeWidth;
        updateBorder(scrollpane);
    }

    public Insets getMargin() {
        return margin;
    }

    public void setMargin(Insets margin) {
        this.margin = margin;
        updateBorder(scrollpane);
    }

    public boolean isDrawFocus() {
        return drawFocus;
    }

    public void setDrawFocus(boolean drawFocus) {
        this.drawFocus = drawFocus;
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getDarkBorder() {
        return darkBorder;
    }

    public void setDarkBorder(Color darkBorder) {
        this.darkBorder = darkBorder;
    }

    public void paint(Graphics g, JComponent c) {
        if (drawBorder) {
            // Border, background and shade
            LafUtils.drawWebStyle((Graphics2D) g, c, drawFocus && focusOwner ? StyleConstants.fieldFocusColor
                    : StyleConstants.shadeColor, shadeWidth, round, drawBackground, false);
        }

        super.paint(g, c);
    }
}
