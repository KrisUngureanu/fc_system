package kz.tamur.util;


import kz.tamur.rt.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: kazakbala
 * Date: 10.09.2004
 * Time: 16:03:08
 * To change this template use File | Settings | File Templates.
 */
public class ComboBoxMultiLine extends JLabel implements ListCellRenderer {
    FontMetrics fm;
    int h;
    int listWidth;
    public ComboBoxMultiLine(JComboBox cmb) {
        setFont(Utils.getDefaultFont());
        fm = getFontMetrics(getFont());
        h = fm.getHeight();
        //if (!isEditor) {
        listWidth = cmb.getWidth();
        //}
        setOpaque(true);
        setVerticalAlignment(SwingConstants.TOP);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String str = (value != null) ? value.toString() : "";
        String[] wStrs = Utils.wrap(str, fm, listWidth);
        StringBuffer sb = new StringBuffer("<html>" + wStrs[0]);
        for (int i = 1; i < wStrs.length; i++) {
            sb.append(wStrs[i]);
        }
        sb.append("</html>");
        setText(sb.toString());
        JDialog p = new JDialog();
        p.getContentPane().add(new JLabel(sb.toString()));
        p.pack();
        int w = p.getWidth();
        int width = list.getWidth();
        int hs = 0;
        if (w > width && width != 0) {
            hs = Math.abs(w/width);
            if (hs > 0)
                hs++;
        }
        setPreferredSize(new Dimension(listWidth, 50));
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }

    public void paint(Graphics g) {
        g.translate(0, -1);
        super.paint(g);
    }
}
