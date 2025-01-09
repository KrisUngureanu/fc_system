package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import com.cifs.or2.kernel.KrnException;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class IntColumnAdapter extends ColumnAdapter {
    private IntFieldAdapter editor;

    public IntColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);

        OrTextComponent textField = (OrTextComponent)column.getEditor();
        editor = (IntFieldAdapter)textField.getAdapter();
    }

    public IntFieldAdapter getEditor() {
        return editor;
    }
}
