
package kz.bee.bip.asyncchannel.v10.types.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import kz.bee.bip.common.v10.types.ErrorInfo;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.bee.bip.asyncchannel.v10.types.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ChangeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types/Client", "changeMassageStatusNotificationFault1_changeMassageStatusNotificationFault");
    private final static QName _SendMessageFault1SendMessageFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types/Client", "sendMessageFault1_sendMessageFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.bee.bip.asyncchannel.v10.types.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendMessage }
     * 
     */
    public SendMessage createSendMessage() {
        return new SendMessage();
    }

    /**
     * Create an instance of {@link SendMessageResponse }
     * 
     */
    public SendMessageResponse createSendMessageResponse() {
        return new SendMessageResponse();
    }

    /**
     * Create an instance of {@link ChangeMassageStatusNotificationResponse }
     * 
     */
    public ChangeMassageStatusNotificationResponse createChangeMassageStatusNotificationResponse() {
        return new ChangeMassageStatusNotificationResponse();
    }

    /**
     * Create an instance of {@link ChangeMassageStatusNotification }
     * 
     */
    public ChangeMassageStatusNotification createChangeMassageStatusNotification() {
        return new ChangeMassageStatusNotification();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types/Client", name = "changeMassageStatusNotificationFault1_changeMassageStatusNotificationFault")
    public JAXBElement<ErrorInfo> createChangeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_ChangeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types/Client", name = "sendMessageFault1_sendMessageFault")
    public JAXBElement<ErrorInfo> createSendMessageFault1SendMessageFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_SendMessageFault1SendMessageFault_QNAME, ErrorInfo.class, null, value);
    }

}
