package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.FontEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

public class FontProperty extends Property {

	public FontProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new FontEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new FontEditorDelegate(table);
	}

}
