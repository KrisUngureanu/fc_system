package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;


public class XmlProperty extends Property {

	public XmlProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new XmlEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new XmlEditorDelegate(table);
	}

}
