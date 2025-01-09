package kz.tamur.admin.clsbrow;

import java.awt.Component;

public interface ObjectEditorDelegate {

	void setValue(Object value);
	void setObjectPropertyEditor(ObjectPropertyEditor editor);
	Object getValue();

	Component getObjectEditorComponent();
	int getClickCountToStart();
}
