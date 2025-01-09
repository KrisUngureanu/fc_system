package kz.tamur.guidesigner.boxes;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 17:06:00
 * To change this template use File | Settings | File Templates.
 */
public class CreateBoxPanel extends JPanel {

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("BoxNode"));
    private JCheckBox folderCheck = Utils.createCheckBox("Папка", false);
    private JLabel textLabel = Utils.createLabel("Имя");
    private JTextField textField = Utils.createDesignerTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public CreateBoxPanel() {
        super(new GridBagLayout());
        init();
    }

    private void init() {
        folderCheck.setOpaque(isOpaque);
        setOpaque(isOpaque);
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
        add(folderCheck, new GridBagConstraints(2, 2, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
    }

    public boolean isFolder() {
        return folderCheck.isSelected();
    }

    public String getText() {
        String text = "";
        if (!"".equals(textField.getText())) {
            text = Funcs.normalizeInput(textField.getText());
        }
        return text;
    }
}
