package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
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

    public TextColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrTextComponent textField = (OrTextComponent)column.getEditor();
        editor = (TextFieldAdapter)textField.getAdapter();
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
                return item.getCurrent();
            }
        } else if (calcRef != null) {
            return calcRef.getItem(editor.langId, i);
        }
        return null;
    }

}
