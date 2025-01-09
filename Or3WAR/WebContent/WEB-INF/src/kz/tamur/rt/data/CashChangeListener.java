package kz.tamur.rt.data;

import kz.tamur.rt.data.Cache;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.03.2005
 * Time: 16:10:08
 * To change this template use File | Settings | File Templates.
 */
public interface CashChangeListener {
    void objectChanged(Object src, long objId, long attrId);
    void objectDeleted(Cache cache, long classId, long objId);
    void objectCreated(Cache cache, long classId, long objId);
}
