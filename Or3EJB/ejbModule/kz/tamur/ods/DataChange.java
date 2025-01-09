package kz.tamur.ods;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.12.2005
 * Time: 10:41:05
 * To change this template use File | Settings | File Templates.
 */
public class DataChange {
    public final long id;
    public final String uid;
    public final String attrUid;
    public final String attrName;
    public final int index;
    public final String langUid;
    public final Object value;

    public DataChange(long id, String uid, String attrUid, int index, String langUid, Object value) {
        this.id = id;
        this.uid = uid;
        this.attrUid = attrUid;
        this.attrName = null;
        this.index = index;
        this.langUid = langUid;
        this.value = value;
    }

    public DataChange(long id, String uid, String attrUid, String attrName, int index, String langUid, Object value) {
        this.id = id;
        this.uid = uid;
        this.attrUid = attrUid;
        this.attrName = attrName;
        this.index = index;
        this.langUid = langUid;
        this.value = value;
    }
}
