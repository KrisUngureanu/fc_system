
package kz.tamur.shep.asynchronous;

import javax.xml.ws.WebFault;


/**
 * Ошибка
 *
 * This class was generated by Apache CXF 2.4.6
 * 2014-09-27T20:09:31.046+06:00
 * Generated source version: 2.4.6
 */

@WebFault(name = "getMessageStatusFault1_getMessageStatusFault", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types")
public class GetMessageStatusGetMessageStatusFaultMsg extends Exception {
    
    private kz.tamur.shep.asynchronous.ErrorInfo getMessageStatusFault1GetMessageStatusFault;

    public GetMessageStatusGetMessageStatusFaultMsg() {
        super();
    }
    
    public GetMessageStatusGetMessageStatusFaultMsg(String message) {
        super(message);
    }
    
    public GetMessageStatusGetMessageStatusFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public GetMessageStatusGetMessageStatusFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo getMessageStatusFault1GetMessageStatusFault) {
        super(message);
        this.getMessageStatusFault1GetMessageStatusFault = getMessageStatusFault1GetMessageStatusFault;
    }

    public GetMessageStatusGetMessageStatusFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo getMessageStatusFault1GetMessageStatusFault, Throwable cause) {
        super(message, cause);
        this.getMessageStatusFault1GetMessageStatusFault = getMessageStatusFault1GetMessageStatusFault;
    }

    public kz.tamur.shep.asynchronous.ErrorInfo getFaultInfo() {
        return this.getMessageStatusFault1GetMessageStatusFault;
    }
}
