package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrComboBoxComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;

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
    private OrComboBoxComponent cmb;
    public ComboColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        cmb = (OrComboBoxComponent) column.getEditor();
        editor = (ComboBoxAdapter)cmb.getAdapter();
        if (dataRef != null) {
            createTitleRef();
        	dataRef.removeOrRefListener(this);
        	dataRef.addOrRefListener(this);
        }
    }

    private void createTitleRef() throws KrnException {
        StringBuffer path = new StringBuffer();

        KrnClass type = dataRef.getType();

        if (type.id > 99) {
	        OrRef pref = editor.getContent();
	        if (pref != null) {
	            KrnClass currType = pref.getType();
	
	            int parentsCount = -1;
	            while (type.id != currType.id && pref.getParent() != null) {
	                path.insert(0, "." + pref.getAttribute().name);
	                pref = pref.getParent();
	                currType = pref.getType();
	                parentsCount++;
	            }
	            path.insert(0, dataRef);
	            titleRef = OrRef.createRef(path.toString(), true, Mode.RUNTIME, frame.getRefs(),
	                    dataRef.getDirtyTransactions(), frame);
	            OrRef r = titleRef;
	            while (parentsCount-- > 0) {
	            	r = r.getParent();
	            	r.setColumn(true);
	            }
	        } else {
	            Util.showErrorMessage(column, "Не задано содержимое", "Свойство: Содержимое");
	        }
	        if (frame.getContentRef().get(path.toString()) == null) {
	            frame.getContentRef().put(path.toString(), titleRef);
	        }
        }
    }

    public Object getValueAt(int row) {
        if (titleRef != null) {
            OrRef.Item item = titleRef.getItem(0, row);
            if (item != null)
                return item.getCurrent();
        } else {
        	return dataRef.getValue(0, row);
        }
        return null;
    }
    
    public String getIndexAt(int row) {
    	if (dataRef != null) {
	    	Object obj = dataRef.getValue(0, row);
	    	if (obj instanceof KrnObject)
	    		return ((KrnObject)obj).uid;
    	}
        return "";
    }
}
