package kz.tamur.ods;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.01.2006
 * Time: 15:39:53
 * To change this template use File | Settings | File Templates.
 */
public class Value implements Comparable, Serializable {
    public long objectId;
    public int index;
    public long trId;
    public Object value;

    public Value(long objectId, int index, long trId, Object value) {
        this.objectId = objectId;
        this.index = index;
        this.trId = trId;
        this.value = value;
    }

    public int compareTo(Object o) {
        Value v = (Value)o;
        long res = objectId - v.objectId;
        if (res == 0) {
            res = index - v.index;
        }
        return (res < 0) ? -1 : (res > 0) ? 1 : 0;
    }
}
