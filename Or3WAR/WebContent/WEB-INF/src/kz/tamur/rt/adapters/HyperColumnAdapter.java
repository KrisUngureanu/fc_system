package kz.tamur.rt.adapters;

import java.util.List;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrHyperLabelComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.rt.adapters.OrRef.Item;

import com.cifs.or2.kernel.KrnException;

public class HyperColumnAdapter extends ColumnAdapter {
    private HyperLabelAdapter editor;
    private OrRef titleRef;

    public HyperColumnAdapter(OrFrame frame, OrColumnComponent column) throws KrnException {
        super(frame, column, false);
        OrHyperLabelComponent hlb = (OrHyperLabelComponent)column.getEditor();
        editor = (HyperLabelAdapter)hlb.getAdapter();
        renderer = hlb.getCellRenderer();
        
        //TitlePath
        PropertyNode prop = column.getProperties().getChild("ref").getChild("treeDataRef");
        PropertyValue pv = column.getPropertyValue(prop);
        String titlePath;
        if (!pv.isNull()) {
            titlePath = pv.stringValue(frame.getKernel());
            titleRef = OrRef.createRef(titlePath, true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            titleRef.addOrRefListener(this);
        }

    }

    public OrRef getDynamicInterfaceRef() {
        return (editor != null) ? editor.getDynamicInterfaceRef() : null;
    }
    
    public Object getObjectValueAt(int i) {
        if (dataRef != null && titleRef != null) {
	        List<Item> items = dataRef.getItems(0);
	        Item item = (i < items.size()) ? items.get(i) : null;
	        if (item != null && !item.isDeleted) {
	        	for(Item tri:titleRef.getItems(editor.langId)) {
		        	if(tri!=null && tri.getRec().getObjId()==item.getRec().getObjId())
		        		return tri.getCurrent();
	        	}
	        }
        }
        return null;
    }
}
