package kz.tamur.rt.data;

public class AttrRecord implements Record {

	private long objId; 
	private long attrId;
	private long langId;
	private int index;
	private Object value;
	private int status;
	
	public AttrRecord(long objId, long attrId, long langId, int index) {
		this.objId = objId;
		this.attrId = attrId;
		this.langId = langId;
		this.index = index;
	}
	
	public AttrRecord(long objId, long attrId, long langId, int index, Object value) {
		this.objId = objId;
		this.attrId = attrId;
		this.langId = langId;
		this.index = index;
		this.value = value;
	}
	
	public long getAttrId() {
		return attrId;
	}

	public long getClassId() {
		return 0;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public long getLangId() {
		return langId;
	}

	public long getObjId() {
		return objId;
	}
	
	public void setObjId(long objId) {
		this.objId = objId;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int compareTo(Object o) {
		AttrRecord rec = (AttrRecord)o;
		long res = objId - rec.objId;
		if (res == 0) {
			res = attrId - rec.attrId;
			if (res == 0) {
				res = langId - rec.langId;
				if (res == 0) {
					res = index - rec.index;
				}
			}
		}
		return res > 0 ? 1 : (res < 0 ? -1 : 0);
	}

    public boolean equals(Object obj) {
        if (obj == null) return false;
        AttrRecord rec = (AttrRecord)obj;
        long res = objId - rec.objId;
        if (res == 0) {
            res = attrId - rec.attrId;
	        if (res == 0) {
                res = langId - rec.langId;
		        if (res == 0) {
		        	res = index - rec.index;
		        }
	        }
        }
        return res == 0;
    }

}
