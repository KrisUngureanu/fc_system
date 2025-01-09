package com.cifs.or2.client;

import kz.tamur.util.OrCellRenderer;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.FloatColumnAdapter;
import kz.tamur.rt.adapters.FloatFieldAdapter;
import kz.tamur.comps.OrFloatField;

import javax.swing.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.06.2004
 * Time: 19:49:16
 * To change this template use File | Settings | File Templates.
 */
public class FloatTableCellRenderer extends OrCellRenderer {

    private NumberFormat format = NumberFormat.getInstance();

/*    static {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setBounds(0, 0, 20, 16);
        chb.paint(img.createGraphics());
        falseIcon = new ImageIcon(img);

        img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setSelected(true);
        chb.paint(img.createGraphics());
        trueIcon = new ImageIcon(img);
    } */

    public FloatTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(true);
            format.setMinimumFractionDigits(1);
        }
    }

    public FloatTableCellRenderer(FloatColumnAdapter orc) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        FloatFieldAdapter f = orc.getEditor();
        OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)f.getFloatField().getFormatter();
        format = ft.getFmt();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final JLabel lb = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null && value instanceof Number) {
            lb.setText(format.format(value));
        } else {
            lb.setText("");
        }
        if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
            // непрозрачность для текста
            JLabel newComp = new JLabel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    // значение параметра прозрачности для текста
                    g.drawString(lb.getText(), 2, 10);

                }
            };
            newComp.setAlignmentX(lb.getAlignmentX());
            newComp.setAlignmentY(lb.getAlignmentY());
            newComp.setIcon(lb.getIcon());
            newComp.setFont(lb.getFont());
            newComp.setBackground(lb.getBackground());
            newComp.setForeground(lb.getForeground());
            newComp.setOpaque(true);
            newComp.setToolTipText(lb.getToolTipText());
            if (value instanceof String) {
                newComp.setToolTipText((String) value);
            }
            return newComp;
        }
        return lb;
    }
}
