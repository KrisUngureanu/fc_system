package kz.tamur.rt.data;

import com.cifs.or2.kernel.KrnObject;

public class ObjectRecord implements Record {
	
	private long classId;
	private KrnObject value;
	private int status;
	
	public ObjectRecord(long classId, KrnObject value) {
		this.classId = classId;
		this.value = value;
	}

	public ObjectRecord(long classId) {
		this.classId = classId;
	}

	public long getAttrId() {
		return 0;
	}

	public long getClassId() {
		return classId;
	}

	public int getIndex() {
		return 0;
	}

	public long getLangId() {
		return 0;
	}

	public long getObjId() {
		return value.id;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (KrnObject)value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int compareTo(Object o) {
		ObjectRecord rec = (ObjectRecord)o;
		long res = classId - rec.classId;
		if (res == 0) {
			long id1 = (value != null) ? value.id : Integer.MIN_VALUE;
			long id2 = (rec.value != null) ? rec.value.id : Integer.MIN_VALUE;
			res = id1 - id2;
		}
		return res > 0 ? 1 : res < 0 ? -1 : 0;
	}

        public boolean equals(Object o) {
            boolean res = false;
            if (o instanceof ObjectRecord) {
                ObjectRecord rec = (ObjectRecord)o;
                res = classId == rec.classId;
                if (res) {
                        long id1 = (value != null) ? value.id : Integer.MIN_VALUE;
                        long id2 = (rec.value != null) ? rec.value.id : Integer.MIN_VALUE;
                        res = id1 == id2;
                }
            }
            return res;
        }
}
