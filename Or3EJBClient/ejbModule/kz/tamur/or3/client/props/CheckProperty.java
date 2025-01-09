package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.CheckEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

public class CheckProperty extends Property {
    
    CheckEditorDelegate ed;
    
    public CheckProperty(Property parent, PropertyNode node) {
        super(parent, node);
    }

    public CheckProperty(Property parent, String id, String title) {
        super(parent, id, title);
    }

    public RendererDelegate createRendererDelegate(JTable table) {
        return getDelegate(table);
    }

    public EditorDelegate createEditorDelegate(JTable table) {
        return getDelegate(table);
    }

    private CheckEditorDelegate getDelegate(JTable table) {
        if (ed == null) {
            ed = new CheckEditorDelegate(table);
        }
        return ed;
    }
}
