package kz.tamur.comps.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.JPanel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.GradientColor;

/**
 * The Class GradientPanel.
 * 
 * @author Sergey Lebedev
 */
public class GradientPanel extends JPanel {

    /** Начальный цвет градиента. */
    private Color startColor;

    /** конечный цвет градиента. */
    private Color endColor;

    /** Ориентация градиента. */
    private int orientation = 0;

    /** Цикличность градиента. */
    private boolean isCycle = true;

    /** позиция отсчёта градиента для начального цвета. */
    private int positionStartColor = 0;

    /** позиция отсчёта градиента для конечного цвета. */
    private int positionEndColor = 50;

    /** The is enable gradient. */
    private boolean isEnableGradient = true;

    private GradientColor gradient;

    /**
     * Instantiates a new or gradient panel.
     */
    public GradientPanel() {
        super();
    }

    /**
     * Instantiates a new or gradient panel.
     * 
     * @param layout
     *            the layout
     */
    public GradientPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Instantiates a new or gradient panel.
     * 
     * @param isDoubleBuffered
     *            the is double buffered
     */
    public GradientPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Instantiates a new or gradient panel.
     * 
     * @param layout
     *            the layout
     * @param isDoubleBuffered
     *            the is double buffered
     */
    public GradientPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isEnableGradient) {
            return;
        }
        if (startColor == null && endColor == null) {
            return;
        } else if (startColor == null) {
            setBackground(endColor);
            return;
        } else if (endColor == null) {
            setBackground(startColor);
            return;
        }

        final int height = getHeight();
        final int wigth = getWidth();
        final int startH = (int) (wigth / 100f * positionStartColor);
        final int endH = (int) (wigth / 100f * positionEndColor);
        final int startV = (int) (height / 100f * positionStartColor);
        final int endV = (int) (height / 100f * positionEndColor);
        /*
         * System.out.println(height + " " + wigth + " " + startH + " " + endH + " " + startV + " " + endV + " | "
         * + positionStartColor + " " + positionEndColor);
         */
        GradientPaint gp;
        switch (orientation) {
        case Constants.HORIZONTAL:
            gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
            break;
        case Constants.VERTICAL:
            gp = new GradientPaint(0, startV, startColor, 0, endV, endColor, isCycle);
            break;
        case Constants.DIAGONAL:
            gp = new GradientPaint(startH, height - startV, startColor, endH, height - endV, endColor, isCycle);
            break;
        case Constants.DIAGONAL2:
            gp = new GradientPaint(startH, startV, startColor, endH, endV, endColor, isCycle);
            break;
        default:
            gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Установить начальный цвет градиента.
     * 
     * @param startColor
     *            новый цвет
     */
    public void setStartColor(Color startColor) {
        this.startColor = (startColor == null) ? Color.WHITE : startColor;
    }

    /**
     * Установить gradient.
     * 
     * @param gradient
     *            the new gradient
     */
    public void setGradient(GradientColor gradient) {
        this.gradient = gradient;
        startColor = gradient.getStartColor();
        endColor = gradient.getEndColor();
        orientation = gradient.getOrientation();
        isCycle = gradient.isCycle();
        positionStartColor = gradient.getPositionStartColor();
        positionEndColor = gradient.getPositionEndColor();
        isEnableGradient = gradient.isEnabled();
        repaint();
    }

    /**
     * @return the gradient
     */
    public GradientColor getGradient() {
        return gradient;
    }

    /**
     * Установить transparent.
     * 
     * @param transparent
     *            the new transparent
     */
    public void setTransparent(int transparent) {
        if (transparent >= 0 && transparent <= 100) {
            repaint();
        }
    }

    /**
     * Установить конечный цвет градиента.
     * 
     * @param endColor
     *            новый цвет
     */
    public void setEndColor(Color endColor) {
        this.endColor = (endColor == null) ? kz.tamur.rt.Utils.getLightGraySysColor() : endColor;
    }

    /**
     * Установить ориентацию заливки копонента.
     * 
     * @param orientation
     *            the new orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Установить цикличность.
     * 
     * @param isCycle
     *            цикличность
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * Установить позицию начального цвета градиента.
     * 
     * @param positionStartColor
     *            позиция(в процентах от линии градиента)
     */
    public void setPositionStartColor(int positionStartColor) {
        this.positionStartColor = positionStartColor;
    }

    /**
     * Установить позицию конечного цвета градиента.
     * 
     * @param positionEndColor
     *            позиция(в процентах от линии градиента)
     */
    public void setPositionEndColor(int positionEndColor) {
        this.positionEndColor = positionEndColor;
    }

    /**
     * Задана цикличность?.
     * 
     * @return true, если циклить градиент
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * Получить позицию начального цвета градиента.
     * 
     * @return the position start color
     */
    public int getPositionStartColor() {
        return positionStartColor;
    }

    /**
     * Получить позицию конечного цвета градиента.
     * 
     * @return позиция конечного цвета градиента
     */
    public int getPositionEndColor() {
        return positionEndColor;
    }

    /**
     * Получить начальный цвет градиента.
     * 
     * @return начальный цвет градиента
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * Получить конечный цвет градиента.
     * 
     * @return конечный цвет градиента
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * Получить ориентацию заливки компонента.
     * 
     * @return ориентация заливки
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Проверяет, является ли enabled gradient.
     * 
     * @return true, если enabled gradient
     */
    public boolean isEnabledGradient() {
        return isEnableGradient;
    }

    /**
     * Установить enabled gradient.
     * 
     * @param isEnableGradient
     *            the new enabled gradient
     */
    public void setEnabledGradient(boolean isEnableGradient) {
        this.isEnableGradient = isEnableGradient;
    }

/*    public OrPanelUI getWebUI() {
        return (OrPanelUI) getUI();
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

    public int getShadeWidth() {
        return getWebUI().getShadeWidth();
    }

    public void setShadeWidth(int shadeWidth) {
        getWebUI().setShadeWidth(shadeWidth);
    }

    public boolean isDrawBackground() {
        return getWebUI().isDrawBackground();
    }

    public void setDrawBackground(boolean drawBackground) {
        getWebUI().setDrawBackground(drawBackground);
    }

    public boolean isWebColored() {
        return getWebUI().isWebColored();
    }

    public void setWebColored(boolean webColored) {
        getWebUI().setWebColored(webColored);
    }

    public boolean isDrawBottom() {
        return getWebUI().isDrawBottom();
    }

    public void setDrawBottom(boolean drawBottom) {
        getWebUI().setDrawBottom(drawBottom);
    }

    public boolean isDrawLeft() {
        return getWebUI().isDrawLeft();
    }

    public void setDrawLeft(boolean drawLeft) {
        getWebUI().setDrawLeft(drawLeft);
    }

    public boolean isDrawRight() {
        return getWebUI().isDrawRight();
    }

    public void setDrawRight(boolean drawRight) {
        getWebUI().setDrawRight(drawRight);
    }

    public boolean isDrawTop() {
        return getWebUI().isDrawTop();
    }

    public void setDrawTop(boolean drawTop) {
        getWebUI().setDrawTop(drawTop);
    }

    public void setDrawSides(boolean top, boolean left, boolean bottom, boolean right) {
        getWebUI().setDrawSides(top, left, bottom, right);
    }

    public void setDrawFocus(boolean drawFocus) {
        getWebUI().setDrawFocus(drawFocus);
    }

    public void setUndecorated(boolean undecorated) {
        getWebUI().setUndecorated(undecorated);
    }

    public int getRound() {
        return getWebUI().getRound();
    }

    public void setRound(int round) {
        getWebUI().setRound(round);
    }*/

    /**
     * Additional childs interaction methods
     */

    public void add(List<? extends Component> components, int index) {
        if (components != null) {
            for (int i = 0; i < components.size(); i++) {
                add(components.get(i), index + i);
            }
        }
    }

    public void add(List<? extends Component> components, String constraints) {
        if (components != null) {
            for (Component component : components) {
                add(component, constraints);
            }
        }
    }

    public void add(List<? extends Component> components) {
        if (components != null) {
            for (Component component : components) {
                add(component);
            }
        }
    }

    public void add(int index, Component... components) {
        if (components != null && components.length > 0) {
            for (int i = 0; i < components.length; i++) {
                add(components[i], index + i);
            }
        }
    }

    public void add(String constraints, Component... components) {
        if (components != null && components.length > 0) {
            for (Component component : components) {
                add(component, constraints);
            }
        }
    }

    public void add(Component... components) {
        if (components != null && components.length > 0) {
            for (Component component : components) {
                add(component);
            }
        }
    }
}
