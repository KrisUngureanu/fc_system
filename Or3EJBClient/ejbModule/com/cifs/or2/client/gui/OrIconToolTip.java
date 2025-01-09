/**
 * 
 */
package com.cifs.or2.client.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * Класс, реализующий всплывающую подсказку в виде картинки
 * @author Sergey Lebedev
 * 
 */
public class OrIconToolTip extends JToolTip {

    /**
     * Создание подсказки с иконкой
     */
    public OrIconToolTip(final ImageIcon icon) {
        setUI(new OrIconToolTipUI(icon));
    }

    /**
     * Создание подсказки без иконки
     */
    public OrIconToolTip() {
        this(null);
    }

    /**
     * Custom Implementation of MetalToolTipUI
     * 
     */
    private final class OrIconToolTipUI extends MetalToolTipUI {
        private Image tooltipIcon = null;

        /**
         * Default Constructor
         * 
         * @param tooltipIcon
         *            The Icon to display or NULL if there is no icon to display
         */
        public OrIconToolTipUI(final ImageIcon tooltipIcon) {
            if (tooltipIcon != null) {
                this.tooltipIcon = tooltipIcon.getImage();
            }
        }

        /**
         * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
         */
        public void paint(final Graphics g, final JComponent c) {
            String tipText = ((JToolTip) c).getTipText();

            if (tipText == null) {
                tipText = "";
            }

            g.setColor(c.getForeground());

            if (tooltipIcon != null) {
                g.drawImage(tooltipIcon, 3, 3, c);
                g.drawString(tipText, tooltipIcon.getWidth(c) + 6, 15);
            } else {
                g.drawString(tipText, 6, 15);
            }
        }

        /**
         * @see javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
         */
        public Dimension getPreferredSize(final JComponent c) {
            final FontMetrics metrics = c.getFontMetrics(c.getFont());
            String tipText = ((JToolTip) c).getTipText();

            if (tipText == null) {
                tipText = "";
            }

            final int width = 10 + SwingUtilities.computeStringWidth(metrics, tipText)
                    + (tooltipIcon == null ? 0 : tooltipIcon.getWidth(c));

            final int height = 6 + Math.max(metrics.getHeight(), tooltipIcon == null ? 0 : tooltipIcon.getHeight(c));

            return new Dimension(width, height);
        }
    }
}
