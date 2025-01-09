package com.cifs.or2.kernel;

import java.util.Date;

public class AttrChangeNote extends Note {
	
	public final KrnObject obj;
	public final long attrId;
	public final long langId;
	public final long trId;

	public AttrChangeNote(Date time, UserSessionValue from, KrnObject obj, long attrId, long langId, long trId) {
		super(time, from);
		this.obj = obj;
		this.attrId = attrId;
		this.langId = langId;
		this.trId = trId;
	}
}
