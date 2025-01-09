package kz.tamur.rt.data;

public interface Record extends Comparable {
	
	long getClassId();
	long getObjId();
	long getAttrId();
	long getLangId();
	int getIndex();
	Object getValue();
	void setValue(Object value);
	int getStatus();
	void setStatus(int status);
}
