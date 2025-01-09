package kz.tamur.ods;

import java.io.Serializable;

public class Lock implements Serializable {

    public static final int LOCK_SESSION = 1;
    public static final int LOCK_FLOW = 2;

	public final long objId;
	public final long lockerId;
	public final long flowId;
	public final String sessionId;
	public final int scope;
	
	public Lock(
			long objId,
			long lockerId,
			long flowId,
			String sessionId,
			int scope) {

		this.objId = objId;
		this.lockerId = lockerId;
		this.flowId = flowId;
		this.sessionId = sessionId;
		this.scope = scope;
	}
}
