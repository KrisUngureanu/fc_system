package kz.tamur.util;

import javax.swing.*;
import java.awt.*;

/**
 * User: Vital
 * Date: 16.02.2005
 * Time: 11:39:08
 */
public class CreateSequencePanel extends JPanel {

    private JLabel label = kz.tamur.rt.Utils.createLabel(
            "Введите наименование последовательности");
    private JTextField text = kz.tamur.rt.Utils.createDesignerTextField();

    public CreateSequencePanel() {
        super(new GridBagLayout());
        setPreferredSize(new Dimension(400, 100));
        add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 10, 10, 0), 0, 0));
        text.setPreferredSize(new Dimension(150, 18));
        add(text, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
        text.requestFocusInWindow();
    }

    public String getSequenceName() {
        return text.getText();
    }

    public JTextField getText() {
        return text;
    }
}
