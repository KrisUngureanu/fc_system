package kz.tamur.guidesigner;

import kz.tamur.comps.*;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;
import kz.tamur.admin.ClassBrowser;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.BOTH;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 07.05.2004
 * Time: 9:49:37
 * To change this template use File | Settings | File Templates.
 */
public class SearchDialog extends JDialog implements ActionListener {

    private JButton findBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_FIND);
    private JButton cancelBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
    private JButton clearBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CLEAR);
    private JPanel buttonsPanel = new JPanel(new GridBagLayout());
    private int dialogResult = ButtonsFactory.BUTTON_CANCEL;
    private Component content;
    /** Пустая метка, необходима как контейнер для спец. картинок */
    private JLabel image = new JLabel();
    private GradientPanel contentPane;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public SearchDialog(Frame owner, String title, Component content) {
        super(owner, title, true);
        this.content = content;
        contentPane = new GradientPanel();
        contentPane.setGradient(MainFrame.GRADIENT_MAIN_FRAME.isEmpty() ? Constants.GLOBAL_DEF_GRADIENT
                : MainFrame.GRADIENT_MAIN_FRAME);
        // Поместить основную панель
        super.getContentPane().add(contentPane, BorderLayout.CENTER);
        if (this.content instanceof SearchPanel) {
            ((SearchPanel) this.content).setParentDialog(this);
            image = ((SearchPanel) this.content).getImageLab();
        }
        init();
    }

    void init() {
        getRootPane().setDefaultButton(findBtn);
        cancelBtn.addActionListener(this);
        buttonsPanel.add(clearBtn, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_4, 0, 0));
        buttonsPanel.add(findBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_4, 0, 0));
        buttonsPanel.add(cancelBtn, new GridBagConstraints(0, 2, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_4, 0, 0));
        buttonsPanel.add(image, new GridBagConstraints(0, 3, 1, 1, 0, 2, CENTER, BOTH, Constants.INSETS_0, 0, 0));
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        cont.add(content, BorderLayout.CENTER);
        cont.add(buttonsPanel, BorderLayout.EAST);
        pack();
        setLocation(Utils.getCenterLocationPoint(getSize()));
        updateDefaultButton();
        buttonsPanel.setOpaque(isOpaque);
        findBtn.setOpaque(isOpaque);
        cancelBtn.setOpaque(isOpaque);
        clearBtn.setOpaque(isOpaque);
        if (content instanceof JComponent) {
            ((JComponent) content).setOpaque(isOpaque);
        }
    }

    public void show() {
        if (content instanceof ClassBrowser) {
            ((ClassBrowser) content).setSplitLocation();
        }
        super.show();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelBtn) {
            dialogResult = ButtonsFactory.BUTTON_CANCEL;
            dispose();
        }
    }

    public int getResult() {
        return dialogResult;
    }

    private void updateDefaultButton() {
        JButton btn = getRootPane().getDefaultButton();
        btn.setText(Funcs.underline(btn.getText()));
    }

    public JButton getFindBtn() {
        return findBtn;
    }

    public JButton getClearBtn() {
        return clearBtn;
    }

    public JLabel getImage() {
        return image;
    }

    public void setImage(JLabel image) {
        this.image = image;
    }

    public Container getContentPane() {
        return contentPane;
    }
}
