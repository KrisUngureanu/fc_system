package com.cifs.or2.kernel;

public final class KrnMethod implements java.io.Serializable {
	
	public String uid = null;
	public String name = null;
	public long classId = (long) 0;
	public String className = null;
	public boolean isClassMethod = false;
	public long ownerId = -1;

	public KrnMethod() {}

	public KrnMethod(String uid, String name, long classId,String className, boolean isClassMethod, long ownerId) {
		this.uid = uid;
		this.name = name;
		this.classId = classId;
		this.className = className;
		this.isClassMethod = isClassMethod;
		this.ownerId = ownerId;
	}
}