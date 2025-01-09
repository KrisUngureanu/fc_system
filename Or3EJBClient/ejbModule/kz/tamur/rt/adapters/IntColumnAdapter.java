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
public class IntColumnAdapter extends ColumnAdapter {
    private IntFieldAdapter editor;

    public IntColumnAdapter(UIFrame frame, OrIntColumn column) throws KrnException {
        super(frame, column, false);
        editor = new IntFieldAdapter(frame, (OrIntField)column.getEditor(), true);
        celleditor = editor.getCellEditor();
        celleditor.setUniqueIndex(getUniqueIndex());
    }

    public IntFieldAdapter getEditor() {
        return editor;
    }
}
