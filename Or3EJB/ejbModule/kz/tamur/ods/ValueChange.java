package kz.tamur.ods;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 18.01.2006
 * Time: 14:57:43
 * To change this template use File | Settings | File Templates.
 */
public class ValueChange implements Comparable {
    public long objectId;
    public long attrId;
    public int index;
    public long langId;
    public boolean insert;
    public Object value;

    public ValueChange(long objectId, long attrId, int index, long langId, boolean inserting, Object value) {
        this.objectId = objectId;
        this.attrId = attrId;
        this.index = index;
        this.langId = langId;
        insert = inserting;
        this.value = value;
    }

    public int compareTo(Object o) {
        ValueChange vc = (ValueChange)o;
        long res = objectId - vc.objectId;
        if (res == 0) {
            res = attrId - vc.attrId;
        }
        if (res == 0) {
            res = index - vc.index;
        }
        if (res == 0) {
            res = langId - vc.langId;
        }
        return res < 0 ? -1 : res > 0 ? 1 : 0;
    }
}
