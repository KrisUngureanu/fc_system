package kz.tamur.util;

import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 30.04.2005
 * Time: 18:04:11
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerListCellRenderer extends JLabel implements ListCellRenderer {

    private Color selColor = Utils.getMidSysColor();//new Color(255, 151, 151);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        setOpaque(true);
        setText(value.toString());
        setHorizontalTextPosition(JLabel.CENTER);
        setHorizontalAlignment(JLabel.CENTER);
        if (isSelected) {
            setBackground(selColor);
            Font f = getFont().deriveFont(Font.BOLD, 12);
            setFont(f);
        } else {
            setBackground(list.getBackground());
            Font f = Utils.getDefaultFont();
            setFont(f);
        }
        if (!list.isEnabled()) {
            setForeground(Color.gray);
        } else {
            setForeground(Color.black);
        }
        setOpaque(isSelected || isOpaque);
        return this;
    }

}
