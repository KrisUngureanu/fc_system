package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.OrDocFieldColumn;
import kz.tamur.comps.OrDocField;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class DocFieldColumnAdapter extends ColumnAdapter {
    private DocFieldAdapter editor;

    public DocFieldColumnAdapter(UIFrame frame, OrDocFieldColumn column) throws KrnException {
        super(frame, column, false);
        editor = new DocFieldAdapter(frame, (OrDocField)column.getEditor(), true);
        celleditor = editor.getCellEditor();
        renderer = editor.getCellRenderer();



    }
}
