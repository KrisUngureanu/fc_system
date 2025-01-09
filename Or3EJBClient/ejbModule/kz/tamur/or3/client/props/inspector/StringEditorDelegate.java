package kz.tamur.or3.client.props.inspector;

import java.awt.Component;

import kz.tamur.comps.ui.textField.OrPropTextField;
import kz.tamur.or3.client.props.ComboPropertyItem;

public class StringEditorDelegate extends OrPropTextField implements EditorDelegate {

    public StringEditorDelegate(boolean editable) {
        super();
        setEditable(editable);
    }

    public Object getValue() {
        return this.getText();
    }

    public void setValue(Object value) {
    	if (value instanceof ExprEditorObject)
    		value = ((ExprEditorObject)value).getObject();

    	if (value instanceof String)
            setText((String) value);
        else if (value instanceof Integer)
            setText(String.valueOf((Integer) value));
    }

    public Component getEditorComponent() {
        return this;
    }

    public int getClickCountToStart() {
        return 2;
    }

    public void setPropertyEditor(PropertyEditor editor) {
    }
}
