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
public class MemoColumnAdapter extends ColumnAdapter {
    private MemoFieldAdapter editor;

    public MemoColumnAdapter(UIFrame frame, OrMemoColumn column) throws KrnException {
        super(frame, column, false);
        OrMemoField mf = (OrMemoField)column.getEditor();
        editor = (MemoFieldAdapter)mf.getAdapter();
        celleditor = editor.getCellEditor(this);
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
            List items = dataRef.getItems(editor.langId);
            OrRef.Item item =
                    (i < items.size()) ? (OrRef.Item) items.get(i) : null;
            if (item != null && !item.isDeleted) {
                if (celleditor != null)
                return celleditor.getValueFor(item);
            }
        } else if (calcRef != null) {
            return calcRef.getItem(editor.langId, i);
        }
        return null;
    }
}
