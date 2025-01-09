package kz.tamur.comps;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.rt.Utils;

/**
 * Класс пунктов меню
 * Реализует градиентную заливку
 */
public class OrMenuItem extends JMenuItem {
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
    
    /** The position image x. */
    private int positionImageX = 3;
    
    /** The position image y. */
    private int positionImageY = 3;
    
    /** The position text x. */
    private int positionTextX;
    
    /** The position text y. */
    private int positionTextY;
    
    /**
     * Создание нового or menu item.
     */
    public OrMenuItem() {
        super();
    }

    /**
     * Создание нового or menu item.
     *
     * @param a the a
     */
    public OrMenuItem(Action a) {
        super(a);
    }

    /**
     * Создание нового or menu item.
     *
     * @param icon the icon
     */
    public OrMenuItem(Icon icon) {
        super(icon);
    }

    /**
     * Создание нового or menu item.
     *
     * @param text the text
     * @param icon the icon
     */
    public OrMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Создание нового or menu item.
     *
     * @param text the text
     * @param mnemonic the mnemonic
     */
    public OrMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
    }

    /**
     * Создание нового or menu item.
     *
     * @param text the text
     */
    public OrMenuItem(String text) {
        super(text);
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
        positionTextX = positionImageX;
        positionTextY =  11;
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (getIcon() != null) {
            g.drawImage(((ImageIcon) getIcon()).getImage(), positionImageX, positionImageY, null);
            positionTextX = positionTextX+getIcon().getIconWidth()+2;
            positionTextY = positionTextY+((int)getIcon().getIconHeight()/3);
        }
        g2.setFont(Utils.getDefaultFont());
        g2.setColor(Utils.getDarkShadowSysColor());
        g2.drawString(getText(), positionTextX, positionTextY);
        
        if(getAccelerator() != null) {
            FontRenderContext frc = g2.getFontRenderContext();
            int textheight = (int) Utils.getDefaultFont().getStringBounds(getText(), frc).getWidth();
            g2.setFont(new Font("Dialog", Font.PLAIN, 10));
            String hotKey = getAccelerator().toString().replaceAll("typed|released|pressed", "-");
            hotKey = Utils.toTitleCase(hotKey);
            g2.drawString(hotKey, positionTextX+textheight+11, positionTextY);
        }
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
