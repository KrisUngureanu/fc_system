package kz.tamur.rt.data;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.03.2005
 * Time: 16:16:34
 * To change this template use File | Settings | File Templates.
 */
public class CashChangeSupport {

    private Map listeners;

    public CashChangeSupport() {
        listeners = new HashMap();
    }

    public void addCashChangeListener(long attrId, CashChangeListener l) {

        Long aid = new Long(attrId);
        List list = (List)listeners.get(aid);
        if (list == null) {
            list = new ArrayList();
            listeners.put(aid, list);
        }
        if (!list.contains(l)) {
            list.add(l);
        }
    }

    public void clear() {
        listeners.clear();
    }

    public void removeCashChangeListener(CashChangeListener l) {
        for (Iterator aidIt = listeners.keySet().iterator(); aidIt.hasNext();) {
            List list = (List)listeners.get(aidIt.next());
            list.remove(l);
        }
    }

    public void fireObjectChanged(Object src, long objId, long attrId) {
        List list = (List)listeners.get(new Long(attrId));
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CashChangeListener l = (CashChangeListener)list.get(i);
                l.objectChanged(src, objId, attrId);
            }
        }
    }

    public void fireObjectCreated(Cache cache, long classId, long objId) {
        List list = (List)listeners.get(new Long(classId));
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CashChangeListener l = (CashChangeListener)list.get(i);
                l.objectCreated(cache, classId, objId);
            }
        }
    }

    public void fireObjectDeleted(Cache cache, long classId, long objId) {
        List list = (List)listeners.get(new Long(classId));
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CashChangeListener l = (CashChangeListener)list.get(i);
                l.objectDeleted(cache, classId, objId);
            }
        }
    }
}
