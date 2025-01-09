package kz.tamur.guidesigner;

import kz.tamur.comps.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 20.10.2004
 * Time: 11:01:07
 * To change this template use File | Settings | File Templates.
 */
public class Console extends JDialog implements ActionListener {

    private DesignerFrame designerFrm;

    private JPanel mainPanel = new JPanel(new GridBagLayout());

    private JButton guiBut = ButtonsFactory.createToolButton("GUIBig","Конструктор интерфейсов системы");
    private JButton classBut = ButtonsFactory.createToolButton("ClassesBig", "Конструктор классов системы");
    private JButton objBut = ButtonsFactory.createToolButton("ObjectsBig", "Управление объектами системы");
    private JButton usersBut = ButtonsFactory.createToolButton("UsersBig", "Управление пользователями системы");
    private JButton accessBut = ButtonsFactory.createToolButton("AccessBig", "Управление доступом");
    private JButton filterBut = ButtonsFactory.createToolButton("FiltersBig", "Управление фильтрами системы");

    public Console(Frame owner) throws HeadlessException {
        super(owner, true);
        setResizable(false);
        init();
        pack();
        setLocation(Utils.getCenterLocationPoint(getSize()));
    }

    private void init() {
        guiBut.setPreferredSize(new Dimension(80, 80));
        guiBut.setMaximumSize(new Dimension(80, 80));
        guiBut.setMinimumSize(new Dimension(80, 80));
        guiBut.setMargin(null);
        guiBut.setHorizontalTextPosition(AbstractButton.CENTER);
        guiBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        guiBut.setText("Интерфейсы");
        guiBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        guiBut.addActionListener(this);

        classBut.setPreferredSize(new Dimension(80, 80));
        classBut.setMaximumSize(new Dimension(80, 80));
        classBut.setMinimumSize(new Dimension(80, 80));
        classBut.setMargin(null);
        classBut.setHorizontalTextPosition(AbstractButton.CENTER);
        classBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        classBut.setText("Классы");
        classBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        objBut.setPreferredSize(new Dimension(80, 80));
        objBut.setMaximumSize(new Dimension(80, 80));
        objBut.setMinimumSize(new Dimension(80, 80));
        objBut.setMargin(null);
        objBut.setHorizontalTextPosition(AbstractButton.CENTER);
        objBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        objBut.setText("Объекты");
        objBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        usersBut.setPreferredSize(new Dimension(80, 80));
        usersBut.setMaximumSize(new Dimension(80, 80));
        usersBut.setMinimumSize(new Dimension(80, 80));
        usersBut.setMargin(null);
        usersBut.setHorizontalTextPosition(AbstractButton.CENTER);
        usersBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        usersBut.setText("Пользователи");
        usersBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        accessBut.setPreferredSize(new Dimension(80, 80));
        accessBut.setMaximumSize(new Dimension(80, 80));
        accessBut.setMinimumSize(new Dimension(80, 80));
        accessBut.setMargin(null);
        accessBut.setHorizontalTextPosition(AbstractButton.CENTER);
        accessBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        accessBut.setText("Доступ");
        accessBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        filterBut.setPreferredSize(new Dimension(80, 80));
        filterBut.setMaximumSize(new Dimension(80, 80));
        filterBut.setMinimumSize(new Dimension(80, 80));
        filterBut.setMargin(null);
        filterBut.setHorizontalTextPosition(AbstractButton.CENTER);
        filterBut.setVerticalTextPosition(AbstractButton.BOTTOM);
        filterBut.setText("Фильтры");
        filterBut.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        mainPanel.add(guiBut, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(classBut, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(objBut, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(usersBut, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(accessBut, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        mainPanel.add(filterBut, new GridBagConstraints(2, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
    }
}
