package kz.tamur.or3.client.props.inspector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import kz.tamur.comps.ui.checkBox.OrBasicCheckBox;

public class CheckEditorDelegate extends OrBasicCheckBox implements EditorDelegate, RendererDelegate, ItemListener {

    private PropertyEditor editor;
 
    public CheckEditorDelegate(JTable table) {
        super();
        setOpaque(false);
        setAnimate(false);
        this.setFont(table.getFont());
        addItemListener(this);
    }

    public int getClickCountToStart() {
        return 1;
    }

    public Component getEditorComponent() {
        return this;
    }

    public Component getRendererComponent() {
        return this;
    }

    public Object getValue() {
        return isSelected();
    }

    public void setValue(Object value) {
    	if (value instanceof ExprEditorObject)
    		value = ((ExprEditorObject)value).getObject();

        setSelected(value instanceof Boolean ? (Boolean) value : false);
    }

    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void itemStateChanged(ItemEvent e) {
        if (editor != null) {
            editor.stopCellEditing();
        }
    }
}
