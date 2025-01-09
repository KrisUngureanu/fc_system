package kz.tamur.guidesigner.config;

import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DialogEventHandler;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: Vital Date: 03.11.2004 Time: 10:49:39 To
 * change this template use File | Settings | File Templates.
 */
public class CreateConfigurationPanel extends JPanel implements DialogEventHandler {

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("CreateUserBig"));
    
    private JLabel nameLabel = Utils.createLabel("Наименование конфигурации");
    private JTextField nameField = Utils.createDesignerTextField();
    private JLabel nameDsLabel = Utils.createLabel("Уникальное наименование конфигурации");
    private JTextField nameDsField = Utils.createDesignerTextField();
    private JLabel schemeLabel = Utils.createLabel("Наименование схемы БД");
    private JTextField schemeField = Utils.createDesignerTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public CreateConfigurationPanel() {
        super(new GridBagLayout());
        init();
    }

    private void init() {
        setOpaque(isOpaque);
        setPreferredSize(new Dimension(500, 200));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 7, 0, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(nameLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 5, 0), 0, 0));
        add(nameField, new GridBagConstraints(2, 1, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
        add(nameDsLabel, new GridBagConstraints(2, 2, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 5, 0), 0, 0));
        add(nameDsField, new GridBagConstraints(2, 3, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
        add(schemeLabel, new GridBagConstraints(2, 4, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 0), 0, 0));
        add(schemeField, new GridBagConstraints(2, 5, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
    }

    public String getName() {
        final String text = nameField.getText();
        return text;
    }

    public String getDsName() {
        final String text = nameDsField.getText();
        return text;
    }

    public String getSchemeName() {
        final String text = schemeField.getText();
        return text;
    }

    public boolean checkConstraints(DesignerDialog dlg) {
        String msg = null;
        String userName = getName();
        String dsName = getDsName();
        String schemeName = getSchemeName();

        if (userName == null || userName.length() == 0) {
            msg = "Наименование конфигурации не должно быть пустым";
        } else if (dsName == null || dsName.length() == 0) {
            msg = "Уникальное наименование конфигурации не должно быть пустым";
        } else if (schemeName == null || schemeName.length() == 0) {
            msg = "Наименование схемы БД не должно быть пустым";
        }
        if (msg != null) {
            MessagesFactory.showMessageDialog(dlg,
                    MessagesFactory.ERROR_MESSAGE, msg);
            return false;
        }
        return true;
    }
}
