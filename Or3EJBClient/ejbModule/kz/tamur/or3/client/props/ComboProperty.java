package kz.tamur.or3.client.props;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.ComboEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;

public class ComboProperty extends Property {
	
	protected List<ComboPropertyItem> items = new ArrayList<ComboPropertyItem>();

	public ComboProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	public ComboProperty(Property parent, String id, String title) {
		super(parent, id, title);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new ComboEditorDelegate(this,table);
	}

	public ComboProperty addItem(String id, String title) {
		items.add(new ComboPropertyItem(id, title));
		return this;
	}
	
	public ComboPropertyItem[] getItems() {
		return items.toArray(new ComboPropertyItem[items.size()]);
	}

    public ComboPropertyItem getItem(String id) {
        for (ComboPropertyItem item : items) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }
}
