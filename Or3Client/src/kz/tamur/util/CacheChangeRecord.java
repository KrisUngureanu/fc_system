package kz.tamur.util;

import java.io.Serializable;

public class CacheChangeRecord implements Serializable {
	
	public static final byte CREATE_OBJECT = 1;
	public static final byte DELETE_OBJECT = 2;
	public static final byte CHANGE_ATTR = 3;
	public static final byte DELETE_ATTR = 4;

	public byte changeType = (byte) 0;
	public long objectId = (long) 0;
	public long attrId = (long) 0;
	public int index = (int) 0;
	public long langId = (long) 0;
	public Object value = null;
	// for COLLECTION_SET only!
	public Object oldValue = null;
	public boolean insert = false;
	public boolean isSetToNull = false;

	public CacheChangeRecord(byte changeType, long objectId, long attrId, int index, long langId, Object value, boolean insert) {
		this.changeType = changeType;
		this.objectId = objectId;
		this.attrId = attrId;
		this.index = index;
		this.langId = langId;
		this.value = value;
		this.insert = insert;
	}

	public CacheChangeRecord(byte changeType, long objectId, long attrId, int index, long langId, Object value, Object oldValue, boolean insert) {
		this(changeType, objectId, attrId, index, langId, value, insert);
		this.oldValue = oldValue;
	}
	
	public CacheChangeRecord(byte changeType, long objectId, long attrId, int index, long langId, Object value, boolean insert, boolean isSetToNull) {
		this(changeType, objectId, attrId, index, langId, value, insert);
		this.isSetToNull = isSetToNull;
	}
	
    public String toString() {
        return new String("ChangeType: " + changeType + " , ObjectID: " + objectId + ", AttributeID: " + attrId
                + ", Index: " + index + ", LangID: " + langId + ", Value: " + value + ", Insert: " + insert + ", IsSetToNull: " + isSetToNull);
    }
}