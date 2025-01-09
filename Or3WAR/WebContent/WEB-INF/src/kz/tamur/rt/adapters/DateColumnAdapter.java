package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrDateComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
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

    public DateColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrDateComponent field = (OrDateComponent)column.getEditor();
        editor = (DateFieldAdapter)field.getAdapter();
    }
}
