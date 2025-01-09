/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package kz.tamur.comps.ui.label;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import kz.tamur.comps.ui.OrLookAndFeel;
import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * 
 * @author Sergey Lebedev
 *
 */
public class OrLabelUI extends BasicLabelUI {
    private Insets margin = OrLabelStyle.margin;
    private Painter painter = OrLabelStyle.painter;
    private boolean drawShade = OrLabelStyle.drawShade;
    private Color shadeColor = OrLabelStyle.shadeColor;

    private JLabel label;
    private PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
        return new OrLabelUI();
    }

    public void installUI(final JComponent c) {
        super.installUI(c);

        // Запомнить компонент для которого применяется UI
        label = (JLabel) c;

        // Настройки по умолчанию
        SwingUtils.setOrientation(label);
        label.setBackground(OrLabelStyle.backgroundColor);

        // Updating border
        updateBorder();

        // Orientation change listener
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateBorder();
            }
        };
        label.addPropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);
    }

    public void uninstallUI(JComponent c) {
        label.removePropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);
        label = null;
        super.uninstallUI(c);
    }

    private void updateBorder() {
        // Actual margin
        boolean ltr = label.getComponentOrientation().isLeftToRight();
        Insets m = new Insets(margin.top, ltr ? margin.left : margin.right, margin.bottom, ltr ? margin.right : margin.left);

        // Applying border
        if (painter != null) {
            // Background insets
            Insets bi = painter.getMargin(label);
            label.setBorder(BorderFactory.createEmptyBorder(m.top + bi.top, m.left + bi.left, m.bottom + bi.bottom, m.right
                    + bi.right));
        } else {
            // Empty insets
            label.setBorder(BorderFactory.createEmptyBorder(m.top, m.left, m.bottom, m.right));
        }
    }

    public Insets getMargin() {
        return margin;
    }

    public void setMargin(Insets margin) {
        this.margin = margin;
        updateBorder();
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
        updateBorder();
    }

    public boolean isDrawShade() {
        return drawShade;
    }

    public void setDrawShade(boolean drawShade) {
        this.drawShade = drawShade;
    }

    public Color getShadeColor() {
        return shadeColor;
    }

    public void setShadeColor(Color shadeColor) {
        this.shadeColor = shadeColor;
    }

    public void paint(Graphics g, JComponent c) {
        // Use background painter instead of default UI graphics
        if (painter != null) {
            painter.paint((Graphics2D) g, SwingUtils.size(c), c);
        }

        super.paint(g, c);
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        if (l.isEnabled() && drawShade) {
            g.setColor(l.getForeground());
            paintShadowText(g, s, textX, textY);
        } else {
            super.paintEnabledText(l, g, s, textX, textY);
        }
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        if (l.isEnabled() && drawShade) {
            g.setColor(l.getBackground().darker());
            paintShadowText(g, s, textX, textY);
        } else {
            super.paintDisabledText(l, g, s, textX, textY);
        }
    }

    private void paintShadowText(Graphics g, String s, int textX, int textY) {
        g.translate(textX, textY);
        LafUtils.paintTextShadow((Graphics2D) g, s, shadeColor);
        g.translate(-textX, -textY);
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension ps = super.getPreferredSize(c);
        if (painter != null) {
            if (c.getLayout() != null) {
                ps = SwingUtils.max(ps, c.getLayout().preferredLayoutSize(c));
            }
            ps = SwingUtils.max(ps, painter.getPreferredSize(c));
        }
        return ps;
    }
}
