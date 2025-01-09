package kz.tamur.or3.client.props.inspector;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Property;

import javax.swing.*;

public class RefProperty extends Property {
	
	public RefProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	public RefProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new RefEditorDelegate(table);
	}
    public RendererDelegate createRendererDelegate(JTable table) {
        return new RefEditorDelegate(table);
    }


}
