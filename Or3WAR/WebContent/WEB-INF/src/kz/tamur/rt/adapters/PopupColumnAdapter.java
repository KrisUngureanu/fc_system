package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrHyperPopupComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableModel;
import kz.tamur.rt.adapters.OrRef.Item;

import com.cifs.or2.kernel.KrnException;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class PopupColumnAdapter extends ColumnAdapter {
    private HyperPopupAdapter editor;

    public PopupColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrHyperPopupComponent hp = (OrHyperPopupComponent)column.getEditor();
        editor = (HyperPopupAdapter)hp.getAdapter();
        renderer = hp.getCellRenderer();
    }
    
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        OrRef ref = e.getRef();
        if (ref == getTitleRef() || ref == getTitleRefExpr()) {
            int i = ref.getIndex();
            if (i > -1 && tableAdapter instanceof TreeTableAdapter) {
                OrTableModel model = (OrTableModel) ((TreeTableAdapter) tableAdapter).getModel();
                int row = model.getRowFromIndex(i);
                if (row > -1)
                    model.fireTableRowsUpdated(row, row);
            } else if (i > -1 && tableAdapter != null) {
                if (e.getReason() == OrRefEvent.CHANGED || e.getReason() == OrRefEvent.UPDATED)
                    tableAdapter.getTable().tableCellUpdated(i, index);
                rowIndex = e.getIndex();
            }
        }
    }
    
    public Object getObjectValueAt(int i) {
        OrRef titleRef = editor.getTitleRef();
        OrCalcRef titleRefExpr = editor.getTitleRefExpr();
        if (titleRef != null) {
            List<Item> items = titleRef.getItems(editor.langId);
            Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        }
        else if (titleRefExpr != null) {
        	Item item = titleRefExpr.getItem(editor.langId, i);
        	if (item != null && !item.isDeleted){
        		return item.getCurrent();
        	}
        }
        return null;
    }

    public Object getValueAt(int i) {
        OrRef titleRef = getTitleRef();
        if (titleRef != null) {
            List<Item> items = titleRef.getItems(editor.langId);
            Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        } else if (calcRef != null) {
            return calcRef.getItem(editor.langId, i);
        }
        return null;
    }

    public OrRef getContentRef() {
        return (editor != null) ? editor.getContentRef() : null;
    }

    public OrRef getTitleRef() {
        return (editor != null) ? editor.getTitleRef() : null;
    }
    
    public OrCalcRef getTitleRefExpr() {
    	return (editor != null) ? editor.getTitleRefExpr() : null;
    }
    
    public void setTableAdapter(TableAdapter a) {
    	super.setTableAdapter(a);
        if (editor != null && editor.getTitleRefExpr() != null) {
        	editor.getTitleRefExpr().setTableRef(tableAdapter.getRef());
        }
    }
}
