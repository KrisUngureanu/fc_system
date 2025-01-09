package kz.tamur.guidesigner.bases;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 03.11.2004
 * Time: 10:49:39
 * To change this template use File | Settings | File Templates.
 */
public class CreateBasePanel extends JPanel {

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("CreateBaseBig"));
    private JLabel textLabel = kz.tamur.rt.Utils.createLabel("Наименование");
    private JTextField textField = kz.tamur.rt.Utils.createDesignerTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public CreateBasePanel() {
        super(new GridBagLayout());
        setOpaque(isOpaque);
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(500, 100));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 3, 0, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(textLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 5, 0), 0, 0));
        add(textField, new GridBagConstraints(2, 1, 1, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 0), 0, 0));
    }


    public String getText() {
        String text = "Безымянный";
        if (!"".equals(textField.getText())) {
            text = textField.getText();
        }
        return text;
    }
}
