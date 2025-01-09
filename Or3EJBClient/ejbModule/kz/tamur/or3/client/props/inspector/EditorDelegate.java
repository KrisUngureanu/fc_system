package kz.tamur.or3.client.props.inspector;

import java.awt.Component;

public interface EditorDelegate {

	void setValue(Object value);
	void setPropertyEditor(PropertyEditor editor);
	Object getValue();
	
	Component getEditorComponent();
	int getClickCountToStart();
}
