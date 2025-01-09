package kz.tamur.or3.client.props;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.ComboToolTipEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
/**
 * @author Sergey Lebedev
 *
 * Класс реализующий работу со свойстами списка с подсказками
 */
public class ComboToolTipProperty extends ComboProperty {
	
	public ComboToolTipProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new ComboToolTipEditorDelegate(this,table);
	}

	@Override
    public ComboPropertyItem[] getItems() {
	    return items.toArray(new ComboToolTipPropertyItem[items.size()]);
    }

    public ComboToolTipProperty addItem(String id, String title,String pathIco) {
		items.add(new ComboToolTipPropertyItem(id, title,pathIco));
		return this;
	}
}
