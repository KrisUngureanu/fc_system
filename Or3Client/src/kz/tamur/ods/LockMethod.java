package kz.tamur.ods;

import java.io.Serializable;

public class LockMethod implements Serializable {

    public static final int LOCK_SESSION = 1;
    public static final int LOCK_FLOW = 2;

	public final String muid;
	public final long flowId;
	public final String sessionId;
	public final int scope;
	
	public LockMethod(
			String muid,
			long flowId,
			String sessionId,
			int scope) {

		this.muid = muid;
		this.flowId = flowId;
		this.sessionId = sessionId;
		this.scope = scope;
	}
}
