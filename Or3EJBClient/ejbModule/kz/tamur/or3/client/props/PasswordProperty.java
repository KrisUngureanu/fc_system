package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import javax.swing.JTable;

public class PasswordProperty extends Property {

	public PasswordProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new PasswordEditorDelegate();
	}

}
