package kz.tamur.or3.client.props;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.ExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

public class ExprProperty extends Property {
    
	public ExprProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	public ExprProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new ExprEditorDelegate(table, getId());
	}
	
//	@Override
	public EditorDelegate createEditorDelegate(JTable table, Property prop) {
		return new ExprEditorDelegate(table, getId(), prop);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new ExprEditorDelegate(table, getId());
	}

}
