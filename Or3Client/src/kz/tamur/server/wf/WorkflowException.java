package kz.tamur.server.wf;

import kz.tamur.OrException;

/**
 * Created by IntelliJ IDEA. User: berik Date: 06.12.2005 Time: 18:26:02 To
 * change this template use File | Settings | File Templates.
 */
public class WorkflowException extends OrException {

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(Throwable cause) {
		super(cause);
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowException(String message, int code) {
		super(message, code);
	}

	public WorkflowException(String message, int code, Throwable cause) {
		super(message, code, cause);
	}
}
