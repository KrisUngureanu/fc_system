package kz.tamur.shep.common;


public class AsyncResponse {
    String status = null;
    Object parametrs = null;
    Object xml = null;
    String messageType = null;
    
    public AsyncResponse() {}

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the parametrs
     */
    public Object getParametrs() {
        return parametrs;
    }

    /**
     * @param parametrs the parametrs to set
     */
    public void setParametrs(Object parametrs) {
        this.parametrs = parametrs;
    }

    /**
     * @return the xml
     */
    public Object getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(Object xml) {
        this.xml = xml;
    }

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}