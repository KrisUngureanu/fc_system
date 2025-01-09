package com.cifs.or2.kernel;

import java.io.Serializable;

public class AttrChange implements Serializable {
	public final KrnObject obj;
	public final long attrId;
	public final long langId;
	public long trId;

	public AttrChange(KrnObject obj, long attrId, long langId, long trId) {
		super();
		this.obj = obj;
		this.attrId = attrId;
		this.langId = langId;
		this.trId = trId;
	}
}
