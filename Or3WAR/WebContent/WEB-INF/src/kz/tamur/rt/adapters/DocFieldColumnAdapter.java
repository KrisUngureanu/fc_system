package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.OrFrame;
import kz.tamur.or3.client.comps.interfaces.OrDocFieldComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class DocFieldColumnAdapter extends ColumnAdapter {
    private DocFieldAdapter editor;

    public DocFieldColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrDocFieldComponent comp = (OrDocFieldComponent)column.getEditor();
        editor = (DocFieldAdapter)comp.getAdapter();
        renderer = comp.getCellRenderer();
    }
}
