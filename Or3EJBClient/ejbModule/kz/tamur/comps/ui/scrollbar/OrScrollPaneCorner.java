package kz.tamur.comps.ui.scrollbar;

import javax.swing.*;

import java.awt.*;

import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrScrollPaneCorner extends JComponent {
    public OrScrollPaneCorner() {
        super();
        SwingUtils.setOrientation(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int vBorder = getComponentOrientation().isLeftToRight() ? 0 : getWidth() - 1;
        g.setColor(OrScrollBarStyle.scrollBg);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(OrScrollBarStyle.scrollBorder);
        g.drawLine(0, 0, getWidth() - 1, 0);
        g.drawLine(vBorder, 0, vBorder, getHeight() - 1);
    }
}
