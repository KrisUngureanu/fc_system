package kz.tamur.util;

import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import com.cifs.or2.kernel.*;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;

import kz.tamur.guidesigner.PopupTextSearchWindow;
import kz.tamur.guidesigner.DesignerDialog;

public class ObjectList extends JList {

    protected KrnClass cls_;
    protected KrnAttribute attr_;
    protected Map titles_;


    private PopupTextSearchWindow popupSearch;

    private String prefix = "";

    public ObjectList(KrnClass cls, String attrName)
            throws KrnException {
        Kernel krn = Kernel.instance();
        ClassNode node = krn.getClassNode(cls.id);
        KrnAttribute attr = node.getAttribute(attrName);
        init(cls, attr);
    }

    public ObjectList(KrnClass cls, KrnAttribute attr, int langId)
            throws KrnException {
        init(cls, attr);
    }
     public void setSingleSelectionMode(boolean single)
     {
         if(single)
         {
             setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         }
         else
         {
             setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         }
     }
    private void init(KrnClass cls, KrnAttribute attr)
            throws KrnException {
        setModel(new DefaultListModel());
        Kernel krn = Kernel.instance();
        cls_ = cls;
        attr_ = attr;
        KrnObject[] objs = krn.getClassObjects(cls_, 0);

        long[] objIds = Funcs.makeObjectIdArray(objs);
        DefaultListModel lm = (DefaultListModel) getModel();
        titles_ = new TreeMap();
        long langId = (attr_ != null && attr_.isMultilingual)
                ? Utils.getDataLangId() : 0;
        if (attr_ == null && cls_.id == Kernel.IC_STRING) {
            StringValue[] svs = krn.getStringValues(objIds, attr_, langId, false, 0);
            for (int i = 0; i < svs.length; ++i)
                titles_.put(new Long(svs[i].objectId), svs[i].value);
        } else if (attr_ != null && (attr_.typeClassId == Kernel.IC_STRING || attr_.typeClassId == Kernel.IC_MSTRING 
        		|| attr_.typeClassId == Kernel.IC_MEMO || attr_.typeClassId == Kernel.IC_MMEMO)) {
            StringValue[] svs = krn.getStringValues(objIds, attr_, langId, false, 0);
            for (int i = 0; i < svs.length; ++i)
                titles_.put(new Long(svs[i].objectId), svs[i].value);
        } else if(attr_==null) {
            for (int i = 0; i < objIds.length; ++i)
                titles_.put(new Long(objIds[i]), ""+objIds[i]);
        } else {
            ObjectValue[] ovs = krn.getObjectValues(objIds, attr_, 0);
            for (int i = 0; i < ovs.length; ++i)
                titles_.put(new Long(ovs[i].objectId), new Long(ovs[i].value.id));
        }

        //Set items = new TreeSet();
        List items=new ArrayList();
        
 /*       if ("Language".equals(cls.name)) {
        	LongValue[] lvs = krn.getLongValues(objIds, krn.getAttributeByName(cls, "lang?"), 0);
            for (int i = 0; i < lvs.length; ++i) {
            	if (lvs[i].value != 1) titles_.remove(new Long(lvs[i].objectId));
            }        	
        }
*/        for (int i = 0; i < objs.length; ++i) {
            KrnObject obj = objs[i];
            String title = (String) titles_.get(new Long(obj.id));
            if (title != null)
            	items.add(new Item(obj, title));
        }
		Collections.sort(items);
        for (Object item :items)
            lm.addElement(item);

        setFont(kz.tamur.rt.Utils.getDefaultFont());

        addMouseListener(new DoubleClickMouseAdapter());
    }



    public String getSelectedTitle() {
        Item item = (Item) getSelectedValue();
        return (item != null) ? item.title : "";
    }

    public KrnObject getSelectedObject() {
        Item item = (Item) getSelectedValue();
        return (item != null) ? item.object : null;
    }

    public KrnObject getObjectById(int objId) {
        DefaultListModel lm = (DefaultListModel) getModel();
        for (int i = 0; i < lm.getSize(); i++) {
            Item item = (Item) lm.getElementAt(i);
            if (item.id == objId) return item.object;
        }
        return null;
    }
    public int getIndexById(int objId) {
        DefaultListModel lm = (DefaultListModel) getModel();
        for (int i = 0; i < lm.getSize(); i++) {
            Item item = (Item) lm.getElementAt(i);
            if (item.id == objId) return i;
        }
        return -1;
    }

    public KrnObject getObjectByIndex(int ind) {
        DefaultListModel lm = (DefaultListModel) getModel();
        Item item = (Item) lm.getElementAt(ind);
        return (item != null) ? item.object : null;
    }

    public long getIdByIndex(int ind) {
        DefaultListModel lm = (DefaultListModel) getModel();
        Item item = (Item) lm.getElementAt(ind);
        return (item != null) ? item.id : 0;
    }

    public String getTitleByIndex(int ind) {
        DefaultListModel lm = (DefaultListModel) getModel();
        Item item = (Item) lm.getElementAt(ind);
        return (item != null) ? item.title : "";
    }

    public KrnObject[] getSelectedObjects() {
        Object[] items = getSelectedValues();
        KrnObject[] res = new KrnObject[items.length];
        for (int i = 0; i < items.length; i++)
            res[i] = ((Item) items[i]).object;
        return res;
    }

    public String[] getSelectedTitles() {
        Object[] items = getSelectedValues();
        String[] res = new String[items.length];
        for (int i = 0; i < items.length; i++)
            res[i] = ((Item) items[i]).title;
        return res;
    }

    public Map getObjectTitles() {
        return titles_;
    }

    protected class Item implements Comparable {
        KrnObject object;
        String title;
        long id;

        public Item(KrnObject obj, String title) {
            object = obj;
            id = 0;
            if (obj != null) id = obj.id;
            this.title = (title != null) ? title : "" + obj.id;
        }

        public String toString() {
            return title;
        }

        public int compareTo(Object o) {
            String str = (o != null) ? o.toString() : null;
            if(object!=null && o instanceof Item && ((Item)o).object!=null &&(""+object.id).equals(title)&& (""+((Item)o).object.id).equals(str)) {
            	return object.id>((Item)o).object.id?1:object.id<((Item)o).object.id?-1:0;
            }else
            	return title.compareTo(str);
        }
    }

    public void setSearchPrefix(String pref) {
        prefix = pref;
    }

    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (Character.isLetter(e.getKeyChar())) {
                popupSearch = new PopupTextSearchWindow(
                       (JDialog)this.getTopLevelAncestor(), this,
                       prefix + new String(new char[] {e.getKeyChar()}));
                popupSearch.setVisible(true);
                popupSearch.setLocation(
                        kz.tamur.rt.Utils.getAbsolutX((JDialog)this.getTopLevelAncestor()) + 10,
                        kz.tamur.rt.Utils.getAbsolutY((JDialog)this.getTopLevelAncestor()) + 10);
            }
        }
        super.processKeyEvent(e);
    }

    private class DoubleClickMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);    //To change body of overridden methods use File | Settings | File Templates.
            if (e.getClickCount() == 2 && getSelectedObject() != null) {
                Container c = getTopLevelAncestor();
                if (c instanceof DesignerDialog &&
                        ((DesignerDialog)c).getRootPane().getDefaultButton() != null) {
                    ((DesignerDialog)c).getRootPane().getDefaultButton().doClick();
                }
            }
        }
    }
}
