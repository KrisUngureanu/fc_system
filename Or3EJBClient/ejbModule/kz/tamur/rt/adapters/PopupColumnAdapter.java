package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.rt.adapters.OrRef.Item;

import com.cifs.or2.kernel.KrnException;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class PopupColumnAdapter extends ColumnAdapter {
    private HyperPopupAdapter editor;

    public PopupColumnAdapter(UIFrame frame, OrPopupColumn column) throws KrnException {
        super(frame, column, false);
        editor = new HyperPopupAdapter(frame, (OrHyperPopup)column.getEditor(), true);
        celleditor = editor.getCellEditor();
        renderer = editor.getCellRenderer();
        OrRef titleRef = editor.getTitleRef();
        if (titleRef != null) {
        	titleRef.addOrRefListener(this);
        }
    }

    public Object getObjectValueAt(int i) {
        OrRef titleRef = editor.getTitleRef();
        if (titleRef != null) {
            List<Item> items = titleRef.getItems(editor.langId);
            Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        }
        return null;
    }

    public Object getValueAt(int i) {
        OrRef titleRef = editor.getTitleRef();
        if (titleRef != null) {
        	List<Item> items = titleRef.getItems(editor.langId);
        	Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                if (celleditor != null)
                return celleditor.getValueFor(item);
            }
        } else if (calcRef != null) {
            return calcRef.getItem(editor.langId, i);
        }
        return null;
    }

    public OrRef getContentRef() {
        return (editor != null) ? editor.getContentRef() : null;
    }

    public OrRef getTitleRef() {
        return (editor != null) ? editor.getTitleRef() : null;
    }
}
