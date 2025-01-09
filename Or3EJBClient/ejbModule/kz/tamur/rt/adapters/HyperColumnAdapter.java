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
public class HyperColumnAdapter extends ColumnAdapter {
    private HyperLabelAdapter editor;
    public HyperColumnAdapter(UIFrame frame, OrHyperColumn column) throws KrnException {
        super(frame, column, false);
        editor = new HyperLabelAdapter(frame, (OrHyperLabel)column.getEditor(), true);
        celleditor = editor.getCellEditor();
        renderer = editor.getCellRenderer();
        //PropertyNode proot = column.getProperties();

    }

    public OrRef getDynamicInterfaceRef() {
        return (editor != null) ? editor.getDynamicInterfaceRef() : null;
    }
}
