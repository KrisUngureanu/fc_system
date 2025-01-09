package kz.tamur.guidesigner;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;

/**
 * User: vital
 * Date: 17.02.2006
 * Time: 10:02:40
 */

public class ComponentsTool extends JPanel implements ActionListener {

    private JComboBox toolsCombo = Utils.createCombo();
    private CardLayout layout = new CardLayout();
    private JPanel buttonsPanel = new JPanel(layout);
    private boolean isOpaque = !kz.tamur.rt.MainFrame.TRANSPARENT_DIALOG;

    public ComponentsTool() {
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        Utils.setAllSize(toolsCombo, new Dimension(130, 22));
        toolsCombo.addActionListener(this);
        toolsCombo.setToolTipText("Выбор типа компонентов");
        add(toolsCombo, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_0, 0, 0));
        add(buttonsPanel, new GridBagConstraints(1, 0, 5, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
        setOpaque(isOpaque);
        buttonsPanel.setOpaque(isOpaque);
    }

    public void addToolBar(String name, JToolBar toolBar) {
        toolsCombo.addItem(name);
        buttonsPanel.add(toolBar, name);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == toolsCombo) {
            layout.show(buttonsPanel, toolsCombo.getSelectedItem().toString());
        }
    }
}
