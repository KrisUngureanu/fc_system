package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.KrnEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

public class KrnObjectProperty extends Property {
	
	private String className;
	private String titleAttr;
	
	public KrnObjectProperty(Property parent, PropertyNode node) {
		super(parent, node);
        this.className = node.getKrnClassName();
        this.titleAttr = node.getTitleAttrName();
    }

	public KrnObjectProperty(Property parent, PropertyNode node, String className, String titleAttr) {
		super(parent, node);
        this.className = className;
        this.titleAttr=titleAttr;
    }

	public KrnObjectProperty(Property parent, String id, String title, String className, String titleAttr) {
		super(parent, id, title);
        this.className = className;
        this.titleAttr=titleAttr;
    }

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new KrnEditorDelegate(table,className,titleAttr);
	}
    public RendererDelegate createRendererDelegate(JTable table) {
        return new KrnEditorDelegate(table,className,titleAttr);
    }

}
