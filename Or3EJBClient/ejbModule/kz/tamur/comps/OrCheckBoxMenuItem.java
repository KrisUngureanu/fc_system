package kz.tamur.comps;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.rt.Utils;
/**
 * Класс пунктов меню с установкой флага
 * Реализует градиентную заливку
 */
public class OrCheckBoxMenuItem extends JCheckBoxMenuItem {

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
    
    /** The position check x. */
    private int positionCheckX = 2;
    
    /** The position check y. */
    private int positionCheckY = 3;
    
    /** The position image x. */
    private int positionImageX = positionCheckX+18;
    
    /** The position image y. */
    private int positionImageY = 0;
    
    /** The position text x. */
    private int positionTextX;
    
    /** The position text y. */
    private int positionTextY = 14;
    
    /**
     * Создание нового or check box menu item.
     */
    public OrCheckBoxMenuItem() {
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param icon the icon
     */
    public OrCheckBoxMenuItem(Icon icon) {
        super(icon);
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param text the text
     */
    public OrCheckBoxMenuItem(String text) {
        super(text);
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param a the a
     */
    public OrCheckBoxMenuItem(Action a) {
        super(a);
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param text the text
     * @param icon the icon
     */
    public OrCheckBoxMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param text the text
     * @param b the b
     */
    public OrCheckBoxMenuItem(String text, boolean b) {
        super(text, b);
    }

    /**
     * Создание нового or check box menu item.
     *
     * @param text the text
     * @param icon the icon
     * @param b the b
     */
    public OrCheckBoxMenuItem(String text, Icon icon, boolean b) {
        super(text, icon, b);
    }
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(!Constants.SE_UI) {
            return;
        }
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
        g2.setFont(Utils.getDefaultFont());
        positionTextX = positionImageX+2;
        if (isSelected()) {
            g.drawImage(kz.tamur.rt.Utils.getImageIconFull("selectedCheck.png").getImage(), positionCheckX, positionCheckY, null);
        }else {
            g.drawImage(kz.tamur.rt.Utils.getImageIconFull("unSelectedCheck.png").getImage(), positionCheckX, positionCheckY, null);
        }
        
        if (getIcon() != null) {
            g.drawImage(((ImageIcon) getIcon()).getImage(), positionImageX, positionImageY, null);
            positionTextX = positionTextX+getIcon().getIconHeight();
        }
        g2.setColor(Utils.getDarkShadowSysColor());
        g2.drawString(getText(), positionTextX, positionTextY);
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
        this.endColor = (endColor == null) ? Utils.getLightGraySysColor() : endColor;
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
}
