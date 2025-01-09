
package kz.bee.bip.asyncchannel.v10.types;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import kz.bee.bip.common.v10.types.ErrorInfo;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.bee.bip.asyncchannel.v10.types package. 
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

    private final static QName _SendMessageFault1SendMessageFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types", "sendMessageFault1_sendMessageFault");
    private final static QName _SendDeliveryNotificationFault1SendDeliveryNotificationFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types", "sendDeliveryNotificationFault1_sendDeliveryNotificationFault");
    private final static QName _GetMessageStatusFault1GetMessageStatusFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types", "getMessageStatusFault1_getMessageStatusFault");
    private final static QName _GetMessagesFault1GetMessagesFault_QNAME = new QName("http://bip.bee.kz/AsyncChannel/v10/Types", "getMessagesFault1_getMessagesFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.bee.bip.asyncchannel.v10.types
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
     * Create an instance of {@link GetMessagesResponse }
     * 
     */
    public GetMessagesResponse createGetMessagesResponse() {
        return new GetMessagesResponse();
    }

    /**
     * Create an instance of {@link GetMessageStatus }
     * 
     */
    public GetMessageStatus createGetMessageStatus() {
        return new GetMessageStatus();
    }

    /**
     * Create an instance of {@link SendDeliveryNotificationResponse }
     * 
     */
    public SendDeliveryNotificationResponse createSendDeliveryNotificationResponse() {
        return new SendDeliveryNotificationResponse();
    }

    /**
     * Create an instance of {@link SendDeliveryNotification }
     * 
     */
    public SendDeliveryNotification createSendDeliveryNotification() {
        return new SendDeliveryNotification();
    }

    /**
     * Create an instance of {@link GetMessageStatusResponse }
     * 
     */
    public GetMessageStatusResponse createGetMessageStatusResponse() {
        return new GetMessageStatusResponse();
    }

    /**
     * Create an instance of {@link GetMessages }
     * 
     */
    public GetMessages createGetMessages() {
        return new GetMessages();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types", name = "sendMessageFault1_sendMessageFault")
    public JAXBElement<ErrorInfo> createSendMessageFault1SendMessageFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_SendMessageFault1SendMessageFault_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types", name = "sendDeliveryNotificationFault1_sendDeliveryNotificationFault")
    public JAXBElement<ErrorInfo> createSendDeliveryNotificationFault1SendDeliveryNotificationFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_SendDeliveryNotificationFault1SendDeliveryNotificationFault_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types", name = "getMessageStatusFault1_getMessageStatusFault")
    public JAXBElement<ErrorInfo> createGetMessageStatusFault1GetMessageStatusFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_GetMessageStatusFault1GetMessageStatusFault_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bip.bee.kz/AsyncChannel/v10/Types", name = "getMessagesFault1_getMessagesFault")
    public JAXBElement<ErrorInfo> createGetMessagesFault1GetMessagesFault(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_GetMessagesFault1GetMessagesFault_QNAME, ErrorInfo.class, null, value);
    }

}
