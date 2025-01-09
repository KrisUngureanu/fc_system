package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.UiOrJumpEditorDelegate;

import javax.swing.*;

public class UiOrJumpProperty extends Property {

	public UiOrJumpProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new UiOrJumpEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new UiOrJumpEditorDelegate(table);
	}
}
