package kz.tamur.web.common;

import java.util.EventListener;

public interface ProcessListener extends EventListener {
	public void processStarted(long defId, long flowId);
	public void processCanceled(long defId, long flowId);
	public void processEnded(long defId, long flowId);
}
