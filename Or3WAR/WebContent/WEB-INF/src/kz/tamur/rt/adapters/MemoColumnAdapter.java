package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrMemoComponent;
import com.cifs.or2.kernel.KrnException;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.rt.adapters.OrRef.Item;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class MemoColumnAdapter extends ColumnAdapter {
    private MemoFieldAdapter editor;

    public MemoColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrMemoComponent field = (OrMemoComponent) column.getEditor();
        editor = (MemoFieldAdapter) field.getAdapter();
    }

    public Object getObjectValueAt(int i) {
        if (dataRef != null) {
            List items = dataRef.getItems(editor.langId);
            OrRef.Item item =
                    (i < items.size()) ? (OrRef.Item) items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        } else if (calcRef != null) {
            Item item = calcRef.getItem(editor.langId, i);
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        }
        return null;
    }
    public Object getValueAt(int i) {
        if (dataRef != null) {
            List<Item> items = dataRef.getItems(editor.langId);
            Item item =
                    (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return editor.getValueFor(item);
            }
        } else if (calcRef != null) {
            return calcRef.getItem(editor.langId, i);
        }
        return null;
    }
}
