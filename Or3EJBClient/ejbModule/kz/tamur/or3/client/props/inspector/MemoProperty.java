package kz.tamur.or3.client.props.inspector;

import javax.swing.JTable;
import kz.tamur.or3.client.props.Property;

public class MemoProperty extends Property {

	public MemoProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new MemoEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new MemoEditorDelegate(table);
	}

}
