package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.TreeOrExprEditorDelegate;

import javax.swing.*;



public class TreeOrExprProperty extends Property {

	private String className;

	public TreeOrExprProperty(Property parent, String id, String title, String className) {
		super(parent, id, title);
        this.className = className;
    }

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new TreeOrExprEditorDelegate(table,className);
	}
    public RendererDelegate createRendererDelegate(JTable table) {
        return new TreeOrExprEditorDelegate(table,className);
    }

}
