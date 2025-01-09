package kz.tamur.util;

import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.BorderFactory.createEmptyBorder;
import static kz.tamur.comps.Constants.INSETS_0;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.OrGradientToolBar;
import kz.tamur.rt.Utils;

/**
 * User: vital
 * Date: 26.02.2005
 * Time: 11:22:58
 */
public class OrMenuBar extends GradientPanel {

    /** Панель главного меню. */
    private OrGradientMenuBar primaryMenuBar = new OrGradientMenuBar();

    /** Панель меню. */
    private OrGradientMenuBar menuBar;

    /** Панель инструментов. */
    private OrGradientToolBar toolBar;

    /** Логотип. */
    private JLabel imageLab;

    /**
     * Создание новой панели управления.
     * 
     * @param menuBar
     *            Панель меню
     * @param toolBar
     *            Панель инструментов
     */
    public OrMenuBar(OrGradientMenuBar menuBar, OrGradientToolBar toolBar) {
        super(new GridBagLayout());
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        init();
    }

    /**
     * Инициализация панели управления.
     */
    private void init() {
        removeAll();
        String path = Funcs.getSystemProperty("menuImage");
        boolean doubleLine = true;
        
        if (path != null && path.length() > 0) {
            imageLab = new JLabel(kz.tamur.rt.Utils.getImageIconFull(path));
        } else {
            imageLab = new JLabel(kz.tamur.rt.Utils.getImageIcon("dialog1"));
            doubleLine = false;
        }
        imageLab.setBorder(createEmptyBorder());

        setBackground(getDarkShadowSysColor());

        menuBar.setBorder(createEmptyBorder());
        primaryMenuBar.setBorder(createEmptyBorder());
        
        add(primaryMenuBar, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, HORIZONTAL, INSETS_0, 0, 0));
        add(menuBar, new GridBagConstraints(1, 0, 1, 1, 1, 0, WEST, HORIZONTAL, INSETS_0, 0, 0));

        if (toolBar != null) {
            add(toolBar, new GridBagConstraints(0, 1, doubleLine ? 3 : 4, 1, 1, 0, WEST, HORIZONTAL, INSETS_0, 0, 0));
        }

        add(imageLab, new GridBagConstraints(3, 0, 1, (doubleLine) ? 2 : 1, 0, 0, EAST, HORIZONTAL, INSETS_0, 0, 0));
    }

    /**
     * Установить menu bar.
     * 
     * @param menu
     *            the new menu bar
     */
    public void setMenuBar(OrGradientMenuBar menu) {
        if (menu != null) {
            remove(menuBar);
            menuBar = menu;
            init();
            validate();
            repaint();
        }
    }

    /**
     * Установить tool bar.
     * 
     * @param tool
     *            the new tool bar
     */
    public void setToolBar(OrGradientToolBar tool) {
        if (tool != null) {
            if (toolBar != null) {
                remove(toolBar);
            }
            toolBar = tool;
            init();
            validate();
            repaint();
        }
    }

    public void dropToolBar() {
        remove(toolBar);
        validate();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldClr = g.getColor();
        g.setColor(Utils.getMidSysColor());
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        g.setColor(oldClr);
    }

    public void setPrimaryMenuBar(OrGradientMenuBar menuBar) {
        if (menuBar != null) {
            remove(primaryMenuBar);
            primaryMenuBar = menuBar;
            init();
            validate();
            repaint();
        }
    }
}
