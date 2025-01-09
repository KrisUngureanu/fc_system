package kz.tamur.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import kz.tamur.comps.ui.checkBox.OrBasicCheckBox;
import kz.tamur.rt.MainFrame;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.06.2004
 * Time: 19:49:16
 * To change this template use File | Settings | File Templates.
 */
public class BooleanTableCellRenderer extends OrCellRenderer {
    private static Icon trueIcon;
    private static Icon falseIcon;

    static {
        OrBasicCheckBox chb = new OrBasicCheckBox();
        chb.setAnimate(false);
        chb.setOpaque(false);

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
    }

    public BooleanTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        final JLabel lb = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null && value instanceof Boolean) {
            lb.setIcon(((Boolean) value).booleanValue() ? trueIcon : falseIcon);
        } else {
            lb.setIcon(falseIcon);
        }
        lb.setText("");
        if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
         // непрозрачность для текста
            JLabel newComp = new JLabel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                }
            };
            newComp.setAlignmentX(lb.getAlignmentX());
            newComp.setAlignmentY(lb.getAlignmentY());
            newComp.setFont(lb.getFont());
            newComp.setBackground(lb.getBackground());
            newComp.setForeground(lb.getForeground());
            newComp.setIcon(lb.getIcon());
            newComp.setToolTipText(lb.getToolTipText());
            newComp.setOpaque(true);
            return newComp;
        }
        return lb;
    }
}
