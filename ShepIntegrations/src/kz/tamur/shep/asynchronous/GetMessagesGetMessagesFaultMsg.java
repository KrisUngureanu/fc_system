
package kz.tamur.shep.asynchronous;

import javax.xml.ws.WebFault;


/**
 * ошибка
 *
 * This class was generated by Apache CXF 2.4.6
 * 2014-09-27T20:09:31.006+06:00
 * Generated source version: 2.4.6
 */

@WebFault(name = "getMessagesFault1_getMessagesFault", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types")
public class GetMessagesGetMessagesFaultMsg extends Exception {
    
    private kz.tamur.shep.asynchronous.ErrorInfo getMessagesFault1GetMessagesFault;

    public GetMessagesGetMessagesFaultMsg() {
        super();
    }
    
    public GetMessagesGetMessagesFaultMsg(String message) {
        super(message);
    }
    
    public GetMessagesGetMessagesFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public GetMessagesGetMessagesFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo getMessagesFault1GetMessagesFault) {
        super(message);
        this.getMessagesFault1GetMessagesFault = getMessagesFault1GetMessagesFault;
    }

    public GetMessagesGetMessagesFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo getMessagesFault1GetMessagesFault, Throwable cause) {
        super(message, cause);
        this.getMessagesFault1GetMessagesFault = getMessagesFault1GetMessagesFault;
    }

    public kz.tamur.shep.asynchronous.ErrorInfo getFaultInfo() {
        return this.getMessagesFault1GetMessagesFault;
    }
}