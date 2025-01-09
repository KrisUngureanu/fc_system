
package kz.tamur.shep.asynchronous;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.shep.asynchronous package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.shep.asynchronous
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SenderInfo }
     * 
     */
    public SenderInfo createSenderInfo() {
        return new SenderInfo();
    }

    /**
     * Create an instance of {@link ErrorInfo }
     * 
     */
    public ErrorInfo createErrorInfo() {
        return new ErrorInfo();
    }

    /**
     * Create an instance of {@link DeliveryStatusInfo }
     * 
     */
    public DeliveryStatusInfo createDeliveryStatusInfo() {
        return new DeliveryStatusInfo();
    }

    /**
     * Create an instance of {@link DeliveryNotification }
     * 
     */
    public DeliveryNotification createDeliveryNotification() {
        return new DeliveryNotification();
    }

    /**
     * Create an instance of {@link MessageStatusInfo }
     * 
     */
    public MessageStatusInfo createMessageStatusInfo() {
        return new MessageStatusInfo();
    }

    /**
     * Create an instance of {@link MessageData }
     * 
     */
    public MessageData createMessageData() {
        return new MessageData();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link AsyncSendMessageRequest }
     * 
     */
    public AsyncSendMessageRequest createAsyncSendMessageRequest() {
        return new AsyncSendMessageRequest();
    }

    /**
     * Create an instance of {@link AsyncSendDeliveryNotificationRequest }
     * 
     */
    public AsyncSendDeliveryNotificationRequest createAsyncSendDeliveryNotificationRequest() {
        return new AsyncSendDeliveryNotificationRequest();
    }

    /**
     * Create an instance of {@link AsyncGetMessageStatusRequest }
     * 
     */
    public AsyncGetMessageStatusRequest createAsyncGetMessageStatusRequest() {
        return new AsyncGetMessageStatusRequest();
    }

    /**
     * Create an instance of {@link AsyncSendDeliveryNotificationResponse }
     * 
     */
    public AsyncSendDeliveryNotificationResponse createAsyncSendDeliveryNotificationResponse() {
        return new AsyncSendDeliveryNotificationResponse();
    }

    /**
     * Create an instance of {@link AsyncGetMessagesRequest }
     * 
     */
    public AsyncGetMessagesRequest createAsyncGetMessagesRequest() {
        return new AsyncGetMessagesRequest();
    }

    /**
     * Create an instance of {@link AsyncMessage }
     * 
     */
    public AsyncMessage createAsyncMessage() {
        return new AsyncMessage();
    }

    /**
     * Create an instance of {@link AsyncGetMessageStatusResponse }
     * 
     */
    public AsyncGetMessageStatusResponse createAsyncGetMessageStatusResponse() {
        return new AsyncGetMessageStatusResponse();
    }

    /**
     * Create an instance of {@link AsyncSendMessageResponse }
     * 
     */
    public AsyncSendMessageResponse createAsyncSendMessageResponse() {
        return new AsyncSendMessageResponse();
    }

    /**
     * Create an instance of {@link AsyncMessageInfo }
     * 
     */
    public AsyncMessageInfo createAsyncMessageInfo() {
        return new AsyncMessageInfo();
    }

    /**
     * Create an instance of {@link AsyncGetMessagesResponse }
     * 
     */
    public AsyncGetMessagesResponse createAsyncGetMessagesResponse() {
        return new AsyncGetMessagesResponse();
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
