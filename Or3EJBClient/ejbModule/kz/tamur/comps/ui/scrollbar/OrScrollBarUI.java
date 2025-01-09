package kz.tamur.comps.ui.scrollbar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * Класс реализует полосу прокрутки для UI.
 * @author Sergey Lebedev
 * 
 */
public class OrScrollBarUI extends BasicScrollBarUI {
    public static final int LENGTH = 13;

    private Color scrollBg = OrScrollBarStyle.scrollBg;
    private Color scrollBorder = OrScrollBarStyle.scrollBorder;
    private Color scrollBarBorder = OrScrollBarStyle.scrollBarBorder;

    private Color scrollGradientLeft = OrScrollBarStyle.scrollGradientLeft;
    private Color scrollGradientRight = OrScrollBarStyle.scrollGradientRight;
    private Color scrollSelGradientLeft = OrScrollBarStyle.scrollSelGradientLeft;
    private Color scrollSelGradientRight = OrScrollBarStyle.scrollSelGradientRight;

    private int round = OrScrollBarStyle.rounding;
    private boolean drawBorder = OrScrollBarStyle.drawBorder;

    private MouseAdapter mouseAdapter;

    public static ComponentUI createUI(JComponent c) {
        return new OrScrollBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        // Default settings
        SwingUtils.setOrientation(scrollbar);
        scrollbar.setUnitIncrement(4);
        scrollbar.setUnitIncrement(16);

        mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                scrollbar.repaint();
            }
        };
        scrollbar.addMouseListener(mouseAdapter);
    }

    public void uninstallUI(JComponent c) {
        scrollbar.removeMouseListener(mouseAdapter);

        super.uninstallUI(c);
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        scrollbar.setOpaque(drawBorder);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Color getScrollBg() {
        return scrollBg;
    }

    public void setScrollBg(Color scrollBg) {
        this.scrollBg = scrollBg;
    }

    public Color getScrollBorder() {
        return scrollBorder;
    }

    public void setScrollBorder(Color scrollBorder) {
        this.scrollBorder = scrollBorder;
    }

    public Color getScrollBarBorder() {
        return scrollBarBorder;
    }

    public void setScrollBarBorder(Color scrollBarBorder) {
        this.scrollBarBorder = scrollBarBorder;
    }

    public Color getScrollGradientLeft() {
        return scrollGradientLeft;
    }

    public void setScrollGradientLeft(Color scrollGradientLeft) {
        this.scrollGradientLeft = scrollGradientLeft;
    }

    public Color getScrollGradientRight() {
        return scrollGradientRight;
    }

    public void setScrollGradientRight(Color scrollGradientRight) {
        this.scrollGradientRight = scrollGradientRight;
    }

    public Color getScrollSelGradientLeft() {
        return scrollSelGradientLeft;
    }

    public void setScrollSelGradientLeft(Color scrollSelGradientLeft) {
        this.scrollSelGradientLeft = scrollSelGradientLeft;
    }

    public Color getScrollSelGradientRight() {
        return scrollSelGradientRight;
    }

    public void setScrollSelGradientRight(Color scrollSelGradientRight) {
        this.scrollSelGradientRight = scrollSelGradientRight;
    }

    public void paint(Graphics g, JComponent c) {
        Object aa = LafUtils.disableAntialias(g);
        super.paint(g, c);
        LafUtils.restoreAntialias(g, aa);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        if (drawBorder) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(scrollBg);
            g2d.fillRect(0, 0, scrollbar.getWidth(), scrollbar.getHeight());

            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                int vBorder = scrollbar.getComponentOrientation().isLeftToRight() ? 0 : scrollbar.getWidth() - 1;
                g2d.setColor(scrollBorder);
                g2d.drawLine(vBorder, 0, vBorder, scrollbar.getHeight() - 1);
            } else {
                g2d.setColor(scrollBorder);
                g2d.drawLine(0, 0, scrollbar.getWidth(), 0);
            }
        }
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2d = (Graphics2D) g;

        Color leftC = isDragging ? scrollSelGradientLeft : scrollGradientLeft;
        Color rightC = isDragging ? scrollSelGradientRight : scrollGradientRight;

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            boolean ltr = scrollbar.getComponentOrientation().isLeftToRight();
            Color leftColor = ltr ? leftC : rightC;
            Color rightColor = ltr ? rightC : leftC;
            int x = ltr ? 2 : 1;

            g2d.setPaint(new GradientPaint(3, 0, leftColor, scrollbar.getWidth() - 4, 0, rightColor));
            g2d.fillRoundRect(thumbRect.x + x, thumbRect.y + 1, thumbRect.width - 4, thumbRect.height - 3, round, round);

            g2d.setPaint(scrollBarBorder);
            g2d.drawRoundRect(thumbRect.x + x, thumbRect.y + 1, thumbRect.width - 4, thumbRect.height - 3, round, round);
        } else {
            g2d.setPaint(new GradientPaint(0, thumbRect.y + 2, leftC, 0, thumbRect.y + 2 + thumbRect.height - 4, rightC));
            g2d.fillRoundRect(thumbRect.x + 1, thumbRect.y + 2, thumbRect.width - 3, thumbRect.height - 4, round, round);

            g2d.setPaint(scrollBarBorder);
            g2d.drawRoundRect(thumbRect.x + 1, thumbRect.y + 2, thumbRect.width - 3, thumbRect.height - 4, round, round);
        }
    }

    protected void installComponents() {
        incrButton = new JButton();
        incrButton.setPreferredSize(new Dimension(0, 0));
        decrButton = new JButton();
        decrButton.setPreferredSize(new Dimension(0, 0));
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension preferredSize = super.getPreferredSize(c);
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL) ? new Dimension(LENGTH, preferredSize.height) : new Dimension(
                preferredSize.width, LENGTH);
    }
}
