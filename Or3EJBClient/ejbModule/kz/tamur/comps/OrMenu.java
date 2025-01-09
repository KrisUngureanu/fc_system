package kz.tamur.comps;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

import javax.swing.Action;
import javax.swing.JMenu;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
/**
 * Класс меню
 * Реализует градиентную заливку
 */
public class OrMenu extends JMenu {
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
    
    /** The sub menu. */
    protected boolean subMenu = false;
    
    /** The is opaque. */
    protected boolean isOpaque = true;
    
    
    /**
     * Создание нового or menu.
     */
    public OrMenu() {
        super();
    }


    /**
     * Создание нового or menu.
     *
     * @param a the a
     */
    public OrMenu(Action a) {
        super(a);
    }


    /**
     * Создание нового or menu.
     *
     * @param s the s
     * @param b the b
     */
    public OrMenu(String s, boolean b) {
        super(s, b);
    }


    /**
     * Создание нового or menu.
     *
     * @param s the s
     */
    public OrMenu(String s) {
        super(s);
    }



    
    public void paintComponent(Graphics g) {
        if(!isOpaque && Constants.SE_UI) {
            if (isSelected()) {
                setOpaque(true);
                setGradient(MainFrame.GRADIENT_MENU_PANEL);
            }else {
                setOpaque(false); 
                super.paintComponent(g);
                return;
            }  
        }
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
        g2.setColor(Utils.getDarkShadowSysColor());
        g2.drawString(getText(), 7, 14);
        
        if (subMenu) {
            FontRenderContext frc = g2.getFontRenderContext();
            int textheight = (int) Utils.getDefaultFont().getStringBounds(getText(), frc).getWidth();
            g.drawImage(kz.tamur.rt.Utils.getImageIconFull("subMenu.png").getImage(), textheight+11, 6, null);
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
     * @param transparent the new transparent
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


    
    @Override
    public void setBackground(Color bg) {
        if (!Constants.SE_UI) {
            super.setBackground(bg);
        }
    }

}
