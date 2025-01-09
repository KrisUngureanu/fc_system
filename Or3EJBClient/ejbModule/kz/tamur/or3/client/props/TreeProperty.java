package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.TreeEditorDelegate;

import javax.swing.*;

public class TreeProperty extends Property {

	private String className;

	public TreeProperty(Property parent, PropertyNode node, String className) {
		super(parent, node);
        this.className = className;
    }

	public TreeProperty(Property parent, String id, String title, String className) {
		super(parent, id, title);
        this.className = className;
    }

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new TreeEditorDelegate(table,className);
	}
    public RendererDelegate createRendererDelegate(JTable table) {
        return new TreeEditorDelegate(table,className);
    }

}
