package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: vital
 * Date: 11.03.2005
 * Time: 10:14:35
 */
public class ServiceNodeIndexPropPanel extends JPanel {

    private JLabel label = Utils.createLabel("Индекс ");
    private JTextField textField = Utils.createDesignerTextField();

    public ServiceNodeIndexPropPanel(String idx) {
        super(new GridBagLayout());
        add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 5), 0, 0));
        add(textField, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        setPreferredSize(new Dimension(300, 60));
        textField.setText(idx);
    }

    public int getIndex() {
        String s = textField.getText();
        if (s != null && !"".equals(s)) {
            return new Integer(s).intValue();
        } else {
            return 0;
        }
    }


}
