
package kz.tamur.shep.asynchronous;

import javax.xml.ws.WebFault;


/**
 * Ошибка
 *
 * This class was generated by Apache CXF 2.4.6
 * 2014-09-27T20:09:31.031+06:00
 * Generated source version: 2.4.6
 */

@WebFault(name = "sendDeliveryNotificationFault1_sendDeliveryNotificationFault", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types")
public class SendDeliveryNotificationSendDeliveryNotificationFaultMsg extends Exception {
    
    private kz.tamur.shep.asynchronous.ErrorInfo sendDeliveryNotificationFault1SendDeliveryNotificationFault;

    public SendDeliveryNotificationSendDeliveryNotificationFaultMsg() {
        super();
    }
    
    public SendDeliveryNotificationSendDeliveryNotificationFaultMsg(String message) {
        super(message);
    }
    
    public SendDeliveryNotificationSendDeliveryNotificationFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public SendDeliveryNotificationSendDeliveryNotificationFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo sendDeliveryNotificationFault1SendDeliveryNotificationFault) {
        super(message);
        this.sendDeliveryNotificationFault1SendDeliveryNotificationFault = sendDeliveryNotificationFault1SendDeliveryNotificationFault;
    }

    public SendDeliveryNotificationSendDeliveryNotificationFaultMsg(String message, kz.tamur.shep.asynchronous.ErrorInfo sendDeliveryNotificationFault1SendDeliveryNotificationFault, Throwable cause) {
        super(message, cause);
        this.sendDeliveryNotificationFault1SendDeliveryNotificationFault = sendDeliveryNotificationFault1SendDeliveryNotificationFault;
    }

    public kz.tamur.shep.asynchronous.ErrorInfo getFaultInfo() {
        return this.sendDeliveryNotificationFault1SendDeliveryNotificationFault;
    }
}
