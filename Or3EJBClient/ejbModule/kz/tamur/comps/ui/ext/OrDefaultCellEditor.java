package kz.tamur.comps.ui.ext;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.EventObject;

import kz.tamur.comps.ui.comboBox.OrComboBoxUI;

public class OrDefaultCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
    public static final String COMBOBOX_CELL_EDITOR = "JComboBox.isTableCellEditor";

    protected JComponent editorComponent;
    protected EditorDelegate delegate;
    protected int clickCountToStart = 1; // При внедрении стиля для таблиц переделать на  OrTableStyle.clickCountToStartEdit

    public OrDefaultCellEditor(final JTextField textField) {
        editorComponent = textField;
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                textField.setText((value != null) ? value.toString() : "");
            }

            public Object getCellEditorValue() {
                return textField.getText();
            }
        };
        textField.addActionListener(delegate);
    }

    public OrDefaultCellEditor(final JCheckBox checkBox) {
        editorComponent = checkBox;
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                boolean selected = false;
                if (value instanceof Boolean) {
                    selected = (Boolean) value;
                } else if (value instanceof String) {
                    selected = value.equals("true");
                }
                checkBox.setSelected(selected);
            }

            public Object getCellEditorValue() {
                return checkBox.isSelected();
            }
        };
        checkBox.addActionListener(delegate);
        checkBox.setRequestFocusEnabled(false);
    }

    public OrDefaultCellEditor(final JComboBox comboBox) {
        editorComponent = comboBox;
        if (comboBox.getUI() instanceof OrComboBoxUI) {
            OrComboBoxUI webComboBoxUI = (OrComboBoxUI) comboBox.getUI();
            webComboBoxUI.setDrawBorder(false);
        }
        comboBox.putClientProperty(COMBOBOX_CELL_EDITOR, Boolean.TRUE);
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                comboBox.setSelectedItem(value);
            }

            public Object getCellEditorValue() {
                return comboBox.getSelectedItem();
            }

            public boolean shouldSelectCell(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    MouseEvent e = (MouseEvent) anEvent;
                    return e.getID() != MouseEvent.MOUSE_DRAGGED;
                }
                return true;
            }

            public boolean stopCellEditing() {
                if (comboBox.isEditable()) {
                    comboBox.actionPerformed(new ActionEvent(OrDefaultCellEditor.this, 0, ""));
                }
                return super.stopCellEditing();
            }
        };
        comboBox.addActionListener(delegate);
    }

    public Component getComponent() {
        return editorComponent;
    }

    public void setClickCountToStart(int count) {
        clickCountToStart = count;
    }

    public int getClickCountToStart() {
        return clickCountToStart;
    }

    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) {
        return delegate.isCellEditable(anEvent);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf,
            int row) {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, false);
        delegate.setValue(stringValue);
        return editorComponent;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        delegate.setValue(value);
        return editorComponent;
    }

    protected class EditorDelegate implements ActionListener, ItemListener, Serializable {
        protected Object value;

        public Object getCellEditorValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
            }
            return true;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean startCellEditing(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public void actionPerformed(ActionEvent e) {
            OrDefaultCellEditor.this.stopCellEditing();
        }

        public void itemStateChanged(ItemEvent e) {
            OrDefaultCellEditor.this.stopCellEditing();
        }
    }

}
