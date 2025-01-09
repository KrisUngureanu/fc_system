package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.BoxOrExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

public class BoxOrExprProperty extends Property {

	public BoxOrExprProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new BoxOrExprEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new BoxOrExprEditorDelegate(table);
	}
}
