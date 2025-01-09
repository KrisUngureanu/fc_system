package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.cifs.or2.client.Kernel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: vital
 * Date: 19.02.2005
 * Time: 11:57:49
 */
public class ServiceFolderPropertyPanel extends JPanel implements ActionListener {

    private boolean isInternal = false;
    private JCheckBox check = Utils.createCheckBox("  Является закладкой", false);
    private JLabel label = Utils.createLabel("Наименование закладки");
    private JTextField text = Utils.createDesignerTextField();
    private JLabel idxLabel = Utils.createLabel("Индекс");
    private JTextField textIndex = Utils.createDesignerTextField();
    static boolean isOpaque = true;
    static {
        if (Kernel.instance().getUser()!=null) {
            isOpaque = !MainFrame.TRANSPARENT_DIALOG;
        }
    }
    
    
    public ServiceFolderPropertyPanel(boolean isInternal) {
        super(new GridBagLayout());
        this.isInternal = isInternal;
        init();
    }

    private void init() {
        setOpaque(isOpaque);
        check.setOpaque(isOpaque);
        setPreferredSize(new Dimension(500, 100));
        check.addActionListener(this);
        text.setEditable(false);
        if (isInternal) {
            Border border = BorderFactory.createLineBorder(
                    Utils.getDarkShadowSysColor());
            TitledBorder titledBorder = Utils.createTitledBorder(border, "Свойства папки");
            setBorder(titledBorder);
        }
        Utils.setAllSize(textIndex, new Dimension(50, 20));
        add(check, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));
        add(text, new GridBagConstraints(1, 1, 3, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 5), 0, 0));
        add(idxLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        add(textIndex, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 0));
        add(new JLabel(""), new GridBagConstraints(3, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == check) {
            text.setEditable(check.isSelected());
            if (text.isEditable()) {
                text.requestFocusInWindow();
            }
        }
    }

    public String getInputName() {
        return text.getText();
    }

    public boolean isTab() {
        return check.isSelected();
    }

    public void setTab(boolean isTab) {
        check.setSelected(isTab);
        text.setEditable(isTab);
    }

    public void setTabName(String tabName) {
        text.setText(tabName);
    }

    public void setIndex(long idx) {
        textIndex.setText(String.valueOf(idx));
    }

    public int getIndex() {
        try {
            return new Integer(textIndex.getText()).intValue();
        } catch(Exception e) {
            return  0;
        }
    }
}
