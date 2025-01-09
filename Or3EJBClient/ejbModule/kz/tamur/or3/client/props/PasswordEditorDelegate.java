package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.PropertyEditor;

import javax.swing.*;
import java.awt.*;

public class PasswordEditorDelegate extends JPasswordField implements EditorDelegate {

	public Object getValue() {
		return this.getPassword();
	}

	public void setValue(Object value) {
		setText((String)value);
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
