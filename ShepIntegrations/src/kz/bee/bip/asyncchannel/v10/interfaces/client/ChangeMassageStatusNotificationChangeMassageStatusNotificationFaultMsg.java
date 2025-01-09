
package kz.bee.bip.asyncchannel.v10.interfaces.client;

import javax.xml.ws.WebFault;


/**
 * Объект ошибки при обработке запроса
 *
 * This class was generated by Apache CXF 2.7.7.redhat-1
 * 2014-09-02T16:37:26.226+06:00
 * Generated source version: 2.7.7.redhat-1
 */

@WebFault(name = "changeMassageStatusNotificationFault1_changeMassageStatusNotificationFault", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types/Client")
public class ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg extends Exception {
    
    private kz.bee.bip.common.v10.types.ErrorInfo changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault;

    public ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg() {
        super();
    }
    
    public ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(String message) {
        super(message);
    }
    
    public ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(String message, kz.bee.bip.common.v10.types.ErrorInfo changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault) {
        super(message);
        this.changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault = changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault;
    }

    public ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(String message, kz.bee.bip.common.v10.types.ErrorInfo changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault, Throwable cause) {
        super(message, cause);
        this.changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault = changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault;
    }

    public kz.bee.bip.common.v10.types.ErrorInfo getFaultInfo() {
        return this.changeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault;
    }
}
