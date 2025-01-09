package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import com.cifs.or2.kernel.KrnException;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class FloatColumnAdapter extends ColumnAdapter {
    private FloatFieldAdapter editor;

    public FloatColumnAdapter(UIFrame frame, OrFloatColumn column) throws KrnException {
        super(frame, column, false);
        editor = new FloatFieldAdapter(frame, (OrFloatField)column.getEditor(), true);
        celleditor = editor.getCellEditor();
        celleditor.setUniqueIndex(getUniqueIndex());
    }

    public FloatFieldAdapter getEditor() {
        return editor;
    }
}
