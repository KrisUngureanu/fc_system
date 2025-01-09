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
public class DateColumnAdapter extends ColumnAdapter {
    private DateFieldAdapter editor;

    public DateColumnAdapter(UIFrame frame, OrDateColumn column) throws KrnException {
        super(frame, column, false);
        OrDateField dateField = (OrDateField)column.getEditor();
        editor = (DateFieldAdapter)dateField.getAdapter();
        celleditor = dateField.getCellEditor();
        celleditor.setUniqueIndex(getUniqueIndex());
    }
}
