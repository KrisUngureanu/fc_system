package kz.tamur.rt.adapters;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeFieldComponent;
import kz.tamur.or3.util.PathElement2;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.03.2005
 * Time: 15:53:47
 * To change this template use File | Settings | File Templates.
 */
public class TreeColumnAdapter extends ColumnAdapter {
    private TreeFieldAdapter editor;
    private OrRef titleRef;

    public TreeColumnAdapter(OrFrame frm, OrColumnComponent column)
            throws KrnException {
        super(frm, column, false);
        OrTreeFieldComponent t = (OrTreeFieldComponent)column.getEditor();
        editor = (TreeFieldAdapter)t.getAdapter();
        createTitleRef();
    }

    private void createTitleRef() throws KrnException {
        
    	StringBuffer path = new StringBuffer(dataRef.toString());

        KrnAttribute[] tattrs = editor.titleAttrs;
        PathElement2[] vattrs = editor.valueAttrs;
        if (tattrs != null && tattrs.length > 0) {
        	KrnAttribute tattr = tattrs[0];
        	Kernel krn = frame.getKernel();
            ClassNode dataType = krn.getClassNode(dataRef.getType().id);
            if (tattrs.length > 1 && dataRef.getType().id == tattr.typeClassId) { 
            	for (int i = 1; i< tattrs.length; i++)
            		path.append('.').append(tattrs[i].name);
            } else if (vattrs != null && vattrs.length > 0) {
        		ClassNode valueType = krn.getClassNode(vattrs[0].type.id);
        		if (valueType.indexOfAttribute(tattr) != -1) {
        			for (PathElement2 pe : vattrs)
        				path.append('.').append(pe.attr.name);
                	for (KrnAttribute attr : tattrs)
                		path.append('.').append(attr.name);
        		}
            } else if (dataType.indexOfAttribute(tattr) != -1) {
            	for (KrnAttribute attr : tattrs)
            		path.append('.').append(attr.name);
            }
        }
        titleRef = OrRef.createRef(path.toString(), true, Mode.RUNTIME, frame.getRefs(),
                dataRef.getDirtyTransactions(), frame);
    }

    public Object getValueAt(int row) {
        if (titleRef != null) {
            OrRef.Item item = titleRef.getItem(0, row);
            if (item != null)
                return item.getCurrent();
        }
        return null;
    }
}
