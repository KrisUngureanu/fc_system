package kz.tamur.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import kz.tamur.Or3Frame;
import kz.tamur.comps.MenuItemRecord;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

/**
 * User: vital
 * Date: 21.02.2005
 * Time: 10:55:40
 */
public class MenuItemEditorPanel extends JPanel {

    private JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JLabel label = kz.tamur.rt.Utils.createLabel("Наименование пункта меню");
    private JTextField textFld = kz.tamur.rt.Utils.createDesignerTextField();
    private JButton addBtn = ButtonsFactory.createToolButton("Добавить");
    private JPanel leftPanel = new JPanel(new GridBagLayout());

    private MenuTableModel model = new MenuTableModel();
    private JTable propTable = new JTable(model);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public MenuItemEditorPanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(600, 400));
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.setMaximumSize(new Dimension(100, 30));
        addBtn.setMinimumSize(new Dimension(100, 30));
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!"".equals(textFld.getText()) && textFld.getText().length() > 0) {
                    model.addItem(new MenuItemRecord(textFld.getText(), ""));
                    textFld.setText("");
                    textFld.requestFocusInWindow();
                }
            }
        });

        leftPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));
        leftPanel.add(textFld, new GridBagConstraints(0, 1, 2, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        leftPanel.add(addBtn, new GridBagConstraints(1, 2, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 0, 0, 5), 0, 0));
        leftPanel.add(new JLabel(" "), new GridBagConstraints(0, 3, 1, 5, 0, 2,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 0, 0, 5), 0, 0));
        splitter.setLeftComponent(leftPanel);
        propTable.getColumnModel().getColumn(1).setCellEditor(new MenuItemPropertyEditor());
        propTable.setFont(Utils.getDefaultFont());
        propTable.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    model.deleteItem(propTable.getSelectedRow());
                }
                super.keyReleased(e);
            }
        });
        JScrollPane sp = new JScrollPane(propTable);
        splitter.setRightComponent(sp);
        add(splitter, BorderLayout.CENTER);
        textFld.requestFocusInWindow();
        setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        propTable.setOpaque(isOpaque);
        splitter.setOpaque(isOpaque);
    }

    public void setDividerLoc() {
        splitter.setDividerLocation(0.4);
    }

    public MenuItemRecord[] getMenuItems() {
        return model.getMenuItems();
    }

    public void setOldValues(MenuItemRecord[] vals) {
        model.setOldValues(vals);
    }

    class MenuTableModel extends AbstractTableModel {

        public final String[] COL_NAMES = {"Наименование", "Выражение"};

        private List menuItems = new ArrayList();

        public void addItem(MenuItemRecord m) {
            menuItems.add(m);
            fireTableDataChanged();
        }

        public void deleteItem(int row) {
            MenuItemRecord mi = (MenuItemRecord)menuItems.get(row);
            if (mi != null) {
                int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE,
                        "Удалить пункт меню \"" + mi.getTitle() + "\" ?");

                if (res == ButtonsFactory.BUTTON_YES) {
                    menuItems.remove(row);
                    fireTableDataChanged();
                }
            }
        }

        public void setOldValues(MenuItemRecord[] vals) {
            for (int i = 0; i < vals.length; i++) {
                MenuItemRecord val = vals[i];
                addItem(val);
            }
            fireTableDataChanged();
        }

        public MenuItemRecord[] getMenuItems() {
            if (menuItems.size() > 0) {
                MenuItemRecord[] recs = new MenuItemRecord[menuItems.size()];
                for (int i = 0; i < menuItems.size(); i++) {
                    MenuItemRecord menuItemRecord = (MenuItemRecord) menuItems.get(i);
                    recs[i] = menuItemRecord;
                }
                return recs;
            } else {
                return null;
            }
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getRowCount() {
            return menuItems.size();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            MenuItemRecord mi = (MenuItemRecord)menuItems.get(rowIndex);
            if (mi != null) {
                switch(columnIndex) {
                    case 0:
                        return mi.getTitle();
                    case 1:
                        return mi.getExpr();
                }
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            MenuItemRecord mi = (MenuItemRecord)menuItems.get(rowIndex);
            if (mi != null) {
                switch(columnIndex) {
                    case 0:
                        mi.setTitle(aValue.toString());
                        break;
                    case 1:
                        mi.setExpr(aValue.toString());
                        break;
                }
                fireTableDataChanged();
            }
        }

    }

    public class MenuItemPropertyEditor extends DefaultCellEditor {

        private JTextField stringField = Utils.createDesignerTextField();

        public MenuItemPropertyEditor() {
            super(new JTextField());
            stringField.setLayout(new BorderLayout());
            setClickCountToStart(1);
        }

        public Object getCellEditorValue() {
            return stringField.getText();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row,
                                                     int column) {
            if (column == 1) {
                final JButton btn = ButtonsFactory.createEditorButton(ButtonsFactory.DEFAULT_EDITOR);
                stringField.removeAll();
                stringField.setText("" + value);
                final String val = value.toString();
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ExpressionEditor exprEditor = new ExpressionEditor(val, MenuItemPropertyEditor.this);
                        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
                        dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
                        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
                        dlg.show();
                        if (dlg.isOK()) {
                        	setExpression(exprEditor.getExpression());
                        } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                            stringField.setText(val);
                        }
                        stopCellEditing();
                    }
                });
                stringField.add(btn, BorderLayout.EAST);
            }
            return stringField;
        }
        
        public void setExpression(String expression) {
            stringField.setText(expression);
        }
    }
}
