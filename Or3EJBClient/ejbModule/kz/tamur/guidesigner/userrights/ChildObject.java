package kz.tamur.guidesigner.userrights;

import com.cifs.or2.kernel.KrnObject;

public class ChildObject {
	private KrnObject obj;
	private int code;
	private String title;
	
	public ChildObject(KrnObject obj, String title) {
		super();
		this.obj = obj;
		this.title = title;
	}

	public ChildObject(KrnObject obj, int code, String title) {
		super();
		this.obj = obj;
		this.code = code;
		this.title = title;
	}

	public KrnObject getObj() {
		return obj;
	}

	public void setObj(KrnObject obj) {
		this.obj = obj;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCode() {
		return code;
	}
}
