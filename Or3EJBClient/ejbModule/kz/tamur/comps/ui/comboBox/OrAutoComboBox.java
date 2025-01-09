package kz.tamur.comps.ui.comboBox;

import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import kz.tamur.comps.ui.textField.OrAutoTextField;

/**
 * Автодополняемый выпадающий список
 * @author Sergey Lebedev
 *
 */
public class OrAutoComboBox extends JComboBox{
    private AutoTextFieldEditor autoTextFieldEditor;

    private boolean isFired;

    public OrAutoComboBox(java.util.List<String> list) {
        isFired = false;
        autoTextFieldEditor = new AutoTextFieldEditor(list);
        setEditable(true);
        setOpaque(false);
        setBorder(null);
        
        setModel(new DefaultComboBoxModel(list.toArray()) {
            protected void fireContentsChanged(Object obj, int i, int j) {
                if (!isFired)
                    super.fireContentsChanged(obj, i, j);
            }

        });
        
        setEditor(autoTextFieldEditor);
    }

    public boolean isCaseSensitive() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isCaseSensitive();
    }

    public void setCaseSensitive(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setCaseSensitive(flag);
    }

    public boolean isStrict() {
        return autoTextFieldEditor.getAutoTextFieldEditor().isStrict();
    }

    public void setStrict(boolean flag) {
        autoTextFieldEditor.getAutoTextFieldEditor().setStrict(flag);
    }

    public java.util.List<String> getDataList() {
        return autoTextFieldEditor.getAutoTextFieldEditor().getDataList();
    }

    public void setDataList(java.util.List<String> list) {
        autoTextFieldEditor.getAutoTextFieldEditor().setDataList(list);
        setModel(new DefaultComboBoxModel(list.toArray()));
    }

    public void setSelectedValue(Object obj) {
        if (!isFired) {
            isFired = true;
            setSelectedItem(obj);
            fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder, 1));
            isFired = false;
        }
    }

    protected void fireActionEvent() {
        if (!isFired) {
            super.fireActionEvent();
        }
    }

    /**
     * Получить текст выбранного пункта
     * 
     * @return
     */
    public String getText() {
        return (String) getSelectedItem();
    }

    private class AutoTextFieldEditor extends BasicComboBoxEditor {
        private OrAutoTextField getAutoTextFieldEditor() {
            return (OrAutoTextField) editor;
        }

        AutoTextFieldEditor(java.util.List<String> list) {
            editor = new OrAutoTextField(list, OrAutoComboBox.this);
        }
    }
}
