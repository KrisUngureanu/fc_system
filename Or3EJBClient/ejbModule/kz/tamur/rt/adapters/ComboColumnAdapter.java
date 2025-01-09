package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.ui.comboBox.OrComboBoxUI;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class ComboColumnAdapter extends ColumnAdapter {
    private ComboBoxAdapter editor;
    private OrRef titleRef;
    private OrComboBox cmb;

    public ComboColumnAdapter(UIFrame frame, OrComboColumn column) throws KrnException {
        super(frame, column, false);
        cmb = (OrComboBox) column.getEditor();
        editor = (ComboBoxAdapter) cmb.getAdapter();
        celleditor = editor.getCellEditor();
        celleditor.setUniqueIndex(getUniqueIndex());
        // показывать выпадающий список при получении фокуса компонентом
        ((OrComboBoxUI) cmb.getComboBox().getUI()).setShowInFocus(true);
        createTitleRef();
    }

    @Override
    public OrCellEditor getCellEditor() {
        return celleditor;
    }

    private void createTitleRef() throws KrnException {
        StringBuffer path = new StringBuffer();

        KrnClass type = dataRef.getType();

        if (type.id > 99) {
            OrRef pref = editor.getContent();
            KrnClass currType = pref.getType();

            int parentsCount = -1;
            while (type.id != currType.id && pref.getParent() != null) {
                path.insert(0, "." + pref.getAttribute().name);
                pref = pref.getParent();
                currType = pref.getType();
                parentsCount++;
            }
            path.insert(0, dataRef);
            titleRef = OrRef.createRef(path.toString(), true, Mode.RUNTIME, frame.getRefs(), dataRef.getDirtyTransactions(),
                    frame);
            OrRef r = titleRef;
            while (parentsCount-- > 0) {
                r = r.getParent();
                r.setColumn(true);
            }
            if (frame.getContentRef().get(path.toString()) == null) {
                frame.getContentRef().put(path.toString(), titleRef);
            }
        }
    }

    public Object getValueAt(int row) {
        if (titleRef != null) {
            OrRef.Item item = titleRef.getItem(0, row);
            if (item != null) {
                return item.getCurrent();
            }
        } else {
            return dataRef.getValue(0, row);
        }
        return null;
    }
}
