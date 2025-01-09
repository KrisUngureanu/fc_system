package kz.tamur.ekyzmet.test;

import java.io.Serializable;

import com.cifs.or2.kernel.KrnObject;

public final class Answer implements Serializable {

	private static final long serialVersionUID = -2735897178564786420L;

	public final Question qsn;
	public final KrnObject obj;
	public final String nameRu;
	public final KrnObject mediaObj;
	public final int mediaType;
	
	public Answer(Question qsn, KrnObject obj, String nameRu, KrnObject mediaObj, int mediaType) {
		super();
		this.qsn = qsn;
		this.obj = obj;
		this.nameRu = nameRu;
		this.mediaObj = mediaObj;
		this.mediaType = mediaType;
	}
	
}
