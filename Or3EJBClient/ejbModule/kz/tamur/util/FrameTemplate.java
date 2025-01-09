package kz.tamur.util;

import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getLightSysColor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.OrGradientToolBar;

/**
 * User: vital
 * Date: 27.11.2004
 * Time: 13:12:55
 */
public class FrameTemplate extends JFrame {

    private OrGradientMenuBar currentMenuBar = new OrGradientMenuBar();
    /** Панель содержащая начинку фрейма*/
    private GradientPanel contentArea = new GradientPanel(new BorderLayout());
    private OrGradientToolBar currentToolBar = null;
    private OrMenuBar orMenuBar = new OrMenuBar(currentMenuBar, currentToolBar);
    public static final int STATE_DEF = 0;  
    public static final int STATE_FIND = 1;  
    protected JLabel stateFrame = createLabel("");
    
    public FrameTemplate() throws HeadlessException {
        /**
         * добавление слушателя для отслеживания позиции окна на мониторах
         */
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                kz.tamur.comps.Utils.isChangeScreen(e);
            }
        });
    	jbInit();
    }
    
    private void jbInit() {
        super.getContentPane().add(orMenuBar, BorderLayout.NORTH);
        super.getContentPane().add(contentArea, BorderLayout.CENTER);
        setSize(new Dimension(800, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public Container getContentPane() {
        return contentArea;
    }
    
    /**
     * Получить панель с начинкой с приемлимой, для установки градиентной заливки, типизацией.
     *
     * @return панель с начинкой
     */
    public GradientPanel getContentGradientPanel() {
        return contentArea;
    }

    public void setToolbar(OrGradientToolBar toolBar) {
        currentToolBar = toolBar;
        orMenuBar.setToolBar(currentToolBar);
    }
    
    public void dropToolBar() {
        orMenuBar.dropToolBar();
    }

    /**
     * Задать приоритетную, главную панель меню.
     * Эта панель не переопределяется во время работы системы.
     * 
     * @param menuBar
     *            панель меню
     */
    public void setPrimaryMenuBar(OrGradientMenuBar menuBar) {
        updateMenu(menuBar);
        orMenuBar.setPrimaryMenuBar(menuBar);
    }
    
    /**
     * Задать панель меню.
     * Эта панель может переопределятся, удаляться во время работы системы.
     * 
     * @param menuBar
     *            панель меню
     */    
    public void setJMenuBar(OrGradientMenuBar menuBar) {
        if (menuBar==null) {
            menuBar = new OrGradientMenuBar();
        }
        currentMenuBar = menuBar;
        updateMenu(menuBar);
        orMenuBar.setMenuBar(currentMenuBar);
    }

    public void setGradienToolBar(GradientColor gradient) {
        currentToolBar.setGradient(gradient);
    }

    public void setGradientMenuBar(GradientColor gradient) {
        currentMenuBar.setGradient(gradient);
    }

    private void updateMenu(OrGradientMenuBar menuBar) {
        menuBar.setBackground(getDarkShadowSysColor());
        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            Component c = menuBar.getComponent(i);
            ((JComponent)c).setOpaque(true);
            c.setBackground(getDarkShadowSysColor());
            c.setForeground(getLightSysColor());
        }
    }
    
    public OrMenuBar getOrMenuBar() {
        return orMenuBar;
    }
    
    public void setState(int state) {
    }

    public void setState(String state) {
    }
}
