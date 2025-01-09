package kz.tamur.comps.ui.ext.utils;

import javax.swing.*;

import java.awt.*;

import kz.tamur.comps.Constants;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class DebugUtils {
    /**
     * Component painting speed debug info
     */

    public static void initDebugInfo() {
        if (Constants.DEBUG) {
            TimeUtils.pinNanoTime();
        }
    }

    public static void paintDebugInfo(Graphics g, JComponent c) {
        if (Constants.DEBUG) {
            paintDebugInfoImpl((Graphics2D) g, c);
        }
    }

    public static void paintDebugInfo(Graphics2D g2d, JComponent c) {
        if (Constants.DEBUG) {
            paintDebugInfoImpl(g2d, c);
        }
    }

    private static void paintDebugInfoImpl(Graphics2D g2d, JComponent c) {
        double ms = TimeUtils.getPassedNanoTime() / 1000000f;
        String micro = "" + Constants.DEBUG_FORMAT.format(ms);
        Rectangle cb = g2d.getClip().getBounds();
        Font font = g2d.getFont();

        g2d.setFont(Constants.DEBUG_FONT);
        Object aa = LafUtils.setupAntialias(g2d);

        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(micro) + 4;
        int h = fm.getHeight();

        g2d.setPaint(Color.BLACK);
        g2d.fillRect(cb.x + cb.width - w, cb.y, w, h);

        g2d.setPaint(Color.WHITE);
        g2d.drawString(micro, cb.x + cb.width - w + 2, cb.y + h - fm.getDescent());

        LafUtils.restoreAntialias(g2d, aa);
        g2d.setFont(font);
    }

    /**
     * Component painting speed debug info
     */

    public static void paintBorderDebugInfo(Graphics graphics, JComponent c) {
        paintBorderDebugInfo(graphics, c, Color.RED);
    }

    public static void paintBorderDebugInfo(Graphics graphics, JComponent c, Color color) {
        Insets margin = c.getInsets();
        graphics.setColor(color);
        graphics.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
        graphics.drawRect(margin.left, margin.top, c.getWidth() - margin.left - margin.right - 1, c.getHeight() - margin.top
                - margin.bottom - 1);
    }
}