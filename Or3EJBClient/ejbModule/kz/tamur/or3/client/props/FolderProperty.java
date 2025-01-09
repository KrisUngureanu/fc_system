package kz.tamur.or3.client.props;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;

public class FolderProperty extends Property {

	public FolderProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	public FolderProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return null;
	}

}
