
package kz.tamur.fc.kazpost.gep.service;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.7.redhat-1
 * 2021-10-02T23:17:07.686+06:00
 * Generated source version: 2.7.7.redhat-1
 */

@WebFault(name = "SendMessageFault1_SendMessageFault", targetNamespace = "http://bip.bee.kz/SyncChannel/v10/Types")
public class SendMessageSendMessageFaultMsg extends Exception {
    
    private kz.tamur.fc.kazpost.gep.service.ErrorInfo sendMessageFault1SendMessageFault;

    public SendMessageSendMessageFaultMsg() {
        super();
    }
    
    public SendMessageSendMessageFaultMsg(String message) {
        super(message);
    }
    
    public SendMessageSendMessageFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMessageSendMessageFaultMsg(String message, kz.tamur.fc.kazpost.gep.service.ErrorInfo sendMessageFault1SendMessageFault) {
        super(message);
        this.sendMessageFault1SendMessageFault = sendMessageFault1SendMessageFault;
    }

    public SendMessageSendMessageFaultMsg(String message, kz.tamur.fc.kazpost.gep.service.ErrorInfo sendMessageFault1SendMessageFault, Throwable cause) {
        super(message, cause);
        this.sendMessageFault1SendMessageFault = sendMessageFault1SendMessageFault;
    }

    public kz.tamur.fc.kazpost.gep.service.ErrorInfo getFaultInfo() {
        return this.sendMessageFault1SendMessageFault;
    }
}