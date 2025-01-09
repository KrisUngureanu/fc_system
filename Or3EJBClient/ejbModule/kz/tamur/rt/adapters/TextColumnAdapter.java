package kz.tamur.rt.adapters;

import kz.tamur.comps.OrTextColumn;
import kz.tamur.comps.OrTextField;
import kz.tamur.rt.adapters.OrRef.Item;

import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class TextColumnAdapter extends ColumnAdapter {
    private TextFieldAdapter editor;

    public TextColumnAdapter(UIFrame frame, OrTextColumn column) throws KrnException {
        super(frame, column, false);
        // TODO Перенести editor и renderer в компонент
        OrTextField textField = (OrTextField)column.getEditor();
        editor = (TextFieldAdapter)(textField).getAdapter();
        celleditor = textField.getCellEditor();
        celleditor.setUniqueIndex(getUniqueIndex());
    }

    public Object getObjectValueAt(int i) {
        if (dataRef != null) {
        	Item item = dataRef.getItem(editor.langId, i);
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
        	Item item = dataRef.getItem(editor.langId, i);
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
