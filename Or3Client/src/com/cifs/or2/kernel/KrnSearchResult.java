package com.cifs.or2.kernel;


public final class KrnSearchResult implements java.io.Serializable {

	public long classId = (long) 0;
	public String uid = null;
	public String title = null;

	public KrnSearchResult() {
	} // ctor

	public KrnSearchResult(long _id, String _uid, String _title) {
		classId = _id;
		uid = _uid;
		title  = _title;
	} // ctor
	
	@Override
	public String toString() {
		return "KrnSearchResult(" + classId + "," + title + "," + uid + ")";
	}

} // class KrnInConfig
