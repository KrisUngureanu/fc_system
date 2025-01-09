package kz.tamur.lang;

import kz.tamur.OrException;

public class EvalException extends OrException {
	private String fullMessage;

    public EvalException(String message, String fullMessage, Throwable cause) {
    	super(message, cause);
    	this.fullMessage = fullMessage;
    }

    public EvalException(String message, Throwable cause) {
    	super(message, cause);
    	fullMessage = message;
    }

    public EvalException(String message) {
        super(message);
    	fullMessage = message;
    }

	public String getFullMessage() {
		return fullMessage;
	}
}
