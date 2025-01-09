package kz.tamur.util.editor;

import kz.tamur.rt.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 28.06.2005
 * Time: 12:45:10
 * To change this template use File | Settings | File Templates.
 */
public class FontCombo extends JComboBox {
    String[] allFonts = new String[]{"Arial", "Arial Black", "Century Gothic", "Comic Sans MS", "Garamond",
                                     "Monotype Corsiva", "Tahoma", "Times New Roman", "Verdana"};
    public FontCombo() {
        setModel(new DefaultComboBoxModel(allFonts));
        setEditable(true);
        setFont(Utils.getDefaultFont());
        setRenderer(new FontComboRenderer());
    }

    public class FontComboRenderer implements ListCellRenderer {
        JLabel label = new JLabel();
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(value.toString());
            label.setFont(new Font(value.toString(), Font.PLAIN, 14));
            return label;
        }
    }

}
