package kz.tamur.guidesigner.users;

import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
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
public class CreateUserPanel extends JPanel implements ItemListener,
        DialogEventHandler {

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("CreateUserBig"));
    private JCheckBox folderCheck = Utils.createCheckBox("Папка", false);
    private JCheckBox adminCheck = Utils.createCheckBox("Администратор", false);
    private JLabel textLabel = Utils.createLabel("Имя");
    private JTextField textField = Utils.createDesignerTextField();
    private JLabel pdLabel = Utils.createLabel("Пароль");
    private JPasswordField pdField = Utils.createDesignerPasswordField();
    private JLabel pdLabel2 = Utils.createLabel("Подтверждение пароля");
    private JPasswordField pdField2 = Utils.createDesignerPasswordField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public CreateUserPanel() {
        super(new GridBagLayout());
        init();
    }

    private void init() {
        setOpaque(isOpaque);
        folderCheck.setOpaque(isOpaque);
        adminCheck.setOpaque(isOpaque);
        setPreferredSize(new Dimension(500, 200));
        add(imageLabel, new GridBagConstraints(0, 0, 1, 7, 0, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(textLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 5, 0), 0, 0));
        add(textField, new GridBagConstraints(2, 1, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
        add(pdLabel, new GridBagConstraints(2, 2, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(20, 5, 5, 0), 0, 0));
        add(pdField, new GridBagConstraints(2, 3, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
        add(pdLabel2, new GridBagConstraints(2, 4, 1, 1, 0, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 0), 0, 0));
        add(pdField2, new GridBagConstraints(2, 5, 2, 1, 1, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 20), 0, 0));
        add(folderCheck, new GridBagConstraints(2, 6, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 0, 0), 0, 0));
        folderCheck.addItemListener(this);
        add(adminCheck, new GridBagConstraints(3, 6, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 0, 0), 0, 0));
    }

    public boolean isFolder() {
        return folderCheck.isSelected();
    }

    public boolean isAdmin() {
        return adminCheck.isSelected();
    }

    public String getText() {
        final String text = Funcs.normalizeInput(textField.getText());
        return (text == null) ? "" : text;
    }

    public char[] getPD() {
        return Utils.getPD(pdField);
    }

    public char[] getPD2() {
        return Utils.getPD(pdField2);
    }

    public void itemStateChanged(ItemEvent e) {
        final boolean b = !(e.getStateChange() == ItemEvent.SELECTED);
        pdLabel.setEnabled(b);
        pdField.setEnabled(b);
        pdLabel2.setEnabled(b);
        pdField2.setEnabled(b);
    }

    public boolean checkConstraints(DesignerDialog dlg) {
        String msg = null;
        String userName = getText();

        if (userName == null || userName.length() == 0) {
            msg = "Имя пользователя не должно быть пустым";
        } else if (!isFolder() && !Arrays.equals(getPD(), getPD2())) {
            msg = "Пароль и подтверждение пароля не совпадают";
        } else if (!isFolder()) {
            PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode();
            if (pnode != null) {
                msg = pnode.getPolicyWrapper().verificationPassAndLogin(getPD(), userName,
                        isAdmin(), true);
            }
        }
        if (msg != null) {
            MessagesFactory.showMessageDialog(dlg,
                    MessagesFactory.ERROR_MESSAGE, msg);
            return false;
        }
        return true;
    }
}
