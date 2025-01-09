package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 30.05.2004
 * Time: 13:12:40
 * To change this template use File | Settings | File Templates.
 */
public class CopyTabPanel extends JPanel {

    private String text;

    private JLabel imageLbl = new JLabel(kz.tamur.rt.Utils.getImageIcon("CopyTab"));
    private JLabel textLbl = Utils.createLabel("Введите заголовок новой закладки");
    private JTextField textField = Utils.createDesignerTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public CopyTabPanel(String text) {
        super(new GridBagLayout());
        this.text = text;
        init();
    }

    private void init() {
        setOpaque(isOpaque);
        textField.setText(text);
        textField.setPreferredSize(new Dimension(350, 23));
        add(imageLbl, new GridBagConstraints(0, 0, 1, 2, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_2, 0, 0));
        add(textLbl, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_2, 0, 0));
        add(textField, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_2, 0, 0));
    }

    public String getText() {
        text = Funcs.normalizeInput(textField.getText());
        return text;
    }
}
