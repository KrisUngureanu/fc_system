package kz.tamur.shep.common;

import java.util.ArrayList;
import java.util.List;

import kz.tamur.shep.synchronous.StatusInfo;

public class Response {
    Object response = null;
    StatusInfo status = null;
    List<String> errors = null;
    String messageId = null;
    String elementNameEmptyPrefix = null;
    String elementNameSignature = null;
    
    public Response() {}

    /**
     * @return the response
     */
    public Object getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(Object response) {
        this.response = response;
    }

    /**
     * @return the status
     */
    public StatusInfo getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(StatusInfo status) {
        this.status = status;
    }

    /**
     * @return the errors
     */
    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        return errors;
    }

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the elementNameEmptyPrefix
	 */
	public String getElementNameEmptyPrefix() {
		return elementNameEmptyPrefix;
	}

	/**
	 * @param elementNameEmptyPrefix the elementNameEmptyPrefix to set
	 */
	public void setElementNameEmptyPrefix(String elementNameEmptyPrefix) {
		this.elementNameEmptyPrefix = elementNameEmptyPrefix;
	}

	/**
	 * @return the elementNameSignature
	 */
	public String getElementNameSignature() {
		return elementNameSignature;
	}

	/**
	 * @param elementNameSignature the elementNameSignature to set
	 */
	public void setElementNameSignature(String elementNameSignature) {
		this.elementNameSignature = elementNameSignature;
	}
}