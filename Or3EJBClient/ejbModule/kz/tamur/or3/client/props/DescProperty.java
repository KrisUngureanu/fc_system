package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.DescEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

public class DescProperty extends Property {

	public DescProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new DescEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new DescEditorDelegate(table);
	}

}
