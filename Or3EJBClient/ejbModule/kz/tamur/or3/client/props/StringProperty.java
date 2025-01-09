package kz.tamur.or3.client.props;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.StringEditorDelegate;

public class StringProperty extends Property {
    private boolean editable = true;

    public StringProperty(Property parent, PropertyNode node) {
        super(parent, node);
    }

    public StringProperty(Property parent, PropertyNode node, boolean editable) {
        super(parent, node);
        this.editable = editable;
    }

    public StringProperty(Property parent, String id, String title) {
        this(parent, id, title, true);
    }

    public StringProperty(Property parent, String id, String title, boolean editable) {
        super(parent, id, title);
        this.editable = editable;
    }

    @Override
    public EditorDelegate createEditorDelegate(JTable table) {
        return new StringEditorDelegate(editable);
    }

}
